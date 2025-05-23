package org.myteam.server.global.config;

import org.springframework.context.annotation.Bean;

import feign.Logger;

public class FeignConfig {

	@Bean
	Logger.Level feignLoggerLevel() {
		return Logger.Level.FULL;
	}
}
