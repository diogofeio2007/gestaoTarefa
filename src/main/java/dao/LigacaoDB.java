package dao;

import util.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LigacaoDB {
    public static Connection conectarDB() {
        try{
            return DriverManager.getConnection(Config.get("db.url"), Config.get("db.user"), Config.get("db.password"));
        }
        catch (SQLException e){
            System.err.println(e.getMessage());
            return null;
        }
    }
}
