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
import model.Context;
import model.StackItem;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.*;
import scriptdebugger.InteractiveScriptAppController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InteractiveScriptStateListener extends ScriptStateListener {

    private static final String NEWLINE = System.getProperty("line.separator");

    private String fullScriptString;
    private InteractiveScriptAppController controller;
    private boolean debugMode;
    public static final BaseEncoding HEX = BaseEncoding.base16().lowerCase();

    public List<String> sb = new ArrayList<>();

    public InteractiveScriptStateListener(boolean debugMode, InteractiveScriptAppController controller) {
        this.debugMode = debugMode;
        this.controller = controller;
    }


    @Override
    public void onBeforeOpCodeExecuted(boolean willExecute) {

        if (getChunkIndex() == 0) {
            fullScriptString = truncateData(String.valueOf(getScript()));
        }

        System.out.println(String.format("\nExecuting %s operation: [%s]", getCurrentChunk().isOpCode() ? "OP_CODE" : "PUSHDATA", ScriptOpCodes.getOpCodeName(getCurrentChunk().opcode)));
        // sb.add(String.format("\nExecuting %s operation: [%s]", getCurrentChunk().isOpCode() ? "OP_CODE" : "PUSHDATA", ScriptOpCodes.getOpCodeName(getCurrentChunk().opcode)));
    }

    @Override
    public void onAfterOpCodeExectuted() {

        ScriptBuilder builder = new ScriptBuilder();
        for (ScriptChunk chunk : getScriptChunks().subList(getChunkIndex(), getScriptChunks().size())) {

            builder.addChunk(chunk);
        }
        Script remainScript = builder.build();
        String remainingString = truncateData(remainScript.toString());
        int startIndex = fullScriptString.indexOf(remainingString);
        String markedScriptString = fullScriptString.substring(0, startIndex) + "^" + fullScriptString.substring(startIndex);
        sb.add("Execution point (^): " + markedScriptString);
        sb.add(NEWLINE);
        List<byte[]> reverseStack = new ArrayList<byte[]>(getStack());
        Collections.reverse(reverseStack);
        sb.add("Stack:");

        String stack = "";
        if (reverseStack.isEmpty()) {
            System.out.println("empty");
            sb.add("Empty stack");
        } else {
            int index = 0;
            for (byte[] bytes : reverseStack) {
                stack = stack + " " + (index++) + " " + bytes + "\n";

                //print the HEX to debugger
                System.out.println("Utils.HEX.encode(bytes) -- >   " + Utils.HEX.encode(bytes));
                sb.add(String.format("StackItem index[%s] length[%s] [%s]", index, bytes.length, Utils.HEX.encode(bytes)));
                StackItem stackItem;
                if (index == 1) {
                    stackItem = new StackItem(index, bytes, remainingString);
                } else {
                    stackItem = new StackItem(index, bytes);
                }
                model.Context.getInstance().getStackItemsList().add(stackItem);

            }
        }


        if (!getAltstack().isEmpty()) {
            reverseStack = new ArrayList<byte[]>(getAltstack());
            Collections.reverse(reverseStack);
            System.out.println("Alt StackItem: ");
            sb.add("Alt Stack: ");

            for (byte[] bytes : reverseStack) {
                System.out.println(HEX.encode(bytes));
                sb.add(HEX.encode(bytes));
            }
            sb.add(NEWLINE);
        }

        if (!getIfStack().isEmpty()) {
            List<Boolean> reverseIfStack = new ArrayList<Boolean>(getIfStack());
            Collections.reverse(reverseIfStack);
            System.out.println("If StackItem: ");
            sb.add("If Stack: ");

            for (Boolean element : reverseIfStack) {
                System.out.println(element);
                sb.add(element.toString());
            }
            System.out.println();
            sb.add(NEWLINE);
        }
        if (debugMode) {

            try {
                System.out.println("-------Press 'Play' To Continue--------");
                controller.isBreakpoint();
                controller.countDownLatch.await();
               controller.countDownLatch = new CountDownLatch(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

    }

    public void playToNextExecPoint() {

        //   System.out.println("countdown call 1 ");
        controller.countDownLatch.countDown();

        //   System.out.println("countdown call 2");
    }

    @Override
    public void onExceptionThrown(ScriptException exception) {
        System.out.println("Exception thrown: ");
        sb.add("Exception thrown: ");
    }

    @Override
    public void onScriptComplete() {
        List<byte[]> stack = getStack();
        controller.scriptCounter++;
        if (stack.isEmpty() || !Script.castToBool(stack.get(stack.size() - 1))) {
            System.out.println("Script failed.");
            model.Context.getInstance().setScriptStatus("Failed");
            controller.onScriptComplete();
        } else {
            System.out.println("Script success.");
            model.Context.getInstance().setScriptStatus("Success");
            controller.onScriptComplete();
        }

        if (debugMode && controller.scriptCounter == 2) {

            controller.runBtn.setVisible(true);
            controller.debugBtn.setVisible(false);
            controller.continueBtn.setVisible(false);
        }

    }

    private String truncateData(String scriptString) {
        System.out.println("Lets scriptString " + scriptString);
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(scriptString);

        StringBuffer stringBuffer = new StringBuffer();
        while (m.find()) {
            String data = m.group(0);
            if (data.length() > 10) {
                data = data.substring(0, 5) + "..." + data.substring(data.length() - 5);
            }
            m.appendReplacement(stringBuffer, data);
        }
        m.appendTail(stringBuffer);
        System.out.println("Lets parse " + stringBuffer.toString());
        return stringBuffer.toString();
    }


}
