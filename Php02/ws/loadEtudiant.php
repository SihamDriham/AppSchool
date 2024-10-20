<?php
error_reporting(E_ALL);
ini_set('display_errors', 1);

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once '../racine.php';
    include_once RACINE . '/service/EtudiantService.php';
    loadAll();
}

function loadAll() {
    $es = new EtudiantService();
    header('Content-type: application/json; charset=utf-8'); // Ajout de l'en-tête pour le codage

    try {
        $etudiants = $es->findAllApi();

        if (is_array($etudiants)) {
            if (empty($etudiants)) {
                echo json_encode([]); // Retourne un tableau vide
            } else {
                echo json_encode($etudiants); // Retourne les données des étudiants
            }
        } else {
            echo json_encode([
                "error" => "Données non valides renvoyées par le service."
            ]);
        }
    } catch (Exception $e) {
        echo json_encode([
            "error" => "Une erreur est survenue lors de la récupération des étudiants : " . $e->getMessage()
        ]);
    }
}