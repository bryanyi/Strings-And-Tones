package com.stringsandtones.OrderService.service;

import com.stringsandtones.OrderService.entity.Order;
import com.stringsandtones.OrderService.exception.CustomException;
import com.stringsandtones.OrderService.external.CompanyClients.PaymentService;
import com.stringsandtones.OrderService.external.CompanyClients.ProductService;
import com.stringsandtones.OrderService.model.*;
import com.stringsandtones.OrderService.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImplementation implements OrderService {

  @Autowired private OrderRepository orderRepository;
  @Autowired private ProductService productService;
  @Autowired private PaymentService paymentService;
  @Autowired private RestTemplate restTemplate;

  private final String PRODUCT_SERVICE_API = "http://PRODUCT-SERVICE/api/v1/product/";
  private final String PAYMENT_SERVICE_API = "http://PAYMENT-SERVICE/api/v1/payment/";

  @Override
  public long placeOrder(OrderRequest orderRequest) {
    // orderRequest needs to be mapped to the Order object
    // Call product service to check quantity before proceeding
    // Call payment service to determine if the order was successful.
    // - Should provide status like COMPLETE or CANCELLED, etc
    log.info("Placing order for {}", orderRequest);

    productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

    log.info(
        "Product with id {} quantity successfully updated in PRODUCT-SERVICE",
        orderRequest.getProductId());

    Order order =
        Order.builder()
            .productId(orderRequest.getProductId())
            .amount(orderRequest.getTotalAmount())
            .orderStatus("created")
            .orderDate(Instant.now())
            .quantity(orderRequest.getQuantity())
            .build();

    order = orderRepository.save(order);

    log.info("Processing payment...");

    PaymentRequest paymentRequest =
        PaymentRequest.builder()
            .orderId(order.getId())
            .paymentMode(orderRequest.getPaymentMode())
            .amount(orderRequest.getTotalAmount())
            .build();

    String orderStatus = null;
    try {
      paymentService.initPayment(paymentRequest);
      log.info("Payment has been successfully placed!");
      orderStatus = "PLACED";
    } catch (Exception e) {
      log.info("PAYMENT FAILED!!");
      orderStatus = "PAYMENT_FAILED";
    }
    order.setOrderStatus(orderStatus);
    orderRepository.save(order);

    log.info("Order with id of {} was successfully placed", order.getId());
    return order.getId();
  }

  @Override
  public OrderResponse getOrderDetails(long orderId) {
    log.info("Request for order details with id of {}", orderId);

    Order order =
        orderRepository
            .findById(orderId)
            .orElseThrow(
                () ->
                    new CustomException(
                        "Order with id of " + orderId + " was not found!", "NOT_FOUND", 404));

    log.info("Fetching product response info from PRODUCT-SERVICE");

    ProductResponse productResponse =
        restTemplate.getForObject(
            PRODUCT_SERVICE_API + order.getProductId(), ProductResponse.class);

    log.info("Fetching payment response info from PAYMENT-SERVICE");
    PaymentResponse paymentResponse =
        restTemplate.getForObject(PAYMENT_SERVICE_API + order.getId(), PaymentResponse.class);

    ProductResponse productDetails =
        ProductResponse.builder()
            .productName(productResponse.getProductName())
            .productId(productResponse.getProductId())
            .build();

    PaymentDetails paymentDetails =
        PaymentDetails.builder()
            .paymentId(paymentResponse.getPaymentId())
            .paymentMode(paymentResponse.getPaymentMode())
            .paymentStatus(paymentResponse.getStatus())
            .paymentDate(paymentResponse.getPaymentDate())
            .build();

    OrderResponse orderResponse =
        OrderResponse.builder()
            .orderId(order.getId())
            .orderStatus(order.getOrderStatus())
            .amount(order.getAmount())
            .orderDate(order.getOrderDate())
            .productDetails(productDetails)
            .paymentDetails(paymentDetails)
            .build();

    return orderResponse;
  }
}
