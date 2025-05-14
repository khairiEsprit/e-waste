-- Table pour les poubelles (existante, ajoutée ici pour référence)
CREATE TABLE IF NOT EXISTS poubelle (
    id INT PRIMARY KEY AUTO_INCREMENT,
    adresse VARCHAR(255) NOT NULL,
    niveau INT DEFAULT 0,
    hauteur_totale INT NOT NULL,
    etat ENUM('FONCTIONNEL', 'NON_FONCTIONNEL', 'EN_REPARATION', 'HORS_SERVICE') DEFAULT 'FONCTIONNEL',
    date_installation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_centre INT,
    FOREIGN KEY (id_centre) REFERENCES centre(id)
);

-- Table pour les capteurs de distance (renommée de l'existante)
CREATE TABLE IF NOT EXISTS capteur (
    id_c INT PRIMARY KEY AUTO_INCREMENT,
    poubelle_id INT NOT NULL,
    distance_mesuree DOUBLE NOT NULL,
    date_mesure TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    portee_maximale DOUBLE DEFAULT 150.0,
    precision_capteur DOUBLE DEFAULT 0.1,
    FOREIGN KEY (poubelle_id) REFERENCES poubelle(id)
);

-- Table pour les capteurs de pression (nouvelle)
CREATE TABLE IF NOT EXISTS capteur_pression (
    id_cp INT PRIMARY KEY AUTO_INCREMENT,
    poubelle_id INT NOT NULL,
    poids_mesure DOUBLE NOT NULL,
    date_mesure TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    precision_capteur DOUBLE DEFAULT 0.1,
    en_service BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (poubelle_id) REFERENCES poubelle(id)
);

-- Table pour l'historique des opérations
CREATE TABLE IF NOT EXISTS historique_poubelle (
    id INT PRIMARY KEY AUTO_INCREMENT,
    poubelle_id INT NOT NULL,
    date_operation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type_operation ENUM('REMPLISSAGE', 'VIDAGE', 'PANNE', 'REPARATION') NOT NULL,
    poids_variation DOUBLE,
    niveau_avant INT,
    niveau_apres INT,
    description TEXT,
    FOREIGN KEY (poubelle_id) REFERENCES poubelle(id)
);
