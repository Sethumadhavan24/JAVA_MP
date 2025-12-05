// frontend/src/App.js (Full Code)

import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { useAuth } from './context/AuthContext'; // Import useAuth hook
import SearchPage from './pages/SearchPage';
import AuthPage from './pages/AuthPage';
import BookingPage from './pages/BookingPage';
import TrainerDashboard from './pages/TrainerDashboard'; // Import the actual component
import TraineeDashboard from './pages/TraineeDashboard'; // Import the trainee dashboard component
import './App.css';

const App = () => {
    const { authState, logout } = useAuth(); // Get auth state and logout function

    return (
        <Router>
            <header className="app-header">
                <nav>
                    <Link to="/"><h1>SkillLink</h1></Link>
                    <Link to="/search">Find a Trainer</Link>
                    {authState.isAuthenticated ? (
                        <>
                            <span>Welcome, {authState.userEmail} ({authState.userRole})</span>
                            <button onClick={logout} className="logout-button">Log Out</button>
                            {authState.userRole === 'TRAINER' && (
                                <Link to="/trainer-dashboard">Trainer Dashboard</Link>
                            )}
                            {authState.userRole === 'TRAINEE' && (
                                <Link to="/trainee-dashboard">My Dashboard</Link>
                            )}
                        </>
                    ) : (
                        <Link to="/auth">Sign Up / Login</Link>
                    )}
                </nav>
            </header>
            <div className="app-content">
                <Routes>
                    <Route path="/" element={<div className="container"><h2>Welcome to SkillLink!</h2><p>Connect with expert trainers and unlock your potential.</p></div>} />
                    <Route path="/search" element={<SearchPage />} />
                    <Route path="/auth" element={<AuthPage />} />

                    {/* NEW: Dynamic route for booking */}
                    <Route path="/booking/:trainerId" element={<BookingPage />} />

                    <Route path="/trainer-dashboard" element={<TrainerDashboard />} />
                    <Route path="/trainee-dashboard" element={<TraineeDashboard />} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;
