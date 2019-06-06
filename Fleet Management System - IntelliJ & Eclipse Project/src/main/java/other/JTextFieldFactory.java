package other;

import javax.swing.*;
import java.util.LinkedHashMap;

public abstract class JTextFieldFactory {
    public static LinkedHashMap<JLabel, JTextField> fieldAndLabel(boolean isEditable, int textFieldWidth, String... labels) {
        LinkedHashMap<JLabel, JTextField> labelAndField = new LinkedHashMap<>();
        for (String label : labels) {
            JLabel jLabel = new JLabel(label + "", JLabel.TRAILING);
            JTextField jTextField = new JTextField(textFieldWidth);
            jTextField.setEditable(isEditable);
            jTextField.setHorizontalAlignment(JTextField.RIGHT);
            labelAndField.put(jLabel, jTextField);
        }
        return labelAndField;
    }
}