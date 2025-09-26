import React, { useState, useEffect } from 'react';
import { reportsService } from '../services';
import Navbar from '../components/Navbar';
import {
  BarChart3,
  TrendingUp,
  Users,
  Settings,
  Clock,
  Activity,
  RefreshCw,
} from 'lucide-react';

const StatsPage = () => {
  const [systemStats, setSystemStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      setError('');
      const stats = await reportsService.getSystemStats();
      setSystemStats(stats);
    } catch (error) {
      console.error('Erro ao carregar estatísticas:', error);
      setError('Erro ao carregar estatísticas do sistema');
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  const handleRefresh = () => {
    setRefreshing(true);
    loadStats();
  };

  if (loading) {
    return (
      <div className="app-shell">
        <Navbar />
        <main className="page-shell">
          <div className="loading-state">Carregando estatísticas...</div>
        </main>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <Navbar />
      <main className="page-shell">
        <header className="page-header">
          <div className="stack-xs">
            <div className="flex" style={{ alignItems: 'center', gap: '0.6rem' }}>
              <div
                style={{
                  width: '44px',
                  height: '44px',
                  borderRadius: '14px',
                  display: 'grid',
                  placeItems: 'center',
                  background: 'linear-gradient(135deg, #1d4ed8 0%, #312e81 100%)',
                  color: 'white',
                  boxShadow: 'var(--shadow-sm)',
                }}
              >
                <BarChart3 size={22} />
              </div>
              <h1 className="page-header__title">Estatísticas e relatórios</h1>
            </div>
            <p className="page-header__subtitle">
              Visualize indicadores operacionais do ecossistema IoT e acompanhe a saúde do sistema.
            </p>
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

        {systemStats ? (
          <>
            <section className="card-stack" style={{ marginBottom: '2.5rem' }}>
              <article className="surface surface--interactive stat-card">
                <span className="stat-card__label">Total de recursos</span>
                <div className="stat-card__value">{systemStats.totalResources || 0}</div>
                <span className="support-text">Dispositivos cadastrados e monitorados pelo sistema.</span>
              </article>
              <article className="surface surface--interactive stat-card">
                <span className="stat-card__label">Recursos ativos</span>
                <div className="stat-card__value" style={{ color: '#16a34a' }}>
                  {systemStats.activeResources || 0}
                </div>
                <span className="support-text">Itens disponíveis para reserva imediata.</span>
              </article>
              <article className="surface surface--interactive stat-card">
                <span className="stat-card__label">Reservas ativas</span>
                <div className="stat-card__value" style={{ color: '#b45309' }}>
                  {systemStats.activeReserves || 0}
                </div>
                <span className="support-text">Reservas em andamento registradas hoje.</span>
              </article>
              <article className="surface surface--interactive stat-card">
                <span className="stat-card__label">Usuários totais</span>
                <div className="stat-card__value" style={{ color: '#7c3aed' }}>
                  {systemStats.totalUsers || 0}
                </div>
                <span className="support-text">Colaboradores com acesso ao sistema IoT.</span>
              </article>
            </section>

            <section className="grid grid--content-2" style={{ gap: '1.5rem', marginBottom: '2.5rem' }}>
              <article className="surface" style={{ padding: '2rem' }}>
                <div className="card-header" style={{ marginBottom: '1.5rem' }}>
                  <div className="flex" style={{ alignItems: 'center', gap: '0.75rem' }}>
                    <div
                      style={{
                        width: '42px',
                        height: '42px',
                        borderRadius: '14px',
                        display: 'grid',
                        placeItems: 'center',
                        background: 'rgba(37,99,235,0.15)',
                        color: '#1d4ed8',
                      }}
                    >
                      <TrendingUp size={20} />
                    </div>
                    <h2 className="card-header__title">Taxa de utilização</h2>
                  </div>
                </div>
                <div className="stack" style={{ gap: '1.25rem' }}>
                  <div className="stack-xs">
                    <div className="flex-between">
                      <span className="support-text">Recursos em uso</span>
                      <span style={{ fontWeight: 600 }}>
                        {systemStats.utilizationRate ? `${systemStats.utilizationRate.toFixed(1)}%` : '0%'}
                      </span>
                    </div>
                    <div
                      style={{
                        width: '100%',
                        height: '12px',
                        borderRadius: '999px',
                        background: 'rgba(37,99,235,0.12)',
                        overflow: 'hidden',
                      }}
                    >
                      <div
                        style={{
                          width: `${Math.min(100, systemStats.utilizationRate || 0)}%`,
                          height: '100%',
                          borderRadius: 'inherit',
                          background: 'linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)',
                          transition: 'width 0.4s ease',
                        }}
                      />
                    </div>
                  </div>

                  <div className="grid grid--cols-2" style={{ gap: '1rem' }}>
                    <div className="surface surface--muted" style={{ padding: '1.2rem', borderRadius: 'var(--radius-lg)' }}>
                      <span className="heading-sm">Disponíveis</span>
                      <div className="stat-card__value" style={{ fontSize: '1.6rem', color: '#16a34a' }}>
                        {(systemStats.totalResources || 0) - (systemStats.occupiedResources || 0)}
                      </div>
                      <span className="support-text">Recursos prontos para reserva</span>
                    </div>
                    <div className="surface surface--muted" style={{ padding: '1.2rem', borderRadius: 'var(--radius-lg)' }}>
                      <span className="heading-sm">Ocupados</span>
                      <div className="stat-card__value" style={{ fontSize: '1.6rem', color: '#dc2626' }}>
                        {systemStats.occupiedResources || 0}
                      </div>
                      <span className="support-text">Itens atualmente em uso</span>
                    </div>
                  </div>
                </div>
              </article>

              <article className="surface" style={{ padding: '2rem' }}>
                <div className="card-header" style={{ marginBottom: '1.5rem' }}>
                  <div className="flex" style={{ alignItems: 'center', gap: '0.75rem' }}>
                    <div
                      style={{
                        width: '42px',
                        height: '42px',
                        borderRadius: '14px',
                        display: 'grid',
                        placeItems: 'center',
                        background: 'rgba(34,197,94,0.16)',
                        color: '#15803d',
                      }}
                    >
                      <Activity size={20} />
                    </div>
                    <h2 className="card-header__title">Atividade recente</h2>
                  </div>
                </div>

                <div className="stack" style={{ gap: '1rem' }}>
                  <div className="surface surface--muted" style={{ padding: '1.1rem 1.25rem', borderRadius: 'var(--radius-lg)' }}>
                    <div className="flex-between" style={{ alignItems: 'flex-start' }}>
                      <div className="stack-xs">
                        <span className="heading-sm">Reservas hoje</span>
                        <span className="support-text">Total de reservas criadas nas últimas 24 horas</span>
                      </div>
                      <span style={{ fontSize: '2rem', fontWeight: 700, color: '#2563eb' }}>
                        {systemStats.todayReserves || 0}
                      </span>
                    </div>
                  </div>

                  <div className="surface surface--muted" style={{ padding: '1.1rem 1.25rem', borderRadius: 'var(--radius-lg)' }}>
                    <div className="flex-between" style={{ alignItems: 'flex-start' }}>
                      <div className="stack-xs">
                        <span className="heading-sm">Tempo médio de uso</span>
                        <span className="support-text">Duração média das reservas finalizadas</span>
                      </div>
                      <span style={{ fontSize: '2rem', fontWeight: 700, color: '#16a34a' }}>
                        {systemStats.averageUsageTime || '0h'}
                      </span>
                    </div>
                  </div>

                  <div className="surface surface--muted" style={{ padding: '1.1rem 1.25rem', borderRadius: 'var(--radius-lg)' }}>
                    <div className="flex-between" style={{ alignItems: 'flex-start' }}>
                      <div className="stack-xs">
                        <span className="heading-sm">Reservas acumuladas</span>
                        <span className="support-text">Histórico completo de reservas realizadas</span>
                      </div>
                      <span style={{ fontSize: '2rem', fontWeight: 700, color: '#7c3aed' }}>
                        {systemStats.totalReserves || 0}
                      </span>
                    </div>
                  </div>
                </div>
              </article>
            </section>
          </>
        ) : (
          <div className="surface" style={{ padding: '3rem 1.5rem', textAlign: 'center' }}>
            <div className="empty-state__icon">
              <BarChart3 size={26} />
            </div>
            <h3 className="card-header__title" style={{ fontSize: '1.1rem' }}>
              Dados não disponíveis
            </h3>
            <p className="support-text" style={{ marginTop: '0.5rem' }}>
              Não foi possível carregar as estatísticas do sistema neste momento.
            </p>
          </div>
        )}
      </main>
    </div>
  );
};

export default StatsPage;