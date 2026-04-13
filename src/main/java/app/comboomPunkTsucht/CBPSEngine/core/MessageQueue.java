package app.comboomPunkTsucht.CBPSEngine.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread-sichere Event-Queue für asynchrone Kommunikation zwischen verschiedenen Engine-Komponenten.
 *
 * <p>Typische Nutzung: GLFW-Event-Callbacks (auf jedem möglichen Thread) posten Events,
 * Game-Loop (Main-Thread) liest sie sequenziell ab.</p>
 *
 * <p>Verwendet {@link LinkedBlockingQueue} für unbegrenzte Längenz (Memory ist sichtbar).</p>
 *
 * @param <T> Der Event-Typ (z.B. {@link net.cbps.engine.input.InputEvent})
 */
public class MessageQueue<T> {
    private final BlockingQueue<T> queue = new LinkedBlockingQueue<>();

    /**
     * Postet eine Nachricht in die Queue.
     * Thread-sicher, nicht-blockierend (unbegrenzte Queue).
     *
     * @param message Die zu postende Nachricht. Darf nicht null sein.
     */
    public void post(final T message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        queue.offer(message);
    }

    /**
     * Versucht, eine Nachricht aus der Queue zu holen.
     * Blockiert nicht wenn die Queue leer ist.
     *
     * @return Die nächste Nachricht, oder {@code null} wenn die Queue leer ist.
     */
    public T poll() {
        return queue.poll();
    }

    /**
     * Gibt die Anzahl der Nachrichten in der Queue zurück.
     * Hinweis: Dieser Wert kann sich zwischen dem Aufrufen und dem Verwenden ändern.
     *
     * @return Größe der Queue
     */
    public int size() {
        return queue.size();
    }

    /**
     * Prüft, ob die Queue leer ist.
     *
     * @return true wenn die Queue leer ist
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Leert die Queue.
     */
    public void clear() {
        queue.clear();
    }
}
