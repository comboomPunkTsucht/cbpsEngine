# Changelog

All notable changes to CBPS Engine are documented in this file. This project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Support for custom ECS systems
- Entity pooling for performance
- Viewport configuration API

### Changed
- Improved delta-time clamping logic
- Refactored input event routing

### Fixed
- OpenGL context thread-local management on macOS

---

## [0.1.0-alpha] – 2026-04-13

### Added
- **Core Engine**
  - Game loop with frame timing (`FrameTimeCalculator`)
  - Window management via LWJGL/GLFW
  - OpenGL 4.1 context initialization

- **ECS System**
  - `World` wrapper for Artemis-ODB
  - `Transform` component (position, rotation, scale)
  - `Velocity` component (linear & angular)
  - `Renderer` component (mesh/material metadata)
  - `PhysicsSystem` – velocity integration
  - `RenderingSystem` – render queue management

- **Input System**
  - Thread-safe input event queue
  - `KeyboardInput` – key state tracking (pressed, down, released)
  - `MouseInput` – mouse position and button states
  - `InputHandler` – event dispatcher

- **Graphics**
  - `Window` interface + `GLFWWindow` implementation
  - `GraphicsContext` – OpenGL state management
  - `ViewportConfig` – viewport and projection settings

- **Utilities**
  - `EngineConstants` – centralized configuration
  - Example game demonstrating full API
  - Comprehensive unit tests

- **Build & Deployment**
  - Maven-based build system
  - OS-specific native profiles (Windows/macOS/Linux)
  - Fat JAR packaging via maven-shade-plugin
  - GitHub Actions CI/CD workflows

- **Documentation**
  - `README.md` – Quick start guide
  - `ARCHITECTURE.md` – Design overview
  - `CONTRIBUTING.md` – Contribution guidelines
  - MIT License

### Platform Support
- ✅ Windows (Java 17+)
- ✅ macOS Intel & M-series (OpenGL 4.1 limit)
- ✅ Linux (Java 17+)

### Dependencies
- LWJGL 3.4.1 (OpenGL/GLFW)
- JOML 1.10.8 (3D math)
- Artemis-ODB 2.3.0 (ECS framework)
- Log4j2 2.24.0 (Logging)
- JUnit 5 5.9.3 (Testing)

### Known Limitations
- No mesh/texture loading yet
- No audio system
- No network multiplayer
- No ImGui integration
- No modding system

---

## Release Checklist

For maintainers releasing a new version:

- [ ] Update version in `pom.xml`
- [ ] Update `CHANGELOG.md` with changes
- [ ] Ensure all tests pass locally
- [ ] Commit with message: "Release: v0.X.Y"
- [ ] Tag commit: `git tag v0.X.Y`
- [ ] Push tag: `git push origin v0.X.Y`
- [ ] GitHub Actions automatically creates release
- [ ] Add release notes on GitHub

---

## Versioning Strategy

CBPS Engine follows **Semantic Versioning**:
- `MAJOR.MINOR.PATCH-PRERELEASE+BUILD`
- `0.1.0-alpha` – Initial alpha release
- `0.1.0` – First stable release
- `1.0.0` – Major feature complete

**Alpha/Beta/RC** versions are pre-releases and may have breaking changes.

---

**For migration guides**, see `MIGRATION.md` (when available).
