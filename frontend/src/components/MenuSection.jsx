import styles from './MenuSection.module.css';
import MenuItem from '../components/MenuItem';
import OrderItem from '../components/OrderItem';
import { useMenu } from '../hooks/useMenu';
import { useState } from 'react';
import api from '../tools/api';

export default function MenuSection() {
  const { menuItems, setMenuItems } = useMenu();
  const [basket, setBasket] = useState();
  const [orderItems, setOrderItems] = useState([]);
  const [counter, setCounter] = useState(0);

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

  return (
    <div className={styles.mainContentsContainer}>
      <div className={styles.leftContainer}>
        <div className={styles.containerLabel}>
          <h2>Menu</h2>
        </div>
        <div className={styles.menuItems}>
          {menuItems.map((item) => (
            <MenuItem
              key={item.id}
              item={item}
              onClick={() => handleAddOrderItem(item)}
            />
          ))}
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
      </div>
    </div>
  )
}
