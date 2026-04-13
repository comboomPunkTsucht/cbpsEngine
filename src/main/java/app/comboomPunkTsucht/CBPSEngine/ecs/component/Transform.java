package app.comboomPunkTsucht.CBPSEngine.ecs.component;

import com.artemis.Component;
import org.joml.Vector3f;
import org.joml.Quaternionf;

/**
 * Transform component for 3D positioning and rotation.
 * Used by all renderable and physical entities.
 */
public class Transform extends Component {
    public final Vector3f position = new Vector3f(0, 0, 0);
    public final Vector3f scale = new Vector3f(1, 1, 1);
    public final Quaternionf rotation = new Quaternionf();

    /**
     * Set position.
     */
    public Transform setPosition(float x, float y, float z) {
        position.set(x, y, z);
        return this;
    }

    /**
     * Set scale.
     */
    public Transform setScale(float x, float y, float z) {
        scale.set(x, y, z);
        return this;
    }

    /**
     * Set rotation (quaternion).
     */
    public Transform setRotation(Quaternionf q) {
        rotation.set(q);
        return this;
    }

    /**
     * Translate by offset.
     */
    public Transform translate(float x, float y, float z) {
        position.add(x, y, z);
        return this;
    }
}
