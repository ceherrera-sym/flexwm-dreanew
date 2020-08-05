/*
 * AmountByWord.java
 *
 * Created on 1 de agosto de 2003, 10:59 AM
 */

package com.flexwm.server;

/**
 *
 * @author  mlopezz
 */
public class AmountByWord implements IAmountByWord {
    
    private String language;
    private String currency;
      
    /**
     * Constructor AmountByWord
     */
    public AmountByWord() {
        super();
    }
    
    /**
     * Method getAmountByWord is the most important method
     * @param amount to be represented in the human-readable format
     * @return String the phrase representation of number
     */
    public String getAmountByWord( double amount ) throws Exception {
        IAmountByWord amountByWord = null;
        
        // First, create an appropriate instance
        try {
            
            if ( language == null ) {
                throw new Exception( "Language has not been assigned yet" );
            }
            else if ( currency == null ) {
                throw new Exception( "Currency has not been assigned yet" );
            }
            
            Class<?> cl = Class.forName( "com.flexwm.server.AmountByWord".concat( language ) );
            
            try {
                amountByWord = (IAmountByWord) cl.newInstance();
            }
            catch ( InstantiationException e ) {
                throw new Exception( "can not be instantiated " + e.toString() );
            } // catch
            catch ( IllegalAccessException e ) {
                throw new Exception(
                "IllegalAccessException arrised during instantiating of " );
            } // catch
            
        } // try
        catch ( ClassNotFoundException e ) {
            throw new Exception( "Class could not be found " );
        } // catch
        
        // for now the number only
        return amountByWord.getAmountByWord( amount );
        
    }
    
    /**
     * Method getMoneyAmountByWord is the most important method
     * @param amount to be represented in the human-readable format
     * @return String the phrase representation of number
     */
    public String getMoneyAmountByWord( double amount ) throws Exception {
        IAmountByWord amountByWord = null;
        
        // First, create an appropriate instance
        try {
            
            if ( language == null ) {
                throw new Exception( "Language has not been assigned yet" );
            }
            else if ( currency == null ) {
                throw new Exception( "Currency has not been assigned yet" );
            }
            
            Class<?> cl = Class.forName( "com.flexwm.server.AmountByWord".concat( language ) );
            
            try {
                amountByWord = (IAmountByWord) cl.newInstance();
            }
            catch ( InstantiationException e ) {
                throw new Exception( "can not be instantiated " + e.toString() );
            } 
            catch ( IllegalAccessException e ) {
                throw new Exception(
                "IllegalAccessException arrised during instantiating of " + e.toString());
            } 
            
        }
        catch ( ClassNotFoundException e ) {
            throw new Exception( "Class could not be found " + e.toString());
        }
        
        // for now the number only
        return amountByWord.getMoneyAmountByWord( amount );
    }
    
    /**
     * getCurrency Method
     * @return To retrieve currency code currently used
     */
    public String getCurrency() {
        return currency;
    }
    
    /**
     * getLanguage
     * @return To retrieve Language locale code currently used
     */
    public String getLanguage() {
        return language;
    }
    
    /**
     * setCurrency
     * @return To setup Currency locale code to be used
     */
    public void setCurrency( String currency ) {
        this.currency = currency;
    }
    
    /**
     * setLanguage
     * @return To setup Language locale code to be used
     */
    public void setLanguage( String language ) {
        this.language = language;
    }
    
}
