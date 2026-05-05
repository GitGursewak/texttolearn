import { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';

const API_URL = 'http://localhost:8080/api';

export default function Home() {
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get(`${API_URL}/courses`)
      .then(response => {
        setCourses(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error fetching courses:', error);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Loading courses...</div>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <h1>Available Courses</h1>
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
            <div key={course.id} className="card" style={{ display: 'flex', flexDirection: 'column', marginBottom: 0 }}>
              <span className="badge" style={{ alignSelf: 'flex-start' }}>{course.difficulty}</span>
              <h2>{course.title}</h2>
              <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem', flex: 1 }}>{course.description}</p>
              <Link to={`/course/${course.id}`} className="btn" style={{ textAlign: 'center' }}>Start Learning</Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
