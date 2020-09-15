<!--  
/**
 * @author Cesar Herrera Hernández
 */ -->


<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.google.gwt.user.client.Window"%>
<%@page import="java.text.SimpleDateFormat"%>
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



<%try{%>
	
	<%@include file="./inc_params.jsp"%>
<% //Fecha hoy
	Calendar date = new GregorianCalendar();
	String year = Integer.toString(date.get(Calendar.YEAR));
	String month = Integer.toString(date.get(Calendar.MONTH)+1);
	String day = Integer.toString(date.get(Calendar.DAY_OF_MONTH));
	String dateNow = year + "-" + month + "-" + day;
	String monthNow = year + "-" + month;	
	String dayFinish = Integer.toString(date.getActualMaximum(Calendar.DAY_OF_MONTH));
	String dayInit = Integer.toString(date.getActualMinimum(Calendar.DAY_OF_MONTH));
	String appTitle = "Cobranza";
	String defaultCss = "symgae.css";
	String gwtCss = "gwt_standard.css";
	String fwmCss = "flexwm.css";
	boolean isMobile = false;	
	String sql = "",sqllessors = "",orLessor = "",filter = "",sql2 = "",customer = "",sql3 = "",sql4= "",duedate1 = "",duedate2 = "";
	String cust_id = (String)session.getAttribute("Id");
	int page1 = 1,print = 0;
	int counter = 1;
	int counterQuery = 0;
	int pagina = 1,custLessor = 0;
	int counterFull = 0;
	int pages = 0;
	boolean payment = false;
	String color = "";
	String[] colors = new String[21];
	colors[1] = "#DF013A";
	colors[2] = "#4000FF";
	colors[3] = "#088A29";
	colors[4] = "#F7D358";
	colors[5] = "#F4FA58";
	colors[6] = "#DA38C7";
	colors[7] = "#98792F";
	colors[8] = "#826969";
	colors[9] = "#3E2C8C";
	colors[10] = "#93B2A5";
	colors[11] = "#F6820D";
	colors[12] = "#2EBACA";
	colors[13] = "#2805F0";
	colors[14] = "#54462A";
	colors[15] = "#4029A7";
	colors[16] = "#B706F3";
	colors[17] = "#FC8301";
	colors[18] = "#EECDAB";
	colors[19] = "#863232";
	colors[20] = "#5F1212";
	 if(Integer.parseInt(cust_id) <= 0)
		 response.sendRedirect("portal_login.jsp") ;
	
	String selestRaccountsStatus = request.getParameter("EstatusP");
	if(request.getParameter("customer1").equalsIgnoreCase("null")){
	     customer = "";
		}else{
		customer = request.getParameter("customer1");
		}
	if(request.getParameter("date1").equalsIgnoreCase("null")){
		duedate1 = "";
		}else{
		duedate1 = request.getParameter("date1");	
		}
	if(request.getParameter("date2").equalsIgnoreCase("null")){
		duedate2 = "";
		}else{
		duedate2 = 	request.getParameter("date2");
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
	if(!request.getParameter("printRacc").equals("")){
		print = Integer.parseInt(request.getParameter("printRacc"));
	}
	String where = "", sqlChart = "";
	
	int id = Integer.parseInt(cust_id);
	double sumPayments = 0;
	double sumBalance = 0;
	PmConn pmConn = new PmConn(sFParams);	
	PmConn pmConnG = new PmConn(sFParams);
	PmConn pmConnCounter = new PmConn(sFParams);
	PmConn pmConnLessors =new PmConn(sFParams);
	PmConn pmConnG2 = new PmConn(sFParams);
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmPropertyRental pmPropertyRental = new PmPropertyRental(sFParams);
	BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();	
	BmoRaccount bmoRaccount = new BmoRaccount();
	PmRaccount pmRaccount = new PmRaccount(sFParams);
	
	
	if(selestRaccountsStatus.equalsIgnoreCase("Estatus de Pago")){
		where = "";
	}	
	if(selestRaccountsStatus.equalsIgnoreCase("Pendiente")){
		where += "AND racc_paymentstatus = '"+ BmoRaccount.PAYMENTSTATUS_PENDING +"'";
		filter = "Estatus: Pendiente,";
	}
	if(selestRaccountsStatus.equalsIgnoreCase("Pago Total")){
		where += "AND racc_paymentstatus = '"+ BmoRaccount.PAYMENTSTATUS_TOTAL +"'";
		filter = "Estatus: Pago Total,";
	}
	if(!customer.equalsIgnoreCase("")){
		where += "AND (cust_code like '%" + customer +"%' OR " +
				 "cust_displayname like '%" + customer +"%' OR " +
				 "cust_legalname like '%" + customer +"%' OR " +
				 "racc_code like '%" + customer +"%' OR " +
				 "prty_description like '%" + customer +"%' ) ";	
		filter += "Busqueda: "+ customer ;
	}
	if(!duedate1.equalsIgnoreCase("") || !duedate2.equalsIgnoreCase("")){
		where += "AND (racc_duedate BETWEEN '" + duedate1 + "' AND '" + duedate2 + "')";	
		filter += "Fecha: F. "+duedate1+",F. Fin "+duedate2;
	}else{
		where += "AND (racc_duedate BETWEEN '" + monthNow + "-1' AND '" + monthNow + "-" + dayFinish + "')";
		filter += "Fecha: F. "+ monthNow + "-1,F. Fin"+duedate2;
	}
	if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
		isMobile = true;
	}
	
	counterQuery = (page1 * sFParams.getListPageSize()) - sFParams.getListPageSize();
	pmConnG.open();
	pmConn.open();
	pmConnCounter.open();
	pmConnLessors.open();
	pmConnG2.open();
	
	sqllessors = "SELECT cust_customerid FROM customers WHERE cust_customercategory = '"+BmoCustomer.CATEGORY_LESSEE+"' AND cust_lessormasterid = "+cust_id;
	pmConnLessors.doFetch(sqllessors);
	while (pmConnLessors.next()){
		orLessor += " OR prty_customerid = "+ pmConnLessors.getInt("cust_customerid");
		
	}
 
	if (!(custLessor > 0)){
		sql = "SELECT * FROM orders "
		  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
		  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
		  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
		  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
		  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
		  +" WHERE racc_raccountid is not null "
		  +" AND ract_type = 'W' AND ract_category = 'O' "
		  +" AND (prty_customerid = " + cust_id + orLessor + " )"
		  + where 
		  +" ORDER BY prty_customerid,orde_orderid ASC";
		if(!(print > 0)){
			sql += " LIMIT " + (counterQuery) + "," 
		 	 + sFParams.getListPageSize();
		}
		sqlChart = "SELECT * FROM orders "
				  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
				  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
				  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
				  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
				  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
				  +" WHERE racc_raccountid is not null "
				  +" AND ract_type = 'W' AND ract_category = 'O' "
				  +" AND (prty_customerid = " + cust_id + orLessor + " )"
				  + where 
				  +" ORDER BY prty_customerid,orde_orderid ASC";
		
		
		sql3 = "SELECT COUNT(racc_raccountid) AS C FROM orders "
			  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
			  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
			  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
			  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
			  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
			  +" WHERE racc_raccountid is not null "
			  +" AND ract_type = 'W' AND ract_category = 'O' "
			  +" AND (prty_customerid = " + cust_id + orLessor + " )"
			  + where 
			  +" ORDER BY orde_orderid"
			  +" ASC ";
	}else {
		sql = "SELECT * FROM orders "
				  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
				  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
				  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
				  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
				  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
				  +" WHERE racc_raccountid is not null "
				  +" AND ract_type = 'W' AND ract_category = 'O' "
				  +" AND (prty_customerid = " + custLessor+ " )"
				  + where 
				  +" ORDER BY orde_orderid ASC";
				 
		if(!(print > 0)){
			sql += " LIMIT " + (counterQuery) + "," 
		 	 + sFParams.getListPageSize();
		}
		sqlChart = "SELECT * FROM orders "
				  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
				  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
				  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
				  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
				  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
				  +" WHERE racc_raccountid is not null "
				  +" AND ract_type = 'W' AND ract_category = 'O' "
				  +" AND (prty_customerid = " + custLessor+ " )"
				  + where 
				  +" ORDER BY orde_orderid ASC";
		
		sql3 = "SELECT COUNT(racc_raccountid) AS C FROM orders "
				  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
				  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
				  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
				  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
				  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
				  +" WHERE racc_raccountid is not null "
				  +" AND ract_type = 'W' AND ract_category = 'O' "
				  +" AND (prty_customerid = " + custLessor+ " )"
				  + where 
				  +" ORDER BY orde_orderid"
				  +" ASC ";
		
	}
	pmConn.doFetch(sqlChart);		
	pmConnCounter.doFetch(sql3);
	
%>	

<!doctype html>


<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">

<%-- <% if (isMobile) { %> --%>
<meta name="viewport"
	content="width=320, initial-scale=1.0, maximum-scale=1">
<%-- <% } %> --%>

<title>::: <%= appTitle %> :::
</title>
		

<body class="default">		


</head>

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
		   <%if(filter.length() > 0){ %>
				<td class="reportSubTitle"><b>Filtros:</b> <%=filter%> <br>
		   <%} %>
			</td>
			<td class="reportDate" align="right">Creado: <%=SFServerUtil.nowToString(sFParams, sFParams.getDateTimeFormat())%>		
					
			</td>
		</tr>
	</table>
<%} %>
   <br>
   
   <table class="formTable " width="100%">
  
  	<% 
  	//Graficas
  	if (!(custLessor > 0)){
  		sql2 = "SELECT COUNT(racc_paymentstatus) AS c,racc_paymentstatus FROM orders "
  			  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
  			  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
  			  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
  			  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
  			  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
  			  +" WHERE racc_raccountid is not null "
  			  +" AND ract_type = 'W' AND ract_category = 'O' "
  			  +" AND (prty_customerid = " + cust_id + orLessor + " )"
  			  + where 
  			  + "GROUP BY racc_paymentstatus "; 	
  		
  		sql4 = "SELECT sum(racc_total) AS sum,count(racc_customerid),racc_customerid FROM orders "
  			  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
  			  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
  			  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
  			  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
  			  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
  			  +" WHERE racc_raccountid is not null "
  			  +" AND ract_type = 'W' AND ract_category = 'O' "
  			  +" AND (prty_customerid = " + cust_id + orLessor + " ) "
  			  + where 
  			  +" group by racc_customerid ";
  			 
  	}else{
  		sql2 = "SELECT COUNT(racc_paymentstatus) AS c,racc_paymentstatus FROM orders "
    			  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
    			  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
    			  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
    			  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
    			  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
    			  +" WHERE racc_raccountid is not null "
    			  +" AND ract_type = 'W' AND ract_category = 'O' "
    			  +" AND (prty_customerid = " + custLessor + " )"
    			  + where 
    			  + "GROUP BY racc_paymentstatus "; 	
  		sql4 = "SELECT sum(racc_total) AS sum,count(racc_customerid),racc_customerid FROM orders "
    			  +" LEFT JOIN raccounts ON (racc_orderid = orde_orderid) "
    			  +" Left join raccounttypes ON (racc_raccounttypeid = ract_raccounttypeid) "
    			  +" LEFT JOIN propertiesrent ON (orde_originreneworderid = prrt_orderid) "
    			  +" LEFT JOIN properties ON (prrt_propertyid = prty_propertyid) "
    			  +" LEFT JOIN customers ON (prrt_customerid = cust_customerid) "
    			  +" WHERE racc_raccountid is not null "
    			  +" AND ract_type = 'W' AND ract_category = 'O' "
    			  +" AND (prty_customerid = " + custLessor + " ) "
    			  + where 
    			  +" group by racc_customerid ";
  	}
  
%>
	

<!--    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script> -->
<!--       <script type="text/javascript"> -->
<!-- //       google.charts.load('current', {packages: ['corechart', 'bar']}); -->
<!-- //       google.charts.setOnLoadCallback(drawBasic); -->
<!-- //       function drawBasic() { -->
<!-- //     	  var data = google.visualization.arrayToDataTable([ -->
<!-- //   		  	['Arrendador', 'Totales', { role: 'style' },{ role: 'annotation' } ], -->
<%-- <%--   			<%   		  	 --%>
<!-- // //   		  	pmConnG2.doFetch(sql4); -->
  		   
<!-- // 		    int c=1; -->
		    
<!-- //   		  	while(pmConnG2.next()){  		   -->
<!-- //   		  	bmoCustomer = (BmoCustomer)pmCustomer.get(pmConnG2.getInt("racc_customerid")); -->
<%--   		  	%> --%>
<%--   		  	<%= "['" + bmoCustomer.getDisplayName() + "', " +  pmConnG2.getDouble("sum") +  --%>
<%--   		  	      ",'color: "+colors[c]+"',"+ pmConnG2.getDouble("sum")+"]," %> --%>
<%--   		  	<% --%>
<!-- //   		    c++; -->
<!-- //   		  	} -->
  		  	
<%-- <%--   		  	%> --%> 
<!-- //   			]); -->
  		  	
<!-- //     	  var options = {  'width':900, 'height':400, -->
    		      
<!-- //   		        hAxis: { -->
<!-- //   		          title: 'Arrendatario', -->
<!-- //   		          minValue: 0 -->
<!-- //   		        }, -->
<!-- //   		        vAxis: { -->
<!-- //   		          title: 'Total'    		           -->
<!-- //   		        }, -->
<!-- //   		        x: { -->
<!-- //   		          0: { side: 'top'}  -->
<!-- //   		        } -->
<!-- //   		        }; -->
  			
<!-- //   		 	 var chart = new google.visualization.ColumnChart(document.getElementById('piechart2'));	  -->
<!-- //   		 	 chart.draw(data, options); -->
    	  
<!-- //       } -->
<!--     </script> -->
	 <tr>	 
<!-- 	 	<td colspan="5" id="piechart2"> -->

<!-- 	 	</td>	 -->
	 	<td colspan="7" id="piechart">
	 	</td> 		
	 </tr>
	 <tr>
				<td class="listHeaderFirstColumn" >#</td>
		    	<td class="listHeaderFirstColumn" >Contrato</td>
		    	<td class="listHeaderFirstColumn" >Arrendador</td>
		    	<td class="listHeaderFirstColumn" >Inmueble</td>
		    	<td class="listHeaderFirstColumn" ><%=bmoRaccount.getBmoCustomer().getDisplayName().getLabel() %></td>
		    	<td class="listHeaderFirstColumn" ><%=bmoRaccount.getDueDate().getLabel() %></td>
		     	<td class="listHeaderFirstColumn" >Comentarios</td>
		     	<td class="listHeaderFirstColumn" ><%=bmoRaccount.getPaymentStatus().getLabel().toString() %></td>
		    	<td class="listHeaderFirstColumn" ><%=bmoRaccount.getTotal().getLabel().toString() %></td>
		    	<td class="listHeaderFirstColumn" ><%=bmoRaccount.getPayments().getLabel().toString() %></td>
		    	<td class="listHeaderFirstColumn" ><%=bmoRaccount.getBalance().getLabel().toString() %></td>
			    	      
	</tr>
	<%
	
	int i = counterQuery +1;
	double sumPayPending = 0, sumPayTotal = 0, sumPayVenc = 0;
	while(pmConn.next()){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = simpleDateFormat.parse(dateNow);
		Date enddate = simpleDateFormat.parse(pmConn.getString("racc_duedate"));
		int days =(int)((enddate.getTime()-startDate.getTime())/86400000);
		
		if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_PENDING) && days > 0 ){ 
			sumPayPending = sumPayPending + pmConn.getDouble("racc_total");
		}else if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_PENDING) && (days <= 0)){
			sumPayVenc = sumPayVenc + pmConn.getDouble("racc_total");
		}else if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_TOTAL)){
			sumPayTotal = sumPayTotal +pmConn.getDouble("racc_total");
		}
	}
	pmConn.doFetch(sql);
	while(pmConn.next()){
		bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn.getInt("prty_customerid"));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = simpleDateFormat.parse(dateNow);
		Date enddate = simpleDateFormat.parse(pmConn.getString("racc_duedate"));
		int days =(int)((enddate.getTime()-startDate.getTime())/86400000);%>		
	<tr> 
		<%=HtmlUtil.formatReportCell(sFParams,""+i,BmFieldType.NUMBER) %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prrt_name"),BmFieldType.STRING) %>
		<%=HtmlUtil.formatReportCell(sFParams,HtmlUtil.stringToHtml(bmoCustomer.getDisplayName().toString()),BmFieldType.STRING)%>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_description"),BmFieldType.STRING) %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("cust_displayname"),BmFieldType.STRING) %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("racc_duedate"),BmFieldType.DATE) %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("racc_commentlog"),BmFieldType.STRING) %>
			<%if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_PENDING) && days > 0 ){ %>
		  		<td  class="reportCellEven" style="text-align: center"  >
					<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_yellow.png") %>>
		  </td>	
		  <%
		  }else %>
		  <%if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_PENDING) && (days <= 0)){ %>
		  <td  class="reportCellEven" style="text-align: center" >
			<img alt="" src= <%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_red.png") %>>
		  </td>	
		  <%
		  } else%>
		   <%if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_TOTAL)){ %>
		  <td  class="reportCellEven" style="text-align: center" >
			<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portal/icons/btn_green.png") %>>
		  </td>	
		  <%
		  } %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("racc_total"),BmFieldType.CURRENCY) %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("racc_payments"),BmFieldType.CURRENCY) %>
		<%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("racc_balance"),BmFieldType.CURRENCY) %>
		
		<%sumPayments += pmConn.getDouble("racc_payments");
		  sumBalance += pmConn.getDouble("racc_balance");%>
		<%i++;%>
	</tr>			
	<%} %>
	<tr class="reportCellEven reportCellCode">
		<td colspan="9" class= "reportCellEven"></td>
		<%=HtmlUtil.formatReportCell(sFParams,""+sumPayments,BmFieldType.CURRENCY) %>
		<%=HtmlUtil.formatReportCell(sFParams,""+sumBalance,BmFieldType.CURRENCY) %>
		
	</tr>
	</table>
	<table>
	<tr>
	<tr></tr>
		<%
	//Contar numero de registros obtenidos
	while(pmConnCounter.next()){
		counterFull = pmConnCounter.getInt("C");
		
	}
	//Obtener total de paginas a mostrar
	pages = (counterFull/sFParams.getListPageSize());
	if (counterFull % sFParams.getListPageSize() > 0)pages ++;
	
	%>
	<%if(!(print > 0)){ %>
		<td  class="reportDate" >Página </td>
	<%if (page1 > 1){ %>	
		 
		
		<td><a class = "listPageCountLabel" title="Primera Página" href = "portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer%>&date1=<%=duedate1%>&date2=<%=duedate2 %>&page=1&lessors=<%=custLessor %>&printRacc=<%=print%>"> << </a></td>
		
		<td><a class ="listPageCountLabel" title="Anterior" href = "portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer%>&date1=<%=duedate1%>&date2=<%=duedate2 %>&page=<%=(page1-1)%>&lessors=<%=custLessor %>&printRacc=<%=print%>"> < </a></td>	
	<%} %>
		
	<%while(pagina <= pages){%>
		
			<%if (pagina == page1){ %>
			<td  ><a class = "listPageCountSelectedLabel" href = "portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer%>&date1=<%=duedate1%>&date2=<%=duedate2 %>&page=<%=pagina%>&lessors=<%=custLessor %>&printRacc=<%=print%>"><b><u><%=pagina  %></u></b></a></td>
			<%}else { %>
			<td ><a class ="listPageCountLabel" href = "portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer%>&date1=<%=duedate1%>&date2=<%=duedate2 %>&page=<%=pagina%>&lessors=<%=custLessor %>&printRacc=<%=print%>"><%=pagina  %></a></td>
			<%} %>
		
	<%	pagina ++;
	}%> 
	<%if (page1 < pages){ %>	
       
		<td><a class ="listPageCountLabel" title="Siguiente" href = "portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer%>&date1=<%=duedate1%>&date2=<%=duedate2 %>&page=<%=(page1+1)%>&lessors=<%=custLessor %>&printRacc=<%=print%>"> > </a></td>
        
	 	<td><a class ="listPageCountLabel" title="Ir a la Ultima Página"  href = "portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer%>&date1=<%=duedate1%>&date2=<%=duedate2 %>&page=<%=pages%>&lessors=<%=custLessor %>&printRacc=<%=print%>"> >> </a></td>
	
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
	<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>

	<script type="text/javascript">
    google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(drawChart);

    function drawChart() {

      var data = google.visualization.arrayToDataTable([
        ['Renta', 'Total'],
        
        ['Pendiente',  <%= sumPayPending%>],
        ['Vencida',  <%= sumPayVenc%>],
        ['Al Corriente',  <%= sumPayTotal%>],
       
      ]);

      var options = {
    	'width':900, 'height':400,
        title: 'Total por Estatus de Pago',
        is3D: true,
        slices: {
            0: { color: '#fed821' },
            1: { color: 'red' },
            2: { color: 'green' }
          }
      };

      var chart = new google.visualization.PieChart(document.getElementById('piechart'));

      chart.draw(data, options);
    }
	
	</script>
</body>
</html>

<%
pmConn.close();
pmConnG.close();
pmConnCounter.close();
pmConnLessors.close();
pmConnG2.close();
}catch(Exception e){
	
 //	response.sendRedirect("portal_login.jsp") ;
}
 %>