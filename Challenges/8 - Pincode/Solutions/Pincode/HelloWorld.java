import java.security.MessageDigest;

public class HelloWorld {

    public static void main(String[] args) {
        // Example usage
        String pin = "703958";

        try {
            byte[] pinBytes = pin.getBytes();
            for (int i = 0; i < 25; i++) {
                for (int j = 0; j < 400; j++) {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(pinBytes);
                    pinBytes = md.digest().clone();
                }
            }
            boolean isValidPin = toHexString(pinBytes).equals("d04988522ddfed3133cc24fb6924eae9");
            System.out.println("Is PIN valid? " + isValidPin);
            if(isValidPin) {
                System.out.println("Found Flag: " + getFlag(pin));
            }
        } catch (Exception e) {
            System.err.println("Exception while checking pin");
        }
    }

    public static String toHexString(byte[] bytes) {
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

    public static String getFlag(String pin) {
        return "FLAG{in_vino_veritas}";
    }
}
