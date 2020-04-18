package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Classe DAO pour Telephone.
 * @author Flora
 */
public class TelephoneDAOFile extends DAO<Telephone> {
    /**
     * Dossier contenant les fichiers.
     * Fichiers représentant les Telephone.
     */
    private String dossier;

    /**
     * Constructeur.
     * Crée un DAO pour Telephone à partir d'un chemin de dossier.
     * @param dossierDB chemin du dossier
     */
    public TelephoneDAOFile(final String dossierDB) {
        dossier = dossierDB + "Telephone\\";
    }

    /**
     * Pour la création.
     * Ecrit un fichier représentant le telephone.
     * @param obj Telephone à créer
     * @return Telephone créé, ou null en cas d'erreur
     */
    @Override
    public Telephone create(final Telephone obj) {
        String chemin = dossier;
        if (!new File(chemin).exists()) {
            new File(chemin).mkdirs();
        }
        chemin += obj.getId() + ".ser";
        File f = new File(chemin);
        if (f.exists()) {
            return null;
        }
        try {
            serialize(obj, chemin);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    /**
     * Pour la recherche.
     * Retourne le telephone avec omme identifiant id,
     * présent dans le dossier de la base de donnée simulée.
     * @param id Identifiant du telephone à trouver
     * @return Telephone trouvé, ou null en cas d'erreur
     */
    @Override
    public Telephone find(final String id) {
        String nomFichier = dossier + id + ".ser";
        try {
            Telephone t = deserialize(nomFichier);
            if (!id.equals(t.getId() + "")) {
                return null;
            } else {
                return t;
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pour la modification.
     * Réécrit le fichier réprésentant ce telephone (repéré par son id), en
     * ayant appliqué les modifications présentes dans obj.
     * @param obj Telephone modifié à réécrire
     * @return Telephone modifié, ou null en cas d'erreur
     */
    @Override
    public Telephone update(final Telephone obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Telephone non supprimé car inexistant.");
            return null;
        }
        Telephone t;
        try {
            t = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        if (obj.getId() != t.getId()) {
            return null;
        }

        try {
            serialize(obj, nomFichier);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    /**
     * Pour la suppression.
     * Suprime le fichier repreésentant le telephone obj.
     * @param obj Telephone à supprimer
     */
    @Override
    public void delete(final Telephone obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Telephone non supprimé car inexistant.");
            return;
        }
        Telephone t;
        try {
            t = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }
        if (obj.getId() != t.getId()) {
            return;
        }
        if (!obj.equals(t)) {
            return;
        }
        f.delete();
    }

    /**
     * Methode pour sérialiser un telehone t.
     * Crée le fichier nomFichier contenant la sérialisation effectuée.
     * @param t Téléphone à sérialiser
     * @param nomFichier Chemin du fichier où écrire
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de l'écriture.
     */
    private void serialize(final Telephone t, final String nomFichier)
            throws FileNotFoundException, IOException {
        try (ObjectOutputStream out =
                new ObjectOutputStream(new BufferedOutputStream(
                        new FileOutputStream(new File(nomFichier))))) {
            out.writeObject(t);
        }
    }

    /**
     * Methode pour désérialiser un telehone.
     * Lis le fichier nomFichier contenant la sérialisation précédemment
     * effectuée.
     * @param nomFichier Chemin du dossier contenant le telephone
     * @return Telephone stocké à nomFichier, ou null en cas d'erreur.
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de la lecture.
     * @throws ClassNotFoundException Si la classe du fichier de sérialisation
     *         est inconnue de l'application.
     */
    private Telephone deserialize(final String nomFichier)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(new File(nomFichier))))) {
            Object o = in.readObject();
            if (o instanceof Telephone) {
                return (Telephone) o;
            }
        }
        return null;
    }
}
