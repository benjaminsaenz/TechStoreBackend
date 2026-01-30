-- =========================================
-- RESET + BD + USER APP
-- =========================================
DROP DATABASE IF EXISTS techstore_db;
CREATE DATABASE techstore_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

DROP USER IF EXISTS 'techstore_user'@'localhost';
CREATE USER 'techstore_user'@'localhost' IDENTIFIED BY 'Techstore123!';
GRANT ALL PRIVILEGES ON techstore_db.* TO 'techstore_user'@'localhost';
FLUSH PRIVILEGES;

USE techstore_db;

-- =========================================
-- TABLAS
-- =========================================

-- USERS: email normalizado + único real (sin duplicados con mayúsculas/espacios)
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(180) NOT NULL,
  email_norm VARCHAR(180)
    GENERATED ALWAYS AS (LOWER(TRIM(email))) STORED,
  password_hash VARCHAR(255) NOT NULL,
  address VARCHAR(255) NULL,
  role VARCHAR(30) NOT NULL DEFAULT 'USER',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT chk_users_email_not_blank CHECK (LENGTH(TRIM(email)) > 0),
  UNIQUE KEY uq_users_email_norm (email_norm)
);

-- PRODUCTS
CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sku VARCHAR(60) NOT NULL UNIQUE,
  name VARCHAR(160) NOT NULL,
  category VARCHAR(80) NOT NULL,
  price INT NOT NULL,
  stock INT NOT NULL DEFAULT 0,
  image_url VARCHAR(255) NULL,
  features JSON NULL,
  on_sale BIT(1) NOT NULL DEFAULT b'0',
  sale_percent INT NOT NULL DEFAULT 0,
  description_html LONGTEXT NULL,
  active BIT(1) NOT NULL DEFAULT b'1',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ORDERS
CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_code VARCHAR(40) NOT NULL UNIQUE,
  user_id BIGINT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE',
  reason VARCHAR(255) NULL,
  total INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ORDER_ITEMS
CREATE TABLE order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NULL,
  product_name VARCHAR(160) NOT NULL,
  unit_price INT NOT NULL,
  qty INT NOT NULL,
  subtotal INT NOT NULL,
  CONSTRAINT fk_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- =========================================
-- DATOS DEMO (sin romper bcrypt)
-- =========================================
-- Insertamos solo si NO existen (por email_norm). No pisamos password_hash si ya existe.
INSERT INTO users (name, email, password_hash, address, role)
SELECT 'Admin TechStore', 'admin@techstore.cl', 'admin123', '—', 'ADMIN'
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email_norm = LOWER(TRIM('admin@techstore.cl'))
);

INSERT INTO users (name, email, password_hash, address, role)
SELECT 'Cliente Demo', 'cliente@techstore.cl', 'cliente123', 'Santiago', 'USER'
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email_norm = LOWER(TRIM('cliente@techstore.cl'))
);

-- PRODUCTS (idempotente por sku)
INSERT INTO products
(sku, name, category, price, stock, image_url, features, on_sale, sale_percent, description_html, active)
VALUES
('TS-TECLADO1','Teclado Gamer','teclado',39990,20,'/img/teclado1.jpg',
 JSON_ARRAY('Mecánico','RGB'), b'0', 0, '<p>Teclado Gamer</p>', b'1'),
('TS-MOUSE1','Mouse Gamer','mouse',19990,20,'/img/mouse.jpg',
 JSON_ARRAY('Inalámbrico','RGB'), b'1', 10, '<p>Mouse Gamer</p>', b'1'),
('TS-AUDIFONOS1','Audífonos Gamer','audifonos',29990,20,'/img/audifonos1.jpg',
 JSON_ARRAY('Sonido 7.1'), b'0', 0, '<p>Audífonos Gamer</p>', b'1'),
('TS-MONITOR1','Monitor 24" Gamer','monitor',159990,20,'/img/monitor.jpg',
 NULL, b'0', 0, '<p>Monitor 24" Gamer</p>', b'1'),
('TS-WEBCAM1','Webcam HD','webcam',24990,20,'/img/webcam1.jpg',
 NULL, b'0', 0, '<p>Webcam HD</p>', b'1'),
('TS-SSD1','SSD NVMe 500GB','ssd',49990,20,'/img/ssd1.jpg',
 NULL, b'0', 0, '<p>SSD NVMe 500GB</p>', b'1'),
('TS-SILLA2','Silla Gamer','silla',129990,20,'/img/silla2.jpg',
 NULL, b'0', 0, '<p>Silla Gamer</p>', b'1'),
('TS-PARLANTES1','Parlantes RGB','parlantes',34990,20,'/img/parlantes1.webp',
 NULL, b'0', 0, '<p>Parlantes RGB</p>', b'1')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  category = VALUES(category),
  price = VALUES(price),
  stock = VALUES(stock),
  image_url = VALUES(image_url),
  features = VALUES(features),
  on_sale = VALUES(on_sale),
  sale_percent = VALUES(sale_percent),
  description_html = VALUES(description_html),
  active = VALUES(active);
