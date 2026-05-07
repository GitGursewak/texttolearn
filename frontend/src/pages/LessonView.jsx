import { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, Link, useLocation } from 'react-router-dom';
import ReactMarkdown from 'react-markdown';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export default function LessonView() {
  const { id } = useParams();
  const location = useLocation();
  const [lesson, setLesson] = useState(null);
  const [loading, setLoading] = useState(true);

  // If we came from the CourseView, we have the courseId in state. 
  // Otherwise, default to home page.
  const courseId = location.state?.courseId;
  const backLink = courseId ? `/course/${courseId}` : '/';
  const backText = courseId ? '\u2190 Back to Course' : '\u2190 Back to Home';

  useEffect(() => {
    axios.get(`${API_URL}/lessons/${id}`)
      .then(response => {
        setLesson(response.data);
        setLoading(false);
      })
      .catch(error => {
        console.error('Error fetching lesson:', error);
        setLoading(false);
      });
  }, [id]);

  if (loading) return <div>Loading lesson...</div>;
  if (!lesson) return <div>Lesson not found.</div>;

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <Link to={backLink} style={{ display: 'inline-block', marginBottom: '1rem', color: 'var(--text-secondary)' }}>
          {backText}
        </Link>
        <h1 style={{ marginBottom: 0 }}>{lesson.title}</h1>
      </div>
      
      <div className="card markdown-body" style={{ padding: '2rem' }}>
        <ReactMarkdown>
          {lesson.content ? lesson.content.split('\n').map(line => line.trimStart()).join('\n') : ''}
        </ReactMarkdown>
      </div>

      {lesson.videoUrl && (
        <div className="video-container">
          <div>
            <h3 style={{ marginBottom: '0.25rem', color: 'var(--text-primary)' }}>Related Video</h3>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Enhance your learning with this visual guide.</p>
          </div>
          <a href={lesson.videoUrl} target="_blank" rel="noopener noreferrer" className="btn" style={{ background: '#ef4444', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19.615 3.184c-3.604-.246-11.631-.245-15.23 0-3.897.266-4.356 2.62-4.385 8.816.029 6.185.484 8.549 4.385 8.816 3.6.245 11.626.246 15.23 0 3.897-.266 4.356-2.62 4.385-8.816-.029-6.185-.484-8.549-4.385-8.816zm-10.615 12.816v-8l8 3.993-8 4.007z"/>
            </svg>
            Watch on YouTube
          </a>
        </div>
      )}
    </div>
  );
}
