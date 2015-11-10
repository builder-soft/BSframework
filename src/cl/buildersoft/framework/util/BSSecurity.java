package cl.buildersoft.framework.util;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import cl.buildersoft.framework.exception.BSProgrammerException;
import cl.buildersoft.framework.exception.BSSystemException;

public class BSSecurity {
	private static final String UNICODE_FORMAT = "UTF8";
	public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
	private KeySpec myKeySpec;
	private SecretKeyFactory mySecretKeyFactory;
	private Cipher cipher;
	byte[] keyAsBytes;
	private String myEncryptionKey;
	private String myEncryptionScheme;
	SecretKey key;

	public BSSecurity() {
		myEncryptionKey = "BuilderSoftEncryptionKey_";
		myEncryptionScheme = DESEDE_ENCRYPTION_SCHEME;

		try {
			keyAsBytes = myEncryptionKey.getBytes(UNICODE_FORMAT);
			myKeySpec = new DESedeKeySpec(keyAsBytes);
			mySecretKeyFactory = SecretKeyFactory.getInstance(myEncryptionScheme);
			cipher = Cipher.getInstance(myEncryptionScheme);
			key = mySecretKeyFactory.generateSecret(myKeySpec);
		} catch (IOException e) {
			throw new BSProgrammerException(e);
		} catch (InvalidKeyException e) {
			throw new BSProgrammerException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new BSProgrammerException(e);
		} catch (NoSuchPaddingException e) {
			throw new BSProgrammerException(e);
		} catch (InvalidKeySpecException e) {
			throw new BSProgrammerException(e);
		}
	}

	public static void main(String args[]) {
		if (args.length != 2) {
			BSConsole.println("Use la clase con 2 parámetros:\n" + "1.- ENC=Encriptar|DEC=Desencriptar\n"
					+ "2.- Cadena a encriptrar/desencriptar\n" + "Ejemplo: BSSecurity ENC Hola\\ Mundo");
		}

		String method = args[0];
		String string = args[1];
		BSSecurity security = new BSSecurity();

		if (method.equals("ENC")) {
			BSConsole.println(security.encript3des(string));
		} else if (method.equals("DEC")) {
			BSConsole.println(security.decript3des(string));
		}

	}

	public String encript3des(String clearString) {
		String encryptedString = null;
		try {
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] plainText = clearString.getBytes(UNICODE_FORMAT);
			byte[] encryptedText = cipher.doFinal(plainText);
			BASE64Encoder base64encoder = new BASE64Encoder();
			encryptedString = base64encoder.encode(encryptedText);
		} catch (IOException e) {
			throw new BSProgrammerException(e);
		} catch (InvalidKeyException e) {
			throw new BSProgrammerException(e);
		} catch (IllegalBlockSizeException e) {
			throw new BSProgrammerException(e);
		} catch (BadPaddingException e) {
			throw new BSProgrammerException(e);
		}
		return encryptedString;
	}

	public String decript3des(String encryptedString) {
		String clearString = null;
		try {
			cipher.init(Cipher.DECRYPT_MODE, key);
			BASE64Decoder base64decoder = new BASE64Decoder();
			byte[] encryptedText = base64decoder.decodeBuffer(encryptedString);
			byte[] plainText = cipher.doFinal(encryptedText);
			clearString = bytes2String(plainText);
		} catch (IOException e) {
			throw new BSProgrammerException(e);
		} catch (InvalidKeyException e) {
			throw new BSProgrammerException(e);
		} catch (IllegalBlockSizeException e) {
			throw new BSProgrammerException(e);
		} catch (BadPaddingException e) {
			throw new BSProgrammerException(e);
		}
		return clearString;
	}

	private String bytes2String(byte[] bytes) {
		StringBuffer stringBuffer = new StringBuffer();
		for (byte b : bytes) {
			stringBuffer.append((char) b);
		}
		return stringBuffer.toString();
	}

	public String md5(String str) {
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new BSSystemException(e);
		}
		char[] charArray = str.toCharArray();

		byte[] byteArray = new byte[charArray.length];

		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];

		byte[] md5Bytes = md5.digest(byteArray);

		StringBuffer hexValue = new StringBuffer();
		int val = 0;
		for (int i = 0; i < md5Bytes.length; i++) {
			val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		return hexValue.toString();
	}
}
