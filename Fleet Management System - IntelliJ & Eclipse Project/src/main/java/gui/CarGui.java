package gui;

import controller.CarController;
import model.Car;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.SqlDateModel;
import other.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

class CarGui extends JPanel {
    private static CarGui carGui;
    private static final CarController carController = CarController.getInstance();

    ArrayList<Car> getCars() throws SQLException {
        return carController.readAll();
    }

    static synchronized CarGui getInstance() {
        if (carGui == null) {
            carGui = new CarGui();
        }
        return carGui;
    }

    private CarGui() {
        setLayout(new BorderLayout());
        final JTable jTable = drawTable();
        final ArrayList<String> columns = new ArrayList<>();
        try {
            columns.addAll(carController.getColumns());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        // MAKE FILTERS
        final ArrayList<RowFilter<Object, Object>> subFilters = new ArrayList<>();
        final TableRowSorter<TableModel> tableRowSorter = new TableRowSorter<>(jTable.getModel());
        RowFilter<Object, Object> mainFilter = RowFilterFactory.multipleRowFilter(subFilters);
        tableRowSorter.setRowFilter(mainFilter);
        jTable.setRowSorter(tableRowSorter);

        final ArrayList<JTextField> filterTextFields = new ArrayList<>();

        for (String columnName : columns) {
            JTextField jTextField = new JTextField();
            filterTextFields.add(jTextField);
            jTextField.addKeyListener(KeyListenerFactory.keyListenerFilterInColumns(jTextField, jTable, columnName, subFilters, tableRowSorter));
        }

        JScrollPane jScrollPane = new JScrollPane(jTable);
        this.add(jScrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh table");
        JButton exportButton = new JButton("Export to file");
        JLabel searchLabel = new JLabel("   Search: ");
        JTextField searchField = new JTextField("", 20);

        bottom.add(refreshButton);
        bottom.add(exportButton);
        bottom.add(searchLabel);
        bottom.add(searchField);
        this.add(bottom, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel();
        JPanel rightButtonsContainer = new JPanel();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.PAGE_START;
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.setConstraints(rightButtonsContainer, gbc);
        rightButtonsContainer.setLayout(gridBagLayout);

        JButton addButton = new JButton("Add row");
        JButton updateButton = new JButton("Update row");
        JButton deleteButton = new JButton("Delete row");
        JButton filtersButton = new JButton("Filters");
        JButton clearFiltersButton = new JButton("Clear filters");

        rightButtonsContainer.add(addButton, gbc);
        rightButtonsContainer.add(updateButton, gbc);
        rightButtonsContainer.add(deleteButton, gbc);
        rightButtonsContainer.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        rightButtonsContainer.add(filtersButton, gbc);
        rightButtonsContainer.add(clearFiltersButton, gbc);

        rightPanel.add(rightButtonsContainer);
        this.add(rightPanel, BorderLayout.EAST);

        clearFiltersButton.addActionListener((clearEvent) -> {
            filterTextFields.forEach((filterTextField) -> filterTextField.setText(""));
            subFilters.removeIf((element) -> true);
            tableRowSorter.sort();
        });

        searchField.addKeyListener(KeyListenerFactory.keyListenerFilterInTable(searchField, subFilters, tableRowSorter));

        filtersButton.addActionListener((event) -> {
            ArrayList<Object> message = new ArrayList<>();
            for (String column : columns) {
                message.add(column);
                message.add(filterTextFields.get(columns.indexOf(column)));
            }


            JOptionPane.showConfirmDialog(this, message.toArray(), "Filters", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
        });

        refreshButton.addActionListener((event) -> {
            DefaultTableModel realTableModel = (DefaultTableModel) jTable.getModel();
            DefaultTableModel tempTableModel = (DefaultTableModel) drawTable().getModel();
            try {
                realTableModel.setDataVector(tempTableModel.getDataVector(), new Vector<>(carController.getColumns()));
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        });

        exportButton.addActionListener((event -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    ToTableExporter.exportTable(jTable, file);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }));

        addButton.addActionListener(event -> {

            ArrayList<Object> message = new ArrayList<>();

            message.add("Car Manufacturer");
            JTextField carManufacturer = new JTextField();
            message.add(carManufacturer);

            message.add("Car Model");
            JTextField carModel = new JTextField();
            message.add(carModel);

            message.add("Year of Production");
            JSpinner year = JSpinnerFactory.yearSpinner();
            message.add(year);

            message.add("Number Plate");
            JTextField numberPlate = new JTextField();
            message.add(numberPlate);

            message.add("Insurance Number");
            JTextField insuranceNr = new JTextField();
            message.add(insuranceNr);

            message.add("Technical Examination Expiration Date");
            JDatePicker technicalExpirationDate = JDatePickerFactory.createDatePicker();
            message.add(technicalExpirationDate);

            message.add("Insurance Expiration Date");
            JDatePicker insuranceExpirationDate = JDatePickerFactory.createDatePicker();
            message.add(insuranceExpirationDate);

            SqlDateModel technicalExpirationDateModel = (SqlDateModel) technicalExpirationDate.getModel();
            SqlDateModel insuranceExpirationDateModel = (SqlDateModel) insuranceExpirationDate.getModel();

            if (JOptionPane.showConfirmDialog(this, message.toArray(), "Add Car", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                Car car = null;
                try {
                    car = carController.create(
                            carManufacturer.getText(),
                            carModel.getText(),
                            Date.valueOf(year.getValue().toString() + "-01-01"),
                            numberPlate.getText(),
                            insuranceNr.getText(),
                            technicalExpirationDateModel.getValue(),
                            insuranceExpirationDateModel.getValue()
                    );
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (car != null) {
                    DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
                    tableModel.addRow(car.toArray());
                }
            }

        });

        updateButton.addActionListener((event -> {


            int selectedRow = jTable.getSelectedRow();

            if(selectedRow != -1) {

                ArrayList<Object> message = new ArrayList<>();

                message.add("Car Manufacturer");
                JTextField carManufacturer = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("carManufacturer")).toString());
                message.add(carManufacturer);

                message.add("Car Model");
                JTextField carModel = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("model")).toString());
                message.add(carModel);

                message.add("Year of Production");
                JSpinner year = JSpinnerFactory.yearSpinner(jTable.getValueAt(selectedRow, columns.indexOf("year")).toString());
                message.add(year);

                message.add("Number Plate");
                JTextField numberPlate = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("numberPlate")).toString());
                message.add(numberPlate);

                message.add("Insurance Number");
                JTextField insuranceNr = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("insuranceNr")).toString());
                message.add(insuranceNr);

                message.add("Technical Examination Expiration Date");
                JDatePicker technicalExpirationDate = JDatePickerFactory.createDatePicker(jTable.getValueAt(selectedRow, columns.indexOf("technicalExpirationDate")).toString());
                message.add(technicalExpirationDate);

                message.add("Insurance Expiration Date");
                JDatePicker insuranceExpirationDate = JDatePickerFactory.createDatePicker(jTable.getValueAt(selectedRow, columns.indexOf("insuranceExpirationDate")).toString());
                message.add(insuranceExpirationDate);

                SqlDateModel technicalExpirationDateModel = (SqlDateModel) technicalExpirationDate.getModel();
                SqlDateModel insuranceExpirationDateModel = (SqlDateModel) insuranceExpirationDate.getModel();

                if (JOptionPane.showConfirmDialog(this, message.toArray(), "Update Car", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                    Car car = null;

                    int idIndex = jTable.getColumn("id").getModelIndex();
                    try {
                        car = carController.update(

                                Integer.parseInt(jTable.getModel().getValueAt(
                                        selectedRow, idIndex
                                ).toString()),

                                carManufacturer.getText(),
                                carModel.getText(),
                                Date.valueOf(year.getValue().toString() + "-01-01"),
                                numberPlate.getText(),
                                insuranceNr.getText(),
                                technicalExpirationDateModel.getValue(),
                                insuranceExpirationDateModel.getValue()
                        );
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (car != null) {
                        jTable.setValueAt(car.getId(), selectedRow, idIndex);
                        jTable.setValueAt(car.getCarManufacturer(), selectedRow, jTable.getColumn("carManufacturer").getModelIndex());
                        jTable.setValueAt(car.getModel(), selectedRow, jTable.getColumn("model").getModelIndex());
                        jTable.setValueAt(car.getYear(), selectedRow, jTable.getColumn("year").getModelIndex());
                        jTable.setValueAt(car.getNumberPlate(), selectedRow, jTable.getColumn("numberPlate").getModelIndex());
                        jTable.setValueAt(car.getInsuranceNr(), selectedRow, jTable.getColumn("insuranceNr").getModelIndex());
                        jTable.setValueAt(car.getTechnicalExpirationDate(), selectedRow, jTable.getColumn("technicalExpirationDate").getModelIndex());
                        jTable.setValueAt(car.getInsuranceExpirationDate(), selectedRow, jTable.getColumn("insuranceExpirationDate").getModelIndex());
                    }
                }
            }
        }));

        deleteButton.addActionListener((event) -> {
            int selectedRow = jTable.getSelectedRow();
            if (selectedRow != -1) {
                if ((JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this car?", "Delete Car", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) == 0) {
                    int idColumnIndex = jTable.getColumn("id").getModelIndex();

                    Car car = null;

                    try {
                        car = carController.delete(Integer.parseInt(jTable.getValueAt(selectedRow, idColumnIndex).toString()));
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (car != null) {
                        JOptionPane.showConfirmDialog(this, "The Car has been deleted!", "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                        model.removeRow(selectedRow);
                    }
                }
            }
        });
    }

    private static JTable drawTable() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Car> cars = new ArrayList<>();

        try {
            columns = carController.getColumns();
            cars = carController.readAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        JTable table = new JTable(new Vector<>(), new Vector<Object>(columns)) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (Car car : cars) {
            model.addRow(car.toArray());
        }

        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return table;
    }
}