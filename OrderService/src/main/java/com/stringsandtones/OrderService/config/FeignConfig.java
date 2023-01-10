package com.stringsandtones.OrderService.config;

import com.stringsandtones.OrderService.external.decoder.CustomerErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

  @Bean
  ErrorDecoder errorDecoder() {
    return new CustomerErrorDecoder();
  }
}
