import '../App.css';
import { Outlet } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import TopBar from '../components/TopBar';
import styles from './HomePage.module.css';
import basePageStyles from './BasePage.module.css';
import api from '../tools/api';

export default function HomePage() {

  const [tableId, setTableId] = useState(1);
  const [activeRequest, setActiveRequest] = useState(false);
  const [notifications, setNotifications] = useState([]);

  const callWaiter = async (e) => {
    e.preventDefault();

    if (activeRequest) {
      alert("A waiter request is already active for this table.");
      return;
    }

    try {
      await api.post(`/api/alerts/call-waiter?tableId=${tableId}`);

      setActiveRequest(true);
      alert("Waiter called successfully");

    } catch (err) {

      console.error(err);

      if (err.response && err.response.data) {
        alert(err.response.data);
      } else {
        alert("Error calling waiter");
      }
    }
  };

  const fetchResolved = async () => {
    try {

      const res = await api.get(`/api/alerts/resolved?tableId=${tableId}`);

      setNotifications(res.data);

      if (res.data.length > 0) {
        setActiveRequest(false);
      }

    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {

    fetchResolved();

  }, [tableId]);

  return (
    <>
      <TopBar
        bottomRow={
          <>
            <div className={basePageStyles.sectionLabel}>
              <Link className={styles.sectionButton} to="/">Menu</Link>
            </div>

            <div className={basePageStyles.labelSeparator}/>

            <div className={basePageStyles.sectionLabel}>
              <Link className={styles.sectionButton} to="/checkout">Checkout</Link>
            </div>

            <div className={basePageStyles.labelSeparator}/>

            <div className={basePageStyles.sectionLabel}>
              <Link
                className={styles.sectionButton}
                to="#"
                onClick={callWaiter}
              >
                Call Waiter
              </Link>
            </div>

            <div className={basePageStyles.labelSeparator}/>

            <div className={basePageStyles.sectionLabel}>
              <select
                value={tableId}
                onChange={(e) => {
                  setTableId(e.target.value);
                  setActiveRequest(false);
                }}
                className={styles.sectionButton}
                style={{
                  border: "none",
                  background: "transparent",
                  font: "inherit",
                  cursor: "pointer"
                }}
              >
                <option value={1}>Table 1</option>
                <option value={2}>Table 2</option>
                <option value={3}>Table 3</option>
                <option value={4}>Table 4</option>
              </select>
            </div>
          </>
        }
      />

      {notifications.length > 0 && (
        <div
          style={{
            padding: "12px",
            background: "#e6ffe6",
            borderBottom: "1px solid #ccc"
          }}
        >
          <strong>Notifications</strong>

          {notifications.map((n) => (
            <div key={n.id}>
              Waiter responded to your request.
            </div>
          ))}
        </div>
      )}

      <Outlet/>
    </>
  );
}