public class Printer {
    static {
        //System.out.println(System.getProperty("java.library.path"));
        System.loadLibrary("mylib");
    }

    private native void Print();
    public static void main(String[] args) {
        new Printer().Print();
    }
}
