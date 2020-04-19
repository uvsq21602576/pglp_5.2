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

import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;

/**
 * Classe DAO pour Personnel.
 * @author Flora
 */
public class PersonnelDAOFile extends DAO<Personnel> {
    /**
     * Dossier contenant les fichiers.
     * Fichiers représentant les Personnel.
     */
    private String dossier;

    /**
     * Constructeur.
     * Crée un DAO pour Personnel à partir d'un chemin de dossier.
     * @param dossierDB chemin du dossier
     */
    public PersonnelDAOFile(final String dossierDB) {
        dossier = dossierDB + "Personnel\\";
    }

    /**
     * Pour la création.
     * Ecrit un fichier représentant le Personnel.
     * @param obj Personnel à créer
     * @return Personnel créé, ou null en cas d'erreur
     */
    @Override
    public Personnel create(final Personnel obj) {
        String chemin = dossier;
        if (!new File(chemin).exists()) {
            if (!new File(chemin).mkdirs()) {
                return null;
            }
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
     * Retourne le Personnel avec comme identifiant id,
     * présent dans le dossier de la base de donnée simulée.
     * @param id Identifiant du Personnel à trouver
     * @return Personnel trouvé, ou null en cas d'erreur
     */
    @Override
    public Personnel find(final String id) {
        String nomFichier = dossier + id + ".ser";
        try {
            Personnel p = deserialize(nomFichier);
            if (!id.equals(p.getId() + "")) {
                return null;
            } else {
                return p;
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pour la modification.
     * Réécrit le fichier réprésentant ce Personnel (repéré par son id), en
     * ayant appliqué les modifications présentes dans obj.
     * @param obj Personnel modifié à réécrire
     * @return Personnel modifié, ou null en cas d'erreur
     */
    @Override
    public Personnel update(final Personnel obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Personnel non modifié car inexistant.");
            return null;
        }
        Personnel p;
        try {
            p = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        if (obj.getId() != p.getId()) {
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
     * Suprime le fichier repreésentant le Personnel obj.
     * @param obj Personnel à supprimer
     */
    @Override
    public void delete(final Personnel obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Personnel non supprimé car inexistant.");
            return;
        }
        Personnel p;
        try {
            p = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }
        if (obj.getId() != p.getId()) {
            return;
        }
        if (!obj.equals(p)) {
            return;
        }
        if (f.delete()) {
            System.out.println("Personnel supprimé.");
        } else {
            System.err.println("Personnel non supprimé, erreur.");
        }
    }

    /**
     * Methode pour sérialiser un Personnel t.
     * Crée le fichier nomFichier contenant la sérialisation effectuée.
     * @param p Personnel à sérialiser
     * @param nomFichier Chemin du fichier où écrire
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de l'écriture.
     */
    private void serialize(final Personnel p, final String nomFichier)
            throws FileNotFoundException, IOException {
        try (ObjectOutputStream out =
                new ObjectOutputStream(new BufferedOutputStream(
                        new FileOutputStream(new File(nomFichier))))) {
            out.writeObject(p);
        }
    }

    /**
     * Methode pour désérialiser un Personnel.
     * Lis le fichier nomFichier contenant la sérialisation précédemment
     * effectuée.
     * @param nomFichier Chemin du dossier contenant le Personnel
     * @return Personnel stocké à nomFichier, ou null en cas d'erreur.
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de la lecture.
     * @throws ClassNotFoundException Si la classe du fichier de sérialisation
     *         est inconnue de l'application.
     */
    private Personnel deserialize(final String nomFichier)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(new File(nomFichier))))) {
            Object o = in.readObject();
            if (o instanceof Personnel) {
                return (Personnel) o;
            }
        }
        return null;
    }
}
