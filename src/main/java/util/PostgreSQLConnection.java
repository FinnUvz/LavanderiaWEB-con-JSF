/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnection {
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/NuevaLavanderiaWeb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "12345";
    
    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        Class.forName(JDBC_DRIVER);
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}
