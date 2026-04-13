package app.comboomPunkTsucht.CBPSEngine.graphics;

/**
 * Viewport configuration for rendering.
 * Defines the rendering area and perspective.
 */
public record ViewportConfig(
        int x,
        int y,
        int width,
        int height,
        float fov,              // Field of view (degrees)
        float zNear,            // Near clipping plane
        float zFar              // Far clipping plane
) {
    /**
     * Create a default viewport for the given screen dimensions.
     */
    public static ViewportConfig forScreen(int screenWidth, int screenHeight) {
        return new ViewportConfig(
                0, 0,
                screenWidth, screenHeight,
                60.0f,    // Default 60-degree FOV
                0.1f,     // Near clip
                1000.0f   // Far clip
        );
    }

    /**
     * Get aspect ratio.
     */
    public float getAspectRatio() {
        return (float) width / (float) height;
    }
}
