import javax.swing.JOptionPane;
import java.awt.*;

public class MsgBox{
    private static Component parentWindow = Main.mainForm;

    public static void info(String msg, String title)
    {
        JOptionPane.showMessageDialog(parentWindow, msg, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void question(String msg, String title)
    {
        JOptionPane.showMessageDialog(parentWindow, msg, title, JOptionPane.QUESTION_MESSAGE);
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