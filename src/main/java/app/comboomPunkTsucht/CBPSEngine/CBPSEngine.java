package app.comboomPunkTsucht.CBPSEngine;

import app.comboomPunkTsucht.CBPSEngine.core.GameLoop;
import app.comboomPunkTsucht.CBPSEngine.ecs.World;
import app.comboomPunkTsucht.CBPSEngine.graphics.Window;
import app.comboomPunkTsucht.CBPSEngine.input.InputHandler;

/**
 * Main facade for the CBPS Engine.
 * Provides static API for window management, game loop, and core functionality.
 */
public final class CBPSEngine {
    private static volatile GameLoop gameLoop;
    private static volatile Window window;
    private static volatile World ecsWorld;
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
        if (ecsWorld == null) {
            ecsWorld = new World();
        }
        if (inputHandler == null) {
            inputHandler = new InputHandler();
        }
    }

    public static boolean shouldClose() {
        return window != null && window.shouldClose();
    }

    public static void setUpdateCallback(Runnable callback) {
        if (gameLoop != null) {
            gameLoop.setUpdateCallback(() -> {
                // Process input events first
                if (inputHandler != null && window != null) {
                    app.comboomPunkTsucht.CBPSEngine.graphics.WindowEvent event;
                    while ((event = window.pollEvent()) != null) {
                        inputHandler.processEvent(event);
                    }
                    inputHandler.updateFrame();
                }

                // Process ECS systems
                if (ecsWorld != null) {
                    ecsWorld.process(getDeltaTime());
                }

                // Then call user logic
                if (callback != null) {
                    callback.run();
                }
            });
        }
    }

    public static void setRenderCallback(Runnable callback) {
        if (gameLoop != null) gameLoop.setRenderCallback(callback);
    }

    public static void run() {
        if (gameLoop != null) {
            gameLoop.start();
            gameLoop.run();
        }
    }

    public static void shutdown() {
        if (gameLoop != null) gameLoop.shutdown();
        if (ecsWorld != null) ecsWorld.cleanup();
        if (window != null) window.cleanup();
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

    public static World getECSWorld() {
        return ecsWorld;
    }

    public static InputHandler getInputHandler() {
        return inputHandler;
    }
}
