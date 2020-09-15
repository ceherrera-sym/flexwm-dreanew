<!--  
/**
 * @author Cesar Herrera Hernández
 */ -->



<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.flexwm.shared.fi.BmoRaccount"%>
<%@page import="com.flexwm.server.fi.PmRaccount"%>
<%@page import="com.flexwm.server.ar.PmPropertyRental"%>
<%@page import="com.flexwm.shared.ar.BmoPropertyRental"%>
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
<%@ page import="com.google.gwt.user.client.ui.ListBox" %>



<% try{ %>
	<%@include file="./inc_params.jsp"%>	
<%	    
	Calendar date = new GregorianCalendar();
	String year = Integer.toString(date.get(Calendar.YEAR));
	String month = Integer.toString(date.get(Calendar.MONTH)+1);
	if((Integer.parseInt(month)) < 10)
		month = "0" + month;
	String day = Integer.toString(date.get(Calendar.DAY_OF_MONTH));

	
	String monthNow = year + "-" + month;	
	String dayFinish = Integer.toString(date.getActualMaximum(Calendar.DAY_OF_MONTH));
	String dayInit = Integer.toString(date.getActualMinimum(Calendar.DAY_OF_MONTH));
	
	// Declarar variables
	String appTitle = "Portal INADICO";
	String defaultCss = "symgae.css";
	String gwtCss = "gwt_standard.css";
	String fwmCss = "flexwm.css",custLessor = "";
	boolean isMobile = false;
	String sql = "",program="";
	String cust_id = (String)session.getAttribute("Id");
	 if(Integer.parseInt(cust_id) <= 0)
		 response.sendRedirect("portal_login.jsp") ;
     
	int id = Integer.parseInt(cust_id),print = 0;	
	
	PmConn pmConn = new PmConn(sFParams);	
	PmConn pmConn2 = new PmConn(sFParams);	
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();
	bmoCustomer = (BmoCustomer)pmCustomer.get(Integer.parseInt(cust_id));
	
	BmoProperty bmoProperty = new BmoProperty();
	PmProperty pmProperty = new PmProperty(sFParams);
	PmPropertyRental pmPropertyRental = new PmPropertyRental(sFParams);
	BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
	PmRaccount pmRaccount = new PmRaccount(sFParams);
	BmoRaccount bmoRaccount = new BmoRaccount();
	
	if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
		isMobile = true;		
	}
	if (!request.getParameter("program").equals(""))
		program = request.getParameter("program");
	pmConn.open();
	pmConn2.open();
	String sqlLessors = "SELECT * FROM customers WHERE cust_customercategory = '"+BmoCustomer.CATEGORY_LESSEE+"' AND cust_lessormasterid = "+cust_id;
	String sqlLessorCount = "SELECT cust_customerid FROM customers WHERE cust_customercategory = '"+BmoCustomer.CATEGORY_LESSEE+"' AND cust_lessormasterid = "+cust_id;;				
%>
<script type="text/javascript">

	

//     Propertiesrental
	function aplicarPrrt(){
		 document.getElementById("propertiesrentalForm").setAttribute("target", "propertiesRentalIframe"); 
		 document.getElementById("printPrrt").setAttribute("value", "0");
	}
	function imprimirPrrt(){
		 document.getElementById("propertiesrentalForm").setAttribute("target", "_blank"); 
		 document.getElementById("printPrrt").setAttribute("value", "1");
	}
// 	Raccounts
	function aplicarRacc(){
		 document.getElementById("raccountsForm").setAttribute("target", "raccountsIframe"); 
		 document.getElementById("printRacc").setAttribute("value", "0");
	}
	function imprimirRacc(){
		 document.getElementById("raccountsForm").setAttribute("target", "_blank"); 
		 document.getElementById("printRacc").setAttribute("value", "1");
	}
// 	properties printPrty
	function aplicarPrty(){
		 document.getElementById("propertiesForm").setAttribute("target", "propertiesIframe"); 
		 document.getElementById("printPrty").setAttribute("value", "0");
	}
	function imprimirPrty(){
		 document.getElementById("propertiesForm").setAttribute("target", "_blank"); 
		 document.getElementById("printPrty").setAttribute("value", "1");
	}
	
		
	function changeLessor() {

		var lessor = document.getElementById("lessors1");
		<%if (program.equals("prrt")) {%>
			var pagina = document.getElementById("propertiesrentalForm")
						.getAttribute("action");
				document.getElementById("lessorsPrrt").setAttribute("value",
						lessor.value);
				document.getElementById("propertiesrentalForm").setAttribute("target",
						"propertiesRentalIframe");
				document.getElementById("propertiesrentalForm").submit();
		<%} else if (program.equals("racc")) {%>
			var pagina = document.getElementById("raccountsForm").getAttribute(
						"action");
				document.getElementById("lessorsRacc").setAttribute("value",
						lessor.value);
				document.getElementById("raccountsForm").setAttribute("target",
						"raccountsIframe");
				document.getElementById("raccountsForm").submit();
		<%} else if (program.equals("prty")) {%>
			var pagina = document.getElementById("propertiesForm").getAttribute(
						"action");
				document.getElementById("lessorsPrty").setAttribute("value",
						lessor.value);
				document.getElementById("propertiesForm").setAttribute("target",
						"propertiesIframe");
				document.getElementById("propertiesForm").submit();
		<%}%>
	}
	
</script>

<!doctype html>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">
<link type="text/css" rel="stylesheet" href="./css/<%= gwtCss %>">
<link type="text/css" rel="stylesheet" href="./css/<%= fwmCss %>">
<link type="text/css" rel="stylesheet" href="./css/tags.jsp">
<link type="text/css" rel="stylesheet" href="./css/login.css">

<meta name="viewport" content="width=100%,height=100%, initial-scale=1.0, maximum-scale=1.0,min-height: 600px">
		
<title>::: <%= appTitle %> :::</title>		
</head>
<body style="background: #fafafa;Transition:width 4s;"  >		
<div style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;height: auto;min-width: 600px;min-width: 1200px">
	 <header style="width: 100%">
			<nav class="menu_down" id="menuDown"></nav>
		<nav id="menu" style="position: absolute;z-index: 1;height: auto;width: 30%;-webkit-box-shadow: 0px 0px 16px 5px rgba(0,0,0,0.75);-moz-box-shadow: 0px 0px 16px 5px rgba(0,0,0,0.75);box-shadow: 0px 0px 16px 5px rgba(0,0,0,0.75);" >
			<ul>
				<li>
					<img src="./img/logoWhite.png" class="startLogo" >
				</li>
				<li class="menuCust " style="height: 5%;margin-left: 8%;margin-right: 10%;font-family: Helvetica;margin-top: 3%">
						<%=bmoCustomer.getDisplayName().toString()%>
				</li>
					<%						
						if (!(program.equals("racc"))) {
					%>
							<li class="menuItem"><a style="color: white; font-size: 10pt; font-family:  Helvetica;"
								href="portal_start.jsp?program=racc&viewHelp=0"><img
									class="programTitle"
									src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/racc.png")%>>&nbsp;&nbsp;Cobranza</a></li>
					<%
						} else {
					%>
							<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica; text-decoration: underline; font-weight: bold;">
							<img class="programTitle" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/racc.png")%>>&nbsp;&nbsp;Cobranza</a></li>
					<%
						}
						if (!(program.equals("prrt"))) {
					%>
							<li class="menuItem"><a	style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif;"
								href="portal_start.jsp?program=prrt&viewHelp=0"><img
									class="programTitle"
									src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prrt.png")%>>&nbsp;&nbsp;Contratos</a></li>
					<%
						} else {
					%>
							<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; text-decoration: underline; font-weight: bold;"><img
									class="programTitle	"
									src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prrt.png")%>>&nbsp;&nbsp;Contratos</a></li>
					<%
						}
						if (!(program.equals("prty"))) {
					%>
							<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif;"
								href="portal_start.jsp?program=prty&viewHelp=0"><img
									class="programTitle"
									src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prty.png")%>>&nbsp;&nbsp;Inmuebles</a></li>
					<%
						} else {
					%>
							<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; text-decoration: underline; font-weight: bold;"><img
									class="programTitle"
									src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prty.png")%>>&nbsp;&nbsp;Inmuebles</a></li>
					<%
						}
					%>
					<li style="height: 55%"></li>
					<li onclick="logout()" class="menuItem"  ><a style="color: #CC1C2B; font-size: 10pt; font-family: Helvetica">
					<img class="programTitle" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/logout.png")%>>&nbsp;&nbsp;Salir</a></li>
			</ul>
		</nav>	
		<div class="topBar" >
			<%
				pmConn.doFetch(sqlLessors);
					pmConn2.doFetch(sqlLessorCount);
			%>
			<div style="text-align: left;vertical-align: middle;"><img id="imgMenu"  class="imgMenu"  src="./img/menu.png" style="padding-top: 10px;padding-left:5px "></div>
			<div style="text-align: center; margin: auto 1px auto 1px;">
				<img src="./img/logoWhite.png	" class="startLogo">
			</div>
			<div style="text-align: right; margin: 27px 1px auto 1px;">
			<%if(pmConn2.next()){ %>
			 				<form>
								<select  class="companyListBox" id ="lessors1" name="lessors1" onchange="changeLessor()" >
								<option  value="0"> Arrendador</option>
									<option value="<%=bmoCustomer.getId()%>"><%=bmoCustomer.getDisplayName().toString() %></option>
			    					<%while(pmConn.next()){ %>
			    						<option  value = "<%=pmConn.getString("cust_customerid")%>"><%=pmConn.getString("cust_displayname") %></option>
			    					<%} %>
			    				</select>
							</form>
			<%} %>

			</div>

		</div>
		
	
		<div class="topBarSecond">
			<%if(program.equals("racc")){ %>
				<div style="border-bottom: 4px solid #BEBDBE;color : white;font-family: helvetica;font-size:10pt;display: table-cell;top : 50%; left : 50%;transform : translate(-50%, -50>%); padding-top:8px;">COBRANZA</div>
			<%} else{%>
				<div style="padding-top:10px;"><a href="portal_start.jsp?program=racc&viewHelp=0"  style="color:white;text-decoration: none;font-family: helvetica;font-size:10pt;">COBRANZA</a></div>
			<%}	 %>
			<%if(program.equals("prrt")){ %>
				<div style="border-bottom: 4px solid #BEBDBE;color : white;font-family: helvetica;font-size:10pt;padding-top:10px;">CONTRATOS</div>
			<%} else{%>
				<div style="padding-top:10px;"><a href="portal_start.jsp?program=prrt&viewHelp=0"  style="color:white;text-decoration: none;font-family: helvetica;font-size:10pt;">CONTRATOS</a></div>
			<%} %>
			<%if(program.equals("prty")){ %>
				<div style="border-bottom: 4px solid #BEBDBE;color : white;font-family: helvetica;font-size:10pt;padding-top:10px;">INMUEBLES</div>
			<%} else{%>
				<div style="padding-top:10px;"><a href="portal_start.jsp?program=prty&viewHelp=0"  style="color:white;text-decoration: none;font-family: helvetica;font-size:10pt;">INMUEBLES</a></div>
			<%} %>
		</div>
		</header>
		<%if(program.equals("racc")){ %>
			<%String selestRaccountsStatus = request.getParameter("EstatusP");
			 String customer1 = request.getParameter("customer1");
			 String date1 = request.getParameter("date1");
			 String date2 = request.getParameter("date2");%>
			<form method="get" name="raccountsForm" style="width: 100%" id="raccountsForm" action="portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer1%>&date1=<%=date1%>&date2=<%=date2 %>" >

				<div class="divFilterRacc" id="loginTable">
			
					<div style="text-align: center;">
						<select id="selestRaccountsStatus" name="EstatusP"
							onchange="this.form.submit(onclick=aplicarRacc())"
							class="listFilterListBox">
							<option selected >Estatus Pago</option>
							<option>Pendiente</option>
							<option>Pago Total</option>
						</select>
					</div>
					<div style="text-align: right;">
						<input value="<%=monthNow + "-01" %>" type="date" id="date1"
							name="date1" onchange="this.form.submit(onclick=aplicarRacc())"
							class="listFilterListBox">
					</div>
					<div style="text-align: left;">
						<input value="<%=monthNow + "-" + dayFinish%>" type="date"
							id="date2" name="date2"
							onchange="this.form.submit(onclick=aplicarRacc())"
							class="listFilterListBox">
					</div>
					<div style="text-align: center;">
						<input type="search" id="customer1" placeholder="Buscar"
							name="customer1" onchange="this.form.submit(onclick=aplicarRacc())"
							class="listFilterListBox">
							<input class = "listPrintImage" align= "right" type="image"  src=<%=GwtUtil.getProperUrl(sFParams, "/icons/print.png") %> onclick="imprimirRacc()" title="Imprimir">	
					</div>
					<input type="hidden" name="lessors"  id="lessorsRacc" />
					<input type="hidden" name="printRacc"  id="printRacc" />
				</div>
		</form	>
		<div class="divConten">
			<iframe  name="raccountsIframe" id="raccountsIframe" border="0" frameborder="0" src="portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer1%>&date1=<%=date1 %>&date2=<%=date2 %>&printRacc=0" height="83%" width="100%" ></iframe>		
		</div>
		<div class="baseBar">
			<div style="padding-top:10px;">
			
			® INADICO <%=date.get(Calendar.YEAR) %>
			</div>		
		</div>
	</div>
	<%} else if(program.equals("prrt")){%>	 
	<%String estatusPropertiesRental = request.getParameter("estatusPropertiesRental");
	String customer = request.getParameter("customer");
	custLessor = request.getParameter("custLessor");%>
		 <form method="get" id="propertiesrentalForm" action="portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&custLessor=<%=custLessor%>" >
		 	<div class="divFilterPrty" id="loginTable">
		 		<div style="text-align: right;">
		 			<select  class="listFilterListBox" id="propertiesrental" name="estatusPropertiesRental" onchange="this.form.submit(onclick=aplicarPrrt())">
						<option>Estatus</option>
						<option>Revisi&oacute;n</option>
						<option selected="selected">Autorizado</option>	
						<option>Finalizado</option>	
						<option>Cancelado</option>				
					</select>		
		 		
		 		</div>
		 		<div>
		 			<input type="search" id="customer" placeholder="Buscar"	name="customer" onchange="this.form.submit(onclick=aplicarPrrt())" class="listFilterListBox">	
		 			<input  class = "listPrintImage" align= "right" type="image" src=<%=GwtUtil.getProperUrl(sFParams, "/icons/print.png") %> onclick="imprimirPrrt()"  title="Imprimir">		 		
		 		</div>
		 		<input type="hidden" name="lessors"  id="lessorsPrrt" />
		 		<input type="hidden" name="printPrrt"  id="printPrrt" />
		 	</div>
		 </form>
		 <div class="divConten">
		 	<iframe  name="propertiesRentalIframe" border="0" frameborder="0" id="propertiesRentalIframe" src="portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&custLessor=<%=custLessor%>&printPrrt=0" height="83%" width="100%"></iframe>	
		 </div>
		 <div class="baseBar">
			<div style="padding-top:10px;">
			
			® INADICO <%=date.get(Calendar.YEAR) %>
			</div>		
		</div>	
	<%} else if(program.equals("prty")){%>	
	<%String statusProperties = request.getParameter("statusProperties");%>
		<form method="get" id="propertiesForm" action="portal_properties.jsp?statusProperties=<%=statusProperties%>"  >
			<div class="divFilterPrrt" id="loginTable">
				<div>
					<select id="properties" name="statusProperties" onchange="this.form.submit(onclick=aplicarPrty())" class="listFilterListBox" align= "right">
							<option>Disponibilidad</option>
							<option>Disponible</option>
							<option>Ocupado</option>							
 					</select>		
 				<input class = "listPrintImage" align= "right" type="image"  src=<%=GwtUtil.getProperUrl(sFParams, "/icons/print.png") %>  onclick="imprimirPrty()" title="Imprimir">						 					
 						
				</div>
			</div>		
				<input type="hidden" name="lessors"  id="lessorsPrty" />
				<input type="hidden" name="printPrty"  id="printPrty" />	
		</form>
		 <div class="divConten">
		 	<iframe name="propertiesIframe" frameborder="0"  id="propertiesIframe" src="portal_properties.jsp?statusProperties=<%=statusProperties%>&printPrty=0" height="83%" width="100%"></iframe>
		 </div>
		 <div class="baseBar">
			<div style="padding-top:10px;">
			
			® INADICO <%=date.get(Calendar.YEAR) %>
			</div>		
		</div>	
	<%} %>

<!-- <script src="http://code.jquery.com/jquery-latest.js"></script> -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
	<script src="./css/menu.js"></script>
	
</body>

</html>
<%pmConn.close();
  pmConn2.close();
}
catch(Exception e){
	response.sendRedirect("portal_login.jsp");
}%>

