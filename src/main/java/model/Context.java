package model;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final static Context instance = new Context();
    private String currentChunk;
    private String scriptStatus;
    private List<StackItem> stackItemsList = new ArrayList<StackItem>();

    public String getCurrentChunk() {
        return currentChunk;
    }
    public void setCurrentChunk(String currentChunk) {
        this.currentChunk = currentChunk;
    }

    public String getScriptStatus() {
        return scriptStatus;
    }

    public void setScriptStatus(String scriptStatus) {
        this.scriptStatus = scriptStatus;
    }

    public static Context getInstance() {
        return instance;
    }

    public List<StackItem> getStackItemsList() {
        return stackItemsList;
    }
}