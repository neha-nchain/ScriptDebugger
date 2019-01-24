package scriptdebugger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import model.Context;
import model.StackItem;
import model.StackItems;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.UnsafeByteArrayOutputStream;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.script.ScriptStateListener;
import scriptlistener.InteractiveScriptStateListener;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import static com.google.common.base.Preconditions.checkArgument;
import static org.bitcoinj.core.Utils.HEX;
import static org.bitcoinj.script.ScriptOpCodes.*;


public class InteractiveScriptAppController implements Initializable {

    @FXML
    public GridPane gridPane;
    @FXML
    public static BorderPane borderPane;
    @FXML
    public GridPane gridPaneStack;
    @FXML
    public Label remainingScriptLabel;
    @FXML
    public Label stackLabel;
    @FXML
    public Button debugBtn;


    @FXML
    public TextField tbScriptSig;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Context.getInstance().getStackItemsList();
    }

    @FXML
    private void debugScript(ActionEvent actionEvent) throws IOException {

        Script script = null;
        LinkedList<byte[]> stack = new LinkedList<byte[]>();
        ScriptStateListener listener = new InteractiveScriptStateListener(true);

        // script = ScriptBuilder.createScriptFromUIInput(tbScriptSig.getText().split(" "));
        script = parseScriptString(tbScriptSig.getText().toUpperCase());
        Script.executeDebugScript(new Transaction(MainNetParams.get()), 0, script, stack, Coin.ZERO, Script.ALL_VERIFY_FLAGS, listener);

        addStack();

    }

    //Display the stack in UI
    public void addStack() {
        gridPaneStack = new GridPane();
        gridPaneStack.setAlignment(Pos.TOP_RIGHT);
        gridPaneStack.setHgap(10);
        gridPaneStack.setVgap(10);
        gridPaneStack.setPadding(new Insets(60,0,20,0));

        int stacksize= 0;
        gridPaneStack.setId("grid_"+stacksize);
        for(StackItem stackItem : Context.getInstance().getStackItemsList()) {


              //  for(StackItem stackItem1: stackItem.getStackItems()){
                    remainingScriptLabel = new Label();
                    remainingScriptLabel.setId("stack_"+ (stacksize+1));
                    remainingScriptLabel.setText(stackItem.getData().toString());
                    gridPaneStack.add(remainingScriptLabel, 0, (++stacksize));
               // }

        }
        borderPane.setCenter(gridPaneStack);
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
