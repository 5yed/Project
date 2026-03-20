import styles from './MenuItem.module.css';

export default function MenuItem({item, onClick}) {
  return(
    <>
      <div className={styles.menuItem} onClick={onClick}>
        <div className={styles.imgContainer}>
          <img src={`${import.meta.env.VITE_BACKEND_URL}/${item.image}`}/>
        </div>
        <div className={styles.menuItemText}>
          <div className={styles.menuItemNameRow}>
            <h2 className={styles.menuItemName}>{item.name}</h2>
            <h3 className={styles.caloriesText}>{item.kcal} kcal</h3>
          </div>
          <p className={styles.alignLeft}>{item.description}</p>
          <div className={styles.menuItemPrice}>
            <h2>£{item.price}</h2> 
          </div>
        </div>
      </div>
    </>
  )
}
