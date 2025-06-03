import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class DemineurFX extends Application {
    private static final int TAILLE = 8;
    private static final int NB_BOMBES = 10;
    private CaseFX[][] plateau = new CaseFX[TAILLE][TAILLE];
    private boolean gameOver = false;
    private int casesRestantes;
    private boolean premierClic = true;

    @Override
    public void start(Stage stage) {
        GridPane root = new GridPane();
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                CaseFX c = new CaseFX(i, j);
                plateau[i][j] = c;
                root.add(c, j, i);
            }
        }
        // Ajout du bouton pour revenir au pendu
        Button penduBtn = new Button("Partir sur Pendu");
        penduBtn.setStyle("-fx-background-color: #E7B7A7; -fx-text-fill: #222; -fx-font-weight: bold; font-size: 18px; -fx-padding: 10 20 10 20; border-radius: 8px;");
        penduBtn.setOnAction(e -> {
            Stage s = (Stage) ((Button) e.getSource()).getScene().getWindow();
            s.close();
            try {
                String javaHome = System.getProperty("java.home");
                String javaBin = javaHome + java.io.File.separator + "bin" + java.io.File.separator + "java";
                String classpath = System.getProperty("java.class.path");
                String mainClass = "Pendu";
                java.util.List<String> command = new java.util.ArrayList<>();
                command.add(javaBin);
                command.add("-cp");
                command.add(classpath);
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
        VBox vbox = new VBox(10, root, penduBtn);
        vbox.setStyle("-fx-alignment: center;");
        Scene scene = new Scene(vbox);
        stage.setTitle("Démineur JavaFX");
        stage.setScene(scene);
        stage.show();

        // NE PAS placerBombes() ici !
        // NE PAS calculerVoisines() ici !
        casesRestantes = TAILLE * TAILLE - NB_BOMBES;
    }

    private void placerBombes(int excluX, int excluY) {
        int bombes = 0;
        while (bombes < NB_BOMBES) {
            int x = (int)(Math.random() * TAILLE);
            int y = (int)(Math.random() * TAILLE);
            // On évite la case du premier clic et ses voisines
            if (Math.abs(x - excluX) <= 1 && Math.abs(y - excluY) <= 1) continue;
            if (!plateau[x][y].bombe) {
                plateau[x][y].bombe = true;
                bombes++;
            }
        }
    }

    private void calculerVoisines() {
        for (int i = 0; i < TAILLE; i++) {
            for (int j = 0; j < TAILLE; j++) {
                int bombes = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int ni = i + dx, nj = j + dy;
                        if (ni >= 0 && ni < TAILLE && nj >= 0 && nj < TAILLE && !(dx == 0 && dy == 0)) {
                            if (plateau[ni][nj].bombe) bombes++;
                        }
                    }
                }
                plateau[i][j].bombesVoisines = bombes;
            }
        }
    }

    private void devoiler(int x, int y) {
        CaseFX c = plateau[x][y];
        if (c.decouverte || c.marquee) return;
        c.decouvrir();
        casesRestantes--;
        if (c.bombesVoisines == 0 && !c.bombe) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int ni = x + dx, nj = y + dy;
                    if (ni >= 0 && ni < TAILLE && nj >= 0 && nj < TAILLE && !(dx == 0 && dy == 0)) {
                        devoiler(ni, nj);
                    }
                }
            }
        }
    }

    private void finDePartie(boolean gagne) {
        gameOver = true;
        for (int i = 0; i < TAILLE; i++)
            for (int j = 0; j < TAILLE; j++)
                plateau[i][j].revelerFin();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(gagne ? "Victoire !" : "Perdu !");
        alert.setHeaderText(null);
        alert.setContentText(gagne ? "Bravo, vous avez gagné !" : "BOOM ! Vous avez perdu.");
        alert.showAndWait();
        // Fermer la fenêtre après avoir cliqué sur le popup
        Stage stage = (Stage) plateau[0][0].getScene().getWindow();
        stage.close();
    }

    private class CaseFX extends Button {
        final int x, y;
        boolean bombe = false;
        boolean decouverte = false;
        boolean marquee = false;
        int bombesVoisines = 0;

        CaseFX(int x, int y) {
            this.x = x; this.y = y;
            setPrefSize(40, 40);
            setFont(Font.font(18));
            setStyle("-fx-padding: 0; -fx-alignment: center;");
            setOnMouseClicked(e -> {
                if (gameOver) return;
                if (e.getButton() == MouseButton.SECONDARY) {
                    if (!decouverte) {
                        marquee = !marquee;
                        if (marquee) {
                            javafx.scene.image.ImageView drapeau = new javafx.scene.image.ImageView(new javafx.scene.image.Image("file:img/drapeau.png"));
                            drapeau.setFitWidth(36);
                            drapeau.setFitHeight(36);
                            setGraphic(drapeau);
                        } else {
                            setGraphic(null);
                        }
                        setText("");
                    }
                } else if (e.getButton() == MouseButton.PRIMARY) {
                    if (marquee || decouverte) return;
                    if (premierClic) {
                        // On place les bombes APRES le premier clic, en évitant la zone du clic
                        for (int i = 0; i < TAILLE; i++)
                            for (int j = 0; j < TAILLE; j++)
                                plateau[i][j].bombe = false;
                        placerBombes(x, y);
                        calculerVoisines();
                        premierClic = false;
                    }
                    if (bombe) {
                        javafx.scene.image.ImageView bombeImg = new javafx.scene.image.ImageView(new javafx.scene.image.Image("file:img/bombe.png"));
                        bombeImg.setFitWidth(36);
                        bombeImg.setFitHeight(36);
                        setGraphic(bombeImg);
                        setText("");
                        setStyle("-fx-background-color: #f55; -fx-padding: 0; -fx-alignment: center;");
                        finDePartie(false);
                    } else {
                        devoiler(x, y);
                        if (casesRestantes == 0) finDePartie(true);
                    }
                }
            });
        }
        void decouvrir() {
            decouverte = true;
            setDisable(true);
            if (bombe) {
                javafx.scene.image.ImageView bombeImg = new javafx.scene.image.ImageView(new javafx.scene.image.Image("file:img/bombe.png"));
                bombeImg.setFitWidth(36);
                bombeImg.setFitHeight(36);
                setGraphic(bombeImg);
                setText("");
            } else if (bombesVoisines > 0) {
                setText(Integer.toString(bombesVoisines));
                setGraphic(null);
            } else {
                setText("");
                setGraphic(null);
            }
        }
        void revelerFin() {
            if (bombe) {
                javafx.scene.image.ImageView bombeImg = new javafx.scene.image.ImageView(new javafx.scene.image.Image("file:img/bombe.png"));
                bombeImg.setFitWidth(36);
                bombeImg.setFitHeight(36);
                setGraphic(bombeImg);
                setText("");
            }
            setDisable(true);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
