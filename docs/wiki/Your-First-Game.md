# Your First Game

This tutorial walks you through creating a simple interactive game with CBPS Engine.

## Goal

Build a 2D game where:
- A player-controlled square moves with arrow keys
- The square can jump with spacebar
- The square bounces off screen edges

## Step 1: Set Up Your Project

Create a new Java file:

```bash
mkdir -p my-first-game/src/main/java/com/example
cd my-first-game
```

Copy `pom.xml` from CBPS Engine and update the artifact ID:

```xml
<artifactId>my-first-game</artifactId>
```

## Step 2: Create the Game Class

Create `src/main/java/com/example/PlayerGame.java`:

```java
package com.example;

import app.comboomPunkTsucht.CBPSEngine.CBPSEngine;
import app.comboomPunkTsucht.CBPSEngine.ecs.World;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Transform;
import app.comboomPunkTsucht.CBPSEngine.ecs.component.Velocity;
import app.comboomPunkTsucht.CBPSEngine.input.InputHandler;
import app.comboomPunkTsucht.CBPSEngine.input.KeyboardInput;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.joml.Vector3f;

public class PlayerGame {
    private static int playerEntity;
    private static float moveSpeed = 5.0f;
    private static float jumpForce = 10.0f;
    private static boolean canJump = false;

    public static void main(String[] args) {
        // Initialize window
        CBPSEngine.initWindow(1280, 720, "My First Game");

        // Get ECS world
        World world = CBPSEngine.getECSWorld();
        playerEntity = world.createEntity();

        // Create player entity with Transform and Velocity
        Transform transform = new Transform();
        transform.setPosition(640, 360, 0);  // Center of screen
        transform.setScale(50, 50, 1);       // 50×50 square

        Velocity velocity = new Velocity();
        velocity.setLinear(0, 0, 0);
        velocity.setAngular(0, 0, 0);

        com.artemis.Entity entity = world.getArtemisWorld().getEntity(playerEntity);
        entity.edit().add(transform).add(velocity);

        // Update callback (game logic)
        CBPSEngine.setUpdateCallback(() -> {
            InputHandler input = CBPSEngine.getInputHandler();
            if (input == null) return;

            KeyboardInput keyboard = input.getKeyboard();
            Transform t = world.getArtemisWorld()
                .getEntity(playerEntity).getComponent(Transform.class);
            Velocity v = world.getArtemisWorld()
                .getEntity(playerEntity).getComponent(Velocity.class);

            // Handle input
            float deltaX = 0;
            if (keyboard.isKeyDown(GLFW.GLFW_KEY_RIGHT)) {
                deltaX += moveSpeed;
            }
            if (keyboard.isKeyDown(GLFW.GLFW_KEY_LEFT)) {
                deltaX -= moveSpeed;
            }

            // Jump
            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE) && canJump) {
                v.setLinear(v.getLinear().x, jumpForce, 0);
                canJump = false;
            }

            // Apply gravity
            Vector3f linear = v.getLinear();
            linear.y -= 9.81f * CBPSEngine.getDeltaTime();

            // Update velocity (horizontal + gravity)
            v.setLinear(deltaX, linear.y, 0);

            // Reset position if out of bounds
            Vector3f pos = t.getPosition();
            if (pos.x < 0) pos.x = 1280;
            if (pos.x > 1280) pos.x = 0;

            // Ground check (y < 360 means on ground)
            if (pos.y <= 360) {
                pos.y = 360;
                canJump = true;
                linear.y = 0;  // Stop falling
            }

            // Quit with ESC
            if (keyboard.isKeyPressed(GLFW.GLFW_KEY_ESCAPE)) {
                CBPSEngine.shutdown();
            }
        });

        // Render callback
        CBPSEngine.setRenderCallback(() -> {
            GL11.glClearColor(0.2f, 0.2f, 0.3f, 1.0f);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // Draw player square (placeholder)
            Transform t = world.getArtemisWorld()
                .getEntity(playerEntity).getComponent(Transform.class);
            Vector3f pos = t.getPosition();
            Vector3f scale = t.getScale();

            // Simple rectangle rendering using GL immediate mode
            GL11.glMatrixMode(GL11.GL_PROJECTION);
            GL11.glLoadIdentity();
            GL11.glOrtho(0, 1280, 720, 0, -1, 1);
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glLoadIdentity();

            GL11.glColor3f(1.0f, 0.5f, 0.0f);  // Orange
            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2f(pos.x - scale.x/2, pos.y - scale.y/2);
                GL11.glVertex2f(pos.x + scale.x/2, pos.y - scale.y/2);
                GL11.glVertex2f(pos.x + scale.x/2, pos.y + scale.y/2);
                GL11.glVertex2f(pos.x - scale.x/2, pos.y + scale.y/2);
            }
            GL11.glEnd();
        });

        System.out.println("[Game] Controls: LEFT/RIGHT=move, SPACE=jump, ESC=quit");

        // Run game loop
        CBPSEngine.run();

        System.out.println("[Game] Goodbye!");
    }
}
```

## Step 3: Build & Run

```bash
mvn clean package
java -XstartOnFirstThread -jar target/my-first-game-1.0-SNAPSHOT-full.jar  # macOS
java -jar target/my-first-game-1.0-SNAPSHOT-full.jar                       # Linux/Windows
```

## Step 4: Try Modifications

### Add a Score System

```java
public class Score extends Component {
    public int points = 0;
}

// In setup:
entity.edit().add(new Score());

// In render:
UI.drawText("Score: " + entity.getComponent(Score.class).points, 10, 10);
```

### Spawn Collectibles

```java
int collectible = world.createEntity();
com.artemis.Entity obj = world.getArtemisWorld().getEntity(collectible);
obj.edit()
    .add(new Transform().setPosition(300, 200, 0).setScale(20, 20, 1))
    .add(new Collectible(10));  // Worth 10 points
```

### Add Sound Effects

```java
// Play on jump
if (keyboard.isKeyPressed(GLFW.GLFW_KEY_SPACE) && canJump) {
    AudioManager.play("assets/jump.wav");
    v.setLinear(v.getLinear().x, jumpForce, 0);
}
```

## Common Issues

### "Transform not found on entity"

The component wasn't added during setup. Check `entity.edit().add(...)`.

### "Game runs but nothing renders"

The viewport projection might be wrong. Ensure:
- `GL11.glOrtho(0, 1280, 720, 0, -1, 1)` matches your window size
- `GL11.glClear()` is called

### Input not responding

Verify `InputHandler` is not null:
```java
InputHandler input = CBPSEngine.getInputHandler();
if (input == null) {
    System.err.println("Input not initialized!");
    return;
}
```

## Next Steps

- Add **enemies** with AI (pathfinding, collision detection)
- Implement **collision detection** (AABB)
- Create a **level system** (load from files)
- Add **particles** and **effects**
- Implement **camera controls**

See:
- [ECS Architecture](ECS-Architecture) for system design
- [API Overview](API-Overview) for available classes
- [Custom Components](Custom-Components) to extend the engine
