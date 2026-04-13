package app.comboomPunkTsucht.CBPSEngine.core;

/**
 * Player input state for this frame.
 */
public class PlayerInput {
    // Movement
    public boolean moveForward = false;
    public boolean moveBackward = false;
    public boolean moveUp = false;
    public boolean moveDown = false;

    // Rotation/Look
    public boolean turnLeft = false;
    public boolean turnRight = false;
    public boolean lookUp = false;
    public boolean lookDown = false;

    // Mouse
    public double mouseDeltaX = 0;
    public double mouseDeltaY = 0;

    // Action
    public boolean primaryAction = false;

    public void reset() {
        moveForward = false;
        moveBackward = false;
        moveUp = false;
        moveDown = false;
        turnLeft = false;
        turnRight = false;
        lookUp = false;
        lookDown = false;
        mouseDeltaX = 0;
        mouseDeltaY = 0;
        primaryAction = false;
    }
}
