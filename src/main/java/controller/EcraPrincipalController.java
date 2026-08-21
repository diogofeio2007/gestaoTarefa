package controller;

import javafx.event.ActionEvent; // Importe esta classe
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class EcraPrincipalController {

    @FXML
    private Hyperlink hlTarefas;

    @FXML
    private Hyperlink hlCategorias;

    @FXML
    private Hyperlink hlAddTarefas;

    @FXML
    private Button btnVerTarefas;

    //TODO 1 - Resolver para o Button quando queremos abrir o ListarTarefas.fxml
    @FXML
    public void abrirListarTarefas(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/ListarTarefas.fxml"));

            Stage stage = (Stage) ((Hyperlink) event.getSource()).getScene().getWindow();


            Scene novaCena = new Scene(root);

            stage.setTitle("Listar Tarefas");
            stage.setMaximized(true);
            stage.setScene(novaCena);
            stage.show();

        } catch (Exception e) {
            System.err.println("Erro ao carregar o ficheiro FXML. Verifique o caminho!");
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirAdicionarTarefa(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AdicionarTarefa.fxml"));

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setTitle("Editar Tarefa");
        stage.setScene(new Scene(root));

        stage.initModality(Modality.APPLICATION_MODAL);

        stage.showAndWait();
    }
}
