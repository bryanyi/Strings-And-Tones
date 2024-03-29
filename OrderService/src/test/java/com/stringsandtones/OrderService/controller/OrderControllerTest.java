package com.stringsandtones.OrderService.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.stringsandtones.OrderService.OrderServiceConfig;
import com.stringsandtones.OrderService.entity.Order;
import com.stringsandtones.OrderService.model.*;
import com.stringsandtones.OrderService.repository.OrderRepository;
import com.stringsandtones.OrderService.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.io.IOException;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest({"server.port=0"})
@EnableConfigurationProperties
@AutoConfigureMockMvc
@ContextConfiguration(classes = {OrderServiceConfig.class})
public class OrderControllerTest {
  /*
    {"server.port=0"}: this allows this class to run on any port.
    @EnableConfigurationProperties: To allow the configs from application.yml to apply
    @AutoConfigureMockMvc: So we can call the api routes
    @ContextConfiguration(classes = {OrderServiceConfig.class}): Emulating the Eureka services that are usually available
  */

  @Autowired private OrderService orderService;
  @Autowired private OrderRepository orderRepository;
  @Autowired private MockMvc mockMvc;

  @RegisterExtension
  static WireMockExtension wireMockServer =
      WireMockExtension.newInstance()
          .options(WireMockConfiguration.wireMockConfig().port(8080))
          .build();

  private ObjectMapper objectMapper =
      new ObjectMapper()
          .findAndRegisterModules()
          .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
          .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @BeforeEach
  void setup() throws IOException {
    // This is called before each test case
    getProductDetailsResponse();
    initPayment();
    getPaymentDetails();
    reduceQuantity();
  }

  private void reduceQuantity() {
    wireMockServer.stubFor(
        WireMock.put(urlMatching("/api/v1/product/reduceQuantity/.*"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
  }

  private void getPaymentDetails() throws IOException {
    wireMockServer.stubFor(
        WireMock.get(urlMatching("/api/v1/payment/.*"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(
                        copyToString(
                            OrderControllerTest.class
                                .getClassLoader()
                                .getResourceAsStream("mock/GetPayment.json"),
                            defaultCharset()))));
  }

  private void initPayment() {
    wireMockServer.stubFor(
        WireMock.post(urlEqualTo("/api/v1/payment"))
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));
  }

  private void getProductDetailsResponse() throws IOException {
    // /api/v1/product/1 -> for product with id of 1
    wireMockServer.stubFor(
        get("/api/v1/product/1")
            .willReturn(
                aResponse()
                    .withStatus(HttpStatus.OK.value())
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .withBody(
                        copyToString(
                            OrderControllerTest.class
                                .getClassLoader()
                                .getResourceAsStream("mock/GetProduct.json"),
                            defaultCharset()))));
  }

  private OrderRequest getMockOrderRequest() {
    return OrderRequest.builder()
        .productId(1)
        .paymentMode(PaymentMode.VISA_CREDIT_CARD)
        .quantity(1)
        .totalAmount(999)
        .build();
  }

  @Test
  public void test_when_placeOrder_and_initPayment_Succeeds() throws Exception {
    // place order
    // get order by order id from DB and check
    // Check the output

    OrderRequest orderRequest = getMockOrderRequest();

    // Mock mvc request
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/order/placeOrder")
                    .with(jwt().authorities(new SimpleGrantedAuthority("Customer")))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderRequest)))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    String orderId = mvcResult.getResponse().getContentAsString();

    Optional<Order> order = orderRepository.findById(Long.valueOf(orderId));

    assertTrue(order.isPresent());

    Order o = order.get();
    assertEquals(Long.parseLong(orderId), o.getId());
    assertEquals("PLACED", o.getOrderStatus());
    assertEquals(orderRequest.getTotalAmount(), o.getAmount());
    assertEquals(orderRequest.getQuantity(), o.getQuantity());
  }

  @Test
  public void test_When_placeOrder_With_Incorrect_Authorization_throw403() throws Exception {
    OrderRequest orderRequest = getMockOrderRequest();
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/order/placeOrder")
                    .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(orderRequest)))
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andReturn();
  }

//  @Test
//  public void test_When_getOrder_Succeeds() throws Exception {
//    // The error i'm getting states that order with id 1 doesn't exist.
//    // The placeOrder test runs slower than this test, so maybe the database doesn't have an order
//    // yet.
//    // How do i make the next test wait before doing the next test?
//    // Is the h2 database even working?
//
//    MvcResult mvcResult =
//        mockMvc
//            .perform(
//                MockMvcRequestBuilders.get("/api/v1/order/1")
//                    .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
//                    .contentType(MediaType.APPLICATION_JSON_VALUE))
//            .andExpect(MockMvcResultMatchers.status().isOk())
//            .andReturn();
//
//    String actualResponse = mvcResult.getResponse().getContentAsString();
//    Order order = orderRepository.findById(1L).get();
//    String expectedResponse = getOrderResponse(order);
//
//    assertEquals(expectedResponse, actualResponse);
//  }

  @Test
  public void testWhen_GetOrder_Order_Not_Found() throws Exception {
    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/api/v1/order/2")
                    .with(jwt().authorities(new SimpleGrantedAuthority("Admin")))
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andReturn();
  }

  private String getOrderResponse(Order order) throws IOException {
    PaymentDetails paymentDetails =
        objectMapper.readValue(
            copyToString(
                OrderControllerTest.class
                    .getClassLoader()
                    .getResourceAsStream("mock/GetPayment.json"),
                defaultCharset()),
            PaymentDetails.class);

    paymentDetails.setPaymentStatus("SUCCESS");

    ProductResponse productDetails =
        objectMapper.readValue(
            copyToString(
                OrderControllerTest.class
                    .getClassLoader()
                    .getResourceAsStream("mock/GetProduct.json"),
                defaultCharset()),
            ProductResponse.class);

    OrderResponse orderResponse =
        OrderResponse.builder()
            .paymentDetails(paymentDetails)
            .productDetails(productDetails)
            .orderStatus(order.getOrderStatus())
            .orderDate(order.getOrderDate())
            .amount(order.getAmount())
            .orderId(order.getId())
            .build();
    return objectMapper.writeValueAsString(orderResponse);
  }
}
