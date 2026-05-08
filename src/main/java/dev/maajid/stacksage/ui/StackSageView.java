package dev.maajid.stacksage.ui;

import dev.maajid.stacksage.analyzer.ExceptionAnalyzer;
import dev.maajid.stacksage.models.AnalysisResult;
import dev.maajid.stacksage.models.ExceptionInfo;
import dev.maajid.stacksage.models.ParsedStackTrace;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;

public class StackSageView {
    private static final String SAMPLE_TRACE = """
            Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "name" is null
                at dev.example.UserService.createUser(UserService.java:42)
                at dev.example.Main.main(Main.java:12)
            """;

    private final ExceptionAnalyzer analyzer = new ExceptionAnalyzer();
    private final TextArea inputArea = new TextArea();
    private final VBox resultBox = new VBox(16);
    private String latestOutput = "";

    public Parent create() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");

        VBox shell = new VBox(18);
        shell.setPadding(new Insets(28));
        shell.getStyleClass().add("shell");

        HBox header = createHeader();
        HBox workspace = new HBox(18, createInputPanel(), createResultPanel());
        VBox.setVgrow(workspace, Priority.ALWAYS);

        shell.getChildren().addAll(header, workspace);
        root.setCenter(shell);

        analyzeInput();
        return root;
    }

    private HBox createHeader() {
        VBox titleGroup = new VBox(4);
        Label title = new Label("StackSage JVM");
        title.getStyleClass().add("app-title");
        Label subtitle = new Label("Paste a JVM stack trace and get the exception, root location, and practical fixes.");
        subtitle.getStyleClass().add("subtitle");
        titleGroup.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label("Offline MVP");
        badge.getStyleClass().add("badge");

        HBox header = new HBox(18, titleGroup, spacer, badge);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private Node createInputPanel() {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("panel");
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.setMinWidth(380);

        Label label = new Label("Stack Trace Input");
        label.getStyleClass().add("section-title");

        inputArea.setPromptText("Paste JVM exception, stack trace, or runtime logs here...");
        inputArea.setText(SAMPLE_TRACE);
        inputArea.setWrapText(false);
        VBox.setVgrow(inputArea, Priority.ALWAYS);

        Button analyzeButton = new Button("Analyze");
        analyzeButton.getStyleClass().add("primary-button");
        analyzeButton.setOnAction(event -> analyzeInput());

        Button sampleButton = new Button("Sample");
        sampleButton.getStyleClass().add("secondary-button");
        sampleButton.setOnAction(event -> {
            inputArea.setText(SAMPLE_TRACE);
            analyzeInput();
        });

        HBox actions = new HBox(10, analyzeButton, sampleButton);
        actions.setAlignment(Pos.CENTER_LEFT);

        panel.getChildren().addAll(label, inputArea, actions);
        return panel;
    }

    private Node createResultPanel() {
        VBox panel = new VBox(12);
        panel.getStyleClass().add("panel");
        HBox.setHgrow(panel, Priority.ALWAYS);
        panel.setMinWidth(380);

        HBox top = new HBox(10);
        top.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label("Analysis Output");
        label.getStyleClass().add("section-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button copyButton = new Button("Copy");
        copyButton.getStyleClass().add("secondary-button");
        copyButton.setOnAction(event -> copyOutput());
        top.getChildren().addAll(label, spacer, copyButton);

        resultBox.getStyleClass().add("result-box");
        VBox.setVgrow(resultBox, Priority.ALWAYS);

        panel.getChildren().addAll(top, resultBox);
        return panel;
    }

    private void analyzeInput() {
        AnalysisResult result = analyzer.analyze(inputArea.getText());
        latestOutput = formatPlainText(result);
        renderResult(result);
    }

    private void renderResult(AnalysisResult result) {
        resultBox.getChildren().clear();

        ParsedStackTrace parsed = result.parsed();
        ExceptionInfo info = result.info();

        resultBox.getChildren().addAll(
                metricRow("Exception", parsed.hasException() ? parsed.exceptionType() : "Not detected"),
                metricRow("Category", info.category()),
                metricRow("Location", parsed.location().orElse("No source frame detected")),
                metricRow("Method", parsed.methodName() == null ? "No method detected" : parsed.methodName()),
                section("Explanation", List.of(info.explanation())),
                section("Root Cause Hints", result.rootCauseHints()),
                section("Fix Suggestions", info.debuggingSteps()),
                section("Prevention Tips", info.preventionTips())
        );
    }

    private Node metricRow(String key, String value) {
        HBox row = new HBox(12);
        row.getStyleClass().add("metric-row");
        row.setAlignment(Pos.CENTER_LEFT);

        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("metric-key");
        keyLabel.setMinWidth(94);

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");
        valueLabel.setWrapText(true);

        row.getChildren().addAll(keyLabel, valueLabel);
        return row;
    }

    private Node section(String title, List<String> lines) {
        VBox section = new VBox(8);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("result-title");
        section.getChildren().add(titleLabel);

        for (String line : lines) {
            Label item = new Label("> " + line);
            item.getStyleClass().add("result-line");
            item.setWrapText(true);
            section.getChildren().add(item);
        }

        return section;
    }

    private String formatPlainText(AnalysisResult result) {
        ParsedStackTrace parsed = result.parsed();
        ExceptionInfo info = result.info();

        return """
                StackSage JVM Analysis
                Exception: %s
                Category: %s
                Location: %s
                Method: %s

                Explanation:
                %s

                Root Cause Hints:
                %s

                Fix Suggestions:
                %s
                """.formatted(
                parsed.hasException() ? parsed.exceptionType() : "Not detected",
                info.category(),
                parsed.location().orElse("No source frame detected"),
                parsed.methodName() == null ? "No method detected" : parsed.methodName(),
                info.explanation(),
                bulletList(result.rootCauseHints()),
                bulletList(info.debuggingSteps())
        );
    }

    private String bulletList(List<String> lines) {
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            builder.append("- ").append(line).append(System.lineSeparator());
        }
        return builder.toString().stripTrailing();
    }

    private void copyOutput() {
        ClipboardContent content = new ClipboardContent();
        content.putString(latestOutput);
        Clipboard.getSystemClipboard().setContent(content);
    }
}
