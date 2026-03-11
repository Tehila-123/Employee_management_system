import at.favre.lib.crypto.bcrypt.BCrypt;

public class HashGen {
    public static void main(String[] args) {
        String password = "Password123!";
        String hash = BCrypt.withDefaults().hashToString(12, password.toCharArray());
        System.out.println(hash);
    }
}
