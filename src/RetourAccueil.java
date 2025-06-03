import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import java.util.Optional;

/**
 * Contrôleur à activer lorsque l'on clique sur le bouton Accueil
 */
public class RetourAccueil implements EventHandler<ActionEvent> {
    /**
     * modèle du jeu
     */
    private MotMystere modelePendu;
    /**
     * vue du jeu
     **/
    private Pendu vuePendu;

    /**
     * @param modelePendu modèle du jeu
     * @param vuePendu vue du jeu
     */
    public RetourAccueil(MotMystere modelePendu, Pendu vuePendu) {
        this.modelePendu = modelePendu;
        this.vuePendu = vuePendu;
        
    }


    /**
     * L'action consiste à retourner sur la page d'accueil. Il faut vérifier qu'il n'y avait pas une partie en cours
     * @param actionEvent l'événement action
     */
    @Override
    public void handle(ActionEvent actionEvent) {
        if (this.modelePendu.partieEnCours()) {
            Optional<ButtonType> reponse = this.vuePendu.popUpPartieEnCours().showAndWait();
            if (reponse.isPresent() && reponse.get().equals(ButtonType.YES)) {  
                System.out.println("Ok ! Retour à l'accueil");
                this.vuePendu.retourAccueil();
            } else {
                System.out.println("D'ac ! Reste dans la partie en cours");
            }
        } else {
            System.out.println("Retour à l'accueil");
            this.vuePendu.retourAccueil();
        }
    }
}
