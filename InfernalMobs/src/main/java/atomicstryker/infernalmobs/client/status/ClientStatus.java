package atomicstryker.infernalmobs.client.status;

public class ClientStatus {

    private static boolean stunned;

    public static boolean isStunned() {
        return stunned;
    }

    public static void setStunned(boolean s) {
        stunned = s;
    }

}
