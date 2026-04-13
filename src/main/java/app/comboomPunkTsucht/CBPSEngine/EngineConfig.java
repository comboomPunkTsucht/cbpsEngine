package app.comboomPunkTsucht.CBPSEngine;

/**
 * Engine-Konfiguration als Record (Java 17).
 * Alle Parameter sind immutable und können zur Laufzeit nicht geändert werden
 * (nur über {@link net.cbps.engine.core.GameLoop#setTargetFPS(int)}).
 */
public record EngineConfig(
    String windowTitle,
    int initialWidth,
    int initialHeight,
    boolean startFullscreen,
    int targetFPS,
    boolean vsyncEnabled,
    boolean debugOpenGL,
    float dpiScale
) {
    /**
     * Default-Konfiguration: 1280x720, 60 FPS, kein Fullscreen.
     */
    public static final EngineConfig DEFAULT = new EngineConfig(
        "CBPS Engine",
        1280,
        720,
        false,
        60,
        true,
        false,
        -1f // Auto-detect
    );

    /**
     * Validiert die Konfiguration beim Erstellen.
     * Wirft IllegalArgumentException bei ungültigen Werten.
     */
    public EngineConfig {
        if (initialWidth <= 0 || initialHeight <= 0) {
            throw new IllegalArgumentException("Window dimensions must be > 0");
        }
        if (targetFPS <= 0) {
            throw new IllegalArgumentException("Target FPS must be > 0");
        }
        if (dpiScale > 0 && dpiScale < 0.1f) {
            throw new IllegalArgumentException("DPI scale must be >= 0.1 or -1 (auto-detect)");
        }
        if (windowTitle == null || windowTitle.isEmpty()) {
            throw new IllegalArgumentException("Window title cannot be null or empty");
        }
    }
}
