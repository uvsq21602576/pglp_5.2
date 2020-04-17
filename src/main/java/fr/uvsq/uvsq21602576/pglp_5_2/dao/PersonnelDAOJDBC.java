package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.awt.font.TextAttribute;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.derby.impl.sql.compile.ParserImpl;
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

    @Override
    public Personnel create(Personnel obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            stmt.executeUpdate("insert into personnel values (" + obj.getId()
                    + ", '" + obj.getNom() + "', '" + obj.getPrenom() + "', '"
                    + obj.getDateNaissance().toString() + "', '"
                    + obj.getFonction() + "')");
        } catch (DerbySQLIntegrityConstraintViolationException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String sqlPossede = "insert into possede values ";
        for (Telephone t : obj.getNumeros()) {
            try {
                TelephoneDAOJDBC.insert(t, connection);
                sqlPossede = sqlPossede
                        .concat("(" + obj.getId() + ", " + t.getId() + "),");
            } catch (DerbySQLIntegrityConstraintViolationException e) {
                System.err.println(e.getMessage());
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        sqlPossede = sqlPossede.substring(0, sqlPossede.length() - 1);

        try {
            stmt.executeUpdate(sqlPossede);
        } catch (DerbySQLIntegrityConstraintViolationException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    @Override
    public Personnel find(String id) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("Select id_telephone from possede "
                    + "WHERE id_personnel = " + id);
        } catch (SQLException e2) {
            e2.printStackTrace();
            return null;
        }
        ArrayList<Integer> idTelephones = new ArrayList<>();
        try {
            while (rs.next()) {
                idTelephones.add(rs.getInt("id_telephone"));
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
        }

        ArrayList<Telephone> telephones = null;
        try {
            telephones = TelephoneDAOJDBC.findAll(idTelephones, connection);
        } catch (SQLException e1) {
            e1.printStackTrace();
            return null;
        }

        if (telephones.isEmpty()) {
            System.err.println(
                    "No telephone associated with personnel of id " + id);
            return null;
        }

        rs = null;
        try {
            rs = stmt.executeQuery(
                    "SELECT * FROM personnel " + "WHERE id = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
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
                System.err.println("No personnel found with id " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Personnel update(Personnel obj) {
        try {
            TelephoneDAOJDBC.updateAll(obj.getNumeros(), connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        try {
            stmt.executeUpdate("Update personnel SET " + "nom = '"
                    + obj.getNom() + "', " + "prenom = '" + obj.getPrenom()
                    + "', " + "dateNaissance = '"
                    + obj.getDateNaissance().toString() + "', " + "fonction = '"
                    + obj.getFonction() + "' " + "WHERE id = " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        String sqlPossede = "insert into possede values ";
        for (Telephone t : obj.getNumeros()) {
            sqlPossede = sqlPossede
                    .concat("(" + obj.getId() + ", " + t.getId() + "),");
        }
        sqlPossede = sqlPossede.substring(0, sqlPossede.length() - 1);
        try {
            stmt.executeUpdate(
                    "Delete from possede WHERE id_personnel = " + obj.getId());
            stmt.executeUpdate(sqlPossede);
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
            stmt.executeUpdate(
                    "Delete from possede WHERE id_personnel = " + obj.getId());
            stmt.executeUpdate("Delete from personnel WHERE id = " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }
}
