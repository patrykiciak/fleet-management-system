package gui;

import controller.CarController;
import controller.CarSwapController;
import controller.DriverController;
import model.Car;
import model.CarSwap;
import model.Driver;
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
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

class CarSwapGui extends JPanel {
    private static CarSwapGui carSwapGui;
    private static final CarSwapController carSwapController = CarSwapController.getInstance();

    static synchronized CarSwapGui getInstance() {
        if (carSwapGui == null) {
            carSwapGui = new CarSwapGui();
        }
        return carSwapGui;
    }

    private CarSwapGui() {
        setLayout(new BorderLayout());
        final JTable jTable = drawTable();
        final ArrayList<String> columns = new ArrayList<>();
        try {
            columns.addAll(carSwapController.getColumns());
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
        JButton seeDetailsButton = new JButton("See details");

        rightButtonsContainer.add(addButton, gbc);
        rightButtonsContainer.add(updateButton, gbc);
        rightButtonsContainer.add(deleteButton, gbc);
        rightButtonsContainer.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        rightButtonsContainer.add(filtersButton, gbc);
        rightButtonsContainer.add(clearFiltersButton, gbc);
        rightButtonsContainer.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        rightButtonsContainer.add(seeDetailsButton, gbc);

        rightPanel.add(rightButtonsContainer);
        this.add(rightPanel, BorderLayout.EAST);

        seeDetailsButton.addActionListener((event) -> {
            int selectedRow = jTable.getSelectedRow();
            Car car = null;
            Driver previousDriver = null;
            Driver currentDriver = null;
            CarSwap carSwap = null;
            try {
                car = CarController.getInstance().readOne(Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("carId")).toString()));
                previousDriver = DriverController.getInstance().readOne(Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("previousDriverId")).toString()));
                currentDriver = DriverController.getInstance().readOne(Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("currentDriverId")).toString()));
                carSwap = new CarSwap(
                        Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("id")).toString()),
                        (Date)jTable.getValueAt(selectedRow, columns.indexOf("startDate")),
                        (Date) jTable.getValueAt(selectedRow, columns.indexOf("endDate")),
                        Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("carId")).toString()),
                        Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("previousDriverId")).toString()),
                        Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("currentDriverId")).toString())
                );
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            if(selectedRow != -1 && car !=  null && previousDriver != null && currentDriver != null) {
                JFrame detailsFrame = new JFrame("Car Swap Details");
                detailsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                JPanel panel = new JPanel(new GridBagLayout());
                detailsFrame.add(panel);

//                JLabel textLabel = new JLabel("Car " + car.getCarManufacturer() + " " + car.getModel() + " (" + car.getNumberPlate() +
//                        ") had been picked up from " + previousDriver.getName() + " " + previousDriver.getSurname() + " (" +
//                        previousDriver.getId() + ") by " + currentDriver.getName() + " " + currentDriver.getSurname() + " (" + currentDriver.getId() + ") on " +
//                        carSwap.getStartDate().toString()
//                        );

//                panel.add(textLabel);

                GridBagConstraints jLabelConstraints = new GridBagConstraints();
                jLabelConstraints.gridx = 0;
                jLabelConstraints.gridy = 0;
                jLabelConstraints.gridwidth = 1;
                jLabelConstraints.anchor = GridBagConstraints.LINE_END;
                jLabelConstraints.insets = new Insets(5, 25, 5, 5);

                GridBagConstraints jTextFieldConstraints = new GridBagConstraints();
                jTextFieldConstraints.gridx = 1;
                jTextFieldConstraints.gridy = 0;
                jTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
                jTextFieldConstraints.gridwidth = 2;
                jTextFieldConstraints.anchor = GridBagConstraints.LINE_START;
                jTextFieldConstraints.insets = new Insets(5, 0, 5, 25);

                Map<JLabel, JTextField> fields = JTextFieldFactory.fieldAndLabel(false, 20,
                        "Car Swap ID", "Beginning Date", "End Date", "Car", "Driver", "Previous Driver");

                final Car lambdaCar = car;
                final Driver lambdaCurrentDriver = currentDriver;
                final Driver lambdaPreviousDriver = previousDriver;
                final CarSwap lambdaCarSwap = carSwap;
                fields.forEach((key, value) -> {

                    panel.add(key, jLabelConstraints);
                    jLabelConstraints.gridy++;
                    panel.add(value, jTextFieldConstraints);
                    jTextFieldConstraints.gridy++;

                    String label = key.getText();
                    switch (label) {
                        case "Car Swap ID": value.setText(lambdaCarSwap.getId() + ""); break;
                        case "Beginning Date": value.setText(lambdaCarSwap.getStartDate() + ""); break;
                        case "End Date": value.setText(lambdaCarSwap.getEndDate() + ""); break;
                        case "Car": value.setText(lambdaCar.getCarManufacturer() + " " + lambdaCar.getModel() + " (" + lambdaCar.getNumberPlate() + ")");break;
                        case "Driver": value.setText(lambdaCurrentDriver.getName() + " " + lambdaCurrentDriver.getSurname() + " (" + lambdaCurrentDriver.getId() + ")"); break;
                        case "Previous Driver": value.setText(lambdaPreviousDriver.getName() + " " + lambdaPreviousDriver.getSurname() + " (" + lambdaPreviousDriver.getId() +")");break;
                    }
                });

                detailsFrame.pack();
                detailsFrame.setMinimumSize(detailsFrame.getSize());
                detailsFrame.setVisible(true);
            }
        });

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
                realTableModel.setDataVector(tempTableModel.getDataVector(), new Vector<>(carSwapController.getColumns()));
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

            String[] driversList = new String[0];
            try {
                ArrayList<Driver> drivers = DriverGui.getInstance().getDrivers();
                driversList = new String[drivers.size()];

                for (int i = 0; i < drivers.size(); i++) {
                    Driver driver = drivers.get(i);
                    driversList[i] = driver.getId() + ". " + driver.getName() + " " + driver.getSurname();
                }
            } catch (SQLException e) {
                JOptionPane.showConfirmDialog(this, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION);
            }

            String[] carsList = new String[0];
            try {
                ArrayList<Car> cars = CarGui.getInstance().getCars();
                carsList = new String[cars.size()];

                for (int i = 0; i < cars.size(); i++) {
                    Car car = cars.get(i);
                    carsList[i] = car.getId() + ". " + car.getCarManufacturer() + " " + car.getModel() + " (" + car.getNumberPlate() + ")";
                }
            } catch (SQLException e) {
                JOptionPane.showConfirmDialog(this, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION);
            }

            message.add("Start Date");
            JDatePicker startDate = JDatePickerFactory.createDatePicker();
            message.add(startDate);

            message.add("End Date");
            JDatePicker endDate = JDatePickerFactory.createDatePicker();
            message.add(endDate);

            message.add("Car");
            JComboBox<String> carComboBox = new JComboBox<>(carsList);
            message.add(carComboBox);

            message.add("Previous Driver");
            JComboBox<String> previousDriverComboBox = new JComboBox<>(driversList);
            message.add(previousDriverComboBox);

            message.add("Current Driver");
            JComboBox<String> currentDriverComboBox = new JComboBox<>(driversList);
            message.add(currentDriverComboBox);

            SqlDateModel startDateModel = (SqlDateModel) startDate.getModel();
            SqlDateModel endDateModel = (SqlDateModel) endDate.getModel();

            if (currentDriverComboBox.getSelectedItem() != null && previousDriverComboBox.getSelectedItem() != null && carComboBox.getSelectedItem() != null && JOptionPane.showConfirmDialog(this, message.toArray(), "Add CarSwap", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                CarSwap carSwap = null;
                try {
                    carSwap = carSwapController.create(
                            startDateModel.getValue(),
                            endDateModel.getValue(),
                            Integer.parseInt(((String)carComboBox.getSelectedItem()).split(Pattern.quote("."))[0]),
                            Integer.parseInt(previousDriverComboBox.getSelectedItem().toString().split(Pattern.quote("."))[0]),
                            Integer.parseInt(currentDriverComboBox.getSelectedItem().toString().split(Pattern.quote("."))[0])
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (carSwap != null) {
                    DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
                    tableModel.addRow(carSwap.toArray());
                }
            }
        });

        updateButton.addActionListener((event -> {

            int selectedRow = jTable.getSelectedRow();
            if(selectedRow != -1) {
                ArrayList<Object> message = new ArrayList<>();

                String[] driversList = new String[0];
                try {
                    ArrayList<Driver> drivers = DriverGui.getInstance().getDrivers();
                    driversList = new String[drivers.size()];

                    for (int i = 0; i < drivers.size(); i++) {
                        Driver driver = drivers.get(i);
                        driversList[i] = driver.getId() + ". " + driver.getName() + " " + driver.getSurname();
                    }
                } catch (SQLException e) {
                    JOptionPane.showConfirmDialog(this, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION);
                }

                String[] carsList = new String[0];
                try {
                    ArrayList<Car> cars = CarGui.getInstance().getCars();
                    carsList = new String[cars.size()];

                    for (int i = 0; i < cars.size(); i++) {
                        Car car = cars.get(i);
                        carsList[i] = car.getId() + ". " + car.getCarManufacturer() + " " + car.getModel() + " (" + car.getNumberPlate() + ")";
                    }
                } catch (SQLException e) {
                    JOptionPane.showConfirmDialog(this, e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION);
                }

                message.add("Start Date");
                JDatePicker startDate = JDatePickerFactory.createDatePicker(jTable.getValueAt(selectedRow, columns.indexOf("startDate")).toString());
                message.add(startDate);

                message.add("End Date");
                JDatePicker endDate = JDatePickerFactory.createDatePicker(jTable.getValueAt(selectedRow, columns.indexOf("endDate")).toString());
                message.add(endDate);

                message.add("Car");
                JComboBox<String> carComboBox = new JComboBox<>(carsList);
                message.add(carComboBox);
                String carId = jTable.getValueAt(selectedRow, columns.indexOf("carId")).toString();
                for (String car : carsList) {
                    if (car.split(Pattern.quote("."))[0].equals(carId)) {
                        carComboBox.setSelectedItem(car);
                        break;
                    }
                }

                message.add("Previous Driver");
                JComboBox<String> previousDriverComboBox = new JComboBox<>(driversList);
                message.add(previousDriverComboBox);
                String previousDriverId = jTable.getValueAt(selectedRow, columns.indexOf("previousDriverId")).toString();
                for (String driver : driversList) {
                    if (driver.split(Pattern.quote("."))[0].equals(previousDriverId)) {
                        previousDriverComboBox.setSelectedItem(driver);
                        break;
                    }
                }

                message.add("Current Driver");
                JComboBox<String> currentDriverComboBox = new JComboBox<>(driversList);
                message.add(currentDriverComboBox);
                String currentDriverId = jTable.getValueAt(selectedRow, columns.indexOf("currentDriverId")).toString();
                for (String driver : driversList) {
                    if (driver.split(Pattern.quote("."))[0].equals(currentDriverId)) {
                        currentDriverComboBox.setSelectedItem(driver);
                        break;
                    }
                }

                SqlDateModel startDateModel = (SqlDateModel) startDate.getModel();
                SqlDateModel endDateModel = (SqlDateModel) endDate.getModel();

                if (currentDriverComboBox.getSelectedItem() != null && previousDriverComboBox.getSelectedItem() != null && carComboBox.getSelectedItem() != null && JOptionPane.showConfirmDialog(this, message.toArray(), "Update CarSwap", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                    CarSwap carSwap = null;
                    int idIndex = jTable.getColumn("id").getModelIndex();
                    try {
                        carSwap = carSwapController.update(
                                Integer.parseInt(jTable.getModel().getValueAt(
                                        selectedRow, idIndex
                                ).toString()),
                                startDateModel.getValue(),
                                endDateModel.getValue(),
                                Integer.parseInt(((String) carComboBox.getSelectedItem()).split(Pattern.quote("."))[0]),
                                Integer.parseInt(previousDriverComboBox.getSelectedItem().toString().split(Pattern.quote("."))[0]),
                                Integer.parseInt(currentDriverComboBox.getSelectedItem().toString().split(Pattern.quote("."))[0])
                        );
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (carSwap != null) {
                        jTable.setValueAt(carSwap.getId(), selectedRow, idIndex);
                        jTable.setValueAt(carSwap.getStartDate(), selectedRow, jTable.getColumn("startDate").getModelIndex());
                        jTable.setValueAt(carSwap.getEndDate(), selectedRow, jTable.getColumn("endDate").getModelIndex());
                        jTable.setValueAt(carSwap.getCarId(), selectedRow, jTable.getColumn("carId").getModelIndex());
                        jTable.setValueAt(carSwap.getPreviousDriverId(), selectedRow, jTable.getColumn("previousDriverId").getModelIndex());
                        jTable.setValueAt(carSwap.getCurrentDriverId(), selectedRow, jTable.getColumn("currentDriverId").getModelIndex());
                    }
                }
            }
        }));

        deleteButton.addActionListener((event) -> {
            int selectedRow = jTable.getSelectedRow();
            if (selectedRow != -1) {
                if ((JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this carSwap?", "Delete CarSwap", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) == 0) {
                    int idColumnIndex = jTable.getColumn("id").getModelIndex();

                    CarSwap carSwap = null;

                    try {
                        carSwap = carSwapController.delete(Integer.parseInt(jTable.getValueAt(selectedRow, idColumnIndex).toString()));
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (carSwap != null) {
                        JOptionPane.showConfirmDialog(this, "The CarSwap has been deleted!", "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                        model.removeRow(selectedRow);
                    }
                }
            }
        });
    }

    private static JTable drawTable() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<CarSwap> carSwaps = new ArrayList<>();

        try {
            columns = carSwapController.getColumns();
            carSwaps = carSwapController.readAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        JTable table = new JTable(new Vector<>(), new Vector<Object>(columns)) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (CarSwap carSwap : carSwaps) {
            model.addRow(carSwap.toArray());
        }

        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return table;
    }
}