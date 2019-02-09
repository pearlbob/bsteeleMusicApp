package com.bsteele.bsteeleMusicApp.shared.songs;

import com.bsteele.bsteeleMusicApp.shared.JsonUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class MetadataTest {

    @Test
    public void toJson() {

        assertEquals("\t", JsonUtil.encode(" "));
      assertEquals("abc",  JsonUtil.encode("abc"));
        assertEquals("", JsonUtil.encode(null));
        assertEquals("", JsonUtil.encode(""));

    }

    @Test
    public void compareTo() {
    }
}