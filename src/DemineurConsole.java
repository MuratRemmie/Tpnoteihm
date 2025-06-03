import java.util.Random;
import java.util.Scanner;

public class DemineurConsole {
    private final int nbLignes;
    private final int nbColonnes;
    private final int nbBombes;
    private final Case[][] plateau;
    private boolean gameOver = false;
    private int casesRestantes;

    public DemineurConsole(int nbLignes, int nbColonnes, int nbBombes) {
        this.nbLignes = nbLignes;
        this.nbColonnes = nbColonnes;
        this.nbBombes = nbBombes;
        this.plateau = new Case[nbLignes][nbColonnes];
        initialiserPlateau();
    }

    private void initialiserPlateau() {
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                plateau[i][j] = new Case();
            }
        }
        placerBombes();
        calculerVoisines();
        casesRestantes = nbLignes * nbColonnes - nbBombes;
    }

    private void placerBombes() {
        Random rand = new Random();
        int bombesPlacees = 0;
        while (bombesPlacees < nbBombes) {
            int x = rand.nextInt(nbLignes);
            int y = rand.nextInt(nbColonnes);
            if (!plateau[x][y].contientUneBombe()) {
                plateau[x][y].poseBombe();
                bombesPlacees++;
            }
        }
    }

    private void calculerVoisines() {
        for (int i = 0; i < nbLignes; i++) {
            for (int j = 0; j < nbColonnes; j++) {
                int bombes = 0;
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int ni = i + dx, nj = j + dy;
                        if (ni >= 0 && ni < nbLignes && nj >= 0 && nj < nbColonnes && !(dx == 0 && dy == 0)) {
                            if (plateau[ni][nj].contientUneBombe()) bombes++;
                        }
                    }
                }
                plateau[i][j].setNombreBombesVoisines(bombes);
            }
        }
    }

    public void afficher() {
        System.out.print("   ");
        for (int j = 0; j < nbColonnes; j++) System.out.print(j + " ");
        System.out.println();
        for (int i = 0; i < nbLignes; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < nbColonnes; j++) {
                System.out.print(plateau[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void jouer(int x, int y) {
        if (x < 0 || x >= nbLignes || y < 0 || y >= nbColonnes || plateau[x][y].estDecouverte()) {
            System.out.println("Coup invalide.");
            return;
        }
        if (plateau[x][y].contientUneBombe()) {
            plateau[x][y].reveler();
            gameOver = true;
            System.out.println("BOOM ! Vous avez perdu.");
            return;
        }
        devoiler(x, y);
        if (casesRestantes == 0) {
            gameOver = true;
            System.out.println("Bravo ! Vous avez gagné !");
        }
    }

    private void devoiler(int x, int y) {
        if (x < 0 || x >= nbLignes || y < 0 || y >= nbColonnes || plateau[x][y].estDecouverte()) return;
        plateau[x][y].reveler();
        casesRestantes--;
        if (plateau[x][y].getNombreBombesVoisines() == 0) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx != 0 || dy != 0) devoiler(x + dx, y + dy);
                }
            }
        }
    }

    public boolean estTerminee() {
        return gameOver;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DemineurConsole jeu = new DemineurConsole(8, 8, 10);
        while (!jeu.estTerminee()) {
            jeu.afficher();
            System.out.print("Entrez la ligne : ");
            int x = sc.nextInt();
            System.out.print("Entrez la colonne : ");
            int y = sc.nextInt();
            jeu.jouer(x, y);
        }
        jeu.afficher();
        System.out.println("Fin de la partie.");
    }

    // Classe interne pour une case du démineur
    private static class Case {
        private boolean bombe = false;
        private boolean decouverte = false;
        private int bombesVoisines = 0;

        public void poseBombe() { bombe = true; }
        public boolean contientUneBombe() { return bombe; }
        public boolean estDecouverte() { return decouverte; }
        public void setNombreBombesVoisines(int n) { bombesVoisines = n; }
        public int getNombreBombesVoisines() { return bombesVoisines; }
        public void reveler() { decouverte = true; }
        public String toString() {
            if (!decouverte) return ".";
            if (bombe) return "*";
            return bombesVoisines == 0 ? " " : Integer.toString(bombesVoisines);
        }
    }
}
