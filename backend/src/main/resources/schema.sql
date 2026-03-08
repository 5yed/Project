
-- TODO: implement all the tables from schema_diagram
-- TEMP: schema link: https://drive.google.com/file/d/1NpzVhazsFnSbN60sZ5PkFwXD40gQiHZt/view?usp=sharing


DROP TABLE IF EXISTS menu_item_category CASCADE;
DROP TABLE IF EXISTS menu_item CASCADE;
DROP TABLE IF EXISTS restaurant_table CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS order_item CASCADE;
DROP TABLE IF EXISTS menu_item_allergens CASCADE;
DROP TABLE IF EXISTS menu_item_dietary_restriction CASCADE;
DROP TABLE IF EXISTS dietary_restriction CASCADE;


CREATE TYPE menu_item_status AS ENUM (
  'available',
  'out_of_stock',
  'hidden'
);

CREATE TYPE order_status AS ENUM (
  'creating',
  'placed',
  'confirmed',
  'in_progress',
  'ready',
  'delivered',
  'cancelled'
);

CREATE TABLE menu_item_category (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE menu_item (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL,
  description TEXT,

  price NUMERIC(10,2) NOT NULL CHECK (price >= 0),

  image TEXT,

  kcal NUMERIC(6,2) CHECK (kcal >= 0),
  fat NUMERIC(6,2) CHECK (fat >= 0),
  protein NUMERIC(6,2) CHECK (protein >= 0),
  carbs NUMERIC(6,2) CHECK (carbs >= 0),

  status menu_item_status NOT NULL DEFAULT 'available',

  category_id INTEGER NOT NULL,
  CONSTRAINT fk_menu_item_category
    FOREIGN KEY (category_id)
    REFERENCES menu_item_category(id)
);

CREATE TABLE restaurant_table (
  id SERIAL PRIMARY KEY,
  table_number INTEGER NOT NULL UNIQUE
);

CREATE TABLE orders (
  id SERIAL PRIMARY KEY,
  table_id INTEGER NOT NULL,

  status order_status NOT NULL DEFAULT 'placed',

  amount_total NUMERIC(10,2) NOT NULL CHECK (amount_total >= 0),
  amount_paid NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (amount_paid >= 0),

  ordered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  started_at TIMESTAMP,
  ready_at TIMESTAMP,
  delivered_at TIMESTAMP,
  cancelled_at TIMESTAMP,

  CONSTRAINT fk_orders_table
    FOREIGN KEY (table_id)
    REFERENCES restaurant_table(id)
);

CREATE TABLE order_item (
  id SERIAL PRIMARY KEY,

  order_id INTEGER NOT NULL,
  menu_item_id INTEGER NOT NULL,

  quantity INTEGER NOT NULL CHECK (quantity > 0),
  available_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
  special_note TEXT,

  CONSTRAINT fk_order_item_order
    FOREIGN KEY (order_id)
    REFERENCES orders(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_order_item_menu_item
    FOREIGN KEY (menu_item_id)
    REFERENCES menu_item(id)
);

CREATE TABLE menu_item_allergens (
  menu_item_id INTEGER NOT NULL,
  allergen_id INTEGER NOT NULL,
  PRIMARY KEY (menu_item_id, allergen_id),
  FOREIGN KEY (menu_item_id) REFERENCES menu_item(id)
);

CREATE TABLE dietary_restriction (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL UNIQUE
);

CREATE TABLE menu_item_dietary_restriction (
  menu_item_id INTEGER NOT NULL,
  dietary_restriction_id INTEGER NOT NULL,

  PRIMARY KEY(menu_item_id, dietary_restriction_id),

  CONSTRAINT fk_menu_item_dietary_restriction_menu_item
    FOREIGN KEY (menu_item_id)
    REFERENCES menu_item(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_menu_item_dietary_restriction_dietary_restriction
    FOREIGN KEY(dietary_restriction_id)
    REFERENCES dietary_restriction(id)
    ON DELETE CASCADE
);
