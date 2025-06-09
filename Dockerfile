FROM openjdk:21-jdk
WORKDIR /app
COPY target/Projet-referendum-Serveur.jar .
COPY .env .
COPY keystore.jks .
CMD ["java", "-jar", "Projet-referendum-Serveur.jar"]