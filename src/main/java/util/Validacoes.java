package util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Validacoes {
    public boolean textoVazio(TextField txtValor) {
        // verefica se esta vazio retirando os espacos
        return !txtValor.getText().strip().isBlank();
    }

    public boolean comboBoxVazio(ComboBox<?> comboValor) {
        return !(comboValor.getValue() == null);
    }
}
