package com.flexwm.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.symgae.server.HtmlUtil;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmField;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoTag;


public class FlexUtil {

	//Envio de Correo
	public static String mailBodyFormat(SFParams sFParams, String subject, String msg,String logourl) {
		String html = "";

		// Cambiar salto de lineas en el texto de body
		String msgAdjust = HtmlUtil.newLineToBr(msg);

		html += " <HTML> "
				+ "<BODY> "
				+ "<TABLE bgcolor=\"#EDEDED\" width=\"95%\" height=\"95%\"> "
				+ "	<TR> "
				+ "		<TD width=\"100%\" heigh=\"100%\" valign=\"top\"> "
				+ "			<TABLE width=\"500px\" style=\"font-family:verdana; font-size:10pt;\" align=\"center\" border=\"0\" bordercolor=\"silver\" cellpading=\"0\" cellspacing=\"0\"> "  
				+ "				<TR> "
				+ "					<TD width=\"75\" align=\"left\" height=\"75\" colspan=\"2\" valign=\"bottom\"> "  
				+ "						<img border=\"0\" width=\"" + SFParams.LOGO_WIDTH + "\" height=\"" + SFParams.LOGO_HEIGHT + "\" src=\"" + sFParams.getAppURL() + "uploadfiles/" +logourl + "" +  "\"> " 
				+ "					</TD> "
				+ "				</TR> "
				+ "				<TR> "
				+ "					<TD bgcolor=\"black\"  align=\"center\" colspan=\"3\" height=\"35px\" style=\"font-size:14px; color:white\"> "  
				+ " 					<b>"
				+ 						subject
				+ "						</b> "
				+ "					</TD> "
				+ "				</TR> "
				+ "				<TR> "
				+ "					<TD bgcolor=\"white\" align=\"center\" width=\"50px\"> "  
				+ "							&nbsp; "
				+ "					 </TD> " 
				+ "					<TD bgcolor=\"white\" align=\"justify\" width=\"400px\" valign=\"top\"> "
				+ "						<br>" 
				+ 						msgAdjust	
				+ "					</TD> "
				+ "					<TD bgcolor=\"white\" align=\"center\" width=\"50px\"> "  
				+ "							&nbsp;  "
				+ "					 </TD> "  
				+ "				</TR> "
				+ "				<TR> "
				+ "					<TD bgcolor=\"white\" colspan=\"3\" align=\"center\" height=\"40px\"> " 
				+ "						 &nbsp;	<br><br><br><br>"
				+ "					</TD> "
				+ "				</TR> "
				+ "				<TR> "
				+ "					<TD bgcolor=\"#EDEDED\" colspan=\"3\" align=\"center\" height=\"30px\"> " 
				+ "						 <br><a href=\"http://www.symetria.com.mx\" title=\"FlexWM Web-Based Management\" style=\"font-size:11px\"><img src=\"" + sFParams.getAppURL() + "/img/main.png\" height=\"25\"></a>  "
				+ "					</TD> "
				+ "				</TR> "
				+ "				<TR bgcolor=\"#EDEDED\" > " 
				+ "					<TD align=\"center\" width=\"100px\" height=\"50px\"> "  
				+ "							&nbsp;  "
				+ "					 </TD> " 
				+ "					<TD align=\"center\" style=\"font-size:9px\"> " 
				+ "						 &nbsp;©" + SFServerUtil.nowToString(sFParams, "YYYY") + " FlexWM, FlexWM Web-Based Management y el logo FlexWM "
				+ "						 son marcas registradas de Ctech Consulting S.A. de C.V. y Symetria Tecnológica S.A. de C.V. "
				+ "						  <br> "
				+ "						 Todos los Derechos Reservados. "
				+ "					</TD> "
				+ "					<TD align=\"center\" > "  
				+ "							&nbsp; " 
				+ "					 </TD> " 
				+ "				</TR> " 
				+ "				<TR bgcolor=\"#EDEDED\" > "
				+ "					<TD colspan=\"3\" align=\"center\" style=\"font-size:8px\" height=\"50px\"> " 
				+ "						 &nbsp; "
				+ "					</TD> "
				+ "				</TR> "
				+ "			</TABLE> "
				+ "		</TD> "
				+ "	</TR> "
				+ "</TABLE> "
				+ "</BODY> "  
				+ "</HTML> "
				; 
		return html;
	}

	public static String dateToLongDate(SFParams sFParams, String date) throws SFException {
		Calendar cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), date);
		int month = cal.get(Calendar.MONTH);
		String mes = "";
		switch (month) {
		case 0:
			mes = "Enero";
			break;
		case 1:
			mes = "Febrero";
			break;
		case 2:
			mes = "Marzo";
			break;
		case 3:
			mes = "Abril";
			break;
		case 4:
			mes = "Mayo";
			break;
		case 5:
			mes = "Junio";
			break;
		case 6:
			mes = "Julio";
			break;
		case 7:
			mes = "Agosto";
			break;
		case 8:
			mes = "Septiembre";
			break;
		case 9:
			mes = "Octubre";
			break;
		case 10:
			mes = "Noviembre";
			break;
		case 11:
			mes = "Diciembre";
			break;
		default:
			mes = "n/d";
			break;
		}

		String fecha = cal.get(Calendar.DAY_OF_MONTH) + " de " + mes + " de " + cal.get(Calendar.YEAR);
		return fecha;
	}

	public static int getMonth(SFParams sFParams, String date) throws SFException {
		Calendar cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), date);
		int month = cal.get(Calendar.MONTH) + 1;
		return month;
	}

	public static String getMonthName(SFParams sFParams, String date) throws SFException {
		Calendar cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), date);
		int month = cal.get(Calendar.MONTH);
		String monthName = "";
		switch (month) {
		case 0:
			monthName = "Enero";
			break;
		case 1:
			monthName = "Febrero";
			break;
		case 2:
			monthName = "Marzo";
			break;
		case 3:
			monthName = "Abril";
			break;
		case 4:
			monthName = "Mayo";
			break;
		case 5:
			monthName = "Junio";
			break;
		case 6:
			monthName = "Julio";
			break;
		case 7:
			monthName = "Agosto";
			break;
		case 8:
			monthName = "Septiembre";
			break;
		case 9:
			monthName = "Octubre";
			break;
		case 10:
			monthName = "Noviembre";
			break;
		case 11:
			monthName = "Diciembre";
			break;
		default:
			monthName = "n/d";
			break;
		}

		return monthName;
	}

	public static int getYear(SFParams sFParams, String date) throws SFException {
		Calendar cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), date);
		int year = cal.get(Calendar.YEAR);
		return year;
	}

	public static int daysDiff(SFParams sFParams, String iDate, String eDate) throws SFException {
		Calendar i = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), iDate);
		Calendar e = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), eDate);
		int diff = e.get(Calendar.DAY_OF_YEAR) - i.get(Calendar.DAY_OF_YEAR);
		diff += (e.get(Calendar.YEAR) - i.get(Calendar.YEAR)) * 365;
		return diff;
	}

	public static int getWorkingDaysDiff(SFParams sFParams, String startDate, String endDate) throws SFException {
		Calendar startD =  SFServerUtil.stringToCalendar(sFParams.getDateFormat(), startDate);		
		Calendar endD = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), endDate);
		int dayDiff = 0;
		while (startD.before(endD)
				|| startD.get(Calendar.DAY_OF_YEAR) == endD
				.get(Calendar.DAY_OF_YEAR)) {
			if (startD.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& startD.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				dayDiff += 1;
			}
			startD.add(Calendar.DATE, 1);
		}
		return dayDiff;
	}

	public static String calendarToString(SFParams sFParams, Calendar cal) {
		SimpleDateFormat df = new SimpleDateFormat(sFParams.getDateFormat());		
		Date d = new Date(cal.getTime().getTime());
		return df.format(d);
	}

	public static String getFirstDateMonth(SFParams sFParams, String date) throws SFException {
		Calendar cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), date);
		int month = cal.get(Calendar.MONTH) + 1;
		int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
		String firstDate = cal.get(Calendar.YEAR) + "-" + month + "-" + firstDay;
		return firstDate;
	}

	public static String getLastDateMonth(SFParams sFParams, String date) throws SFException {
		Calendar cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), date);
		int month = cal.get(Calendar.MONTH) + 1;
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		String lastDate = cal.get(Calendar.YEAR) + "-" + month + "-" + lastDay;
		return lastDate;
	}

	// Redondea un valor que es moneda
	public static double roundCurrency(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal("" + value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static double roundDouble(double d, int decimals)  {
		//El NumbreFormat se obtiene formatos espec�ficos de la localidad para n�meros, monedas y porcentajes.
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMaximumFractionDigits(decimals);
		nf.setMinimumFractionDigits(decimals);
		String temp = nf.format(d).replace(",","");
		return stringToDouble(temp);
	}

	public static double roundDouble(double d) {
		d = d * 100;
		// La funcion Math.round realiza el redondeo 
		d = Math.round(d);
		return d / 100;
	}

	public static double stringToDouble(String str) {
		try {
			/*
			 * Se utiliza el metodo parseDouble para covertir la cadena a double
			 */	
			return Double.parseDouble(str);
		} catch (Exception e) {
			return 0;
		}
	}

	public static String formatDouble(double d, int decimals)  {
		//El NumbreFormat se obtiene formatos especeficos de la localidad para nemeros, monedas y porcentajes.
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMaximumFractionDigits(decimals);
		nf.setMinimumFractionDigits(decimals);
		return nf.format(d);
	}

	public static String codeFormatDigits(int id, int zeros, String codeFormat) {
		String s = "" + id;
		int nowId = s.length();
		for (int i = nowId; i < zeros; i++) {
			if (s.length() < zeros)
				s = "0" + s;
		}
		return codeFormat + s;
	}

	public static String getFiscalPeriod(String dateFormatString, int startMonthFiscal, String periodType, String date)  throws SFException {
		String fiscalPeriod = "";
		int periodTypeNumber = 0;

		if (periodType.equals("" + BmoCompany.PERIODTYPE_BIMESTER))
			periodTypeNumber = 2;
		else if (periodType.equals("" + BmoCompany.PERIODTYPE_TRIMESTER))
			periodTypeNumber = 3;
		//		else if (periodType.equals("" + BmoCompany.PERIODTYPE_QUATRIMESTER))
		//			periodTypeNumber = 4;
		else if (periodType.equals("" + BmoCompany.PERIODTYPE_SEMESTER))
			periodTypeNumber = 6;
		else 
			periodTypeNumber = 2;

		// Convertir fecha a tipo calendario
		Calendar cal = SFServerUtil.stringToCalendar(dateFormatString, date);

		//		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;

		// Estamos pasando el mes fiscal a como si fuera el primer mes del año, que realmente lo es para el año fiscal
		// Al mes de la fecha(dato a pasar) restar mes inicial fiscal de la empresa y sumar uno(para empezar ene=1 a dic=12)
		// ej: fecha(5(may)) - mesFiscal(5) = 0 + 1 : Entonces mayo = "enero"
		month = (month - startMonthFiscal) + 1;

		// Si da negativo, sumar 12 meses
		// ej: ene(1) - 5 = -4  : -4 + 12 = 8("ago")
		if (month <= 0) month = month + 12;
		//System.out.println("mes_calculado: " + month);

		// Sacar periodo 
		double period =  Math.ceil( (Double.parseDouble("" + month) / periodTypeNumber) );
		//System.out.println("PeridoFiscal: " + period + " int: "+(int)period);

		fiscalPeriod = periodType + "" + ((int)period);

		return fiscalPeriod;
	}

	public static int getFiscalYear(String dateFormatString, int startMonthFiscal,  String date)  throws SFException {
		int fiscalYear = 0;
		//		System.out.println("startMonthFiscal:" + startMonthFiscal + " - date: " + date + " - dateFormatString:" + dateFormatString);
		Calendar cal = SFServerUtil.stringToCalendar(dateFormatString, date);

		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		//		System.out.println("year: "+year + " - month: "+month);

		// si el año fiscal NO inicia en el mes 1, entonces el año fiscal ya es año actual(año f.cierre) + 1
		if (startMonthFiscal == 1) {
			fiscalYear = year;
		} else {
			// Sumar anio si el mes sobrepasa el mes de inicio fiscal, sino se queda el mismo
			if (month >= startMonthFiscal) 
				fiscalYear = year + 1;
			else fiscalYear = year;
		}
		//		System.out.println("fiscalYear: "+fiscalYear);
		return fiscalYear;
	}

	//Filtros para tags
	public static String parseTagsFiltersToSql(String fieldName, String filters) {
		String sqlFilter = " ";		

		filters = filters.trim();

		if (filters.indexOf(SFParams.TAG_SEPARATOR) > 0) {

			StringTokenizer st = new StringTokenizer(filters, SFParams.TAG_SEPARATOR);

			if (st.countTokens() > 0) {
				sqlFilter = " AND ( ";

				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					token = token.trim();

					sqlFilter += "" + fieldName + " LIKE '%:" + token + ":%' "; 


					if (st.hasMoreTokens())
						sqlFilter += " OR ";
				}

				sqlFilter += ") ";
			}
		} else {


			sqlFilter = " AND " + fieldName + " LIKE '%:" + filters + ":%' "; 

		}

		return sqlFilter;
	}
	//Quitar acentos y mas
	private static final String ORIGINAL
	= "ÁáÉéÍíÓóÚúÑñÜü";
	private static final String REPLACEMENT
	= "AaEeIiOoUuNnUu";
	public static String stripAccents(String str) {
		if (str == null) {
			return null;
		}
		char[] array = str.toCharArray();
		for (int index = 0; index < array.length; index++) {
			int pos = ORIGINAL.indexOf(array[index]);
			if (pos > -1) {
				array[index] = REPLACEMENT.charAt(pos);
			}
		}
		return new String(array);
	}

	// Obtener tags y comprobar que exista
	public static String getTags(SFParams sFParams, BmField bmField) {
		String tagListHtml = "";
		if (!bmField.toString().equals("")) {
			String[] split = bmField.toString().split("\\" + SFParams.TAG_SEPARATOR);

			for (int i = 0; i < split.length; i++) {
				String tagId = split[i];
				if (!tagId.equals("")) {
					BmoTag bmoTag = sFParams.getBmoTag(tagId);
					if (bmoTag != null) {
						tagListHtml += "<span class=\"" + bmoTag.getBmoUiColor().getCss().toString() + "\">" + bmoTag.getCode().toString() + "</span>";
					} 
				}
			}
		}
		return tagListHtml;
	}

	// Conversion de tiempo
	public static double convertTimeUnit(double time, TimeUnit from, TimeUnit to) {
		// Es la misma unidad, no hay conversion
		if (from.ordinal() == to.ordinal()) {
			return time;
		} else {
			// Es origen(from) o destino(to) la unidad mas grande?
			if (from.ordinal() < to.ordinal())  // origen(from) es mas pequeño
				return time / from.convert(1, to);
			else 
				return time * to.convert(1, from);
		}
	}

	// regresa rango de fechas string (5 a 6 de agosto de 2020,5 de julio a 6 de agosto de 2020 ó 5 de julio de 2019 a 6 de agosto de 2020)
	public static String parseRangeDateTimeToString(SFParams sfParams,String startDate, String endDate) throws SFException {
		//Variable para resultado
		String dateResult = "";
		//Pasar fechas string a Date
		Date start = SFServerUtil.stringToDate(sfParams.getDateFormat(), startDate);
		Date end = SFServerUtil.stringToDate(sfParams.getDateFormat(), endDate);

		Calendar calStart = Calendar.getInstance(TimeZone.getTimeZone(sfParams.getTimeZone()));
		//Asignar fecha inicio
		calStart.setTime(start);		
		Calendar calEnd = Calendar.getInstance(TimeZone.getTimeZone(sfParams.getTimeZone()));
		//Asignar fecha fin
		calEnd.setTime(end);		
		//Agrgar dia inicio al resultado
		dateResult += calStart.get(Calendar.DAY_OF_MONTH);
		//si es de año diferente agrega nombre de mes y año
		if (calStart.get(Calendar.YEAR) != calEnd.get(Calendar.YEAR)) {

			dateResult += " de " + getMonthName(sfParams, startDate);
			dateResult += " de " + calStart.get(Calendar.YEAR);

		} else {
			//si el mes diferente agrega el nombre del mes
			if (calStart.get(Calendar.MONTH) != calEnd.get(Calendar.MONTH)) {
				dateResult += " de " + getMonthName(sfParams, endDate);
			}
		}
		//Agregar fecha fin
		dateResult += " al " + calEnd.get(Calendar.DAY_OF_MONTH) + " de " + getMonthName(sfParams,endDate) + " de " + calEnd.get(Calendar.YEAR);

		return dateResult;
	}
}
