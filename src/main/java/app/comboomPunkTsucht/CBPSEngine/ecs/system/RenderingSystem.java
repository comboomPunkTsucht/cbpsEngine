package app.comboomPunkTsucht.CBPSEngine.ecs.system;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.artemis.ComponentMapper;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Transform;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Renderer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Rendering system: computes model matrices from transforms.
 * Prepares data for GPU rendering (simplified - no actual GL calls).
 */
public class RenderingSystem extends IteratingSystem {
    private ComponentMapper<Transform> transformMapper;
    private ComponentMapper<Renderer> rendererMapper;

    /**
     * Create rendering system which processes entities with Transform + Renderer.
     */
    public RenderingSystem() {
        super(Aspect.all(Transform.class, Renderer.class));
    }

    @Override
    protected void process(int entityId) {
        Transform transform = transformMapper.get(entityId);
        Renderer renderer = rendererMapper.get(entityId);

        // Skip invisible entities
        if (!renderer.visible) {
            return;
        }

        // In a real renderer, we would:
        // 1. Compute model matrix from transform
        // 2. Bind mesh and material
        // 3. Submit draw call to GPU

        // For now, just compute the matrix (no actual rendering)
        Matrix4f modelMatrix = new Matrix4f()
                .translationRotateScale(
                        transform.position,
                        transform.rotation,
                        transform.scale
                );

        // Could store in a component or pass to renderer
        // modelMatrix would be used for actual rendering
    }
}
