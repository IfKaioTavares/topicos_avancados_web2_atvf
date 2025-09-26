import React from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from './context/AuthContext'
import { ProtectedRoute, PublicRoute } from './components/ProtectedRoute'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import HistoryPage from './pages/HistoryPage'
import StatsPage from './pages/StatsPage'
import AdminResourcesPage from './pages/AdminResourcesPage'
import './App.css'

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="App">
          <Routes>
            {/* Rota raiz redireciona para dashboard */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            
            {/* Rotas públicas */}
            <Route path="/login" element={
              <PublicRoute>
                <Login />
              </PublicRoute>
            } />
            <Route path="/register" element={
              <PublicRoute>
                <Register />
              </PublicRoute>
            } />
            
            {/* Rotas protegidas */}
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            } />
            <Route path="/history" element={
              <ProtectedRoute>
                <HistoryPage />
              </ProtectedRoute>
            } />
            <Route path="/stats" element={
              <ProtectedRoute>
                <StatsPage />
              </ProtectedRoute>
            } />
            
            {/* Rotas de administração */}
            <Route path="/admin/resources" element={
              <ProtectedRoute adminOnly={true}>
                <AdminResourcesPage />
              </ProtectedRoute>
            } />
            
            {/* Rota para página não encontrada */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  )
}

export default App
