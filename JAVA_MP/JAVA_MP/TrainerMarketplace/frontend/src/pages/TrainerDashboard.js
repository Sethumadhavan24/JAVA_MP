// frontend/src/pages/TrainerDashboard.js

import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { getTrainerDashboardData, addAvailability, updateTrainerProfile, deleteAvailability } from '../api/trainerService';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import './TrainerDashboard.css';

const TrainerDashboard = () => {
    const { authState, logout } = useAuth();
    const navigate = useNavigate();
    const [dashboardData, setDashboardData] = useState({
        profile: null,
        bookings: [],
        currentMonthEarnings: 0,
        totalEarnings: 0,
        monthlyEarnings: {},
        availability: [] // Add availability to dashboard data
    });

    // Rate update form state
    const [rateForm, setRateForm] = useState({
        hourlyRate: '',
        dailyRate: '',
        rateType: 'HOUR'
    });
    const [rateMessage, setRateMessage] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Availability form state
    const [availabilityForm, setAvailabilityForm] = useState({
        startTime: '',
        endTime: ''
    });
    const [availabilityMessage, setAvailabilityMessage] = useState(null);

    // Effect to check authentication and fetch data
    useEffect(() => {
        // 1. Security Check: Redirect if not a logged-in TRAINER
        if (!authState.isAuthenticated || authState.userRole !== 'TRAINER') {
            navigate('/auth');
            return;
        }

        const fetchDashboard = async () => {
            try {
                const data = await getTrainerDashboardData();
                setDashboardData(data);
                setError(null);
            } catch (err) {
                if (err.response && err.response.status === 401) {
                    setError("Session expired or unauthorized access. Please log in again.");
                    logout();
                } else {
                    setError("Failed to load dashboard data.");
                }
            } finally {
                setLoading(false);
            }
        };

        fetchDashboard();
    }, [authState.isAuthenticated, authState.userRole, navigate, logout]);

    if (loading) return <div className="container"><h2>Loading Dashboard...</h2></div>;
    if (error) return <div className="container"><h2 style={{color: 'red'}}>Error: {error}</h2></div>;

    const { profile, bookings, currentMonthEarnings, totalEarnings, monthlyEarnings, availability } = dashboardData;

    // Prepare chart data
    const chartData = Object.entries(monthlyEarnings).map(([month, amount]) => ({
        month,
        earnings: parseFloat(amount)
    }));

    // Get unique clients count
    const uniqueClients = new Set(bookings.map(b => b.trainee.id)).size;

    // Handle availability form submission
    const handleAddAvailability = async (e) => {
        e.preventDefault();
        setAvailabilityMessage(null);

        if (!availabilityForm.startTime || !availabilityForm.endTime) {
            setAvailabilityMessage({ type: 'error', text: 'Please fill in both start and end times.' });
            return;
        }

        try {
            const availabilityData = {
                startTime: new Date(availabilityForm.startTime).toISOString(),
                endTime: new Date(availabilityForm.endTime).toISOString()
            };

            await addAvailability(profile.id, availabilityData);
            setAvailabilityMessage({ type: 'success', text: 'Availability slot added successfully!' });

            // Reset form
            setAvailabilityForm({ startTime: '', endTime: '' });

            // Refresh dashboard data to show updated availability
            const updatedData = await getTrainerDashboardData();
            setDashboardData(updatedData);

        } catch (error) {
            setAvailabilityMessage({ type: 'error', text: 'Failed to add availability slot. Please try again.' });
        }
    };

    // Handle delete availability
    const handleDeleteAvailability = async (availabilityId) => {
        if (!window.confirm('Are you sure you want to delete this availability slot?')) {
            return;
        }

        try {
            await deleteAvailability(availabilityId);
            // Refresh dashboard data to show updated availability
            const updatedData = await getTrainerDashboardData();
            setDashboardData(updatedData);
        } catch (error) {
            alert('Failed to delete availability slot. Please try again.');
        }
    };

    // Handle rate update form submission
    const handleUpdateRate = async (e) => {
        e.preventDefault();
        setRateMessage(null);

        if (!rateForm.hourlyRate && !rateForm.dailyRate) {
            setRateMessage({ type: 'error', text: 'Please enter at least one rate.' });
            return;
        }

        try {
            const updateData = {
                hourlyRate: rateForm.hourlyRate || profile.hourlyRate.toString(),
                dailyRate: rateForm.dailyRate || profile.dailyRate?.toString() || '0',
                rateType: rateForm.rateType
            };

            await updateTrainerProfile(updateData);
            setRateMessage({ type: 'success', text: 'Rate updated successfully!' });

            // Refresh dashboard data
            const updatedData = await getTrainerDashboardData();
            setDashboardData(updatedData);

        } catch (error) {
            setRateMessage({ type: 'error', text: 'Failed to update rate. Please try again.' });
        }
    };

    // --- Dashboard Rendering (Business Automation Features) ---
    return (
        <div className="container dashboard-container">
            <h1>Trainer Dashboard (CRM)</h1>
            <p className="status-badge">Logged in as: <strong>{profile.firstName} {profile.lastName}</strong> ({authState.userRole})</p>
            <p><strong>Total Unique Clients:</strong> {uniqueClients}</p>
            <p><strong>Available Slots:</strong> {availability.length}</p>

            {/* Earnings Section */}
            <div className="earnings-section">
                <h2>Earnings Overview</h2>
                <div className="earnings-cards">
                    <div className="earnings-card">
                        <h3>Current Month</h3>
                        <p className="earnings-amount">₹{currentMonthEarnings.toFixed(2)}</p>
                    </div>
                    <div className="earnings-card">
                        <h3>Total Earnings</h3>
                        <p className="earnings-amount">₹{totalEarnings.toFixed(2)}</p>
                    </div>
                </div>
            </div>

            <div className="profile-section">
                <h2>Your Profile & Metrics</h2>
                <p><strong>Email:</strong> {authState.userEmail}</p>
                <p><strong>Rate:</strong> ₹{profile.hourlyRate.toFixed(2)}/hr, ₹{profile.dailyRate ? profile.dailyRate.toFixed(2) : '0'}/day ({profile.rateType})</p>
                <p className={profile.isCertificationVerified ? 'verified' : 'unverified'}>
                    Verification Status: {profile.isCertificationVerified ? 'Certified' : 'Pending Review'}
                </p>
            </div>

            {/* Rate Management Section */}
            <div className="rate-section">
                <h2>Update Your Rates</h2>
                <form onSubmit={handleUpdateRate} className="rate-form">
                    <div className="form-group">
                        <label htmlFor="rateType">Rate Type:</label>
                        <select
                            id="rateType"
                            value={rateForm.rateType}
                            onChange={(e) => setRateForm({ ...rateForm, rateType: e.target.value })}
                        >
                            <option value="HOUR">Per Hour</option>
                            <option value="DAY">Per Day</option>
                        </select>
                    </div>
                    <div className="form-group">
                        <label htmlFor="hourlyRate">Hourly Rate (₹):</label>
                        <input
                            type="number"
                            id="hourlyRate"
                            value={rateForm.hourlyRate}
                            onChange={(e) => setRateForm({ ...rateForm, hourlyRate: e.target.value })}
                            placeholder={profile.hourlyRate.toString()}
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="dailyRate">Daily Rate (₹):</label>
                        <input
                            type="number"
                            id="dailyRate"
                            value={rateForm.dailyRate}
                            onChange={(e) => setRateForm({ ...rateForm, dailyRate: e.target.value })}
                            placeholder={profile.dailyRate ? profile.dailyRate.toString() : '0'}
                        />
                    </div>
                    <button type="submit" className="update-rate-button">Update Rates</button>
                </form>
                {rateMessage && (
                    <div className={`alert alert-${rateMessage.type}`} style={{ marginTop: '10px' }}>
                        {rateMessage.text}
                    </div>
                )}
            </div>

            {/* Earnings Chart */}
            <div className="chart-section">
                <h2>Monthly Earnings</h2>
                {chartData.length === 0 ? (
                    <p>No earnings data available.</p>
                ) : (
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={chartData}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="month" />
                            <YAxis />
                            <Tooltip formatter={(value) => [`₹${value.toFixed(2)}`, 'Earnings']} />
                            <Line type="monotone" dataKey="earnings" stroke="#82ca9d" strokeWidth={2} />
                        </LineChart>
                    </ResponsiveContainer>
                )}
            </div>

            {/* Availability Management Section */}
            <div className="availability-section">
                <h2>Manage Your Availability</h2>
                <form onSubmit={handleAddAvailability} className="availability-form">
                    <div className="form-group">
                        <label htmlFor="startTime">Start Time:</label>
                        <input
                            type="datetime-local"
                            id="startTime"
                            value={availabilityForm.startTime}
                            onChange={(e) => setAvailabilityForm({ ...availabilityForm, startTime: e.target.value })}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="endTime">End Time:</label>
                        <input
                            type="datetime-local"
                            id="endTime"
                            value={availabilityForm.endTime}
                            onChange={(e) => setAvailabilityForm({ ...availabilityForm, endTime: e.target.value })}
                            required
                        />
                    </div>
                    <button type="submit" className="add-availability-button">Add Availability Slot</button>
                </form>
                {availabilityMessage && (
                    <div className={`alert alert-${availabilityMessage.type}`} style={{ marginTop: '10px' }}>
                        {availabilityMessage.text}
                    </div>
                )}

                {/* Availability List */}
                <div className="availability-list">
                    <h3>Your Availability Slots</h3>
                    {availability.length === 0 ? (
                        <p>No availability slots added yet.</p>
                    ) : (
                        <table className="availability-table">
                            <thead>
                                <tr>
                                    <th>Start Time</th>
                                    <th>End Time</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {availability.map(slot => (
                                    <tr key={slot.id}>
                                        <td>{new Date(slot.startTime).toLocaleString()}</td>
                                        <td>{new Date(slot.endTime).toLocaleString()}</td>
                                        <td>{slot.available ? 'Available' : 'Booked'}</td>
                                        <td>
                                            <button
                                                onClick={() => handleDeleteAvailability(slot.id)}
                                                className="delete-button"
                                                disabled={!slot.available}
                                            >
                                                Delete
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>

            <div className="client-history-section">
                <h2>Client History (Admin Overhead Reduction)</h2>
                {bookings.length === 0 ? (
                    <p>No bookings completed yet.</p>
                ) : (
                    <table className="client-table">
                        <thead>
                            <tr>
                                <th>Client ID</th>
                                <th>Session Time</th>
                                <th>Amount</th>
                                <th>Payout</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            {bookings.map(b => (
                                <tr key={b.id}>
                                    <td>{b.trainee.firstName} {b.trainee.lastName} (TID: {b.trainee.id})</td>
                                    <td>{new Date(b.sessionStart).toLocaleString()}</td>
                                    <td>₹{b.totalAmount.toFixed(2)}</td>
                                    <td>₹{b.trainerPayout.toFixed(2)}</td>
                                    <td>{b.status}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>
            <button onClick={logout} className="logout-button">Log Out</button>
        </div>
    );
};

// We move the placeholder definition to a real component definition in the file
// and remove the placeholder from App.js
export default TrainerDashboard;
