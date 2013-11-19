import javax.swing.JOptionPane;
import java.awt.*;

public class MsgBox{
    private static Component parentWindow = Main.mainForm;

    public static void info(String msg, String title)
    {
        JOptionPane.showMessageDialog(parentWindow, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean question(String msg, String title)
    {
        int response = JOptionPane.showConfirmDialog(parentWindow, msg, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return response == JOptionPane.YES_OPTION;
    }

    public static int option(String msg, String title)
    {
        return JOptionPane.showConfirmDialog(parentWindow, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public static void warning(String msg, String title)
    {
        JOptionPane.showMessageDialog(parentWindow, msg, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void error(String msg, String title)
    {
        JOptionPane.showMessageDialog(parentWindow, msg, title, JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }

}