package app.comboomPunkTsucht.CBPSEngine.core;

import app.comboomPunkTsucht.CBPSEngine.exception.EngineException;
import app.comboomPunkTsucht.CBPSEngine.graphics.FrameTimeCalculator;
import app.comboomPunkTsucht.CBPSEngine.graphics.Window;
import app.comboomPunkTsucht.CBPSEngine.graphics.WindowEvent;

/**
 * Main game loop engine.
 *
 * Loop Structure:
 * 1. Update frame timing
 * 2. Poll window events
 * 3. Execute update callback (game logic)
 * 4. Execute render callback (graphics)
 * 5. Swap buffers and present frame
 * 6. Check shutdown conditions
 *
 * Thread-Safety:
 * - isRunning: volatile flag for graceful shutdown signaling
 * - pauseRequested: volatile flag for pause/unpause
 * - Window is thread-local after initialization
 */
public class GameLoop {
    private final Window window;
    private final FrameTimeCalculator frameTimer;

    private volatile boolean isRunning;
    private volatile boolean pauseRequested;

    private Runnable updateCallback;
    private Runnable renderCallback;

    /**
     * Create a game loop for the given window.
     *
     * @param window The GLFW window
     * @param targetFPS Target frames per second
     * @param fpsCapped Whether to cap FPS at target
     */
    public GameLoop(Window window, double targetFPS, boolean fpsCapped) {
        this.window = window;
        this.frameTimer = new FrameTimeCalculator(targetFPS, fpsCapped);
        this.isRunning = false;
        this.pauseRequested = false;
        this.updateCallback = null;
        this.renderCallback = null;
    }

    /**
     * Initialize and start the game loop.
     * Must be called before {@link #run()}.
     */
    public void start() {
        isRunning = true;
        pauseRequested = false;
        frameTimer.reset();
        frameTimer.update(); // Prime the frame timer
    }

    /**
     * Run the main game loop (blocking).
     * Continues until {@link #shutdown()} is called or window closes.
     *
     * Frame Iteration:
     * - setActive() → ensure GL context is current
     * - update() → calculate frame timing
     * - window.update() → poll events
     * - updateCallback() → game logic (if not paused)
     * - renderCallback() → render frame
     * - swapBuffers() → present frame
     */
    public void run() {
        if (!isRunning) {
            throw new EngineException("Game loop must be started with start() before calling run()");
        }

        while (isRunning) {
            // Ensure GL context is current for this thread
            window.setActive();

            // Update frame timing
            frameTimer.update();

            // Poll window events (non-blocking)
            window.update();
            processEvents();

            // Execute update callback (game logic)
            if (!pauseRequested && updateCallback != null) {
                try {
                    updateCallback.run();
                } catch (Exception e) {
                    System.err.println("[GameLoop] Error in update callback: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Execute render callback (graphics)
            if (renderCallback != null) {
                try {
                    renderCallback.run();
                } catch (Exception e) {
                    System.err.println("[GameLoop] Error in render callback: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Present frame
            window.swapBuffers();

            // Check window close condition
            if (window.shouldClose()) {
                shutdown();
            }
        }
    }

    /**
     * Process pending window events (input, resize, etc.).
     * Called every frame to drain the event queue.
     */
    private void processEvents() {
        WindowEvent event;
        while ((event = window.pollEvent()) != null) {
            // Events are queued, could do further processing here
            // For now, just drain the queue (callbacks were already called)
        }
    }

    /**
     * Request shutdown. Loop will exit after current frame.
     * Non-blocking - sets flag for graceful shutdown.
     */
    public void shutdown() {
        isRunning = false;
        if (EngineConstants.VERBOSE_LOGGING) {
            System.out.println("[GameLoop] Shutdown requested");
        }
    }

    /**
     * Pause game logic (updates), keep rendering.
     * Useful for pause screens, menus, debug pause.
     */
    public void pause() {
        pauseRequested = true;
    }

    /**
     * Resume game logic if paused.
     */
    public void resume() {
        pauseRequested = false;
    }

    /**
     * Check if loop is currently running.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Check if updates are paused.
     */
    public boolean isPaused() {
        return pauseRequested;
    }

    /**
     * Register callback for game logic update phase.
     */
    public void setUpdateCallback(Runnable callback) {
        this.updateCallback = callback;
    }

    /**
     * Register callback for render phase.
     */
    public void setRenderCallback(Runnable callback) {
        this.renderCallback = callback;
    }

    /**
     * Get delta time (time in seconds since last frame).
     */
    public double getDeltaTime() {
        return frameTimer.getDeltaTime();
    }

    /**
     * Get current frames per second (average).
     */
    public long getFPS() {
        return frameTimer.getFPS();
    }

    /**
     * Get total elapsed time since loop started.
     */
    public double getElapsedTime() {
        return frameTimer.getElapsedTime();
    }

    /**
     * Get the associated window.
     */
    public Window getWindow() {
        return window;
    }
}
