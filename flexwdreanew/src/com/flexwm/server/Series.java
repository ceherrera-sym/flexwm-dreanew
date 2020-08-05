package com.flexwm.server;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symgae.server.SFServerUtil;


public class Series {

	// SieAPI Tipo de cambio Banco de Mexico
	// (Archivos relacionados en com.flexwm.server: DataSerie, Response, Serie, Series, SeriesResponse)

	public static Response readSeries() throws Exception {
		// Del Catalogo de Series: https://www.banxico.org.mx/SieAPIRest/service/v1/doc/catalogoSeries
		// 		Titulo de serie: Tipo de cambio Pesos por dólar E.U.A. Tipo de cambio para solventar obligaciones denominadas 
		//							en moneda extranjera Fecha de determinación (FIX).
		// 		URL: https://www.banxico.org.mx/SieAPIRest/service/v1/series/SF43718

		//La URL a consultar con los parametros de idSerie y fechas 
		String now = SFServerUtil.dateToString(new Date(), "yyyy-MM-dd");

		// Ajuste de dias; si es domingo o lunes traer el del viernes
		Calendar calDate = SFServerUtil.stringToCalendar("yyyy-MM-dd", now);
		int dia = calDate .get(Calendar.DAY_OF_WEEK);
		int removeDays = 1;
		// domingo
		if (dia == 1) removeDays = 2;

		// lunes
		if (dia == 2) removeDays = 3;

		// Quitar días para tomar el FIX del día pasado(o del viernes) ya que los FIX del día actual los coloca banxico a las 12pm
		now = SFServerUtil.addDays("yyyy-MM-dd", now, -removeDays);

		String idSerie = "SF43718"; // De Catalogo de series

		URL url = new URL("https://www.banxico.org.mx/SieAPIRest/service/v1/series/" + idSerie + "/datos/" + now + "/" + now + "");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//Se realiza una petición GET
		conn.setRequestMethod("GET");
		//Se solicita que la respuesta esté en formato JSON
		conn.setRequestProperty("Content-Type", "application/json");
		//Se envía el header Bmx-Token con el token de consulta
		//Modificar por el token de consulta propio
		conn.setRequestProperty("Bmx-Token", "c7695991ff9437b3eb159c5f25ac6b3cf002cb095f2bc6aaea5f7d6b77e6593e");

		//En caso de ser exitosa la petición se devuelve un estatus HTTP 200
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("HTTP error code : "+ conn.getResponseCode());
		}

		//Se utiliza Jackson para mapear el JSON a objetos Java
		ObjectMapper mapper = new ObjectMapper();
		Response response=mapper.readValue(conn.getInputStream(), Response.class);

		conn.disconnect();

		return response;
	}

	//	public static void main(String[] args) {
	//		try {
	//			Response response=readSeries();
	//			Serie serie=response.getBmx().getSeries().get(0);
	//			System.out.println("Serie: "+serie.getTitulo());
	//			for(DataSerie data:serie.getDatos()){
	//				//Se omiten las observaciones sin dato (N/E)
	//				if(data.getDato().equals("N/E")) continue;
	//				System.out.println("Fecha: "+data.getFecha());
	//				System.out.println("Dato: "+data.getDato());
	//			}
	//			
	//		} catch(Exception e) {
	//			System.out.println("ERROR: "+e.getMessage());
	//		}
	//	}

}
