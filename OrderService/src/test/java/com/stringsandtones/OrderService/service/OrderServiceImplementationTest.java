package com.stringsandtones.OrderService.service;

import com.stringsandtones.OrderService.entity.Order;
import com.stringsandtones.OrderService.exception.CustomException;
import com.stringsandtones.OrderService.external.CompanyClients.PaymentService;
import com.stringsandtones.OrderService.external.CompanyClients.ProductService;
import com.stringsandtones.OrderService.model.*;
import com.stringsandtones.OrderService.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplementationTest {

  @Mock private OrderRepository orderRepository;
  @Mock private ProductService productService;
  @Mock private PaymentService paymentService;
  @Mock private RestTemplate restTemplate;

  @InjectMocks OrderService orderService = new OrderServiceImplementation();

  private final String PRODUCT_SERVICE_API = "http://PRODUCT-SERVICE/api/v1/product/";
  private final String PAYMENT_SERVICE_API = "http://PAYMENT-SERVICE/api/v1/payment/";

  @DisplayName("Get Order - Success Scenario")
  @Test
  void test_When_Order_Successful() {
    // Mocking
    Order order = getMockOrder();
    // Fetching orderId
    when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

    // Fetching product info based on orderId
    when(restTemplate.getForObject(
            PRODUCT_SERVICE_API + order.getProductId(), ProductResponse.class))
        .thenReturn(getMockProductResponse());

    // Fetching payment info based on orderId
    when(restTemplate.getForObject(PAYMENT_SERVICE_API + order.getId(), PaymentResponse.class))
        .thenReturn(getMockPaymentResponse());

    // Actual method call
    OrderResponse orderResponse = orderService.getOrderDetails(1);

    // Verification: The verify() methods makes sure that the when() methods are actually being
    // called
    verify(orderRepository, times(1)).findById(anyLong());
    verify(restTemplate, times(1))
        .getForObject(PRODUCT_SERVICE_API + order.getProductId(), ProductResponse.class);
    verify(restTemplate, times(1))
        .getForObject(PAYMENT_SERVICE_API + order.getId(), PaymentResponse.class);

    // Assert
    assertNotNull(orderResponse);
    assertEquals(order.getId(), orderResponse.getOrderId());
  }

  @DisplayName("Get Order - Failure Scenario")
  @Test
  void test_When_getOrder_fails() {

    when(orderRepository.findById(anyLong())).thenReturn(Optional.ofNullable(null));

    CustomException exception =
        assertThrows(CustomException.class, () -> orderService.getOrderDetails(1));

    assertEquals("NOT_FOUND", exception.getErrorCode());
    assertEquals(404, exception.getStatus());

    verify(orderRepository, times(1)).findById(anyLong());
  }

  @DisplayName("Place Order - Success Scenario")
  @Test
  void test_when_placeOrder_Succeeds() {
    Order order = getMockOrder();
    OrderRequest orderRequest = getMockOrderRequest();

    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(productService.reduceQuantity(anyLong(), anyLong()))
        .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
    when(paymentService.initPayment(any(PaymentRequest.class)))
        .thenReturn(new ResponseEntity<Long>(1L, HttpStatus.OK));

    long orderId = orderService.placeOrder(orderRequest);

    verify(orderRepository, times(2)).save(any());
    verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
    verify(paymentService, times(1)).initPayment(any(PaymentRequest.class));

    assertEquals(order.getId(), orderId);
  }

  @DisplayName("Place Order - Payment Failing Scenario")
  @Test
  void test_When_placeOrder_Fails_But_Order_Placed() {
    Order order = getMockOrder();
    OrderRequest orderRequest = getMockOrderRequest();

    when(orderRepository.save(any(Order.class))).thenReturn(order);
    when(productService.reduceQuantity(anyLong(), anyLong()))
        .thenReturn(new ResponseEntity<Void>(HttpStatus.OK));
    when(paymentService.initPayment(any(PaymentRequest.class))).thenThrow(new RuntimeException());

    long orderId = orderService.placeOrder(orderRequest);

    verify(orderRepository, times(2)).save(any());
    verify(productService, times(1)).reduceQuantity(anyLong(), anyLong());
    verify(paymentService, times(1)).initPayment(any(PaymentRequest.class));

    assertEquals(order.getId(), orderId);
  }

  private OrderRequest getMockOrderRequest() {
    return OrderRequest.builder()
        .productId(1)
        .quantity(1)
        .paymentMode(PaymentMode.VISA_CREDIT_CARD)
        .totalAmount(100)
        .build();
  }

  private PaymentResponse getMockPaymentResponse() {
    return PaymentResponse.builder()
        .paymentId(1)
        .paymentDate(Instant.now())
        .paymentMode(PaymentMode.VISA_CREDIT_CARD)
        .amount(1)
        .orderId(1)
        .status("ACCEPTED")
        .build();
  }

  private ProductResponse getMockProductResponse() {
    return ProductResponse.builder()
        .productId(1)
        .productName("Fender Strat")
        .price(100)
        .quantity(1)
        .build();
  }

  private Order getMockOrder() {
    return Order.builder()
        .orderStatus("PLACED")
        .orderDate(Instant.now())
        .id(1)
        .amount(100)
        .quantity(200)
        .productId(1)
        .build();
  }
}
