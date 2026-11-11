package com.kuet.ishara.auth;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
/** Portable Java logic. No Android dependencies. */
public final class PasswordHasher {
    private static final int ROUNDS=150000;
    private PasswordHasher(){}
    public static String newSalt(){byte[] bytes=new byte[16];new SecureRandom().nextBytes(bytes);return hex(bytes);}
    private static String hex(byte[] data){StringBuilder out=new StringBuilder();for(byte b:data)out.append(String.format(java.util.Locale.US,"%02x", b & 255));return out.toString();}
    private static byte[] unhex(String hex){byte[] a=new byte[hex.length()/2];for(int i=0;i<a.length;i++)a[i]=(byte)Integer.parseInt(hex.substring(i*2,i*2+2),16);return a;}
    public static String digest(char[] password,String salt){PBEKeySpec spec=new PBEKeySpec(password,unhex(salt),ROUNDS,256);try{
      SecretKeyFactory factory=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");return hex(factory.generateSecret(spec).getEncoded());
    }catch(Exception e){throw new IllegalStateException("Password hashing unavailable",e);}finally{spec.clearPassword();}}
    public static boolean verify(char[] password,String salt,String expected){try {return MessageDigest.isEqual(unhex(digest(password,salt)),unhex(expected));}catch(RuntimeException e){return false;}}
}
