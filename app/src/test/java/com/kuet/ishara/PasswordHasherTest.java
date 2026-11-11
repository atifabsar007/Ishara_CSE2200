package com.kuet.ishara;
import com.kuet.ishara.auth.PasswordHasher;
import org.junit.Test;
import static org.junit.Assert.*;
public class PasswordHasherTest {
 @Test public void saltedVerifierWorks(){String salt=PasswordHasher.newSalt();String hash=PasswordHasher.digest("strongPass123".toCharArray(),salt);assertTrue(PasswordHasher.verify("strongPass123".toCharArray(),salt,hash));assertFalse(PasswordHasher.verify("incorrect".toCharArray(),salt,hash));assertFalse(PasswordHasher.newSalt().equals(salt));}
}
