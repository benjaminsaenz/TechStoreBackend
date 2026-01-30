package com.techstore.api.controller;

import com.techstore.api.dto.CreateOrderRequest;
import com.techstore.api.dto.OrderResponse;
import com.techstore.api.model.Order;
import com.techstore.api.model.OrderItem;
import com.techstore.api.model.Product;
import com.techstore.api.model.User;
import com.techstore.api.repo.OrderRepository;
import com.techstore.api.repo.ProductRepository;
import com.techstore.api.repo.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

  private final UserRepository userRepo;
  private final ProductRepository productRepo;
  private final OrderRepository orderRepo;

  public OrderController(UserRepository userRepo, ProductRepository productRepo, OrderRepository orderRepo) {
    this.userRepo = userRepo;
    this.productRepo = productRepo;
    this.orderRepo = orderRepo;
  }

  @PostMapping
  public OrderResponse create(@RequestBody CreateOrderRequest req) {
    if (req == null || req.userId == null) throw new RuntimeException("Falta userId");
    if (req.items == null || req.items.isEmpty()) throw new RuntimeException("Carrito vacío");

    User user = userRepo.findById(req.userId)
      .orElseThrow(() -> new RuntimeException("Usuario no existe"));

    Order order = new Order();
    order.setUser(user);
    order.setOrderCode("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
    order.setStatus(Order.Status.PENDING);

    int total = 0;

    for (CreateOrderRequest.Item it : req.items) {
      if (it.qty == null || it.qty <= 0) continue;

      Product product = productRepo.findById(it.productId)
        .orElseThrow(() -> new RuntimeException("Producto no existe: " + it.productId));

      int price = product.getPrice() != null ? product.getPrice() : 0;
      int subtotal = price * it.qty;

      OrderItem item = new OrderItem();
      item.setOrder(order);
      item.setProduct(product);
      item.setQty(it.qty);
      item.setPrice(price);
      item.setSubtotal(subtotal);

      order.getItems().add(item);
      total += subtotal;
    }

    if (total <= 0) throw new RuntimeException("Total inválido");

    order.setTotal(total);

    Order saved = orderRepo.save(order);

    return toResponse(saved);
  }

  private OrderResponse toResponse(Order o) {
    OrderResponse res = new OrderResponse();
    res.id = o.getId();
    res.orderCode = o.getOrderCode();
    res.status = o.getStatus().name();
    res.total = o.getTotal();
    res.createdAt = o.getCreatedAt();

    res.customer = new OrderResponse.Customer();
    res.customer.id = o.getUser().getId();
    res.customer.name = o.getUser().getName();
    res.customer.email = o.getUser().getEmail();
    res.customer.address = o.getUser().getAddress();

    res.items = o.getItems().stream().map(oi -> {
      OrderResponse.Item item = new OrderResponse.Item();
      item.productId = oi.getProduct().getId();
      item.name = oi.getProduct().getName();
      item.price = oi.getPrice();
      item.qty = oi.getQty();
      item.subtotal = oi.getSubtotal();
      return item;
    }).toList();

    return res;
  }
}
