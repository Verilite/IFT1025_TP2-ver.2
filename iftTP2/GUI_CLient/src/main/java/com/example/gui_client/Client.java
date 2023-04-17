package com.example.gui_client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;


/**
 * Notre classe principale du client. On charge les cours du serveur d'ici, ainsi qu'envoyer les demandes
 * d'inscription au serveur.
 */
public class Client {
    /**
     * La partie du client qui demande au serveur de charger ce qui est disponible dans la liste de cours.
     * Si la connexion est un succès, on enregistre les informations nécessaires aux variables locaux et
     * ferme la connexion.
     * @param Session String qui correspond à la session choisie. (Automne, Hiver, Été)
     * @return true si la connexion est un succès, false si une exception est attrapée.
     */
    public boolean client_ChargerLesCours(String Session) {

        try {
            Socket socket = new Socket("localhost", 1337);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            /** Envoi de la commande au serveur */
            String commande = "CHARGER " + Session;
            outputStream.writeObject(commande);

            /** Réception de la réponse du serveur et on l'affiche. */
            List<Course> reponse = (List<Course>)inputStream.readObject();

            ObservableList<String[]> data = FXCollections.observableArrayList();
            for (int i = 0; i < reponse.size(); i++)
                data.add(new String[]{reponse.get(i).getCode(), reponse.get(i).getName()});

            HelloApplication.tableau_CodeCours.setItems(data);

            /** Fermeture des connexions */
            outputStream.close();
            inputStream.close();
            socket.close();

            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * La partie du client qui envoie les informations de l'inscription au serveur.
     * @param prenom Le prénom de l'élève (String)
     * @param nom Le nom de l'élève (String)
     * @param email Le courriel de l'élève (String)
     * @param matricule La matricule de l'élève (String)
     * @param cours Le cours choisi par l'élève. (Course, contient les infos comme code de cours, nom de cours, etc.)
     * @return un String qui indique la réponse du serveur après avoir envoyé l'inscription.
     *         Si une erreur est attrapée, on affiche l'erreur.
     */
    public String client_EnvoyerInscription(String prenom, String nom, String email, String matricule, Course cours){

        try{
            Socket socket = new Socket("localhost", 1337);
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());

            RegistrationForm formulaire = new RegistrationForm(prenom, nom, email, matricule, cours);

            /** Envoi de la commande au serveur */
            String commande = "INSCRIRE";
            outputStream.writeObject(commande);
            outputStream.writeObject(formulaire);

            /** Réception de la réponse du serveur et on l'affiche. */
            String reponse = inputStream.readObject().toString();
            System.out.println(reponse);

            /** Fermeture des connexions */
            outputStream.close();
            inputStream.close();
            socket.close();

            return reponse;

        } catch (Exception e){
            return e.getMessage();
        }

    }

}
