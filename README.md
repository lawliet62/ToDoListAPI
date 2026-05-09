# ToDoListAPI

## Project path: https://roadmap.sh/projects/todo-list-api

JWT 인증 기반의 To-Do List REST API 프로젝트입니다.

## 기술 스택

- Java 21
- Spring Boot 4
- Spring Security
- Spring Data JPA
- MySQL
- Swagger UI (`springdoc-openapi`)

## 실행 전 준비

### 1. Java 21 설치

이 프로젝트는 Java 21을 사용합니다.

### 2. MySQL 실행 및 데이터베이스 생성

`src/main/resources/application.yml` 기준 기본 설정은 아래와 같습니다.

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/todolist?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: user
    password: 1234
```

먼저 MySQL에서 데이터베이스를 생성하고, 필요하면 계정 정보도 맞춰주세요.

```sql
CREATE DATABASE todolist;
```

기본 계정 정보를 그대로 쓸 경우 예시는 다음과 같습니다.

```sql
CREATE USER 'user'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON todolist.* TO 'user'@'localhost';
FLUSH PRIVILEGES;
```

로컬 환경이 다르면 `application.yml` 값을 자신의 DB 설정에 맞게 수정하면 됩니다.

## 실행 방법

프로젝트 루트에서 아래 명령어를 실행합니다.

### Windows

```powershell
.\gradlew.bat bootRun
```

### macOS / Linux

```bash
./gradlew bootRun
```

애플리케이션이 정상 실행되면 기본적으로 `http://localhost:8080` 에서 API를 사용할 수 있습니다.

## API 확인 방법

이 프로젝트는 별도 웹 화면이 없는 백엔드 API 프로젝트입니다.  
따라서 실행 확인은 브라우저 화면이 아니라 HTTP 요청으로 합니다.

가장 간단한 방법은 두 가지입니다.

### 1. Swagger UI로 확인

애플리케이션 실행 후 아래 주소로 접속합니다.

```text
http://localhost:8080/swagger-ui/index.html
```

여기서 요청 스펙을 보고 직접 API를 호출할 수 있습니다.

### 2. Postman 또는 curl로 확인

아래 순서로 테스트하면 됩니다.

#### 회원가입

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "tester",
    "email": "tester@example.com",
    "password": "1234"
  }'
```

#### 로그인

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "tester@example.com",
    "password": "1234"
  }'
```

로그인 응답의 `data.token` 값을 복사해서 이후 요청에 `Authorization: Bearer {token}` 형식으로 넣습니다.

#### 할 일 생성

```bash
curl -X POST http://localhost:8080/todos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "title": "Read README",
    "description": "Write project usage guide"
  }'
```

#### 할 일 목록 조회

```bash
curl "http://localhost:8080/todos?page=1&limit=10" \
  -H "Authorization: Bearer {token}"
```

## 주요 엔드포인트

- `POST /auth/register`: 회원가입
- `POST /auth/login`: 로그인
- `POST /todos`: 할 일 생성
- `GET /todos?page=1&limit=10`: 할 일 목록 조회
- `PUT /todos/{todoId}`: 할 일 수정
- `DELETE /todos/{todoId}`: 할 일 삭제
