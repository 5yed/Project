import { useNavigate, useOutletContext } from "react-router-dom";
import styles from './MenuSection.module.css';
import MenuItem from '../components/MenuItem';
import OrderItem from '../components/OrderItem';
import { useMenu } from '../hooks/useMenu';
import { useState, useEffect } from 'react';
import api from '../tools/api';

export default function MenuSection() {
  const navigate = useNavigate();
  const { searchTerm, selectedCategory } = useOutletContext();

  const { menuItems, setMenuItems } = useMenu();
  const [basket, setBasket] = useState();
  const [orderItems, setOrderItems] = useState([]);
  const [counter, setCounter] = useState(0);

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
    const res = await api.post("/api/orderItems", {
      orderId: _basket.id,
      menuItemId: item.id,
      quantity: 1,
    });
    setOrderItems([...orderItems, res.data]);
  };

  const canCheckout = basket && orderItems.length > 0;

  // category display name
  const getCategoryName = (id) => {
    const numId = Number(id);
    switch(numId) {
      case 1: return 'Starters';
      case 2: return 'Main Courses';
      case 3: return 'Desserts';
      case 4: return 'Drinks';
      default: return `Category ${id}`;
    }
  };

  return (
    <div className={styles.mainContentsContainer}>
      <div className={styles.leftContainer}>
        <div className={styles.containerLabel}>
          <h2>Menu</h2>
          {(searchTerm || selectedCategory !== 'all') && (
            <p style={{ 
              fontSize: '14px', 
              color: '#666', 
              marginLeft: '20px',
              fontWeight: 'normal'
            }}>
              Found {filteredItems.length} items
              {selectedCategory !== 'all' && ` in ${getCategoryName(selectedCategory)}`}
              {searchTerm && ` matching "${searchTerm}"`}
            </p>
          )}
        </div>
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
            <p style={{ 
              textAlign: 'center', 
              padding: '40px', 
              color: '#999',
              width: '100%'
            }}>
              No menu items found
              {searchTerm && ` matching "${searchTerm}"`}
              {selectedCategory !== 'all' && ` in ${getCategoryName(selectedCategory)}`}
            </p>
          )}
        </div>
      </div>
      <div className={styles.basket}>
        <div className={styles.basketLabel}>
          <h2>Basket</h2>
        </div>
        <div className={styles.orderItems}>
          {orderItems.map((item) => (
            <OrderItem
              key={item.id}
              item={item}
            />
          ))}
        </div>
        <button
          className={styles.checkoutButton}
          disabled={!canCheckout}
          onClick={() => navigate(`/menu/checkout/${basket.id}`)}
        >
          Checkout
        </button>
      </div>
    </div>
  )
}
