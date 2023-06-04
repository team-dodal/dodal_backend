# java 환경 구성
FROM openjdk:11
# build 시점 JAR 파일명 설정
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} ./dodal.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=dev", "./dodal.jar"]