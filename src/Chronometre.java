import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;


/**
 * Permet de gérer un Text associé à une Timeline pour afficher un temps écoulé
 */
public class Chronometre extends Text{
    /**
     * timeline qui va gérer le temps
     */
    private Timeline timeline;
    /**
     * la fenêtre de temps
     */
    private KeyFrame keyFrame;
    /**
     * le contrôleur associé au chronomètre
     */
    private ControleurChronometre actionTemps;

    private long tempsEcoule = 0;

    /**
     * Constructeur permettant de créer le chronomètre
     * avec un label initialisé à "0:0:0"
     * Ce constructeur créer la Timeline, la KeyFrame et le contrôleur
     */
    public Chronometre(){
        this.timeline = new Timeline();
        this.actionTemps = new ControleurChronometre(this);
        this.keyFrame = new KeyFrame(Duration.seconds(1), this.actionTemps);
        this.timeline.getKeyFrames().add(this.keyFrame);
        this.timeline.setCycleCount(Animation.INDEFINITE);
        this.setText("0:00");
        this.setFont(Font.font(24));
        this.setTextAlignment(TextAlignment.CENTER);
    }

    public long getTempsEcoule() {
        return this.tempsEcoule;
    }

    /**
     * Permet au controleur de mettre à jour le text
     * la durée est affichée sous la forme m:s
     * @param tempsMillisec la durée depuis à afficher
     */
    public void setTime(long tempsMillisec){
        this.tempsEcoule = tempsMillisec;
        long secondes = tempsMillisec / 1000;
        long minutes = secondes / 60;
        secondes = secondes % 60;
        this.setText(String.format("%d:%02d", minutes, secondes));
        // A implémenter
        
    }

    /**
     * Permet de démarrer le chronomètre
     */
    public void start(){
        if (this.timeline != null) {
            this.timeline.play();
        }
        // A implémenter
    }

    /**
     * Permet d'arrêter le chronomètre
     */
    public void stop(){
        if (this.timeline !=  null) {
            this.timeline.stop();
        }
        // A implémenter
    }

    /**
     * Permet de remettre le chronomètre à 0
     */
    public void resetTime(){
        this.stop();
        if (this.actionTemps != null) {
            this.actionTemps.reset();
        }
        this.setTime(0);
    }
}
