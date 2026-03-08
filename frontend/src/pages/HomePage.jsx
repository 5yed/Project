import '../App.css';
import { Outlet } from 'react-router-dom';
import { Link } from 'react-router-dom';
import TopBar from '../components/TopBar';
import styles from './HomePage.module.css';
import basePageStyles from './BasePage.module.css';

export default function HomePage() {
  return (
    <>
      <TopBar
        bottomRow={
          <>
            <div className={basePageStyles.sectionLabel}>
              <Link className={styles.sectionButton} to="/">Menu</Link>
            </div>
            <div className={basePageStyles.labelSeparator}/>
            <div className={basePageStyles.sectionLabel}>
              <Link className={styles.sectionButton} to="/checkout">Checkout</Link>
            </div>
          </>
        }
      />

      <Outlet/>
    </>
  );
}
