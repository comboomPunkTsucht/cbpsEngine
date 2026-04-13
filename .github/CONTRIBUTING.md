# Contributing to CBPS Engine

Thank you for your interest in contributing! This document provides guidelines for contributing code, documentation, and bug reports.

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- No harassment or discrimination

## How to Contribute

### Reporting Bugs

1. **Check existing issues** – Search to see if the bug is already reported
2. **Provide details:**
   - OS and Java version (`java -version`)
   - Engine version
   - Minimal code to reproduce
   - Expected vs. actual behavior
   - Screenshots/logs if applicable

### Suggesting Features

1. **Check roadmap** – See `ARCHITECTURE.md` for planned features
2. **Describe the use case** – Why is this feature needed?
3. **Propose an API** – How should users interact with it?
4. **Discuss trade-offs** – Performance, complexity, backwards compatibility

### Submitting Code

#### Setup

```bash
git clone https://github.com/comboom-sucht/cbps-engine.git
cd cbps-engine
mvn clean install
```

#### Branch Naming

```
feature/your-feature-name
bugfix/issue-description
docs/documentation-update
```

#### Code Style

**Java 17+ Features:**
- Use `records` for data holders
- Use `var` for local variables (where obvious)
- Use sealed interfaces/classes where appropriate
- Use text blocks for multi-line strings

**Naming:**
```java
// ✅ Good
public class PhysicsSystem extends IteratingSystem { }
private Vector3f linearVelocity;
void processEntity(int entityId) { }

// ❌ Avoid
public class physicsSystem { }
private Vector3f lv;
void do_stuff() { }
```

**Structure:**
- 1 top-level class per file
- Package-private fields + public accessors
- Fluent builders where applicable
- No getter/setter bloat for data classes (use records)

**Comments:**
- JavaDoc for **public APIs** only
- Inline comments for **non-obvious logic**
- No obvious comments: `i++; // increment i`

**Constants:**
- All magic numbers → `EngineConstants.java`
- Package-scoped for internal use
- Descriptive names

#### Testing

Write tests for:
- Public APIs
- Complex logic (math, ECS, input)
- Edge cases

---

```bash
# Run all tests
mvn test

# Run a specific test class
mvn test -Dtest=FrameTimeCalculatorTest
```

**Test Style:**
```java
@Test
public void testXXX() {
    // Arrange
    FrameTimeCalculator timer = new FrameTimeCalculator(60.0, false);

    // Act
    timer.update();

    // Assert
    assertEquals(1, timer.getFrameCount());
}
```

#### Documentation

- Update `README.md` for user-facing changes
- Update `ARCHITECTURE.md` for design changes
- Add JavaDoc to new public methods
- Link to related code/issues in comments

#### Commits

```bash
git add src/...
git commit -m "Feature: ECS system for physics simulation

- Implement PhysicsSystem with velocity integration
- Add Velocity component
- Add Transform.translate() helper
- Include unit tests

Fixes #123"
```

**Message Format:**
```
Type: Brief description (≤50 chars)

Longer explanation if needed.
- Bullet point 1
- Bullet point 2

Fixes #issue-number
Relates to #other-issue
```

**Types:**
- `Feature:` – New functionality
- `Fix:` – Bug fix
- `Refactor:` – Code improvement (no behavior change)
- `Docs:` – Documentation
- `Test:` – Test additions
- `Perf:` – Performance improvement

#### Pull Request

1. **Rebase** on latest `main`
   ```bash
   git fetch origin
   git rebase origin/main
   ```

2. **Push** to your fork
   ```bash
   git push -u origin feature/your-feature
   ```

3. **Create PR** with:
   - Clear title (same as commit)
   - Description of changes
   - Link to related issues
   - Screenshots for graphics changes
   - Checklist:
     ```markdown
     - [ ] Tests added/updated
     - [ ] Documentation updated
     - [ ] No breaking API changes (or justified)
     - [ ] Code follows style guide
     ```

4. **Respond to feedback** – Address review comments promptly

### Documentation Contributions

Documentation lives in:
- `README.md` – User guide & quick start
- `ARCHITECTURE.md` – Design overview
- `docs/*.md` – Detailed guides (future)
- `.github/wiki/*.md` – GitHub Wiki pages
- JavaDoc – In-code API docs

**To edit:**
```bash
# Example guide
vim docs/GETTING_STARTED.md

# Or update inline docs
vim src/main/java/.../CBPSEngine.java  # Update JavaDoc
```

## Architecture Guidelines

Following `CLAUDE.md`:

### ✅ Do

- **ECS > Inheritance** – Use components, not class hierarchies
- **Facade Pattern** – Hide complexity behind `CBPSEngine.*`
- **3D-First** – Use Vector3f, Quaternionf, Matrix4f
- **Constants** – All magic numbers in `EngineConstants.java`
- **Thread-safe** – Use queues for cross-thread communication
- **Test-friendly** – Separate concerns, inject dependencies
- **Simple APIs** – Raylib-inspired simplicity

### ❌ Don't

- Generic `GameObject` base class
- Over-engineering for hypothetical features
- OpenGL calls outside Graphics module
- Blocking network calls on game thread
- Hardcoded magic numbers
- Excessively nested abstractions
- Premature optimization

## Dependency Management

### Adding Dependencies

1. **Justify:** Why is this library needed?
2. **Check:** Does it conflict with existing deps?
3. **Scope:** Is it test-only (`<scope>test</scope>`)? Runtime?
4. **Update:** `pom.xml` and `README.md` dependency table

### Removing Dependencies

Should be rare. Consider:
- Is it unused? (grep for imports)
- Can we replace it locally? (small library, small cost)
- Is there controversy? (license, maintenance, security)

## Performance

Before optimizing:
1. **Profile** – Use JFR or profiler to find hotspots
2. **Measure** – Before/after numbers
3. **Document** – Why this optimization? What's the trade-off?

ECS is already cache-friendly. Focus on:
- GL batch rendering (fewer draw calls)
- Frustum culling (render less)
- Asset loading (async/streaming)

## Security

- No hardcoded credentials
- Validate external input (files, network)
- Use `try-finally` for resource cleanup
- Report security issues privately first

## License

By contributing, you agree your code is licensed under MIT. See `LICENSE` for details.

## Getting Help

- **Questions?** Open a discussion on GitHub
- **Stuck?** Comment on an issue for guidance
- **Design discussion?** Open an issue before starting big refactors

---

**Thank you for contributing to CBPS Engine!** 🎮
