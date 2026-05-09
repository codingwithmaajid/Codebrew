public class App {
    private static final String BANNER = """
███████╗████████╗ █████╗  ██████╗██╗  ██╗███████╗ █████╗  ██████╗ ███████╗
██╔════╝╚══██╔══╝██╔══██╗██╔════╝██║ ██╔╝██╔════╝██╔══██╗██╔════╝ ██╔════╝
███████╗   ██║   ███████║██║     █████╔╝ ███████╗███████║██║  ███╗█████╗
╚════██║   ██║   ██╔══██║██║     ██╔═██╗ ╚════██║██╔══██║██║   ██║██╔══╝
███████║   ██║   ██║  ██║╚██████╗██║  ██╗███████║██║  ██║╚██████╔╝███████╗
╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚══════╝

☕ StackSage JVM
Paste a JVM crash. Get the story, source, and fix.
""";

    public static void main(String[] args) throws Exception {
        System.out.println(BANNER);

        String input;
        if (args.length > 0) {
            if ("--help".equals(args[0]) || "-h".equals(args[0])) {
                printUsage();
                return;
            }
            input = java.nio.file.Files.readString(java.nio.file.Path.of(args[0]));
        } else if (System.console() != null) {
            printUsage();
            return;
        } else {
            input = new String(System.in.readAllBytes());
        }

        if (input.isBlank()) {
            printUsage();
            return;
        }

        System.out.println(StackTraceAnalyzer.analyze(input));
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  stacksage");
        System.out.println("  stacksage error.txt");
        System.out.println("  cat error.txt | stacksage");
        System.out.println("  stacksage --help");
    }
}
