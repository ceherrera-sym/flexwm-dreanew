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
public class AmountByWordes implements IAmountByWord {
    
    final private Properties langTokens = new Properties();
    
    AmountByWordes() {
        
        langTokens.put( "0", "cero" );
        langTokens.put( "1", "uno" );
        langTokens.put( "2", "dos" );
        langTokens.put( "3", "tres" );
        langTokens.put( "4", "cuatro" );
        langTokens.put( "5", "cinco" );
        langTokens.put( "6", "seis" );
        langTokens.put( "7", "siete" );
        langTokens.put( "8", "ocho" );
        langTokens.put( "9", "nueve" );
        langTokens.put( "10", "diez" );
        langTokens.put( "11", "once" );
        langTokens.put( "12", "doce" );
        langTokens.put( "13", "trece" );
        langTokens.put( "14", "catorce" );
        langTokens.put( "15", "quince" );
        langTokens.put( "16", "dieciseis" );
        langTokens.put( "17", "diecisiete" );
        langTokens.put( "18", "dieciocho" );
        langTokens.put( "19", "diecinueve" );
        langTokens.put( "20", "veinte" );
        langTokens.put( "30", "treinta" );
        langTokens.put( "40", "cuarenta" );
        langTokens.put( "50", "cincuenta" );
        langTokens.put( "60", "sesenta" );
        langTokens.put( "70", "setenta" );
        langTokens.put( "80", "ochenta" );
        langTokens.put( "90", "noventa" );
        langTokens.put( "100", "cien" );
        langTokens.put( "1000", "mil" );
        langTokens.put( "1000000", "millones" );
        langTokens.put( "1000000000", "billones" );
        langTokens.put( "1000000000000L", "trillones" );
        langTokens.put( "minus", "menos" );
        langTokens.put( "and", "y" );
        langTokens.put( "point", "punto" );
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
        fullNumber = fullNumber.append(numberByWord.getNumberByWord( (long) amount ).trim() );
        
        // for now 2 numbers after .
        int theRest = (int) Math.ceil((amount - (long)amount) * 100);
        if ( theRest != 0 ) {
            fullNumber.append( " " ). append( langTokens.get( "point" ) ).
            // Presenta los decimales con letras
            append( " " ).append(numberByWord.getNumberByWord( (long) theRest ).trim() );
        }        
        
        return fixAmount(fullNumber.toString(), amount);
    }
    
    private String fixAmount(String s, double amount) {
    		
        s = s.replaceAll("veinte uno", "veintiuno");
        s = s.replaceAll("veinte dos", "veintidos");
        s = s.replaceAll("veinte tres", "veintitres");
        s = s.replaceAll("veinte cuatro", "veinticuatro");
        s = s.replaceAll("veinte cinco", "veinticinco");
        s = s.replaceAll("veinte seis", "veintiseis");
        s = s.replaceAll("veinte siete", "veintisiete");
        s = s.replaceAll("veinte ocho", "veintiocho");
        s = s.replaceAll("veinte nueve", "veintinueve");
        
        s = s.replaceAll("dos cien", "doscientos");
        s = s.replaceAll("tres cien", "trescientos");
        s = s.replaceAll("cuatro cien", "cuatrocientos");
        s = s.replaceAll("cinco cien", "quinientos");
        s = s.replaceAll("seis cien", "seiscientos");
        s = s.replaceAll("siete cien", "setecientos");
        s = s.replaceAll("ocho cien", "ochocientos");
        s = s.replaceAll("nueve cien", "novecientos");
        s = s.replaceAll("nueve cien", "novecientos");
        
        s = s.replaceAll("uno cien uno","ciento un");
        s = s.replaceAll("uno cien dos","ciento dos");
        s = s.replaceAll("uno cien tres","ciento tres");
        s = s.replaceAll("uno cien cuatro","ciento cuatro");
        s = s.replaceAll("uno cien cinco","ciento cinco");
        s = s.replaceAll("uno cien seis","ciento seis");
        s = s.replaceAll("uno cien siete","ciento siete");
        s = s.replaceAll("uno cien ocho","ciento ocho");
        s = s.replaceAll("uno cien nueve","ciento nueve");
        
        s = s.replaceAll("cien once","ciento once");
        s = s.replaceAll("cien doce","ciento doce");
        s = s.replaceAll("cien trece","ciento trece");
        s = s.replaceAll("cien catorce","ciento catorce");
        s = s.replaceAll("cien quince","ciento quince");
        s = s.replaceAll("cien dieciseis","ciento dieciseis");
        s = s.replaceAll("cien diecisiete","ciento dicisiete");
        s = s.replaceAll("cien dieciocho","ciento dieciocho");
        s = s.replaceAll("cien diecinueve","ciento diecinueve");
        
        s = s.replaceAll("uno cien diez","ciento diez");
        s = s.replaceAll("uno cien veinte","ciento veinte");
        s = s.replaceAll("uno cien treinta","ciento treinta");
        s = s.replaceAll("uno cien cuarenta","ciento cuarenta");
        s = s.replaceAll("uno cien cincuenta","ciento cincuenta");
        s = s.replaceAll("uno cien sesenta","ciento sesenta");
        s = s.replaceAll("uno cien setenta","ciento setenta");
        s = s.replaceAll("uno cien ochenta","ciento ochenta");
        s = s.replaceAll("uno cien noventa","ciento noventa");
        
        s = s.replaceAll("uno millones", "un millon");
		s = s.replaceAll("uno mil", "un mil");
        s = s.replaceAll("millones mil", "millones de"); 
        s = s.replaceAll("millon mil", "millon de");
        
        s = s.replaceAll("uno cien", "cien");
        return s;
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
        fullNumber = fullNumber.append(numberByWord.getNumberByWord( (long) amount ).trim() );
        
        // for now 2 numbers after .
        int theRest = (int)Math.round(((amount - (long)amount)*100));
        
        String cents = "";
        // Asigna los decimales como palabras 
        //cents = " con cero centavos";
        //if (theRest > 0) cents = " con " + numberByWord.getNumberByWord( (long) theRest ).trim() + " centavos";
        
        // Asigna los decimales como numeros
        cents = " 00/100 ";
        if (theRest > 0) cents = " " + numberToConsecutive(theRest,2).trim() + "/100";
        
        fullNumber.append(" pesos" + cents + " m.n.");
        
        return fixAmount(fullNumber.toString(), amount);
    }
    
    /**
	 * El metodo numberToConsecutive agrega un cero a una lista que tiene un
	 * numero por cada elemento de la lista.
	 * @param number Variable de tipo entero
	 * @param zeros Varibale de tipo entero
	 * @return
	 */
    public static String numberToConsecutive(int number, int zeros) {
		String s = "" + number;
		for (int i = 0; i < zeros; i++) {
			if (s.length() < zeros)
				s = "0" + s;
		}
		return s;
	}
    
}
