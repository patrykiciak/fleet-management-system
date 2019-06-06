package other;

import org.junit.Assert;
import org.junit.Test;

import javax.swing.*;

public class JSpinnerFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void incorrectDate() {
        JSpinnerFactory.yearSpinner("dasdsa");
    }

    @Test(expected = IllegalArgumentException.class)
    public void incorrectDateFormat() {
        JSpinnerFactory.yearSpinner("12-10-98");
    }

    @Test
    public void trickyDate1() {
        JSpinner jSpinner = JSpinnerFactory.yearSpinner("2019-13-13");
        Assert.assertNotEquals("2019-13-13", jSpinner.getModel().getValue().toString());
    }

    @Test
    public void trickyDate2() {
        JSpinner jSpinner = JSpinnerFactory.yearSpinner("2019-02-30");
        Assert.assertNotEquals("2019-02-30", jSpinner.getModel().getValue().toString());
    }

    @Test
    public void correct() {
        JSpinnerFactory.yearSpinner("2019-01-01");
    }

    @Test
    public void yearSpinner1() {
    }
}