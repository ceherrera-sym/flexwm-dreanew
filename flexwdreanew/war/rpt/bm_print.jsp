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
 
<%@page import="com.symgae.shared.GwtUtil"%>
<%@include file="../inc/login.jsp" %>

<%

	// Obtener parametros
	String program = (String)request.getParameter("program");
	String programLabel = (String)request.getParameter("programLabel");
	
	ArrayList<BmFilter> filterList = SFServerUtil.parseFilterList(request);
	ArrayList<BmSearchField> searchList = SFServerUtil.parseSearchList(request); 
	String searchText = (String)request.getParameter("searchtext");
	
	ArrayList<BmOrder> orderList = SFServerUtil.parseOrderList(request);
	
	// Exportar a Excel
	String exportExcel = "0";
	if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
	if (exportExcel.equals("1")) {
		response.setContentType("application/vnd.ms-excel");
    	response.setHeader("Content-Disposition", "inline; filename=symgf_report.xls");
    }
 %>

<html>
<head>
	<title>:::<%= appTitle %>:::</title>
	<link rel="stylesheet" type="text/css" href="../css/<%= defaultCss %>"> 
	<link type="text/css" rel="stylesheet" href="../css/tags.jsp">
</head>

<body class="default">

<table width="100%">
	<tr>
		<td align="left" width="60" rowspan="2" valign="top">	
			<img src="<%= sFParams.getMainImageUrl() %>" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>">
		</td>
		<td class="reportTitle" align="left">
			<%= programLabel %>
		</td>
		<td align="right">
		<% if (!exportExcel.equals("1")) { %>
			<!--
			<a href="<%= request.getRequestURL().toString() + "?" + request.getQueryString() + "&exportexcel=1" %>"><input type="image" id="exportExcel" src="<%= GwtUtil.getProperUrl(sFParams, "/icons/xls.png")%>" name="exportExcel" title="Exportar a Excel"></a>
			-->
			<input type="image" id="printImage" src="<%= GwtUtil.getProperUrl(sFParams, "/icons/print.png")%>" name="image" onclick="doPrint()" title="Imprimir">
		<% } %>
		</td>
	</tr>
	<tr>
		<td class="reportSearchTitle">

<% if (filterList.size() > 0) { %><b>Filtros:</b> <% } %> 
<%
	Iterator<BmFilter> filterIterator = filterList.iterator();
	while (filterIterator.hasNext()) {
		BmFilter bmFilter = (BmFilter)filterIterator.next();
		if (bmFilter.getFilterType() == BmFilter.RANGE) {
		%>
			<%= bmFilter.getFieldLabel() %> entre "<%= bmFilter.getMinValue() %>" y "<%= bmFilter.getMaxValue() %>", 
		<%		
		} else if (bmFilter.getFilterType() == BmFilter.CONTAINS) {
		%>
			<%= bmFilter.getFieldLabel() %> contiene "<%= bmFilter.getValueLabel() %>", 
		<%
		} else if (bmFilter.getFilterType() == BmFilter.NOTCONTAINS) {
		%>
			<%= bmFilter.getFieldLabel() %> NO contiene "<%= bmFilter.getValueLabel() %>", 
		<%
		} else {
		%>
			<%= bmFilter.getFieldLabel() %> <%= bmFilter.getOperator() %> "<%= bmFilter.getValueLabel() %>", 
		<%
		}
	}
%>
<% if (searchList.size() > 0) { %><b>B&uacute;squeda de Palabra</b>: "<%= searchText %>" en:<% } %> 
<%
	Iterator<BmSearchField> searchIterator = searchList.iterator();
	while (searchIterator.hasNext()) {
		BmSearchField bmSearchField = (BmSearchField)searchIterator.next();
%>
		<%= sFParams.getFieldListTitle(program, bmSearchField.getField(), bmSearchField.getFieldLabel()) %>, 
<%
		}
%>
	<% if (orderList.size() > 0) { %><b>Orden:</b><% } %> 
<%
	Iterator<BmOrder> orderIterator = orderList.iterator();
	while (orderIterator.hasNext()) {
		BmOrder bmOrder = (BmOrder)orderIterator.next();
%>
		<%= sFParams.getFieldListTitle(program, bmOrder.getField(), bmOrder.getLabel()) %>, 
<%
	}
	
	// Modulo PmObject
	Constructor<?> c = Class.forName(program).getConstructor(SFParams.class);
	PmObject pmObject = (PmObject) c.newInstance(sFParams);
	
	// Habilita filtro disclosure
	pmObject.enableDiscloseSetting();
	
	// Contador de registros
	int count = pmObject.count(filterList, searchList, searchText);
%>
		Registros: <%= count %>
	</td>
	<td class="reportDate" align="right">
			Creado: <%= SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat()) %> por: <%= sFParams.getLoginInfo().getEmailAddress() %>
		</td>
	</tr>
</table>
<%	
	// Solo muestra si son 5000 registros o menos
	if (count < 5000) {
	
		ArrayList<BmObject> programList = pmObject.list(filterList, searchList, searchText, orderList, -1, -1);
		Iterator<BmObject> programIterator = programList.iterator();
		if (programIterator.hasNext()) {
		BmObject bmObjectTitle = (BmObject)programIterator.next();
%>
<br>
	<table class="report">
		<tr>
			<td class="reportHeaderCell">
				&nbsp;#
			</td>
		<%			
			ArrayList<BmField> fieldListTitle = bmObjectTitle.getDisplayFieldList();
			Iterator<BmField> fieldTitleIterator = fieldListTitle.iterator();
			while (fieldTitleIterator.hasNext()) {
				BmField bmField = (BmField)fieldTitleIterator.next();
				if (!bmField.isInternal()) {
				%>
					<td class="reportHeaderCell">
						<%= sFParams.getFieldListTitle(bmObjectTitle.getProgramCode(), bmField) %>
					</td>
				<%
				}
			}
		%>
		</tr>
			<%
				int row = 1;
				
				programIterator = programList.iterator();
				
				while (programIterator.hasNext()) {
					BmObject bmObject = (BmObject)programIterator.next();
					String styleName = "reportCellOdd";
					if (row % 2 == 0 ) styleName = "reportCellEven";
			%>
		<tr class="<%= styleName %>">
						<%= HtmlUtil.formatReportCell(sFParams, "" + row, BmFieldType.NUMBER) %>
						
						<%												
								ArrayList<BmField> fieldList = bmObject.getDisplayFieldList();
								Iterator<BmField> fieldIterator = fieldList.iterator();
								int CONTADOR =0;
								while (fieldIterator.hasNext()) {
									CONTADOR=CONTADOR+1;

									BmField bmField = (BmField)fieldIterator.next();
									if (!bmField.isInternal()) {	
						%>
										<%= HtmlUtil.formatReportCell(bmField) %>
						<%
									}
								}
						%>
						
				</tr>
			<%
				row++;
				}
			%>
	</table>	
	<% 
			} 
		} else {
	%>
			No se puede desplegar una lista con mas de 5,000 registros.
	<% } %>

	<script>
		function doPrint() {
		    var img = document.getElementById('printImage');
    			img.style.visibility = 'hidden';
	    		//var img2 = document.getElementById('exportExcel');
	    		//img2.style.visibility = 'hidden';
			window.print();
			img.style.visibility = 'visible';
			//img2.style.visibility = 'visible';			
		}
	</script>

  </body>
</html>