/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbayes.r;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.rosuda.JRI.REXP;

/**
 * Integration tests for @link{R}. Note, that environment variable "R_LIB_HOME"
 * should be set (e.g., %USERPROFILE%\Documents\R\win-library\3.1\).
 *
 * @author Dmytro
 */
public class RTest {

    private final static Double DELTA = 1e-15;
    private static R r;


    @Before
    public void setUp() throws Exception {
        r = R.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        r.clearAll();
        r = null;
    }

    @Test
    public void test_getInstance_with_default_env_var_is_not_null() {
        assertNotNull(r);
    }
    
    @Test
    public void test_eval_of_non_existed_variable_returns_null() {
        final REXP result = r.eval("a");
        assertNull(result);
    }

    @Test
    public void test_eval_returns_correct_result() {
        final double expected = 4.0;
        double result = r.eval("2+2").asDouble();
        assertEquals(expected, result, DELTA);
    }

    @Test
    public void test_eval_of_variables_returns_correct_result() {
        final double expected = 4.0;
        r.eval("a<-2+2");
        double result = r.eval("a").asDouble();
        assertEquals(expected, result, DELTA);
    }

    @Test
    public void test_attach_library_throws_no_exception() {
        r.lib("gRbase");
    }

    @Test
    public void test_clearAll_cleares_variables_in_R_environment() {
        r.eval("a<-2+2");
        r.clearAll();
        final REXP result = r.eval("a");
        assertNull(result);
    }
}
