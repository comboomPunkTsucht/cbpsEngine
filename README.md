# CBPS Engine

A **lightweight, modular Java game engine** built on LWJGL, GLFW, and an Entity Component System (ECS) via Artemis-ODB. Designed for 3D games with a simple, Raylib-inspired API.

![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-25+-orange.svg)
![Version](https://img.shields.io/badge/Version-0.1.0--alpha-red.svg)

## Features

- **Entity Component System (ECS)** – Modular game object architecture via Artemis-ODB
- **3D-First Rendering** – OpenGL 4.1+ (macOS compatible) with JOML math library
- **Input Handling** – Thread-safe keyboard & mouse input with event queues
- **Frame Timing** – Configurable FPS capping with delta-time clamping
- **Facade API** – Simple static `CBPSEngine.*` interface (Raylib-inspired)
- **Cross-Platform** – Runs on Windows, macOS (Intel/M-series), Linux

## Quick Start

### Requirements

- **Java 25+**
- **Maven 3.8+**
- **macOS users**: Native builds work on Intel & M-series Macs; OpenGL 4.1 is the maximum supported version

### Building

```bash
git clone https://github.com/comboom-sucht/cbps-engine.git
cd cbps-engine
mvn clean package
```

This creates two JARs:
- `target/CBPSEngine-0.1.0-alpha.jar` – JAR with dependency references
- `target/CBPSEngine-full.jar` – Fat JAR (all dependencies bundled)

### Running the Example Game

```bash
# macOS (requires -XstartOnFirstThread for GLFW)
java -XstartOnFirstThread -jar target/CBPSEngine-full.jar

# Linux / Windows
java -jar target/CBPSEngine-full.jar
```

**Controls:**
- `W` / `A` / `S` / `D` – Move
- `ESC` – Quit
- `Left Click` – Test input

## Architecture

The engine follows these core principles:

1. **ECS over Inheritance** – Entities are containers; behavior is defined by Components + Systems
2. **Facade Pattern** – Users see clean `CBPSEngine.*` API; internals are hidden
3. **3D-First Math** – All transforms use Vector3f, Quaternionf, and Matrix4f (JOML)
4. **Thread Safety** – Input events go through BlockingQueue; GL calls stay on main thread
5. **No Magic Numbers** – All constants in `EngineConstants.java`

### Modules

```
src/main/java/app/comboomPunkTsucht/CBPSEngine/
├── CBPSEngine.java           # Main facade (static API)
├── EngineConstants.java      # Hard-coded config values
│
├── core/
│   ├── GameLoop.java         # Game loop orchestration
│   ├── FrameTimeCalculator.java  # Timing & FPS management
│   └── MessageQueue.java      # Thread-safe event queue
│
├── graphics/
│   ├── Window.java           # Window interface
│   ├── GLFWWindow.java       # GLFW/LWJGL implementation
│   ├── GraphicsContext.java  # OpenGL management
│   └── RenderingSystem.java  # Renderer entity system
│
├── input/
│   ├── InputHandler.java     # Input dispatcher
│   ├── KeyboardInput.java    # Keyboard state tracking
│   └── MouseInput.java       # Mouse state tracking
│
└── ecs/
    ├── World.java            # ECS world wrapper (Artemis)
    ├── component/
    │   ├── Transform.java    # Position, scale, rotation
    │   ├── Velocity.java     # Linear & angular velocity
    │   └── Renderer.java     # Render component
    └── system/
        ├── PhysicsSystem.java   # Physics integration
        └── RenderingSystem.java # Render system
```

## Usage Example

```java
import app.comboomPunkTsucht.CBPSEngine.CBPSEngine;
import app.comboomPunkTsucht.CBPSEngine.ecs.World;
import org.lwjgl.glfw.GLFW;

public class MyGame {
    public static void main(String[] args) {
        // Initialize window (1280×720)
        CBPSEngine.initWindow(1280, 720, "My Game");

        // Get ECS world and create an entity
        World world = CBPSEngine.getECSWorld();
        int myEntity = world.createEntity();

        // Add components...
        com.artemis.Entity entity = world.getArtemisWorld().getEntity(myEntity);
        entity.edit()
            .add(new Transform().setPosition(0, 0, -5))
            .add(new Velocity().setLinear(1, 0.5f, 0));

        // Set game logic callback
        CBPSEngine.setUpdateCallback(() -> {
            if (CBPSEngine.getInputHandler().getKeyboard()
                    .isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                CBPSEngine.shutdown();
            }
        });

        // Set rendering callback
        CBPSEngine.setRenderCallback(() -> {
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            // Render your scene...
        });

        // Run the game loop (blocking)
        CBPSEngine.run();
    }
}
```

## Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| LWJGL | 3.4.1 | OpenGL/GLFW bindings |
| JOML | 1.10.8 | 3D math (vectors, matrices, quaternions) |
| Artemis-ODB | 2.3.0 | Entity Component System framework |
| Log4j2 | 2.24.0 | Logging |
| JUnit 5 | 5.9.3 | Testing |

See `pom.xml` for full dependency tree.

## Testing

Run all tests:

```bash
mvn test
```

Tests cover:
- Frame timing (delta, FPS, clamping)
- Input handling
- ECS operations

## Roadmap

- [x] Window management
- [x] Game loop & frame timing
- [x] ECS system
- [x] Input handling
- [ ] Mesh loading (glTF/OBJ)
- [ ] Texture system
- [ ] Audio (OpenAL)
- [ ] UI framework
- [ ] Network (KryoNet)
- [ ] Modding API

## Contributing

See [CONTRIBUTING.md](.github/CONTRIBUTING.md) for code style and contribution guidelines.

## License

MIT License (2026) – comboom.sucht
See [LICENSE](LICENSE) for details.

## Resources

- **Documentation**: Check the [GitHub Wiki](https://github.com/comboom-sucht/cbps-engine/wiki)
- **Example Game**: `src/main/java/.../example/ExampleGame.java`
- **Architecture Docs**: See `docs/ARCHITECTURE.md`

## Troubleshooting

### macOS: "GLFW may only be used on the main thread"

Add the `-XstartOnFirstThread` JVM argument:

```bash
java -XstartOnFirstThread -jar target/CBPSEngine-full.jar
```

### macOS: "OpenGL 4.6 not available"

macOS only supports OpenGL 4.1. The engine automatically defaults to this. If you get version mismatch errors, check `EngineConstants.java`:

```java
public static final int GL_VERSION_MAJOR = 4;
public static final int GL_VERSION_MINOR = 1;  // Not 6
```

## Support

For issues, questions, or feature requests, please open an issue on [GitHub](https://github.com/comboom-sucht/cbps-engine/issues).

---

**CBPS = "Comboom Punk Sucht" Engine** | Lightweight. Modular. Simple.
