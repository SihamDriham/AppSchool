create database school1;
use school1;

create table Etudiant(
	id int primary key auto_increment,
    nom varchar(50),
    prenom varchar(50),
    ville varchar(50),
    sexe varchar(20),
    photo varchar(50)
);

INSERT INTO Etudiant (nom, prenom, ville, sexe) VALUES
('DRIHAM', 'Siham', 'Safi', 'F'),
('LOUMANI', 'Amal', 'Rabat', 'F');

drop table Etudiant;