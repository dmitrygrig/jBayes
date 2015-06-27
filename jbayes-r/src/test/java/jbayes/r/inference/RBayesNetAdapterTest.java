/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jbayes.r.inference;

import jbayes.core.BayesNet;
import jbayes.r.inference.RBayesInfererBase.RBayesNetAdapter;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.*;


/**
 *
 * @author Dmytro
 */
public class RBayesNetAdapterTest {
    
    public RBayesNetAdapterTest() {
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_underlying_bn_cannot_be_null() {
        new RBayesNetAdapter(null);
    }

    @Test
    public void test_underlying_bn_is_not_null() {
        BayesNet bn = mock(BayesNet.class);
        RBayesNetAdapter instance = new RBayesNetAdapter(bn);
        final BayesNet result = instance.getNetwork();
        assertNotNull(result);
    }
    
    @Test
    public void test_alias_when_it_is_based_on_bn_name() {
        BayesNet bn = mock(BayesNet.class);
        when(bn.getName()).thenReturn("asia");
        RBayesNetAdapter instance = new RBayesNetAdapter(bn);
        
        final String expected = "bn.asia";
        final String result = instance.getAlias();
        
        assertEquals(expected, result);
    }
    
    @Test
    public void test_alias_when_it_is_set_in_ctor() {
        BayesNet bn = mock(BayesNet.class);
        RBayesNetAdapter instance = new RBayesNetAdapter(bn, "asianet");
        
        final String expected = "asianet";
        final String result = instance.getAlias();
        
        assertEquals(expected, result);
    }
    
    @Test
    public void test_alias_when_it_is_set_in_setter() {
        BayesNet bn = mock(BayesNet.class);
        RBayesNetAdapter instance = new RBayesNetAdapter(bn);
        instance.setAlias("asianet");
        
        final String expected = "asianet";
        final String result = instance.getAlias();
        
        assertEquals(expected, result);
    }
    
}
