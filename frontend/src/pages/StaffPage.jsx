import '../App.css'
import { Link } from 'react-router-dom';
import { Outlet } from 'react-router-dom';
import menuSectionStyles from '../components/MenuSection.module.css';
import styles from './StaffPage.module.css';
import basePageStyles from './BasePage.module.css';
import api from '../tools/api';

import TopBar from "../components/TopBar"

import { useState, useEffect, useRef } from "react"
import { useMenu } from '../hooks/useMenu';

export default function StaffPage() {
  const {menuItems, setMenuItems} = useMenu();

  const [selectedOrder, setSelectedOrder] = useState(null); 

  const [editOrderPopupStatus, setEditOrderPopupStatus] = useState(false); 
  const [orderNotifications, setOrderNotifications] = useState([]);

  const [loading, setLoading] = useState(false);
  const [alerts, setAlerts] = useState([]);
  const [resolvedAlerts, setResolvedAlerts] = useState([]);

  const fetchAlerts = async () => {
    try {
      const res = await api.get("/api/alerts/active");
      setAlerts(res.data);
    } catch (err) {
      console.error("Failed to load alerts:", err);
    }
  };

  const fetchResolvedAlerts = async () => {
      try {
          const res = await api.get("/api/alerts/resolved/all");
          setResolvedAlerts(res.data);
      } catch (err) {
          console.error("Failed to load resolved alerts:", err);
      }
  };

  const resolveAlert = async (id) => {
    try {
      await api.patch(`/api/alerts/${id}/resolve`);
      fetchAlerts();
      fetchResolvedAlerts();
    } catch (err) {
      console.error("Failed to resolve alert:", err);
    }
  };


  useEffect(() => {
    fetchAlerts();
    fetchResolvedAlerts();

    const alertsInterval = setInterval(() => {
      fetchAlerts();
    }, 5000);

    return () => {
      clearInterval(alertsInterval);
    };
  }, []);

  if (loading) {
    return (
      <>
        <TopBar />
        <div style={{ padding: '50px', textAlign: 'center' }}>
          Loading orders...
        </div>
      </>
    );
  }
  
  return (
    <>
      <TopBar 
        alertCount = {alerts.length}
        resolvedAlerts={resolvedAlerts}
        bottomRow={
          <>
            <div className={basePageStyles.sectionLabel}>
              <Link className={styles.sectionButton} to="/staffPage/menu">Menu</Link>
            </div>
            <div className={basePageStyles.labelSeparator}/>
            <div className={basePageStyles.sectionLabel}>
              <Link className={styles.sectionButton} to="/staffPage/orders">Orders</Link>
            </div>
          </>
        }
      />

      {orderNotifications.length > 0 && (
        <div
          style={{
            padding: "12px",
            background: "#e6ffe6",
            borderBottom: "1px solid #ccc"
          }}
        >
          <strong>Notifications</strong>
          {orderNotifications.map((msg, index) => (
            <div
              key={index}
              style={{
                position: "relative",
                marginTop: "5px",
                textAlign: "center"
              }}
            >
              <span>{msg}</span>

              <button
                onClick={() =>
                  setOrderNotifications(prev =>
                    prev.filter((_, i) => i !== index)
                  )
                }
                style={{
                  position: "absolute",
                  right: "0",
                  top: "50%",
                  transform: "translateY(-50%)",
                  cursor: "pointer",
                  border: "none",
                  background: "transparent",
                  fontWeight: "bold"
                }}
              >
                ✖
              </button>
            </div>
          ))}
        </div>
      )}

      {alerts.length > 0 && (
        <div
          style={{
            background: "#ffdddd",
            border: "2px solid red",
            padding: "15px",
            margin: "10px"
          }}
        >
          <h3>⚠ Waiter Calls</h3>

          {alerts.map((alert) => (
            <div key={alert.id} style={{ marginBottom: "10px" }}>
              Table {alert.tableId} is calling a waiter!

              <button
                style={{ marginLeft: "10px" }}
                onClick={async () => {
                  await api.patch(`/api/alerts/${alert.id}/resolve`);
                  fetchAlerts();
                }}
              >
                Mark Done
              </button>
            </div>
          ))}
        </div>
      )}
      <Outlet context={{
        setLoading,
        selectedOrder,
        setSelectedOrder,
        editOrderPopupStatus,
        setEditOrderPopupStatus,
        setOrderNotifications,
      }} />
    </>
  );
}
