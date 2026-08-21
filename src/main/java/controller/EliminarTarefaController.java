package controller;

import dao.LigacaoDB;
import dao.TarefaDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Tarefa;

import java.io.IOException;
import java.sql.Connection;

public class EliminarTarefaController {
    @FXML
    private Text txtEliminar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnEliminar;
    @FXML
    private TextField tfConfirmar;

    private Tarefa tarefa = new Tarefa();

    public void setTarefa(Tarefa tarefa){
        this.tarefa = tarefa;
        txtEliminar.setText("Tem a certeza de que pretende eliminar a tarefa \"" + tarefa.getTitulo() + "\"?");
    }

    public void eliminarTarefa(){
        String confirmar = tfConfirmar.getText();
        if(confirmar.trim().equals("Confirmar")){
            try(Connection conexao = LigacaoDB.conectarDB()){
                TarefaDAO tarefaDAO = new TarefaDAO(conexao);
                if(tarefaDAO.eliminarTarefa(tarefa)){
                    Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                    alerta.setTitle("Tarefa eliminada com sucesso");
                    alerta.showAndWait();
                }
                else {
                    Alert alerta = new Alert(Alert.AlertType.ERROR);
                    alerta.setTitle("Erro ao tentar eliminar");
                    alerta.showAndWait();
                }
            } catch (Exception e) {
                Alert alerta = new Alert(Alert.AlertType.ERROR);
                alerta.setTitle("Erro ao tentar eliminar");
                alerta.setHeaderText("Erro: " + e.getMessage());
                alerta.showAndWait();
            }
        }
        else{
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro ao tentar eliminar");
            alerta.setContentText("Deve escrever \"Confirmar\"");
            alerta.showAndWait();
        }
    }
    @FXML
    public void abrirListarTarefas(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
