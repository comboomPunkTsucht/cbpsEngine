package app.comboomPunkTsucht.CBPSEngine.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Player with integrated camera and movement.
 * Supports both 2D and 3D movement modes.
 */
public class Player {
    // Position and rotation
    public Vector3f position = new Vector3f(0, 1.5f, 5);
    public float yaw = 0;      // Left/right rotation (Y axis)
    public float pitch = 0;    // Up/down rotation (X axis) - clamped to -89..89
    public float roll = 0;     // Tilt (Z axis)

    // Movement
    private float moveSpeed = 5.0f;
    private float rotationSpeed = 90.0f; // degrees per second

    // Camera properties
    private float fov = 45.0f;
    private float nearPlane = 0.1f;
    private float farPlane = 1000.0f;

    public Player() {}

    /**
     * Update player in 3D mode (rotation affects movement direction).
     */
    public void update3D(float deltaTime, PlayerInput input) {
        // Calculate forward/right vectors from current rotation
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        float forwardX = (float) (Math.sin(yawRad) * Math.cos(pitchRad));
        float forwardY = (float) Math.sin(pitchRad);
        float forwardZ = (float) (-Math.cos(yawRad) * Math.cos(pitchRad));

        float rightX = (float) Math.cos(yawRad);
        float rightZ = (float) Math.sin(yawRad);

        // Apply movement relative to look direction
        if (input.moveForward) {
            position.x += forwardX * moveSpeed * deltaTime;
            position.y += forwardY * moveSpeed * deltaTime;
            position.z += forwardZ * moveSpeed * deltaTime;
        }
        if (input.moveBackward) {
            position.x -= forwardX * moveSpeed * deltaTime;
            position.y -= forwardY * moveSpeed * deltaTime;
            position.z -= forwardZ * moveSpeed * deltaTime;
        }
        if (input.moveUp) {
            position.y += moveSpeed * deltaTime;
        }
        if (input.moveDown) {
            position.y -= moveSpeed * deltaTime;
        }

        // Apply rotation
        if (input.turnLeft) {
            yaw -= rotationSpeed * deltaTime;
        }
        if (input.turnRight) {
            yaw += rotationSpeed * deltaTime;
        }
        if (input.lookUp) {
            pitch -= rotationSpeed * deltaTime;
        }
        if (input.lookDown) {
            pitch += rotationSpeed * deltaTime;
        }

        // Clamp pitch
        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;

        // Apply mouse look
        if (input.mouseDeltaX != 0 || input.mouseDeltaY != 0) {
            float mouseSense = 0.075f;
            yaw -= input.mouseDeltaX * mouseSense;
            pitch -= input.mouseDeltaY * mouseSense;

            if (pitch > 89.0f) pitch = 89.0f;
            if (pitch < -89.0f) pitch = -89.0f;
        }

        // Normalize rotation angles
        normalizeRotation();
    }

    /**
     * Update player in 2D mode (rotation does NOT affect movement direction).
     */
    public void update2D(float deltaTime, PlayerInput input) {
        // Movement in world space (not affected by rotation)
        if (input.moveForward) {
            position.z -= moveSpeed * deltaTime;
        }
        if (input.moveBackward) {
            position.z += moveSpeed * deltaTime;
        }
        if (input.moveUp) {
            position.y += moveSpeed * deltaTime;
        }
        if (input.moveDown) {
            position.y -= moveSpeed * deltaTime;
        }

        // Rotation (only yaw for 2D)
        if (input.turnLeft) {
            yaw -= rotationSpeed * deltaTime;
        }
        if (input.turnRight) {
            yaw += rotationSpeed * deltaTime;
        }

        // Mouse look (only horizontal in 2D)
        if (input.mouseDeltaX != 0) {
            float mouseSense = 0.075f;
            yaw -= input.mouseDeltaX * mouseSense;
        }

        // Normalize rotation angles
        normalizeRotation();
    }

    /**
     * Normalize rotation angles to proper ranges.
     * Yaw/Roll: 0-359°, Pitch: -89 to 89° (flip protection)
     */
    private void normalizeRotation() {
        // Normalize yaw to 0-359
        yaw = yaw % 360.0f;
        if (yaw < 0) yaw += 360.0f;

        // Normalize roll to 0-359
        roll = roll % 360.0f;
        if (roll < 0) roll += 360.0f;

        // Clamp pitch to prevent camera flip
        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;
    }

    /**
     * Get view matrix (camera transform).
     */
    public Matrix4f getViewMatrix() {
        Matrix4f view = new Matrix4f().identity();
        view.rotateX((float) Math.toRadians(pitch));
        view.rotateY((float) Math.toRadians(yaw));
        view.translate(-position.x, -position.y, -position.z);
        return view;
    }

    /**
     * Get projection matrix.
     */
    public Matrix4f getProjectionMatrix(float aspectRatio) {
        return new Matrix4f().perspective(
            (float) Math.toRadians(fov),
            aspectRatio,
            nearPlane,
            farPlane
        );
    }

    public float getMoveSpeed() { return moveSpeed; }
    public void setMoveSpeed(float speed) { this.moveSpeed = speed; }

    public float getRotationSpeed() { return rotationSpeed; }
    public void setRotationSpeed(float speed) { this.rotationSpeed = speed; }

    public Vector3f getPosition() { return position; }
    public float getYaw() { return yaw; }
    public float getPitch() { return pitch; }
}
