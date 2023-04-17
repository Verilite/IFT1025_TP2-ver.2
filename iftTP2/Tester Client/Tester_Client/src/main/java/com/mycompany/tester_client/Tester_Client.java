package com.mycompany.tester_client;


import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import server.models.Course;
import server.models.RegistrationForm;

/**
 * Cette classe est pour tester le client si ça fonctionne bien sans utiliser javaFX.
 * Elle vérifie si les cours sont bien chargés et les réponses bien enregistrées.
 */
public class Tester_Client {

    //Déclarations Globales
    public static Scanner scanner;
    public static List<Course> loadedCourseList;
    
    public static void main(String[] args) {
        
        //Initialisation
        scanner = new Scanner(System.in);
        loadedCourseList = new ArrayList<Course>();
        
        System.out.println("*** Bienvenue au portail d'inscription de cours de l'UDEM ***");

        while (true){
            System.out.print("Veuillez choisir la session pour la quelle vous voulez consulter la liste des cours :\n" +
                    "1. Automne\n" +
                    "2. Hiver\n" +
                    "3. Ete\n" +
                    "> Choix :");

            /** Fonction 1: on détermine quoi montrer selon la session choisie. */
            switch (scanner.nextLine()) {
                case "1":
                    F1("Automne");
                    break;
                case "2":
                    F1("Hiver");
                    break;
                case "3":
                    F1("Ete");
                    break;
                default:
                    throw new IllegalStateException("Valeur non-valide: " + scanner.nextLine());
            }

            System.out.print("> Choix :\n" +
                             "1. Consulter les cours offerts pour une autre session\n" +
                             "2. Inscription à un Cours\n");

                             switch (scanner.nextLine()) {
                                 case "1":
                                     break;
                                 case "2":
                                     /** On répète fonction 2 jusqu'à ce que ceci réussie ou l'usager arrête le programme. */
                                     while (!F2())
                                         System.out.println("\nVeuillez réessayer :\n\n");
                                    
                                     return;
                            }
            }
                         
    }

    /**
     * Cette fonction est responsable pour le chargement des cours du serveur. On obtient les cours disponibles pour
     * une session choisie du serveur et on le montre dans le client. Si c'est un succès, on montre ce qui est là.
     * On ferme la connexion ensuite.
     * @param session La session choisie pour laquelle on va montrer les cours disponibles.
     */
    public static void F1(String session) {

        try {
            Socket socket = new Socket("localhost", 1337);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            String commande = "CHARGER " + session;

            outputStream.writeObject(commande);

            List<Course> answer = (List<Course>)inputStream.readObject();

            for (int i = 0; i < answer.size(); i++){
                System.out.println((i+1) + ". " + answer.get(i).getCode() + "    " + answer.get(i).getName());
                loadedCourseList.add(new Course(answer.get(i).getName(), answer.get(i).getCode(), answer.get(i).getSession()));
            }

            inputStream.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Erreur lors du chargement de la Session : '" + session + "'.");
        }
    
    }

    /**
     * Cette fonction vérifie si une réponse a été bien enregistrée. On récupère les renseignements nécessaires
     * de l'usager, trouve le cours sélectionné et envoie la commande au serveur ensuite. Si on reçoit une réponse,
     * on montre un message qui indique que c'est bien fait et on ferme les processus.
     * @return true si la réponse est bien enregistrée, false sinon.
     */
    public static boolean F2() {
        
    
        try {
            Socket socket = new Socket("localhost", 1337);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            System.out.print("Veuillez saisir votre prénom :"); String prenom = scanner.nextLine();
            System.out.print("Veuillez saisir votre nom :"); String nom = scanner.nextLine();
            System.out.print("Veuillez saisir votre email :"); String email = scanner.nextLine();
            System.out.print("Veuillez saisir votre matricule :"); String matricule = scanner.nextLine();
            System.out.print("Veuillez saisir le code du cours: "); String code = scanner.nextLine();

            Course selectedCourse = null;
            for (Course cours : loadedCourseList)
                if (cours.getCode().equals(code)){
                    selectedCourse = cours;
                    break;
                }

            if (selectedCourse == null){
                System.out.println("Vous n'avez chargé aucun cours avec le Code : '" + code + "'.");
                return false;
            }

            RegistrationForm formulaire = new RegistrationForm(prenom, nom, email, matricule, selectedCourse);

            String commande = "INSCRIRE";

            outputStream.writeObject(commande);
            outputStream.writeObject(formulaire);

            String reponse = inputStream.readObject().toString();
            System.out.println(reponse);

            outputStream.close();
            inputStream.close();
            socket.close();
            
            return true;

        } catch (Exception e) {
            System.out.println("Erreur lors de l'envoi du Formulaire d'inscription.");
            return false;
        }     
    }

}
