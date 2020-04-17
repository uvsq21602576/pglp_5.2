package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Classe DAO pour Telephone.
 * Marche avec une base de donnée.
 * @author Flora
 */
public class TelephoneDAOJDBC extends DAO<Telephone> {
    /**
     * Connection avec la base de données.
     */
    Connection connection;

    /**
     * Constructeur.
     * Initialise la connexion avec la base de donnée.
     */
    public TelephoneDAOJDBC() {
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
    private static boolean tableExists(Connection conn) throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, "telephone".toUpperCase(),
                null);
        if (rs.next()) {
            return true;
        }
        return false;
    }

    static void createTable(Connection conn) throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();
        if (!tableExists(conn)) {
            stmt.execute(
                    "Create table telephone (id int primary key, numero varchar(30) unique not null,"
                            + "information varchar(30))");
        }
    }

    static void insert(Telephone obj, Connection conn)
            throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();
        stmt.executeUpdate("insert into telephone values (" + obj.getId()
        + ", '" + obj.getNumero() + "', '" + obj.getInformation()
        + "')");
    }

    static ArrayList<Telephone> findAll(ArrayList<Integer> id, Connection conn) throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();
        ResultSet rs = null;
        ArrayList<Telephone> telephones = new ArrayList<Telephone>();

        for(int i : id) {
            try {
                rs = stmt.executeQuery(
                        "SELECT * FROM telephone " + "WHERE id = " + i);
                if (rs.next()) {
                    telephones.add(new Telephone(rs.getInt("id"),
                            rs.getString("numero"), rs.getString("information")));
                }
            } catch (DerbySQLIntegrityConstraintViolationException e) {
                System.err.println(e.getMessage());
            } 
        }

        return telephones;
    }

    static void updateAll(List<Telephone> list, Connection conn) throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();

        for(Telephone t : list) {
            int nb = stmt.executeUpdate("Update telephone SET " + "numero = '"
                    + t.getNumero() + "', " + "information = '"
                    + t.getInformation() + "' " 
                    + "WHERE id = " + t.getId());
            if(nb<1) {
                insert(t, conn);
            }
        }
    }

    /**
     * Pour la création.
     * Enregistre le telephone dans la base de donnée.
     * @param obj Telephone à créer
     * @return Telephone créé, ou null en cas d'erreur
     */
    @Override
    public Telephone create(Telephone obj) {
        try {
            createTable(connection);
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

    /**
     * Pour la recherche.
     * Retourne le telephone avec comme identifiant id,
     * présent dans la base de donnée.
     * @param id Identifiant du telephone à trouver
     * @return Telephone trouvé, ou null en cas d'erreur
     */
    @Override
    public Telephone find(String id) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        ResultSet rs = null;
        try {
            rs = stmt.executeQuery(
                    "SELECT * FROM telephone " + "WHERE id = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            if (rs != null && rs.next()) {
                Telephone t = new Telephone(rs.getInt("id"),
                        rs.getString("numero"), rs.getString("information"));
                return t;
            } else {
                System.err.println("No telephone found with id " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Pour la modification.
     * Met à jour le telephone (repéré par son id) dans la BD.
     * @param obj Telephone modifié à mettre à jour
     * @return Telephone modifié, ou null en cas d'erreur
     */
    @Override
    public Telephone update(Telephone obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            stmt.executeUpdate("Update telephone SET " + "numero = '"
                    + obj.getNumero() + "', " + "information = '"
                    + obj.getInformation() + "'" + "WHERE id = " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return obj;
    }

    /**
     * Pour la suppression.
     * Supprime le telephone obj de la base de donnée.
     * @param obj Telephone à supprimer
     */
    @Override
    public void delete(Telephone obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            stmt.executeUpdate(
                    "Delete from telephone " + "WHERE id = " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }
}
