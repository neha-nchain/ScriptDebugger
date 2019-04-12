/*

package scriptdebugger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.Breakpoints;

import java.io.IOException;

public class BreakpointsListViewCell extends ListCell<Breakpoints> {
    private final TextField textField = new TextField();
    @FXML
    public GridPane gridPaneBr;
    private FXMLLoader mLLoader;

    @FXML
    private Label label1;

    @Override
    protected void updateItem(Breakpoints breakpoints, boolean empty) {
        super.updateItem(breakpoints, empty);

        mLLoader = new FXMLLoader(getClass().getResource("/scriptdebugger/br_list_cell.fxml"));
        mLLoader.setController(this);

        try {
            mLLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (breakpoints != null) {
            label1.setText(String.valueOf(breakpoints.getBreakpoints()));
            setText(null);
            setGraphic(gridPaneBr);
        }
    }
}

*/
