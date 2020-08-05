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






	<%try {%>
	<%@include file="./inc_params.jsp"%>	
<%
			String appTitle = "Inmuebles";
			String defaultCss = "symgae.css";
			String gwtCss = "gwt_standard.css";
			String fwmCss = "flexwm.css";			
			boolean isMobile = false;
			
			String sql = "", orLessor = "", sqllessors = "", filter = "",customer = "";
			String sql2 = "";
			String cust_id = (String) session.getAttribute("Id");
			String tabDiv = "25% 25% 25% 25%";
			int page1 = 1, custLessor = 0, print = 0, pageSize = 10;
			int counterQuery = 0;
			int counterFull = 0;
			int pages = 0;
			int pagina = 1;

			if (Integer.parseInt(cust_id) <= 0)
				response.sendRedirect("portal_login.jsp");
			
			customer = request.getParameter("customer");
			String selestRaccountsStatus = request.getParameter("statusProperties");

			String where = "";
			 
			try {

				custLessor = Integer.parseInt(request.getParameter("lessors"));
			} catch (Exception e) {
				custLessor = 0;
			}
			try{
				print = Integer.parseInt(request.getParameter("printPrty"));
			}catch (Exception e) {
				print = 0;
			}
			
			
			int id = Integer.parseInt(cust_id);

			PmConn pmConn = new PmConn(sFParams);
			PmConn pmConnPropertytax = new PmConn(sFParams);
			PmConn pmConnCounter = new PmConn(sFParams);
			PmConn pmConnLessors = new PmConn(sFParams);

			BmoProperty bmoProperty = new BmoProperty();
			PmProperty pmProperty = new PmProperty(sFParams);

			if (selestRaccountsStatus.equalsIgnoreCase("Disponibilidad")) {
				where = "";
			}
			if (selestRaccountsStatus.equalsIgnoreCase("Disponible")) {
				where += "AND prty_available = 1";
				filter = "Disponibilidad: Disponible";
			}
			if (selestRaccountsStatus.equalsIgnoreCase("Ocupado")) {
				where += "AND prty_available = 0";
				filter = "Disponibilidad: Ocupado";
			}
			if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
				isMobile = true;
			}
		

			counterQuery = (page1 * pageSize) - pageSize;
			pmConnCounter.open();

			pmConn.open();
			pmConnLessors.open();

			sqllessors = "SELECT cust_customerid FROM customers WHERE cust_customercategory = '"
					+ BmoCustomer.CATEGORY_LESSEE + "' AND cust_lessormasterid = " + cust_id;
			pmConnLessors.doFetch(sqllessors);
			while (pmConnLessors.next()) {
				orLessor += " OR prty_customerid = " + pmConnLessors.getInt("cust_customerid");

			}

			if (!(custLessor > 0)) {

				sql = "SELECT * FROM properties LEFT JOIN cities ON (prty_cityid = city_cityid) LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = "
						+ cust_id + orLessor + " )" + where + " ORDER BY prty_customerid,prty_propertyid ASC ";
				if (!(print > 0)) {
					sql += "LIMIT " + (counterQuery) + "," + pageSize;
				}
				sql2 = "SELECT COUNT(prty_propertyid) AS C FROM properties LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = "
						+ cust_id + orLessor + " )" + where + " ORDER BY prty_propertyid ASC";
			} else {
				sql = "SELECT * FROM properties LEFT JOIN cities ON (prty_cityid = city_cityid) LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = "
						+ custLessor + " )" + where + " ORDER BY prty_propertyid ASC  ";
				if (!(print > 0)) {
					sql += "LIMIT " + (counterQuery) + "," + pageSize;
				}

				sql2 = "SELECT COUNT(prty_propertyid) AS C FROM properties LEFT JOIN customers ON properties.prty_customerid = customers.cust_customerid where ( cust_customerid = "
						+ custLessor + " )" + where + " ORDER BY prty_propertyid ASC";
			}

			pmConn.doFetch(sql);
			pmConnCounter.doFetch(sql2);
%>


<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="./css/<%=defaultCss%>">


 <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
   <script src="//code.jquery.com/jquery-1.12.0.min.js"></script>
   <script src="./css/bootstrap.min.js"></script>  
<!--     <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script> -->
   <script>
   //para mostrar el detalle de el Inmueble
      function showDetail(id)
      {    	
         $("#mostrarmodal"+id).modal("show");
      }
      function showImg(id)
      {    	
         $("#mostrarmodalImg"+id).modal("show");
      }
      $(document).ready(function(){
    	 	$('body').hide(); 
    		 $('body').fadeIn(1000); 
    		 document.getElementById("table").style.display = "block";
    	 }); 
    </script>
<%-- <% --%>
<!-- // 	if (isMobile) { -->
<%-- %> --%>
<!-- <meta name="viewport" -->
<!-- 	content="width=device-width, user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0"> -->

<%-- <% --%>
<!-- // 	} -->
<%-- %> --%>
<meta name="viewport"
	content="width=device-width, user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0">
<%%>
<title>::: <%=appTitle%> :::
</title>

</head>

<body id="table"   onload="iniciaFrame(3)" style="display: none;">	
	 
	<table  width="100%">

	<%PmCustomer pmCustomer = new PmCustomer(sFParams);
	  BmoCustomer bmoCustomer = new BmoCustomer();
	  int i = counterQuery + 1;
	  pmConnPropertytax.open();
	  if(pmConn.next()){
		pmConn.doFetch();
	  	while (pmConn.next()) {
				bmoCustomer = (BmoCustomer) pmCustomer.get(pmConn.getInt("prty_customerid"));
				sql = "SELECT  * FROM propertytax" + " WHERE prtx_propertyid ="
						+ pmConn.getInt("prty_propertyid") + " ORDER BY prtx_propertytaxid DESC limit 2";
				pmConnPropertytax.doFetch(sql);
				String propertytax = "";
				while (pmConnPropertytax.next()) {
					if (propertytax.equals(""))
						propertytax += pmConnPropertytax.getString("prtx_accountno");
					else
					propertytax += ", " + pmConnPropertytax.getString("prtx_accountno");
				}
			%>
			<tr height="2px">
			</tr>			
			<tr align="center"  >
				<td >
					<table   class = "imgTable "  width="90%" style="font-family: Helvetica Neue,Helvetica,Arial,sans-serif;"  >

						<tr>
							<td colspan="5" class = "titleContractLink">
								<a  onclick="showDetail(<%=pmConn.getInt("prty_propertyid") %>)" style="color: #120180;"><%=HtmlUtil.stringToHtml(pmConn.getString("prty_description"))%></a>
							</td>
						</tr>	
						<tr>
							<td class = "textInfoTitle" wi>
								Arrendador:
							</td>
							<td class="textInfo" colspan="4">
								<%=bmoCustomer.getDisplayName().toString() %>
							</td>
						</tr>
						<tr>
							<td class = "textInfoTitle">
								# Predial:
							</td>
							<td class="textInfo">							
								<%=propertytax%>
							</td>
							
							<td colspan="2" rowspan="6" align="right">
								<%if(pmConn.getString("properties", "prty_facade").length() > 0){
			    			
			    				String blobkey = pmConn.getString("prty_facade");
			    				String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    				<a onclick="showImg(<%=pmConn.getInt("prty_propertyid") %>)"  ><img  class="imageProperty" src="<%=blobKeyParseFacade %>" >
			       				</a>
			   					<%}else{%>		
			   						<span class="imageProperty"	></span>
			   					<%} %>	   			
			   				</td>		
			   				<td>&nbsp;</td>	
						
						</tr>
						<tr>
							<td class = "textInfoTitle">
								Ubicación:
							</td>
							<td colspan="3">
								<%if(pmConn.getString("properties","prty_coordinates").length() > 0){%>		   
		  							 <a target='_blank' href="https://www.google.com/maps/place/<%=pmConn.getString("prty_coordinates") %>">
										<img src="./icons/gps.png" width="20px" height="20px">
									</a>
	 							 <% } %>
							</td>
						</tr>
						<tr>
							<td class = "textInfoTitle">
								Recibo Predial:
							</td>
							<td>
								 <%if(pmConn.getString("properties","prty_propertyreceipt").length() > 0){
	    							String blobkey = pmConn.getString("prty_propertyreceipt");
	    							String blobKeyParseReceip = HtmlUtil.getFileViewURL(sFParams, blobkey);%>	   
	    							<a  target='_blank' href="<%=blobKeyParseReceip %>">
	    								<img  src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/file-search.png") %>  width="20px" height="20px">
	    							</a>
	   								
	   							<%} %>
							</td>
						</tr>
						<tr>
							<td class = "textInfoTitle">
								<%= sFParams.getFieldFormTitle(bmoProperty.getCertifiedWriting()) %>:
							</td>
							<td>
								 <%if(pmConn.getString("properties", "prty_certifiedWriting").length() > 0){
	    							String blobkey = pmConn.getString("prty_certifiedWriting");
	    							String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    	
	    							<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
    									<img  src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/file-search.png") %>   width="20px" height="20px">
    								</a>
	    	
	   							<%  } %>
							</td>
						</tr>
						<tr>
							<td class = "textInfoTitle">
								Disponible:
							</td>
							<td>
								<%if(pmConn.getBoolean("prty_available")){%>
	      							<img  src= "./icons/true.png" width="20px" height="20px">	      		     			    	
	     	 					<%} else{%>
	    	 						<img  src="./icons/false.png" width="18px" height="18px">    	 
	    	  	   		 		<% }%>
							</td>
						</tr>						
										
					</table>
				</td>				
			</tr>	
			
					<div class="modal fade" id="mostrarmodalImg<%=pmConn.getInt("prty_propertyid") %>" tabindex="-1"  aria-labelledby="basicModal" aria-hidden="true">
			<div class="modal-dialog">
				 <div class="modal-content" style="border:2px solid white;">
<!-- 				 	<div class="modal-header "> -->
				 		<%if(pmConn.getString("prty_facade").length() > 0){
			    			
 			    				String blobkey = pmConn.getString("prty_facade"); 
			    				String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	 
			    				 <img  style="max-width: 100%; height: auto;border-radius: 5px" src="<%=blobKeyParseFacade %>" >
			       		<%}%>	
				 		
<!-- 				 	</div> -->
				 </div>
			</div>
		</div>
			
<!-- 			inicia ventana emergente	 -->
			<div class="modal fade" id="mostrarmodal<%=pmConn.getInt("prty_propertyid") %>" tabindex="-1"  aria-labelledby="basicModal" aria-hidden="true">
    		  <div class="modal-dialog">
       			 <div class="modal-content">
           			<div class="modal-header ">
<!--          			 	   <button type="button" class="botonClose" data-dismiss="modal" aria-hidden="true"><img src="./icons/close.png"></button> -->
         			 		<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>">       	
         			 			<div class="detailTitle">	           	
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCode()))%>: </b>&nbsp
								</div>
								<div>	
									<%=pmConn.getString("prty_code")%>		
								</div>
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getBlueprint()))%></b>&nbsp	
								</div>
								<div>
										<%if(pmConn.getString("properties", "prty_blueprint").length() > 0){
			    							if(pmConn.getString("prty_blueprint").endsWith(".pdf")){
			    								String blobkey = pmConn.getString("prty_blueprint");
			    					    		String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    		<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    				<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    			</a>
			    							<%}else{
			    							String blobkey = pmConn.getString("prty_blueprint");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 	<a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="17px" height="17px">
				       					 </a>
			       						<%}%>
			       	
			   						<%}%>									
								</div>
							</div>
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">	 
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getDescription()))%></b>&nbsp	
								</div>
								<div>	
									<%=pmConn.getString("prty_description")%>
								</div>
								<div  class="detailTitle">
									<b><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getContract()))%></b>&nbsp
								</div>
								<div>		
								<%if(pmConn.getString("properties", "prty_contract").length() > 0){
			    						if(pmConn.getString("prty_contract").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_contract");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    						String blobkey = pmConn.getString("prty_contract");
			    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 </a>
			       						<%}%>			       	
			   					<%}%>
								</div>
							</div>
						
			   				<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">   
			   					<div class="detailTitle">
			   						<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getStreet()))%></b>&nbsp
			   					</div>
			   					<div>
			   						<%=pmConn.getString("prty_street") %>
			   					</div>
			   					<div class="detailTitle">
			   						<b><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getNumber()))%></b>&nbsp
			   					</div>
			   					<div>
									<%=pmConn.getString("prty_number") %>
			   					</div>
							</div>	
												
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getPropertyReceipt()))%></b>&nbsp
								</div>
								<div>
										<%if(pmConn.getString("properties", "prty_propertyReceipt").length() > 0){
			    							if(pmConn.getString("prty_propertyReceipt").endsWith(".pdf")){
			    								String blobkey = pmConn.getString("prty_propertyReceipt");
			    					    		String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    		<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    				<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    			</a>
			    							<%}else{
			    							String blobkey = pmConn.getString("prty_propertyReceipt");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    						 	<a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       						 </a>
			       							<%}%>
			       	
			   							<%}%>
			   					</div>
			   					<div class="detailTitle" >
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getNeighborhood()))%></b>&nbsp
								</div>
								<div>
									<%=pmConn.getString("prty_neighborhood")%>
								</div>
							</div>
							
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getPropertyTitle()))%></b>&nbsp
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_propertyTitle").length() > 0){
			    						if(pmConn.getString("prty_propertyTitle").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_propertyTitle");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    						String blobkey = pmConn.getString("prty_propertyTitle");
			    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 </a>
			       						<%}%>
			       	
			   					<%}%>
			   					</div>
			   					<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getBetweenStreets()))%></b>&nbsp
								</div>
								<div>
									<%=pmConn.getString("prty_betweenStreets")%>
								</div>
							</div>
							
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getConstitutiveAct()))%></b>&nbsp
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_constitutiveAct").length() > 0){
			    							if(pmConn.getString("prty_constitutiveAct").endsWith(".pdf")){
			    								String blobkey = pmConn.getString("prty_constitutiveAct");
			    					    		String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    		<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    				<img src="./icons/file-search.png" width="17px" height="17px">
			    				    			</a>
			    							<%}else{
			    							String blobkey = pmConn.getString("prty_constitutiveAct");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    						 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 	</a>
			       							<%}%>
			   						<%}%>
			   					</div>
			   					<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getZip()))%></b> &nbsp
								</div>
								<div>
									<%=pmConn.getString("prty_zip")%>
								</div>
							</div>
						
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCertifiedWriting()))%></b>&nbsp
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_certifiedWriting").length() > 0){
			    							if(pmConn.getString("prty_certifiedWriting").endsWith(".pdf")){
			    								String blobkey = pmConn.getString("prty_certifiedWriting");
			    					    		String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    		<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    				<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    			</a>
			    							<%}else{
			    							String blobkey = pmConn.getString("prty_certifiedWriting");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    						 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 	</a>
			       							<%}%>
			       	
			   						<%}%>			
			   					</div>	
			   					<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCityId()))%></b>&nbsp
								</div>
								<div>
									<%=pmConn.getString("city_name")%>
								</div>				
							</div>
							
							<div class="divText" style="display: grid;grid-template-columns:<%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getDemarcation()))%></b>&nbsp
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_demarcation").length() > 0){
			    						if(pmConn.getString("prty_demarcation").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_demarcation");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    						String blobkey = pmConn.getString("prty_demarcation");
			    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 </a>
			       						<%}%>
			       	
			   						<%}%>
			   					</div>
			   					<div class="detailTitle" >
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getRegistryNumber()))%></b>&nbsp
								</div>
								<div>
									<%=propertytax %>
								</div>
							</div>
						
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getNotaryQuotation()))%></b>&nbsp
								</div>
								<div>
								<%if(pmConn.getString("properties", "prty_notaryQuotation").length() > 0){
			    						if(pmConn.getString("prty_notaryQuotation").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_notaryQuotation");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    						String blobkey = pmConn.getString("prty_notaryQuotation");
			    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 </a>
			       						<%}%>
			   					<%}%>
			   					</div>
			   					<div  class="detailTitle">
									<b><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getExtraPrice()))%></b>&nbsp
								</div>
								<div>
									<%=pmConn.getString("prty_extraPrice") %>
								</div>
							</div>
						
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getAppraise()))%></b>&nbsp
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_appraise").length() > 0){
			    						if(pmConn.getString("prty_appraise").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_appraise");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    						String blobkey = pmConn.getString("prty_appraise");
			    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 </a>
			       						<%}%>
			       	
			   						<%}%>
			   					</div>
			   					<div class="detailTitle" >
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCustomerId()))%></b>&nbsp
								</div>
								<div>
									<%=pmConn.getString("cust_displayname") %>
								</div>
							</div>
							
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getDebtCertificate()))%></b>&nbsp
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_debtCertificate").length() > 0){
			    						if(pmConn.getString("prty_debtCertificate").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_debtCertificate");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    						String blobkey = pmConn.getString("prty_debtCertificate");
			    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 </a>
			       						<%}%>
			       	
			   						<%}%>
			   					</div>
			   					<div class="detailTitle" >
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCoordinates()))%></b>&nbsp
								</div>
								<div>
									<%if(pmConn.getString("properties","prty_coordinates").length() > 0){%>
				   
				   					 <a target='_blank' href="https://www.google.com/maps/place/<%=pmConn.getString("prty_coordinates") %>" title="Ver en Google Maps">
										 <img src="./icons/gps.png" width="20px" height="20px">
									 </a>
			 						 <% } %>
								</div>
							</div>
							
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getTaxCertificate()))%></b>&nbsp
								</div>
								<div>								
			    					<%if(pmConn.getString("properties", "prty_taxCertificate").length() > 0){
			    						if(pmConn.getString("prty_taxCertificate").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_taxCertificate");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    							String blobkey = pmConn.getString("prty_taxCertificate");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 		<a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 		</a>
			       						<%}%>			       	
			   						<%}%>
			   					</div>
			   					<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getAvailable()))%></b>&nbsp	
								</div>
								<div>
								 	<%String raccLinked = ((pmConn.getBoolean("prty_available") ? "Si" : "No")); %>
								 	<%= raccLinked %>
								</div>
							</div>
						
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getWaterBill()))%></b>&nbsp	
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_waterBill").length() > 0){
			    						if(pmConn.getString("prty_waterBill").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_waterBill");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    							String blobkey = pmConn.getString("prty_waterBill");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    							 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
				       				 		</a>
			       						<%}%>
			       	
			   				  	<%}%>
			   				  </div>
			   				  <div class="detailTitle" >
								<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getLandSize()))%></b>&nbsp	
							  </div>
							  <div>	
								<%=pmConn.getString("prty_landSize")%>
							  </div>
							</div>
						
							<div class="divText" style="display: grid;grid-template-columns:<%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getElectricityBill()))%></b>&nbsp	
								</div>
								<div>
									<%if(pmConn.getString("properties", "prty_electricityBill").length() > 0){
			    						if(pmConn.getString("prty_electricityBill").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_electricityBill");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px"></a>
			    						<%}else{
			    							String blobkey = pmConn.getString("prty_electricityBill");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    					 		<a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px"></a>
			       						<%}%>
			       	
			   						 <%}%>
			   					</div>
			   					<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getConstructionSize()))%></b>&nbsp	
								</div>
								<div>
									<%=pmConn.getString("prty_constructionSize")%>
								</div>
							</div>
							
							<div class="divText" style="display: grid;grid-template-columns: <%=tabDiv%>;">  
								<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getOtherDocuments()))%></b>&nbsp	
								</div>
								<div>		
									<%if(pmConn.getString("properties", "prty_otherdocuments").length() > 0){
			    						if(pmConn.getString("prty_otherdocuments").endsWith(".pdf")){
			    							String blobkey = pmConn.getString("prty_otherdocuments");
			    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
			    					    	
			    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
			    				    			<img  src="./icons/file-search.png" width="17px" height="17px">
			    				    		</a>
			    						<%}else{
			    							String blobkey = pmConn.getString("prty_otherdocuments");
			    							String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
			    						 	<a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px"></a>
			       						<%}%>
			       	
			   					 	<%}%>	
			   					 </div>		
			   					<div class="detailTitle">
									<b ><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getAdjoins()))%></b>&nbsp	
								</div>
								<div>
									<%=pmConn.getString("prty_adjoins") %>
								</div>		
							</div>
							
					</div>     

  			 </div>
		</div>
<!-- 		fin ventana emergente -->


	<%} 
	}else{%>
		<tr height="2px">
		

		</tr>
		<tr align="center"  >
		
			<td>
				<table  class = "imgTable" style="font-family:  Helvetica Neue,Helvetica,Arial,sans-serif;"  width="90%" >
					<tr>
						<td colspan="3" class = "titleContract" >
							No hay datos relacionados 
						</td>
					</tr>
				</table>
			</td>
		</tr>
	<%} %>
	</table>
	 <script src="./css/menu.js"></script>
</body>
</html>
<%
	pmConn.close();
			pmConnPropertytax.close();
			pmConnCounter.close();
			pmConnLessors.close();
		} catch (Exception e) {

		}
	 
 %>
