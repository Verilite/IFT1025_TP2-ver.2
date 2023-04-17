package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import javafx.util.Pair;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import server.models.Course;
import server.models.RegistrationForm;

/**
 * Notre classe Server qui va être utilisé pour lancer notre serveur.
 * Elle contient les différents variables qui doivent être utilisés pour appeler et communiquer avec le serveur.
 */
public class Server {

    public final static String REGISTER_COMMAND = "INSCRIRE";
    public final static String LOAD_COMMAND = "CHARGER";
    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;
    
    /** Des déclarations bonus qui n'était pas dans le code initial donné. */
    private static final Object Serrure_RWFiles = new Object();
    private static boolean mode_DemoBonus = true;

    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents);
    }

    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Partie du code qui lance le programme.
     * En faisant les tâches du serveur dans un Thread séparé pour chaque utilisateur, on peux générer plusieurs
     * utilisateurs en même temps. Cependant, il faut faire attention à ne pas écrire/lire du même fichier en même temps
     * car ceci peux causer des erreurs tels que: corruption des données, perte de données, etc.
     * Ceci peux être résolu en utilisant un Lock qui fait que certaines parties du code ne peuvent s'exécuter qu'en
     * série et non en parallèle.
     */
    public void run() {
        while (true) {
            try {
                client = server.accept();

                Thread thread_bonus = new Thread(() -> {
                    try {
                        System.out.println("Connecté au client: " + client);
                        objectInputStream = new ObjectInputStream(client.getInputStream());
                        objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                        listen();
                        disconnect();
                        System.out.println("Client déconnecté!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                
                thread_bonus.start();
               
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void listen() throws IOException, ClassNotFoundException {
        String line = this.objectInputStream.readObject().toString();
        if (line != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);
        }
    }

    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1, parts.length));
        return new Pair<>(cmd, args);
    }

    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * JAVADOC FOURNI AU DÉBUT:
     * Lire un fichier texte contenant des informations sur les cours et les
     * transofmer en liste d'objets 'Course'.La méthode filtre les cours par la session spécifiée en argument.
     * Ensuite, elle renvoie la liste des cours pour une session au client en
     * utilisant l'objet 'objectOutputStream'.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du
     * fichier ou de l'écriture de l'objet dans le flux.
     * 
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     * @throws java.io.FileNotFoundException
     */
    public void handleLoadCourses(String arg) {
        List<Course> liste_Cours = new ArrayList<Course>();

        /** On lit les cours de la session sélectionnée. */
        try {
            
            synchronized(Serrure_RWFiles) {

                BufferedReader reader = new BufferedReader(new FileReader("IFT1025-TP2-server-main/src/main/java/server/data/cours.txt"));

                String line;
                while ((line = reader.readLine()) != null) {

                    String[] line_split = line.split("\t");

                    if (line_split[2].equals(arg)) {
                        liste_Cours.add(new Course(line_split[1], line_split[0], line_split[2]));
                    }
                }

                reader.close();
            }
            
        } catch (Exception e) {
            System.out.println("Erreur lors de la lecture du Fichier : \n\n" + e.getMessage());
            return;
        }

        /**
         * On envoie la liste en JSON au client.
         * PARTIE BONUS: en mode démonstration bonus, le server se bloque dans une boucle infinie
         * dès qu'il a fini de répondre à une commande 'Charger'.
         * Observation: malgré la présence de la boucle infinie, le serveur ne bloquera pas et continueras à servir
         * ses clients parce qu'il va utiliser des Threads séparées pour générer chaque commande d'un client.
         * Par contre, la RAM va prendre cher...
         */
        try {
            objectOutputStream.writeObject(liste_Cours);

            if (mode_DemoBonus)
                try {
                    while (true)
                        Thread.sleep(1000);
                } catch (Exception e) { }
            
        } catch (Exception e) {
            System.out.println("Erreur lors de l'envois de la Liste des Objets : \n\n" + e.getMessage());
            return;
        }
    }

    /**
     * JAVADOC DÉJÀ FOURNI AU DÉBUT:
     * Récupérer l'objet 'RegistrationForm' envoyé par le client en utilisant
     * 'objectInputStream', l'enregistrer dans un fichier texte
     * et renvoyer un message de confirmation au client.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture de
     * l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {

        /** On récupère l'objet 'RegistrationForm' envoyé par le client. */
        RegistrationForm registrationForm = null;
        try {
            registrationForm = (RegistrationForm)objectInputStream.readObject();

        } catch (Exception e) {
            System.out.println("Erreur lors de la réception de l'objet 'registrationForm' : \n\n" + e.getMessage());
            return;
        }

        /**
         * On rajoute 'RegistrationForm' au fichier texte.
         * On crée le fichier s'il n'existe pas déjà. Sinon, on charge le fichier et on ajoute la ligne au fichier.
         */
        try {
            
            synchronized(Serrure_RWFiles) {
                File file = new File("IFT1025-TP2-server-main/src/main/java/server/data/inscription.txt");
                if (!file.exists())
                    file.createNewFile();

                BufferedReader reader = new BufferedReader(new FileReader("IFT1025-TP2-server-main/src/main/java/server/data/inscription.txt"));
                String fileCache = "";
                String line;
                while ((line = reader.readLine()) != null)
                    fileCache += line + "\n";

                FileWriter fileWriter = new FileWriter ("IFT1025-TP2-server-main/src/main/java/server/data/inscription.txt");
                fileWriter.write(fileCache +
                                        registrationForm.getCourse().getSession() + "\t" +
                                        registrationForm.getCourse().getCode() + "\t" +
                                        registrationForm.getMatricule() + "\t" +
                                        registrationForm.getPrenom() + "\t" +
                                        registrationForm.getNom() + "\t" +
                                        registrationForm.getEmail());
                fileWriter.close();
            }
                        
        } catch (Exception e) {
            System.out.println("Erreur lors de la sauvegarde du Fichier 'RegistrationForm' : \n\n" + e.getMessage());
            return;
        }

        /**On envoie un message de confirmation au client. */
        try {
            objectOutputStream.writeObject("Félicitations! Inscription réussie de " + registrationForm.getPrenom() +
                    " au cours " + registrationForm.getCourse().getCode() +
                    " de la session de " + registrationForm.getCourse().getSession() + ".");

        } catch (Exception e) {
            System.out.println("Erreur lors de l'envois du message de confirmation au client' : \n\n" + e.getMessage());
        }

    }
}
