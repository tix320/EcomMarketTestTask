package org.ecommarket.controller;

import org.ecommarket.filter.ip.IpThrottle;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@GetMapping("/hello")
	@IpThrottle
	public ResponseEntity<?> hello() {
		return ResponseEntity.ok().build();
	}
}
