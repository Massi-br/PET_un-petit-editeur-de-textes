package pet.gui;

import java.util.EnumMap;
import java.util.Map;

enum Menu {
    FILE("Fichier"),
    EDIT("Édition"),
    QUIT("Quitter");
    
    private String label;

    Menu(String lb) {
        label = lb;
    }

    String label() {
        return label;
    }
    
    static final Map<Menu, Item[]> STRUCT;
    static {
        STRUCT = new EnumMap<Menu, Item[]>(Menu.class); 
        STRUCT.put(Menu.FILE, new Item[] {
                Item.NEW, Item.NEW_FROM_FILE,
                null,
                Item.OPEN, Item.REOPEN,
                null,
                Item.SAVE, Item.SAVE_AS,
                null,
                Item.CLOSE });
        STRUCT.put(Menu.EDIT, new Item[] { Item.CLEAR });
        STRUCT.put(Menu.QUIT, new Item[] { Item.QUIT });
    }
}
