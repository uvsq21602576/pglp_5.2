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

import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;

/**
 * Classe DAO pour Groupe.
 * @author Flora
 */
public class GroupeDAOFile extends DAO<Groupe> {
    /**
     * Dossier contenant les fichiers.
     * Fichiers représentant les Groupe.
     */
    private String dossier;

    /**
     * Constructeur.
     * Crée un DAO pour Groupe à partir d'un chemin de dossier.
     * @param dossierDB chemin du dossier
     */
    public GroupeDAOFile(final String dossierDB) {
        dossier = dossierDB + "Groupe\\";
    }

    /**
     * Pour la création.
     * Ecrit un fichier représentant le Groupe.
     * @param obj Groupe à créer
     * @return Groupe créé, ou null en cas d'erreur
     */
    @Override
    public Groupe create(final Groupe obj) {
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
     * Retourne le Groupe avec comme identifiant id,
     * présent dans le dossier de la base de donnée simulée.
     * @param id Identifiant du Groupe à trouver
     * @return Groupe trouvé, ou null en cas d'erreur
     */
    @Override
    public Groupe find(final String id) {
        String nomFichier = dossier + id + ".ser";
        try {
            Groupe g = deserialize(nomFichier);
            if (!id.equals(g.getId() + "")) {
                return null;
            } else {
                return g;
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pour la modification.
     * Réécrit le fichier réprésentant ce Groupe (repéré par son id), en
     * ayant appliqué les modifications présentes dans obj.
     * @param obj Groupe modifié à réécrire
     * @return Groupe modifié, ou null en cas d'erreur
     */
    @Override
    public Groupe update(final Groupe obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Telephone non supprimé car inexistant.");
            return null;
        }
        Groupe g;
        try {
            g = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        if (obj.getId() != g.getId()) {
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
     * Suprime le fichier repreésentant le Groupe obj.
     * @param obj Groupe à supprimer
     */
    @Override
    public void delete(final Groupe obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Personnel non supprimé car inexistant.");
            return;
        }
        Groupe g;
        try {
            g = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }
        if (obj.getId() != g.getId()) {
            return;
        }
        if (!obj.equals(g)) {
            return;
        }
        f.delete();
    }

    /**
     * Methode pour sérialiser un Groupe t.
     * Crée le fichier nomFichier contenant la sérialisation effectuée.
     * @param g Groupe à sérialiser
     * @param nomFichier Chemin du fichier où écrire
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de l'écriture.
     */
    private void serialize(final Groupe g, final String nomFichier)
            throws FileNotFoundException, IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(
                        new FileOutputStream(new File(nomFichier))))) {
            out.writeObject(g);
        }
    }

    /**
     * Methode pour désérialiser un Groupe.
     * Lis le fichier nomFichier contenant la sérialisation précédemment
     * effectuée.
     * @param nomFichier Chemin du dossier contenant le Groupe
     * @return Groupe stocké à nomFichier, ou null en cas d'erreur.
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de la lecture.
     * @throws ClassNotFoundException Si la classe du fichier de sérialisation
     *         est inconnue de l'application.
     */
    private Groupe deserialize(final String nomFichier)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File(nomFichier))))) {
            Object o = in.readObject();
            if (o instanceof Groupe) {
                return (Groupe) o;
            }
        }
        return null;
    }
}
