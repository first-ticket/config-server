# config-server

MSA 서비스들의 중앙 설정을 제공하는 Spring Cloud Config Server입니다.
config-repo의 설정 파일을 읽어 인증된 클라이언트 서비스에 전달합니다.

## 요구사항

- JDK 21
- GitHub Personal Access Token (config-repo 접근용, `repo` scope 필요)

## 로컬 실행

### 1. 환경변수 설정

`.env.example`을 참고하여 IntelliJ Run Configuration에 다음 환경변수를 추가하세요:

| 변수명 | 설명 |
|---|---|
| `GIT_USERNAME` | GitHub 아이디 |
| `GIT_PASSWORD` | GitHub Personal Access Token (`repo` scope) |
| `CONFIG_SERVER_USERNAME` | config-server 접근용 사용자 이름 |
| `CONFIG_SERVER_PASSWORD` | config-server 접근용 비밀번호 |
| `ENCRYPT_KEY` | 설정값 암호화/복호화용 마스터 키 |

> 💡 `ENCRYPT_KEY` 생성: `openssl rand -base64 32`

### 2. 프로파일 설정

Active profiles에 `local` 지정

### 3. 실행 및 검증

앱 실행 후 인증 정보를 포함하여 요청:

```bash
# 인증 없이 → 401 Unauthorized
curl -i http://localhost:8888/queue-service/local

# 인증 포함 → 200 OK
curl -u $CONFIG_SERVER_USERNAME:$CONFIG_SERVER_PASSWORD \
http://localhost:8888/queue-service/local
```

JSON 응답에 config-repo의 설정값이 포함되면 정상 동작.

## 🔐 Basic Auth

config-server의 모든 엔드포인트는 Basic Auth로 보호됩니다.
인증 없이 접근 시 `401 Unauthorized` 응답이 반환됩니다.

### 클라이언트 서비스 연동

각 클라이언트 서비스의 `application.yml`에 다음 설정을 추가하세요:

```yaml
spring:
  config:
    import: "optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}"
  cloud:
    config:
      username: ${CONFIG_SERVER_USERNAME}
      password: ${CONFIG_SERVER_PASSWORD}
```

클라이언트 서비스도 동일한 환경변수(`CONFIG_SERVER_USERNAME`, `CONFIG_SERVER_PASSWORD`)를 주입받아야 config-server에 접근할 수 있습니다.

## 🔑 설정값 암호화

DB 비밀번호, API 키 등 민감한 설정값은 **암호화하여** config-repo에 저장합니다.
config-server가 `ENCRYPT_KEY`로 자동 복호화하여 클라이언트에 평문으로 전달합니다.

### 동작 원리

```
[config-repo]
password: '{cipher}5d4fc8f6...'       ← 암호화된 상태로 Git 저장
↓
[config-server가 ENCRYPT_KEY로 자동 복호화]
↓
[클라이언트 서비스]
password: "qwer1234"                  ← 평문으로 수신
```

### 암호화 절차

1. config-server의 `/encrypt` 엔드포인트로 평문을 암호문으로 변환:

```bash
curl -u $CONFIG_SERVER_USERNAME:$CONFIG_SERVER_PASSWORD \
-X POST http://localhost:8888/encrypt \
-d "평문값"
```

2. 응답받은 암호문을 config-repo의 yml에 `{cipher}` 접두어와 함께 저장:

```yaml
spring:
  datasource:
  password: '{cipher}응답받은 암호문'
```

⚠️ **반드시 작은따옴표(`'`)로 감싸야 합니다.**

3. config-repo에 커밋/푸시

4. 클라이언트 서비스는 재시작 시 자동으로 평문 값 수신

### 복호화 테스트

암호문이 올바른지 확인:

```bash
curl -u $CONFIG_SERVER_USERNAME:$CONFIG_SERVER_PASSWORD \
-X POST http://localhost:8888/decrypt \
-d "암호문"
```

### 주의사항

- ❌ 평문 민감정보를 config-repo에 커밋 금지
- ❌ `ENCRYPT_KEY`를 코드/yml/Git에 작성 금지
- ❌ `{cipher}` 접두어 없이 암호문만 작성 금지
- ✅ 민감정보는 반드시 암호화 후 저장
- ✅ `ENCRYPT_KEY`는 환경변수로만 관리

## 프로파일

| 프로파일 | 용도 | 포트 |
|---|---|---|
| `local` | 로컬 개발 (IntelliJ 직접 실행) | 8888 |
| `prod` | 운영/컨테이너 환경 | 8080 |

> 💡 컨테이너 내부 포트는 모든 서비스 8080으로 통일됩니다 (팀 컨벤션).
> Docker로 실행 시 `-p 8888:8080`으로 매핑하면 외부에서는 8888로 접근 가능합니다.

## 관련 레포

- [config-repo](https://github.com/first-ticket/config-repo) — 이 서버가 읽어오는 설정 파일 저장소
