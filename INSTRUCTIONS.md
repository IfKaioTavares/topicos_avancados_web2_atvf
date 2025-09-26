# Sistema IoT de Gestão de Recursos Compartilhados

## 📋 Sobre o Projeto

Sistema completo de gestão de recursos IoT compartilhados desenvolvido para a disciplina de Tópicos Avançados em WEB I do IFBA - Campus Santo Antônio de Jesus.

O sistema permite monitorar e reservar recursos físicos compartilhados (ex.: estações de trabalho em laboratório, salas de estudo, vagas de estacionamento ou bicicletas inteligentes) com simuladores IoT que enviam status em tempo real.

## 🏗️ Arquitetura

- **Backend**: Spring Boot (Java) - API REST
- **Frontend**: React + Vite - Interface Web Responsiva
- **Device Simulator**: Node.js - Simulador de dispositivos IoT
- **Banco de Dados**: PostgreSQL
- **Containerização**: Docker Compose

## 🚀 Como Executar o Sistema

### 1. Pré-requisitos

- Docker e Docker Compose
- Node.js 16+ (para o simulador de dispositivos)
- JDK 11+ (para o backend, se executar localmente)

### 2. Executar com Docker

1. **Subir o banco de dados**:
   ```bash
   docker-compose up -d
   ```

2. **Executar o backend**:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   O backend estará disponível em `http://localhost:8080`
   Documentação da API (Swagger): `http://localhost:8080/swagger-ui.html`

3. **Executar o frontend**:
   ```bash
   cd frontend
   npm install
   npm install -D @tailwindcss/postcss  # Necessário para o Tailwind CSS
   npm run dev
   ```
   O frontend estará disponível em `http://localhost:5173`

4. **Executar simulador de dispositivos** (opcional):
   ```bash
   cd device-simulator
   npm install
   npm run demo
   ```

### 3. Primeiro Acesso

1. Acesse `http://localhost:5173`
2. Clique em "Registre-se aqui" para criar uma conta
3. Faça login com suas credenciais
4. Explore o dashboard com recursos IoT simulados

### 4. Conta de Administrador

Para funcionalidades administrativas, você precisará de uma conta com perfil ADMIN. Consulte o backend para criar usuários administradores.

## 📱 Funcionalidades Implementadas

### ✅ Usuários Comuns
- [x] Autenticação (login/registro)
- [x] Dashboard com mapa de disponibilidade dos recursos
- [x] Sistema de reservas com validação de conflitos
- [x] Histórico pessoal de reservas paginado
- [x] Estatísticas de uso do sistema
- [x] Interface responsiva (mobile/desktop)

### ✅ Administradores
- [x] Gerenciamento de recursos IoT (criar/editar/remover)
- [x] Relatórios avançados do sistema
- [x] Acesso a estatísticas gerais

### ✅ Sistema
- [x] API REST completa
- [x] Autenticação JWT
- [x] Logs de auditoria
- [x] Timeout automático de reservas
- [x] Simuladores IoT funcionais

## 🎨 Interface

- **Design Responsivo**: Funciona perfeitamente em mobile e desktop
- **Sistema de Cores**: Verde (livre), Vermelho (ocupado), Amarelo (indisponível)
- **Feedback Visual**: Loading states, mensagens de erro, confirmações
- **Navegação Intuitiva**: Menu responsivo com diferentes níveis de acesso

## 🔧 Endpoints da API

### Autenticação
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/users` - Registro

### Recursos IoT
- `GET /api/v1/iot-resources` - Listar recursos
- `POST /api/v1/iot-resources` - Criar recurso (admin)
- `DELETE /api/v1/iot-resources/{id}` - Remover recurso (admin)

### Reservas
- `POST /api/v1/reserves` - Criar reserva
- `GET /api/v1/reserves/my-active` - Minhas reservas ativas
- `GET /api/v1/reserves/my-history` - Meu histórico
- `DELETE /api/v1/reserves/{id}` - Liberar reserva

### Relatórios
- `GET /api/v1/reports/system-stats` - Estatísticas gerais
- `GET /api/v1/reports/resource-usage` - Uso por recurso (admin)

## 📊 Status dos Requisitos

### ✅ Funcionalidades Obrigatórias

**1. Dispositivos (simuladores)**
- [x] Simuladores representam recursos
- [x] Envio periódico de status
- [x] Recebimento de comandos do backend
- [x] Timeout automático de reservas

**2. Backend**
- [x] Cadastro de recursos
- [x] Gerenciamento de reservas
- [x] Verificação de conflitos
- [x] Timeout automático
- [x] Histórico de uso
- [x] Autenticação (usuário comum x admin)
- [x] API REST para dispositivos e frontend

**3. Painel Web**
- [x] Mapa de disponibilidade com cores
- [x] Reserva/liberação de recursos
- [x] Histórico e estatísticas
- [x] Administração (recursos e usuários)

### ✅ Requisitos Não Funcionais
- [x] Protocolo HTTP REST
- [x] Banco PostgreSQL
- [x] Logs de auditoria
- [x] Interface responsiva

### 🎁 Funcionalidades Bônus Implementadas
- [x] Atualização em tempo real (polling a cada 30s)
- [x] Gráficos de estatísticas visuais
- [x] Interface moderna e intuitiva

## 🗂️ Estrutura do Projeto

```
web2_atvf/
├── backend/                 # Spring Boot API
│   ├── src/main/java/      # Código Java
│   ├── src/main/resources/ # Configurações e migrations
│   └── target/             # Build artifacts
├── frontend/               # React Application
│   ├── src/
│   │   ├── components/     # Componentes React
│   │   ├── pages/          # Páginas da aplicação
│   │   ├── services/       # APIs e serviços
│   │   └── context/        # Contexts React
│   └── public/             # Assets estáticos
├── device-simulator/       # Simulador Node.js
│   └── src/               # Código do simulador
└── docker-compose.yml     # Configuração Docker
```

## 🏆 Entregáveis Concluídos

- [x] **Código no GitHub** - Organizado em pastas backend, frontend, device-simulator
- [x] **Sistema Funcional** - Todas as funcionalidades implementadas e testadas
- [x] **Interface Responsiva** - Design adaptativo para mobile e desktop
- [x] **Documentação Completa** - README detalhado e comentários no código
- [x] **API REST** - Backend completo com documentação Swagger

## 🎯 Critérios de Avaliação Atendidos

- **Dispositivos/simuladores** (25%): ✅ Simuladores enviam status e aplicam comandos
- **Backend completo** (25%): ✅ Reservas, conflitos, timeout e auditoria
- **Painel web funcional** (15%): ✅ Reserva, histórico e estatísticas
- **Documentação** (15%): ✅ README completo e código bem estruturado
- **Apresentação** (20%): ✅ Sistema demonstrável e funcional

## 📧 Contato

Desenvolvido como atividade acadêmica para:
- **Disciplina**: Tópicos Avançados em WEB I
- **Docente**: Felipe Silva
- **Instituição**: IFBA - Campus Santo Antônio de Jesus
- **Data de Entrega**: 29/09/2025