package ec.edu.grupo3.client.controller;

import ec.edu.grupo3.client.model.ConversionModel;
import ec.edu.grupo3.client.view.MainView;
import javafx.application.Platform;

public class ConversionController {

    private final ConversionModel conversionModel;
    private final MainView mainView;

    public ConversionController(MainView mainView) {
        this.conversionModel = new ConversionModel();
        this.mainView = mainView;
        setupHandlers();
        updateConnectionStatus();
    }

    private void setupHandlers() {
        mainView.setOnMassConvert(() -> handleMassConversion());
        mainView.setOnLengthConvert(() -> handleLengthConversion());
        mainView.setOnTempConvert(() -> handleTemperatureConversion());
    }

    private void updateConnectionStatus() {
        Platform.runLater(() -> {
            if (conversionModel.isServiceAvailable()) {
                mainView.getStatusLabel().setText("Conectado al servidor SOAP");
                mainView.getStatusLabel().setStyle("-fx-text-fill: #22c55e;");
            } else {
                mainView.getStatusLabel().setText("Error de conexión con el servidor");
                mainView.getStatusLabel().setStyle("-fx-text-fill: #ef4444;");
            }
        });
    }

    public void handleMassConversion() {
        executeConversion(() -> {
            double value = Double.parseDouble(mainView.getMassValueField().getText());
            String from = mainView.getMassFromCombo().getValue();
            String to = mainView.getMassToCombo().getValue();

            if (from == null || to == null) throw new Exception("Seleccione las unidades");

            var response = conversionModel.convertMass(value, from, to);
            displayResult(mainView.getMassResultLabel(), response);
        }, mainView.getMassConvertButton(), mainView.getMassProgressBar());
    }

    public void handleLengthConversion() {
        executeConversion(() -> {
            double value = Double.parseDouble(mainView.getLengthValueField().getText());
            String from = mainView.getLengthFromCombo().getValue();
            String to = mainView.getLengthToCombo().getValue();

            if (from == null || to == null) throw new Exception("Seleccione las unidades");

            var response = conversionModel.convertLength(value, from, to);
            displayResult(mainView.getLengthResultLabel(), response);
        }, mainView.getLengthConvertButton(), mainView.getLengthProgressBar());
    }

    public void handleTemperatureConversion() {
        executeConversion(() -> {
            double value = Double.parseDouble(mainView.getTempValueField().getText());
            String from = mainView.getTempFromCombo().getValue();
            String to = mainView.getTempToCombo().getValue();

            if (from == null || to == null) throw new Exception("Seleccione las unidades");

            var response = conversionModel.convertTemperature(value, from, to);
            displayResult(mainView.getTempResultLabel(), response);
        }, mainView.getTempConvertButton(), mainView.getTempProgressBar());
    }

    private void executeConversion(RunnableWithException action, javafx.scene.control.Button button, javafx.scene.control.ProgressBar progressBar) {
        new Thread(() -> {
            try {
                action.run();
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError(mainView.getMassResultLabel(), "Error: " + e.getMessage());
                });
            } finally {
                Platform.runLater(() -> {
                    button.setDisable(false);
                    progressBar.setVisible(false);
                });
            }
        }).start();
    }

    @FunctionalInterface
    private interface RunnableWithException {
        void run() throws Exception;
    }

    private void displayResult(javafx.scene.control.Label label, ec.edu.grupo3.client.generated.ConversionResponseView response) {
        Platform.runLater(() -> {
            String text = String.format("%.4f %s = %.4f %s",
                response.getInputValue(),
                response.getFromUnit(),
                response.getResultValue(),
                response.getToUnit()
            );
            label.setText(text);
            label.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 16;");
        });
    }

    private void showError(javafx.scene.control.Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 14;");
    }
}