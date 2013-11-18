import forms.MainForm;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import java.awt.*;
import java.awt.event.KeyEvent;
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
    final static MainForm mainForm = new MainForm();
    private static ArrayList<Controller> foundControllers;

    public static void main(String[] args) {
        mainForm.minimizeWindow();

        mainForm.setStatus("Getting controller list.");

        foundControllers = new ArrayList<Controller>();
        searchControllers();

        if (foundControllers.isEmpty()) {
            mainForm.setStatus("No controller found.");
        } else {
            mainForm.setStatus("Done.");
            startController();
        }

    }

    private static void searchControllers() {
        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for (int i = 0; i < controllers.length; i++) {
            Controller controller = controllers[i];

            if (controller.getType() == Controller.Type.STICK ||
                    controller.getType() == Controller.Type.GAMEPAD) {
                foundControllers.add(controller);
                mainForm.addController(controller.getName() + " (" + controller.getType().toString() + ")");
            }
        }
    }

    private static void startController() {
        int[] hatFlag = new int[] {0,0,0,0};
        int[] btnFlag = new int[] {0,0};

        while (true) {

            // Get the selected item index on combolist.
            int controllerIndex = mainForm.getController();
            Controller controller = foundControllers.get(controllerIndex);

            // Polls the controller and checks if it has been unplugged.
            if (!controller.poll()) {
                mainForm.setStatus("Controller unplugged!");
                break;
            }

            EventQueue queue = controller.getEventQueue();
            Event event = new Event();

            while (queue.getNextEvent(event)) {
                Component component = event.getComponent();
                float value = event.getValue();
                Component.Identifier compID = component.getIdentifier();

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
                            mainForm.pressUp();
                        } else if (Float.compare(value, Component.POV.DOWN) == 0) {
                            mainForm.pressDown();
                        } else if (Float.compare(value, Component.POV.LEFT) == 0) {
                            mainForm.pressLeft();
                        } else if (Float.compare(value, Component.POV.RIGHT) == 0) {
                            mainForm.pressRight();
                        } else {
                            mainForm.releaseArrowKey();
                        }
                    }

                } else { // Buttons
                    if (compID == Component.Identifier.Button._0 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // X
                        mainForm.pressButton();
                    } else if (compID == Component.Identifier.Button._0 && value == 1.0f && mainForm.getState() == Frame.ICONIFIED) { // X
                        mainForm.pressLeftMouseBtn();
                    } else if (compID == Component.Identifier.Button._0 && value != 1.0f && mainForm.getState() == Frame.ICONIFIED) { // X
                        mainForm.releaseLeftMouseBtn();
                    } else if (compID == Component.Identifier.Button._1 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // O
                        mainForm.pressBackspace(true);
                    } else if (compID == Component.Identifier.Button._1 && value != 1.0f && mainForm.getState() == Frame.NORMAL) { // O
                        mainForm.pressBackspace(false);
                    } else if (compID == Component.Identifier.Button._1 && value == 1.0f && mainForm.getState() == Frame.ICONIFIED) { // O
                        mainForm.pressRightMouseBtn();
                    } else if (compID == Component.Identifier.Button._1 && value != 1.0f && mainForm.getState() == Frame.ICONIFIED) { // O
                        mainForm.releaseRightMouseBtn();
                    } else if (compID == Component.Identifier.Button._2 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // []
                        mainForm.pressBackspace(false);
                    } else if (compID == Component.Identifier.Button._2 && value == 1.0f && mainForm.getState() == Frame.ICONIFIED) { // []
                        mainForm.pressWinKey();
                    } else if (compID == Component.Identifier.Button._2 && value != 1.0f && mainForm.getState() == Frame.ICONIFIED) { // []
                        mainForm.releaseWinKey();
                    } else if (compID == Component.Identifier.Button._3 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // /\
                        mainForm.pressSpace();
                    } else if (compID == Component.Identifier.Button._3 && value == 1.0f && mainForm.getState() == Frame.ICONIFIED) { // /\
                        mainForm.pressMiddleMouseBtn();
                    } else if (compID == Component.Identifier.Button._3 && value != 1.0f && mainForm.getState() == Frame.ICONIFIED) { // /\
                        mainForm.releaseMiddleMouseBtn();
                    } else if (compID == Component.Identifier.Button._4 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // L
                        mainForm.pressLeft();
                    } else if (compID == Component.Identifier.Button._4 && value != 1.0f && mainForm.getState() == Frame.NORMAL) { // L
                        mainForm.releaseArrowKey();
                    } else if (compID == Component.Identifier.Button._5 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // R
                        mainForm.pressRight();
                    } else if (compID == Component.Identifier.Button._5 && value != 1.0f && mainForm.getState() == Frame.NORMAL) { // R
                        mainForm.releaseArrowKey();
                    } else if (compID == Component.Identifier.Button._6 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // Select
                        mainForm.pressShift();
                    } else if (compID == Component.Identifier.Button._6 && value == 1.0f && mainForm.getState() == Frame.ICONIFIED) { // Select
                        mainForm.pressEnter();
                    } else if (compID == Component.Identifier.Button._7 && value == 1.0f && mainForm.getState() == Frame.NORMAL) { // Start
                        mainForm.minimizeWindow();
                    } else if (compID == Component.Identifier.Button._7 && value == 1.0f && mainForm.getState() == Frame.ICONIFIED) { // Start
                        mainForm.restoreWindow();
                    }
                }
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
