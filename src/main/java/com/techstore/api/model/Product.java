package com.techstore.api.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "sku", nullable = false, unique = true, length = 60)
  private String sku;

  @Column(nullable = false, length = 160)
  private String name;

  @Column(nullable = false, length = 80)
  private String category;

  @Column(nullable = false)
  private Integer price;

  @Column(nullable = false)
  private Integer stock = 0;

  @Column(name = "image_url", length = 255)
  private String imageUrl;

  @Lob
  @Column(name = "description_html", columnDefinition = "LONGTEXT")
  private String descriptionHtml;

  @Column(name = "active", nullable = false, columnDefinition = "TINYINT")
  private Boolean active;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt = Instant.now();

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getSku() { return sku; }
  public void setSku(String sku) { this.sku = sku; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }

  public Integer getPrice() { return price; }
  public void setPrice(Integer price) { this.price = price; }

  public Integer getStock() { return stock; }
  public void setStock(Integer stock) { this.stock = stock; }

  public String getImageUrl() { return imageUrl; }
  public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

  public String getDescription() { return descriptionHtml; }
  public void setDescription(String description) { this.descriptionHtml = description; }

  public Boolean getActive() { return active; }
  public void setActive(Boolean active) { this.active = active; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
