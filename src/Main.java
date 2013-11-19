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
    public static String version = "1.0";

    public final static MainForm mainForm = new MainForm();
    private static ArrayList<Controller> foundControllers;
    private static File filePref = new File("GameBoard.ini");
    private static Ini pref = new Ini();
    private static Boolean comboStop = true;

    private static Boolean beingConfigured = false;
    private static int counter = 0;

    public static void main(String[] args) {
        mainForm.setStatus("Loading settings...");

        // Check if preference file exists.
        if (!filePref.exists()) {
            mainForm.restoreWindow(true);
            mainForm.setStatus("Preference file can't be found.");
            System.out.println("error");
            MsgBox.error("Can't find the preference file. Please re-download at http://coffeecone.com/gameboard to fix the issue.", "Error");
        }

        // Check if required keys are intact in the preference file.
        try {
            pref.load(filePref);
            if (!pref.get("about","version").equals(version)) {
                mainForm.setStatus("Preference file mismatch.");
                MsgBox.error("You are using a mismatched preference file.", "Error");
            }
        } catch (IOException ioe) {
            MsgBox.error(ioe.getMessage(),"Error");
        }

        mainForm.about.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MsgBox.info("GameBoard v" + version + "\n\nAn open-source on-screen keyboard for gamepads.\nVisit http://coffeecone.com/gameboard for more information.\n\nCopyright (c) 2013+ Shedo Surashu (CoffeeCone.com)", "About");
            }
        });

        mainForm.configure.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mainForm.configure.getText().equals("Cancel")) {
                    if (MsgBox.question("Your changes will be lost the next time you open GameBoard. Continue?", "Cancel Changes")) {
                        doneConfigure();
                        mainForm.setStatus("Idle.");
                    }
                } else {
                    if (MsgBox.question("Would you like to configure this controller?", "Configure")) {
                        beingConfigured = true;
                        mainForm.controllerList.setEnabled(false);
                        mainForm.configure.setEnabled(false);
                        mainForm.configure.setText("Cancel");
                        assignPrompt("mouse_leftclick");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                mainForm.configure.setEnabled(true);
                                startController();
                            }
                        }, 250);
                    }
                }
            }
        });

        mainForm.controllerList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!comboStop) {
                    if (configuredController(mainForm.controllerList.getSelectedItem().toString())) {
                        if (pref.get("about","lastused") != null && !pref.get("about","lastused").equals("")) {
                            mainForm.controllerList.setSelectedItem(pref.get("about","lastused"));
                        } else {
                            pref.put("about","lastused",mainForm.controllerList.getSelectedItem().toString());
                        }
                        try {
                            pref.store();
                        } catch (IOException ignored) {}
                        mainForm.setStatus("Ready.");
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                startController();
                            }
                        }, 250);
                    }
                }
            }
        });
        comboStop = false;

        mainForm.minimizeWindow();

        foundControllers = new ArrayList<Controller>();
        searchControllers();

    }

    private static void searchControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        mainForm.setStatus("Searching for available controllers...");

        comboStop = true;

        foundControllers.clear();
        mainForm.clearControllers();

        for (Controller controller : controllers) {
            if (controller.getType() == Controller.Type.STICK ||
                    controller.getType() == Controller.Type.GAMEPAD) {
                if (!pref.containsKey(controller.getName())) {
                    pref.put(controller.getName(),"mouse_leftclick","");
                    pref.put(controller.getName(),"mouse_rightclick","");
                    pref.put(controller.getName(),"mouse_middleclick","");
                    pref.put(controller.getName(),"key_enter","");
                    pref.put(controller.getName(),"key_windows","");
                    pref.put(controller.getName(),"key_esc","");
                    pref.put(controller.getName(),"key_alttab","");
                    pref.put(controller.getName(),"key_altf4","");
                    pref.put(controller.getName(),"gb_showhide","");
                    pref.put(controller.getName(),"gb_confirm","");
                    pref.put(controller.getName(),"gb_remove","");
                    pref.put(controller.getName(),"gb_removecont","");
                    pref.put(controller.getName(),"gb_space","");
                    pref.put(controller.getName(),"gb_changecase","");
                    pref.put(controller.getName(),"gb_enter","");
                    pref.put(controller.getName(),"gb_left","");
                    pref.put(controller.getName(),"gb_right","");
                }

                foundControllers.add(controller);
                mainForm.addController(controller.getName());
            }
        }
        mainForm.setStatus("Controllers loaded.");

        if (pref.get("about","lastused") != null && !pref.get("about","lastused").equals("")) {
            mainForm.controllerList.setSelectedItem(pref.get("about", "lastused"));
        } else {
            pref.put("about","lastused",mainForm.controllerList.getSelectedItem().toString());
        }

        try {
            pref.store(filePref);
        } catch (IOException ioe) {
            MsgBox.error(ioe.getMessage(),"Error");
        }

        if (foundControllers.isEmpty()) {
            mainForm.configure.setEnabled(false);
            mainForm.setStatus("No controller found.");
        } else {
            mainForm.configure.setEnabled(true);

            mainForm.setStatus("Done.");
        }

        comboStop = false;

        startController();
    }

    private static boolean configuredController(String c)  {
        boolean isConfigured = false;

        if (pref.get(c,"mouse_leftclick") != null && !pref.get(c,"mouse_leftclick").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"mouse_rightclick") != null && !pref.get(c,"mouse_rightclick").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"mouse_middleclick") != null && !pref.get(c,"mouse_middleclick").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"key_enter") != null && !pref.get(c,"key_enter").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"key_windows") != null && !pref.get(c,"key_windows").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"key_esc") != null && !pref.get(c,"key_esc").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"key_alttab") != null && !pref.get(c,"key_alttab").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"key_altf4") != null && !pref.get(c,"key_altf4").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_showhide") != null && !pref.get(c,"gb_showhide").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_confirm") != null && !pref.get(c,"gb_confirm").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_remove") != null && !pref.get(c,"gb_remove").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_removecont") != null && !pref.get(c,"gb_removecont").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_space") != null && !pref.get(c,"gb_space").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_changecase") != null && !pref.get(c,"gb_changecase").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_enter") != null && !pref.get(c,"gb_enter").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_left") != null && !pref.get(c,"gb_left").equals("")) {
            isConfigured = true;
        } else if (pref.get(c,"gb_right") != null && !pref.get(c,"gb_right").equals("")) {
            isConfigured = true;
        }

        return isConfigured;
    }

    private static void startController() {
        String prevDir = "center";
        Boolean breakLoop = false;
        String curConf = "mouse_leftclick";

        while (true) {

            // Get the selected item index on combolist.
            int controllerIndex = mainForm.getController();
            Controller controller = foundControllers.get(controllerIndex);
            String c = controller.getName();

            if (!beingConfigured) {
                if (!configuredController(controller.getName())) {
                    breakLoop = true;
                    mainForm.restoreWindow(true);
                    mainForm.setStatus("This controller is not configured so click this. =>");
                    break;
                }
            }

            // Polls the controller and checks if it has been unplugged.
            if (!controller.poll()) {
                breakLoop = true;
                mainForm.restoreWindow(true);
                mainForm.setStatus("Controller unplugged!");
                mainForm.controllerList.setEnabled(true);
                doneConfigure();
                break;
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
                       int x, y;

                       mainForm.minimizeWindow();

                       if (compID == Component.Identifier.Axis.X || compID == Component.Identifier.Axis.Y) {

                           if (compID == Component.Identifier.Axis.X) {
                               fX = component.getPollData();
                               fX = fX * 10.0f;
                               x = Math.round(fX);
                               mainForm.mouseX = x;
                               mainForm.mouseSetX(x);
                           }

                           if (compID == Component.Identifier.Axis.Y) {
                               fY = component.getPollData();
                               fY = fY * 10.0f;
                               y = Math.round(fY);
                               mainForm.mouseY = y;
                               mainForm.mouseSetY(y);
                           }

                       }
                   }

                } else if (compID == Component.Identifier.Axis.POV) { // Hat switches
                    if (!beingConfigured) {

                        showTyping();

                        if (mainForm.getState() == Frame.NORMAL) {
                            if (Float.compare(value, Component.POV.UP) == 0) {
                                mainForm.selectButton("up");
                            } else if (Float.compare(value, Component.POV.DOWN) == 0) {
                                mainForm.selectButton("down");
                            } else if (Float.compare(value, Component.POV.LEFT) == 0) {
                                mainForm.selectButton("left");
                            } else if (Float.compare(value, Component.POV.RIGHT) == 0) {
                                mainForm.selectButton("right");
                            }
                        } else if (mainForm.getState() == Frame.ICONIFIED) {
                            if (Float.compare(value, Component.POV.UP) == 0) {
                                if (!prevDir.equals("up")) {
                                    mainForm.releaseArrowKey();
                                    prevDir = "up";
                                }
                                mainForm.pressUp();
                            } else if (Float.compare(value, Component.POV.DOWN) == 0) {
                                if (!prevDir.equals("down")) {
                                    mainForm.releaseArrowKey();
                                    prevDir = "down";
                                }
                                mainForm.pressDown();
                            } else if (Float.compare(value, Component.POV.LEFT) == 0) {
                                if (!prevDir.equals("left")) {
                                    mainForm.releaseArrowKey();
                                    prevDir = "left";
                                }
                                mainForm.pressLeft();
                            } else if (Float.compare(value, Component.POV.RIGHT) == 0) {
                                if (!prevDir.equals("right")) {
                                    mainForm.releaseArrowKey();
                                    prevDir = "right";
                                }
                                mainForm.pressRight();
                            } else {
                                prevDir = "center";
                                mainForm.releaseArrowKey();
                            }
                        }
                    }

                } else { // Buttons

                    if (beingConfigured && value == 1.0f) {

                        if (counter > 16) {
                            counter = 0;
                            try {
                                pref.store(filePref);
                            } catch (IOException e) {
                                MsgBox.error(e.getMessage(), "Error");
                            }
                            beingConfigured = false;
                            mainForm.setStatus("Ready.");
                            doneConfigure();
                        } else {
                            int result = MsgBox.option("Are you sure you want to assign button " + String.valueOf(compID) + " to " + curConf + "?\nTo disable this function, select NO.\nIf you want to change the button, select CANCEL.","Assign Button");
                            if (result < 2) {

                                if (result == 0) {
                                    pref.put(c,curConf,String.valueOf(compID));
                                } else {
                                    pref.put(c,curConf,"");
                                }

                                counter++;

                                switch (counter) {
                                    case 0: curConf = "mouse_leftclick"; break;
                                    case 1: curConf = "mouse_rightclick"; break;
                                    case 2: curConf = "mouse_middleclick"; break;
                                    case 3: curConf = "key_enter"; break;
                                    case 4: curConf = "key_windows"; break;
                                    case 5: curConf = "key_esc"; break;
                                    case 6: curConf = "key_alttab"; break;
                                    case 7: curConf = "key_altf4"; break;
                                    case 8: curConf = "gb_showhide"; break;
                                    case 9: curConf = "gb_confirm"; break;
                                    case 10: curConf = "gb_remove"; break;
                                    case 11: curConf = "gb_removecont"; break;
                                    case 12: curConf = "gb_space"; break;
                                    case 13: curConf = "gb_changecase"; break;
                                    case 14: curConf = "gb_enter"; break;
                                    case 15: curConf = "gb_left"; break;
                                    case 16: curConf = "gb_right"; break;
                                }

                            }
                            //System.out.println(counter);
                            if (counter <= 16) {
                                assignPrompt(curConf);
                            } else {
                                mainForm.setStatus("Done! Press any button to continue.");
                            }
                        }

                    } else if (!beingConfigured) {

                        prevDir = "center";
                        mainForm.releaseArrowKey();

                        if (mainForm.getState() == Frame.ICONIFIED) {

                            // Desktop mode

                            try {
                                if (pref.get(c,"mouse_leftclick").equals(String.valueOf(compID))) { // Left Click
                                    if (value == 1.0f) {mainForm.pressLeftMouseBtn();} else {mainForm.releaseLeftMouseBtn();}
                                } else if (pref.get(c,"mouse_rightclick").equals(String.valueOf(compID))) { // Right Click
                                    if (value == 1.0f) {mainForm.pressRightMouseBtn();} else {mainForm.releaseRightMouseBtn();}
                                } else if (pref.get(c,"mouse_middleclick").equals(String.valueOf(compID))) { // Middle Click
                                    if (value == 1.0f) {mainForm.pressMiddleMouseBtn();} else {mainForm.releaseMiddleMouseBtn();}
                                } else if (pref.get(c,"key_enter").equals(String.valueOf(compID)) && value == 1.0f) { // Enter
                                    mainForm.pressEnter();
                                } else if (pref.get(c,"key_windows").equals(String.valueOf(compID))) { // Windows
                                    if (value == 1.0f) {mainForm.pressWinKey();} else {mainForm.releaseWinKey();}
                                } else if (pref.get(c,"key_esc").equals(String.valueOf(compID)) && value == 1.0f) { // Escape
                                    mainForm.pressEscape();
                                } else if (pref.get(c,"key_alttab").equals(String.valueOf(compID))) { // Alt + Tab
                                    if (value == 1.0f) {mainForm.pressAltTab();} else {mainForm.releaseAltTab();}
                                } else if (pref.get(c,"key_altf4").equals(String.valueOf(compID)) && value == 1.0f) { // Alt + F4
                                    mainForm.pressAltF4();
                                } else if (pref.get(c,"gb_showhide").equals(String.valueOf(compID)) && value == 1.0f) { // Show
                                    mainForm.restoreWindow(false);
                                }
                            } catch (Exception e) {
                                MsgBox.error(e.getMessage(),"Execution Error");
                            }

                        } else if (mainForm.getState() == Frame.NORMAL) {

                            // GameBoard mode

                            try {
                                if (pref.get(c,"gb_showhide").equals(String.valueOf(compID)) && value == 1.0f) { // Hide
                                    mainForm.minimizeWindow();
                                } else if (pref.get(c,"gb_confirm").equals(String.valueOf(compID)) && value == 1.0f) { // Confirm
                                    showTyping();
                                    mainForm.pressButton();
                                } else if (pref.get(c,"gb_remove").equals(String.valueOf(compID)) && value == 1.0f) { // Remove
                                    mainForm.pressBackspace(false);
                                } else if (pref.get(c,"gb_removecont").equals(String.valueOf(compID))) { // Remove Cont.
                                    if (value == 1.0f) {mainForm.pressBackspace(true);} else {mainForm.pressBackspace(false);}
                                } else if (pref.get(c,"gb_space").equals(String.valueOf(compID)) && value == 1.0f) { // Remove
                                    mainForm.pressSpace();
                                } else if (pref.get(c,"gb_changecase").equals(String.valueOf(compID)) && value == 1.0f) { // Change Case
                                    mainForm.pressShift();
                                } else if (pref.get(c,"gb_enter").equals(String.valueOf(compID)) && value == 1.0f) { // Enter
                                    mainForm.pressEnter();
                                } else if (pref.get(c,"gb_left").equals(String.valueOf(compID)) && value == 1.0f) { // Left
                                    if (!prevDir.equals("left")) {
                                        mainForm.releaseArrowKey();
                                        prevDir = "left";
                                    }
                                    mainForm.pressLeft();
                                } else if (pref.get(c,"gb_right").equals(String.valueOf(compID)) && value == 1.0f) { // Right
                                    if (!prevDir.equals("right")) {
                                        mainForm.releaseArrowKey();
                                        prevDir = "right";
                                    }
                                    mainForm.pressRight();
                                }
                            } catch (Exception e) {
                                MsgBox.error(e.getMessage(), "Execution Error");
                            }

                        }
                    }

                }
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                MsgBox.error(e.getMessage(), "Execution Error");
            }

        }

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

        if (!breakLoop && !beingConfigured) {
            searchControllers();
        }

    }

    public static void showTyping() {
        mainForm.setStatus("Typing...");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mainForm.setStatus("Idle.");
            }
        }, 250);
    }

    public static void assignPrompt(String t) {
        mainForm.setStatus("Assign a button for \""+t+"\"...");
    }

    public static void doneConfigure() {
        beingConfigured = false;
        mainForm.controllerList.setEnabled(true);
        mainForm.configure.setText("Configure");
    }

}
