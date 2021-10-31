public class BasicFunctions {
    private BasicFunctions(){};

    public static void sleep (int timeOut){
        try {
            Thread.sleep(timeOut);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
