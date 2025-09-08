import java.security.SecureRandom;
import java.util.Base64;

public class JwtKeyGenerator {
  public static void main(String[] args) {
    SecureRandom random = new SecureRandom();
    byte[] key = new byte[64]; // 512 bits
    random.nextBytes(key);
    String base64Key = Base64.getEncoder().encodeToString(key);
    System.out.println("Generated JWT Secret Key:");
    System.out.println(base64Key);
  }
}
