package pet.model;

import java.io.File;
import java.io.IOException;

import javax.swing.event.ChangeListener;
import javax.swing.text.Document;


/**
 * Le modèle est une entité qui maintient :
 * <ul>
 * <li>un chemin d'accès à un fichier (File getFile())</li>
 * <li>un document contenant le texte à afficher (Document getDocument())</li>
 * </ul>
 * et qui indique si le document et le fichier (lorsqu'ils existent) sont
 *  en phase (boolean isSynchronized()).
 * Notez que le modèle n'a une chance d'être synchronisé que s'il contient
 *  un document et un chemin.
 * 
 * @inv <pre>
 *     getFile() != null
 *         ==> getFile().isFile()
 *             getFile().canRead()
 *             getFile().canWrite()
 *     isSynchronized() <==>
 *         getDocument != null
 *         getFile() != null
 *         la séquence de caractères du document n'a pas évolué depuis
 *             le dernier chargement ou la dernière sauvegarde </pre>
 * 
 * @cons <pre>
 *     $ARGS$ -
 *     $POST$
 *         getDocument() == null
 *         getFile() == null
 *         !isSynchronized() </pre>
 */
public interface PetModel {
    
    // REQUETES

    /**
     * Les observateurs du modèle.
     */
    ChangeListener[] getChangeListeners();

    /**
     * Le document associé à ce modèle.
     */
    Document getDocument();
    
    /**
     * Le chemin d'accès à un fichier, associé à ce modèle.
     */
    File getFile();

    /**
     * Le modèle est-il synchronisé ?
     * La réponse vaut true ssi il existe un document et un chemin et qu'alors
     *  la séquence de caractères du document n'a pas évolué depuis le dernier
     *  chargement ou la dernière sauvegarde.
     */
    boolean isSynchronized();
    
    // COMMANDES
    
    /**
     * Ajoute un ChangeListener au modèle.
     * @pre
     *     listener != null
     * @post <pre>
     *     getChangeListeners() contient listener </pre>
     */
    void addChangeListener(ChangeListener listener);
    
    /**
     * Vide le contenu du document.
     * @pre <pre>
     *     getDocument() != null </pre>
     * @post <pre>
     *     getFile() == old getFile()
     *     getDocument().getLength() == 0
     *     !isSynchronized() </pre>
     */
    void clearDocument();

    /**
     * Retire un ChangeListener du modèle.
     * @pre
     *     listener != null
     * @post <pre>
     *     getChangeListeners() ne contient pas listener </pre>
     */
    void removeChangeListener(ChangeListener listener);

    /**
     * Supprime du modèle le document et le chemin courants.
     * @post <pre>
     *     getDocument() == null
     *     getFile() == null
     *     !isSynchronized() </pre>
     */
    void removeDocAndFile();

    /**
     * Recharge le contenu du fichier courant dans le document courant.
     * @pre <pre>
     *     getFile() != null
     *     getDocument() != null </pre>
     * @post <pre>
     *     getFile() == old getFile()
     *     getDocument() est toujours le même objet, mais son contenu est
     *         le même que celui du fichier de chemin getFile()
     *     isSynchronized() </pre>
     * @throws IOException
     *     S'il s'est produit une erreur durant la lecture du fichier courant
     */
    void resetCurrentDocWithCurrentFile() throws IOException;

    /**
     * Sauvegarde le contenu du document courant dans le fichier courant.
     * @pre <pre>
     *     getFile() != null
     *     getDocument() != null </pre>
     * @post <pre>
     *     getDocument() == old getDocument()
     *     getFile() == old getFile()
     *     le contenu du fichier de chemin getFile()
     *         est le même que celui de getDocument()
     *     isSynchronized() </pre>
     * @throws IOException
     *     S'il s'est produit une erreur durant l'écriture dans le fichier
     *      courant
     */
    void saveCurrentDocIntoCurrentFile() throws IOException;

    /**
     * Sauvegarde le contenu du document courant dans le fichier associé au
     *  chemin f, qui devient ensuite le chemin courant.
     * @pre <pre>
     *     getDocument() != null
     *     f != null && f.isFile() && f.canRead() && f.canWrite() </pre>
     * @post <pre>
     *     getDocument() == old getDocument()
     *     getFile() == f
     *     le contenu du fichier de chemin getFile()
     *         est le même que celui de getDocument()
     *     isSynchronized() </pre>
     * @throws IOException
     *     S'il s'est produit une erreur durant l'écriture dans le fichier
     *      courant
     */
    void saveCurrentDocIntoFile(File f) throws IOException;

    /**
     * Crée un nouveau document à partir du texte contenu dans le fichier
     *  associé à f, puis mémorise ce dernier comme chemin courant.
     * @pre <pre>
     *     f != null && f.isFile() && f.canRead() && f.canWrite() </pre>
     * @post <pre>
     *     getDocument() != old getDocument()
     *     getDocument() != null
     *     getFile() == f
     *     le contenu de getDocument()
     *         est le même que celui du fichier de chemin getFile()
     *     isSynchronized() </pre>
     * @throws IOException
     *     S'il s'est produit une erreur durant la lecture du fichier courant
     */
    void setNewDocAndNewFile(File f) throws IOException;

    /**
     * Crée un nouveau document à partir du texte contenu dans le fichier
     *  associé à f, et supprime le chemin courant du modèle.
     * @pre <pre>
     *     f != null && f.isFile() && f.canRead() </pre>
     * @post <pre>
     *     getDocument() != old getDocument()
     *     getDocument() != null
     *     getFile() == null
     *     le contenu de getDocument()
     *         est le même que celui du fichier de chemin f
     *     !isSynchronized() </pre>
     * @throws IOException
     *     S'il s'est produit une erreur durant la lecture du fichier courant
     */
    void setNewDocFromFile(File f) throws IOException;

    /**
     * Crée un nouveau document vide et supprime le chemin courant du modèle.
     * @post <pre>
     *     getDocument() != old getDocument()
     *     getDocument() != null && getDocument().getLength() == 0
     *     getFile() == null
     *     !isSynchronized() </pre>
     */
    void setNewDocWithoutFile();
}
