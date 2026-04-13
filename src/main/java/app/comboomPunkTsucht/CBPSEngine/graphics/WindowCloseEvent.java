package app.comboomPunkTsucht.CBPSEngine.graphics;

/**
 * Event fired when window close button is pressed.
 */
public record WindowCloseEvent(long timestamp) implements WindowEvent {
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
