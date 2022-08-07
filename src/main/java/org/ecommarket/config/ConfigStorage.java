package org.ecommarket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigStorage {

	private final long ipThrottlingIntervalMillis;

	private final int ipThrottlingCount;

	public ConfigStorage(@Value("${throttling.ip.intervalMillis}") long ipThrottlingIntervalMillis,
						 @Value("${throttling.ip.count}") int ipThrottlingCount) {
		this.ipThrottlingIntervalMillis = ipThrottlingIntervalMillis;
		this.ipThrottlingCount = ipThrottlingCount;
	}

	public long getIpThrottlingIntervalMillis() {
		return ipThrottlingIntervalMillis;
	}

	public int getIpThrottlingCount() {
		return ipThrottlingCount;
	}
}
