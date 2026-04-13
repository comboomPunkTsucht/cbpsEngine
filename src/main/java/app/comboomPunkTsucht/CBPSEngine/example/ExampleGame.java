package app.comboomPunkTsucht.CBPSEngine.example;

import app.comboomPunkTsucht.CBPSEngine.CBPSEngine;
import app.comboomPunkTsucht.CBPSEngine.ecs.World;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Transform;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Velocity;
import app.comboomPunkTsucht.CBPSEngine.input.InputHandler;
import app.comboomPunkTsucht.CBPSEngine.input.KeyboardInput;
import app.comboomPunkTsucht.CBPSEngine.input.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

/**
 * Example game demonstrating CBPS Engine API.
 *
 * Features:
 * - Creates entities with Transform and Velocity components (ECS)
 * - Reads keyboard input
 * - Renders a simple scene
 * - Demonstrates frame-by-frame game loop
 */
public class ExampleGame {
    private static int cubeEntity;
    private static int frameCount = 0;
    private static long lastPrintTime = System.currentTimeMillis();

    public static void main(String[] args) {
        System.out.println("[ExampleGame] Starting CBPS Engine example...");

        // Initialize engine
        CBPSEngine.initWindow(1280, 720, "CBPS Engine Example");

        // Get ECS world and create a test entity
        World ecsWorld = CBPSEngine.getECSWorld();
        cubeEntity = ecsWorld.createEntity();

        // Add components to entity
        Transform transform = new Transform();
        transform.setPosition(0, 0, -5);
        transform.setScale(1, 1, 1);

        Velocity velocity = new Velocity();
        velocity.setLinear(1, 0.5f, 0);
        velocity.setAngular(45, 30, 60); // Rotate

        com.artemis.Entity entity = ecsWorld.getArtemisWorld().getEntity(cubeEntity);
        entity.edit().add(transform).add(velocity);

        // Set update callback (game logic)
        CBPSEngine.setUpdateCallback(() -> {
            InputHandler input = CBPSEngine.getInputHandler();
            if (input != null) {
                KeyboardInput keyboard = input.getKeyboard();

                // Press ESC to quit
                if (keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                    System.out.println("[ExampleGame] ESC pressed, quitting...");
                    CBPSEngine.shutdown();
                }

                // WASD to move camera (would need a camera entity)
                if (keyboard.isKeyDown(GLFW.GLFW_KEY_W)) {
                    System.out.println("[ExampleGame] W pressed (forward)");
                }
                if (keyboard.isKeyDown(GLFW.GLFW_KEY_A)) {
                    System.out.println("[ExampleGame] A pressed (left)");
                }

                MouseInput mouse = input.getMouse();
                if (mouse.isButtonPressed(0)) { // Left click
                    System.out.println("[ExampleGame] Left click at " + mouse.getX() + ", " + mouse.getY());
                }
            }

            // Print FPS every second
            frameCount++;
            long now = System.currentTimeMillis();
            if (now - lastPrintTime >= 1000) {
                System.out.println("[ExampleGame] FPS: " + CBPSEngine.getFPS()
                        + " | Delta: " + String.format("%.4f", CBPSEngine.getDeltaTime()) + "s");
                lastPrintTime = now;
                frameCount = 0;
            }
        });

        // Set render callback (graphics)
        CBPSEngine.setRenderCallback(() -> {
            // Clear screen (black background)
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // In a real app, here is where we'd render meshes, particles, UI, etc.
            // For now, just a black screen to verify rendering works
        });

        System.out.println("[ExampleGame] Initialized. Controls: WASD=move, ESC=quit, LeftClick=test");
        System.out.println("[ExampleGame] Starting main loop...");

        // Run the game loop (blocking call)
        CBPSEngine.run();

        System.out.println("[ExampleGame] Engine shutdown complete.");
    }
}
