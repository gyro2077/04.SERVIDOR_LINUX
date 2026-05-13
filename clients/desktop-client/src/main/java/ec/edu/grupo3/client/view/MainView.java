package ec.edu.grupo3.client.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MainView {

    private TabPane tabPane;
    private Label statusLabel;
    private Button logoutButton;

    private TextField massValueField, lengthValueField, tempValueField;
    private ComboBox<String> massFromCombo, lengthFromCombo, tempFromCombo;
    private ComboBox<String> massToCombo, lengthToCombo, tempToCombo;
    private Button massConvertButton, lengthConvertButton, tempConvertButton;
    private Label massResultLabel, lengthResultLabel, tempResultLabel;
    private ProgressBar massProgressBar, lengthProgressBar, tempProgressBar;

    private Runnable massConversionHandler;
    private Runnable lengthConversionHandler;
    private Runnable tempConversionHandler;

    public void setOnMassConvert(Runnable handler) {
        this.massConversionHandler = handler;
    }

    public void setOnLengthConvert(Runnable handler) {
        this.lengthConversionHandler = handler;
    }

    public void setOnTempConvert(Runnable handler) {
        this.tempConversionHandler = handler;
    }

    @FunctionalInterface
    private interface ConversionAction {
        void run() throws Exception;
    }

    public VBox getView(String username) {
        VBox mainContainer = new VBox();
        mainContainer.setStyle("-fx-background-color: #0f172a;");
        mainContainer.setPadding(new Insets(20));

        BorderPane header = createHeader(username);
        tabPane = createTabPane();
        HBox statusBar = createStatusBar();

        mainContainer.getChildren().addAll(header, tabPane, statusBar);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        return mainContainer;
    }

    private BorderPane createHeader(String username) {
        BorderPane header = new BorderPane();
        header.setPadding(new Insets(0, 0, 15, 0));
        header.setStyle("-fx-border-color: #334155; -fx-border-width: 0 0 1 0;");

        Label titleLabel = new Label("SOAP Hub - Conversor Premium");
        titleLabel.setFont(Font.font("System", FontWeight.EXTRA_BOLD, 22));
        titleLabel.setStyle("-fx-text-fill: #a855f7;");

        Label userLabel = new Label("👤 " + username);
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        userLabel.setStyle("-fx-text-fill: #e2e8f0;");

        logoutButton = new Button("Salir");
        logoutButton.setStyle(
            "-fx-background-color: #ef4444; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8;"
        );

        VBox leftBox = new VBox(2);
        Label subtitle = new Label("Cliente de Conversión SOAP");
        subtitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12;");
        leftBox.getChildren().addAll(titleLabel, subtitle);

        HBox rightBox = new HBox(15);
        rightBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        rightBox.getChildren().addAll(userLabel, logoutButton);

        header.setLeft(leftBox);
        header.setRight(rightBox);

        return header;
    }

    private TabPane createTabPane() {
        tabPane = new TabPane();
        tabPane.setStyle("-fx-background-color: transparent;");

        Tab massTab = createTab("Masa", "KILOGRAM,GRAM,POUND,OUNCE".split(","), () -> { 
            double value = Double.parseDouble(massValueField.getText());
            if (massFromCombo.getValue() == null || massToCombo.getValue() == null) 
                throw new Exception("Seleccione unidades"); 
        });
        Tab lengthTab = createTab("Longitud", "METER,KILOMETER,CENTIMETER,MILE,YARD,FOOT,INCH".split(","), () -> { 
            double value = Double.parseDouble(lengthValueField.getText());
            if (lengthFromCombo.getValue() == null || lengthToCombo.getValue() == null) 
                throw new Exception("Seleccione unidades"); 
        });
        Tab tempTab = createTab("Temperatura", "CELSIUS,FAHRENHEIT,KELVIN".split(","), () -> { 
            double value = Double.parseDouble(tempValueField.getText());
            if (tempFromCombo.getValue() == null || tempToCombo.getValue() == null) 
                throw new Exception("Seleccione unidades"); 
        });

        tabPane.getTabs().addAll(massTab, lengthTab, tempTab);
        return tabPane;
    }

    private Tab createTab(String title, String[] units, ConversionAction conversionAction) {
        Tab tab = new Tab(title);
        tab.setClosable(false);
        tab.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        VBox content = new VBox(15);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: #1e293b;");
        content.setPrefWidth(600);

        Label tabTitle = new Label("Conversión de " + title);
        tabTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        tabTitle.setStyle("-fx-text-fill: white;");

        Label valueLabel = new Label("Valor a convertir:");
        valueLabel.setStyle("-fx-text-fill: #94a3b8;");
        TextField valueField = new TextField();
        valueField.setPromptText("Ingrese el valor");
        styleTextField(valueField);

        Label fromLabel = new Label("Unidad de origen:");
        fromLabel.setStyle("-fx-text-fill: #94a3b8;");
        ComboBox<String> fromCombo = new ComboBox<>();
        fromCombo.getItems().addAll(units);
        fromCombo.setPromptText("Seleccionar");
        styleComboBox(fromCombo);

        Label toLabel = new Label("Unidad de destino:");
        toLabel.setStyle("-fx-text-fill: #94a3b8;");
        ComboBox<String> toCombo = new ComboBox<>();
        toCombo.getItems().addAll(units);
        toCombo.setPromptText("Seleccionar");
        styleComboBox(toCombo);

        Button convertButton = new Button("PROCESAR CONVERSIÓN");
        convertButton.setPrefHeight(45);
        convertButton.setStyle(
            "-fx-background-color: #6366f1; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 10;"
        );

        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setPrefWidth(200);

        Label resultLabel = new Label();
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        resultLabel.setStyle("-fx-text-fill: #22c55e;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.add(valueLabel, 0, 0);
        grid.add(valueField, 0, 1);
        grid.add(fromLabel, 0, 2);
        grid.add(fromCombo, 0, 3);
        grid.add(toLabel, 1, 2);
        grid.add(toCombo, 1, 3);
        GridPane.setColumnSpan(valueField, 2);
        GridPane.setColumnSpan(convertButton, 2);

        content.getChildren().addAll(tabTitle, grid, convertButton, progressBar, resultLabel);

        convertButton.setOnAction(e -> {
            convertButton.setDisable(true);
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            if (title.equals("Masa") && massConversionHandler != null) {
                massConversionHandler.run();
            } else if (title.equals("Longitud") && lengthConversionHandler != null) {
                lengthConversionHandler.run();
            } else if (title.equals("Temperatura") && tempConversionHandler != null) {
                tempConversionHandler.run();
            } else {
                // Fallback: local validation only
                new Thread(() -> {
                    try {
                        conversionAction.run();
                    } catch (Exception ex) {
                        javafx.application.Platform.runLater(() -> {
                            resultLabel.setStyle("-fx-text-fill: #ef4444;");
                            resultLabel.setText("Error: " + ex.getMessage());
                            convertButton.setDisable(false);
                            progressBar.setVisible(false);
                        });
                    }
                }).start();
            }
        });

        if (title.equals("Masa")) {
            massValueField = valueField; massFromCombo = fromCombo; massToCombo = toCombo;
            massConvertButton = convertButton; massResultLabel = resultLabel; massProgressBar = progressBar;
        } else if (title.equals("Longitud")) {
            lengthValueField = valueField; lengthFromCombo = fromCombo; lengthToCombo = toCombo;
            lengthConvertButton = convertButton; lengthResultLabel = resultLabel; lengthProgressBar = progressBar;
        } else {
            tempValueField = valueField; tempFromCombo = fromCombo; tempToCombo = toCombo;
            tempConvertButton = convertButton; tempResultLabel = resultLabel; tempProgressBar = progressBar;
        }

        tab.setContent(content);
        return tab;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 1 0 0 0;");

        statusLabel = new Label("Conectado al servidor SOAP");
        statusLabel.setFont(Font.font(12));
        statusLabel.setStyle("-fx-text-fill: #22c55e;");

        statusBar.getChildren().add(statusLabel);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        return statusBar;
    }

    private void styleTextField(TextField field) {
        field.setStyle(
            "-fx-background-color: #334155; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: #94a3b8; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-border-color: #475569;"
        );
    }

    private void styleComboBox(ComboBox<String> combo) {
        combo.setStyle(
            "-fx-background-color: #334155; " +
            "-fx-text-fill: white; " +
            "-fx-prompt-text-fill: #94a3b8; " +
            "-fx-background-radius: 10; " +
            "-fx-border-radius: 10; " +
            "-fx-border-color: #475569;"
        );
    }

    public TextField getMassValueField() { return massValueField; }
    public TextField getLengthValueField() { return lengthValueField; }
    public TextField getTempValueField() { return tempValueField; }

    public ComboBox<String> getMassFromCombo() { return massFromCombo; }
    public ComboBox<String> getLengthFromCombo() { return lengthFromCombo; }
    public ComboBox<String> getTempFromCombo() { return tempFromCombo; }

    public ComboBox<String> getMassToCombo() { return massToCombo; }
    public ComboBox<String> getLengthToCombo() { return lengthToCombo; }
    public ComboBox<String> getTempToCombo() { return tempToCombo; }

    public Button getMassConvertButton() { return massConvertButton; }
    public Button getLengthConvertButton() { return lengthConvertButton; }
    public Button getTempConvertButton() { return tempConvertButton; }

    public Label getMassResultLabel() { return massResultLabel; }
    public Label getLengthResultLabel() { return lengthResultLabel; }
    public Label getTempResultLabel() { return tempResultLabel; }

    public ProgressBar getMassProgressBar() { return massProgressBar; }
    public ProgressBar getLengthProgressBar() { return lengthProgressBar; }
    public ProgressBar getTempProgressBar() { return tempProgressBar; }

    public Label getStatusLabel() { return statusLabel; }

    public Button getLogoutButton() {
        return logoutButton;
    }
}