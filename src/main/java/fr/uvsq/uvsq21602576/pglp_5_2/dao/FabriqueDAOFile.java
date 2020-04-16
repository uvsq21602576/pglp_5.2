package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;
import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Fabrique DAO.
 * Fonctionne par fichier de sérialisation.
 * @author Flora
 */
public class FabriqueDAOFile extends FabriqueDAO {

    /**
     * Chemin du dossier.
     * où se trouveront les fichiers contenant les instances.
     */
    private static String dossierDB;

    /**
     * Chemin du dossier principal.
     * dossierDB sera toujours dans ce dossier.
     */
    public static final String PRINCIPAL_DOSSIER = "donneesPourDB\\";

    /**
     * Constructeur.
     * Crée une fabrique avec comme dossier "simulDB\"
     */
    public FabriqueDAOFile() {
        dossierDB = PRINCIPAL_DOSSIER + "simulDB\\";
    }

    /**
     * Change le nom du dossier de la base de données simulée.
     * @param nomDossier Chemin du dossier
     */
    public void setDossierDB(final String nomDossier) {
        dossierDB = PRINCIPAL_DOSSIER + nomDossier;
        if (dossierDB.charAt(dossierDB.length() - 1) != '\\') {
            dossierDB += "\\";
        }
    }

    /**
     * Retourne le chemin du dossier.
     * @return chemin du dossier de la base de donnée simulée
     */
    public String getDossierDB() {
        return dossierDB;
    }

    /**
     * Retourne le DAO pour le Telephone.
     * @return DAO pour Telephone
     */
    public DAO<Telephone> getTelephoneDAO() {
        return new TelephoneDAOFile(dossierDB);
    }

    /**
     * Retourne le DAO pour le Personnel.
     * @return DAO pour Personnel
     */
    public DAO<Personnel> getPersonnelDAO() {
        return new PersonnelDAOFile(dossierDB);
    }

    /**
     * Retourne le DAO pour le Groupe.
     * @return DAO pour Groupe
     */
    public DAO<Groupe> getGroupeDAO() {
        return new GroupeDAOFile(dossierDB);
    }

    /**
     * Retourne le DAO pour le Annuaire.
     * @return DAO pour Annuaire
     */
    public DAO<Annuaire> getAnnuaireDAO() {
        return new AnnuaireDAOFile(dossierDB);
    }
}
