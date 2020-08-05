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
 
<%@page import="com.flexwm.server.ev.PmVenue"%>
<%@page import="com.flexwm.shared.ev.BmoVenue"%>
<%@page import="com.flexwm.shared.op.*"%>
<%@page import="com.flexwm.shared.cm.*"%>
<%@page import="com.flexwm.shared.wf.*"%>
<%@page import="com.symgae.client.ui.UiParams"%>
<%@page import="com.flexwm.server.cm.*"%>
<%@page import="com.flexwm.server.op.*"%>
<%@page import="com.flexwm.server.wf.*"%>
<%@include file="../inc/login.jsp" %>

<%
	String title = "CheckList de Operaciones";

	BmoProject bmoProject = new BmoProject();
	BmoProgram bmoProgram = new BmoProgram();
	PmProgram pmProgram  = new PmProgram(sFParams);
	bmoProgram = (BmoProgram)sFParams.getBmoProgram(bmoProject.getProgramCode());
%>

<html>
		<% 
		// Imprimir
		String print = "0", permissionPrint = "";
		if ((String)request.getParameter("print") != null) print = (String)request.getParameter("print");
		
		// Exportar a Excel
		String exportExcel = "0";
		if ((String)request.getParameter("exportexcel") != null) exportExcel = (String)request.getParameter("exportexcel");
		if (exportExcel.equals("1") && sFParams.hasPrint(bmoProgram.getCode().toString())) {
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "inline; filename=\""+title+".xls\"");
		}
		
		//Si se visualiza y no se tienen permisos, deshabilita: copiar, pegar, seleccionar, menú(clic-derecho).
		//En caso de que mande a imprimir, deshabilita contenido
		if(!(sFParams.hasPrint(bmoProgram.getCode().toString()))) { %>
		<style> 
			body{
				user-select: none;
				-moz-user-select: none; 
				-o-user-select: none; 
				-webkit-user-select: none; 
				-ie-user-select: none; 
				-khtml-user-select:none; 
				-ms-user-select:none; 
				-webkit-touch-callout:none
			}
		</style>
		<style type="text/css" media="print">
		    * { display: none; }
		</style>
		<%
		permissionPrint = "oncopy='return false' oncut='return false' onpaste='return false' oncontextmenu='return false' onkeydown='return false' onselectstart='return false' ondragstart='return false'";
			//Mensaje 
			if(print.equals("1") || exportExcel.equals("1")) { %>
				<script>
					alert('No tiene permisos para imprimir/exportar el documento, el documento saldr\u00E1 en blanco');
				</script>
			<% }
		}
		 
		%>
		<head>
		<title>:::<%= title %>:::</title>
		<link rel="stylesheet" type="text/css" href="<%= sFParams.getAppURL()%>css/<%= defaultCss %>"> 
		</head>
		<body class="default" <%= permissionPrint %>>
		
		<%
			try {
			    int projectId = 0;
			    if (request.getParameter("foreignId") != null) projectId = Integer.parseInt(request.getParameter("foreignId"));
			    
			    String sql = "";
			    
			    // Conexciones BD
			    PmConn pmConnCustomer= new PmConn(sFParams);
				pmConnCustomer.open();
			    
			    PmProject pmProject = new PmProject(sFParams);
			    bmoProject = (BmoProject)pmProject.get(projectId);
			    
			    BmoOrder bmoOrder = new BmoOrder();
			    PmOrder pmOrder = new PmOrder(sFParams);
			    bmoOrder = (BmoOrder)pmOrder.get(bmoProject.getOrderId().toInteger());
			    
			    BmoProjectDetail bmoProjectDetail = new BmoProjectDetail();
			    PmProjectDetail pmProjectDetail = new PmProjectDetail(sFParams);
			    bmoProjectDetail = (BmoProjectDetail)pmProjectDetail.getBy(projectId, bmoProjectDetail.getProjectId().getName());
			    
			    if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) throw new Exception("El pedido no esta Autorizado/Liberado - no se puede desplegar.");
			 			    
			    BmoCompany bmoCompany = new BmoCompany();
				PmCompany pmCompany = new PmCompany(sFParams);
				bmoCompany = (BmoCompany)pmCompany.get(bmoProject.getCompanyId().toInteger());
				
				// Imagen empresa, si no hay imagen de la empresa, toma por defecto el logo de la APP de conf
				String logoURL ="";
				if (!bmoCompany.getLogo().toString().equals(""))
					logoURL = HtmlUtil.parseImageLink(sFParams, bmoCompany.getLogo());
				else 
					logoURL = sFParams.getMainImageUrl();
				
				// Obtener Direccion del evento
				String venue = "", venueDir =  "";
				// direccion cliente
				BmoCustomerAddress bmoCustomerAddress = new BmoCustomerAddress();
				PmCustomerAddress pmCustomerAddress = new PmCustomerAddress(sFParams);
				// direccion del salon
				BmoVenue bmoVenue = new BmoVenue();
				PmVenue pmVenue = new PmVenue(sFParams);
				
				// Si viene de un salon tomar datos, sino de la direccion del cliente
				if (bmoProject.getVenueId().toInteger() > 0) {
					bmoVenue = (BmoVenue)pmVenue.get(bmoProject.getVenueId().toInteger());

				} else if (bmoProject.getCustomerAddressId().toInteger() > 0) {
					bmoCustomerAddress = (BmoCustomerAddress)pmCustomerAddress.get(bmoProject.getCustomerAddressId().toInteger());
				}
				
				// Correo del cliente, si no del catalogo de correos del cliente
				String emailCust = "";
				BmoCustomerEmail bmoCustomerEmail = new BmoCustomerEmail();
				PmCustomerEmail pmCustomerEmail = new PmCustomerEmail(sFParams);
				
				int countEmailCust = 1;
				if (!bmoProject.getBmoCustomer().getEmail().toString().equals("")) {
					emailCust = bmoProject.getBmoCustomer().getEmail().toString();
					countEmailCust++;
				}
				
				String sqlCust = " SELECT cuem_email FROM customeremails WHERE cuem_customerid = " + bmoProject.getBmoCustomer().getId();
				pmConnCustomer.doFetch(sqlCust);
				
				while (pmConnCustomer.next()) {
					if (countEmailCust > 1) emailCust += ", ";
					emailCust += pmConnCustomer.getString("cuem_email");
					countEmailCust++;
				}
				
				pmConnCustomer.close();
		%>
		
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		      <tr>
		            <td align="left" width="" rowspan="6" valign="top">
						<img border="0" width="<%= SFParams.LOGO_WIDTH %>" height="<%= SFParams.LOGO_HEIGHT %>" src="<%= logoURL %>" >
		            </td>
		            <td colspan="4" class="reportHeaderCell">
		                Datos del Evento
		            </td>
		      </tr>     
		      <tr>
		            <th align = "left" class="reportCellEven">Proyecto:</th>
		            <td class="reportCellEven">
		                <b><%= bmoProject.getCode().toString() %></b> <%= bmoProject.getName().toString() %>                 
		            </td>
		            <th align = "left" class="reportCellEven">Evento:</th>
		            <td class="reportCellEven">
		                <%= bmoProject.getBmoWFlow().getName() %>                 
		            </td>
		      </tr>
		      <tr>
		            <th align = "left" class="reportCellEven">Tipo:</th>
		            <td class="reportCellEven">
		                <%=  bmoProject.getBmoWFlow().getBmoWFlowType().getName().toString() %>                 
		            </td>
		            <th align = "left" class="reportCellEven">Cliente:</th>
		            <td class="reportCellEven">
		                <b><%= bmoProject.getBmoCustomer().getCode().toString() %></b> <%= bmoProject.getBmoCustomer().getDisplayName().toString() %>                
		            </td>            
		      </tr>
		      <tr>
		            <th align = "left" class="reportCellEven">Tel&eacute;fonos Cliente:</th>
		            <td class="reportCellEven">
		                <%
					    	if (!bmoProject.getBmoCustomer().getMobile().toString().equals("") 
					    			|| !bmoProject.getBmoCustomer().getPhone().toString().equals("") ) {
			
					    		if (!bmoProject.getBmoCustomer().getPhone().toString().equals("")) {%>
					    			Tel.: <%= bmoProject.getBmoCustomer().getPhone().toString() %><%
					    			if (!bmoProject.getBmoCustomer().getMobile().toString().equals("")) { %>,<% }
				    			} 
					    		if (!bmoProject.getBmoCustomer().getMobile().toString().equals("")) { %>
					    			M&oacute;vil: <%= bmoProject.getBmoCustomer().getMobile().toString() %>
				    		<%	}
			    			} else {
			    				String homePhone = "", officePhone = "", movilPhone = "";
						    	BmoCustomerPhone bmoCustomerPhone = new BmoCustomerPhone();
						    	PmCustomerPhone pmCustomerPhone = new PmCustomerPhone(sFParams);
						    	BmFilter bmFilter = new BmFilter();
						    	bmFilter.setValueFilter(bmoCustomerPhone.getKind(), bmoCustomerPhone.getCustomerId().getName(), 
						    			bmoProject.getBmoCustomer().getId());	      
						    	Iterator<BmObject> fields = pmCustomerPhone.list(bmFilter).iterator();
						    	while (fields.hasNext()) { 
						    		bmoCustomerPhone = (BmoCustomerPhone)fields.next();
						    		%> 	    	   
						    		<%= bmoCustomerPhone.getType().getSelectedOption().getLabel() %>: <%= bmoCustomerPhone.getNumber().toHtml() %>, 
					    		<%	
					    		}
			    			}
			    		%>     
		            </td>
		            <th align = "left" class="reportCellEven">Emails Cliente:</th>
		            <td class="reportCellEven">
						<%= emailCust %>
		            </td>            
		      </tr>
		      <tr>
		            <th align = "left" class="reportCellEven">Inicia:</th>
		            <td class="reportCellEven">
		                <%= bmoProject.getStartDate() %>                 
		            </td>
		            <th align = "left" class="reportCellEven">Finaliza:</th>
		            <td class="reportCellEven">
		                <%= bmoProject.getEndDate() %>
		            </td>
		      </tr>      
		      <tr>           
		            <th align = "left" class="reportCellEven">Estatus:</th>
		            <td class="reportCellEven">
		                <%= bmoProject.getStatus().getSelectedOption().getLabel() %>                 
		            </td>  
		            <th align = "left" class="reportCellEven">Invitados:</th>
		            <td class="reportCellEven">
		                <%=  bmoProject.getGuests().toString() %>                 
		            </td>           
		      </tr>           
		</table>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px"> 
		        <tr>
		            <td colspan="4" class="reportHeaderCell">
		                Datos Generales                
		            </td>
		        </tr>
		        <TR class="">                    
		            <th class="reportCellEven" align="left">Preparaci&oacute;n:</th>
		            <td class="reportCellEven" align="left"><%= bmoProjectDetail.getPrepDate().toString() %></td>
		            <th class="reportCellEven" align="left">Entrega:</th>
		            <td class="reportCellEven" align="left"><%= bmoProjectDetail.getDeliveryDate().toString() %></td>
		       </TR>        
		       <TR>    
		            <th class="reportCellEven" align="left">Inicio Montaje:</th>
		            <td class="reportCellEven" align="left"><%= bmoProjectDetail.getLoadStartDate().toString() %></td>
		            <th class="reportCellEven" align="left">Inicio Desmontaje:</th>
		            <td class="reportCellEven" align="left"><%= bmoProjectDetail.getUnloadStartDate().toString() %></td>
		       </TR>
		       <TR>    
		            <th class="reportCellEven" align="left">Salida:</th>
		            <td class="reportCellEven" align="left"><%= bmoProjectDetail.getExitDate().toString() %></td>
		            <th class="reportCellEven" align="left">Regreso:</th>
		            <td class="reportCellEven" align="left"><%= bmoProjectDetail.getReturnDate().toString() %></td>
		        </TR>         
		</table>
		<br> 
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		      <tr>
		            <td colspan="6" class="reportHeaderCell">
		                Datos del Lugar / Ubicaci&oacute;n
		            </td>
		        </tr>          
		        <TR class="">                    
		            <td class="" align="left" colspan="6"></td>
		        </TR> 
		        <% if (bmoProject.getBmoVenue().getId() > 0) { %>      
			        <TR class="">                    
			            <th class="reportCellEven" align="left">Nombre</th>
			            <% 	if (bmoProject.getBmoVenue().getHomeAddress().toInteger() > 0) { %>
					            <th class="reportCellEven" align="left">Direcci&oacute;n</th>                                  
			            <%	} else { %>
				             	<th class="reportCellEven" align="left">Direcci&oacute;n</th>                                  
				            	<th class="reportCellEven" align="left">Ciudad</th>
			            <%	} %>
			            <th class="reportCellEven" align="left">Tel&eacute;fonos</th>
			            <th class="reportCellEven" align="left">Email</th>
			        </TR> 
			        <TR>
			           <td class="reportCellEven" align ="left" colspan="">  
			                <%= bmoVenue.getCode().toString() %> <%= bmoVenue.getName().toString() %>
			           </td>
			           <% 	if (bmoProject.getBmoVenue().getHomeAddress().toInteger() > 0) { %>
			          			<td class="reportCellEven" align ="left" colspan="">
					             	<%= bmoProject.getHomeAddress().toString() %>
					          	</td> 
			           <%	} else { %>
					           	<td class="reportCellEven" align ="left" colspan="">
					           		<%= bmoVenue.getName().toString() %>:
					             	<%= bmoVenue.getStreet().toString() %> 
					             	#<%= bmoVenue.getNumber().toString() %> 
					             	<%= bmoVenue.getNeighborhood().toString() %>
					           	</td> 
					           	<td class="reportCellEven" align ="left" colspan="">  
					           		<%= bmoVenue.getBmoCity().getName().toString() + " " + 
					           				bmoVenue.getBmoCity().getBmoState().getName().toString() + " " +
					           				bmoVenue.getBmoCity().getBmoState().getBmoCountry().getName().toString()
				                	%>
					           	</td>
			           <%	} %>
			           <td class="reportCellEven" align ="left" colspan=""> 
							<%= bmoVenue.getContactPhone().toString() %> <%= bmoVenue.getContactPhoneExt().toString() %><br>
							<%= bmoVenue.getContactPhone2().toString() %> <%= bmoVenue.getContactPhoneExt2().toString() %><br>
			           </td>
			           <td class="reportCellEven" align ="left" colspan=""> 
							<%= bmoVenue.getContactEmail().toString() %>
			           </td>
			       </TR>
		        <% } %>  
		       <tr>
		        <td colspan="9" class="">
		                            
		        </td>
		        </tr>
		</TABLE>
		<br>
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
		        <tr>
		            <td colspan="7" class="reportHeaderCell">
		                Colaboradores                
		            </td>
		        </tr>
		        <tr>
		            <td colspan="10"></td>
		        </tr>
		        <TR class="">                    
		            <th class="reportCellEven" align="left">Nombre</th>                                  
		            <th class="reportCellEven" align="left">Email</th>
		            <th class="reportCellEven" align="left">Tel&eacute;fonos</th>
		            <th class="reportCellEven" align="left">Departamento</th>
		            <th class="reportCellEven" align="left">F. Inicio</th>
		            <th class="reportCellEven" align="left">F. Fin</th>
		            <th class="reportCellEven" align="left">Estatus</th>
		        </TR>
		      
					<%
						BmoWFlowUser bmoWFlowUser = new BmoWFlowUser();
						BmFilter wFlowUserByWFlow = new BmFilter();
						wFlowUserByWFlow.setValueFilter(bmoWFlowUser.getKind(), bmoWFlowUser.getWFlowId(), bmoProject.getWFlowId().toInteger());
						Iterator<BmObject> wFlowUserList = new PmWFlowUser(sFParams).list(wFlowUserByWFlow).iterator();
						while (wFlowUserList.hasNext()) {
							bmoWFlowUser = (BmoWFlowUser)wFlowUserList.next();
					%>
			           <TR class="">             
			               <td class="reportCellEven" align="left">
			                  <%= bmoWFlowUser.getBmoUser().getFirstname().toString() %>  <%= bmoWFlowUser.getBmoUser().getFatherlastname().toString() %>
			               </td>
			               <td class="reportCellEven" align="left">
			                  <%= bmoWFlowUser.getBmoUser().getEmail().toString() %>
			               </td>
			               <td class="reportCellEven" align="left">
								<%
									BmoUserPhone bmoUserPhone = new BmoUserPhone();
									BmFilter phonesByUser = new BmFilter();
									phonesByUser.setValueFilter(bmoUserPhone.getKind(), bmoUserPhone.getUserId(), bmoWFlowUser.getUserId().toInteger());
									Iterator<BmObject> userPhoneList = new PmUserPhone(sFParams).list(phonesByUser).iterator();
									while (userPhoneList.hasNext()) {
										bmoUserPhone = (BmoUserPhone)userPhoneList.next();
								%>
										<%= bmoUserPhone.getNumber().toString() %>, 
								<%
									}
								%>                
			               </td>
			               <td class="reportCellEven" align="left">
			                  <%= bmoWFlowUser.getBmoUser().getBmoArea().getName().toString() %>                  
			               </td> 
			               <td class="reportCellEven" align="left">
			                  <%= bmoWFlowUser.getLockStart().toString() %>
			               </td>
			               <td class="reportCellEven" align="left">
			                  <%= bmoWFlowUser.getLockEnd().toString() %>
			               </td> 
		                   <td class="reportCellEven" align="left">
		                        <%= bmoWFlowUser.getLockStatus().getSelectedOption().getLabel() %>
		                   </td>                   
			          </TR>
		          <%
		            }
		           %>
		        
		</table> 
		<br>    
		<br> 
		<table border="0" cellspacing="0" width="100%" cellpadding="0" style="font-size: 12px">
	        <tr>
	            <td colspan="9" class="reportHeaderCell">
	                Pedido                
	            </td>
	        </tr>
	        <tr>
	            <td colspan="9">&nbsp;</td>
	        </tr>
			
			<%
				BmoOrderStaff bmoOrderStaff = new BmoOrderStaff();
				BmFilter filterOrderStaff = new BmFilter();
				filterOrderStaff.setValueFilter(bmoOrderStaff.getKind(), bmoOrderStaff.getOrderId(), bmoOrder.getId());
				Iterator<BmObject> listOrderStaff = new PmOrderStaff(sFParams).list(filterOrderStaff).iterator();
				int iOStaff = 1;
				while (listOrderStaff.hasNext()) {
					bmoOrderStaff = (BmoOrderStaff)listOrderStaff.next();
					if (iOStaff == 1) {
				%>
						<tr>
				            <td colspan="9" class="reportHeaderCell">
				                Personal                
				            </td>
				        </tr>
				        <TR class="">                    
				            <th class="reportCellEven" align="left">Cantidad</th>
				            <th class="reportCellEven" align="left" colspan="2">Nombre</th>
				            <th class="reportCellEven" align="left">Detalle</th>            
				            <th class="reportCellEven" align="left">D&iacute;as</th>            
							<th class="reportCellEven" align="left" colspan="2">Estatus</th>
				        </TR>
	        	<%	} %>
		      		<TR class=""> 
			        	<td class="reportCellEven" align="left">
		                	<%= bmoOrderStaff.getQuantity().toString() %>                  
			           	</td>
			           	<td class="reportCellEven" align="left" colspan="2"> 
			                <%= bmoOrderStaff.getName().toString() %>  
			                <%//= bmoOrderStaff.getBmoProfile().getCode().toString() %> 
			                <%= bmoOrderStaff.getBmoProfile().getName().toString() %>                
			           	</td>
			           	<td class="reportCellEven" align="left">
			                <%= bmoOrderStaff.getDescription().toString() %>                  
			          	</td>
			           	<td class="reportCellEven" align="left">
			                <%= bmoOrderStaff.getDays().toString() %>                   
			           	</td>                   
			           	<td class="reportCellEven" align="left" colspan="2">
			                <%= bmoOrderStaff.getLockStatus().getSelectedOption().getLabel() %>
			           	</td>
		      		</TR>
		      		<tr>
			            <td colspan="9">&nbsp;</td>
			        </tr>
	      	<%
	      			iOStaff++;
	        	}

				BmoOrderGroup bmoOrderGroup = new BmoOrderGroup();
				BmFilter filterOrderGroup = new BmFilter();
				filterOrderGroup.setValueFilter(bmoOrderGroup.getKind(), bmoOrderGroup.getOrderId(), bmoOrder.getId());
				Iterator<BmObject> listOrderGroup = new PmOrderGroup(sFParams).list(filterOrderGroup).iterator();
				while (listOrderGroup.hasNext()) {
					bmoOrderGroup = (BmoOrderGroup)listOrderGroup.next();
			%>
			<tr>
	            <td colspan="9" class="reportHeaderCell"> <%= bmoOrderGroup.getName().toString() %> </td>
	        </tr>
	        <TR class="">                    
	            <th class="reportCellEven" align="left">Cantidad</th>
	            <th class="reportCellEven" align="left">Nombre</th>
	            <th class="reportCellEven" align="left">Modelo</th>                                  
	            <th class="reportCellEven" align="left">Detalle</th>            
	            <th class="reportCellEven" align="left">D&iacute;as</th>            
	            <th class="reportCellEven" align="left">Tipo</th>
				<th class="reportCellEven" align="left">Estatus</th>
	        </TR>
	        
					<%
						BmoOrderItem bmoOrderItem = new BmoOrderItem();
						BmFilter filterOrderItem = new BmFilter();
						filterOrderItem.setValueFilter(bmoOrderItem.getKind(), bmoOrderItem.getOrderGroupId(), bmoOrderGroup.getId());
						Iterator<BmObject> listOrderItem = new PmOrderItem(sFParams).list(filterOrderItem).iterator();
						while (listOrderItem.hasNext()) {
							bmoOrderItem = (BmoOrderItem)listOrderItem.next();
					%>
	               <TR class=""> 
	                   <td class="reportCellEven" align="left">
	                        <%= bmoOrderItem.getQuantity().toString() %>                  
	                   </td>
	                   <td class="reportCellEven" align="left">
		                   <% if (bmoOrderItem.getBmoProduct().getDisplayName().toString().equals("")) { %>
                      			<%= bmoOrderItem.getName().toHtml() %>
                      			<%= bmoOrderItem.getBmoProduct().getBrand().toHtml() %>
                      			<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %>
                     		<% } else { %>
								<%= bmoOrderItem.getBmoProduct().getDisplayName().toHtml() %>
								<%= bmoOrderItem.getBmoProduct().getBrand().toHtml() %>
                      			<%= bmoOrderItem.getBmoProduct().getModel().toHtml() %>
							<% } %>
	                   </td>
	                   <td class="reportCellEven" align="left">
	                        <%= bmoOrderItem.getBmoProduct().getModel().toString() %>                  
	                   </td>
	                   <td class="reportCellEven" align="left">
	                        <%= bmoOrderItem.getDescription().toString() %>                  
	                   </td>
	                   <td class="reportCellEven" align="left">
	                        <%= bmoOrderItem.getDays().toString() %>                   
	                   </td>                   
	                   <td class="reportCellEven" align="left">
	                        <%= bmoOrderItem.getBmoProduct().getType().getSelectedOption().getLabel() %>
	                   </td>
	                   <td class="reportCellEven" align="left">
	                        <%= bmoOrderItem.getLockStatus().getSelectedOption().getLabel() %>
	                   </td>
	              </TR>
		          <%
		            }
		           %>
			<%
	        }
	       	%>
	        <tr>
	            <td colspan="9">&nbsp;</td>
	        </tr>
			<%
				BmoOrderEquipment bmoOrderEquipment = new BmoOrderEquipment();
				BmFilter filterOrderEquipment = new BmFilter();
				filterOrderEquipment.setValueFilter(bmoOrderEquipment.getKind(), bmoOrderEquipment.getOrderId(),bmoOrder.getId());
				Iterator<BmObject> listOrderEquipment = new PmOrderEquipment(sFParams).list(filterOrderEquipment).iterator();
				int iOEquipment = 1;
				while (listOrderEquipment.hasNext()) {
					bmoOrderEquipment = (BmoOrderEquipment)listOrderEquipment.next();
					if (iOEquipment == 1) {
						%>
						<tr>
				            <td colspan="9" class="reportHeaderCell">
				                Recursos                
				            </td>
				        </tr>
				        <TR class="">                    
				            <th class="reportCellEven" align="left">Cantidad</th>
				            <th class="reportCellEven" align="left">Nombre</th>
				            <th class="reportCellEven" align="left">Modelo</th>                                  
				            <th class="reportCellEven" align="left">Detalle</th>            
				            <th class="reportCellEven" align="left">D&iacute;as</th>            
				            <th class="reportCellEven" align="left">Tipo</th>
				            <th class="reportCellEven" align="left">Estatus</th>
				        </TR>
       		<%		} %>
			       <TR class=""> 
			           <td class="reportCellEven" align="left">
			                <%= bmoOrderEquipment.getQuantity().toString() %>                  
			           </td>
			           <td class="reportCellEven" align="left">
			                <%= bmoOrderEquipment.getName().toString() %>                 
			           </td>
			           <td class="reportCellEven" align="left">
			                <%= bmoOrderEquipment.getBmoEquipment().getModel().toString() %>                  
			           </td>
			           <td class="reportCellEven" align="left">
			                <%= bmoOrderEquipment.getDescription().toString() %>                  
			           </td>
			           <td class="reportCellEven" align="left">
			                <%= bmoOrderEquipment.getDays().toString() %>                   
			           </td>                   
			           <td class="reportCellEven" align="left">
			                <%= bmoOrderEquipment.getBmoEquipment().getBmoEquipmentType().getName().toString() %>
			           </td>
			           <td class="reportCellEven" align="left">
			                <%= bmoOrderEquipment.getLockStatus().getSelectedOption().getLabel() %>
		           	   </td>
			      	</TR>
	      		<%
	      			iOEquipment++;
	        	}
	       		%>
		        <tr>
		            <td colspan="9">&nbsp;</td>
		        </tr> 
			</table>  
		
		<p class="documentTitleCaption" align="left"> 
			Fecha de Impresi&oacute;n <%= SFServerUtil.nowToString(sFParams, "dd/MM/yyyy HH:mm") %>
		</p> 
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
		    <tr align="center">
		        <td align="center"><br><br>
		            _______________________________________
		        </td>
		        <td align="center"><br><br>
		            
		        </td>
		        <td align="center"><br><br>
		            _______________________________________
		        </td>
		    </tr>
		    <tr>
		            <td align="center" class="documentTitleCaption">               
		                <br>
		                Responsable del Evento
		                <br>Team Leader 
		            </td>
		            <td align="center" class="documentTitleCaption"><br>
		                <br>            
		            </td>
		            <td align="center" class="documentTitleCaption"><br>
		                Responsable Almac&eacute;n <br>            
		            </td>
		        </tr>
		</table>
		<%  
			} catch (Exception e) { 
			    String errorLabel = "Error del CheckList";
			    String errorText = "El checkList no pudo ser desplegado exitosamente.";
			    String errorException = e.toString();
			    
			    response.sendRedirect(sFParams.getAppURL() + "/jsp/error.jsp?errorLabel=" + errorLabel + "&errorText=" + errorText + "&errorException=" + errorException);
		    }
		%>