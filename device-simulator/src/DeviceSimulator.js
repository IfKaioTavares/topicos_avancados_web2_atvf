const axios = require('axios');

class DeviceSimulator {
    constructor(config) {
        this.resourceId = config.resourceId;
        this.timeoutDurationMinutes = config.timeoutDurationMinutes;
        this.backendUrl = config.backendUrl;
        this.statusIntervalSeconds = config.statusIntervalSeconds;
        
        // Configurações de liberação automática
        this.autoReleaseEnabled = config.autoReleaseEnabled;
        this.autoReleaseProbability = config.autoReleaseProbability;
        this.autoReleaseMinMinutes = config.autoReleaseMinMinutes;
        
        // Estado atual do dispositivo
        this.currentStatus = 'FREE';  // FREE, RESERVED, INACTIVE
        this.reservedAt = null;
        this.timeoutTimer = null;
        this.statusInterval = null;
        this.commandCheckInterval = null;
        this.autoReleaseInterval = null;
        this.isFirstStatusSent = false;
        
        console.log(`🔧 Dispositivo ${this.resourceId} inicializado com timeout de ${this.timeoutDurationMinutes} minutos`);
        
        if (this.autoReleaseEnabled) {
            console.log(`🎲 Liberação automática ativada: ${(this.autoReleaseProbability * 100).toFixed(1)}% de chance (mín: ${this.autoReleaseMinMinutes}min)`);
        }
    }
    
    start() {
        console.log(`▶️  Iniciando simulador para recurso ${this.resourceId}...`);
        
        // Iniciar envio periódico de status
        this.startStatusReporting();
        
        // Iniciar verificação periódica de comandos
        this.startCommandChecking();
        
        // Iniciar liberação automática se habilitada
        if (this.autoReleaseEnabled) {
            this.startAutoReleaseChecking();
        }
        
        console.log(`✅ Simulador ativo! Status inicial: ${this.currentStatus}`);
    }
    
    stop() {
        console.log(`⏹️  Parando simulador para recurso ${this.resourceId}...`);
        
        if (this.statusInterval) {
            clearInterval(this.statusInterval);
        }
        
        if (this.commandCheckInterval) {
            clearInterval(this.commandCheckInterval);
        }
        
        if (this.autoReleaseInterval) {
            clearInterval(this.autoReleaseInterval);
        }
        
        if (this.timeoutTimer) {
            clearTimeout(this.timeoutTimer);
        }
        
        console.log('✅ Simulador parado');
    }
    
    startStatusReporting() {
        // Enviar status imediatamente
        this.sendStatusUpdate();
        
        // Configurar envio periódico
        this.statusInterval = setInterval(() => {
            this.sendStatusUpdate();
        }, this.statusIntervalSeconds * 1000);
        
        console.log(`📡 Envio de status configurado a cada ${this.statusIntervalSeconds} segundos`);
    }
    
    startCommandChecking() {
        // Verificar comandos a cada 10 segundos
        this.commandCheckInterval = setInterval(() => {
            this.checkForCommands();
        }, 10000);
        
        console.log('🔍 Verificação de comandos ativada (a cada 10 segundos)');
    }
    
    startAutoReleaseChecking() {
        // Verificar liberação automática a cada 2 minutos
        this.autoReleaseInterval = setInterval(() => {
            this.checkAutoRelease();
        }, 120000); // 2 minutos
        
        console.log('🎲 Verificação de liberação automática ativada (a cada 2 minutos)');
    }
    
    async sendStatusUpdate() {
        try {
            const payload = {
                resourceId: this.resourceId,
                status: this.currentStatus,
                timestamp: new Date().toISOString(),
                firstConnection: !this.isFirstStatusSent // Flag para ativação no backend
            };
            
            const response = await axios.post(
                `${this.backendUrl}/api/v1/devices/status`,
                payload,
                {
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    timeout: 5000 // 5 segundos de timeout
                }
            );
            
            if (!this.isFirstStatusSent) {
                console.log(`🔌 Primeira conexão realizada - dispositivo ativado no backend`);
                this.isFirstStatusSent = true;
            }
            
            console.log(`📤 Status enviado: ${this.currentStatus} (${response.status})`);
            
        } catch (error) {
            if (error.code === 'ECONNREFUSED' || error.code === 'ENOTFOUND') {
                console.error(`❌ Não foi possível conectar ao backend: ${this.backendUrl}`);
            } else if (error.response) {
                console.error(`❌ Erro ao enviar status: ${error.response.status} - ${error.response.data}`);
            } else {
                console.error(`❌ Erro ao enviar status: ${error.message}`);
            }
        }
    }
    
    async checkForCommands() {
        try {
            // Verificar comando de reserva
            const reserveResponse = await axios.get(
                `${this.backendUrl}/api/v1/devices/${this.resourceId}/commands/reserve`,
                { timeout: 3000 }
            );
            
            if (reserveResponse.data && reserveResponse.data.command === 'RESERVE') {
                this.executeReserveCommand();
            }
            
        } catch (error) {
            // Se der 404, não há comando pendente, isso é normal
            if (error.response && error.response.status !== 404) {
                console.error(`❌ Erro ao verificar comando de reserva: ${error.response?.status}`);
            }
        }
        
        try {
            // Verificar comando de liberação
            const releaseResponse = await axios.get(
                `${this.backendUrl}/api/v1/devices/${this.resourceId}/commands/release`,
                { timeout: 3000 }
            );
            
            if (releaseResponse.data && releaseResponse.data.command === 'RELEASE') {
                this.executeReleaseCommand();
            }
            
        } catch (error) {
            // Se der 404, não há comando pendente, isso é normal
            if (error.response && error.response.status !== 404) {
                console.error(`❌ Erro ao verificar comando de liberação: ${error.response?.status}`);
            }
        }
    }
    
    executeReserveCommand() {
        if (this.currentStatus === 'FREE') {
            this.currentStatus = 'RESERVED';
            this.reservedAt = new Date();
            
            // Configurar timeout automático
            this.setAutoTimeout();
            
            console.log(`🔒 Recurso RESERVADO às ${this.reservedAt.toLocaleString()}`);
            console.log(`⏰ Timeout automático em ${this.timeoutDurationMinutes} minutos`);
            
            // Enviar atualização imediatamente
            this.sendStatusUpdate();
        } else {
            console.log(`⚠️  Comando de reserva ignorado - status atual: ${this.currentStatus}`);
        }
    }
    
    executeReleaseCommand() {
        if (this.currentStatus === 'RESERVED') {
            this.currentStatus = 'FREE';
            this.reservedAt = null;
            
            // Cancelar timeout se existir
            if (this.timeoutTimer) {
                clearTimeout(this.timeoutTimer);
                this.timeoutTimer = null;
            }
            
            console.log(`🔓 Recurso LIBERADO às ${new Date().toLocaleString()}`);
            
            // Enviar atualização imediatamente
            this.sendStatusUpdate();
        } else {
            console.log(`⚠️  Comando de liberação ignorado - status atual: ${this.currentStatus}`);
        }
    }
    
    checkAutoRelease() {
        // Só verifica liberação automática se estiver reservado e habilitado
        if (!this.autoReleaseEnabled || this.currentStatus !== 'RESERVED' || !this.reservedAt) {
            return;
        }
        
        // Verificar se já passou o tempo mínimo desde a reserva
        const minutesSinceReserved = (new Date() - this.reservedAt) / (1000 * 60);
        if (minutesSinceReserved < this.autoReleaseMinMinutes) {
            return;
        }
        
        // Gerar número aleatório para verificar se deve liberar
        const randomValue = Math.random();
        
        if (randomValue <= this.autoReleaseProbability) {
            console.log(`🎲 LIBERAÇÃO AUTOMÁTICA! (${(randomValue * 100).toFixed(1)}% ≤ ${(this.autoReleaseProbability * 100).toFixed(1)}%)`);
            console.log(`   Recurso estava reservado há ${minutesSinceReserved.toFixed(1)} minutos`);
            
            // Liberar o recurso
            this.currentStatus = 'FREE';
            this.reservedAt = null;
            
            // Cancelar timeout se existir
            if (this.timeoutTimer) {
                clearTimeout(this.timeoutTimer);
                this.timeoutTimer = null;
            }
            
            // Enviar atualização imediatamente
            this.sendStatusUpdate();
        } else {
            console.log(`🎲 Verificação de liberação: ${(randomValue * 100).toFixed(1)}% > ${(this.autoReleaseProbability * 100).toFixed(1)}% - mantendo reservado`);
        }
    }
    
    setAutoTimeout() {
        // Cancelar timeout anterior se existir
        if (this.timeoutTimer) {
            clearTimeout(this.timeoutTimer);
        }
        
        // Configurar novo timeout
        const timeoutMs = this.timeoutDurationMinutes * 60 * 1000;
        
        this.timeoutTimer = setTimeout(() => {
            console.log(`⏰ TIMEOUT AUTOMÁTICO! Liberando recurso após ${this.timeoutDurationMinutes} minutos`);
            this.currentStatus = 'FREE';
            this.reservedAt = null;
            this.timeoutTimer = null;
            
            // Enviar atualização imediatamente
            this.sendStatusUpdate();
        }, timeoutMs);
    }
    
    // Métodos auxiliares
    getCurrentStatus() {
        return {
            status: this.currentStatus,
            reservedAt: this.reservedAt,
            timeoutInMinutes: this.timeoutDurationMinutes
        };
    }
    
    forceStatus(newStatus) {
        const oldStatus = this.currentStatus;
        this.currentStatus = newStatus;
        
        if (newStatus === 'RESERVED') {
            this.reservedAt = new Date();
            this.setAutoTimeout();
        } else {
            this.reservedAt = null;
            if (this.timeoutTimer) {
                clearTimeout(this.timeoutTimer);
                this.timeoutTimer = null;
            }
        }
        
        console.log(`🔄 Status forçado: ${oldStatus} → ${newStatus}`);
        
        // Enviar atualização imediatamente
        this.sendStatusUpdate();
    }
}

module.exports = DeviceSimulator;