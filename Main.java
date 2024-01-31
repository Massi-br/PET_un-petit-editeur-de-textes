package pet;

import javax.swing.SwingUtilities;

import pet.gui.Pet;

public final class Main {

    private Main() {
        // rien
    }
    
    // POINT D'ENTREE

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Pet().display();
            }
        });
    }
}
