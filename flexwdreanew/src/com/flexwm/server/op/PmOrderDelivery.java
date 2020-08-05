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
import com.flexwm.server.cr.PmCredit;
import com.flexwm.server.fi.PmBankAccount;
import com.flexwm.server.fi.PmBankMovConcept;
import com.flexwm.server.fi.PmBankMovType;
import com.flexwm.server.fi.PmBankMovement;
import com.flexwm.server.fi.PmFiscalPeriod;
import com.flexwm.server.fi.PmRaccount;
import com.flexwm.server.fi.PmRaccountItem;
import com.flexwm.server.fi.PmRaccountType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cr.BmoCredit;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountItem;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderDeliveryItem;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoWhBoxTrack;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhSection;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.SQLUtil;
import com.symgae.shared.sf.BmoCompany;


public class PmOrderDelivery extends PmObject {
	BmoOrderDelivery bmoOrderDelivery;

	public PmOrderDelivery(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderDelivery = new BmoOrderDelivery();
		setBmObject(bmoOrderDelivery);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderDelivery.getOrderId(), bmoOrderDelivery.getBmoOrder()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getOrderTypeId(), bmoOrderDelivery.getBmoOrder().getBmoOrderType()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getCurrencyId(), bmoOrderDelivery.getBmoOrder().getBmoCurrency()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getCustomerId(), bmoOrderDelivery.getBmoOrder().getBmoCustomer()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoCustomer().getSalesmanId(), bmoOrderDelivery.getBmoOrder().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoCustomer().getBmoUser().getAreaId(), bmoOrderDelivery.getBmoOrder().getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoCustomer().getBmoUser().getLocationId(), bmoOrderDelivery.getBmoOrder().getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoCustomer().getTerritoryId(), bmoOrderDelivery.getBmoOrder().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoCustomer().getReqPayTypeId(), bmoOrderDelivery.getBmoOrder().getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getWFlowId(), bmoOrderDelivery.getBmoOrder().getBmoWFlow()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoWFlow().getWFlowPhaseId(), bmoOrderDelivery.getBmoOrder().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getWFlowTypeId(), bmoOrderDelivery.getBmoOrder().getBmoWFlowType()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoWFlowType().getWFlowCategoryId(), bmoOrderDelivery.getBmoOrder().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoOrderDelivery.getBmoOrder().getBmoWFlow().getWFlowFunnelId(), bmoOrderDelivery.getBmoOrder().getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();

		// Filtro de oportunidades de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			filters = "( odly_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " odly_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoOrderDelivery = (BmoOrderDelivery)autoPopulate(pmConn, new BmoOrderDelivery());

		// Pedido
		BmoOrder bmoOrder = new BmoOrder();
		int requisitionId = pmConn.getInt(bmoOrder.getIdFieldName());
		if (requisitionId > 0) bmoOrderDelivery.setBmoOrder((BmoOrder) new PmOrder(getSFParams()).populate(pmConn));
		else bmoOrderDelivery.setBmoOrder(bmoOrder);

		return bmoOrderDelivery;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoOrderDelivery = (BmoOrderDelivery)bmObject;

		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoOrderDelivery.getOrderId().toInteger());
		// Operaciones si es nuevo envio
		if (!(bmoOrderDelivery.getId() > 0)) {
			bmoOrderDelivery.getUserCreate().setValue(getSFParams().getLoginInfo().getBmoUser().getFirstname().toString()+" "+
					getSFParams().getLoginInfo().getBmoUser().getFatherlastname().toString()+" "+
					getSFParams().getLoginInfo().getBmoUser().getMotherlastname().toString());
			// Establecer clave y nombre
			super.save(pmConn, bmoOrderDelivery, bmUpdateResult);
			bmoOrderDelivery.getCode().setValue(bmoOrderDelivery.getCodeFormat());
			bmoOrderDelivery.getName().setValue(bmoOrder.getCode().toString());
			bmoOrderDelivery.setId(bmUpdateResult.getId());

			// Establece la seccion destino si es envio
			if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {

				// Establece sección destino si es de tipo renta
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					PmWhSection pmWhSection = new PmWhSection(getSFParams());
					BmoWhSection bmoWhSection = new BmoWhSection();
					bmoWhSection = (BmoWhSection)pmWhSection.getBy(pmConn, bmoOrderDelivery.getOrderId().toInteger(), bmoWhSection.getOrderId().getName());
					bmoOrderDelivery.getToWhSectionId().setValue(bmoWhSection.getId());
				}

			} else {
				if(!getOrderHasTypeDelivery(pmConn , bmoOrderDelivery.getOrderId().toInteger())){
					bmUpdateResult.addError(bmoOrderDelivery.getType().getName(), "Los Envíos de Tipo 'Devolución' requieren primero un Envío de Tipo 'Envío'");
				}
			}

			// Si el Pedido tiene items, crear los items del envío
			if (orderHasItems(pmConn, bmoOrderDelivery.getOrderId().toInteger())) {
				PmOrderDeliveryItem pmOrderDeliveryItem = new PmOrderDeliveryItem(getSFParams());					
				pmOrderDeliveryItem.createItemsFromOrder(pmConn, bmoOrderDelivery, bmUpdateResult);
			}
		}
		
		// Validar perido operativo de acuerdo a configuracion.
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequiredPeriodFiscal().toBoolean() ) {
			printDevLog("Envio Ped: INICIO Periodo Operativo(requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsOpen = pmFiscalPeriod.isOpen(pmConn, bmoOrderDelivery.getDeliveryDate().toString().substring(0, 10), bmoOrderDelivery.getCompanyId().toInteger());
			printDevLog("Envio Ped: FIN Periodo Operativo: "+fiscalPeriodIsOpen);

			if (!fiscalPeriodIsOpen)
				bmUpdateResult.addError(bmoOrderDelivery.getDeliveryDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoOrderDelivery.getDeliveryDate().toString().substring(0, 10) + ").");
		} else {
			printDevLog("Envio Ped: INICIO Periodo Operativo(NO requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsClosed = pmFiscalPeriod.isClosed(pmConn, bmoOrderDelivery.getDeliveryDate().toString().substring(0, 10), bmoOrderDelivery.getCompanyId().toInteger());
			printDevLog("Envio Ped: FIN Periodo Operativo: "+fiscalPeriodIsClosed);

			if (fiscalPeriodIsClosed)
				bmUpdateResult.addError(bmoOrderDelivery.getDeliveryDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoOrderDelivery.getDeliveryDate().toString().substring(0, 10) + ").");
		}

		// Actualiza registros actuales
		super.save(pmConn, bmoOrderDelivery, bmUpdateResult);

		// Actualizar totales del envío
		setAmounts(pmConn, bmoOrderDelivery);

		// Actualizar el estatus de entrega de la OC
		pmOrder.updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);

		if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			// Operaciones con movimientos de almacen
			updateWhMovement(pmConn, bmoOrder, bmoOrderDelivery, bmUpdateResult);
		} 

		// Se almacena para establecer id correcto
		bmUpdateResult = super.save(pmConn, bmoOrderDelivery, bmUpdateResult);

		return bmUpdateResult;
	}	

	public BmUpdateResult updateBalance(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoOrderDelivery = (BmoOrderDelivery)bmObject;

		// Actualizar estatus de envio del pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		pmOrder.updateDeliveryStatus(pmConn, bmoOrderDelivery.getBmoOrder(), bmUpdateResult);

		// Calcular montos
		setAmounts(pmConn, bmoOrderDelivery);	

		return super.save(pmConn, bmoOrderDelivery, bmUpdateResult);		
	}

	private void setAmounts(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery) throws SFException {
		double amount = 0;
		String sql = " SELECT sum(odyi_amount) FROM orderdeliveryitems " +
				" WHERE odyi_orderdeliveryid = " + bmoOrderDelivery.getId() + 
				" AND odyi_quantity > 0 ";		
		pmConn.doFetch(sql);

		if (pmConn.next()) amount = pmConn.getDouble(1);		
		// Calcular montos
		if (amount == 0) {			
			bmoOrderDelivery.getAmount().setValue(0);
			bmoOrderDelivery.getTax().setValue(0);
			bmoOrderDelivery.getTotal().setValue(0);			
		} else {			
			bmoOrderDelivery.getAmount().setValue(amount);

			//Obtener el Iva
			if (getOrderHasTax(pmConn, bmoOrderDelivery.getOrderId().toInteger())) {
				double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
				bmoOrderDelivery.getTax().setValue(amount * taxRate);
				bmoOrderDelivery.getTotal().setValue(amount +  bmoOrderDelivery.getTax().toDouble());
			} else {
				bmoOrderDelivery.getTax().setValue(0);
				bmoOrderDelivery.getTotal().setValue(amount);
			}	
		}
	}

	private boolean getOrderHasTax(PmConn pmConn, int orderId) throws SFException {
		boolean result = false;

		String sql = "";
		double reqiTax = 0;

		//Sumar los items de la OC		
		sql  = " SELECT orde_tax from orders " +
				" WHERE orde_orderid = " + orderId;
		pmConn.doFetch(sql);
		if (pmConn.next()) reqiTax = pmConn.getDouble("orde_tax");

		if (reqiTax > 0) result = true;			

		return result;
	}

	private boolean orderHasItems(PmConn pmConn, int orderId) throws SFException {
		boolean result = false;
		int items = 0;

		String sql = "";
		//Sumar los items de la OC		
		sql  = " SELECT COUNT(*) AS elements FROM orderitems " +
				" LEFT JOIN ordergroups ON (ordi_ordergroupid = ordg_ordergroupid) " +
				" WHERE ordg_orderid = " + orderId;
		pmConn.doFetch(sql);
		if (pmConn.next()) items = pmConn.getInt("elements");			

		if (items > 0) result = true;
		else {
			sql  = " SELECT COUNT(*) AS elements FROM ordersessiontypepackages " +					
					" WHERE orsp_orderid = " + orderId;
			pmConn.doFetch(sql);
			if (pmConn.next()) items = pmConn.getInt("elements");
		}

		if(items > 0) result = true;
		else {
			sql  = " SELECT COUNT(*) AS elements FROM ordercredits " +					
					" WHERE orcr_orderid = " + orderId;
			pmConn.doFetch(sql);
			if (pmConn.next()) items = pmConn.getInt("elements");
		}

		if(items > 0) result = true;		
		return result;
	}

	private void updateWhMovement(PmConn pmConn, BmoOrder bmoOrder, BmoOrderDelivery bmoOrderDelivery, BmUpdateResult bmUpdateResult) throws SFException {
		PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
		BmoWhMovement bmoWhMovement = new BmoWhMovement();

		// Si no existe el movimiento de almacen, crearlo
		if (!pmWhMovement.orderDeliveryWhMovementExists(pmConn, bmoOrderDelivery.getId())) {

			bmoWhMovement.getDescription().setValue(bmoOrder.getName().toString());

			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT))
				bmoWhMovement.getDescription().setValue("Dispersion del crédito al cliente");

			bmoWhMovement.getOrderDeliveryId().setValue(bmoOrderDelivery.getId());
			bmoWhMovement.getDatemov().setValue(bmoOrderDelivery.getDeliveryDate().toString());
			bmoWhMovement.getCompanyId().setValue(bmoOrderDelivery.getCompanyId().toInteger());
			bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);

			// Asignaciones dependiendo el tipo de envio
			if (bmoOrderDelivery.getType().toChar() == BmoOrderDelivery.TYPE_DELIVERY) {

				// Establece tipo de pedido
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
					bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_RENTAL_OUT);

					// Establece seccion destino si es de salida tipo renta
					bmoWhMovement.getToWhSectionId().setValue(bmoOrderDelivery.getToWhSectionId().toInteger());
				} else {
					bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_OUT);
				}

			} else {
				if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL))
					bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_RENTAL_IN);
				else bmoWhMovement.getType().setValue(BmoWhMovement.TYPE_OUT_DEV);
			}
		} else {
			bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, bmoOrderDelivery.getId(), bmoWhMovement.getOrderDeliveryId().getName());
			if (bmoOrderDelivery.getStatus().toChar() == BmoOrderDelivery.STATUS_AUTHORIZED) 
				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
			else 
				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_REVISION);

			bmoWhMovement.getDatemov().setValue(bmoOrderDelivery.getDeliveryDate().toString());
		}
		pmWhMovement.save(pmConn, bmoWhMovement, bmUpdateResult);
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value) throws SFException {
		// Actualiza datos de la cotización
		bmoOrderDelivery = (BmoOrderDelivery)this.get(bmObject.getId());

		// Revisar cantidad de items apartados
		if (action.equals(BmoOrderDelivery.ACTION_WHBOX)) {
			updateItemsFromWhBox(bmoOrderDelivery, value, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Actualiza la informacion del envio tomando como base la caja de productos
	public void updateItemsFromWhBox(BmoOrderDelivery bmoOrderDelivery, String whBoxId, BmUpdateResult bmUpdateResult) throws SFException {
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

			PmWhStock pmWhStock = new PmWhStock(getSFParams());

			while (whBoxTrackList.hasNext()) {
				BmoWhBoxTrack nextBmoWhBoxTrack = (BmoWhBoxTrack)whBoxTrackList.next();

				// Si el rastreo no tiene disponibilidad, manda mensaje de error
				if (!(nextBmoWhBoxTrack.getBmoWhTrack().getInQuantity().toDouble() > 0)) 
					bmUpdateResult.addMsg("El Producto de la Caja con #Serie/lote | " + nextBmoWhBoxTrack.getBmoWhTrack().getSerial().toString() + " | no se encuentra en Existencia.");

				// Actualiza el Item del Envio del pedido
				PmOrderDeliveryItem pmOrderDeliveryItem = new PmOrderDeliveryItem(getSFParams());
				BmoOrderDeliveryItem bmoOrderDeliveryItem = new BmoOrderDeliveryItem();

				//Validacion de que exista el #serie/lote con cantidad mayor a 0 en almacen tipo normal
				if(!pmOrderDeliveryItem.serialExistsWarehouseNormal(pmConn, nextBmoWhBoxTrack.getBmoWhTrack().getSerial().toString()))
					bmUpdateResult.addMsg("El Producto de la Caja con #Serie/lote | " + nextBmoWhBoxTrack.getBmoWhTrack().getSerial().toString() + " | no se encuentra disponible");

				try {
					// Debe buscar OrderItem, por clave de producto y que no tengan cantidad mayor a 0 asignada
					bmoOrderDeliveryItem = pmOrderDeliveryItem.searchProductByCodeUnassigned(pmConn, bmoOrderDelivery.getId(), nextBmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getCode().toString());

					// Si lo encuentra procede a agregarlo
					if (bmoOrderDeliveryItem.getId() > 0) {
						printDevLog("Si lo encontro:");

						// Revisa que no exista ya el serial en la lista de productos
						if (!pmOrderDeliveryItem.serialExists(pmConn, bmoOrderDelivery.getId(), nextBmoWhBoxTrack.getBmoWhTrack().getSerial().toString())) {
							// Si ya tiene asignada cantidad, generar mensaje de error
							//								if (bmoOrderDeliveryItem.getQuantity().toDouble() > 0) 
							//									bmUpdateResult.addMsg("El Producto de la Caja |" + nextBmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getCode() + " " + nextBmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getName() + "| ya tiene asignada Cantidad.");

							bmoOrderDeliveryItem.getQuantity().setValue(nextBmoWhBoxTrack.getQuantity().toDouble());
							bmoOrderDeliveryItem.getSerial().setValue(nextBmoWhBoxTrack.getBmoWhTrack().getSerial().toString());

							// Encuentra la seccion del item, de acuerdo a # de serie / lote
							if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {
								int whSectionId = pmWhStock.getWhSectionByWhTrackId(pmConn, nextBmoWhBoxTrack.getWhTrackId().toInteger());
								if (whSectionId > 0)
									bmoOrderDeliveryItem.getFromWhSectionId().setValue(whSectionId);
								else 
									bmUpdateResult.addMsg("El Producto de la Caja |" + nextBmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getCode() + " " + nextBmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getName() + "| no se encuentra en Existencia.");
							}

							// No hay errores, almacena
							if (!bmUpdateResult.hasErrors()) 
								pmOrderDeliveryItem.save(pmConn, bmoOrderDeliveryItem, bmUpdateResult);
						}
					}

				} catch (SFException e) {
					System.out.println(this.getClass().getName() + "-updateItemsFromWhBox() No Existe el producto en el Pedido, sigue adelante con el resto.");
					// bmUpdateResult.addMsg("El Producto de la Caja |" + nextBmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getCode() + " " + nextBmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getName() + "| no existe en el Pedido.");
				}
			}

			if (!bmUpdateResult.hasErrors()) 
				pmConn.commit();			

		} catch (SFException e) {
			bmUpdateResult.addMsg(e.toString());
			if (!getSFParams().isProduction()) System.out.println(this.getClass().getName() + "-updateItemsFromWhBox() ERROR " + e.toString());
		} finally {
			pmConn.close();
		}
	}

	public boolean getOrderHasTypeDelivery(PmConn pmConn, int orderId) throws SFException {
		boolean result = false;
		int count = 0;

		//Regresa verdadero si existen envios de pedido de tipo envio, de un pedido.	
		String sql  = " SELECT COUNT(odly_orderdeliveryid) as odlyCount FROM orderdeliveries " +
				" WHERE odly_orderid = " + orderId +
				" AND odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY + "'";
		pmConn.doFetch(sql);

		if (pmConn.next()) count = pmConn.getInt("odlyCount");

		if (count > 0) result = true;			

		return result;
	}

	// Crea en automatico el envio de pedido
	public void createOrderDelivery(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		PmOrder pmOrder = new PmOrder(getSFParams());
		PmRaccount pmRaccount = new PmRaccount(getSFParams());

		// Es de tipo Credito
		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
			// Obtener el Credito
			BmoCredit bmoCredit = new BmoCredit();
			PmCredit pmCredit = new PmCredit(getSFParams());
			bmoCredit = (BmoCredit)pmCredit.getBy(pmConn, bmoOrder.getId(), bmoCredit.getOrderId().getName());

			// Crear el pedido
			BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());		
			bmoOrderDelivery.getOrderId().setValue(bmoOrder.getId());
			bmoOrderDelivery.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoOrderDelivery.getType().setValue(BmoOrderDelivery.TYPE_DELIVERY);
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_REVISION);
			bmoOrderDelivery.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoOrderDelivery.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoOrderDelivery.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
			bmoOrderDelivery.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());
			pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);

			printDevLog(this.getClass().getName() + "createOrderDelivery(): Se genero el envio de pedido");

			// Crea la CxC de Nota de Credito
			BmoRaccount bmoNewRaccount = new BmoRaccount();	
			bmoNewRaccount.getInvoiceno().setValue("Autorización Crédito");		
			bmoNewRaccount.getReceiveDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoNewRaccount.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));		
			bmoNewRaccount.getDescription().setValue("Creado Automaticamente.");
			bmoNewRaccount.getReference().setValue("Creado Autom.");
			bmoNewRaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoCredit.getAmount().toDouble()));
			bmoNewRaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoCredit.getAmount().toDouble()));
			bmoNewRaccount.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());

			// Obtener el Tipo
			BmoRaccountType bmoRaccountType = new BmoRaccountType();
			PmRaccountType pmRaccountType = new PmRaccountType(getSFParams());
			bmoRaccountType = (BmoRaccountType)pmRaccountType.getBy(pmConn, "" + BmoRaccountType.CATEGORY_CREDITNOTE, bmoRaccountType.getCategory().getName());
			bmoNewRaccount.getRaccountTypeId().setValue(bmoRaccountType.getId());

			bmoNewRaccount.getOrderId().setValue(bmoOrder.getId());
			bmoNewRaccount.getUserId().setValue(bmoOrder.getUserId().toInteger());
			bmoNewRaccount.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoNewRaccount.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoNewRaccount.getStatus().setValue(BmoRaccount.STATUS_AUTHORIZED);
			bmoNewRaccount.getAutoCreate().setValue(0); 

			pmRaccount.saveSimple(pmConn, bmoNewRaccount, bmUpdateResult);

			printDevLog(this.getClass().getName() + "createOrderDelivery(): Se genero el cxc nota de credito ID: " + bmoNewRaccount.getId());

			// Asigna clave
			bmoNewRaccount.getCode().setValue(bmoNewRaccount.getCodeFormat());

			// Crear el item con el monto de la liquidacion
			PmRaccountItem pmRaccItemNew = new PmRaccountItem(getSFParams());
			BmoRaccountItem bmoRaccItemNew = new BmoRaccountItem();
			bmoRaccItemNew.getName().setValue(bmoNewRaccount.getInvoiceno().toString());
			bmoRaccItemNew.getQuantity().setValue("1");
			bmoRaccItemNew.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(bmoCredit.getAmount().toDouble()));
			bmoRaccItemNew.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(bmoCredit.getAmount().toDouble()));			
			bmoRaccItemNew.getRaccountId().setValue(bmoNewRaccount.getId());

			pmRaccItemNew.saveSimple(pmConn, bmoRaccItemNew, bmUpdateResult);

			pmRaccount.saveSimple(pmConn, bmoNewRaccount, bmUpdateResult);

			printDevLog(this.getClass().getName() + "createOrderDelivery(): Se genero el item de la CxC ID " + bmoNewRaccount.getId());

			// Crear el MB de Banco

			// Obtener el ejecutivo
			int userId = pmOrder.getUserByGroup(pmConn, bmoOrder, bmUpdateResult);

			//Obtener la Cuenta de Banco del Supervisor
			BmoBankAccount bmoBankAccount = new BmoBankAccount();
			PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
			bmoBankAccount = (BmoBankAccount)pmBankAccount.getBy(pmConn, userId, bmoBankAccount.getResponsibleId().getName());

			//Crear el MB de tipo Dispersion
			// Obtener el tipo pago en bancos
			PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
			BmoBankMovType bmoBankMovType = new BmoBankMovType();		
			bmoBankMovType = (BmoBankMovType) pmBankMovType.getBy("" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC, bmoBankMovType.getCategory().getName());

			//Crear el Mov de Banco de Tipo Pago a Proveedor
			PmBankMovement pmBkmvNew = new PmBankMovement(getSFParams());
			BmoBankMovement bmoBkmvNew = new BmoBankMovement();

			bmoBkmvNew.getBankAccountId().setValue(bmoBankAccount.getId());		
			bmoBkmvNew.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoBkmvNew.getBankReference().setValue("Entrega de Dinero");
			bmoBkmvNew.getDescription().setValue("Entrega de Dinero");
			bmoBkmvNew.getBankMovTypeId().setValue(bmoBankMovType.getId());
			bmoBkmvNew.getStatus().setValue("" + BmoBankMovement.STATUS_REVISION);
			bmoBkmvNew.getInputDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoBkmvNew.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));

			pmBkmvNew.save(pmConn, bmoBkmvNew, bmUpdateResult);

			printDevLog(this.getClass().getName() + "createOrderDelivery(): Se genero el Movimiento de Banco ID " + bmoBkmvNew.getId());

			// Crear el concepto de Banco				
			PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
			BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
			bmoBankMovConcept.getBankMovementId().setValue(bmoBkmvNew.getId());
			bmoBankMovConcept.getRaccountId().setValue(bmoNewRaccount.getId());		
			bmoBankMovConcept.getAmount().setValue(bmoCredit.getAmount().toDouble());

			pmBankMovConcept.save(pmConn, bmoBankMovConcept, bmUpdateResult);

			printDevLog(this.getClass().getName() + "createOrderDelivery(): Se genero el Concepto de Movimiento de Banco ID " + bmoBankMovConcept.getId());
		} 
		// Es de tipo Sesion
		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
			BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());		
			bmoOrderDelivery.getOrderId().setValue(bmoOrder.getId());
			bmoOrderDelivery.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoOrderDelivery.getType().setValue(BmoOrderDelivery.TYPE_DELIVERY);			
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_REVISION);			
			bmoOrderDelivery.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoOrderDelivery.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoOrderDelivery.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());
			bmoOrderDelivery.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());

			pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);			

			//Recibir en automático
			if (bmoOrder.getBmoOrderType().getEnableDeliverySend().toBoolean()) {	
				int orderDeliveryId = bmUpdateResult.getId(); 
				if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {
					this.automaticReception(pmConn, bmoOrderDelivery, bmUpdateResult);
				}				

				//Autorizar el whmovement 
				PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
				BmoWhMovement bmoWhMovement = new BmoWhMovement();
				bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, orderDeliveryId, bmoWhMovement.getOrderDeliveryId().getName());
				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
				pmWhMovement.saveSimple(pmConn, bmoWhMovement, bmUpdateResult);

				//Autorizar el Envio de Pedido
				bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_AUTHORIZED);
				pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);
			}

		} 
		// Es de tipo Venta
		else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());		
			bmoOrderDelivery.getOrderId().setValue(bmoOrder.getId());
			bmoOrderDelivery.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoOrderDelivery.getType().setValue(BmoOrderDelivery.TYPE_DELIVERY);
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_REVISION);						
			bmoOrderDelivery.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoOrderDelivery.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoOrderDelivery.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());			
			bmoOrderDelivery.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());

			pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);			

			//Recibir en automático
			if (bmoOrder.getBmoOrderType().getEnableDeliverySend().toBoolean()) {
				int orderDeliveryId = bmUpdateResult.getId();
				if (bmoOrderDelivery.getType().equals(BmoOrderDelivery.TYPE_DELIVERY)) {
					this.automaticReception(pmConn, bmoOrderDelivery, bmUpdateResult);
				}

				//Autorizar el whmovement 
				PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
				BmoWhMovement bmoWhMovement = new BmoWhMovement();
				bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, orderDeliveryId, bmoWhMovement.getOrderDeliveryId().getName());
				bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
				pmWhMovement.saveSimple(pmConn, bmoWhMovement, bmUpdateResult);

				//Autorizar el Envio de Pedido
				bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_AUTHORIZED);
				pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);
			}
		}
	}

	//Realizar la recepcion en automático
	public void automaticReception(PmConn pmConn, BmoOrderDelivery bmoOrderDelivery, BmUpdateResult bmUpdateResult) throws SFException {
		BmoOrderDeliveryItem bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
		PmOrderDeliveryItem pmOrderDeliveryItem = new PmOrderDeliveryItem(getSFParams());

		//Listar los productos del envio de pedido
		BmFilter filterOrderDelivery = new BmFilter();		
		filterOrderDelivery.setValueFilter(bmoOrderDeliveryItem.getKind(), bmoOrderDeliveryItem.getOrderDeliveryId(), bmoOrderDelivery.getId());		
		Iterator<BmObject> listOrderDeliveryItem = new PmOrderDeliveryItem(getSFParams()).list(pmConn, filterOrderDelivery).iterator();
		while (listOrderDeliveryItem.hasNext()) {
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)listOrderDeliveryItem.next();

			pmOrderDeliveryItem.setSerialTrackByProduct(pmConn, bmoOrderDeliveryItem, bmoOrderDelivery.getCompanyId().toInteger(), bmUpdateResult);

		}
	}

	//Crear las devoluciones en automático
	public void createOrderReturn(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		//		PmOrder pmOrder = new PmOrder(getSFParams());

		if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {
			//Realizar la devolución en automático
			BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());		
			bmoOrderDelivery.getOrderId().setValue(bmoOrder.getId());
			bmoOrderDelivery.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoOrderDelivery.getType().setValue(BmoOrderDelivery.TYPE_RETURN);
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_REVISION);						
			bmoOrderDelivery.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoOrderDelivery.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoOrderDelivery.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());			
			bmoOrderDelivery.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());

			pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);	

			int orderDeliveryId = bmUpdateResult.getId();

			automaticReturn(pmConn, bmoOrderDelivery, bmUpdateResult);

			//Autorizar el whmovement 
			PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
			BmoWhMovement bmoWhMovement = new BmoWhMovement();
			bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, orderDeliveryId, bmoWhMovement.getOrderDeliveryId().getName());
			bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
			pmWhMovement.saveSimple(pmConn, bmoWhMovement, bmUpdateResult);

			//Autorizar el Envio de Pedido
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_AUTHORIZED);
			pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);

		} else if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)) {
			//Realizar la devolución en automático
			BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
			PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());		
			bmoOrderDelivery.getOrderId().setValue(bmoOrder.getId());
			bmoOrderDelivery.getCustomerId().setValue(bmoOrder.getCustomerId().toInteger());
			bmoOrderDelivery.getType().setValue(BmoOrderDelivery.TYPE_RETURN);
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_REVISION);						
			bmoOrderDelivery.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoOrderDelivery.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());
			bmoOrderDelivery.getCurrencyId().setValue(bmoOrder.getCurrencyId().toInteger());			
			bmoOrderDelivery.getCurrencyParity().setValue(bmoOrder.getCurrencyParity().toDouble());

			pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);

			automaticReturn(pmConn, bmoOrderDelivery, bmUpdateResult);

			int orderDeliveryId = bmUpdateResult.getId();

			automaticReturn(pmConn, bmoOrderDelivery, bmUpdateResult);

			//Autorizar el whmovement 
			PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
			BmoWhMovement bmoWhMovement = new BmoWhMovement();
			bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, orderDeliveryId, bmoWhMovement.getOrderDeliveryId().getName());
			bmoWhMovement.getStatus().setValue(BmoWhMovement.STATUS_AUTHORIZED);
			pmWhMovement.saveSimple(pmConn, bmoWhMovement, bmUpdateResult);

			//Autorizar el Envio de Pedido
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_AUTHORIZED);
			pmOrderDelivery.save(pmConn, bmoOrderDelivery, bmUpdateResult);
		}
	}

	//Devolución en automatico
	public void automaticReturn(PmConn pmConn, BmoOrderDelivery bmoOrderDeliveryReturn, BmUpdateResult bmUpdateResult) throws SFException {
		BmoOrderDeliveryItem bmoOrderDeliveryItem = new BmoOrderDeliveryItem();
		PmOrderDeliveryItem pmOrderDeliveryItem = new PmOrderDeliveryItem(getSFParams());

		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
		BmoOrderDeliveryItem bmoOrderDeliveryItemReturn = new BmoOrderDeliveryItem();

		//Obtener el envio de pedido de entrega
		String sql = "";
		int orderDeliveryId = 0;
		sql = " SELECT * FROM orderdeliveries " +
				" WHERE odly_orderid = " + bmoOrderDeliveryReturn.getOrderId().toInteger() +
				" AND odly_type = '" + BmoOrderDelivery.TYPE_DELIVERY + "'";
		pmConn.doFetch(sql);

		if (pmConn.next()) {
			orderDeliveryId = pmConn.getInt("odly_orderdeliveryid");
		}				

		//Listar los productos del envio de pedido
		BmFilter filterOrderDelivery = new BmFilter();		
		filterOrderDelivery.setValueFilter(bmoOrderDeliveryItem.getKind(), bmoOrderDeliveryItem.getOrderDeliveryId().getName(), orderDeliveryId);		
		Iterator<BmObject> listOrderDeliveryItem = new PmOrderDeliveryItem(getSFParams()).list(pmConn, filterOrderDelivery).iterator();
		while (listOrderDeliveryItem.hasNext()) {
			bmoOrderDeliveryItem = (BmoOrderDeliveryItem)listOrderDeliveryItem.next();

			if (!bmoOrderDeliveryItem.getSerial().toString().equals("")) {
				int orderDeliveryItemId = 0;
				//Obtener el item de devolución
				sql = " SELECT odyi_orderdeliveryitemid FROM orderdeliveryitems " +
						" LEFT join orderdeliveries ON (odyi_orderdeliveryid = odly_orderdeliveryid) " +
						" WHERE odly_orderid = " + bmoOrderDeliveryReturn.getOrderId().toInteger() +
						" AND odyi_orderitemid = " + bmoOrderDeliveryItem.getOrderItemId().toInteger() +
						" AND odly_type = '" + BmoOrderDelivery.TYPE_RETURN + "'";
				pmConn.doFetch(sql);				
				if (pmConn.next()) {
					orderDeliveryItemId = pmConn.getInt("odyi_orderdeliveryitemid");

					bmoOrderDeliveryItemReturn = (BmoOrderDeliveryItem)pmOrderDeliveryItem.get(pmConn,orderDeliveryItemId);
					bmoOrderDeliveryItemReturn.getSerial().setValue(bmoOrderDeliveryItem.getSerial().toString());					
					bmoOrderDeliveryItemReturn.getQuantity().setValue(bmoOrderDeliveryItem.getQuantity().toDouble());

					pmOrderDeliveryItem.save(pmConn, bmoOrderDeliveryItemReturn, bmUpdateResult);	
				}			
			}
		}

		//Autorizar el Envio de Pedido
		bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_AUTHORIZED);
		save(pmConn, bmoOrderDeliveryReturn, bmUpdateResult);
	}

	//Eliminar el envio al cambiar el pedido a revision
	public void deleteOrderDelivery(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {
		BmoOrderDelivery bmoOrderDelivery = new BmoOrderDelivery();
		
		// Si existe el envio, borrarlo
		if (this.hasOrderDelivery(pmConn, bmoOrder.getId(), bmUpdateResult)) {
		bmoOrderDelivery = (BmoOrderDelivery)this.getBy(pmConn, bmoOrder.getId(), bmoOrderDelivery.getOrderId().getName());
			//Cambiar estatus de envio
			bmoOrderDelivery.getStatus().setValue(BmoOrderDelivery.STATUS_REVISION);
			save(pmConn, bmoOrderDelivery, bmUpdateResult);
			//Eliminar el envio
			delete(pmConn, bmoOrderDelivery, bmUpdateResult);
		}
	}

	// Existen envio de pedido 
	public boolean hasOrderDelivery(PmConn pmConn, int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		pmConn.doFetch("SELECT * FROM " + SQLUtil.formatKind(getSFParams(), "orderdeliveries")
		+ " WHERE odly_orderid = " + orderId);

		return pmConn.next();
	}


	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoOrderDelivery = (BmoOrderDelivery)bmObject;

		// Si el recibo ya está autorizado, no se puede hacer movimientos

		if (!bmoOrderDelivery.getStatus().equals(BmoOrderDelivery.STATUS_REVISION)) {
			bmUpdateResult.addMsg("No se pueden realizar movimientos sobre el Envío - ya está Autorizado.");
		} else {

			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = (BmoOrder)pmOrder.get(bmoOrderDelivery.getOrderId().toInteger());

			if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL) 
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SALE)
					|| bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_SESSION)) {			
				// Eliminar Movimiento de Almacen
				PmWhMovement pmWhMovement = new PmWhMovement(getSFParams());
				BmoWhMovement bmoWhMovement = new BmoWhMovement();
				if (pmWhMovement.orderDeliveryWhMovementExists(pmConn, bmoOrderDelivery.getId())) {
					bmoWhMovement = (BmoWhMovement)pmWhMovement.getBy(pmConn, bmoOrderDelivery.getId(), bmoWhMovement.getOrderDeliveryId().getName());
					pmWhMovement.delete(pmConn, bmoWhMovement, bmUpdateResult);
				}
			}	

			// Eliminar los items	
			PmOrderDeliveryItem pmOrderDeliveryItem = new PmOrderDeliveryItem(getSFParams());
			pmOrderDeliveryItem.deleteItems(pmConn, bmoOrderDelivery, bmUpdateResult);

			// Eliminar Registro
			super.delete(pmConn, bmObject, bmUpdateResult);

			// Actualizar estatus de envio del pedido			
			pmOrder.updateDeliveryStatus(pmConn, bmoOrder, bmUpdateResult);

		}

		return bmUpdateResult;
	}
}
