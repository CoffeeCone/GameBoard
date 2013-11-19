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

    public static void main(String[] args) {
        mainForm.setStatus("Loading settings...");

        // Check if preference file exists.
        if (!filePref.exists()) {
            mainForm.setStatus("Preference file can't be found.");
            mainForm.setAlwaysOnTop(false);
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
//            System.out.println(pref.get("FuSGamePad","mouse_leftclick"));
//            if (pref.get("FuSa GamePd","mouse_leftclick") == "") {
//                mainForm.setStatus("Null detected.");
//                MsgBox.error("No button assigned here.", "Error");
//            }
        } catch (IOException ioe) {
            MsgBox.error(ioe.getMessage(),"Error");
        }

        mainForm.about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MsgBox.info("GameBoard v" + version + "\n\nAn open-source on-screen keyboard for gamepads.\nVisit http://coffeecone.com/gameboard for more information.\n\nCopyright (c) 2013+ Shedo Surashu (CoffeeCone.com)","About");
            }
        });

        mainForm.minimizeWindow();

        foundControllers = new ArrayList<Controller>();
        searchControllers();

    }

    private static void searchControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        mainForm.setStatus("Searching for available controllers...");

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
        try {
            pref.store(filePref);
        } catch (IOException ioe) {
            MsgBox.error(ioe.getMessage(),"Error");
        }

        if (foundControllers.isEmpty()) {
            mainForm.setStatus("No controller found.");
        } else {
            mainForm.setStatus("Done.");
            startController();
        }

    }

    public static void configureController() {

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
//        pref.put(c,"mouse_leftclick","");
//        pref.put(c,"mouse_rightclick","");
//        pref.put(c,"mouse_middleclick","");
//        pref.put(c,"key_enter","");
//        pref.put(c,"key_windows","");
//        pref.put(c,"key_esc","");
//        pref.put(c,"key_alttab","");
//        pref.put(c,"key_altf4","");
//        pref.put(c,"gb_showhide","");
//        pref.put(c,"gb_confirm","");
//        pref.put(c,"gb_remove","");
//        pref.put(c,"gb_removecont","");
//        pref.put(c,"gb_space","");
//        pref.put(c,"gb_changecase","");
//        pref.put(c,"gb_enter","");
//        pref.put(c,"gb_left","");
//        pref.put(c,"gb_right","");

        return isConfigured;
    }

    private static void startController() {
        String prevDir = "center";
        Boolean breakLoop = false;

        while (true) {

            // Get the selected item index on combolist.
            int controllerIndex = mainForm.getController();
            Controller controller = foundControllers.get(controllerIndex);
            String c = controller.getName();

            if (!configuredController(controller.getName())) {
                breakLoop = true;
                mainForm.restoreWindow(true);
                mainForm.setStatus("This controller is not configured so click this. =>");
                break;
            }

            // Polls the controller and checks if it has been unplugged.
            if (!controller.poll()) {
                breakLoop = true;
                mainForm.restoreWindow(true);
                mainForm.setStatus("Controller unplugged!");
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

                } else if (compID == Component.Identifier.Axis.POV) { // Hat switches

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

                } else { // Buttons
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
                            e.printStackTrace();
                        }

                    } else if (mainForm.getState() == Frame.NORMAL) {

                        // GameBoard mode

                        try {
                            if (pref.get(c,"gb_showhide").equals(String.valueOf(compID)) && value == 1.0f) { // Hide
                                mainForm.minimizeWindow();
                            } else if (pref.get(c,"gb_confirm").equals(String.valueOf(compID)) && value == 1.0f) { // Confirm
                                mainForm.pressButton();
                            } else if (pref.get(c,"gb_remove").equals(String.valueOf(compID)) && value == 1.0f) { // Remove
                                mainForm.pressBackspace(false);
                            } else if (pref.get(c,"gb_removecont").equals(String.valueOf(compID))) { // Remove Cont.
                                if (value == 1.0f) {mainForm.pressBackspace(true);} else {mainForm.pressBackspace(false);}
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
                            e.printStackTrace();
                        }

                    }

                }
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
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

        if (!breakLoop) {
            searchControllers();
        }

    }

}
