import { Link, useNavigate } from "react-router-dom";
import styles from './MenuSection.module.css';
import MenuItem from '../components/MenuItem';
import OrderItem from '../components/OrderItem';
import { useMenu } from '../hooks/useMenu';
import { useState, useEffect } from 'react';
import api from '../tools/api';

export default function MenuSection() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');

  const { menuItems, setMenuItems } = useMenu();
  const [basket, setBasket] = useState();
  const [orderItems, setOrderItems] = useState([]);

  // hardcoded directly from sql
  const categories = [
    { id: 1, name: 'Starters' },
    { id: 2, name: 'Main Courses' },
    { id: 3, name: 'Desserts' },
    { id: 4, name: 'Drinks' }
  ];

  // ive added category id since backend cannot be fetched
  const itemsWithCategories = menuItems.map(item => {
    // create copy of id
    const itemWithCat = { ...item };

    // add category directly from id
    if (item.name === 'Garlic Bread' || item.name === 'Tomato Soup') {
      itemWithCat.categoryId = 1; // starters
    } else if (item.name === 'Margherita Pizza' || item.name === 'Cheeseburger' || item.name === 'Grilled Chicken') {
      itemWithCat.categoryId = 2; // main course
    } else if (item.name === 'Chocolate Cake' || item.name === 'Vanilla Ice Cream') {
      itemWithCat.categoryId = 3; // desserts 
    } else if (item.name === 'Coca-Cola' || item.name === 'Espresso') {
      itemWithCat.categoryId = 4; // drink
    }

    return itemWithCat;
  });

  // Filter menu items based on search term AND category
  const filteredItems = itemsWithCategories.filter(item => {
    // Search filter
    const matchesSearch = !searchTerm ||
      (item.name && item.name.toLowerCase().includes(searchTerm.toLowerCase())) ||
      (item.description && item.description.toLowerCase().includes(searchTerm.toLowerCase()));

    // Category filter
    const matchesCategory = selectedCategory === 'all' ||
      Number(item.categoryId) === Number(selectedCategory);

    return matchesSearch && matchesCategory;
  });

  const createBasket = async (tableId) => {
    const res = await api.post("/api/orders", {
      tableId: tableId,
      status: 'CREATING',
    });
    setBasket(res.data);
    return res.data;
  };

  const handleAddOrderItem = async (item) => {
    let _basket = basket;
    if (!_basket) {
      _basket = await createBasket(1);
    }

    const existingItem = orderItems.find((orderItem) => orderItem.menuItem.id === item.id);
    if (existingItem) {
      await incrementOrderItemQuantity(existingItem, _basket, 1);
    }
    else {
      const newItem = await addNewOrderItem(item, _basket);
      setOrderItems([...orderItems, newItem]);
    }

    const updatedOrder = await api.get(`api/orders/${_basket.id}`);
    setBasket(updatedOrder.data);
  };

  const addNewOrderItem = async (item, _basket) => {
    const res = await api.post("/api/orderItems", {
      orderId: _basket.id,
      menuItemId: item.id,
      quantity: 1,
    });
    return res.data;
  }

  const incrementOrderItemQuantity = async (existingItem, _basket, delta) => {
    const res = await api.post("/api/orderItems", {
      orderId: _basket.id,
      menuItemId: existingItem.menuItem.id,
      quantity: delta,
    });

    const updatedItem = res.data;
    setOrderItems((items) => (
      items.map((orderItem) => (
        orderItem.id === updatedItem.id ? updatedItem : orderItem
      ))
    ));
  }

  const canCheckout = basket && orderItems.length > 0;

  // category display name
  const getCategoryName = (id) => {
    const numId = Number(id);
    switch (numId) {
      case 1: return 'Starters';
      case 2: return 'Main Courses';
      case 3: return 'Desserts';
      case 4: return 'Drinks';
      default: return `Category ${id}`;
    }
  };

  const handleIncrement = async (item, delta) => {
    await incrementOrderItemQuantity(item, basket, delta);
    const updatedOrder = await api.get(`api/orders/${basket.id}`);
    setBasket(updatedOrder.data);
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.mainContentsContainer}>
        <div className={styles.leftContainer}>
          <div className={styles.filter}>
            <div className={styles.filter__searchWrapper}>
              <div className={styles.filter__searchBar}>
                <input
                  className={styles.filter__searchInput} type="text"
                  placeholder="Search menu..." value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                {searchTerm && (
                  <button
                    onClick={() => setSearchTerm('')}
                    className={styles.filter__searchClearButton}
                  >
                    ✕
                  </button>
                )}
              </div>
            </div>

            <div className={styles.filter__buttonsWrapper}>
              <button
                className={selectedCategory === 'all' ? styles['filter__button--active'] : styles.filter__button}
                onClick={() => setSelectedCategory('all')}
              >
                All Items
              </button>

              {categories.map(category => (
                <button
                  className={selectedCategory === category.id ? styles['filter__button--active'] : styles.filter__button}
                  key={category.id}
                  onClick={() => setSelectedCategory(category.id)}
                >
                  {category.name}
                </button>
              ))}
            </div>
          </div>
          <div className={styles.menuSurface}>
            <div className={styles.menuItems}>
              {filteredItems.length > 0 ? (
                filteredItems.map((item) => (
                  <MenuItem
                    key={item.id}
                    item={item}
                    onClick={() => handleAddOrderItem(item)}
                  />
                ))
              ) : (
                <p className={styles.noItemsText}>
                  No menu items found
                  {searchTerm && ` matching "${searchTerm}"`}
                  {selectedCategory !== 'all' && ` in ${getCategoryName(selectedCategory)}`}
                </p>
              )}
            </div>
          </div>
        </div>

        <div className={styles.basket}>
          <div className={styles.basket__label}>
            <h2>Review Order</h2>
          </div>
          <div className={styles.basket__surface}>
            <div className={styles.basket__items}>
              {orderItems.map((item) => (
                <OrderItem
                  key={item.id}
                  item={item}
                  onIncrement={() => handleIncrement(item, 1)}
                  onDecrement={() => handleIncrement(item, -1)}
                />
              ))}
            </div>
            <div className={styles.basket__bottomWrapper}>
              <div className={styles.basket__price}>
                <h2>Total: £{(basket?.amountTotal || 0).toFixed(2)}</h2>
              </div>
              <div className={styles.basket__separator}/>
              <Link
                className={canCheckout ? styles.basket__checkout : styles['basket__checkout--disabled']}
                to={`/menu/checkout/${basket?.id}`}
              >
                Checkout
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
