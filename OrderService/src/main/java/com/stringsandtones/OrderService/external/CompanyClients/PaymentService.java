package com.stringsandtones.OrderService.external.CompanyClients;

import com.stringsandtones.OrderService.exception.CustomException;
import com.stringsandtones.OrderService.model.PaymentRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CircuitBreaker(name = "external", fallbackMethod = "fallback")
@FeignClient(name = "PAYMENT-SERVICE/api/v1/payment")
public interface PaymentService {

  @PostMapping
  ResponseEntity<Long> initPayment(@RequestBody PaymentRequest paymentRequest);

  default ResponseEntity<Long> fallback(Exception e) {
    throw new CustomException("Payment service is current unavailable!", "UNAVAILABLE", 500);
  }
}
