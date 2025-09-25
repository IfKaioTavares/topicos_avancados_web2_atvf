# Simulador de Dispositivos IoT

Este simulador representa um dispositivo IoT que monitora e gerencia recursos compartilhados (ex: estações de trabalho, salas de estudo, etc.).

## Funcionalidades

### ✅ Funcionalidades Implementadas

1. **Envio Periódico de Status**
   - Envia `{resourceId, status, timestamp, firstConnection}` para o backend a cada X segundos
   - Status possíveis: `FREE`, `RESERVED`, `INACTIVE`
   - Flag `firstConnection` ativa o dispositivo no backend na primeira requisição

2. **Recebimento de Comandos**
   - Verifica periodicamente comandos de `RESERVE` e `RELEASE`
   - Executa comandos automaticamente quando disponíveis

3. **Timeout Automático**
   - Se o recurso não for liberado manualmente, volta para `FREE` após X minutos
   - Duração configurável via variável de ambiente

4. **Liberação Automática Aleatória** ⭐ **NOVO**
   - Dispositivo pode liberar recurso espontaneamente quando reservado
   - Probabilidade configurável (ex: 5% de chance a cada verificação)
   - Tempo mínimo antes de poder liberar automaticamente

5. **Auto-ativação no Backend** ⭐ **NOVO**
   - Na primeira conexão, o dispositivo envia flag especial
   - Backend recebe sinal para ativar o dispositivo no banco de dados

6. **Variáveis de Ambiente**
   - `RESOURCE_ID`: ID único do recurso (obrigatório)
   - `TIMEOUT_DURATION_MINUTES`: Duração do timeout (padrão: 30 minutos)
   - `AUTO_RELEASE_ENABLED`: Habilitar liberação automática (padrão: true)
   - `AUTO_RELEASE_PROBABILITY`: Probabilidade de liberação (padrão: 0.05 = 5%)
   - `AUTO_RELEASE_MIN_MINUTES`: Tempo mínimo antes de liberar (padrão: 5 min)

## Configuração

### Variáveis de Ambiente

Copie `.env.example` para `.env` e configure:

```bash
cp .env.example .env
```

Variáveis disponíveis:
- `RESOURCE_ID`: ID único do recurso (obrigatório)
- `TIMEOUT_DURATION_MINUTES`: Duração do timeout em minutos (padrão: 30)
- `BACKEND_URL`: URL do backend (padrão: http://localhost:8080)
- `STATUS_INTERVAL_SECONDS`: Intervalo de envio de status (padrão: 30)
- `PORT`: Porta do servidor local (padrão: 3000)
- `AUTO_RELEASE_ENABLED`: Habilitar liberação automática (padrão: true)
- `AUTO_RELEASE_PROBABILITY`: Probabilidade de liberação automática (padrão: 0.05)
- `AUTO_RELEASE_MIN_MINUTES`: Tempo mínimo para liberação automática (padrão: 5)

## Instalação e Execução

### 1. Instalar dependências

```bash
npm install
```

### 2. Configurar variáveis de ambiente

```bash
# Copiar arquivo de exemplo
cp .env.example .env

# Editar configurações necessárias
nano .env
```

### 3. Executar o simulador

```bash
# Execução normal
npm start

# Execução com auto-reload (desenvolvimento)
npm run dev
```

## APIs do Simulador

### GET /status
Retorna o status atual do dispositivo:

```json
{
  "resourceId": "LAB001",
  "currentStatus": {
    "status": "FREE",
    "reservedAt": null,
    "timeoutInMinutes": 30
  },
  "uptime": 120.5,
  "timestamp": "2025-09-25T10:30:00.000Z"
}
```

### POST /force-status
Força uma mudança de status (para testes):

```bash
curl -X POST http://localhost:3000/force-status \
  -H "Content-Type: application/json" \
  -d '{"status": "RESERVED"}'
```

## Fluxo de Operação

### 1. Status Normal
- Dispositivo envia status `FREE` periodicamente
- Backend pode visualizar disponibilidade

### 2. Reserva
- Backend disponibiliza comando `RESERVE`
- Dispositivo verifica comandos e executa reserva
- Status muda para `RESERVED`
- Timer de timeout é iniciado

### 3. Liberação Manual
- Backend disponibiliza comando `RELEASE`
- Dispositivo executa liberação
- Status volta para `FREE`
- Timer de timeout é cancelado

### 4. Liberação Manual
- Backend disponibiliza comando `RELEASE`
- Dispositivo executa liberação
- Status volta para `FREE`
- Timer de timeout é cancelado

### 5. Liberação Automática (Timeout)
- Após X minutos sem liberação manual
- Status automaticamente volta para `FREE`
- Log de timeout é registrado

### 6. Liberação Automática Aleatória ⭐ **NOVO**
- A cada 2 minutos, verifica se deve liberar espontaneamente
- Só funciona se passou o tempo mínimo desde a reserva
- Probabilidade configurável (ex: 5% de chance)
- Simula uso real onde pessoas liberam recursos antecipadamente

## Comunicação com Backend

### Endpoints Utilizados

1. **POST /api/v1/devices/status**
   - Envia atualizações de status
   - Payload: `{resourceId, status, timestamp, firstConnection}`
   - Flag `firstConnection=true` na primeira requisição para ativar dispositivo

2. **GET /api/v1/devices/{resourceId}/commands/reserve**
   - Verifica se há comando de reserva pendente
   - Retorna: `{resourceId, command, details}`

3. **GET /api/v1/devices/{resourceId}/commands/release**
   - Verifica se há comando de liberação pendente
   - Retorna: `{resourceId, command, details}`

## Logs

O simulador registra todas as atividades:
- ✅ Status enviados com sucesso
- ❌ Erros de comunicação com backend
- � Primeira conexão e ativação no backend
- �🔒 Execução de comandos de reserva
- 🔓 Execução de comandos de liberação
- ⏰ Timeouts automáticos
- 🎲 Verificações e execuções de liberação automática
- 🔄 Mudanças de status

## Exemplos de Uso

### Múltiplos Dispositivos

Para simular múltiplos recursos, execute várias instâncias:

```bash
# Terminal 1 - Laboratório 001
RESOURCE_ID=LAB001 PORT=3001 npm start

# Terminal 2 - Laboratório 002  
RESOURCE_ID=LAB002 PORT=3002 npm start

# Terminal 3 - Sala de Estudo 001
RESOURCE_ID=SALA001 PORT=3003 npm start
```

### Teste de Timeout

```bash
# 1. Forçar reserva
curl -X POST http://localhost:3000/force-status \
  -H "Content-Type: application/json" \
  -d '{"status": "RESERVED"}'

# 2. Aguardar timeout (configurado em TIMEOUT_DURATION_MINUTES)
# 3. Verificar se voltou para FREE automaticamente
curl http://localhost:3000/status
```

## Troubleshooting

### Erro de Conexão com Backend
```
❌ Não foi possível conectar ao backend: http://localhost:8080
```
**Solução**: Verifique se o backend está rodando e a URL está correta.

### Variável de Ambiente Obrigatória
```
❌ Variável de ambiente obrigatória não definida: RESOURCE_ID
```
**Solução**: Configure `RESOURCE_ID` no arquivo `.env`.

### Comandos Não Executados
Se comandos não são executados, verifique:
1. Backend está retornando comandos nos endpoints corretos
2. Status atual permite a operação (FREE→RESERVED, RESERVED→FREE)
3. Logs para identificar erros de comunicação