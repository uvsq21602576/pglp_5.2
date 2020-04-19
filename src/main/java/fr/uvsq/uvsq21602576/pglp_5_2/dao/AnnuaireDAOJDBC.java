package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import fr.uvsq.uvsq21602576.pglp_5_2.Annuaire;
import fr.uvsq.uvsq21602576.pglp_5_2.Composant;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;

/**
 * Classe DAO pour Annuaire.
 * Marche avec la base de donnée intégré, présente à l'url
 * jdbc:derby:donneesPourDB\\jdbcDB.
 * @author Flora
 */
public class AnnuaireDAOJDBC extends DAO<Annuaire> {
    /**
     * Connection avec la base de données.
     */
    private Connection connection;

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
     * Dans la base de donnée connecté à conn.
     * @param name Nom de la table
     * @param conn Connexion avec la base de donnée.
     * @return true si elle existe, false sinon
     * @throws SQLException En cas d'erreur de connection avec la BD.
     */
    private static boolean tableExists(final String name, final Connection conn)
            throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, name.toUpperCase(), null);
        if (rs.next()) {
            return true;
        }
        return false;
    }

    /**
     * Crée les tables utiles pour l'Annuaire.
     * C'est à dire : Telephone, Personnel, Possède,
     * Groupe, Appartenir, Contenir et Annuaire.
     * @param conn Connexion à la base de donnée
     * @throws SQLException En cas de problème lors de l'éxécution des requetes
     *         sql.
     */
    public static void createTables(final Connection conn) throws SQLException {
        TelephoneDAOJDBC.createTable(conn);
        PersonnelDAOJDBC.createTables(conn);
        GroupeDAOJDBC.createTables(conn);
        try (Statement stmt = conn.createStatement()) {
            if (!tableExists("annuaire", conn)) {
                stmt.executeUpdate("Create table annuaire ("
                        + "id int primary key, " + "racine_personnel int, "
                        + "racine_groupe int,"
                        + "check ((racine_personnel is null and"
                        + " racine_groupe is not null) or"
                        + "(racine_personnel is not null and"
                        + " racine_groupe is null)),"
                        + "foreign key (racine_personnel)"
                        + " references personnel(id),"
                        + "foreign key (racine_groupe)"
                        + " references groupe(id))");
            }
        }
    }

    /**
     * Pour la création.
     * Enregistre l'annuaire dans la base de donnée.
     * @param obj Annuaire à enregistré
     * @return Annuaire enregistré, ou null en cas d'erreur
     */
    @Override
    public Annuaire create(final Annuaire obj) {
        try {
            createTables(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try (PreparedStatement insertAnnuaire = connection.prepareStatement(
                "INSERT into annuaire values " + "(?, ?, ?)")) {
            insertAnnuaire.setInt(1, obj.getId());
            Composant racine = obj.getRacine();
            if (racine instanceof Personnel) {
                Personnel p = (Personnel) racine;
                try (PreparedStatement selectPersonnel =
                        connection.prepareStatement(
                                "SELECT * FROM personnel WHERE id = ?")) {
                    selectPersonnel.setInt(1, p.getId());
                    try (ResultSet rs = selectPersonnel.executeQuery()) {
                        if (!rs.next()) {
                            PersonnelDAOJDBC.insert(p, connection);
                        } else {
                            System.err.println("Personnel " + p.getId()
                                    + " not insert : already exists.");
                        }
                    }
                }
                insertAnnuaire.setNull(3, Types.INTEGER);
                insertAnnuaire.setInt(2, p.getId());
            } else if (racine instanceof Groupe) {
                Groupe g = (Groupe) racine;
                try (PreparedStatement selectGroupe =
                        connection.prepareStatement(
                                "SELECT * FROM groupe WHERE id = ?")) {
                    selectGroupe.setInt(1, g.getId());
                    try (ResultSet rs = selectGroupe.executeQuery()) {
                        if (!rs.next()) {
                            GroupeDAOJDBC.insert(g, connection);
                        } else {
                            System.err.println("Groupe " + g.getId()
                                    + " not insert : already exists.");
                        }
                    }
                }
                insertAnnuaire.setNull(2, Types.INTEGER);
                insertAnnuaire.setInt(3, g.getId());
            }
            insertAnnuaire.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    /**
     * Pour la recherche.
     * Retourne l'annuaire avec comme identifiant id,
     * présent dans la base de donnée.
     * @param id Identifiant de l'annuaire à trouver
     * @return Annauire trouvé, ou null en cas d'erreur
     */
    @Override
    public Annuaire find(final String id) {
        Annuaire a = null;
        try (PreparedStatement selectAnnuaire = connection
                .prepareStatement("SELECT * FROM annuaire WHERE id = ?")) {
            selectAnnuaire.setInt(1, Integer.parseInt(id));
            try (ResultSet rs = selectAnnuaire.executeQuery()) {
                if (rs.next()) {
                    int idRacine = rs.getInt("racine_personnel");
                    if (!rs.wasNull()) {
                        Personnel p =
                                PersonnelDAOJDBC.read(idRacine, connection);
                        a = new Annuaire(rs.getInt("id"), p);
                    } else {
                        idRacine = rs.getInt("racine_groupe");
                        if (!rs.wasNull()) {
                            Groupe g = GroupeDAOJDBC.read(idRacine, connection);
                            a = new Annuaire(rs.getInt("id"), g);
                        }
                    }
                } else {
                    System.err.println("No annuaire for id " + id);
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return a;
    }

    /**
     * Pour la modification.
     * Met à jour l'annuaire (repéré par son id) dans la BD.
     * Met à jour toute sa déscendance.
     * @param obj Annuaire modifié à mettre à jour
     * @return Annuaire modifié, ou null en cas d'erreur
     */
    @Override
    public Annuaire update(final Annuaire obj) {
        try (PreparedStatement updateAnnuaire = connection.prepareStatement(
                "UPDATE annuaire SET racine_groupe = ?, racine_personnel = ? "
                        + "WHERE id = ?")) {
            updateAnnuaire.setInt(3, obj.getId());
            Composant racine = obj.getRacine();
            if (racine instanceof Personnel) {
                Personnel p = (Personnel) racine;
                PersonnelDAOJDBC.modify(p, connection, true);
                updateAnnuaire.setNull(1, Types.INTEGER);
                updateAnnuaire.setInt(2, p.getId());
            } else if (racine instanceof Groupe) {
                Groupe g = (Groupe) racine;
                GroupeDAOJDBC.modify(g, connection, true);
                updateAnnuaire.setNull(2, Types.INTEGER);
                updateAnnuaire.setInt(1, g.getId());
            }
            updateAnnuaire.execute();
            return obj;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Pour la suppression.
     * Supprime l'annuaire obj de la base de donnée.
     * Ne supprime pas sa descendance.
     * @param obj Annuaire à supprimer
     */
    @Override
    public void delete(final Annuaire obj) {
        try (PreparedStatement delete = connection
                .prepareStatement("DELETE FROM annuaire WHERE id = ?")) {
            delete.setInt(1, obj.getId());
            delete.execute();
            System.out.println("Annuaire deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
