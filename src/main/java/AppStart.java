public class AppStart {
    public static void main(String[] args) {
        Utils.log("OS starting...");

        System os = new System();
        os.run();

        Utils.log("quitting...");
    }
}
