package com.stringsandtones.CloudGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

  @GetMapping("/orderServiceFallback")
  public String orderServiceFallback() {
    return "Order service is currently down!";
  }

  @GetMapping("/paymentServiceFallback")
  public String paymentServiceFallback() {
    return "Payment service is currently down!";
  }

  @GetMapping("/productServiceFallback")
  public String productServiceFallback() {
    return "Product service is currently down!";
  }
}
