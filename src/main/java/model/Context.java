package model;

import java.util.ArrayList;
import java.util.List;

public class Context {

    private final static Context instance = new Context();
    private List<StackItems> stackItemsList = new ArrayList<StackItems>();
    public static Context getInstance() {
        return instance;
    }

    public List<StackItems> getStackItemsList() {
        return stackItemsList;
    }
}
