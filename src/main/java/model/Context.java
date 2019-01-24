package model;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final static Context instance = new Context();

    public boolean isScriptStatus() {
        return scriptStatus;
    }

    public void setScriptStatus(boolean scriptStatus) {
        this.scriptStatus = scriptStatus;
    }

    private boolean scriptStatus;
    private List<StackItem> stackItemsList = new ArrayList<StackItem>();
    public static Context getInstance() {
        return instance;
    }

    public List<StackItem> getStackItemsList() {
        return stackItemsList;
    }
}
