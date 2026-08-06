package controller;

import dao.CategoriaDAO;
import dao.LigacaoDB;
import dao.TarefaDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import model.Categoria;
import model.Tarefa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdicionarTarefaController {
    @FXML
    private TextField txtTitulo;
    @FXML
    private ComboBox<String> cbPrioridade;
    @FXML
    private ComboBox<String> cbCategoria;
    @FXML
    private DatePicker dpDataLimite;
    @FXML
    private TextArea txtDescricao;

    ArrayList<Categoria> categorias = new ArrayList<>();

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

    public void guardarTarefa(){
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(txtTitulo.getText());
        tarefa.setPrioridade(cbPrioridade.getValue());
        tarefa.setData_limite(dpDataLimite.getValue());
        tarefa.setDescricao(txtDescricao.getText());
        for(Categoria categoria : categorias) {
            if(cbCategoria.getValue().equals(categoria.getNome())){
                tarefa.setId_cat(categoria.getId_cat());
            }
        }

        try(Connection conexao = LigacaoDB.conectarDB()){
            TarefaDAO tarefaDAO =  new TarefaDAO(conexao);
            tarefaDAO.inserirTarefa(tarefa);

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
