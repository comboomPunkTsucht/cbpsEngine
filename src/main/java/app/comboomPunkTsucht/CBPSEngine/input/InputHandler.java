package app.comboomPunkTsucht.CBPSEngine.input;

import app.comboomPunkTsucht.CBPSEngine.graphics.*;

/**
 * Input handler: processes window events and updates input state.
 * Bridges Window-Events to KeyboardInput/MouseInput.
 */
public class InputHandler {
    private final KeyboardInput keyboard;
    private final MouseInput mouse;

    public InputHandler() {
        this.keyboard = new KeyboardInput();
        this.mouse = new MouseInput();
    }

    /**
     * Process a window event and update input state.
     */
    public void processEvent(WindowEvent event) {
        if (event instanceof KeyEvent keyEvent) {
            handleKeyEvent(keyEvent);
        } else if (event instanceof MouseEvent mouseEvent) {
            handleMouseEvent(mouseEvent);
        }
    }

    private void handleKeyEvent(KeyEvent event) {
        // GLFW_PRESS = 1, GLFW_RELEASE = 0
        if (event.action() == 1) { // PRESS
            keyboard.onKeyPressed(event.key());
        } else if (event.action() == 0) { // RELEASE
            keyboard.onKeyReleased(event.key());
        }
    }

    private void handleMouseEvent(MouseEvent event) {
        mouse.setPosition(event.x(), event.y());
        
        // If button != -1, it's a button event
        if (event.button() != -1) {
            if (event.action() == 1) { // PRESS
                mouse.onButtonPressed(event.button());
            } else if (event.action() == 0) { // RELEASE
                mouse.onButtonReleased(event.button());
            }
        }
    }

    /**
     * Update input frame state (should be called once per frame).
     */
    public void updateFrame() {
        keyboard.updateFrame();
        mouse.updateFrame();
    }

    /**
     * Get keyboard input state.
     */
    public KeyboardInput getKeyboard() {
        return keyboard;
    }

    /**
     * Get mouse input state.
     */
    public MouseInput getMouse() {
        return mouse;
    }
}
