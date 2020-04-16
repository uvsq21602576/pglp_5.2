package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;
import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

public class FabriqueDAOJDBC extends FabriqueDAO {

    @Override
    public DAO<Telephone> getTelephoneDAO() {
        return new TelephoneDAOJDBC();
    }

    @Override
    public DAO<Personnel> getPersonnelDAO() {
        return new PersonnelDAOJDBC();
    }

    @Override
    public DAO<Groupe> getGroupeDAO() {
        return new GroupeDAOJDBC();
    }

    @Override
    public DAO<Annuaire> getAnnuaireDAO() {
        return new AnnuaireDAOJDBC();
    }

}
