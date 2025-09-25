#!/bin/bash

# Script para executar múltiplos simuladores de dispositivos IoT
# Uso: ./run-multiple-devices.sh

echo "🚀 Iniciando múltiplos simuladores IoT..."

# Verificar se o backend está rodando
echo "🔍 Verificando se o backend está disponível..."
if ! curl -f -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "⚠️  Backend não está disponível em http://localhost:8080"
    echo "   Certifique-se de que o backend Spring Boot está rodando"
fi

# Função para iniciar um simulador
start_simulator() {
    local resource_id=$1
    local port=$2
    local timeout_minutes=$3
    
    echo "📱 Iniciando simulador: $resource_id na porta $port (timeout: ${timeout_minutes}min)"
    
    RESOURCE_ID=$resource_id \
    PORT=$port \
    TIMEOUT_DURATION_MINUTES=$timeout_minutes \
    BACKEND_URL=http://localhost:8080 \
    STATUS_INTERVAL_SECONDS=30 \
    npm start &
    
    local pid=$!
    echo "   PID: $pid"
    echo $pid >> .pids
}

# Limpar arquivo de PIDs anterior
rm -f .pids

echo ""
echo "📋 Configuração dos simuladores:"

# Laboratórios de Informática
start_simulator "LAB001" 3001 30
start_simulator "LAB002" 3002 30
start_simulator "LAB003" 3003 45

# Salas de Estudo
start_simulator "SALA001" 3004 60
start_simulator "SALA002" 3005 60

# Estações de Trabalho
start_simulator "EST001" 3006 120
start_simulator "EST002" 3007 120

echo ""
echo "✅ Simuladores iniciados!"
echo ""
echo "📊 URLs de status dos dispositivos:"
echo "   LAB001:  http://localhost:3001/status"
echo "   LAB002:  http://localhost:3002/status" 
echo "   LAB003:  http://localhost:3003/status"
echo "   SALA001: http://localhost:3004/status"
echo "   SALA002: http://localhost:3005/status"
echo "   EST001:  http://localhost:3006/status"
echo "   EST002:  http://localhost:3007/status"
echo ""
echo "🛑 Para parar todos os simuladores: ./stop-all-devices.sh"

# Aguardar interrupção
wait