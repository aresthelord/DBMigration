package com.dod.db.migration.tasks;

import com.dod.db.migration.controls.TitledBorder;
import com.dod.db.migration.main.MainController;
import com.dod.db.migration.util.Enums;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Deniz on 13.01.2016.
 */
public class MetaDataTask implements Runnable {
    private DatabaseMetaData dbMetaData;
    private TreeView<String> treeView;
    private Connection connection;
    private TextArea consoleText;
    private Enums enums;
    private TitledBorder group1;
    private TitledBorder group2;
    private TitledBorder group3;
    private TitledBorder group4;
    private final  Node dbIcon = new ImageView(new Image(MainController.class.getResourceAsStream("db_icon.png")));
    private final  Image tableIcon = new Image(MainController.class.getResourceAsStream("table_icon.png"));
    private final  Image fieldIcon = new Image(MainController.class.getResourceAsStream("field_icon.png"));

    public MetaDataTask(TreeView<String> treeView, Connection connection, TextArea consoleText,Enums enums,TitledBorder group1,TitledBorder group2, TitledBorder group3, TitledBorder group4) {
        this.treeView = treeView;
        this.connection = connection;
        this.consoleText = consoleText;
        this.enums = enums;
        this.group1=group1;
        this.group2= group2;
        this.group3= group3;
        this.group4 = group4;
    }

    @Override
    public void run(){
        TreeItem<String> root = null;


       switch (enums){
           case SOURCEDATABASE: root = new TreeItem<String>("SOURCE",dbIcon);
               break;
           case TARGETDATABASE: root = new TreeItem<String>("TARGET",dbIcon);
               break;
           default: root = new TreeItem<String>("Database",dbIcon);
               break;
       }
        root.setExpanded(false);

        try {
            dbMetaData = connection.getMetaData();
            prepareDbNodes(root);
            treeView.setRoot(root);
        } catch (SQLException e) {
            consoleText.appendText("Database Metadata Could not be acquired " + e.getMessage());
        }

        ContextMenu rootContextMenu
                = ContextMenuBuilder.create()
                .items(
                        MenuItemBuilder.create()
                                .text("Add Group 1")
                                .onAction(
                                        new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent arg0) {
                                                ReadOnlyObjectProperty<TreeItem<String>> selectedItemProperty = treeView.getSelectionModel().selectedItemProperty();
                                                final TreeItem<String> treeItem = selectedItemProperty.getValue();
                                                System.out.println("Added Group 1!" +treeItem.getValue());
                                                group1.autosize();
                                                group1.setContent(new Label(treeItem.getValue()));
                                                //group1.getChildren().add(new Label(treeItem.getValue()));
                                            }
                                        }
                                )
                                .build(),
                        MenuItemBuilder.create()
                                .text("Add Group 2")
                                .onAction(
                                        new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent arg0) {
                                                ReadOnlyObjectProperty<TreeItem<String>> selectedItemProperty = treeView.getSelectionModel().selectedItemProperty();
                                                final TreeItem<String> treeItem = selectedItemProperty.getValue();
                                                System.out.println("Added Group 2!" +treeItem.getValue());
                                                group2.getChildren().add(new Label(treeItem.getValue()));
                                            }
                                        }
                                )
                                .build(),
                        MenuItemBuilder.create()
                                .text("Add Group 3")
                                .onAction(
                                        new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent arg0) {
                                                ReadOnlyObjectProperty<TreeItem<String>> selectedItemProperty = treeView.getSelectionModel().selectedItemProperty();
                                                final TreeItem<String> treeItem = selectedItemProperty.getValue();
                                                System.out.println("Added Group 3!" +treeItem.getValue());
                                                group3.getChildren().add(new Label(treeItem.getValue()));
                                            }
                                        }
                                )
                                .build(),
                        MenuItemBuilder.create()
                                .text("Add Group 4")
                                .onAction(
                                        new EventHandler<ActionEvent>() {
                                            @Override
                                            public void handle(ActionEvent arg0) {
                                                ReadOnlyObjectProperty<TreeItem<String>> selectedItemProperty = treeView.getSelectionModel().selectedItemProperty();
                                                final TreeItem<String> treeItem = selectedItemProperty.getValue();
                                                System.out.println("Added Group 4!" +treeItem.getValue());
                                                group4.getChildren().add(new Label(treeItem.getValue()));
                                            }
                                        }
                                )
                                .build()
                )
                .build();

        treeView.setContextMenu(rootContextMenu);


    }

    private TreeItem<String> prepareDbNodes(TreeItem<String> root) throws SQLException{
        String[] tableTypes = {"TABLE"};

            ResultSet tables = dbMetaData.getTables(null, null, null, tableTypes);
            String tableName ;
            TreeItem<String> tableNode;
            while(tables.next()){

                tableName = tables.getString("TABLE_NAME");
                tableNode = new TreeItem<String>(tableName,new ImageView(tableIcon));

                ResultSet columnNames = dbMetaData.getColumns(null, null, tableName, null);
                while(columnNames.next()){
                    tableNode.getChildren().add(new TreeItem<String>(columnNames.getString("COLUMN_NAME"),new ImageView(fieldIcon)));
                }
                root.getChildren().add(tableNode);

            }
        return root;

    }


}
