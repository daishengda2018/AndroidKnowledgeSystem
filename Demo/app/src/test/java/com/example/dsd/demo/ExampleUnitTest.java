package com.example.dsd.demo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        char[] charsScr = s.toCharArray();
        char[] charsTarget = t.toCharArray();
        int[] count = new int[26];

        for (int i = 0; i < charsScr.length; i++) {
            count[charsScr[i] - 'a'] ++;
            count[charsTarget[i] - 'a'] --;
        }

        for (int i : count) {
            if (i != 0) {
                return false;
            }
        }
        return true;
    }

    @Test
    public void text() {
        isAnagram("anagram", "nagaram");
    }
}