package com.stringsandtones.ProductService.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

  @Id
  @SequenceGenerator(name = "product_id_sequence", sequenceName = "product_id_sequence")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_id_sequence")
  private long productId;

  @Column(name = "PRODUCT_NAME")
  private String productName;

  @Column(name = "PRICE")
  private long price;

  @Column(name = "QUANTITY")
  private long quantity;
}
