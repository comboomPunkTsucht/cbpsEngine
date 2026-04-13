# ECS Architecture

Entity Component System (ECS) is a different way of structuring game code. This page explains how it works in CBPS Engine.

## Traditional OOP vs ECS

### Traditional Approach (Inheritance)

```java
// ❌ Problems: tight coupling, rigid hierarchy, diamond problem
class GameObject { }
  ├── class Character extends GameObject { name, health, damage }
  │   ├── class Player extends Character { ... }
  │   └── class Enemy extends Character { ... }
  └── class Item extends GameObject { itemType }
```

When you need a character that flies, you create `FlyingCharacter`, and then `FlyingPlayer`. This explodes into a hierarchy.

### ECS Approach (Composition)

```java
// ✅ Flexible: entities are just ID + components
Entity player = world.createEntity();
Entity flying_npc = world.createEntity();

player.add(
    new Transform(),   // Position, rotation, scale
    new Health(100),   // Has 100 HP
    new PlayerInput()  // Responds to keyboard
);

flying_npc.add(
    new Transform(),
    new Health(50),
    new AIBehavior(),  // Has AI instead of input
    new Flight()       // Can fly
);

// Same entity type (ID), different components!
```

**Benefits:**
- Mix and match components freely
- No rigid hierarchies
- Easy to add/remove behaviors at runtime
- Cache-friendly iteration

## Three Parts of ECS

### 1. **Entity** – A Container

```java
// Entity is just an ID (integer)
int entityId = world.createEntity();

// Access via Artemis world
com.artemis.Entity entity = world.getArtemisWorld().getEntity(entityId);

// Add/remove components
entity.edit()
    .add(new Transform())
    .add(new Health(100))
    .add(new Sprite("player.png"));

// Get a component
Health h = entity.getComponent(Health.class);
```

### 2. **Component** – Data Container

Components are pure data. No logic:

```java
// ✅ Good component
public class Health extends Component {
    public int hp = 100;
    public int maxHp = 100;
}

// ✅ Good – Can have helpers, but not core logic
public class Transform extends Component {
    private Vector3f position = new Vector3f();

    public void translate(float x, float y, float z) {
        position.add(x, y, z);
    }
}

// ❌ Bad component – Behavior belongs in System
public class Health extends Component {
    void takeDamage(int dmg) {  // Wrong place!
        hp -= dmg;
    }
}
```

## Built-in Components

### `Transform`
Spatial data (position, rotation, scale):

```java
Transform t = new Transform();
t.setPosition(5, 10, 0);
t.setRotation(45, 0, 0);  // Euler angles
t.setScale(1.5f, 1.5f, 1);

// Get as matrix
Matrix4f model = t.getModelMatrix();
```

### `Velocity`
Physics velocity (linear + angular):

```java
Velocity v = new Velocity();
v.setLinear(1, 0.5f, 0);      // m/s
v.setAngular(10, 0, 0);       // degrees/s (or rad/s)

// Access underlying vectors
Vector3f lin = v.getLinear();
lin.add(0.5f, 0, 0);  // Modify in-place
```

### `Renderer`
Render metadata:

```java
Renderer r = new Renderer();
r.setMeshName("cube");
r.setMaterialName("red");
r.setVisible(true);
r.setRenderLayer(0);
```

### Custom Components

```java
// Define
public class Health extends Component {
    public int hp = 100;
    public int maxHp = 100;
}

public class Inventory extends Component {
    public List<Item> items = new ArrayList<>();
}

// Use
entity.edit()
    .add(new Health())
    .add(new Inventory());

// Access
Health health = entity.getComponent(Health.class);
health.hp -= 10;
```

## 3. **System** – Behavior Processor

Systems iterate over entities with matching components and apply logic:

```java
// Process all entities with Transform + Velocity
public class PhysicsSystem extends IteratingSystem {
    // Matches: entities with BOTH Transform AND Velocity
    PhysicsSystem() {
        super(Aspect.all(Transform.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {
        Transform t = transforms.get(entityId);
        Velocity v = velocities.get(entityId);

        // Apply velocity to position
        t.getPosition().add(
            v.getLinear().x * world.getDelta(),
            v.getLinear().y * world.getDelta(),
            v.getLinear().z * world.getDelta()
        );

        // Apply rotation
        // ... (quaternion math)
    }
}

// Register system
world.setSystem(new PhysicsSystem());

// Each frame, world.process() runs all systems
world.process(deltaTime);
```

## Aspects (Matching Logic)

Aspects define which entities a system operates on:

```java
// All with A, all with B
Aspect.all(Transform.class, Velocity.class)
// Result: entities with both Transform AND Velocity

// Any of A, B, C
Aspect.one(Health.class, Shield.class)
// Result: entities with at least one

// With A but not B
Aspect.all(Transform.class).exclude(Invisible.class)
// Result: entities with Transform that are NOT Invisible

// Complex
Aspect.all(Transform.class)
    .one(Health.class, Shield.class)
    .exclude(Dead.class)
// Entities with Transform, (Health OR Shield), but not Dead
```

## System Types

### IteratingSystem (Most Common)

Process each matching entity individually:

```java
public class DamageSystem extends IteratingSystem {
    DamageSystem() {
        super(Aspect.all(Health.class, DamageTaken.class));
    }

    @Override
    protected void process(int entityId) {
        Health h = healthMapper.get(entityId);
        DamageTaken d = damageMapper.get(entityId);

        h.hp -= d.damage;

        // Remove the damage component after applying
        damageMapper.remove(entityId);
    }
}
```

### VoidEntitySystem

Process all matching entities together:

```java
public class RenderSystem extends VoidEntitySystem {
    RenderSystem() {
        super(Aspect.all(Transform.class, Renderer.class));
    }

    @Override
    protected void processSystem() {
        // Get all matching entities
        IntBag entities = subscription.getEntities();
        for (int i = 0; i < entities.size(); i++) {
            int entityId = entities.get(i);
            // Render this entity
        }
    }
}
```

## Lifecycle Example

```java
// 1. Create world
World world = new World();

// 2. Create entity
int playerId = world.createEntity();

// 3. Add components
com.artemis.Entity player = world.getArtemisWorld().getEntity(playerId);
player.edit()
    .add(new Transform())
    .add(new Velocity())
    .add(new Health())
    .add(new PlayerInput());

// 4. Register systems (done once)
world.getArtemisWorld().setSystem(new PhysicsSystem());
world.getArtemisWorld().setSystem(new PlayerInputSystem());
world.getArtemisWorld().setSystem(new RenderSystem());

// 5. Each frame, process
for (int frame = 0; frame < 1000; frame++) {
    world.process(deltaTime);
    // ~PhysicsSystem updates position based on velocity
    // ~PlayerInputSystem reads input, modifies velocity
    // ~RenderSystem prepares entities for rendering
}

// 6. Cleanup
player.deleteFromWorld();
world.cleanup();
```

## Performance Notes

### Cache Efficiency

Artemis stores components of the same type contiguously in memory. Iterating is very fast:

```
Memory layout:
[Position0, Position1, Position2, ..., Position999]  ← One cache line
[Vel0, Vel1, Vel2, ..., Vel999]                      ← One cache line

IteratingSystem loops through these efficiently!
```

### Avoiding Bottlenecks

1. **Don't create/destroy entities every frame** – Batch operations
2. **Don't over-component** – Avoid 100 components on one entity
3. **Keep systems simple** – Complex logic = slow iteration
4. **Profile before optimizing** – Measure what's slow

## Testing ECS Systems

ECS makes testing easy:

```java
@Test
public void testPhysicsIntegration() {
    // Create minimal world
    World world = new World();

    // Create test entity
    int entity = world.createEntity();
    com.artemis.Entity e = world.getArtemisWorld().getEntity(entity);

    e.edit()
        .add(new Transform().setPosition(0, 0, 0))
        .add(new Velocity().setLinear(1, 0, 0));

    // Process once
    world.process(1.0);  // 1 second

    // Verify
    Transform t = e.getComponent(Transform.class);
    assertEquals(1.0f, t.getPosition().x, 0.001f);
}
```

## Real-World Example

```
Game: Space Shooter
──────────────────

Entity: PlayerShip
├── Transform (pos, rot, scale)
├── Velocity (movement speed)
├── Health (100 hp)
├── Sprite ("player.png")
└── PlayerInput (keyboard handler)

Entity: Enemy1
├── Transform
├── Velocity
├── Health (50 hp)
├── Sprite ("enemy.png")
├── AIBehavior (pathfinding)
└── LootOnDeath (drop items)

Entity: Bullet1
├── Transform
├── Velocity
├── Damage (10 hp)
├── Sprite ("bullet.png")
├── LifeSpan (3 seconds)
└── Collider2D

Systems that run each frame:
├── PhysicsSystem (updates Transform from Velocity)
├── PlayerInputSystem (reads keyboard, modifies player Velocity)
├── AIBehaviorSystem (updates enemy Velocity based on target)
├── CollisionSystem (detects intersections, triggers damage)
├── LifeSpanSystem (removes entities after time expires)
└── RenderSystem (collects entities, submits to GPU)
```

## Further Reading

- [Artemis-ODB Documentation](https://github.com/junkdog/artemis-odb)
- [Your First Game Tutorial](Your-First-Game)
- [API Overview](API-Overview)
