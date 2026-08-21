package controller;

import dao.LigacaoDB;
import dao.TarefaDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Tarefa;
import util.Alertas;
import util.Validacoes;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ConcluirTarefaController {
    @FXML
    private Text txtConfirmar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnConcluir;
    private Tarefa tarefa = new Tarefa();

    public void setTarefa(Tarefa tarefa){
        this.tarefa = tarefa;
        txtConfirmar.setText("Tem a certeza que pretende concluir a tarefa \"" + tarefa.getTitulo() + "\"?");
    }

    public void validarCampos(){
        Validacoes validacoes = new Validacoes();
        Alertas alertas = new Alertas();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tarefa Guardada!");
            alert.setHeaderText("Tarefa guardada com sucesso!");
            alert.showAndWait();
    }

    public void guardarEstado(){
        try(Connection conexao = LigacaoDB.conectarDB()){
        TarefaDAO tarefaDAO = new TarefaDAO(conexao);
        if(tarefaDAO.atualizarEstado(tarefa)){
            validarCampos();
        }
        else{

        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void abrirListarTarefas(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
