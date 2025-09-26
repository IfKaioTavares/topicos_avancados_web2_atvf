import React from 'react';
import {
  Wifi,
  WifiOff,
  Clock,
  Calendar,
  AlertTriangle,
  CheckCircle,
  XCircle,
} from 'lucide-react';

const ResourceCard = ({ resource, userReserve, onReserve, onRelease }) => {
  const isAvailable = resource.status === 'FREE';
  const isOccupied = resource.status === 'RESERVED'; // Corrigido: backend usa RESERVED, não OCCUPIED
  const isUnavailable = resource.status === 'INACTIVE'; // Corrigido: backend usa INACTIVE, não UNAVAILABLE
  const hasUserReserve = Boolean(userReserve);

  const status = (() => {
    switch (resource.status) {
      case 'FREE':
        return {
          label: 'Livre',
          icon: <Wifi size={18} color="#16a34a" />,
          pillClass: 'status-pill status-pill--free',
        };
      case 'RESERVED':
        return {
          label: 'Ocupado',
          icon: <WifiOff size={18} color="#dc2626" />,
          pillClass: 'status-pill status-pill--occupied',
        };
      case 'INACTIVE':
        return {
          label: 'Indisponível',
          icon: <AlertTriangle size={18} color="#b45309" />,
          pillClass: 'status-pill status-pill--unavailable',
        };
      default:
        return {
          label: 'Indisponível',
          icon: <AlertTriangle size={18} color="#b45309" />,
          pillClass: 'status-pill status-pill--unavailable',
        };
    }
  })();

  const formatDate = (value) => {
    if (!value) return '';
    try {
      const date = new Date(value);
      if (Number.isNaN(date.getTime())) return 'Data inválida';
      return date.toLocaleString('pt-BR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch (error) {
      console.error('Erro ao formatar data:', error);
      return 'Data inválida';
    }
  };

  const handleReserveClick = () => {
    if (hasUserReserve) {
      onRelease(userReserve.id);
    } else {
      onReserve(resource.id);
    }
  };

  return (
    <div className="surface surface--interactive resource-card">
      <div className="resource-card__header">
        <div className="stack-xs" style={{ flex: 1 }}>
          <h3 className="resource-card__title">{resource.name}</h3>
        </div>
        <span className={status.pillClass}>
          {status.icon}
          {status.label}
        </span>
      </div>

      <div className="resource-card__details">
        <div className="resource-card__row" style={{ fontWeight: 600, color: '#334155' }}>
          <span>Tipo:</span>
          <span style={{ fontWeight: 500 }}>{resource.type || 'Recurso IoT'}</span>
        </div>
        {resource.lastUpdate && (
          <div className="resource-card__row">
            <Clock size={16} color="#64748b" />
            <span>Atualizado: {formatDate(resource.lastUpdate)}</span>
          </div>
        )}
      </div>

      {hasUserReserve && (
        <div className="reserve-banner">
          <span className="reserve-banner__title">
            <CheckCircle size={16} /> Reservado por você
          </span>
          <div className="reserve-banner__meta">
            <span className="flex" style={{ gap: '0.35rem', alignItems: 'center' }}>
              <Calendar size={14} /> Desde: {formatDate(userReserve.startTime)}
            </span>
            {userReserve.predictedEndTime && (
              <span className="flex" style={{ gap: '0.35rem', alignItems: 'center' }}>
                <Clock size={14} /> Expira: {formatDate(userReserve.predictedEndTime)}
              </span>
            )}
          </div>
        </div>
      )}

      <div className="resource-card__actions">
        {isAvailable && !hasUserReserve && (
          <button type="button" onClick={handleReserveClick} className="btn btn--primary">
            <CheckCircle size={16} /> Reservar
          </button>
        )}

        {hasUserReserve && (
          <button type="button" onClick={handleReserveClick} className="btn btn--danger">
            <XCircle size={16} /> Liberar
          </button>
        )}

        {isOccupied && !hasUserReserve && (
          <button
            type="button"
            className="btn btn--ghost"
            disabled
            style={{ cursor: 'not-allowed', opacity: 0.7 }}
          >
            <WifiOff size={16} /> Ocupado
          </button>
        )}

        {isUnavailable && (
          <button
            type="button"
            className="btn btn--ghost"
            disabled
            style={{ cursor: 'not-allowed', opacity: 0.7 }}
          >
            <AlertTriangle size={16} /> Indisponível
          </button>
        )}
      </div>
    </div>
  );
};

export default ResourceCard;