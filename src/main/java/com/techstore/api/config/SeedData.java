package com.techstore.api.config;

import com.techstore.api.model.Product;
import com.techstore.api.model.User;
import com.techstore.api.repo.ProductRepository;
import com.techstore.api.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {

  @Bean
  CommandLineRunner seed(UserRepository userRepo, ProductRepository productRepo, PasswordEncoder passwordEncoder) {
    return args -> {
      // -------------------------------------------------------------------
      // Usuarios demo (idempotentes)
      // -------------------------------------------------------------------

      // Admin demo
      userRepo.findByEmail("admin@techstore.cl").orElseGet(() -> {
        User admin = new User();
        admin.setName("Admin TechStore");
        admin.setEmail("admin@techstore.cl");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setAddress("Oficina");
        admin.setRole(User.Role.ADMIN);
        return userRepo.save(admin);
      });

      // Cliente demo (para login rápido en presentaciones)
      userRepo.findByEmail("cliente@techstore.cl").orElseGet(() -> {
        User demo = new User();
        demo.setName("Cliente Demo");
        demo.setEmail("cliente@techstore.cl");
        demo.setPasswordHash(passwordEncoder.encode("Cliente123"));
        demo.setAddress("Av. Calle Falsa 123");
        demo.setRole(User.Role.USER);
        return userRepo.save(demo);
      });

      // Si existen usuarios con password en texto plano (o no bcrypt), re-encodeamos.
      // Evita el error: "encoded password does not look like BCrypt".
      userRepo.findAll().forEach(u -> {
        String hash = u.getPasswordHash();
        if (hash != null && !hash.startsWith("$2")) {
          u.setPasswordHash(passwordEncoder.encode(hash));
          userRepo.save(u);
        }
      });

      // -------------------------------------------------------------------
      // Productos demo si está vacío
      // -------------------------------------------------------------------
      if (productRepo.count() == 0) {
        Product p1 = new Product();
        p1.setSku("MOUSE-001");
        p1.setName("Mouse Gamer");
        p1.setCategory("mouse");
        p1.setPrice(19990);
        p1.setStock(20);
        p1.setImageUrl("/img/mouse.jpg");
        p1.setDescription("Mouse gamer de ejemplo (luego lo editarás con CKEditor).");
        productRepo.save(p1);

        Product p2 = new Product();
        p2.setSku("TECL-001");
        p2.setName("Teclado Gamer");
        p2.setCategory("teclado");
        p2.setPrice(39990);
        p2.setStock(10);
        p2.setImageUrl("/img/teclado1.jpg");
        p2.setDescription("Teclado mecánico de ejemplo.");
        productRepo.save(p2);
      }
    };
  }
}
