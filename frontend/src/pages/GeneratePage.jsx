import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export default function GeneratePage() {
  const [topic, setTopic] = useState('');
  const [difficulty, setDifficulty] = useState('beginner');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleGenerate = async (e) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      const response = await axios.post(`${API_URL}/courses/generate`, null, {
        params: { topic, difficulty }
      });
      // Backend returns 202 Accepted with the skeleton course instantly
      // Navigate to course view which will show a loading animation and subscribe to SSE
      navigate(`/course/${response.data.id}`);
    } catch (error) {
      console.error('Error generating course:', error);
      alert('Failed to start course generation. Please try again.');
      setLoading(false);
    }
  };

  return (
    <div className="card" style={{ maxWidth: '600px', margin: '2rem auto' }}>
      <h1 style={{ fontSize: '1.75rem', marginBottom: '0.5rem', textAlign: 'center' }}>Generate New Course</h1>
      <p style={{ color: 'var(--text-secondary)', textAlign: 'center', marginBottom: '2rem' }}>
        Let AI create a customized learning path for you in seconds.
      </p>

      <form onSubmit={handleGenerate} style={{ display: 'flex', flexDirection: 'column' }}>
        <div className="form-group">
          <label htmlFor="topic" className="form-label">What do you want to learn?</label>
          <input 
            type="text" 
            id="topic" 
            className="form-input"
            value={topic} 
            onChange={(e) => setTopic(e.target.value)} 
            placeholder="e.g. Python Programming, Machine Learning, World History"
            required 
          />
        </div>
        
        <div className="form-group">
          <label htmlFor="difficulty" className="form-label">Difficulty Level</label>
          <select 
            id="difficulty" 
            className="form-select"
            value={difficulty} 
            onChange={(e) => setDifficulty(e.target.value)}
          >
            <option value="beginner">Beginner</option>
            <option value="intermediate">Intermediate</option>
            <option value="advanced">Advanced</option>
          </select>
        </div>
        
        <button 
          type="submit" 
          className="btn" 
          disabled={loading} 
          style={{ 
            marginTop: '1rem', 
            padding: '1rem', 
            fontSize: '1.1rem',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            gap: '0.5rem'
          }}
        >
          {loading ? (
            <>
              <svg className="animate-spin" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{ animation: 'spin 1s linear infinite' }}>
                <path d="M21 12a9 9 0 1 1-6.219-8.56"></path>
              </svg>
              Generating Course...
            </>
          ) : 'Generate Course with AI ✨'}
        </button>
      </form>
      <style>{`
        @keyframes spin { 100% { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
}
