import API from './api';

export const authService = {
  // Login
  async login(credentials) {
    const response = await API.post('/auth/login', credentials);
    return response.data;
  },

  // Registrar usuário
  async register(userData) {
    const response = await API.post('/users', userData);
    return response.data;
  },

  // Obter dados do usuário atual
  async getCurrentUser() {
    const response = await API.get('/users/me');
    return response.data;
  }
};

export const resourceService = {
  // Listar todos os recursos
  async getResources() {
    const response = await API.get('/iot-resources');
    return response.data;
  },

  // Obter recurso por ID
  async getResourceById(id) {
    const response = await API.get(`/iot-resources/${id}`);
    return response.data;
  },

  // Criar recurso (admin)
  async createResource(resourceData) {
    const response = await API.post('/iot-resources', resourceData);
    return response.data;
  },

  // Deletar recurso (admin)
  async deleteResource(id) {
    const response = await API.delete(`/iot-resources/${id}`);
    return response.data;
  },

  // Atualizar status do recurso
  async updateResourceStatus(id, statusData) {
    const response = await API.put(`/iot-resources/${id}/status`, statusData);
    return response.data;
  }
};

export const reserveService = {
  // Criar reserva
  async createReserve(reserveData) {
    const response = await API.post('/reserves', reserveData);
    return response.data;
  },

  // Minhas reservas ativas
  async getMyActiveReserves() {
    const response = await API.get('/reserves/my-active');
    return response.data;
  },

  // Meu histórico de reservas
  async getMyReserveHistory(page = 0, size = 10) {
    const response = await API.get(`/reserves/my-history?page=${page}&size=${size}`);
    return response.data;
  },

  // Histórico geral (admin)
  async getAllReservesHistory(page = 0, size = 10) {
    const response = await API.get(`/reserves/history?page=${page}&size=${size}`);
    return response.data;
  },

  // Reservas de um recurso específico
  async getReservesByResource(resourceId) {
    const response = await API.get(`/reserves/resource/${resourceId}`);
    return response.data;
  },

  // Liberar reserva
  async releaseReserve(reserveId) {
    const response = await API.delete(`/reserves/${reserveId}`);
    return response.data;
  }
};

export const reportsService = {
  // Estatísticas do sistema
  async getSystemStats() {
    const response = await API.get('/reports/system-stats');
    return response.data;
  },

  // Estatísticas de uso dos recursos (admin)
  async getResourceUsageStats() {
    const response = await API.get('/reports/resource-usage');
    return response.data;
  }
};