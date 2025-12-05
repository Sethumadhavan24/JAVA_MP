// frontend/src/pages/AuthPage.js (Full Code)

import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext'; // New Import
import { useNavigate } from 'react-router-dom'; // New Import for redirection
import axios from 'axios';
import './AuthPage.css';

const AuthPage = () => {
    const { login, register } = useAuth(); // Destructure login function from context
    const navigate = useNavigate(); // Hook for navigation

    const [isSignUp, setIsSignUp] = useState(true);
    const [formData, setFormData] = useState({
        email: '',
        password: '',
        role: 'TRAINEE',
        firstName: '',
        lastName: '',
        mainSkill: ''
    });
    const [message, setMessage] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleRegistrationSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setLoading(true);

        const dataToSend = {
            ...formData,
            mainSkill: formData.role === 'TRAINER' ? formData.mainSkill : undefined,
        };

        try {
            await register(dataToSend); // Use context register function
            setMessage({ type: 'success', text: 'Registration successful! You can now log in.' });
            // Clear form and switch to login view automatically
            setFormData({ email: '', password: '', role: 'TRAINEE', firstName: '', lastName: '', mainSkill: '' });
            setIsSignUp(false);
        } catch (errorData) {
            let errorText = 'Registration failed. Check if all fields are valid.';
            if (typeof errorData === 'string') {
                errorText = errorData; 
            } else if (errorData.errors) {
                errorText = errorData.errors.map(err => err.defaultMessage).join(', ');
            }
            setMessage({ type: 'error', text: errorText });
        } finally {
            setLoading(false);
        }
    };
    
    // --- NEW: Handle Login Submission ---
    const handleLoginSubmit = async (e) => {
        e.preventDefault();
        setMessage(null);
        setLoading(true);

        try {
            const response = await axios.post('http://localhost:8081/api/auth/login', { email: formData.email, password: formData.password });
            const { token, role, userId } = response.data;
            login(token, role, formData.email, userId);
            setMessage({ type: 'success', text: 'Login successful!' });
            navigate('/search');
        } catch (error) {
            let errorText = 'Login failed. Check your credentials.';
            if (error.response && error.response.data) {
                errorText = error.response.data;
            }
            setMessage({ type: 'error', text: errorText });
        } finally {
            setLoading(false);
        }
    };

    const isTrainer = formData.role === 'TRAINER';

    return (
        <div className="container auth-container">
            <h1>{isSignUp ? 'New User Sign Up' : 'User Login'}</h1>
            <button 
                className="switch-button" 
                onClick={() => setIsSignUp(!isSignUp)}>
                {isSignUp ? 'Already have an account? Switch to Login' : 'Need an account? Switch to Sign Up'}
            </button>

            <form onSubmit={isSignUp ? handleRegistrationSubmit : handleLoginSubmit} className="auth-form">
                {message && (
                    <div className={`alert alert-${message.type}`}>
                        {message.text}
                    </div>
                )}

                {/* Role Selector (Only visible during signup) */}
                {isSignUp && (
                    <div className="form-group">
                        <label>Registering As:</label>
                        <select name="role" value={formData.role} onChange={handleChange}>
                            <option value="TRAINEE">Learner (Trainee)</option>
                            <option value="TRAINER">Expert (Trainer)</option>
                        </select>
                    </div>
                )}
                
                {/* Email and Password Fields (Common) */}
                <div className="form-group">
                    <input type="text" name="email" value={formData.email} onChange={handleChange} placeholder="Email" required />
                </div>
                <div className="form-group">
                    <input type="password" name="password" value={formData.password} onChange={handleChange} placeholder="Password" required />
                </div>

                {/* Sign Up Specific Fields */}
                {isSignUp && (
                    <>
                        <div className="form-group">
                            <input type="text" name="firstName" value={formData.firstName} onChange={handleChange} placeholder="First Name" required />
                        </div>
                        <div className="form-group">
                            <input type="text" name="lastName" value={formData.lastName} onChange={handleChange} placeholder="Last Name" required />
                        </div>
                        {formData.role === 'TRAINER' && (
                            <div className="form-group">
                                <input type="text" name="mainSkill" value={formData.mainSkill} onChange={handleChange} placeholder="Main Skill (e.g., Yoga, Python)" required />
                            </div>
                        )}
                    </>
                )}

                <button type="submit" disabled={loading} className="submit-button">
                    {loading ? 'Processing...' : (isSignUp ? 'Sign Up' : 'Log In')}
                </button>
            </form>
        </div>
    );
};

export default AuthPage;