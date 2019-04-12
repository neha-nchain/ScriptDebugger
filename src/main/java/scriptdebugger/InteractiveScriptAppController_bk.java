/*
package scriptdebugger;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.Breakpoints;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.UnsafeByteArrayOutputStream;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptOpCodes;
import scriptlistener.InteractiveScriptStateListener;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static org.bitcoinj.core.Utils.HEX;
import static org.bitcoinj.script.ScriptOpCodes.*;


public class InteractiveScriptAppController_bk implements Initializable {

    @FXML
    public static BorderPane borderPane;
    @FXML
    public ScrollPane scrollPaneRun;
    @FXML
    public ScrollPane scrollPaneDebug;
    @FXML
    public Label remainingScriptLabel;
    @FXML
    public Button debugBtn;
    @FXML
    public Button continueBtn;
    @FXML
    public Button runBtn;
    @FXML
    public TextField tbScriptSig;
    @FXML
    public TextField tbScriptPub;

    private InteractiveScriptStateListener listener;

    @FXML
    private ListView<String> breakpointsListView;

    private ObservableList<String> breakpointsObservableList;

    private List<String> breakHere;


    static int encodeToOpN(int value) {
        checkArgument(value >= -1 && value <= 16, "encodeToOpN called for " + value + " which we cannot encode in an opcode.");
        if (value == 0)
            return OP_0;
        else if (value == -1)
            return OP_1NEGATE;
        else
            return value - 1 + OP_1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        */
/*breakpointsObservableList = FXCollections.observableArrayList();
        breakpointsListView.setItems(breakpointsObservableList);
        breakpointsListView.setEditable(true);

     //   breakpointsListView.setCellFactory(breakpointsListView -> new BreakpointListViewCell());
        breakpointsListView.setCellFactory(TextFieldListCell.forListView());

        breakpointsListView.setOnEditCommit(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                breakpointsListView.getItems().set(t.getIndex(), t.getNewValue());
                System.out.println("setOnEditCommit");
            }

        });

        breakpointsListView.setOnEditCancel(new EventHandler<ListView.EditEvent<String>>() {
            @Override
            public void handle(ListView.EditEvent<String> t) {
                System.out.println("setOnEditCancel");
            }
        });
*//*


    }

    @FXML
    private void runScript(ActionEvent actionEvent) throws IOException {

   //     Script script;
       // Context.getInstance().getStackItemsList().clear();
   //     LinkedList<byte[]> stack = new LinkedList<byte[]>();
       // InteractiveScriptStateListener listener = new InteractiveScriptStateListener(false, this);

        //  script = parseScriptString(tbScriptSig.getText().toUpperCase());
        // Script.executeDebugScript(new Transaction(MainNetParams.get()), 0, script, stack, Coin.ZERO, Script.ALL_VERIFY_FLAGS, listener);

        //      script = parseScriptString(tbScriptPub.getText().toUpperCase());
        // Script.executeDebugScript(new Transaction(MainNetParams.get()), 0, script, stack, Coin.ZERO, Script.ALL_VERIFY_FLAGS, listener);
        spiltIntoBreakpoints(tbScriptSig.getText(), tbScriptPub.getText());
        // addStackRun(listener);
        addListView();
    }

    @FXML
    private void debugScript(ActionEvent actionEvent) throws IOException {
         breakHere= new ArrayList<>();
         for (String breakpoints : breakpointsListView.getSelectionModel().getSelectedItems()) {
            breakHere.add(breakpoints);
        }

       // Context.getInstance().getStackItemsList().clear();
        LinkedList<byte[]> stack = new LinkedList<byte[]>();
        listener = new InteractiveScriptStateListener(true, this);

        Script scriptSig = parseScriptString(tbScriptSig.getText().toUpperCase());
        Script scriptPub = parseScriptString(tbScriptPub.getText().toUpperCase());
        debugBtn.setVisible(false);
        continueBtn.setVisible(true);
        Executors.newSingleThreadExecutor().submit(() -> {
            for(String breakpoints: breakpointsListView.getItems()) {
                try {
                    Script.executeDebugScript(
                            new Transaction(MainNetParams.get()), 0, parseScriptString(breakpoints), stack, Coin.ZERO, Script.ALL_VERIFY_FLAGS, listener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
          */
/*  Script.executeDebugScript(
                    new Transaction(MainNetParams.get()), 0, scriptPub, stack, Coin.ZERO, Script.ALL_VERIFY_FLAGS, listener);*//*

        });
    }

    public void isHitBreakpoint() {

        if(breakHere.size()>0){
            onHitBreakpoint();
            breakHere.remove(0);
        }

    }

    public void onHitBreakpoint() {
        Platform.runLater(() -> {
            addStackDebug(listener);
        });
        int lastlabel = listener.sb.size();

        listener.sb.remove(lastlabel - 1);
    }

    @FXML
    private void continueDebugging(ActionEvent actionEvent) throws IOException {
        listener.playToNextExecPoint();

    }

    private void addListView() {

       // scrollPaneRun.setContent(breakpointsListView);
    }

    //Display the stack in run UI
    public void addStackRun(InteractiveScriptStateListener listener) {

        GridPane gridPane = new GridPane();
        int stacksize = 0;
        gridPane.setId("grid_pane" + stacksize);

        List<String> lines = listener.sb;
        for (int i = 0; i < lines.size(); i++) {
            createLabel(i, lines.get(i), false, gridPane);
        }

        scrollPaneRun.setContent(gridPane);
    }

    //Display the stack in debug UI
    public void addStackDebug(InteractiveScriptStateListener listener) {

        GridPane gridPane = new GridPane();
        int stacksize = 0;
        gridPane.setId("grid_pane_debug" + stacksize);

        List<String> lines = listener.sb;
        for (int i = 0; i < lines.size(); i++) {
            createLabel(i, lines.get(i), false, gridPane);
        }
        scrollPaneDebug.setContent(gridPane);
    }

    private void createLabel(int stacksize, String data, boolean style, GridPane gridPane) {
        remainingScriptLabel = new Label();
        remainingScriptLabel.setId("stack_" + (stacksize + 1));
        remainingScriptLabel.setText(data);
        if (style) {
            remainingScriptLabel.setStyle("-fx-font-weight:bold");
        }
        gridPane.add(remainingScriptLabel, 0, stacksize);
    }

    private void spiltIntoBreakpoints(String tbScriptSig, String tbScriptPub) {
        breakpointsListView.getItems().clear();
        String finalScript = Stream.of(tbScriptSig,tbScriptPub)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(" "));

        addtoListOfTokens(finalScript);
      //  addtoListOfTokens(tbScriptPub);

    }

    private void addtoListOfTokens(String tbScript) {
        if (!tbScript.isEmpty()) {
            String[] scriptSigBreakPoint = (tbScript.trim()).split("\\s+");
            for (String tokens : scriptSigBreakPoint) {
                Breakpoints token = new Breakpoints(new SimpleStringProperty(tokens));
                breakpointsObservableList.add(token.getBreakpoints());
            }

        }
    }

    private Script parseScriptString(String string) throws IOException {
        String[] words = string.split("[ \\t\\n]");

        UnsafeByteArrayOutputStream out = new UnsafeByteArrayOutputStream();

        for (String w : words) {
            if (w.equals(""))
                continue;
            if (w.matches("^-?[0-9]*$")) {
                // Number
                long val = Long.parseLong(w);
                if (val >= -1 && val <= 16)
                    out.write(encodeToOpN((int) val));
                else
                    Script.writeBytes(out, Utils.reverseBytes(Utils.encodeMPI(BigInteger.valueOf(val), false)));
            } else if (w.matches("^0x[0-9a-fA-F]*$")) {
                // Raw hex data, inserted NOT pushed onto stack:
                out.write(HEX.decode(w.substring(2).toLowerCase()));
            } else if (w.length() >= 2 && w.startsWith("'") && w.endsWith("'")) {
                // Single-quoted string, pushed as data. NOTE: this is poor-man's
                // parsing, spaces/tabs/newlines in single-quoted strings won't work.
                Script.writeBytes(out, w.substring(1, w.length() - 1).getBytes(Charset.forName("UTF-8")));
            } else if (ScriptOpCodes.getOpCode(w) != OP_INVALIDOPCODE) {
                // opcode, e.g. OP_ADD or OP_1:
                out.write(ScriptOpCodes.getOpCode(w));
            } else if (w.startsWith("OP_") && ScriptOpCodes.getOpCode(w.substring(3)) != OP_INVALIDOPCODE) {
                // opcode, e.g. OP_ADD or OP_1:
                out.write(ScriptOpCodes.getOpCode(w.substring(3)));
            } else {
                throw new RuntimeException("Invalid Data");
            }
        }
        // addStack(string);
        return new Script(out.toByteArray());
    }

}
*/
