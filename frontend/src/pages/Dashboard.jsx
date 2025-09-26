import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { resourceService, reserveService } from '../services';
import { Settings, Wifi, WifiOff, Clock, RefreshCw } from 'lucide-react';
import Navbar from '../components/Navbar';
import ResourceCard from '../components/ResourceCard';

const Dashboard = () => {
  const { user } = useAuth();
  const [resources, setResources] = useState([]);
  const [activeReserves, setActiveReserves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState('');
  const [lastUpdated, setLastUpdated] = useState(null);

  // Carregar dados iniciais
  useEffect(() => {
    loadDashboardData();
    
    // Atualizar dados a cada 30 segundos
    const interval = setInterval(loadDashboardData, 30000);
    return () => clearInterval(interval);
  }, []);

  const loadDashboardData = async () => {
    try {
      setError('');
      const [resourcesData, reservesData] = await Promise.all([
        resourceService.getResources(),
        reserveService.getMyActiveReserves()
      ]);
      
  setResources(resourcesData);
  setActiveReserves(reservesData);
  setLastUpdated(new Date());
    } catch (error) {
      console.error('Erro ao carregar dados:', error);
      setError('Erro ao carregar dados do sistema');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const handleRefresh = () => {
    setRefreshing(true);
    loadDashboardData();
  };

  const handleReserve = async (resourceId) => {
    try {
      await reserveService.createReserve({ resourceId });
      await loadDashboardData(); // Recarregar dados
    } catch (error) {
      console.error('Erro ao fazer reserva:', error);
      setError('Erro ao fazer reserva');
    }
  };

  const handleRelease = async (reserveId) => {
    try {
      await reserveService.releaseReserve(reserveId);
      await loadDashboardData(); // Recarregar dados
    } catch (error) {
      console.error('Erro ao liberar reserva:', error);
      setError('Erro ao liberar reserva');
    }
  };

  if (loading) {
    return (
      <div className="app-shell">
        <Navbar />
        <main className="page-shell">
          <div className="loading-state">Carregando painel...</div>
        </main>
      </div>
    );
  }

  // Estatísticas rápidas
  const totalResources = resources.length;
  const availableResources = resources.filter(r => r.status === 'FREE').length;
  const occupiedResources = resources.filter(r => r.status === 'OCCUPIED').length;
  const myActiveReservesCount = activeReserves.length;

  const stats = [
    {
      label: 'Total de recursos',
      value: totalResources,
      icon: <Settings size={20} color="#3730a3" />,
      description: 'Infraestrutura monitorada pelo sistema',
    },
    {
      label: 'Disponíveis agora',
      value: availableResources,
      icon: <Wifi size={20} color="#16a34a" />,
      description: 'Prontos para reserva imediata',
    },
    {
      label: 'Em uso neste momento',
      value: occupiedResources,
      icon: <WifiOff size={20} color="#dc2626" />,
      description: 'Recursos ocupados por outros usuários',
    },
    {
      label: 'Minhas reservas ativas',
      value: myActiveReservesCount,
      icon: <Clock size={20} color="#2563eb" />,
      description: 'Reservas que você acompanha agora',
    },
  ];

  return (
    <div className="app-shell">
      <Navbar />
      <main className="page-shell">
        <header className="page-header">
          <div>
            <h1 className="page-header__title">Visão geral do ecossistema IoT</h1>
            <p className="page-header__subtitle">
              Bem-vindo, {user?.name || user?.email}. Acompanhe o status dos recursos compartilhados.
            </p>
            {lastUpdated && (
              <div className="support-text" style={{ marginTop: '0.35rem' }}>
                Última atualização às {lastUpdated.toLocaleTimeString('pt-BR')}
              </div>
            )}
          </div>
          <div className="page-actions">
            <button
              type="button"
              onClick={handleRefresh}
              disabled={refreshing}
              className="btn btn--ghost"
              style={refreshing ? { opacity: 0.6, cursor: 'wait' } : undefined}
            >
              <RefreshCw size={16} className={refreshing ? 'icon-spin' : ''} />
              {refreshing ? 'Atualizando...' : 'Atualizar'}
            </button>
          </div>
        </header>

        {error && <div className="alert alert--error">{error}</div>}

        <section className="card-stack" style={{ marginBottom: '2.5rem' }}>
          {stats.map((stat) => (
            <article key={stat.label} className="surface stat-card surface--interactive">
              <div className="card-header">
                <span className="badge" style={{ background: 'rgba(37,99,235,0.12)' }}>
                  {stat.icon}
                </span>
              </div>
              <div>
                <span className="stat-card__label">{stat.label}</span>
                <div className="stat-card__value">{stat.value}</div>
              </div>
              <span className="support-text">{stat.description}</span>
            </article>
          ))}
        </section>

        <section className="surface" style={{ padding: '2rem 2rem 2.4rem', marginBottom: '2.5rem' }}>
          <div className="card-header" style={{ marginBottom: '1.5rem' }}>
            <div>
              <h2 className="card-header__title">Mapa de disponibilidade</h2>
              <p className="support-text" style={{ marginTop: '0.35rem' }}>
                Explore e reserve os recursos disponíveis em tempo real.
              </p>
            </div>
            <span className="chip">
              {totalResources} recursos monitorados · {availableResources} livres
            </span>
          </div>

          {resources.length === 0 ? (
            <div className="empty-state">
              <div className="empty-state__icon">
                <Settings size={28} />
              </div>
              <h3 className="card-header__title" style={{ fontSize: '1.05rem' }}>
                Nenhum recurso cadastrado ainda
              </h3>
              <p className="support-text">
                Administre recursos pelo painel administrativo para começar a monitorá-los aqui.
              </p>
            </div>
          ) : (
            <div className="resource-grid">
              {resources.map((resource) => {
                const userReserve = activeReserves.find(
                  (reserve) => reserve.resourceId === resource.id
                );

                return (
                  <ResourceCard
                    key={resource.id}
                    resource={resource}
                    userReserve={userReserve}
                    onReserve={handleReserve}
                    onRelease={handleRelease}
                  />
                );
              })}
            </div>
          )}
        </section>
      </main>
    </div>
  );
};

export default Dashboard;