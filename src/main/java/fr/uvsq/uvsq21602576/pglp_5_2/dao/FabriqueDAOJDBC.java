package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;
import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Fabrique DAO.
 * Fonctionne avec une base de donnée embarqué Derby.
 * @author Flora
 */
public class FabriqueDAOJDBC extends FabriqueDAO {

    /**
     * Retourne le DAO pour le Telephone.
     * @return DAO pour Telephone
     */
    @Override
    public DAO<Telephone> getTelephoneDAO() {
        return new TelephoneDAOJDBC();
    }

    /**
     * Retourne le DAO pour le Personnel.
     * @return DAO pour Personnel
     */
    @Override
    public DAO<Personnel> getPersonnelDAO() {
        return new PersonnelDAOJDBC();
    }

    /**
     * Retourne le DAO pour le Groupe.
     * @return DAO pour Groupe
     */
    @Override
    public DAO<Groupe> getGroupeDAO() {
        return new GroupeDAOJDBC();
    }

    /**
     * Retourne le DAO pour l'Annuaire.
     * @return DAO pour Annuaire
     */
    @Override
    public DAO<Annuaire> getAnnuaireDAO() {
        return new AnnuaireDAOJDBC();
    }

}
