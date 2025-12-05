// frontend/src/pages/SearchPage.js

import React, { useState, useEffect, useRef } from 'react';
import { searchTrainers, getSkillSuggestions } from '../api/trainerService';
import TrainerCard from '../components/TrainerCard';
import './SearchPage.css';

const SearchPage = () => {
    const [skill, setSkill] = useState('');
    const [location, setLocation] = useState('');
    const [results, setResults] = useState([]);
    const [suggestions, setSuggestions] = useState([]); 
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const debounceTimeout = useRef(null); 

    // Debounced function to fetch suggestions (Unchanged)
    useEffect(() => {
        clearTimeout(debounceTimeout.current);

        debounceTimeout.current = setTimeout(async () => {
            if (skill.length > 1) {
                const data = await getSkillSuggestions(skill);
                setSuggestions(data.map(s => s.name));
            } else {
                setSuggestions([]);
            }
        }, 300);

        return () => clearTimeout(debounceTimeout.current);
    }, [skill]);

    const handleSearch = async (e) => {
        e.preventDefault();

        if (!skill.trim()) return;

        setLoading(true);
        setError(null);
        setSuggestions([]); 

        try {
            // Pass both skill and location. The backend logic handles filtering.
            const data = await searchTrainers(skill, location); 
            
            setResults(data);
        } catch (err) {
            setError("Failed to fetch trainers. Is the Spring Boot API running?");
            setResults([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSuggestionClick = (suggestionName) => {
        setSkill(suggestionName); 
        setSuggestions([]);
    };

    return (
        <div className="container">
            <h1>Find Your Expert Trainer</h1>
            <form onSubmit={handleSearch} className="search-form">
                <div style={{ position: 'relative', flexGrow: 1 }}>
                    <input
                        type="text"
                        value={skill}
                        onChange={(e) => setSkill(e.target.value)}
                        placeholder="Skill (e.g., Coding, Yoga)"
                        required
                    />
                    {suggestions.length > 0 && (
                        <ul className="suggestions-list">
                            {suggestions.map((s, index) => (
                                <li key={index} onClick={() => handleSuggestionClick(s)}>
                                    {s}
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
                
                <input
                    type="text"
                    value={location}
                    onChange={(e) => setLocation(e.target.value)}
                    placeholder="Location (e.g., Chennai)"
                />
                <button type="submit" disabled={loading} id="searchButton">
                    {loading ? 'Searching...' : 'Search'}
                </button>
            </form>

            {/* Results Display */}
            <div className="results-list">
                {error && <p className="error-message">{error}</p>}
                
                {loading && <p>Loading results...</p>}

                {!loading && results.length === 0 && !error && (
                    <p>No trainers found. Try searching above!</p>
                )}

                {results.map(trainer => (
                    <TrainerCard key={trainer.id} trainer={trainer} />
                ))}
            </div>
        </div>
    );
};

export default SearchPage;