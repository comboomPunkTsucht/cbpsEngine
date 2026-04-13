package app.comboomPunkTsucht.CBPSEngine.example;

/**
 * Example launcher for different experiment scenarios.
 *
 * Usage:
 *   java ExampleLauncher --example=game
 *   java ExampleLauncher --example=cube (default)
 */
public class ExampleLauncher {
    public static void main(String[] args) {
        String exampleName = "cube"; // Default

        // Parse command line arguments
        for (String arg : args) {
            if (arg.startsWith("--example=")) {
                exampleName = arg.substring("--example=".length());
            }
        }

        System.out.println("[ExampleLauncher] Starting example: " + exampleName);

        switch (exampleName.toLowerCase()) {
            case "game":
            case "cube":
            case "rotating-cube":
                ExampleGame.main(new String[]{});
                break;

            default:
                System.err.println("[ExampleLauncher] Unknown example: " + exampleName);
                System.err.println("Available examples: game, cube");
                System.exit(1);
        }
    }
}
