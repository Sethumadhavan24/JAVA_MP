// frontend/src/index.js (Full Code)

import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { AuthProvider } from './context/AuthContext'; // New Import

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        {/* Wrap App with AuthProvider */}
        <AuthProvider>
            <App />
        </AuthProvider>
    </React.StrictMode>
);