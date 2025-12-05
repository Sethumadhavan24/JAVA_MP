// frontend/src/components/TrainerCard.js

import React from 'react';
import './TrainerCard.css';

const TrainerCard = ({ trainer }) => {
    // --- Defensive Data Extraction ---
    // 1. Ensure hourlyRate is treated as optional, and format it.
    const hourlyRateValue = trainer.hourlyRate || 0;
    const formattedRate = `₹${hourlyRateValue.toFixed(2)}/hour`;
    
    // 2. Ensure properties are handled even if the object is partially empty
    const firstName = trainer.firstName || 'Expert'; 
    const lastName = trainer.lastName || 'Trainer';
    const mainSkill = trainer.mainSkill || 'Specialty Unknown';
    
    // 3. Status handling (No changes needed, looks robust)
    const verificationBadge = trainer.isCertificationVerified 
        ? '✅ Certified Expert' 
        : '⏳ Verification Pending';

    return (
        // Note: You need to add basic styling for these classes in App.css or TrainerCard.css
        <div className="trainer-card"> 
            <h2>{firstName} {lastName} ({mainSkill})</h2>
            <p><strong>Rate:</strong> {formattedRate}</p>
            <p><strong>Location:</strong> {trainer.location || 'Remote/Global'}</p>
            <p className="badge">{verificationBadge}</p>
            <a href={`/booking/${trainer.id}`} className="button-primary">View Availability & Book</a>
        </div>
    );
};

export default TrainerCard;