package com.techstore.api.dto;

import java.time.Instant;
import java.util.List;

public class OrderResponse {
  public Long id;
  public String orderCode;
  public String status;
  public Integer total;
  public Instant createdAt;

  public Customer customer;
  public List<Item> items;

  public static class Customer {
    public Long id;
    public String name;
    public String email;
    public String address;
  }

  public static class Item {
    public Long productId;
    public String name;
    public Integer price;
    public Integer qty;
    public Integer subtotal;
  }
}
