package forms;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * GameBoard - An on-screen keyboard for gamepads.
 * This is my very first full-blown Java project.
 * User: Shedo Surashu
 *       http://coffeecone.com/gameboard
 * Date: November 17, 2013
 * Time: 7:54 PM
 */

public class MainForm extends JFrame {
    private JPanel panel1;
    private JButton btnA1;
    private JButton btnA2;
    private JButton btnA3;
    private JButton btnA4;
    private JButton btnA5;
    private JButton btnA6;
    private JButton btnA7;
    private JButton btnA8;
    private JLabel status;
    private JButton btnA9;
    private JButton btnA10;
    private JButton btnA11;
    private JButton btnA12;
    private JButton btnB1;
    private JButton btnB2;
    private JButton btnB3;
    private JButton btnB4;
    private JButton btnB5;
    private JButton btnB6;
    private JButton btnB7;
    private JButton btnB8;
    private JButton btnB9;
    private JButton btnB10;
    private JButton btnB11;
    private JButton btnB12;
    private JButton btnC1;
    private JButton btnC2;
    private JButton btnC3;
    private JButton btnC4;
    private JButton btnC5;
    private JButton btnC6;
    private JButton btnC7;
    private JButton btnC8;
    private JButton btnC9;
    private JButton btnC10;
    private JButton btnC11;
    private JButton btnC12;
    private JButton btnD1;
    private JButton btnD2;
    private JButton btnD3;
    private JButton btnD4;
    private JButton btnD5;
    private JButton btnD6;
    private JButton btnD7;
    private JButton btnD8;
    private JButton btnD9;
    private JButton btnD10;
    private JButton btnD11;
    private JButton btnD12;
    private JButton btnShift;
    private JButton btnEnter;
    public JComboBox controllerList;
    public JButton configure;
    public JButton about;

    private int curShift = 0;
    private String[] curKeyMap = new String[] {
            "a","b","c","d","e","f","g","h","i","j","k","l",
            "m","n","o","p","q","r","s","t","u","v","w","x",
            "y","z","0","1","2","3","4","5","6","7","8","9",
            ",",".",":",";","?","!","@","^","_","-","/","\\"
    };
    private JButton[] btn = {
            btnA1, btnA2, btnA3, btnA4, btnA5, btnA6, btnA7, btnA8, btnA9, btnA10, btnA11, btnA12,
            btnB1, btnB2, btnB3, btnB4, btnB5, btnB6, btnB7, btnB8, btnB9, btnB10, btnB11, btnB12,
            btnC1, btnC2, btnC3, btnC4, btnC5, btnC6, btnC7, btnC8, btnC9, btnC10, btnC11, btnC12,
            btnD1, btnD2, btnD3, btnD4, btnD5, btnD6, btnD7, btnD8, btnD9, btnD10, btnD11, btnD12,
            btnShift, btnEnter
    };
    private int curBtn = 0;
    private int oldBtn = 0;
    private Robot typeBot;
    private boolean continueBackspace = false;
    private Timer backspaceTimer;
    private Timer arrowKeysTimer;
    private Timer mouseTimerX;
    private Timer mouseTimerY;
    public int mouseX = 0;
    public int mouseY = 0;


    public MainForm() {
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setFocusableWindowState(false);
        setLocationByPlatform(true);
        setFocusable(false);
        setAlwaysOnTop(true);
        pack();
        setVisible(true);

        try {
            typeBot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

    }

    public void populateButtons(int type) {
        String[] row;

        switch (type) {
            case 1:
                row = new String[] {
                        "A","B","C","D","E","F","G","H","I","J","K","L",
                        "M","N","O","P","Q","R","S","T","U","V","W","X",
                        "Y","Z","0","1","2","3","4","5","6","7","8","9",
                        ",",".",":",";","?","!","@","^","_","-","/","\\"
                };
                break;
            case 0:
            default:
                row = new String[] {
                        "a","b","c","d","e","f","g","h","i","j","k","l",
                        "m","n","o","p","q","r","s","t","u","v","w","x",
                        "y","z","0","1","2","3","4","5","6","7","8","9",
                        ",",".",":",";","?","!","@","^","_","-","/","\\"
                };
                break;
        }

        for (int i = 0; i < 48; i++) {
            btn[i].setText(row[i]);
        }

        curKeyMap = row;
        pack();
    }

    public void mouseSetX(final int x) {
        try {
            mouseTimerX.cancel();
            mouseTimerX.purge();
        } catch (Exception ignored) {
        }

        if (Math.abs(x) == x) {
            mouseTimerX = new Timer();
            mouseTimerX.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mouseX == 0) {
                        cancel();
                    } else {
                        typeBot.mouseMove((MouseInfo.getPointerInfo().getLocation().x + (Math.abs(x)-2)), MouseInfo.getPointerInfo().getLocation().y);
                    }
                }
            }, 25, 25);
        }  else {
            mouseTimerX = new Timer();
            mouseTimerX.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mouseX == 0) {
                        cancel();
                    } else {
                        typeBot.mouseMove((MouseInfo.getPointerInfo().getLocation().x - (Math.abs(x)-2)), MouseInfo.getPointerInfo().getLocation().y);
                    }
                }
            }, 25, 25);
        }
    }

    public void mouseSetY(final int y) {
        try {
            mouseTimerY.cancel();
            mouseTimerY.purge();
        } catch (Exception ignored) {
        }

        if (Math.abs(y) == y) {
            mouseTimerY = new Timer();
            mouseTimerY.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mouseY == 0) {
                        cancel();
                    } else {
                        typeBot.mouseMove((MouseInfo.getPointerInfo().getLocation().x), MouseInfo.getPointerInfo().getLocation().y + (Math.abs(y)-2));
                    }
                }
            }, 25, 25);
        } else {
            mouseTimerY = new Timer();
            mouseTimerY.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mouseY == 0) {
                        cancel();
                    } else {
                        typeBot.mouseMove((MouseInfo.getPointerInfo().getLocation().x), MouseInfo.getPointerInfo().getLocation().y - (Math.abs(y)-2));
                    }
                }
            }, 25, 25);
        }
    }

    public void restoreWindow(Boolean center) {
        Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowDim = getSize();
        Point windowLoc = new Point();
        Double x;
        Double y;

        if (center) {
            x = (screenDim.getWidth()-windowDim.getWidth())/2;
            y = (screenDim.getHeight()-windowDim.getHeight())/2;
            windowLoc.setLocation(x, y);
        } else {
            Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
            x = mouseLoc.getX();
            y = mouseLoc.getY();

            if ((mouseLoc.getX()+windowDim.getWidth()) >= screenDim.getWidth()) {
                x = (screenDim.getWidth()-windowDim.getWidth());
            }
            if ((mouseLoc.getY()+windowDim.getHeight()) >= screenDim.getHeight()) {
                y = (screenDim.getHeight()-windowDim.getHeight());
            }
            windowLoc.setLocation(x, y);
        }
        setLocation(windowLoc);
        setState(Frame.NORMAL);
    }

    public void minimizeWindow() {
        setState(Frame.ICONIFIED);
    }

    public void pressLeftMouseBtn() {
        typeBot.mousePress(KeyEvent.BUTTON1_MASK);
    }

    public void releaseLeftMouseBtn() {
        typeBot.mouseRelease(KeyEvent.BUTTON1_MASK);
    }

    public void pressRightMouseBtn() {
        typeBot.mousePress(KeyEvent.BUTTON3_MASK);
    }

    public void releaseRightMouseBtn() {
        typeBot.mouseRelease(KeyEvent.BUTTON3_MASK);
    }

    public void pressMiddleMouseBtn() {
        typeBot.mousePress(KeyEvent.BUTTON2_MASK);
    }

    public void releaseMiddleMouseBtn() {
        typeBot.mouseRelease(KeyEvent.BUTTON2_MASK);
    }

    public void pressEnter() {
        btnEnter.doClick(100);

        typeBot.keyPress(KeyEvent.VK_ENTER);
    }

    public void pressShift() {
        btnShift.doClick(100);

        if (curShift == 1) {
            populateButtons(0);
            curShift = 0;
        } else {
            populateButtons(1);
            curShift = 1;
        }
    }

    public void selectButton(String direction) {
        toFront();

        if (direction == "up" && curBtn >= 12 && curBtn < 48) {
            btn[curBtn].setBorderPainted(false);
            curBtn = curBtn-12;
            btn[curBtn].setBorderPainted(true);
        } else if (direction == "up" && curBtn > 47) {
            btn[curBtn].setBorderPainted(false);
            curBtn = oldBtn;
            btn[curBtn].setBorderPainted(true);
        } else if (direction == "down" && curBtn < 36) {
            btn[curBtn].setBorderPainted(false);
            curBtn = curBtn+12;
            btn[curBtn].setBorderPainted(true);
        } else if (direction == "down" && curBtn >= 36 && curBtn <= 38) { // Shift button
            btn[curBtn].setBorderPainted(false);
            oldBtn = curBtn;
            curBtn = 48;
            btn[curBtn].setBorderPainted(true);
        } else if (direction == "down" && curBtn > 38 && curBtn < 48) { // Done button
            btn[curBtn].setBorderPainted(false);
            oldBtn = curBtn;
            curBtn = 49;
            btn[curBtn].setBorderPainted(true);
        } else if (direction == "left" && curBtn != 0 && curBtn != 12 && curBtn != 24 && curBtn != 36 && curBtn != 48) {
            btn[curBtn].setBorderPainted(false);
            curBtn = curBtn-1;
            btn[curBtn].setBorderPainted(true);
        } else if (direction == "right" && curBtn != 11 && curBtn != 23 && curBtn != 35 && curBtn != 47 && curBtn != 49) {
            btn[curBtn].setBorderPainted(false);
            curBtn = curBtn+1;
            btn[curBtn].setBorderPainted(true);
        }
    }

    public void pressBackspace(boolean cont) {
        if (cont) {
            continueBackspace = true;
            pressBackspaceContinuously();
        } else {
            continueBackspace = false;
            try {
                backspaceTimer.cancel();
                backspaceTimer.purge();
            } catch (Exception ignored) {}
            typeBot.keyPress(KeyEvent.VK_BACK_SPACE);
        }
    }

    private void pressBackspaceContinuously() {
        backspaceTimer = new Timer();
        backspaceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (continueBackspace) {
                    typeBot.keyPress(KeyEvent.VK_BACK_SPACE);
                }
            }
        }, 75, 75);
    }

    public void pressSpace() {
        typeBot.keyPress(KeyEvent.VK_SPACE);
    }

    public void pressEscape() {
        typeBot.keyPress(KeyEvent.VK_ESCAPE);
    }

    public void pressAltTab() {
        typeBot.keyPress(KeyEvent.VK_ALT);
        typeBot.keyPress(KeyEvent.VK_TAB);
    }

    public void releaseAltTab() {
        typeBot.keyRelease(KeyEvent.VK_ALT);
        typeBot.keyRelease(KeyEvent.VK_TAB);
    }

    public void pressAltF4() {
        doType(KeyEvent.VK_ALT, KeyEvent.VK_F4);
    }

    public void releaseArrowKey() {
        try {
            arrowKeysTimer.cancel();
            arrowKeysTimer.purge();
        } catch (Exception ignored) {
        }
    }

    public void pressUp() {
        arrowKeysTimer = new Timer();
        arrowKeysTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                typeBot.keyPress(KeyEvent.VK_UP);
            }
        }, 75, 75);
    }

    public void pressDown() {
        arrowKeysTimer = new Timer();
        arrowKeysTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                typeBot.keyPress(KeyEvent.VK_DOWN);
            }
        }, 75, 75);
    }

    public void pressLeft() {
        arrowKeysTimer = new Timer();
        arrowKeysTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                typeBot.keyPress(KeyEvent.VK_LEFT);
            }
        }, 75, 75);
    }

    public void pressRight() {
        arrowKeysTimer = new Timer();
        arrowKeysTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                typeBot.keyPress(KeyEvent.VK_RIGHT);
            }
        }, 75, 75);
    }

    public void pressShiftKey() {
        typeBot.keyPress(KeyEvent.VK_SHIFT);
    }

    public void releaseShiftKey() {
        typeBot.keyRelease(KeyEvent.VK_SHIFT);
    }

    public void pressAltKey() {
        typeBot.keyPress(KeyEvent.VK_ALT);
    }

    public void releaseAltKey() {
        typeBot.keyRelease(KeyEvent.VK_ALT);
    }

    public void pressCtrlKey() {
        typeBot.keyPress(KeyEvent.VK_CONTROL);
    }

    public void releaseCtrlKey() {
        typeBot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void pressWinKey() {
        typeBot.keyPress(KeyEvent.VK_WINDOWS);
    }

    public void releaseWinKey() {
        typeBot.keyRelease(KeyEvent.VK_WINDOWS);
    }

    public void pressTabKey() {
        typeBot.keyRelease(KeyEvent.VK_TAB);
    }

    public void pressButton() {
        if (curBtn < 48) {
            btn[curBtn].doClick(50);

            switch (curKeyMap[curBtn].charAt(0)) {
                case 'a': doType(KeyEvent.VK_A); break;
                case 'b': doType(KeyEvent.VK_B); break;
                case 'c': doType(KeyEvent.VK_C); break;
                case 'd': doType(KeyEvent.VK_D); break;
                case 'e': doType(KeyEvent.VK_E); break;
                case 'f': doType(KeyEvent.VK_F); break;
                case 'g': doType(KeyEvent.VK_G); break;
                case 'h': doType(KeyEvent.VK_H); break;
                case 'i': doType(KeyEvent.VK_I); break;
                case 'j': doType(KeyEvent.VK_J); break;
                case 'k': doType(KeyEvent.VK_K); break;
                case 'l': doType(KeyEvent.VK_L); break;
                case 'm': doType(KeyEvent.VK_M); break;
                case 'n': doType(KeyEvent.VK_N); break;
                case 'o': doType(KeyEvent.VK_O); break;
                case 'p': doType(KeyEvent.VK_P); break;
                case 'q': doType(KeyEvent.VK_Q); break;
                case 'r': doType(KeyEvent.VK_R); break;
                case 's': doType(KeyEvent.VK_S); break;
                case 't': doType(KeyEvent.VK_T); break;
                case 'u': doType(KeyEvent.VK_U); break;
                case 'v': doType(KeyEvent.VK_V); break;
                case 'w': doType(KeyEvent.VK_W); break;
                case 'x': doType(KeyEvent.VK_X); break;
                case 'y': doType(KeyEvent.VK_Y); break;
                case 'z': doType(KeyEvent.VK_Z); break;

                case 'A': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_A); break;
                case 'B': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_B); break;
                case 'C': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_C); break;
                case 'D': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_D); break;
                case 'E': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_E); break;
                case 'F': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_F); break;
                case 'G': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_G); break;
                case 'H': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_H); break;
                case 'I': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_I); break;
                case 'J': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_J); break;
                case 'K': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_K); break;
                case 'L': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_L); break;
                case 'M': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_M); break;
                case 'N': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_N); break;
                case 'O': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_O); break;
                case 'P': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_P); break;
                case 'Q': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Q); break;
                case 'R': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_R); break;
                case 'S': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_S); break;
                case 'T': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_T); break;
                case 'U': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_U); break;
                case 'V': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_V); break;
                case 'W': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_W); break;
                case 'X': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_X); break;
                case 'Y': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Y); break;
                case 'Z': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_Z); break;

                case '0': doType(KeyEvent.VK_0); break;
                case '1': doType(KeyEvent.VK_1); break;
                case '2': doType(KeyEvent.VK_2); break;
                case '3': doType(KeyEvent.VK_3); break;
                case '4': doType(KeyEvent.VK_4); break;
                case '5': doType(KeyEvent.VK_5); break;
                case '6': doType(KeyEvent.VK_6); break;
                case '7': doType(KeyEvent.VK_7); break;
                case '8': doType(KeyEvent.VK_8); break;
                case '9': doType(KeyEvent.VK_9); break;

                case ',': doType(KeyEvent.VK_COMMA); break;
                case '.': doType(KeyEvent.VK_PERIOD); break;
                case ':': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SEMICOLON); break;
                case ';': doType(KeyEvent.VK_SEMICOLON); break;
                case '?': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_SLASH); break;
                case '!': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_1); break;
                case '@': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_2); break;
                case '^': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_6); break;
                case '_': doType(KeyEvent.VK_SHIFT, KeyEvent.VK_MINUS); break;
                case '-': doType(KeyEvent.VK_MINUS); break;
                case '/': doType(KeyEvent.VK_SLASH); break;
                case '\\': doType(KeyEvent.VK_BACK_SLASH); break;
            }
        } else {
            switch (curBtn) {
                case 48:
                    pressShift();
                    break;
                case 49:
                    pressEnter();
                    break;
                default:
                    // None
                    break;
            }
        }
    }

    public void doType(int... keyCodes) {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) {
        if (length == 0) {
            return;
        }

        try {
            typeBot.keyPress(keyCodes[offset]);
            doType(keyCodes, offset + 1, length - 1);
            typeBot.keyRelease(keyCodes[offset]);
        } catch (Exception ignore) {}

    }

    public void setStatus(String message) {
        status.setText("Status: " + message);
    }

    public void clearControllers() {
        controllerList.removeAllItems();
    }

    public void addController(String controller) {
        controllerList.addItem(controller);
    }

    public int getController() {
        return controllerList.getSelectedIndex();
    }

}
