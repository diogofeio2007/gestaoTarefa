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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

    // =========================================================
    // TABELA
    // =========================================================

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


    // =========================================================
    // FILTROS
    // =========================================================

    @FXML
    private TextField txtPesquisa;

    @FXML
    private ComboBox<String> cbFiltroCategoria;

    @FXML
    private ComboBox<String> cbFiltroPrioridade;

    @FXML
    private ComboBox<String> cbFiltroEstado;


    // =========================================================
    // PAGINAÇÃO
    // =========================================================

    @FXML
    private Pagination paginacao;

    private static final int TAREFAS_POR_PAGINA = 8;


    // =========================================================
    // LISTAS
    // =========================================================

    private ObservableList<Tarefa> tarefas =
            FXCollections.observableArrayList();

    private ObservableList<Tarefa> tarefasFiltradas =
            FXCollections.observableArrayList();

    private ArrayList<Categoria> categorias =
            new ArrayList<>();


    // =========================================================
    // INITIALIZE
    // =========================================================

    public void initialize() {

        apresentarFiltros();

        apresentarTabela();

        configurarColunaAcoes();
    }


    // =========================================================
    // COLUNA AÇÕES
    // =========================================================

    private void configurarColunaAcoes() {

        colAcao.setCellFactory(param ->
                new TableCell<Tarefa, Void>() {

                    private final Button btnEditar = new Button();
                    {
                        btnEditar.getStyleClass().add("button-tabela");
                    }
                    private final Button btnConcluir = new Button();
                    {
                        btnConcluir.getStyleClass().add("button-tabela");
                    }
                    private final Button btnEliminar = new Button();
                    {
                        btnEliminar.getStyleClass().add("button-tabela");
                    }

                    private final ImageView imgEditarView = new ImageView(new Image(getClass().getResourceAsStream("/images/editar.png")));

                    private final ImageView imgConcluirView =
                            new ImageView(
                                    new Image(
                                            getClass()
                                                    .getResourceAsStream(
                                                            "/images/concluir.png"
                                                    )
                                    )
                            );

                    private final ImageView imgEliminarView =
                            new ImageView(
                                    new Image(
                                            getClass()
                                                    .getResourceAsStream(
                                                            "/images/eliminar.png"
                                                    )
                                    )
                            );

                    private final HBox container =
                            new HBox(
                                    10,
                                    btnEditar,
                                    btnConcluir,
                                    btnEliminar
                            );

                    {
                        configurarImagem(imgEditarView);
                        configurarImagem(imgConcluirView);
                        configurarImagem(imgEliminarView);

                        btnEditar.setGraphic(imgEditarView);
                        btnConcluir.setGraphic(imgConcluirView);
                        btnEliminar.setGraphic(imgEliminarView);

                        container.setAlignment(Pos.CENTER);


                        // EDITAR
                        btnEditar.setOnAction(event -> {

                            Tarefa tarefa =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            editarTarefa(tarefa);
                        });


                        // CONCLUIR
                        btnConcluir.setOnAction(event -> {

                            Tarefa tarefa =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            concluirTarefa(tarefa);
                        });


                        // ELIMINAR
                        btnEliminar.setOnAction(event -> {

                            Tarefa tarefa =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());

                            eliminarTarefa(tarefa);
                        });
                    }


                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {

                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(container);
                        }
                    }
                }
        );
    }


    // =========================================================
    // CONFIGURAR IMAGENS
    // =========================================================

    private void configurarImagem(ImageView imagem) {

        imagem.setFitWidth(20);
        imagem.setFitHeight(20);
        imagem.setPreserveRatio(true);
    }


    // =========================================================
    // PAGINAÇÃO
    // =========================================================

    private void configurarPaginacao(
            ObservableList<Tarefa> lista
    ) {

        int numeroPaginas = (int) Math.ceil(
                (double) lista.size()
                        / TAREFAS_POR_PAGINA
        );

        paginacao.setPageCount(
                Math.max(numeroPaginas, 1)
        );

        paginacao.setCurrentPageIndex(0);

        paginacao.setPageFactory(
                paginaIndex -> {

                    mostrarPagina(
                            paginaIndex,
                            lista
                    );

                    return new VBox();
                }
        );
    }


    // =========================================================
    // MOSTRAR PÁGINA
    // =========================================================

    private void mostrarPagina(
            int pagina,
            ObservableList<Tarefa> lista
    ) {

        int inicio =
                pagina * TAREFAS_POR_PAGINA;


        if (inicio >= lista.size()) {

            tblTarefas.setItems(
                    FXCollections.observableArrayList()
            );

            return;
        }


        int fim =
                Math.min(
                        inicio + TAREFAS_POR_PAGINA,
                        lista.size()
                );


        ObservableList<Tarefa> paginaAtual =
                FXCollections.observableArrayList(
                        lista.subList(
                                inicio,
                                fim
                        )
                );


        tblTarefas.setItems(
                paginaAtual
        );
    }


    // =========================================================
    // EDITAR
    // =========================================================

    private void editarTarefa(Tarefa tarefa) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/EditarTarefa.fxml"
                            )
                    );

            Parent root = loader.load();

            EditarTarefaController controller =
                    loader.getController();

            controller.setTarefa(tarefa);


            Stage stage = new Stage();

            stage.setTitle(
                    "Editar Tarefa"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.initOwner(
                    tblTarefas
                            .getScene()
                            .getWindow()
            );

            stage.showAndWait();


            apresentarTabela();

        } catch (IOException e) {

            System.err.println(
                    "Erro ao abrir EditarTarefa.fxml:"
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // CONCLUIR
    // =========================================================

    private void concluirTarefa(Tarefa tarefa) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/ConcluirTarefa.fxml"
                            )
                    );

            Parent root = loader.load();

            ConcluirTarefaController controller =
                    loader.getController();

            controller.setTarefa(tarefa);


            Stage stage = new Stage();

            stage.setTitle(
                    "Concluir Tarefa"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.initOwner(
                    tblTarefas
                            .getScene()
                            .getWindow()
            );

            stage.showAndWait();


            apresentarTabela();

        } catch (IOException e) {

            System.err.println(
                    "Erro ao abrir ConcluirTarefa.fxml:"
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // ELIMINAR
    // =========================================================

    private void eliminarTarefa(Tarefa tarefa) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/EliminarTarefa.fxml"
                            )
                    );

            Parent root = loader.load();

            EliminarTarefaController controller =
                    loader.getController();

            controller.setTarefa(tarefa);


            Stage stage = new Stage();

            stage.setTitle(
                    "Eliminar Tarefa"
            );

            stage.setScene(
                    new Scene(root)
            );

            stage.initModality(
                    Modality.APPLICATION_MODAL
            );

            stage.initOwner(
                    tblTarefas
                            .getScene()
                            .getWindow()
            );

            stage.showAndWait();


            apresentarTabela();

        } catch (IOException e) {

            System.err.println(
                    "Erro ao abrir EliminarTarefa.fxml:"
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // FILTRAR
    // =========================================================

    @FXML
    private void filtrarTabela() {

        String texto =
                txtPesquisa
                        .getText()
                        .trim()
                        .toLowerCase();


        String categoriaSelecionada =
                cbFiltroCategoria.getValue();

        String prioridadeSelecionada =
                cbFiltroPrioridade.getValue();

        String estadoSelecionado =
                cbFiltroEstado.getValue();


        tarefasFiltradas =
                FXCollections.observableArrayList();


        for (Tarefa tarefa : tarefas) {

            boolean valido = true;


            // PESQUISA
            if (!texto.isEmpty()) {

                if (
                        tarefa.getTitulo() == null
                                ||
                                !tarefa
                                        .getTitulo()
                                        .toLowerCase()
                                        .contains(texto)
                ) {

                    valido = false;
                }
            }


            // CATEGORIA
            if (
                    categoriaSelecionada != null
                            &&
                            !categoriaSelecionada.equals(
                                    "Todas as Categorias"
                            )
            ) {

                if (
                        tarefa.getCategoria() == null
                                ||
                                !tarefa
                                        .getCategoria()
                                        .getNome()
                                        .equals(
                                                categoriaSelecionada
                                        )
                ) {

                    valido = false;
                }
            }


            // PRIORIDADE
            if (
                    prioridadeSelecionada != null
                            &&
                            !prioridadeSelecionada.equals(
                                    "Todas as Prioridades"
                            )
            ) {

                if (
                        !tarefa
                                .getPrioridade()
                                .equals(
                                        prioridadeSelecionada
                                )
                ) {

                    valido = false;
                }
            }


            // ESTADO
            if (
                    estadoSelecionado != null
                            &&
                            !estadoSelecionado.equals(
                                    "Todos os Estados"
                            )
            ) {

                boolean estado =
                        tarefa.getEstado();


                if (
                        estadoSelecionado.equals(
                                "Concluído"
                        )
                                &&
                                !estado
                ) {

                    valido = false;
                }


                if (
                        estadoSelecionado.equals(
                                "Pendente"
                        )
                                &&
                                estado
                ) {

                    valido = false;
                }
            }


            if (valido) {

                tarefasFiltradas.add(
                        tarefa
                );
            }
        }


        configurarPaginacao(
                tarefasFiltradas
        );
    }


    // =========================================================
    // LIMPAR FILTROS
    // =========================================================

    @FXML
    private void limparFiltros() {

        txtPesquisa.clear();

        cbFiltroCategoria
                .getSelectionModel()
                .selectFirst();

        cbFiltroPrioridade
                .getSelectionModel()
                .selectFirst();

        cbFiltroEstado
                .getSelectionModel()
                .selectFirst();


        configurarPaginacao(
                tarefas
        );
    }


    // =========================================================
    // APRESENTAR FILTROS
    // =========================================================

    private void apresentarFiltros() {

        // PRIORIDADES

        cbFiltroPrioridade
                .getItems()
                .setAll(
                        "Todas as Prioridades",
                        "Baixa",
                        "Media",
                        "Alta"
                );


        // CATEGORIAS

        try (
                Connection conexao =
                        LigacaoDB.conectarDB()
        ) {

            CategoriaDAO categoriaDAO =
                    new CategoriaDAO(conexao);


            categorias.addAll(
                    categoriaDAO.listarCategorias()
            );


            cbFiltroCategoria
                    .getItems()
                    .add(
                            "Todas as Categorias"
                    );


            for (
                    Categoria categoria :
                    categorias
            ) {

                cbFiltroCategoria
                        .getItems()
                        .add(
                                categoria.getNome()
                        );
            }

        } catch (SQLException e) {

            System.err.println(
                    "Erro ao carregar categorias:"
            );

            e.printStackTrace();
        }


        // ESTADOS

        cbFiltroEstado
                .getItems()
                .setAll(
                        "Todos os Estados",
                        "Concluído",
                        "Pendente"
                );


        // SELECIONAR PRIMEIRO

        cbFiltroCategoria
                .getSelectionModel()
                .selectFirst();

        cbFiltroPrioridade
                .getSelectionModel()
                .selectFirst();

        cbFiltroEstado
                .getSelectionModel()
                .selectFirst();
    }


    // =========================================================
    // APRESENTAR TABELA
    // =========================================================

    private void apresentarTabela() {

        tblTarefas.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN
        );


        // TÍTULO

        colTitulo.setCellValueFactory(
                new PropertyValueFactory<>(
                        "titulo"
                )
        );


        // CATEGORIA

        colCategoria.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getCategoria()
                                        .getNome()
                        )
        );


        // PRIORIDADE

        colPrioridade.setCellValueFactory(
                new PropertyValueFactory<>(
                        "prioridade"
                )
        );


        // DATA LIMITE

        colDataLimite.setCellValueFactory(
                new PropertyValueFactory<>(
                        "data_limite"
                )
        );


        // DATA ENTREGA

        colDataEntrega.setCellValueFactory(
                new PropertyValueFactory<>(
                        "data_entrega"
                )
        );


        // ESTADO

        colEstado.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData
                                        .getValue()
                                        .getEstado()
                                        ? "Concluído"
                                        : "Pendente"
                        )
        );


        // CARREGAR TAREFAS

        try (
                Connection conexao =
                        LigacaoDB.conectarDB()
        ) {

            TarefaDAO tarefaDAO =
                    new TarefaDAO(conexao);


            tarefas =
                    FXCollections.observableArrayList(
                            tarefaDAO.listarTarefas()
                    );


            configurarPaginacao(
                    tarefas
            );


        } catch (SQLException e) {

            System.err.println(
                    "Erro ao carregar tarefas:"
            );

            e.printStackTrace();
        }
    }


    // =========================================================
    // ABRIR ADICIONAR TAREFA
    // =========================================================

    @FXML
    private void abrirAdicionarTarefa(
            ActionEvent event
    ) {

        try {

            FXMLLoader loader =
                    new FXMLLoader(
                            getClass().getResource(
                                    "/fxml/AdicionarTarefa.fxml"
                            )
                    );

            Parent root = loader.load();


            Stage stage = new Stage();

            stage.setTitle(
                    "Adicionar Tarefa"
            );

            stage.setScene(
                    new Scene(root)
            );


            stage.initModality(
                    Modality.APPLICATION_MODAL
            );


            Stage janelaPrincipal =
                    (Stage)
                            ((Control) event.getSource())
                                    .getScene()
                                    .getWindow();


            stage.initOwner(
                    janelaPrincipal
            );


            stage.showAndWait();


            apresentarTabela();


        } catch (IOException e) {

            System.err.println(
                    "Erro ao abrir AdicionarTarefa.fxml:"
            );

            e.printStackTrace();
        }
    }
}