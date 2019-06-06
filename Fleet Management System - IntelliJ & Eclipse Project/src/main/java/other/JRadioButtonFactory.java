package other;

import javax.swing.*;

public abstract class JRadioButtonFactory {
    public static Object[] createJRadioButtonsGrouped(String[] options) {

        Object[] buttons = new Object[options.length+1];
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < options.length; i++) {
            JRadioButton jRadioButton = new JRadioButton(options[i]);
            group.add(jRadioButton);
            buttons[i] = jRadioButton;
        }

        buttons[buttons.length-1] = group;

        return buttons;
    }
}
