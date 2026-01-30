package com.techstore.api.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "order_id")
  private Order order;

  @ManyToOne(optional = false)
  @JoinColumn(name = "product_id")
  private Product product;

  @Column(nullable = false)
  private Integer qty;

  @Column(nullable = false)
  private Integer unit_price;

  @Column(nullable = false)
  private Integer subtotal;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }

  public Product getProduct() { return product; }
  public void setProduct(Product product) { this.product = product; }

  public Integer getQty() { return qty; }
  public void setQty(Integer qty) { this.qty = qty; }

  public Integer getPrice() { return unit_price; }
  public void setPrice(Integer price) { this.unit_price = price; }

  public Integer getSubtotal() { return subtotal; }
  public void setSubtotal(Integer subtotal) { this.subtotal = subtotal; }
}
