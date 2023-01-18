package com.example.PaymentService.controller;

import com.example.PaymentService.model.PaymentRequest;
import com.example.PaymentService.model.PaymentResponse;
import com.example.PaymentService.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

  @Autowired private PaymentService paymentService;

  @PostMapping
  public ResponseEntity<Long> initPayment(@RequestBody PaymentRequest paymentRequest) {
    return new ResponseEntity<>(paymentService.initPayment(paymentRequest), HttpStatus.OK);
  }

  @GetMapping("/{orderId}")
  public ResponseEntity<PaymentResponse> getPaymentDetailsByOrderId(@PathVariable String orderId) {
    return new ResponseEntity<>(paymentService.getPaymentDetailsByOrderId(orderId), HttpStatus.OK);
  }
}
