package com.dod.db.migration.tasks;

import javafx.concurrent.Task;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.Callable;

/**
 * Created by Deniz on 13.01.2016.
 */
public class ConnectTask<T> implements Callable {
    private String url;
    private String driver;
    private String username;
    private String password;
    private TextArea consoleText ;

    public ConnectTask(String url, String driver, String username, String password,TextArea consoleText) {
        this.url = url;
        this.driver = driver;
        this.username = username;
        this.password = password;
        this.consoleText = consoleText;
    }

    @Override
    public Connection call()
    {
        Connection connection = null ;
        try {

                connection= createConnection(driver, url, username, password);


        }catch (ClassNotFoundException cne){
            consoleText.appendText("Connection could not be established due to ClassNotFound: "+cne.getMessage());
        }catch (SQLException se){
            consoleText.appendText("Connection could not be established due to SQLException: "+se.getMessage());
        }
        return connection;
    }

    public  Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        if ((username == null) || (password == null) || (username.trim().length() == 0) || (password.trim().length() == 0)) {
            return DriverManager.getConnection(url);
        } else {
            return DriverManager.getConnection(url, username, password);
        }
    }
}
