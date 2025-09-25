#!/bin/bash

# Demonstração das novas funcionalidades do simulador IoT

echo "🎬 DEMONSTRAÇÃO - NOVAS FUNCIONALIDADES"
echo "========================================"
echo ""

echo "🆕 FUNCIONALIDADES ADICIONADAS:"
echo "1. 🎲 Liberação Automática Aleatória"
echo "2. 🔌 Auto-ativação no Backend"
echo ""

# Verificar se as dependências estão instaladas
if [ ! -d "node_modules" ]; then
    echo "📦 Instalando dependências..."
    npm install
    echo ""
fi

echo "📋 Configuração atual (.env):"
echo "=============================="
cat .env | grep -v "^#" | grep -v "^$"
echo ""

echo "💡 EXPLICAÇÃO DAS NOVAS CONFIGURAÇÕES:"
echo ""
echo "🎲 AUTO_RELEASE_ENABLED=true"
echo "   → Habilita liberação automática aleatória"
echo ""
echo "🎲 AUTO_RELEASE_PROBABILITY=0.05"
echo "   → 5% de chance a cada verificação (2 em 2 minutos)"
echo ""
echo "🎲 AUTO_RELEASE_MIN_MINUTES=5"
echo "   → Só libera automaticamente após 5 minutos de reserva"
echo ""

echo "🔄 FLUXO DE OPERAÇÃO ATUALIZADO:"
echo "1. Primeira conexão → Backend ativa o dispositivo automaticamente"
echo "2. Dispositivo envia status FREE periodicamente"
echo "3. Quando reservado → Timer de timeout + liberação aleatória"
echo "4. A cada 2 minutos → Verifica se deve liberar (5% chance)"
echo "5. Se passou 5 minutos → Pode liberar automaticamente"
echo ""

read -p "Pressione ENTER para iniciar o simulador com as novas funcionalidades..."

echo ""
echo "🚀 Iniciando simulador..."
echo "  - Observe os logs de liberação automática (🎲)"
echo "  - Primeira conexão será marcada com (🔌)"
echo "  - Use Ctrl+C para parar"
echo ""

# Aguardar um momento
sleep 2

# Iniciar o simulador
npm start