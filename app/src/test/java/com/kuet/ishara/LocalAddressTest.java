package com.kuet.ishara;
import com.kuet.ishara.network.LocalAddress;
import org.junit.Test;
import static org.junit.Assert.*;
public class LocalAddressTest {
 @Test public void onlyPrivateIpv4Allowed(){assertTrue(LocalAddress.isPrivateIPv4("192.168.1.4"));assertTrue(LocalAddress.isPrivateIPv4("10.3.1.9"));assertTrue(LocalAddress.isPrivateIPv4("172.16.9.1"));assertFalse(LocalAddress.isPrivateIPv4("8.8.8.8"));assertFalse(LocalAddress.isPrivateIPv4("999.168.1.1"));assertFalse(LocalAddress.isPrivateIPv4("example.com"));}
 @Test public void validateCodeAndMessages(){assertTrue(LocalAddress.validCode("012345"));assertFalse(LocalAddress.validCode("1234"));assertFalse(LocalAddress.validCode("12x456"));assertTrue(LocalAddress.validMessage("আমি পানি চাই"));assertFalse(LocalAddress.validMessage(" "));assertFalse(LocalAddress.validMessage("a".repeat(401)));}
}
