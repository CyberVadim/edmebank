# Edmebank Monolith

Проект представляет собой модульный монолит с готовностью к миграции в микросервисную архитектуру.

## Структура проекта
```
gradle
└── libs.versions.toml         # Version Catalog для зависимостей
src
├── main
│   ├── java
│   │   └── ru.edmebank
│   │       ├── clients                     # Модуль работы с клиентами
│   │       │   ├── adapter
│   │       │   │   ├── input
│   │       │   │   │   ├── rest           # REST API для фронта, мобильных приложений, входящих запросов
│   │       │   │   │   |   ├── mapper     dto -> dto (client -> ui, ui -> client)
│   │       │   │   │   └── kafka          # Обработка входящих событий
│   │       │   │   │       ├── mapper     dto -> dto (client -> ui, ui -> client)
│   │       │   │   └── output
│   │       │   │       ├── repository     # Репозитории для работы с БД
│   │       │   │       └── external       # Интеграция с внешними сервисами (например, СБ), так же rest/kafka
│   │       │   │           ├── mapper     dto -> dto (client -> print, print -> client)
│   │       │   ├── app
│   │       │   │   ├── api
│   │       │   │   │   ├── service        # Интерфейсы сервисов
│   │       │   │   │   └── repository     # Интерфейсы репозиториев
│   │       │   │   └── impl
│   │       │   │       ├── service        # Реализация бизнес-логики (useCase, component и т.д.)
│   │       │   │       └── mapper         # Преобразование DTO <-> Entity
│   │       │   ├── domain
│   │       │   │   ├── entity             # JPA-сущности (Client, Passport и т.д.)
│   │       │   │   └── annotation         # Кастомные аннотации (например, @AgeValidation)
│   │       │   ├── fw
│   │       │   │   ├── config             # Конфигурации модуля
│   │       │   │   ├── security           # Настройки безопасности
│   │       │   │   └── aspect             # Аспекты для логирования/валидации
│   │       │   └── utils
│   │       │       ├── document           # Утилиты для работы с документами
│   │       │       └── validation         # Кастомные валидаторы
│   │       ├── print                      # Модуль для работы с экспортом документов и шаблонизатором
│   │       ├── credit                      # Модуль кредитования
│   │       ├── deposit                     # Модуль депозитов и дальнейшие продукты как новые модули
│   │       └── contracts                   # Общие контракты
│   │           ├── dto                     # Общие DTO
│   │           ├── enum                    # Общие перечисления
│   │           └── event                   # Общие события (Kafka/Redis)
│   └── resources
│       ├── db
│       │   └── migration                   # Flyway/Liquibase скрипты
│       ├── jwt                             # Ключи для JWT
│       ├── openapi                         # OpenAPI спецификации
│       └── application.yml                 # Основной конфиг
├── test
│   ├── java                                # Повторяет структуру main
│   └── resources
│       └── test-data                       # Тестовые данные
│           ├── clients                     # Пример: client-create-request.json
│           └── credit                      # Пример: credit-approve-response.json
├── docker-compose.yml
├── build.gradle                          
├── README.md
└── .gitignore
```
## Технологии
- Java 17
- Spring Boot 3.x
- PostgreSQL, Kafka, Liquibase
- Spring Cloud OpenFeign
- MapStruct, Lombok
- OpenAPI (springdoc)

## Тестирование

- JUnit 5
- Testcontainers
- Тестовые данные: `src/test/resources/test-data`

## Безопасность

- Маскирование данных через AOP
- Кастомные аннотации для доступа
- JWT-фильтрация запросов