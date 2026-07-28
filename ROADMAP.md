# 🛣️ Roadmap – Service Desk API

Este roadmap descreve as etapas de evolução planejadas para o projeto, com foco em aprendizado prático, boas práticas de engenharia e escalabilidade.

---

## ✅ Fase 1 – Base funcional (Concluída)
- [x] CRUD de chamados
- [x] Arquitetura em camadas
- [x] Validações com Jakarta Validation
- [x] Autenticação e autorização com JWT
- [x] Documentação com Swagger
- [x] Tratamento global de exceções
- [x] Testes unitários e de controller
- [x] README estruturado

---

## 🔄 Fase 2 – Consolidação (Atual)
- [x] Diagrama de arquitetura
- [x] Melhorar documentação de execução e consumo da API
- [x] Dockerização da aplicação
- [x] Corrigir e padronizar o tratamento de exceções
- [x] Refinar DTOs de entrada e saída
- [x] Evitar exposição direta das entidades nos endpoints de chamados
- [x] Enriquecer chamados com prioridade, categoria e data de conclusão
- [ ] Revisar e padronizar a organização dos pacotes
- [ ] Melhorar cobertura de testes

---

## 🚀 Fase 3 – Automação e infraestrutura
- [ ] Criar pipeline de CI com GitHub Actions para build e execução dos testes
- [ ] Corrigir e validar a geração de metadados do Git no build
- [ ] Integrar a aplicação com PostgreSQL
- [ ] Avaliar migrations com Flyway ou Liquibase
- [ ] Adicionar testes de integração

---

## 🌱 Fase 4 – Evolução do domínio

- [ ] Padronizar os nomes das classes
- [ ] Adicionar paginação e ordenação
- [ ] Implementar filtros combináveis por status, período, prioridade e categoria
- [ ] Associar o chamado ao usuário solicitante
- [ ] Permitir atribuição de um usuário responsável
- [ ] Registrar histórico de alterações do chamado
- [ ] Avaliar regras de SLA por prioridade
