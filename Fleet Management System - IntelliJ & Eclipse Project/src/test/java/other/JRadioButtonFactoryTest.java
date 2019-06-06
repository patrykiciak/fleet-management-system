package other;

import org.junit.Test;

import javax.swing.*;

import static org.junit.Assert.assertTrue;

public class JRadioButtonFactoryTest {

    @Test
    public void createJRadioButtonsGroupedWithNoElements() {
        JRadioButtonFactory.createJRadioButtonsGrouped(new String[]{});
    }

    @Test
    public void checkInstances() {
        checkInstances(new String[0]);
        checkInstances(new String[1]);
        checkInstances(new String[] {"A", "123321"});
    }

    private void checkInstances(String[] elements) {
        Object[] objects = JRadioButtonFactory.createJRadioButtonsGrouped(elements);
        for (int i = 0; i < objects.length; i++) {
            if(i != objects.length - 1) assertTrue(objects[i] instanceof JRadioButton);
            else assertTrue(objects[i] instanceof ButtonGroup);
        }
    }
}