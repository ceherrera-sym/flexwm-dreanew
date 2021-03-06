package com.flexwm.server;

import javax.xml.soap.*;

public class BanxicoObsolete {

	    public static void main(String args[]) throws Exception {
	        // Create SOAP Connection
	        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
	        SOAPConnection soapConnection = soapConnectionFactory.createConnection();

	        // Send SOAP Message to SOAP Server
	        String url = "http://www.banxico.org.mx:80/DgieWSWeb/DgieWS?WSDL";
	        SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(), url);

	        // print SOAP Response
	        System.out.print("Response SOAP Message:");
	        
	        System.out.print("Item: " + soapResponse.getProperty("Obs"));
	        System.out.println();	        
	        
	        soapResponse.writeTo(System.out);
	        
	        System.out.println();

	        soapConnection.close();
	    }

	    private static SOAPMessage createSOAPRequest() throws Exception {
	        MessageFactory messageFactory = MessageFactory.newInstance();
	        SOAPMessage soapMessage = messageFactory.createMessage();
	        SOAPPart soapPart = soapMessage.getSOAPPart();

	        String serverURI = "http://DgieWSWeb/DgieWS?WSDL/";

	        // SOAP Envelope
	        SOAPEnvelope envelope = soapPart.getEnvelope();
	        envelope.addNamespaceDeclaration("example", serverURI);

	        /*
	        Constructed SOAP Request Message:
	        <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:example="http://ws.cdyne.com/">
	            <SOAP-ENV:Header/>
	            <SOAP-ENV:Body>
	                <example:VerifyEmail>
	                    <example:email>mutantninja@gmail.com</example:email>
	                    <example:LicenseKey>123</example:LicenseKey>
	                </example:VerifyEmail>
	            </SOAP-ENV:Body>
	        </SOAP-ENV:Envelope>
	         */

	        // SOAP Body
//	        SOAPBody soapBody = envelope.getBody();
//	        SOAPElement soapBodyElem = soapBody.addChildElement("tiposDeCambioBanxico", "example");
//	        SOAPElement soapBodyElem1 = soapBodyElem.addChildElement("email", "example");
//	        soapBodyElem1.addTextNode("mutantninja@gmail.com");
//	        SOAPElement soapBodyElem2 = soapBodyElem.addChildElement("LicenseKey", "example");
//	        soapBodyElem2.addTextNode("123");

	        MimeHeaders headers = soapMessage.getMimeHeaders();
	        headers.addHeader("SOAPAction", serverURI  + "tiposDeCambioBanxico");

	        soapMessage.saveChanges();

	        /* Print the request message */
	        System.out.print("Request SOAP Message:");
	        soapMessage.writeTo(System.out);
	        
	        System.out.println();	        
	        System.out.println();
	        
	        System.out.println();

	        return soapMessage;
	    }

	
}
