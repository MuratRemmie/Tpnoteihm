import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

/**
 * Contrôleur pour la validation des paramètres
 */
public class ControleurParametres implements EventHandler<ActionEvent> {
    private Pendu vuePendu;
    private ColorPicker colorPicker;
    private Spinner<Integer> nbMotsSpinner;
    private ComboBox<String> dictCombo;
    private ComboBox<String> policeCombo;

    public ControleurParametres(Pendu vuePendu, ColorPicker colorPicker, Spinner<Integer> nbMotsSpinner, ComboBox<String> dictCombo, ComboBox<String> policeCombo) {
        this.vuePendu = vuePendu;
        this.colorPicker = colorPicker;
        this.nbMotsSpinner = nbMotsSpinner;
        this.dictCombo = dictCombo;
        this.policeCombo = policeCombo;
    }

    @Override
    public void handle(ActionEvent event) {
        // Appliquer la couleur principale
        Color couleur = colorPicker.getValue();
        String couleurHex = String.format("#%02X%02X%02X", (int)(couleur.getRed()*255), (int)(couleur.getGreen()*255), (int)(couleur.getBlue()*255));
        vuePendu.setMainColor(couleurHex);
        // Appliquer la police d'écriture sur les principaux labels
        String police = policeCombo.getValue();
        vuePendu.setMainFont(police);
        // Nombre de mots, dictionnaire : à stocker ou appliquer si besoin
        // ...
        // Retour au menu
        vuePendu.modeAccueil();
    }
}
