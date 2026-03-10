INSERT INTO menu_item_category (name) VALUES
  ('Starters'),
  ('Main Courses'),
  ('Desserts'),
  ('Drinks');

INSERT INTO menu_item (
  name, description, image,
  price, kcal, fat, protein, carbs,
  status, category_id
) VALUES
-- Starters
('Garlic Bread', 'Toasted bread with garlic butter', 'files/menu-items/Garlic_Bread.jpg', 4.50, 250, 8, 6, 30, 'available', 1),
('Tomato Soup', 'Fresh tomatoes with herbs',  'files/menu-items/Tomato_Soup.jpg', 5.00, 180, 4, 5, 22, 'available', 1),

-- Main courses
('Margherita Pizza', 'Classic pizza with mozzarella and basil', 'files/menu-items/Margherita_Pizza.jpg', 11.00, 850, 22, 30, 90, 'available', 2),
('Cheeseburger', 'Beef burger with cheese and fries', 'files/menu-items/Cheeseburger.jpg', 13.50, 980, 45, 38, 70, 'available', 2),
('Grilled Chicken', 'Chicken breast with vegetables', 'files/menu-items/Grilled_Chicken.jpg', 14.00, 620, 12, 48, 25, 'out_of_stock', 2),

-- Desserts
('Chocolate Cake', 'Rich chocolate layered cake', 'files/menu-items/Chocolate_Cake.jpg', 6.50, 520, 28, 7, 55, 'available', 3),
('Vanilla Ice Cream', 'Homemade vanilla ice cream', 'files/menu-items/Vanilla_Ice_Cream.jpg', 4.00, 210, 11, 4, 22, 'available', 3),

-- Drinks
('Coca-Cola', 'Chilled soft drink', 'files/menu-items/Coca-Cola.jpg', 2.50, 140, 0, 0, 35, 'available', 4),
('Espresso', 'Strong Italian coffee', 'files/menu-items/Espresso.jpg', 2.00, 5, 0, 1, 0, 'available', 4);
INSERT INTO restaurant_table (table_number) VALUES
  (1),
  (2),
  (3),
  (4),
  (5);

INSERT INTO orders (
  table_id,
  status,
  amount_total,
  amount_paid,
  ordered_at,
  started_at,
  ready_at
) VALUES
-- Table 1, active order
(
  1,
  'in_progress',
  17.50,
  0,
  DATEADD('MINUTE', -15, CURRENT_TIMESTAMP),
  DATEADD('MINUTE', -10, CURRENT_TIMESTAMP),
  NULL
),

-- Table 2, completed order
(
  2,
  'delivered',
  20.00,
  20.00,
  DATEADD('HOUR', -1, CURRENT_TIMESTAMP),
  DATEADD('MINUTE', -50, CURRENT_TIMESTAMP),
  DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)
),

-- Table 3, just placed
(
  3,
  'placed',
  6.50,
  0,
  CURRENT_TIMESTAMP,
  NULL,
  NULL
);


INSERT INTO order_item (
  order_id,
  menu_item_id,
  quantity,
  available_confirmed,
  special_note
) VALUES
-- Order 1 (table 1)
(1, 1, 1, TRUE, 'Extra garlic'),
(1, 3, 1, TRUE, NULL),

-- Order 2 (table 2)
(2, 4, 1, TRUE, 'No onions'),
(2, 8, 2, TRUE, NULL),

-- Order 3 (table 3)
(3, 6, 1, FALSE, 'Birthday candle please');


INSERT INTO menu_item_allergens (menu_item_id, allergen_id) VALUES
(1, 2), -- Garlic Bread, gluten
(1, 7), -- Garlic Bread, milk
(3, 2), -- Margherita Pizza, gluten
(3, 7), -- Margherita Pizza, milk
(4, 2), -- Cheeseburger, gluten
(4, 7), -- Cheeseburger, milk
(4, 4), -- Cheeseburger, eggs
(6, 2), -- Chocolate Cake, gluten
(6, 7), -- Chocolate Cake, milk
(6, 4), -- Chocolate Cake, eggs
(6, 13), -- Chocolate Cake, soya
(7, 7), -- Vanilla Ice Cream, milk
(7, 4); -- Vanilla Ice Cream, eggs

INSERT INTO dietary_restriction (name) VALUES
('Vegan'),
('Vegetarian'),
('Gluten-free'),
('Dairy-free'),
('Nut-free');
