package com.ochodek.server;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void filterDiacriticChars() {
        String sourceString = "ąźĘŹĆŚół";
        assertEquals("azEZCSol", StringUtils.filterDiacriticChars(sourceString));
    }
}