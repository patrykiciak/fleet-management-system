package gui;

import controller.DriverController;
import controller.EarningController;
import model.Driver;
import model.Earning;
import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.SqlDateModel;
import other.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

class EarningGui extends JPanel {
    private static EarningGui earningGui;
    private static final EarningController earningController
            = EarningController.getInstance();

    static synchronized EarningGui getInstance() {
        if (earningGui == null) {
            earningGui = new EarningGui();
        }
        return earningGui;
    }

    private EarningGui() { // ...
        setLayout(new BorderLayout());
        final JTable jTable = drawTable();
        final ArrayList<String> columns = new ArrayList<>();
        try {
            columns.addAll(earningController.getColumns());
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

        JButton calculateWagesButton = new JButton("Calculate wages");
        JButton seeTransfers = new JButton("See transfers");

        rightButtonsContainer.add(addButton, gbc);
        rightButtonsContainer.add(updateButton, gbc);
        rightButtonsContainer.add(deleteButton, gbc);
        rightButtonsContainer.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        rightButtonsContainer.add(filtersButton, gbc);
        rightButtonsContainer.add(clearFiltersButton, gbc);
        rightButtonsContainer.add(Box.createRigidArea(new Dimension(0, 10)), gbc);
        rightButtonsContainer.add(calculateWagesButton, gbc);
        rightButtonsContainer.add(seeTransfers, gbc);

        clearFiltersButton.addActionListener((clearEvent) -> {
            filterTextFields.forEach((filterTextField) -> filterTextField.setText(""));
            subFilters.removeIf((element) -> true);
            tableRowSorter.sort();
        });

        rightPanel.add(rightButtonsContainer);
        this.add(rightPanel, BorderLayout.EAST);

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
                realTableModel.setDataVector(tempTableModel.getDataVector(), new Vector<>(earningController.getColumns()));
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

            message.add("Driver");
            JComboBox<String> driver = new JComboBox<>(driversList);
            message.add(driver);

            message.add(Box.createRigidArea(new Dimension(0, 10)));
            message.add("Start Date");
            JDatePicker startDate = JDatePickerFactory.createDatePicker();
            message.add(startDate);

            message.add(Box.createRigidArea(new Dimension(0, 10)));
            message.add("End Date");
            JDatePicker endDate = JDatePickerFactory.createDatePicker();
            message.add(endDate);

            message.add(Box.createRigidArea(new Dimension(0, 10)));
            message.add("Turnover");
            JTextField turnover = new JTextField();

            message.add(turnover);

            String[] radioButtonsLabels = {"Paid", "Not Paid"};
            Object[] isPaidRadioButtons = JRadioButtonFactory.createJRadioButtonsGrouped(radioButtonsLabels);
            ((JRadioButton)isPaidRadioButtons[1]).setSelected(true);

            JPanel radioButtonsPanel = new JPanel(new FlowLayout());
            for (int i = 0; i < isPaidRadioButtons.length-1; i++) {
                radioButtonsPanel.add((JRadioButton)isPaidRadioButtons[i]);
            }
            message.add(radioButtonsPanel);

            SqlDateModel startDateModel = (SqlDateModel) startDate.getModel();
            SqlDateModel endDateModel = (SqlDateModel) endDate.getModel();

            if (driver.getSelectedItem() != null && JOptionPane.showConfirmDialog(this, message.toArray(),
                    "Add Earning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                Earning earning = null;
                try {
                    boolean isPaid = ((JRadioButton)isPaidRadioButtons[0]).isSelected();
                    earning = earningController.create(
                            Integer.parseInt(driver.getSelectedItem().toString().split(Pattern.quote("."))[0]),
                            startDateModel.getValue(),
                            endDateModel.getValue(),
                            new BigDecimal(turnover.getText()),
                            isPaid
                    );
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Incorrect number!", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (earning != null) {
                    DefaultTableModel tableModel = (DefaultTableModel) jTable.getModel();
                    tableModel.addRow(earning.toArray());
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

                message.add("Driver");
                JComboBox<String> driversComboBox = new JComboBox<>(driversList);
                message.add(driversComboBox);
                String id = jTable.getValueAt(selectedRow, columns.indexOf("driverId")).toString();
                for (String driver : driversList) {
                    if (driver.split(Pattern.quote("."))[0].equals(id)) {
                        driversComboBox.setSelectedItem(driver);
                        break;
                    }
                }

                message.add(Box.createRigidArea(new Dimension(0, 10)));
                message.add("Start Date");
                JDatePicker startDate = JDatePickerFactory.createDatePicker(jTable.getValueAt(selectedRow, columns.indexOf("startDate")).toString());
                message.add(startDate);

                message.add(Box.createRigidArea(new Dimension(0, 10)));
                message.add("End Date");
                JDatePicker endDate = JDatePickerFactory.createDatePicker(jTable.getValueAt(selectedRow, columns.indexOf("endDate")).toString());
                message.add(endDate);

                message.add(Box.createRigidArea(new Dimension(0, 10)));
                message.add("Turnover");
                JTextField turnover = new JTextField(jTable.getValueAt(selectedRow, columns.indexOf("turnover")).toString());

                message.add(turnover);

                String[] radioButtonsLabels = {"Paid", "Not Paid"};
                Object[] isPaidRadioButtons = JRadioButtonFactory.createJRadioButtonsGrouped(radioButtonsLabels);

                if (jTable.getValueAt(selectedRow, columns.indexOf("isPaid")).toString().equals("true"))
                    ((JRadioButton) isPaidRadioButtons[0]).setSelected(true);
                else if (jTable.getValueAt(selectedRow, columns.indexOf("isPaid")).toString().equals("false"))
                    ((JRadioButton) isPaidRadioButtons[1]).setSelected(true);

                JPanel radioButtonsPanel = new JPanel(new FlowLayout());
                for (int i = 0; i < isPaidRadioButtons.length - 1; i++) {
                    radioButtonsPanel.add((JRadioButton) isPaidRadioButtons[i]);
                }
                message.add(radioButtonsPanel);

                SqlDateModel startDateModel = (SqlDateModel) startDate.getModel();
                SqlDateModel endDateModel = (SqlDateModel) endDate.getModel();

                if (driversComboBox.getSelectedItem() != null && JOptionPane.showConfirmDialog(this, message.toArray(), "Update Earning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == 0) {
                    Earning earning = null;
                    int idIndex = jTable.getColumn("id").getModelIndex();
                    try {
                        boolean isPaid = ((JRadioButton) isPaidRadioButtons[0]).isSelected();
                        earning = earningController.update(
                                Integer.parseInt(jTable.getModel().getValueAt(
                                        selectedRow, idIndex
                                ).toString()),
                                Integer.parseInt(driversComboBox.getSelectedItem().toString().split(Pattern.quote("."))[0]),
                                startDateModel.getValue(),
                                endDateModel.getValue(),
                                new BigDecimal(turnover.getText()),
                                isPaid
                        );
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Incorrect number!", "Error", JOptionPane.ERROR_MESSAGE);
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (earning != null) {
                        jTable.setValueAt(earning.getId(), selectedRow, idIndex);
                        jTable.setValueAt(earning.getDriverId(), selectedRow, jTable.getColumn("driverId").getModelIndex());
                        jTable.setValueAt(earning.getStartDate(), selectedRow, jTable.getColumn("startDate").getModelIndex());
                        jTable.setValueAt(earning.getEndDate(), selectedRow, jTable.getColumn("endDate").getModelIndex());
                        jTable.setValueAt(earning.getTurnover(), selectedRow, jTable.getColumn("turnover").getModelIndex());
                        jTable.setValueAt(earning.isPaid(), selectedRow, jTable.getColumn("isPaid").getModelIndex());
                    }
                }
            }
        }));

        deleteButton.addActionListener((event) -> {
            int selectedRow = jTable.getSelectedRow();
            if (selectedRow != -1) {
                if ((JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this earning?", "Delete Earning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) == 0) {
                    int idColumnIndex = jTable.getColumn("id").getModelIndex();

                    Earning earning = null;

                    try {
                        earning = earningController.delete(Integer.parseInt(jTable.getValueAt(selectedRow, idColumnIndex).toString()));
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }

                    if (earning != null) {
                        JOptionPane.showConfirmDialog(this, "The Earning has been deleted!", "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
                        model.removeRow(selectedRow);
                    }
                }
            }
        });

        calculateWagesButton.addActionListener((event) -> {
            int selectedRow = jTable.getSelectedRow();
            if(selectedRow != -1) {
                int driverId = Integer.parseInt(jTable.getValueAt(selectedRow, jTable.getColumn("driverId").getModelIndex()).toString());
                Driver currentDriver = null;
                try {
                    currentDriver = DriverController.getInstance().readOne(driverId);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (currentDriver != null) {
                    JFrame wagesFrame = new JFrame("Wage Calculation");
                    wagesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    Container wagesFrameContentPane = wagesFrame.getContentPane();
                    wagesFrameContentPane.setLayout(new BorderLayout());

                    JPanel wagesFrameWorkArea = new JPanel();
                    wagesFrameContentPane.add(wagesFrameWorkArea, BorderLayout.CENTER);
                    wagesFrameWorkArea.setLayout(new GridBagLayout());

                    JPanel leftColumn = new JPanel(new GridBagLayout());
                    JPanel rightColumn = new JPanel(new GridBagLayout());

                    GridBagConstraints wagesFrameWorkAreaConstraints = new GridBagConstraints();
                    wagesFrameWorkAreaConstraints.gridy = 0;
                    wagesFrameWorkAreaConstraints.gridwidth = 1;
                    wagesFrameWorkAreaConstraints.gridheight = 1;
                    wagesFrameWorkAreaConstraints.anchor = GridBagConstraints.PAGE_START;

                    wagesFrameWorkAreaConstraints.gridx = 0;
                    wagesFrameWorkArea.add(leftColumn, wagesFrameWorkAreaConstraints);

                    wagesFrameWorkAreaConstraints.gridx = 1;
                    wagesFrameWorkArea.add(rightColumn, wagesFrameWorkAreaConstraints);

                    GridBagConstraints jTextFieldConstraints = new GridBagConstraints();
                    jTextFieldConstraints.fill = GridBagConstraints.HORIZONTAL;
                    jTextFieldConstraints.gridwidth = 2;
                    jTextFieldConstraints.anchor = GridBagConstraints.LINE_START;
                    jTextFieldConstraints.insets = new Insets(5, 0, 5, 25);

                    GridBagConstraints jLabelConstraints = new GridBagConstraints();
                    jLabelConstraints.gridwidth = 1;
                    jLabelConstraints.anchor = GridBagConstraints.LINE_END;
                    jLabelConstraints.insets = new Insets(5, 25, 5, 5);
                    int fieldWidth = 15;

                    // HEADER

                    String fullName = currentDriver.getName() + " " + currentDriver.getSurname() + " (" + currentDriver.getId() + ")";
                    String dates = "from " + jTable.getValueAt(selectedRow, jTable.getColumn("startDate").getModelIndex()).toString() + " to " + jTable.getValueAt(selectedRow, jTable.getColumn("endDate").getModelIndex()).toString();
                    JLabel header = new JLabel(fullName + " " + dates);
                    header.setFont(header.getFont().deriveFont(18f));
                    header.setBorder(new EmptyBorder(15, 10, 15, 10));
                    header.setHorizontalAlignment(SwingConstants.CENTER);
                    wagesFrameContentPane.add(header, BorderLayout.NORTH);


                    //LEFT COLUMN

                    jLabelConstraints.gridx = 0;
                    jLabelConstraints.gridy = 0;
                    jTextFieldConstraints.gridx = 1;
                    jTextFieldConstraints.gridy = 0;

                    Map<JLabel, JTextField> generals = JTextFieldFactory.fieldAndLabel(false, fieldWidth, "Turnover", "Gross Wage", "Net Wage", "Total Cost");
                    generals.forEach((key, value) -> {
                        leftColumn.add(key, jLabelConstraints);
                        leftColumn.add(value, jTextFieldConstraints);
                        jLabelConstraints.gridy++;
                        jTextFieldConstraints.gridy++;
                    });
                    leftColumn.add(Box.createRigidArea(new Dimension(0, 0)), jLabelConstraints);
                    leftColumn.add(Box.createRigidArea(new Dimension(0, 0)), jTextFieldConstraints);
                    jLabelConstraints.gridy++;
                    jTextFieldConstraints.gridy++;

                    HashMap<JLabel, JTextField> healthCare = JTextFieldFactory.fieldAndLabel(false, fieldWidth, "Tax Allowance", "Cost of Earning", "Healthcare Base", "Deducted", "Collected", "Tax Advance");
                    healthCare.forEach((key, value) -> {
                        leftColumn.add(key, jLabelConstraints);
                        leftColumn.add(value, jTextFieldConstraints);
                        jLabelConstraints.gridy++;
                        jTextFieldConstraints.gridy++;
                    });
                    leftColumn.add(Box.createRigidArea(new Dimension(0, 0)), jLabelConstraints);
                    leftColumn.add(Box.createRigidArea(new Dimension(0, 0)), jTextFieldConstraints);
                    jLabelConstraints.gridy++;
                    jTextFieldConstraints.gridy++;

                    //RIGHT COLUMN

                    jLabelConstraints.gridx = 0;
                    jLabelConstraints.gridy = 0;
                    jTextFieldConstraints.gridx = 1;
                    jTextFieldConstraints.gridy = 0;

                    Map<JLabel, JTextField> employeeInsurance = JTextFieldFactory.fieldAndLabel(false, fieldWidth, "Employee Total", "Employee Pension 9,76%", "Employee Disability 1,5%", "Employee Sickness 2,45%");
                    employeeInsurance.forEach((key, value) -> {
                        rightColumn.add(key, jLabelConstraints);
                        rightColumn.add(value, jTextFieldConstraints);
                        jLabelConstraints.gridy++;
                        jTextFieldConstraints.gridy++;
                    });
                    rightColumn.add(Box.createRigidArea(new Dimension(0, 0)), jLabelConstraints);
                    rightColumn.add(Box.createRigidArea(new Dimension(0, 0)), jTextFieldConstraints);
                    jLabelConstraints.gridy++;
                    jTextFieldConstraints.gridy++;

                    HashMap<JLabel, JTextField> employerInsurance = JTextFieldFactory.fieldAndLabel(false, fieldWidth, "Employer Total", "Employer Pension 9,76%", "Employer Disability 6,5%", "Employer Accident 1,67%", "FP 2,45%", "FGSP 0,1%");
                    employerInsurance.forEach((key, value) -> {
                        rightColumn.add(key, jLabelConstraints);
                        rightColumn.add(value, jTextFieldConstraints);
                        jLabelConstraints.gridy++;
                        jTextFieldConstraints.gridy++;
                    });
                    rightColumn.add(Box.createRigidArea(new Dimension(0, 0)), jLabelConstraints);
                    rightColumn.add(Box.createRigidArea(new Dimension(0, 0)), jTextFieldConstraints);
                    jLabelConstraints.gridy++;
                    jTextFieldConstraints.gridy++;


                    // POPULATE
                    LinkedHashMap<String, JTextField> fields = new LinkedHashMap<>();

                    generals.forEach((jLabel, jTextField) -> fields.put(jLabel.getText(), jTextField));
                    employeeInsurance.forEach((jLabel, jTextField) -> fields.put(jLabel.getText(), jTextField));
                    employerInsurance.forEach((jLabel, jTextField) -> fields.put(jLabel.getText(), jTextField));
                    healthCare.forEach((jLabel, jTextField) -> fields.put(jLabel.getText(), jTextField));

                    BigDecimal turnover = new BigDecimal(jTable.getValueAt(selectedRow, jTable.getColumn("turnover").getModelIndex()).toString()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal grossWage = turnover.multiply(new BigDecimal("0.6")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal employeePension, employeeDisability, employeeSickness;

                    if (currentDriver.getTaxationType() == TaxationType.STUDENT) {
                        employeePension = new BigDecimal("0");
                        employeeDisability = new BigDecimal("0");
                        employeeSickness = new BigDecimal("0");
                    } else if (currentDriver.getTaxationType() == TaxationType.WORKING_SOMEWHERE_ELSE) {
                        employeePension = new BigDecimal("0");
                        employeeDisability = new BigDecimal("0");
                        employeeSickness = new BigDecimal("0");
                    } else {
                        employeePension = grossWage.multiply(new BigDecimal("0.0976")).setScale(2, RoundingMode.HALF_UP);
                        employeeDisability = grossWage.multiply(new BigDecimal("0.015")).setScale(2, RoundingMode.HALF_UP);
                        employeeSickness = new BigDecimal("0.00");
                    }

                    BigDecimal employeeTotal = employeePension.add(employeeDisability.add(employeeSickness)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxAllowance = new BigDecimal("0.00");
                    BigDecimal healthcareBase;
                    if (currentDriver.getTaxationType() == TaxationType.STUDENT) {
                        healthcareBase = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    } else if (currentDriver.getTaxationType() == TaxationType.WORKING_SOMEWHERE_ELSE) {
                        healthcareBase = grossWage.subtract(employeeTotal).setScale(2, RoundingMode.HALF_UP);
                    } else {
                        healthcareBase = grossWage.subtract(employeeTotal).setScale(2, RoundingMode.HALF_UP);
                    }

                    BigDecimal collected = healthcareBase.multiply(new BigDecimal("0.09")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal deducted = healthcareBase.multiply(new BigDecimal("0.0775")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal costOfEarning = healthcareBase.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxBase = grossWage.subtract(costOfEarning).subtract(employeeTotal).setScale(0, RoundingMode.HALF_UP);

                    BigDecimal fp, fgsp, employerPension, employerDisability, employerAccident;
                    if (currentDriver.getTaxationType() == TaxationType.STUDENT) {
                        employerPension = new BigDecimal("0");
                        employerDisability = new BigDecimal("0");
                        employerAccident = new BigDecimal("0");
                        fp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                        fgsp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    } else if (currentDriver.getTaxationType() == TaxationType.WORKING_SOMEWHERE_ELSE) {
                        employerPension = new BigDecimal("0");
                        employerDisability = new BigDecimal("0");
                        employerAccident = new BigDecimal("0");
                        fp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                        fgsp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    } else {
                        employerPension = grossWage.multiply(new BigDecimal("0.0976")).setScale(2, RoundingMode.HALF_UP);
                        employerDisability = grossWage.multiply(new BigDecimal("0.065")).setScale(2, RoundingMode.HALF_UP);
                        employerAccident = grossWage.multiply(new BigDecimal("0.0167")).setScale(2, RoundingMode.HALF_UP);
                        fp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                        fgsp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    }

                    BigDecimal employerTotal = employerPension.add(employerDisability.add(employerAccident.add(fp.add(fgsp)))).setScale(2, RoundingMode.HALF_UP);

                    BigDecimal taxAdvance = taxBase.multiply(new BigDecimal("0.18")).subtract(deducted).setScale(0, RoundingMode.HALF_UP);
                    BigDecimal netWage = grossWage.subtract(employeeTotal).subtract(collected).subtract(taxAdvance);
                    BigDecimal totalCost = grossWage.add(employerTotal);

                    fields.get("Turnover").setText(turnover.toString());
                    fields.get("Gross Wage").setText(grossWage.toString());
                    fields.get("Employee Pension 9,76%").setText(employeePension.toString());
                    fields.get("Employee Disability 1,5%").setText(employeeDisability.toString());
                    fields.get("Employee Sickness 2,45%").setText(employeeSickness.toString());
                    fields.get("Employee Total").setText(employeeTotal.toString());
                    fields.get("Healthcare Base").setText(healthcareBase.toString());
                    fields.get("Collected").setText(collected.toString());
                    fields.get("Deducted").setText(deducted.toString());
                    fields.get("Cost of Earning").setText(costOfEarning.toString());
                    fields.get("Tax Advance").setText(taxAdvance.toString());
                    fields.get("Employer Pension 9,76%").setText(employerPension.toString());
                    fields.get("Employer Disability 6,5%").setText(employerDisability.toString());
                    fields.get("Employer Accident 1,67%").setText(employerAccident.toString());
                    fields.get("Employer Total").setText(employerTotal.toString());
                    fields.get("FP 2,45%").setText(fp.toString());
                    fields.get("FGSP 0,1%").setText(fgsp.toString());
                    fields.get("Employer Total").setText(employerTotal.toString());
                    fields.get("Net Wage").setText(netWage.toString());
                    fields.get("Total Cost").setText(totalCost.toString());
                    fields.get("Tax Allowance").setText(taxAllowance.toString());


                    JPanel southPanel = new JPanel(new FlowLayout());
                    wagesFrameContentPane.add(southPanel, BorderLayout.SOUTH);

                    JButton markAsPaidButton = new JButton("Mark as paid");
                    JButton closeButton = new JButton("Close");

                    closeButton.setPreferredSize(markAsPaidButton.getPreferredSize());

                    markAsPaidButton.addActionListener((markEvent) -> {
                        try {
                            int id = Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("id")).toString());
                            Earning previousEarning = earningController.readOne(id);

                            if (earningController.update(id, previousEarning.getDriverId(), previousEarning.getStartDate(), previousEarning.getEndDate(), previousEarning.getTurnover(), true) != null) {
                                jTable.setValueAt(true, selectedRow, jTable.getColumn("isPaid").getModelIndex());
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    closeButton.addActionListener(closeEvent -> wagesFrame.dispose());

                    southPanel.add(markAsPaidButton);
                    southPanel.add(closeButton);
                    southPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

                    wagesFrame.pack();
                    wagesFrame.setMinimumSize(wagesFrame.getPreferredSize());
                    wagesFrame.setVisible(true);
                }
            }
        });

        seeTransfers.addActionListener((event) -> {
            int selectedRow = jTable.getSelectedRow();
            if(selectedRow != -1) {
                int driverId = Integer.parseInt(jTable.getValueAt(selectedRow, jTable.getColumn("driverId").getModelIndex()).toString());
                Driver currentDriver = null;
                try {
                    currentDriver = DriverController.getInstance().readOne(driverId);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

                if (currentDriver != null) {
                    JFrame wagesFrame = new JFrame("Wage Calculation");
                    wagesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                    Container wagesFrameContentPane = wagesFrame.getContentPane();
                    wagesFrameContentPane.setLayout(new BoxLayout(wagesFrameContentPane, BoxLayout.PAGE_AXIS));

                    // HEADER

                    String fullName = currentDriver.getName() + " " + currentDriver.getSurname() + " (" + currentDriver.getId() + ")";
                    String dates = "from " + jTable.getValueAt(selectedRow, jTable.getColumn("startDate").getModelIndex()).toString() + " to " + jTable.getValueAt(selectedRow, jTable.getColumn("endDate").getModelIndex()).toString();
                    JLabel header = new JLabel(fullName + " " + dates);
                    header.setFont(header.getFont().deriveFont(18f));
//                    header.setBorder(new EmptyBorder(15, 10, 15, 10));
//                    header.setHorizontalAlignment(SwingConstants.CENTER);
                    wagesFrameContentPane.add(header);

                    // CALCULATIONS
                    BigDecimal turnover = new BigDecimal(jTable.getValueAt(selectedRow, jTable.getColumn("turnover").getModelIndex()).toString()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal grossWage = turnover.multiply(new BigDecimal("0.6")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal employeePension, employeeDisability, employeeSickness;
                    if (currentDriver.getTaxationType() == TaxationType.STUDENT) {
                        employeePension = new BigDecimal("0");
                        employeeDisability = new BigDecimal("0");
                        employeeSickness = new BigDecimal("0");
                    } else if (currentDriver.getTaxationType() == TaxationType.WORKING_SOMEWHERE_ELSE) {
                        employeePension = new BigDecimal("0");
                        employeeDisability = new BigDecimal("0");
                        employeeSickness = new BigDecimal("0");
                    } else {
                        employeePension = grossWage.multiply(new BigDecimal("0.0976")).setScale(2, RoundingMode.HALF_UP);
                        employeeDisability = grossWage.multiply(new BigDecimal("0.015")).setScale(2, RoundingMode.HALF_UP);
                        employeeSickness = new BigDecimal("0.00");
                    }
                    BigDecimal employeeTotal = employeePension.add(employeeDisability.add(employeeSickness)).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal healthcareBase;
                    if (currentDriver.getTaxationType() == TaxationType.STUDENT) {
                        healthcareBase = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    } else if (currentDriver.getTaxationType() == TaxationType.WORKING_SOMEWHERE_ELSE) {
                        healthcareBase = grossWage.subtract(employeeTotal).setScale(2, RoundingMode.HALF_UP);
                    } else {
                        healthcareBase = grossWage.subtract(employeeTotal).setScale(2, RoundingMode.HALF_UP);
                    }
                    BigDecimal collected = healthcareBase.multiply(new BigDecimal("0.09")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal deducted = healthcareBase.multiply(new BigDecimal("0.0775")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal costOfEarning = healthcareBase.multiply(new BigDecimal("0.20")).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxBase = grossWage.subtract(costOfEarning).subtract(employeeTotal).setScale(0, RoundingMode.HALF_UP);
                    BigDecimal fp, fgsp, employerPension, employerDisability, employerAccident;
                    if (currentDriver.getTaxationType() == TaxationType.STUDENT) {
                        employerPension = new BigDecimal("0");
                        employerDisability = new BigDecimal("0");
                        employerAccident = new BigDecimal("0");
                        fp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                        fgsp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    } else if (currentDriver.getTaxationType() == TaxationType.WORKING_SOMEWHERE_ELSE) {
                        employerPension = new BigDecimal("0");
                        employerDisability = new BigDecimal("0");
                        employerAccident = new BigDecimal("0");
                        fp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                        fgsp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    } else {
                        employerPension = grossWage.multiply(new BigDecimal("0.0976")).setScale(2, RoundingMode.HALF_UP);
                        employerDisability = grossWage.multiply(new BigDecimal("0.065")).setScale(2, RoundingMode.HALF_UP);
                        employerAccident = grossWage.multiply(new BigDecimal("0.0167")).setScale(2, RoundingMode.HALF_UP);
                        fp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                        fgsp = new BigDecimal("0").setScale(2, RoundingMode.HALF_UP);
                    }
                    BigDecimal employerTotal = employerPension.add(employerDisability.add(employerAccident.add(fp.add(fgsp)))).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal taxAdvance = taxBase.multiply(new BigDecimal("0.18")).subtract(deducted).setScale(0, RoundingMode.HALF_UP);
                    BigDecimal netWage = grossWage.subtract(employeeTotal).subtract(collected).subtract(taxAdvance);

                    JPanel southPanel = new JPanel(new FlowLayout());

                    JButton markAsPaidButton = new JButton("Mark as paid");
                    JButton closeButton = new JButton("Close");

                    closeButton.setPreferredSize(markAsPaidButton.getPreferredSize());

                    markAsPaidButton.addActionListener((markEvent) -> {
                        try {
                            int id = Integer.parseInt(jTable.getValueAt(selectedRow, columns.indexOf("id")).toString());
                            Earning previousEarning = earningController.readOne(id);

                            if (earningController.update(id, previousEarning.getDriverId(), previousEarning.getStartDate(), previousEarning.getEndDate(), previousEarning.getTurnover(), true) != null) {
                                jTable.setValueAt(true, selectedRow, jTable.getColumn("isPaid").getModelIndex());
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });

                    closeButton.addActionListener(closeEvent -> wagesFrame.dispose());


                    JPanel workArea = new JPanel();
                    wagesFrameContentPane.add(workArea);


                    DefaultTableModel tableModel = new DefaultTableModel(new Object[]{
                            "Institution", "Amount", "Bank Account Number"
                    }, 0);

                    JTable transfers = new JTable(tableModel) {
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };

                    JScrollPane tableScrollPane = new JScrollPane(transfers);

                    tableModel.addRow(new Object[]{
                            "Social Security", employeeTotal.add(employerTotal.add(collected)).setScale(2, RoundingMode.HALF_UP).toString() + " PLN", getSocialSecurityBankAccountNumber()
                    });

                    tableModel.addRow(new Object[]{
                            "Tax Office", taxAdvance.setScale(2, RoundingMode.HALF_UP).toString() + " PLN", getTaxOfficeBankAccountNumber()
                    });

                    tableModel.addRow(new Object[]{
                            "Employee", netWage.setScale(2, RoundingMode.HALF_UP).toString() + " PLN", currentDriver.getBankNumber()
                    });

                    workArea.add(tableScrollPane);

                    wagesFrameContentPane.add(southPanel);
                    southPanel.add(markAsPaidButton);
                    southPanel.add(closeButton);
                    southPanel.setBorder(new EmptyBorder(10, 0, 10, 0));


                    wagesFrame.setMinimumSize(wagesFrame.getPreferredSize());
                    wagesFrame.pack();
                    wagesFrame.setVisible(true);
                }
            }
        });
    }

    private static String getSocialSecurityBankAccountNumber() {
        return "56 5678 5678 5678 5678 5678 5687";
    }

    private static String getTaxOfficeBankAccountNumber() {
        return "76 5437 2111 8786 7784 2345 7834";
    }

    private static JTable drawTable() {
        ArrayList<String> columns = new ArrayList<>();
        ArrayList<Earning> earnings = new ArrayList<>();

        try {
            columns = earningController.getColumns();
            earnings = earningController.readAll();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        JTable table = new JTable(new Vector<>(), new Vector<Object>(columns)) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableModel model = (DefaultTableModel) table.getModel();

        for (Earning earning : earnings) {
            model.addRow(earning.toArray());
        }

        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return table;
    }
}