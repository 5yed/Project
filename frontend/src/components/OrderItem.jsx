import styles from './OrderItem.module.css'

export default function OrderItem({item}) {
  return(
    <div className={styles.orderItem}>
      <div>
        <h2 className={styles.orderItemName}>{item.menuItem.name}</h2>
      </div>
      <div>
        {/* placeholder*/}
        <p className={styles.orderItemPrice}>£{item.menuItem.price}</p>
      </div>
    </div>
  )
}
