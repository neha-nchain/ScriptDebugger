package model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Breakpoints {


    private StringProperty breakpoints = new SimpleStringProperty();
    private BooleanProperty select = new SimpleBooleanProperty();

    public Breakpoints(SimpleStringProperty breakpoints) {
        this.breakpoints= breakpoints;

    }


    public StringProperty breakpointsProperty() {
        return breakpoints;
    }

    public String getBreakpoints() {
        return breakpointsProperty().get();
    }

    public void setBreakpoints(String breakpoints) {
        this.breakpointsProperty().set(breakpoints);
    }

    public boolean isSelect() {
        return select.get();
    }

    public BooleanProperty selectProperty() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select.set(select);
    }
}
