package other;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.util.ArrayList;

public class RowFilterFactoryTest {
    private ArrayList<RowFilter<Object, Object>> subFilters;

    @Before
    public void instantiate() {
        subFilters = new ArrayList<>();
    }

    @Test
    public void rowFilterFactory() {
        RowFilter<Object, Object> rowFilter = RowFilterFactory.multipleRowFilter(subFilters);
        Assert.assertNotEquals(rowFilter, null);
    }
}