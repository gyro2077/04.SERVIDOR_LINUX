package ec.edu.grupo3.client;

import ec.edu.grupo3.client.generated.ConversionService;
import ec.edu.grupo3.client.generated.ConversionSoapWS;
import ec.edu.grupo3.client.generated.ConversionResponse;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;

/**
 * Cliente Desktop con JavaFX para el Servicio de Conversión de Unidades SOAP
 */
public class DesktopClient extends Application {

    private static final String WSDL_URL = "http://localhost:8080/04.SERVIDOR/conversion?wsdl";

    // Campos por cada pestaña
    private TextField massValueField, lengthValueField, tempValueField;
    private ComboBox<String> massFromCombo, lengthFromCombo, tempFromCombo;
    private ComboBox<String> massToCombo, lengthToCombo, tempToCombo;
    private Button massConvertButton, lengthConvertButton, tempConvertButton;
    private Label massResultLabel, lengthResultLabel, tempResultLabel;
    private ProgressBar massProgressBar, lengthProgressBar, tempProgressBar;
    private Label statusLabel;

    private ConversionSoapWS soapService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Cliente de Conversión de Unidades - SOAP Service");

        initializeSoapService();

        TabPane tabPane = createTabPane();

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
            createHeader(),
            tabPane,
            createStatusBar()
        );

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        checkServerConnection();
    }

    private void initializeSoapService() {
        try {
            ConversionService service = new ConversionService();
            soapService = service.getConversionSoapWSPort();
            System.out.println("Servicio SOAP inicializado");
        } catch (Exception e) {
            System.err.println("Error al inicializar servicio SOAP: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox createHeader() {
        VBox header = new VBox(5);
        Label titleLabel = new Label("Cliente de Conversión de Unidades");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        Label descLabel = new Label("Servicio Web SOAP - Grupo 3");
        descLabel.setFont(Font.font("System", 12));
        header.getChildren().addAll(titleLabel, descLabel);
        return header;
    }

    private TabPane createTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(
            createMassTab(),
            createLengthTab(),
            createTemperatureTab()
        );
        return tabPane;
    }

    private Tab createMassTab() {
        Tab tab = new Tab("Masa");
        tab.setClosable(false);

        VBox form = createForm(
            "Conversión de Masa",
            new String[]{"KILOGRAM", "GRAM", "POUND", "OUNCE"},
            valueField -> massValueField = valueField,
            fromCombo -> massFromCombo = fromCombo,
            toCombo -> massToCombo = toCombo,
            convertBtn -> massConvertButton = convertBtn,
            resultLbl -> massResultLabel = resultLbl,
            progressBar -> massProgressBar = progressBar,
            this::executeMassConversion
        );

        tab.setContent(form);
        return tab;
    }

    private Tab createLengthTab() {
        Tab tab = new Tab("Longitud");
        tab.setClosable(false);

        VBox form = createForm(
            "Conversión de Longitud",
            new String[]{"METER", "KILOMETER", "CENTIMETER", "MILE", "YARD", "FOOT", "INCH"},
            valueField -> lengthValueField = valueField,
            fromCombo -> lengthFromCombo = fromCombo,
            toCombo -> lengthToCombo = toCombo,
            convertBtn -> lengthConvertButton = convertBtn,
            resultLbl -> lengthResultLabel = resultLbl,
            progressBar -> lengthProgressBar = progressBar,
            this::executeLengthConversion
        );

        tab.setContent(form);
        return tab;
    }

    private Tab createTemperatureTab() {
        Tab tab = new Tab("Temperatura");
        tab.setClosable(false);

        VBox form = createForm(
            "Conversión de Temperatura",
            new String[]{"CELSIUS", "FAHRENHEIT", "KELVIN"},
            valueField -> tempValueField = valueField,
            fromCombo -> tempFromCombo = fromCombo,
            toCombo -> tempToCombo = toCombo,
            convertBtn -> tempConvertButton = convertBtn,
            resultLbl -> tempResultLabel = resultLbl,
            progressBar -> tempProgressBar = progressBar,
            this::executeTemperatureConversion
        );

        tab.setContent(form);
        return tab;
    }

    private interface FieldSetter<T> {
        void accept(T field);
    }

    private interface ConversionCallback {
        void run() throws Exception;
    }

    private VBox createForm(
            String title,
            String[] units,
            FieldSetter<TextField> valueSetter,
            FieldSetter<ComboBox<String>> fromSetter,
            FieldSetter<ComboBox<String>> toSetter,
            FieldSetter<Button> buttonSetter,
            FieldSetter<Label> resultSetter,
            FieldSetter<ProgressBar> progressSetter,
            ConversionCallback callback
    ) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(20));

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        TextField valueField = new TextField();
        valueField.setPromptText("Valor a convertir");
        valueSetter.accept(valueField);

        ComboBox<String> fromCombo = new ComboBox<>();
        fromCombo.getItems().addAll(units);
        fromCombo.setPromptText("Unidad de origen");
        fromSetter.accept(fromCombo);

        ComboBox<String> toCombo = new ComboBox<>();
        toCombo.getItems().addAll(units);
        toCombo.setPromptText("Unidad de destino");
        toSetter.accept(toCombo);

        Button convertButton = new Button("Convertir");
        convertButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 16px;");
        buttonSetter.accept(convertButton);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setPrefWidth(200);
        progressSetter.accept(progressBar);

        Label resultLabel = new Label("Resultado: ");
        resultLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        resultSetter.accept(resultLabel);

        convertButton.setOnAction(e -> {
            convertButton.setDisable(true);
            progressBar.setVisible(true);
            progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);

            CompletableFuture.runAsync(() -> {
                try {
                    callback.run();
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        showError(resultLabel, "Error: " + ex.getMessage());
                        convertButton.setDisable(false);
                        progressBar.setVisible(false);
                    });
                }
            });
        });

        form.getChildren().addAll(
            titleLabel,
            new Label("Valor:"), valueField,
            new Label("De:"), fromCombo,
            new Label("A:"), toCombo,
            convertButton,
            progressBar,
            resultLabel
        );

        return form;
    }

    private void executeMassConversion() {
        double value = Double.parseDouble(massValueField.getText());
        String from = massFromCombo.getValue();
        String to = massToCombo.getValue();

        if (from == null || to == null) {
            throw new IllegalArgumentException("Seleccione unidades");
        }

        ConversionResponse response = soapService.convertMass(value, from, to);

        Platform.runLater(() -> {
            displayResult(massResultLabel, response);
            massConvertButton.setDisable(false);
            massProgressBar.setVisible(false);
        });
    }

    private void executeLengthConversion() {
        double value = Double.parseDouble(lengthValueField.getText());
        String from = lengthFromCombo.getValue();
        String to = lengthToCombo.getValue();

        if (from == null || to == null) {
            throw new IllegalArgumentException("Seleccione unidades");
        }

        ConversionResponse response = soapService.convertLength(value, from, to);

        Platform.runLater(() -> {
            displayResult(lengthResultLabel, response);
            lengthConvertButton.setDisable(false);
            lengthProgressBar.setVisible(false);
        });
    }

    private void executeTemperatureConversion() {
        double value = Double.parseDouble(tempValueField.getText());
        String from = tempFromCombo.getValue();
        String to = tempToCombo.getValue();

        if (from == null || to == null) {
            throw new IllegalArgumentException("Seleccione unidades");
        }

        ConversionResponse response = soapService.convertTemperature(value, from, to);

        Platform.runLater(() -> {
            displayResult(tempResultLabel, response);
            tempConvertButton.setDisable(false);
            tempProgressBar.setVisible(false);
        });
    }

    private void displayResult(Label label, ConversionResponse response) {
        String text = String.format("%.4f %s = %.4f %s",
            response.getInputValue(),
            response.getFromUnit(),
            response.getResultValue(),
            response.getToUnit()
        );
        label.setText(text);
        label.setStyle("-fx-text-fill: #2E7D32;");
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #C62828;");
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(10));
        statusBar.setStyle("-fx-background-color: #F5F5F5;");

        statusLabel = new Label("Conectando...");
        statusLabel.setFont(Font.font(11));

        statusBar.getChildren().add(statusLabel);
        HBox.setHgrow(statusLabel, Priority.ALWAYS);

        return statusBar;
    }

    private void checkServerConnection() {
        CompletableFuture.runAsync(() -> {
            try {
                java.net.URL url = new java.net.URL(WSDL_URL);
                url.openConnection().connect();

                Platform.runLater(() -> {
                    statusLabel.setText("Conectado al servidor SOAP");
                    statusLabel.setStyle("-fx-text-fill: #2E7D32;");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error de conexion: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #C62828;");
                });
            }
        });
    }
}