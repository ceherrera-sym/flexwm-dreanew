/*
 * NumberByWord.java
 *
 * Created on 1 de agosto de 2003, 11:01 AM
 */

package com.flexwm.server;


/**
 *
 * @author  mlopezz
 */
import java.util.*;

/**
 * This class implements generic algoritm of converting a number into
 * human-readable format.
 *
 *
 * @author Igor Artimenko <igorart7@yahoo.com>
 *
 * @version 1.0 Initial draft for review
 * Created on Jun 5, 2003
 *
 * You can use getNumberByWord method
 * if your language fits into this algorithm or
 * extend from this class and make some transformations in the child
 * method.
 *
 * Don't forget to setup language Tokens
 * via setLanguageTokens method call first
 *
 */
public class NumberByWord {
    
    private Properties langTokens = new Properties();
    
    /**
     * Constructor for NumberByWord.
     */
    public NumberByWord() {
        super();
    }
    
    /**
     * Method getNumberByWord. The most important method.
     * Standard actions are here
     * @param amount Accepts only mathematical integer
     * @return String representation of this phrase
     */
    @SuppressWarnings("unchecked")
	public String getNumberByWord( long amount ) {
        StringBuffer strNumberByWord = new StringBuffer( "" );
        @SuppressWarnings("rawtypes")
		SortedMap smallAmounts = new TreeMap();
        String str = new Long( Math.abs( amount ) ).toString();
        int currentPosition = str.length();
        long currentThousand = 1;
        
        // break by thousands
        while ( currentPosition > 0 ) {
            smallAmounts.put( new Long( currentThousand ), new Integer(
            str.substring( Math.max( 0, currentPosition - 3 ),
            currentPosition ) ) );
            
            strNumberByWord.insert( 0,
            getSmallest( (Integer) smallAmounts.get( new Long( currentThousand ) ) ) +
            ( currentThousand == 1 ? "" : " " +
            langTokens.getProperty( new Long( currentThousand ).toString() ) ) );
            
            currentPosition -= 3;
            currentThousand *= 1000;
        }
        
        if ( amount == 0 ) {
            strNumberByWord.append( langTokens.getProperty( "0" ) );
        }
        else if ( amount < 0 ) {
            strNumberByWord.insert( 0, langTokens.getProperty( "minus" ) );
        }
        return strNumberByWord.toString().trim();
        
    }
    
    /**
     * @param smallNumber this number must in the range from 0 to 999
     * inclusive
     * @return String representation of this number
     */
    public StringBuffer getSmallest( Integer smallNumber ) {
        StringBuffer smallestNumberByWord = new StringBuffer( "" );
        int hundreds = (int) ( smallNumber.intValue() / 100 );
        int dozens = (int)( ( smallNumber.intValue() - hundreds * 100 ) / 10 );
        int rest = smallNumber.intValue() - hundreds * 100 - dozens * 10;
        
        // using smallNumber create small phrase
        // Let's compund the phrase itself
        if ( hundreds > 0 ) {
            smallestNumberByWord.append( " " ).append(
            langTokens.getProperty( new Long( hundreds ).toString() ) ).append( " " ).append(
            langTokens.getProperty( new Long( 100 ).toString() ) );
        }
        
        Object obj = langTokens.getProperty( new Long( dozens * 10 + rest ).toString() );
        
        // is atomic like 15 juust substitute
        if ( dozens * 10 + rest != 0 ) {
            
            if ( obj != null ) {
                smallestNumberByWord.append( " " ).append( obj );
            }
            else {
                if (rest > 0) {
                    smallestNumberByWord.append( " " ).append(
                    langTokens.getProperty( new Long( dozens * 10 ).toString() ) ).
                    append( " " + langTokens.getProperty("and") + " " ).append( langTokens.getProperty( new Long( rest ).
                    toString() ) );
                } else {
                    smallestNumberByWord.append( " " ).append(
                    langTokens.getProperty( new Long( dozens * 10 ).toString() ) ).
                    append( " " ).append( langTokens.getProperty( new Long( rest ).
                    toString() ) );                    
                }
                
            }
            
        }
        
        return smallestNumberByWord;
    }
    
    /**
     * setLanguageTokens Use before making call to getNumberByWord
     * @param langTokens Properties of language token used by
     * particular language
     */
    public void setLanguageTokens( Properties langTokens ) {
        this.langTokens = langTokens;
    }
    
}
