/*
 * IAmountByWord.java
 *
 * Created on 1 de agosto de 2003, 11:00 AM
 */

package com.flexwm.server;


/**
 *
 * @author  mlopezz
 */
public interface IAmountByWord {
    
    /**
     * getMoneyAmountByWord The purpose of this method is to return
     * string representation on the number in the human-readable format
     * in a regular format ( like 25.78
     * "twenty five point seventy eight" )
     *
     * @param anAmount to be converted into it's string representation
     * @return A string representation of the number
     * @throws Exception if something went wrong
     */
    public String getAmountByWord( double anAmount ) throws Exception;
    
    
    /**
     * getMoneyAmountByWord The purpose of this method is to return
     * string representation on the number in the human-readable format
     * in a money format like ( 25.78 Can
     * "twenty five Canadian Dollars and seventy eight Cents" )
     *
     * @param anAmount to be converted into it's string representation
     * @return A string representation of the number
     * @throws Exception if something went wrong
     */
    public String getMoneyAmountByWord( double anAmount )
    throws Exception;
    
}
