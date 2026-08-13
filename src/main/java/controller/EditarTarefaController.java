package controller;

import dao.CategoriaDAO;
import dao.LigacaoDB;
import dao.TarefaDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Categoria;
import model.Tarefa;
import util.Alertas;
import util.Validacoes;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditarTarefaController {
    @FXML
    private TextField txtTitulo;
    @FXML
    private Label lbErroTitulo;
    @FXML
    private ComboBox<String> cbPrioridade;
    @FXML
    private Label lbErroPrioridade;
    @FXML
    private ComboBox<String> cbCategoria;
    @FXML
    private Label lbErroCategoria;
    @FXML
    private DatePicker dpDataLimite;
    @FXML
    private TextArea txtDescricao;
    @FXML
    private Button btnCancelar;

    ArrayList<Categoria> categorias = new ArrayList<>();
    private Tarefa tarefa;

    public void setTarefa(Tarefa tarefa){
        this.tarefa = tarefa;
        txtTitulo.setText(tarefa.getTitulo());

        cbCategoria.setValue(tarefa.getCategoria().getNome());

        cbPrioridade.setValue(tarefa.getPrioridade());

        dpDataLimite.setValue(tarefa.getData_limite());

        txtDescricao.setText(tarefa.getDescricao());
    }

    public void initialize() {
        try(Connection conexao = LigacaoDB.conectarDB()){
            CategoriaDAO categoriaDAO = new CategoriaDAO(conexao);
            categorias.addAll(categoriaDAO.listarCategorias());
        } catch (SQLException e){
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }

        for(Categoria categoria : categorias) {
            cbCategoria.getItems().add(categoria.getNome());
        }
        cbPrioridade.getItems().setAll("Baixa", "Média", "Alta");
    }

    public boolean validarCampos(){
        Validacoes validacoes = new Validacoes();
        Alertas alertas = new Alertas();
        boolean validarTitulo = (validacoes.textoVazio(txtTitulo)) ?  true : false;
        boolean validarCategoria = (validacoes.comboBoxVazio(cbCategoria)) ?  true : false;
        boolean validarPrioridade = (validacoes.comboBoxVazio(cbPrioridade)) ?  true : false;

        String mensagemErro = "";

        if(!validarTitulo){
            alertas.mostrarLabel(lbErroTitulo);
            mensagemErro += "Campo Título Vazio!\n";
        }
        else{
            alertas.ocultarLabel(lbErroTitulo);
        }

        if(!validarCategoria){
            alertas.mostrarLabel(lbErroCategoria);
            mensagemErro += "Campo Categoria Vazio!\n";
        }
        else{
            alertas.ocultarLabel(lbErroCategoria);
        }

        if(!validarPrioridade){
            alertas.mostrarLabel(lbErroPrioridade);
            mensagemErro += "Campo Prioridade Vazio!\n";
        }
        else{
            alertas.ocultarLabel(lbErroPrioridade);
        }

        if(validarTitulo && validarCategoria && validarPrioridade){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tarefa Guardada!");
            alert.setHeaderText("Tarefa guardada com sucesso!");
            alert.showAndWait();
            return true;
        }
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING);

            alert.setTitle("Dados incompletos");
            alert.setHeaderText("Não foi possível guardar a tarefa.");
            alert.setContentText(
                    mensagemErro
            );

            alert.showAndWait();
            return false;
        }

    }

    public void guardarTarefa(){
        if(!validarCampos()) {
            return;
        }

        Tarefa tarefaAtualizada = new Tarefa();
        tarefaAtualizada.setId_tarefa(tarefa.getId_tarefa());
        tarefaAtualizada.setTitulo(txtTitulo.getText());
        tarefaAtualizada.setPrioridade(cbPrioridade.getValue());
        tarefaAtualizada.setData_limite(dpDataLimite.getValue());
        tarefaAtualizada.setDescricao(txtDescricao.getText());
        for(Categoria categoria : categorias) {
            if(cbCategoria.getValue().equals(categoria.getNome())){
                tarefaAtualizada.setCategoria(categoria);
            }
        }
        try(Connection conexao = LigacaoDB.conectarDB()){
            TarefaDAO tarefaDAO =  new TarefaDAO(conexao);
            tarefaDAO.atualizarTarefa(tarefaAtualizada);

        } catch (SQLException e) {
            System.err.println("Erro ao conectar a base de dados: " + e.getMessage());
        }
    }

    @FXML
    public void abrirListarTarefas(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();    }
}
