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
  cloud:
    config:
      uri: ${CONFIG_SERVER_URI:http://localhost:8888}
      username: ${CONFIG_SERVER_USERNAME}
      password: ${CONFIG_SERVER_PASSWORD}
```

클라이언트 서비스도 동일한 환경변수(`CONFIG_SERVER_USERNAME`, `CONFIG_SERVER_PASSWORD`)를 주입받아야 config-server에 접근할 수 있습니다.

## 프로파일

| 프로파일 | 용도 | 포트 |
|---|---|---|
| `local` | 로컬 개발 (IntelliJ 직접 실행) | 8888 |
| `prod` | 운영/컨테이너 환경 | 8080 |

> 💡 컨테이너 내부 포트는 모든 서비스 8080으로 통일됩니다 (팀 컨벤션).
> Docker로 실행 시 `-p 8888:8080`으로 매핑하면 외부에서는 8888로 접근 가능합니다.

## 관련 레포

- [config-repo](https://github.com/first-ticket/config-repo) — 이 서버가 읽어오는 설정 파일 저장소
