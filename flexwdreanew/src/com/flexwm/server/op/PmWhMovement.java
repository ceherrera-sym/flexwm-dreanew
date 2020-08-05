/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoWhBoxTrack;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;



public class PmWhMovement extends PmObject {
	BmoWhMovement bmoWhMovement;

	public PmWhMovement(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhMovement = new BmoWhMovement();
		setBmObject(bmoWhMovement);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWhMovement.getOrderDeliveryId(), bmoWhMovement.getBmoOrderDelivery()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getOrderId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getOrderTypeId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoOrderType()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getCurrencyId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCurrency()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getCustomerId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getSalesmanId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getBmoUser().getAreaId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getBmoUser().getLocationId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getTerritoryId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getWFlowId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlow()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlow().getWFlowPhaseId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getWFlowTypeId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlowType()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlowType().getWFlowCategoryId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlow().getWFlowFunnelId(), bmoWhMovement.getBmoOrderDelivery().getBmoOrder().getBmoWFlow().getBmoWFlowFunnel()),
				new PmJoin(bmoWhMovement.getCompanyId(), bmoWhMovement.getBmoCompany()),
				new PmJoin(bmoWhMovement.getToWhSectionId(), bmoWhMovement.getBmoToWhSection()),
				new PmJoin(bmoWhMovement.getBmoToWhSection().getWarehouseId(), bmoWhMovement.getBmoToWhSection().getBmoWarehouse()),
				//new PmJoin(bmoWhMovement.getBmoToWhSection().getBmoWarehouse().getCompanyId(), bmoWhMovement.getBmoToWhSection().getBmoWarehouse().getBmoCompany()),
				new PmJoin(bmoWhMovement.getRequisitionReceiptId(), bmoWhMovement.getBmoRequisitionReceipt()),
				new PmJoin(bmoWhMovement.getBmoRequisitionReceipt().getRequisitionId(), bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition()),
				new PmJoin(bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getSupplierId(), bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getBmoSupplier()),
				new PmJoin(bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getBmoSupplier().getSupplierCategoryId(), bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getReqPayTypeId(), bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getBmoReqPayType()),
				new PmJoin(bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getRequisitionTypeId(), bmoWhMovement.getBmoRequisitionReceipt().getBmoRequisition().getBmoRequisitionType())

				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de tx-inventario de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = "( whmv_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " whmv_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWhMovement = (BmoWhMovement) autoPopulate(pmConn, new BmoWhMovement());		

		// BmoWhSection
		BmoWhSection bmoWhSection = new BmoWhSection();
		int whSectionId = pmConn.getInt(bmoWhSection.getIdFieldName());
		if (whSectionId > 0) bmoWhMovement.setBmoToWhSection((BmoWhSection) new PmWhSection(getSFParams()).populate(pmConn));
		else bmoWhMovement.setBmoToWhSection(bmoWhSection);

		// BmoOrderDelivery
		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
		int orderDeliveryId = pmConn.getInt(bmoOrderDelivery.getIdFieldName());
		if (orderDeliveryId > 0) bmoWhMovement.setBmoOrderDelivery((BmoOrderDelivery) new PmOrderDelivery(getSFParams()).populate(pmConn));
		else bmoWhMovement.setBmoOrderDelivery(bmoOrderDelivery);

		// BmoRequisitionReceipt
		BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
		int requisitionReceiptId = pmConn.getInt(bmoRequisitionReceipt.getIdFieldName());
		if (requisitionReceiptId > 0) bmoWhMovement.setBmoRequisitionReceipt((BmoRequisitionReceipt) new PmRequisitionReceipt(getSFParams()).populate(pmConn));
		else bmoWhMovement.setBmoRequisitionReceipt(bmoRequisitionReceipt);

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoWhMovement.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoWhMovement.setBmoCompany(bmoCompany);

		return bmoWhMovement;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoWhMovement = (BmoWhMovement)bmObject;

		// Es un registro nuevo, recuperar ID y clave
		if (!(bmoWhMovement.getId() > 0)) {
			// Es un registro nuevo, guardar una vez para recuperar ID, luego guardar de nuevo con la clave asignada
			super.save(pmConn, bmoWhMovement, bmUpdateResult);
			bmoWhMovement.setId(bmUpdateResult.getId());
			bmoWhMovement.getCode().setValue(bmoWhMovement.getCodeFormat());
		}

		// Si se esta cambiando el estatus a En Revision, se modifica para poder hacer el resto de procesos
		if (bmoWhMovement.getStatus().toChar() == BmoWhMovement.STATUS_REVISION)
			super.save(pmConn, bmoWhMovement, bmUpdateResult);

		// Entrada de Almacen
		if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN) {
			//			PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
			//			BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoWhMovement.getRequisitionReceiptId().toInteger());
			//
			//			// La orden de compra no esta autorizada
			//			if (bmoRequisitionReceipt.getBmoRequisition().getStatus().toChar() != BmoRequisition.STATUS_AUTHORIZED) 
			//				bmUpdateResult.addError(bmoWhMovement.getRequisitionReceiptId().getName(), "La Orden de Compra no está Autorizada.");
			//			// La orden de compra ya fue surtida en su totalidad
			//			else if (newRecord && bmoRequisitionReceipt.getBmoRequisition().getDeliveryStatus().toChar() == BmoRequisition.DELIVERYSTATUS_TOTAL) 
			//				bmUpdateResult.addError(bmoWhMovement.getRequisitionReceiptId().getName(), "La Orden de Compra ya fue recibida en su totalidad.");
			//			// Parece no haber problemas, seguir
			//			else {
			//				// Crear los items de la orden de compra si es nuevo registro
			////				if (newRecord && bmoWhMovement.getAutoCreateItems().toBoolean()) 
			//					//createWhMovItemsFromRequisitionReceipt(pmConn, bmoWhMovement, bmoRequisitionReceipt, bmUpdateResult);
			//			}

			// Entrada por ajuste de almacen
		} else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_IN_ADJUST) {

			// Revisar permiso especial
			if (!getSFParams().hasSpecialAccess(BmoWhMovement.ACCESS_ADJUST)) 
				bmUpdateResult.addError(bmoWhMovement.getType().getName(), "No tiene permisos para hacer ajustes de Inventario");

			if (!(bmoWhMovement.getToWhSectionId().toInteger() > 0))
				bmUpdateResult.addError(bmoWhMovement.getToWhSectionId().getName(), "Seleccione la Sección de Almacén");

			// Salida de almacen
		} else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT) {
			//			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
			//			BmoOrderDelivery bmoOrderDelivery = (BmoOrderDelivery)pmOrderDelivery.get(pmConn, bmoWhMovement.getOrderDeliveryId().toInteger());

			//			if (bmoOrderDelivery.getStatus().toChar() != BmoOrderDelivery.STATUS_AUTHORIZED) 
			//				bmUpdateResult.addError(bmoWhMovement.getOrderDeliveryId().getName(), "El Envío del Pedido no está Autorizado.");
			//			else if (newRecord && bmoOrderDelivery.getDeliveryStatus().toChar() == BmoOrderDelivery.DELIVERYSTATUS_TOTAL)
			//				bmUpdateResult.addError(bmoWhMovement.getOrderId().getName(), "El Pedido ya fue surtido en su totalidad.");
			//			else 
			//				if (newRecord && bmoWhMovement.getAutoCreateItems().toBoolean()) createWhMovItemsFromOrder(pmConn, bmoWhMovement, bmoOrder, bmUpdateResult);

			// Devolucion de salida
		} else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_DEV) {
			//			PmOrder pmOrder = new PmOrder(getSFParams());
			//			BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoWhMovement.getOrderDeliveryId().toInteger());
			//
			//			if (bmoOrder.getDeliveryStatus().toChar() == BmoOrder.DELIVERYSTATUS_PENDING) 
			//				bmUpdateResult.addError(bmoWhMovement.getOrderDeliveryId().getName(), "No han salido Productos al Pedido seleccionado.");

			// Salida por ajuste
		} else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_OUT_ADJUST) {

			// Revisar permiso especial
			if (!getSFParams().hasSpecialAccess(BmoWhMovement.ACCESS_ADJUST)) 
				bmUpdateResult.addError(bmoWhMovement.getType().getName(), "No tiene permisos para hacer Ajustes de Inventario");

			// Salida de por renta
		} else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_OUT) {
			//			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
			//			BmoOrderDelivery bmoOrderDelivery = (BmoOrderDelivery)pmOrderDelivery.get(pmConn, bmoWhMovement.getOrderDeliveryId().toInteger());
			//
			//			// Obtener la seccion de almacen destino y asignarla al movimiento
			//			PmWhSection pmWhSection = new PmWhSection(getSFParams());
			//			BmoWhSection bmoWhSection = new BmoWhSection();
			//			bmoWhSection = (BmoWhSection)pmWhSection.get(pmConn, bmoOrderDelivery.getWhSectionId().toInteger());
			//			bmoWhMovement.getToWhSectionId().setValue(bmoWhSection.getId());

			//			if (bmoOrderDelivery.getStatus().toChar() != BmoOrderDelivery.STATUS_AUTHORIZED) 
			//				bmUpdateResult.addError(bmoWhMovement.getOrderDeliveryId().getName(), "El Envío del Pedido no está Autorizado.");
			//			else if (newRecord && bmoOrder.getDeliveryStatus().toChar() == BmoOrder.DELIVERYSTATUS_TOTAL)
			//				bmUpdateResult.addError(bmoWhMovement.getOrderDeliveryId().getName(), "El Pedido ya fue surtido en su totalidad.");
			//			else 
			//				if (newRecord && bmoWhMovement.getAutoCreateItems().toBoolean()) createWhMovItemsFromOrder(pmConn, bmoWhMovement, bmoOrder, bmUpdateResult);

			// Entrada de Renta
		} else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_RENTAL_IN) {
			//			PmOrder pmOrder = new PmOrder(getSFParams());
			//			BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoWhMovement.getOrderDeliveryId().toInteger());

			// Obtener la seccion de almacen origen y asignarla al movimiento
			//			PmWhSection pmWhSection = new PmWhSection(getSFParams());
			//			BmoWhSection bmoWhSection = new BmoWhSection();
			//			bmoWhSection = (BmoWhSection)pmWhSection.getBy(pmConn, bmoOrder.getId(), bmoWhSection.getOrderId().getName());
			//			bmoWhMovement.getBaseWhSectionId().setValue(bmoWhSection.getId());
			//
			//			if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_AUTHORIZED) 
			//				bmUpdateResult.addError(bmoWhMovement.getOrderDeliveryId().getName(), "El Pedido no está Autorizado.");
			//			else if (newRecord && bmoOrder.getDeliveryStatus().toChar() == BmoOrder.DELIVERYSTATUS_PENDING)
			//				bmUpdateResult.addError(bmoWhMovement.getOrderDeliveryId().getName(), "El Pedido no ha sido Surtido.");
			//			else 
			//				if (newRecord && bmoWhMovement.getAutoCreateItems().toBoolean()) createWhMovItemsFromOrder(pmConn, bmoWhMovement, bmoOrder, bmUpdateResult);

			// Transferencia
		} else if (bmoWhMovement.getType().toChar() == BmoWhMovement.TYPE_TRANSFER) {
			if (!(bmoWhMovement.getToWhSectionId().toInteger() > 0))
				bmUpdateResult.addError(bmoWhMovement.getToWhSectionId().getName(), "Seleccione la Sección de Almacén");
		} 

		// Almacena el movimiento
		if (!bmUpdateResult.hasErrors()) { 
			super.save(pmConn, bmoWhMovement, bmUpdateResult);

			// Actualiza valor del movimiento completo
			if (bmoWhMovement.getId() > 0) updateAmount(pmConn, bmoWhMovement, bmUpdateResult);
			
			 
			
			
		}

		return bmUpdateResult;
	}

	public void updateAmount(PmConn pmConn, BmoWhMovement bmoWhMovement, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "SELECT SUM(whmi_amount) FROM whmovitems WHERE whmi_whmovementid = " + bmoWhMovement.getId();

		pmConn.doFetch(sql);
		double amount = 0;
		if (pmConn.next()) {
			amount = pmConn.getDouble(1);
			bmoWhMovement.getAmount().setValue(amount);
			super.save(pmConn, bmoWhMovement, bmUpdateResult);
		} else {
			throw new SFException("PmWhMovement-updateAmount() ERROR: no se puede obtener la suma de items del movimiento de almacén.");
		}
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		bmoWhMovement = (BmoWhMovement)this.get(bmObject.getId());

		// Revisar cantidad de items apartados
		if (action.equals(BmoWhMovement.ACTION_WHBOX)) {
			createWhMovItemsFromWhBox(bmoWhMovement, value, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Revisar si existe la el movimiento de la ROC
	public boolean requisitionReceiptWhMovementExists(PmConn pmConn, int requisitionReceiptId) throws SFPmException {
		pmConn.doFetch("SELECT whmv_whmovementid FROM whmovements WHERE whmv_requisitionreceiptid = " + requisitionReceiptId);
		return pmConn.next();
	}

	// Revisar si existe la el movimiento de la ROC
	public boolean orderDeliveryWhMovementExists(PmConn pmConn, int orderDeliveryId) throws SFPmException {
		pmConn.doFetch("SELECT whmv_whmovementid FROM whmovements WHERE whmv_orderdeliveryid = " + orderDeliveryId);
		return pmConn.next();
	}

	public void updateWhMovementFromRequisitionReceipt(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {

		// Obtener orden de compra
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(bmoRequisitionReceipt.getRequisitionId().toInteger());

		// Si es tipo compra, crear el movimiento de almacén
		if (pmRequisition.requisitionAffectsInventory(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger())) {

			PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
			BmoWhMovement bmoWhMovement = new BmoWhMovement();

			// Si no existe el movimiento de almacen, crearlo
			if (!pmWhMovement.requisitionReceiptWhMovementExists(pmConn, bmoRequisitionReceipt.getId())) {
				bmoWhMovement.getDescription().setValue(bmoRequisitionReceipt.getName().toString());
				bmoWhMovement.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
				bmoWhMovement.getDatemov().setValue(bmoRequisitionReceipt.getReceiptDate().toString());
				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);
				bmoWhMovement.getToWhSectionId().setValue(bmoRequisitionReceipt.getWhSectionId().toInteger());
				bmoWhMovement.getCompanyId().setValue(bmoRequisition.getCompanyId().toInteger());

				// Asignaciones dependiendo el tipo de envio
				if (bmoRequisitionReceipt.getType().toChar() == BmoRequisitionReceipt.TYPE_RECEIPT) {
					bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_IN);
				} else if (bmoRequisitionReceipt.getType().toChar() == BmoRequisitionReceipt.TYPE_RETURN){
					bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_IN_DEV);
				}
			} else {
				bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, bmoRequisitionReceipt.getId(), bmoWhMovement.getRequisitionReceiptId().getName());
				if (bmoRequisitionReceipt.getStatus().toChar() == BmoRequisitionReceipt.STATUS_AUTHORIZED) 
					bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
				else 
					bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);

				bmoWhMovement.getDatemov().setValue(bmoRequisitionReceipt.getReceiptDate().toString());
			}

			pmWhMovement.save(pmConn, bmoWhMovement, bmUpdateResult);
		}
	}

	private void createWhMovItemsFromWhBox(BmoWhMovement bmoWhMovement, String whBoxId, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());

		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			// Crear items de ingreso a almacen, con 0 en la cantidad
			BmoWhBoxTrack bmoWhBoxTrack = new BmoWhBoxTrack();
			BmFilter bmFilterByWhBox = new BmFilter();
			bmFilterByWhBox.setValueFilter(bmoWhBoxTrack.getKind(), bmoWhBoxTrack.getWhBoxId(), whBoxId);
			PmWhBoxTrack pmWhBoxTrack = new PmWhBoxTrack(getSFParams());
			Iterator<BmObject> whBoxTrackList = (pmWhBoxTrack.list(pmConn, bmFilterByWhBox)).iterator();

			PmWhMovItem pmWhMovItem = new PmWhMovItem(getSFParams());

			while (whBoxTrackList.hasNext()) {
				BmoWhBoxTrack nextBmoWhBoxTrack = (BmoWhBoxTrack)whBoxTrackList.next();

				// Crea el nuevo item del movimiento
				BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
				bmoWhMovItem.getProductId().setValue(nextBmoWhBoxTrack.getBmoWhTrack().getProductId().toString());
				bmoWhMovItem.getQuantity().setValue(nextBmoWhBoxTrack.getQuantity().toDouble());
				bmoWhMovItem.getAmount().setValue(0);
				bmoWhMovItem.getWhMovementId().setValue(bmoWhMovement.getId());
				bmoWhMovItem.getSerial().setValue(nextBmoWhBoxTrack.getBmoWhTrack().getSerial().toString());
				pmWhMovItem.save(pmConn, bmoWhMovItem, bmUpdateResult);	
			}

			if (!bmUpdateResult.hasErrors()) pmConn.commit();			

		} catch (SFException e) {
			bmUpdateResult.addMsg(e.toString());
			if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-createWhMovItemsFromWhBox() ERROR " + e.toString());
		} finally {
			pmConn.close();
		}
	}

	public BmUpdateResult populateProducts(String populateData) throws SFException {
		BmUpdateResult bmUpdateResult = new BmUpdateResult();

		PmConn pmConn = new PmConn(getSFParams());
		try {
			pmConn.open();
			pmConn.disableAutoCommit();

			bmUpdateResult = this.populateProducts(pmConn, populateData, bmUpdateResult);

			if (!bmUpdateResult.hasErrors()) pmConn.commit();

		} catch (BmException e) {
			pmConn.rollback();
			throw new SFException(e.toString());

		} finally {
			pmConn.close();
		}

		return bmUpdateResult;
	}

	// Actualiza status de disponibilidad de la propiedad
	public BmUpdateResult populateProducts(PmConn pmConn, String populateData, BmUpdateResult bmUpdateResult) throws SFException {		
		PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
		PmWhMovItem pmWhMovItem = new PmWhMovItem(getSFParams());

		//Almacen
		PmWhSection pmWhSection = new PmWhSection(getSFParams());
		BmoWhSection bmoWhSection = new BmoWhSection();

		//Productos
		PmProduct pmProduct = new PmProduct(getSFParams());
		BmoProduct bmoProduct = new BmoProduct();

		String s = "";
		String wareSectionCode = "";	
		String prodCode = "";    
		String prodName = "";
		String quantity = "";
		String serial = "";  		

		int i = 1;		
		StringTokenizer inputData = new StringTokenizer(new String(populateData), "\n");

		while (inputData.hasMoreTokens() && i < 2000) {        

			s = inputData.nextToken();
			StringTokenizer tabs = new StringTokenizer(s, "\t");        			

			//Recuperar valores
			wareSectionCode = tabs.nextToken();        			    				
			prodCode = tabs.nextToken();
			prodName = tabs.nextToken();
			quantity = tabs.nextToken();
			serial = tabs.nextToken();   				    				

			//Obtener el Almacen
			bmoWhSection = (BmoWhSection)pmWhSection.getBy(pmConn, wareSectionCode, bmoWhSection.getName().getName());
			if (!(bmoWhSection.getId() > 0)) bmUpdateResult.addMsg("No Existe la Sección del Almacen: " + wareSectionCode);

			//Crear el Movimiento de Entrada por Ajuste
			BmoWhMovement bmoWhMovement = new BmoWhMovement();
			bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_IN_ADJUST);
			bmoWhMovement.getDescription().setValue("Entrada por Ajuste Automatico");
			bmoWhMovement.getDatemov().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoWhMovement.getToWhSectionId().setValue(bmoWhSection.getId());
			//bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);
			pmWhMovement.save(pmConn, bmoWhMovement, bmUpdateResult);			

			//Crear el Item del Mov Por Ajuste
			BmoWhMovItem bmoWhMovItemNew = new BmoWhMovItem();			

			//Obtener el Producto mediante la clave
			bmoProduct = (BmoProduct)pmProduct.getBy(pmConn, prodCode, bmoProduct.getCode().getName());			
			if (!(bmoProduct.getId() > 0)) {
				bmUpdateResult.addMsg("No Existe el Producto: " + prodCode);
			} else {
				bmoProduct.getEnabled().setValue(1);
				super.save(pmConn, bmoProduct, bmUpdateResult);
			}

			bmoWhMovItemNew.getWhMovementId().setValue(bmoWhMovement.getId());
			bmoWhMovItemNew.getToWhSectionId().setValue(bmoWhSection.getId());
			bmoWhMovItemNew.getProductId().setValue(bmoProduct.getId());			
			bmoWhMovItemNew.getQuantity().setValue(quantity);
			bmoWhMovItemNew.getSerial().setValue(serial);
			pmWhMovItem.save(pmConn, bmoWhMovItemNew, bmUpdateResult);

			//Actualizar el estatus whmovement
			bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
			pmWhMovement.save(pmConn, bmoWhMovement, bmUpdateResult);

			System.out.println("Producto" + prodName);



			i++;	
		}	

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWhMovement = (BmoWhMovement)bmObject;

		if (bmoWhMovement.getStatus().toChar() == BmoWhMovement.STATUS_REVISION) {	
			// Eliminar items
			if (!bmUpdateResult.hasErrors()) {
				PmWhMovItem pmWhMovItem = new PmWhMovItem(getSFParams());
				BmoWhMovItem bmoWhMovItem = new BmoWhMovItem();
				BmFilter filterByWhMovement = new BmFilter();
				filterByWhMovement.setValueFilter(bmoWhMovItem.getKind(), bmoWhMovItem.getWhMovementId(), bmoWhMovement.getId());
				ListIterator<BmObject> whMovItemList = pmWhMovItem.list(pmConn, filterByWhMovement).listIterator();
				while (whMovItemList.hasNext()) {
					bmoWhMovItem = (BmoWhMovItem)whMovItemList.next();
					pmWhMovItem.delete(pmConn,  bmoWhMovItem, bmUpdateResult);
				}
			}
			super.delete(pmConn, bmoWhMovement, bmUpdateResult);

		} else {
			bmUpdateResult.addError(bmoWhMovement.getStatus().getName(), "No se puede eliminar la Transacción de Inventario - está Autorizada.");
		}

		return bmUpdateResult;
	}

}
