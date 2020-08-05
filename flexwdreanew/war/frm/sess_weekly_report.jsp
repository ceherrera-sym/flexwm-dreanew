<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */ -->
<%@page import="com.flexwm.server.ac.*"%>
<%@page import="com.flexwm.shared.ac.*"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.server.*"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@include file="../inc/login_opt.jsp" %>
<%@page import="com.symgae.shared.SQLUtil" %>

<% 

	try {

	// Inicializar variables
 	String title = "Sesiones General";
	
   	String sql = "", where = "", whereDate = "", whereDateDemo = "", whereCustDate = "", start = "";
   	String filters = "";
   	int programId = 0;
   	int rows = 0;   	
   	
   	PmSession pmSession = new PmSession(sFParams);
   	BmoSession bmoSession = new BmoSession();
   	String startDate = "", endDate = "";
   	int userId = 0, sessionTypeId = 0;
   	
   
   	// Obtener parametros  
   	if (request.getParameter("programId") != null) programId = Integer.parseInt(request.getParameter("programId"));
	if (request.getParameter("startdate") != null) startDate = request.getParameter("startdate");
	if (request.getParameter("enddate") != null) endDate = request.getParameter("enddate");
	
	if (startDate.equals("")) {
		endDate = SFServerUtil.nowToString(sFParams,sFParams.getDateFormat());
		//Obtener la semana pasada
		//endDate = "2017-01-15";
		Calendar cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), endDate);
		cal.add(Calendar.DAY_OF_YEAR, -5);
		startDate = FlexUtil.calendarToString(sFParams, cal);
	}
	
	if (endDate.equals("")) {
		endDate = startDate;
	}
	
	// Construir filtros 	
   	if (userId > 0) {
		where += " AND sess_userid = " + userId;
   		filters += "<i>Instructor: </i>" + request.getParameter("sess_useridLabel") + ", ";
   	}
   	
   	if (sessionTypeId > 0) {
		where += " AND sess_sessiontypeid = " + sessionTypeId;
   		filters += "<i>Tipo Sesi&oacute;n: </i>" + request.getParameter("sess_sessiontypeidLabel") + ", ";
   	}
   	
   	
   	if (!endDate.equals("")) {
		whereDate += " AND sess_startdate >= '" + startDate + " 00:00' ";
		whereDateDemo += " AND sesa_startdate >= '" + startDate + " 00:00' ";
		whereCustDate += " AND cust_datecreate >= '" + startDate + " 00:00' ";
   		filters += "<i>Fecha Inicio: </i>" + startDate + ", ";
   	}
   	
    if (!endDate.equals("")) {
		whereDate += " AND sess_startdate <= '" + endDate + " 23:59' ";
		whereDateDemo += " AND sesa_startdate <= '" + endDate + " 23:59' ";
		whereCustDate += " AND cust_datecreate <= '" + endDate + " 23:59' ";
   		filters += "<i>Fecha Fin: </i>" + endDate + ", ";
   	}
    
    PmConn pmConn = new PmConn(sFParams);
	pmConn.open();
	
	PmConn pmConn2 = new PmConn(sFParams);
	pmConn2.open();
   	
	
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoSession.getProgramCode());

	NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
	
	title = "Reporte Semanal";
%>	

<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 8px">
	<tr>
		<td colspan="" align="" class="" style="font-size: 18px">
		    <b><%= title %></b>
		</td>
	</tr>
	<tr>
		<td class="reportSubTitle" colspan="" align="">
			<b>Periodo:</b><%= filters %>
			
		</td>
		<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>		
</table>
<br><br>
<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 14px">
	<tr>		
		<td class="" align ="left"  colspan="7">
			<b><%= "Listado Informes" %></b>
		</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 8px">
	<tr class="" bgcolor="#BDBDBD">
		<th class="">#</th>
		<th class="">Cliente</th>
		<th class="">Email</th>
		<th class="">Telef&oacute;no</th>
		<th class="">Como se Entero</th>
		<th class="">Fecha</th>
		<th class="">Observaci&oacute;n</th>					
	</tr>		
<%

	sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customers ") +  	      
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customeremails")+" ON (cuem_customerid = cust_customerid) " +
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customerphones")+" ON (cuph_customerid = cust_customerid) " +
	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +  		  	
		  " LEFT JOIN " + SQLUtil.formatKind(sFParams, " industries")+" ON (indu_industryid = cust_industryid) " +
		  " WHERE cust_customerid > 0 " + whereCustDate + 
		  " ORDER BY cust_customerid ASC";	
	int i = 1;	
	pmConn.doFetch(sql);	
	while (pmConn.next()) { 
%>
		<tr>
			
			<td class="" align ="center" style="border-bottom:1pt solid black;" align ="">
				<%= i %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;">
				<%= pmConn.getString("customers","cust_displayname") %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("customeremails","cuem_email") %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("customerphones","cuph_number") %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("referrals","refe_name") %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("customers","cust_datecreate").substring(0,10) %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("customers","cust_referralcomments") %>
			</td>
			
		</tr>
<%	
	i++;
	}	
%>	
	
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 14px">
	<tr>		
		<td class="" align="left" colspan="11">
			<b><%= "Clases de Prueba" %></b>
		</td>
	</tr>
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0"  style="font-size: 8px">
	<tr class="" bgcolor="#BDBDBD">
		<th class="">#</th>
		<th class="">Venta</th>		
		<th class="">Cliente</th>
		<th class="">Fecha</th>
		<th class="">Edad</th>
		<th class="">Tutor</th>
		<th class="">Telefono</th>
		<th class="">Maestro</th>
		<th class="">Atendido Por</th>
		<th class="">Como se entero</th>
		<th class="">Observaci&oacute;n</th>
	</tr>
	<%

		sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
	 	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (sesa_customerid = cust_customerid) " +
	 	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +
	 	      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " users")+" ON (sesa_salesuserid = user_userid) " +		 
	 	      " WHERE sesa_sessiondemo = 1 " + whereDateDemo +
	 	      " ORDER BY sesa_sessionsaleid ";
			 	        	
		pmConn.doFetch(sql);

	i = 1;
	start = "";
	pmConn.doFetch(sql);	
	while (pmConn.next()) {		
%>	
		<tr>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= i %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("sessionsales","sesa_code") %>
			</td>
			<td class=""  style="border-bottom:1pt solid black;" align ="left">
				<%= pmConn.getString("customers","cust_displayname") %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("sessionsales","sesa_startdate").substring(0,10) %>
			</td>			
			<%
				int age = 0;
				//Calcular edad
				if (!pmConn.getString("cust_birthdate").equals(""))
					age = SFServerUtil.daysBetween(sFParams.getDateFormat(), pmConn.getString("cust_birthdate"), SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()));
					age = age/365;
			%>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= age %>
			</td>			
			<%
				//Obtener el Padre o Tutor
				String relative = "";
				String phones = "";
				sql  = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerrelatives ") +
				       " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
			           " AND curl_responsible = 1 ";
			    pmConn2.doFetch(sql);
			    if(pmConn2.next()) {
			    	relative = pmConn2.getString("customerrelatives", "curl_fullname") + " " + pmConn2.getString("customerrelatives", "curl_fatherlastname");
			        phones = pmConn2.getString("customerrelatives", "curl_number") + "/" +pmConn2.getString("customerrelatives", "curl_cellphone") ;
			    }      
			%>
			<td class="" align ="left" style="border-bottom:1pt solid black;" >
				<%= relative %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= phones %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("users","user_code") %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("referrals","refe_name") %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("sessionsales","sesa_description") %>
			</td>							
		</tr>
	<%  i++;	
	}  
	%>
</table>
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 14px">
	<tr>
		<td class="" align="left" colspan="17">
			<b><%= "Listado de Registro" %></b>
		</td>
	</tr>
</table>	
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 8px">	
	<tr class="" bgcolor="#BDBDBD">
		<th class="">#</th>
		<th class="">Cliente</th>
		<th class="">Inscripci&oacute;n</th>
		<th class="">Tipo Sesi&oacute;n</th>
		<th class="">Sesiones</th>
		<th class="">Horarios</th>
		<th class="">Nacimiento</th>
		<th class="">Edad</th>		
		<th class="">Padre</th>
		<th class="">Madre</th>
		<th class="">Celular</th>
		<th class="">Tel&eacute;fono</th>
		<th class="">Email</th>
		<th class="">Mesualidad</th>
		<th class="">Referencia</th>
		<th class="">Firmo</th>
		<th class="">Imagenes</th>		
	</tr>
<%

	sql = 	" SELECT * FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +   			
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " customers")+" ON (sesa_customerid = cust_customerid) " +
	        " LEFT JOIN " + SQLUtil.formatKind(sFParams, " referrals")+" ON (cust_referralid = refe_referralid) " +
			" LEFT JOIN " + SQLUtil.formatKind(sFParams, " sessiontypepackages")+" ON (sesa_sessiontypepackageid = setp_sessiontypepackageid) " + 
	        " WHERE sesa_status <> '" + BmoSessionSale.STATUS_CANCELLED + "'" + whereDateDemo +
	        " AND (sesa_sessiondemo <> 1 OR sesa_sessiondemo is null) " +   	          
	        " ORDER BY sesa_startdate";	
	pmConn.doFetch(sql);


	i = 1;
	start = "";
	pmConn.doFetch(sql);	
	while (pmConn.next()) {	
%>
		<tr>
			<td class="" align ="center" style="border-bottom:1pt solid black;">
				<%= i %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;">
				<%= pmConn.getString("customers","cust_displayname") %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("sessionsales","sesa_startdate").substring(0,10) %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("sessiontypepackages","setp_name") %>
			</td>
			<%
				Calendar cal = Calendar.getInstance();
				//Obtener los dias de las sessiones
				String sessionDays = "";
				String sessionTime = "";
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " ordersessions ") +
				      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " sessions")+" ON (orss_sessionid = sess_sessionid) " +
					  " WHERE orss_orderid = " + pmConn.getInt("sesa_orderid");
				pmConn2.doFetch(sql);
				while (pmConn2.next()) {
					cal = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), pmConn2.getString("sessions", "sess_startdate"));
					if (cal.get(cal.DAY_OF_WEEK) == 0) {
						sessionDays += "Dom,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 1) {
						sessionDays += "Lun,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 2) {
						sessionDays += "Mar,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 3) {
						sessionDays += "Mie,";					 
					} else if (cal.get(cal.DAY_OF_WEEK) == 4) {
						sessionDays += "Jue,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 5) {
						sessionDays += "Vie,";
					} else if (cal.get(cal.DAY_OF_WEEK) == 6) {
						sessionDays += "Sab,";
					}	
					
					sessionTime += pmConn2.getString("sessions", "sess_startdate").substring(10); 
				}		
			%>
			<td class="" align ="left" style="border-bottom:1pt solid black;" >
				<%= sessionDays %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;" >
				<%= sessionTime %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;" >
				<%= pmConn.getString("customers","cust_birthdate") %>
			</td>
			<%	
				//Calcular la edad
				int custAge = 0;
				if (!pmConn.getString("customers","cust_birthdate").equals(""))
					custAge = SFServerUtil.daysBetween(sFParams.getDateFormat(), pmConn.getString("customers","cust_birthdate") , SFServerUtil.nowToString(sFParams, sFParams.getDateFormat())) / 365; 
			
			%>
			<td class="" align ="center" style="border-bottom:1pt solid black;" >
				<%= custAge %>
			</td>			
			<%
				//Obtener el padre y Madre
				String fatherName = "";
				String motherName = "";
				
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerrelatives ") + 
				      " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
				      " AND curl_type = '" + BmoCustomerRelative.TYPE_FATHER + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) { 
					fatherName = pmConn2.getString("customerrelatives", "curl_fullName") + " " + pmConn2.getString("customerrelatives", "curl_fatherlastname"); 
				}
				
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerrelatives ") + 
				      " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
				      " AND curl_type = '" + BmoCustomerRelative.TYPE_MOTHER + "'";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) { 
					motherName = pmConn2.getString("customerrelatives", "curl_fullName") + " " + pmConn2.getString("customerrelatives", "curl_fatherlastname"); 
				}
				
				//Reponsible
				String cellPhone = "";
				String phone = "";
				String email = "";
				sql = " SELECT * FROM " + SQLUtil.formatKind(sFParams, " customerrelatives ") + 
					  " WHERE curl_customerid = " + pmConn.getInt("cust_customerid") +
					  " AND curl_responsible = 1 ";
				pmConn2.doFetch(sql);
				if (pmConn2.next()) { 
					cellPhone = pmConn2.getString("customerrelatives", "curl_cellphone");
					phone = pmConn2.getString("customerrelatives", "curl_number");
					email = pmConn2.getString("customerrelatives", "curl_email");
				}
			%>
			<td class="" align ="left" style="border-bottom:1pt solid black;">
				<%= fatherName %>
			</td>
			<td class="" align ="left" style="border-bottom:1pt solid black;">
				<%= motherName %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;">
				<%= cellPhone %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;">
				<%= phone %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;">
				<%= email %>
			</td>
			<td align="rigth" style="border-bottom:1pt solid black;">
				<%= formatCurrency.format(pmConn.getDouble("setp_saleprice")) %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;">
				<%= pmConn.getString("customers","cust_referralcomments") %>
			</td>
			<%
				String letter = "No", photo = "No";
				if (pmConn.getInt("sesa_signletter") > 0) letter = "Si";
				if (pmConn.getInt("sesa_takephoto") > 0) photo = "Si";
			
			%>
			<td class="" align ="center" style="border-bottom:1pt solid black;">
				<%= letter %>
			</td>
			<td class="" align ="center" style="border-bottom:1pt solid black;">
				<%= photo %>
			</td>
		</tr>
<%	
	i++;
	}	
%>
	
</table>  
<br>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 14px">
	<tr>
		<th class="" align="left">
			<%= " Resumen Mensual" %>
		</th>
	</tr>	
</table>
<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 8px">	
	<tr>
		<%
			int lastMonthSale = 0;
			int nowMonthSale = 0;
			//Obtener el mes anterior
			Calendar lastMont = SFServerUtil.stringToCalendar(sFParams.getDateFormat(), startDate);
			lastMont.add(lastMont.MONTH, -1);						
			String lastMonthDate  = FlexUtil.calendarToString(sFParams, lastMont);
			
			String lastMonthName = FlexUtil.getMonthName(sFParams, lastMonthDate);
			String nowMonthName = FlexUtil.getMonthName(sFParams, startDate);
			
			
			//Obtener las Fechas
			sql = " SELECT COUNT(sesa_sessionsaleid) AS lastMonthSale FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
			      " WHERE sesa_startdate >= '" + FlexUtil.getFirstDateMonth(sFParams, lastMonthDate) + "'" +
				  " AND sesa_startdate <= '" + FlexUtil.getLastDateMonth(sFParams, lastMonthDate) + "'";
			pmConn.doFetch(sql);
			if (pmConn.next()) lastMonthSale = pmConn.getInt("lastMonthSale");
			
			
			//Obtener las Fechas
			sql = " SELECT COUNT(sesa_sessionsaleid) AS nowMonthSale FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
			      " WHERE sesa_startdate >= '" + FlexUtil.getFirstDateMonth(sFParams, startDate) + "'" +
				  " AND sesa_startdate <= '" + FlexUtil.getLastDateMonth(sFParams, startDate) + "'";
			pmConn.doFetch(sql);
			if (pmConn.next()) nowMonthSale = pmConn.getInt("nowMonthSale");

			//Ventas Pagadas
			int sessionSale = 0;
			sql = " SELECT COUNT(sesa_sessionsaleid) AS sesionSale FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (sesa_orderid = orde_orderid) " +				  	
			      " WHERE (orde_status = '" + BmoOrder.STATUS_AUTHORIZED + "' OR orde_status = '" + BmoOrder.STATUS_REVISION + "')" +
			      " AND (sesa_status = '" + BmoSessionSale.STATUS_REVISION + "' OR sesa_status = '" + BmoSessionSale.STATUS_AUTHORIZED + "')" +
			      " AND sesa_startdate >= '" + FlexUtil.getFirstDateMonth(sFParams, startDate) + "'" +
				  " AND sesa_startdate <= '" + FlexUtil.getLastDateMonth(sFParams, startDate) + "'";
		    pmConn.doFetch(sql);
		    if (pmConn.next()) sessionSale = pmConn.getInt("sesionSale");
			
		
		%>
		<th align="left" bgcolor="#BDBDBD">Comparativo: <%= lastMonthName %>/<%= nowMonthName %></th>		
		<td class="" align ="center" style="border-bottom:1pt solid black;" >
			<%= (lastMonthSale - nowMonthSale) %>
		</td>
		<th align="left" bgcolor="#BDBDBD">Ventas Activas</th>
		<td class="" align ="center" style="border-bottom:1pt solid black;" >
			<%= sessionSale %>
		</td>		
	</tr>
	<tr class="">
		<%
			//ReIngreso
			int reEntry = 0;
			sql = " SELECT COUNT(sesa_customerid) AS reEntry FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
			      " WHERE sesa_customerid IN ( " +
						" SELECT sesa_sessionsaleid FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +						
						" WHERE sesa_startdate <= '" + FlexUtil.getLastDateMonth(sFParams, lastMonthDate) + "'" +   
				  " ) ";			
		    pmConn.doFetch(sql);
		    if (pmConn.next()) reEntry = pmConn.getInt("reEntry");	  
		%>
		<th align="left" bgcolor="#BDBDBD">ReIngreso:</th>
		<td class="" align ="center" style="border-bottom:1pt solid black;" >
			<%= reEntry %>
		</td>		
		<%
			//Nuevo Ingreso
			int newIncome = 0;
			sql = " SELECT COUNT(sesa_customerid) AS newIncome FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
			      " WHERE sesa_customerid NOT IN ( " +
						" SELECT sesa_sessionsaleid FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +						
						" WHERE sesa_startdate <= '" + FlexUtil.getLastDateMonth(sFParams, lastMonthDate) + "'" +   
				  " ) ";			
		    pmConn.doFetch(sql);
		    if (pmConn.next()) newIncome = pmConn.getInt("newIncome");	  
		%>
		<th align="left" bgcolor="#BDBDBD">Nuevo Ingreso</th>
		<td class="" align ="center" style="border-bottom:1pt solid black;" >
			<%= newIncome %>
		</td>				
	</tr>	
	<tr class="">
		<%	//Ventas Pagadas
			int ordePayment = 0;
			sql = " SELECT COUNT(sesa_sessionsaleid) AS ordePayment FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (sesa_orderid = orde_orderid) " +				  	
			      " WHERE orde_paymentstatus = '" + BmoOrder.PAYMENTSTATUS_TOTAL + "'" +
			      " AND sesa_startdate >= '" + FlexUtil.getFirstDateMonth(sFParams, startDate) + "'" +
				  " AND sesa_startdate <= '" + FlexUtil.getLastDateMonth(sFParams, startDate) + "'";
		    pmConn.doFetch(sql);
		    if (pmConn.next()) ordePayment = pmConn.getInt("ordePayment");
		    
		    //Ventas No Pagadas
			int ordeNotPayment = 0;
			sql = " SELECT COUNT(sesa_sessionsaleid) AS ordePayment FROM " + SQLUtil.formatKind(sFParams, " sessionsales ") +
			      " LEFT JOIN " + SQLUtil.formatKind(sFParams, " orders")+" ON (sesa_orderid = orde_orderid) " +				  	
			      " WHERE orde_paymentstatus = '" + BmoOrder.PAYMENTSTATUS_PENDING + "' " +
			      " AND sesa_startdate >= '" + FlexUtil.getFirstDateMonth(sFParams, startDate) + "'" +
				  " AND sesa_startdate <= '" + FlexUtil.getLastDateMonth(sFParams, startDate) + "'";
		    pmConn.doFetch(sql);
		    if (pmConn.next()) ordeNotPayment = pmConn.getInt("ordePayment");		    
%>
		<th align="left" bgcolor="#BDBDBD">Venta Pagadas:</th>
		<td class="" align ="center" style="border-bottom:1pt solid black;" >
			<%= ordePayment %>
		</td>		
		<th align="left" bgcolor="#BDBDBD">Venta No Pagadas:</th>
		<td class="" align ="center" style="border-bottom:1pt solid black;" >
			<%= ordeNotPayment %>
		</td>		
	</tr>
</table>
<%	
	pmConn2.open();
	pmConn.close();
	} catch (Exception e) {		
%>	
		<%= e.toString() %>	
<%		
	}
%>
 
 