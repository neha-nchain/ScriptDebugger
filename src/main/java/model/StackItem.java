package model;


public class StackItem {

    private int index;
    private  byte[] data;
    private String remainingScript;


    public StackItem(int index, byte[] data) {
       this.index=index;
       this.data=data;
    }

    public StackItem(int index, byte[] data, String remainingScript) {
        this.index=index;
        this.data=data;
        this.remainingScript=remainingScript;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getRemainingScript() {
        return remainingScript;
    }

    public void setRemainingScript(String remainingScript) {
        this.remainingScript = remainingScript;
    }
}
