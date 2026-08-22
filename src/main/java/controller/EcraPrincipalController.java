package controller;

import javafx.event.ActionEvent; // Importe esta classe
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EcraPrincipalController {

    @FXML
    private Button hlTarefas;

    @FXML
    private Button hlCategorias;

    @FXML
    private Button hlAddTarefas;

    @FXML
    private Button btnVerTarefas;

    @FXML
    private BorderPane bpMain;

    @FXML
    public void abrirListarTarefas(ActionEvent event) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListarTarefas.fxml"));

            Stage janelaAtual = (Stage) ((Control)event.getSource()).getScene().getWindow();

            Scene scene = new Scene(loader.load());


            Stage stage = new Stage();
            stage.setTitle("Gestor de Tarefas");
            stage.setMaximized(true);
            stage.setScene(scene);

            stage.show();
            janelaAtual.close();

        } catch (IOException e) {
            System.err.println("Erro ao abrir AdicionarTarefa.fxml:");
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirAdicionarTarefa() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdicionarTarefa.fxml"));

            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Editar Tarefa");
            stage.setScene(new Scene(root));

            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
