package com.techstore.api.controller;

import com.techstore.api.model.Product;
import com.techstore.api.repo.ProductRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductRepository productRepo;

  public ProductController(ProductRepository productRepo) {
    this.productRepo = productRepo;
  }

  @GetMapping
  public List<Product> list() {
    return productRepo.findAll()
      .stream()
      .filter(p -> Boolean.TRUE.equals(p.getActive()))
      .toList();
  }

  @GetMapping("/{id}")
  public Product byId(@PathVariable Long id) {
    return productRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
  }
}
