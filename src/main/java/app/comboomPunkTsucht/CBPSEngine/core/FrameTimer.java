package app.comboomPunkTsucht.CBPSEngine.core;

import app.comboomPunkTsucht.CBPSEngine.logging.EngineLogger;

/**
 * Verwaltet Frame-Timing: Delta-Time, FPS-Calculation, FPS-Limiting.
 *
 * <p>Verwendet {@link System#nanoTime()} für präzise Zeitmessungen.</p>
 *
 * <p>Der FrameTimer wird von {@link GameLoop} auf jedem Frame aufgerufen:</p>
 * <ol>
 *   <li>{@link #startFrame()} am Anfang des Frames</li>
 *   <li>{@link #getDeltaTime()} wird während Update/Render gelesen</li>
 *   <li>{@link #endFrame()} am Ende des Frames (vor limitFPS)</li>
 *   <li>{@link #limitFPS(int)} optional, wenn FPS-Limiting gewünscht ist</li>
 * </ol>
 */
public class FrameTimer {
    private static final long NANOS_PER_SECOND = 1_000_000_000L;

    private long lastFrameTimeNanos = 0;
    private long currentFrameStartNanos = 0;
    private float deltaTimeSeconds = 0.016f; // Default: ~60 FPS
    private float totalTimeSeconds = 0f;

    // FPS-Calculation
    private int frameCount = 0;
    private long fpsUpdateTimeNanos = 0;
    private float currentFPS = 60f;

    /**
     * Startet einen neuen Frame.
     * Muss am Anfang jedes Frames aufgerufen werden.
     */
    public void startFrame() {
        lastFrameTimeNanos = currentFrameStartNanos;
        currentFrameStartNanos = System.nanoTime();

        // Delta-Time berechnen (beim ersten Frame: Default 0.016s verwenden)
        if (lastFrameTimeNanos != 0) {
            long deltaNanos = currentFrameStartNanos - lastFrameTimeNanos;
            deltaTimeSeconds = deltaNanos / (float) NANOS_PER_SECOND;
        }
    }

    /**
     * Beendet einen Frame.
     * Muss am Ende jedes Frames aufgerufen werden (vor {@link #limitFPS(int)}).
     * Aktualisiert die Gesamt-Zeit.
     */
    public void endFrame() {
        totalTimeSeconds += deltaTimeSeconds;
        frameCount++;

        // FPS alle 100ms neu berechnen
        long now = System.nanoTime();
        long timeSinceLastFpsUpdate = now - fpsUpdateTimeNanos;
        if (timeSinceLastFpsUpdate >= 100_000_000L) { // 100ms
            currentFPS = (frameCount * NANOS_PER_SECOND) / (float) timeSinceLastFpsUpdate;
            frameCount = 0;
            fpsUpdateTimeNanos = now;
        }
    }

    /**
     * Limitiert die Frame-Rate auf die Maximum FPS.
     * Schläft, wenn nötig, um die angezeigte Frame-Rate nicht zu überschreiten.
     *
     * <p>Diese Methode sollte NACH {@link #endFrame()} aufgerufen werden.</p>
     *
     * @param maxFPS Maximum anzustrebende FPS (0 = unbegrenzt)
     */
    public void limitFPS(final int maxFPS) {
        if (maxFPS <= 0) {
            return; // Unbegrenzte FPS
        }

        long targetFrameTimeNanos = NANOS_PER_SECOND / maxFPS;
        long elapsedNanos = System.nanoTime() - currentFrameStartNanos;

        if (elapsedNanos < targetFrameTimeNanos) {
            try {
                long sleepMillis = (targetFrameTimeNanos - elapsedNanos) / 1_000_000L;
                long sleepNanos = (targetFrameTimeNanos - elapsedNanos) % 1_000_000L;

                if (sleepMillis > 0) {
                    Thread.sleep(sleepMillis, (int) sleepNanos);
                } else if (sleepNanos > 0) {
                    // Busy-wait, wenn sleep zu kurz wäre
                    long spinStart = System.nanoTime();
                    while (System.nanoTime() - spinStart < sleepNanos) {
                        // Spin
                    }
                }
            } catch (InterruptedException e) {
                EngineLogger.warn("FPS limiting sleep interrupted", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Gibt die Delta-Time des aktuellen Frames in Sekunden zurück.
     *
     * @return Delta-Time in Sekunden (z.B. 0.016f für 60 FPS)
     */
    public float getDeltaTime() {
        return deltaTimeSeconds;
    }

    /**
     * Gibt die aktuelle Frame-Rate zurück.
     * Wird alle 100ms neu berechnet.
     *
     * @return Ungefähre aktuelle FPS
     */
    public float getFPS() {
        return currentFPS;
    }

    /**
     * Gibt die Gesamt-Zeit seit Engine-Start in Sekunden zurück.
     *
     * @return Gesamtzeit in Sekunden
     */
    public float getTotalTime() {
        return totalTimeSeconds;
    }

    /**
     * Setzt den Timer zurück (z.B. für Testing).
     */
    public void reset() {
        lastFrameTimeNanos = 0;
        currentFrameStartNanos = System.nanoTime();
        deltaTimeSeconds = 0.016f;
        totalTimeSeconds = 0f;
        frameCount = 0;
        fpsUpdateTimeNanos = currentFrameStartNanos;
        currentFPS = 60f;
    }
}
