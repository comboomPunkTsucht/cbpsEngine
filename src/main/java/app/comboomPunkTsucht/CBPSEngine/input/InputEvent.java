package app.comboomPunkTsucht.CBPSEngine.input;

/**
 * Sealed Interface für verschiedene Input-Event-Typen.
 * Java 17 Feature: Nur die unten definierten Record-Typen sind erlaubt.
 *
 * <p>Beispiel-Nutzung in GameLoop:</p>
 * <pre>{@code
 * InputEvent event = ...;
 * switch (event) {
 *     case InputEvent.KeyboardEvent kb -> handleKeyboard(kb);
 *     case InputEvent.MouseEvent mouse -> handleMouse(mouse);
 *     case InputEvent.GamepadEvent gp -> handleGamepad(gp);
 * }
 * }</pre>
 */
public sealed interface InputEvent permits
        InputEvent.KeyboardEvent,
        InputEvent.MouseEvent,
        InputEvent.GamepadEvent {

    /**
     * Tastatur-Event.
     *
     * @param key GLFW-Key-Code (GLFW_KEY_A, etc.)
     * @param scancode Platform-spezifischer Scancode
     * @param action GLFW_PRESS (1), GLFW_RELEASE (0), GLFW_REPEAT (2)
     * @param mods Modifier-Flags (GLFW_MOD_SHIFT, GLFW_MOD_CONTROL, etc.)
     */
    record KeyboardEvent(int key, int scancode, int action, int mods) implements InputEvent {}

    /**
     * Maus-Event (Movement, Button, Scroll).
     *
     * @param x X-Koordinate in logischen Pixeln
     * @param y Y-Koordinate in logischen Pixeln
     * @param button GLFW-Button-Code (-1 für Movement, 0=LEFT, 1=RIGHT, 2=MIDDLE)
     * @param action GLFW_PRESS, GLFW_RELEASE
     * @param mods Modifier-Flags
     */
    record MouseEvent(double x, double y, int button, int action, int mods) implements InputEvent {}

    /**
     * Gamepad/Joystick-Event.
     *
     * @param joystick GLFW-Joystick-ID (0-15)
     * @param action "BUTTON_PRESSED", "AXIS_MOVED", etc.
     * @param value 0.0f-1.0f für Button, -1.0f-1.0f für Axis
     */
    record GamepadEvent(int joystick, String action, float value) implements InputEvent {}
}
