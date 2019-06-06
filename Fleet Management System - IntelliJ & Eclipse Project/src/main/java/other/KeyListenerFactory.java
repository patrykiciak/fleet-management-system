package other;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.regex.Pattern;

public abstract class KeyListenerFactory {
    public static KeyListener keyListenerFilterInColumns(JTextField jTextField, JTable jTable, String columnName, ArrayList<RowFilter<Object, Object>> busFilter, TableRowSorter<TableModel> tableRowSorter) {
        return new KeyListener() {
            RowFilter<Object, Object> assignedFilter;
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyPressed(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) {
                String regex = Pattern.quote(jTextField.getText());
                int index = jTable.getColumn(columnName).getModelIndex();
                if (assignedFilter != null) busFilter.remove(assignedFilter);

                assignedFilter = RowFilter.regexFilter(regex, index);
                busFilter.add(assignedFilter);
                tableRowSorter.sort();
            }
        };
    }

    public static KeyListener keyListenerFilterInTable(JTextField searchField, ArrayList<RowFilter<Object, Object>> busFilter, TableRowSorter<TableModel> tableRowSorter) {
        return new KeyListener() {
            RowFilter<Object, Object> assignedFilter;
            @Override public void keyTyped(KeyEvent e) {}
            @Override public void keyPressed(KeyEvent e) {}
            @Override public void keyReleased(KeyEvent e) {
                String regex = Pattern.quote(searchField.getText());
                if(assignedFilter !=null) busFilter.remove(assignedFilter);
                assignedFilter = RowFilter.regexFilter(regex);

                busFilter.add(assignedFilter);
                tableRowSorter.sort();
            }
        };
    }
}
