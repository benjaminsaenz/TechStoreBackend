package com.techstore.api.controller;

import com.techstore.api.model.Product;
import com.techstore.api.repo.ProductRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

  private final ProductRepository productRepo;

  public AdminProductController(ProductRepository productRepo) {
    this.productRepo = productRepo;
  }

  @PostMapping
  public Product create(@RequestBody Product p) {
    if (p.getPrice() == null || p.getPrice() <= 0) throw new RuntimeException("Precio inválido");
    if (p.getStock() == null || p.getStock() < 0) throw new RuntimeException("Stock inválido");
    return productRepo.save(p);
  }

  @PutMapping("/{id}")
  public Product update(@PathVariable Long id, @RequestBody Product p) {
    Product current = productRepo.findById(id)
      .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

    current.setSku(p.getSku());
    current.setName(p.getName());
    current.setCategory(p.getCategory());
    current.setPrice(p.getPrice());
    current.setStock(p.getStock());
    current.setImageUrl(p.getImageUrl());
    current.setDescription(p.getDescription());
    current.setActive(p.getActive());

    return productRepo.save(current);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    productRepo.deleteById(id);
  }
    @GetMapping
  public List<Product> list() {
    return productRepo.findAll();
  }
}
