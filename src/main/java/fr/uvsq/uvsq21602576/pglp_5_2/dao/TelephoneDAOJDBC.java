package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Classe DAO pour Telephone.
 * Marche avec la base de donnée intégré, présente à l'url
 * jdbc:derby:donneesPourDB\\jdbcDB.
 * @author Flora
 */
public class TelephoneDAOJDBC extends DAO<Telephone> {
    /**
     * Connection avec la base de données.
     */
    private Connection connection;

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
     * Dans la base de donnée connecté à conn.
     * @param name Nom de la table
     * @param conn Connexion avec la base de donnée.
     * @return true si elle existe, false sinon
     * @throws SQLException En cas d'erreur de connection avec la BD.
     */
    private static boolean tableExists(final String name, final Connection conn)
            throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        try (ResultSet rs =
                dbmd.getTables(null, null, name.toUpperCase(), null)) {
            if (rs.next()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crée les tables utiles pour le Telephone.
     * C'est à dire : Telephone.
     * @param conn Connexion à la base de donnée
     * @throws SQLException En cas de problème lors de l'éxécution des requetes
     *         sql.
     */
    static void createTable(final Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            if (!tableExists("telephone", conn)) {
                stmt.execute("Create table telephone " + "(id int primary key, "
                        + "numero varchar(30) unique not null,"
                        + "information varchar(30))");
            }
        }
    }

    /**
     * Insert l'objet telephone dans la base de donnée.
     * Via la connexion conn.
     * @param obj Telephone à insérer
     * @param conn Connexion à la base de donnée
     * @throws SQLException En cas d'erreur dues aux requetes sql.
     */
    static void insert(final Telephone obj, final Connection conn)
            throws SQLException {
        try (PreparedStatement insertTelephone = conn
                .prepareStatement("insert into telephone values (?, ?, ?)")) {
            insertTelephone.setInt(1, obj.getId());
            insertTelephone.setString(2, obj.getNumero());
            insertTelephone.setString(3, obj.getInformation());
            insertTelephone.execute();
        }
    }

    /**
     * Pour la création.
     * Enregistre le telephone dans la base de donnée.
     * @param obj Telephone à créer
     * @return Telephone créé, ou null en cas d'erreur
     */
    @Override
    public Telephone create(final Telephone obj) {
        try {
            createTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        Telephone created = null;

        try {
            connection.setAutoCommit(false);
            insert(obj, connection);
            connection.commit();
            created = obj;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            try {
                connection.rollback();
                System.err.println("Insertion of Telephones " + obj.getId() + " has been rolled back.");
            } catch (SQLException e1) {
                System.err.println(e1.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }

        return created;

    }

    /**
     * Recherche et renvoie uns liste de telephones.
     * Identifié gra^ce à une liste d'identifiant id.
     * Via la connexion à la base de données, conn.
     * @param id Liste d'identifiant de telephones
     * @param conn Connexion à la base de données
     * @return Liste de telephone trouvés
     * @throws SQLException En cas d'erreur lors des requetes SQL
     */
    static ArrayList<Telephone> findAll(final ArrayList<Integer> id,
            final Connection conn) throws SQLException {

        ArrayList<Telephone> telephones = new ArrayList<Telephone>();

        try (PreparedStatement selectTelephone =
                conn.prepareStatement("SELECT * FROM telephone WHERE id = ?")) {
            for (int i : id) {
                try {
                    selectTelephone.setInt(1, i);
                    try (ResultSet rs = selectTelephone.executeQuery()) {
                        if (rs.next()) {
                            telephones.add(new Telephone(rs.getInt("id"),
                                    rs.getString("numero"),
                                    rs.getString("information")));
                        }
                    }
                } catch (DerbySQLIntegrityConstraintViolationException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
        return telephones;
    }

    /**
     * Pour la recherche.
     * Retourne le telephone avec comme identifiant id,
     * présent dans la base de donnée.
     * @param id Identifiant du telephone à trouver
     * @return Telephone trouvé, ou null en cas d'erreur
     */
    @Override
    public Telephone find(final String id) {
        try (PreparedStatement selectTelephone = connection
                .prepareStatement("SELECT * FROM telephone WHERE id = ?")) {
            selectTelephone.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = selectTelephone.executeQuery()) {
                if (rs != null && rs.next()) {
                    Telephone t = new Telephone(rs.getInt("id"),
                            rs.getString("numero"),
                            rs.getString("information"));
                    return t;
                } else {
                    System.err.println("No telephone found with id " + id);
                }
            }
        } catch (SQLException e1) {
            System.err.println(e1.getMessage());
        }
        return null;
    }

    /**
     * Update tous les telephones présent dans list.
     * via la connexion à la base de donnée, conn.
     * @param list Liste de Telephone à modifier
     * @param conn Connexion à la base donnée
     * @throws SQLException En cas d'erreur lors des requetes SQL.
     */
    static void updateAll(final List<Telephone> list, final Connection conn)
            throws SQLException {

        try (PreparedStatement selectTelephone =
                conn.prepareStatement("SELECT * FROM telephone WHERE id = ?")) {
            try (PreparedStatement updateTelephone =
                    conn.prepareStatement(
                            "Update telephone SET numero = ?,"
                                    + "information = ? "
                                    + "WHERE id = ?")) {
                for (Telephone t : list) {
                    selectTelephone.setInt(1, t.getId());
                    try (ResultSet rs = selectTelephone.executeQuery()) {
                        if (!rs.next()) {
                            insert(t, conn);
                        } else {
                            updateTelephone.setString(1, t.getNumero());
                            updateTelephone.setString(2, t.getInformation());
                            updateTelephone.setInt(3, t.getId());
                            updateTelephone.execute();
                        }
                    }
                }
            }
        }
    }

    /**
     * Pour la modification.
     * Met à jour le telephone (repéré par son id) dans la BD.
     * @param obj Telephone modifié à mettre à jour
     * @return Telephone modifié, ou null en cas d'erreur
     */
    @Override
    public Telephone update(final Telephone obj) {
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        Telephone updated = null;
        try (PreparedStatement updateTelephone =
                connection.prepareStatement("Update telephone SET numero = ?,"
                        + "information = ? WHERE id = ?")) {
            updateTelephone.setString(1, obj.getNumero());
            updateTelephone.setString(2, obj.getInformation());
            updateTelephone.setInt(3, obj.getId());
            updateTelephone.execute();
            connection.commit();
            updated = obj;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            try {
                connection.rollback();
                System.err.println("Update of Telephones " + obj.getId() + " has been rolled back.");
            } catch (SQLException e1) {
                System.err.println(e1.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
        return updated;
    }

    /**
     * Pour la suppression.
     * Supprime le telephone obj de la base de donnée.
     * @param obj Telephone à supprimer
     */
    @Override
    public void delete(final Telephone obj) {
        try {
            connection.setAutoCommit(false);
            if (tableExists("possede", connection)) {
                try (PreparedStatement deletePossede =
                        connection.prepareStatement(
                                "DELETE from possede where id_telephone = ?")) {
                    deletePossede.setInt(1, obj.getId());
                    deletePossede.execute();
                    System.out.println("Possede with telephone " + obj.getId()
                    + " deleted.");
                }
            }
            try (PreparedStatement deleteTelephone = connection
                    .prepareStatement("Delete from telephone WHERE id = ?")) {
                deleteTelephone.setInt(1, obj.getId());
                deleteTelephone.execute();
            }
            connection.commit();
            System.out.println("Telephone " + obj.getId() + " deleted.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            try {
                connection.rollback();
                System.err.println("Deletion of Telephones " + obj.getId() + " has been rolled back.");
            } catch (SQLException e1) {
                System.err.println(e1.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
