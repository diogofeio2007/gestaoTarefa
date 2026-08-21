package dao;

import model.Categoria;
import model.Tarefa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
             stmt.setInt(4, tarefa.getCategoria().getId_cat());
             stmt.setObject(5, tarefa.getData_limite());
             stmt.setString(6, tarefa.getDescricao());
             stmt.execute();
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }
    public void atualizarTarefa(Tarefa tarefa){
        String query = "UPDATE tarefas SET (titulo, prioridade, estado, id_cat, data_limite, descricao) = (?, ?, ?, ?, ?, ?) WHERE id_tarefa = ?;";
        try(PreparedStatement stmt = ligarDB.prepareStatement(query)){
            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getPrioridade());
            stmt.setBoolean(3, tarefa.getEstado());
            stmt.setInt(4, tarefa.getCategoria().getId_cat());
            stmt.setObject(5, tarefa.getData_limite());
            stmt.setString(6, tarefa.getDescricao());
            stmt.setInt(7, tarefa.getId_tarefa());
            stmt.execute();
        } catch (SQLException e){
            System.err.println(e.getMessage());
        }
    }

    public ArrayList<Tarefa> listarTarefas(){
        Categoria categoria = new Categoria();

        String query = "SELECT tarefas.id_tarefa, tarefas.titulo, tarefas.id_cat, categorias.nome, tarefas.prioridade, tarefas.estado, tarefas.data_criacao, tarefas.data_limite, tarefas.data_entrega, tarefas.descricao FROM tarefas JOIN categorias ON tarefas.id_cat = categorias.id_cat;";

        try(PreparedStatement stmt = ligarDB.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            ArrayList<Tarefa> tarefas = new ArrayList<>();
            while(rs.next()){
                Tarefa tarefa = new Tarefa();
                tarefa.setId_tarefa(rs.getInt("id_tarefa"));
                tarefa.setTitulo(rs.getString("titulo"));
                tarefa.setPrioridade(rs.getString("prioridade"));
                tarefa.setEstado(rs.getBoolean("estado"));

                categoria.setId_cat(rs.getInt("id_cat"));
                categoria.setNome(rs.getString("nome"));

                tarefa.setCategoria(categoria);

                tarefa.setData_criacao(rs.getDate("data_criacao").toLocalDate());
                if(rs.getDate("data_limite") != null) {
                    tarefa.setData_limite(rs.getDate("data_limite").toLocalDate());
                }
                if(rs.getDate("data_entrega") != null) {
                    tarefa.setData_entrega(rs.getDate("data_entrega").toLocalDate());
                }
                tarefa.setDescricao(rs.getString("descricao"));

                tarefas.add(tarefa);
            }
            return tarefas;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public boolean atualizarEstado(Tarefa tarefa){
        String query = "UPDATE tarefas SET (estado, data_entrega) = (true, CURRENT_DATE) WHERE id_tarefa = ?;";
        try(PreparedStatement stmt = ligarDB.prepareStatement(query)){
            stmt.setInt(1, tarefa.getId_tarefa());
            stmt.execute();
            return true;
        }
        catch (SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean eliminarTarefa(Tarefa tarefa){
        String query = "DELETE FROM tarefas WHERE id_tarefa = ?;";
        try(PreparedStatement stmt = ligarDB.prepareStatement(query)){
            stmt.setInt(1, tarefa.getId_tarefa());
            stmt.execute();
            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}
