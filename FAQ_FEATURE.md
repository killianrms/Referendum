# FAQ Chatbot Feature

## Description

Un système de FAQ interactif sous forme de chatbot a été ajouté au système de vote par référendum. Ce chatbot fournit une assistance contextuelle adaptée à chaque type d'utilisateur.

## Fonctionnalités

### Pour tous les utilisateurs :
- Interface de chat intuitive avec suggestions rapides
- Réponses adaptées au rôle de l'utilisateur
- Questions communes sur la sécurité, les erreurs, etc.

### Pour les Employés/Votants :
- Comment voter
- Voir les résultats
- Gestion des problèmes de connexion
- Comprendre la sécurité du vote

### Pour les Scrutateurs :
- Gestion des clés publiques
- Création et chargement de fichiers de sécurisation
- Processus de déchiffrement des résultats
- Format du mot de passe (16 caractères)

### Pour les Administrateurs :
- Création de référendums
- Gestion des utilisateurs et scrutateurs
- Format des dates et heures
- Suppression de référendums

## Utilisation

1. Cliquez sur le bouton vert "FAQ / Aide" disponible dans chaque interface
2. Une fenêtre de chat s'ouvre avec des suggestions de questions
3. Tapez votre question ou cliquez sur une suggestion
4. Le chatbot répond instantanément avec des informations pertinentes

## Architecture technique

- `FAQChatbot.java` : Moteur du chatbot avec base de connaissances
- `VueFAQChatbot.java` : Interface JavaFX du chat
- `faqChatbot.fxml` : Layout FXML de l'interface

Le chatbot utilise une recherche par mots-clés pour identifier les questions et fournir des réponses appropriées selon le rôle de l'utilisateur.