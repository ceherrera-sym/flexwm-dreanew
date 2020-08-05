<!--  
/**
 * @author Cesar Herrera Hernández
 */ -->

<%@page import="com.symgae.shared.GwtUtil"%>
<%@page import="com.symgae.client.ui.UiDetail"%>
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



<%try{
	String appTitle = "Inmuebles";
	String defaultCss = "symgae.css";
	String gwtCss = "gwt_standard.css";
	String fwmCss = "flexwm.css",height = "";
	boolean isMobile = false;
	SFParams sFParams = new SFParams();
	String sql = "",orLessor = "",sqllessors = "",filter = "";
	String sql2 = "";
	String cust_id = (String)session.getAttribute("Id");
	int page1 = 1,custLessor = 0,print = 0;
	int counterQuery = 0;
	int counterFull = 0;
	int pages = 0;
	int pagina = 1;
	
	 if(Integer.parseInt(cust_id) <= 0)
		 response.sendRedirect("portal_login.jsp") ;
	 
	 String selestRaccountsStatus = request.getParameter("statusProperties");
		
    String where = "";
    try{
		
		custLessor = Integer.parseInt(request.getParameter("lessors"));
	}catch (Exception e){
		custLessor = 0;
	}
    if(!request.getParameter("printPrty").equals("")){
		print = Integer.parseInt(request.getParameter("printPrty"));
	}
    
	LoginInfo loginInfo = new LoginInfo();
	loginInfo.setLoggedIn(true);
	loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
	SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
	SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
	int id = Integer.parseInt(cust_id);
	
	PmConn pmConn = new PmConn(sFParams);	
	PmConn pmConnPropertytax = new PmConn(sFParams);
	PmConn pmConnCounter = new PmConn(sFParams);
	PmConn pmConnLessors = new PmConn(sFParams);	
	
	
	BmoProperty bmoProperty = new BmoProperty();
	PmProperty pmProperty = new PmProperty(sFParams);
	
	if(selestRaccountsStatus.equalsIgnoreCase("< Disponibilidad >")){
		where = "";
	}	
	if(selestRaccountsStatus.equalsIgnoreCase("Disponible")){
		where += "AND prty_available = 1";
		filter = "Disponibilidad: Disponible";
	}
	if(selestRaccountsStatus.equalsIgnoreCase("Ocupado")){
		where += "AND prty_available = 0";
		filter = "Disponibilidad: Ocupado";
	}if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
		isMobile = true;
	}

	
	counterQuery = (page1 * sFParams.getListPageSize()) - sFParams.getListPageSize();
	pmConnCounter.open();
	

	pmConn.open();
	pmConnLessors.open();
	
	sqllessors = "SELECT cust_customerid FROM customers WHERE cust_customercategory = '"+BmoCustomer.CATEGORY_LESSEE+"' AND cust_lessormasterid = "+cust_id;
	pmConnLessors.doFetch(sqllessors);
	while (pmConnLessors.next()){
		orLessor += " OR prty_customerid = "+ pmConnLessors.getInt("cust_customerid");
		
	}
	
	if (!(custLessor > 0)){
	
		sql = "SELECT * FROM properties LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = " + cust_id
		  + orLessor +" )" + where + " ORDER BY prty_customerid,prty_propertyid ASC ";
		if(!(print > 0)){
			sql+="LIMIT " + (counterQuery) + "," + sFParams.getListPageSize();
		}
		sql2 = "SELECT COUNT(prty_propertyid) AS C FROM properties LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = " + cust_id
			  + orLessor +" )" + where + " ORDER BY prty_propertyid ASC";
	}else {
		sql = "SELECT * FROM properties LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = " + custLessor
			  + " )" + where + " ORDER BY prty_propertyid ASC  " ;
		if(!(print > 0)){
			sql+="LIMIT " + (counterQuery) + "," + sFParams.getListPageSize();
		}
			
		sql2 = "SELECT COUNT(prty_propertyid) AS C FROM properties LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = " + custLessor
			  + " )" + where + " ORDER BY prty_propertyid ASC";
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
<%%>
<title>::: <%= appTitle %> :::
</title>
                  
<script type="text/javascript" language="javascript"
	src="flexwm/flexwm.nocache.js"></script>
</head>

<body class="default">
<div style="position: absolute; left: 0px; top: 0px; right: 0px; bottom: 0px;height: 100%">
	<%if (print > 0){ %>
		<br>
    	<table border="0" cellspacing="0" cellpading="0" width="99%">
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
		<%height = "90%";
		} else{
			height = "100%";
		}%>
		<div style="height:<%=height %> ;width: 100%;text-align: center;">			
			<%PmCustomer pmCustomer = new PmCustomer(sFParams);  
			  BmoCustomer bmoCustomer = new BmoCustomer(); 
			  int i = counterQuery +1;
			   pmConnPropertytax.open();
			    while(pmConn.next()){
			    	bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn.getInt("prty_customerid"));
			    	sql = "SELECT  * FROM propertytax" +
			    	 " WHERE prtx_propertyid =" + pmConn.getInt("prty_propertyid") +
			    	 " ORDER BY prtx_propertytaxid DESC limit 2";
			    	  pmConnPropertytax.doFetch(sql);
			    	  String propertytax = "";
			    	   while(pmConnPropertytax.next()){
			    		   if (propertytax.equals(""))
			    			   propertytax += pmConnPropertytax.getString("prtx_accountno");
			    		   else
			    			   propertytax += ", "+pmConnPropertytax.getString("prtx_accountno");
			    	   }%>
			    	   <div style="text-align: center;font-family:Helvetica;width: 100%;">
			    	   		<div class="divTable">
			    	   			<table   style="width: 100%">
			    	   				<tr >
			    	   					<td colspan="5" class="titleContract" align="center">
			    	   						<a style="color: #bfbfbf;" target='_blank' href="portal_propertydetail.jsp?property=<%=pmConn.getInt("prty_propertyid") %>"><%=HtmlUtil.stringToHtml(pmConn.getString("prty_description"))%></a> 
			    	   					</td>
			    	   				</tr>
			    	   				<tr>
			    	   					<td class="textInfoTitle" style="text-align: right;width: 20%;">
			    	   						ARRENDADOR: 
			    	   					</td>
			    	   					<td class="textInfo" style="text-align: left;width: 20%;" >
			    	   						<%=bmoCustomer.getDisplayName().toString() %>
			    	   					</td>
			    	   					<td class="textInfoTitle"style="text-align: right;width: 20%;">
			    	   						RECIBO PREDIAL: 
			    	   					</td>
			    	   					<td class="textInfo" style="text-align: left;width: 20%;">
			    	   						<%if(pmConn.getString("properties","prty_propertyreceipt").length() > 0){
	    										String blobkey = pmConn.getString("prty_propertyreceipt");	
    											String blobKeyParseReceip = HtmlUtil.getFileViewURL(sFParams, blobkey);%>	    
	    											<a  target='_blank' href="<%=blobKeyParseReceip %>">
	    												<img  src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/file-search.png") %>  width="20px" height="20px">
	    											</a>	   								
	   										<%} %>
			    	   					</td>
			    	   					<td rowspan="3" style="width: 20%;">			    	   						
										<%if(pmConn.getString("properties", "prty_facade").length() > 0){
			    			
 			    							String blobkey = pmConn.getString("prty_facade");
 			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	 
			    							<a target='_blank' href="<%=blobKeyParseFacade %>" ><img  class="imageProperty" src="<%=blobKeyParseFacade %>" >
			       							</a>
			   							<%}%>	
			    	   					</td>
			    	   				</tr>
			    	   				<tr>
			    	   					<td class="textInfoTitle" style="text-align: right;">
			    	   						# PREDIAL:
			    	   					</td>
			    	   					<td class="textInfo" style="text-align: left;">
			    	   						<%=propertytax%>
			    	   					</td>
			    	   					<td class="textInfoTitle" style="text-align: right;">
			    	   						ESCRITURA:
			    	   					</td>
			    	   					<td class="textInfo" style="text-align: left;">
			    	   						
										 <%if(pmConn.getString("properties", "prty_propertytitle").length() > 0){
	    									String blobkey = pmConn.getString("prty_propertytitle");
	    									String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    	
	    									<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
    											<img  src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/file-search.png") %>   width="20px" height="20px">
    										</a>
	    	
	   									<%  } %>
			    	   					</td>
			    	   					<td>			    	   						
										
			    	   					</td>
			    	   				</tr>
			    	   				<tr>
			    	   					<td class="textInfoTitle" style="text-align: right;">
			    	   						UBICACION:
			    	   					</td>
			    	   					<td class="textInfo" style="text-align: left;">			    	   						
											<%if(pmConn.getString("properties","prty_coordinates").length() > 0){%>		   
		  							 			<a target='_blank' href="https://www.google.com/maps/place/<%=pmConn.getString("prty_coordinates") %>">
													<img src="./icons/gps.png" width="20px" height="20px">
												</a>
	 							 			<% } %>
			    	   					</td>
			    	   					<td class="textInfoTitle" style="text-align: right;">
			    	   						DISPONIBLE:
			    	   					</td>
			    	   					<td class="textInfo" style="text-align: left;">	   	   						
										 	<%if(pmConn.getBoolean("prty_available")){%>
	      										<img  src= "./icons/true.png" width="20px" height="20px">	      		     			    	
	     	 								<%} else{%>
	    	 									<img  src="./icons/false.png" width="18px" height="18px">    	 
	    	  	   		 					<% }%>
			    	   					</td>
			    	   					<td>			    	   						
										
			    	   					</td>
			    	   				</tr>
			    	   			</table>

			    	   		</div>
			    	   </div>
			    <%} %>
		</div>
</div>

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
pmConnPropertytax.close();
pmConnCounter.close();
pmConnLessors.close();
}catch(Exception e){
	//response.sendRedirect("portal_login.jsp");	

}

 %>
