import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, Settings, Menu, X, User, Shield } from 'lucide-react';

const Navbar = () => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navigation = [
    { name: 'Dashboard', href: '/dashboard' },
    { name: 'Histórico', href: '/history' },
    { name: 'Estatísticas', href: '/stats' },
  ];

  const adminNavigation = [
    { name: 'Gerenciar Recursos', href: '/admin/resources' },
    // { name: 'Usuários', href: '/admin/users' }, // habilite quando houver rota
    // { name: 'Relatórios', href: '/admin/reports' },
  ];

  const isActive = (href) => {
    if (href === '/') return location.pathname === '/';
    return location.pathname === href || location.pathname.startsWith(href + '/');
  };

  const renderNavLinks = (links, variant = 'default') => (
    <div className="nav-links">
      {links.map((item) => (
        <button
          key={item.name}
          onClick={() => navigate(item.href)}
          className={`nav-link ${
            isActive(item.href) ? 'nav-link--active' : ''
          } ${variant === 'admin' ? 'nav-link--admin' : ''}`.trim()}
        >
          {item.name}
        </button>
      ))}
    </div>
  );

  return (
    <nav className="navbar" role="navigation" aria-label="Menu principal">
      <div className="navbar__inner">
        <div className="navbar__brand">
          <span className="navbar__logo" aria-hidden="true">
            <Settings size={20} />
          </span>
          <div>
            <span className="navbar__title">IoT Manager</span>
            <div className="support-text" style={{ marginTop: 2 }}>Monitoramento de recursos inteligentes</div>
          </div>
        </div>

        {renderNavLinks(navigation)}

        {isAdmin() && (
          <div className="navbar__meta-extra">
            {renderNavLinks(adminNavigation, 'admin')}
          </div>
        )}

        <div className="navbar__meta">
          <div className="navbar__user">
            {isAdmin() ? (
              <Shield size={16} color="#dc2626" />
            ) : (
              <User size={16} color="#64748b" />
            )}
            <span className="navbar__user-name">{user?.name || user?.email}</span>
            {isAdmin() && <span className="badge badge--red">Admin</span>}
          </div>
          <button onClick={handleLogout} className="btn btn--ghost" type="button">
            <LogOut size={16} />
            Sair
          </button>
        </div>

        <button
          type="button"
          className="mobile-trigger"
          onClick={() => setMobileMenuOpen(true)}
          aria-label="Abrir menu"
        >
          <Menu size={20} />
        </button>
      </div>

      {mobileMenuOpen && (
        <div className="mobile-sheet" onClick={() => setMobileMenuOpen(false)}>
          <div className="mobile-sheet__card" onClick={(event) => event.stopPropagation()}>
            <div className="flex-between" style={{ marginBottom: '0.75rem' }}>
              <div>
                <div className="navbar__title" style={{ fontSize: '1rem' }}>Navegação</div>
                <div className="support-text">Acesse rapidamente qualquer área</div>
              </div>
              <button
                type="button"
                className="mobile-trigger"
                onClick={() => setMobileMenuOpen(false)}
                aria-label="Fechar menu"
              >
                <X size={20} />
              </button>
            </div>

            <div className="mobile-sheet__links">
              {[...navigation, ...(isAdmin() ? adminNavigation : [])].map((item) => (
                <button
                  key={item.name}
                  type="button"
                  onClick={() => {
                    navigate(item.href);
                    setMobileMenuOpen(false);
                  }}
                  className={`mobile-sheet__link ${
                    isActive(item.href) ? 'mobile-sheet__link--active' : ''
                  }`.trim()}
                >
                  {item.name}
                </button>
              ))}
            </div>

            <div className="divider" style={{ margin: '1rem 0' }}></div>

            <div className="stack-sm">
              <div className="navbar__user" style={{ width: '100%' }}>
                {isAdmin() ? (
                  <Shield size={18} color="#dc2626" />
                ) : (
                  <User size={18} color="#64748b" />
                )}
                <div className="stack-xs" style={{ alignItems: 'flex-start' }}>
                  <span className="navbar__user-name" style={{ fontSize: '0.95rem' }}>
                    {user?.name || user?.email}
                  </span>
                  <span className="support-text" style={{ margin: 0 }}>
                    {isAdmin() ? 'Administrador do sistema' : 'Usuário autenticado'}
                  </span>
                </div>
              </div>
              <button type="button" className="btn btn--primary" onClick={handleLogout}>
                <LogOut size={16} />
                Encerrar sessão
              </button>
            </div>
          </div>
        </div>
      )}
    </nav>
  );
};

export default Navbar;