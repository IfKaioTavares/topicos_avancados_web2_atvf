const axios = require('axios');

class DeviceSimulator {
    constructor(config) {
        this.resourceId = config.resourceId;
        this.timeoutDurationMinutes = config.timeoutDurationMinutes;
        this.backendUrl = config.backendUrl;
        
        // Estado atual do dispositivo
        this.currentStatus = 'FREE';  // FREE, RESERVED
        this.reservedAt = null;
        this.predictedEndTime = null;
        this.statusInterval = null;
        this.statusCheckInterval = null;
        this.autoReleaseTimer = null;
        this.isFirstConnection = true;
        this.lastStatusSent = null; // Timestamp do último envio de status
        
        console.log(`🔧 Dispositivo ${this.resourceId} inicializado com timeout de ${this.timeoutDurationMinutes} minutos`);
    }
    
    start() {
        console.log(`▶️  Iniciando simulador para recurso ${this.resourceId}...`);
        
        // Enviar dados periodicamente para o backend (a cada 30 segundos)
        this.startStatusReporting();
        
        // Perguntar periodicamente se está livre ou não (a cada 15 segundos)
        this.startStatusChecking();
        
        console.log(`✅ Simulador ativo! Status inicial: ${this.currentStatus}`);
    }
    
    stop() {
        console.log(`⏹️  Parando simulador para recurso ${this.resourceId}...`);
        
        if (this.statusInterval) {
            clearInterval(this.statusInterval);
        }
        
        if (this.statusCheckInterval) {
            clearInterval(this.statusCheckInterval);
        }
        
        if (this.autoReleaseTimer) {
            clearTimeout(this.autoReleaseTimer);
        }
        
        console.log('✅ Simulador parado');
    }
    
    startStatusReporting() {
        // Enviar dados imediatamente
        this.sendDeviceData();
        
        // Configurar envio periódico a cada 30 segundos
        this.statusInterval = setInterval(() => {
            this.sendDeviceData();
        }, 30000);
        
        console.log(`📡 Envio de dados do dispositivo configurado a cada 30 segundos`);
    }
    
    startStatusChecking() {
        // Perguntar ao backend se está livre ou não a cada 15 segundos
        this.statusCheckInterval = setInterval(() => {
            this.askBackendStatus();
        }, 15000);
        
        console.log('❓ Verificação de status no backend ativada (a cada 15 segundos)');
    }
    
    async sendDeviceData() {
        try {
            const payload = {
                resourceId: this.resourceId,
                status: this.currentStatus,
                timestamp: new Date().toISOString(),
                firstConnection: this.isFirstConnection,
                reservedAt: this.reservedAt,
                predictedEndTime: this.predictedEndTime
            };
            
            const response = await axios.post(
                `${this.backendUrl}/api/v1/devices/status`,
                payload,
                {
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    timeout: 5000
                }
            );
            
            if (this.isFirstConnection) {
                console.log(`🔌 Primeira conexão realizada - dispositivo ativado no backend`);
                this.isFirstConnection = false;
            }
            
            // Registrar timestamp do envio
            this.lastStatusSent = Date.now();
            
            console.log(`📤 Dados enviados: ${this.currentStatus} (${response.status})`);
            
        } catch (error) {
            if (error.code === 'ECONNREFUSED' || error.code === 'ENOTFOUND') {
                console.error(`❌ Erro ao conectar ao backend: ${this.backendUrl}`);
            } else if (error.response) {
                console.error(`❌ Erro ao enviar dados: ${error.response.status} - ${error.response.data || ''}`);
            } else {
                console.error(`❌ Erro ao enviar dados: ${error.message}`);
            }
        }
    }
    
    async askBackendStatus() {
        try {
            const response = await axios.get(
                `${this.backendUrl}/api/v1/devices/${this.resourceId}/status`,
                { timeout: 3000 }
            );
            
            const backendStatus = response.data.status;
            const backendReserveDetails = response.data.reserveDetails;
            
            console.log(`📋 Status no backend: ${backendStatus} | Status local: ${this.currentStatus}`);
            
            // Só sincronizar se houver uma diferença significativa e não acabamos de enviar uma atualização
            if (backendStatus !== this.currentStatus) {
                // Evitar sync imediato após envio - aguardar um pouco
                if (this.lastStatusSent && (Date.now() - this.lastStatusSent < 2000)) {
                    console.log(`⏳ Aguardando estabilização após envio recente (${Date.now() - this.lastStatusSent}ms atrás)`);
                    return;
                }
                
                console.log(`🔄 Sincronizando status: ${this.currentStatus} → ${backendStatus}`);
                this.currentStatus = backendStatus;
                
                if (backendStatus === 'RESERVED' && backendReserveDetails) {
                    this.reservedAt = new Date(backendReserveDetails.startTime);
                    this.predictedEndTime = new Date(backendReserveDetails.predictedEndTime);
                    
                    // Configurar auto-liberação baseada no tempo previsto
                    this.scheduleAutoRelease();
                    
                    console.log(`🔒 Recurso reservado até: ${this.predictedEndTime.toLocaleString()}`);
                } else if (backendStatus === 'FREE') {
                    this.reservedAt = null;
                    this.predictedEndTime = null;
                    
                    // Cancelar auto-liberação se existir
                    if (this.autoReleaseTimer) {
                        clearTimeout(this.autoReleaseTimer);
                        this.autoReleaseTimer = null;
                    }
                    
                    console.log(`🔓 Recurso liberado pelo backend`);
                }
            } else {
                console.log(`✅ Status sincronizado: ${this.currentStatus}`);
            }
            
        } catch (error) {
            if (error.response && error.response.status === 404) {
                console.log(`⚠️  Recurso não encontrado no backend: ${this.resourceId}`);
            } else if (error.code === 'ECONNREFUSED' || error.code === 'ENOTFOUND') {
                console.error(`❌ Erro ao conectar ao backend: ${this.backendUrl}`);
            } else {
                console.error(`❌ Erro ao verificar status: ${error.response?.status || error.message}`);
            }
        }
    }
    
    scheduleAutoRelease() {
        // Cancelar timer anterior se existir
        if (this.autoReleaseTimer) {
            clearTimeout(this.autoReleaseTimer);
        }
        
        if (!this.predictedEndTime) return;
        
        const now = new Date();
        const timeUntilAutoRelease = this.predictedEndTime.getTime() - now.getTime();
        
        if (timeUntilAutoRelease > 0) {
            this.autoReleaseTimer = setTimeout(() => {
                console.log(`⏰ AUTO-LIBERAÇÃO! Tempo previsto expirado.`);
                this.performAutoRelease();
            }, timeUntilAutoRelease);
            
            console.log(`⏰ Auto-liberação agendada para: ${this.predictedEndTime.toLocaleString()}`);
        } else {
            // Tempo já passou, liberar imediatamente
            console.log(`⏰ AUTO-LIBERAÇÃO! Tempo previsto já expirou.`);
            this.performAutoRelease();
        }
    }
    
    async performAutoRelease() {
        console.log(`🔓 Executando auto-liberação do recurso ${this.resourceId}`);
        
        this.currentStatus = 'FREE';
        this.reservedAt = null;
        this.predictedEndTime = null;
        
        // Cancelar timer
        if (this.autoReleaseTimer) {
            clearTimeout(this.autoReleaseTimer);
            this.autoReleaseTimer = null;
        }
        
        // Notificar o backend sobre a auto-liberação
        try {
            await axios.post(
                `${this.backendUrl}/api/v1/devices/${this.resourceId}/auto-release`,
                {
                    timestamp: new Date().toISOString(),
                    reason: 'timeout_expired'
                },
                { timeout: 5000 }
            );
            
            console.log(`📤 Auto-liberação notificada ao backend`);
            
        } catch (error) {
            console.error(`❌ Erro ao notificar auto-liberação: ${error.message}`);
        }
        
        // Enviar dados atualizados imediatamente
        this.sendDeviceData();
    }


    
        
    // Métodos auxiliares
    getCurrentStatus() {
        return {
            resourceId: this.resourceId,
            status: this.currentStatus,
            reservedAt: this.reservedAt,
            predictedEndTime: this.predictedEndTime,
            isFirstConnection: this.isFirstConnection
        };
    }
    
    // Função para debug e monitoramento
    printConfig() {
        console.log('📋 CONFIGURAÇÕES DO DISPOSITIVO:');
        console.log(`   Resource ID: ${this.resourceId}`);
        console.log(`   Backend URL: ${this.backendUrl}`);
        console.log(`   Porto: ${this.port}`);
        console.log(`   Status Atual: ${this.currentStatus}`);
        console.log(`   Timeout: ${this.timeoutDurationMinutes} minutos`);
        console.log(`   Primeira Conexão: ${this.isFirstConnection ? 'sim' : 'não'}`);
        
        if (this.reservedAt) {
            console.log(`   Reservado desde: ${this.reservedAt.toLocaleString()}`);
        }
        
        if (this.predictedEndTime) {
            console.log(`   Liberação prevista: ${this.predictedEndTime.toLocaleString()}`);
        }
    }
}

module.exports = DeviceSimulator;