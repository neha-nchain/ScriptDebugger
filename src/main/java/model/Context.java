package model;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final static Context instance = new Context();
    private String scriptStatus;

    public String getScriptStatus() {
        return scriptStatus;
    }

    public void setScriptStatus(String scriptStatus) {
        this.scriptStatus = scriptStatus;
    }

    private List<StackItem> stackItemsList = new ArrayList<StackItem>();
    public static Context getInstance() {
        return instance;
    }

    public List<StackItem> getStackItemsList() {
        return stackItemsList;
    }
}
