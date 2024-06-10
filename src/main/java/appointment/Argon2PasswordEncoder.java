package appointment;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;

public class Argon2PasswordEncoder {

    private final int iterations;
    private final int memory; // In kilobytes
    private final int parallelism;
    private final int saltLength;
    private final int hashLength;
    private final SecureRandom random;

    public Argon2PasswordEncoder(int iterations, int memory, int parallelism, int saltLength, int hashLength) {
        this.iterations = iterations;
        this.memory = memory;
        this.parallelism = parallelism;
        this.saltLength = saltLength;
        this.hashLength = hashLength;
        this.random = new SecureRandom();
    }

    public String encode(String password) {
        try {
            byte[] salt = new byte[saltLength];
            random.nextBytes(salt);

            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withIterations(iterations)
                .withMemoryAsKB(memory) // memory is set in kilobytes
                .withParallelism(parallelism)
                .withSalt(salt)
                .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] hash = new byte[hashLength];
            generator.generateBytes(password.getBytes(StandardCharsets.UTF_8), hash);

            // Combine salt and hashed password for storage (Base64 encoding is optional)
            StringBuilder sb = new StringBuilder();
            sb.append(Base64.getEncoder().encodeToString(salt));
            sb.append("$");
            sb.append(Base64.getEncoder().encodeToString(hash));
            return sb.toString();
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return null;
        }
    }

    public boolean matches(String rawPassword, String encodedPassword) {
        try {
            String[] parts = encodedPassword.split("\\$");
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            Argon2Parameters params = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withIterations(iterations)
                .withMemoryAsKB(memory) // memory is set in kilobytes
                .withParallelism(parallelism)
                .withSalt(salt)
                .build();

            Argon2BytesGenerator generator = new Argon2BytesGenerator();
            generator.init(params);

            byte[] testHash = new byte[hashLength];
            generator.generateBytes(rawPassword.getBytes(StandardCharsets.UTF_8), testHash);
            
            // Compare passwords using constant-time comparison
            return Arrays.equals(testHash, hash);
        } catch (Exception e) {
            // Handle exceptions appropriately
            e.printStackTrace();
            return false;
        }
    }
}
