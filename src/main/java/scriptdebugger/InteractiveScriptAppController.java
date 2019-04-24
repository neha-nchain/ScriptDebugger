package scriptdebugger;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.Breakpoints;
import model.Context;
import model.StackItem;
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
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static org.bitcoinj.core.Utils.HEX;
import static org.bitcoinj.script.ScriptOpCodes.*;


public class InteractiveScriptAppController implements Initializable {

    @FXML
    public static BorderPane borderPane;
    @FXML
    public ScrollPane scrollPaneRun;
    @FXML
    public ScrollPane scrollPaneDebug;
    @FXML
    public Label remainingScriptLabel;
    @FXML
    public Label scriptStatusLabel;

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

    public int scriptCounter;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listener = new InteractiveScriptStateListener(true, this);
        Context.getInstance().getStackItemsList();
        breakpointsObservableList = FXCollections.observableArrayList();
        breakpointsListView.setItems(breakpointsObservableList);
        breakpointsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        breakpointsListView.setEditable(true);
        debugBtn.setVisible(false);

    }


    @FXML
    private void runScript(ActionEvent actionEvent) throws IOException {
        Context.getInstance().getStackItemsList().clear();
        Context.getInstance().setScriptStatus(null);
        scriptCounter=0;

    //    listener.getScriptChunks().subList(listener.getChunkIndex(), listener.getScriptChunks().size());
        spiltIntoBreakpoints(tbScriptSig.getText(), tbScriptPub.getText());
        createBreakpointView();
        debugBtn.setVisible(true);
        runBtn.setVisible(false);
        // addStackRun(listener);
      //  addListView();
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

    @FXML
    private void debugScript(ActionEvent actionEvent) throws IOException {

        Context.getInstance().getStackItemsList().clear();
        LinkedList<byte[]> stack = new LinkedList<byte[]>();
       // Script scriptSig = parseScriptString((tbScriptSig.getText().concat(" ").concat(tbScriptPub.getText())).toUpperCase());
        Script scriptSig = parseScriptString(tbScriptSig.getText().toUpperCase());
        Script scriptPub = parseScriptString(tbScriptPub.getText().toUpperCase());
        debugBtn.setVisible(false);
        continueBtn.setVisible(true);

        try {
            Executors.newSingleThreadExecutor().submit(() -> {
                Script.executeDebugScript(
                        new Transaction(MainNetParams.get()), 0, scriptSig, stack, Coin.ZERO, Script.ALL_VERIFY_FLAGS, listener);
                Script.executeDebugScript(
                        new Transaction(MainNetParams.get()), 0, scriptPub, stack, Coin.ZERO, Script.ALL_VERIFY_FLAGS, listener);
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createBreakpointView() {
         scrollPaneRun.setContent(breakpointsListView);
    }

    public void isBreakpoint() {
        try {
            onHitBreakpoint();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onHitBreakpoint() {
        Platform.runLater(()->{
            addStackDebug(listener,false);});
    }

    public void onScriptComplete() {
        Platform.runLater(()->{
            addStackDebug(listener, true);});
    }

    @FXML
    private void continueDebugging(ActionEvent actionEvent) throws IOException {
        listener.playToNextExecPoint();
    }

    //Display the stack in debug UI
    public void addStackDebug(InteractiveScriptStateListener listener, boolean over ) {

        GridPane gridPane = new GridPane();
        int stacksize = 0;
        gridPane.setId("grid_pane_debug" + stacksize);

       ///// adding back context
        for (StackItem stackItem : Context.getInstance().getStackItemsList()) {
                if (stackItem.getRemainingScript() != null) {
                    createLabel(stacksize, "Execution point:  " + stackItem.getRemainingScript(), true, gridPane);
                    stacksize = stacksize + 1;
                }
                createLabel(stacksize, "index[" + stackItem.getIndex() + "] " + Utils.HEX.encode(stackItem.getData()).toString(), false, gridPane);
                stacksize = stacksize + 1;
            }

        if(over) {
            createStatusLabel(stacksize, "Script status: " + Context.getInstance().getScriptStatus(), true, gridPane);
        }
        //////////////////
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
    private void createStatusLabel(int stacksize, String data, boolean style, GridPane gridPane) {
        scriptStatusLabel = new Label();
        scriptStatusLabel.setId("stack_" + (stacksize + 1));
        scriptStatusLabel.setText(data);
        scriptStatusLabel.setStyle("-fx-font-weight:bold");

        gridPane.add(scriptStatusLabel, 0, stacksize);
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

    static int encodeToOpN(int value) {
        checkArgument(value >= -1 && value <= 16, "encodeToOpN called for " + value + " which we cannot encode in an opcode.");
        if (value == 0)
            return OP_0;
        else if (value == -1)
            return OP_1NEGATE;
        else
            return value - 1 + OP_1;
    }


}
