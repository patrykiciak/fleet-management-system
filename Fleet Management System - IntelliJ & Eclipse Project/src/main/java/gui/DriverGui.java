package gui;

import controller.DriverController;
import model.Driver;
import other.KeyListenerFactory;
import other.RowFilterFactory;
import other.TaxationType;
import other.ToTableExporter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

class DriverGui extends JPanel {
    private static DriverGui driverGui;
    private static final DriverController driverController = DriverController.getInstance();


    ArrayList<Driver> getDrivers() throws SQLException {
        return driverController.readAll();
    }

    static synchronized DriverGui getInstance() {
        if (driverGui == null) {
            driverGui = new DriverGui();
        }
        return driverGui;
    }

    private DriverGui() {
        setLayout(new BorderLayout());
        final JTable jTable = drawTable();
        final ArrayList<String> columns = new ArrayList<>();
        try {
            columns.addAll(driverController.getColumns());
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
            JOptionPane.showMessageDialog(this, message.toArray(), "Filters", JOptionPane.PLAIN_MESSAGE);
        });

        refreshButton.addActionListener((event) -> {
            DefaultTableModel realTableModel = (DefaultTableModel) jTable.getModel();
            DefaultTableModel tempTableModel = (DefaultTableModel) drawTable().getModel();
            try {
                realTableModel.setDataVector(tempTableModel.getDataVector(), new Vector<>(driverController.getColumns()));
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

            message.add("Name");
            JTextField name = new JTextField();
            message.add(name);

            message.add("Surname");
            JTextField surname = new JTextField();
            message.add(surname);

            message.add("Address");
            JTextField address = new JTextField();
            message.add(address);

            message.add("Father Name");
            JTextField fatherName = new JTextField();
            message.add(fatherName);

            message.add("Identification Number");
            JTextField personalNr = new JTextField();
            message.add(personalNr);

            message.add("Serial Number of ID");
            JTextField idSerialNr = new JTextField();
            message.add(idSerialNr);

            message.add("Taxation Type");
            JComboBox<Object> taxationTypeBox = new JComboBox<>(new Object[] {
                    TaxationType.STUDENT,
                    TaxationType.WORKING_SOMEWHERE_ELSE,
                    TaxationType.NOT_WORKING_ANYWHERE_ELSE
            });
            message.add(taxationTypeBox);

            message.add("Bank Account Number");
            JTextField bankNumber = new JTextField();
            message.add(bankNumber);

            if (taxationTypeBox.getSelectedItem() != null && JOptionPane.showConfirmDialog(this, message.toArray(), "Add Driver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                Driver driver = null;
                try {
                    driver = driverController.create(
                            name.getText(),
                            surname.getText(),
                            address.getText(),
                            fatherName.getText(),
                            personalNr.getText(),
                            idSerialNr.getText(),
                            taxationTypeBox.getSelectedItem().toString(),
                            bankNumber.getText()
                    );
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (driver != null) {
                    DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
                    tableModel.addRow(driver.toArray());
                }
            }

        });

        updateButton.addActionListener((event) -> {
            int selectedRow = jTable.getSelectedRow();
            if (selectedRow != -1) {
                ArrayList<Object> message = new ArrayList<>();

                message.add("Name");
                JTextField name = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("name")).toString());
                message.add(name);

                message.add("Surname");
                JTextField surname = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("surname")).toString());
                message.add(surname);

                message.add("Address");
                JTextField address = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("address")).toString());
                message.add(address);

                message.add("Father Name");
                JTextField fatherName = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("fatherName")).toString());
                message.add(fatherName);

                message.add("Identification Number");
                JTextField personalNr = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("personalNr")).toString());
                message.add(personalNr);

                message.add("Serial Number of ID");
                JTextField idSerialNr = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("idSerialNr")).toString());
                message.add(idSerialNr);

                message.add("Taxation Type");
                TaxationType[] taxationTypes = new TaxationType[] {TaxationType.STUDENT, TaxationType.WORKING_SOMEWHERE_ELSE, TaxationType.NOT_WORKING_ANYWHERE_ELSE};
                JComboBox<Object> taxationTypeBox = new JComboBox<>(taxationTypes);
                message.add(taxationTypeBox);
                String currentTaxationType = jTable.getValueAt(selectedRow, columns.indexOf("taxationType")).toString();
                for(TaxationType taxationType: taxationTypes) {
                    if(currentTaxationType.equals(taxationType.getTaxationType())) taxationTypeBox.setSelectedItem(taxationType);
                }

                message.add("Bank Account Number");
                JTextField bankNumber = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("bankNumber")).toString());
                message.add(bankNumber);

                if (taxationTypeBox.getSelectedItem() != null && JOptionPane.showConfirmDialog(this, message.toArray(), "Update Driver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                    Driver driver = null;
                    int idIndex = jTable.getColumn("id").getModelIndex();
                    try {
                        driver = driverController.update(
                                Integer.parseInt(jTable.getModel().getValueAt(
                                        selectedRow, idIndex
                                ).toString()),
                                name.getText(),
                                surname.getText(),
                                address.getText(),
                                fatherName.getText(),
                                personalNr.getText(),
                                idSerialNr.getText(),
                                TaxationType.valueOf(taxationTypeBox.getSelectedItem().toString()),
                                bankNumber.getText()
                        );
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (driver != null) {
                        jTable.setValueAt(driver.getId(), selectedRow, jTable.getColumn("id").getModelIndex());
                        jTable.setValueAt(driver.getName(), selectedRow, jTable.getColumn("name").getModelIndex());
                        jTable.setValueAt(driver.getSurname(), selectedRow, jTable.getColumn("surname").getModelIndex());
                        jTable.setValueAt(driver.getAddress(), selectedRow, jTable.getColumn("address").getModelIndex());
                        jTable.setValueAt(driver.getFatherName(), selectedRow, jTable.getColumn("fatherName").getModelIndex());
                        jTable.setValueAt(driver.getPersonalNr(), selectedRow, jTable.getColumn("personalNr").getModelIndex());
                        jTable.setValueAt(driver.getIdSerialNr(), selectedRow, jTable.getColumn("idSerialNr").getModelIndex());
                        jTable.setValueAt(driver.getTaxationType(), selectedRow, jTable.getColumn("taxationType").getModelIndex());
                        jTable.setValueAt(driver.getBankNumber(), selectedRow, jTable.getColumn("bankNumber").getModelIndex());
                    }
                }
            }
        });

        deleteButton.addActionListener((event) -> {
            if (jTable.getSelectedRow() != -1) {
                if ((JOptionPane.showConfirmDialog(this, "Are you sure you wanna delete this driver?", "Delete Driver", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) == 0) {

                    int row = jTable.getSelectedRow();
                    int column = jTable.getColumn("id").getModelIndex();
                    try {
                        Driver driver = driverController.delete(Integer.parseInt(jTable.getValueAt(row, column).toString()));

                        if (driver != null) {
                            JOptionPane.showConfirmDialog(this, "The Driver has been deleted!", "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                            DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                            model.removeRow(row);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    private static JTable drawTable() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Driver> drivers = new ArrayList<>();

        try {
            columns = driverController.getColumns();
            drivers = driverController.readAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        JTable table = new JTable(new Vector<>(), new Vector<Object>(columns)) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (Driver driver : drivers) {
            model.addRow(driver.toArray());
        }

        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return table;
    }
}