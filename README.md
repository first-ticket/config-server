# config-server

MSA 서비스들의 중앙 설정을 제공하는 Spring Cloud Config Server입니다.

## 요구사항
- GitHub Personal Access Token (config-repo 접근용, `repo` scope 필요)

## 로컬 실행

### 1. 환경변수 설정
IntelliJ Run Configuration에 다음 환경변수를 추가하세요:
- `GIT_USERNAME`: GitHub 아이디
- `GIT_PASSWORD`: GitHub Personal Access Token

### 2. 프로파일 설정
Active profiles에 `local` 지정

### 3. 실행 및 검증
앱 실행 후:

```bash
curl http://localhost:8888/queue-service/local
```

JSON 응답에 config-repo의 설정값이 포함되면 정상 동작

## 프로파일
- `local`: 로컬 개발 (포트 8888)
- `prod`: 운영/컨테이너 (포트 8080)

## 관련 레포
- [config-repo](https://github.com/first-ticket/config-repo) — 이 서버가 읽어오는 설정 파일 저장소