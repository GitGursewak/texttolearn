import { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, Link, useNavigate } from 'react-router-dom';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export default function CourseView() {
  const { id } = useParams();
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const navigate = useNavigate();

  // Fetch course data
  const fetchCourse = () => {
    axios.get(`${API_URL}/courses/${id}`)
      .then(response => {
        setCourse(response.data);
        setLoading(false);
        if (response.data.status === 'PENDING') {
          setGenerating(true);
        } else {
          setGenerating(false);
        }
      })
      .catch(error => {
        console.error('Error fetching course:', error);
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchCourse();
  }, [id]);

  // Subscribe to SSE when course is PENDING
  useEffect(() => {
    if (!generating) return;

    const eventSource = new EventSource(`${API_URL}/courses/${id}/stream`);

    eventSource.addEventListener('COMPLETED', (event) => {
      fetchCourse();
      setGenerating(false);
      eventSource.close();
    });

    eventSource.addEventListener('FAILED', (event) => {
      fetchCourse();
      setGenerating(false);
      eventSource.close();
    });

    eventSource.addEventListener('INIT', (event) => {
      console.log('SSE connected:', event.data);
    });

    eventSource.onerror = () => {
      setTimeout(() => fetchCourse(), 3000);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [generating, id]);

  if (loading) return <div>Loading course details...</div>;
  if (!course) return <div>Course not found.</div>;

  // Show generating animation when status is PENDING
  if (generating) {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '60vh', gap: '2rem' }}>
        <div style={{ position: 'relative', width: '80px', height: '80px' }}>
          <svg width="80" height="80" viewBox="0 0 24 24" fill="none" stroke="var(--primary-color)" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" style={{ animation: 'spin 1.5s linear infinite' }}>
            <path d="M21 12a9 9 0 1 1-6.219-8.56"></path>
          </svg>
        </div>
        <div style={{ textAlign: 'center' }}>
          <h2 style={{ marginBottom: '0.5rem', fontSize: '1.5rem' }}>Generating Your Course</h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem', marginBottom: '1rem' }}>
            Our AI is building a personalized curriculum for <strong>"{course.title}"</strong>
          </p>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
            This usually takes 15–30 seconds. You'll see the content appear automatically.
          </p>
        </div>

        {/* Skeleton loader for modules */}
        <div style={{ width: '100%', maxWidth: '700px', display: 'flex', flexDirection: 'column', gap: '1rem' }}>
          {[1, 2, 3].map((i) => (
            <div key={i} className="card" style={{ opacity: 0.5, animation: `pulse 1.5s ease-in-out infinite ${i * 0.2}s` }}>
              <div style={{ height: '1.2rem', width: '60%', background: 'var(--border-color)', borderRadius: '6px', marginBottom: '0.75rem' }}></div>
              <div style={{ height: '0.8rem', width: '90%', background: 'var(--border-color)', borderRadius: '4px', marginBottom: '0.5rem' }}></div>
              <div style={{ height: '0.8rem', width: '75%', background: 'var(--border-color)', borderRadius: '4px' }}></div>
            </div>
          ))}
        </div>

        <style>{`
          @keyframes spin { 100% { transform: rotate(360deg); } }
          @keyframes pulse { 0%, 100% { opacity: 0.4; } 50% { opacity: 0.7; } }
        `}</style>
      </div>
    );
  }

  // Show error state when generation failed
  if (course.status === 'FAILED') {
    return (
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '60vh', gap: '1.5rem' }}>
        <div style={{ fontSize: '4rem' }}>❌</div>
        <h2 style={{ marginBottom: '0.25rem' }}>Course Generation Failed</h2>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.05rem', textAlign: 'center', maxWidth: '500px' }}>
          Something went wrong while generating <strong>"{course.title}"</strong>. This is usually a temporary issue with the AI service.
        </p>
        <div style={{ display: 'flex', gap: '1rem', marginTop: '0.5rem' }}>
          <button 
            className="btn" 
            onClick={() => navigate('/generate')}
            style={{ padding: '0.75rem 1.5rem' }}
          >
            Try Again
          </button>
          <button 
            className="btn" 
            onClick={() => navigate('/')}
            style={{ padding: '0.75rem 1.5rem', background: 'var(--card-bg)', color: 'var(--text-primary)', border: '1px solid var(--border-color)' }}
          >
            Back to Courses
          </button>
        </div>
      </div>
    );
  }

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
