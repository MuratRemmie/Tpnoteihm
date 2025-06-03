import java.util.Set;
import java.util.HashSet;

/**
 * Modèle pour le jeu du pendu.
 */
public class MotMystere {
    // constantes pour gérer les différents niveaux de jeu
    /** Niveau FACILE : la première lettre et la dernière lettre du mot à trouver sont données ainsi que les éventuels caractères non alphabétiques (traits d'union par exemple)*/
    public final static int FACILE = 0;
    /** Niveau MOYEN : la première lettre du mot à trouver est donnée ainsi que les traits d'union si le mot à trouver en comporte */
    public final static int MOYEN = 1;
    /** Niveau DIFFICILE : seuls les traits d'union si le mot à trouver en comporte */
    public final static int DIFFICILE = 2;
    /** Niveau EXPERT : rien n'est donné, ni lettre ni trait d'union */
    public final static int EXPERT = 3;
    
    /**
     * le mot à trouver
     */
    private String motATrouver;
    /**
     * le niveau de jeu
     */
    private int niveau;
    /**
     * chaine contenant les lettres déjà trouvées et des * à la place des lettres non encore trouvées
     */
    private String motCrypte;
    /**
     * chaine contenant l'ensemble des lettres déjà essayées
     */
    private Set<String> lettresEssayees;
    /**
     * entier inquant le nombre de lettres restant à trouver
     */
    private int nbLettresRestantes;
    /**
     * le nombre d'essais déjà effectués
     */
    private int nbEssais;
    /**
     * le nombre d'erreurs encore possibles
     */
    private int nbErreursRestantes;
    /**
     * le nombre total de tentatives autorisées
     */
    private int nbErreursMax;
    /**
     * dictionnaire dans lequel on choisit les mots
     */
    private Dictionnaire dict;

    /**
     * indique si une partie a été démarrée
     */
    private boolean partieDemarree = false;


    /**
     * constructeur dans lequel on impose le mot à trouver
     * @param motATrouve mot à trouver
     * @param niveau niveau du jeu
     * @param nbErreursMax le nombre total d'essais autorisés
     */
    public MotMystere(String motATrouver, int niveau, int nbErreursMax) {
        super();
        this.initMotMystere(motATrouver, niveau, nbErreursMax);
    }

    /**
     * Constructeur dans lequel on va initialiser un dictionnaire pour choisir les mots à trouver
     * @param nomFichier est le chemin vers le dictionnaire utilisé qui est un fichier texte
     *  contenant une liste de mots (un mot par ligne)
     * @param longMin longueur minimale des mots retenus dans le dictionnaire
     * @param longMax longueur maximale des mots retenus dans le dictionnaire
     * @param niveau niveau initial de jeu
     * @param nbErreursMax le nombre total d'essais autorisés
     */
    public MotMystere(String nomFichier, int longMin, int longMax, int niveau, int nbErreursMax) {
        super();
        this.dict = new Dictionnaire(nomFichier,longMin,longMax);
        this.partieDemarree = false;
        this.nbErreursMax = nbErreursMax; // Correction : stocker le nombre d'erreurs max pour la première partie
        // Partie non démarrée ici, elle sera démarrée explicitement par setMotATrouver() dans lancePartie()
    }

    /**
     * initialisation du jeu
     * @param motATrouver le mot à trouver
     * @param niveau le niveau de jeu
     * @param nbErreursMax  le nombre total d'essais autorisés
     */
    private void initMotMystere(String motATrouver, int niveau, int nbErreursMax){
        System.out.println("[DEBUG] initMotMystere: motATrouver=" + motATrouver + ", niveau=" + niveau + ", nbErreursMax=" + nbErreursMax);
        this.niveau =niveau;
        this.nbEssais=0;
        this.motATrouver = Dictionnaire.sansAccents(motATrouver).toUpperCase();
        this.motCrypte = "";
        this.lettresEssayees = new HashSet<>();

        nbLettresRestantes=0;
        int len = motATrouver.length();
        // Première lettre
        if (niveau == MotMystere.FACILE || niveau == MotMystere.MOYEN) {
            motCrypte += this.motATrouver.charAt(0); // révélée
        } else {
            motCrypte += "*";
            this.nbLettresRestantes += 1;
        }
        // Lettres du milieu
        for (int i=1; i<len-1; i++){
            char lettre = this.motATrouver.charAt(i);
            if (niveau == MotMystere.EXPERT) {
                motCrypte += "*";
                this.nbLettresRestantes += 1;
            } else if (niveau == MotMystere.DIFFICILE) {
                if (Character.isAlphabetic(lettre)) {
                    motCrypte += "*";
                    this.nbLettresRestantes += 1;
                } else {
                    motCrypte += lettre; // révèle les traits d'union
                }
            } else {
                // FACILE ou MOYEN
                if (Character.isAlphabetic(lettre)) {
                    motCrypte += "*";
                    this.nbLettresRestantes += 1;
                } else {
                    motCrypte += lettre;
                }
            }
        }
        // Dernière lettre
        if (niveau == MotMystere.FACILE) {
            motCrypte += this.motATrouver.charAt(len-1); // révélée
        } else {
            motCrypte += "*";
            this.nbLettresRestantes += 1;
        }
        this.nbErreursMax = nbErreursMax;
        this.nbErreursRestantes = nbErreursMax;
        System.out.println("[DEBUG] Après init: partieDemarree=" + partieDemarree + ", nbLettresRestantes=" + nbLettresRestantes + ", nbErreursRestantes=" + nbErreursRestantes);
        // On ne touche pas à partieDemarree ici
    }

    /**
     * @return le mot à trouver
     */
    public String getMotATrouver() {
        return this.motATrouver;
    }

    /**
     * @return le niveau de jeu
     */
    public int getNiveau(){
        return this.niveau;
    }

    /** réinitialise le jeu avec un nouveau à trouver
     * @param motATrouver le nouveau mot à trouver
     */
    public void setMotATrouver(String motATrouver) {
        System.out.println("[DEBUG] setMotATrouver(String): avant, partieDemarree=" + partieDemarree);
        this.initMotMystere(motATrouver, this.niveau, this.nbErreursMax);
        this.partieDemarree = true;
        System.out.println("[DEBUG] setMotATrouver(String): après, partieDemarree=" + partieDemarree);
    }

    /**
     * Réinitialise le jeu avec un nouveau mot à trouver choisi au hasard dans le dictionnaire
     */
    public void setMotATrouver() {
        System.out.println("[DEBUG] setMotATrouver(): avant, partieDemarree=" + partieDemarree);
        this.initMotMystere(this.dict.choisirMot(), this.niveau, this.nbErreursMax);
        this.partieDemarree = true;
        System.out.println("[DEBUG] setMotATrouver(): après, partieDemarree=" + partieDemarree);
    }

    /**
     * change le niveau de jeu (n'a pas d'effet en cours de partie)
     * @param niveau le nouveau niveu de jeu
     */
    public void setNiveau(int niveau){
        this.niveau = niveau;
    }

    /**
     * @return le mot avec les lettres trouvées affichées et des étoiles pour les lettres non trouvées
     */
    public String getMotCrypte() {
        return this.motCrypte;
    }

    /**
     * @return les lettres déjà essayées
     */
    public Set<String> getLettresEssayees() {
        return this.lettresEssayees;
    }

    /**
     * @return le nombre de lettres restant à trouver
     */
    public int getNbLettresRestantes() {
        return this.nbLettresRestantes;
    }

    /**
     * @return le nombre d'essais déjà effectués
     */
    public int getNbEssais(){
        return this.nbEssais;
    }

    /**
     * @return le nombre total de tentatives autorisées
     */
    public int getNbErreursMax(){
        return this.nbErreursMax;
    }

    /**
     * @return le nombre d'erreurs encore autorisées
     */
    public int getNbErreursRestants(){
        return this.nbErreursRestantes;
    }

    /**
     * @return un booléen indiquant si le joueur a perdu
     */
    public boolean perdu(){
        if (this.nbErreursRestantes == 0) {
            this.partieDemarree = false;
            System.out.println("[DEBUG] perdu(): partie perdue, partieDemarree=" + partieDemarree);
            return true;
        }
        return false;
    }

    /**
     * @return un booléen indiquant si le joueur a gangé
     */
    public boolean gagne(){
        if (this.nbLettresRestantes == 0) {
            this.partieDemarree = false;
            System.out.println("[DEBUG] gagne(): partie gagnée, partieDemarree=" + partieDemarree);
            return true;
        }
        return false;
    }

    /**
     * permet au joueur d'essayer une lettre
     * @param lettre la lettre essayée par le joueur
     * @return le nombre de fois où la lettre apparait dans le mot à trouver
     */
    public int essaiLettre(char lettre){
        int nbNouvelles = 0;
        char[] aux = this.motCrypte.toCharArray();
        for (int i=0; i<this.motATrouver.length(); i++){
            if (this.motATrouver.charAt(i) == lettre && this.motCrypte.charAt(i) == '*'){
                nbNouvelles += 1;
                aux[i] = lettre;
            }
        }
        this.motCrypte = String.valueOf(aux);
        this.nbLettresRestantes -= nbNouvelles;
        this.lettresEssayees.add(lettre+"");
        // Le nombre d'essais augmente de 1
        this.nbEssais += 1;
        // Si aucune lettre n'a été trouvée, le nombre d'erreurs restante diminue de 1
        if (nbNouvelles == 0){
            this.nbErreursRestantes-=1;
        }
        return nbNouvelles;
    }

    /**
     * @return une chaine de caractère donnant l'état du jeu
     */
    public String toString(){
        return "Mot a trouve: "+this.motATrouver+" Lettres trouvees: "+
               this.motCrypte+" nombre de lettres restantes "+this.nbLettresRestantes+
               " nombre d'essais restents: "+this.nbErreursRestantes;
    }

    /**
     * Indique si une partie est en cours
     * @return true si une partie est en cours, false sinon
     */
    public boolean partieEnCours() {
        boolean enCours = partieDemarree && nbLettresRestantes > 0 && nbErreursRestantes > 0;
        System.out.println("[DEBUG] partieEnCours(): partieDemarree=" + partieDemarree + ", nbLettresRestantes=" + nbLettresRestantes + ", nbErreursRestantes=" + nbErreursRestantes + " => " + enCours);
        return enCours;
    }


    /**
     * Retourne le nombre d'erreurs commises par le joueur
     * @return le nombre d'erreurs commises
     */
    public int getNbErreurs(){
        return this.nbErreursMax - this.nbErreursRestantes;
    }

    /**
     * Définit le nombre maximum d'erreurs autorisées pour la partie en cours
     * @param nbErreursMax le nouveau nombre maximum d'erreurs autorisées
     */
    public void setNbErreursMax(int nbErreursMax) {
        this.nbErreursMax = nbErreursMax;
    }

    /**
     * Retourne le nombre de mots disponibles dans le dictionnaire associé
     */
    public int getNombreMotsDictionnaire() {
        if (this.dict == null) return 0;
        return this.dict.getNombreMots();
    }
    
    /**
     * Termine explicitement la partie en cours (pour éviter les popups multiples)
     */
    public void setFinPartie() {
        this.partieDemarree = false;
    }
}
