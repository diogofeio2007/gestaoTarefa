package util;

import javafx.scene.control.Label;

public class Alertas {
    // Tornar Visivel a label de Erro
    public void mostrarLabel(Label label){
        label.setVisible(true);
        label.setManaged(true);
    }

    public void ocultarLabel(Label label){
        label.setVisible(false);
        label.setManaged(false);
    }
}
