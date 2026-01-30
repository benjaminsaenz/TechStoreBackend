package com.techstore.api.dto;

import java.util.List;

public class CreateOrderRequest {
  public Long userId;
  public List<Item> items;

  public static class Item {
    public Long productId;
    public Integer qty;
  }
}
