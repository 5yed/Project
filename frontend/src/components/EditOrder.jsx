import Popup from './Popup';
import styles from './EditOrder.module.css';
import { useEffect, useState } from "react"; 


export default function EditOrderPopup({ selectTrigger, setTrigger, selectedOrder, saveItem }) {
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    
    const updatedItems = [];
    for (let i = 0; i < selectedOrder.orderItems.length; i++) {
      const quantity = formData.get(`quantity-${i}`);
      if (quantity) {
        updatedItems.push({
          id: selectedOrder.orderItems[i].id,
          quantity: parseInt(quantity)
        });
      }
    }
    
    await saveItem(updatedItems);
  };

  if (!selectedOrder) return null;

  return (
    <Popup
      trigger={selectTrigger}
      setTrigger={setTrigger}
      submitButton={
        <input
          type="submit"
          form="editOrderForm" 
          className={styles.submitButton}
          value="Save Changes"
        />
      }
    >
      <form id="editOrderForm" name="editOrderForm" onSubmit={handleSubmit}>
        <h3>Edit Order #{selectedOrder.id}</h3>
        <p><strong>Table:</strong> {selectedOrder.tableId}</p>
        <p><strong>Status:</strong> {selectedOrder.status}</p>
        
        <div style={{ 
          maxHeight: '400px', 
          overflowY: 'auto',
          padding: '10px',
          border: '1px solid #eee',
          borderRadius: '8px'
        }}>
          {selectedOrder.orderItems?.map((item, index) => (
            <div key={item.id} style={{ 
              marginBottom: '15px', 
              padding: '15px',
              backgroundColor: '#f9f9f9',
              borderRadius: '8px'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
                <strong>{item.menuItem?.name || 'Item'}</strong>
                <span>£{item.menuItem?.price?.toFixed(2) || '0.00'}</span>
              </div>
              
              <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                <label style={{ minWidth: '60px' }}>Quantity:</label>




                <input 
                  type="number" 
                  name={`quantity-${index}`}
                  defaultValue={item.quantity} 
                  min="1"
                  style={{ 
                    width: '70px', 
                    padding: '8px',
                    borderRadius: '4px',
                    border: '1px solid #ccc'
                  }}
                />
              </div>
            </div>




          ))}
        </div>
      </form>
    </Popup>
  );
}
