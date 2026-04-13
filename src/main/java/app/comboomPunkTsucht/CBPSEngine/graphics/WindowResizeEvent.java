package app.comboomPunkTsucht.CBPSEngine.graphics;

/**
 * Event fired when window is resized.
 */
public record WindowResizeEvent(long timestamp, int width, int height) implements WindowEvent {
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
