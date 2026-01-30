package com.techstore.api.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "order_code", nullable = false, unique = true)
  private String orderCode;


  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private Status status = Status.PENDING;

  @Column(nullable = false)
  private Integer total = 0;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  public enum Status { PENDING, APPROVED, REJECTED, PAYMENT_ERROR }

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getOrderCode() { return orderCode; }
  public void setOrderCode(String orderCode) { this.orderCode = orderCode; }

  public User getUser() { return user; }
  public void setUser(User user) { this.user = user; }

  public Status getStatus() { return status; }
  public void setStatus(Status status) { this.status = status; }

  public Integer getTotal() { return total; }
  public void setTotal(Integer total) { this.total = total; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }
}
