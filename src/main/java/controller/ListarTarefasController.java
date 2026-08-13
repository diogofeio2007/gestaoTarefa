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
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
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
    private TableColumn<Tarefa, Void> colAcao;

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
        apresentarTabela();
        configurarColunaAcoes();
    }

    private void configurarColunaAcoes() {

        colAcao.setCellFactory(param -> new TableCell<Tarefa, Void>() {

            private final Button btnEditar = new Button();
            private final Button btnConcluir = new Button();
            private final Button btnEliminar = new Button();

            private final ImageView imgEditarView = new ImageView(new Image(getClass().getResourceAsStream("/images/editar.png")));

            private final ImageView imgConcluirView = new ImageView(new Image(getClass().getResourceAsStream("/images/concluir.png")));

            private final ImageView imgEliminarView = new ImageView(new Image(getClass().getResourceAsStream("/images/eliminar.png")));

            private final HBox container = new HBox(10, btnEditar, btnConcluir, btnEliminar);
            {
                configurarImagem(imgEditarView);
                configurarImagem(imgConcluirView);
                configurarImagem(imgEliminarView);

                btnEditar.setGraphic(imgEditarView);
                btnConcluir.setGraphic(imgConcluirView);
                btnEliminar.setGraphic(imgEliminarView);

                container.setAlignment(Pos.CENTER);

                // Ação do botão editar
                btnEditar.setOnAction(event -> {

                    Tarefa tarefa = getTableView()
                            .getItems()
                            .get(getIndex());

                    editarTarefa(tarefa);
                });

                // Ação do botão concluir
                btnConcluir.setOnAction(event -> {

                    Tarefa tarefa = getTableView()
                            .getItems()
                            .get(getIndex());

                    concluirTarefa(tarefa);
                });

                // Ação do botão eliminar
                btnEliminar.setOnAction(event -> {

                    Tarefa tarefa = getTableView()
                            .getItems()
                            .get(getIndex());

                    eliminarTarefa(tarefa);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void configurarImagem(ImageView imagem) {
        imagem.setFitWidth(20);
        imagem.setFitHeight(20);
        imagem.setPreserveRatio(true);
    }

    @FXML
    private void editarTarefa(Tarefa tarefa){
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/EditarTarefa.fxml")
            );

            Parent root = loader.load();

            EditarTarefaController controller = loader.getController();

            // Envia a tarefa para o controller
            controller.setTarefa(tarefa);

            Stage stage = new Stage();
            stage.setTitle("Editar Tarefa");
            stage.setScene(new Scene(root));

            // Torna a janela modal
            stage.initModality(Modality.APPLICATION_MODAL);

            // Impede interação com a janela principal
            stage.initOwner(tblTarefas.getScene().getWindow());

            stage.showAndWait();

            // Depois de fechar o modal, atualiza a tabela
            apresentarTabela();


        } catch (IOException e) {
            System.err.println("Erro ao abrir EditarTarefa.fxml: " + e.getMessage());
        }
    }

    private void concluirTarefa(Tarefa tarefa) {
        System.out.println(
                "Concluir tarefa: " + tarefa.getTitulo()
        );

    }

    private void eliminarTarefa(Tarefa tarefa) {

        System.out.println(
                "Eliminar tarefa: " + tarefa.getTitulo()
        );

    }

    @FXML
    private void filtrarTabela() {
        String texto = txtPesquisa.getText().trim();
        String categoriaSelecionada = cbFiltroCategoria.getValue();
        String prioridadeSelecionada = cbFiltroPrioridade.getValue();
        String estadoSelecionado = cbFiltroEstado.getValue();

        tarefasFiltradas =
                FXCollections.observableArrayList();

        for (Tarefa tarefa : tarefas) {
            boolean valido = true;

            if(!texto.isEmpty()){
                if (!tarefa.getTitulo().toLowerCase().contains(texto.toLowerCase())) {
                    valido = false;
                }
            }

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
        txtPesquisa.clear();
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

        cbFiltroCategoria.getSelectionModel().selectFirst();
        cbFiltroPrioridade.getSelectionModel().selectFirst();
        cbFiltroEstado.getSelectionModel().selectFirst();
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
