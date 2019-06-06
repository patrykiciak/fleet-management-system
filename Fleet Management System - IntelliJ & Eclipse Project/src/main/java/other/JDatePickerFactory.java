package other;

import net.sourceforge.jdatepicker.JDatePicker;
import net.sourceforge.jdatepicker.impl.JDatePanelImpl;
import net.sourceforge.jdatepicker.impl.JDatePickerImpl;
import net.sourceforge.jdatepicker.impl.SqlDateModel;

import java.util.Calendar;

public abstract class JDatePickerFactory {
    public static JDatePicker createDatePicker() {

        Calendar calendar = Calendar.getInstance();

        SqlDateModel model = new SqlDateModel();
        model.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        model.setSelected(true);
        
        JDatePanelImpl datePanel = new JDatePanelImpl(model);

        return new JDatePickerImpl(datePanel);
    }

    public static JDatePicker createDatePicker(String dateString) {

        String[] date = dateString.split("-");


        SqlDateModel model = new SqlDateModel();
        model.setDate(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[2]));
        model.setSelected(true);

        JDatePanelImpl datePanel = new JDatePanelImpl(model);

        return new JDatePickerImpl(datePanel);
    }
}