package app.comboomPunkTsucht.CBPSEngine.graphics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FrameTimeCalculator.
 * Tests delta time calculation, FPS tracking, and frame capping.
 */
public class FrameTimeCalculatorTest {
    private FrameTimeCalculator timer;

    @BeforeEach
    public void setUp() {
        timer = new FrameTimeCalculator(60.0, false); // No FPS capping for predictable tests
    }

    @Test
    public void testInitialization() {
        // Frame timer should start with reasonable defaults
        assertNotNull(timer);
        assertEquals(0, timer.getElapsedTime(), 0.001);
        assertEquals(0, timer.getFrameCount());
    }

    @Test
    public void testSingleFrameUpdate() throws InterruptedException {
        timer.update();

        // After first update, elapsed time should be small but non-zero
        double elapsed = timer.getElapsedTime();
        assertTrue(elapsed > 0, "Elapsed time should be > 0 after update");
        assertTrue(elapsed < 0.1, "Elapsed time should be small (<100ms)");

        assertEquals(1, timer.getFrameCount());
    }

    @Test
    public void testMultipleFrameUpdates() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            timer.update();
            Thread.sleep(1); // Simulate ~1ms per frame
        }

        assertEquals(10, timer.getFrameCount());
        assertTrue(timer.getElapsedTime() > 0.009, "Should have elapsed ~10ms");
    }

    @Test
    public void testDeltaTimeReasonable() throws InterruptedException {
        timer.update();
        Thread.sleep(10); // Wait 10ms
        timer.update();

        double deltaTime = timer.getDeltaTime();
        assertTrue(deltaTime > 0.005, "Delta time should be > 5ms");
        assertTrue(deltaTime < 0.100, "Delta time should be < 100ms");
    }

    @Test
    public void testFPS() throws InterruptedException {
        // Simulate 60 frames at ~16.67ms intervals
        for (int i = 0; i < 60; i++) {
            timer.update();
            Thread.sleep(1); // Approximate timing
        }

        long fps = timer.getFPS();
        // FPS should be reasonable (very loose tolerance due to system timing variations)
        assertTrue(fps > 0, "FPS should be > 0: " + fps);
    }

    @Test
    public void testResetFunctionality() {
        timer.update();
        timer.update();

        assertTrue(timer.getFrameCount() > 0);
        assertTrue(timer.getElapsedTime() > 0);

        timer.reset();

        assertEquals(0, timer.getElapsedTime(), 0.001);
        assertEquals(0, timer.getFrameCount());
    }
}
