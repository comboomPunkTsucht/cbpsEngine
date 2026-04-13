package app.comboomPunkTsucht.CBPSEngine.ecs.system;

import com.artemis.Aspect;
import com.artemis.systems.IteratingSystem;
import com.artemis.ComponentMapper;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Transform;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Velocity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Physics system: applies velocity to transform each frame.
 * Handles linear and angular movement.
 */
public class PhysicsSystem extends IteratingSystem {
    private ComponentMapper<Transform> transformMapper;
    private ComponentMapper<Velocity> velocityMapper;

    private static final Vector3f tempAxis = new Vector3f();

    /**
     * Create physics system which processes entities with Transform + Velocity.
     */
    public PhysicsSystem() {
        super(Aspect.all(Transform.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {
        Transform transform = transformMapper.get(entityId);
        Velocity velocity = velocityMapper.get(entityId);
        float deltaTime = world.getDelta();

        // Apply linear velocity
        transform.position.add(
                velocity.linear.x * deltaTime,
                velocity.linear.y * deltaTime,
                velocity.linear.z * deltaTime
        );

        // Apply angular velocity (rotate by Euler angles)
        if (velocity.angular.lengthSquared() > 0) {
            float rotX = (float) Math.toRadians(velocity.angular.x * deltaTime);
            float rotY = (float) Math.toRadians(velocity.angular.y * deltaTime);
            float rotZ = (float) Math.toRadians(velocity.angular.z * deltaTime);

            Quaternionf deltaRot = new Quaternionf()
                    .rotateXYZ(rotX, rotY, rotZ);
            transform.rotation.mul(deltaRot);
        }
    }
}
