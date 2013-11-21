import forms.MainForm;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import org.ini4j.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
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

public class Main {
    public static String version = "1.2";

    // Define all available key binds.
    public final static String[] keyBinds = {
            "mouse_leftclick", // 0
            "mouse_rightclick", // 1
            "mouse_middleclick", // 2
            "key_enter", // 3
            "key_windows", // 4
            "key_esc", // 5
            "key_alttab", // 6
            "key_altf4", // 7
            "gb_showhide", // 8
            "gb_confirm", // 9
            "gb_remove", // 10
            "gb_removecont", // 11
            "gb_space", // 12
            "gb_changecase", // 13
            "gb_enter", // 14
            "gb_left", // 15
            "gb_right", // 16
            "key_ctrltab", // 17
            "key_ctrlshifttab" // 18
    };

    public final static MainForm w = new MainForm();

    private static ArrayList<Controller> foundControllers;
    private static File filePref = new File("GameBoard.ini");
    private static Ini pref = new Ini();
    private static boolean comboStop = false;

    private static boolean beingConfigured = false;
    private static int counter = 0;
    private static String curConf = "mouse_leftclick";
    private static String curBtn = "";
    private static String curCon = "";
    private static boolean ignoreBtn = false;

    private static boolean isLooping = false;

    private static Timer mouseTimer;
    private static boolean moveMouseX = false;
    private static boolean moveMouseY = false;
    private static int[] mouseSpeedDiff = {0,0};

    public static void main(String[] args) {
        w.status("Loading settings...");

        // Check if preference file exists.
        if (!filePref.exists()) {
            w.restore(true);
            w.status("Preference file can't be found.");
            MsgBox.error("Can't find the preference file. Please re-download at http://coffeecone.com/gameboard to fix the issue.", "Error");
        }

        // Check if required keys are intact in the preference file.
        try {
            pref.load(filePref);
            if (!pref.get("about","version").equals(version)) {
                w.status("Preference file mismatch.");
                MsgBox.error("You are using a mismatched preference file.", "Error");
            }
        } catch (IOException ioe) {
            MsgBox.error(ioe.getMessage(),"Error");
        }

        w.about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MsgBox.info("GameBoard v" + version + "\n\nAn open-source on-screen keyboard for gamepads.\nVisit http://coffeecone.com/gameboard for more information.\n\nCopyright (c) 2013+ Shedo Surashu (CoffeeCone.com)", "About");
            }
        });

        w.configure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (w.isConfCancel()) {
                    if (MsgBox.question("Your changes will be lost the next time you open GameBoard. Continue?", "Cancel Changes")) {
                        doneConfigure();
                        w.status("Idle.");
                    }
                } else {
                    if (MsgBox.question("Would you like to configure this controller?", "Configure")) {
                        beingConfigured = true;
                        w.enableList(false);
                        w.enableConf(true);
                        w.confIsCancel(true);
                        assignPrompt("mouse_leftclick");
                        if (!isLooping) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    startController();
                                }
                            }, 250);
                        }
                    }
                }
            }
        });

        w.controllerList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!comboStop) {
                    curCon = w.getCon();
                    pref.put("about", "lastused", curCon);
                    savePrefs();
                    if (configuredController(curCon)) {
                        w.status("Ready.");
                        if (!isLooping) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    startController();
                                }
                            }, 250);
                        }
                    }
                }
            }
        });
        comboStop = false;

        w.minimize();

        foundControllers = new ArrayList<Controller>();
        searchControllers();

    }

    private static void searchControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        w.status("Searching for available controllers...");

        comboStop = true;

        foundControllers.clear();
        w.clearControllers();

        for (Controller controller : controllers) {
            if (controller.getType() == Controller.Type.STICK ||
                    controller.getType() == Controller.Type.GAMEPAD) {

                Component[] components = controller.getComponents();
                int compCheck = 0;
                for (Component component : components) { // Checks if controller has d-pad and an X-and-Y analog stick.
                    if (component.getIdentifier().toString().equals("pov") ||
                            component.getIdentifier().toString().equals("x") ||
                            component.getIdentifier().toString().equals("y")) {
                        compCheck++;
                    }
                }

                if (compCheck == 3) {
                    if (!pref.containsKey(controller.getName())) {
                        for (String keyBind : keyBinds) {
                            pref.put(controller.getName(), keyBind, "");
                        }
                    }

                    foundControllers.add(controller);
                    w.addController(controller.getName());
                }
            }
        }
        w.status("Controllers loaded.");

        if (pref.get("about","lastused") != null && !pref.get("about","lastused").equals("")) {
            w.controllerList.setSelectedItem(pref.get("about", "lastused"));
        } else {
            pref.put("about", "lastused", w.getCon());
        }

        savePrefs();

        if (foundControllers.isEmpty()) {
            curCon = "";
            w.enableConf(false);
            w.status("No controller found.");
        } else {
            curCon = w.getCon();
            w.enableConf(true);
            w.status("Ready.");
        }

        comboStop = false;

        startController();
    }

    private static boolean configuredController(String c)  {
        boolean isConfigured = false;

        for (String keyBind : keyBinds) {
            if (pref.get(c,keyBind) != null && !pref.get(c,keyBind).equals("")) {
                isConfigured = true;
            }
        }

        return isConfigured;
    }

    private static void startController() {
        String hatDir = "center";
        boolean breakLoop = false;
        boolean mouseMoving = false;

        mouseSpeedDiff[0] = 0;
        mouseSpeedDiff[1] = 0;

        while (true) {

            isLooping = true;

            Controller controller = foundControllers.get(w.getConID());
            String c = curCon;

            // Checks if user is in configuring the current controller.
            if (!beingConfigured) {
                if (!configuredController(controller.getName())) {
                    breakLoop = true;
                    w.restore(true);
                    w.status("This controller is not configured so click this button. =>");
                    break;
                }
            }

            // Polls the controller and checks if it has been unplugged.
            if (!controller.poll()) {
                breakLoop = true;
                w.restore(true);
                w.status("Controller unplugged!");
                doneConfigure();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        forceResetControllers();
                    }
                }, 50);
                break;
            }

            // Checks if X, Y, or both axes is being used by the user.
            if (moveMouseX || moveMouseY) {
                if (!mouseMoving) {
                    mouseMoving = true;
                    mouseSpeedDiff[1] = mouseSpeedDiff[0];
                    mouseStop();
                    mouseMove(mouseSpeedDiff[0]);
                }
            } else {
                mouseStop();
                mouseMoving = false;
            }

            EventQueue queue = controller.getEventQueue();
            Event event = new Event();

            while (queue.getNextEvent(event)) {
                Component component = event.getComponent();
                float value = event.getValue();
                Component.Identifier compID = component.getIdentifier();

                if (breakLoop) {
                    break;
                }

                if (component.isAnalog()) { // Analog sticks

                   if (!beingConfigured) {

                       float fX, fY;
                       int x = 0, y = 0;

                       w.minimize();

                       if (compID == Component.Identifier.Axis.X || compID == Component.Identifier.Axis.Y) {

                           if (compID == Component.Identifier.Axis.X) {
                               fX = component.getPollData();
                               fX = fX * 10.0f;
                               x = Math.round(fX);
                               if (x != 0) {
                                   if (Math.abs(x) == x) {
                                       w.mouseIncX = 5;
                                   } else {
                                       w.mouseIncX = -5;
                                   }
                                   moveMouseX = true;
                               } else {
                                   w.mouseIncX = 0;
                                   moveMouseX = false;
                               }
                           }

                           if (compID == Component.Identifier.Axis.Y) {
                               fY = component.getPollData();
                               fY = fY * 10.0f;
                               y = Math.round(fY);
                               if (y != 0) {
                                   if (Math.abs(y) == y) {
                                       w.mouseIncY = 5;
                                   } else {
                                       w.mouseIncY = -5;
                                   }
                                   moveMouseY = true;
                               } else {
                                   w.mouseIncY = 0;
                                   moveMouseY = false;
                               }
                           }

                           if (Math.abs(x) > Math.abs(y)) {
                               mouseSpeedDiff[0] = Math.abs(x);
                           } else {
                               mouseSpeedDiff[0] = Math.abs(y);
                           }

                       }

                   }

                } else if (compID == Component.Identifier.Axis.POV) { // Hat switches

                    if (!beingConfigured) {

                        showTyping();

                        if (w.getState() == Frame.NORMAL) {

                            if (Float.compare(value, Component.POV.UP) == 0) {
                                w.selectButton("up");
                            } else if (Float.compare(value, Component.POV.DOWN) == 0) {
                                w.selectButton("down");
                            } else if (Float.compare(value, Component.POV.LEFT) == 0) {
                                w.selectButton("left");
                            } else if (Float.compare(value, Component.POV.RIGHT) == 0) {
                                w.selectButton("right");
                            }

                        } else if (w.getState() == Frame.ICONIFIED) {

                            if (Float.compare(value, Component.POV.UP) == 0) {
                                if (!hatDir.equals("up")) {
                                    w.releaseArrowKey();
                                    hatDir = "up";
                                }
                                w.pressUp();
                            } else if (Float.compare(value, Component.POV.DOWN) == 0) {
                                if (!hatDir.equals("down")) {
                                    w.releaseArrowKey();
                                    hatDir = "down";
                                }
                                w.pressDown();
                            } else if (Float.compare(value, Component.POV.LEFT) == 0) {
                                if (!hatDir.equals("left")) {
                                    w.releaseArrowKey();
                                    hatDir = "left";
                                }
                                w.pressLeft();
                            } else if (Float.compare(value, Component.POV.RIGHT) == 0) {
                                if (!hatDir.equals("right")) {
                                    w.releaseArrowKey();
                                    hatDir = "right";
                                }
                                w.pressRight();
                            } else {
                                hatDir = "center";
                                w.releaseArrowKey();
                            }

                        }

                    }

                } else { // Buttons

                    if (beingConfigured && value == 1.0f) {

                        curBtn = compID.getName();

                        if (counter < keyBinds.length) {

                            if (!ignoreBtn) {
                                ignoreBtn = true;
                                new Timer().schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        configureIncrement();
                                    }
                                }, 50);
                            }

                        } else {

                            counter = 0;
                            savePrefs();
                            beingConfigured = false;
                            w.status("Ready.");
                            doneConfigure();

                        }

                    } else if (!beingConfigured) {

                        hatDir = "center";
                        w.releaseArrowKey();

                        // TODO: Figure out a more dynamic way of making the settings work.

                        if (w.getState() == Frame.ICONIFIED) {

                            // Desktop mode

                            try {
                                if (pref.get(c,keyBinds[0]).equals(String.valueOf(compID))) { // Left Click
                                    if (value == 1.0f) {
                                        w.pressLeftMouseBtn();} else {
                                        w.releaseLeftMouseBtn();}
                                } else if (pref.get(c,keyBinds[1]).equals(String.valueOf(compID))) { // Right Click
                                    if (value == 1.0f) {
                                        w.pressRightMouseBtn();} else {
                                        w.releaseRightMouseBtn();}
                                } else if (pref.get(c,keyBinds[2]).equals(String.valueOf(compID))) { // Middle Click
                                    if (value == 1.0f) {
                                        w.pressMiddleMouseBtn();} else {
                                        w.releaseMiddleMouseBtn();}
                                } else if (pref.get(c,keyBinds[3]).equals(String.valueOf(compID)) && value == 1.0f) { // Enter
                                    w.pressEnter();
                                } else if (pref.get(c,keyBinds[4]).equals(String.valueOf(compID))) { // Windows
                                    if (value == 1.0f) {
                                        w.pressWinKey();} else {
                                        w.releaseWinKey();}
                                } else if (pref.get(c,keyBinds[5]).equals(String.valueOf(compID)) && value == 1.0f) { // Escape
                                    w.pressEscape();
                                } else if (pref.get(c,keyBinds[6]).equals(String.valueOf(compID))) { // Alt + Tab
                                    if (value == 1.0f) {
                                        w.pressAltTab();} else {
                                        w.releaseAltTab();}
                                } else if (pref.get(c,keyBinds[7]).equals(String.valueOf(compID)) && value == 1.0f) { // Alt + F4
                                    w.pressAltF4();
                                } else if (pref.get(c,keyBinds[8]).equals(String.valueOf(compID)) && value == 1.0f) { // Show
                                    w.restore(false);
                                } else if (pref.get(c,keyBinds[17]).equals(String.valueOf(compID)) && value == 1.0f) { // Ctrl + Tab
                                    w.pressCtrlTab();
                                } else if (pref.get(c,keyBinds[18]).equals(String.valueOf(compID)) && value == 1.0f) { // Crel + Shift + Tab
                                    w.pressCtrlShiftTab();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                MsgBox.error(e.getMessage(),"Execution Error");
                            }

                        } else if (w.getState() == Frame.NORMAL) {

                            // Keyboard mode

                            try {
                                if (pref.get(c,keyBinds[8]).equals(String.valueOf(compID)) && value == 1.0f) { // Hide
                                    w.minimize();
                                } else if (pref.get(c,keyBinds[9]).equals(String.valueOf(compID)) && value == 1.0f) { // Confirm
                                    showTyping();
                                    w.pressButton();
                                } else if (pref.get(c,keyBinds[10]).equals(String.valueOf(compID)) && value == 1.0f) { // Remove
                                    w.pressBackspace(false);
                                } else if (pref.get(c,keyBinds[11]).equals(String.valueOf(compID))) { // Remove Cont.
                                    if (value == 1.0f) {
                                        w.pressBackspace(true);} else {
                                        w.pressBackspace(false);}
                                } else if (pref.get(c,keyBinds[12]).equals(String.valueOf(compID)) && value == 1.0f) { // Remove
                                    w.pressSpace();
                                } else if (pref.get(c,keyBinds[13]).equals(String.valueOf(compID)) && value == 1.0f) { // Change Case
                                    w.pressShift();
                                } else if (pref.get(c,keyBinds[14]).equals(String.valueOf(compID)) && value == 1.0f) { // Enter
                                    w.pressEnter();
                                } else if (pref.get(c,keyBinds[15]).equals(String.valueOf(compID)) && value == 1.0f) { // Left
                                    if (!hatDir.equals("left")) {
                                        w.releaseArrowKey();
                                        hatDir = "left";
                                    }
                                    w.pressLeft();
                                } else if (pref.get(c,keyBinds[16]).equals(String.valueOf(compID)) && value == 1.0f) { // Right
                                    if (!hatDir.equals("right")) {
                                        w.releaseArrowKey();
                                        hatDir = "right";
                                    }
                                    w.pressRight();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                MsgBox.error(e.getMessage(), "Execution Error");
                            }

                        }

                    }

                }
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
                MsgBox.error(e.getMessage(), "Execution Error");
            }

        }

        isLooping = false;

        if (!breakLoop && !beingConfigured) {
            forceResetControllers();
        }

    }

    // Temporary fix to JInput bug. This forces reset the list of controllers available.
    // Hope this gets fixed soon.
    private static void forceResetControllers() {
        try {
            Class<?> clazz = Class.forName("net.java.games.input.DefaultControllerEnvironment");
            Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
            defaultConstructor.setAccessible(true); // set visibility to public

            Field defaultEnvironementField = ControllerEnvironment.class.getDeclaredField("defaultEnvironment");
            defaultEnvironementField.setAccessible(true);
            defaultEnvironementField.set(ControllerEnvironment.getDefaultEnvironment(), defaultConstructor.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchControllers();
    }

    private static void mouseMove(int s) {
        int[] diffMap = {30,25,20,15,10}; // Cursor speed settings. Higher is slower.
        int diff = s-6;
        if (diff < 0) {
            diff = 0;
        }

        mouseTimer = new Timer();

        try {
            mouseTimer.schedule( new TimerTask() {
                @Override
                public void run() {
                    if (Math.abs(mouseSpeedDiff[0]-mouseSpeedDiff[1]) > 0) {
                        mouseSpeedDiff[1] = mouseSpeedDiff[0];
                        cancel();
                        mouseStop();
                        mouseMove(mouseSpeedDiff[0]);
                    } else {
                        w.mouseSet();
                    }
                }
            },diffMap[diff],diffMap[diff]);
        } catch (Exception e) {
            System.out.println(diff);
            e.printStackTrace();
            MsgBox.error(e.getMessage(), "Error");
        }

    }

    private static void mouseStop() {
        try {
            mouseTimer.cancel();
            mouseTimer.purge();
        } catch (Exception ignored) {}
    }

    public static void showTyping() {
        w.status("Typing...");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                w.status("Idle.");
            }
        }, 150);
    }

    public static void assignPrompt(String t) {
        w.status("Assign a button for \"" + t + "\"...");
    }

    public static void doneConfigure() {
        counter = 0;
        curConf = "mouse_leftclick";
        curBtn = "";
        ignoreBtn = false;
        beingConfigured = false;
        w.enableList(true);
        w.enableConf(true);
        w.confIsCancel(false);
    }

    public static void configureIncrement() {

        int result = MsgBox.option("Are you sure you want to assign button " + curBtn + " to " + curConf + "?\nTo disable this function, select NO.\nIf you want to change the button, select CANCEL.","Assign Button");
        if (result < 2) {

            if (result == 0) {
                pref.put(curCon,curConf,String.valueOf(curBtn));
            } else {
                pref.put(curCon,curConf,"");
            }

            counter++;
            if (counter < keyBinds.length) {
                curConf = keyBinds[counter];
            }

        }

        ignoreBtn = false;

        if (counter < keyBinds.length) {
            assignPrompt(curConf);
        } else {
            w.status("Done! Press any button to continue.");
        }
    }

    private static void savePrefs() {
        try {
            pref.store(filePref);
        } catch (IOException e) {
            e.printStackTrace();
            MsgBox.error(e.getMessage(), "File Write Error");
        }
    }

}
