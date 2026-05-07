import { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export default function Home() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchCourses = () => {
    axios.get(`${API_URL}/courses`)
      .then(response => {
        setCourses(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error fetching courses:', error);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  const handleDelete = async (courseId) => {
    try {
      await axios.delete(`${API_URL}/courses/${courseId}`);
      fetchCourses();
    } catch (error) {
      console.error('Error deleting course:', error);
    }
  };

  if (loading) return <div>Loading courses...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <h1>My Courses</h1>
        <Link to="/generate" className="btn">Generate New Course</Link>
      </div>
      
      {courses.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '1rem' }}>No courses available yet.</p>
          <Link to="/generate" className="btn">Create Your First Course</Link>
        </div>
      ) : (
        <div style={{ display: 'grid', gap: '1.5rem', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))' }}>
          {courses.map(course => (
            <div key={course.id} className="card" style={{ 
              display: 'flex', flexDirection: 'column', marginBottom: 0,
              opacity: course.status === 'PENDING' ? 0.85 : 1,
              animation: course.status === 'PENDING' ? 'pulse 2s ease-in-out infinite' : 'none'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                <span className="badge" style={{ alignSelf: 'flex-start' }}>{course.difficulty}</span>
                {course.status === 'PENDING' && (
                  <span style={{ 
                    fontSize: '0.75rem', fontWeight: 600, padding: '0.25rem 0.6rem', 
                    borderRadius: '999px', background: 'rgba(234, 179, 8, 0.15)', color: '#eab308'
                  }}>⏳ Generating...</span>
                )}
                {course.status === 'FAILED' && (
                  <span style={{ 
                    fontSize: '0.75rem', fontWeight: 600, padding: '0.25rem 0.6rem', 
                    borderRadius: '999px', background: 'rgba(239, 68, 68, 0.15)', color: '#ef4444'
                  }}>❌ Failed</span>
                )}
              </div>
              <h2>{course.title}</h2>
              <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem', flex: 1 }}>{course.description}</p>
              
              {course.status === 'PENDING' ? (
                <Link to={`/course/${course.id}`} className="btn" style={{ textAlign: 'center', background: 'var(--card-bg)', color: 'var(--text-secondary)', border: '1px solid var(--border-color)' }}>
                  View Progress
                </Link>
              ) : (
                <div style={{ display: 'flex', gap: '0.5rem' }}>
                  {course.status === 'FAILED' ? (
                    <Link to="/generate" className="btn" style={{ textAlign: 'center', flex: 1 }}>Generate Again</Link>
                  ) : (
                    <Link to={`/course/${course.id}`} className="btn" style={{ textAlign: 'center', flex: 1 }}>Start Learning</Link>
                  )}
                  <button 
                    onClick={() => { if (confirm('Delete this course?')) handleDelete(course.id); }} 
                    className="btn" 
                    style={{ background: 'rgba(239, 68, 68, 0.1)', color: '#ef4444', border: '1px solid rgba(239, 68, 68, 0.3)', padding: '0.5rem 0.75rem', fontSize: '1rem' }}
                    title="Delete course"
                  >
                    🗑️
                  </button>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
      <style>{`
        @keyframes pulse { 0%, 100% { opacity: 0.7; } 50% { opacity: 1; } }
      `}</style>
    </div>
  );
}
