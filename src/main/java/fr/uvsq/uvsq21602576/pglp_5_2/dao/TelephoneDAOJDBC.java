package fr.uvsq.uvsq21602576.pglp_5_2.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        stmt.executeUpdate("Create table users (id int primary key, name varchar(30))");

        // insert 2 rows
        stmt.executeUpdate("insert into users values (1,'tom')");
        stmt.executeUpdate("insert into users values (2,'peter')");

        // query
        ResultSet rs = stmt.executeQuery("SELECT * FROM users");

        // print out query result
        while (rs.next()) { 
            System.out.printf("%d\t%s\n", rs.getInt("id"), rs.getString("name"));
        }
    }

    @Override
    public Telephone create(Telephone obj) {
        try {
            normalDbUsage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Telephone find(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Telephone update(Telephone obj) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(Telephone obj) {
        // TODO Auto-generated method stub

    }

}
