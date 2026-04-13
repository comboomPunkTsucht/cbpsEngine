package app.comboomPunkTsucht.CBPSEngine.graphics;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.glfw.GLFWWindowSizeCallbackI;
import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

/**
 * Static dispatcher for GLFW callbacks.
 * Registers native GLFW callbacks and delegates them to Window instance.
 */
public final class GLFWEventDispatcher {
    private GLFWEventDispatcher() {} // Utility class

    /**
     * Register all GLFW event callbacks for the window.
     * Called once during Window initialization.
     */
    public static void registerCallbacks(Window window) {
        long windowHandle = window.getGlfwWindowHandle();

        // Window close callback
        GLFW.glfwSetWindowCloseCallback(windowHandle, (GLFWWindowCloseCallbackI) (handle) -> {
            window.onWindowClose();
        });

        // Window resize callback
        GLFW.glfwSetWindowSizeCallback(windowHandle, (GLFWWindowSizeCallbackI) (handle, width, height) -> {
            window.onWindowResize(width, height);
        });

        // Key callback
        GLFW.glfwSetKeyCallback(windowHandle, (GLFWKeyCallbackI) (handle, key, scancode, action, mods) -> {
            window.onKey(key, scancode, action, mods);
        });

        // Mouse position callback
        GLFW.glfwSetCursorPosCallback(windowHandle, (GLFWCursorPosCallbackI) (handle, x, y) -> {
            window.onMouseMove(x, y);
        });

        // Mouse button callback
        GLFW.glfwSetMouseButtonCallback(windowHandle, (GLFWMouseButtonCallbackI) (handle, button, action, mods) -> {
            double[] cursorPos = new double[2];
            GLFW.glfwGetCursorPos(handle, cursorPos, cursorPos);
            window.onMouseButton(cursorPos[0], cursorPos[1], button, action);
        });
    }
}
