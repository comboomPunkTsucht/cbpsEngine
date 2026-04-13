# Getting Started

This guide covers setting up CBPS Engine and running your first program.

## Prerequisites

- **Java 17+** (LTS) – [download from Oracle](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or use [Adoptium](https://adoptium.net/)
- **Maven 3.8+** – [installation guide](https://maven.apache.org/install.html)
- **Git** – for cloning the repository

## Installation

### 1. Clone the Repository

```bash
git clone https://github.com/comboom-sucht/cbps-engine.git
cd cbps-engine
```

### 2. Build the Engine

```bash
mvn clean package
```

This creates:
- `target/CBPSEngine-0.1.0-alpha.jar` – Standard JAR
- `target/CBPSEngine-full.jar` – Fat JAR (all dependencies bundled)

### 3. Verify Installation

Run the example game:

```bash
# macOS (requires -XstartOnFirstThread for GLFW)
java -XstartOnFirstThread -jar target/CBPSEngine-full.jar

# Linux / Windows
java -jar target/CBPSEngine-full.jar
```

You should see:
```
[ExampleGame] Starting CBPS Engine example...
[ExampleGame] Initialized. Controls: WASD=move, ESC=quit, LeftClick=test
[ExampleGame] Starting main loop...
[ExampleGame] FPS: 60.0 | Delta: 0.0166s
```

## Troubleshooting

### "GLFW may only be used on the main thread" (macOS)

Add the JVM flag:
```bash
java -XstartOnFirstThread -jar target/CBPSEngine-full.jar
```

If using Maven's `exec:java` plugin, configure in `pom.xml`:
```xml
<systemProperties>
    <systemProperty>
        <key>java.awt.headless</key>
        <value>false</value>
    </systemProperty>
</systemProperties>
```

### "OpenGL version unavailable" (macOS)

macOS supports OpenGL 4.1 max. Engine auto-detects this (see `EngineConstants.GL_VERSION_MINOR`). If you see version mismatch errors, verify:

```bash
glxinfo | grep "OpenGL version"  # Linux
system_profiler SPDisplaysDataType | grep "Metal"  # macOS (uses Metal instead)
```

### Build Fails ("LWJGL natives not found")

Maven profiles should auto-detect your OS. Manually specify if needed:

```bash
mvn clean package -Plwjgl-natives-macos-aarch64  # Apple Silicon
mvn clean package -Plwjgl-natives-linux-amd64    # Linux
mvn clean package -Plwjgl-natives-windows-amd64  # Windows
```

## What's Next?

- Read the [API Overview](API-Overview) for available methods
- Check out [Your First Game](Your-First-Game) for a simple tutorial
- See [ECS Architecture](ECS-Architecture) to understand the design

## Project Structure

```
cbps-engine/
├── pom.xml                          # Maven config
├── README.md                        # Quick reference
├── ARCHITECTURE.md                  # Design docs
├── LICENSE                          # MIT License
│
├── src/
│   ├── main/java/app/comboomPunkTsucht/CBPSEngine/
│   │   ├── CBPSEngine.java          # Main API
│   │   ├── EngineConstants.java     # Config
│   │   ├── core/                    # Game loop
│   │   ├── graphics/                # Rendering
│   │   ├── input/                   # Input handling
│   │   └── ecs/                     # Entity system
│   │
│   └── test/java/...                # Tests
│
├── .github/
│   ├── workflows/                   # CI/CD pipelines
│   └── CONTRIBUTING.md              # Contribution guide
│
└── docs/
    └── wiki/                        # Wiki pages
```

## Building Your Own Project

### Option 1: Use as a Dependency (Future)

Once published to Maven Central:

```xml
<dependency>
    <groupId>app.comboomPunkTsucht</groupId>
    <artifactId>CBPSEngine</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Option 2: Clone & Modify

Clone this repo and customize:

```bash
git clone https://github.com/comboom-sucht/cbps-engine.git my-game
cd my-game
mvn clean package
```

### Option 3: Add to Existing Project

Copy the JAR:

```bash
mvn install:install-file \
  -Dfile=target/CBPSEngine-full.jar \
  -DgroupId=app.comboomPunkTsucht \
  -DartifactId=CBPSEngine \
  -Dversion=0.1.0-alpha \
  -Dpackaging=jar
```

Then use in your `pom.xml`.

## Next Steps

- **New to game dev?** → [Your First Game](Your-First-Game) (tutorial)
- **Want details?** → [API Overview](API-Overview)
- **Understand the engine?** → [ECS Architecture](ECS-Architecture)
- **Need help?** → [GitHub Discussions](https://github.com/comboom-sucht/cbps-engine/discussions)
