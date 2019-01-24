package model;


public class StackItem {

    private int index;
    private  byte[] data;


    public StackItem(int index, byte[] data) {
       this.index=index;
       this.data=data;
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
}
