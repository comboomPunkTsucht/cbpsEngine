package app.comboomPunkTsucht.CBPSEngine.input;

/**
 * Keyboard input state tracking.
 * Tracks which keys are currently pressed.
 */
public class KeyboardInput {
    private static final int NUM_KEYS = 512; // GLFW key codes
    private final boolean[] keyPressed = new boolean[NUM_KEYS];
    private final boolean[] keyDown = new boolean[NUM_KEYS];

    /**
     * Called at end of frame to reset key state.
     */
    public void updateFrame() {
        // Copy current pressed to down, reset pressed
        System.arraycopy(keyPressed, 0, keyDown, 0, NUM_KEYS);
    }

    /**
     * Notify that key was pressed.
     */
    void onKeyPressed(int keyCode) {
        if (keyCode >= 0 && keyCode < NUM_KEYS) {
            keyPressed[keyCode] = true;
        }
    }

    /**
     * Notify that key was released.
     */
    void onKeyReleased(int keyCode) {
        if (keyCode >= 0 && keyCode < NUM_KEYS) {
            keyPressed[keyCode] = false;
        }
    }

    /**
     * Check if key is currently held down.
     */
    public boolean isKeyDown(int keyCode) {
        return keyCode >= 0 && keyCode < NUM_KEYS && keyPressed[keyCode];
    }

    /**
     * Check if key was pressed this frame (just went from up to down).
     */
    public boolean isKeyPressed(int keyCode) {
        return keyCode >= 0 && keyCode < NUM_KEYS && keyPressed[keyCode] && !keyDown[keyCode];
    }

    /**
     * Check if key was released this frame.
     */
    public boolean isKeyReleased(int keyCode) {
        return keyCode >= 0 && keyCode < NUM_KEYS && !keyPressed[keyCode] && keyDown[keyCode];
    }
}
