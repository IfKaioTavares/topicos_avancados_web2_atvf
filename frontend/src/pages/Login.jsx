import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Lock, User, Eye, EyeOff } from 'lucide-react';

const Login = () => {
  const [credentials, setCredentials] = useState({
    username: '',
    password: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || '/dashboard';

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const result = await login(credentials);
      if (result.success) {
        navigate(from, { replace: true });
      } else {
        setError(result.error);
      }
    } catch {
      setError('Erro inesperado. Tente novamente.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    setCredentials({
      ...credentials,
      [e.target.name]: e.target.value
    });
  };

  return (
    <div className="auth-layout">
      <div className="auth-card">
        <div className="auth-card__header">
          <div
            style={{
              width: '64px',
              height: '64px',
              borderRadius: '20px',
              display: 'grid',
              placeItems: 'center',
              margin: '0 auto',
              background: 'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)',
              color: 'white',
              boxShadow: 'var(--shadow-md)',
            }}
          >
            <Lock size={28} />
          </div>
          <h1 className="auth-card__title">Acessar o sistema</h1>
          <p className="auth-card__subtitle">
            Gerencie reservas e monitore recursos IoT em tempo real.
          </p>
        </div>

        <form className="stack" style={{ gap: '1.25rem' }} onSubmit={handleSubmit}>
          {error && <div className="alert alert--error">{error}</div>}

          <div className="stack" style={{ gap: '1rem' }}>
            <div className="form-control">
              <label htmlFor="username">Nome de usuário</label>
              <div style={{ position: 'relative' }}>
                <User
                  size={18}
                  style={{
                    position: 'absolute',
                    top: '50%',
                    left: '0.85rem',
                    transform: 'translateY(-50%)',
                    color: 'var(--color-muted)',
                  }}
                />
                <input
                  id="username"
                  name="username"
                  type="text"
                  required
                  value={credentials.username}
                  onChange={handleChange}
                  className="input"
                  placeholder="Digite seu nome de usuário"
                  style={{ paddingLeft: '2.8rem' }}
                />
              </div>
            </div>

            <div className="form-control">
              <label htmlFor="password">Senha</label>
              <div style={{ position: 'relative' }}>
                <Lock
                  size={18}
                  style={{
                    position: 'absolute',
                    top: '50%',
                    left: '0.85rem',
                    transform: 'translateY(-50%)',
                    color: 'var(--color-muted)',
                  }}
                />
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  required
                  value={credentials.password}
                  onChange={handleChange}
                  className="input"
                  placeholder="Digite sua senha"
                  style={{ paddingLeft: '2.8rem', paddingRight: '2.8rem' }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute',
                    top: '50%',
                    right: '0.85rem',
                    transform: 'translateY(-50%)',
                    border: 'none',
                    background: 'transparent',
                    color: 'var(--color-muted)',
                    cursor: 'pointer',
                  }}
                >
                  {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn--primary"
            style={{ width: '100%', minHeight: '48px', fontSize: '1rem' }}
          >
            {loading ? 'Entrando...' : 'Entrar no sistema'}
          </button>

          <div className="divider" style={{ margin: '1rem 0' }} />

          <p className="support-text" style={{ textAlign: 'center' }}>
            Não possui conta?
            {' '}
            <Link to="/register" className="link">
              Registre-se aqui
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Login;