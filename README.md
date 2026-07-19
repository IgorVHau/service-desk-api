<h1 align="center">Service Desk API</h1>

<p align="center">API REST para gerenciamento de chamados desenvolvida com <b>Java 17</b> e <b>Spring Boot 3</b>.</p>
<p align="center">O projeto demonstra a implementação de autenticação JWT, arquitetura em camadas, monitoramento com Spring Boot Actuator, documentação OpenAPI e empacotamento para produção utilizando Docker com multi-stage build.</p>

<p align="center">
	<img src="https://img.shields.io/badge/Java-17-red?labelColor=blue"/>
	<img src="https://img.shields.io/badge/Spring_Boot-3.x-brightgreen">
</p>
<p align="center">
	<img src="https://img.shields.io/badge/Status-Em_evolução-blue">
</p>

<br>

<details>
	<summary>📚 <b> Table of Contents </b> </summary>

- [Arquitetura](#arquitetura)
- [Tecnologias](#tecnologias)
- [Principais recursos](#principais-recursos)
- [Execução](#execução)
- [Segurança](#segurança)
- [Swagger](#swagger)
- [Testes](#testes)
- [Informações operacionais](#informações-operacionais)
- [Perfis de ambiente](#perfis-de-ambiente)
- [Objetivo](#objetivo)

</details>

## Arquitetura

A aplicação adota uma arquitetura em camadas, separando responsabilidades entre apresentação, regras de negócio e persistência.

Abaixo a representação visual do diagrama de arquitetura.

```mermaid
---
title: Diagrama de arquitetura de camadas
config:
  flowchart:
    htmlLabels: false
---
flowchart LR

Client@{ shape: circle, label: "Cliente" }
Controller@{ shape: rounded, label: "Controller" }
Service@{ shape: rounded, label: "Serviço" }
Repository@{ shape: rounded, label: "Repositório" }
Database@{ shape: cyl, label: "Banco de Dados" }

subgraph Fluxo_principal
	Client f1@--> Controller
	f1@{ curve: linear }
	Controller f2@--> Service
	f2@{ curve: linear }
	Service f3@--> Repository
	f3@{ curve: linear }
	Repository f4@--> Database:::fod
	classDef fod stroke:#00a7ff, stroke-width: 1px
	f4@{ curve: linear }
end

Fluxo_principal:::foa
classDef foa stroke: #fff, stroke-width: 2px

subgraph Exceção
	Exception@{ shape: rounded, label: "Tratamento Global<br/> de Exceções" }
end

Controller e1@--> Exceção:::foe
classDef foe stroke: #f00, stroke-width: 2px
e1@{ animation: slow, curve: linear }
Service e2@--> Exceção
e2@{ animation: slow, curve: linear }


subgraph Segurança
	Security@{ shape: odd, label: "JWT / Filtros" }
end
	Client s1@--> |envia<br/> requisição| Segurança:::foo
	classDef foo stroke:#f50, stroke-width: 2px
	s1@{ animation: slow, curve: linear }
	Segurança s2@--> |valida<br/> token JWT| Controller
	s2@{ animation: slow, curve: linear }

```
### Responsabilidades das camadas

- **[Controller](src/main/java/service_desk_api/api/controller)** – expõe os endpoints REST e valida os dados de entrada.
- **[Service](src/main/java/service_desk_api/api/service)** – concentra as regras de negócio e orquestra o fluxo da aplicação.
- **[Repository](src/main/java/service_desk_api/api/repository)** – realiza o acesso aos dados com Spring Data JPA.
- **[Model](src/main/java/service_desk_api/api/model)** – representa as entidades persistidas e o estado do domínio.
- **[DTO](src/main/java/service_desk_api/api/dto)** – define os dados de entrada e saída da API.
- **[Config](src/main/java/service_desk_api/api/config)** – reúne configurações de segurança (Spring Security, JWT e filtros), OpenAPI e beans.
- **[Exception Handler](src/main/java/service_desk_api/api/exception)** – centraliza o tratamento de exceções e a padronização das respostas.

A autenticação e a autorização são tratadas transversalmente pelo Spring Security e pelos filtros JWT. Model e DTO não aparecem no fluxo principal do diagrama porque representam estruturas de dados, e não etapas de processamento da requisição.

## Tecnologias

- Java 17
- Spring Boot 3
- Spring Data JPA
- Spring Security
- Spring Boot Actuator
- JWT (jjwt)
- H2 Database
- Lombok
- Springdoc OpenAPI (Swagger)
- JUnit 5
- Mockito
- Docker
- Maven

## Principais recursos

- CRUD completo de chamados
- Autenticação stateless com JWT
- Controle de acesso por perfis `USER` e `ADMIN`
- Validação de entrada com Jakarta Validation
- Tratamento global de exceções
- Padronização das respostas da API
- Documentação interativa com Swagger e OpenAPI
- Informações operacionais e de build com Spring Boot Actuator
- Perfis separados para desenvolvimento e produção
- Empacotamento com Docker multi-stage e execução como usuário não-root

## Execução

### Pré-requisitos

Escolha uma das formas de execução:

- **Maven:** Java 17
- **Docker:** Docker Engine ou Docker Desktop

### Clonar o projeto

```bash
git clone https://github.com/IgorVHau/service-desk-api.git
cd service-desk-api
```

### Executar com Maven

| Sistema Operacional | Comando |
|:----------:|:----------|
|Linux, macOS ou Git Bash|`./mvnw spring-boot:run`|
|Windows|`mvnw.cmd spring-boot:run`|


### Executar com Docker

Construa a imagem:

```bash
docker build -t service-desk-api .
```

Execute o container:

```bash
docker run -d --name service-desk-api-container -p 8080:8080 service-desk-api
```

A imagem utiliza multi-stage build e contém apenas o ambiente de execução e o artefato da aplicação. O processo Java é executado por um usuário sem privilégios de root.

Visualize os logs:

```bash
docker logs -f service-desk-api-container
```

Interrompa e remova o container:

```bash
docker stop service-desk-api-container
docker rm service-desk-api-container
```

### Endereços locais

| Recurso | Endereço |
|---|---|
| API | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |

## Segurança

A API utiliza autenticação baseada em **JWT (JSON Web Token)** para proteger seus endpoints, com filtro de segurança customizado e integração com Swagger para autorização via token.

É necessário autenticar o usuário por meio de login e senha. Caso contrário, todas as operações serão bloqueadas.

> [!NOTE] 
> As credenciais abaixo são fictícias e destinadas exclusivamente à execução local:

| Usuário | E-mail | Senha | Perfil |
|:-------:|:-------:|:-------:|:-------:|
| Jorge | `user@email.com` | `654321` | `USER` |
| Fernando | `admin@email.com` | `123456` | `ADMIN` |

### Fluxo de autenticação

1. Envie uma requisição **POST** para `/auth/login`.
2. Envie no corpo da requisição um JSON contendo os campos `email` e `senha` utilizando uma das credenciais da tabela acima.
3. Após a autenticação bem-sucedida, a API retornará um **token JWT** conforme ilustrado abaixo.

<p align="center">
	<img src="docs/images/auth-login-response.png" alt="Resposta do endpoint de login" width="650">
</p>

4. Nas requisições seguintes, envie o token no header `Authorization` utilizando o formato `Bearer <token>`.

<p align="center">
	<img src="docs/images/bearer-token-auth.png" alt="Autorização usando Bearer Token" width="650">
</p>

> [!NOTE]
> O token é válido por 1 hora. Após sua expiração, uma nova autenticação deve ser realizada.


## Swagger

A API disponibiliza uma documentação interativa por meio do Swagger UI e uma especificação OpenAPI em formato JSON.

Por meio do Swagger UI, é possível:
- consultar endpoints, contratos e schemas;
- autenticar-se com JWT;
- executar requisições diretamente pelo navegador;
- visualizar os formatos esperados de requisição e resposta.

Exemplo da interface do Swagger UI:

<p align="center">
	<img src="docs/images/swagger-ui.png" alt="Interface do Swagger UI" width="800">
</p>

## Testes

O projeto utiliza JUnit 5, Mockito e MockMvc em dois níveis:

- **[Testes unitários de serviço](src/test/java/service_desk_api/api/service/ChamadoServiceTest.java):** validam as regras de negócio com dependências simuladas.
- **[Testes da camada Web](src/test/java/service_desk_api/api/controller/ChamadoControllerTest.java):** validam status HTTP, respostas JSON e tratamento de exceções.

Execute todos os testes com:

```bash
./mvnw test
```

No Windows:

```powershell
mvnw.cmd test
```

## Informações operacionais

A aplicação utiliza o **Spring Boot Actuator** para expor informações operacionais e metadados de build.

| Método HTTP | Endpoint | Permissão |
|:----------:|:----------|:----------|
| `GET` | `/actuator/info` | `ADMIN` 🔐 |

Exemplo de informações expostas:

- Nome e versão da aplicação
- Dados de build (artifact, versão, data)
- Metadados do Git (branch, commit, timestamp)

Essas informações são obtidas a partir da configuração do [pom.xml](pom.xml) e do repositório Git.


## Perfis de ambiente

| Perfil | Banco de dados | Finalidade |
|---|---|---|
| [`dev`](src/main/resources/application-dev.yml) | H2 em memória | Desenvolvimento local, logs SQL e criação automática do schema |
| [`prod`](src/main/resources/application-prod.yml) | PostgreSQL externo | Configuração próxima de produção, credenciais por variáveis de ambiente e validação do schema |


## Objetivo
Desenvolver uma API REST completa para aplicar, de forma prática, conceitos de arquitetura em camadas, segurança com JWT, testes automatizados, documentação OpenAPI, informações operacionais e containerização no ecossistema Spring.
