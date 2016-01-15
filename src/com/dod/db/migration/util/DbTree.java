package com.dod.db.migration.util;

import com.dod.db.migration.console.ConsoleFrame;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
/**
 * Created by Deniz on 12.01.2016.
 */
public class DbTree  extends JTree implements TreeSelectionListener {


    public DbTree(DatabaseMetaData metadata,DefaultMutableTreeNode rootNode)  {
        String[] tableTypes = {"TABLE"};
        try{
            ResultSet tables = metadata.getTables(null, null, null, tableTypes);
            String tableName ;
            DefaultMutableTreeNode tableNode;
            while(tables.next()){

                tableName = tables.getString("TABLE_NAME");
                tableNode = new DefaultMutableTreeNode(tableName);

                ResultSet columnNames = metadata.getColumns(null, null, tableName, null);
                while(columnNames.next()){
                    tableNode.add(new DefaultMutableTreeNode(columnNames.getString("COLUMN_NAME")));
                }
                rootNode.add(tableNode);

            }
        }catch(Exception ex){
            ConsoleFrame.hataYaz("Hata : Metadata Çekilirken Hata Oluştu :" + ex.getMessage());
        }

    }


    public void valueChanged(TreeSelectionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
