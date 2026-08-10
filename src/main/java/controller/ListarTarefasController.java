package controller;

import dao.LigacaoDB;
import dao.TarefaDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Tarefa;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class ListarTarefasController {
    @FXML
    private TableView<Tarefa> tblTarefas;

    @FXML
    private TableColumn<Tarefa, String> colTitulo;

    @FXML
    private TableColumn<Tarefa, String> colCategoria;

    @FXML
    private TableColumn<Tarefa, String> colPrioridade;

    @FXML
    private TableColumn<Tarefa, LocalDate> colDataLimite;

    @FXML
    private TableColumn<Tarefa, LocalDate> colDataEntrega;

    @FXML
    private TableColumn<Tarefa, String> colEstado;

    public void initialize(){
        tblTarefas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNome()));

        colPrioridade.setCellValueFactory(new PropertyValueFactory<>("prioridade"));

        colDataLimite.setCellValueFactory(new PropertyValueFactory<>("data_limite"));

        colDataEntrega.setCellValueFactory(new PropertyValueFactory<>("data_entrega"));

        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado() ? "Concluído" : "Pendente"));

        try(Connection conexao = LigacaoDB.conectarDB()){
            TarefaDAO tarefaDAO = new TarefaDAO(conexao);
            ObservableList<Tarefa> tarefas = FXCollections.observableArrayList(tarefaDAO.listarTarefas());
            tblTarefas.setItems(tarefas);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    private void abrirAdicionarTarefa(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/AdicionarTarefa.fxml")
        );

        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Adicionar Tarefa");
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
