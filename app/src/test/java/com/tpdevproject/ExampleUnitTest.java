package com.tpdevproject;

import com.tpdevproject.utils.DateTimeUtils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void elapsedTimesTest() throws Exception {
        assertTrue(DateTimeUtils.elapsedTimes(new Long(1000),new Long(2000)).equals("1s"));
    }
    @Test
    public void elapsedTimesTest2() throws Exception {
        assertTrue(DateTimeUtils.elapsedTimes(new Long(1000),new Long(20000)).equals("19s"));
    }
    @Test
    public void elapsedTimesTest3() throws Exception {
        assertTrue(DateTimeUtils.elapsedTimes(new Long(1),new Long(240001)).equals("4m"));
    }
    @Test
    public void elapsedTimesTest4() throws Exception {
        assertTrue(DateTimeUtils.elapsedTimes(new Long(1),new Long(7200001)).equals("2h"));
    }
    @Test
    public void elapsedTimesTest5() throws Exception {
        assertTrue(DateTimeUtils.elapsedTimes(new Long(4),new Long(259200004)).equals("3d"));
    }
    @Test
    public void elapsedTimesTest6() throws Exception {
        assertTrue(DateTimeUtils.elapsedTimes(new Long(4),new Long(60007)).equals("1m"));
    }
    @Test
    public void elapsedTimesTest7() throws Exception {
        assertTrue(DateTimeUtils.elapsedTimes(new Long(1),new Long(3660001)).equals("1h"));
    }
    @Test
    public void elapsedTimesTestFail() throws Exception {
        assertFalse(DateTimeUtils.elapsedTimes(new Long(1000),new Long(200)).equals("1s"));
    }
}