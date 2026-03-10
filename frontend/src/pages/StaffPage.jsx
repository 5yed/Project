import '../App.css'
import menuSectionStyles from '../components/MenuSection.module.css';
import styles from './StaffPage.module.css';
import basePageStyles from './BasePage.module.css';
import api from '../tools/api';
import socketUrl from '../tools/webSocketBase';

import TopBar from "../components/TopBar"
import MenuItem from "../components/MenuItem";
import Popup from '../components/Popup';
import OrderColumn from "../components/OrderColumn"
import EditOrder from "../components/EditOrder"; 

import { useState, useEffect, useRef } from "react"
import { useMenu } from '../hooks/useMenu';

export default function StaffPage() {
  const {menuItems, setMenuItems} = useMenu();
  const [addItemPopupStatus, setAddItemPopupStatus] = useState(false);
  const [editItemPopupStatus, setEditItemPopupStatus] = useState(false);
  const [editOrderPopupStatus, setEditOrderPopupStatus] = useState(false); 
  const [selectedItem, setSelectedItem] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null); 

  const [pending, setPending] = useState([]);
  const [confirmed, setConfirmed] = useState([]);
  const [inProgress, setInProgress] = useState([]);
  const [ready, setReady] = useState([]);
  const [delivered, setDelivered] = useState([]);

  const [loading, setLoading] = useState(true);
  const [alerts, setAlerts] = useState([]);

  const socketRef = useRef();

  const setupWebSocket = () => {
    const socket = new WebSocket(socketUrl("/ws"));
    socketRef.current = socket;

    socket.onmessage = (e) => {
      const data = JSON.parse(e.data);
      switch (data.type) {
        case "ORDER_UPDATED":
        case "ORDER_CREATED":
          fetchOrder(data.orderId);
          break;
      }
    };

    socket.onopen = () => console.log("WS connected");
    socket.onerror = (err) => console.error("WS error", err);
  };

  const extractFormData = (e) => {
    const formData = new FormData(e.currentTarget);
    const itemData = Object.fromEntries(formData.entries());
    itemData.price = parseFloat(itemData.price);
    itemData.kcal = parseFloat(itemData.kcal);
    itemData.allergens = [];
    itemData.categoryId = parseFloat(1);
    return itemData;
  }

  const handleAddMenuItem = async (e) => {
    e.preventDefault();
    const itemData = extractFormData(e);
    const res = await api.post("/api/menuItems", itemData);
    setMenuItems([...menuItems, res.data])
    setAddItemPopupStatus(false);
  }

  const handleEditMenuItem = async (e) => {
    e.preventDefault();
    const itemData = extractFormData(e);
    const res = await api.put("/api/menuItems/" + selectedItem.id, itemData);
    setMenuItems(menuItems.map((item) => item.id == selectedItem.id ? res.data : item));
    setEditItemPopupStatus(false);
  }

  const fetchOrders = async () => {
    try {
      const res = await api.get("/api/orders/all");
      const data = res.data;

      setPending(data.filter(o => o.status === "PLACED"));
      setConfirmed(data.filter(o => o.status === "CONFIRMED"));
      setInProgress(data.filter(o => o.status === "IN_PROGRESS"));
      setReady(data.filter(o => o.status === "READY"));
      setDelivered(data.filter(o => o.status === "DELIVERED"));

      setLoading(false);
    } catch (err) {
      console.error("Failed to load orders:", err);
      setLoading(false);
    }
  };

  const fetchAlerts = async () => {
    try {
      const res = await api.get("/api/alerts/active");
      setAlerts(res.data);
    } catch (err) {
      console.error("Failed to load alerts:", err);
    }
  };

  // Forward movement functions
  const moveToConfirmed = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "CONFIRMED" });
    const updated = res.data;
    setPending(prev => prev.filter(o => o.id !== order.id));
    setConfirmed(prev => [...prev, updated]);
  };

  const moveToInProgress = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "IN_PROGRESS" });
    const updated = res.data;
    setConfirmed(prev => prev.filter(o => o.id !== order.id));
    setInProgress(prev => [...prev, updated]);
  };

  const moveToReady = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "READY" });
    const updated = res.data;
    setInProgress(prev => prev.filter(o => o.id !== order.id));
    setReady(prev => [...prev, updated]);
  };

  const moveToDelivered = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "DELIVERED" });
    const updated = res.data;
    setReady(prev => prev.filter(o => o.id !== order.id));
    setDelivered(prev => [...prev, updated]);
  };

  // Backward movement functions
  const moveBackToPlaced = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "PLACED" });
    const updated = res.data;
    setConfirmed(prev => prev.filter(o => o.id !== order.id));
    setPending(prev => [...prev, updated]);
  };

  const moveBackToConfirmed = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "CONFIRMED" });
    const updated = res.data;
    setInProgress(prev => prev.filter(o => o.id !== order.id));
    setConfirmed(prev => [...prev, updated]);
  };

  const moveBackToInProgress = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "IN_PROGRESS" });
    const updated = res.data;
    setReady(prev => prev.filter(o => o.id !== order.id));
    setInProgress(prev => [...prev, updated]);
  };

  const moveBackToReady = async (order) => {
    const res = await api.patch(`/api/orders/${order.id}/status`, { status: "READY" });
    const updated = res.data;
    setDelivered(prev => prev.filter(o => o.id !== order.id));
    setReady(prev => [...prev, updated]);
  };

  // Handle saving edited order
  const handleSaveOrder = async (updatedItems) => {
    try {
      for (const item of updatedItems) {
        await api.patch(`/api/orderItems/${item.id}`, { quantity: item.quantity });
      }
      await fetchOrder(selectedOrder.id);
      setEditOrderPopupStatus(false);
      setSelectedOrder(null);
    } catch (err) {
      console.error("Failed to update order:", err);
    }
  };

  const fetchOrder = async (id) => {
    try {
      const res = await api.get(`/api/orders/${id}`);
      const order = res.data;

      // Remove from all categories first
      setPending(prev => prev.filter(o => o.id !== id));
      setConfirmed(prev => prev.filter(o => o.id !== id));
      setInProgress(prev => prev.filter(o => o.id !== id));
      setReady(prev => prev.filter(o => o.id !== id));
      setDelivered(prev => prev.filter(o => o.id !== id));

      // Add to the correct category based on status
      switch (order.status) {
        case "PLACED":
          setPending(prev => [...prev, order]);
          break;
        case "CONFIRMED":
          setConfirmed(prev => [...prev, order]);
          break;
        case "IN_PROGRESS":
          setInProgress(prev => [...prev, order]);
          break;
        case "READY":
          setReady(prev => [...prev, order]);
          break;
        case "DELIVERED":
          setDelivered(prev => [...prev, order]);
          break;
        default:
          console.warn("Unknown order status:", order.status);
      }

    } catch (err) {
      console.error("Failed to fetch order", id, err);
    }
  };

  useEffect(() => {
    fetchOrders();
    fetchAlerts();

    setupWebSocket();
    return () => {
      if (socketRef.current?.readyState === WebSocket.OPEN) {
        socketRef.current.close();
      }
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
        bottomRow={
          <>
            <div className={basePageStyles.sectionLabel}>
              <h2>Menu</h2>
            </div>
            <div className={basePageStyles.labelSeparator}/>
            <div className={basePageStyles.sectionLabel}>
              <h2>Orders</h2>
            </div>
          </>
        }
      />

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

      <div className={styles.buttonBar}>
        <button onClick={() => setAddItemPopupStatus(true)}>
          + Add Item
        </button>
      </div>

      <Popup 
        trigger={addItemPopupStatus} 
        setTrigger={setAddItemPopupStatus}
        submitButton={<input type="submit" form="newMenuItemForm" className="submit-button"/>}
      >
        <form id="newMenuItemForm" name="newMenuItemForm" onSubmit={handleAddMenuItem}>
          <div className={styles.grid}>
            <label htmlFor="itemName">Item Name:</label>
            <input type="text" id="itemName" name="name" required={true}/>
            <label htmlFor="itemDescription">Description:</label>
            <textarea id="itemDescription" name="description" required={true}/>
            <label htmlFor="itemPrice">Item Price:</label>
            <input type="text" id="itemPrice" name="price" required={true}/>
            <label htmlFor="itemKcal">Kcal:</label>
            <input type="text" id="itemKcal" name="kcal" required={true}/>
            <label htmlFor="itemStatus">Status:</label>
            <select id="itemStatus" name="status">
              <option value="AVAILABLE">Available</option>
              <option value="OUT_OF_STOCK">Out of stock</option>
              <option value="HIDDEN">Hidden</option>
            </select>
            <label htmlFor="itemCategory">Cateogry:</label>
            <input type="text" id="itemCategory" name="categoryId" required={true}/>
          </div>
        </form>
      </Popup>

      <Popup
        trigger={editItemPopupStatus}
        setTrigger={setEditItemPopupStatus}
        submitButton={<input type="submit" form="editMenuItemForm" className="submit-button"/>}
      >
        <form id="editMenuItemForm" name="editMenuItemForm" onSubmit={handleEditMenuItem}>
          <div className={styles.grid}>
            <label htmlFor="itemName">Item Name:</label>
            <input type="text" id="itemName" name="name" defaultValue={selectedItem?.name} required={true}/>
            <label htmlFor="itemDescription">Description:</label>
            <textarea id="itemDescription" name="description" defaultValue={selectedItem?.description} required={true}/>
            <label htmlFor="itemPrice">Item Price:</label>
            <input type="text" id="itemPrice" name="price" defaultValue={selectedItem?.price} required={true}/>
            <label htmlFor="itemKcal">Kcal:</label>
            <input type="text" id="itemKcal" name="kcal" defaultValue={selectedItem?.kcal} required={true}/>
          </div>
        </form>
      </Popup>

      
      <EditOrder
        trigger={editOrderPopupStatus}
        setTrigger={setEditOrderPopupStatus}
        selectedOrder={selectedOrder}
        onSave={handleSaveOrder}
      />

      <div className={menuSectionStyles.mainContentsPane}>
        <div className={menuSectionStyles.menuPane}>
          <div className={menuSectionStyles.menuLabel}>
            <h2>Menu</h2>
          </div>
          <div className={menuSectionStyles.menuItems}>
            {menuItems.map((item) => (
              <MenuItem
                key={item.id}
                item={item}
                onClick={() => {
                  setSelectedItem(item);
                  setEditItemPopupStatus(true);
                }}
              />
            ))}
          </div>
        </div>
      </div>

      <div className={styles.ordersContainer}>
        <OrderColumn 
          title="Review Order"
          orders={pending}
          backgroundColor="#E7C78B"
          onMove={moveToConfirmed}
          onEdit={(order) => {
            setSelectedOrder(order);
            setEditOrderPopupStatus(true);
          }}
          showBackButton={false}
          showEditButton={true}
        />

        <OrderColumn 
          title="Active Orders"
          orders={[...confirmed, ...inProgress, ...ready, ...delivered]}
          backgroundColor="#E7C78B"
          onMove={(order) => {
            if (order.status === "CONFIRMED") moveToInProgress(order);
              else if (order.status === "IN_PROGRESS") moveToReady(order);
                else if (order.status === "READY") moveToDelivered(order);
          }}
          onBack={(order) => {
            if (order.status === "READY") moveBackToInProgress(order);
              else if (order.status === "IN_PROGRESS") moveBackToConfirmed(order);
                else if (order.status === "CONFIRMED") moveBackToPlaced(order);
          }}
          onEdit={(order) => {
            setSelectedOrder(order);
            setEditOrderPopupStatus(true);
          }}
          showBackButton={true}
          showEditButton={true}
        />
      </div>
    </>
  );
}
