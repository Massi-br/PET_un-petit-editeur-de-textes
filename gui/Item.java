package pet.gui;

enum Item {
    NEW("Créer"),
    NEW_FROM_FILE("Créer à partir de..."),
    OPEN("Ouvrir..."),
    REOPEN("Réouvrir"),
    SAVE("Sauvegarder"),
    SAVE_AS("Sauvegarder comme..."),
    CLOSE("Fermer"),
    CLEAR("Effacer"),
    QUIT("Quitter");
    
    private String label;

    Item(String lb) {
        label = lb;
    }
    
    String label() {
        return label;
    }
}
