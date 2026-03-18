import { Link } from 'react-router-dom';
import styles from './TableSelectionPage.module.css';
import { useState } from 'react';

export default function TableSelectionPage() {
  const [continueAvailable, setContinueAvailable] = useState(false);

  const checkTableNumberInput = (input) => {
    if(input != "" && parseInt(input) > 0 && parseInt(input) <= 30) { // 30 is max number of tables, can be changed
      setContinueAvailable(true);
    }
    else {
      setContinueAvailable(false);
    }
  }
  
  return (
    <div className={styles.tableSelection}>
      <div className={styles.tableSelection__inner}>
        <div className={styles.tableSelection__welcome}>
          <h2>Welcome to Oaxaca!</h2>
        </div>
        <div className={styles.tableSelection__instruction}>
          <p>Please enter your table number below</p>
        </div>
        <div className={styles.tableSelection__grid}>
          <div className={styles.tableSelection__inputWrapper}>
            <label htmlFor="tableNumber">Table number:</label>
            <input
              className={styles.tableSelection__input} id="tableNumber" name="tableNumber"
              type="number" pattern="[0-9]"
              onChange={(e) => checkTableNumberInput(e.target.value)}/>
          </div>
          <div className={styles.tableSelection__continueWrapper}>
            <Link
              className={continueAvailable ? styles.tableSelection__continue : styles['tableSelection__continue--disabled']}
              to="/menu">
                Continue
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}