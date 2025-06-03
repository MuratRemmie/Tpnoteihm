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
        // Boutons à droite
        this.boutonMaison = new Button();
        this.boutonMaison.setGraphic(new ImageView(new Image("file:img/home.png", 32, 32, true, true)));
        this.boutonMaison.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        this.boutonMaison.setOnAction(e -> {
            // Correction : ne demander la confirmation qu'une seule fois
            if (this.modelePendu.partieEnCours()) {
                Alert alert = this.popUpPartieEnCours();
                alert.showAndWait();
                if (alert.getResult() != ButtonType.YES) {
                    return;
                }
                // On arrête la partie explicitement pour éviter le double popup
                this.modelePendu.setFinPartie();
            }
            this.modeAccueil();
        });

        this.boutonParametres = new Button();
        this.boutonParametres.setGraphic(new ImageView(new Image("file:img/parametres.png", 32, 32, true, true)));
        this.boutonParametres.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        // TODO: Ajouter un vrai controleur pour les paramètres si besoin
        this.boutonParametres.setOnAction(e -> this.modeParametres());

        Button info = new Button();
        Image infoImage = new Image("file:img/info.png");
        ImageView infoView = new ImageView(infoImage);
        infoView.setFitHeight(32);
        infoView.setFitWidth(32);
        info.setGraphic(infoView);
        info.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5;");
        info.setOnAction(new ControleurInfos(this));

        HBox boutons = new HBox(10, this.boutonMaison, this.boutonParametres, info);
        boutons.setStyle("-fx-alignment: center-right;");
        borderPane.setRight(boutons);

        return borderPane;
    }

    // /**
    // * @return le panel du chronomètre
    // */
    private TitledPane leChrono(){
        TitledPane res = new TitledPane("Chronomètre", this.chrono);
        res.setCollapsible(false);
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
         motCrypte.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

         VBox centre = new VBox();
         this.dessin = new ImageView(this.lesImages.get(0));
         this.dessin.setFitHeight(250); // agrandir l'image du pendu
         this.dessin.setPreserveRatio(true);
         this.pg = new ProgressBar(0.0);
         this.pg.setPrefWidth(300);
         this.clavier = new Clavier("ABCDEFGHIJKLMNOPQRSTUVWXYZ", new ControleurLettres(this.modelePendu, this));
         this.clavier.setStyle("-fx-font-size: 22px; -fx-spacing: 10px; -fx-padding: 20 0 20 0;");
         centre.setAlignment(javafx.geometry.Pos.CENTER);
         centre.setSpacing(20);
         centre.getChildren().addAll(this.dessin, this.pg, this.clavier);

         VBox aDroite = new VBox();
         int valniv = this.modelePendu.getNiveau();
         String niv = this.niveaux.get(valniv);
         // Afficher le niveau choisi, non modifiable
         Label niveauLabel = new Label("Niveau " + niv);
         niveauLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

         TitledPane chrono = this.leChrono();
         Button nvmot = new Button("Nouveau mot");
         nvmot.setStyle("-fx-font-size: 18px; -fx-padding: 10 20 10 20;");
         aDroite.setSpacing(20);
         
         // On retire labelTemps ici
         aDroite.getChildren().setAll(niveauLabel, chrono, nvmot);
         aDroite.setAlignment(javafx.geometry.Pos.CENTER);

         VBox centreAvecMot = new VBox();
         centreAvecMot.setSpacing(20);
         centreAvecMot.setAlignment(javafx.geometry.Pos.CENTER);
         centreAvecMot.getChildren().addAll(this.motCrypte, centre);

         mid.setRight(aDroite);
         mid.setCenter(centreAvecMot);
         // Suppression du bas, le clavier est déjà dans le centre
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
        this.bJouer.setStyle("-fx-font-size: 20px; -fx-padding: 15 30 15 30; -fx-background-radius: 10; -fx-background-color: #B784A7; -fx-text-fill: white; -fx-font-weight: bold;");
        this.bJouer.setOnAction(e -> this.lancePartie());

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

        VBox niveauxBox = new VBox(20, facile, moyen, difficile, expert);
        niveauxBox.setAlignment(javafx.geometry.Pos.CENTER);
        niveauxBox.setStyle("-fx-font-size: 18px; -fx-padding: 30 0 30 0; -fx-background-color: #f5f5f5; -fx-background-radius: 15;");
        niveauxBox.setMaxWidth(300);

        VBox root = new VBox(40);
        root.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        root.getChildren().addAll(bJouer, niveauxBox);
        root.setStyle("-fx-background-color: transparent; -fx-padding: 40 0 0 0;");

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
        // Correction : ne pas redemander la confirmation ici si déjà fait dans boutonMaison
        this.panelCentral = this.fenetreAccueil();
        this.fenetrePrincipale.setCenter(this.panelCentral);
        this.boutonMaison.setDisable(true);
        this.boutonParametres.setVisible(true);
        this.boutonParametres.setDisable(false);
        reappliquerMainFont();
    }

    public void modeJeu(){
        this.panelCentral = this.fenetreJeu();
        this.fenetrePrincipale.setCenter(this.panelCentral);
        this.boutonMaison.setDisable(false);
        this.boutonParametres.setDisable(true); // Désactive le bouton paramètres pendant le jeu
        this.boutonParametres.setVisible(true); // Le bouton reste visible (grisé) pendant le jeu
        reappliquerMainFont();
        this.chrono.resetTime();
        this.chrono.start();
        if (this.chronoLabel != null) this.chronoLabel.setText(this.chrono.getText());
    }
    
    // Affiche la fenêtre des paramètres
    public void modeParametres() {
        // Création des contrôles pour les paramètres
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 30; -fx-background-color: #f5f5f5;");

        Label titre = new Label("Paramètres du jeu");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Choix de la couleur principale
        Label couleurLabel = new Label("Couleur principale :");
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(javafx.scene.paint.Color.web("#B784A7"));

        // Choix de la longueur des mots
        Label longueurMotsLabel = new Label("Longueur des mots (nombre de lettres) :");
        Spinner<Integer> longueurMotsSpinner = new Spinner<>(3, 12, 5);
        longueurMotsSpinner.setEditable(true);

        // Choix du dictionnaire
        Label dictLabel = new Label("Dictionnaire :");
        ComboBox<String> dictCombo = new ComboBox<>();
        dictCombo.getItems().addAll("src/mots.txt", "src/autre_dico.txt");
        dictCombo.setValue("src/mots.txt");

        // Choix de la police d'écriture
        Label policeLabel = new Label("Police d'écriture :");
        ComboBox<String> policeCombo = new ComboBox<>();
        policeCombo.getItems().addAll("Arial", "Verdana", "Comic Sans MS", "Times New Roman");
        // Initialiser la valeur du ComboBox avec la police actuellement sélectionnée
        if (this.mainFontFamily != null) {
            policeCombo.setValue(this.mainFontFamily);
        } else {
            policeCombo.setValue("Arial");
        }
        // Bouton valider
        Button valider = new Button("Valider");
        valider.setStyle("-fx-background-color: #B784A7; -fx-text-fill: white; -fx-font-weight: bold;");
        valider.setOnAction(e -> {
            // Appliquer les paramètres
            String couleur = colorPicker.getValue().toString().replace("0x", "#").substring(0, 7);
            int longueurMots = longueurMotsSpinner.getValue();
            String dictFile = dictCombo.getValue();
            // Vérification du dictionnaire et de la longueur des mots
            MotMystere testModele = new MotMystere(dictFile, longueurMots, longueurMots, MotMystere.FACILE, 10);
            int nbMotsDispo = testModele.getNombreMotsDictionnaire();
            if (nbMotsDispo < 1) {
                this.popUpErreurDictionnaire("Aucun mot de " + longueurMots + " lettres n'a été trouvé dans le dictionnaire sélectionné.\nVeuillez choisir un autre fichier ou changer la longueur des mots.").showAndWait();
                return;
            }
            // Couleur principale
            this.fenetrePrincipale.setStyle("-fx-background-color:" + couleur + ";");
            // On réinstancie le modèle avec la nouvelle longueur de mots
            this.modelePendu = new MotMystere(dictFile, longueurMots, longueurMots, MotMystere.FACILE, 10);
            // Police d'écriture (à appliquer sur les labels principaux)
            this.setMainFont(policeCombo.getValue());
            // Retour au menu
            this.modeAccueil();
        });

        root.getChildren().addAll(titre, couleurLabel, colorPicker, longueurMotsLabel, longueurMotsSpinner, dictLabel, dictCombo, policeLabel, policeCombo, valider);
        this.panelCentral = new BorderPane(root);
        this.fenetrePrincipale.setCenter(this.panelCentral);
        reappliquerMainFont();
    }

    // Affiche une popup d'erreur si le dictionnaire est vide ou invalide
    public Alert popUpErreurDictionnaire(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de dictionnaire");
        alert.setHeaderText("Impossible de lancer la partie");
        alert.setContentText(message);
        return alert;
    }

    /** lance une partie */
    public void lancePartie(){
        // Vérification du modèle avant de lancer la partie
        int nbMotsDispo = this.modelePendu.getNombreMotsDictionnaire();
        int nbMotsVoulu = 3; // valeur par défaut
        try {
            nbMotsVoulu = Integer.parseInt(System.getProperty("nbMots", "3"));
        } catch (Exception ex) {}
        if (nbMotsDispo < 1) {
            this.popUpErreurDictionnaire("Aucun mot valide n'a été trouvé dans le dictionnaire sélectionné.\nVeuillez choisir un autre fichier ou réduire le nombre de mots dans les paramètres.").showAndWait();
            this.modeAccueil();
            return;
        }
        if (nbMotsDispo < nbMotsVoulu) {
            this.popUpErreurDictionnaire("Le dictionnaire ne contient pas assez de mots (" + nbMotsDispo + ").\nVeuillez réduire le nombre de mots ou choisir un autre dictionnaire dans les paramètres.").showAndWait();
            this.modeAccueil();
            return;
        }
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

        this.clavier.majAffichage(this.modelePendu.getLettresEssayees());
        this.chrono.setTime(this.chrono.getTempsEcoule());

        // Mise à jour du label du chrono
        // if (this.chronoLabel != null) {
        //     this.chronoLabel.setText(this.chrono.getText());
        // }
        // Pour afficher le temps en texte
        // labelTemps.setText("Temps : " + chrono.getText());

        // Afficher les popups de victoire/défaite si la partie vient de se terminer
        if (!this.modelePendu.partieEnCours()) {
            this.chrono.stop();
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"La partie est en cours!\nEtes-vous sûr de l'interrompre ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Attention");
        return alert;
    }
        
    public Alert popUpReglesDuJeu(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Règles du jeu du Pendu");
        alert.setHeaderText("Comment jouer ?");
        alert.setContentText("Le but du jeu est de deviner un mot en proposant des lettres.\n" +
            "À chaque mauvaise lettre, une partie du pendu est dessinée.\n" +
            "Vous gagnez si vous trouvez toutes les lettres avant que le pendu ne soit complet !\n\n" +
            "- Cliquez sur les lettres pour proposer une lettre.\n" +
            "- Le nombre d'erreurs autorisées dépend du niveau.\n" +
            "- Bonne chance !");
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

// Permet au contrôleur d'appliquer la couleur principale à la fenêtre principale
    public void setMainColor(String couleurHex) {
        this.fenetrePrincipale.setStyle("-fx-background-color:" + couleurHex + ";");
    }

// Permet d'appliquer la police d'écriture principale sur les labels importants
    public void setMainFont(String fontFamily) {
        // Appliquer la police sur le titre
        if (this.fenetrePrincipale.getTop() instanceof Pane) {
            Pane topPane = (Pane) this.fenetrePrincipale.getTop();
            for (javafx.scene.Node node : topPane.getChildrenUnmodifiable()) {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #222; -fx-font-family: '" + fontFamily + "';");
                }
            }
        }
        // Appliquer la police sur le mot crypté
        if (this.motCrypte != null) {
            this.motCrypte.setStyle("-fx-font-size: 24px; -fx-font-family: '" + fontFamily + "';");
        }
        // SUPPRIMÉ : plus de label leNiveau à styliser
        // if (this.leNiveau != null) {
        //     this.leNiveau.setStyle("-fx-font-size: 18px; -fx-font-family: '" + fontFamily + "';");
        // }
        // Appliquer la police sur le label du temps
        // if (this.labelTemps != null) {
        //     this.labelTemps.setStyle("-fx-font-size: 16px; -fx-font-family: '" + fontFamily + "';");
        // }
        // Stocker la police pour la réappliquer lors des changements d'écran
        this.mainFontFamily = fontFamily;
    }

    // À appeler après chaque changement d'écran pour réappliquer la police
    private void reappliquerMainFont() {
        if (this.mainFontFamily != null) {
            setMainFont(this.mainFontFamily);
        }
    }

    // Ajout d'un champ pour stocker la police principale
    private String mainFontFamily = null;

    // Chronomètre fonctionnel
    private Label chronoLabel = null; // Affichage du chrono
}