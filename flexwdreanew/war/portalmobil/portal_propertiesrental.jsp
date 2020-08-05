<!--  
/**
 * @author Cesar Herrera Hernández
 */ -->

<%@page import="java.text.NumberFormat"%>
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


<% try{%>
	
<%@include file="./inc_params.jsp"%>	
<%	Calendar date = new GregorianCalendar();
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

	String sql = "",filter = "";
	String sql2 = "";
	String customer = "",orLessor = "",sqllessors = "";
	String cust_id = (String)session.getAttribute("Id");
	int page1 = 1,custLessor = 0,print = 0,pageSize = 10;
	int counterQuery = 0,viewMore = 0;
	int counterFull = 0;
	int pages = 0;
	int pagina = 1;
	Locale locale = new Locale("es", "MX");
	NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
	double sumAmount = 0,sumBalance = 0;
	
	 if(Integer.parseInt(cust_id) <= 0)
		 response.sendRedirect("portal_login.jsp") ;
	if(!request.getParameter("viewMore").equals("")){
		viewMore = Integer.parseInt(request.getParameter("viewMore"));
	}
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
	if(!(Integer.parseInt(request.getParameter("viewMore")) > 0)){
		viewMore = Integer.parseInt(request.getParameter("viewMore"));
	}
	
	//si se realiza una busqueda mostrara la suma total
	if(customer.length()>0)filters = true;
	
	String where = "";
	String initialWhere = " AND orde_status = '"+ BmoOrder.STATUS_AUTHORIZED+"'";
	filter = "Estatus: Autorizado";
	
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
	

	pmConn.open();

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
		
	}
	
	
	pmConn.doFetch(sql);

%>
	

<!doctype html>


<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">



<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
	<script src="//code.jquery.com/jquery-1.12.0.min.js"></script>
   <script src="./css/bootstrap.min.js"></script>  
   <script>
   //para mostrar el detalle de el Inmueble
      function showDetail(id)
      {    	
         $("#mostrarmodal"+id).modal("show");
      }
    </script>

<%-- <% if (isMobile) { %> --%>
<!-- <meta name="viewport" -->
<!-- 	content="width=320, initial-scale=1.0, maximum-scale=1"> -->
<%-- <% } %> --%>
<!-- <meta name="viewport" -->
<!-- 	content="width=device-width, user-scalable=yes, initial-scale=1.0, maximum-scale=2.0, minimum-scale=1.0"> -->
<title>::: <%= appTitle %> :::
</title>			
 <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript">
 $(document).ready(function(){
 	$('body').hide(); 
	 $('body').fadeIn(1000); 
	 document.getElementById("table").style.display = "block";
 }); 

</script> 

</head>
<body id="table"  onload="iniciaFrame(1)" style="position: relative;display: none">

	
   <table  width="100%" class = "table">
	<%
	BmoCustomer bmoCustomer = new BmoCustomer();
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	int i = counterQuery +1;
	if(pmConn.next()){
		pmConn.doFetch();
		while(pmConn.next()){
		bmoCustomer = (BmoCustomer)pmCustomer.get(pmConn.getInt("prty_customerid"));
		
		SFServerUtil sFServerUtil = new SFServerUtil();
		int months1 = sFServerUtil.daysBetween("yyyy-MM-dd",dateNow , pmConn.getString("prrt_enddate"));
		int months3 = sFServerUtil.daysBetween("yyyy-MM-dd",dateNow , pmConn.getString("prrt_rentincrease")); 
			
		%>
		<tr height="2px">	
		</tr>
		<tr align="center"  >
			<td>
				<table  class = "imgTable" style="font-family:  Helvetica Neue,Helvetica,Arial,sans-serif;"  width="90%" >

			   		<tr>
			   			<td colspan="5" class = "titleContract" >
			   				<%=pmConn.getString("orde_name") %>
			   				
			   				
			   			</td>
			   			   			
			   		</tr>
			   		<tr>
			   			<td  class="textInfoTitle"  >
			   				Arrendador:
			   			</td>
			   			<td class="textInfo" align="center"  colspan="4">
			   				<%=bmoCustomer.getDisplayName().toString() %>
			   			</td>
<!-- 			   			<td rowspan="3" align="center"> -->
<%-- 			   				<%if(pmConn.getString("cust_logo").length() > 0){ --%>
<!-- // 	    						String blobkey = pmConn.getString("cust_logo"); -->
<%-- 	    						String blobKeyParseLogo =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>	   		     --%>
	        					
<%-- 		      						<img class = "custLogo" src="<%=blobKeyParseLogo %>" > --%>
	        						
<%-- 	        				<%} %> --%>
<!-- 			   			</td> -->
			   		</tr>
			   		
			   		<tr>
			   			<td class="textInfoTitle">
			   				Nombre Contrato:
			   			</td>
			   			<td  class="textInfo" align="center" colspan="4">
			   				<%=pmConn.getString("prrt_code") %>
			   				
			   			</td>
			   		</tr>
			   		<tr>
			   			<td class="textInfoTitle" width="25%">
			   				Contrato:
			   			</td>
			   			<td   class="textInfo" width="25%" >	
							<%if(pmConn.getString("wfdo_file").length() > 0){
								String blobkey = pmConn.getString("wfdo_file");
								String blobKeyParseWfdoFile = HtmlUtil.getFileViewURL(sFParams, blobkey);%>
								<a  target="_blank" href="<%=blobKeyParseWfdoFile %>">			
	    						<img  src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/file-search.png") %>  width="20px" height="20px"></a>
							<%} %>
						</td >	
						 <td class = "textInfoTitle" width="25%">
			   				Clave Renovados:
			   			</td>
			   			<td  class="textInfo" align="center" width="25%" > 
			   				<%=pmConn.getString("orde_code") %>
			   			</td>
			   		</tr>
			   		
			   		<tr>
			   			<td class = "textInfoTitle">
			   				Nombre del Cliente:
			   			</td>
			   			<td  class="textInfo" align="center" >
			   				<%=pmConn.getString("cust_displayname") %>
			   			</td>			   			
			   		
			   			<td class = "textInfoTitle">
			   				Meses:
			   			</td>
			   			<td  class="textInfo" align="center" >
			   				<%=pmConn.getString("orpt_quantity") %>
			   			</td>			   			
			   		</tr>
			   		<tr>
			   			<td class = "textInfoTitle">
			   				Vencimiento:		   				
			   			</td>		
			   			<td class="textInfo" >
			   				<%= pmConn.getString("prrt_enddate")%>
			   			</td>  			
			   	
			   			<td class = "textInfoTitle">
			   				Vigencia Contrato:			   				
			   			</td>		
			   			<td class="textInfo" >
			   				<%if(months1 <= 60){ %>				
								<img alt="" src= <%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_red.png") %>>			 	
	    					<%} %>
	    					<%if(months1 <= 180 && months1 >= 61){ %>		
		 						<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_yellow.png") %>>
					    	<%} %>
	    					<%if(months1 >= 181){ %>	     
	     						<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_green.png") %>>
		    				<%} %>
			   			</td>	   			
			   		</tr>
			   			<tr>
			   			<td class="textInfoTitle">
			   				Incremento Renta:
			   			</td>
			   			<td class="textInfo" >
			   				<%=pmConn.getString("prrt_rentincrease") %>
			   			</td>
			   	
			   			<td class="textInfoTitle">
			   				Vigencia Incremento:
			   			</td>
			   			<td class="textInfo" >
			   				  <%if(months3 <= 30){ %>
								<img alt="" src= <%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_red.png") %>>			
	    					 <%} %>
	    					 <%if(months3 <= 60	&&  months3 >= 31){ %>		
								<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_yellow.png") %>>		 	
	    					 <%} %>
	    		             <%if(months3 >= 61){ %>	    
								<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_green.png") %>>
		 		    		 <%} %> 
			   			</td>
			   		</tr>			   
			   		<tr>	
			   			<td  class="textInfoTitle" >
			   				Estatus:
			   			</td>		   			
			   			<td  class="textInfo" align="center" >
			   				<%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_AUTHORIZED)) %>Autorizado
		     				<%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_CANCELLED)) %>Cancelado
		     				<%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_FINISHED)) %>Finalizado
		     				<%if(pmConn.getString("orde_status").equalsIgnoreCase(""+BmoOrder.STATUS_REVISION)) %>En Revisi&oacute;n 	
			   			</td>	
			   						    	
						<td rowspan="5" colspan="2" style="text-align: center;">
							<%if(pmConn.getString("cust_logo").length() > 0){
	    						String blobkey = pmConn.getString("cust_logo");
	    						String blobKeyParseLogo =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>	   		    
	        					
		      						<img class = "custLogo" src="<%=blobKeyParseLogo %>" >
	        						
	        				<%} %>

			   			</td>			   			
			   				   			
			   		</tr>
			   		<tr>
			   			<td class="textInfoTitle" >
			   				Renta Inicial:
			   			</td>
			   			<td  class="textInfoCurrency" align="center" style="color: #00187C">
			   				<%=fmt.format(pmConn.getDouble("prrt_initialIconme")) %>
			   			</td>
			   		</tr>
			   		

			   	
			   		<tr>
			   			<td  class = "textInfoTitle" >
			   				Renta Vigente:
			   			</td>
			   			<td class="textInfoCurrency" style="color: #009B26">
			   				<%=fmt.format(pmConn.getDouble("orpt_price")) %>
			   			</td>
			   		</tr>
			   		<tr>
			   			<td  class = "textInfoTitle" >
			   				Total:
			   			</td>
			   			<td class="textInfoCurrency" style="color: #00187C">
			   				<%=fmt.format(pmConn.getDouble("orpt_amount")) %>
			   			</td>
			   		</tr>
			   		<tr>
			   			<td  class = "textInfoTitle" >
			   				Saldo:
			   			</td>
			   			<td class="textInfoCurrency" style="color: #00187C">
			   				<%=fmt.format(pmConn.getDouble("orde_balance")) %>
			   			</td>
			   		</tr>
			   	
			   	</table>			   
			</td>
		</tr>
		
		<div class="modal fade" id="mostrarmodal<%=pmConn.getInt("prty_propertyid") %>" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
			<div class="modal-dialog">
				 <div class="modal-content" style="border:2px solid white;">
<!-- 				 	<div class="modal-header "> -->
				 		<%if(pmConn.getString("properties", "prty_facade").length() > 0){
			    			
 			    				String blobkey = pmConn.getString("prty_facade"); 
			    				String blobKeyParseFacade =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>    	 
			    				 <img  style="max-width: 100%; height: auto;border-radius: 5px" src="<%=blobKeyParseFacade %>" >
			       		<%}%>	
				 		
<!-- 				 	</div> -->
				 </div>
			</div>
		</div>
		<%i++;
		} 
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
   <table>
   
   </table>
 
   <script src="./css/menu.js"></script>
</body>
</html>
<%pmConn.close();
pmConnCounter.close();
pmConnLessors.close();
}catch(Exception e){

}

 %>
