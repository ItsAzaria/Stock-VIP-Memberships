FROM gradle:6.7.0-jdk15 AS build
COPY --chown=gradle:gradle . /memberships
WORKDIR /memberships
RUN gradle shadowJar --no-daemon

FROM openjdk:8-jre-slim
RUN mkdir /config/
COPY --from=build /memberships/build/libs/*.jar /

ENTRYPOINT ["java", "-jar", "/Memberships.jar"]