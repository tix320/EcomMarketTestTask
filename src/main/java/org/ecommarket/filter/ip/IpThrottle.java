package org.ecommarket.filter.ip;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface IpThrottle {

	long intervalMillis() default 0;

	int requestsLimit() default 0;
}
