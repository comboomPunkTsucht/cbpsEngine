package app.comboomPunkTsucht.CBPSEngine.core;

/**
 * Central constants for engine configuration.
 * All magic numbers and config values are defined here.
 */
public final class EngineConstants {
    private EngineConstants() {} // Utility class

    // Window Defaults
    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    public static final String DEFAULT_TITLE = "CBPS Engine";

    // Frame Timing
    public static final double TARGET_FPS = 60.0;
    public static final boolean VSYNC_ENABLED = true;
    public static final boolean FPS_CAPPED = true;

    // Delta-Time Clamping (prevent large frame spikes)
    public static final double MAX_DELTA_TIME = 0.1; // 100ms max
    public static final double MIN_DELTA_TIME = 0.001; // 1ms min

    // OpenGL Version
    public static final int GL_VERSION_MAJOR = 4;
    public static final int GL_VERSION_MINOR = 1;

    // Debug/Logging
    public static final boolean DEBUG_MODE = false;
    public static final boolean VERBOSE_LOGGING = false;

    // Event Queue Size
    public static final int EVENT_QUEUE_SIZE = 256;
}
