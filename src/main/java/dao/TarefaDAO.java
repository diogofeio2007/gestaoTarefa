package dao;

import model.Tarefa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TarefaDAO {
    private final Connection ligarDB;

    public TarefaDAO(Connection ligarDB) {
        this.ligarDB = ligarDB;
    }

    public void inserirTarefa(Tarefa tarefa){
        String query = "INSERT INTO tarefas (titulo, prioridade, estado, id_cat, data_limite, descricao) values (?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = ligarDB.prepareStatement(query)){
             stmt.setString(1, tarefa.getTitulo());
             stmt.setString(2, tarefa.getPrioridade());
             stmt.setBoolean(3, tarefa.getEstado());
             stmt.setInt(4, tarefa.getId_cat());
             stmt.setObject(5, tarefa.getData_limite());
             stmt.setString(6, tarefa.getDescricao());
             stmt.execute();
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }
}
