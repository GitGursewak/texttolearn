import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import { useEffect } from 'react';
import axios from 'axios';
import Home from './pages/Home';
import CourseView from './pages/CourseView';
import LessonView from './pages/LessonView';
import GeneratePage from './pages/GeneratePage';
import AuthPage from './pages/AuthPage';
import { AuthProvider, useAuth } from './context/AuthContext';
import { supabase } from './supabaseClient';

// Set up Axios interceptor to add Supabase token to all requests
axios.interceptors.request.use(async (config) => {
  const { data: { session } } = await supabase.auth.getSession();
  if (session?.access_token) {
    config.headers.Authorization = `Bearer ${session.access_token}`;
  }
  return config;
});

// Protected Route Component
function ProtectedRoute({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/auth" />;
  return children;
}

// Navbar Component to consume Auth
function Navbar() {
  const { user, signOut } = useAuth();
  return (
    <nav className="navbar">
      <Link to="/" className="nav-link" style={{ fontSize: '1.25rem', fontWeight: '700', color: 'var(--primary-color)' }}>TextToLearn</Link>
      
      {user ? (
        <>
          <Link to="/" className="nav-link">My Courses</Link>
          <Link to="/generate" className="nav-link">Generate Course</Link>
          <div style={{ flex: 1 }}></div>
          <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{user.email}</span>
          <button onClick={signOut} className="btn" style={{ background: '#ef4444', padding: '0.4rem 0.8rem', fontSize: '0.9rem' }}>Sign Out</button>
        </>
      ) : (
        <>
          <div style={{ flex: 1 }}></div>
          <Link to="/auth" className="btn" style={{ padding: '0.4rem 0.8rem' }}>Sign In</Link>
        </>
      )}
    </nav>
  );
}

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app-container">
          <Navbar />
          <main className="main-content">
            <Routes>
              <Route path="/auth" element={<AuthPage />} />
              <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
              <Route path="/course/:id" element={<ProtectedRoute><CourseView /></ProtectedRoute>} />
              <Route path="/lesson/:id" element={<ProtectedRoute><LessonView /></ProtectedRoute>} />
              <Route path="/generate" element={<ProtectedRoute><GeneratePage /></ProtectedRoute>} />
            </Routes>
          </main>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
