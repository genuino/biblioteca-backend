# Biblioteca

Sistema de gerenciamento de biblioteca, com frontend em React/TypeScript (Material UI) e backend em Java Spring Boot (JPA/Hibernate + PostgreSQL).

## Tecnologias

- **Frontend:** React, TypeScript, Material UI
- **Backend:** Java, Spring Boot, JPA/Hibernate
- **Banco de dados:** PostgreSQL
- **Build/Execução do backend:** Maven

## Pré-requisitos

- [Docker](https://www.docker.com/) instalado e em execução (necessário para subir o banco de dados PostgreSQL)
- [Java JDK](https://adoptium.net/) (17 ou superior)
- [Maven](https://maven.apache.org/) instalado (ou utilize o Maven Wrapper `./mvnw` incluído no projeto, se disponível)
- [Node.js](https://nodejs.org/) e npm/yarn (para rodar o frontend)

## Como executar

### 1. Subir o banco de dados com Docker

Certifique-se de que o Docker esteja instalado e em execução, então rode:

```bash
docker-compose up -d
```

Isso irá subir o container do PostgreSQL necessário para o backend.

### 2. Executar o backend via Maven

Dentro da pasta do backend, execute:

```bash
mvn clean install
mvn spring-boot:run
```

Ou, se o projeto utilizar o Maven Wrapper:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

O backend estará disponível em `http://localhost:8081` (ajuste conforme a configuração da aplicação).

### 3. Frontend

Link:

```https://github.com/genuino/biblioteca-frontend
```

## Estrutura do projeto

```
biblioteca/
├── backend/       # API Spring Boot
├── frontend/       # Aplicação React
└── docker-compose.yml
```

## Licença

Este projeto está sob a licença [MIT](LICENSE).
