package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;
import fr.uvsq.uvsq21602576.pglp_5_2.Composant;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;

public class AnnuaireDAOJDBC extends DAO<Annuaire> {
    /**
     * Connection avec la base de données.
     */
    Connection connection;

    /**
     * Constructeur.
     * Initialise la connexion avec la base de donnée.
     */
    public AnnuaireDAOJDBC() {
        String dbUrl = "jdbc:derby:donneesPourDB\\jdbcDB;create=true";
        try {
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            connection = null;
            e.printStackTrace();
        }
    }
    
    /**
     * Teste si la table name existe.
     * @return true si ell existe, false sinon
     * @throws SQLException En cas d'erreur de connection avec la BD.
     */
    private static boolean tableExists(String name, Connection conn)
            throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, name.toUpperCase(), null);
        if (rs.next()) {
            return true;
        }
        return false;
    }

    public static void createTables(Connection conn) throws SQLException {
        TelephoneDAOJDBC.createTable(conn);
        PersonnelDAOJDBC.createTables(conn);
        GroupeDAOJDBC.createTables(conn);
        Statement stmt = null;
        stmt = conn.createStatement();
        if (!tableExists("annuaire", conn)) {
            stmt.executeUpdate("Create table annuaire ("
                    + "id int primary key, "
                    + "racine_personnel int, "
                    + "racine_groupe int,"
                    + "check ((racine_personnel is null and racine_groupe is not null) or"
                    + "(racine_personnel is not null and racine_groupe is null)),"
                    + "foreign key (racine_personnel) references personnel(id),"
                    + "foreign key (racine_groupe) references groupe(id))");
        }
    }

    @Override
    public Annuaire create(Annuaire obj) {
        try {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return null;
        }
        
        Composant racine = obj.getRacine();
        if(racine instanceof Personnel) {
            try {
                Personnel p = (Personnel) racine;
                ResultSet rs = stmt.executeQuery("SELECT * FROM personnel WHERE id = " + p.getId());
                if(!rs.next()) {
                    PersonnelDAOJDBC.insert(p, connection);
                } else {
                    System.err.println("Personnel " + p.getId() + " not insert : already exists.");
                }
                stmt.executeUpdate("INSERT into annuaire(id,racine_personnel) values "
                        + "(" + obj.getId() + ", " + p.getId() + ")");
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else if(racine instanceof Groupe) {
            try {
                Groupe g = (Groupe) racine;
                ResultSet rs = stmt.executeQuery("SELECT * FROM groupe WHERE id = " + g.getId());
                if(!rs.next()) {
                    GroupeDAOJDBC.insert(g, connection);
                } else {
                    System.err.println("Groupe " + g.getId() + " not insert : already exists.");
                }
                stmt.executeUpdate("INSERT into annuaire(id,racine_groupe) values "
                        + "(" + obj.getId() + ", " + g.getId() + ")");
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        return null;
    }

    @Override
    public Annuaire find(String id) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return null;
        }
        
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT * FROM annuaire WHERE id = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        Annuaire a = null;
        try {
            if(rs.next()) {
                int id_racine = rs.getInt("racine_personnel");
                if(!rs.wasNull()){
                    Personnel p = PersonnelDAOJDBC.read(id_racine, connection);
                    a = new Annuaire(rs.getInt("id"),p);
                } else {
                    id_racine = rs.getInt("racine_groupe");
                    if(!rs.wasNull()){
                        Groupe g = GroupeDAOJDBC.read(id_racine, connection);
                        a = new Annuaire(rs.getInt("id"),g);
                    }
                }
            } else {
                System.err.println("No annuaire for id " + id);
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        return a;
    }

    @Override
    public Annuaire update(Annuaire obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return null;
        }
        
        
        Composant racine = obj.getRacine();
        if(racine instanceof Personnel) {
            try {
                Personnel p = (Personnel) racine;
                PersonnelDAOJDBC.modify(p, connection, true);
                stmt.executeUpdate("UPDATE annuaire SET racine_groupe = null, racine_personnel = "
                        + p.getId() +" WHERE id = " + obj.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else if(racine instanceof Groupe) {
            try {
                Groupe g = (Groupe) racine;
                GroupeDAOJDBC.modify(g, connection, true);
                stmt.executeUpdate("UPDATE annuaire SET racine_personnel = null, racine_groupe = "
                        + g.getId() +" WHERE id = " + obj.getId());
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @Override
    public void delete(Annuaire obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
            return;
        }
        
        try {
            System.out.println(stmt.executeUpdate(
                    "DELETE FROM annuaire WHERE id = " + obj.getId()) + " annuaires deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
