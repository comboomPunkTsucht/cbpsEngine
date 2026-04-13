package app.comboomPunkTsucht.CBPSEngine.core;

import app.comboomPunkTsucht.CBPSEngine.exception.EngineException;
import app.comboomPunkTsucht.CBPSEngine.graphics.FrameTimeCalculator;
import app.comboomPunkTsucht.CBPSEngine.graphics.Window;

/**
 * Main game loop engine with Fixed Time Step (Accumulator Pattern).
 *
 * Architecture:
 * - Variable rendering: Frame rate adapts to hardware
 * - Fixed physics: ECS/physics runs at fixed 60 Hz timestep
 * - Interpolation: Render callback receives 'alpha' for smooth animation
 *
 * Loop Structure (per frame):
 * 1. Poll window events (non-blocking)
 * 2. Accumulate deltaTime
 * 3. While accumulator >= fixedTimeStep:
 *    - Execute fixedUpdateCallback(timeStep) → ECS, physics
 *    - Drain accumulator
 * 4. Calculate interpolation factor alpha = accumulator / fixedTimeStep
 * 5. Execute renderCallback(alpha) → graphics with interpolation
 * 6. Swap buffers
 *
 * Thread-Safety:
 * - isRunning: volatile flag for graceful shutdown
 * - pauseRequested: volatile flag for pause/unpause
 * - Window is thread-local after initialization
 */
public class GameLoop {
    private final Window window;
    private final FrameTimeCalculator frameTimer;

    private volatile boolean isRunning;
    private volatile boolean pauseRequested;

    private final double fixedTimeStep;     // 1.0 / 60.0 = 0.01667 seconds
    private double accumulator;              // Accumulates deltaTime

    private FixedUpdateCallback fixedUpdateCallback;
    private RenderCallback renderCallback;

    /**
     * Callback interface for fixed timestep updates (physics, ECS).
     */
    @FunctionalInterface
    public interface FixedUpdateCallback {
        void update(float timeStep);
    }

    /**
     * Callback interface for rendering with interpolation factor.
     */
    @FunctionalInterface
    public interface RenderCallback {
        void render(float alpha);
    }

    /**
     * Create a game loop for the given window.
     *
     * @param window The GLFW window
     * @param targetFPS Target frames per second for rendering (uncapped variable rate)
     * @param fpsCapped Whether to cap output FPS
     */
    public GameLoop(Window window, double targetFPS, boolean fpsCapped) {
        this.window = window;
        this.frameTimer = new FrameTimeCalculator(targetFPS, fpsCapped);
        this.fixedTimeStep = 1.0 / 60.0;  // 60 Hz fixed timestep
        this.accumulator = 0.0;
        this.isRunning = false;
        this.pauseRequested = false;
        this.fixedUpdateCallback = null;
        this.renderCallback = null;
    }

    /**
     * Initialize and start the game loop.
     * Must be called before {@link #run()}.
     */
    public void start() {
        isRunning = true;
        pauseRequested = false;
        accumulator = 0.0;
        frameTimer.reset();
        frameTimer.update(); // Prime the frame timer
    }

    /**
     * Run the main game loop (blocking) with fixed time step accumulator.
     *
     * Continues until {@link #shutdown()} is called or window closes.
     * Decouples variable rendering from fixed physics simulation.
     */
    public void run() {
        if (!isRunning) {
            throw new EngineException("Game loop must be started with start() before calling run()");
        }

        while (isRunning) {
            // Ensure GL context is current for this thread
            window.setActive();

            // Update frame timing (variable deltaTime)
            frameTimer.update();
            double deltaTime = Math.min(frameTimer.getDeltaTime(), 0.25); // Max 250ms per frame

            // Accumulate time for fixed timestep
            accumulator += deltaTime;

            // Poll window events (non-blocking) → handled by CBPSEngine
            window.update();

            // Fixed timestep loop: run physics/ECS at fixed rate
            while (accumulator >= fixedTimeStep && !pauseRequested) {
                if (fixedUpdateCallback != null) {
                    try {
                        fixedUpdateCallback.update((float) fixedTimeStep);
                    } catch (Exception e) {
                        System.err.println("[GameLoop] Error in fixed update callback: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                accumulator -= fixedTimeStep;
            }

            // Calculate interpolation factor for smooth rendering
            float alpha = (float) (accumulator / fixedTimeStep);

            // Render frame with interpolation
            if (renderCallback != null) {
                try {
                    renderCallback.render(alpha);
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
     * Pause game logic (fixed updates), keep rendering.
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
     * Register callback for fixed timestep updates (physics, ECS).
     * Called at fixed 60 Hz regardless of frame rate.
     */
    public void setFixedUpdateCallback(FixedUpdateCallback callback) {
        this.fixedUpdateCallback = callback;
    }

    /**
     * Register callback for render phase with interpolation factor.
     * Alpha: 0.0 = start of fixed frame, 1.0 = end of fixed frame
     */
    public void setRenderCallback(RenderCallback callback) {
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
     * Get the fixed timestep used for physics/ECS (60 Hz).
     */
    public double getFixedTimeStep() {
        return fixedTimeStep;
    }

    /**
     * Get the associated window.
     */
    public Window getWindow() {
        return window;
    }
}
