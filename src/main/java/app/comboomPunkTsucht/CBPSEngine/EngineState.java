package app.comboomPunkTsucht.CBPSEngine;

/**
 * Engine-Zustandsmaschine.
 * Definiert die verschiedenen Zustände der Engine während ihres Lebenszyklus.
 */
public enum EngineState {
    /**
     * Engine wurde nicht initialisiert. {@link CBPSEngine#init(EngineConfig)} wurde nicht aufgerufen.
     */
    UNINITIALIZED,

    /**
     * Engine ist initialisiert und bereit, aber Game-Loop läuft nicht.
     */
    INITIALIZED,

    /**
     * Game-Loop läuft aktuell ({@link CBPSEngine#run(net.cbps.engine.core.GameLoopListener)} wurde aufgerufen).
     */
    RUNNING,

    /**
     * Engine wird heruntergefahren (letzte Frames werden verarbeitet).
     */
    SHUTTING_DOWN,

    /**
     * Engine ist heruntergefahren. Alle Ressourcen sind freigegeben.
     */
    SHUTDOWN;
}
