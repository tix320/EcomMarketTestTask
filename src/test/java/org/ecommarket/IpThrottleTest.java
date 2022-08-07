package org.ecommarket;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class IpThrottleTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void simpleTest() throws Exception {
		List<HttpStatus> statuses = doRequest("192.168.0.2", 500, 6);
		assertEquals(List.of(OK, OK, OK, OK, OK, BAD_GATEWAY), statuses);

		Thread.sleep(1000);

		statuses = doRequest("192.168.0.2", 500, 4);
		assertEquals(List.of(BAD_GATEWAY, BAD_GATEWAY, OK, OK), statuses);
	}

	@Test
	void parallelTest() throws ExecutionException, InterruptedException {
		List<CompletableFuture<?>> futures = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			String ip = "192.168.0.%s".formatted(i);
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				try {
					List<HttpStatus> statuses = doRequest(ip, 500, 4);
					assertEquals(List.of(OK, OK, OK, OK), statuses);

					Thread.sleep(1000);

					statuses = doRequest(ip, 500, 3);
					assertEquals(List.of(OK, BAD_GATEWAY, BAD_GATEWAY), statuses);

					Thread.sleep(500);

					statuses = doRequest(ip, 300, 5);
					assertEquals(List.of(OK, BAD_GATEWAY, OK, OK, BAD_GATEWAY), statuses);
				}
				catch (Exception e) {
					throw new RuntimeException(e);
				}
			});

			futures.add(future);
		}

		CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).get();
	}

	List<HttpStatus> doRequest(String ip, long interval, int requestCount) throws Exception {
		List<HttpStatus> statuses = new ArrayList<>(requestCount);

		for (int i = 0; i < requestCount; i++) {
			int status = this.mockMvc.perform(get("/hello").with(request -> {
				request.setRemoteAddr(ip);
				return request;
			})).andReturn().getResponse().getStatus();

			statuses.add(HttpStatus.resolve(status));

			Thread.sleep(interval);
		}

		return statuses;
	}
}
