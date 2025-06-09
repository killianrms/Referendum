# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Java-based secure referendum voting system with client-server architecture, using JavaFX for UI and Oracle Database for persistence. The system uses SSL/TLS encryption for all communications.

## Build Commands

```bash
# Compile the project
mvn compile

# Package into JAR files (creates 3 JARs: Client, Server, Scrutateur)
mvn package

# Clean and rebuild
mvn clean package
```

## Running the Applications

```bash
# 1. Start the server (requires .env configuration and keystore.jks)
java -jar target/Projet-referendum-Serveur.jar

# 2. Run the client application (for voters)
java -jar target/Projet-referendum-Client.jar

# 3. Run the scrutineer application (for vote monitoring)
java -jar target/Projet-referendum-Scrutateur.jar
```

## Architecture

The system follows a three-tier architecture:

1. **Server** (`fr.iut.referendum.Serveur`)
   - Main server class: `Serveur.java`
   - Handles client connections via `ServerThread.java`
   - Manages referendums through `Referendum.java`
   - Uses SSL/TLS with `keystore.jks`

2. **Client Application** (`fr.iut.referendum.Client`)
   - Entry point: `MainClient.java`
   - JavaFX views for voter interaction
   - Secure communication with server

3. **Scrutineer Application** (`fr.iut.referendum.Scrutateur`)
   - Entry point: `MainScrutateur.java`
   - JavaFX views for vote monitoring
   - Admin capabilities for referendum management

## Key Components

- **Database**: Oracle Database connection via `ConnexionBD.java` (singleton pattern)
- **Security**: BCrypt password hashing (`MotDePasse.java`), SSL/TLS encryption
- **Cryptography**: Vote encryption using `Crypto.java` with BigInteger operations
- **Configuration**: Environment variables loaded via `EnvLoader.java` from `.env` file

## Required Configuration

The `.env` file must contain:
- `dburl`, `dblogin`, `dbpassword` - Oracle database credentials
- `port`, `adresse` - Server network configuration
- `socketmdp` - Keystore password for SSL
- `poivre` - Salt for additional security

## Database Schema

Key tables:
- `Employes` - Employee/voter records
- `Referendums` - Referendum definitions
- `Voter` - Vote records
- `Scrutateurs` - Scrutineer accounts

## Development Notes

- Java 21 required
- JavaFX for UI (included via Maven)
- No automated tests currently implemented
- Legal documents in `src/main/Légal/`