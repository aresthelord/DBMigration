package com.dod.db.migration.main;

import com.dod.db.migration.controls.TitledBorder;
import com.dod.db.migration.tasks.ConnectTask;
import com.dod.db.migration.tasks.MetaDataTask;
import com.dod.db.migration.util.Enums;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.*;


public class MainController {
    @FXML
    private ComboBox<String> sourceDbUrlOptions;
    @FXML
    private ComboBox<String> sourceDbDriverOptions;
    @FXML
    private TextField sourceDbUserName;
    @FXML
    private TextField sourceDbPassword;
    @FXML
    private Button sourceDbConnectButton;
    @FXML
    private Button sourceDbDisconnectButton;

    @FXML
    private ComboBox<String> targetDbUrlOptions;
    @FXML
    private ComboBox<String> targetDbDriverOptions;
    @FXML
    private TextField targetDbUserName;
    @FXML
    private TextField targetDbPassword;
    @FXML
    private Button targetDbConnectButton;
    @FXML
    private Button targetDbDisconnectButton;

    @FXML
    private TreeView<String> sourceDbTreeView;
    @FXML
    private TreeView<String> targetDbTreeView;
    @FXML
    private TextArea consoleText;
    @FXML
    private VBox threadGroup;
    @FXML
    private TitledBorder group1;
    @FXML
    private TitledBorder group2;
    @FXML
    private TitledBorder group3;
    @FXML
    private TitledBorder group4;

    private String sourceDbUrl;
    private String targetDbUrl;
    private String sourceDbDriver;
    private String targetDbDriver;
    private String sourceDbUsernameValue;
    private String targetDbUsernameValue;
    private String sourceDbPasswordValue;
    private String targetDbPasswordValue;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Connection sourceConnection;

    private ObservableList<String> dbUrlOptions =
            FXCollections.observableArrayList(
                    "jdbc:db2://kh001dvt00:50000",
                    "jdbc:db2://kh001dys04:50000",
                    "jdbc:as400://JGNK003",
                    "jdbc:mysql://94.73.145.204:3306/dbdukkan1712"
            );
    private ObservableList<String> dbDriverOptions =
            FXCollections.observableArrayList(
                    "com.ibm.as400.access.AS400JDBCDriver",
                    "sun.jdbc.odbc.JdbcOdbcDriver",
                    "com.ibm.db2.jcc.DB2Driver",
                    "com.mysql.jdbc.Driver"
            );

    public void initialize() {
        loadTreeItems(sourceDbTreeView, "Source DB", "Not Connected");
        loadTreeItems(targetDbTreeView, "Target DB", "Not Connected");
        addListener();
        initOptions(sourceDbDriverOptions,dbDriverOptions);
        initOptions(targetDbDriverOptions,dbDriverOptions);
        initOptions(sourceDbUrlOptions,dbUrlOptions);
        initOptions(targetDbUrlOptions,dbUrlOptions);
    }

    private void initOptions(ComboBox<String> comboBox , ObservableList<String> options) {
        comboBox.setItems(options);
    }

    // loads some strings into the tree in the application UI.
    public void loadTreeItems(TreeView<String> treeView, String rootName, String... rootItems) {
        TreeItem<String> root = new TreeItem<String>(rootName);
        root.setExpanded(true);
        for (String itemString : rootItems) {
            root.getChildren().add(new TreeItem<String>(itemString));
        }

        treeView.setRoot(root);
    }

    private void addListener() {
        sourceDbUrlOptions.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue observable, String oldValue, String newValue) {
                /*System.out.println(ov);
                System.out.println(t);
                System.out.println(t1);*/
                sourceDbUrl = newValue;
                consoleText.appendText("Source DB Selecting Url to connect : "+sourceDbUrl+"\n");
            }
        });
        targetDbUrlOptions.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue observable, String oldValue, String newValue) {
                /*System.out.println(ov);
                System.out.println(t);
                System.out.println(t1);*/
                targetDbUrl = newValue;
                consoleText.appendText("Target DB Selecting Url to connect : "+targetDbUrl+"\n");
            }
        });
        sourceDbDriverOptions.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue observable, String oldValue, String newValue) {
                sourceDbDriver = newValue;
                consoleText.appendText("Source DB Selecting Driver to connect : "+sourceDbDriver+"\n");
            }
        });
        targetDbDriverOptions.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue observable, String oldValue, String newValue) {
                targetDbDriver = newValue;
                consoleText.appendText("Target DB Selecting Driver to connect : "+targetDbDriver+"\n");
            }
        });

        /*sourceDbTreeView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {

                    @Override
                    public void changed(
                            ObservableValue<? extends TreeItem<String>> observable,
                            TreeItem<String> oldValue, TreeItem<String> newValue) {
                        TreeItem<String> selectedItem = newValue;

                        consoleText.appendText("Selected Text : " + selectedItem.getValue() + "\n");


                        // do what ever you want
                    }

                });*/
        targetDbTreeView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<String>>() {

                    @Override
                    public void changed(
                            ObservableValue<? extends TreeItem<String>> observable,
                            TreeItem<String> oldValue, TreeItem<String> newValue) {
                        TreeItem<String> selectedItem = newValue;

                        consoleText.appendText("Selected Text : " + selectedItem.getValue() + "\n");
                        // do what ever you want
                    }

                });
        sourceDbConnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                consoleText.appendText("Source DB Attempting to connect\n");
                consoleText.appendText("Using URL : "+sourceDbUrl+"\n");
                consoleText.appendText("Using Driver : "+sourceDbDriver+"\n");
                sourceDbUsernameValue = sourceDbUserName.getText().trim();
                consoleText.appendText("Using UserName : "+sourceDbUsernameValue+"\n");
                sourceDbPasswordValue = sourceDbPassword.getText().trim();
                consoleText.appendText("Using Password : "+sourceDbPasswordValue+"\n");
                consoleText.appendText("Source DB Attempting to connect\n");

                ConnectTask<Connection> task =  new ConnectTask<Connection>(sourceDbUrl,sourceDbDriver,sourceDbUsernameValue,sourceDbPasswordValue,consoleText);
                Future<Connection> future  =  executorService.submit(task);
                try {
                    while (!future.isDone()) {
                        System.out.println("Connection not established yet....");
                        Thread.sleep(1); //sleep for 1 millisecond before checking again

                    }

                    sourceConnection = future.get();

                } catch (InterruptedException e1) {
                    consoleText.appendText("Connection interrupted " + e1.getMessage());
                } catch (ExecutionException e1) {
                    consoleText.appendText("Connection interrupted " + e1.getMessage());
                }
                if(sourceConnection != null){
                    consoleText.appendText("Connection Established\n");
                    sourceDbConnectButton.setText("Connected");
                    MetaDataTask metaDataTask = new MetaDataTask(sourceDbTreeView, sourceConnection, consoleText , Enums.SOURCEDATABASE,group1,group2,group3,group4);
                    Platform.runLater(metaDataTask);

                }
            }
        });
        sourceDbDisconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                consoleText.appendText("Source DB Attempting to disconnect\n");
                if(sourceConnection != null){
                    try {
                        if(!sourceConnection.isClosed()){
                            sourceConnection.close();
                            consoleText.appendText("Connection Closed\n");
                        }
                    } catch (SQLException e1) {
                        consoleText.appendText("Connection Already Closed\n");
                    }
                    sourceConnection = null;
                    sourceDbConnectButton.setText("Connect");
                }

            }
        });
        targetDbConnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                consoleText.appendText("Target DB Attempting to connect\n");
                consoleText.appendText("Using URL : "+targetDbUrl+"\n");
                consoleText.appendText("Using Driver : "+targetDbDriver+"\n");
                targetDbUsernameValue = targetDbUserName.getText().trim();
                consoleText.appendText("Using UserName : "+targetDbUsernameValue+"\n");
                targetDbPasswordValue = targetDbPassword.getText().trim();
                consoleText.appendText("Using Password : "+targetDbPasswordValue+"\n");
                consoleText.appendText("Target DB Attempting to connect\n");
                targetDbConnectButton.setText("Connected");
            }
        });
        targetDbDisconnectButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                consoleText.appendText("Target DB Attempting to disconnect\n");
                targetDbConnectButton.setText("Connect");
            }
        });
    }




}
