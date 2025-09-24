**Instituto Federal de Educação, Ciência e Tecnologia da Bahia Campus**: Santo Antônio de Jesus **Data**: \_\_\_\_\_/\_\_\_\_\_/\_\_\_\_\_\_\_\_ **Curso**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ **Disciplina**: Tópicos Avançados em WEB I | **Docente**: Felipe Silva **Discente**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ 

**Sistema IoT de Gestão de Recursos Compartilhados com Interface Web** 

**Objetivo Geral** 

Construir (em trio) uma solução IoT que permita monitorar e reservar recursos físicos  compartilhados (ex.: estações de trabalho em laboratório, salas de estudo, vagas de  estacionamento ou bicicletas inteligentes). 

Cada recurso tem um dispositivo (ou simulador) que envia status em tempo real (livre,  ocupado, indisponível) e aceita comandos de reserva/liberação. O sistema precisa  integrar: 

• Simuladores IoT → enviam estado dos recursos (livre/ocupado) e recebem  comandos de reserva. 

• Backend → gerencia autenticação, reservas, conflitos de uso e histórico. 

• Painel Web → usuários visualizam disponibilidade, reservam recursos e veem o  histórico. 

**Funcionalidades obrigatórias** 

**1 Dispositivos (ou simuladores)** 

• Cada simulador representa um recurso (ex.: sala ou estação de trabalho). • Envia periodicamente seu status: {resourceId, status, timestamp}. • Recebe comandos do backend: reservar ou liberar. 

• Deve aplicar um timeout de reserva (se não houver liberação manual, o recurso  volta a ficar livre após X minutos).

P á g i n a 1 | 4 

**Instituto Federal de Educação, Ciência e Tecnologia da Bahia | Campus:** Santo Antônio de Jesus Superior de Tecnologia em Análise e Desenvolvimento de Sistemas 

Tópicos Avançados em WEB I **20251.6.TADSSAJ.1N**   
**Instituto Federal de Educação, Ciência e Tecnologia da Bahia Campus**: Santo Antônio de Jesus **Data**: \_\_\_\_\_/\_\_\_\_\_/\_\_\_\_\_\_\_\_ **Curso**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ **Disciplina**: Tópicos Avançados em WEB I | **Docente**: Felipe Silva **Discente**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ 

**2 Backend** 

• Cadastro de recursos (ID, nome, tipo, status inicial). 

• Gerenciamento de reservas: 

o Criação e liberação. 

o Verificação de conflito (impedir reservas simultâneas). 

o Aplicação de timeout automático. 

• Histórico de uso: consultas por recurso, período e usuário. 

• Autenticação simples (usuário comum x administrador). 

• API REST ou WebSocket para interação com dispositivos e painel. 

**3 Painel Web** 

• Mapa de disponibilidade (grid ou lista com cores: verde \= livre, vermelho \=  ocupado). 

• Tela de reserva/liberação: usuário comum só pode reservar e liberar os recursos  permitidos. 

• Histórico e estatísticas: listar reservas passadas, tempo médio de uso, recursos  mais utilizados. 

• Administração: cadastro/remoção de recursos e gerenciamento de usuários. 

**Requisitos não funcionais** 

• Um único protocolo de comunicação (HTTP REST ou WebSocket). • Armazenamento em banco leve (SQLite, PostgreSQL ou MongoDB). • Logs de auditoria: {timestamp, user, action, resourceId, result}. • Interface web responsiva (acessível em desktop e mobile).

P á g i n a 2 | 4 

**Instituto Federal de Educação, Ciência e Tecnologia da Bahia | Campus:** Santo Antônio de Jesus Superior de Tecnologia em Análise e Desenvolvimento de Sistemas 

Tópicos Avançados em WEB I **20251.6.TADSSAJ.1N**   
**Instituto Federal de Educação, Ciência e Tecnologia da Bahia Campus**: Santo Antônio de Jesus **Data**: \_\_\_\_\_/\_\_\_\_\_/\_\_\_\_\_\_\_\_ **Curso**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ **Disciplina**: Tópicos Avançados em WEB I | **Docente**: Felipe Silva **Discente**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ 

**Entregáveis** 

1\. Código no GitHub (organizado em /device, /backend, /frontend). 2\. Diagramas UML com codificação: 

o Diagrama de componentes (device, backend, frontend). 

o Diagrama de sequência (fluxo reserva/liberação). 

o Diagrama de casos de uso (usuário comum x administrador). 

3\. Relatório técnico (6–8 páginas) com: 

o Arquitetura. 

o Modelo de dados. 

o Casos de uso. 

o Decisões técnicas (protocolo, armazenamento). 

o Limitações e próximos passos. 

4\. Apresentação (PPTX, 10–12 slides) com resumo, diagramas e prints das telas. 

**Critérios de avaliação – Peso 3,0**

| Critério  | Peso   25%  25%  15%  15% |
| ----- | ----- |
| Dispositivos/simuladores enviando status e aplicando comandos 20% |  |
| Backend com reservas, conflitos, timeout e auditoria  |  |
| Painel web funcional com reserva, histórico e estatísticas  |  |
| UML com codificação \+ relatório técnico  |  |
| Apresentação final  |  |

P á g i n a 3 | 4 

**Instituto Federal de Educação, Ciência e Tecnologia da Bahia | Campus:** Santo Antônio de Jesus Superior de Tecnologia em Análise e Desenvolvimento de Sistemas 

Tópicos Avançados em WEB I **20251.6.TADSSAJ.1N**   
**Instituto Federal de Educação, Ciência e Tecnologia da Bahia Campus**: Santo Antônio de Jesus **Data**: \_\_\_\_\_/\_\_\_\_\_/\_\_\_\_\_\_\_\_ **Curso**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ **Disciplina**: Tópicos Avançados em WEB I | **Docente**: Felipe Silva **Discente**: \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ 

**Bônus (até \+0,2):** 

• Implementar notificações em tempo real (WebSocket ou push). 

• Gráficos de estatísticas (uso por hora/dia). 

• Exportação de histórico em CSV/PDF. 

**Data de Entrega:** 29/09/2025 até as 23:59Hs 

**Respostas contendo o código-fonte do sistema e documentação devem ser enviadas  exclusivamente via email:** felipe\_silva@ifba.edu.br com a devida identificação do aluno,  disciplina e turma.

P á g i n a 4 | 4 

**Instituto Federal de Educação, Ciência e Tecnologia da Bahia | Campus:** Santo Antônio de Jesus Superior de Tecnologia em Análise e Desenvolvimento de Sistemas 

Tópicos Avançados em WEB I **20251.6.TADSSAJ.1N** 