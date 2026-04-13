package app.comboomPunkTsucht.CBPSEngine.graphics;

import app.comboomPunkTsucht.CBPSEngine.core.EngineConstants;
import app.comboomPunkTsucht.CBPSEngine.exception.EngineException;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GLFW Window wrapper managing OpenGL context lifecycle.
 *
 * Thread-Safety Notes:
 * - GL context is stored in ThreadLocal (only Game-Loop thread can call GL)
 * - Events are queued in thread-safe BlockingQueue
 * - Window creation/destruction must happen on initialization thread
 */
public class Window {
    private static final ThreadLocal<Long> glContext = new ThreadLocal<>();
    private static Window instance; // Singleton for GLFW callbacks

    private final int width;
    private final int height;
    private final String title;

    private long glfwWindow;
    private GLCapabilities glCapabilities;
    private volatile boolean shouldClose;

    private final BlockingQueue<WindowEvent> eventQueue;

    /**
     * Private constructor. Use {@link #create(int, int, String)} factory method.
     */
    private Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.eventQueue = new LinkedBlockingQueue<>(EngineConstants.EVENT_QUEUE_SIZE);
        this.shouldClose = false;
    }

    /**
     * Factory method to create and initialize a new window.
     * Performs GLFW initialization, window creation, and GL context setup.
     */
    public static Window create(int width, int height, String title) {
        if (instance != null) {
            throw new EngineException("Window already exists. Only one window per application.");
        }

        Window window = new Window(width, height, title);
        instance = window;
        window.initialize();
        return window;
    }

    /**
     * Initialize GLFW, create window, and set up GL context.
     * GLFW Initialization Sequence:
     * 1. glfwInit() - Initialize GLFW
     * 2. Set window hints (GL version, profile, etc.)
     * 3. Create window handle
     * 4. Make GL context current (thread-local)
     * 5. Setup vsync
     * 6. Initialize GL capabilities
     */
    private void initialize() {
        try {
            // Register GLFW error callback early
            GLFWErrorCallback.createPrint(System.err).set();

            // Initialize GLFW
            if (!GLFW.glfwInit()) {
                throw new EngineException("Failed to initialize GLFW");
            }

            // Configure window hints for OpenGL 4.6 Core Profile
            GLFW.glfwDefaultWindowHints();
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, EngineConstants.GL_VERSION_MAJOR);
            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, EngineConstants.GL_VERSION_MINOR);
            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
            GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
            GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);

            // Create window
            glfwWindow = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
            if (glfwWindow == MemoryUtil.NULL) {
                throw new EngineException("Failed to create GLFW window. Check graphics driver compatibility.");
            }

            // Make GL context current for THIS thread (store in ThreadLocal)
            GLFW.glfwMakeContextCurrent(glfwWindow);
            glContext.set(glfwWindow);

            // Setup VSync (swap interval = 1 means vsync on)
            if (EngineConstants.VSYNC_ENABLED) {
                GLFW.glfwSwapInterval(1);
            } else {
                GLFW.glfwSwapInterval(0);
            }

            // Initialize OpenGL capabilities for this thread
            glCapabilities = GL.createCapabilities();
            if (glCapabilities == null) {
                throw new EngineException("Failed to initialize OpenGL capabilities");
            }

            // Register GLFW event callbacks
            GLFWEventDispatcher.registerCallbacks(this);

            if (EngineConstants.VERBOSE_LOGGING) {
                System.out.println("[Window] Initialized: " + width + "x" + height + " - " + title);
            }
        } catch (Exception e) {
            cleanup();
            throw e instanceof EngineException ? (EngineException) e : new EngineException("Window initialization failed", e);
        }
    }

    /**
     * Make GL context current for THIS thread.
     * Must be called at the start of each frame in the Game-Loop thread.
     */
    public void setActive() {
        GLFW.glfwMakeContextCurrent(glfwWindow);
        glContext.set(glfwWindow);
    }

    /**
     * Poll GLFW events and queue them for processing.
     * Non-blocking - just retrieves queued events from callbacks.
     */
    public void update() {
        GLFW.glfwPollEvents();
    }

    /**
     * Swap front/back buffers to present the rendered frame.
     */
    public void swapBuffers() {
        GLFW.glfwSwapBuffers(glfwWindow);
    }

    /**
     * Clear background with RGBA color (Raylib-style wrapper).
     * Calls glClearColor + glClear internally.
     */
    public void clearBackground(float r, float g, float b, float a) {
        org.lwjgl.opengl.GL11.glClearColor(r, g, b, a);
        org.lwjgl.opengl.GL11.glClear(
            org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT |
            org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT
        );
    }

    /**
     * Check if window should close (user clicked close button or shutdown requested).
     */
    public boolean shouldClose() {
        return shouldClose || GLFW.glfwWindowShouldClose(glfwWindow);
    }

    /**
     * Get the thread-local GL context handle.
     * Can only be called from the thread that initialized this window.
     */
    public static Long getGLContext() {
        return glContext.get();
    }

    /**
     * Get pending events from the queue (non-blocking poll).
     */
    public WindowEvent pollEvent() {
        return eventQueue.poll();
    }

    /**
     * Queue a window event (called from GLFW callbacks).
     */
    void queueEvent(WindowEvent event) {
        eventQueue.offer(event); // Non-blocking offer (drops if queue full)
    }

    /**
     * Cleanup resources: destroy window and terminate GLFW.
     * Should be called once at application shutdown.
     */
    public void cleanup() {
        if (glfwWindow != MemoryUtil.NULL) {
            GLFW.glfwDestroyWindow(glfwWindow);
        }
        GLFW.glfwTerminate();
        glContext.remove();
        instance = null;

        if (EngineConstants.VERBOSE_LOGGING) {
            System.out.println("[Window] Cleaned up");
        }
    }

    // Callback handler methods (invoked by GLFWEventDispatcher)

    void onWindowClose() {
        shouldClose = true;
        queueEvent(new WindowCloseEvent(System.currentTimeMillis()));
    }

    void onWindowResize(int newWidth, int newHeight) {
        // Update internal dimensions (optional - could get from glfwGetWindowSize() instead)
        queueEvent(new WindowResizeEvent(System.currentTimeMillis(), newWidth, newHeight));
    }

    void onKey(int key, int scancode, int action, int mods) {
        queueEvent(new KeyEvent(System.currentTimeMillis(), key, scancode, action, mods));
    }

    void onMouseMove(double x, double y) {
        queueEvent(new MouseEvent(System.currentTimeMillis(), x, y, -1, -1));
    }

    void onMouseButton(double x, double y, int button, int action) {
        queueEvent(new MouseEvent(System.currentTimeMillis(), x, y, button, action));
    }

    // Accessors

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getGlfwWindowHandle() {
        return glfwWindow;
    }

    public static Window getInstance() {
        return instance;
    }
}
