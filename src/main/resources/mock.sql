BEGIN;

-- ============================================================
-- NETTOYAGE OPTIONNEL
-- ============================================================
-- ATTENTION :
-- Cette partie supprime les données existantes.
-- Décommente-la uniquement si tu veux repartir de zéro.
--
-- TRUNCATE TABLE image_maquette, ligne_commande, commande,
-- fiche_mesure, client RESTART IDENTITY CASCADE;


-- ============================================================
-- 1. CLIENTS
-- ============================================================
-- 20 clients
--
-- 1  - 5  : HOMME
-- 6  - 10 : FEMME
-- 11 - 15 : ENFANT
-- 16 - 20 : ADO
-- ============================================================

INSERT INTO clients (
    nom,
    prenom,
    telephone,
    email,
    genre,
    notes_morphologie
)
SELECT

    -- --------------------------------------------------------
    -- NOM
    -- --------------------------------------------------------
    CASE i
        WHEN 1  THEN 'DUPONT'
        WHEN 2  THEN 'MARTIN'
        WHEN 3  THEN 'BERNARD'
        WHEN 4  THEN 'THOMAS'
        WHEN 5  THEN 'ROBERT'

        WHEN 6  THEN 'PETIT'
        WHEN 7  THEN 'DURAND'
        WHEN 8  THEN 'LEROY'
        WHEN 9  THEN 'MOREAU'
        WHEN 10 THEN 'SIMON'

        WHEN 11 THEN 'LAURENT'
        WHEN 12 THEN 'LEFEBVRE'
        WHEN 13 THEN 'MICHEL'
        WHEN 14 THEN 'GARCIA'
        WHEN 15 THEN 'DAVID'

        WHEN 16 THEN 'BERTRAND'
        WHEN 17 THEN 'ROUX'
        WHEN 18 THEN 'VINCENT'
        WHEN 19 THEN 'FOURNIER'
        WHEN 20 THEN 'MOREL'
        END,

    -- --------------------------------------------------------
    -- PRENOM
    -- --------------------------------------------------------
    CASE i
        WHEN 1  THEN 'Alexandre'
        WHEN 2  THEN 'Jean'
        WHEN 3  THEN 'Pierre'
        WHEN 4  THEN 'Thomas'
        WHEN 5  THEN 'Nicolas'

        WHEN 6  THEN 'Marie'
        WHEN 7  THEN 'Sophie'
        WHEN 8  THEN 'Camille'
        WHEN 9  THEN 'Emma'
        WHEN 10 THEN 'Sarah'

        WHEN 11 THEN 'Lucas'
        WHEN 12 THEN 'Chloé'
        WHEN 13 THEN 'Nathan'
        WHEN 14 THEN 'Léa'
        WHEN 15 THEN 'Hugo'

        WHEN 16 THEN 'Paul'
        WHEN 17 THEN 'Laura'
        WHEN 18 THEN 'Louis'
        WHEN 19 THEN 'Inès'
        WHEN 20 THEN 'Mathis'
        END,

    -- --------------------------------------------------------
    -- TELEPHONE UNIQUE
    -- --------------------------------------------------------
    '+2305' || LPAD((100000 + i)::text, 6, '0'),

    -- --------------------------------------------------------
    -- EMAIL
    -- --------------------------------------------------------
    'client' || i || '@luxecouture.com',

    -- --------------------------------------------------------
    -- GENRE
    -- --------------------------------------------------------
    CASE
        WHEN i BETWEEN 1 AND 5 THEN 'HOMME'
        WHEN i BETWEEN 6 AND 10 THEN 'FEMME'
        WHEN i BETWEEN 11 AND 15 THEN 'ENFANT'
        ELSE 'ADO'
        END,

    -- --------------------------------------------------------
    -- NOTES MORPHOLOGIE
    -- --------------------------------------------------------
    CASE
        WHEN i BETWEEN 1 AND 5 THEN
            'Silhouette masculine standard, épaules équilibrées'

        WHEN i BETWEEN 6 AND 10 THEN
            'Silhouette féminine standard, taille légèrement marquée'

        WHEN i BETWEEN 11 AND 15 THEN
            'Morphologie enfant, proportions adaptées à la croissance'

        ELSE
            'Morphologie adolescent, proportions en évolution'
        END

FROM generate_series(1, 20) AS i;


-- ============================================================
-- 2. FICHES DE MESURE
-- ============================================================
-- 2 fiches par client
-- 20 x 2 = 40 fiches
--
-- Les mesures sont stockées dans la colonne JSONB "mesures".
-- ============================================================

INSERT INTO fiches_mesure (
    nom_projet,
    mesures,
    client_id,
    remarques_specifiques,
    date_prise
)
SELECT

    -- --------------------------------------------------------
    -- NOM DU PROJET
    -- --------------------------------------------------------
    CASE fiche.numero
        WHEN 1 THEN 'Costume sur mesure'
        WHEN 2 THEN 'Tenue de cérémonie'
        END,

    -- --------------------------------------------------------
    -- MESURES JSONB
    -- --------------------------------------------------------
    CASE

        -- ==========================
        -- PREMIERE FICHE
        -- ==========================
        WHEN fiche.numero = 1 THEN

            CASE

                -- HOMME
                WHEN c.genre = 'HOMME' THEN
                    jsonb_build_object(
                            'tourPoitrine', 98.0 + c.id,
                            'tourTaille', 84.0 + c.id,
                            'tourHanches', 100.0 + c.id,
                            'longueurVeste', 72.0,
                            'longueurManche', 62.0,
                            'tourCou', 39.0,
                            'largeurEpaules', 44.0,
                            'longueurPantalon', 105.0
                    )

                -- FEMME
                WHEN c.genre = 'FEMME' THEN
                    jsonb_build_object(
                            'tourPoitrine', 88.0 + c.id,
                            'tourTaille', 70.0 + c.id,
                            'tourHanches', 96.0 + c.id,
                            'longueurRobe', 145.0,
                            'longueurManche', 60.0,
                            'tourCou', 34.0,
                            'largeurEpaules', 39.0
                    )

                -- ENFANT
                WHEN c.genre = 'ENFANT' THEN
                    jsonb_build_object(
                            'tourPoitrine', 65.0 + c.id,
                            'tourTaille', 60.0 + c.id,
                            'tourHanches', 68.0 + c.id,
                            'longueurVeste', 50.0,
                            'longueurManche', 45.0,
                            'tourCou', 29.0,
                            'largeurEpaules', 31.0
                    )

                -- ADO
                ELSE
                    jsonb_build_object(
                            'tourPoitrine', 78.0 + c.id,
                            'tourTaille', 68.0 + c.id,
                            'tourHanches', 82.0 + c.id,
                            'longueurVeste', 62.0,
                            'longueurManche', 55.0,
                            'tourCou', 33.0,
                            'largeurEpaules', 37.0
                    )

                END

        -- ==========================
        -- DEUXIEME FICHE
        -- ==========================
        ELSE

            CASE

                -- HOMME
                WHEN c.genre = 'HOMME' THEN
                    jsonb_build_object(
                            'tourPoitrine', 100.0 + c.id,
                            'tourTaille', 86.0 + c.id,
                            'tourHanches', 102.0 + c.id,
                            'longueurVeste', 74.0,
                            'longueurManche', 63.0,
                            'tourCou', 40.0,
                            'largeurEpaules', 45.0,
                            'longueurPantalon', 106.0
                    )

                -- FEMME
                WHEN c.genre = 'FEMME' THEN
                    jsonb_build_object(
                            'tourPoitrine', 90.0 + c.id,
                            'tourTaille', 72.0 + c.id,
                            'tourHanches', 98.0 + c.id,
                            'longueurRobe', 147.0,
                            'longueurManche', 61.0,
                            'tourCou', 35.0,
                            'largeurEpaules', 40.0
                    )

                -- ENFANT
                WHEN c.genre = 'ENFANT' THEN
                    jsonb_build_object(
                            'tourPoitrine', 67.0 + c.id,
                            'tourTaille', 62.0 + c.id,
                            'tourHanches', 70.0 + c.id,
                            'longueurVeste', 52.0,
                            'longueurManche', 46.0,
                            'tourCou', 30.0,
                            'largeurEpaules', 32.0
                    )

                -- ADO
                ELSE
                    jsonb_build_object(
                            'tourPoitrine', 80.0 + c.id,
                            'tourTaille', 70.0 + c.id,
                            'tourHanches', 84.0 + c.id,
                            'longueurVeste', 64.0,
                            'longueurManche', 56.0,
                            'tourCou', 34.0,
                            'largeurEpaules', 38.0
                    )

                END

        END,

    -- --------------------------------------------------------
    -- CLIENT
    -- --------------------------------------------------------
    c.id,

    -- --------------------------------------------------------
    -- REMARQUES
    -- --------------------------------------------------------
    CASE fiche.numero
        WHEN 1 THEN
            'Première prise de mesures du client'
        WHEN 2 THEN
            'Mise à jour des mesures après essayage'
        END,

    -- --------------------------------------------------------
    -- DATE DE PRISE
    -- --------------------------------------------------------
    CURRENT_TIMESTAMP
        - ((fiche.numero * 30 + c.id) || ' days')::interval

FROM clients c

         CROSS JOIN (
    SELECT 1 AS numero
    UNION ALL
    SELECT 2 AS numero
) fiche;


-- ============================================================
-- 3. COMMANDES
-- ============================================================
-- 5 commandes par client
--
-- Chaque client possède les 5 statuts :
--
-- 1 → CREEE
-- 2 → EN_CONFECTION
-- 3 → ESSAYAGE
-- 4 → TERMINEE
-- 5 → ANNULEE
--
-- 20 clients x 5 commandes = 100 commandes
-- ============================================================

INSERT INTO commandes (
    reference,
    date_commande,
    date_livraison,
    cout_total,
    statut,
    client_id
)
SELECT

    -- --------------------------------------------------------
    -- REFERENCE UNIQUE
    -- --------------------------------------------------------
    'CMD-2026-' ||
    LPAD(
            ((c.id - 1) * 5 + s.numero)::text,
            5,
            '0'
    ),

    -- --------------------------------------------------------
    -- DATE COMMANDE
    -- --------------------------------------------------------
    CURRENT_TIMESTAMP
        - ((s.numero * 10 + c.id) || ' days')::interval,

    -- --------------------------------------------------------
    -- DATE LIVRAISON
    -- --------------------------------------------------------
            CURRENT_DATE
        + ((s.numero * 7 + c.id) || ' days')::interval,

    -- --------------------------------------------------------
    -- COUT TOTAL
    -- Calculé après insertion des lignes
    -- --------------------------------------------------------
    0.00,

    -- --------------------------------------------------------
    -- STATUT
    -- --------------------------------------------------------
    s.statut,

    -- --------------------------------------------------------
    -- CLIENT
    -- --------------------------------------------------------
    c.id

FROM clients c

         CROSS JOIN (
    VALUES
        (1, 'CREEE'),
        (2, 'EN_CONFECTION'),
        (3, 'ESSAYAGE'),
        (4, 'TERMINEE'),
        (5, 'ANNULEE')
) AS s(numero, statut);


-- ============================================================
-- 4. LIGNES DE COMMANDE
-- ============================================================
-- 5 lignes par commande
--
-- 100 commandes x 5 lignes = 500 lignes
-- ============================================================

INSERT INTO ligne_commande (
    commande_id,
    nom_maquette,
    quantite,
    prix_confection,
    sous_total
)
SELECT

    c.id,

    CASE ligne.numero
        WHEN 1 THEN 'Veste sur mesure'
        WHEN 2 THEN 'Pantalon sur mesure'
        WHEN 3 THEN 'Chemise premium'
        WHEN 4 THEN 'Gilet élégant'
        WHEN 5 THEN 'Cravate personnalisée'
        END,

    ligne.numero,

    CASE ligne.numero
        WHEN 1 THEN 450.00
        WHEN 2 THEN 300.00
        WHEN 3 THEN 180.00
        WHEN 4 THEN 250.00
        WHEN 5 THEN 80.00
        END,

    ligne.numero *
    CASE ligne.numero
        WHEN 1 THEN 450.00
        WHEN 2 THEN 300.00
        WHEN 3 THEN 180.00
        WHEN 4 THEN 250.00
        WHEN 5 THEN 80.00
        END

FROM commandes c

         CROSS JOIN (
    SELECT 1 AS numero
    UNION ALL
    SELECT 2 AS numero
    UNION ALL
    SELECT 3 AS numero
    UNION ALL
    SELECT 4 AS numero
    UNION ALL
    SELECT 5 AS numero
) ligne;


-- ============================================================
-- 5. CALCUL DU COUT TOTAL DES COMMANDES
-- ============================================================
-- coutTotal = SUM(sousTotal des lignes)
-- ============================================================

UPDATE commandes c
SET cout_total = (
    SELECT COALESCE(
                   SUM(l.sous_total),
                   0.00
           )
    FROM ligne_commande l
    WHERE l.commande_id = c.id
);


-- ============================================================
-- 6. IMAGES DE MAQUETTE
-- ============================================================
-- 2 images par ligne :
--
-- 1 → Face
-- 2 → Dos
--
-- 500 lignes x 2 images = 1000 images
-- ============================================================

INSERT INTO images_maquette (
    ligne_commande_id,
    url,
    description
)
SELECT

    l.id,

    CASE image.numero
        WHEN 1 THEN
            'https://placehold.co/800x1000/png?text=Maquette-Face-' ||
            l.id

        WHEN 2 THEN
            'https://placehold.co/800x1000/png?text=Maquette-Dos-' ||
            l.id
        END,

    CASE image.numero
        WHEN 1 THEN 'Face'
        WHEN 2 THEN 'Dos'
        END

FROM ligne_commande l

         CROSS JOIN (
    SELECT 1 AS numero
    UNION ALL
    SELECT 2 AS numero
) image;


-- ============================================================
-- VALIDATION
-- ============================================================

-- Nombre de clients
SELECT
    'CLIENTS' AS table_name,
    COUNT(*) AS total
FROM clients;


-- Nombre de fiches de mesure
SELECT
    'FICHES_MESURE' AS table_name,
    COUNT(*) AS total
FROM fiches_mesure;


-- Nombre de commandes
SELECT
    'COMMANDES' AS table_name,
    COUNT(*) AS total
FROM commandes;


-- Nombre de lignes
SELECT
    'LIGNES_COMMANDE' AS table_name,
    COUNT(*) AS total
FROM ligne_commande;


-- Nombre d'images
SELECT
    'IMAGES_MAQUETTE' AS table_name,
    COUNT(*) AS total
FROM images_maquette;


-- ============================================================
-- VERIFICATION DES GENRES
-- ============================================================

SELECT
    genre,
    COUNT(*) AS nombre_clients
FROM clients
GROUP BY genre
ORDER BY genre;


-- ============================================================
-- VERIFICATION DES STATUTS
-- ============================================================

SELECT
    statut,
    COUNT(*) AS nombre_commandes
FROM commandes
GROUP BY statut
ORDER BY statut;


-- ============================================================
-- VERIFICATION PAR CLIENT
-- ============================================================

SELECT
    c.id,
    c.nom,
    c.prenom,
    c.genre,

    COUNT(DISTINCT co.id) AS nombre_commandes,

    COUNT(DISTINCT fm.id) AS nombre_fiches_mesure,

    COUNT(DISTINCT lc.id) AS nombre_lignes,

    COUNT(DISTINCT im.id) AS nombre_images

FROM clients c

         LEFT JOIN commandes co
                   ON co.client_id = c.id

         LEFT JOIN fiches_mesure fm
                   ON fm.client_id = c.id

         LEFT JOIN ligne_commande lc
                   ON lc.commande_id = co.id

         LEFT JOIN images_maquette im
                   ON im.ligne_commande_id = lc.id

GROUP BY
    c.id,
    c.nom,
    c.prenom,
    c.genre

ORDER BY c.id;


COMMIT;