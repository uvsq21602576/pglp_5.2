package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import fr.uvsq.uvsq21602576.pglp_5_2.Composant;
import fr.uvsq.uvsq21602576.pglp_5_2.Groupe;
import fr.uvsq.uvsq21602576.pglp_5_2.IterateurComposant;
import fr.uvsq.uvsq21602576.pglp_5_2.Personnel;

/**
 * Classe DAO pour Groupe.
 * Marche avec la base de donnée intégré, présente à l'url
 * jdbc:derby:donneesPourDB\\jdbcDB.
 * @author Flora
 */
public class GroupeDAOJDBC extends DAO<Groupe> {
    /**
     * Connection avec la base de données.
     */
    private Connection connection;

    /**
     * Constructeur.
     * Initialise la connexion avec la base de donnée.
     */
    public GroupeDAOJDBC() {
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
     * Crée les tables utiles pour le Groupe.
     * C'est à dire : Telephone, Personnel, Possède,
     * Groupe, Appartenir et Contenir.
     * @param conn Connexion à la base de donnée
     * @throws SQLException En cas de problème lors de l'éxécution des requetes
     *         sql.
     */
    public static void createTables(final Connection conn) throws SQLException {
        TelephoneDAOJDBC.createTable(conn);
        PersonnelDAOJDBC.createTables(conn);
        Statement stmt = null;
        stmt = conn.createStatement();
        if (!tableExists("groupe", conn)) {
            stmt.executeUpdate("Create table groupe (" + "id int primary key, "
                    + "nom varchar(30) not null)");
        }

        if (!tableExists("appartenir", conn)) {
            stmt.executeUpdate("Create table appartenir ("
                    + "id_personnel int not null, " + "id_groupe int not null, "
                    + "primary key (id_personnel, id_groupe), "
                    + "foreign key (id_personnel) references personnel(id), "
                    + "foreign key (id_groupe) references groupe(id))");
        }

        if (!tableExists("contenir", conn)) {
            stmt.executeUpdate("Create table contenir ("
                    + "id_groupe_contenu int not null, "
                    + "id_groupe_contenant int not null, "
                    + "primary key (id_groupe_contenu, id_groupe_contenant), "
                    + "foreign key (id_groupe_contenu) references groupe(id), "
                    + "foreign key (id_groupe_contenant)"
                    + " references groupe(id))");
        }
    }

    /**
     * Insert l'objet groupe dans la base de donnée.
     * Via la connexion conn.
     * Insert aussi sa dépendance.
     * @param obj Groupe à insérer
     * @param conn Connexion à la base de donnée
     * @throws SQLException En cas d'erreur dues aux requetes sql.
     */
    static void insert(final Groupe obj, final Connection conn)
            throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();
        stmt.executeUpdate("insert into groupe values (" + obj.getId() + ", '"
                + obj.getNom() + "')");

        IterateurComposant ite = obj.iterateur();
        while (ite.hasNext()) {
            Composant c = ite.next();
            if (c instanceof Personnel) {
                Personnel p = (Personnel) c;
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM personnel WHERE id = " + p.getId());
                if (!rs.next()) {
                    PersonnelDAOJDBC.insert(p, conn);
                } else {
                    System.err.println("Personnel " + p.getId()
                            + " not insert : already exists.");
                }
                stmt.executeUpdate("insert into appartenir values " + "("
                        + p.getId() + ", " + obj.getId() + ")");
            } else if (c instanceof Groupe) {
                Groupe g = (Groupe) c;
                ResultSet rs = stmt.executeQuery(
                        "SELECT * FROM groupe WHERE id = " + g.getId());
                if (!rs.next()) {
                    GroupeDAOJDBC.insert(g, conn);
                } else {
                    System.err.println("Groupe " + g.getId()
                            + " not insert : already exists.");
                }
                stmt.executeUpdate("insert into contenir values " + "("
                        + g.getId() + ", " + obj.getId() + ")");
            }
        }
    }

    /**
     * Pour la création.
     * Enregistre le groupe dans la base de donnée.
     * @param obj Groupe à enregistré
     * @return Groupe enregistré, ou null en cas d'erreur
     */
    @Override
    public Groupe create(final Groupe obj) {
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

        return null;
    }

    /**
     * Lis le Groupe à l'id idGroupe.
     * Et toute sa descendance.
     * De la base de donnée, via la connexion conn.
     * Renvoie le Groupe formé.
     * @param idGroupe id du Groupe recherché
     * @param conn Connexion à la base de donnée
     * @return Groupe trouvé ou null si inexistant
     * @throws SQLException En cas d'erreur lors des requetes sql.
     */
    static Groupe read(final int idGroupe, final Connection conn)
            throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();

        ResultSet rs = null;
        rs = stmt.executeQuery(
                "Select * from Groupe " + "WHERE id = " + idGroupe);

        Groupe g = null;
        if (rs.next()) {
            g = new Groupe(idGroupe, rs.getString("nom"));
        } else {
            return null;
        }

        rs = stmt.executeQuery(
                "Select * from Appartenir " + "WHERE id_groupe = " + idGroupe);
        while (rs.next()) {
            Personnel p =
                    PersonnelDAOJDBC.read(rs.getInt("id_personnel"), conn);
            if (p != null) {
                g.add(p);
            }
        }

        rs = stmt.executeQuery("Select * from contenir "
                + "WHERE id_groupe_contenant = " + idGroupe);
        while (rs.next()) {
            Groupe gFils =
                    GroupeDAOJDBC.read(rs.getInt("id_groupe_contenu"), conn);
            if (gFils != null) {
                g.add(gFils);
            }
        }

        return g;
    }

    /**
     * Pour la recherche.
     * Retourne le groupe avec comme identifiant id,
     * présent dans la base de donnée.
     * @param id Identifiant du groupe à trouver
     * @return Groupe trouvé, ou null en cas d'erreur
     */
    @Override
    public Groupe find(final String id) {
        try {
            return GroupeDAOJDBC.read(Integer.parseInt(id), connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Met à jour la base de donnée.
     * Modifie l'obj groupe et sa descendance.
     * Le booléen insert indique si l'obj groupe doit être
     * insérer en cas d'inexistance dans la base de donnée.
     * Via la connexion à la base de donnée conn.
     * @param obj Groupe à ajouter
     * @param conn Connexion à la base de donnée
     * @param insert Bouléen indiquant si obj doit être inséré si non existant.
     * @return Si l'objet à bien pu être modifié.
     * @throws SQLException En cas d'erreur lors des requetes SQL.
     */
    static boolean modify(final Groupe obj, final Connection conn,
            final boolean insert) throws SQLException {
        Statement stmt = null;
        stmt = conn.createStatement();

        ResultSet rs = stmt
                .executeQuery("SELECT * FROM groupe WHERE id = " + obj.getId());
        if (!rs.next()) {
            if (insert) {
                stmt.executeUpdate("insert into groupe values (" + obj.getId()
                        + ", '" + obj.getNom() + "')");
            } else {
                return false;
            }
        }

        stmt.executeUpdate("UPDATE groupe SET nom = '" + obj.getNom()
                + "' WHERE id = " + obj.getId());

        stmt.executeUpdate(
                "DELETE from appartenir WHERE id_groupe = " + obj.getId());
        stmt.executeUpdate("DELETE from contenir WHERE id_groupe_contenant = "
                + obj.getId());

        IterateurComposant ite = obj.iterateur();
        while (ite.hasNext()) {
            Composant c = ite.next();
            if (c instanceof Personnel) {
                Personnel p = (Personnel) c;
                PersonnelDAOJDBC.modify(p, conn, true);
                stmt.executeUpdate("insert into appartenir values " + "("
                        + p.getId() + ", " + obj.getId() + ")");
            } else if (c instanceof Groupe) {
                Groupe g = (Groupe) c;
                GroupeDAOJDBC.modify(g, conn, true);
                stmt.executeUpdate("insert into contenir values " + "("
                        + g.getId() + ", " + obj.getId() + ")");
            }
        }
        return true;
    }

    /**
     * Pour la modification.
     * Met à jour le groupe (repéré par son id) dans la BD.
     * Met à jour toute sa déscendance.
     * @param obj Groupe modifié à mettre à jour
     * @return Groupe modifié, ou null en cas d'erreur
     */
    @Override
    public Groupe update(final Groupe obj) {
        try {
            if (!modify(obj, connection, false)) {
                return null;
            }
            return obj;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Pour la suppression.
     * Supprime le groupe obj de la base de donnée.
     * Ne supprime pas sa descendance.
     * @param obj Groupe à supprimer
     */
    @Override
    public void delete(final Groupe obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        try {
            stmt.executeUpdate(
                    "DELETE from appartenir WHERE id_groupe = " + obj.getId());
            stmt.executeUpdate(
                    "DELETE from contenir WHERE id_groupe_contenant = "
                            + obj.getId());
            stmt.executeUpdate("DELETE from contenir WHERE id_groupe_contenu = "
                    + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            System.out.println(stmt.executeUpdate(
                    "DELETE from groupe WHERE id = " + obj.getId())
                    + " groupes deleted.");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

}
