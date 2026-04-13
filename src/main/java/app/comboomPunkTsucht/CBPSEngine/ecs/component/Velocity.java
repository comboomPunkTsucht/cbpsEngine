package app.comboomPunkTsucht.CBPSEngine.ecs.component;

import com.artemis.Component;
import org.joml.Vector3f;

/**
 * Velocity component for physics and movement.
 * Represents linear velocity in units per second.
 */
public class Velocity extends Component {
    public final Vector3f linear = new Vector3f(0, 0, 0);
    public final Vector3f angular = new Vector3f(0, 0, 0); // Euler angles per second

    /**
     * Set linear velocity.
     */
    public Velocity setLinear(float x, float y, float z) {
        linear.set(x, y, z);
        return this;
    }

    /**
     * Set angular velocity (rotation speed).
     */
    public Velocity setAngular(float x, float y, float z) {
        angular.set(x, y, z);
        return this;
    }
}
