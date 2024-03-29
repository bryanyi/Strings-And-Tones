package com.stringsandtones.OrderService.external.CompanyClients;

import com.stringsandtones.OrderService.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name = "PRODUCT-SERVICE/api/v1/product")
public interface ProductService {

  @PutMapping("/reduceQuantity/{productId}")
  ResponseEntity<Void> reduceQuantity(@PathVariable long productId, @RequestParam long quantity);

  default ResponseEntity<Void> fallback(Exception e) {
    throw new CustomException("Product service is current unavailable!", "UNAVAILABLE", 500);
  }
}
