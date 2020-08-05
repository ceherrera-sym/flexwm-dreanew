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
<%@ page import="com.google.gwt.user.client.ui.ListBox"%>



<%
	try {%>
	<%@include file="./inc_params.jsp"%>	
<%		Calendar date = new GregorianCalendar();
		String year = Integer.toString(date.get(Calendar.YEAR));
		String month = Integer.toString(date.get(Calendar.MONTH) + 1);
		if ((Integer.parseInt(month)) < 10)
			month = "0" + month;
		String day = Integer.toString(date.get(Calendar.DAY_OF_MONTH));

		String monthNow = year + "-" + month;
		String dayFinish = Integer.toString(date.getActualMaximum(Calendar.DAY_OF_MONTH));
		String dayInit = Integer.toString(date.getActualMinimum(Calendar.DAY_OF_MONTH));

		String appTitle = "Portal INADICO";
		String defaultCss = "symgae.css";
		String gwtCss = "gwt_standard.css";
		String fwmCss = "flexwm.css", custLessor = "";
		boolean isMobile = false;
		String program = "", image = "", styleBotom = "", programTitle = "", classTitle = "",classstyleDate = "";
		
		String sql = "";
		String cust_id = (String) session.getAttribute("Id");
		if (Integer.parseInt(cust_id) <= 0)
			response.sendRedirect("portal_login.jsp");

		if (!request.getParameter("program").equals(""))
			program = request.getParameter("program");

		
		int id = Integer.parseInt(cust_id), print = 0,viewHelp = 0;

		PmConn pmConn = new PmConn(sFParams);
		PmConn pmConn2 = new PmConn(sFParams);
		PmCustomer pmCustomer = new PmCustomer(sFParams);
		BmoCustomer bmoCustomer = new BmoCustomer();
		bmoCustomer = (BmoCustomer) pmCustomer.get(Integer.parseInt(cust_id));

		BmoProperty bmoProperty = new BmoProperty();
		PmProperty pmProperty = new PmProperty(sFParams);
		PmPropertyRental pmPropertyRental = new PmPropertyRental(sFParams);
		BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();
		PmRaccount pmRaccount = new PmRaccount(sFParams);
		BmoRaccount bmoRaccount = new BmoRaccount();
		viewHelp = Integer.parseInt(request.getParameter("viewHelp"));
		if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
			isMobile = true;
		}
		pmConn.open();
		pmConn2.open();
		String sqlLessors = "SELECT * FROM customers WHERE cust_customercategory = '"
				+ BmoCustomer.CATEGORY_LESSEE + "' AND cust_lessormasterid = " + cust_id;
		String sqlLessorCount = "SELECT cust_customerid FROM customers WHERE cust_customercategory = '"
				+ BmoCustomer.CATEGORY_LESSEE + "' AND cust_lessormasterid = " + cust_id;;

// 				String userAgent = request.getHeader("user-agent");
// 				  if (userAgent.matches(".*Android.*"))
// 				  {
// 				    classstyleDate = "listFilterListBox";
// 				  }else{
// 					  classstyleDate = "listFilterListBoxDate";
// 				  }
%>
<script type="text/javascript">



	
	//    aplicación de filtros Propertiesrental

	function aplicarPrrt() {
		document.getElementById("propertiesrentalForm").setAttribute("target",
				"propertiesRentalIframe");
		document.getElementById("printPrrt").setAttribute("value", "0");
	}
	//    aplicación de filtros	Raccounts
	function aplicarRacc() {
		document.getElementById("raccountsForm").setAttribute("target",
				"raccountsIframe");
		document.getElementById("printRacc").setAttribute("value", "0");
	}

	//    aplicación de filtros	properties 
	function aplicarPrty() {
		document.getElementById("propertiesForm").setAttribute("target",
				"propertiesIframe");
		document.getElementById("printPrty").setAttribute("value", "0");
	}
	// Filtro de arrendador 
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
<link href="./css/alertify.min.css" rel="stylesheet"/>
<script src="./css/alertify.min.js"></script>
<link type="text/css" rel="stylesheet" href="./css/<%=defaultCss%>">

<%	//Etiqutas Meta segun dispositivo
String userAgent = request.getHeader("user-agent");
				  if (userAgent.matches(".*Android.*"))	  {%>
					  
					   <meta name="viewport"
	content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0">
				   <% classstyleDate = "listFilterListBox";
				  }else{%>

<meta name="viewport" content="width = device-width">
<!-- <meta name="viewport" content="initial-scale = 1.0"> -->
<meta name="viewport" content="initial-scale = 1.0, user-scalable = yes">
<meta name="format-detection" content="telephone=no"> 
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">

					<%  classstyleDate = "listFilterListBoxDate";
				  }
				  %>
 

 


<title>::: <%=appTitle%> :::
</title>



</head>
<body onload="inicia(),main(<%=viewHelp %>)" style="background: #F1F3F4;">
	
	<div id="divIndex" style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px; margin: top;">
  	
		<header >
		<nav class="menu_down" id="menuDown" style="z-index: 1;"></nav>
			<nav id="menu" style="position: absolute;z-index: 2;height: 100%" >
				<ul>
					<li class = "menuItem" height="10%">

								<img src="img/logo.png" width="160px" height="30px" >


					</li >
					<li class="menuCust " style="height: 5%;margin-left: 8%;margin-right: 10%;font-family: Helvetica;margin-top: 1%">
						<%=bmoCustomer.getDisplayName().toString()%>
					</li>
					<li style="height: 5%;margin-left: 8%;margin-right: 10%">
						<%
							pmConn.doFetch(sqlLessors);
						//ejecutar para saber di hay arrendadores por debajo de el logeado
								pmConn2.doFetch(sqlLessorCount);
						%> <%//llenar combo de arrendatarios 
 							if (pmConn2.next()) {
 %>
						
								<form>
									<select class="companyListBox " id="lessors1" name="lessors1"
										onchange="changeLessor()"
										style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;height: 100%">
										<option value="0"> &nbsp;&nbsp;Arrendador</option>
										<option value="<%=bmoCustomer.getId()%>"><%=bmoCustomer.getDisplayName().toString()%></option>
										<%
										while (pmConn.next()) {
											%>
											<option value="<%=pmConn.getString("cust_customerid")%>"><%=pmConn.getString("cust_displayname")%></option>
											<%
										}
									%>
									</select>
								</form> 
									<%
 							}
 %>
					</li>

					<%
						//Muestar afiltros e información segun programa seleccionado
							if (!(program.equals("racc"))) {
					%>
					<li class="menuItem"><a style="color: white; font-size: 10pt; font-family:  Helvetica;"
						href="portal_start_mobile.jsp?program=racc&viewHelp=0"><img
							class="programTitle"
							src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/racc.png")%>>Cobranza</a></li>
					<%
						} else {
					%>
					<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica; text-decoration: underline; font-weight: bold;">
					<img class="programTitleSelected" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/racc.png")%>>Cobranza</a></li>
					<%
						}
					if (!(program.equals("prrt"))) {
						%>
						<li class="menuItem"><a	style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif;"
							href="portal_start_mobile.jsp?program=prrt&viewHelp=0"><img
								class="programTitle"
								src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prrt.png")%>>Contratos</a></li>
						<%
							} else {
						%>
						<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; text-decoration: underline; font-weight: bold;"><img
								class="programTitleSelected"
								src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prrt.png")%>>Contratos</a></li>
						<%
							}
							if (!(program.equals("prty"))) {
					%>
					<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif;"
						href="portal_start_mobile.jsp?program=prty&viewHelp=0"><img
							class="programTitle"
							src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prty.png")%>>Inmuebles</a></li>
					<%
						} else {
					%>
					<li class="menuItem"><a style="color: white; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif; text-decoration: underline; font-weight: bold;"><img
							class="programTitleSelected"
							src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/prty.png")%>>Inmuebles</a></li>
					<%
						}
					%>
					<li style="height: 55%"></li>
					<li onclick="logout()" style="height: 5%;margin-left: 8%"><a style="color: #CC1C2B; font-size: 10pt; font-family: Helvetica Neue, Helvetica, Arial, sans-serif;"><img
							class="programTitle"
							src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/img/logout.png")%>>Salir</a></li>
				</ul>
				

			</nav>
			<div class="menu_bar">				
				<a class="bt-menu"><img id="imgMenu"  class="imgMenu" src="./img/menu.png"><span
					class="programTitleLabel" style="font-family: Helvetica">
					<img  src="img/logo.png" width="200px" height="35	px" style="margin-right: 8%">
					<img  class = "helpImg" id="helpImg" src="./icons/help.png" onclick="help(1)">
					<img  class = "btn-float-close" id="btn-close" src="./img/close.png" onclick="help(1)">
				
					<p id="menuLabel" class = "helpLabel">
					</p></span>
				</a>
			</div>
			
			<div>
				<table id="sectionTable" class="titlesTable" >
					<tr height="1px" align="center" >
					</tr>
					<tr align="center" >					
						<%//segun el programa en el que se encuentra mostrar link o solo etiqueta
							if (program.equals("racc")) {
						%>
						<td style="border-bottom: 2px solid white;color : white;font-family: helvetica;font-size:9pt;">COBRANZA</td>
						<%
							} else {
						%>
						<td><a href="portal_start_mobile.jsp?program=racc&viewHelp=0"  style="color:white;text-decoration: none;font-family: helvetica;font-size:9pt">COBRANZA</a></td>
						<%
							}
						%>
							<%
							if (program.equals("prrt")) {
						%>
						<td  style="border-bottom: 2px solid white;color : white;vertical-align: top;font-family: helvetica;font-size:9pt">CONTRATOS</td>
						<%
							} else {
						%>
						<td><a href="portal_start_mobile.jsp?program=prrt&viewHelp=0"  style="color:white;text-decoration: none;font-family: helvetica;font-size:9pt">CONTRATOS</a></td>
						<%
							}
						%>
						<%
							if (program.equals("prty")) {
						%>
						<td  style="border-bottom: 2px solid white;color : white;font-family: helvetica;font-size:9pt">INMUEBLES</td>
						<%
							} else {
						%>
						<td><a href="portal_start_mobile.jsp?program=prty&viewHelp=0"  style="color:white;text-decoration: none;font-family: helvetica;font-size:9pt">INMUEBLES</a></td>
						<%
							}
						%>
					</tr>
<!-- 					<tr height="1px" align="center" > -->
<!-- 					</tr> -->
				</table>
			</div>
			


		</header>
		<!-- 	Termina Menu -->

		<table class="iframeTable" >

			<%
				if (program.equals("prrt")) {
			%>
			<tr height="3%">
				<td>
					<table width="100%">
						<%
							String estatusPropertiesRental = request.getParameter("estatusPropertiesRental");
									String customer = request.getParameter("customer");
									custLessor = request.getParameter("custLessor");
						%>
						<form method="get" id="propertiesrentalForm"
							action="portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&custLessor=<%=custLessor%>">
							<tr>
								<td>
									<table id= "filters" width="100%">
										<tr >
											<td align="right"><select class="listFilter"
												style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;"
												id="propertiesrental" name="estatusPropertiesRental"
												onchange="this.form.submit(onclick=aplicarPrrt())">
													<option>Estatus</option>
													<option>Revisi&oacute;n</option>
													<option selected="selected">Autorizado</option>
													<option>Finalizado</option>
													<option>Cancelado</option>
											</select> 
											<input type="search" id="customer" placeholder="Buscar"
												name="customer"
												onchange="this.form.submit(onclick=aplicarPrrt())"
												class="listFilterListBox text"
												style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;">
											<input type="hidden" name="lessors" id="lessorsPrrt" /> <input
												type="hidden" name="printPrrt" id="printPrrt" /> <input
												type="hidden" name="viewMore" value="0" /></td>
										</tr>
									</table>

								</td>
							</tr>
						</form>
					</table>
				</td>
			</tr>
			<tr height="100%">				
				<td id="frame" colspan="2" height="100%" width="100%"  >	
					<div id="divFrame" style="align-items: stretch; height:100%; width:100%;overflow:scroll; -webkit-overflow-scrolling:touch;z-index: 10" > 		
						<iframe class="frame" name="propertiesRentalIframe" id="propertiesRentalIframe" 
						src="portal_propertiesrental.jsp?estatusPropertiesRental=<%=estatusPropertiesRental%>&customer=<%=customer%>&custLessor=<%=custLessor%>&printPrrt=0&viewMore=0" 
						height="100%" width="100%" >
						</iframe>
					</div>
				</td>				
			</tr>
			<%
				} else if (program.equals("racc")) {
			%>
			<tr height="3%">
				<td>
					<table width="100%">
						<%
							String selestRaccountsStatus = request.getParameter("EstatusP");
									String customer1 = request.getParameter("customer1");
									String date1 = request.getParameter("date1");
									String date2 = request.getParameter("date2");
						%>
						<form method="get" id="raccountsForm"
							action="portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer1%>&date1=<%=date1%>&date2=<%=date2%>">
							<tr>
								<td>
									<table id= "filters" width="100%">

										<tr>
											<td align="center" >
												<select id="selestRaccountsStatus"
												name="EstatusP"
												onchange="this.form.submit(onclick=aplicarRacc())"
												class="listFilterListBox"
												style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;">
													<option>Estatus Pago</option>
													<option>Pendiente</option>
													<option>Pago Total</option>
											</select> 
											<input type="search" id="customer1" placeholder="Buscar"
												name="customer1"
												onchange="this.form.submit(onclick=aplicarRacc())"
												class="listFilterListBox"
												style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;padding-bottom: 1px">
											<input value="<%=monthNow + "-01"%>" type="date" id="date1"
												name="date1"
												onchange="this.form.submit(onclick=aplicarRacc())"
												class="<%=classstyleDate %>"
												style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;padding-bottom: 1px">
											<input value="<%=monthNow + "-" + dayFinish%>" type="date"
												id="date2" name="date2"
												onchange="this.form.submit(onclick=aplicarRacc())"
												class="<%=classstyleDate %>"
												style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;">
											<input type="hidden" name="lessors" id="lessorsRacc" /> <input
												type="hidden" name="printRacc" id="printRacc" /> <input
												type="hidden" name="viewMore" value="0" />
										</td> 
										</tr>
									</table>
								</td>
							</tr>
						</form>
					</table>
				</td>
			</tr>
			<tr height="100%">				
				<td id="frame" colspan="3" height="100%" width="100%" >
						<div id="divFrame" style="align-items: stretch;height:100%; width:100%;overflow:scroll; -webkit-overflow-scrolling:touch;position: 0;  -webkit-transition: -webkit-transform 3s ease-out;" > 
						<iframe class="frame" name="raccountsIframe"
						id="raccountsIframe"
						src="portal_raccounts.jsp?EstatusP=<%=selestRaccountsStatus%>&customer1=<%=customer1%>&date1=<%=date1%>&date2=<%=date2%>&printRacc=0&viewMore=0"
						height="100%" width="100%"></iframe>
					</div>
				</td>
			</tr>
			<%
				} else if (program.equals("prty")) {
			%>
			<tr height="3%">
				<td>
					<table id= "filters" width="100%">
						<%
							String statusProperties = request.getParameter("statusProperties");
						%>
						<form method="get" id="propertiesForm"
							action="portal_properties.jsp?statusProperties=<%=statusProperties%>">
							<tr>
								<td align="right"><select id="properties"
									name="statusProperties"
									onchange="this.form.submit(onclick=aplicarPrty())"
									class="listFilter" align="right"
									style="font-family: Helvetica Neue, Helvetica, Arial, sans-serif;">
										<option>Disponibilidad</option>
										<option>Disponible</option>
										<option>Ocupado</option>
								</select> <input type="hidden" name="lessors" id="lessorsPrty" /> <input
									type="hidden" name="printPrty" id="printPrty" /> <input
									type="hidden" name="property" id="printPrty" value="0" /> <input
									type="hidden" name="viewMore" value="0" /></td>
							</tr>
						</form>
					</table>
				</td>
			</tr>
			<tr height="100%">				
				<td id="frame" colspan="5" height="100%" width="100%" >
					<div id="divFrame" style="align-items: stretch;height:100%; width:100%;overflow:scroll; -webkit-overflow-scrolling:touch;position: static;" >  
						<iframe class="frame" name="propertiesIframe"
						id="propertiesIframe"
						src="portal_properties.jsp?statusProperties=<%=statusProperties%>&printPrty=0&property=0&viewMore=0"
						height="100%" width="100%"></iframe>
					</div>
				</td>
			</tr>
			<%
				}
			%>

		</table>

	
	<!-- 	efecto y acción de el menu -->
	<script src="./css/jquery-latest.js"></script>
	<script src="./css/jquery.min.js"></script>
	<script src="./css/menu.js"></script>
	
	</div>
	<div id="btn-left">
	   <img src="./img/retro.png" class="btn-float-left"> 
    </div>
    <div id="btn-right">
	   <img src="./img/next.png" class="btn-float-right"> 
    </div> 

</body>

</html>
<%
	pmConn.close();
		pmConn2.close();
	} catch (Exception e) {
		response.sendRedirect("portal_login.jsp");
	}
%>

