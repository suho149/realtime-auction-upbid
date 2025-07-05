# 1. 베이스 이미지 선택 (JDK 17 포함)
FROM openjdk:17-jdk-slim

# 2. JAR 파일이 위치할 디렉토리 생성
WORKDIR /app

# 3. Gradle 빌드 결과물(JAR 파일)을 이미지 안으로 복사
# build.gradle에서 JAR 파일명을 'app.jar'로 통일하면 관리가 편합니다.
COPY build/libs/*.jar app.jar

# 4. 컨테이너가 시작될 때 실행할 명령어 지정
ENTRYPOINT ["java", "-jar", "app.jar"]

# 5. (선택사항) 애플리케이션이 사용하는 포트 명시
EXPOSE 8080