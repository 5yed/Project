import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css'
import './colors.css';
import HomePage from './pages/HomePage';
import StaffPage from './pages/StaffPage';
import MenuSection from './components/MenuSection';
import CheckoutSection from './components/CheckoutSection';
import TableSelectionPage from './pages/TableSelectionPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path='/' element={<TableSelectionPage/>}/>
        <Route path="/menu" element={<HomePage/>}>
          <Route index element={<MenuSection/>}/>
          <Route path="checkout/:id" element={<CheckoutSection/>}/>
        </Route>
        <Route path="/staffPage" element={<StaffPage/>}/>
      </Routes>
    </Router>
  )
}

export default App
