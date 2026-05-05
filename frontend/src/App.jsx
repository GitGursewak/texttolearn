import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Home from './pages/Home';
import CourseView from './pages/CourseView';
import LessonView from './pages/LessonView';
import GeneratePage from './pages/GeneratePage';

function App() {
  return (
    <Router>
      <div className="app-container">
        <nav className="navbar">
          <Link to="/" className="nav-link" style={{ fontSize: '1.25rem', fontWeight: '700', color: 'var(--primary-color)' }}>TextToLearn</Link>
          <Link to="/" className="nav-link">Home</Link>
          <Link to="/generate" className="nav-link">Generate Course</Link>
        </nav>
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/course/:id" element={<CourseView />} />
            <Route path="/lesson/:id" element={<LessonView />} />
            <Route path="/generate" element={<GeneratePage />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
