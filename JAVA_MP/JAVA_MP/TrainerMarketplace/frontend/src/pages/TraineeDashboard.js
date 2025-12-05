import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { getTraineeDashboardData } from '../api/trainerService';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';
import './TraineeDashboard.css';

const TraineeDashboard = () => {
    const { authState } = useAuth();
    const [dashboardData, setDashboardData] = useState({
        profile: null,
        sessions: [],
        totalSpent: 0,
        monthlySpending: {}
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadDashboardData = async () => {
            try {
                const data = await getTraineeDashboardData();
                setDashboardData(data);
            } catch (error) {
                console.error('Error loading dashboard data:', error);
            } finally {
                setLoading(false);
            }
        };

        if (authState.isAuthenticated) {
            loadDashboardData();
        }
    }, [authState.isAuthenticated]);

    if (loading) return <div className="container"><h2>Loading Dashboard...</h2></div>;
    if (!authState.isAuthenticated) return <div className="container"><h2>Please log in to view your dashboard.</h2></div>;

    const { profile, sessions, totalSpent, monthlySpending } = dashboardData;

    // Separate upcoming and past sessions
    const now = new Date();
    const upcomingSessions = sessions.filter(session => new Date(session.sessionStart) > now);
    const pastSessions = sessions.filter(session => new Date(session.sessionStart) <= now);

    // Prepare chart data
    const chartData = Object.entries(monthlySpending).map(([month, amount]) => ({
        month,
        spending: parseFloat(amount)
    }));

    return (
        <div className="container trainee-dashboard">
            <h1>Learner Dashboard</h1>

            {/* Profile Section */}
            <div className="dashboard-section profile-section">
                <h2>Welcome, {profile?.firstName} {profile?.lastName}</h2>
                <p><strong>Total Spent:</strong> ₹{totalSpent.toFixed(2)}</p>
            </div>

            {/* Upcoming Sessions */}
            <div className="dashboard-section sessions-section">
                <h2>Upcoming Sessions ({upcomingSessions.length})</h2>
                {upcomingSessions.length === 0 ? (
                    <p>No upcoming sessions. <a href="/search">Find a trainer</a></p>
                ) : (
                    <div className="sessions-list">
                        {upcomingSessions.map(session => (
                            <div key={session.id} className="session-card upcoming">
                                <h3>{session.trainer.firstName} {session.trainer.lastName}</h3>
                                <p><strong>Skill:</strong> {session.trainer.mainSkill}</p>
                                <p><strong>Date:</strong> {new Date(session.sessionStart).toLocaleDateString()}</p>
                                <p><strong>Time:</strong> {new Date(session.sessionStart).toLocaleTimeString()} - {new Date(session.sessionEnd).toLocaleTimeString()}</p>
                                <p><strong>Amount:</strong> ₹{session.totalAmount.toFixed(2)}</p>
                                <p><strong>Status:</strong> {session.status}</p>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Past Sessions */}
            <div className="dashboard-section sessions-section">
                <h2>Past Sessions ({pastSessions.length})</h2>
                {pastSessions.length === 0 ? (
                    <p>No past sessions yet.</p>
                ) : (
                    <div className="sessions-list">
                        {pastSessions.map(session => (
                            <div key={session.id} className="session-card past">
                                <h3>{session.trainer.firstName} {session.trainer.lastName}</h3>
                                <p><strong>Skill:</strong> {session.trainer.mainSkill}</p>
                                <p><strong>Date:</strong> {new Date(session.sessionStart).toLocaleDateString()}</p>
                                <p><strong>Amount:</strong> ₹{session.totalAmount.toFixed(2)}</p>
                                <p><strong>Status:</strong> {session.status}</p>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {/* Spending Chart */}
            <div className="dashboard-section chart-section">
                <h2>Monthly Spending</h2>
                {chartData.length === 0 ? (
                    <p>No spending data available.</p>
                ) : (
                    <ResponsiveContainer width="100%" height={300}>
                        <LineChart data={chartData}>
                            <CartesianGrid strokeDasharray="3 3" />
                            <XAxis dataKey="month" />
                            <YAxis />
                            <Tooltip formatter={(value) => [`₹${value.toFixed(2)}`, 'Spending']} />
                            <Line type="monotone" dataKey="spending" stroke="#8884d8" strokeWidth={2} />
                        </LineChart>
                    </ResponsiveContainer>
                )}
            </div>

            <a href="/search" className="dashboard-link">← Continue Searching</a>
        </div>
    );
};

export default TraineeDashboard;
