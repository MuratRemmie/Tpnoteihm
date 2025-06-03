import java.util.Scanner;

public class MonExecutable{
   public static void main(String[] args) {
       
   
        Scanner scanner = new Scanner(System.in);
        MotMystere m = new MotMystere("pomme", 5, 7);
        System.out.println("Bienvenue au jeu du Pendu !");
        while (!m.gagne() && m.getNbLettresRestantes() > 0) {
            System.out.println("Mot à deviner : " + m.getMotCrypte());
            System.out.println("Lettres essayées : " + m.getLettresEssayees());
            System.out.println("Essais restants : " + m.getNbLettresRestantes());
            System.out.print("Entrez une lettre : ");
            String input = scanner.nextLine();
            char lettre = input.charAt(0);
            m.essaiLettre(lettre);
        }

        if (m.gagne()) {
            System.out.println("Bravo !!! tu as gagné  le mot : " + m.getMotATrouver());
        } else {
            System.out.println("Perdu ! Le mot était : " + m.getMotATrouver());
        }
        scanner.close();

    }

}