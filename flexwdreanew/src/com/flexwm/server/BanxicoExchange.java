package com.flexwm.server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.symgae.shared.SFException;


public class BanxicoExchange{

	//Variables del webservice http://www.banxico.org.mx/DgieWSWeb/DgieWS?WSDL
	private final String URL        = "http://www.banxico.org.mx/DgieWSWeb/DgieWS";
	private final String NS         = "http://ws.dgie.banxico.org.mx";
	private final String OPERATION  = "tiposDeCambioBanxico";
	private final String CHARSET    = "ISO-8859-1";
	private final String ENVELOPE = "<?xml version=\"1.0\" encoding=\"" + CHARSET + "\" standalone=\"no\"?>"
			+ "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:apachesoap=\"http://xml.apache.org/xml-soap\" "
			+ "xmlns:impl=\"" + NS + "\" xmlns:intf=\"" + NS + "\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" "
			+ "xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:wsdlsoap=\"http://schemas.xmlsoap.org/wsdl/soap/\" "
			+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" >"
			+ "<SOAP-ENV:Body><mns:" + OPERATION + " xmlns:mns=\"" + NS + "\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
			+ "</mns:" + OPERATION + "></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	private final static String REGEX = 
			"<bm:Series\\s++TITULO\\s*+=\\s*+\"(?<titulo>[^\"]*+)\""
					+ "\\s++IDSERIE\\s*+=\\s*+\"SF43718\"[^>]*+>"
					+ "\\s*+<bm:Obs\\s++TIME_PERIOD\\s*+=\\s*+\"(?<fecha>[^\"]*+)\""
					+ "\\s++OBS_VALUE\\s*+=\\s*+\"(?<cotizacion>[^\"]*)";

	public static void main(String[] args) throws Exception {
		BanxicoExchange http = new BanxicoExchange();

		String resultado = http.sendPost();

		procesarTexto(resultado);

	}

	// Imprimir los datos
	public String getRate() throws SFException {
		String rate = "-1";

		try {
			// Sacar las entities 
			//  (se rompe el XML pero es mas facil procesarlo directamente)
			String xml = unescapeCommonEntities(sendPost());

			// Obtener los campos buscados con una expresion regular sobre todo el xml
			Pattern idPatt = Pattern.compile(REGEX);
			Matcher m = idPatt.matcher(xml);
			if (m.find()) {
				rate = m.group("cotizacion");
			} else {
				throw new SFException(this.getClass().getName() + "-getRate(): no se encontro la cotizacion.");
			}

		} catch (IOException e) {
			throw new SFException(this.getClass().getName() + "-getRate(): " + e.toString());
		}

		return rate;
	}

	// HTTP POST
	public String sendPost() throws MalformedURLException, ProtocolException, IOException {

		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Configuracion del Header
		con.setRequestMethod("POST");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Content-Encoding", "deflate");
		con.setRequestProperty("Content-Type", "text/xml; charset=" + CHARSET);
		con.setRequestProperty("SOAPAction", OPERATION);

		// Enviar el request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(ENVELOPE);
		wr.flush();
		wr.close();

		// Leer la respuesta
		//int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Devolver el XML
		return response.toString();
	}

	// Imprimir los datos
	public static void procesarTexto(final String uxml) {
		// Sacar las entities 
		//  (se rompe el XML pero es mas facil procesarlo directamente)
		String xml = unescapeCommonEntities(uxml);

		// Obtener los campos buscados con una expresion regular sobre todo el xml
		Pattern idPatt = Pattern.compile(REGEX);
		Matcher m = idPatt.matcher(xml);
		if (m.find()) {
			System.out.println("Cotizacion: " + m.group("cotizacion"));
			System.out.println("Fecha: " + m.group("fecha"));
			System.out.println("Descripcion: " + m.group("titulo"));
		} else {
			System.out.println("ERROR!");
		}
	}

	// Funcion para decodificar las 5 entities mas comunes
	private static String unescapeCommonEntities( final String xmle )
	{
		return xmle.replaceAll( "&lt;", "<" )
				.replaceAll( "&gt;", ">" )
				.replaceAll( "&amp;", "&" )
				.replaceAll( "&apos;", "'" )
				.replaceAll( "&quot;", "\"" );
	}

}