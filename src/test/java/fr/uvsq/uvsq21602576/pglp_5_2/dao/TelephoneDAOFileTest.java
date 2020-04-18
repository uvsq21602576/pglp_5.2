package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Classe de test pour telephoneDAO.
 * @author Flora
 */
public class TelephoneDAOFileTest {
    /** FabriqueDAO ayant donné daoT. */
    private static FabriqueDAO fabrique;
    /** nomDossier de la fabrique avant modification pour les tests. */
    private static String nomDossierFabriqueAvant;
    /** nomDossier de la fabrique pour les tests. */
    private static String nomDossierFabriqueTest;
    /** DAO pour Telephone. */
    private static DAO<Telephone> daoT;

    /**
     * Initialise les variables de classes pour les tests.
     * Le dossier utilisé pour les tests est forcément un dossier n'existant pas
     * avant.
     * Il est créé uniquement pour les tests.
     */
    @BeforeClass
    public static void initDAO() {
        fabrique = FabriqueDAO.getFabriqueDAO(FabriqueDAO.TypeDAO.FILE);
        nomDossierFabriqueAvant = ((FabriqueDAOFile) fabrique).getDossierDB();
        int i = 0;
        do {
            ((FabriqueDAOFile) fabrique)
                    .setDossierDB("databaseFileForTestUniquely" + i + "\\");
            nomDossierFabriqueTest =
                    ((FabriqueDAOFile) fabrique).getDossierDB();
            i++;
        } while (new File(nomDossierFabriqueTest).exists());

        daoT = fabrique.getTelephoneDAO();
    }

    /**
     * Pour terminer correctement tous les tests.
     * La fabrique reprend son dossier d'origine.
     * Le dossier utilisé pour tous les tests est entièrement supprimé.
     */
    @AfterClass
    public static void termineDAO() {
        ((FabriqueDAOFile) fabrique).setDossierDB(nomDossierFabriqueAvant);
        deleteAllDossier(new File(nomDossierFabriqueTest));
    }

    /**
     * Supprime le dossier et son contenu.
     * Fonctionne en récursivité.
     * @param dossier Chemin du dossier à supprimer
     * @return Si la délétion à réussie.
     */
    private static boolean deleteAllDossier(final File dossier) {
        if (dossier.isDirectory()) {
            File[] sousDossiers = dossier.listFiles();
            if (sousDossiers != null) {
                for (File f : sousDossiers) {
                    deleteAllDossier(f);
                }
            }
        }
        return dossier.delete();
    }

    /**
     * Test de la création.
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de la lecture.
     * @throws ClassNotFoundException Si la classe du fichier de sérialisation
     *         est inconnue de l'application.
     */
    @Test
    public void createTest()
            throws FileNotFoundException, ClassNotFoundException, IOException {
        Telephone t = new Telephone(1, "0678", "portable");
        daoT.create(t);

        String nomFichier =
                nomDossierFabriqueTest + "Telephone\\" + t.getId() + ".ser";
        File f = new File(nomFichier);
        assertTrue(f.exists());
        Telephone observed = deserialize(nomFichier);
        assertEquals(t, observed);
        assertEquals(t.getId(), observed.getId());
    }

    /**
     * Test de la recherche.
     */
    @Test
    public void findTest() {
        Telephone t = new Telephone(2, "0678", "portable");
        daoT.create(t);

        Telephone observed = daoT.find(t.getId() + "");
        assertEquals(t.getId(), observed.getId());
        assertEquals(t, observed);
    }

    /**
     * Test de la modification.
     * @throws FileNotFoundException Si l'emplacement du fichier est impossible
     *         à atteindre
     * @throws IOException En cas d'erreur lors de la lecture.
     * @throws ClassNotFoundException Si la classe du fichier de sérialisation
     *         est inconnue de l'application.
     */
    @Test
    public void updateTest()
            throws FileNotFoundException, ClassNotFoundException, IOException {
        Telephone t = new Telephone(3, "0678", "portable");
        daoT.create(t);

        Telephone updateT = new Telephone(3, "0672458", "portab4le");
        daoT.update(updateT);

        String nomFichier = nomDossierFabriqueTest + "Telephone\\"
                + updateT.getId() + ".ser";
        File f = new File(nomFichier);
        assertTrue(f.exists());
        Telephone observed = deserialize(nomFichier);
        assertEquals(updateT.getId(), observed.getId());
        assertEquals(updateT, observed);
    }

    /**
     * Teste de la délétion.
     */
    @Test
    public void deleteTest() {
        Telephone t = new Telephone(4, "0678", "portable");
        daoT.create(t);

        daoT.delete(t);
        String nomFichier =
                nomDossierFabriqueTest + "Telephone\\" + t.getId() + ".ser";
        File f = new File(nomFichier);
        assertFalse(f.exists());
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
