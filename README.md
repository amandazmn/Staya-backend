# Staya API

[![CI](https://github.com/amandazmn/Staya-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/amandazmn/Staya-backend/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-6DB33F)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1)

API REST para gestão de hotéis: cadastro de hóspedes e quartos, reservas com controle de disponibilidade, check-in e check-out, e um painel com a visão do dia para a recepção.

## Funcionalidades

- **Clientes e endereços:** cadastro de hóspedes brasileiros (CPF) e estrangeiros (passaporte), sem duplicidade de documento.
- **Tipos de quarto e quartos:** capacidade, tarifa base, andar e status operacional (`DISPONIVEL`, `OCUPADO`, `LIMPEZA`, `MANUTENCAO`, `BLOQUEADO`).
- **Reservas:** ciclo de vida completo (`PENDENTE` → `CONFIRMADA` → `EM_HOSPEDAGEM` → `CONCLUIDA`, ou `CANCELADA`), com bloqueio de conflitos de período e de capacidade do quarto.
- **Busca de disponibilidade:** quartos livres para um período e uma quantidade de hóspedes.
- **Estadias:** check-in a partir de uma reserva ou direto no balcão (walk-in), com acompanhantes, e check-out.
- **Painel do dia:** chegadas e saídas previstas, realizadas e atrasadas, taxa de ocupação, quartos por status e hóspedes no hotel.

### Algumas regras de negócio

- Um quarto não pode ter duas reservas ativas no mesmo período.
- A quantidade de hóspedes não pode passar da capacidade do tipo de quarto.
- Só reservas confirmadas fazem check-in, e só dentro do período reservado.
- O check-in marca o quarto como `OCUPADO`; o check-out conclui a reserva e envia o quarto para `LIMPEZA`.
- Um quarto ocupado só muda de status pelo check-out e não pode ser excluído.
- Datas como "hoje" seguem o fuso horário do hotel (configurável).

## Tecnologias

- Java 17 e Spring Boot 4 (Web MVC, Data JPA, Validation)
- MySQL 8, com o schema versionado por migrations do **Flyway**
- **Swagger / OpenAPI** (springdoc) para documentação interativa
- JUnit 5 e MockMvc nos testes, com banco H2 em memória
- Docker Compose para o ambiente local
- GitHub Actions para integração contínua

## Arquitetura

O projeto segue uma arquitetura em camadas:

```
controller  →  service  →  repository  →  banco (MySQL)
    ↑             ↑
   dto  ←→  mapper      model (entidades JPA)
```

- **controller:** endpoints REST e validação da entrada.
- **service:** regras de negócio e transações.
- **repository:** acesso a dados com Spring Data JPA.
- **dto / mapper:** separam o contrato da API das entidades do banco.
- **exception:** tratamento global de erros, com respostas padronizadas (`400`, `404`, `409` e `422`).

## Como rodar

**Pré-requisitos:** Java 17 e Docker.

```bash
# 1. Suba o banco MySQL
docker compose up -d

# 2. Inicie a aplicação (as migrations do Flyway criam as tabelas)
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080` e a documentação fica em **http://localhost:8080/swagger-ui.html**.

### Variáveis de ambiente

| Variável       | Padrão                               |
|----------------|--------------------------------------|
| `DB_URL`       | `jdbc:mysql://localhost:3307/staya`  |
| `DB_USERNAME`  | `root`                               |
| `DB_PASSWORD`  | `1234`                               |
| `FUSO_HORARIO` | `America/Sao_Paulo`                  |
| `SHOW_SQL`     | `false`                              |

## Testes

```bash
./mvnw test
```

Os testes de integração usam H2 em memória com as mesmas migrations do Flyway, então não precisam de MySQL rodando. Um relógio de teste permite simular datas nas regras de reserva e check-in. Os testes rodam automaticamente no GitHub Actions a cada push.

## Endpoints

| Método   | Rota                         | Descrição                                       |
|----------|------------------------------|-------------------------------------------------|
| `GET`    | `/clientes`                  | Lista clientes                                  |
| `POST`   | `/clientes`                  | Cadastra cliente                                |
| `GET/PUT/DELETE` | `/clientes/{id}`     | Consulta, atualiza ou remove cliente            |
| `GET/POST` | `/enderecos`               | Lista ou cadastra endereços                     |
| `GET/PUT/DELETE` | `/enderecos/{id}`    | Consulta, atualiza ou remove endereço           |
| `GET/POST` | `/tipos-quarto`            | Lista ou cadastra tipos de quarto               |
| `GET/PUT/DELETE` | `/tipos-quarto/{id}` | Consulta, atualiza ou remove tipo de quarto     |
| `GET`    | `/quartos`                   | Lista quartos (filtros: status, tipo, andar)    |
| `GET`    | `/quartos/disponiveis`       | Quartos livres (`entrada`, `saida`, `hospedes`) |
| `POST`   | `/quartos`                   | Cadastra quarto                                 |
| `GET/PUT/DELETE` | `/quartos/{id}`      | Consulta, atualiza ou remove quarto             |
| `PATCH`  | `/quartos/{id}/status`       | Altera o status operacional                     |
| `GET`    | `/reservas`                  | Lista reservas (filtros: cliente, quarto, status) |
| `POST`   | `/reservas`                  | Cria reserva                                    |
| `GET/PUT` | `/reservas/{id}`            | Consulta ou altera reserva                      |
| `PATCH`  | `/reservas/{id}/confirmar`   | Confirma reserva                                |
| `PATCH`  | `/reservas/{id}/cancelar`    | Cancela reserva                                 |
| `GET`    | `/estadias`                  | Lista estadias (filtros: status, quarto, cliente) |
| `GET`    | `/estadias/{id}`             | Consulta estadia                                |
| `POST`   | `/estadias/checkin`          | Check-in a partir de uma reserva                |
| `POST`   | `/estadias/checkin-direto`   | Check-in sem reserva prévia (walk-in)           |
| `POST`   | `/estadias/{id}/checkout`    | Check-out                                       |
| `GET`    | `/painel/dia`                | Painel da recepção (parâmetro opcional `data`)  |

### Exemplo: criar uma reserva

```http
POST /reservas
Content-Type: application/json

{
  "clienteId": 1,
  "quartoId": 3,
  "dataEntrada": "2026-12-20",
  "dataSaida": "2026-12-23",
  "quantidadeHospedes": 2,
  "observacoes": "Chegada prevista à noite"
}
```

## Próximos passos

- Frontend em React para a recepção
- Autenticação e perfis de acesso
- Módulo financeiro (diárias, consumo e fechamento de conta)

## Autora

**Amanda Zimmermann** · [LinkedIn](https://www.linkedin.com/in/amandazimn)
