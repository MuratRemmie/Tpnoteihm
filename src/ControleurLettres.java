import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
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
        this.vuePendu.majAffichage();
        
        if(modelePendu.gagne()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Partie gagnée");
            alert.setHeaderText("Bravo !");
            alert.setContentText("Tu as gagné, félicitations !");
            alert.showAndWait();
        }else{
            if(modelePendu.perdu()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Partie perdue");
                alert.setHeaderText("Dommage !");
                alert.setContentText("Tu as perdu, le mot était: " + modelePendu.getMotATrouve());
                alert.showAndWait();
            }
        }
        
    }
}
