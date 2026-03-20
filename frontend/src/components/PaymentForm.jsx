import api from '../tools/api';
import styles from './CheckoutSection.module.css';
import menuSectionStyles from './MenuSection.module.css';

export default function PaymentForm({ id, amount, onPay }) {
  const extractFormData = (e) => {
    const formData = new FormData(e.currentTarget);
    const itemData = Object.fromEntries(formData.entries());

    const nameOnCard = itemData.firstName + " " + itemData.lastName;
    const [em, ey] = itemData.expiryDate.split('/');
    const cardNumber = itemData.cardNumber.replace(/-/g, '');
    const expiryMonth = parseFloat(em);
    const expiryYear = parseFloat(ey) + 2000;

    const finalData = {
      cardNumber: cardNumber,
      nameOnCard: nameOnCard,
      expiryMonth: expiryMonth,
      expiryYear: expiryYear,
      cvv: itemData.cvv,
      amount: amount
    }

    return finalData;
  }

  const handleOrderCheckout = async (e) => {
    e.preventDefault();
    const itemData = extractFormData(e);
    const res = await api.post(`/api/orders/${id}/checkout`, itemData);
    onPay();
  }

  return (
    <div className={menuSectionStyles.leftContainer}>
      <div className={menuSectionStyles.containerLabel}>
        <h2>Checkout</h2>
      </div>
      <div className={styles.checkoutFormContainer}>
        <form id="checkoutForm" name="checkoutForm" onSubmit={handleOrderCheckout}>
          <div className={styles.grid}>
            <label htmlFor="firstName">First Name:</label>
            <input
              type="text" id="firstName" name="firstName" required={true}
              placeholder="First"
            />

            <label htmlFor="lastName">Last Name:</label>
            <input
              type="text" id="lastName" name="lastName" required={true}
              placeholder="Last"
            />

            <label htmlFor="cardNumber">Card Number:</label>
            <input
              type="text" id="cardNumber" name="cardNumber" required={true}
              pattern="[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{4}" placeholder="1234-5678-9012-3456"
            />

            <label htmlFor="cvv">CVV:</label>
            <input
              type="text" id="cvv" name="cvv" required={true}
              pattern="[0-9]{3}" placeholder="123"
            />

            <label htmlFor="expiryDate">Expiry Date:</label>
            <input
              type="text" id="expiryDate" name="expiryDate" required={true}
              pattern="[0-9]{2}/[0-9]{2}" placeholder="12/34"
            />
          </div>
        </form>
        <input type="submit" form="checkoutForm" className="submit-button" />
      </div>
    </div>
  )
}
