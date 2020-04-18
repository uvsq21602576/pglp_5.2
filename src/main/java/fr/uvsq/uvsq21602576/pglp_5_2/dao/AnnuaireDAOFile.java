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

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;

/**
 * Classe DAO pour Annuaire.
 * @author Flora
 */
public class AnnuaireDAOFile extends DAO<Annuaire> {
    /**
     * Dossier contenant les fichiers.
     * Fichiers représentant les Annuaire.
     */
    private String dossier;

    /**
     * Constructeur.
     * Crée un DAO pour Annuaire à partir d'un chemin de dossier.
     * @param dossierDB chemin du dossier
     */
    public AnnuaireDAOFile(final String dossierDB) {
        dossier = dossierDB + "Annuaire\\";
    }

    /**
     * Pour la création.
     * Ecrit un fichier représentant le Annuaire.
     * @param obj Annuaire à créer
     * @return Annuaire créé, ou null en cas d'erreur
     */
    @Override
    public Annuaire create(final Annuaire obj) {
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
     * Retourne le Annuaire avec comme identifiant id,
     * présent dans le dossier de la base de donnée simulée.
     * @param id Identifiant du Annuaire à trouver
     * @return Annuaire trouvé, ou null en cas d'erreur
     */
    @Override
    public Annuaire find(final String id) {
        String nomFichier = dossier + id + ".ser";
        try {
            Annuaire a = deserialize(nomFichier);
            if (!id.equals(a.getId() + "")) {
                return null;
            } else {
                return a;
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pour la modification.
     * Réécrit le fichier réprésentant ce Annuaire (repéré par son id), en
     * ayant appliqué les modifications présentes dans obj.
     * @param obj Annuaire modifié à réécrire
     * @return Annuaire modifié, ou null en cas d'erreur
     */
    @Override
    public Annuaire update(final Annuaire obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Annuaire non supprimé car inexistant.");
            return null;
        }
        Annuaire a;
        try {
            a = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        if (obj.getId() != a.getId()) {
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
     * Suprime le fichier repreésentant le Annuaire obj.
     * @param obj Annuaire à supprimer
     */
    @Override
    public void delete(final Annuaire obj) {
        String nomFichier = dossier + obj.getId() + ".ser";
        File f;
        f = new File(nomFichier);
        if (!f.exists()) {
            System.err.println("Telephone non supprimé car inexistant.");
            return;
        }
        Annuaire a;
        try {
            a = deserialize(nomFichier);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return;
        }
        if (obj.getId() != a.getId()) {
            return;
        }
        if (!obj.equals(a)) {
            return;
        }
        f.delete();
    }

    /**
     * Methode pour sérialiser un Annuaire t.
     * Crée le fichier nomFichier contenant la sérialisation effectuée.
     * @param a Annuaire à sérialiser
     * @param nomFichier Chemin du fichier où écrire
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de l'écriture.
     */
    private void serialize(final Annuaire a, final String nomFichier)
            throws FileNotFoundException, IOException {
        try (ObjectOutputStream out =
                new ObjectOutputStream(new BufferedOutputStream(
                        new FileOutputStream(new File(nomFichier))))) {
            out.writeObject(a);
        }
    }

    /**
     * Methode pour désérialiser un Annuaire.
     * Lis le fichier nomFichier contenant la sérialisation précédemment
     * effectuée.
     * @param nomFichier Chemin du dossier contenant le Annuaire
     * @return Annuaire stocké à nomFichier, ou null en cas d'erreur.
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de la lecture.
     * @throws ClassNotFoundException Si la classe du fichier de sérialisation
     *         est inconnue de l'application.
     */
    private Annuaire deserialize(final String nomFichier)
            throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                new ObjectInputStream(new BufferedInputStream(
                        new FileInputStream(new File(nomFichier))))) {
            Object o = in.readObject();
            if (o instanceof Annuaire) {
                return (Annuaire) o;
            }
        }
        return null;
    }
}
