import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Arrays;
import java.io.File;
import java.util.ArrayList;



/**
 * Vue du jeu du pendu
 */
public class Pendu extends Application {
    /**
     * modèle du jeu
     **/
    private MotMystere modelePendu;
    /**
     * Liste qui contient les images du jeu
     */
    private ArrayList<Image> lesImages;
    /**
     * Liste qui contient les noms des niveaux
     */    
    public List<String> niveaux;

    // les différents contrôles qui seront mis à jour ou consultés pour l'affichage
    /**
     * le dessin du pendu
     */
    private ImageView dessin;
    /**
     * le mot à trouver avec les lettres déjà trouvé
     */
    private Text motCrypte;
    /**
     * la barre de progression qui indique le nombre de tentatives
     */
    private ProgressBar pg;
    /**
     * le clavier qui sera géré par une classe à implémenter
     */
    private Clavier clavier;
    /**
     * le text qui indique le niveau de difficulté
     */
    private Text leNiveau;
    /**
     * le chronomètre qui sera géré par une clasee à implémenter
     */
    private Chronometre chrono;
    /**
     * le panel Central qui pourra être modifié selon le mode (accueil ou jeu)
     */
    private BorderPane panelCentral;
    /**
     * le bouton qui permet de (lancer ou relancer une partie
     */ 
    private Button bJouer;
    private  BorderPane fenetrePrincipale ; 
    private ToggleGroup groupeNiveaux;
    private Label labelTemps;

    /**
     * le bouton Paramètre / Engrenage
     */
    private Button boutonParametres;
    /**
     * le bouton Accueil / Maison
     */    
    private Button boutonMaison;

    /**
    
     * initialise les attributs (créer le modèle, charge les images, crée le chrono ...)
     */
    @Override
    public void init() {
        this.modelePendu = new MotMystere("src/mots.txt", 3, 10, MotMystere.FACILE, 10);
        this.lesImages = new ArrayList<Image>();
        this.chargerImages("./img");
        this.chrono = new Chronometre();    
        this.boutonParametres = new Button();
        this.boutonMaison = new Button();
        this.niveaux = Arrays.asList("Facile", "Moyen", "Difficile", "Expert");
        this.panelCentral = new BorderPane();
        
        
    }

    /**
     * @return  le graphe de scène de la vue à partir de methodes précédantes
     */
    private Scene laScene(){
        this.fenetrePrincipale = new BorderPane();
        fenetrePrincipale.setTop(this.titre());
        fenetrePrincipale.setCenter(this.panelCentral);
        return new Scene(fenetrePrincipale, 800, 1000);
    }

    /**
     * @return le panel contenant le titre du jeu
     */
    private Pane titre(){
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color:#B784A7; -fx-padding: 10px;");

        Label jeu = new Label("Jeu du Pendu");
        jeu.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #222;");
        borderPane.setLeft(jeu);

        // (Ré)initialisation des boutons pour éviter les problèmes d'affichage
        this.boutonMaison = new Button();
        this.boutonMaison.setGraphic(new ImageView(new Image("file:img/home.png", 32, 32, true, true)));
        this.boutonMaison.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        this.boutonMaison.setOnAction(e -> this.modeAccueil());

        this.boutonParametres = new Button();
        this.boutonParametres.setGraphic(new ImageView(new Image("file:img/parametres.png", 32, 32, true, true)));
        this.boutonParametres.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        this.boutonParametres.setOnAction(e -> this.modeParametres());

        Button info = new Button();
        Image infoImage = new Image("file:img/info.png");
        ImageView infoView = new ImageView(infoImage);
        infoView.setFitHeight(32);
        infoView.setFitWidth(32);
        info.setGraphic(infoView);
        info.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");

        HBox boutons = new HBox(10, this.boutonMaison, this.boutonParametres, info);
        boutons.setStyle("-fx-alignment: center-right;");
        borderPane.setRight(boutons);

        return borderPane;
    }

    // /**
     // * @return le panel du chronomètre
     // */
     private TitledPane leChrono(){
        // A implementer
         TitledPane res = new TitledPane();
         
         return res;
     }

    // /**
     // * @return la fenêtre de jeu avec le mot crypté, l'image, la barre
     // *         de progression et le clavier
     // */
     private BorderPane fenetreJeu(){
         BorderPane mid = new BorderPane();

         this.motCrypte = new Text("Mot à trouver : ");
         motCrypte.setText(this.modelePendu.getMotCrypte());

         VBox centre = new VBox();
         this.dessin = new ImageView(this.lesImages.get(0));
         this.pg = new ProgressBar(0.0);
         centre.getChildren().addAll(this.dessin, this.pg);

         this.labelTemps = new Label("Temps : 0:00");
         VBox aDroite = new VBox();
         int valniv = this.modelePendu.getNiveau();
         String niv = this.niveaux.get(valniv);
         this.leNiveau = new Text(niv);

         TitledPane chrono = this.leChrono();
         Button nvmot = new Button("Nouveau mot");
         aDroite.getChildren().addAll(leNiveau, chrono, labelTemps, nvmot);

         VBox bas = new VBox();
         this.clavier = new Clavier("ABCDEFGHIJKLMNOPQRSTUVWXYZ", new ControleurLettres(this.modelePendu, this));
         bas.getChildren().add(this.clavier);

         // Ajoute le mot crypté au-dessus du centre
         VBox centreAvecMot = new VBox();
         centreAvecMot.setSpacing(10);
         centreAvecMot.getChildren().addAll(this.motCrypte, centre);

         mid.setRight(aDroite);
         mid.setCenter(centreAvecMot);
         mid.setBottom(bas);

         return mid;
     }

    // /**
     // * @return la fenêtre d'accueil sur laquelle on peut choisir les paramètres de jeu
     // */
     private BorderPane fenetreAccueil(){
        HBox head = new HBox();
        head.setStyle("-fx-background-color:#B784A7; -fx-padding: 10px;");
        Label jeu = new Label();
        jeu.setText("Jeu du pendu ");

        this.bJouer= new Button("Lancer une partie");
        this.bJouer.setOnAction(new ControleurLancerPartie(this.modelePendu, this));

        ToggleGroup niv = new ToggleGroup();
        RadioButton facile = new RadioButton("Facile");
        RadioButton moyen = new RadioButton("Moyen");
        RadioButton difficile = new RadioButton("Difficile");
        RadioButton expert = new RadioButton("Expert");
        facile.setToggleGroup(niv);
        facile.setSelected(true); // par défaut le niveau facile est sélectionné
        moyen.setToggleGroup(niv);
        difficile.setToggleGroup(niv);
        expert.setToggleGroup(niv);

        this.groupeNiveaux = niv;

        VBox root = new VBox();
        root.getChildren().addAll(bJouer, facile, moyen, difficile, expert);
       
        BorderPane pane = new BorderPane();
        // Ne pas ajouter de pane.setTop(head);
        pane.setCenter(root);
        return pane;
       
    }
     

    /**
     * charge les images à afficher en fonction des erreurs
     * @param repertoire répertoire où se trouvent les images
     */
    private void chargerImages(String repertoire){
        for (int i=0; i<this.modelePendu.getNbErreursMax()+1; i++){
            File file = new File(repertoire+"/pendu"+i+".png");
            System.out.println(file.toURI().toString());
            this.lesImages.add(new Image(file.toURI().toString()));
        }
    }

    public void modeAccueil(){
        // Si une partie est en cours, demander confirmation avant de quitter
        if (this.modelePendu.partieEnCours()) {
            Alert alert = this.popUpPartieEnCours();
            alert.showAndWait();
            if (alert.getResult() != ButtonType.YES) {
                // L'utilisateur a annulé, on ne quitte pas la partie
                return;
            }
        }
        this.panelCentral = this.fenetreAccueil();
        this.fenetrePrincipale.setCenter(this.panelCentral);
        // Désactive le bouton maison dans le menu accueil
        this.boutonMaison.setDisable(true);
    }

    public void modeJeu(){
        this.panelCentral = this.fenetreJeu();
        this.fenetrePrincipale.setCenter(this.panelCentral);
        // Réactive le bouton maison quand on est en jeu
        this.boutonMaison.setDisable(false);
    }
    
    public void modeParametres(){
        // A implémenter
        
    }

    /** lance une partie */
    public void lancePartie(){
        int niveau = MotMystere.FACILE; // niveau par défaut
         if (this.groupeNiveaux != null && this.groupeNiveaux.getSelectedToggle() != null) {
        String choix = ((RadioButton)this.groupeNiveaux.getSelectedToggle()).getText();
        if (choix.equals("Facile")) {
        niveau = MotMystere.FACILE;
        } else if (choix.equals("Moyen")) {
        niveau = MotMystere.MOYEN;
        } else if (choix.equals("Difficile")) {
        niveau = MotMystere.DIFFICILE;
        } else if (choix.equals("Expert")) {
        niveau = MotMystere.EXPERT;
        }
    }
        this.modelePendu.setNiveau(niveau);
        this.modelePendu.setMotATrouver();
        this.chrono.resetTime();
        this.modeJeu();
        this.majAffichage();
        
    }

    /**
     * raffraichit l'affichage selon les données du modèle
     */
    public void majAffichage(){
        this.motCrypte.setText(this.modelePendu.getMotCrypte());
        int nombreErreursRealiser = this.modelePendu.getNbErreursMax()  - this.modelePendu.getNbErreursRestants();
        int imageIndex = Math.min(nombreErreursRealiser, this.lesImages.size() - 1);
        this.dessin.setImage(this.lesImages.get(imageIndex));    

        int nombreErreursMaximum = this.modelePendu.getNbErreursMax();
        double progression = (double) nombreErreursRealiser / nombreErreursMaximum ;
        this.pg.setProgress(progression);

        this.leNiveau.setText(this.niveaux.get(this.modelePendu.getNiveau()));

        this.clavier.majAffichage(this.modelePendu.getLettresEssayees());
        this.chrono.setTime(this.chrono.getTempsEcoule());

        // Pour afficher le temps en texte
        labelTemps.setText("Temps : " + chrono.getText());

        // Pour afficher le temps en millisecondes
        long temps = chrono.getTempsEcoule();

        // Afficher les popups de victoire/défaite si la partie vient de se terminer
        if (!this.modelePendu.partieEnCours()) {
            if (this.modelePendu.gagne()){
                this.popUpMessageGagne().showAndWait();
                this.modeAccueil();
            } else if (this.modelePendu.perdu()){
                this.popUpMessagePerdu().showAndWait();
                this.modeAccueil();
            }
        }
    }

    /**
     * accesseur du chronomètre (pour les controleur du jeu)
     * @return le chronomètre du jeu
     */
    public Chronometre getChrono(){
        return this.chrono; 
    }

    public Alert popUpPartieEnCours(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"La partie est en cours!\n Etes-vous sûr de l'interrompre ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Attention");
        return alert;
    }
        
    public Alert popUpReglesDuJeu(){
        // A implenter
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        return alert;
    }
    
    public Alert popUpMessageGagne(){
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);   
         alert.setTitle("Victoire");
        alert.setHeaderText("Vous avez gagné !");
        alert.setContentText("Le mot était : " + this.modelePendu.getMotATrouver());
        return alert;
    }
    
    public Alert popUpMessagePerdu(){
           
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dommage");
        alert.setHeaderText("Vous avez perdu !");
        alert.setContentText("Le mot a trouver était : " + this.modelePendu.getMotATrouver());
        return alert;
    }

    /**
     * créer le graphe de scène et lance le jeu
     * @param stage la fenêtre principale
     */
    @Override
    public void start(Stage stage) {
        // A implementer 
        stage.setTitle("IUTEAM'S - La plateforme de jeux de l'IUTO");
        stage.setScene(this.laScene());
        this.modeAccueil();
        stage.show();
    }

    /**
     * Programme principal
     * @param args inutilisé
     */
    public static void main(String[] args) {
        launch(args);
    }    
public void retourAccueil() {
    this.panelCentral = this.fenetreAccueil();
    this.fenetrePrincipale.setCenter(this.panelCentral);
    this.boutonMaison.setDisable(true); // grise le bouton maison dans le menu
}
}