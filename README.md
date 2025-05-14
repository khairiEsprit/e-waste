# 🌍 E-Waste - Collecte Intelligente des Déchets

[![Licence MIT](https://img.shields.io/badge/Licence-MIT-blue.svg)](LICENSE)
[![Symfony 6.4](https://img.shields.io/badge/Symfony-6.4-green.svg)](https://symfony.com)
[![Contributions bienvenues](https://img.shields.io/badge/Contributions-Bienvenues-brightgreen.svg)](https://github.com/khairiEsprit/e-waste_symfony)

**Un logiciel desktop innovant** développé avec **Symfony 6.4** pour optimiser la gestion des déchets électroniques grâce à des technologies modernes comme l’IA, la blockchain et la reconnaissance faciale.

🔗 **[Télécharger le logiciel](#)** | **[Explorer la documentation](#)**  
🐞 [Signaler un bug](#) | 💡 [Suggérer une fonctionnalité](#)

---

## 📑 Tableau des Matières

- [À propos du Projet](#-à-propos-du-projet)
- [Fonctionnalités Principales](#-fonctionnalités-principales)
- [Technologies Utilisées](#-technologies-utilisées)
- [Installation](#-installation)
  - [Prérequis](#prérequis)
  - [Étapes](#étapes)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [API Intégrées](#-api-intégrées)
- [Feuille de Route](#-feuille-de-route)
- [Contribuer](#-contribuer)
- [Licence](#-licence)
- [Contact](#-contact)
- [Remerciements](#-remerciements)

---

## 📦 À propos du Projet

**E-Waste** est un logiciel desktop révolutionnaire basé sur **Symfony 6.4**, conçu pour transformer la collecte des déchets électroniques. Il permet une gestion complète des utilisateurs, centres, poubelles, tâches, avis, événements et demandes, tout en intégrant des technologies avancées comme la **reconnaissance faciale**, la **blockchain** et des **APIs tierces** pour une expérience utilisateur fluide et sécurisée.

> 🌱 **Objectif** : Rendre la gestion des déchets électroniques plus intelligente et durable.

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🌟 Fonctionnalités Principales

| Fonctionnalité | Description |
| --- | --- |
| 👤 **Gestion des Utilisateurs** | Inscription, connexion et gestion des rôles (admin, employé, utilisateur). |
| 🔐 **Sign In with Google** | Authentification rapide et sécurisée via Google. |
| 😊 **Reconnaissance Faciale** | Sécurité renforcée pour les accès sensibles. |
| 🏢 **Gestion des Centres** | Administration des centres avec contrats numériques. |
| 🗑️ **Gestion des Poubelles** | Détection automatique du niveau de remplissage via capteurs IoT. |
| 📋 **Gestion des Tâches** | Assignation et suivi des tâches pour les employés. |
| ⭐ **Gestion des Avis** | Système d’évaluation interactif intégré au logiciel. |
| 📅 **Gestion des Événements** | Planification et suivi des collectes ou campagnes de sensibilisation. |
| 📩 **Gestion des Demandes** | Traitement des demandes avec détection des mots inappropriés via IA. |
| 🔗 **Blockchain** | Transactions sécurisées avec le token **E-Waste Token (EWT)**. |

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🛠️ Technologies Utilisées

- **Framework** : Symfony 6.4
- **Base de Données** : SQLite (installation locale simplifiée) ou MySQL
- **Frontend** : Twig, HTML5, CSS3, JavaScript (via Electron)
- **APIs Intégrées** :
  - 📧 **Mailing** : Notifications par email
  - 🗺️ **Mapbox & Leaflet** : Cartes interactives
  - ☁️ **OpenWeatherMap** : Données météo
  - 🤖 **Open Router AI & Gemini** : Analyse intelligente
  - 📅 **Événements en ligne** : Planification d’événements
  - 🔗 **Blockchain** : E-Waste Token (EWT)
- **Autres** :
  - 😊 Reconnaissance faciale (Python/ML)
  - 🗑️ Détection de niveau des poubelles (capteurs IoT)
  - 💻 Electron (interface desktop)

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🚀 Installation

### Prérequis

- 🖥️ **Système** : Windows, macOS ou Linux
- 🐘 **PHP** : 8.1+
- 📦 **Composer**
- 🌐 **Node.js & NPM**
- 🗄️ **Base de Données** : SQLite ou MySQL
- 🔑 **Clés API** :
  - Google OAuth
  - Mapbox
  - OpenWeatherMap
  - Open Router AI
  - Gemini
  - Blockchain (EWT)
  - Mailing

Installez les outils nécessaires :

```bash
composer self-update
npm install npm@latest -g
```

### Étapes

1. **Cloner le Repository** :

   ```bash
   git clone https://github.com/khairiEsprit/e-waste_symfony.git
   cd e-waste
   ```

2. **Installer les Dépendances** :

   ```bash
   composer install
   npm install
   ```

3. **Configurer l’Environnement** :

   ```bash
   cp .env.example .env
   ```

   Modifiez `.env` pour inclure les paramètres de la base de données et les clés API.

4. **Créer la Base de Données** :

   Pour SQLite (recommandé) :

   ```bash
   php bin/console doctrine:database:create
   php bin/console doctrine:migrations:migrate
   ```

   Pour MySQL, configurez `.env` et exécutez les mêmes commandes.

5. **Compiler les Assets** :

   ```bash
   npm run build
   ```

6. **Lancer le Logiciel** :

   ```bash
   npm run electron:start
   ```

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## ⚙️ Configuration

### Fichiers Clés

- 📄 `.env` : Variables d’environnement (base de données, clés API)
- 📄 `config/services.yaml` : Configuration des services Symfony
- 📄 `config/packages/security.yaml` : Paramètres de sécurité et authentification

### Clés API

Ajoutez les clés dans `.env` :

```env
GOOGLE_CLIENT_ID=your_google_client_id
MAPBOX_ACCESS_TOKEN=your_mapbox_token
OPENWEATHERMAP_API_KEY=your_openweathermap_key
OPENROUTER_API_KEY=your_openrouter_key
GEMINI_API_KEY=your_gemini_key
EWT_API_KEY=your_ewt_key
MAILER_DSN=your_mailer_dsn
```

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🖥️ Utilisation

- 🔐 **Authentification** : Connexion via email, Google Sign-In ou reconnaissance faciale.
- 🗑️ **Gestion des Poubelles** : Surveillance en temps réel et assignation des tâches.
- 📅 **Événements** : Planification de collectes ou campagnes.
- ⭐ **Avis** : Soumission et modération des avis dans l’interface.
- 📩 **Demandes** : Gestion avec filtrage IA des mots inappropriés.
- 📊 **Tableaux de Bord** : Visualisation des centres et poubelles via Mapbox/Leaflet.

📖 Consultez la **documentation intégrée** pour plus de détails.

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🔗 API Intégrées

- 📧 **Mailing** : Notifications par email.
- 🗺️ **Mapbox & Leaflet** : Localisation des poubelles et centres.
- ☁️ **OpenWeatherMap** : Optimisation des collectes avec données météo.
- 🤖 **Open Router AI & Gemini** : Détection d’anomalies et traitement des demandes.
- 🔗 **Blockchain (EWT)** : Gestion des transactions et récompenses.
- 📅 **Événements en Ligne** : Planification et promotion.

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🛤️ Feuille de Route

- ✅ Gestion des utilisateurs et authentification
- ✅ Détection du niveau des poubelles
- ✅ Intégration blockchain (EWT)
- ✅ Planification des événements
- ⏳ Notifications push en temps réel
- ⏳ Support multilingue (🇫🇷 Français, 🇬🇧 Anglais, 🇪🇸 Espagnol)
- ⏳ Tableaux de bord analytiques avancés
- ⏳ Intégration IoT supplémentaire

🔍 Voir les [issues ouvertes](https://github.com/khairiEsprit/e-waste_symfony/issues).

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🤝 Contribuer

Nous adorons les contributions ! Voici comment participer :

1. 🍴 **Forkez le projet** :

   ```bash
   git clone https://github.com/khairiEsprit/e-waste_symfony.git
   ```

2. 🌿 **Créez une branche** :

   ```bash
   git checkout -b feature/NouvelleFonctionnalité
   ```

3. 💾 **Commitez vos modifications** :

   ```bash
   git commit -m 'Ajout de NouvelleFonctionnalité'
   ```

4. 🚀 **Poussez la branche** :

   ```bash
   git push origin feature/NouvelleFonctionnalité
   ```

5. 📬 **Ouvrez une Pull Request** sur GitHub.

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 📜 Licence

Distribué sous la **[licence MIT](LICENSE)**.

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 📬 Contact

**Equipe E-Waste**  
📧 Email : [contact@e-waste.com](mailto:contact@e-waste.com)  
🐦 Twitter : [@E-Waste](#)  
🌐 Projet : [github.com/khairiEsprit/e-waste_symfony](https://github.com/khairiEsprit/e-waste_symfony)

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

## 🙏 Remerciements

- 📚 [Symfony Documentation](https://symfony.com/doc)
- 🗺️ [Mapbox API](https://docs.mapbox.com)
- 🗺️ [Leaflet.js](https://leafletjs.com)
- ☁️ [OpenWeatherMap](https://openweathermap.org)
- 📜 [Choose an Open Source License](https://choosealicense.com)
- 😄 [GitHub Emoji Cheat Sheet](https://github.com/ikatyang/emoji-cheat-sheet)
- 🛡️ [Shields.io](https://shields.io)

⬆️ *[retour en haut](#e-waste---collecte-intelligente-des-déchets)*

---

**E-Waste** : Pour une collecte plus intelligente et un avenir durable ! 🌍✨