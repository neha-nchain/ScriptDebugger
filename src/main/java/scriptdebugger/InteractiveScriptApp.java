package scriptdebugger;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Context;

import java.util.List;

import static scriptdebugger.InteractiveScriptAppController.borderPane;

public class InteractiveScriptApp extends Application {

    private static final String APP_NAME = "Interactive Script Debugger";
    private static InteractiveScriptApp instance;
   // private static BorderPane borderPane;
    private Stage scriptWindow;
    private InteractiveScriptAppController controller;
    private TableView tableview;

    @FXML
    public GridPane gridPaneStack;

    @FXML
    public Label remainingScriptLabel;
    @FXML
    public Label stackLabel;

    @FXML
    public TextField tbScriptSig;
    @FXML
    public Button debugBtn;

    InteractiveScriptAppController interactiveScriptAppController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        interactiveScriptAppController = new InteractiveScriptAppController();
        FXMLLoader fxmlLoader= new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/scriptdebugger/interactive_script_debugger.fxml"));
       // fxmlLoader.setController(interactiveScriptAppController);
        borderPane = fxmlLoader.<BorderPane>load();

        primaryStage.setTitle(APP_NAME);
        primaryStage.setScene(new Scene(borderPane, 800, 500));
      // tableview = new TableView();
        primaryStage.show();
    }

/*
    public void addStack(List stackItems) {
        gridPaneStack = new GridPane();
        gridPaneStack.setAlignment(Pos.TOP_RIGHT);
        gridPaneStack.setHgap(10);
        gridPaneStack.setVgap(10);
        gridPaneStack.setPadding(new Insets(20,0,20,0));
        remainingScriptLabel = new Label();
        remainingScriptLabel.setText(Context.getInstance().getStackItemsList().toString());
       ///gridPaneStack.add(remainingScriptLabel,0, index);
        borderPane.setLeft(gridPaneStack);

    }
*/



}
