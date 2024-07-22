package base.server;

/**
 * Custom attachment. Basically ArrayList, if necessary will be extended.
 */
public class Attachment {
    private final int[] arr;
    private int position = 0;

    public Attachment(int size) {
        arr = new int[size];
    }

    public int[] getArr() {
        return arr;
    }

    public int getPosition() {
        return position;
    }

    public void pushBack(int val) {
        arr[position] = val;
        position++;
    }
}
