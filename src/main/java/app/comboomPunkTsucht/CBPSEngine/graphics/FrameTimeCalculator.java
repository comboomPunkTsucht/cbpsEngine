package app.comboomPunkTsucht.CBPSEngine.graphics;

import app.comboomPunkTsucht.CBPSEngine.core.EngineConstants;

/**
 * Calculates frame time, delta time, and FPS.
 * Supports optional FPS capping via Thread.sleep().
 *
 * Delta-Time Calculation:
 * - Accurately measures time between frames
 * - Clamps value to MIN/MAX range to prevent extreme jumps (lag spikes, etc.)
 *
 * FPS Calculation:
 * - Cumulative average: FPS = frameCount / elapsedTime
 * - Updated incrementally for smooth values
 */
public class FrameTimeCalculator {
    private long lastFrameNanos;
    private double deltaTime;
    private double elapsedTime;
    private long frameCount;

    private final double targetFPS;
    private final boolean fpsCapped;
    private final long frameTimeNanos;

    /**
     * Create a frame timer with target FPS.
     *
     * @param targetFPS Target frames per second (e.g., 60.0)
     * @param fpsCapped If true, will sleep to cap FPS at target
     */
    public FrameTimeCalculator(double targetFPS, boolean fpsCapped) {
        this.targetFPS = targetFPS;
        this.fpsCapped = fpsCapped;
        this.frameTimeNanos = (long) (1_000_000_000.0 / targetFPS); // nano-seconds per frame
        this.lastFrameNanos = System.nanoTime();
        this.deltaTime = 0.0;
        this.elapsedTime = 0.0;
        this.frameCount = 0;
    }

    /**
     * Update frame timings. Call once per frame.
     * Calculates deltaTime, ensures capping if enabled, updates cumulative time.
     */
    public void update() {
        long now = System.nanoTime();
        long deltaTimeNanos = now - lastFrameNanos;

        // Convert to seconds and clamp to sane range
        deltaTime = deltaTimeNanos / 1_000_000_000.0;
        deltaTime = Math.max(EngineConstants.MIN_DELTA_TIME, Math.min(deltaTime, EngineConstants.MAX_DELTA_TIME));

        // Update cumulative time
        elapsedTime += deltaTime;
        frameCount++;

        // Cap FPS if requested
        if (fpsCapped) {
            capFPS(deltaTimeNanos);
        }

        lastFrameNanos = now;
    }

    /**
     * Sleep to maintain target FPS.
     * Only sleeps if frame finished ahead of schedule.
     */
    private void capFPS(long deltaTimeNanos) {
        long sleepTimeNanos = frameTimeNanos - deltaTimeNanos;
        if (sleepTimeNanos > 0) {
            try {
                Thread.sleep(sleepTimeNanos / 1_000_000, (int) (sleepTimeNanos % 1_000_000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Time elapsed since last frame (seconds).
     */
    public double getDeltaTime() {
        return deltaTime;
    }

    /**
     * Total elapsed time since timer started (seconds).
     */
    public double getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Current average FPS (frameCount / elapsedTime).
     */
    public long getFPS() {
        if (elapsedTime <= 0) return 0;
        return Math.round(frameCount / elapsedTime);
    }

    /**
     * Total frames processed since timer started.
     */
    public long getFrameCount() {
        return frameCount;
    }

    /**
     * Target FPS this timer is configured for.
     */
    public double getTargetFPS() {
        return targetFPS;
    }

    /**
     * Reset timer state.
     */
    public void reset() {
        lastFrameNanos = System.nanoTime();
        deltaTime = 0.0;
        elapsedTime = 0.0;
        frameCount = 0;
    }
}
