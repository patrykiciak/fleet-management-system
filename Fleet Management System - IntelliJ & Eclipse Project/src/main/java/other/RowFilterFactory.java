package other;

import javax.swing.*;
import java.util.ArrayList;

public abstract class RowFilterFactory {

    public static RowFilter<Object, Object> multipleRowFilter(ArrayList<RowFilter<Object, Object>> subFilters) {
        return new RowFilter<Object, Object>() {
            @Override
            public boolean include(Entry<?, ?> entry) {
                boolean shouldBeReturned = true;

                for (RowFilter<Object, Object> subFilter : subFilters) {
                    if(!subFilter.include(entry)) {
                        shouldBeReturned = false;
                        break;
                    }
                }
                return shouldBeReturned;
            }
        };
    }
}