# Sistema IoT - Frontend

Frontend React para o Sistema de Gestão de Recursos Compartilhados IoT.

## 🚀 Funcionalidades

### Usuários Comuns
- **Autenticação**: Login e registro de usuários
- **Dashboard**: Visualização em tempo real dos recursos IoT com mapa de disponibilidade
- **Reservas**: Criar e liberar reservas de recursos
- **Histórico**: Visualizar histórico pessoal de reservas
- **Estatísticas**: Visualizar estatísticas do sistema e métricas de uso

### Administradores
- **Gerenciamento de Recursos**: Criar, editar e remover recursos IoT
- **Relatórios Avançados**: Acesso a relatórios detalhados do sistema
- **Administração de Usuários**: Gerenciar usuários do sistema

## 🛠️ Tecnologias

- **React 18**: Framework principal
- **Vite**: Build tool e dev server
- **React Router**: Navegação entre páginas
- **Tailwind CSS**: Estilização responsiva
- **Axios**: Requisições HTTP
- **Lucide React**: Biblioteca de ícones

## 📦 Instalação

1. **Instalar dependências**:
   ```bash
   npm install
   npm install -D @tailwindcss/postcss  # Plugin correto do Tailwind para PostCSS
   ```

2. **Configurar variáveis de ambiente**:
   Edite o arquivo `src/services/api.js` para configurar a URL do backend:
   ```javascript
   const API = axios.create({
     baseURL: 'http://localhost:8080/api/v1', // Ajuste conforme necessário
   });
   ```

3. **Executar em modo de desenvolvimento**:
   ```bash
   npm run dev
   ```

4. **Build para produção**:
   ```bash
   npm run build
   ```

## 🔧 Configuração do Backend

O frontend espera que o backend esteja rodando em `http://localhost:8080` com as seguintes APIs:

### Autenticação
- `POST /api/v1/auth/login` - Login
- `POST /api/v1/users` - Registro
- `GET /api/v1/users/me` - Dados do usuário atual

### Recursos IoT
- `GET /api/v1/iot-resources` - Listar recursos
- `GET /api/v1/iot-resources/{id}` - Obter recurso por ID
- `POST /api/v1/iot-resources` - Criar recurso (admin)
- `DELETE /api/v1/iot-resources/{id}` - Deletar recurso (admin)
- `PUT /api/v1/iot-resources/{id}/status` - Atualizar status

### Reservas
- `POST /api/v1/reserves` - Criar reserva
- `GET /api/v1/reserves/my-active` - Reservas ativas do usuário
- `GET /api/v1/reserves/my-history` - Histórico de reservas
- `DELETE /api/v1/reserves/{id}` - Liberar reserva

### Relatórios
- `GET /api/v1/reports/system-stats` - Estatísticas do sistema
- `GET /api/v1/reports/resource-usage` - Estatísticas de uso (admin)

## 📱 Design Responsivo

O sistema foi desenvolvido com design responsivo usando Tailwind CSS:

- **Mobile First**: Interface otimizada para dispositivos móveis
- **Breakpoints**: Adaptação para tablet e desktop
- **Navegação Móvel**: Menu hamburger para dispositivos pequenos
- **Cards Responsivos**: Layout de grid adaptativo para recursos

## 🎨 Sistema de Cores

- **Verde**: Recursos disponíveis (FREE)
- **Vermelho**: Recursos ocupados (OCCUPIED)
- **Amarelo**: Recursos indisponíveis (UNAVAILABLE)
- **Azul**: Ações principais e links
- **Cinza**: Informações secundárias

## 🔐 Autenticação

- **JWT Token**: Armazenado no localStorage
- **Interceptors**: Adição automática do token nas requisições
- **Proteção de Rotas**: Rotas protegidas baseadas em autenticação e perfil
- **Auto-logout**: Logout automático em caso de token inválido

## 📊 Estados da Aplicação

### Loading States
- Spinners durante carregamento de dados
- Estados de loading para ações específicas
- Feedback visual em botões durante processamento

### Error Handling
- Mensagens de erro amigáveis
- Tratamento de erros de rede
- Validação de formulários

## 🗂️ Estrutura do Projeto

```
src/
├── components/          # Componentes reutilizáveis
│   ├── Navbar.jsx      # Barra de navegação
│   ├── ProtectedRoute.jsx  # Proteção de rotas
│   └── ResourceCard.jsx    # Card de recurso IoT
├── context/            # Contextos React
│   └── AuthContext.jsx # Contexto de autenticação
├── pages/              # Páginas da aplicação
│   ├── Dashboard.jsx   # Dashboard principal
│   ├── Login.jsx       # Página de login
│   ├── Register.jsx    # Página de registro
│   ├── HistoryPage.jsx # Histórico de reservas
│   ├── StatsPage.jsx   # Estatísticas
│   └── AdminResourcesPage.jsx # Admin - recursos
├── services/           # Serviços e APIs
│   ├── api.js         # Configuração do Axios
│   └── index.js       # Serviços organizados
└── utils/             # Utilitários
```

## 🚦 Como Usar

### 1. Primeiro Acesso
1. Acesse `/register` para criar uma conta
2. Faça login em `/login`
3. Será redirecionado para o dashboard

### 2. Usuário Comum
1. **Dashboard**: Visualize recursos disponíveis
2. **Reservar**: Clique em "Reservar" em recursos livres
3. **Liberar**: Clique em "Liberar" em suas reservas ativas
4. **Histórico**: Acesse "Histórico" no menu
5. **Estatísticas**: Veja métricas em "Estatísticas"

### 3. Administrador
1. Acesso a todas as funcionalidades de usuário comum
2. **Gerenciar Recursos**: Menu "Gerenciar Recursos"
3. **Criar Recursos**: Botão "Novo Recurso"
4. **Editar/Excluir**: Ações na tabela de recursos

## 🐛 Solução de Problemas

### Erro de CORS
Certifique-se que o backend está configurado para aceitar requisições do frontend.

### Token Expirado
O sistema faz logout automático. Faça login novamente.

### Recursos não Carregam
Verifique se o backend está rodando e acessível.

## 🔄 Atualizações em Tempo Real

O sistema atualiza dados automaticamente:
- **Dashboard**: Atualização a cada 30 segundos
- **Botão Atualizar**: Atualização manual disponível
- **Ações**: Dados atualizados após cada ação (reservar/liberar)

## 📄 Licença

Este projeto foi desenvolvido como atividade acadêmica para a disciplina de Tópicos Avançados em WEB I do IFBA - Campus Santo Antônio de Jesus.
