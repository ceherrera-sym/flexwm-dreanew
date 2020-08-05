<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.symgae.server.SFServerUtil"%>
<%@page import="com.symgae.server.PmConn"%>
<%@page import="com.symgae.shared.BmFieldType"%>
<%@page import="com.symgae.server.HtmlUtil"%>
<%@page import="com.symgae.server.SFParamsServiceImpl"%>
<%@page import="com.symgae.shared.SFParamsService"%>
<%@page import="com.symgae.shared.LoginInfo"%>
<%@page import="com.symgae.shared.SFParams"%>
<%@page import="com.flexwm.server.co.PmProperty"%>
<%@page import="com.flexwm.shared.co.BmoProperty"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>


<%
try{
String defaultCss = "symgae.css";
String gwtCss = "gwt_standard.css";
String fwmCss = "flexwm.css";
boolean isMobile = false;
int propertyid = 0;
String title = "Detalle Inmueble";
String sql = "";
String width = "30%";

SFParams sFParams = new SFParams();


LoginInfo loginInfo = new LoginInfo();
loginInfo.setLoggedIn(true);
loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());

BmoProperty bmoProperty = new BmoProperty();
PmProperty pmProperty = new PmProperty(sFParams);

PmConn pmConn = new PmConn(sFParams);
PmConn pmConnPropertytax = new PmConn(sFParams);

if (SFServerUtil.isMobile(request.getHeader("user-agent"))) {
	isMobile = true;		
}


propertyid = Integer.parseInt(request.getParameter("property"));



sql = " SELECT * FROM properties LEFT JOIN customers ON (prty_customerid = cust_customerid )" +
      " LEFT JOIN cities ON (prty_cityid = city_cityid) WHERE prty_propertyid = " + propertyid;


pmConn.open();
pmConnPropertytax.open();
pmConn.doFetch(sql);


%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<link type="text/css" rel="stylesheet" href="../css/<%= defaultCss %>">
    <link type="text/css" rel="stylesheet" href="../css/<%= gwtCss %>">
	<link type="text/css" rel="stylesheet" href="../css/<%= fwmCss %>">
	<link type="text/css" rel="stylesheet" href="../css/tags.jsp">
	<link type="text/css" rel="stylesheet" href="../css/login.css">
<title><%=title %></title>
<% if (isMobile) { %>
<meta name="viewport"
	content="width=320, initial-scale=1.0, maximum-scale=1">
<% 
  width = "100%";
  } %>
</head>
<body>
<%while(pmConn.next()){ %>
	<table  >
		
					<% 
						sql  = "SELECT  * FROM propertytax" +
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
					 <tr>
						<td colspan="2" class="contractTitle">Detalles Inmueble</td>
					 </tr>
			
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCode()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_code"),BmFieldType.CODE) %></td>		
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getFacade()))%></td>	
						<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_facade").length() > 0){
	    						if(pmConn.getString("prty_facade").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_facade");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_facade");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	   						 <%}%>
	   		  	    	</td>	
					 </tr>
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getDescription()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_description"),BmFieldType.STRING) %></td>	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getBlueprint()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_blueprint").length() > 0){
	    						if(pmConn.getString("prty_blueprint").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_blueprint");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_blueprint");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>	
								
					 </tr>
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getStreet()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_street"),BmFieldType.STRING) %></td>		
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getContract()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_contract").length() > 0){
	    						if(pmConn.getString("prty_contract").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_contract");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_contract");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>		
					 </tr>
				   	 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getNumber()))%></td>
			 	  	    <td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_number"),BmFieldType.STRING) %></td>	
			 	  	    <td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getPropertyReceipt()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_propertyReceipt").length() > 0){
	    						if(pmConn.getString("prty_propertyReceipt").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_propertyReceipt");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src=src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_propertyReceipt");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>			
					 </tr>
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getNeighborhood()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_neighborhood"),BmFieldType.STRING) %></td>	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getPropertyTitle()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_propertyTitle").length() > 0){
	    						if(pmConn.getString("prty_propertyTitle").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_propertyTitle");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_propertyTitle");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>			
					 </tr>
				   	 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getBetweenStreets()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_betweenStreets"),BmFieldType.STRING) %></td>	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getConstitutiveAct()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_constitutiveAct").length() > 0){
	    						if(pmConn.getString("prty_constitutiveAct").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_constitutiveAct");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_constitutiveAct");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	   						 <%}%>
	   		  	    	</td>	
								
					 </tr>
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getZip()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_zip"),BmFieldType.STRING) %></td>		
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCertifiedWriting()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_certifiedWriting").length() > 0){
	    						if(pmConn.getString("prty_certifiedWriting").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_certifiedWriting");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_certifiedWriting");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>		
					 </tr>
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCityId()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("city_name"),BmFieldType.STRING) %></td>		
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getDemarcation()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_demarcation").length() > 0){
	    						if(pmConn.getString("prty_demarcation").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_demarcation");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_demarcation");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>		
					 </tr>
				  	 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getRegistryNumber()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,propertytax,BmFieldType.STRING) %></td>		
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getNotaryQuotation()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_notaryQuotation").length() > 0){
	    						if(pmConn.getString("prty_notaryQuotation").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_notaryQuotation");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_notaryQuotation");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	   						 <%}%>
	   		  	    	</td>		
					 </tr>
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getExtraPrice()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_extraPrice"),BmFieldType.STRING) %></td>	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getAppraise()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_appraise").length() > 0){
	    						if(pmConn.getString("prty_appraise").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_appraise");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_appraise");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>			
					 </tr>
					 <tr>
<%-- 						<td class="reportHeaderCell"><%=bmoProperty.getCustomerId().getLabel() %></td> --%>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCustomerId()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("cust_displayname"),BmFieldType.STRING) %></td>		
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getDebtCertificate()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   									
	    					<%if(pmConn.getString("properties", "prty_debtCertificate").length() > 0){
	    						if(pmConn.getString("prty_debtCertificate").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_debtCertificate");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_debtCertificate");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>		
					 </tr>
					 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getCoordinates()))%></td>
						<td class="reportCellEven"></td>
						<td class="reportCellEven">
						 <%if(pmConn.getString("properties","prty_coordinates").length() > 0){%>
		   
		   					 <a target='_blank' href="https://www.google.com/maps/place/<%=pmConn.getString("prty_coordinates") %>" title="Ver en Google Maps">
								 <img src="./img/gps.png" width="" height="">
							 </a>
	 					 <% } %>
						</td>	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getTaxCertificate()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_taxCertificate").length() > 0){
	    						if(pmConn.getString("prty_taxCertificate").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_taxCertificate");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_taxCertificate");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>			
					 </tr>	
				  	 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getAvailable()))%></td>
						 <%String raccLinked = ((pmConn.getBoolean("prty_available") ? "Si" : "No")); %>	
						<td class="reportCellEven"><%= HtmlUtil.formatReportCell(sFParams, raccLinked, BmFieldType.STRING) %></td>	   	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getWaterBill()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_waterBill").length() > 0){
	    						if(pmConn.getString("prty_waterBill").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_waterBill");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_waterBill");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>		
					 </tr>	
<!-- 				  	 <tr> -->
<!-- 						<td colspan="2" class="contractTitle">Dimensiones</td> -->
<!-- 					 </tr> -->
				 	 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getLandSize()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_landSize"),BmFieldType.STRING) %></td>	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getElectricityBill()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_electricityBill").length() > 0){
	    						if(pmConn.getString("prty_electricityBill").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_electricityBill");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_electricityBill");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>	
				 	 </tr>
				 	 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getConstructionSize()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_constructionSize"),BmFieldType.STRING) %></td>	
						<td class= "reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getOtherDocuments()))%></td>
	   		  	    	<td class="reportCellEven" style="text-align: center" title="Clic para abrir en pesta&ntilde;a nueva">
	   
	    					<%if(pmConn.getString("properties", "prty_otherdocuments").length() > 0){
	    						if(pmConn.getString("prty_otherdocuments").endsWith(".pdf")){
	    							String blobkey = pmConn.getString("prty_otherdocuments");
	    					    	String blobKeyParseTitle = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
	    					    	
	    					    	<a align="center" target='_blank' href="<%=blobKeyParseTitle%>">
	    				    			<img  src="../icons/file-search.png" width="17px" height="17px">
	    				    		</a>
	    						<%}else{
	    						String blobkey = pmConn.getString("prty_otherdocuments");
	    						String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	
	    					 <a target='_blank' href="<%= blobKeyParseFacade%>"> <img src="<%=blobKeyParseFacade %>" width="50px" height="25px">
		       				 </a>
	       						<%}%>
	       	
	   						 <%}%>
	   		  	    	</td>	
				 	 </tr>
				 	 <tr>
						<td class="reportHeaderCell"><%= HtmlUtil.stringToHtml(sFParams.getFieldFormTitle(bmoProperty.getProgramCode().toString(), bmoProperty.getAdjoins()))%></td>
						<td class="reportCellEven"><%=HtmlUtil.formatReportCell(sFParams,pmConn.getString("prty_adjoins"),BmFieldType.STRING) %></td>	
						<td class="reportCellEven" colspan="2">
				 	 </tr>	
   
	</table>
	<%} %>
</body>
</html>
<%pmConn.close();
  pmConnPropertytax.close();
} catch(Exception e) {
}%>