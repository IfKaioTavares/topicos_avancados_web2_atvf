#!/bin/bash

# Script para parar todos os simuladores IoT
echo "🛑 Parando todos os simuladores IoT..."

if [ -f .pids ]; then
    while IFS= read -r pid; do
        if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
            echo "   Parando processo $pid..."
            kill "$pid"
        fi
    done < .pids
    
    # Aguardar um pouco e forçar se necessário
    sleep 2
    
    while IFS= read -r pid; do
        if [ -n "$pid" ] && kill -0 "$pid" 2>/dev/null; then
            echo "   Forçando parada do processo $pid..."
            kill -9 "$pid" 2>/dev/null || true
        fi
    done < .pids
    
    rm -f .pids
    echo "✅ Todos os simuladores foram parados"
else
    echo "ℹ️  Nenhum arquivo de PIDs encontrado"
    
    # Tentar parar processos Node.js relacionados ao simulador
    pkill -f "node.*app.js" 2>/dev/null || true
    echo "ℹ️  Tentativa de limpeza de processos Node.js realizada"
fi