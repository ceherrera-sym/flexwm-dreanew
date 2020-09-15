
<%@include file="../inc/imports.jsp" %>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.fi.*"%>
<%@page import="com.flexwm.shared.fi.*"%>
<%@page import="com.flexwm.server.cr.*"%>
<%@page import="com.flexwm.shared.cr.*"%>
<%@page import="com.symgae.shared.SFException"%>
<%@page import="com.symgae.shared.BmUpdateResult"%>
<%@include file="../inc/login.jsp" %>

<%
	String programTitle = "Asignar/Crear Fallas de los Creditos de una fecha determinada";
	String programDescription = programTitle;
	
%>

<html>
<head>
<title>:::<%= programTitle %>:::</title>
<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>">
<link rel="stylesheet" type="text/css" href="../css/flexwm.css">  
<meta charset=utf-8>
</head>
<body class="default">
<table border="0" style="font-size: 12px: width:100%">
	<tr>
		<td align="left" width="" rowspan="5" valign="top">
		    <img border="0" height="" src="<%= sFParams.getMainImageUrl() %>" >
		</td>
		<td colspan="" class="reportTitle">
		    &nbsp;<%= programTitle %>
		</td>
	</tr>
</table>

<%
	String sql = "", where = "" , nowDate = "", typeCredit ="", typeCreditText ="";
	if (request.getParameter("fecha") != null) nowDate = request.getParameter("fecha");
	if (request.getParameter("tipoCredito") != null) typeCredit = request.getParameter("tipoCredito");
	
	if (typeCredit.equalsIgnoreCase("" + BmoCreditType.TYPE_DAILY))
		typeCreditText = "Diario";
	else if (typeCredit.equalsIgnoreCase("" + BmoCreditType.TYPE_WEEKLY))
		typeCreditText = "Semanal";
	else typeCreditText = "Indefinido";
	PmRaccount pmRaccount = new PmRaccount(sFParams);
	%>
		<div>Proceso manual de fallas, este proceso busca las CxC que no se hayan pagado en el dia asginado, dependiendo del Tipo de credito</div>
		<div>Fecha de cxc: <b><%= nowDate%></b> y Tipo de credito: <b><%= typeCreditText%></b></div>
		<div>*En la URL La fecha debe ser en formato: "a&ntilde;o-mes-d&iacute;a"</div>
		<div>*En la URL El Tipo de credito: Diario=D, Semanal=W</div>

	<% 
	try {		
		if (!nowDate.equalsIgnoreCase("") && !typeCreditText.equalsIgnoreCase("Indefinido")) {
			System.out.println("Proceso manual de fallas, fecha de cxc:" + nowDate + ", TipoCredito:" + typeCredit);
			if (typeCredit.equalsIgnoreCase("" + BmoCreditType.TYPE_DAILY))
				pmRaccount.checkFailureDailyDaCredito(nowDate);
			else if (typeCredit.equalsIgnoreCase("" + BmoCreditType.TYPE_WEEKLY))
				pmRaccount.checkFailureWeeklyDaCredito(nowDate);
		}
	} catch (Exception e) {
		throw new SFException(e.toString());
	} finally {
	// 	pmConn2.close();
	// 	pmConn.close();
	}	
	

%>

</body>
</html>


