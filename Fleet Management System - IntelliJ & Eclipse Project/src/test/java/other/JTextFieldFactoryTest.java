package other;

import org.junit.Test;
public class JTextFieldFactoryTest {

    @Test(expected = Exception.class)
    public void negativeWidth() {
        JTextFieldFactory.fieldAndLabel(false, -1, "0");
    }

    @Test
    public void zeroWidth() {
        JTextFieldFactory.fieldAndLabel(false, 0, "0");
    }

    @Test
    public void noArgs() {
        JTextFieldFactory.fieldAndLabel(true, 0);
    }

    @Test(expected = Exception.class)
    public void notRealStringArg() {
        JTextFieldFactory.fieldAndLabel(true, 1, (String) new Object());
    }
}