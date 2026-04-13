package app.comboomPunkTsucht.CBPSEngine.exception;

/**
 * Runtime exception thrown when engine initialization or operation fails.
 * Used at system boundaries (GLFW, Window creation, GL context setup).
 */
public class EngineException extends RuntimeException {
    public EngineException(String message) {
        super(message);
    }

    public EngineException(String message, Throwable cause) {
        super(message, cause);
    }

    public EngineException(Throwable cause) {
        super(cause);
    }
}
