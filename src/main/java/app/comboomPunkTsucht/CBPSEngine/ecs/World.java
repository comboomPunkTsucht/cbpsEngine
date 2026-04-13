package app.comboomPunkTsucht.CBPSEngine.ecs;

/**
 * ECS World wrapper for Artemis-ODB.
 * Manages entities, components, and systems.
 *
 * Features:
 * - Entity creation/destruction
 * - Component attachment/removal
 * - System processing and callbacks
 */
public class World {
    private final com.artemis.World internalWorld;

    /**
     * Create a new ECS world.
     */
    public World() {
        this.internalWorld = new com.artemis.World();
    }

    /**
     * Process all systems (runs each frame).
     * Delta time in seconds.
     */
    public void process(float deltaTime) {
        internalWorld.setDelta(deltaTime);
        internalWorld.process();
    }

    /**
     * Create a new entity.
     *
     * @return Entity ID
     */
    public int createEntity() {
        return internalWorld.create();
    }

    /**
     * Delete an entity and all its components.
     */
    public void deleteEntity(int entityId) {
        internalWorld.delete(entityId);
    }

    /**
     * Get the underlying Artemis world for advanced usage.
     */
    public com.artemis.World getArtemisWorld() {
        return internalWorld;
    }

    /**
     * Cleanup (should be called at shutdown).
     */
    public void cleanup() {
        internalWorld.dispose();
    }
}
