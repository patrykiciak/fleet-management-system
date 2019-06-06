package other;

import net.sourceforge.jdatepicker.JDatePicker;
import org.junit.Assert;
import org.junit.Test;

public class JDatePickerFactoryTest {

    @Test(expected = Exception.class)
    public void createDatePickerWithWrongString() {
        JDatePickerFactory.createDatePicker("sfdsdfsdf");
    }

    @Test
    public void createDatePickerWithCorrectString() {
        JDatePickerFactory.createDatePicker("2019-01-01");
    }

    @Test
    public void createDatePickerWithTrickyString() {
        JDatePicker jDatePicker = JDatePickerFactory.createDatePicker("2019-02-30");
        Assert.assertNotEquals("2019-02-30", jDatePicker.getModel().getValue().toString());
    }
}