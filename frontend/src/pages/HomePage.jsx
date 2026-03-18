import '../App.css';
import { Outlet } from 'react-router-dom';
import { Link } from 'react-router-dom';
import { useState, useEffect } from 'react';
import TopBar from '../components/TopBar';
import styles from './HomePage.module.css';
import basePageStyles from './BasePage.module.css';
import searchStyles from './SearchBar.module.css';
import api from '../tools/api';

export default function HomePage() {

  const [tableId, setTableId] = useState(1);
  const [activeRequest, setActiveRequest] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('all');

  // hardcoded directly from sql
  const categories = [
    { id: 1, name: 'Starters' },
    { id: 2, name: 'Main Courses' },
    { id: 3, name: 'Desserts' },
    { id: 4, name: 'Drinks' }
  ];

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
              Waiter has responded to your request.
            </div>
          ))}
        </div>
      )}

      <div className={searchStyles.searchContainer}>
        <div className={searchStyles.searchBox}>
          <input
            type="text"
            placeholder="Search menu..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className={searchStyles.searchInput}
          />
          {searchTerm && (
            <button
              onClick={() => setSearchTerm('')}
              className={searchStyles.clearButton}
            >
              ✕
            </button>
          )}
        </div>
      </div>

    




      <div style={{
        display: 'flex',
        justifyContent: 'center',
        gap: '15px',
        padding: '20px',
        flexWrap: 'wrap'
      }}>
        <button
          onClick={() => setSelectedCategory('all')}
          style={{
            padding: '12px 24px',
            borderRadius: '30px',
            border: selectedCategory === 'all' ? '3px solid #0B0033' : '1px solid #ccc',
            background: selectedCategory === 'all' ? '#0B0033' : 'white',
            color: selectedCategory === 'all' ? 'white' : '#333',
            cursor: 'pointer',
            fontSize: '16px',
            fontWeight: '600',
            boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
            transition: 'all 0.2s'
          }}
        >
          All Items
        </button>
        
        {categories.map(category => (
          <button
            key={category.id}
            onClick={() => setSelectedCategory(category.id)}
            style={{
              padding: '12px 24px',
              borderRadius: '30px',
              border: selectedCategory === category.id ? '3px solid #0B0033' : '1px solid #ccc',
              background: selectedCategory === category.id ? '#0B0033' : 'white',
              color: selectedCategory === category.id ? 'white' : '#333',
              cursor: 'pointer',
              fontSize: '16px',
              fontWeight: '600',
              boxShadow: '0 2px 5px rgba(0,0,0,0.1)',
              transition: 'all 0.2s'
            }}
          >
            {category.name}
          </button>
        ))}
      </div>

      <Outlet context={{ searchTerm, selectedCategory }}/>
    </>
  );
}