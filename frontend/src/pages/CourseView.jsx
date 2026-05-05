import { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, Link } from 'react-router-dom';

const API_URL = 'http://localhost:8080/api';

export default function CourseView() {
  const { id } = useParams();
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    axios.get(`${API_URL}/courses/${id}`)
      .then(response => {
        setCourse(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error fetching course:', error);
        setLoading(false);
      });
  }, [id]);

  if (loading) return <div>Loading course details...</div>;
  if (!course) return <div>Course not found.</div>;

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <Link to="/" style={{ display: 'inline-block', marginBottom: '1rem', color: 'var(--text-secondary)' }}>&larr; Back to Home</Link>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', marginBottom: '0.5rem' }}>
          <h1 style={{ marginBottom: 0 }}>{course.title}</h1>
          <span className="badge" style={{ marginBottom: 0 }}>{course.difficulty}</span>
        </div>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>{course.description}</p>
      </div>
      
      <h2>Course Modules</h2>
      {course.modules && course.modules.length > 0 ? (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          {course.modules.map((mod, index) => (
            <div key={mod.id || index} className="card" style={{ marginBottom: 0 }}>
              <h3>Module {index + 1}: {mod.title}</h3>
              <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>{mod.description}</p>
              
              <h4 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '0.75rem', color: 'var(--text-primary)' }}>Lessons</h4>
              {mod.lessons && mod.lessons.length > 0 ? (
                <div style={{ display: 'grid', gap: '0.75rem' }}>
                  {mod.lessons.map((lesson, lessonIndex) => (
                    <Link 
                      key={lesson.id} 
                      to={`/lesson/${lesson.id}`}
                      state={{ courseId: course.id }}
                      style={{ 
                        display: 'flex', 
                        alignItems: 'center', 
                        padding: '1rem', 
                        background: 'var(--bg-color)', 
                        border: '1px solid var(--border-color)',
                        borderRadius: 'var(--radius-md)',
                        color: 'var(--text-primary)',
                        transition: 'border-color 0.2s'
                      }}
                      onMouseOver={(e) => e.currentTarget.style.borderColor = 'var(--primary-color)'}
                      onMouseOut={(e) => e.currentTarget.style.borderColor = 'var(--border-color)'}
                    >
                      <span style={{ marginRight: '1rem', color: 'var(--primary-color)', fontWeight: 600 }}>{lessonIndex + 1}.</span>
                      {lesson.title}
                    </Link>
                  ))}
                </div>
              ) : (
                <p style={{ color: 'var(--text-secondary)', fontStyle: 'italic' }}>No lessons in this module.</p>
              )}
            </div>
          ))}
        </div>
      ) : (
        <div className="card" style={{ textAlign: 'center', padding: '3rem' }}>
          <p style={{ color: 'var(--text-secondary)' }}>No modules available for this course yet.</p>
        </div>
      )}
    </div>
  );
}
