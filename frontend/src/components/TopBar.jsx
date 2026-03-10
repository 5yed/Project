import { Link } from "react-router-dom";
import styles from './TopBar.module.css';

export default function TopBar(props) {  
  return (
    <>
      {/* top bar */}
      <div className={styles.topBarContainer}>
        <div className={styles.topBarRow1}>
          <div>
            <p>placeholder</p>
          </div>
          {/* Restaurant name */}
          <div>
            <h1 className={styles.restaurantName}>OAXACA</h1>
          </div>
          {/* log in buttons */}
          <div className={styles.navBar}>
            <Link className={styles.navButton} to="/">Customer Page</Link>
            <Link className={styles.navButton} to="/staffPage">Staff Page</Link>
          </div>
        </div>
        <div className={styles.topBarRow2}>
          {props.bottomRow}
        </div>
      </ div>
    </>
  )
}
