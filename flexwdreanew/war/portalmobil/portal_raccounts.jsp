<!--  
/**
 * @author Cesar Herrera Hernández
 */ -->


<%@page import="java.text.NumberFormat"%>
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
	
<%	Calendar date = new GregorianCalendar();
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
	boolean isMobile = false,data = true;	

	String sql = "",sqllessors = "",orLessor = "",filter = "",sql2 = "",customer = "",sql3 = "",sql4= "",duedate1 = "",duedate2 = "";
	String cust_id = (String)session.getAttribute("Id");
	int page1 = 1,print = 0,pageSize = 10,viewMore = 0;
	int counter = 1;
	int counterQuery = 0;
	int pagina = 1,custLessor = 0;
	int counterFull = 0;
	int pages = 0;
	boolean payment = false;
	Locale locale = new Locale("es", "MX");
	NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
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
	if(!request.getParameter("viewMore").equals("")){
		viewMore = Integer.parseInt(request.getParameter("viewMore"));
	}

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
	if(!(Integer.parseInt(request.getParameter("viewMore")) > 0)){
		viewMore = Integer.parseInt(request.getParameter("viewMore"));
	}
	String where = "";
	
// 	LoginInfo loginInfo = new LoginInfo();
// 	loginInfo.setLoggedIn(true);
// 	loginInfo.setEmailAddress(getServletContext().getInitParameter("systememail"));
// 	SFParamsServiceImpl.fillSFParamsFromWebXML(sFParams, config.getServletContext());
// 	SFParamsServiceImpl.paramsFactory(sFParams, loginInfo, config.getServletContext());
	
	int id = Integer.parseInt(cust_id);
	double sumPayments = 0;
	double sumBalance = 0;
	PmConn pmConn = new PmConn(sFParams);	
	
	PmConn pmConnCounter = new PmConn(sFParams);
	PmConn pmConnLessors =new PmConn(sFParams);
	PmConn pmConnG2 = new PmConn(sFParams);
	PmCustomer pmCustomer = new PmCustomer(sFParams);
	BmoCustomer bmoCustomer = new BmoCustomer();


	PmPropertyRental pmPropertyRental = new PmPropertyRental(sFParams);
	BmoPropertyRental bmoPropertyRental = new BmoPropertyRental();	
	BmoRaccount bmoRaccount = new BmoRaccount();
	PmRaccount pmRaccount = new PmRaccount(sFParams);
	
	
	if(selestRaccountsStatus.equalsIgnoreCase("< Estatus de Pago >")){
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
	
	counterQuery = (page1 * pageSize) - pageSize;


	
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

	pmConn.doFetch(sql);		
	pmConnCounter.doFetch(sql3);
	

%>	

<!doctype html>

<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<link type="text/css" rel="stylesheet" href="./css/<%= defaultCss %>">


<link type="text/css" rel="stylesheet" href="./css/login.css">
<script src="//code.jquery.com/jquery-1.12.0.min.js"></script>
 <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
<script type="text/javascript">
 $(document).ready(function(){
 	$('body').hide(); 
	 $('body').fadeIn(1000); 
	 document.getElementById("table").style.display = "block";
 }); 

</script> 
<title>::: <%= appTitle %> :::
</title>
		
</head>
<body id="table"  onload="iniciaFrame(2)" style="position: relative;display: none;">	
   <table width="100%" class = "table" >
<% if(pmConn.next()){%>

   
	 	
		<tr height="2px">
		</tr>  		
		<tr   align="center" >
			<td  >
				<table  class = "imgTable"   >
					<tr class = "titleContract" >
						<td style="background: white; ">
							<div align="left" id="piechart" style="border-radius: 5px 5px 200px 5px;"> </div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	<%
	
	int i = counterQuery +1;
	double sumPayPending = 0, sumPayTotal = 0, sumPayVenc = 0;
	pmConn.doFetch(sql);	
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
		
		<tr height="2px">
		</tr>
		
		<tr align="center"  >
			<td>
				<table  class = "imgTable" style="font-family: Helvetica Neue,Helvetica,Arial,sans-serif;" >
			    	
			   		<tr>
			   			<td colspan="5" class = "titleContract" >
			   				<%=pmConn.getString("prrt_name")%>
			   			</td>
			   		</tr>
			   		<tr>
			   			<td  class="textInfoTitle" width="25%">
			   				Arrendador:
			   			</td>
			   			<td class="textInfo" colspan = "4" >
			   				<%=bmoCustomer.getDisplayName().toString() %>
			   			</td>
			   			
			   		</tr>
			   		<tr>	
			   			<td  class="textInfoTitle" >
			   				Inmueble:
			   			</td>		   			
			   			<td  class="textInfo" colspan = "4" >
			   				<%=pmConn.getString("prty_description")%>
			   			</td>
			   			
			   		</tr>
			   	
			   		<tr>	
			   			<td  class="textInfoTitle" >
			   				Nom. Cliente:
			   			</td>		   			
			   			<td  class="textInfo" colspan = "4" >
			   				<%=pmConn.getString("cust_displayname")%>
			   			</td>
			   			
			   		</tr>
			   		
			   		<tr>
			   			<td class = "textInfoTitle">
			   				<%=bmoRaccount.getDueDate().getLabel() %>:			   				
			   			</td>		
			   			<td class="textInfo" colspan = "4" >
			   				<%=pmConn.getString("racc_duedate") %>
			   			</td>	   			
			   		</tr>			   		
			   		<tr>
			   			<td class="textInfoTitle">
			   				<%=bmoRaccount.getPaymentStatus().getLabel().toString() %>:
			   			</td>
			   			<td class="textInfo" >
			   			<%if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_PENDING) && days > 0 ){ %>
		 					
							<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_yellow.png") %>>
		  
		  				<%}else %>
		  				<%if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_PENDING) && (days <= 0)){ %>
		  
							<img alt="" src= <%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_red.png") %>>
		  
		  				<%} else%>
		  				 <%if(pmConn.getString("racc_paymentstatus").equals(""+BmoRaccount.PAYMENTSTATUS_TOTAL)){ %>
		 						
							<img alt="" src=<%=GwtUtil.getProperUrl(sFParams, "portalmobil/icons/btn_green.png") %>>
		
		 				 <%} %>
			   			
			   			</td>
			   		</tr>
			   		<tr>
			   			<td  class = "textInfoTitle" >
			   				<%=bmoRaccount.getTotal().getLabel().toString() %>:
			   			</td>
			   			<td class="textInfoCurrency"  style="color: #00187C">
			   				<%=fmt.format(pmConn.getDouble("racc_total")) %>
			   			</td>
			   			
			   		</tr>
			   		<tr>
			   			<td  class = "textInfoTitle" >
			   				<%=bmoRaccount.getPayments().getLabel().toString() %>:
			   			</td>
			   			<td class="textInfoCurrency" style="color: #009B26">
			   				<%=fmt.format(pmConn.getDouble("racc_payments")) %>
			   			</td>
			   			<td rowspan="2" colspan="3" style="text-align: center;" >
			   				<%if(pmConn.getString("cust_logo").length() > 0){
	    						String blobkey = pmConn.getString("cust_logo");
	    						String blobKeyParseLogo =  HtmlUtil.getFileViewURL(sFParams, blobkey);%>	   		    
	        					
		      						<img class = "custLogo" src="<%=blobKeyParseLogo %>" >
	        						
	        				<%} %>
			   			</td>
			   		</tr>
			   		<tr>
			   			<td  class = "textInfoTitle" >
			   				<%=bmoRaccount.getBalance().getLabel().toString() %>:	
			   			</td>
			   			<td class="textInfoCurrency" style="color: #D30000">
			   				<%=fmt.format(pmConn.getDouble("racc_balance")) %>
			   			</td>
			   			
			   		</tr>
			   		<tr>	
			   			<td  class="textInfoTitle" >
			   				Comentarios:
			   			</td>		   			
			   			<td  class="textInfo" colspan = "4"  >
			   				<%=pmConn.getString("racc_commentlog")%>
			   			</td>
			   			
			   		</tr>
			   	
			   	</table>			   
			</td>
		</tr>
		<%sumPayments += pmConn.getDouble("racc_payments");
		  sumBalance += pmConn.getDouble("racc_balance");%>
		<%i++;%>
				
	<%} 
	if(data){%>
	<tr   align="center" >
			<td  >
				<table  class = "imgTable" style="font-family: Helvetica ;height: 100%" >
					
					<tr height="50%" style="margin-top: 3px;margin-bottom: 3px" 	>
						<td  class="textInfoTitle"width="25%">
						   Suma Pagos :
						</td>
						<td  class="textInfoCurrency" style="color: #00187C">
							<%=fmt.format(sumPayments)%>
						</td>
						<td >&nbsp;</td>
					</tr>
					<tr height="50%" >
						<td  class="textInfoTitle" style="margin-top: 30px;margin-bottom: 3px">
						   Suma Saldo :
						</td>
						<td  class="textInfoCurrency" style="color: #00187C">
							<%=fmt.format(sumBalance)%>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<%} %>
		 <script src="./css/menu.js"></script>
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
    	'width':300, 'height':240,
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
		
		<%}else{ %>
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


</body>
</html>

<%


pmConn.close();
pmConnCounter.close();
pmConnLessors.close();
pmConnG2.close();
}catch(Exception e){
	
 //	response.sendRedirect("portal_login.jsp") ;
}

 %>
