# Тестове завдання: Два Spring Boot сервіси з Postgres та Docker

Цей проект реалізує мінімальний функціональний обсяг, що складається з двох Spring Boot сервісів (`auth-api` та `data-api`) та бази даних PostgreSQL, які працюють разом у середовищі Docker Compose.

## ✅ Основні досягнення

- Повна реалізація захисту API за допомогою JWT
- Коректна міжсервісна комунікація з внутрішнім токеном безпеки (`X-Internal-Token`)
- Стійкість до помилок `ObjectOptimisticLockingFailureException` при реєстрації користувача
- Успішне логування операцій у базі даних

## 🧱 Архітектура та ендпоінти

| Компонент              | Порт              | Функціонал                                                          |
| ---------------------- | ----------------- | ------------------------------------------------------------------- |
| Service A (`auth-api`) | 8080 (зовнішній)  | Реєстрація, логін (JWT) та захищений ендпоінт `/process`            |
| Service B (`data-api`) | 8081 (внутрішній) | Приймає запити лише від Service A, виконує обробку (`/transform`)   |
| PostgreSQL             | 5432              | Зберігає користувачів (`users`) та логі операцій (`processing_log`) |

## 🚀 Запуск проекту

Переконайтеся, що у вас встановлені **Maven** та **Docker**.

### 1. Збірка JAR-файлів

Виконайте збірку обох Spring Boot додатків з кореневої директорії проекту за допомогою Docker.

### 1.2. Запуск Docker Compose

```bash
docker compose up -d --build

Сервіси будуть доступні через http://localhost:8080.

**## 2. Приклад роботи та перевірка вимог ТЗ**

Цей розділ демонструє виконання мінімального функціонального обсягу згідно з технічним завданням.

**### 2.1. Перевірка 1: Реєстрація користувача (POST /api/auth/register)

Підтвердження, що користувач може бути створений.

Invoke-WebRequest -Method POST -Uri http://localhost:8080/api/auth/register -ContentType "application/json" -Body '{"email":"testuser@wwt.com","password":"secure_password"}'

Вивід PowerShell:

StatusCode : 201
StatusDescription :
Content : {}
RawContent : HTTP/1.1 201
Vary: Origin,Access-Control-Request-Method,Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Pragma: no-cache
X-Frame-Options: DENY
Keep-Alive: tim...
Headers : {[Vary, Origin,Access-Control-Request-Method,Access-Control-Request-Headers], [X-Content-Type-Optio
ns, nosniff], [X-XSS-Protection, 0], [Pragma, no-cache]...}
RawContentLength : 0

Очікуваний результат: StatusCode : 201

**### 2.2. Перевірка 2: Логін та отримання JWT-токену (POST /api/auth/login)

Підтвердження коректної аутентифікації.

# PowerShell (Зберігання токену в змінну $TOKEN)

$response = Invoke-WebRequest -Method POST -Uri http://localhost:8080/api/auth/login -ContentType "application/json" -Body '{"email":"testuser@wwt.com","password":"secure_password"}'
$token_json = $response.Content | ConvertFrom-Json
$TOKEN = $token_json.token

Write-Host "Отриманий JWT-токен: $TOKEN"

Вивід PowerShell:

StatusCode : 200
StatusDescription :
Content : {"token":"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJkNjAxOWE4Ny05ODk3LTQ5OWMtODliMC1jNDRiNDMyZmViNWIiLCJz
dWIiOiJkNjAxOWE4Ny05ODk3LTQ5OWMtODliMC1jNDRiNDMyZmViNWIiLCJpYXQiOjE3NjE0Nzg0NTMsImV4cCI6MTc2MTU2NDg
1M..."}
RawContent : HTTP/1.1 200
Vary: Origin,Access-Control-Request-Method,Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Pragma: no-cache
X-Frame-Options: DENY
Transfer-Encodi...
Forms : {}
Headers : {[Vary, Origin,Access-Control-Request-Method,Access-Control-Request-Headers], [X-Content-Type-Optio
ns, nosniff], [X-XSS-Protection, 0], [Pragma, no-cache]...}
Images : {}
InputFields : {}
Links : {}
ParsedHtml : mshtml.HTMLDocumentClass
RawContentLength : 248

Отриманий JWT-токен: eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJkNjAxOWE4Ny05ODk3LTQ5OWMtODliMC1jNDRiNDMyZmViNWIiLCJzdWIiOiJkNjAxOWE4Ny05ODk3LTQ5OWMtODliMC1jNDRiNDMyZmViNWIiLCJpYXQiOjE3NjE0NzY1OTIsImV4cCI6MTc2MTU2Mjk5Mn0.weDpbFicKTH5FdU3jZHTpeWuHZUHpzc0Ntu_ebaI1Ck

Очікуваний результат: StatusCode : 200 та виведення валідного токену.

**### 2.3. Перевірка 3: Захищений процесинг та міжсервісна комунікація (POST /api/process)

Перевірка всього ланцюжка: JWT-безпека, міжсервісний виклик та логування.

# PowerShell (Використання збереженого токену)

Invoke-WebRequest -Method POST -Uri http://localhost:8080/api/process `    -Headers @{"Authorization"="Bearer $TOKEN"}`
-ContentType "application/json" `
-Body '{"text":"This is the text to be processed by Service B."}'

Вивід PowerShell:

StatusCode : 200
StatusDescription :
Content : {"result":".B ECIVRES YB DESSECORP EB OT TXET EHT SI SIHT"}
RawContent : HTTP/1.1 200
Vary: Origin,Access-Control-Request-Method,Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Pragma: no-cache
X-Frame-Options: DENY
Transfer-Encodi...
Forms : {}
Headers : {[Vary, Origin,Access-Control-Request-Method,Access-Control-Request-Headers], [X-Content-Type-Optio
ns, nosniff], [X-XSS-Protection, 0], [Pragma, no-cache]...}
Images : {}
InputFields : {}
Links : {}
ParsedHtml : mshtml.HTMLDocumentClass
RawContentLength : 59

Очікуваний результат: StatusCode : 200 та оброблений текст.

### 2.4. Перевірка 4: Логування операції (Вимога 2c)

**Мета:** Перевірка збереження запису про обробку в базі даних.

---

#### 🔧 Підключення до контейнера PostgreSQL

```bash
docker exec -it <postgres_container_id> psql -U postgres -d mydb
-- SQL-запит для перевірки логу
SELECT id, input_text, output_text, user_id, created_at FROM processing_log;

Вивід перевірки логів:

$ docker exec -it 737baef769e6 psql -U postgres -d mydb
psql (15.14 (Debian 15.14-1.pgdg13+1))
Type "help" for help.

mydb=# SELECT * FROM processing_log;
                 id                 |       created_at        |                input_text                 |               output_text                |               user_id
--------------------------------------+----------------------------+--------------------------------------------+--------------------------------------------+--------------------------------------
392cefbc-0757-49b9-a407-f3831df81fde | 2025-10-26 11:37:57.207912 | This is the text to be processed by Service B. | .B ECIVRES YB DESSECORP EB OT TXET EHT SI SIHT | d6019a87-9897-499c-89b0-c44b432feb5b
(1 row)

Очікуваний результат: Отримання рядка з ID користувача та текстами, що відповідають останній операції.
```
