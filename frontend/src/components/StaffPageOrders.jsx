import { useState, useEffect, useRef } from "react"
import { useOutletContext } from 'react-router-dom';
import styles from '../pages/StaffPage.module.css';

import api from '../tools/api';
import socketUrl from '../tools/webSocketBase';

import OrderColumn from "../components/OrderColumn"

export default function StaffPageOrders() {
  const {
    setLoading,
    setSelectedOrder,
    setEditOrderPopupStatus,
    setOrderNotifications,
  } = useOutletContext();
  const [pending, setPending] = useState([]);
  const [confirmed, setConfirmed] = useState([]);
  const [inProgress, setInProgress] = useState([]);
  const [ready, setReady] = useState([]);
  const [delivered, setDelivered] = useState([]);

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
          setOrderNotifications(prev => [...prev, `Order ${order.id} for Table ${order.tableId} is ready!`]);
          setTimeout(() => {setOrderNotifications(prev => prev.slice(1));}, 4000);
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
    setLoading(false);
    fetchOrders();

    setupWebSocket();

    return () => {
      if (socketRef.current?.readyState === WebSocket.OPEN) {
        socketRef.current.close();
      }
    };
  }, []);

  return (
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
  )
}
