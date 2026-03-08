import styles from './MenuItem.module.css';

export default function MenuItem({item, onClick}) {
  return(
    <>
      <div className={styles.menuItem} onClick={onClick}>
        <div className={styles.imgContainer}>
          <img src={`${import.meta.env.VITE_BACKEND_URL}/${item.image}`}/>
        </div>
        <div className={styles.menuItemText}>
          <h2 className={styles.menuItemName}>{item.name}</h2>
          <p className={styles.alignLeft}>{item.description}</p>
          <h3 className={styles.caloriesText}>{item.kcal} kcal</h3>
          <div className={styles.menuItemPrice}>
            <h2>£{item.price}</h2> 
          </div>
        </div>
      </div>
    </>
  )
}
