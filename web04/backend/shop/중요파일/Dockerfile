# Java 21 실행 환경
FROM openjdk:21-slim

# 컨테이너 내 작업 디렉토리
WORKDIR /app

# 현재 폴더의 shop.jar를 컨테이너 안으로 app.jar라는 이름으로 복사
COPY shop.jar app.jar

# application.yml에 설정된 8214 포트 개방
EXPOSE 8214

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]