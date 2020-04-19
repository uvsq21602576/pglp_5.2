package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;
import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

/**
 * Classe DAO pour Personnel.
 * Marche avec la base de donnée intégré, présente à l'url
 * jdbc:derby:donneesPourDB\\jdbcDB.
 * @author Flora
 */
public class PersonnelDAOJDBC extends DAO<Personnel> {
    /**
     * Connection avec la base de données.
     */
    private Connection connection;

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
     * Crée les tables utiles pour le Personnel.
     * C'est à dire : Telephone, Personnel et Possède.
     * @param conn Connexion à la base de donnée
     * @throws SQLException En cas de problème lors de l'éxécution des requetes
     *         sql.
     */
    public static void createTables(final Connection conn) throws SQLException {
        TelephoneDAOJDBC.createTable(conn);
        try (Statement stmt = conn.createStatement()) {
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
                        + "foreign key (id_personnel)"
                        + " references personnel(id), "
                        + "foreign key (id_telephone)"
                        + " references telephone(id))");
            }
        }
    }

    /**
     * Insert l'objet personnel dans la base de donnée.
     * Via la connexion conn.
     * Insert aussi les telephones.
     * @param obj Personnel à insérer
     * @param conn Connexion à la base de donnée
     * @throws SQLException En cas d'erreur dues aux requetes sql.
     */
    static void insert(final Personnel obj, final Connection conn)
            throws SQLException {

        // Insertion personnel
        try (PreparedStatement insertPersonnel = conn.prepareStatement(
                "insert into personnel values " + "(?, ?, ?, ?, ?)")) {
            insertPersonnel.setInt(1, obj.getId());
            insertPersonnel.setString(2, obj.getNom());
            insertPersonnel.setString(3, obj.getPrenom());
            insertPersonnel.setString(4, obj.getDateNaissance().toString());
            insertPersonnel.setString(5, obj.getFonction());
            insertPersonnel.execute();
        }

        // Insertion telephones
        try (PreparedStatement insertPossede =
                conn.prepareStatement("insert into possede values (?, ?)")) {
            insertPossede.setInt(1, obj.getId());
            for (Telephone t : obj.getNumeros()) {
                try {
                    TelephoneDAOJDBC.insert(t, conn);
                } catch (DerbySQLIntegrityConstraintViolationException e) {
                    System.err.println(e.getMessage());
                }
                insertPossede.setInt(2, t.getId());
                insertPossede.execute();
            }
        }
    }

    /**
     * Pour la création.
     * Enregistre le personnel dans la base de donnée.
     * @param obj Personnel à enregistré
     * @return Personnel enregistré, ou null en cas d'erreur
     */
    @Override
    public Personnel create(final Personnel obj) {
        try {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        Personnel created = null;
        try {
            connection.setAutoCommit(false);
            insert(obj, connection);
            connection.commit();
            created = obj;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            try {
                connection.rollback();
                System.err.println("Insertion of Personnel " + obj.getId() + " has been rolled back.");
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
     * Lis le Personnel à l'id idPersonnel.
     * Et les telephones associés.
     * De la base de donnée, via la connexion conn.
     * Renvoie le Personnel formé.
     * @param idPersonnel id du Personnel recherché
     * @param conn Connexion à la base de donnée
     * @return Personnel trouvé ou null si inexistant
     * @throws SQLException En cas d'erreur lors des requetes sql.
     */
    static Personnel read(final int idPersonnel, final Connection conn)
            throws SQLException {

        ArrayList<Integer> idTelephones = new ArrayList<>();
        try (PreparedStatement selectPossede = conn.prepareStatement(
                "Select id_telephone from possede WHERE id_personnel = ?")) {
            selectPossede.setInt(1, idPersonnel);
            try (ResultSet rs = selectPossede.executeQuery()) {
                while (rs.next()) {
                    idTelephones.add(rs.getInt("id_telephone"));
                }
            }
        }

        ArrayList<Telephone> telephones = null;
        telephones = TelephoneDAOJDBC.findAll(idTelephones, conn);
        if (telephones.isEmpty()) {
            System.err.println("No telephone associated with personnel of id "
                    + idPersonnel);
            return null;
        }

        try (PreparedStatement selectPersonnel =
                conn.prepareStatement("SELECT * FROM personnel WHERE id = ?")) {
            selectPersonnel.setInt(1, idPersonnel);
            try (ResultSet rs = selectPersonnel.executeQuery()) {
                if (rs != null && rs.next()) {
                    String[] partDate =
                            rs.getString("dateNaissance").split("-");
                    Personnel.Builder pBuilder =
                            new Personnel.Builder(rs.getInt("id"),
                                    rs.getString("nom"), rs.getString("prenom"),
                                    LocalDate.of(Integer.parseInt(partDate[0]),
                                            Integer.parseInt(partDate[1]),
                                            Integer.parseInt(partDate[2])),
                                    telephones.remove(0));
                    for (Telephone t : telephones) {
                        pBuilder.addNumero(t);
                    }
                    return pBuilder.build();
                } else {
                    System.err.println(
                            "No personnel found with id " + idPersonnel);
                }
            }
        }
        return null;
    }

    /**
     * Pour la recherche.
     * Retourne le personnel avec comme identifiant id,
     * présent dans la base de donnée.
     * @param id Identifiant du personnel à trouver
     * @return Personnel trouvé, ou null en cas d'erreur
     */
    @Override
    public Personnel find(final String id) {
        try {
            return read(Integer.parseInt(id), connection);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    /**
     * Met à jour la base de donnée.
     * Modifie l'obj personnel et ses telephones.
     * Le booléen insert indique si l'obj personnel doit être
     * insérer en cas d'inexistance dans la base de donnée.
     * Via la connexion à la base de donnée conn.
     * @param obj Personnel à ajouter
     * @param conn Connexion à la base de donnée
     * @param insert Bouléen indiquant si obj doit être inséré si non existant.
     * @return Si l'objet à bien pu être modifié.
     * @throws SQLException En cas d'erreur lors des requetes SQL.
     */
    static boolean modify(final Personnel obj, final Connection conn,
            final boolean insert) throws SQLException {
        TelephoneDAOJDBC.updateAll(obj.getNumeros(), conn);

        try (PreparedStatement selectPersonnel =
                conn.prepareStatement("SELECT * FROM personnel WHERE id = ?")) {
            selectPersonnel.setInt(1, obj.getId());
            try (ResultSet rs = selectPersonnel.executeQuery()) {
                if (!rs.next()) {
                    if (insert) {
                        try (PreparedStatement insertPersonnel =
                                conn.prepareStatement(
                                        "insert into personnel "
                                                + "values (?, ?, ?, ?, ?)")) {
                            insertPersonnel.setInt(1, obj.getId());
                            insertPersonnel.setString(2, obj.getNom());
                            insertPersonnel.setString(3, obj.getPrenom());
                            insertPersonnel.setString(4,
                                    obj.getDateNaissance().toString());
                            insertPersonnel.setString(5, obj.getFonction());
                            insertPersonnel.execute();
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        try (PreparedStatement updatePersonnel = conn
                .prepareStatement("Update personnel SET nom = ?, prenom = ?, "
                        + "dateNaissance = ?, fonction = ?" + " WHERE id = ?")) {
            updatePersonnel.setInt(5, obj.getId());
            updatePersonnel.setString(1, obj.getNom());
            updatePersonnel.setString(2, obj.getPrenom());
            updatePersonnel.setString(3, obj.getDateNaissance().toString());
            updatePersonnel.setString(4, obj.getFonction());
            updatePersonnel.execute();
        }

        try (PreparedStatement deletePossede = conn.prepareStatement(
                "Delete from possede WHERE id_personnel = ?")) {
            deletePossede.setInt(1, obj.getId());
            deletePossede.execute();
        }

        try (PreparedStatement insertPossede =
                conn.prepareStatement("insert into possede values (?, ?)")) {
            insertPossede.setInt(1, obj.getId());
            for (Telephone t : obj.getNumeros()) {
                insertPossede.setInt(2, t.getId());
                insertPossede.execute();
            }
        }
        return true;
    }

    /**
     * Pour la modification.
     * Met à jour le personnel (repéré par son id) dans la BD.
     * Met à jour tous les numéros de telephones associés.
     * @param obj Personnel modifié à mettre à jour
     * @return Personnel modifié, ou null en cas d'erreur
     */
    @Override
    public Personnel update(final Personnel obj) {
        Personnel updated = null;
        try {
            connection.setAutoCommit(false);
            if (modify(obj, connection, false)) {
                connection.commit();
                updated = obj;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            try {
                connection.rollback();
                System.err.println("Update of Personnel " + obj.getId() + " has been rolled back.");
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
     * Supprime le personnel obj de la base de donnée.
     * Ne supprime pas les Telephones.
     * @param obj Personnel à supprimer
     */
    @Override
    public void delete(final Personnel obj) {
        try {
            connection.setAutoCommit(false);
            if (tableExists("appartenir", connection)) {
                try (PreparedStatement deleteAppartenir =
                        connection.prepareStatement(
                                "DELETE from appartenir "
                                        + "where id_personnel = ?")) {
                    deleteAppartenir.setInt(1, obj.getId());
                    deleteAppartenir.execute();
                    System.out.println(
                            "Appartenir with personnel " + obj.getId() + " deleted.");
                }
            }

            try (PreparedStatement deletePossede = connection.prepareStatement(
                    "Delete from possede WHERE id_personnel = ?")) {
                deletePossede.setInt(1, obj.getId());
                deletePossede.execute();
                System.out.println(
                        "Possede with personnel " + obj.getId() + " deleted.");
            }
            if (tableExists("Annuaire", connection)) {
                try (PreparedStatement deleteAnnuaire =
                        connection.prepareStatement(
                                "DELETE from annuaire "
                                        + "where racine_personnel = ?")) {
                    deleteAnnuaire.setInt(1, obj.getId());
                    deleteAnnuaire.execute();
                    System.out.println("Annuaire with personnel " + obj.getId()
                    + " deleted.");
                }
            }
            try (PreparedStatement deletePersonnel = connection
                    .prepareStatement("DELETE from personnel where id = ?")) {
                deletePersonnel.setInt(1, obj.getId());
                deletePersonnel.execute();
            }
            connection.commit();
            System.out.println("Personnel deleted.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            try {
                connection.rollback();
                System.err.println("Deletion of Personnel " + obj.getId() + " has been rolled back.");
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
