import java.security.MessageDigest;

public class PinBruteForce {

    public static void main(String[] args) {
        String targetHash = "d04988522ddfed3133cc24fb6924eae9";

        for (int i = 0; i <= 999999; i++) {
            String pin = String.format("%06d", i); // Assuming 6-digit PIN
            String hash = hashPin(pin);

            if (hash.equals(targetHash)) {
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
