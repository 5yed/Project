import menuSectionStyles from './MenuSection.module.css';
import OrderItem from '../components/OrderItem';

export default function OrderSummary({ order }) {
  return (
    <div className={menuSectionStyles.basket}>
      <div className={menuSectionStyles.basket__label}>
        <h2>Review Order</h2>
      </div>
      <div className={menuSectionStyles.basket__surface}>
        <div className={menuSectionStyles.basket__items}>
          {order.orderItems.map((item) => (
            <OrderItem
              key={item.id}
              item={item}
              readOnly={true}
            />
          ))}
        </div>
        <div className={menuSectionStyles.basket__bottomWrapper}>
          <div className={menuSectionStyles.basket__price}>
            <h2>Total: £{(order?.amountTotal || 0).toFixed(2)}</h2>
          </div>
          <div className={menuSectionStyles.basket__separator}/>
          <div
            className={menuSectionStyles.basket__checkout}
          >
            Checkout
          </div>
        </div>
      </div>
    </div>
  )
}
