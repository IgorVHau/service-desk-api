package service_desk_api.api.controller;

import static org.mockito.Mockito.mock;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

import service_desk_api.api.security.JwtUtil;

@TestConfiguration
class TestSecurityConfig {
	
	@Bean
	JwtUtil jwtUtil() {
		return mock(JwtUtil.class);
	}
	
	@Bean
	UserDetailsService userDetailsService() {
		return mock(UserDetailsService.class);
	}

}
