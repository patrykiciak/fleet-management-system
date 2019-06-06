package other;

import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class KeyListenerFactoryTest {
    private JTextField jTextField;
    private JTable jTable;
    private String columnName;
    private ArrayList<RowFilter<Object, Object>> rowFilters;
    private TableRowSorter<TableModel> tableRowSorter;

    @Before
    public void prepare() {
        jTextField = new JTextField();
        jTable = new JTable();
        DefaultTableModel defaultTableModel = new DefaultTableModel();
        jTable.setModel(defaultTableModel);
        columnName = "Kolumna";
        rowFilters = new ArrayList<>();
        tableRowSorter = new TableRowSorter<>();
        jTable.setRowSorter(tableRowSorter);
    }

    @Test
    public void keyListenerFilterInColumns() {
        KeyListener keyListener = KeyListenerFactory.keyListenerFilterInColumns(jTextField, jTable, columnName, rowFilters, tableRowSorter);
        jTextField.addKeyListener(keyListener);
    }

    @Test
    public void keyListenerFilterInTable() {
        KeyListener keyListener = KeyListenerFactory.keyListenerFilterInColumns(null, null, null, null, null);
        jTextField.addKeyListener(keyListener);
    }
}