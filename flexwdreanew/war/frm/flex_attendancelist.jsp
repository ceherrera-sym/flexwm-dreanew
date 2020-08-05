<%@page import="com.flexwm.server.cv.PmCourseProgram"%>
<%@page import="com.flexwm.shared.cv.BmoCourseProgram"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="com.flexwm.shared.BmoFlexConfig"%>
<%@page import="com.flexwm.server.cv.PmTrainingSession"%>
<%@page import="com.flexwm.shared.cv.BmoTrainingSession"%>
<%@page import="com.flexwm.server.cv.PmUserSession"%>
<%@page import="com.flexwm.shared.cv.BmoUserSession"%>
<%@page import="com.flexwm.server.cv.PmCourseProgram"%>
<%@page import="com.flexwm.shared.cv.BmoCourseProgram"%>
<%@page import="com.flexwm.server.cv.PmCourse"%>
<%@page import="com.flexwm.shared.cv.BmoCourse"%>
<%@page import="com.symgae.client.ui.UiParams"%>

<%@include file="../inc/login_opt.jsp" %>

<%
try {
	String title = "Lista de Asistencia y Evaluaci&oacute;n";
	
	BmoTrainingSession bmoTrainingSession = new BmoTrainingSession();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoTrainingSession.getProgramCode());	
%>

<html>
		<% 
		// Imprimir
		String print = "0", permissionPrint = "";
		if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
		
		// Exportar a Excel
		String exportExcel = "0";
		if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
		if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
		}
		
		//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
		//En caso de que mande a imprimir, deshabilita contenido
		if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
		<style> 
			body{
				user-select: none;
				-moz-user-select: none; 
				-o-user-select: none; 
				-webkit-user-select: none; 
				-ie-user-select: none; 
				-khtml-user-select:none; 
				-ms-user-select:none; 
				-webkit-touch-callout:none
			}
		</style>
		<style type="text/css" media="print">
		    * { display: none; }
		</style>
		<%
		permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
			//Mensaje 
			if(print.equals("1") || exportExcel.equals("1")) { %>
				<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
			<% }
		}
		 
		%>
			<head>
			<title>:::<%= title %>:::</title>
			<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
			 <link rel="stylesheet" type="text/css" href="../css/flexwm.css"> 
			</head>
	<body class="default" <%= permissionPrint %>>
		<%
	
		NumberFormat formatCurrency = NumberFormat.getCurrencyInstance(Locale.US);
		
	    // Obtener parametros
	    int trainingSessionId = 0;
	    if (isExternal) trainingSessionId = trainingSessionId;
	    else trainingSessionId = Integer.parseInt(request.getParameter("foreignId"));  
	    	
		PmTrainingSession  pmTrainingSession = new PmTrainingSession(sFParams);
		bmoTrainingSession = (BmoTrainingSession)pmTrainingSession.get(trainingSessionId);
		
		BmoCourseProgram BmoCourseProgram = new BmoCourseProgram();
		PmCourseProgram pmCourseProgram = new PmCourseProgram(sFParams);
		if(bmoTrainingSession.getBmoCourse().getProgramId().toInteger() > 0 )
			bmoProgram = (BmoProgram)pmProgram.get(bmoTrainingSession.getBmoCourse().getProgramId().toInteger());
		
		String customer = "";
		
		
		%>		
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td align="left" rowspan="10" valign="top">
					<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= sFParams.getMainImageUrl() %>" >
				</td>
				<td colspan="4"  class="reportTitleCell">
					&nbsp;SECCI&Oacute;N 06.2 - <%= title%>
				</td>
			</tr>
			<tr>
				<td colspan="4">
					<div class="contractTitle">&nbsp;&nbsp;<b>A. Datos del Curso<b></div>
				</td>
			</tr>
			<tr>
				<th class="reportCellEven" align = "left">
					Programa: 
				</th>
				<td class="reportCellEven">
					<%= bmoProgram.getName().toString()%>
				</td>
				<th class="reportCellEven" align = "left">
					Fecha: 
				</th>
				<td class="reportCellEven">
					<%= bmoTrainingSession.getDate().toHtml()%>
				</td>
			</tr>
				<th class="reportCellEven" align = "left">
					Curso: 
				</th>
				<td class="reportCellEven">
					<%= bmoTrainingSession.getBmoCourse().getName().toHtml()%>
				</td>
				<th class="reportCellEven" align = "left">
					Hora Inicio: 
				</th>
				<td class="reportCellEven">
					<%= bmoTrainingSession.getTime().toHtml()%>
				</td>
			</tr>
			<tr>
				<th class="reportCellEven" align = "left">
					Expositor: 
				</th>
				<td class="reportCellEven">
					<%= bmoTrainingSession.getBmoCourse().getInstructor().toHtml() %> 
				</td>
				<th class="reportCellEven" align = "left">
					Hora Conclusi&oacute;n:
				</th>
				<td class="reportCellEven">
					<%= bmoTrainingSession.getTimeEnd().toHtml() %>
				</td>
			</tr>
			<tr>
				<th class="reportCellEven" align = "left">
					Elaborado: 
				</th>
				<td class="reportCellEven">
					<%= SFServerUtil.nowToString(sFParams, "yyyy/MM/dd") %>
				</td>
				<th class="reportCellEven" align = "left">
					&nbsp; 
				</th>
				<td class="reportCellEven">
					&nbsp;
				</td>
			</tr>
		</table>
		<br>
		<div class="documentTitleCaption">
			<span style = "font-weight: bold;">
				&nbsp;Instrucciones Evaluar la efectividad del curso de acuerdo a la siguiente escala:<br>
				&nbsp;a = Sin Efectividad &nbsp;&nbsp;b = Conocimiento sin aplicaci&oacute;n al trabajo &nbsp;&nbsp;c = Aplicaci&oacute;n moderada de nuevos conocimientos<br>
				&nbsp;d = Buena Aplicaci&oacute;n de conocimientos&nbsp;&nbsp;e = Mejora de los procesos por aplicaci&oacute;n de nuevos conocimientos<br>
				&nbsp;Nota: la evaluaci&oacute;n se debe hacer de 5 a 30 d&iacute;as despu&eacute;s de con concluir el curso.
			</span>
			<br>
		</div>
		<br>
		<div class="contractTitle"><b>B. Asistentes<b></div>
		<table border="1" cellspacing="0" width="100%" cellpadding="0" style="font-size: 11px">
			<tr>
				<td class="reportHeaderCellCenter" colspan="3">
					Asistentes
				</td>
				<td class="reportHeaderCellCenter" colspan="3">
					Eval. Aprob. Intructor
				</td>
				<td class="reportHeaderCellCenter" colspan="8">
					Evaluaci&oacute;n de la efectividad del curso
				</td>
			</tr>
			<tr>
				<th class="reportHeaderCell" width="1%">
					#
				</th>
				<th class="reportHeaderCell">
					Nombre
				</th>
				<th class="reportHeaderCell">
					Firma Asistente
				</th>
				<th class="reportHeaderCell" width="1%">
					SI
				</th>
				<th class="reportHeaderCell" width="1%">
					NO
				</th>
				<th class="reportHeaderCell" width="10%">
					Firma
				</th>
				<th class="reportHeaderCell" width="8%">
					Realizo Evaluaci&oacute;n
				</th>
				<th class="reportHeaderCell" width="10%">
					Firma
				</th>
				<th class="reportHeaderCell">
					Fecha
				</th>
				<th class="reportHeaderCell" width="1%">
					a&nbsp;
				</th>
				<th class="reportHeaderCell" width="1%">
					b&nbsp;
				</th>
				<th class="reportHeaderCell" width="1%">
					c&nbsp;
				</th>
				<th class="reportHeaderCell" width="1%">
					d&nbsp;
				</th>
				<th class="reportHeaderCell" width="1%">
					e&nbsp;
				</th>
			</tr>
			<%
			int i = 1;
			BmoUserSession bmoUserSession = new BmoUserSession();
			PmUserSession pmUserSession = new PmUserSession(sFParams);
			BmFilter bmFilterUses = new BmFilter();
			bmFilterUses.setValueFilter(bmoUserSession.getKind(), bmoUserSession.getTrainingSessionId().getName(), bmoTrainingSession.getId());
			Iterator<BmObject> users = pmUserSession.list(bmFilterUses).iterator();
			while(users.hasNext()) {
				bmoUserSession = (BmoUserSession)users.next();	
				%>      
				<tr>	      
					<td class="reportCellEven"> 
						<%= i %>
					</td>
					<td class="reportCellEven" > 
						<%= bmoUserSession.getBmoUser().getFirstname().toHtml() %>
						<%= bmoUserSession.getBmoUser().getFatherlastname().toHtml() %>
						<%= bmoUserSession.getBmoUser().getMotherlastname().toHtml() %>
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						&nbsp;
					</td>
					<td class="reportCellEven"> 
						a
					</td>
					<td class="reportCellEven"> 
						b
					</td>
					<td class="reportCellEven"> 
						c
					</td>
					<td class="reportCellEven"> 
						d
					</td>
					<td class="reportCellEven"> 
						e
					</td>
				<tr>
			<%	
				i++; 
			}
			%>	
		</table>
		<div class="documentTitleCaption">
			<span style = "font-weight: bold; font-size: 12px">
				&nbsp;Intrucciones Cuando la efectividad de la capacitaci&oacute;n sea evaluada con "a", "b" o "c", deber&aacute; indicar las acciones<br>
   				&nbsp;generadas para mejorar efectividad de la capacitaci&oacute;n.<br>
			</span>
			<br>
		</div>
		
		<div class="contractTitle"><b>A. Conclusiones / Acciones<b></div>
		<table class="documentComments"  border="1" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
			<tr>
				<td width="90%" align="right">
					Fecha de Compromiso:
				</td>
				<td width="10%">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td width="90%" align="right">
					Fecha de Compromiso:
				</td>
				<td width="10%">
					&nbsp;
				</td>
			</tr>
			<tr>
				<td width="90%" align="right">
					Fecha de Compromiso:
				</td>
				<td width="10%">
					&nbsp;
				</td>
			</tr>
			<tr>
		</tr>
		</table>
		<br>
		<div class="documentComments" align="right">C&oacute;digo del Documento: FO-06.2.1.3-2 (15-Jul-05)</div>

			<%
		} catch (Exception e) { 
				String errorLabel = "Error de Formato";
				String errorText = "La lista  de Asistencia no puede ser desplegada.";
				String errorException = e.toString();		
		%>
			<%= errorException %>
		<%
			}
		%>
	</body>
</html>