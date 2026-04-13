package app.comboomPunkTsucht.CBPSEngine.test;

import app.comboomPunkTsucht.CBPSEngine.CBPSEngine;
import org.lwjgl.opengl.GL11;

/**
 * Simple test application to verify window creation and main loop functionality.
 *
 * Expected Behavior:
 * - Window opens with black background
 * - FPS counter prints to console (should reach ~60 FPS)
 * - Window can be resized
 * - Alt+F4 or clicking close button exits
 * - No crashes or GL errors
 */
public class SimpleWindowTest {
    public static void main(String[] args) {
        System.out.println("[Test] Starting SimpleWindowTest...");

        // Initialize engine with a window
        CBPSEngine.initWindow(1280, 720, "SimpleWindowTest - CBPS Engine");

        // Set update callback (game logic)
        CBPSEngine.setUpdateCallback(() -> {
            // Game logic runs here ~60 times per second
        });

        // Set render callback (graphics)
        CBPSEngine.setRenderCallback(() -> {
            // Clear screen to black
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        });

        System.out.println("[Test] Window initialized. Starting main loop...");
        System.out.println("[Test] Close the window to exit.");

        // Start the game loop (blocking call)
        CBPSEngine.run();

        System.out.println("[Test] Window closed. Shutting down...");
        CBPSEngine.shutdown();

        System.out.println("[Test] Test completed successfully!");
    }
}
