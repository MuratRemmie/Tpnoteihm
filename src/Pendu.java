import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.ButtonBar.ButtonData ;

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
     * le bouton Paramètre / Engrenage
     */
    private Button boutonParametres;
    /**
     * le bouton Accueil / Maison
     */    
    private Button boutonMaison;
    /**
     * le bouton qui permet de (lancer ou relancer une partie
     */ 
    private Button bJouer;
    private  BorderPane fenetrePrincipale ; 
    private ToggleGroup groupeNiveaux;

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

        HBox head = new HBox();
        head.setStyle("-fx-background-color:#B784A7; -fx-padding: 10px;");
        Label jeu = new Label();
        jeu.setText("Jeu du pendu ");

        Button home = new Button();
        Image homeImage = new Image("file:img/home.png");
        ImageView homeView = new ImageView(homeImage);
        homeView.setFitHeight(20);
        homeView.setFitWidth(20);
        home.setGraphic(homeView);
        home.setOnAction(e -> this.modeAccueil());

        Button param = new Button();
        Image paramImage = new Image("file:img/parametres.png");
        ImageView paramView = new ImageView(paramImage);
        paramView.setFitHeight(20);
        paramView.setFitWidth(20);
        param.setGraphic(paramView);
        param.setOnAction(e -> this.modeParametres());

        Button info = new Button();
        Image infoImage = new Image("file:img/info.png");
        ImageView infoView = new ImageView(infoImage);
        infoView.setFitHeight(20);
        infoView.setFitWidth(20);
        info.setGraphic(infoView);

    
        head.getChildren().addAll(jeu, home, param, info);

        borderPane.setTop(head);
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

         VBox aDroite = new VBox();
         int valniv = this.modelePendu.getNiveau();
         String niv = this.niveaux.get(valniv);
         this.leNiveau = new Text(niv);

         TitledPane chrono = this.leChrono();
         Button nvmot = new Button("Nouveau mot");
         aDroite.getChildren().addAll(leNiveau, chrono, nvmot);

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

        Button home = new Button();
        Image homeImage = new Image("file:img/home.png");
        
        ImageView homeView = new ImageView(homeImage);
        homeView.setFitHeight(20);
        homeView.setFitWidth(20);
        home.setGraphic(homeView);

        Button param = new Button();
        Image paramImage = new Image("file:img/parametres.png");
        ImageView paramView = new ImageView(paramImage);
        paramView.setFitHeight(20);
        paramView.setFitWidth(20);
        param.setGraphic(paramView);

        Button info = new Button();
        Image infoImage = new Image("file:img/info.png");
        ImageView infoView = new ImageView(infoImage);
        infoView.setFitHeight(20);
        infoView.setFitWidth(20);
        info.setGraphic(infoView);
        head.getChildren().addAll(jeu, home, param, info);

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
        this.panelCentral=this.fenetreAccueil();
        this.fenetrePrincipale.setCenter(this.panelCentral);
    }
    
    public void modeJeu(){
   
        this.panelCentral = this.fenetreJeu();
        this.fenetrePrincipale.setCenter(this.panelCentral);
        
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
        this.dessin.setImage(this.lesImages.get(nombreErreursRealiser));    

        int nombreErreursMaximum = this.modelePendu.getNbErreursMax();
        double progression = (double) nombreErreursRealiser / nombreErreursMaximum ;
        this.pg.setProgress(progression);

        this.leNiveau.setText(this.niveaux.get(this.modelePendu.getNiveau()));

        this.clavier.majAffichage(this.modelePendu.getLettresEssayees());
        this.chrono.setTime(this.modelePendu.getTempsEcoule());

        if (this.modelePendu.gagne()){
            this.popUpMessageGagne().showAndWait();
            this.modeAccueil();
        } else if (this.modelePendu.perdu()){
            this.popUpMessagePerdu().showAndWait();
            this.modeAccueil();
            
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
        // A implementer
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        return alert;
    }
    
    public Alert popUpMessageGagne(){
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);   
         alert.setTitle("Victoire");
        alert.setHeaderText("Vous avez gagné !");
        alert.setContentText("Le mot était : " + this.modelePendu.getMotATrouve());
        return alert;
    }
    
    public Alert popUpMessagePerdu(){
           
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Dommage");
        alert.setHeaderText("Vous avez perdu !");
        alert.setContentText("Le mot a trouver était : " + this.modelePendu.getMotATrouve());
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
}