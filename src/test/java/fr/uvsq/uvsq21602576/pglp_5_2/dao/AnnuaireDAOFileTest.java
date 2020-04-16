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
import java.time.LocalDate;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;
import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Classe de test pour AnnuaireDAO.
 * @author Flora
 */
public class AnnuaireDAOFileTest {
    /** FabriqueDAO ayant donné daoP. */
    private static FabriqueDAO fabrique;
    /** nomDossier de la fabrique avant modification pour les tests. */
    private static String nomDossierFabriqueAvant;
    /** nomDossier de la fabrique pour les tests. */
    private static String nomDossierFabriqueTest;
    /** DAO pour Annuaire. */
    private static DAO<Annuaire> daoG;

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
            nomDossierFabriqueTest = ((FabriqueDAOFile) fabrique)
                    .getDossierDB();
            i++;
        } while (new File(nomDossierFabriqueTest).exists());

        daoG = fabrique.getAnnuaireDAO();
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
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        Personnel p2 = new Personnel.Builder(2, "1", "2",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        Groupe g1 = new Groupe(1, "G");
        g1.add(p);
        g1.add(p2);
        Annuaire a = new Annuaire(1, g1);
        daoG.create(a);

        String nomFichier = nomDossierFabriqueTest + "Annuaire\\" + a.getId()
                + ".ser";
        File f = new File(nomFichier);
        assertTrue(f.exists());
        Annuaire observed = deserialize(nomFichier);
        assertEquals(a, observed);
        assertEquals(a.getId(), observed.getId());
    }

    /**
     * Test de la recherche.
     */
    @Test
    public void findTest() {
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        Personnel p2 = new Personnel.Builder(2, "1", "2",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        Groupe g1 = new Groupe(1, "G");
        g1.add(p);
        g1.add(p2);
        Annuaire a = new Annuaire(2, g1);
        daoG.create(a);

        Annuaire observed = daoG.find(a.getId() + "");
        assertEquals(a.getId(), observed.getId());
        assertEquals(a, observed);
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
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        Personnel p2 = new Personnel.Builder(2, "1", "2",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        Groupe g1 = new Groupe(1, "G");
        g1.add(p);
        g1.add(p2);
        Annuaire a = new Annuaire(3, g1);
        daoG.create(a);

        Personnel updateP = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        Personnel updateP2 = new Personnel.Builder(2, "1", "2",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        Groupe updateG1 = new Groupe(1, "G");
        updateG1.add(updateP);
        updateG1.add(updateP2);
        Annuaire updateA = new Annuaire(3, updateG1);
        daoG.update(updateA);

        String nomFichier = nomDossierFabriqueTest + "Annuaire\\"
                + updateA.getId() + ".ser";
        File f = new File(nomFichier);
        assertTrue(f.exists());
        Annuaire observed = deserialize(nomFichier);
        assertEquals(updateA.getId(), observed.getId());
        assertEquals(updateA, observed);
    }

    /**
     * Teste de la délétion.
     */
    @Test
    public void deleteTest() {
        Personnel p = new Personnel.Builder(1, "1", "1",
                LocalDate.of(2000, 01, 05),
                new Telephone(1, "06...", "portable")).build();
        Personnel p2 = new Personnel.Builder(2, "1", "2",
                LocalDate.of(2000, 01, 05),
                new Telephone(2, "06...", "portable")).build();
        Groupe g1 = new Groupe(1, "G");
        g1.add(p);
        g1.add(p2);
        Annuaire a = new Annuaire(4, g1);
        daoG.create(a);

        daoG.delete(a);
        String nomFichier = nomDossierFabriqueTest + "Annuaire\\" + a.getId()
                + ".ser";
        File f = new File(nomFichier);
        assertFalse(f.exists());
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
        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(
                        new FileInputStream(new File(nomFichier))))) {
            Object o = in.readObject();
            if (o instanceof Annuaire) {
                return (Annuaire) o;
            }
        }
        return null;
    }
}
