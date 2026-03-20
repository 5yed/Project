import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

import PaymentForm from './PaymentForm';
import OrderSummary from './OrderSummary';

import api from '../tools/api';
import styles from './CheckoutSection.module.css';
import menuSectionStyles from './MenuSection.module.css';

export default function CheckoutSection() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [paid, setPaid] = useState(false);

  useEffect(() => {
    const fetchOrder = async () => {
      const res = await api.get(`/api/orders/${id}`);
      setOrder(res.data);
    };

    fetchOrder();
  }, [id]);

  if (!order) return <div>Loading...</div>;

  if (paid) {
    return (
      <div className={menuSectionStyles.mainContentsPane}>
        <div className={styles.paymentSuccessText}>
          <h2>Payment successful</h2>
        </div>
      </div>
    )
  }
  else {
    return (
      <div className={styles.container}>
        <PaymentForm
          id={id}
          amount={order.amountTotal - order.amountPaid}
          onPay={() => setPaid(true)}
        />
        <OrderSummary
          order={order}
        />
      </div>
    )
  }
}
