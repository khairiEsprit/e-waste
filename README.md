  E-Waste - Collecte Intelligente des Déchets
  
    Une plateforme web développée avec Symfony 6.4 pour optimiser la gestion des déchets électroniques grâce à des technologies modernes comme l'IA, la blockchain et la reconnaissance faciale.
    
    Explorer la documentation »
    
    Voir la démo
    ·
    Signaler un bug
    ·
    Suggérer une fonctionnalité
  




  Tableau des Matières
  
    À propos du Projet
    Fonctionnalités Principales
    Technologies Utilisées
    
      Installation
      
        Prérequis
        Étapes
      
    
    Configuration
    Utilisation
    API Intégrées
    Feuille de Route
    Contribuer
    Licence
    Contact
    Remerciements
  



📦 À propos du Projet

E-Waste est une application web innovante basée sur Symfony 6.4, conçue pour révolutionner la collecte des déchets électroniques. Elle offre une gestion complète des utilisateurs, centres, poubelles, tâches, avis, événements et demandes, tout en intégrant des technologies avancées comme la reconnaissance faciale, la blockchain et des APIs tierces pour une expérience utilisateur optimale.
(retour en haut)


🌟 Fonctionnalités Principales

Gestion des Utilisateurs : Inscription, connexion et gestion des rôles (admin, employé, utilisateur).
Sign In with Google : Authentification rapide et sécurisée via Google.
Reconnaissance Faciale : Sécurité renforcée pour les accès sensibles.
Gestion des Centres : Administration des centres avec contrats numériques.
Gestion des Poubelles : Détection automatique du niveau de remplissage via capteurs.
Gestion des Tâches : Assignation et suivi des tâches pour les employés.
Gestion des Avis : Système d’évaluation interactif pour le site web.
Gestion des Événements : Planification et suivi des collectes ou campagnes de sensibilisation.
Gestion des Demandes : Traitement des demandes avec détection des mots inappropriés via IA.
Blockchain : Transactions sécurisées avec le token E-Waste Token (EWT).

(retour en haut)


🛠️ Technologies Utilisées

Framework : Symfony 6.4
Base de Données : MySQL
Frontend : Twig, HTML5, CSS3, JavaScript
APIs :
Mailing (Notifications par email)
Mapbox (Cartes interactives)
OpenWeatherMap (Données météo)
Leaflet Map (Visualisation cartographique)
Open Router AI (IA pour tâches et détection)
Gemini (Analyse intelligente)
Événements en ligne (Planification d'événements)
Blockchain (E-Waste Token - EWT)


Autres : Reconnaissance faciale (Python/ML), détection de niveau des poubelles (capteurs IoT)

(retour en haut)


🚀 Installation
Prérequis

PHP 8.1+
Composer
Node.js & NPM
MySQL
Comptes et clés API pour :
Google OAuth
Mapbox
OpenWeatherMap
Open Router AI
Gemini
Blockchain (EWT)
Mailing



Installez les outils nécessaires :
composer self-update
npm install npm@latest -g

Étapes

Cloner le Repository :
git clone https://github.com/khairiEsprit/e-waste_symfony.git
cd e-waste


Installer les Dépendances :
composer install
npm install


Configurer l’Environnement :Copiez .env.example en .env et ajustez les variables (base de données, clés API).
cp .env.example .env


Créer la Base de Données :
php bin/console doctrine:database:create
php bin/console doctrine:migrations:migrate


Compiler les Assets :
npm run dev


Lancer le Serveur :
symfony server:start



(retour en haut)


⚙️ Configuration
Fichiers Clés

.env : Variables d’environnement (connexion DB, clés API).
config/services.yaml : Configuration des services Symfony.
config/packages/security.yaml : Paramètres de sécurité et authentification.

Clés API
Ajoutez les clés suivantes dans .env :

Google OAuth (GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET)
Mapbox (MAPBOX_ACCESS_TOKEN)
OpenWeatherMap (OPENWEATHERMAP_API_KEY)
Open Router AI (OPENROUTER_API_KEY)
Gemini (GEMINI_API_KEY)
Blockchain EWT (EWT_API_KEY)
Mailing (MAILER_DSN)

Exemple :
GOOGLE_CLIENT_ID=your_google_client_id
MAPBOX_ACCESS_TOKEN=your_mapbox_token

(retour en haut)


🖥️ Utilisation

Authentification : Connexion via email, Google Sign-In ou reconnaissance faciale.
Gestion des Poubelles : Surveillance en temps réel du niveau de remplissage et assignation des tâches.
Événements : Planification de collectes ou campagnes de sensibilisation.
Avis : Soumission, modération et consultation des avis sur le site.
Demandes : Gestion des demandes avec filtrage automatique des mots inappropriés.
Tableaux de Bord : Interfaces intuitives avec cartes interactives (Mapbox/Leaflet) pour visualiser les centres et poubelles.

Pour plus de détails, consultez la documentation.
(retour en haut)


🔗 API Intégrées

Mailing : Envoi de notifications par email pour les utilisateurs et administrateurs.
Mapbox & Leaflet : Affichage de cartes interactives pour localiser les poubelles et centres.
OpenWeatherMap : Données météo pour optimiser les collectes.
Open Router AI & Gemini : Analyse intelligente pour la détection d’anomalies et le traitement des demandes.
Blockchain (EWT) : Gestion des transactions et récompenses via le token E-Waste.
Événements en Ligne : Planification et promotion d’événements.

(retour en haut)


🛤️ Feuille de Route

 Gestion des utilisateurs et authentification (Google, reconnaissance faciale)
 Détection du niveau des poubelles
 Intégration blockchain (EWT)
 Planification des événements
 Ajout de notifications push en temps réel
 Support multilingue
 Français
 Anglais
 Espagnol


 Analyse de données avec tableaux de bord avancés
 Intégration IoT pour capteurs supplémentaires

Voir les issues ouvertes pour plus de détails.
(retour en haut)


🤝 Contribuer
Nous accueillons les contributions ! Suivez ces étapes :

Forkez le projet :git clone https://github.com/khairiEsprit/e-waste_symfony.git


Créez une branche pour votre fonctionnalité :git checkout -b feature/NouvelleFonctionnalité


Commitez vos modifications :git commit -m 'Ajout de NouvelleFonctionnalité'


Poussez la branche :git push origin feature/NouvelleFonctionnalité


Ouvrez une Pull Request sur GitHub.

(retour en haut)


📜 Licence
Distribué sous la licence MIT. Voir LICENSE pour plus d’informations.
(retour en haut)


📬 Contact
Equipe_E-waste - @E-Waste - contact@e-waste.com
Lien du Projet : https://github.com/khairiEsprit/e-waste_symfony.git
(retour en haut)


🙏 Remerciements

Symfony Documentation
Mapbox API
Leaflet.js
OpenWeatherMap
Choose an Open Source License
GitHub Emoji Cheat Sheet
Shields.io

(retour en haut)


E-Waste : Pour une collecte plus intelligente et un avenir durable ! 🌍✨
