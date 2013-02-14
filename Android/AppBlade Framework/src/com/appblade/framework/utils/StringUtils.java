package com.appblade.framework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;


/**
 * Utility class for helpful String methods
 * Contains all MD5, HMAC, and SHA2 hashing methods at the String level that you should need. See {@link com.appblade.framework.utils.Base64} for the deeper implementation. 
 * @see javax.crypto.Mac
 * @see javax.crypto.spec.SecretKeySpec
 */
public class StringUtils {
	 public static final int BUFFER_SIZE = 2048;
	 
	/**
	 * Utility method for pulling plain text from an InputStream object
	 * @param in InputStream object retrieved from an HttpResponse
	 * @return String contents of stream
	 */
	public static String readStream(InputStream in)
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try
		{
			while((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
		}
		catch(Exception ex) { }
		finally
		{
			IOUtils.safeClose(in);
			IOUtils.safeClose(reader);
		}
		return sb.toString();
	}
	
	/**
	 * Checks whether the input string is either null or has a trimmed length of zero.
	 * @param input String to check 
	 * @return true on null or if there are zero non-whitespace characters in the string.
	 */
	public static boolean isNullOrEmpty(String input)
	{
		return input == null || input.trim().length() == 0;
	}
	
	/**
	 * Appends a formatted string (with objects) to an existing StringBuilder. StringBuilder will contain the appended string and objects on success.
	 * @param builder The reference to the StringBuilder to alter.
	 * @param format String to append, can contain formatting.
	 * @param params Optional objects to include with the format String. Can be null.
	 */
	public static void append(StringBuilder builder, String format, Object... params)
	{
		builder.append(String.format(format, params));
	}
	
	/**
	 * Tries to parse a String into an integer, fails silently and returns a fallback value.
	 * @param input String to parse for an integer.
	 * @param defaultValue value to return in the event the parsing fails.
	 * @return the parsed string value on success, or the default value on failure.
	 */
	public static int safeParse(String input, int defaultValue) {
		int value = defaultValue;
		try
		{
			value = Integer.parseInt(input);
		}
		catch (Exception ex) { }
		return value;
	}

	
	/**
	 * Hash-based Message Authentication Code that encrypts a message based on a shared secret. Fails silently.
	 * @param key The shared secret that both the sender and the receiver possess.
	 * @param message The message to be encrypted.
	 * @return An HMAC-SHA256 encrypted message or an empty string in the case of either an InvalidKeyException or NoSuchAlgorithmException. 
	 */
	public static String hmacSha256(String key, String message)
	{
		String result = "";
		try
		{
			Mac mac = Mac.getInstance("HmacSHA256");
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
			mac.init(signingKey);
			byte[] rawHmac = mac.doFinal(message.getBytes());
			result = Base64.encodeToString(rawHmac, 0, rawHmac.length, 0);
		}
		catch(InvalidKeyException ex) { }
		catch(NoSuchAlgorithmException ex) { }
		
		return result;
	}
	
	/**
	 * Secure Hash Algorithm that encrypts a message with a 256-bit digest. Fails silently.
	 * @param input String message to encrypt.
	 * @return A byte array of the message digest, or null in the case of a NoSuchAlgorithmException.
	 */
	public static byte[] sha256(String input)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(input.getBytes());
			byte[] messageDigest = digest.digest();
			return messageDigest;
		}
		catch(NoSuchAlgorithmException ex) { }
		
		return null;
	}
	
	/**
	 * Secure Hash Algorithm that encrypts a message with a 256-bit digest. Fails silently.
	 * @param is InputStream of the message to encrypt.
	 * @return A String of the message digest in hexadecimal format, or null in the case of a NoSuchAlgorithmException.
	 */
	public static String sha256FromInputStream(InputStream is)  {
		MessageDigest md = null;
        StringBuffer hexString = new StringBuffer();
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		if(md != null){
	        DigestInputStream dis = new DigestInputStream(is, md);
	        byte[] buffer = new byte[BUFFER_SIZE];
	       try {
	    	   try {
					while (dis.read(buffer) != -1) {
					 //
					}
			        dis.close();
	    	   }
	    	   finally {
		    	   is.close();
		       }
	       }
	       catch (IOException e) {
				e.printStackTrace();
	       }
	        
			byte[] byteArray = md.digest();
	        for (int i = 0; i < byteArray.length; i++) {
	            String hex = Integer.toHexString(byteArray[i] & 0xff);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }
		}

        return hexString.toString();
	}

	
	/**
	 * Mostly deprecated due to vulnerabilities with MD5
	 * @param input
	 * @return MD5 String
	 */
	public static String md5(String input) {
		String hash = input;
		MessageDigest m = null;

	    try
	    {
            m = MessageDigest.getInstance("MD5");
    	    m.update(input.getBytes(), 0, input.length());
    	    hash = new BigInteger(1, m.digest()).toString(16);
	    }
	    catch (NoSuchAlgorithmException e) { }
	    return hash;
	}
	
	
	/**
	 * @warning Mostly deprecated due to vulnerabilities with MD5
	 * @param is inputStream to MD5 
	 * @return MD5 String
	 */
	public static String md5FromInputStream(InputStream is)  {
		MessageDigest md = null;
		byte[] byteArray = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		
		if(md != null){
			try {
				is = new DigestInputStream(is, md);
				// read stream to EOF as normal...
			}
			finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			byteArray = md.digest();
		}
		
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            String hex = Integer.toHexString(0xff & byteArray[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
	}

	
	/**
	 * Helper Function for printing the most error detail we can find.
	 * @param e Exception that we want printed
	 */
	public static String exceptionInfo(Exception e)
	{
		String toRet = null;
		if(e != null){
			toRet = e.getLocalizedMessage() == null ? e.toString() : e.getLocalizedMessage();
		}
		else
		{
			toRet = "[exception is null]";
		}
		
		return toRet;
	}
}