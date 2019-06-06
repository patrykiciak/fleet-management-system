package other;

import javax.swing.*;
import java.util.Calendar;

public abstract class JSpinnerFactory {
    public static JSpinner yearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        SpinnerModel spinnerModel = new SpinnerNumberModel(currentYear, currentYear - 100,currentYear + 1,1);
        JSpinner spinner = new JSpinner(spinnerModel);

        spinner.setEditor(new JSpinner.NumberEditor(spinner,"#"));

        JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        jFormattedTextField.setEditable(false);

        return spinner;
    }

    public static JSpinner yearSpinner(String dateString) {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

        int year = Integer.parseInt(dateString.split("-")[0]);

        SpinnerModel spinnerModel = new SpinnerNumberModel(year, currentYear - 100,currentYear + 1,1);
        JSpinner spinner = new JSpinner(spinnerModel);

        spinner.setEditor(new JSpinner.NumberEditor(spinner,"#"));

        JFormattedTextField jFormattedTextField = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        jFormattedTextField.setEditable(false);

        return spinner;
    }
}
