package controller;

import dao.CategoriaDAO;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Categoria;
import model.Tarefa;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

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

    @FXML
    private TextField txtPesquisa;

    @FXML
    private ComboBox<String> cbFiltroCategoria;

    @FXML
    private ComboBox<String> cbFiltroPrioridade;

    @FXML
    private ComboBox<String> cbFiltroEstado;


    ObservableList<Tarefa> tarefas;
    ObservableList<Tarefa> tarefasFiltradas;
    ArrayList<Categoria> categorias =  new ArrayList<>();

    public void initialize(){
        apresentarFiltros();

            cbFiltroCategoria.getSelectionModel().selectFirst();
        cbFiltroPrioridade.getSelectionModel().selectFirst();
        cbFiltroEstado.getSelectionModel().selectFirst();

        apresentarTabela();
    }

    @FXML
    private void filtrarTabela() {

        String categoriaSelecionada =
                cbFiltroCategoria.getValue();

        String prioridadeSelecionada =
                cbFiltroPrioridade.getValue();

        String estadoSelecionado =
                cbFiltroEstado.getValue();

        ObservableList<Tarefa> tarefasFiltradas =
                FXCollections.observableArrayList();

        for (Tarefa tarefa : tarefas) {
            boolean valido = true;

            if (categoriaSelecionada != null && !categoriaSelecionada.equals(cbFiltroCategoria.getItems().getFirst())) {
                if (!tarefa.getCategoria().getNome().equals(categoriaSelecionada)) {
                    valido = false;
                }
            }

            if (prioridadeSelecionada != null && !prioridadeSelecionada.equals(cbFiltroPrioridade.getItems().getFirst())) {
                if (!tarefa.getPrioridade().equals(prioridadeSelecionada)) {
                    valido = false;
                }
            }

            if (estadoSelecionado != null && !estadoSelecionado.equals(cbFiltroEstado.getItems().getFirst())) {
                boolean estado = tarefa.getEstado();

                if (estadoSelecionado.equals("Concluído") && !estado) {
                    valido = false;
                }

                if (estadoSelecionado.equals("Pendente") && estado) {
                    valido = false;
                }
            }

            if (valido) {
                tarefasFiltradas.add(tarefa);
            }
        }

        tblTarefas.setItems(tarefasFiltradas);
    }

    @FXML
    private void limparFiltros() {
        cbFiltroCategoria.getSelectionModel().selectFirst();
        cbFiltroPrioridade.getSelectionModel().selectFirst();
        cbFiltroEstado.getSelectionModel().selectFirst();

        tblTarefas.setItems(tarefas);
    }

    private void apresentarFiltros(){
        cbFiltroPrioridade.getItems().setAll("Todas as Prioridades", "Baixa", "Media", "Alta");
        try(Connection conexao = LigacaoDB.conectarDB()){
            CategoriaDAO categoriaDAO = new CategoriaDAO(conexao);
            categorias.addAll(categoriaDAO.listarCategorias());

            cbFiltroCategoria.getItems().add("Todas as Categorias");
            for(Categoria categoria : categorias){
                cbFiltroCategoria.getItems().add(categoria.getNome());
            }
        } catch(SQLException e){
            System.err.println(e.getMessage());
        }

        cbFiltroEstado.getItems().setAll("Todos os Estados", "Concluído", "Pendente");
    }

    private void apresentarTabela(){
        tblTarefas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));

        colCategoria.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategoria().getNome()));

        colPrioridade.setCellValueFactory(new PropertyValueFactory<>("prioridade"));

        colDataLimite.setCellValueFactory(new PropertyValueFactory<>("data_limite"));

        colDataEntrega.setCellValueFactory(new PropertyValueFactory<>("data_entrega"));

        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEstado() ? "Concluído" : "Pendente"));

        try(Connection conexao = LigacaoDB.conectarDB()){
            TarefaDAO tarefaDAO = new TarefaDAO(conexao);
            tarefas = FXCollections.observableArrayList(tarefaDAO.listarTarefas());

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
