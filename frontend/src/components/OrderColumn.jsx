import styles from './OrderColumn.module.css';
import { useEffect, useState } from "react";

const getStatusStyle = (status) => {
  switch (status) {
    case "PLACED":
      return { backgroundColor: "#F4C95D", color: "#000" };
    case "CONFIRMED":
      return { backgroundColor: "#E0A800", color: "#fff" };
    case "IN_PROGRESS":
      return { backgroundColor: "#3D6B35", color: "#fff" };
    case "READY":
      return { backgroundColor: "#2F5D2F", color: "#fff" };
    case "DELIVERED":
      return { backgroundColor: "#777", color: "#fff" };
    default:
      return { backgroundColor: "#ddd", color: "#000" };
  }
};

export default function OrderColumn({ 
  title, 
  orders = [], 
  backgroundColor = '#FFE28A', 
  onMove,
  onBack,
  onEdit,
  showBackButton = false,
  showEditButton = false
}) {
  // Timer
  const [now, setNow] = useState(Date.now());
  useEffect(() => {
    const interval = setInterval(() => {
      setNow(Date.now());
    }, 60000);

    return () => clearInterval(interval);

  }, []);
  
  const getElapsedMinutes = (timestamp) => {
    if (!timestamp) return null;
    const t = new Date(timestamp).getTime();
    return Math.max(0, Math.floor((now - t) / 60000));
  };

  const getTimeStyle = (minutes) => {
    if (minutes < 5) return { backgroundColor: "#F4C95D", color: "#000" };
    if (minutes < 15) return { backgroundColor: "#E08E00", color: "#fff" };
    return { backgroundColor: "#A4161A", color: "#fff" };
  };
  
  return (
    <div className={styles.container}>
      <h2 className={styles.columnTitle}>
        {title} ({orders.length})
      </h2>

      <div className={styles.orders}>
        {orders.length > 0 ? (
          [...orders]
            .sort((a, b) => {
              const aDelivered = a.status === "DELIVERED";
              const bDelivered = b.status === "DELIVERED";
              if (aDelivered === bDelivered) return 0;
              return aDelivered ? 1 : -1;
            })
            .map((order) => (
            <div
              key={order.id}
              className={styles.orderCard}
              style={{
                opacity: order.status === "DELIVERED" ? 0.6 : 1,
                filter: order.status === "DELIVERED" ? "grayscale(0.2)" : "none",
              }}
            >
              <div className={styles.orderHeader}>
                <h3 className={styles.orderTableNum}>
                  Table {order.tableId}
                </h3>

                <div className={styles.statusContainer}>
                  <span className={styles.status}
                    style={{...getStatusStyle(order.status)}}
                  >
                    {order.status.replace("_", " ")}
                  </span>
                </div>
              </div>

              {order.orderedAt && (
                <div className={styles.timeStatusContainer}>
                  <div 
                    className={styles.timeStatus}
                    style={{
                    ...getTimeStyle(getElapsedMinutes(order.orderedAt))
                  }}>
                    {getElapsedMinutes(order.orderedAt)} mins
                  </div>
                </div>
              )}

              <div style={{ marginBottom: '12px' }}>
                {order.orderItems?.map((item, idx) => (
                  <div key={idx} className={styles.orderItem}>
                    <span>{item}</span>
                  </div>
                ))}
              </div>

              <div className={styles.buttonsRow}>
                {showEditButton && onEdit && (  
                  <button 
                    onClick={() => onEdit(order)}
                    className={`${styles.buttonBase} ${styles.editButton}`}
                  >
                    Edit
                  </button>
                )}
                
                {onMove && (
                  <button 
                    onClick={() => {
                      if (order.status !== "DELIVERED") onMove(order);
                    }}
                    className={`${styles.buttonBase} ${styles.moveButton}`}
                    style={{
                      flex: (showBackButton && !showEditButton) ? 3 : (showEditButton ? 1 : 1),
                      width: (showBackButton && !showEditButton) ? 'auto' : '100%',
                      cursor: order.status === "DELIVERED" ? "not-allowed" : "pointer",
                      opacity: order.status === "DELIVERED" ? 0.7 : 1,
                    }}
                  >
                    {order.status === "PLACED" ? "Confirm Order"
                      : order.status === "CONFIRMED" ? "Start Preparing"
                      : order.status === "IN_PROGRESS" ? "Mark Ready"
                      : order.status === "READY" ? "Mark Delivered"
                      : "Checkout Order"}
                  </button>
                )}
                
                {showBackButton && onBack && (
                  <button 
                    onClick={() => onBack(order)}
                    className={`${styles.buttonBase} ${styles.backButton}`}
                  >
                    ← Back
                  </button>
                )}
              </div>
            </div>
          ))
        ) : (
          <p className={styles.noOrdersAvailable}>
            No Orders Available
          </p>
        )}
      </div>
    </div>
  );
}
