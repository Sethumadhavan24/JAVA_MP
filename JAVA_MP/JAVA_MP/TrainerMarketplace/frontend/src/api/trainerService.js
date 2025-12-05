// frontend/src/api/trainerService.js (Full Code)

import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

// 1. Search Trainer Profiles (Existing)
export const searchTrainers = async (skill, location, verified = false) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/search/trainers`, {
            params: { skill, location, verified }
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching trainers:", error);
        return [];
    }
};

// 2. Get Skill Suggestions API (Existing)
export const getSkillSuggestions = async (query) => {
    if (!query || query.length < 2) return [];
    try {
        const response = await axios.get(`${API_BASE_URL}/search/skills/suggest`, {
            params: { query }
        });
        return response.data;
    } catch (error) {
        console.error("Error fetching suggestions:", error);
        return [];
    }
};

// 3. Get Single Trainer Profile (Updated to use new endpoint)
export const getTrainerProfileById = async (trainerId) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/search/trainers/${trainerId}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching trainer details:", error);
        return null;
    }
};

// 4. Get Availability Slots (Existing)
export const getAvailableSlots = async (trainerId) => {
    try {
        const response = await axios.get(`${API_BASE_URL}/booking/trainer/${trainerId}/slots`);
        return response.data;
    } catch (error) {
        console.error("Error fetching slots:", error);
        return [];
    }
};

// 5. Submit Booking Transaction (Existing)
export const submitBooking = async (traineeUserId, slotId) => {
    const token = localStorage.getItem('userToken');

    try {
        const response = await axios.post(`${API_BASE_URL}/booking/submit`, null, {
            params: { traineeUserId, slotId },
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

// 6. FIX: Registration API Call
// This was the missing export causing the error in AuthContext.js
export const registerUser = async (userData) => {
    try {
        const response = await axios.post(`${API_BASE_URL}/auth/register`, userData);
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

// --- Trainer Dashboard API ---
export const getTrainerDashboardData = async () => {
    const token = localStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    const profileResponse = await axios.get(`${API_BASE_URL}/trainer/dashboard/profile`, { headers });
    const bookingsResponse = await axios.get(`${API_BASE_URL}/trainer/dashboard/bookings`, { headers });
    const currentMonthEarningsResponse = await axios.get(`${API_BASE_URL}/trainer/dashboard/earnings/current-month`, { headers });
    const totalEarningsResponse = await axios.get(`${API_BASE_URL}/trainer/dashboard/earnings/total`, { headers });
    const monthlyEarningsResponse = await axios.get(`${API_BASE_URL}/trainer/dashboard/earnings/monthly`, { headers });
    const availabilityResponse = await axios.get(`${API_BASE_URL}/trainer/dashboard/availability`, { headers });

    return {
        profile: profileResponse.data,
        bookings: bookingsResponse.data,
        currentMonthEarnings: currentMonthEarningsResponse.data,
        totalEarnings: totalEarningsResponse.data,
        monthlyEarnings: monthlyEarningsResponse.data,
        availability: availabilityResponse.data
    };
};

// --- Trainee Dashboard API ---
export const getTraineeDashboardData = async () => {
    const token = localStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    const profileResponse = await axios.get(`${API_BASE_URL}/trainee/dashboard/profile`, { headers });
    const sessionsResponse = await axios.get(`${API_BASE_URL}/trainee/dashboard/sessions`, { headers });
    const totalSpentResponse = await axios.get(`${API_BASE_URL}/trainee/dashboard/total-spent`, { headers });
    const monthlySpendingResponse = await axios.get(`${API_BASE_URL}/trainee/dashboard/monthly-spending`, { headers });

    return {
        profile: profileResponse.data,
        sessions: sessionsResponse.data,
        totalSpent: totalSpentResponse.data,
        monthlySpending: monthlySpendingResponse.data
    };
};

// --- Trainer Availability API ---
export const addAvailability = async (trainerId, availabilityData) => {
    const token = localStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    try {
        const response = await axios.post(`${API_BASE_URL}/booking/trainer/${trainerId}/availability`, availabilityData, { headers });
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

// --- Delete Availability API ---
export const deleteAvailability = async (availabilityId) => {
    const token = localStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    try {
        const response = await axios.delete(`${API_BASE_URL}/trainer/dashboard/availability/${availabilityId}`, { headers });
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};

// --- Update Trainer Profile API ---
export const updateTrainerProfile = async (profileData) => {
    const token = localStorage.getItem('userToken');
    const headers = token ? { Authorization: `Bearer ${token}` } : {};

    try {
        const response = await axios.put(`${API_BASE_URL}/trainer/dashboard/profile`, profileData, { headers });
        return response.data;
    } catch (error) {
        throw error.response ? error.response.data : error.message;
    }
};
