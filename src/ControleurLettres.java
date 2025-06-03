import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;


/**
 * Controleur du clavier
 */
public class ControleurLettres implements EventHandler<ActionEvent> {

    /**
     * modèle du jeu
     */
    private MotMystere modelePendu;
    /**
     * vue du jeu
     */
    private Pendu vuePendu;

    /**
     * @param modelePendu modèle du jeu
     * @param vuePendu vue du jeu
     */
    ControleurLettres(MotMystere modelePendu, Pendu vuePendu){
        this.modelePendu = modelePendu;
        this.vuePendu= vuePendu;
    }

    /**
     * Actions à effectuer lors du clic sur une touche du clavier
     * Il faut donc: Essayer la lettre, mettre à jour l'affichage et vérifier si la partie est finie
     * @param actionEvent l'événement
     */
    @Override
    public void handle(ActionEvent actionEvent) {
        Button bouton = (Button) actionEvent.getTarget();
        String lettre = bouton.getText();
        char lettreChar = lettre.charAt(0);
        this.modelePendu.essaiLettre(lettreChar);
        // Désactive le bouton utilisé
        bouton.setDisable(true);
        // Met à jour l'affichage (mot, pendu, progression, clavier)
        this.vuePendu.majAffichage();
        // La gestion de la victoire/défaite et popup est faite dans majAffichage() de Pendu.java
    }
}
