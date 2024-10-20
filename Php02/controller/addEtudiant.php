<?php

   include_once '../racine.php';
   include_once RACINE.'/service/EtudiantService.php';

   extract($_POST);

   $photo=$_FILES['photo'];
	echo $photo['name']."<br>".$photo['tmp_name'];
   $filename=$photo['tmp_name'];
   $extension= pathinfo($photo['name'], PATHINFO_EXTENSION);
	$destination = $_SERVER['DOCUMENT_ROOT'] . "/Php02/images/$nom.$extension";
	move_uploaded_file($filename,$destination);

   $es = new EtudiantService();
   $es->create(new Etudiant(1, $nom, $prenom, $ville, $sexe, $destination));

   header("location:../index.php");

?>