/*
 * Copyright 2018 the bitcoinj-cash developers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scriptlistener;

import com.google.common.io.BaseEncoding;
import model.StackItem;
import model.StackItems;
import org.bitcoinj.core.Context;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.script.*;
import scriptdebugger.InteractiveScriptApp;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple demonstration of ScriptStateListener that dumps the state of the script interpreter to console after each op code execution.
 * <p>
 * Created by shadders on 7/02/18.
 */
public class InteractiveScriptStateListener extends ScriptStateListener {

    private String fullScriptString;
    private boolean pauseForUser = true;
    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();

    public InteractiveScriptStateListener(boolean pauseForUser) {
        this.pauseForUser = pauseForUser;
    }
    StackItems stackItems = new StackItems();
    List<StackItem> stackItemList = new ArrayList<StackItem>();
    @Override
    public void onBeforeOpCodeExecuted(boolean willExecute) {

        if (getChunkIndex() == 0) {
            fullScriptString = truncateData(String.valueOf(getScript()));
        }

        System.out.println(String.format("\nExecuting %s operation: [%s]", getCurrentChunk().isOpCode() ? "OP_CODE" : "PUSHDATA", ScriptOpCodes.getOpCodeName(getCurrentChunk().opcode)));
    }

    @Override
    public void onAfterOpCodeExectuted() {

        ScriptBuilder builder = new ScriptBuilder();

        for (ScriptChunk chunk : getScriptChunks().subList(getChunkIndex(), getScriptChunks().size())) {
/*            if (chunk.data != null)
                System.out.println("HEX data " + new BigInteger(chunk.data).intValue());
            else
                System.out.println("HEX opcode " + ScriptOpCodes.getOpCodeName(chunk.opcode));*/
            builder.addChunk(chunk);
        }
        System.out.println("chunk " + builder.build());

        //  interactiveScriptAppController.addStack(builder.build().toString());
        Script remainScript = builder.build();
        String remainingString = truncateData(remainScript.toString());
        int startIndex = fullScriptString.indexOf(remainingString);
        String markedScriptString = fullScriptString.substring(0, startIndex) + "^" + fullScriptString.substring(startIndex);
        //System.out.println("Remaining code: " + remainingString);
        System.out.println("Execution point (^): " + markedScriptString);
        System.out.println();
        System.out.println("Remaining script" + remainingString);
        //dump stacks
        List<byte[]> reverseStack = new ArrayList<byte[]>(getStack());
        Collections.reverse(reverseStack);
        System.out.println("----------------------------------------");
        System.out.println("StackItem:");
        String stack = "";
        if (reverseStack.isEmpty()) {
            System.out.println("empty");
        } else {
            int index = 0;
            for (byte[] bytes : reverseStack) {
                stack = stack + " " + (index++) + " " + bytes + "\n";

                //print the HEX to debugger
                System.out.println(String.format("StackItem index[%s] length[%s] [%s]", index, bytes.length, (bytes)));

                StackItem stackItem = new StackItem(index,bytes);
              //  stackItemList.add(stackItem);
              //  stackItems.setStackItems(stackItemList);
                model.Context.getInstance().getStackItemsList().add(stackItem);

            }
        }


        if (!getAltstack().isEmpty()) {
            reverseStack = new ArrayList<byte[]>(getAltstack());
            Collections.reverse(reverseStack);
            System.out.println("Alt StackItem: ");

            for (byte[] bytes : reverseStack) {
                System.out.println(HEX.encode(bytes));
            }
            System.out.println();
        }

        if (!getIfStack().isEmpty()) {
            List<Boolean> reverseIfStack = new ArrayList<Boolean>(getIfStack());
            Collections.reverse(reverseIfStack);
            System.out.println("If StackItem: ");

            for (Boolean element : reverseIfStack) {
                System.out.println(element);
            }
            System.out.println();
        }

    }

    @Override
    public void onExceptionThrown(ScriptException exception) {
        System.out.println("Exception thrown: ");
    }

    @Override
    public void onScriptComplete() {
        List<byte[]> stack = getStack();
        if (stack.isEmpty() || !Script.castToBool(stack.get(stack.size() - 1))) {
            System.out.println("Script failed.");
        } else {
            System.out.println("Script success.");
        }
    }

    private String truncateData(String scriptString) {
        System.out.println("Lets scriptString " + scriptString);
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(scriptString);

        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String data = m.group(0);
            if (data.length() > 10) {
                data = data.substring(0, 5) + "..." + data.substring(data.length() - 5);
            }
            m.appendReplacement(sb, data);
        }
        m.appendTail(sb);
        System.out.println("Lets parse " + sb.toString());
        return sb.toString();
    }
}
