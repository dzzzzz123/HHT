package ext.HHT.singleSignOn;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {
//加密
	public static String Encryption(String password) throws Exception {
		String originalString = password;
		String secretKey = "mySecretKey11111";

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encryptedBytes = cipher.doFinal(originalString.getBytes(StandardCharsets.UTF_8));
		String encryptedString = Base64.getEncoder().encodeToString(encryptedBytes);

		System.out.println("加密后的密码: " + encryptedString);
		return encryptedString;
	}

//解密
	public static String Decryption(String password) throws Exception {
		String encryptedString = password;
		String secretKey = "mySecretKey11111";

		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
		String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);

		System.out.println("解密后的项目: " + decryptedString);
		return decryptedString;
	}

}
