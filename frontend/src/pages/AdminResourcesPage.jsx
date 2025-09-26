import React, { useEffect, useMemo, useState } from 'react';
import {
  AlertTriangle,
  Clock,
  Plus,
  Save,
  Settings,
  Shield,
  Trash2,
  Wifi,
  X,
} from 'lucide-react';
import Navbar from '../components/Navbar';
import { resourceService } from '../services';

const typeOptions = [
  { value: 'SALA', label: 'Sala' },
  { value: 'LABORATORIO', label: 'Laboratório' },
  { value: 'ESTACAO', label: 'Estação de trabalho' },
  { value: 'EQUIPAMENTO', label: 'Equipamento' },
  { value: 'VEICULO', label: 'Veículo' },
  { value: 'OUTRO', label: 'Outro' },
];

const statusOptions = [
  { value: 'INACTIVE', label: 'Inativo' },
  { value: 'FREE', label: 'Disponível' },
  { value: 'RESERVED', label: 'Reservado' },
];

const initialFormState = {
  resourceId: '',
  name: '',
  type: 'SALA',
  status: 'INACTIVE',
  timeoutUsageInMinutes: 60,
  lockedForAdmin: false,
};

const AdminResourcesPage = () => {
  const [resources, setResources] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showModal, setShowModal] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [formData, setFormData] = useState(() => ({ ...initialFormState }));

  const statusMeta = useMemo(
    () => ({
      INACTIVE: {
        label: 'Inativo',
        icon: AlertTriangle,
        tone: 'chip chip--warning',
      },
      FREE: {
        label: 'Disponível',
        icon: Wifi,
        tone: 'chip chip--success',
      },
      RESERVED: {
        label: 'Reservado',
        icon: Clock,
        tone: 'chip chip--accent',
      },
    }),
    [],
  );

  const loadResources = async () => {
    try {
      setLoading(true);
      const response = await resourceService.getResources();
      setResources(response || []);
    } catch (err) {
      setError('Não foi possível carregar os recursos. Tente novamente.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadResources();
  }, []);

  const resetForm = () => setFormData({ ...initialFormState });

  const toggleModal = () => {
    setShowModal((prev) => !prev);
    setError('');
    resetForm();
  };

  const handleChange = (field) => (event) => {
    const { value, checked, type } = event.target;
    setFormData((prev) => ({
      ...prev,
      [field]: type === 'checkbox' ? checked : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');

    const payload = {
      resourceId: formData.resourceId.trim(),
      name: formData.name.trim(),
      type: formData.type,
      status: formData.status,
      timeoutUsageInMinutes: Number(formData.timeoutUsageInMinutes) || 60,
      lockedForAdmin: Boolean(formData.lockedForAdmin),
    };

    if (!payload.resourceId || !payload.name) {
      setError('Identificador e nome são obrigatórios.');
      return;
    }

    try {
      setSubmitting(true);
      await resourceService.createResource(payload);
      await loadResources();
      resetForm();
    } catch (err) {
      const fallbackMessage = 'Erro ao criar o recurso. Verifique os dados e tente novamente.';
      const serverMessage = err?.response?.data?.message;
      setError(serverMessage || fallbackMessage);
      console.error(err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Tem certeza que deseja remover este recurso?')) return;

    try {
      await resourceService.deleteResource(id);
      await loadResources();
    } catch (err) {
      setError('Erro ao remover o recurso. Tente novamente.');
      console.error(err);
    }
  };

  const formatType = (type) => typeOptions.find((option) => option.value === type)?.label || type || '—';

  return (
    <div className="app-shell">
      <Navbar />
      <main className="page-shell">
        <header className="page-header">
          <div>
            <p className="overline">Gerenciamento • Recursos</p>
            <h1 className="page-title">Recursos compartilhados</h1>
            <p className="page-subtitle">
              Cadastre, visualize e controle os recursos disponíveis para reservas.
            </p>
          </div>
          <button type="button" className="btn btn--primary" onClick={toggleModal}>
            <Plus size={18} />
            <span>Novo recurso</span>
          </button>
        </header>

        <section className="surface" style={{ padding: '2rem' }}>
          <header className="card-header">
            <div className="card-header__title">
              <span className="icon-chip">
                <Settings size={18} />
              </span>
              <div>
                <h2 className="card-title">Recursos cadastrados</h2>
                <p className="card-subtitle">
                  {loading
                    ? 'Carregando recursos disponíveis...'
                    : `${resources.length} recurso${resources.length === 1 ? '' : 's'} encontrado${
                        resources.length === 1 ? '' : 's'
                      }.`}
                </p>
              </div>
            </div>
          </header>

          {error && (
            <div className="alert alert--error" role="alert">
              <AlertTriangle size={18} />
              <span>{error}</span>
            </div>
          )}

          {loading ? (
            <div className="empty-state">
              <div className="spinner" />
              <p>Buscando recursos...</p>
            </div>
          ) : resources.length === 0 ? (
            <div className="empty-state">
              <Shield size={36} />
              <p>Nenhum recurso cadastrado ainda.</p>
            </div>
          ) : (
            <div className="table-wrapper">
              <table className="table">
                <thead>
                  <tr>
                    <th>Identificador</th>
                    <th>Nome</th>
                    <th>Tipo</th>
                    <th>Status</th>
                    <th>Tempo de uso</th>
                    <th>Ações</th>
                  </tr>
                </thead>
                <tbody>
                  {resources.map((resource) => {
                    const meta =
                      statusMeta[resource.status] ||
                      {
                        label: resource.status || 'Desconhecido',
                        icon: AlertTriangle,
                        tone: 'chip chip--warning',
                      };
                    const StatusIcon = meta.icon;
                    return (
                      <tr key={resource.id}>
                        <td className="table-cell--mono">{resource.resourceId}</td>
                        <td>
                          <div className="heading-sm">{resource.name}</div>
                        </td>
                        <td>{formatType(resource.type)}</td>
                        <td>
                          <span className={meta.tone}>
                            <StatusIcon size={16} />
                            {meta.label}
                          </span>
                        </td>
                        <td>
                          {resource.timeoutUsageInMinutes
                            ? `${resource.timeoutUsageInMinutes} min`
                            : '—'}
                        </td>
                        <td>
                          <div className="table-actions">
                            <button
                              type="button"
                              className="icon-button icon-button--danger"
                              onClick={() => handleDelete(resource.id)}
                              aria-label="Remover recurso"
                            >
                              <Trash2 size={16} />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </main>

      {showModal && (
        <div className="modal-overlay" role="presentation">
          <div className="modal" role="dialog" aria-modal="true">
            <div className="modal__content">
              <header className="modal__header">
                <div>
                  <h2 className="modal__title">Cadastrar novo recurso</h2>
                  <p className="modal__subtitle">
                    Informe os dados obrigatórios para disponibilizar um novo recurso no sistema.
                  </p>
                </div>
                <button type="button" className="icon-button" onClick={toggleModal} aria-label="Fechar modal">
                  <X size={18} />
                </button>
              </header>

            <form className="resource-form" onSubmit={handleSubmit}>
              <div className="resource-form__grid">
                <label className="resource-form__field">
                  <span className="resource-form__label">Identificador *</span>
                  <input
                    required
                    type="text"
                    className="resource-form__control"
                    value={formData.resourceId}
                    onChange={handleChange('resourceId')}
                    placeholder="Ex.: SALA-101"
                  />
                  <span className="resource-form__hint">Identificador único usado nas reservas.</span>
                </label>

                <label className="resource-form__field">
                  <span className="resource-form__label">Nome *</span>
                  <input
                    required
                    type="text"
                    className="resource-form__control"
                    value={formData.name}
                    onChange={handleChange('name')}
                    placeholder="Nome descritivo"
                  />
                </label>

                <label className="resource-form__field">
                  <span className="resource-form__label">Tipo</span>
                  <select
                    className="resource-form__control"
                    value={formData.type}
                    onChange={handleChange('type')}
                  >
                    {typeOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="resource-form__field">
                  <span className="resource-form__label">Status inicial</span>
                  <select
                    className="resource-form__control"
                    value={formData.status}
                    onChange={handleChange('status')}
                  >
                    {statusOptions.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="resource-form__field">
                  <span className="resource-form__label">Tempo máximo de uso (min)</span>
                  <input
                    type="number"
                    min={1}
                    step={1}
                    className="resource-form__control"
                    value={formData.timeoutUsageInMinutes}
                    onChange={handleChange('timeoutUsageInMinutes')}
                  />
                  <span className="resource-form__hint">Defina o tempo limite para cada reserva.</span>
                </label>

                <label className="resource-form__checkbox resource-form__field--full">
                  <input
                    type="checkbox"
                    checked={formData.lockedForAdmin}
                    onChange={handleChange('lockedForAdmin')}
                  />
                  <div>
                    <span className="resource-form__label">Visível apenas para administradores</span>
                    <span className="resource-form__hint">
                      Ative para restringir o recurso a contas administrativas.
                    </span>
                  </div>
                </label>
              </div>

              <div className="resource-form__footer">
                <button type="submit" className="btn btn--primary" disabled={submitting}>
                  <Save size={18} />
                  <span>{submitting ? 'Salvando...' : 'Salvar recurso'}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      )}
    </div>
  );
};

export default AdminResourcesPage;