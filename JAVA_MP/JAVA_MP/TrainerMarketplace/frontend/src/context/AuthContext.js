// frontend/src/context/AuthContext.js (Full Code)

import React, { createContext, useState, useContext } from 'react';
import { registerUser } from '../api/trainerService'; // Use existing register function

// 1. Create the Context object
const AuthContext = createContext(null);

// 2. Define the Provider component
export const AuthProvider = ({ children }) => {
    // Initial state: Load from localStorage if available
    const [authState, setAuthState] = useState(() => {
        const token = localStorage.getItem('userToken');
        const role = localStorage.getItem('userRole');
        const email = localStorage.getItem('userEmail');
        const userId = localStorage.getItem('userId');
        return {
            token: token || null,
            isAuthenticated: !!token,
            userRole: role || null,
            userEmail: email || null,
            userId: userId ? parseInt(userId) : null,
        };
    });

    // Function to handle login success
    const login = (token, role, email, userId) => {
        setAuthState({
            token,
            isAuthenticated: true,
            userRole: role,
            userEmail: email,
            userId,
        });
        // Optional: Store token in localStorage for persistence (future step)
        localStorage.setItem('userToken', token);
        localStorage.setItem('userRole', role);
        localStorage.setItem('userEmail', email);
        localStorage.setItem('userId', userId.toString());
    };

    // Function to handle logout
    const logout = () => {
        setAuthState({
            token: null,
            isAuthenticated: false,
            userRole: null,
            userEmail: null,
            userId: null,
        });
        // Remove persistence from storage
        localStorage.removeItem('userToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('userEmail');
        localStorage.removeItem('userId');
    };
    
    // Function to handle registration (uses existing API function)
    const register = async (userData) => {
        return registerUser(userData);
    };
    
    // 3. Provide state and functions to consumers
    return (
        <AuthContext.Provider value={{ authState, login, logout, register }}>
            {children}
        </AuthContext.Provider>
    );
};

// 4. Custom Hook for easy access to context data
export const useAuth = () => {
    return useContext(AuthContext);
};