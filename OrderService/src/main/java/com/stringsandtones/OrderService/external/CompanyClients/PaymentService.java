package com.stringsandtones.OrderService.external.CompanyClients;

import com.stringsandtones.OrderService.model.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "PAYMENT-SERVICE/api/v1/payment")
public interface PaymentService {

  @PostMapping
  ResponseEntity<Long> initPayment(@RequestBody PaymentRequest paymentRequest);
}
