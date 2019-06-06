package other;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class ToTableExporter {
    public static void exportTable(JTable table, File file) throws IOException {
        TableModel model = table.getModel();
        FileWriter fileWriter = new FileWriter(file);
        String groupExport;

        for (int i = 0; i < (model.getColumnCount()); i++) {
            groupExport = String.valueOf(model.getColumnName(i));
            fileWriter.write(groupExport + "\t");
        }

        fileWriter.write("\n");
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < (model.getColumnCount()); j++) {
                if (model.getValueAt(i, j) == null) {
                    fileWriter.write("null" + "\t");
                } else {
                    groupExport = String.valueOf(model.getValueAt(i, j));
                    fileWriter.write(groupExport + "\t");
                }
            }
            fileWriter.write("\n");
        }
        fileWriter.close();
    }
}