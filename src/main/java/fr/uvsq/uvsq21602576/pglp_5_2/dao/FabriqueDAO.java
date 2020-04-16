package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;
import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Fabrique abstraite DAO.
 * Pour récupérer la bonne fabrique pour le type recherché.
 * @author Flora
 */
public abstract class FabriqueDAO {
    /**
     * Types de DAO implémentés.
     */
    public enum TypeDAO {
        /**
         * Pour une simulation de base de données par fichier.
         * Et sérialisation.
         */
        FILE;
    }

    /**
     * Retourne le DAO pour le Telephone.
     * @return DAO pour Telephone
     */
    public abstract DAO<Telephone> getTelephoneDAO();

    /**
     * Retourne le DAO pour le Personnel.
     * @return DAO pour Personnel
     */
    public abstract DAO<Personnel> getPersonnelDAO();

    /**
     * Retourne le DAO pour le Groupe.
     * @return DAO pour Groupe
     */
    public abstract DAO<Groupe> getGroupeDAO();

    /**
     * Retourne le DAO pour le Annuaire.
     * @return DAO pour Annuaire
     */
    public abstract DAO<Annuaire> getAnnuaireDAO();

    /**
     * Retorune la fabriqueDAO correspondante au type.
     * @param type typeDAO recherché
     * @return FabriqueDAO
     */
    public static FabriqueDAO getFabriqueDAO(final TypeDAO type) {
        if (type == TypeDAO.FILE) {
            return new FabriqueDAOFile();
        }
        return null;
    }
}
