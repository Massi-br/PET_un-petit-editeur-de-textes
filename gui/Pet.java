package pet.gui;


import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.FileChooserUI;
import javax.swing.text.Document;

import pet.model.PetModel;
import pet.model.StdPetModel;
import util.Contract;

public class Pet {
    
    private final JFrame frame;
    private final JLabel statusBar;
    private final JTextArea editor;
    private final JScrollPane scroller;
    private final PetModel model;
    private final Map<Item, JMenuItem> menuItems;

    // CONSTRUCTEUR
    
    public Pet() {
        // MODELE
        model = new StdPetModel();
        // VUE
        frame = buildMainFrame();
        editor = buildEditor();
        scroller = new JScrollPane();
        statusBar = new JLabel();
        menuItems = buildMenuItemsMap();
        placeMenuItemsAndMenus();
        placeComponents();
        // CONTROLEUR
        connectControllers();
    }
    
    //COMMANDE
    
    public void display() {
        setItemsEnabledState();
        updateStatusBar();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // OUTILS
    
    private JFrame buildMainFrame() {
        final Dimension prefSize = new Dimension(640, 480);
        
        JFrame jf = new JFrame("Petit Éditeur de Texte");
        jf.setPreferredSize(prefSize);
        return jf;
    }
    
    private JTextArea buildEditor() {
        final int fontSize = 14;
        
        JTextArea jta = new JTextArea();
        jta.setBackground(Color.BLACK);
        jta.setForeground(Color.LIGHT_GRAY);
        jta.setCaretColor(Color.RED);
        jta.setFont(new Font("Courier New", Font.PLAIN, fontSize));
        return jta;
    }
    
    /**
     * Création de la correspondance Item -> JMenuItem.
     */
    private Map<Item, JMenuItem> buildMenuItemsMap() {
        @SuppressWarnings("unchecked")
		Map<Item, JMenuItem> menuItemsMap = new EnumMap<Item, JMenuItem>(Item.class);

        for (Item item : Item.values()) {
            JMenuItem jMenuItem = new JMenuItem(item.label());
            menuItemsMap.put(item, jMenuItem);
        }
        return menuItemsMap;
    }
    /**
     * Place les menus et les éléments de menu sur une barre de menus, et cette
     *  barre de menus sur la fenêtre principale.
     */
    private void placeMenuItemsAndMenus() {
    	JMenuBar jmb = new JMenuBar();
    		for (Menu m: Menu.values()) {
    			JMenu jm = new JMenu(m.label());
    			
    				for (Item x: Menu.STRUCT.get(m)) {
    					if( x==null ) {
    						jm.addSeparator();
    					} else {
    						jm.add(menuItems.get(x));
    					}
    				}
    			
    			jmb.add(jm);
    		}
    	frame.setJMenuBar(jmb);
    }
    private void placeComponents() {
        frame.add(scroller, BorderLayout.CENTER);
        
        JPanel p = new JPanel(new GridLayout(1, 0));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED),
                BorderFactory.createEmptyBorder(3, 5, 3, 5)));
        { //--
            p.add(statusBar);
        } //--
        
        frame.add(p, BorderLayout.SOUTH);
    }
    
    private void connectControllers() {
        /*
         * L'opération de fermeture par défaut ne doit rien faire car on se
         *  charge de tout dans un écouteur qui demande confirmation puis
         *  libère les ressources de la fenêtre en cas de réponse positive.
         */
    	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        /*
         * Observateur du modèle.
         */
    	model.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setItemsEnabledState();
				updateScrollerAndEditorComponents();
				updateStatusBar();
			}
		});
        
        /*
         * Écouteurs des items du menu
         */
        menuItems.get(Item.NEW).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(confirmAction()) {
            		model.setNewDocWithoutFile();
            	}
            }
        });
        
        menuItems.get(Item.NEW_FROM_FILE).addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (confirmAction()) {
					File f= selectLoadFile();
					if (f != null) {
						try {
							model.setNewDocFromFile(f);
						} catch (IOException e2) {
							displayError("Erreur de lecture du fichier ");
						}
						
					}
				}
			}
		});
        
       
        menuItems.get(Item.OPEN).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
				if (confirmAction()) {
					File f= selectLoadFile();
					if (f != null) {
						try {
							model.setNewDocAndNewFile(f);
						} catch (IOException e2) {
							displayError("Erreur de lecture du fichier ");
						}
						
					}
				}
			}
		});
        menuItems.get(Item.REOPEN).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(confirmAction()){
            		File f= selectLoadFile();
            		if (f != null) {
            			try {
            				model.resetCurrentDocWithCurrentFile();
						} catch (IOException e2) {
							displayError("Erreur de lecture du fichier ");
						}
					}
            		
            	}
            }
        });
        menuItems.get(Item.SAVE).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
            		if (confirmReplaceContent(model.getFile())) {
            			model.saveCurrentDocIntoCurrentFile();
            		}
				} catch (IOException e2) {
					displayError("Erreur d'ecriture dans le fichier ");
				}
            }
        });
        menuItems.get(Item.SAVE_AS).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	File f = selectSaveFile();
            	try {
            		model.saveCurrentDocIntoFile(f);
				} catch (IOException e2) {
					displayError("Erreur d'ecriture dans le fichier ");
				}
            	
            }
        });
        menuItems.get(Item.CLOSE).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(confirmAction()) {
            		model.removeDocAndFile(); 
            	}
            }
        });
        menuItems.get(Item.CLEAR).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(confirmAction()) {
            		model.clearDocument();
            	}
            }
        });
        menuItems.get(Item.QUIT).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if (!model.isSynchronized() && model.getDocument() != null) {
            		if (confirmAction()) {
            			if (model.getFile() != null) {
            				if (confirmSave()) {
								try {
									model.saveCurrentDocIntoCurrentFile();
								} catch (IOException e2) {
									displayError("Le fichier n'a pas été sauvegardé");
									return;
								}
							}
							
						}else {
							if (confirmSave()) {
								try {
									File file = selectSaveFile();
									if (file == null) {
										displayError("Aucun fichier selectioné ");
										return; 
									}
									model.saveCurrentDocIntoFile(file);
								} catch (IOException e2) {
									displayError("Le fichier n'a pas été sauvegardé");
									return;
								}								
							}
						}
					}
            		frame.dispose();
                	System.exit(0);
				}else {
					frame.dispose();
				}
            }
        });
    }
    
    /**
     * Gère l'état de la disponibilité des éléments du menu en fonction de
     *  l'état du modèle.
     */
    private void setItemsEnabledState() {
    	Document d =model.getDocument();
    	File f = model.getFile();
    	boolean b =d != null && f!=null && !model.isSynchronized();
    	
    	for (Item item : Item.values()) {
			menuItems.get(item).setEnabled(true);
		}
    	menuItems.get(Item.CLEAR).setEnabled(d != null && d.getLength() > 0);
    	menuItems.get(Item.REOPEN).setEnabled(b);
    	menuItems.get(Item.SAVE).setEnabled(b);
    	menuItems.get(Item.SAVE_AS).setEnabled(d != null);
    	menuItems.get(Item.CLOSE).setEnabled(d != null);
    }
    
    /**
     * Met à jour le Viewport du JScrollPane en fonction de la présence d'un
     *  document dans le modèle.
     * Remplace le document de la zone de texte par celui du modèle quand c'est
     *  nécessaire.
     */
    @SuppressWarnings("unused")
	private void updateScrollerAndEditorComponents() { 
    	Document modelDoc=model.getDocument();
    	Document editorDoc=editor.getDocument();
    	if (modelDoc == null){
    		scroller.setViewportView(null);
    	}else {
        	if (editorDoc != modelDoc) {
    			editor.setDocument(modelDoc);
    		}
			scroller.setViewportView(editor);
		}
    }
    
    /**
     * Met à jour la barre d'état.
     */
    private void updateStatusBar() {
    	File f = model.getFile();
    	
    	StringBuffer rslt= new StringBuffer("Fichier : ");
    	if ((model.getDocument() != null && f== null) || !model.isSynchronized()) {
			rslt.append("* ");
		}
		if (f == null) 	{
			rslt.append("<aucun>");
		}else {
			rslt.append(f.getAbsolutePath());
		}
    	statusBar.setText(rslt.toString());
    }
    
    /**
     * Demande une confirmation de poursuite d'action.
     * @post
     *     result == true <==>
     *         le modèle était synchronisé
     *         || il n'y avait pas de document dans le modèle
     *         || le document était en cours d'édition mais l'utilisateur
     *            a répondu positivement à la demande de confirmation
     */
    private boolean confirmAction() { 
    	/*qst : si le modele est synch mais l'utilisateur tape non ??*/
    	
    	int choix = JOptionPane.showConfirmDialog(null, "Voulez-vous continuer ?", "Confirmation", JOptionPane.YES_NO_OPTION);
    	if (model.isSynchronized() || model.getDocument() == null ||(choix == JOptionPane.YES_OPTION)) {
    		return true;
		}
    	return false;
    }
    private boolean confirmSave() {
    	return JOptionPane.showConfirmDialog(null, 
        		"Voulez-vous sauvegarder?",
        		"Confirmation",
        		JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    /**
     * Demande une confirmation d'écrasement de fichier.

     * @pre
     *     f != null
     * @post
     *     result == true <==>
     *         le fichier n'existe pas
     *         || le fichier existe mais l'utilisateur a répondu positivement
     *            à la demande de confirmation
     */
    @SuppressWarnings("unused") 
	private boolean confirmReplaceContent(File f) {
    	Contract.checkCondition(f != null);
    	
    	return ( !f.exists() || confirmAction())? true:false; 
    }
    
    /**
     * Toute erreur de l'application due au comportement de l'utilisateur doit
     *  être interceptée et transformée en message présenté dans une boite de
     *  dialogue.
     */
    @SuppressWarnings("unused")
	private void displayError(String m) {
    	JOptionPane.showMessageDialog(null, m, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Permet au client de sélectionner un fichier de sauvegarde en choisissant
     *  un nom de fichier à l'aide d'un JFileChooser.
     * Si le nom de fichier choisi n'existe pas encore, un nouveau fichier est
     *  créé avec ce nom.
     * Retourne null si et seulement si :
     * - l'utilisateur a annulé l'opération,
     * - le nom choisi correspond à un élément préexistant du système de fichier
     *   mais cet élément n'est pas un fichier
     * - le nom choisi ne correspond pas à un élément préexistant du système de
     *   fichier mais le fichier n'a pas pu être créé.
     * Dans les deux derniers cas, une boite de dialogue vient de plus décrire
     *  le problème.
     */
    @SuppressWarnings("unused")
	private File selectSaveFile() {
    	JFileChooser fc = new JFileChooser();
    	int choice =fc.showSaveDialog(null);
    	
        if (choice == JFileChooser.APPROVE_OPTION) {
        	File selectedFile = fc.getSelectedFile();
  	
	    	if (!selectedFile.isFile()) {
	                displayError("Le chemin spécifié existe, mais ce n'est pas un fichier.");
	                return null;
	    	}  
        	if (!selectedFile.exists()) {
        		try {
					if (!selectedFile.createNewFile()) {
						displayError("file already exists");
						return null;
					}
				} catch (IOException e) {
					e.printStackTrace();
					displayError("Erreur lors de la création du fichier.");
	                return null;
				}
			}
            return selectedFile; 
        } else {
            return null;
        }
    }
    
    /**
     * Permet au client de sélectionner un fichier à charger en choisissant
     *  un nom de fichier à l'aide d'un JFileChooser.
     * Retourne null si et seulement si :
     * - l'utilisateur a annulé l'opération,
     * - le nom choisi ne correspond pas un fichier existant.
     * Dans ce dernier cas, une boite de dialogue vient de plus décrire
     *  le problème.
     */
    @SuppressWarnings("unused")
	private File selectLoadFile() {
    	JFileChooser fc = new JFileChooser();
    	int choice =fc.showOpenDialog(null);
    	if (choice ==JFileChooser.APPROVE_OPTION) {
    		File selectedFile = fc.getSelectedFile();
    		if (!selectedFile.exists() || !selectedFile.isFile()) {
				displayError("le nom choisi ne correspond pas un fichier existant");
				return null;
			}
    		return selectedFile;
		}else {
			return null;
		}
    }
}
