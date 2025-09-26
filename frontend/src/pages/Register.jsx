import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { User, Mail, Lock, Eye, EyeOff, UserPlus } from 'lucide-react';

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    confirmPassword: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validações
    if (formData.password !== formData.confirmPassword) {
      setError('As senhas não coincidem');
      return;
    }

    if (formData.password.length < 6) {
      setError('A senha deve ter pelo menos 6 caracteres');
      return;
    }

    setLoading(true);

    try {
      const userData = {
        name: formData.name,
        email: formData.email,
        password: formData.password
      };

      const result = await register(userData);
      if (result.success) {
        navigate('/login', { 
          state: { 
            message: 'Conta criada com sucesso! Faça login para continuar.' 
          } 
        });
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
    setFormData({
      ...formData,
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
              background: 'linear-gradient(135deg, #16a34a 0%, #0f766e 100%)',
              color: 'white',
              boxShadow: 'var(--shadow-md)',
            }}
          >
            <UserPlus size={28} />
          </div>
          <h1 className="auth-card__title">Criar conta</h1>
          <p className="auth-card__subtitle">
            Registre-se para começar a gerenciar os recursos compartilhados do campus.
          </p>
        </div>

        <form className="stack" style={{ gap: '1.2rem' }} onSubmit={handleSubmit}>
          {error && <div className="alert alert--error">{error}</div>}

          <div className="stack" style={{ gap: '1rem' }}>
            <div className="form-control">
              <label htmlFor="name">Nome completo</label>
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
                  id="name"
                  name="name"
                  type="text"
                  required
                  value={formData.name}
                  onChange={handleChange}
                  className="input"
                  placeholder="Digite seu nome completo"
                  style={{ paddingLeft: '2.8rem' }}
                />
              </div>
            </div>

            <div className="form-control">
              <label htmlFor="email">Email institucional</label>
              <div style={{ position: 'relative' }}>
                <Mail
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
                  id="email"
                  name="email"
                  type="email"
                  required
                  value={formData.email}
                  onChange={handleChange}
                  className="input"
                  placeholder="nome@instituicao.edu"
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
                  value={formData.password}
                  onChange={handleChange}
                  className="input"
                  placeholder="Crie uma senha (mínimo 6 caracteres)"
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

            <div className="form-control">
              <label htmlFor="confirmPassword">Confirmar senha</label>
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
                  id="confirmPassword"
                  name="confirmPassword"
                  type={showConfirmPassword ? 'text' : 'password'}
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  className="input"
                  placeholder="Repita a senha escolhida"
                  style={{ paddingLeft: '2.8rem', paddingRight: '2.8rem' }}
                />
                <button
                  type="button"
                  onClick={() => setShowConfirmPassword(!showConfirmPassword)}
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
                  {showConfirmPassword ? <EyeOff size={18} /> : <Eye size={18} />}
                </button>
              </div>
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="btn btn--primary"
            style={{ width: '100%', minHeight: '48px', fontSize: '1rem', background: 'linear-gradient(135deg, #16a34a 0%, #0f766e 100%)' }}
          >
            {loading ? 'Criando conta...' : 'Criar conta'}
          </button>

          <div className="divider" style={{ margin: '1rem 0' }} />

          <p className="support-text" style={{ textAlign: 'center' }}>
            Já possui acesso?
            {' '}
            <Link to="/login" className="link">
              Faça login aqui
            </Link>
          </p>
        </form>
      </div>
    </div>
  );
};

export default Register;