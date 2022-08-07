package org.ecommarket.filter.ip;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.ecommarket.config.ConfigStorage;
import org.ecommarket.excpetion.IpRequestLimitExceededException;
import org.ecommarket.util.ExtendedLongArray;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class IpThrottleAspect {

	private final long intervalMillis;

	private final int requestsLimit;

	private final Map<ThrottleID, ExtendedLongArray> requestTimestamps;

	public IpThrottleAspect(ConfigStorage configStorage) {
		intervalMillis = configStorage.getIpThrottlingIntervalMillis();
		requestsLimit = configStorage.getIpThrottlingCount();
		this.requestTimestamps = new ConcurrentHashMap<>();
	}

	@Around("@annotation(IpThrottle)")
	public Object throttleIP(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request;
		try {
			request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		}
		catch (IllegalStateException e) { // Not in request context
			return joinPoint.proceed();
		}

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();

		Method method = signature.getMethod();
		IpThrottle ipThrottle = method.getAnnotation(IpThrottle.class);

		long intervalMillis = ipThrottle.intervalMillis() <= 0 ? this.intervalMillis : ipThrottle.intervalMillis();
		int requestsLimit = ipThrottle.requestsLimit() <= 0 ? this.requestsLimit : ipThrottle.requestsLimit();

		String methodSignature = signature.toString();
		String remoteAddress = request.getRemoteAddr();

		ExtendedLongArray timestamps = requestTimestamps.computeIfAbsent(new ThrottleID(methodSignature, remoteAddress),
				throttleID -> new ExtendedLongArray(requestsLimit));

		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (timestamps) {
			long currentTimestamp = System.currentTimeMillis();

			if (timestamps.size() < requestsLimit) {
				timestamps.add(currentTimestamp);
			} else {
				long intervalStartPoint = currentTimestamp - intervalMillis;
				int index = timestamps.binarySearch(intervalStartPoint);

				if (index < 0) {
					index = ~index;
				}

				if (index == 0) { // All requests are in last interval
					throw new IpRequestLimitExceededException();
				} else {
					timestamps.removeFirstItems(index);
					timestamps.add(currentTimestamp);
				}
			}
		}


		return joinPoint.proceed();
	}

	private record ThrottleID(String methodSignature, String address) {}
}
