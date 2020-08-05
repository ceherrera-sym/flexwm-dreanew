/*
 * AmountByWorden.java
 *
 * Created on 1 de agosto de 2003, 11:00 AM
 */

package com.flexwm.server;


/**
 *
 * @author  mlopezz
 */
import java.util.Properties;

/**
 * The purpose is to return string representation on the number
 * in the human-readable format based on English grammar
 * Generally you can use this class as a base for your own language
 * like Russian, German, Spanish, French etc.
 * They must implement IAmountByWord interface
 *
 * @author Igor Artimenko <igorart7@yahoo.com>
 *
 * @version 1.0 Initial draft for review
 * Created on Jun 5, 2003
 *
 */

public class AmountByWorden implements IAmountByWord {
    
    final private Properties langTokens = new Properties();
    
    AmountByWorden() {
        
        langTokens.put( "0", "zero" );
        langTokens.put( "1", "one" );
        langTokens.put( "2", "two" );
        langTokens.put( "3", "three" );
        langTokens.put( "4", "four" );
        langTokens.put( "5", "five" );
        langTokens.put( "6", "six" );
        langTokens.put( "7", "seven" );
        langTokens.put( "8", "eight" );
        langTokens.put( "9", "nine" );
        langTokens.put( "10", "ten" );
        langTokens.put( "11", "eleven" );
        langTokens.put( "12", "twelve" );
        langTokens.put( "13", "thirteen" );
        langTokens.put( "14", "fourteen" );
        langTokens.put( "15", "fifteen" );
        langTokens.put( "16", "sixteen" );
        langTokens.put( "17", "seventeen" );
        langTokens.put( "18", "eighteen" );
        langTokens.put( "19", "nineteen" );
        langTokens.put( "20", "twenty" );
        langTokens.put( "30", "thirty" );
        langTokens.put( "40", "fourty" );
        langTokens.put( "50", "fifty" );
        langTokens.put( "60", "sixty" );
        langTokens.put( "70", "seventy" );
        langTokens.put( "80", "eighty" );
        langTokens.put( "90", "ninety" );
        langTokens.put( "100", "hundred" );
        langTokens.put( "1000", "thousand" );
        langTokens.put( "1000000", "million" );
        langTokens.put( "1000000000", "billion" );
        langTokens.put( "1000000000000L", "trillion" );
        langTokens.put( "minus", "minus" );
        langTokens.put( "and", "and" );
        langTokens.put( "point", "point" );
    }
    
/* (non-Javadoc)
 * It does not yet working. Proper implementation coming soon.
 * @see org.apache.bussinessapi.IAmountByWord#getAmountByWord(double)
 */
    public String getAmountByWord( double amount ) throws Exception {
        
        NumberByWord numberByWord = new NumberByWord();
        StringBuffer fullNumber = new StringBuffer();
        
        numberByWord.setLanguageTokens( langTokens );
        
        // also should be replaced by full phrase
        fullNumber = fullNumber.append(
        numberByWord.getNumberByWord( (long) amount ).trim() );
        
        // for now 2 numbers after .
        int theRest = (int) ( ( amount - (long) amount ) * 100 );
        if ( theRest != 0 ) {
            fullNumber.append( " " ). append( langTokens.get( "point" ) ).
            append( " " ).append(
            numberByWord.getNumberByWord( (long) theRest ).trim() );
        }
        
        return fullNumber.toString();
    }
    
/* (non-Javadoc)
 * It does not yet working. Proper implementation coming soon.
 * @see org.apache.bussinessapi.IAmountByWord#getMoneyAmountByWord(double)
 */
    public String getMoneyAmountByWord( double amount ) throws Exception {
        
        NumberByWord numberByWord = new NumberByWord();
        StringBuffer fullNumber = new StringBuffer();
        
        numberByWord.setLanguageTokens( langTokens );
        
        // also should be replaced by full phrase
        fullNumber = fullNumber.append(
        numberByWord.getNumberByWord( (long) amount ).trim() );
        
        // for now 2 numbers after .
        int theRest = (int) ( ( amount - (long) amount ) * 100 );
        if ( theRest != 0 ) {
            fullNumber.append( " " ). append( langTokens.get( "point" ) ).
            append( " " ).append(
            numberByWord.getNumberByWord( (long) theRest ).trim() );
        }
        
        return fullNumber.toString();
    }
    
}
