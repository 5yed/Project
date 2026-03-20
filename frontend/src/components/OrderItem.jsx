import styles from './OrderItem.module.css'

export default function OrderItem({item, onIncrement, onDecrement, readOnly=false}) {
  return(
    <div className={styles.orderItem}>
      <div className={styles.titleRow}>
        <h2 className={styles.itemName}>{item.menuItem.name}</h2>
        <div className={styles.quantityContainer}>
          {!readOnly &&
            <button
              className={styles.quantityBtn}
              onClick={onIncrement}
            >+</button>
          }
          <span className={styles.quantityValue}>{item.quantity}</span>
          {!readOnly &&
            <button
              className={styles.quantityBtn}
              onClick={onDecrement}
            >-</button>
          }
        </div>
      </div>
      <p className={styles.priceRow}>£{(item.menuItem.price * item.quantity).toFixed(2)}</p>
    </div>
  )
}
