package co.edu.sena.finanzas.api.servicio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Cifra y verifica contrasenas con PBKDF2 (incluido en Java, no necesita librerias).
 *
 * Cada contrasena lleva una "sal" aleatoria distinta, asi dos usuarios con la misma
 * clave quedan con un resultado diferente. Se guarda con el formato
 * {@code pbkdf2$iteraciones$sal$hash} (sal y hash en Base64).
 */
public final class ClaveSegura {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 120_000;
    private static final int BITS = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private ClaveSegura() {
    }

    /** Devuelve la contrasena cifrada lista para guardar en la base de datos. */
    public static String cifrar(String clave) {
        byte[] sal = new byte[16];
        ALEATORIO.nextBytes(sal);
        byte[] hash = calcular(clave, sal, ITERACIONES);
        Base64.Encoder b64 = Base64.getEncoder();
        return "pbkdf2$" + ITERACIONES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(hash);
    }

    /** Indica si la contrasena escrita coincide con la guardada. */
    public static boolean coincide(String clave, String guardada) {
        if (guardada == null) {
            return false;
        }
        String[] partes = guardada.split("\\$");
        if (partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        Base64.Decoder b64 = Base64.getDecoder();
        byte[] sal = b64.decode(partes[2]);
        byte[] esperado = b64.decode(partes[3]);
        byte[] calculado = calcular(clave, sal, Integer.parseInt(partes[1]));
        // Comparacion en tiempo constante para no dar pistas por el tiempo de respuesta.
        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] calcular(String clave, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec especificacion = new PBEKeySpec(clave.toCharArray(), sal, iteraciones, BITS);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("No se pudo cifrar la contrasena", e);
        }
    }
}
