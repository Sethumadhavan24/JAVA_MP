// frontend/src/pages/BookingPage.js (Full Code)

import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { getAvailableSlots, submitBooking, getTrainerProfileById } from '../api/trainerService';
import './BookingPage.css';

const BookingPage = () => {
    const { authState } = useAuth();
    const { trainerId } = useParams(); // Get trainerId from the URL path
    
    const [trainer, setTrainer] = useState(null);
    const [slots, setSlots] = useState([]);
    const [loading, setLoading] = useState(true);
    const [message, setMessage] = useState(null);

    // Get the actual Trainee User ID from the context
    const TRAINEE_USER_ID = authState.userId;

    // Effect to load trainer details and slots
    useEffect(() => {
        const loadData = async () => {
            setLoading(true);
            
            // 1. Fetch Profile Details
            const trainerData = await getTrainerProfileById(trainerId);
            setTrainer(trainerData);
            
            // 2. Fetch Slots
            const slotsData = await getAvailableSlots(trainerId);
            setSlots(slotsData);
            
            setLoading(false);
        };
        loadData();
    }, [trainerId]);

    // Handle the transactional submission
    const handleBooking = async (slotId) => {
        if (!authState.isAuthenticated) {
            setMessage({ type: 'error', text: 'You must be logged in to book a session.' });
            return;
        }

        setMessage(null);
        try {
            const booking = await submitBooking(TRAINEE_USER_ID, slotId);
            
            // Success: Update UI
            setMessage({ 
                type: 'success', 
                text: `Booked! Total: ₹${booking.totalAmount.toFixed(2)}. Payout: ₹${booking.trainerPayout.toFixed(2)}. (Payment Pending)` 
            });
            // Reload slots to show the booked slot is gone
            setSlots(prevSlots => prevSlots.filter(slot => slot.id !== slotId));

        } catch (errorData) {
            let errorText = 'Booking failed. Please try again.';
            if (typeof errorData === 'string' && errorData.includes('available')) {
                 errorText = 'CONFILCT: Slot was just booked. Choose another time.'; // Concurrency Lock!
            } else if (typeof errorData === 'string') {
                 errorText = `Error: ${errorData}`;
            }
            setMessage({ type: 'error', text: errorText });
        }
    };

    if (loading) return <div className="container"><h2>Loading Booking Details...</h2></div>;
    if (!trainer) return <div className="container"><h2 style={{color: 'red'}}>Trainer Not Found.</h2></div>;


    return (
        <div className="container booking-page">
            <h1 className="header">{trainer.firstName} {trainer.lastName} ({trainer.mainSkill})</h1>
            <p className="status-badge">Rate: **₹{trainer.hourlyRate.toFixed(2)} / hour, ₹{trainer.dailyRate ? trainer.dailyRate.toFixed(2) : '0'} / day ({trainer.rateType})**</p>
            <p>{trainer.bio || "This expert has not provided a detailed bio yet."}</p>
            
            <div className={`alert alert-${message?.type}`} style={{ display: message ? 'block' : 'none' }}>
                {message?.text}
            </div>

            <h2>Available Slots ({slots.length} open)</h2>
            <div className="slot-list">
                {slots.length === 0 ? (
                    <p>No immediate availability. Check back later!</p>
                ) : (
                    slots.map(slot => (
                        <div key={slot.id} className="slot-card">
                            <span>
                                {new Date(slot.startTime).toLocaleString('en-IN', { dateStyle: 'medium', timeStyle: 'short' })} 
                                - 
                                {new Date(slot.endTime).toLocaleTimeString('en-IN', { timeStyle: 'short' })}
                            </span>
                            <button 
                                onClick={() => handleBooking(slot.id)}
                                disabled={!authState.isAuthenticated}
                                className="book-button">
                                {authState.isAuthenticated ? 'BOOK NOW' : 'Log In to Book'}
                            </button>
                        </div>
                    ))
                )}
            </div>
            <a href="/search" className="back-link">← Continue Searching</a>
        </div>
    );
};

export default BookingPage;