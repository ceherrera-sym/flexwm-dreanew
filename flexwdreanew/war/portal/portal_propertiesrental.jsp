<!--  
/**
 * @author Cesar Herrera Hernández
 */ -->

<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.google.gwt.aria.client.AlertdialogRole"%>
<%@page import="com.symgae.client.ui.UiDetail"%>
<%@page import="com.flexwm.shared.op.BmoOrder"%>
<%@page import="com.flexwm.shared.wf.BmoWFlowDocument"%>
<%@page import="com.flexwm.server.wf.PmWFlowDocument"%>
<%@page import="com.flexwm.shared.wf.BmoWFlow"%>
<%@page import="com.flexwm.server.wf.PmWFlow"%>
<%@page import="com.flexwm.server.ar.PmPropertyRental"%>
<%@page import="com.flexwm.shared.ar.BmoPropertyRental"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.cm.PmCustomer"%>
<%@page import="com.flexwm.shared.cm.BmoCustomer"%>
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@page import="com.flexwm.shared.op.BmoProduct"%>
<%@page import="com.symgae.shared.SFPmException"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.*"%>
<%@ page import="com.symgae.server.*"%>
<%@ page import="com.symgae.server.sf.*"%>
<%@ page import="com.symgae.shared.SFParams"%>
<%@ page import="com.symgae.shared.BmFilter"%>
<%@ page import="com.symgae.shared.BmSearchField"%>
<%@ page import="com.symgae.shared.BmOrder"%>
<%@ page import="com.symgae.shared.BmObject"%>
<%@ page import="com.symgae.shared.BmField"%>
<%@ page import="com.symgae.shared.BmFieldType"%>
<%@ page import="com.symgae.shared.sf.*"%>
<%@ page import="com.symgae.shared.LoginInfo"%>
<%@ page import="java.lang.reflect.Constructor"%>
 <%@ page import="java.text.SimpleDateFormat"%>


<% try{
	
	//Fecha hoy
	Calendar date = new GregorianCalendar();
	String year = Integer.toString(date.get(Calendar.YEAR));
	String month = Integer.toString(date.get(Calendar.MONTH)+1);
	String day = Integer.toString(date.get(Calendar.DAY_OF_MONTH));
	String dateNow = year + "-" + month + "-" + day;
	String monthNow = year + "-" + month;	
	//Dia Final de el mes
	String dayFinish = Integer.toString(date.getActualMaximum(Calendar.DAY_OF_MONTH));
	
	String appTitle = "Contratos";
	String defaultCss = "symgae.css";
	String gwtCss = "gwt_standard.css";
	String fwmCss = "flexwm.css";
	boolean isMobile = false, filters = false;
	SFParams sFParams = new SFParams();
	String sql = "",filter = "";
	String sql2 = "";
	String customer = "",orLessor = "",sqllessors = "";
	String cust_id = (String)session.getAttribute("Id");
	int page1 = 1,custLessor = 0,print = 0;
	int counterQuery = 0;
	int counterFull = 0;
	int pages = 0;
	int pagina = 1;
	
	double sumAmount = 0,sumBalance = 0;
	
	 if(Integer.parseInt(cust_id) <= 0)
		 response.sendRedirect("portal_login.jsp") ;
	 
    String estatusPropertiesRental = request.getParameter("estatusPropertiesRental");
	if(request.getParameter("customer").equalsIgnoreCase("null")){
     customer = "";
         
	}else{
	customer = request.getParameter("customer");
	
	}
	try{
		if (request.getParameter("page") != null)
			 page1 = Integer.parseInt(request.getParameter("page"));
	}catch(Exception e){
		page1 = 1;
	}
	try{
		
		custLessor = Integer.parseInt(request.getParameter("lessors"));
	}catch (Exception e){
		custLessor = 0;
	}
	if(!request.getParameter("printPrrt").equals("")){
		print = Integer.parseInt(request.getParameter("printPrrt"));
	}
	
	//si se realiza una busqueda mostrara la suma total
	if(customer.length()>0)filters = true;
	
	String where = "";
	String initialWhere = " AND orde_status = '"+ BmoOrder.STATUS_AUTHORIZED+"'";
	filter = "Estatus: Autorizado";
	LoginInfo loginInfo = new LoginInfo();
	loginInfo.setLoggedIn(true);
	loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
	SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
	SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
	int id = Integer.parseInt(cust_id);
	
	PmConn pmConn = new PmConn(sFParams);	
	PmConn pmConnCounter = new PmConn(sFParams);
	PmConn pmConnLessors = new PmConn(sFParams);
	
	
	
	
	
	
	BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
	BmoOrder bmoOrder = new BmoOrder ();
	BmoProperty bmoProperty = new BmoProperty();
	PmProperty pmProperty = new PmProperty(sFParams);
	PmWFlowDocument pmWFlowDocument = new PmWFlowDocument(sFParams);
	BmoWFlowDocument bmoWFlowDocument = new BmoWFlowDocument();
	
	
	if(estatusPropertiesRental.equalsIgnoreCase("Estatus")){
		where = "";
		initialWhere = "";
		filter ="";
	}	
	if(estatusPropertiesRental.equalsIgnoreCase("Revisión")){
		where += " AND orde_status = '"+ BmoOrder.STATUS_REVISION +"'";
		initialWhere = "";
		filter = "Estatus: Revisión,";
	}
	if(estatusPropertiesRental.equalsIgnoreCase("Autorizado")){
		where += " AND orde_status = '"+ BmoOrder.STATUS_AUTHORIZED +"'";
		initialWhere = "";
		filter = "Estatus: Autorizado,";
	}
	if(estatusPropertiesRental.equalsIgnoreCase("Finalizado")){
		where += " AND orde_status = '"+ BmoOrder.STATUS_FINISHED +"'";
		initialWhere = "";
		filter= "Estatus: Finalizado,";
	}
	if(estatusPropertiesRental.equalsIgnoreCase("Cancelado")){
		where += " AND orde_status = '"+ BmoOrder.STATUS_CANCELLED +"'";
		initialWhere = "";
		filter = "Estatus: Cancelado,";
	}
	if(!customer.equalsIgnoreCase("")){
		where += " AND (cust_code like '%"+ customer 
				 +"%' OR cust_displayname like '%"+ customer 
				 +"%' or cust_legalname  like '%"+ customer 
				 +"%' or orde_name like '%"+ customer 
				 +"%' or orde_code like '%"+customer+"%')";
		filter += "Busqueda: "+ customer ;
	}
	if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
		isMobile = true;
	}
		
	counterQuery = (page1 * sFParams.getListPageSize()) - sFParams.getListPageSize();
	pmConn.open();
	pmConnCounter.open();
	pmConnLessors.open();
	
	sqllessors = "SELECT cust_customerid FROM customers WHERE cust_customercategory = '"+BmoCustomer.CATEGORY_LESSEE+"' AND cust_lessormasterid = "+cust_id;
	pmConnLessors.doFetch(sqllessors);
	while (pmConnLessors.next()){
		orLessor += " OR prty_customerid = "+ pmConnLessors.getInt("cust_customerid");
		
	}
	
	if (!(custLessor > 0)){
		sql = "SELECT * FROM orders " +
		  	" LEFT JOIN propertiesrent ON (prrt_orderid = orde_originreneworderid) " + 
	     	" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) " +
	      	" LEFT JOIN customers ON (prrt_customerid = cust_customerid) " +
	      	" LEFT JOIN wflows ON (prrt_wflowid = wflw_wflowid) " + 
	      	" LEFT JOIN orderpropertiestax ON (orde_orderid = orpt_orderid) " +
	      	" LEFT JOIN wflowdocuments ON (wflw_wflowid = wfdo_wflowid) " +
          	" WHERE (prty_customerid = " + cust_id + orLessor + " ) AND wfdo_name = 'CONTRATO' " + initialWhere + where +
	      	" ORDER BY prty_customerid,prrt_propertiesrentid ASC ";
		if(!(print > 0)){
			sql += " LIMIT "+(counterQuery) + "," + sFParams.getListPageSize() ;
		}
		sql2 = "SELECT COUNT(orde_orderid) AS C FROM orders " +
			  " LEFT JOIN propertiesrent ON (prrt_orderid = orde_originreneworderid) " + 
		      " LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) " +
		      " LEFT JOIN customers ON (prrt_customerid = cust_customerid) " +
		      " LEFT JOIN wflows ON (prrt_wflowid = wflw_wflowid) " + 
		      " LEFT JOIN wflowdocuments ON (wflw_wflowid = wfdo_wflowid) " +
		      " WHERE (prty_customerid = " + cust_id + orLessor + " ) AND wfdo_name = 'CONTRATO' " + initialWhere + where +
		      " ORDER BY prrt_propertiesrentid ASC";
	}else {
		sql = "SELECT * FROM orders " +
				  " LEFT JOIN propertiesrent ON (prrt_orderid = orde_originreneworderid) " + 
			      " LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) " +
			      " LEFT JOIN customers ON (prrt_customerid = cust_customerid) " +
			      " LEFT JOIN wflows ON (prrt_wflowid = wflw_wflowid) " + 
			      " LEFT JOIN orderpropertiestax ON (orde_orderid = orpt_orderid) " +
			      " LEFT JOIN wflowdocuments ON (wflw_wflowid = wfdo_wflowid) " +
		          " WHERE (prty_customerid = " + custLessor + " ) AND wfdo_name = 'CONTRATO' " + initialWhere + where +
			      " ORDER BY prrt_propertiesrentid ASC ";
		if(!(print > 0)){
			sql+= " LIMIT "+(counterQuery) + "," + sFParams.getListPageSize() ;
		}
		sql2 = "SELECT COUNT(orde_orderid) AS C FROM orders " +
				  " LEFT JOIN propertiesrent ON (prrt_orderid = orde_originreneworderid) " + 
			      " LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) " +
			      " LEFT JOIN customers ON (prrt_customerid = cust_customerid) " +
			      " LEFT JOIN wflows ON (prrt_wflowid = wflw_wflowid) " + 
			      " LEFT JOIN wflowdocuments ON (wflw_wflowid = wfdo_wflowid) " +
			      " WHERE (prty_customerid = " + custLessor + " ) AND wfdo_name = 'CONTRATO' " + initialWhere + where +
			      " ORDER BY prrt_propertiesrentid ASC";
	}
	
	
	pmConn.doFetch(sql);
	pmConnCounter.doFetch(sql2);
%>
	

<!doctype html>


<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">


<% if (isMobile) { %>
<meta name="viewport"
	content="width=320, initial-scale=1.0, maximum-scale=1">
<% } %>

<title>::: <%= appTitle %> :::
</title>			
		                        
<script type="text/javascript" language="javascript"
	src="flexwm/flexwm.nocache.js"></script>
</head>
<body class="default">
<%if (print > 0){ %>
<table border="0" cellspacing="0" cellpading="0" width="100%">
		<tr>
			<td align="left" width="80" rowspan="2" valign="top"><img
				border="0" width="<%=SFParams.LOGO_WIDTH%>"
				height="<%=SFParams.LOGO_HEIGHT%>"
				src="./img/portal_logo.png"></td>
			<td class="reportTitle" align="left" colspan="2"><%=appTitle%></td>
			<td align="right">
			 <input
				type="image" id="printImage" src="<%= GwtUtil.getProperUrl(sFParams, "/icons/print.png")%>"
				name="image" onclick="doPrint()" title="Imprimir"> 
			</td>
		</tr>
		<tr>
           <%if(filter.length() > 0) {%>		
				<td class="reportSubTitle"><b>Filtros:</b> <%=filter%> <br></td>	
		   <%} %>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>			
			</td>
		</tr>
	</table>
<%} %>

	<br>
   <table class="formTable ">
  	 <tr>
				<td class="listHeaderFirstColumn" >#</td>
				<td class="listHeaderFirstColumn" ><%=bmoPropertyRental.getCode().getLabel().toString() %></td>
				<td class="listHeaderFirstColumn" >Arrendador</td>
		     	<td class="listHeaderFirstColumn" >Nombre Contrato</td>
		     	
		   	  	<td class="listHeaderFirstColumn" >Contrato</td>
		   	  	<td class="listHeaderFirstColumn" >Clave Renovados</td>		   	  	
		   	  	<td class="listHeaderFirstColumn" ><%=bmoOrder.getBmoCustomer().getDisplayName().getLabel().toString() %></td>
		   	  	<td class="listHeaderFirstColumn" >Logo </td>
		   	  	<td class="listHeaderFirstColumn" >Meses</td>	
		   	  	<td class="listHeaderFirstColumn" >Vencimiento</td>
				<td class="listHeaderFirstColumn" >Vigencia Contrato</td>
				<td class="listHeaderFirstColumn" ><%=bmoPropertyRental.getRentIncrease().getLabel().toString() %></td>
				<td class="listHeaderFirstColumn" >Vigencia Incremento</td>
		   	  	
<!-- 		      	<td class="reportHeaderCell" >Vencimiento</td>	 -->
<!-- 		      	<td class="reportHeaderCell" >Vigencia Pedido</td>	   -->
		      	<td class="listHeaderFirstColumn" ><%=bmoOrder.getStatus().getLabel().toString()%></td>    
		        <td class="listHeaderFirstColumn" ><%=bmoPropertyRental.getInitialIconme().getLabel().toString() %></td>
		        <td class=listHeaderFirstColumn >Renta Vigente</td>		        
		        <td class="listHeaderFirstColumn" >Total</td>
		        <td class="listHeaderFirstColumn" >Saldo</td>
	</tr>
	<%
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	int i = counterQuery +1;
	while(pmConn.next()){
		bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn.getInt("prty_customerid"));
		
		SFServerUtil sFServerUtil = new SFServerUtil();
		int months1 = sFServerUtil.daysBetween("yyyy-MM-dd",dateNow , pmConn.getString("prrt_enddate"));
// 		int months2 = sFServerUtil.daysBetween("yyyy-MM-dd",dateNow , pmConn.getString("orde_lockend"));
		int months3 = sFServerUtil.daysBetween("yyyy-MM-dd",dateNow , pmConn.getString("prrt_rentincrease")); 
		
	
		%>
		
		
		
		
	<tr>
		<%=HtmlUtil.formatReportCell(sFParams,"" + i,BmFieldType.NUMBER) %>		
		<%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(pmConn.getString("prrt_code")),BmFieldType.CODE)%>   	    	
	    <%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(bmoCustomer.getDisplayName().toString()),BmFieldType.STRING)%>
		<%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(pmConn.getString("orde_name")),BmFieldType.STRING)%>
		
		<td  class="reportCellEven" style="text-align: center" title="Clic para abrir el documento en pesta&ntilde;a nueva">	
			<%if(pmConn.getString("wfdo_file").length() > 0){
				String blobkey = pmConn.getString("wfdo_file");
				String blobKeyParseWfdoFile = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			<a align="center" target='_blank' href="<%=blobKeyParseWfdoFile %>">
			
	    		<img  src=<%=GwtUtil.getProperUrl(sFParams, "/icons/file-search.png") %> width="17px" height="17px">
	    	</a>
			<%} %>
		</td >	
		<%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(pmConn.getString("orde_code")),BmFieldType.CODE)%>
			
		<%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(pmConn.getString("cust_displayname")),BmFieldType.STRING)%>
		<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva" >
			<%if(pmConn.getString("cust_logo").length() > 0){
	    	String blobkey = pmConn.getString("cust_logo");
	    	String blobKeyParseLogo =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    	
	    
	        <a target='_blank' href="<%= blobKeyParseLogo%>">
		        <img src="<%=blobKeyParseLogo %>" width="50px" height="25px">
	        </a>	
	    <%}%>
		</td>
		<%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(pmConn.getString("orpt_quantity")),BmFieldType.NUMBER)%>
				<%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(pmConn.getString("prrt_enddate")) ,BmFieldType.DATE)%>
<!-- 	    	Semaforos Vigencia Contrato -->
		<%if(months1 <= 60){ %>
		 <td  class="reportCellEven" style="text-align: center">		
			<img alt="" src= <%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_red.png") %>>
		 </td>	 	
	    	<%} %>
	    		<%if(months1 <= 180 && months1 >= 61){ %>
		 <td  class="reportCellEven" style="text-align: center" >
		 	<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_yellow.png") %>>			
		 </td>	 	
	    	<%} %>
	    	<%if(months1 >= 181){ %>
	     <td  class="reportCellEven" style="text-align: center" >
	     	<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_green.png") %>>			
		 </td>	 	
	    	<%} %>
	    	
	    	<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prrt_rentincrease") ,BmFieldType.DATE) %>	
<!-- 	    	Semaforos Incremento Renta -->
	    	<%if(months3 <= 30){ %>
		 <td  class="reportCellEven" style="text-align: center">
			<img alt="" src= <%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_red.png") %>>
		 </td>	 	
	    	<%} %>
	    	<%if(months3 <= 60	&&  months3 >= 31){ %>
		 <td  class="reportCellEven" style="text-align: center" >
			<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_yellow.png") %>>
		 </td>	 	
	    	<%} %>
	    	<%if(months3 >= 61){ %>
	     <td  class="reportCellEven" style="text-align: center" >
			<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_green.png") %>>
		 </td>	 	
	    	<%} %>  	    	
	    		 

	    	
	    <td  class="reportCellText" style="text-align: center">
		     <%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_AUTHORIZED)) %>Autorizado
		     <%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_CANCELLED)) %>Cancelado
		     <%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_FINISHED)) %>Finalizado
		     <%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_REVISION)) %>En Revisi&oacute;n 		    
		</td>  
		</td>	
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prrt_initialIconme"),BmFieldType.CURRENCY) %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("orpt_price"),BmFieldType.CURRENCY) %>
		
		<%=HtmlUtil.formatReportCell(sFParams,"" + pmConn.getDouble("orpt_amount"),BmFieldType.CURRENCY) %>
		<%=HtmlUtil.formatReportCell(sFParams,"" + pmConn.getDouble("orde_balance"),BmFieldType.CURRENCY) %>
			
		<%sumAmount += pmConn.getDouble("orpt_amount"); 
		  sumBalance += pmConn.getDouble("orde_balance");%>
	</tr>
	<%i++;
	} %>
	<%if (filters || print > 0) {%>
	<tr class="reportCellEven reportCellCode">
		<td colspan="16" class= "reportCellEven"></td>
		<%=HtmlUtil.formatReportCell(sFParams,"" + sumAmount,BmFieldType.CURRENCY) %>
		<%=HtmlUtil.formatReportCell(sFParams,"" + sumBalance,BmFieldType.CURRENCY) %>
	</tr>
	<%} %>
   </table>	
   <table>
   		<tr></tr>   
  	 	<tr>
   <%while(pmConnCounter.next()){
		counterFull = pmConnCounter.getInt("C");
	}
	//Obtener total de paginas a mostrar
	pages = (counterFull/sFParams.getListPageSize());
	if (counterFull % sFParams.getListPageSize() > 0)pages ++;%>
	<%if(!(print > 0)){ %>
		<td  class="reportDate" >Página </td>
	<%} %>
	<%if (page1 > 1){ %>	
		<td><a class ="listPageCountLabel"  title="Primera Página" href = "portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&page=1&lessors=<%=custLessor %>&printPrrt=<%=print%>"> << </a></td>
		<td><a class ="listPageCountLabel"  title="Anterior" href = "portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&page=<%=(page1-1)%>&lessors=<%=custLessor %>&printPrrt=<%=print%>"> < </a></td>	
	<%} %>
	<%if(!(print > 0)){ %>
	<%while(pagina <= pages){%>
	
		<%if (pagina == page1){ %>
			<td ><a class = "listPageCountSelectedLabel" href = "portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&page=<%=pagina%>&lessors=<%=custLessor %>&printPrrt=<%=print%>"><b><u><%=pagina  %></u></b></a></td>
		<%}else { %>
			<td ><a  class ="listPageCountLabel" href = "portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&page=<%=pagina%>&lessors=<%=custLessor %>&printPrrt=<%=print%>"><%=pagina  %></a></td>
		<%} %>
		
		<%	pagina ++;
		}%> 
		<%if (page1 < pages){ %>	

		<td><a  class ="listPageCountLabel" title="Siguiente" href = "portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&page=<%=(page1+1)%>&lessors=<%=custLessor %>&printPrrt=<%=print%>"> > </a></td>

	 	<td><a  class ="listPageCountLabel" title="Ir a la Ultima Página"  href = "portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&page=<%=pages%>&lessors=<%=custLessor %>&printPrrt=<%=print%>"> >> </a></td>
	
		<%} %>
	<%} %>
	</tr>
   </table>
   	<script>
		function doPrint() {
			var img = document.getElementById('printImage');
    		img.style.visibility = 'hidden';
    		
			window.print();
			
			img.style.visibility = 'visible';	
		}
	</script>
</body>
</html>
<%pmConn.close();
pmConnCounter.close();
pmConnLessors.close();
}catch(Exception e){
// 	response.sendRedirect("portal_login.jsp");
}
 %>