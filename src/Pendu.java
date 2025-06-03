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
        // Utiliser la couleur du header personnalisée
        String couleurHeader = this.headerColorHex != null ? this.headerColorHex : "#B784A7";
        // Calcul de la couleur de texte adaptée
        int r = Integer.valueOf(couleurHeader.substring(1, 3), 16);
        int g = Integer.valueOf(couleurHeader.substring(3, 5), 16);
        int b = Integer.valueOf(couleurHeader.substring(5, 7), 16);
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
        String textColor = (luminance < 0.5) ? "white" : "#222";
        borderPane.setStyle("-fx-background-color:" + couleurHeader + "; -fx-padding: 10px;");

        Label jeu = new Label("Jeu du Pendu");
        jeu.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + textColor + ";");
        borderPane.setLeft(jeu);

        // (Ré)initialisation des boutons pour éviter les problèmes d'affichage
        // Boutons à droite
        this.boutonMaison = new Button();
        this.boutonMaison.setGraphic(new ImageView(new Image("file:img/home.png", 32, 32, true, true)));
        this.boutonMaison.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-text-fill: #222;");
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
        this.boutonParametres.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-text-fill: #222;");
        this.boutonParametres.setOnAction(e -> this.modeParametres());

        Button info = new Button();
        Image infoImage = new Image("file:img/info.png");
        ImageView infoView = new ImageView(infoImage);
        infoView.setFitHeight(32);
        infoView.setFitWidth(32);
        info.setGraphic(infoView);
        info.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-text-fill: #222;");
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
         nvmot.setOnAction(e -> this.nouveauMotInstantane());
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

    /**
     * Méthode principale pour lancer l'application JavaFX.
     * @param args Arguments de la ligne de commande (non utilisés ici)
     */
    public void modeAccueil(){
        // Correction : ne pas redemander la confirmation ici si déjà fait dans boutonMaison
        this.panelCentral = this.fenetreAccueil();
        this.fenetrePrincipale.setCenter(this.panelCentral);
        this.boutonMaison.setDisable(true);
        this.boutonParametres.setVisible(true);
        this.boutonParametres.setDisable(false);
        reappliquerMainFont();
    }

    /**
     * Applique la police principale à tous les éléments de l'interface.
     */
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
    
    /** Affiche la fenêtre des paramètres du jeu (couleurs, police, dictionnaire, etc.).
     * Met à jour dynamiquement l'UI selon les choix de l'utilisateur.
     */
    public void modeParametres() {
        VBox root = new VBox(20);
        root.setStyle("-fx-padding: 30;");

        Label titre = new Label("Paramètres du jeu");
        titre.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Choix de la couleur principale
        Label couleurLabel = new Label("Couleur principale :");
        ColorPicker colorPicker = new ColorPicker();
        // Initialiser le ColorPicker avec la couleur actuellement sélectionnée
        try {
            colorPicker.setValue(javafx.scene.paint.Color.web(this.mainColorHex));
        } catch (Exception ex) {
            colorPicker.setValue(javafx.scene.paint.Color.web("#B784A7"));
        }

        // Choix de la couleur du header (barre du haut)
        Label headerColorLabel = new Label("Couleur du top (barre du haut) :");
        ColorPicker headerColorPicker = new ColorPicker();
        try {
            headerColorPicker.setValue(javafx.scene.paint.Color.web(this.headerColorHex));
        } catch (Exception ex) {
            headerColorPicker.setValue(javafx.scene.paint.Color.web("#B784A7"));
        }

        // Choix de la longueur des mots
        Label longueurMotsLabel = new Label("Longueur des mots (nombre de lettres) :");
        Spinner<Integer> longueurMotsSpinner = new Spinner<>(3, 12, 5);
        longueurMotsSpinner.setEditable(true);
        CheckBox aleatoireCheck = new CheckBox("Longueur aléatoire");
        aleatoireCheck.setStyle("-fx-font-size: 16px;");
        aleatoireCheck.setSelected(false);
        longueurMotsSpinner.disableProperty().bind(aleatoireCheck.selectedProperty());

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
            // Récupérer la couleur choisie
            String couleur = colorPicker.getValue().toString().replace("0x", "#").substring(0, 7);
            this.setMainColor(couleur); // Appliquer la couleur immédiatement
            // Récupérer la couleur du header
            String headerCouleur = headerColorPicker.getValue().toString().replace("0x", "#").substring(0, 7);
            this.setHeaderColor(headerCouleur); // Appliquer la couleur du header
            boolean aleatoire = aleatoireCheck.isSelected();
            int longueurMots = longueurMotsSpinner.getValue();
            String dictFile = dictCombo.getValue();
            int longMin = 3;
            int longMax = 12;
            if (aleatoire) {
                Dictionnaire dico = new Dictionnaire(dictFile, 3, 100);
                List<Integer> longueurs = dico.getLongueurs();
                int min = 100, max = 3;
                for (int l : longueurs) {
                    if (l < min) min = l;
                    if (l > max) max = l;
                }
                longMin = (longueurs.isEmpty() ? 3 : min);
                longMax = (longueurs.isEmpty() ? 12 : max);
                longueurMots = -1;
            }
            int testLong = (aleatoire ? longMin : longueurMots);
            MotMystere testModele = new MotMystere(dictFile, testLong, testLong, MotMystere.FACILE, 10);
            int nbMotsDispo = testModele.getNombreMotsDictionnaire();
            if (nbMotsDispo < 1) {
                this.popUpErreurDictionnaire("Aucun mot de " + (aleatoire ? (longMin + " à " + longMax) : longueurMots) + " lettres n'a été trouvé dans le dictionnaire sélectionné.\nVeuillez choisir un autre fichier ou changer la longueur des mots.").showAndWait();
                return;
            }
            this.longueurAleatoire = aleatoire;
            this.longueurMotsParam = longueurMots;
            this.dictFileParam = dictFile;
            this.longMinParam = longMin;
            this.longMaxParam = longMax;
            this.modelePendu = new MotMystere(dictFile, (aleatoire ? longMin : longueurMots), (aleatoire ? longMax : longueurMots), MotMystere.FACILE, 10);
            this.setMainFont(policeCombo.getValue());
            this.modeAccueil();
        });
        // --- Bouton pour lancer le démineur ---
        Button demineurBtn = new Button("Partir sur Démineur");
        demineurBtn.setStyle("-fx-background-color: #A7B7E7; -fx-text-fill: #222; -fx-font-weight: bold; font-size: 18px; -fx-padding: 10 20 10 20; border-radius: 8px;");
        demineurBtn.setOnAction(e -> {
            // Ferme la fenêtre du pendu
            Stage stage = (Stage) this.fenetrePrincipale.getScene().getWindow();
            stage.close();
            // Lance le démineur FX dans un nouveau process avec JavaFX
            try {
                String javaHome = System.getProperty("java.home");
                String javaBin = javaHome + java.io.File.separator + "bin" + java.io.File.separator + "java";
                String classpath = System.getProperty("java.class.path");
                String mainClass = "DemineurFX";
                java.util.List<String> command = new java.util.ArrayList<>();
                command.add(javaBin);
                command.add("-cp");
                command.add(classpath);
                // Ajout automatique du module-path si détecté (pour Windows/VSCode)
                String fxPath = System.getProperty("javafx.module.path");
                if (fxPath == null) {
                    String[] args = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
                    for (String arg : args) {
                        if (arg.startsWith("--module-path")) {
                            command.add(arg);
                        } else if (arg.startsWith("--add-modules")) {
                            command.add(arg);
                        }
                    }
                } else {
                    command.add("--module-path");
                    command.add(fxPath);
                    command.add("--add-modules");
                    command.add("javafx.controls,javafx.fxml");
                }
                command.add(mainClass);
                new ProcessBuilder(command).start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        // Ajout du bouton démineur uniquement
        root.getChildren().addAll(titre, couleurLabel, colorPicker, headerColorLabel, headerColorPicker, longueurMotsLabel, longueurMotsSpinner, aleatoireCheck, dictLabel, dictCombo, policeLabel, policeCombo, valider, demineurBtn);
        this.panelCentral = new BorderPane(root);
        this.fenetrePrincipale.setCenter(this.panelCentral);
        reappliquerMainFont();
    }

    /**
     * Applique la couleur du header (barre du haut) et rafraîchit l'affichage du header.
     * @param couleurHex Couleur hexadécimale à appliquer au header (ex : "#B784A7")
     */
    public void setHeaderColor(String couleurHex) {
        this.headerColorHex = couleurHex;
        // Rafraîchir le header si déjà affiché
        if (this.fenetrePrincipale != null) {
            this.fenetrePrincipale.setTop(this.titre());
            reappliquerMainFont();
        }
    }

    /**
     * Affiche une popup d'erreur si le dictionnaire est vide ou invalide.
     * @param message Message d'erreur à afficher
     * @return une alerte JavaFX configurée
     */
    public Alert popUpErreurDictionnaire(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur de dictionnaire");
        alert.setHeaderText("Impossible de lancer la partie");
        alert.setContentText(message);
        return alert;
    }

    /**
     * lance une partie */
    public void lancePartie(){
        // Si longueur aléatoire, choisir une longueur présente dans le dictionnaire
        if (this.longueurAleatoire) {
            Dictionnaire dico = new Dictionnaire(this.dictFileParam, this.longMinParam, this.longMaxParam);
            List<Integer> longueurs = dico.getLongueurs();
            if (longueurs.isEmpty()) {
                this.popUpErreurDictionnaire("Aucun mot valide n'a été trouvé dans le dictionnaire sélectionné.").showAndWait();
                this.modeAccueil();
                return;
            }
            // Tirer une longueur au hasard parmi les longueurs existantes
            int idx = (int)(Math.random() * longueurs.size());
            int longueur = longueurs.get(idx);
            this.modelePendu = new MotMystere(this.dictFileParam, longueur, longueur, MotMystere.FACILE, 10);
        }
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
        this.chrono.start();
        this.modeJeu();
        this.majAffichage();
    }

    // Relance une partie instantanément avec les mêmes paramètres
    /**
     * Relance une partie instantanément avec les mêmes paramètres (mot différent).
     */
    public void nouveauMotInstantane() {
        int niveau = this.modelePendu.getNiveau();
        int nbErreursMax = this.modelePendu.getNbErreursMax();
        // Si longueur aléatoire, on relance un mot aléatoire de la même façon
        if (this.longueurAleatoire) {
            Dictionnaire dico = new Dictionnaire(this.dictFileParam, this.longMinParam, this.longMaxParam);
            List<Integer> longueurs = dico.getLongueurs();
            if (longueurs.isEmpty()) {
                this.popUpErreurDictionnaire("Aucun mot valide n'a été trouvé dans le dictionnaire sélectionné.").showAndWait();
                this.modeAccueil();
                return;
            }
            int idx = (int)(Math.random() * longueurs.size());
            int longueur = longueurs.get(idx);
            this.modelePendu = new MotMystere(this.dictFileParam, longueur, longueur, niveau, nbErreursMax);
        } else {
            // Sinon, on relance avec la longueur fixe
            this.modelePendu = new MotMystere(this.dictFileParam, this.longueurMotsParam, this.longueurMotsParam, niveau, nbErreursMax);
        }
        this.modelePendu.setNiveau(niveau);
        this.modelePendu.setMotATrouver();
        this.chrono.resetTime();
        this.chrono.start();
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
                Alert alert = this.popUpMessageGagne();
                alert.showAndWait();
                // Revenir à l'accueil après la victoire (ne pas fermer la fenêtre)
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

    /**
     * Affiche une popup de confirmation si une partie est en cours.
     * @return une alerte JavaFX de confirmation
     */
    public Alert popUpPartieEnCours(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"La partie est en cours!\nEtes-vous sûr de l'interrompre ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Attention");
        return alert;
    }
        
    /**
     * Affiche une popup d'information sur les règles du jeu du pendu.
     * @return une alerte JavaFX d'information
     */
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
    
    /**
     * Affiche une popup de victoire.
     * @return une alerte JavaFX d'information
     */
    public Alert popUpMessageGagne(){
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);   
         alert.setTitle("Victoire");
        alert.setHeaderText("Vous avez gagné !");
        alert.setContentText("Le mot était : " + this.modelePendu.getMotATrouver());
        return alert;
    }
    
    /**
     * Affiche une popup de défaite.
     * @return une alerte JavaFX d'information
     */
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
    this.mainColorHex = couleurHex;
    // Déterminer si la couleur est sombre
    int r = Integer.valueOf(couleurHex.substring(1, 3), 16);
    int g = Integer.valueOf(couleurHex.substring(3, 5), 16);
    int b = Integer.valueOf(couleurHex.substring(5, 7), 16);
    double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
    String textColor = (luminance < 0.5) ? "white" : "#222";
    // Appliquer la couleur ET la police si définie, et la couleur du texte
    String style = "-fx-background-color:" + couleurHex + ";-fx-text-fill:" + textColor + ";";
    if (this.mainFontFamily != null) {
        style += "-fx-font-family: '" + this.mainFontFamily + "';";
    }
    this.fenetrePrincipale.setStyle(style);
}

// Permet d'appliquer la police d'écriture principale sur les labels importants
public void setMainFont(String fontFamily) {
    // Calcul de la couleur de texte selon la couleur de fond
    String textColor = "#222";
    if (this.mainColorHex != null) {
        int r = Integer.valueOf(this.mainColorHex.substring(1, 3), 16);
        int g = Integer.valueOf(this.mainColorHex.substring(3, 5), 16);
        int b = Integer.valueOf(this.mainColorHex.substring(5, 7), 16);
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
        textColor = (luminance < 0.5) ? "white" : "#222";
    }
    // Appliquer la police et la couleur sur le titre
    if (this.fenetrePrincipale.getTop() instanceof Pane) {
        Pane topPane = (Pane) this.fenetrePrincipale.getTop();
        for (javafx.scene.Node node : topPane.getChildrenUnmodifiable()) {
            if (node instanceof Label) {
                ((Label) node).setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + textColor + "; -fx-font-family: '" + fontFamily + "';");
            }
        }
    }
    // Appliquer la police et la couleur sur le mot crypté
    if (this.motCrypte != null) {
        this.motCrypte.setStyle("-fx-font-size: 24px; -fx-font-family: '" + fontFamily + "'; -fx-fill: " + textColor + ";");
    }
    // Appliquer la police et la couleur sur tous les boutons visibles
    applyTextColorToButtons(this.panelCentral, textColor, fontFamily);
    // Appliquer la police ET la couleur sur tout le contenu de la fenêtre principale
    String style = "";
    if (this.mainColorHex != null) {
        style += "-fx-background-color:" + this.mainColorHex + ";";
    }
    style += "-fx-font-family: '" + fontFamily + "';";
    this.fenetrePrincipale.setStyle(style);
    // Stocker la police pour la réappliquer lors des changements d'écran
    this.mainFontFamily = fontFamily;
}

// Applique la couleur du texte et la police à tous les boutons et labels enfants d'un parent
private void applyTextColorToButtons(Pane parent, String textColor, String fontFamily) {
    if (parent == null) return;
    for (javafx.scene.Node node : parent.getChildren()) {
        if (node instanceof Button) {
            Button btn = (Button) node;
            // Si le fond principal est sombre, forcer les boutons à fond blanc et texte noir
            int r = Integer.valueOf(this.mainColorHex.substring(1, 3), 16);
            int g = Integer.valueOf(this.mainColorHex.substring(3, 5), 16);
            int b = Integer.valueOf(this.mainColorHex.substring(5, 7), 16);
            double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
            boolean fondSombre = luminance < 0.5;
            if (fondSombre) {
                btn.setStyle("-fx-background-color: white; -fx-text-fill: #222; -fx-font-family: '" + fontFamily + "';");
            } else {
                // Ancien comportement :
                String style = btn.getStyle();
                boolean headerBtn = false;
                if (style != null && (style.contains("background-color: white") || style.contains("background-color:#fff") || style.contains("background-color: #fff"))) {
                    headerBtn = true;
                }
                if (headerBtn) {
                    btn.setStyle(style + ";-fx-text-fill: #222; -fx-font-family: '" + fontFamily + "';");
                } else {
                    btn.setStyle("-fx-text-fill: " + textColor + "; -fx-font-family: '" + fontFamily + "';");
                }
            }
        } else if (node instanceof Label) {
            ((Label) node).setStyle("-fx-text-fill: " + textColor + "; -fx-font-family: '" + fontFamily + "';");
        } else if (node instanceof Pane) {
            applyTextColorToButtons((Pane) node, textColor, fontFamily);
        }
    }
}

// À appeler après chaque changement d'écran pour réappliquer la police
private void reappliquerMainFont() {
    if (this.mainFontFamily != null) {
        setMainFont(this.mainFontFamily);
    }
}

// Ajout d'un champ pour stocker la couleur principale
private String mainColorHex = "#B784A7";
// Ajout d'un champ pour stocker la police principale
private String mainFontFamily = null;
// Ajout d'un champ pour stocker la couleur du header (barre du haut)
private String headerColorHex = "#B784A7";

    // Chronomètre fonctionnel
    private Label chronoLabel = null; // Affichage du chrono

    // --- Ajout pour gestion longueur aléatoire ---
    private boolean longueurAleatoire = false;
    private int longueurMotsParam = 5;
    private String dictFileParam = "src/mots.txt";
    private int longMinParam = 3;
    private int longMaxParam = 12;

    // La méthode titre() correcte existe déjà plus haut et retourne un Pane
    // La méthode getContrastColor n'est utile qu'en version utilitaire, à placer ici :
    private String getContrastColor(String hexColor) {
        int r = Integer.valueOf(hexColor.substring(1, 3), 16);
        int g = Integer.valueOf(hexColor.substring(3, 5), 16);
        int b = Integer.valueOf(hexColor.substring(5, 7), 16);
        double luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255;
        return luminance > 0.5 ? "black" : "white";
    }
}