package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import fr.uvsq.uvsq21602576.pglp_5_2.Telephone;

public class TelephoneDAOJDBC extends DAO<Telephone> {
    Connection connection;

    public TelephoneDAOJDBC() {
        String dbUrl = "jdbc:derby:donneesPourDB\\jdbcDB;create=true";
        try {
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            connection = null;
            e.printStackTrace();
        }
    }

    public void close() {
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void normalDbUsage() throws SQLException {
        Statement stmt = connection.createStatement();

        // drop table
        // stmt.executeUpdate("Drop Table users");

        // create table
        DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, "users".toUpperCase(), null);
        if (rs.next()) {
            System.out.println("Table " +  rs.getString(3) + " exists");
        } else {
            stmt.execute("Create table users (id int primary key, name varchar(30))");
        }
        // insert 2 rows
        stmt.executeUpdate("insert into users values (3,'tom')");
        stmt.executeUpdate("insert into users values (4,'peter')");

        // query
        rs = stmt.executeQuery("SELECT * FROM users");

        // print out query result
        while (rs.next()) { 
            System.out.printf("%d\t%s\n", rs.getInt("id"), rs.getString("name"));
        }
    }

    private boolean tableExists() throws SQLException {
        DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getTables(null, null, "telephone".toUpperCase(), null);
        if (rs.next()) {
            return true;
        }
        return false;
    }

    @Override
    public Telephone create(Telephone obj) {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            if(!tableExists()) {
                stmt.execute("Create table telephone (id int primary key, numero varchar(30),"
                        + "information varchar(30))");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            stmt.executeUpdate("insert into telephone values ("
                    + obj.getId() + ", '"
                    + obj.getNumero() + "', '"
                    + obj.getInformation() + "')");
        } catch(DerbySQLIntegrityConstraintViolationException e) {
            System.err.println(e.getMessage());
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return obj;
    }

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
            rs = stmt.executeQuery("SELECT * FROM telephone "
                    + "WHERE id = " + id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        try {
            if(rs!=null && rs.next()) {
                Telephone t = new Telephone(rs.getInt("id"), rs.getString("numero"), rs.getString("information"));
                return t;
            } else {
                System.err.println("No telephone found with id "+id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

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
            stmt.executeUpdate("Update telephone SET "
                    + "numero = '" + obj.getNumero() + "', "
                    +  "information = '" + obj.getInformation() + "'"
                            + "WHERE id = " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        
        return obj;
    }

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
            stmt.executeUpdate("Delete from telephone "
                    + "WHERE id = " + obj.getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
    }

}
