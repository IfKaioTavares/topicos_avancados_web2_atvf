#!/bin/bash

# Script de demonstração do simulador IoT

echo "🎬 DEMONSTRAÇÃO DO SIMULADOR IoT"
echo "=================================="
echo ""

# Verificar se as dependências estão instaladas
if [ ! -d "node_modules" ]; then
    echo "📦 Instalando dependências..."
    npm install
    echo ""
fi

# Verificar se o arquivo .env existe
if [ ! -f ".env" ]; then
    echo "⚙️  Configurando arquivo .env..."
    cp .env.example .env
    echo "✅ Arquivo .env criado com configurações padrão"
    echo ""
fi

echo "📋 Configurações atuais:"
echo "------------------------"
cat .env | grep -v "^#" | grep -v "^$"
echo ""

echo "🚀 Iniciando simulador..."
echo "  - O simulador começará a enviar status para o backend"
echo "  - Pressione Ctrl+C para parar"
echo ""

# Aguardar um momento
sleep 2

# Iniciar o simulador
npm start