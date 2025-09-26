import React, { useState, useEffect, useCallback } from 'react';
import { reserveService } from '../services';
import Navbar from '../components/Navbar';
import {
  History,
  Calendar,
  Clock,
  CheckCircle,
  XCircle,
  ChevronLeft,
  ChevronRight,
} from 'lucide-react';

const HistoryPage = () => {
  const [reserves, setReserves] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const loadReserveHistory = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const response = await reserveService.getMyReserveHistory(currentPage, 10);
      setReserves(response.content || []);
      setTotalPages(response.totalPages || 0);
      setTotalElements(response.totalElements || 0);
    } catch (error) {
      console.error('Erro ao carregar histórico:', error);
      setError('Erro ao carregar histórico de reservas');
    } finally {
      setLoading(false);
    }
  }, [currentPage]);

  useEffect(() => {
    loadReserveHistory();
  }, [loadReserveHistory]);

  

  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('pt-BR');
  };

  const statusConfig = {
    ACTIVE: { label: 'Ativa', className: 'chip chip--success' },
    EXPIRED: { label: 'Expirada', className: 'chip chip--warning' },
    RELEASED: { label: 'Liberada', className: 'chip' },
    CANCELLED: { label: 'Cancelada', className: 'chip chip--danger' },
  };

  const getStatusChip = (status) => {
    const config = statusConfig[status] || { label: status, className: 'chip' };
    return <span className={config.className}>{config.label}</span>;
  };

  const calculateDuration = (startDate, endDate) => {
    if (!startDate || !endDate) return 'Em andamento';
    
    const start = new Date(startDate);
    const end = new Date(endDate);
    const diffInMinutes = Math.floor((end - start) / (1000 * 60));
    
    if (diffInMinutes < 60) {
      return `${diffInMinutes} min`;
    }
    
    const hours = Math.floor(diffInMinutes / 60);
    const minutes = diffInMinutes % 60;
    return `${hours}h ${minutes}min`;
  };

  if (loading) {
    return (
      <div className="app-shell">
        <Navbar />
        <main className="page-shell">
          <div className="loading-state">Carregando histórico...</div>
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
                  background: 'linear-gradient(135deg, #4338ca 0%, #2563eb 100%)',
                  color: 'white',
                  boxShadow: 'var(--shadow-sm)',
                }}
              >
                <History size={22} />
              </div>
              <h1 className="page-header__title">Histórico de reservas</h1>
            </div>
            <p className="page-header__subtitle">
              Acompanhe todas as reservas realizadas e monitore o status de cada utilização.
            </p>
          </div>
        </header>

        {error && <div className="alert alert--error">{error}</div>}

        <section className="surface" style={{ padding: '2rem', marginBottom: '2.5rem' }}>
          <div className="card-header">
            <div className="stack-xs">
              <span className="heading-sm">Resumo rápido</span>
              <h2 className="card-header__title">Monitoramento das suas reservas</h2>
            </div>
            <span className="chip chip--success">{totalElements} registros</span>
          </div>

          <div className="grid grid--cols-2" style={{ marginTop: '1.5rem' }}>
            <article className="surface surface--interactive stat-card">
              <span className="stat-card__label">Reservas totais</span>
              <div className="stat-card__value">{totalElements}</div>
              <span className="support-text">
                Histórico completo das reservas realizadas no sistema.
              </span>
            </article>
            <article className="surface surface--interactive stat-card">
              <span className="stat-card__label">Ativas nesta página</span>
              <div className="stat-card__value">
                {reserves.filter((reserve) => reserve.status === 'ACTIVE').length}
              </div>
              <span className="support-text">
                Reservas ainda em andamento entre os resultados exibidos.
              </span>
            </article>
          </div>
        </section>

        <section className="stack" style={{ gap: '1.25rem' }}>
          {reserves.length === 0 ? (
            <div className="surface" style={{ padding: '3rem 1.5rem', textAlign: 'center' }}>
              <div className="empty-state__icon">
                <History size={26} />
              </div>
              <h3 className="card-header__title" style={{ fontSize: '1.1rem' }}>
                Nenhuma reserva encontrada
              </h3>
              <p className="support-text" style={{ marginTop: '0.5rem' }}>
                Assim que você realizar reservas, elas aparecerão neste painel histórico.
              </p>
            </div>
          ) : (
            reserves.map((reserve) => {
              const isActive = reserve.status === 'ACTIVE';
              const iconColor = isActive ? '#16a34a' : '#94a3b8';

              return (
                <article
                  key={reserve.id}
                  className="surface surface--interactive"
                  style={{ padding: '1.6rem', display: 'grid', gap: '1rem' }}
                >
                  <div className="flex-between" style={{ gap: '1rem', flexWrap: 'wrap' }}>
                    <div className="flex" style={{ alignItems: 'center', gap: '0.9rem' }}>
                      <div
                        style={{
                          width: '44px',
                          height: '44px',
                          borderRadius: '50%',
                          display: 'grid',
                          placeItems: 'center',
                          background: isActive ? 'rgba(34,197,94,0.15)' : 'rgba(148,163,184,0.18)',
                          color: iconColor,
                        }}
                      >
                        {isActive ? <CheckCircle size={20} /> : <XCircle size={20} />}
                      </div>
                      <div className="stack-xs">
                        <div className="flex" style={{ alignItems: 'center', gap: '0.6rem', flexWrap: 'wrap' }}>
                          <h3 className="card-header__title" style={{ fontSize: '1.05rem' }}>
                            {reserve.resourceName || `Recurso ${reserve.resourceId}`}
                          </h3>
                          {getStatusChip(reserve.status)}
                        </div>
                        <span className="support-text">Reserva #{reserve.id}</span>
                      </div>
                    </div>
                    <span className="support-text" style={{ fontWeight: 600 }}>
                      Criada em {formatDate(reserve.createdAt)}
                    </span>
                  </div>

                  <div className="grid grid--cols-2" style={{ gap: '0.75rem' }}>
                    {reserve.endedAt && (
                      <div className="flex" style={{ alignItems: 'center', gap: '0.45rem' }}>
                        <Clock size={16} color="#2563eb" />
                        <span className="support-text">
                          Duração: {calculateDuration(reserve.createdAt, reserve.endedAt)}
                        </span>
                      </div>
                    )}
                    {reserve.expiresAt && reserve.status === 'ACTIVE' && (
                      <div className="flex" style={{ alignItems: 'center', gap: '0.45rem' }}>
                        <Clock size={16} color="#b45309" />
                        <span className="support-text">Expira: {formatDate(reserve.expiresAt)}</span>
                      </div>
                    )}
                    <div className="flex" style={{ alignItems: 'center', gap: '0.45rem' }}>
                      <Calendar size={16} color="#4338ca" />
                      <span className="support-text">Identificador do recurso: {reserve.resourceId}</span>
                    </div>
                    <div className="flex" style={{ alignItems: 'center', gap: '0.45rem' }}>
                      <History size={16} color="#64748b" />
                      <span className="support-text">Status atual: {statusConfig[reserve.status]?.label || reserve.status}</span>
                    </div>
                  </div>
                </article>
              );
            })
          )}
        </section>

        {totalPages > 1 && (
          <div
            className="surface"
            style={{
              marginTop: '2.5rem',
              padding: '1.25rem 1.5rem',
              display: 'flex',
              flexWrap: 'wrap',
              gap: '1rem',
              alignItems: 'center',
              justifyContent: 'space-between',
            }}
          >
            <span className="support-text">
              Mostrando {currentPage * 10 + 1} –{' '}
              {Math.min((currentPage + 1) * 10, totalElements)} de {totalElements} registros
            </span>

            <div className="pill-group">
              <button
                type="button"
                onClick={() => setCurrentPage(Math.max(0, currentPage - 1))}
                disabled={currentPage === 0}
                className="btn btn--ghost"
                style={currentPage === 0 ? { opacity: 0.5, cursor: 'not-allowed' } : undefined}
              >
                <ChevronLeft size={16} /> Anterior
              </button>

              {Array.from({ length: Math.min(5, totalPages) }, (_, index) => {
                const start = Math.max(0, Math.min(totalPages - 5, currentPage - 2));
                const pageNumber = start + index;
                if (pageNumber >= totalPages) return null;

                const isActive = pageNumber === currentPage;
                return (
                  <button
                    key={pageNumber}
                    type="button"
                    onClick={() => setCurrentPage(pageNumber)}
                    className="btn btn--ghost"
                    style={
                      isActive
                        ? {
                            background: 'rgba(37,99,235,0.12)',
                            color: '#1d4ed8',
                            borderColor: 'rgba(37,99,235,0.25)',
                          }
                        : undefined
                    }
                  >
                    {pageNumber + 1}
                  </button>
                );
              })}

              <button
                type="button"
                onClick={() => setCurrentPage(Math.min(totalPages - 1, currentPage + 1))}
                disabled={currentPage >= totalPages - 1}
                className="btn btn--ghost"
                style={
                  currentPage >= totalPages - 1
                    ? { opacity: 0.5, cursor: 'not-allowed' }
                    : undefined
                }
              >
                Próximo <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default HistoryPage;