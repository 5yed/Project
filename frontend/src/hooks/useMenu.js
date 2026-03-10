import { useState, useEffect } from 'react'
import api from '../tools/api'

export function useMenu() {
  const [menuItems, setMenuItems] = useState([]);
  
  const fetchMenuItems = async () => {
    const res = await api.get("/menuItems");
    const items = res.data._embedded?.menuItems ?? [];
    setMenuItems(items);
  }

  useEffect(() => {
    fetchMenuItems();
  }, []);

  return {menuItems, setMenuItems};
}