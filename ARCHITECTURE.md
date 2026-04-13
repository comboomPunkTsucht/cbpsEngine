# CBPS Engine – Architecture Documentation

## Overview

CBPS Engine is a modular, ECS-based game engine written in Java 17+. It provides a clean, Raylib-inspired static API (`CBPSEngine.*`) while maintaining sophisticated internal architecture that decouples concerns and ensures thread safety.

## Design Principles

### 1. Entity Component System (ECS) > Inheritance

Game objects are **not** a class hierarchy. Instead:
- Each game object is an **Entity** (just an ID)
- Behavior is defined by **Components** (data containers)
- Logic runs in **Systems** (behavior processors)

**Example:**
```java
// Create entity
int entityId = world.createEntity();

// Add components
Entity entity = world.getArtemisWorld().getEntity(entityId);
entity.edit()
    .add(new Transform())
    .add(new Velocity());

// PhysicsSystem automatically processes entities with both
```

**Benefits:**
- No diamond problem or rigid hierarchies
- Easy to add new behaviors without modifying existing code
- Cache-friendly iteration over component data

### 2. Facade Pattern for User API

Internal complexity is hidden behind `CBPSEngine.*` static methods:

```java
// User sees:
CBPSEngine.initWindow(1280, 720, "Game");
CBPSEngine.run();

// We hide:
// - GLFW window creation/destruction
// - OpenGL context management
// - Thread-local GL state
// - ECS world orchestration
// - Input event queuing
```

**Structure:**
```
┌─────────────────────────────┐
│  CBPSEngine (Public Facade) │
├─────────────────────────────┤
│  GameLoop                   │
│  GraphicsContext            │
│  InputHandler               │
│  World (ECS)                │
│  Window (GLFW)              │
└─────────────────────────────┘
```

### 3. 3D-First Math

All transforms use:
- `Vector3f position` – point in 3D space
- `Quaternionf rotation` – orientation (avoids gimbal lock)
- `Vector3f scale` – uniform/non-uniform scaling
- `Matrix4f modelMatrix` – computed from above

2D games use an **orthographic camera** pointing down the Z-axis.

### 4. Thread Safety via Message Passing

```
         GLFW Callbacks
              ↓
        MessageQueue
              ↓
       GameLoop (Main)
              ↓
         GL Context
              ↓
       Window::render()
```

- GLFW callbacks post events to `BlockingQueue<WindowEvent>`
- Game loop explicitly polls and processes events
- GL calls never leave the main thread

### 5. No Magic Numbers

All configuration lives in `EngineConstants.java`:
- Window defaults (1280×720)
- Target FPS (60.0)
- OpenGL version (4.1)
- Delta-time clamping (0.001–0.1)
- Event queue size (256)

## Module Structure

```
app.comboomPunkTsucht.CBPSEngine/
│
├── CBPSEngine.java                    # Main façade
├── EngineConstants.java               # Centralized constants
│
├── core/
│   ├── GameLoop.java                  # Game loop orchestration
│   ├── FrameTimeCalculator.java       # Timing, FPS, delta-time
│   └── MessageQueue.java              # Thread-safe event queue
│
├── graphics/
│   ├── Window.java                    # Interface (abstraction)
│   ├── GLFWWindow.java                # GLFW/LWJGL implementation
│   ├── GraphicsContext.java           # OpenGL state management
│   ├── RenderingSystem.java           # ECS system for render
│   └── ViewportConfig.java            # Viewport record
│
├── input/
│   ├── InputHandler.java              # Dispatcher (GLFW → app)
│   ├── InputEvent.java                # Sealed interface (events)
│   │   ├── KeyEvent
│   │   └── MouseEvent
│   ├── KeyboardInput.java             # Keyboard state
│   └── MouseInput.java                # Mouse state
│
├── ecs/
│   ├── World.java                     # Artemis-ODB wrapper
│   │
│   ├── component/
│   │   ├── Transform.java             # Position, rotation, scale
│   │   ├── Velocity.java              # Linear + angular
│   │   └── Renderer.java              # Render metadata
│   │
│   └── system/
│       ├── PhysicsSystem.java         # Integrates velocity → transform
│       └── RenderingSystem.java       # Collects entities to render
│
└── example/
    └── ExampleGame.java               # Demo application
```

## Game Loop Lifecycle

Each frame:

```
1. Input Phase
   ├─ Poll GLFW events from queue
   ├─ Route to InputHandler
   └─ Update keyboard/mouse state

2. Update Phase
   ├─ Call user's updateCallback()
   └─ Process ECS systems (Physics, etc.)

3. Render Phase
   ├─ Call user's renderCallback()
   └─ SwapBuffers

4. Timing Phase
   ├─ Record frame end time
   ├─ Calculate deltaTime
   ├─ Clamp deltaTime to [MIN, MAX]
   ├─ Update FPS counter
   └─ Sleep if FPS capped
```

**Code Flow:**
```java
while (!gameLoop.shouldClose()) {
    // Input
    inputHandler.processEvents(window.pollEvents());
    inputHandler.updateFrame();

    // Update
    userUpdateCallback.run();
    ecsWorld.process(deltaTime);

    // Render
    userRenderCallback.run();
    window.swapBuffers();

    // Timing
    frameTimer.update();
}
```

## Component System

### Built-in Components

#### `Transform`
Stores spatial data:
```java
public class Transform extends Component {
    private Vector3f position = new Vector3f();
    private Quaternionf rotation = new Quaternionf();
    private Vector3f scale = new Vector3f(1);

    // Compute world matrix
    Matrix4f getModelMatrix() { ... }
}
```

#### `Velocity`
Physics data:
```java
public class Velocity extends Component {
    private Vector3f linear = new Vector3f();
    private Vector3f angular = new Vector3f();
}
```

#### `Renderer`
Render metadata:
```java
public class Renderer extends Component {
    String meshName;
    String materialName;
    boolean visible = true;
    int renderLayer = 0;
}
```

### Custom Components

Users extend `com.artemis.Component`:
```java
public class Health extends Component {
    public int hp = 100;
    public int maxHp = 100;
}

// Use in game
entity.edit().add(new Health());
```

## System Architecture

### ECS Systems (Artemis)

Systems process groups of entities with matching components:

```java
public class PhysicsSystem extends IteratingSystem {
    PhysicsSystem() {
        super(Aspect.all(Transform.class, Velocity.class));
    }

    @Override
    protected void process(int entityId) {
        Transform t = transforms.get(entityId);
        Velocity v = velocities.get(entityId);

        // Apply velocity to transform
        t.getPosition().add(
            v.linear.x * deltaTime,
            v.linear.y * deltaTime,
            v.linear.z * deltaTime
        );
    }
}
```

**Aspects** (matching):
- `Aspect.all(A, B)` – entities with both A and B
- `Aspect.one(A, B)` – entities with A or B
- `Aspect.exclude(A)` – entities without A

## Input Handling

### Event Flow

```
GLFW Key Callback
    ↓
MessageQueue.offer(KeyEvent)
    ↓
GameLoop polls queue
    ↓
InputHandler.processEvent(e)
    ├─ KeyEvent → KeyboardInput.onKeyPressed/onKeyReleased
    └─ MouseEvent → MouseInput updates position/buttons
    ↓
InputHandler.updateFrame()
    ├─ KeyboardInput.updateFrame() – sync pressed↔️down
    └─ MouseInput.updateFrame() – save delta position
    ↓
User calls:
├─ keyboard.isKeyDown(GLFW_KEY_W)
├─ keyboard.isKeyPressed(GLFW_KEY_SPACE)
├─ mouse.getX() / mouse.getY()
└─ mouse.isButtonDown(0) // left click
```

### State Tracking

```
pressed: true only 1 frame after key goes down
down:    true while key is held
released: true only 1 frame after key goes up

// Frame update: pressed/released → false, sync down
```

## Graphics (OpenGL)

### Context Management

```java
class GLFWWindow {
    private ThreadLocal<Long> glContext = ThreadLocal.withInitial(() -> {
        // Create GL context on current thread
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();
        return windowHandle;
    });

    void render(Runnable callback) {
        glContext.get();  // Ensure GL available on this thread
        callback.run();
    }
}
```

**Why?** GLFW/OpenGL contexts are thread-local. We enforce main-thread-only rendering.

### Viewport & Projection

```java
record ViewportConfig(
    int x, int y,
    int width, int height,
    float fov,      // Field of view (degrees)
    float zNear,    // Near clip plane
    float zFar      // Far clip plane
) {
    static ViewportConfig forScreen(int w, int h) {
        return new ViewportConfig(0, 0, w, h, 60, 0.1f, 1000);
    }

    float getAspectRatio() {
        return (float) width / (float) height;
    }
}
```

## Timing & Frame Pacing

### FrameTimeCalculator

```java
public class FrameTimeCalculator {
    private long lastTime = System.nanoTime();
    private double deltaTime = 0;
    private long frameCount = 0;
    private double elapsedTime = 0;

    void update() {
        long now = System.nanoTime();
        deltaTime = (now - lastTime) / 1e9; // Convert to seconds

        // Clamp to [MIN, MAX]
        deltaTime = Math.max(MIN_DELTA_TIME, Math.min(MAX_DELTA_TIME, deltaTime));

        elapsedTime += deltaTime;
        frameCount++;
        lastTime = now;
    }

    long getFPS() {
        return deltaTime > 0 ? Math.round(1.0 / deltaTime) : 0;
    }
}
```

**Clamping Rationale:**
- Too large → game physics breaks (skips collisions)
- Too small → unrealistic timestep (lag compensation)

## Configuration

All settings in `EngineConstants.java`:

```java
public final class EngineConstants {
    // Window
    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;

    // Graphics
    public static final double TARGET_FPS = 60.0;
    public static final boolean VSYNC_ENABLED = true;
    public static final int GL_VERSION_MAJOR = 4;
    public static final int GL_VERSION_MINOR = 1;  // macOS limit

    // Physics
    public static final double MAX_DELTA_TIME = 0.1;
    public static final double MIN_DELTA_TIME = 0.001;

    // Debugging
    public static final boolean DEBUG_MODE = false;
    public static final boolean VERBOSE_LOGGING = false;
}
```

## Thread Safety Summary

| Resource | Strategy | Rationale |
|----------|----------|-----------|
| Input Events | BlockingQueue | GLFW calls from main thread, app polls queue |
| shouldClose Flag | AtomicBoolean | Read/write from multiple threads safely |
| Window Dimensions | AtomicInteger | Can be changed by user while loop running |
| GL Context | ThreadLocal + Main Thread Enforcement | GL is not thread-safe |
| ECS World | Single-threaded (main) | Artemis not thread-safe; update callback on main |
| Graphics State | Main Thread Only | OpenGL requires thread affinity |

## Future Extensions

### Mesh Loading
```java
Mesh mesh = AssetManager.loadMesh("models/cube.gltf");
Renderer r = entity.edit().get(Renderer.class);
r.meshName = "cube";
```

### Material System
```java
Material mat = new Material()
    .setAlbedo(Color.RED)
    .setRoughness(0.5f);
renderer.materialName = "red_matte";
```

### Physics Engine Integration
```java
public class RigidBody extends Component {
    Vector3f linearVelocity;
    Vector3f angularVelocity;
    float mass;
    Collider collider;
}
```

### Audio System
```java
AudioManager.playSound("assets/sounds/jump.wav");
AudioManager.playMusic("assets/music/theme.ogg", true);
```

### Networking (For Multiplayer)
```java
Server server = new Server(PORT);
Client client = new Client(HOST, PORT);
server.broadcast(new PlayerMoveEvent(entityId, position));
```

## Performance Considerations

### ECS Iteration Speed
Artemis uses **archetype-based** storage:
- Components of same type are contiguous in memory
- Iterating over `(Transform, Velocity)` is cache-friendly
- ~millions of entities per second on modern hardware

### Avoiding Bottlenecks

1. **Batch GL calls** – Group vertices into single mesh, render once
2. **Cull off-screen** – Use frustum culling before sending to GPU
3. **Delta time sensibly** – Clamp to avoid physics explosions
4. **Poll input once per frame** – Not in every system

## Testing Strategy

### Unit Tests
- `FrameTimeCalculatorTest` – Timing accuracy
- Input handler tests – Event routing
- Component tests – Serialization

### Integration Tests
- ECS world creation/deletion
- System processing
- Full game loop simulation

### Manual Tests
- Graphics (hard to automate)
- Input responsiveness
- Cross-platform (Windows/Mac/Linux)

## Roadmap

- [x] Window & GLFW integration
- [x] Game loop with timing
- [x] ECS (Artemis) integration
- [x] Input handling
- [ ] Mesh loading (glTF/OBJ)
- [ ] Texture system
- [ ] Material system
- [ ] Audio (OpenAL)
- [ ] UI framework
- [ ] Networking (KryoNet)
- [ ] Modding system

---

**For more info**, see:
- `README.md` – Quick start
- `CLAUDE.md` – Development guidelines
- `src/main/java/.../example/ExampleGame.java` – API demo
