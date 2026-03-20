import { useState, useEffect, useRef } from "react"
import { useOutletContext } from 'react-router-dom';

import styles from '../pages/StaffPage.module.css';
import menuSectionStyles from '../components/MenuSection.module.css';
import Popup from './Popup';
import EditOrder from "../components/EditOrder"; 
import MenuItem from "../components/MenuItem";
import { useMenu } from '../hooks/useMenu';
import api from '../tools/api';


export default function StaffPageMenu() {
  const {
    editOrderPopupStatus,
    setEditOrderPopupStatus,
    selectedOrder,
    setSelectedOrder
  } = useOutletContext();
  const {menuItems, setMenuItems} = useMenu();

  const [selectedItem, setSelectedItem] = useState(false);

  const [addItemPopupStatus, setAddItemPopupStatus] = useState(false);
  const [editItemPopupStatus, setEditItemPopupStatus] = useState(false);

  const extractFormData = (e) => {
    const formData = new FormData(e.currentTarget);
    const itemData = Object.fromEntries(formData.entries());
    delete itemData.image;
    itemData.price = parseFloat(itemData.price);
    itemData.kcal = parseFloat(itemData.kcal);
    itemData.allergens = [];
    itemData.categoryId = parseFloat(1);
    return itemData;
  };

  const handleAddMenuItem = async (e) => {
    e.preventDefault();
    const form = e.currentTarget;
    const itemData = extractFormData(e);
    const imageFile = form.elements.image?.files?.[0];

    // Create item
    const res = await api.post("/api/menuItems", itemData);
    const createdItem = res.data;

    // Upload image if provided
    if (imageFile) {
      const formData = new FormData();
      formData.append("file", imageFile);

      await api.post(
        `/api/menuItems/${createdItem.id}/upload`,
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        }
      );
    }

    // Fetch updated item
    const updatedRes = await api.get(`/menuItems/${createdItem.id}`);
    const updatedItem = updatedRes.data;

    setMenuItems(prev => [...prev, updatedItem]);
    setAddItemPopupStatus(false);
  };

  const handleEditMenuItem = async (e) => {
    e.preventDefault();
    const form = e.currentTarget;
    const itemData = extractFormData(e);

    // Update text fields
    const res = await api.put("/api/menuItems/" + selectedItem.id, itemData);
    
    // Upload image if selected
    const imageFile = form.elements.image?.files[0];
    if (imageFile) {
      const formData = new FormData();
      formData.append("file", imageFile);
      await api.post(`/api/menuItems/${selectedItem.id}/upload`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      });
    }

    // Fetch the updated menu item including the uploaded image
    const updatedRes = await api.get(`/menuItems/${selectedItem.id}`);
    const updatedItem = updatedRes.data;

    setMenuItems(menuItems.map(item =>
      item.id === selectedItem.id ? updatedItem : item
    ));
    setEditItemPopupStatus(false);
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

  return (
    <>
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
            <label htmlFor="itemImage">Item Image:</label>
            <input
              type="file"
              id="itemImage"
              name="image"
              accept="image/*"
            />
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
            <label htmlFor="itemImage">Item Image:</label>
            <input
              type="file"
              id="itemImage"
              name="image"
              accept="image/*"
            />
          </div>
        </form>
      </Popup>

      <EditOrder
        trigger={editOrderPopupStatus}
        setTrigger={setEditOrderPopupStatus}
        selectedOrder={selectedOrder}
        onSave={handleSaveOrder}
      />

      <div className={styles.mainContentsContainer}>
        <div className={styles.buttonBar}>
          <button onClick={() => setAddItemPopupStatus(true)}>
            + Add Item
          </button>
        </div>
        <div className={menuSectionStyles.menuSurface}>
          <div className={menuSectionStyles.menuItems}>
            {menuItems.length > 0 ? (
              menuItems.map((item) => (
                <MenuItem
                  key={item.id}
                  item={item}
                  onClick={() => {
                    setSelectedItem(item);
                    setEditItemPopupStatus(true);
                  }}
                />
              ))
            ) : (
              <p className={menuSectionStyles.noItemsText}>
                No menu items found
              </p>
            )}
          </div>
        </div>
      </div>
    </>
  )
}
