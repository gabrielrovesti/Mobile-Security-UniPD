import java.security.MessageDigest;

public class MultithreadPinCracker {

    private static final String TARGET_HASH = "d04988522ddfed3133cc24fb6924eae9";
    private static final int THREAD_COUNT = 4; // Adjust the number of threads as needed

    public static void main(String[] args) {
        int pinRange = 999999 / THREAD_COUNT;
        Thread[] threads = new Thread[THREAD_COUNT];

        for (int t = 0; t < THREAD_COUNT; t++) {
            final int start = t * pinRange;
            final int end = (t + 1) * pinRange - 1;

            threads[t] = new Thread(() -> findPinInRange(start, end));
            threads[t].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void findPinInRange(int start, int end) {
        for (int i = start; i <= end; i++) {
            String pin = String.format("%06d", i);
            String hash = hashPin(pin);

            if (hash.equals(TARGET_HASH)) {
                System.out.println("Found PIN: " + pin);
                break;
            }
        }
    }

    private static String hashPin(String pin) {
        try {
            byte[] pinBytes = pin.getBytes();
            MessageDigest md = MessageDigest.getInstance("MD5");
            for (int i = 0; i < 25; i++) {
                for (int j = 0; j < 400; j++) {
                    md.update(pinBytes);
                    pinBytes = md.digest().clone();
                }
            }
            return toHexString(pinBytes);
        } catch (Exception e) {
            System.err.println("Exception while hashing pin");
            return null;
        }
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
