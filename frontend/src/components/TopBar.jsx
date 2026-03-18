import { Link } from "react-router-dom";
import styles from './TopBar.module.css';
import { useState } from "react";

export default function TopBar(props) {  
  const [showNotifications, setShowNotifications] = useState(false);

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
            <Link className={styles.navButton} to="/menu">Customer Page</Link>
            <Link className={styles.navButton} to="/staffPage">Staff Page</Link>
          </div>
        </div>

        <div className={styles.topBarRow2}>
          {props.bottomRow}

          {/* Alerts button (right side) */}
          <div className={styles.alertWrapper}>
            <button
              className={styles.alertButton}
              onClick={() => setShowNotifications(!showNotifications)}
            >
              Active Alerts: {props.alertCount ?? 0}
            </button>

            {/* Dropdown */}
            {showNotifications && (
              <div className={styles.alertDropdown}>
                <div className={styles.dropdownTitle}>Resolved Waiter Calls</div>

                {props.resolvedAlerts && props.resolvedAlerts.length > 0 ? (
                  props.resolvedAlerts.map((alert) => (
                    <div key={alert.id} className={styles.dropdownItem}>
                      Table {alert.tableId} waiter call resolved
                    </div>
                  ))
                ) : (
                  <div className={styles.emptyMessage}>No resolved alerts yet.</div>
                )}
              </div>
            )}
          </div>

        </div>
      </ div>
    </>
  )
}