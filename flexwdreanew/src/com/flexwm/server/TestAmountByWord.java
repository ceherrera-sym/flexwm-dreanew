/*
 * TestAmountByWord.java
 *
 * Created on 1 de agosto de 2003, 10:58 AM
 */

package com.flexwm.server;


/**
 *
 * @author  mlopezz
 */
public class TestAmountByWord {
    
    public TestAmountByWord() {
        
        double array[] = { 9000000, 262000.00, 1334.23, 1120299.99};
        AmountByWord amountByWord = new AmountByWord();
        amountByWord.setLanguage( "es" );
        amountByWord.setCurrency( "es" );
        
        for (int i = 0; i < array.length; i++) {
            System.out.println( array[ i ] );
            
            try {
                System.out.println( amountByWord.getMoneyAmountByWord( array[ i ] ) );
            } catch (Exception e) {
                e.printStackTrace();
            }            
            System.out.println( "---------------------------------" );
        }
    }
    
    public static void main(String[] args) {
    	
    }
}

