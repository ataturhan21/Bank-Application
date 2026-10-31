# Bank Application

A Java/Spring Boot learning project with a REST banking service and a command-line client. It demonstrates account operations, persistence, JMS notifications, Kafka publishing, and service-level logging.

## Modules

| Module | Responsibility |
| --- | --- |
| [BankRestServer](BankRestServer/) | REST endpoints, account services, JPA persistence, and JMS integration |
| [BankRestClient](BankRestClient/) | Calls the REST service and publishes account instructions to Kafka |
| [AccountTest](BankRestServer/src/test/java/AccountTest.java) | Unit tests for the account domain model |

Both modules use Spring Boot 2.6.5. The server targets Java 17; the client targets Java 11. Use JDK 17 to work with both projects.

## Local dependencies

Start these services before running the demo:

| Service | Default endpoint | Used by |
| --- | --- | --- |
| HSQLDB server | `localhost:9003/trainingdb` | Server persistence |
| ActiveMQ broker | `localhost:61616` | Server JMS messages to `taxQueue` |
| Kafka broker | `localhost:9092` | Client messages to `accountTopic` |
| Bank REST server | `http://localhost:8080/accounts` | Client HTTP requests |

Review each module's `src/main/resources/application.properties` for your local service settings. The client REST URL is defined in [BankGateway.java](BankRestClient/src/main/java/client/BankGateway.java).

This repository contains local learning defaults. The server uses `spring.jpa.hibernate.ddl-auto=create`, which recreates its schema on startup, and exposes actuator endpoints. Use an isolated development database and local services. It does not implement production banking authentication or account authorization.

## Build and test

Clone the repository, then run from its root with Maven on your PATH:

```bash
git clone https://github.com/ataturhan21/Bank-Application.git
cd Bank-Application
mvn -f BankRestServer/pom.xml test
mvn -f BankRestClient/pom.xml package
```

Each module also includes Maven wrapper files. The commands above avoid relying on a particular shell's executable permissions.

## Run

1. Start HSQLDB, ActiveMQ, and Kafka with endpoints matching the configuration.
2. Start `bank.Application` from `BankRestServer`. With Maven:

   ```bash
   mvn -f BankRestServer/pom.xml spring-boot:run
   ```

3. Run `client.Application` from `BankRestClient` in your Java IDE, using that module's Maven classpath.

The client automatically creates sample accounts, performs deposits/withdrawals/transfers, publishes Kafka messages, and prints account statements. Run it against disposable demo data.

## REST entry points

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/accounts` | List accounts |
| GET | `/accounts/{accountNumber}` | Read an account |
| POST | `/accounts/create` | Create an account using `accountNumber` and `customerName` query parameters |
| POST | `/accounts/operation` | Run `deposit`, `depositEuros`, `withdraw`, `withdrawEuros`, or `transferFunds` |

For operation parameters, see [AccountController.java](BankRestServer/src/main/java/bank/controller/AccountController.java). Runtime integration requires the external services above; no end-to-end execution is claimed by this documentation update.

## License

See [LICENSE](LICENSE).
