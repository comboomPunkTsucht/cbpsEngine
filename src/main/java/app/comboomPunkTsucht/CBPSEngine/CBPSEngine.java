package app.comboomPunkTsucht.CBPSEngine;

import app.comboomPunkTsucht.CBPSEngine.core.GameLoop;
import app.comboomPunkTsucht.CBPSEngine.graphics.Window;
import app.comboomPunkTsucht.CBPSEngine.input.InputHandler;

/**
 * Main facade for the CBPS Engine.
 * Provides static API for window management, game loop, and core functionality.
 *
 * Architecture:
 * - Window: GLFW context management
 * - GameLoop: Fixed timestep 60 Hz for physics, variable rendering
 * - InputHandler: Event queue polling and state tracking
 *
 * Event Flow:
 * 1. CBPSEngine.run() starts game loop
 * 2. Each frame: window.update() → pollEvent() → InputHandler.processEvent()
 * 3. Fixed 60 Hz: fixedUpdateCallback() → game logic
 * 4. Variable FPS: renderCallback(alpha) → graphics with interpolation
 */
public final class CBPSEngine {
    private static volatile GameLoop gameLoop;
    private static volatile Window window;
    private static volatile InputHandler inputHandler;
    private static final double DEFAULT_TARGET_FPS = 60.0;
    private static final boolean DEFAULT_FPS_CAPPED = true;

    private CBPSEngine() {}

    public static void initWindow(int width, int height, String title) {
        if (window == null) {
            window = Window.create(width, height, title);
        }
        if (gameLoop == null) {
            gameLoop = new GameLoop(window, DEFAULT_TARGET_FPS, DEFAULT_FPS_CAPPED);
        }
        if (inputHandler == null) {
            inputHandler = new InputHandler();
            // Set GLFW window handle for mouse polling
            if (window != null) {
                inputHandler.setGlfwWindow(window.getGlfwWindowHandle());
            }
        }
    }

    public static boolean shouldClose() {
        return window != null && window.shouldClose();
    }

    /**
     * Set fixed update callback for game logic (runs at fixed 60 Hz).
     * Input handling is automatic (happens before this callback).
     */
    public static void setFixedUpdateCallback(GameLoop.FixedUpdateCallback callback) {
        if (gameLoop != null) {
            gameLoop.setFixedUpdateCallback(timeStep -> {
                // Always process input events first (before user logic)
                if (inputHandler != null && window != null) {
                    app.comboomPunkTsucht.CBPSEngine.graphics.WindowEvent event;
                    while ((event = window.pollEvent()) != null) {
                        inputHandler.processEvent(event);
                    }
                    inputHandler.updateFrame();
                }

                // Call user fixed update logic
                if (callback != null) {
                    callback.update(timeStep);
                }
            });
        }
    }

    /**
     * Set render callback with interpolation factor (variable FPS).
     * Alpha: 0.0 = start of fixed frame, 1.0 = end of fixed frame
     */
    public static void setRenderCallback(GameLoop.RenderCallback callback) {
        if (gameLoop != null) {
            gameLoop.setRenderCallback(callback);
        }
    }

    public static void run() {
        if (gameLoop != null) {
            gameLoop.start();
            gameLoop.run();
        }
    }

    /**
     * Shutdown engine and clean up all resources.
     * Sets all static variables to null for re-initialization.
     */
    public static void shutdown() {
        if (gameLoop != null) gameLoop.shutdown();
        if (window != null) window.cleanup();

        // Clean shutdown: set all statics to null
        gameLoop = null;
        window = null;
        inputHandler = null;
    }

    /**
     * Clear background with RGBA color.
     * Wrapper for glClearColor + glClear.
     */
    public static void clearBackground(float r, float g, float b, float a) {
        if (window != null) {
            window.clearBackground(r, g, b, a);
        }
    }

    public static float getDeltaTime() {
        return gameLoop != null ? (float) gameLoop.getDeltaTime() : 0f;
    }

    public static float getFPS() {
        return gameLoop != null ? (float) gameLoop.getFPS() : 0f;
    }

    public static double getElapsedTime() {
        return gameLoop != null ? gameLoop.getElapsedTime() : 0.0;
    }

    public static double getFixedTimeStep() {
        return gameLoop != null ? gameLoop.getFixedTimeStep() : 0.0;
    }

    public static void beginFrame() {
        if (gameLoop != null) {
            gameLoop.start();
        }
    }

    public static void endFrame() {
        if (gameLoop != null && window != null) {
            window.swapBuffers();
        }
    }

    public static Window getWindow() {
        return window;
    }

    public static InputHandler getInputHandler() {
        return inputHandler;
    }
}

