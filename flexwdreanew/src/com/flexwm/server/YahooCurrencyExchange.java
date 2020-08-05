package com.flexwm.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import com.symgae.shared.SFException;

public class YahooCurrencyExchange {
	
    public static Double currencyExchange(String from, String to, int amount) throws SFException {
        try {
            //Yahoo Finance API
            URL url = new URL("http://finance.yahoo.com/d/quotes.csv?f=l1&s="+ from + to + "=X");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            if (line.length() > 0) {
            	System.out.println("URL: " + url +  ", Valor recuperado: " + line);
                return Double.parseDouble(line) * amount;
            }
            reader.close();
        } catch (Exception e) {
            throw new SFException("-currencyExchange" + e.toString());
        }
        return null;
    }
}
