package app.comboomPunkTsucht.CBPSEngine.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Zentraler Logger für die CBPS Engine.
 * Wrappet Apache Log4j2 und bietet eine einfache Schnittstelle für
 * die Engine-Komponenten sowie später für In-Game-Chat-Integration.
 *
 * <p>Alle Logs werden in {@code logs/engine.log} geschrieben (asynchron via Log4j2)</p>
 */
public final class EngineLogger {
    private static final Logger LOGGER = LogManager.getLogger("net.cbps.engine");

    private EngineLogger() {
        // Static utility class
    }

    /**
     * Info-Level Log
     */
    public static void info(final String message) {
        LOGGER.info(message);
    }

    /**
     * Info-Level Log mit Formatierung
     */
    public static void info(final String message, final Object... args) {
        LOGGER.info(message, args);
    }

    /**
     * Debug-Level Log
     */
    public static void debug(final String message) {
        LOGGER.debug(message);
    }

    /**
     * Debug-Level Log mit Formatierung
     */
    public static void debug(final String message, final Object... args) {
        LOGGER.debug(message, args);
    }

    /**
     * Warn-Level Log
     */
    public static void warn(final String message) {
        LOGGER.warn(message);
    }

    /**
     * Warn-Level Log mit Exception
     */
    public static void warn(final String message, final Throwable throwable) {
        LOGGER.warn(message, throwable);
    }

    /**
     * Error-Level Log
     */
    public static void error(final String message) {
        LOGGER.error(message);
    }

    /**
     * Error-Level Log mit Exception
     */
    public static void error(final String message, final Throwable throwable) {
        LOGGER.error(message, throwable);
    }

    /**
     * Fatal-Level Log
     */
    public static void fatal(final String message) {
        LOGGER.fatal(message);
    }

    /**
     * Fatal-Level Log mit Exception
     */
    public static void fatal(final String message, final Throwable throwable) {
        LOGGER.fatal(message, throwable);
    }

    /**
     * Flush alle gepufferten Logs (wird beim Shutdown aufgerufen).
     * Diese Methode ist blockierend.
     */
    public static void flush() {
        LogManager.shutdown();
    }
}
