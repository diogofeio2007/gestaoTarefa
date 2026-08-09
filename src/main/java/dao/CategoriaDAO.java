package dao;

import model.Categoria;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoriaDAO {
    private final Connection ligarDB;
    private ArrayList<Categoria> categorias = new ArrayList<>();

    public CategoriaDAO(Connection ligarDB) {
        this.ligarDB = ligarDB;
    }
    /* todo Caso vá criar wireframe para inserir Categoria
    public void insirirCategoria(Categoria categoria) {
        String query = "INSERT INTO categorias(nome) VALUES (?)";
        try(PreparedStatement stmt = ligarDB.prepareStatement(query)){
            stmt.setString(1, categoria.getNome());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    */
    public ArrayList<Categoria> listarCategorias() {
        String query = "SELECT * FROM categorias";
        try(PreparedStatement stmt = ligarDB.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId_cat(rs.getInt("id_cat"));
                categoria.setNome(rs.getString("nome"));
                categorias.add(categoria);
            }
            return categorias;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }


}
