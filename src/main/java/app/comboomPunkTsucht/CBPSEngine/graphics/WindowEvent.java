package app.comboomPunkTsucht.CBPSEngine.graphics;

/**
 * Sealed interface for all window events.
 * Ensures type-safe event handling with pattern matching.
 */
public sealed interface WindowEvent permits
    WindowCloseEvent, WindowResizeEvent, KeyEvent, MouseEvent {

    long getTimestamp();
}
