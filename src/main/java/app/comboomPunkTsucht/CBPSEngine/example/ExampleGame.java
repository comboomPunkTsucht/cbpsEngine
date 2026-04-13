package app.comboomPunkTsucht.CBPSEngine.example;

import app.comboomPunkTsucht.CBPSEngine.CBPSEngine;
import app.comboomPunkTsucht.CBPSEngine.core.Player;
import app.comboomPunkTsucht.CBPSEngine.core.PlayerInput;
import app.comboomPunkTsucht.CBPSEngine.graphics.SimpleShaderRenderer;
import app.comboomPunkTsucht.CBPSEngine.input.InputHandler;
import app.comboomPunkTsucht.CBPSEngine.input.KeyboardInput;
import app.comboomPunkTsucht.CBPSEngine.input.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.joml.Matrix4f;

/**
 * Example game with OOP-based player and camera (no ECS).
 * Demonstrates 3D movement with rotation-affected direction.
 *
 * Controls:
 * - W/S or UP/DOWN: Move forward/backward
 * - SPACE/SHIFT: Move up/down
 * - A/D or LEFT/RIGHT: Turn left/right
 * - Mouse: Look around (pitch/yaw)
 * - ESC: Quit
 */
public class ExampleGame {
    private static Player player;
    private static PlayerInput input = new PlayerInput();

    // Cube state
    private static float cubeRotX = 0, cubeRotY = 0, cubeRotZ = 0;
    private static float cubeRotSpeedX = 45, cubeRotSpeedY = 30, cubeRotSpeedZ = 60;

    // Stats
    private static int frameCount = 0;
    private static long lastPrintTime = System.currentTimeMillis();

    // Mouse
    private static long glfwWindow;

    public static void main(String[] args) {
        System.out.println("[ExampleGame] Starting (OOP version, no ECS)...");

        // Initialize engine
        CBPSEngine.initWindow(1280, 720, "CBPS Engine - OOP Example");

        // Initialize renderer
        SimpleShaderRenderer.initialize();

        // Initialize player
        player = new Player();
        player.setMoveSpeed(5.0f);
        player.setRotationSpeed(90.0f);

        // Lock mouse
        glfwWindow = CBPSEngine.getWindow().getGlfwWindowHandle();
        GLFW.glfwSetInputMode(glfwWindow, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        System.out.println("[ExampleGame] Initialized. Controls: W/S or UP/DOWN=move, SPACE/SHIFT=up/down, A/D or LEFT/RIGHT=turn, Mouse=look, ESC=quit");
        System.out.println("[ExampleGame] Starting main loop...");

        // Set fixed update callback
        CBPSEngine.setFixedUpdateCallback(timeStep -> {
            // Update cube rotation
            cubeRotX = (cubeRotX + cubeRotSpeedX * timeStep) % 360;
            cubeRotY = (cubeRotY + cubeRotSpeedY * timeStep) % 360;
            cubeRotZ = (cubeRotZ + cubeRotSpeedZ * timeStep) % 360;

            // Get input
            InputHandler inputHandler = CBPSEngine.getInputHandler();
            if (inputHandler != null) {
                input.reset();
                KeyboardInput keyboard = inputHandler.getKeyboard();
                MouseInput mouse = inputHandler.getMouse();

                // ESC to quit
                if (keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                    System.out.println("[ExampleGame] ESC pressed, quitting...");
                    CBPSEngine.shutdown();
                    return;
                }

                // Movement input
                input.moveForward = keyboard.isKeyDown(GLFW.GLFW_KEY_W) || keyboard.isKeyDown(GLFW.GLFW_KEY_UP);
                input.moveBackward = keyboard.isKeyDown(GLFW.GLFW_KEY_S) || keyboard.isKeyDown(GLFW.GLFW_KEY_DOWN);
                input.moveUp = keyboard.isKeyDown(GLFW.GLFW_KEY_SPACE);
                input.moveDown = keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT) || keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT_SHIFT);

                // Turn input
                input.turnLeft = keyboard.isKeyDown(GLFW.GLFW_KEY_A) || keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT);
                input.turnRight = keyboard.isKeyDown(GLFW.GLFW_KEY_D) || keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT);

                // Mouse input
                double mouseX = mouse.getX();
                double mouseY = mouse.getY();
                double centerX = 640.0;
                double centerY = 360.0;

                // Calculate delta relative to center (prevents drift)
                input.mouseDeltaX = mouseX - centerX;
                input.mouseDeltaY = mouseY - centerY;

                // Ignore small deltas to prevent drift from cursor not reaching exact center
                if (Math.abs(input.mouseDeltaX) < 5.0) input.mouseDeltaX = 0;
                if (Math.abs(input.mouseDeltaY) < 5.0) input.mouseDeltaY = 0;

                // Reset mouse to center
                GLFW.glfwSetCursorPos(glfwWindow, centerX, centerY);

                // Click input
                input.primaryAction = mouse.isButtonPressed(0);
            }

            // Update player (3D mode)
            player.update3D((float) timeStep, input);

            // Print FPS
            frameCount++;
            long now = System.currentTimeMillis();
            if (now - lastPrintTime >= 1000) {
                System.out.println("[ExampleGame] FPS: " + CBPSEngine.getFPS()
                        + " | Pos: (" + String.format("x:%.1f", player.position.x)
                        + ", " + String.format("y:%.1f", player.position.y)
                        + ", " + String.format("z:%.1f", player.position.z) +" ( " + String.format("yaw:%.1f", player.yaw)+ ", " + String.format("pitch:%.1f", player.pitch)+ ", " + String.format("roll:%.1f", player.roll)+ " ))");
                lastPrintTime = now;
                frameCount = 0;
            }
        });

        // Set render callback
        CBPSEngine.setRenderCallback(alpha -> {
            // Clear
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            GL11.glEnable(GL11.GL_DEPTH_TEST);

            // Get matrices
            float aspectRatio = 1280.0f / 720.0f;
            Matrix4f projection = player.getProjectionMatrix(aspectRatio);
            Matrix4f view = player.getViewMatrix();

            // Model matrix for cube
            Matrix4f model = new Matrix4f().identity();
            model.rotateX((float) Math.toRadians(cubeRotX));
            model.rotateY((float) Math.toRadians(cubeRotY));
            model.rotateZ((float) Math.toRadians(cubeRotZ));

            // Draw cube
            SimpleShaderRenderer.drawCube(model, view, projection);
        });

        // Run
        CBPSEngine.run();

        System.out.println("[ExampleGame] Shutdown complete.");
        SimpleShaderRenderer.cleanup();
    }
}
