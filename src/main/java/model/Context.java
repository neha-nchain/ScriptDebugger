package model;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final static Context instance = new Context();
    private List<StackItem> stackItemsList = new ArrayList<StackItem>();
    public static Context getInstance() {
        return instance;
    }

    public List<StackItem> getStackItemsList() {
        return stackItemsList;
    }
}
