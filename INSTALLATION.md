# 📚 Notice d'Installation - Système de Vote Électronique

## 📋 Table des matières

1. [Prérequis](#-prérequis)
2. [Installation du Serveur](#-installation-du-serveur---projet-referendum_serveur)
3. [Installation du Client/Admin](#-installation-du-clientadmin---projet-referendum_client)
4. [Installation du Scrutateur](#-installation-du-scrutateur---projet-referendum_scrutateur)

## 🔧 Prérequis

### Logiciels requis

- **Java 21** ou supérieur ([Télécharger](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.8+** ([Télécharger](https://maven.apache.org/download.cgi))
- **Docker & Docker Compose** (pour le serveur uniquement) ([Télécharger](https://www.docker.com/get-started))

### Vérifier les installations

```bash
# Vérifier Java
java --version

# Vérifier Maven
mvn --version

# Vérifier Docker (pour le serveur)
docker --version
docker-compose --version
```

### (Utilisation des 3 projets différents : 'Projet-referendum_Serveur', 'Projet-referendum_Client', 'Projet-referendum_Scrutateur')

## 🖥️ Installation du Serveur - [Projet-referendum_Serveur](https://gitlabinfo.iutmontp.univ-montp2.fr/sae-referendum/projet-referendum_serveur)

### Option 1 : Avec Docker (Recommandé)

1. **Compiler le projet**
   ```bash
   mvn clean package
   ```

2. **Lancer le serveur virtualisé**
   ```bash
   docker-compose up -d
   ```

3. **Vérifier que le serveur est actif**
   ```bash
   docker ps
   # Vous devriez voir le conteneur "projet-referendum-serveur"
   ```

4. **Consulter les logs**
   ```bash
   docker logs projet-referendum-serveur
   ```

5. **Arrêter le serveur**
   ```bash
   docker-compose down
   ```

### Option 2 : Sans Docker

1. **Compiler et lancer directement**
   ```bash
   mvn clean package
   java -jar target/Projet-referendum-Serveur.jar
   ```

## 👤 Installation du Client/Admin - [Projet-referendum_Client](https://gitlabinfo.iutmontp.univ-montp2.fr/sae-referendum/projet-referendum_client)

Le client et l'admin partagent la même interface. Les permissions sont gérées selon le compte utilisé.

### Option 1 : Via JAR compilé

1. **Compiler le projet**
   ```bash
   mvn clean package
   ```

2. **Exécuter le JAR**

   **lancer directement**
   ```bash
   java --module-path /'cheminvers'/javafx-sdk-'Version'/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media -jar target/Projet-referendum-Client.jar
   ```

### Option 2 : Via IDE

1. **Ouvrir le projet dans votre IDE** (IntelliJ IDEA, Eclipse, VSCode)

2. **Naviguer vers le fichier principal**
   ```
   src/main/java/fr/iut/referendum/Client/MainClient.java
   ```

3. **Exécuter la classe** `MainClient` (clic droit → Run)

## 🔍 Installation du Scrutateur - [Projet-referendum_Scrutateur](https://gitlabinfo.iutmontp.univ-montp2.fr/sae-referendum/projet-referendum_scrutateur)

### Option 1 : Via JAR compilé

1. **Compiler le projet**
   ```bash
   mvn clean package
   ```

2. **Exécuter le JAR**

   **lancer directement**
   ```bash
   java --module-path /'cheminvers'/javafx-sdk-'Version'/lib --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base,javafx.media -jar target/Projet-referendum-Scrutateur.jar
   ```

### Option 2 : Via IDE

1. **Ouvrir le projet dans votre IDE**

2. **Naviguer vers le fichier principal**
   ```
   src/main/java/fr/iut/referendum/Scrutateur/MainScrutateur.java
   ```

3. **Exécuter la classe** `MainScrutateur`

## 📝 Configuration

### Fichier .env

Créez un fichier `.env` à la racine du projet avec les paramètres suivants :

```env
# Configuration SSL
socketmdp=VotreMotDePasseSSL

# Configuration Serveur
adresse=localhost
port=3390

# Configuration Base de données
DB_URL=jdbc:oracle:thin:@localhost:1521:XE
DB_USER=referendum_user
DB_PASSWORD=VotreMotDePasse

# Configuration API (optionnel)
DEEPSEEK_API_KEY=VotreCleAPI
```

### Certificat SSL

Le fichier `keystore.jks` doit être présent à la racine du projet. Si absent :

```bash
keytool -genkey -alias serveur -keyalg RSA -keystore keystore.jks -keysize 2048
```

## 🚀 Ordre de démarrage

1. **Serveur** : Doit être lancé en premier
2. **Client/Admin** : Peut être lancé une fois le serveur actif
3. **Scrutateur** : Peut être lancé indépendamment