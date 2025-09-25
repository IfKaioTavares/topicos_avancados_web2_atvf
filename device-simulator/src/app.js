require('dotenv').config();
const express = require('express');
const DeviceSimulator = require('./DeviceSimulator');

const app = express();
app.use(express.json());

// Validar variáveis de ambiente obrigatórias
const requiredEnvVars = ['RESOURCE_ID'];
for (const envVar of requiredEnvVars) {
    if (!process.env[envVar]) {
        console.error(`❌ Variável de ambiente obrigatória não definida: ${envVar}`);
        process.exit(1);
    }
}

// Configurações do dispositivo
const config = {
    resourceId: process.env.RESOURCE_ID,
    timeoutDurationMinutes: parseInt(process.env.TIMEOUT_DURATION_MINUTES) || 30,
    backendUrl: process.env.BACKEND_URL || 'http://localhost:8080',
    statusIntervalSeconds: parseInt(process.env.STATUS_INTERVAL_SECONDS) || 30,
    port: parseInt(process.env.PORT) || 3000,
    
    // Configurações de liberação automática
    autoReleaseEnabled: process.env.AUTO_RELEASE_ENABLED === 'true',
    autoReleaseProbability: parseFloat(process.env.AUTO_RELEASE_PROBABILITY) || 0.05,
    autoReleaseMinMinutes: parseInt(process.env.AUTO_RELEASE_MIN_MINUTES) || 5
};

console.log('🚀 Iniciando Simulador IoT...');
console.log(`📋 Configurações:
  - Resource ID: ${config.resourceId}
  - Timeout Duration: ${config.timeoutDurationMinutes} minutos
  - Backend URL: ${config.backendUrl}
  - Status Interval: ${config.statusIntervalSeconds} segundos
  - Port: ${config.port}
  - Auto Release: ${config.autoReleaseEnabled ? `${(config.autoReleaseProbability * 100).toFixed(1)}% (min: ${config.autoReleaseMinMinutes}min)` : 'Desabilitado'}
`);

// Criar instância do simulador
const deviceSimulator = new DeviceSimulator(config);

// Endpoint para status do dispositivo (para monitoramento)
app.get('/status', (req, res) => {
    res.json({
        resourceId: config.resourceId,
        currentStatus: deviceSimulator.getCurrentStatus(),
        uptime: process.uptime(),
        timestamp: new Date().toISOString()
    });
});

// Endpoint para forçar mudança de status (para testes)
app.post('/force-status', (req, res) => {
    const { status } = req.body;
    
    if (!['FREE', 'RESERVED', 'INACTIVE'].includes(status)) {
        return res.status(400).json({ 
            error: 'Status inválido. Use: FREE, RESERVED, ou INACTIVE' 
        });
    }
    
    deviceSimulator.forceStatus(status);
    res.json({ 
        message: `Status forçado para ${status}`,
        timestamp: new Date().toISOString()
    });
});

// Iniciar servidor
app.listen(config.port, () => {
    console.log(`🌐 Servidor do dispositivo rodando na porta ${config.port}`);
    console.log(`📊 Status: http://localhost:${config.port}/status`);
    
    // Iniciar simulador
    deviceSimulator.start();
});

// Graceful shutdown
process.on('SIGINT', () => {
    console.log('\n🛑 Parando simulador...');
    deviceSimulator.stop();
    process.exit(0);
});

process.on('SIGTERM', () => {
    console.log('\n🛑 Parando simulador...');
    deviceSimulator.stop();
    process.exit(0);
});