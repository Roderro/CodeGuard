# CodeGuard OTP Service

Сервис для генерации и валидации одноразовых паролей (OTP) с доставкой через SMS, Email и Telegram. Поддерживает два режима запуска: Docker Compose и Standalone.

## 🚀 Способы запуска
### 1. Запуск через Docker Compose (рекомендуемый)
**Требования**: Docker, Docker Compose

1. Настройте переменные окружения в `docker-compose.yml`:
```yaml
environment:
- SPRING_JWT_SECRET=your_jwt_secret
- SPRING_MAIL_HOST=smtp.example.com
- SPRING_MAIL_USERNAME=your@email.com
- SPRING_MAIL_PASSWORD=your_password
- SPRING_TGBOT_TOKEN=your_telegram_token
```
2. Запустите сервисы:
```bash
docker-compose up -d
```
3. Проверьте логи:
```bash
docker-compose logs -f codeguard
```
### Доступные сервисы:
* Приложение: `http://localhost:8080`
* Swagger Web UI: `http://localhost:8080/swagger-ui.html`
* PostgreSQL: jdbc:postgresql://localhost:5432/codeguard

### 2. Standalone запуск
1. Заполните файл `application-standalone.properties`
2. Укажите в `application.properties` `spring.profiles.active=standalone`
3. Запустите приложение `mvn spring-boot:run`

## 📚 API Документация

Сервис предоставляет автоматически сгенерированную OpenAPI 3.0 документацию, доступную в нескольких форматах:

1. **Интерактивный Swagger UI**  
   Доступен после запуска приложения по адресу:  
   `http://localhost:8080/swagger-ui.html`

2. **JSON-спецификация**  
   Полная спецификация API в формате OpenAPI 3.0:  
   `http://localhost:8080/v3/api-docs`  
   Или локальная в файле:  
   `api-docs/api-docs_v1.1.json`

## 📌 Как пользоваться сервисом

### 1. Регистрация и аутентификация
- **Регистрация**: POST `/api/auth/register`  
  Тело запроса (JSON):
  ```json
  {
    "username": "your_username",
    "password": "your_password"
  }
  ```
- **Авторизация**: POST `/api/auth/login`  
  Тело запроса (JSON):
  ```json
  {
    "username": "your_username",
    "password": "your_password"
  }
  ```
  В ответе получите JWT-токен для доступа к защищенным эндпоинтам.
### 2. Генерация OTP
- **Запрос**: POST `/api/otp/generate`  
  Заголовок: `Authorization: Bearer <ваш_JWT_токен>`  
  Тело запроса (JSON):
  ```json
  {
  "operationId": "unique_operation_id",
  "phone": "+1234567890",
  "email": "user@example.com",
  "tgUsername": "telegram_username"
  }
  ```
  Сервис отправит код на указанные каналы (минимум один обязателен).
### 3. Валидация OTP
- **Запрос**: GET  `/api/otp/validate?operationId=<operation_id>&code=<otp_code>`  
  Заголовок: `Authorization: Bearer <ваш_JWT_токен>`
### 4. Управление пользователями (ADMIN)
- **Получить всех пользователей:**: GET `/api/user/all`  
  Заголовок: `Authorization: Bearer <ваш_JWT_токен>`
- **Удалить пользователя:**: DELETE `/api/user/{username}`  
  Заголовок: `Authorization: Bearer <ваш_JWT_токен>`
### 5. Настройки OTP (ADMIN)
- **Получить конфигурацию:**: GET `/api/otp/config`  
  Заголовок: `Authorization: Bearer <ваш_JWT_токен>`
- **Изменить конфигурацию:**: PATCH `/api/otp/config`  
  Заголовок: `Authorization: Bearer <ваш_JWT_токен>`  
  Тело запроса (JSON):
  ```json
  {
  "length": 6,
  "lifetimeInSeconds": 60
  }
  ```

## 🧪 Как протестировать
1. Интеграционные тесты:

* Используйте Postman или curl для проверки API.
* Пример curl для генерации OTP:
```bash
  curl -X POST -H "Authorization: Bearer <JWT>" -H "Content-Type: application/json" -d '{"operationId":"test1","email":"test@example.com"}' http://localhost:8080/api/otp/generate
```

### 📦 Зависимости
* Java 21
* Spring Boot 3.x
* PostgreSQL
* SMPP (для SMS)
* SMPPSim для эмуляции отправки сообщений
* Telegram Bot API