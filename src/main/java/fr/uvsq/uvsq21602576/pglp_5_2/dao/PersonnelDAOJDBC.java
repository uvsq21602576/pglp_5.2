package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;
import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Classe DAO pour Telephone.
 * Marche avec une base de donnée.
 * @author Flora
 */
public class PersonnelDAOJDBC extends DAO<Personnel> {
    /**
     * Connection avec la base de données.
     */
    Connection connection;

    /**
     * Constructeur.
     * Initialise la connexion avec la base de donnée.
     */
    public PersonnelDAOJDBC() {
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
        Statement stmt = null;
        stmt = conn.createStatement();
        if (!tableExists("personnel", conn)) {
            stmt.executeUpdate("Create table personnel ("
                    + "id int primary key, " + "nom varchar(30) not null, "
                    + "prenom varchar(30) not null,"
                    + "dateNaissance varchar(30) not null,"
                    + "fonction varchar(30))");
        }

        if (!tableExists("Possede", conn)) {
            stmt.executeUpdate("Create table possede ("
                    + "id_personnel int not null, "
                    + "id_telephone int not null, "
                    + "primary key (id_personnel, id_telephone), "
                    + "foreign key (id_personnel) references personnel(id), "
                    + "foreign key (id_telephone) references telephone(id))");
        }
    }

    static void insert(Personnel obj, Connection conn) throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();

        //Insertion personnel
        stmt.executeUpdate("insert into personnel values (" + obj.getId()
        + ", '" + obj.getNom() + "', '" + obj.getPrenom() + "', '"
        + obj.getDateNaissance().toString() + "', '"
        + obj.getFonction() + "')");

        //Insertion telephones
        String sqlPossede = "insert into possede values ";
        for (Telephone t : obj.getNumeros()) {
            try {
                TelephoneDAOJDBC.insert(t, conn);
            } catch (DerbySQLIntegrityConstraintViolationException e) {
                System.err.println(e.getMessage());
            }
            sqlPossede = sqlPossede
                    .concat("(" + obj.getId() + ", " + t.getId() + "),");
        }
        sqlPossede = sqlPossede.substring(0, sqlPossede.length() - 1);

        stmt.executeUpdate(sqlPossede);
    }

    @Override
    public Personnel create(Personnel obj) {
        try {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            insert(obj, connection);
        } catch (DerbySQLIntegrityConstraintViolationException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    static Personnel read(int idPersonnel, Connection conn) throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();

        ResultSet rs = null;
        rs = stmt.executeQuery("Select id_telephone from possede "
                + "WHERE id_personnel = " + idPersonnel);
        ArrayList<Integer> idTelephones = new ArrayList<>();
        while (rs.next()) {
            idTelephones.add(rs.getInt("id_telephone"));
        }
        ArrayList<Telephone> telephones = null;
        telephones = TelephoneDAOJDBC.findAll(idTelephones, conn);
        if (telephones.isEmpty()) {
            System.err.println(
                    "No telephone associated with personnel of id " + idPersonnel);
            return null;
        }
        rs = stmt.executeQuery(
                "SELECT * FROM personnel " + "WHERE id = " + idPersonnel);
        if (rs != null && rs.next()) {
            String[] partDate = rs.getString("dateNaissance").split("-");
            Personnel.Builder pBuilder = new Personnel.Builder(
                    rs.getInt("id"), rs.getString("nom"),
                    rs.getString("prenom"),
                    LocalDate.of(Integer.parseInt(partDate[0]),
                            Integer.parseInt(partDate[1]),
                            Integer.parseInt(partDate[2])),
                    telephones.remove(0));
            for (Telephone t : telephones) {
                pBuilder.addNumero(t);
            }
            return pBuilder.build();
        } else {
            System.err.println("No personnel found with id " + idPersonnel);
        }
        return null;
    }

    @Override
    public Personnel find(String id) {
        try {
            return read(Integer.parseInt(id), connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    static boolean modify(Personnel obj, Connection conn, boolean insert) throws SQLException {
        TelephoneDAOJDBC.updateAll(obj.getNumeros(), conn);
        Statement stmt = null;
        stmt = conn.createStatement();
        
        ResultSet rs = stmt.executeQuery("SELECT * FROM personnel WHERE id = " + obj.getId());
        if(!rs.next()) {
            if(insert) {
                stmt.executeUpdate("insert into personnel values (" + obj.getId()
                + ", '" + obj.getNom() + "', '" + obj.getPrenom() + "', '"
                + obj.getDateNaissance().toString() + "', '"
                + obj.getFonction() + "')");
            } else {
                return false;
            }
        }
        
        stmt.executeUpdate("Update personnel SET " + "nom = '"
                + obj.getNom() + "', " + "prenom = '" + obj.getPrenom()
                + "', " + "dateNaissance = '"
                + obj.getDateNaissance().toString() + "', " + "fonction = '"
                + obj.getFonction() + "' " + "WHERE id = " + obj.getId());
        
        String sqlPossede = "insert into possede values ";
        for (Telephone t : obj.getNumeros()) {
            sqlPossede = sqlPossede
                    .concat("(" + obj.getId() + ", " + t.getId() + "),");
        }
        sqlPossede = sqlPossede.substring(0, sqlPossede.length() - 1);
        stmt.executeUpdate(
                "Delete from possede WHERE id_personnel = " + obj.getId());
        stmt.executeUpdate(sqlPossede);
        return true;
    }

    @Override
    public Personnel update(Personnel obj) {
        try {
            if(!modify(obj, connection, false)) {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    @Override
    public void delete(Personnel obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        try {
            if(tableExists("appartenir", connection)) {
                stmt.executeUpdate("DELETE from appartenir where id_personnel = " + obj.getId());
            }
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        
        try {
            stmt.executeUpdate(
                    "Delete from possede WHERE id_personnel = " + obj.getId());
            System.out.println(stmt.executeUpdate(
                    "Delete from personnel WHERE id = " + obj.getId()) + " personnels deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }
}
