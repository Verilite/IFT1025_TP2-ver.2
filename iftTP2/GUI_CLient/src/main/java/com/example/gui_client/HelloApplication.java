package com.example.gui_client;

import javafx.beans.property.SimpleStringProperty;
import server.models.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

/**
 * Lance la fenêtre pour l'application dans un cours.
 * On affiche les cours disponibles pour la session choisie et l'étudiant pourra s'inscrire
 * dans un cours de son choix en mettant les informations pertinentes. Si les informations
 * sont mal entrées, on donne une indication disant que c'est pas bien entrée.
 */
public class HelloApplication extends Application {

    /**
     * Notre méthode main. Ceci lance notre programme.
     * @param args Notre argument, utilisé pour lancer notre programme.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Nos variables statiques. Les noms font référence à certains contrôles de notre UI.
     * On a les éléments pour le panneau gauche et le panneau droit, tels que (mais non limité à)
     * un bouton pour charger les cours, les champs pour entrer notre information, etc.
     */
    public static TableView<String[]> tableau_CodeCours;
    public static ComboBox<String> comboBox_Sessions;
    public static Button button_Charger;
    public static String sessionChargee;

    public static TextField textField_Prenom;
    public static TextField textField_Nom;
    public static TextField textField_Email;
    public static TextField textField_Matricule;
    public static Button button_Envoyer;

    /**
     * Ceci démarre notre affichage. Elle contient les codes nécessaires pour formatter le stage
     * selon les paramètres exigés. On ajoute aussi des actions qui sont exécutées lorsque certains
     * boutons sont cliqué.
     * @param primaryStage Le stage primaire de notre application.
     */
    @Override
    public void start(Stage primaryStage) {

        /** Partie 1. Création du panneau gauche. */
        VBox panneauGauche = new VBox();
        panneauGauche.setPadding(new Insets(10,-88,10,-88));
        panneauGauche.setAlignment(Pos.TOP_CENTER);
        panneauGauche.setSpacing(20);

        /** Titre du panneau gauche. */
        Label label_listeCours = new Label("Liste des Cours");
        label_listeCours.setFont(new Font(20));

        /** Tableau qui montre nos cours sous le format "code | cours". */
        tableau_CodeCours = new TableView<>();
        tableau_CodeCours.setMaxWidth(350);
        TableColumn<String[], String> colonne_Code = new TableColumn<>("Code"); colonne_Code.setPrefWidth(105);
        TableColumn<String[], String> colonne_Cours = new TableColumn<>("Cours"); colonne_Cours.setPrefWidth(245);

        colonne_Code.setCellValueFactory(cellData -> {
            String[] row = cellData.getValue();
            return new SimpleStringProperty(row[0]);
        });
        colonne_Cours.setCellValueFactory(cellData -> {
            String[] row = cellData.getValue();
            return new SimpleStringProperty(row[1]);
        });
        tableau_CodeCours.getColumns().addAll(colonne_Code, colonne_Cours);

        /** Séparateur blanc entre notre tableau et le ComboBox et Bouton */
        Rectangle sep0 = new Rectangle(374, 4);
        sep0.setFill(Color.WHITE);

        /** Grille qui va contenir notre ComboBox et le bouton */
        GridPane grille0 = new GridPane();
        grille0.setAlignment(Pos.CENTER);
        grille0.setHgap(30);
        grille0.setVgap(20);

        /** Notre ComboBox qui contient les choix des sessions */
        comboBox_Sessions = new ComboBox<>();
        comboBox_Sessions.getItems().addAll("Hiver", "Automne", "Ete");
        comboBox_Sessions.getSelectionModel().selectFirst();
        grille0.add(comboBox_Sessions, 0, 0);

        /** Bouton sélecteur pour charger les cours de la session choisie. */
        button_Charger = new Button("Charger");
        grille0.add(button_Charger, 1, 0);

        /** Ajout des éléments dans le panneau gauche. */
        panneauGauche.getChildren().addAll(label_listeCours, tableau_CodeCours, sep0, grille0);

        /** Partie 2: Création du panneau droit */
        VBox panneauDroit = new VBox();
        panneauDroit.setPadding(new Insets(10));
        panneauDroit.setAlignment(Pos.TOP_CENTER);
        panneauDroit.setSpacing(20);

        /** Titre du panneau droit */
        Label label_formulaire = new Label("Formulaire d'inscription");
        label_formulaire.setFont(new Font(20));

        /** Grille qui va contenir les champs pour le prénom, nom, email et matricule */
        GridPane grille1 = new GridPane();
        grille1.setAlignment(Pos.CENTER);
        grille1.setHgap(30);
        grille1.setVgap(4);

        /** Les champs de textes pour le prénom, nom, email et matricule */
        textField_Prenom = new TextField();
        textField_Nom = new TextField();
        textField_Email = new TextField();
        textField_Matricule = new TextField();

        /** Ajout des champs de textes avec leurs labels respectifs */
        grille1.add(new Label("Prénom"), 0, 0);
        grille1.add(textField_Prenom, 1, 0);

        grille1.add(new Label("Nom"), 0, 1);
        grille1.add(textField_Nom, 1, 1);

        grille1.add(new Label("Email"), 0, 2);
        grille1.add(textField_Email, 1, 2);

        grille1.add(new Label("Matricule"), 0, 3);
        grille1.add(textField_Matricule, 1, 3);

        /** Bouton pour envoyer notre information */
        button_Envoyer = new Button("Envoyer");
        button_Envoyer.setPrefWidth(100);

        /** Ajout des éléments dans le panneau droit */
        panneauDroit.getChildren().addAll(label_formulaire, grille1, button_Envoyer);

        /** Partie 3: initialisation de notre layout principal */
        HBox layoutPrincipal = new HBox(1);

        /** Séparateur blanc entre les deux panneaux */
        Rectangle sep1 = new Rectangle(4, 400);
        sep1.setFill(Color.WHITE);

        /** Ajout des panneaux droits et gauches au layout principal */
        HBox.setHgrow(panneauGauche, Priority.ALWAYS);
        HBox.setHgrow(panneauDroit, Priority.ALWAYS);
        layoutPrincipal.getChildren().addAll(panneauGauche, sep1, panneauDroit);

        /** Création de la scène avec le layout principal et une mise en place du ratio 2:1 */
        Scene scene = new Scene(layoutPrincipal, 800, 400);
        primaryStage.setTitle("Inscription UdeM");
        primaryStage.setScene(scene);
        primaryStage.show();

        /** Partie 4: Gestion des événements de notre GUI */
        /** 4.1: panneau gauche. On demande les cours de la session sélectionnée au serveur. */
        button_Charger.setOnAction(e -> {

            Client monClient = new Client();

            boolean repClient = monClient.client_ChargerLesCours(comboBox_Sessions.getValue().toString());

            if (!repClient)
                infoBox("Erreur lors du chargement de la liste des cours pour la session : '" + comboBox_Sessions.getValue().toString() + "'");
            else
                sessionChargee = comboBox_Sessions.getValue().toString();
        });

        /** 4.2: Panneau droit. On vérifie l'intégrité des inputs de l'usager ici selon ce qui est nécessaire,
         * tels que la sélection d'un cours, si le courriel est bien formatté, si le matricule est bon, etc.
         * Par la suite, on envoie la réponse au serveur.
         */
        button_Envoyer.setOnAction(e -> {

            Client monClient = new Client();
            String[] chosenCoursStr = tableau_CodeCours.getSelectionModel().getSelectedItem();

            if (chosenCoursStr == null){
                infoBox("Veuillez sélectionner un cours pour votre Inscription.");
                return;
            }
            Course chosenCours = new Course(chosenCoursStr[1], chosenCoursStr[0], sessionChargee);


            if (textField_Prenom.getText().isEmpty() || textField_Prenom.getText().isBlank()){
                infoBox("La case prénom ne peux pas être vide");
                return;
            }

            if (textField_Nom.getText().isEmpty() || textField_Nom.getText().isBlank()){
                infoBox("La case nom ne peux pas être vide");
                return;
            }

            if (!textField_Email.getText().matches("\\w+@\\w+\\.\\w{2,}")){
                infoBox("La case email doit contenir un email valide");
                return;
            }

            if (textField_Matricule.getText().length() != 6){
                infoBox("La Matricule doit être composée de 6 Chiffres.");
                return;
            }
            try {
                Integer.parseInt(textField_Matricule.getText());
            } catch (Exception e_){
                infoBox("La Matricule doit être composée de 6 Chiffres.");
                return;
            }

            String reponse = monClient.client_EnvoyerInscription(textField_Prenom.getText(), textField_Nom.getText(), textField_Email.getText(), textField_Matricule.getText(), chosenCours);
            if (reponse.isBlank() || reponse.isEmpty())
                infoBox("Erreur Serveur lors de l'inscription de l'élève.");
            else
                infoBox(reponse);

        });
    }

    /**
     * Ceci crée une fenêtre pop-up avec un message qu'on désire imprimer.
     * @param infoMessage C'est le String qu'on veut afficher dans la fenêtre.
     */
    public static void infoBox(String infoMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

}