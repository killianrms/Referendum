# 🗳️ Système de Vote Électronique Sécurisé

## 📋 Description

Système de vote électronique haute sécurité pour la gestion de référendums d'entreprise. Cette solution garantit l'anonymat des votes grâce au chiffrement homomorphe et aux preuves à divulgation nulle de connaissance (Zero-Knowledge Proofs).

### ✨ Fonctionnalités principales

- 🔐 **Chiffrement homomorphe** : Anonymat total des votes
- 🛡️ **Preuves Zero-Knowledge** : Validation cryptographique de la validité des votes
- 🔒 **Communications SSL/TLS** : Sécurisation de tous les échanges
- 👥 **Multi-rôles** : Admin, Employé (votant), Scrutateur
- 🖥️ **Interface JavaFX** : Interface utilisateur moderne et intuitive
- 🤖 **Chatbot FAQ intégré** : Assistant intelligent avec IA DeepSeek

## 🚀 Architecture

Le système suit une architecture trois-tiers sécurisée :

```
┌─────────────────┐     SSL/TLS      ┌─────────────────┐
│   Client GUI    │ ◄──────────────► │     Serveur     │
│   (JavaFX)      │                  │   Multi-thread  │
└─────────────────┘                  └────────┬────────┘
                                              │
┌─────────────────┐                           │
│  Scrutateur GUI │                           ▼
│   (JavaFX)      │                  ┌─────────────────┐
└─────────────────┘                  │  Base Oracle    │
                                     └─────────────────┘
```

### 🔧 Technologies utilisées

- **Backend** : Java 21, Sockets SSL, Threads
- **Frontend** : JavaFX 20.0.1, FXML
- **Sécurité** : Chiffrement El Gamal
- **Base de données** : Oracle
- **Build** : Maven
- **Containerisation** : Docker & Docker Compose
- **Tests** : JUnit 5, TestFX

## 👥 Équipe de développement

| Rôle                 | Membre           |
|----------------------|------------------|
| 🎯 **Scrum Master**  | Maël NICOLAS     |
| 📊 **Product Owner** | Killian RAMUS    |
| 💻 **Développeur**   | Cyprien BONS     |
| 💻 **Développeur**   | Raphaël RIVAS    |

## 📦 Installation

### Prérequis

- Java 21 ou supérieur
- Maven 3.8+
- Docker & Docker Compose (pour le serveur)
- Oracle Database (ou instance distante)

### Configuration

1. Cloner le repository :
```bash
git clone git@gitlabinfo.iutmontp.univ-montp2.fr:sae-referendum/projet-referendum.git
```

2. Créer le fichier `.env` à la racine :
```env
socketmdp=votre_mot_de_passe_ssl
adresse=localhost
port=3390
# Paramètres de base de données
DB_URL=jdbc:oracle:thin:@localhost:1521:XE
DB_USER=votre_user
DB_PASSWORD=votre_password
```

3. Compiler le projet :
```bash
mvn clean package -DskipTests
```

## 🚀 Démarrage rapide

### 1. Lancer le serveur (Docker)
```bash
docker-compose up -d
```

### 2. Lancer l'application client
```bash
java --module-path /'cheminvers'/javafx-sdk-'Version'/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media -jar target/Projet-referendum-Client.jar
```

### 3. Lancer l'application scrutateur
```bash
java --module-path /'cheminvers'/javafx-sdk-'Version'/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media -jar target/Projet-referendum-Scrutateur.jar
```

## 📖 Documentation

- [Notice d'installation détaillée](INSTALLATION.md)

## 🧪 Tests

Exécuter la suite de tests complète :
```bash
mvn test
```

Les tests incluent :
- Tests d'intégration complets
- Tests de sécurité (détection de falsification)
- Tests d'interface utilisateur (TestFX)

## 🔒 Sécurité

- Chiffrement de bout en bout des votes
- Authentification forte par bcrypt
- Preuves cryptographiques de validité
- Audit trail complet
- Protection contre la falsification