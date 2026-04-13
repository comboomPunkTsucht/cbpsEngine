package app.comboomPunkTsucht.CBPSEngine.graphics;

import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.FloatBuffer;

/**
 * Simple shader-based renderer for modern OpenGL (Core Profile compatible).
 * Provides basic cube rendering with colors.
 */
public class SimpleShaderRenderer {
    private static int shaderProgram;
    private static int VAO, VBO, colorVBO;
    private static int vertexCount;
    private static boolean initialized = false;

    /**
     * Initialize shader program and cube geometry.
     */
    public static void initialize() {
        if (initialized) return;

        // Create shader program
        shaderProgram = createShaderProgram();

        // Setup cube geometry (6 faces, each with 4 vertices)
        setupCubeGeometry();

        initialized = true;
    }

    /**
     * Draw a cube with the given transformation matrices.
     */
    public static void drawCube(Matrix4f model, Matrix4f view, Matrix4f projection) {
        if (!initialized) initialize();

        GL30.glUseProgram(shaderProgram);

        // Set uniforms
        int modelLoc = GL30.glGetUniformLocation(shaderProgram, "model");
        int viewLoc = GL30.glGetUniformLocation(shaderProgram, "view");
        int projLoc = GL30.glGetUniformLocation(shaderProgram, "projection");

        FloatBuffer matrix = MemoryUtil.memAllocFloat(16);
        model.get(matrix);
        GL33.glUniformMatrix4fv(modelLoc, false, matrix);
        MemoryUtil.memFree(matrix);

        matrix = MemoryUtil.memAllocFloat(16);
        view.get(matrix);
        GL33.glUniformMatrix4fv(viewLoc, false, matrix);
        MemoryUtil.memFree(matrix);

        matrix = MemoryUtil.memAllocFloat(16);
        projection.get(matrix);
        GL33.glUniformMatrix4fv(projLoc, false, matrix);
        MemoryUtil.memFree(matrix);

        // Draw cube
        GL30.glBindVertexArray(VAO);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, vertexCount);
        GL30.glBindVertexArray(0);
        GL30.glUseProgram(0);
    }

    private static void setupCubeGeometry() {
        // Cube vertices (2 triangles per face, 6 faces)
        float[] vertices = {
            // Front (Z+) - Red
            -0.5f, -0.5f, 0.5f,  0.5f, -0.5f, 0.5f,  0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,  0.5f, 0.5f, 0.5f,  -0.5f, 0.5f, 0.5f,
            // Back (Z-) - Green
            -0.5f, -0.5f, -0.5f,  -0.5f, 0.5f, -0.5f,  0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, -0.5f, -0.5f,
            // Top (Y+) - Blue
            -0.5f, 0.5f, -0.5f,  -0.5f, 0.5f, 0.5f,  0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,  0.5f, 0.5f, 0.5f,  0.5f, 0.5f, -0.5f,
            // Bottom (Y-) - Yellow
            -0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,  0.5f, -0.5f, 0.5f,  -0.5f, -0.5f, 0.5f,
            // Right (X+) - Cyan
            0.5f, -0.5f, -0.5f,  0.5f, 0.5f, -0.5f,  0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, -0.5f,  0.5f, 0.5f, 0.5f,  0.5f, -0.5f, 0.5f,
            // Left (X-) - Magenta
            -0.5f, -0.5f, -0.5f,  -0.5f, -0.5f, 0.5f,  -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,  -0.5f, 0.5f, 0.5f,  -0.5f, 0.5f, -0.5f,
        };

        float[] colors = {
            // Front - Red (6 verts)
            1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,  1.0f, 0.0f, 0.0f,
            // Back - Green (6 verts)
            0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,  0.0f, 1.0f, 0.0f,
            // Top - Blue (6 verts)
            0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,  0.0f, 0.0f, 1.0f,
            // Bottom - Yellow (6 verts)
            1.0f, 1.0f, 0.0f,  1.0f, 1.0f, 0.0f,  1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,  1.0f, 1.0f, 0.0f,  1.0f, 1.0f, 0.0f,
            // Right - Cyan (6 verts)
            0.0f, 1.0f, 1.0f,  0.0f, 1.0f, 1.0f,  0.0f, 1.0f, 1.0f,
            0.0f, 1.0f, 1.0f,  0.0f, 1.0f, 1.0f,  0.0f, 1.0f, 1.0f,
            // Left - Magenta (6 verts)
            1.0f, 0.0f, 1.0f,  1.0f, 0.0f, 1.0f,  1.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 1.0f,  1.0f, 0.0f, 1.0f,  1.0f, 0.0f, 1.0f,
        };

        vertexCount = vertices.length / 3;

        // Create VAO
        VAO = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(VAO);

        // Position VBO
        VBO = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, VBO);
        FloatBuffer vertexBuffer = MemoryUtil.memAllocFloat(vertices.length);
        vertexBuffer.put(vertices).flip();
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, vertexBuffer, GL30.GL_STATIC_DRAW);
        MemoryUtil.memFree(vertexBuffer);

        // Vertex attrib (position)
        GL30.glVertexAttribPointer(0, 3, GL30.GL_FLOAT, false, 12, 0);
        GL30.glEnableVertexAttribArray(0);

        // Color VBO
        colorVBO = GL30.glGenBuffers();
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, colorVBO);
        FloatBuffer colorBuffer = MemoryUtil.memAllocFloat(colors.length);
        colorBuffer.put(colors).flip();
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, colorBuffer, GL30.GL_STATIC_DRAW);
        MemoryUtil.memFree(colorBuffer);

        // Vertex attrib (color)
        GL30.glVertexAttribPointer(1, 3, GL30.GL_FLOAT, false, 12, 0);
        GL30.glEnableVertexAttribArray(1);

        GL30.glBindVertexArray(0);
    }

    private static int createShaderProgram() {
        String vertexShader = """
            #version 410 core
            layout(location = 0) in vec3 position;
            layout(location = 1) in vec3 color;

            uniform mat4 model;
            uniform mat4 view;
            uniform mat4 projection;

            out vec3 fragColor;

            void main() {
                gl_Position = projection * view * model * vec4(position, 1.0);
                fragColor = color;
            }
            """;

        String fragmentShader = """
            #version 410 core
            in vec3 fragColor;
            out vec4 outColor;

            void main() {
                outColor = vec4(fragColor, 1.0);
            }
            """;

        int vShader = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
        GL30.glShaderSource(vShader, vertexShader);
        GL30.glCompileShader(vShader);

        if (GL30.glGetShaderi(vShader, GL30.GL_COMPILE_STATUS) == 0) {
            System.err.println("[SimpleShaderRenderer] Vertex shader error: " + GL30.glGetShaderInfoLog(vShader));
        }

        int fShader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
        GL30.glShaderSource(fShader, fragmentShader);
        GL30.glCompileShader(fShader);

        if (GL30.glGetShaderi(fShader, GL30.GL_COMPILE_STATUS) == 0) {
            System.err.println("[SimpleShaderRenderer] Fragment shader error: " + GL30.glGetShaderInfoLog(fShader));
        }

        int program = GL30.glCreateProgram();
        GL30.glAttachShader(program, vShader);
        GL30.glAttachShader(program, fShader);
        GL30.glLinkProgram(program);

        if (GL30.glGetProgrami(program, GL30.GL_LINK_STATUS) == 0) {
            System.err.println("[SimpleShaderRenderer] Linking error: " + GL30.glGetProgramInfoLog(program));
        }

        GL30.glDeleteShader(vShader);
        GL30.glDeleteShader(fShader);

        return program;
    }

    public static void cleanup() {
        if (initialized) {
            GL30.glDeleteBuffers(VBO);
            GL30.glDeleteBuffers(colorVBO);
            GL30.glDeleteVertexArrays(VAO);
            GL30.glDeleteProgram(shaderProgram);
            initialized = false;
        }
    }
}
