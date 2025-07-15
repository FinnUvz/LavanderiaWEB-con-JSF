package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilidad para encriptar y verificar contraseñas
 */
public class PasswordUtils {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * Encripta una contraseña usando SHA-256 con salt
     * @param contrasena La contraseña a encriptar
     * @return La contraseña encriptada con salt
     */
    public static String encriptarContrasena(String contrasena) {
        try {
            // Generar salt aleatorio
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Crear hash con salt
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] hashedPassword = md.digest(contrasena.getBytes());
            
            // Combinar salt y hash
            byte[] saltedHash = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, saltedHash, 0, salt.length);
            System.arraycopy(hashedPassword, 0, saltedHash, salt.length, hashedPassword.length);
            
            // Codificar en Base64
            return Base64.getEncoder().encodeToString(saltedHash);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
    
    /**
     * Verifica si una contraseña coincide con su hash
     * @param contrasena La contraseña en texto plano
     * @param hashAlmacenado El hash almacenado en la base de datos
     * @return true si la contraseña es correcta
     */
    public static boolean verificarContrasena(String contrasena, String hashAlmacenado) {
        try {
            // Decodificar el hash almacenado
            byte[] saltedHash = Base64.getDecoder().decode(hashAlmacenado);
            
            // Extraer salt
            byte[] salt = new byte[SALT_LENGTH];
            System.arraycopy(saltedHash, 0, salt, 0, SALT_LENGTH);
            
            // Extraer hash original
            byte[] originalHash = new byte[saltedHash.length - SALT_LENGTH];
            System.arraycopy(saltedHash, SALT_LENGTH, originalHash, 0, originalHash.length);
            
            // Generar hash con la contraseña proporcionada y el salt extraído
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(salt);
            byte[] testHash = md.digest(contrasena.getBytes());
            
            // Comparar hashes
            return MessageDigest.isEqual(originalHash, testHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Método simple para casos donde ya tienes hashes SHA-256 simples
     * @param contrasena La contraseña a hashear
     * @return Hash SHA-256 simple
     */
    public static String hashSimple(String contrasena) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            byte[] hash = md.digest(contrasena.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al crear hash", e);
        }
    }
}
