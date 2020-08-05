/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.server.fi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.flexwm.server.FlexUtil;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.server.op.PmRequisitionReceipt;
import com.flexwm.server.op.PmSupplier;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoWorkContract;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountAssignment;
import com.flexwm.shared.fi.BmoPaccountItem;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoSupplier;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.sf.PmFile;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoFile;

import com.symgae.shared.sf.BmoUser;


public class PmPaccount extends PmObject {
	BmoPaccount bmoPaccount;

	public PmPaccount(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPaccount = new BmoPaccount();
		setBmObject(bmoPaccount);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPaccount.getUserId(), bmoPaccount.getBmoUser()),
				new PmJoin(bmoPaccount.getBmoUser().getAreaId(), bmoPaccount.getBmoUser().getBmoArea()),
				new PmJoin(bmoPaccount.getBmoUser().getLocationId(), bmoPaccount.getBmoUser().getBmoLocation()),
				new PmJoin(bmoPaccount.getCompanyId(), bmoPaccount.getBmoCompany()),
				new PmJoin(bmoPaccount.getPaccountTypeId(), bmoPaccount.getBmoPaccountType()),					
				new PmJoin(bmoPaccount.getSupplierId(), bmoPaccount.getBmoSupplier()),
				new PmJoin(bmoPaccount.getCurrencyId(), bmoPaccount.getBmoCurrency()),
				new PmJoin(bmoPaccount.getBmoSupplier().getSupplierCategoryId(), bmoPaccount.getBmoSupplier().getBmoSupplierCategory())
				)));			
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserAreaId = getSFParams().getLoginInfo().getBmoUser().getAreaId().toInteger();

		if (getSFParams().restrictData(bmoPaccount.getProgramCode())) {
			// Filtro muestra las CxP que creo el usuario
			filters = " ( pacc_usercreateid IN " +
					" ( " +
					" SELECT user_userid FROM users " +
					" WHERE " + 
					" user_userid = " + loggedUserId +
					" OR user_userid IN ( " +
					" SELECT u2.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u3.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u4.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +
					" OR user_userid IN ( " +
					" SELECT u5.user_userid FROM users u1 " +
					" LEFT JOIN users u2 ON (u2.user_parentid = u1.user_userid) " +
					" LEFT JOIN users u3 ON (u3.user_parentid = u2.user_userid) " +
					" LEFT JOIN users u4 ON (u4.user_parentid = u3.user_userid) " +
					" LEFT JOIN users u5 ON (u5.user_parentid = u4.user_userid) " +
					" WHERE u1.user_userid = " + loggedUserId +
					" ) " +

					// Mostrar las CxP que tienen OC donde se encuentre el solicitado/creado/departamento del usuario
					" OR pacc_requisitionid IN ( " +
					" SELECT reqi_requisitionid " +
					" FROM requisitions" +
					" WHERE reqi_usercreateid = " + loggedUserId +
					" OR reqi_requestedby =  " + loggedUserId +
					" OR reqi_areaid = " + loggedUserAreaId + 
					" ) " +

					// Mostrar si es Nota de credito
					// Entra a la Aplicaciones para saber cual es la cxp de tipo cargo y a partir
					// de aqui hacer la busqueda en OC, del solicitado/creado/departamento del usuario 
					" OR pacc_paccountid IN ( " +
					" SELECT pass_paccountid " +
					" FROM paccountassignments " +
					" LEFT JOIN paccounts ON (pacc_paccountid = pass_foreignpaccountid) " +
					" LEFT JOIN requisitions ON (reqi_requisitionid = pacc_requisitionid ) " +
					" WHERE (reqi_usercreateid = " + loggedUserId +
					" OR reqi_requestedby =  " + loggedUserId +
					" OR reqi_areaid = " + loggedUserAreaId + 
					" ) " +
					" ) " +

					" ) " +
					" ) ";
		}

		// Filtro de cxp de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( pacc_companyid IN ( " +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )" +
					" ) ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " pacc_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPaccount bmoPaccount = (BmoPaccount) autoPopulate(pmConn, new BmoPaccount());

		//BmoPaccountType
		BmoPaccountType bmoPaccountType = new BmoPaccountType();
		int paccountTypeId = pmConn.getInt(bmoPaccountType.getIdFieldName());
		if (paccountTypeId > 0) bmoPaccount.setBmoPaccountType((BmoPaccountType) new PmPaccountType(getSFParams()).populate(pmConn));
		else bmoPaccount.setBmoPaccountType(bmoPaccountType);

		//BmoSupplier
		BmoSupplier bmoSupplier = new BmoSupplier();
		int supplierId = pmConn.getInt(bmoSupplier.getIdFieldName());
		if (supplierId > 0) bmoPaccount.setBmoSupplier((BmoSupplier) new PmSupplier(getSFParams()).populate(pmConn));
		else bmoPaccount.setBmoSupplier(bmoSupplier);

		//BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoPaccount.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoPaccount.setBmoCompany(bmoCompany);

		//BmoUser
		BmoUser bmoUser = new BmoUser();
		int userId = pmConn.getInt(bmoUser.getIdFieldName());
		if (userId > 0) bmoPaccount.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		else bmoPaccount.setBmoUser(bmoUser);

		//BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0) bmoPaccount.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoPaccount.setBmoCurrency(bmoCurrency);

		return bmoPaccount;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		bmoPaccount = (BmoPaccount)bmObject;
//		BmoPaccount bmoPaccountPrev = new BmoPaccount();
		double reqiBalance = 0;
		int programId = getSFParams().getProgramId(bmoPaccount.getProgramCode());
		boolean newRecord = false;

		// Se almacena de forma prelimarg0inar para asignar ID y la Clave
		if (!(bmoPaccount.getId() > 0)) {
			newRecord = true;
			super.save(pmConn, bmoPaccount, bmUpdateResult);
			bmoPaccount.setId(bmUpdateResult.getId());
			bmoPaccount.getCode().setValue(bmoPaccount.getCodeFormat());

			if (bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)) {
				if (!(bmoPaccount.getRequisitionReceiptId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoPaccount.getRequisitionReceiptId().getName(), "Debe seleccionar una Recibo de OC");
				}
			}
		} else {
			// Cuentas por Pagar Previa
//			bmoPaccountPrev = (BmoPaccount)this.get(bmoPaccount.getId());

			// Validar que no se pueda camiar de estatus a en revision si ya tiene pagos
			if((!bmoPaccount.getBmoPaccountType().getCategory().equals(""+BmoPaccountType.CATEGORY_CREDITNOTE))) {
				if (bmoPaccount.getStatus().toChar() ==  BmoRaccount.STATUS_REVISION) {
					if (bmoPaccount.getPayments().toDouble() > 0) 
						bmUpdateResult.addError(bmoPaccount.getStatus().getLabel(), "<b>No se puede cambiar el Estatus, " 
								+ " la " + getSFParams().getProgramTitle(getBmObject()) + " tiene Pagos.</b>");

					// Validar si tiene pagos, que no pueda cambiar el estatus a en revison. El escenario es cuando ya tiene un mb pago, y una de devolucion
					if (hasPayments(pmConn, bmoPaccount.getId()))
						bmUpdateResult.addError(bmoPaccount.getStatus().getName(), "No se puede cambiar el estatus " + bmoPaccount.getStatus().getSelectedOption().getLabel() 
								+ " la " + getSFParams().getProgramTitle(getBmObject()) + " tiene Pagos ligados.");
				}
			}


			// Validar que si esta cancelando y tiene pagos, regresa error
//			if (bmoPaccount.getStatus().toChar() ==  BmoPaccount.STATUS_CANCELLED) {
//				if (bmoPaccount.getPayments().toDouble() > 0) {
//					bmUpdateResult.addError(bmoPaccount.getStatus().getName(), "No se puede Cancelar, " 
//							+ " la " + getSFParams().getProgramTitle(getBmObject()) + " tiene Pagos.");
//				}
//			}
		}

		if (((BmoFlexConfig) getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (!(bmoPaccount.getBudgetItemId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoPaccount.getBudgetItemId().getName(), "Seleccione una Partida.");
			}

			//			if (!(bmoPaccount.getAreaId().toInteger() > 0)) {
			//				bmUpdateResult.addError(bmoPaccount.getAreaId().getName(), "Seleccione un Departamento");
			//			}
		}
		//Agregar la paridad de la moneda
		if (bmoPaccount.getCurrencyId().toInteger() > 0) {
			if (!(bmoPaccount.getCurrencyParity().toDouble() > 0)) {
				PmCurrency pmCurrency = new PmCurrency(getSFParams());
				BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(pmConn, bmoPaccount.getCurrencyId().toInteger());
				bmoPaccount.getCurrencyParity().setValue(bmoCurrency.getParity().toDouble());
			}
		}

		// Si la cuenta x pagar esta en revision
		if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_REVISION)) {
			//			if (!bmoPaccount.getPaymentStatus().equals(BmoPaccount.PAYMENTSTATUS_PENDING)) {				
			//				bmoPaccount.getStatus().setValue(bmoPaccountPrev.getStatus().toString());
			//			}

			// Crear los item de la CxP con la informacion de los items del ROC
			if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {
				PmPaccountItem pmPaccountItem = new PmPaccountItem(getSFParams());
				pmPaccountItem.createItemsFromRequisitionReceipt(pmConn, bmoPaccount, bmUpdateResult);

				PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
				BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());

				bmoPaccount.getSupplierId().setValue(bmoRequisitionReceipt.getSupplierId().toInteger());
				bmoPaccount.getRequisitionId().setValue(bmoRequisitionReceipt.getRequisitionId().toInteger());

				PmRequisition pmRequisition = new PmRequisition(getSFParams());
				BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());

				// Asignar el budgetItemId
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					if (!(bmoRequisitionReceipt.getBudgetItemId().toInteger() > 0))
						bmUpdateResult.addError(bmoRequisitionReceipt.getBudgetItemId().getName(), "El R.O.C. no tiene asignada una Partida Presupuestal");

					bmoPaccount.getBudgetItemId().setValue(bmoRequisitionReceipt.getBudgetItemId().toInteger());
					bmoPaccount.getAreaId().setValue(bmoRequisition.getAreaId().toInteger());
				}	

				// Asignar la Clave de la OC
				bmoPaccount.getReqiCode().setValue(bmoRequisition.getCode().toString());

				bmoPaccount.getCurrencyId().setValue(bmoRequisition.getCurrencyId().toInteger());
				bmoPaccount.getCurrencyParity().setValue(bmoRequisition.getCurrencyParity().toDouble());

				// Obtener el Tipo de OC
				bmoPaccount.getRequisitionType().setValue(bmoRequisition.getBmoRequisitionType().getType().toString());

				// Obtener el proyecto y Agregarlo a la Descripcion de la CXP
				if (bmoRequisition.getOrderId().toInteger() > 0) {
					PmOrder pmOrder = new PmOrder(getSFParams());
					BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoRequisition.getOrderId().toInteger());					
					bmoPaccount.getDescription().setValue(bmoPaccount.getDescription().toString() + " " + bmoOrder.getCode().toString());
				}	

				// Hacer copia de los documentos de la Orden de Compra
				createLinkReqiFiles(pmConn, bmoPaccount, bmUpdateResult);
			} 

		} 
		// Autoriza la CxP
		else if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_AUTHORIZED)) {
			// Obtener el total del Recibo O. C, y validar que el monto de la cxp no supere el saldo del orden de compra
			if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0 && bmoPaccount.getBmoPaccountType().getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {
				reqiBalance = getRequisitionReceitpBalance(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger(), bmUpdateResult);				
				if (bmoPaccount.getTotal().toDouble() > reqiBalance && reqiBalance > 0)
					bmUpdateResult.addError(bmoPaccount.getTotal().getName(), "El Monto de la CxP no debe superar el Saldo del Recibo de la Orden de Compra: $" + reqiBalance);

				// Validar que si la oc tiene MB anticipo prov. el monto de este sea <= al del roc
				PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
				//				if (pmBankMovConcept.hasMovConceptRequisitionAdvance(pmConn, bmoPaccount.getRequisitionId().toInteger(), bmUpdateResult)) {
				//					double sumAmount = pmBankMovConcept.getSumAmountRequisitionAdvance(pmConn, bmoPaccount.getRequisitionId().toInteger(), bmUpdateResult);
				//					if (sumAmount > bmoPaccount.getTotal().toDouble()) {
				//						bmUpdateResult.addError(bmoPaccount.getTotal().getName(), "La O.C. tiene un pago 'Anticipo Proveedor' con un monto mayor al del Recibo O.C.");
				//					}
				//				}

				// Si la OC tiene anticipo, ligarlo al concepto
				printDevLog("Autorizar CP: " + bmoPaccount.getCode().toString());
				// Actualizar balance y estatus de pago
				updateBalance(pmConn, bmoPaccount, bmUpdateResult);
				super.save(pmConn, bmoPaccount, bmUpdateResult);
				pmBankMovConcept.setPaccountToBankMovConcepts(pmConn, bmoPaccount, bmUpdateResult);
			}

			//Cambios Nuevo Framework
			bmoPaccount.getAuthorizedDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
			bmoPaccount.getAuthorizedUser().setValue(getSFParams().getLoginInfo().getUserId());
		} 
		// Cancelar la CxP
//		else if (bmoPaccount.getStatus().equals(BmoPaccount.STATUS_CANCELLED)) {
//			// Liberar el ROC
//			if (bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)) {
//				PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
//				BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
//				if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0)
//					bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());
//				else
//					bmUpdateResult.addError(bmoPaccount.getRequisitionReceiptId().getName(), "Debe seleccionar un Recibo de OC.");
//
//				//bmoRequisitionReceipt.getPayment().setValue("" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED);
//				pmRequisitionReceipt.updatePaymentStatus(pmConn, bmoRequisitionReceipt, bmUpdateResult);
//			}
//		}
		
		if (getFiles(programId, bmoPaccount.getId())) {
			bmoPaccount.getFile().setValue("1");
		}else {
			bmoPaccount.getFile().setValue("0");
		}

		// Validar perido operativo de acuerdo a configuracion.
		if ( ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequiredPeriodFiscal().toBoolean() ) {
			printDevLog("pacc: INICIO Periodo Operativo(requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsOpen = pmFiscalPeriod.isOpen(pmConn, bmoPaccount.getReceiveDate().toString(), bmoPaccount.getCompanyId().toInteger());
			printDevLog("pacc: FIN Periodo Operativo: "+fiscalPeriodIsOpen);

			if (!fiscalPeriodIsOpen)
				bmUpdateResult.addError(bmoPaccount.getReceiveDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoPaccount.getReceiveDate().toString() + ").");
		} else {
			printDevLog("pacc: INICIO Periodo Operativo(NO requerido)");
			PmFiscalPeriod pmFiscalPeriod = new PmFiscalPeriod(getSFParams());
			boolean fiscalPeriodIsClosed = pmFiscalPeriod.isClosed(pmConn, bmoPaccount.getReceiveDate().toString(), bmoPaccount.getCompanyId().toInteger());
			printDevLog("pacc: FIN Periodo Operativo: "+fiscalPeriodIsClosed);

			if (fiscalPeriodIsClosed)
				bmUpdateResult.addError(bmoPaccount.getReceiveDate().getName(), 
						"El Periodo Operativo está Cerrado en la fecha del Documento (" + bmoPaccount.getReceiveDate().toString() + ").");
		}

		if (!bmUpdateResult.hasErrors()) {
			super.save(pmConn, bmoPaccount, bmUpdateResult);

			// Actualizar balance y estatus de pago
			updateBalance(pmConn, bmoPaccount, bmUpdateResult);
			// Forzar estatus a pendiente cuando se esta creando, ya que lo ponia como total
			if (newRecord)
				bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);

			super.save(pmConn, bmoPaccount, bmUpdateResult);
			//Actualizar partida en items
			if (!bmUpdateResult.hasErrors()) {
				updateItems(pmConn,bmUpdateResult.getId());
			}
		}
		return bmUpdateResult;
	}	

	public void updateItems(PmConn pmConn,int paccountId) throws SFException {
		BmoPaccount bmoPaccount = (BmoPaccount)this.get(pmConn,paccountId);
		BmoPaccountItem bmoPaccountItem = new BmoPaccountItem();
		PmPaccountItem pmPaccountItem = new PmPaccountItem(getSFParams());
		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

		BmFilter bmFilterPacc = new BmFilter();
		bmFilterPacc.setValueFilter(bmoPaccountItem.getKind(), bmoPaccountItem.getPaccountId(), bmoPaccount.getId());
		filterList.add(bmFilterPacc);
		BmFilter bmFilterNotBudget = new BmFilter();
		bmFilterNotBudget.setNullFilter(bmoPaccountItem.getKind(), bmoPaccountItem.getBudgetItemId().getName(), BmFilter.ISNULL);
		filterList.add(bmFilterNotBudget);

		if (bmoPaccount.getStatus().toString().equals(""+BmoPaccount.STATUS_REVISION)) {
			Iterator< BmObject> itemIterator = pmPaccountItem.list(filterList).iterator();
			while (itemIterator.hasNext()) {
				BmoPaccountItem nextBmoPaccountItem = (BmoPaccountItem)itemIterator.next();
				nextBmoPaccountItem.getBudgetItemId().setValue(bmoPaccount.getBudgetItemId().toInteger());
				nextBmoPaccountItem.getAreaId().setValue(bmoPaccount.getAreaId().toInteger());
				pmPaccountItem.saveSimple(pmConn, nextBmoPaccountItem, new BmUpdateResult());
			}
		}

	}

	public void updateBalance(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {		
		PmPaccountType pmPaccountType = new PmPaccountType(getSFParams());
		BmoPaccountType bmoPaccountType = (BmoPaccountType)pmPaccountType.get(pmConn, bmoPaccount.getPaccountTypeId().toInteger());

		// Validar tipo de CxP
		if (bmoPaccountType.getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {			

			//Sumar los items de la cxp si es de tipo otros					 
			bmoPaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(this.sumPaccountItems(pmConn, bmoPaccount, bmUpdateResult)));

			if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {

				PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
				BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());

				PmRequisition pmRequisition = new PmRequisition(getSFParams());
				BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());

				// Si tiene retenciones la OC, restar al total de la cxp
				double holdBack = 0;
				if (bmoRequisition.getHoldBack().toDouble() > 0)
					holdBack = bmoRequisition.getHoldBack().toDouble();

				if (bmoRequisition.getTaxApplies().toBoolean()) {
					if(!bmoPaccountType.getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
						bmoPaccount.getTaxApplies().setValue(1);
						//Calcular el IVA
						double taxRate = (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);						 
						double tax = (bmoPaccount.getAmount().toDouble() * taxRate);		
						// Si viene de un XML Calcula el iva por el iva de los Items
						if(bmoPaccount.getIsXml().toBoolean()) {
							tax = calculateTaxByItems(pmConn,bmoPaccount.getId());
						}
						bmoPaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
						bmoPaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals((bmoPaccount.getAmount().toDouble() + tax) - holdBack));
						
						if(bmoPaccount.getIsXml().toBoolean()) {
							bmoPaccount.getTotal().setValue(bmoPaccount.getTotal().toDouble() - bmoPaccount.getSumRetention().toDouble());
						}
					} else {
						double tax = 0;
						bmoPaccount.getTaxApplies().setValue(0);
						bmoPaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
						bmoPaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals((bmoPaccount.getAmount().toDouble() + tax) - holdBack));
					}
				} else {					
					bmoPaccount.getTax().setValue(0);
					bmoPaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble() - holdBack));
				}

				printDevLog("-- Antes updatePayments() --");
				// Se actualiza estatus de pago
				this.updatePayments(pmConn, bmoPaccount, bmUpdateResult);
				printDevLog("-- Despues updatePayments() --");
				// Actualiza Recibos de Ordenes de Compra si existia liga
				if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {					
					pmRequisitionReceipt.updatePaymentStatus(pmConn, bmoRequisitionReceipt, bmUpdateResult);
				}
			} else {
				if (bmoPaccount.getTaxApplies().toBoolean()) {					
					//Calcular el IVA
					double taxRate = (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);						 
					double tax = (bmoPaccount.getAmount().toDouble() * taxRate);
					// Si viene de un XML Calcula el iva por el iva de los Items
					if(bmoPaccount.getIsXml().toBoolean()) {
						tax = calculateTaxByItems(pmConn,bmoPaccount.getId());
					}
					bmoPaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
					bmoPaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble() + tax));
					
					if(bmoPaccount.getIsXml().toBoolean()) {
						bmoPaccount.getTotal().setValue(bmoPaccount.getTotal().toDouble() - bmoPaccount.getSumRetention().toDouble());
					}
				} else {
					bmoPaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(this.sumPaccountItems(pmConn, bmoPaccount, bmUpdateResult)));
					bmoPaccount.getTax().setValue(0);
					bmoPaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble()));
				}
				printDevLog("-- Antes updatePayments() --");
				// Se actualiza estatus de pago
				this.updatePayments(pmConn, bmoPaccount, bmUpdateResult);
				printDevLog("-- Despues updatePayments() --");
			}
		} else {

			//Sumar los items de la cxp si es de tipo otros
			bmoPaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(this.sumPaccountItems(pmConn, bmoPaccount, bmUpdateResult)));

			if (bmoPaccount.getTaxApplies().toBoolean()) {					
				//Calcular el IVA
				double taxRate = (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100);						 
				double tax = (bmoPaccount.getAmount().toDouble() * taxRate);
				// Si viene de un XML Calcula el iva por el iva de los Items
				if(bmoPaccount.getIsXml().toBoolean()) {
					tax = calculateTaxByItems(pmConn,bmoPaccount.getId());
				}
				bmoPaccount.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
				bmoPaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble() + tax));
				if(bmoPaccount.getIsXml().toBoolean()) {
					bmoPaccount.getTotal().setValue(bmoPaccount.getTotal().toDouble() - bmoPaccount.getSumRetention().toDouble());
				}
			} else {
				bmoPaccount.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(this.sumPaccountItems(pmConn, bmoPaccount, bmUpdateResult)));
				bmoPaccount.getTax().setValue(0);
				bmoPaccount.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getAmount().toDouble()));
			}
			printDevLog("-- Antes updatePayments() --");
			this.updatePayments(pmConn, bmoPaccount, bmUpdateResult);
			printDevLog("-- Despues updatePayments() --");

			// Actualiza Recibos de Ordenes de Compra si existia liga
			if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {
				PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
				BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());
				pmRequisitionReceipt.updatePaymentStatus(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			}
		}

		bmoPaccount.getDiscount().setValue(SFServerUtil.roundCurrencyDecimals(calculeDiscount(pmConn,bmoPaccount.getId())));
		bmoPaccount.getAmount().setValue(bmoPaccount.getAmount().toDouble() + 	bmoPaccount.getDiscount().toDouble());
		super.save(pmConn, bmoPaccount, bmUpdateResult);
	}

	private double calculateTaxByItems(PmConn pmConn,int paccountId) throws SFPmException {
		double sumTax = 0;
		String sql = "SELECT SUM(pait_tax) AS sumTax FROM paccountitems WHERE pait_paccountid = " + paccountId;
		pmConn.doFetch(sql);

		if(pmConn.next()) {
			sumTax = pmConn.getDouble("sumTax");
		}		

		return sumTax;
	}
	private double calculeDiscount(PmConn pmConn,int paccountId) throws SFPmException {
		double result = 0.0;
		String sql = "SELECT SUM(pait_discount) AS result FROM paccountitems WHERE pait_paccountid = " + paccountId;
		System.err.println();
		pmConn.doFetch(sql);

		if(pmConn.next()) result = pmConn.getDouble("result");

		return result;
	}

	//Actualizar la partidad del presupuesto
	public void updateBudgetItem(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		BmoPaccount bmoPaccount = new BmoPaccount();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		Iterator<BmObject> paccountIterator = pmPaccount.list(pmConn, bmFilter).iterator();

		PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());

		while (paccountIterator.hasNext()) {
			bmoPaccount = (BmoPaccount)paccountIterator.next();

			bmoPaccount.getBudgetItemId().setValue(bmoRequisitionReceipt.getBudgetItemId().toInteger());			
			pmPaccount.saveSimple(pmConn, bmoPaccount, bmUpdateResult);

			// Actualizar los conceptos de MB
			pmBankMovConcept.updateBudgetItem(pmConn, bmoPaccount, bmUpdateResult);
		}
	}	

	//Ligar los documentos de la OC
	public void createLinkReqiFiles(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		PmFile pmFile = new PmFile(getSFParams());
		BmoFile bmoFile = new BmoFile();
		BmoRequisition bmoRequisition = new BmoRequisition();

		ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();


		BmFilter filterProgram = new BmFilter();
		filterProgram.setValueFilter(bmoFile.getKind(), bmoFile.getProgramId(), getSFParams().getProgramId(bmoRequisition.getProgramCode()));
		filterList.add(filterProgram);

		BmFilter filterForeign= new BmFilter();
		filterForeign.setValueFilter(bmoFile.getKind(), bmoFile.getForeignId(), bmoPaccount.getRequisitionId().toInteger());			
		filterList.add(filterForeign);

		Iterator<BmObject> fileIterator = pmFile.list(filterList).iterator();

		BmoFile bmoNewFile = new BmoFile();

		while (fileIterator.hasNext()) {
			bmoFile = (BmoFile)fileIterator.next();

			//validar si ya exite en files con el registro de CXṔ (NOMBRE BLOB_KEY

			ArrayList<BmFilter> filterListCxP = new ArrayList<BmFilter>();

			BmFilter filterProgramCxP = new BmFilter();
			filterProgramCxP.setValueFilter(bmoFile.getKind(), bmoFile.getProgramId(), getSFParams().getProgramId(bmoPaccount.getProgramCode()));
			filterListCxP.add(filterProgramCxP);

			BmFilter filterByName = new BmFilter();
			filterByName.setValueFilter(bmoFile.getKind(), bmoFile.getName(), bmoFile.getName().toString());
			filterListCxP.add(filterByName);

			Iterator<BmObject> fileIteratorCxP = pmFile.list(pmConn, filterListCxP).iterator();
			//Si NO existe el registro en file de la CXP se registra 
			if (!fileIteratorCxP.hasNext()) {
				bmoNewFile = new BmoFile();
				bmoNewFile.getName().setValue(bmoFile.getName().toString());
				bmoNewFile.getDescription().setValue(bmoFile.getDescription().toString());
				bmoNewFile.getEncoding().setValue(bmoFile.getEncoding().toString());
				bmoNewFile.getBlobkey().setValue(bmoFile.getBlobkey().toString());
				bmoNewFile.getForeignId().setValue(bmoPaccount.getId());
				bmoNewFile.getProgramId().setValue(getSFParams().getProgramId(bmoPaccount.getProgramCode()));

				pmFile.save(pmConn, bmoNewFile, bmUpdateResult);
			}
		}

	}	

	public boolean rocInCxP(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		boolean result =  false;


		sql = " SELECT pacc_requisitionreceiptid FROM paccounts " +
				" WHERE pacc_requisitionreceiptid = " + bmoPaccount.getRequisitionReceiptId().toInteger() + 
				" AND pacc_paccountid <> " + bmoPaccount.getId(); 
		pmConn.doFetch(sql);
		if (pmConn.next()) result = true;

		return result;
	}

	// Actualizar pagos/saldo y estatus de pago
	private void updatePayments(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		double totalPayments = 0, totalBalance = 0, sumMB = 0, sumNC = 0, sumMBD = 0;
		String sql = "";
		//PmCurrency pmCurrency = new PmCurrency(getSFParams());

		// Sumar pagos MB (pago proveedor u otros)
		sql = " SELECT bkmv_code, bkac_currencyid, bkmc_currencyparity, bkmc_amount, bkmc_amountconverted " +
				" FROM " + formatKind("bankmovconcepts") + 
				" LEFT JOIN " + formatKind("bankmovements") + " ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN " + formatKind("bankmovtypes") + " ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +	
				" LEFT JOIN " + formatKind("bankaccounts") + " ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
				" LEFT JOIN " + formatKind("paccounts") + " ON (bkmc_paccountid = pacc_paccountid) " +
				" LEFT JOIN " + formatKind("currencies") + " ON (bkac_currencyid = cure_currencyid) " +		      
				" WHERE bkmc_paccountid > 0 " +
				" AND bkmc_paccountid = " + bmoPaccount.getId() +		  
				" AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'";			

		printDevLog("sql_bancos: "+sql);
		pmConn.doFetch(sql);
		while (pmConn.next()) {

			printDevLog("mb: "+ pmConn.getString("bkmv_code"));
			printDevLog("moneda pacc: " + bmoPaccount.getCurrencyId());
			printDevLog("tc pacc: " +bmoPaccount.getCurrencyParity());
			printDevLog("moneda cuentaBanco: " + pmConn.getInt("bkac_currencyid"));
			printDevLog("tc concepto: " + pmConn.getDouble("bkmc_currencyparity"));
			printDevLog("monto concepto: " + pmConn.getDouble("bkmc_amount"));
			printDevLog(" --- ");

			// Conversion de mb a pacc
			//				sumMB += (pmCurrency.currencyExchange(pmConn.getDouble("bkmc_amount"), 
			//								pmConn.getInt("bkac_currencyid"), 
			//								pmConn.getDouble("bkmc_currencyparity"), 
			//								bmoPaccount.getCurrencyId().toInteger(), 
			//								bmoPaccount.getCurrencyParity().toDouble()));

			if (pmConn.getDouble("bkmc_amountconverted") > 0)
				sumMB += pmConn.getDouble("bkmc_amountconverted");
			else 
				sumMB += pmConn.getDouble("bkmc_amount");
		}	

		// Sumar pagos de Notas de Crédito
		if (bmoPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE) 
				&& bmoPaccount.getBmoPaccountType().getType().equals(BmoPaccountType.TYPE_DEPOSIT)) {
			// Si la CXP es Nota de Crédito, sumar pagos directamente.
			sql = " SELECT pacc_code, pacc_currencyid, pacc_currencyparity, pass_amount, pass_amountconverted " + 
					" FROM " + formatKind("paccountassignments") + 
					" LEFT JOIN " + formatKind("paccounts") + " ON (pass_paccountid = pacc_paccountid) " +
					" LEFT JOIN " + formatKind("currencies") + " ON (pacc_currencyid = cure_currencyid) " +	
					" LEFT JOIN " + formatKind("paccounttypes") + " ON (pact_paccounttypeid = pacc_paccounttypeid) " +
					" WHERE pact_category  = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
					" AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +
					" AND pacc_paccountid = " + bmoPaccount.getId();

		} else {
			// NO es Nota de credito, sumar pagos de Notas de Crédito APLICADAS
			sql = " SELECT pacc_code, pacc_currencyid, pacc_currencyparity, pass_amount, pass_amountconverted " + 
					" FROM " + formatKind("paccountassignments") + 
					" LEFT JOIN " + formatKind("paccounts") + " ON (pass_paccountid = pacc_paccountid) " +
					" LEFT JOIN " + formatKind("currencies") + " ON (pacc_currencyid = cure_currencyid) " +	
					" LEFT JOIN " + formatKind("paccounttypes") + " ON (pact_paccounttypeid = pacc_paccounttypeid) " +
					" WHERE pact_category = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
					" AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +
					" AND pass_foreignpaccountid IN ( " +
					" SELECT pacc_paccountid FROM " + formatKind("paccounts") + 
					" LEFT JOIN " + formatKind("paccounttypes") + " ON (pact_paccounttypeid = pacc_paccounttypeid) " +  
					" WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
					" AND pacc_paccountid = " + bmoPaccount.getId() +				
					" ) ";
		}

		printDevLog("sql_notaCredito: "+sql);
		pmConn.doFetch(sql);		
		while (pmConn.next()) {

			printDevLog("cp: "+ pmConn.getString("pacc_code"));
			printDevLog("monedaPACC: " + bmoPaccount.getCurrencyId());
			printDevLog("tcPACC: " +bmoPaccount.getCurrencyParity());
			printDevLog("monedaPASS: " + pmConn.getInt("pacc_currencyid"));
			printDevLog("tcPASS: " + pmConn.getDouble("pacc_currencyparity"));
			printDevLog(" --- ");

			// Conversion de pass a pacc
			//				sumNC += (pmCurrency.currencyExchange(pmConn.getDouble("pass_amount"), 
			//							pmConn.getInt("pacc_currencyid"), 
			//							pmConn.getDouble("pacc_currencyparity"), 
			//							bmoPaccount.getCurrencyId().toInteger(), 
			//							bmoPaccount.getCurrencyParity().toDouble())
			//						);

			if (pmConn.getDouble("pass_amountconverted") > 0) {
				sumNC += pmConn.getDouble("pass_amountconverted");
			} else {
				sumNC += pmConn.getDouble("pass_amount");
			}
		}

		// Sumar MB de devoluciones a proveedor
		sql = " SELECT bkmv_code, bkac_currencyid, bkmc_currencyparity, bkmc_amount, bkmc_amountconverted " + 
				" FROM " + formatKind("bankmovconcepts") +		      
				" LEFT JOIN " + formatKind("bankmovements") + " ON (bkmv_bankmovementid = bkmc_bankmovementid) " +
				" LEFT JOIN " + formatKind("bankmovtypes") + " ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN " + formatKind("bankaccounts") + " ON (bkmv_bankaccountid = bkac_bankaccountid) " +
				" WHERE bkmc_paccountid > 0 " + 
				" AND bkmc_paccountid = " + bmoPaccount.getId() +
				" AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'" +
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'" +
				" AND bkmv_status <> '" + BmoBankMovement.STATUS_CANCELLED + "'" ; 

		printDevLog("sql bancos_devoluciones: "+sql);
		pmConn.doFetch(sql);
		while (pmConn.next()) {
			// Conversion de mb a pacc
			//				sumMBD += (pmCurrency.currencyExchange(pmConn.getDouble("bkmc_amount"), 
			//							pmConn.getInt("bkac_currencyid"), 
			//							pmConn.getDouble("bkmc_currencyparity"), 
			//							bmoPaccount.getCurrencyId().toInteger(), 
			//							bmoPaccount.getCurrencyParity().toDouble())
			//						);

			if (pmConn.getDouble("bkmc_amountconverted") > 0)
				sumMBD += pmConn.getDouble("bkmc_amountconverted");
			else 
				sumMBD += pmConn.getDouble("bkmc_amount");
		}

		// pagos = pagos MB + pagos NC - dev Prov
		totalPayments = sumMB + sumNC - sumMBD;

		// saldo = totalCXP - totalPayments[pagos MB - pagos NC + dev Prov]
		totalBalance = bmoPaccount.getTotal().toDouble() - totalPayments;

		// Asigna estatus
		if (bmoPaccount.getTotal().toDouble() <= totalPayments)
			bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_TOTAL);
		else
			bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);

		bmoPaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(totalBalance));
		bmoPaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));


		printDevLog(" --- Totales ("+ bmoPaccount.getCode() +") ---");
		printDevLog("Total CxP " + SFServerUtil.roundCurrencyDecimals(bmoPaccount.getTotal().toDouble()));
		printDevLog("Pagos MB: " + SFServerUtil.roundCurrencyDecimals(sumMB));
		printDevLog("Pagos NC: " + SFServerUtil.roundCurrencyDecimals(sumNC));
		printDevLog("Dev MB: " + SFServerUtil.roundCurrencyDecimals(sumMBD));

		printDevLog("Total Pagos : " + SFServerUtil.roundCurrencyDecimals(totalPayments));
		printDevLog("Total Saldo : " + SFServerUtil.roundCurrencyDecimals(totalBalance));

		// Almacenar el cambio de estatus
		super.save(pmConn, bmoPaccount, bmUpdateResult);
	}

	// Actualizar saldo de los cargos
	//	private void updateWithdrawBalance(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
	//		//Obtener los pagos ligados al cargo
	//		double totalPayments = 0;
	//		String sql = "";
	//		
	//		//Sumar los conceptos de banco
	//		sql = " SELECT * FROM bankmovconcepts " +
	//		      " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
	//			  " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +	
	//			  " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
	//		      " LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
	//		      " LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +		      
	//			  " WHERE bkmc_paccountid = " + bmoPaccount.getId() +			  
	//			  " AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'";			  
	//		pmConn.doFetch(sql);
	//		while(pmConn.next()) {
	//			if (pmConn.getDouble("bkmc_amountconverted") > 0)
	//				totalPayments += pmConn.getDouble("bkmc_amountconverted");
	//			else 
	//				totalPayments += pmConn.getDouble("bkmc_amount");
	//		}
	//		
	//		//Sumar los conceptos de banco
	//		sql = " SELECT * FROM bankmovconcepts " +		
	//		      " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
	//		      " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
	//			  " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
	//		      " LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
	//		      " LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +		      
	//			  " WHERE bkmc_paccountid = " + bmoPaccount.getId() +
	//			  " AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'";			  
	//		pmConn.doFetch(sql);
	//		while(pmConn.next()) {
	//			if (pmConn.getDouble("bkmc_amountconverted") > 0)
	//				totalPayments -= pmConn.getDouble("bkmc_amountconverted");
	//			else 
	//				totalPayments -= pmConn.getDouble("bkmc_amount");
	//		}
	//		
	//		
	//		//Notas de Crédito
	//		sql = " SELECT * FROM paccountassignments " +
	//				" LEFT JOIN paccounts ON (pass_paccountid = pacc_paccountid) " +
	//				" LEFT JOIN currencies ON (pacc_currencyid = cure_currencyid) " +	
	//				" LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	//				" WHERE pact_category  = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
	//				" AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +				
	//				" AND pass_foreignpaccountid IN ( " +
	//				" SELECT pacc_paccountid FROM paccounts " +
	//				"	LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +  
	//				"	WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
	//				"	AND pacc_paccountid = " + bmoPaccount.getId() +				
	//				"  ) ";
	//		pmConn.doFetch(sql);		
	//		while (pmConn.next()) {
	//			if (pmConn.getDouble("pass_amountconverted") > 0) {
	//				totalPayments += pmConn.getDouble("pass_amountconverted");
	//			} else {
	//				totalPayments += pmConn.getDouble("pass_amount");
	//			}
	//		}
	//		
	//		// Asigna estatus
	//		if (bmoPaccount.getTotal().toDouble() <= totalPayments)
	//			bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_TOTAL);
	//		else
	//			bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);
	//		
	//		bmoPaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getTotal().toDouble() - totalPayments));
	//		bmoPaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));
	//
	//		// Almacenar el cambio de estatus
	//		super.save(pmConn, bmoPaccount, bmUpdateResult);
	//	}
	//
	//	// Actualiza valores de saldos
	//	private void updateDepositBalance(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
	//		//Obtener los pagos ligados al cargo
	//		double totalPayments = 0;
	//		String sql = "";
	//
	//		//Notas de Crédito
	//		sql = " SELECT * FROM paccountassignments " +
	//				" LEFT JOIN paccounts ON (pass_paccountid = pacc_paccountid) " +
	//				" LEFT JOIN currencies ON (pacc_currencyid = cure_currencyid) " +	
	//				" LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +
	//				" WHERE pact_category  = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
	//				" AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +
	//				" AND pacc_paccountid = " + bmoPaccount.getId();
	//
	//		pmConn.doFetch(sql);		
	//		while (pmConn.next()) {
	//			if (pmConn.getDouble("pass_amountconverted") > 0) 
	//				totalPayments += pmConn.getDouble("pass_amountconverted");
	//			else 
	//				totalPayments += pmConn.getDouble("pass_amount");
	//		}
	//		
	//		//Sumar las devoluciones de banco
	//		sql = " SELECT * FROM bankmovconcepts " +
	//		      " LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
	//			  " LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
	//		      " LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
	//		      " LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
	//		      " LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
	//			  " WHERE bkmc_paccountid = " + bmoPaccount.getId() + 
	//			  " AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'" +
	//			  " AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'"; 
	//		pmConn.doFetch(sql);
	//		while(pmConn.next()) {
	//			if (pmConn.getDouble("bkmc_currencyparity") != 1)
	//				totalPayments += pmConn.getDouble("bkmc_amount");
	//			else 
	//				totalPayments += pmConn.getDouble("bkmc_amountconverted");
	//		}
	//
	//		// Asigna estatus
	//		if (bmoPaccount.getTotal().toDouble() <= totalPayments) {
	//			bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_TOTAL);
	//		} else {
	//			bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);
	//		}
	//
	//		// Calcular los saldos		
	//		bmoPaccount.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoPaccount.getTotal().toDouble() - totalPayments));
	//		bmoPaccount.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));
	//
	//		// Almacenar el cambio de estatus
	//		super.save(pmConn, bmoPaccount, bmUpdateResult);
	//	}

	public double sumPaccountItems(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";

		BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());

		PmPaccountType pmPaccountType = new PmPaccountType(getSFParams());
		BmoPaccountType bmoPaccountType = (BmoPaccountType)pmPaccountType.get(pmConn, bmoPaccount.getPaccountTypeId().toInteger());

		if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0)
			bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());

		double sumItems = 0;

		if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0 && bmoPaccountType.getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {
			sumItems = bmoRequisitionReceipt.getAmount().toDouble();

		} else {
			sql = " SELECT SUM(pait_amount) AS sumItems FROM paccountitems " +
					" WHERE pait_paccountid = " + bmoPaccount.getId();
			pmConn.doFetch(sql);
			if (pmConn.next()) 
				sumItems = pmConn.getDouble("sumItems");
		}

		return sumItems;
	}

	public double sumPaccountAssignments(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";

		double sumItems = 0;
		sql = " SELECT SUM(pass_amount) AS sumAssignments FROM paccountassignments " +
				" WHERE pass_paccountid = " + bmoPaccount.getId();
		pmConn.doFetch(sql);

		if (pmConn.next()) 
			sumItems = pmConn.getDouble("sumAssignments"); 

		return sumItems;
	}
	
	// Revisa si tiene pagos aplicados
	public boolean hasPayments(PmConn pmConn, int raccountId) throws SFPmException {
		boolean hasPayment = false;
		if (payments(pmConn, raccountId) > 0) hasPayment = true;
		return hasPayment;
	}

	// Obtener SOLO el monto de lo pagos
	public double payments(PmConn pmConn, int paccountId) throws SFPmException {
		double payments = 0;
		// Sumar los conceptos de banco
		String sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid ) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN paccounts ON (bkmc_paccountid = pacc_paccountid) " +
				" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
				" WHERE bkmc_paccountid = " + paccountId + 
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_CXP + "'"; 
		pmConn.doFetch(sql);
		while(pmConn.next()) {			
			if (pmConn.getDouble("bkmc_amountconverted") > 0)
				payments += pmConn.getDouble("bkmc_amountconverted");
			else 
				payments += pmConn.getDouble("bkmc_amount");
		}

		// Notas de Crédito
		sql = " SELECT * FROM paccountassignments " +
				" LEFT JOIN paccounts ON (pass_paccountid = pacc_paccountid) " +
				" LEFT JOIN currencies ON (pacc_currencyid = cure_currencyid) " +	
				" LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +
				" WHERE pact_category  = '" + BmoPaccountType.CATEGORY_CREDITNOTE + "'" +  
				" AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" +				
				" AND pass_foreignpaccountid IN ( " +
				" SELECT pacc_paccountid FROM paccounts " +
				"	LEFT JOIN paccounttypes ON (pact_paccounttypeid = pacc_paccounttypeid) " +  
				"	WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
				"	AND pacc_paccountid = " + paccountId +				
				"  ) ";
		pmConn.doFetch(sql);		
		while (pmConn.next()) {
			if (pmConn.getDouble("pass_amountconverted") > 0) {
				payments += pmConn.getDouble("pass_amountconverted");
			} else {
				payments += pmConn.getDouble("pass_amount");
			}
		}

		// Redondear decimales
		payments = Double.parseDouble(SFServerUtil.roundCurrencyDecimals(payments));

		return payments;
	}

	//	private void assignmentRequisitionAdvance(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
	//		String sql = "";
	//
	//		//Comprobar si existe una aplicacion (Anticipo OC) sin cxp de origen
	//		sql = " SELECT  * FROM paccountassignments " +
	//				" LEFT JOIN paccounts ON (pass_paccountid = pacc_paccountid) " +
	//				" LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +	
	//				" WHERE pacc_requisitionid = " + bmoPaccount.getRequisitionId().toInteger() +
	//				" AND pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'" + 
	//				" AND pacc_parentid <> 0 ";
	//		pmConn.doFetch(sql);		
	//		if (pmConn.next()) {
	//
	//
	//			//Ligar el cargo al abono			
	//			sql = " UPDATE paccounts SET pacc_parentid = " + bmoPaccount.getId() +
	//					" WHERE pacc_paccountid = " + pmConn.getInt("pacc_paccountid");
	//			pmConn.doUpdate(sql);
	//
	//			sql = " UPDATE bankmovconcepts SET bkmc_paccountid = " + bmoPaccount.getId() +
	//					" WHERE bkmc_foreignid = " + pmConn.getInt("pacc_paccountid");
	//			pmConn.doUpdate(sql);
	//
	//			//ligar a la aplicacion
	//			sql = " UPDATE paccountassignments SET pass_foreignpaccountid = " + bmoPaccount.getId() +
	//					", pass_code = '" + bmoPaccount.getCode().toString() + "'" +
	//					", pass_invoiceno = '" + bmoPaccount.getInvoiceno().toString() + "'" +
	//					" WHERE pass_paccountid = " + pmConn.getInt("pacc_paccountid");
	//			pmConn.doUpdate(sql);
	//
	//		} 
	//	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String data, String value) throws SFException {		
		double result = 0;
		bmoPaccount = (BmoPaccount)bmObject;

		if (data.equals(BmoPaccount.ACTION_NEWPACCOUNT)) {
			if(bmoPaccount.getXmlFileUpload().toString().equals("") || bmoPaccount.getXmlFileUpload().toString().length() <= 2 ) {
				bmUpdateResult.addError(bmoPaccount.getXmlFileUpload().getName(), "Debe seleccionar un archivo");
				return bmUpdateResult;
			}
			else {
				bmUpdateResult = createNewPaccount(value, bmUpdateResult,bmoPaccount);
			}
		} 
		else {
			PmConn pmConn = new PmConn(getSFParams());
			pmConn.open();
			// Obtener el saldo de la orden de compra
			try {
				result = getRequisitionReceitpBalance(pmConn, Integer.parseInt(value), bmUpdateResult);
				bmUpdateResult.setMsg("" + result);
			} catch (SFException e) {
				throw new SFException(this.getClass().getName() + "-action() " + e.toString());
			} finally {
				pmConn.close();
			}
		}
		return bmUpdateResult;
	}


	// Crear Cuenta por cobrar XML
	private BmUpdateResult createNewPaccount(String value, BmUpdateResult bmUpdateResult, BmoPaccount bmoPaccount) throws SFException {
		// Variables para guardar datos del XML
		String tax = "0.0",discount = "";
		String retentions = "0.0";
		int currencyId = 0;
		int idSupplier = 0;
		int idCompany = 0;
		String invoiceno = "";
		int taxApplies = 0;
		double amount = 0.0;
		double total = 0.0;
		int budgetItemId = 0;
		double currencyParity = 0;
		int requisitionReceiptId = 0;
		int requisitionid = 0;
		String reqi_code = "";
		String rqtp_type = "";
		String requisitionReceip = "";
		String date = "";
		String supplierRfc = "";
		String companyRfc = "";
		String description = "Otros";

		int paccountTypeId = 0;

		// Separa el campo value
		StringTokenizer tabs = new StringTokenizer(value, "|");
		while (tabs.hasMoreTokens()) {
			requisitionReceip = (tabs.nextToken());
			value = tabs.nextToken();
		}

		value = value.substring(0, value.indexOf("."));

		DocumentBuilder documentBuilder;
		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		PmConn pmConn2 = new PmConn(getSFParams());
		pmConn2.open();
		pmConn2.disableAutoCommit();

		try {
			String link = GwtUtil.parseImageLink(getSFParams(), bmoPaccount.getXmlFileUpload());

			URL url = new URL(link);

			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String input;
			String xml = "";
			while ((input = br.readLine()) != null) {
				xml = xml + input;
			}
			xml = xml.trim().replaceFirst("^([\\W]+)<", "<");

			InputSource archivo = new InputSource();
			archivo.setCharacterStream(new StringReader(xml));

			documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = documentBuilder.parse(archivo);
			doc.getDocumentElement().normalize();

			NodeList comprobante = doc.getElementsByTagName("cfdi:Comprobante");
			if (comprobante == null || comprobante.getLength() == 0) {
				bmUpdateResult.addError(bmoPaccount.getXmlFileUpload().getName(), "Error al leer el tag Comprobante.");
				return bmUpdateResult;
			}
			Node cNode = comprobante.item(0);
			Element eElement = (Element) cNode;

			NodeList impuestos = doc.getElementsByTagName("cfdi:Impuestos");
			if (impuestos != null && impuestos.getLength() > 0) {
				for (int i = 0; i <  impuestos.getLength(); i++) {
					Node cNode2 = impuestos.item(i);
					Element eElementI = (Element) cNode2;
					
					//impuestos retenidos
					if (!eElementI.getAttribute("TotalImpuestosRetenidos").isEmpty()) {
						retentions = eElementI.getAttribute("TotalImpuestosRetenidos");
					} else {
						retentions = eElementI.getAttribute("TotalImpuestosRetenidos");
					}
					//impuestos trasladados
					if (!eElementI.getAttribute("TotalImpuestosTrasladados").isEmpty()) {
						tax = eElementI.getAttribute("TotalImpuestosTrasladados");
					} else {
						tax = eElementI.getAttribute("TotalImpuestosTrasladados");
					}

				}
				taxApplies = 1;
			}

			NodeList importe = doc.getElementsByTagName("cfdi:Traslado");

			if (tax.equals("0.0")) {
				if (importe != null && importe.getLength() > 0) {
					for (int i = 0; i <  importe.getLength(); i++) {
						Node cNode2 = importe.item(i);
						Element eElementI = (Element) cNode2;

						if (!eElementI.getAttribute("Importe").isEmpty()) {
							tax = eElementI.getAttribute("Importe");
						} else {
							tax = eElementI.getAttribute("importe");
						}
					}
					taxApplies = 1;
				}
			}


			try {
				String currency = eElement.getAttribute("Moneda").substring(0, 1);
				String sql = "";

				if(currency.equals("P") || currency.equals("p")) {
					currency = "M";
				}

				sql = "SELECT cure_currencyid FROM currencies WHERE cure_code LIKE '" + currency + "%'";
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					currencyId = pmConn.getInt("cure_currencyid");
				}

			} catch (Exception e) {
				bmUpdateResult.addMsg("Error al buscar la moneda" + e.toString());
			}

			//paridad
			if (!eElement.getAttribute("TipoCambio").isEmpty()) {
				String parity = eElement.getAttribute("TipoCambio");
				currencyParity = Double.parseDouble(parity);
			} else if (!eElement.getAttribute("Tipocambio").isEmpty()) {
				String parity = eElement.getAttribute("Tipocambio");
				currencyParity = Double.parseDouble(parity);
			} else  if (!eElement.getAttribute("tipocambio").isEmpty()) {
				String parity = eElement.getAttribute("tipocambio");
				currencyParity = Double.parseDouble(parity);
			}


			if (!eElement.getAttribute("Folio").isEmpty()) {
				invoiceno = eElement.getAttribute("Folio");
			} else {
				invoiceno = eElement.getAttribute("folio");
			}

			if (!eElement.getAttribute("SubTotal").isEmpty()) {
				amount = Double.parseDouble(eElement.getAttribute("SubTotal"));

			} else {
				amount = Double.parseDouble(eElement.getAttribute("subTotal"));
			}

			if (!eElement.getAttribute("Total").isEmpty()) {
				total = Double.parseDouble(eElement.getAttribute("Total"));
			} else {
				total = Double.parseDouble(eElement.getAttribute("total"));
			}

			if (!eElement.getAttribute("Fecha").isEmpty()) {
				date = eElement.getAttribute("Fecha");
			} else {
				date = eElement.getAttribute("fecha");
			}
			date = date.substring(0, 10);
			// Proveedor
			NodeList Supplier = doc.getElementsByTagName("cfdi:Emisor");
			Node cNode3 = Supplier.item(0);
			Element eElementS = (Element) cNode3;

			if (!eElementS.getAttribute("Rfc").isEmpty()) {
				supplierRfc = eElementS.getAttribute("Rfc");
			} else {
				supplierRfc = eElementS.getAttribute("rfc");
			}

			// Empresa
			NodeList Company = doc.getElementsByTagName("cfdi:Receptor");
			Node cNode4 = Company.item(0);
			Element eElementCo = (Element) cNode4;
			if (!eElementCo.getAttribute("Rfc").isEmpty()) {
				companyRfc = eElementCo.getAttribute("Rfc");
			} else {
				companyRfc = eElementCo.getAttribute("rfc");
			}


			//Descuento
			if (!eElement.getAttribute("Descuento").isEmpty()) {
				discount = eElement.getAttribute("Descuento");
			} else if (!eElement.getAttribute("descuento").isEmpty()) {
				discount = eElement.getAttribute("descuento");
			} else {
				discount = "0.0";
			}


			String sql = "";


			sql = "SELECT supl_supplierid FROM suppliers WHERE supl_rfc = '" + supplierRfc + "'";
			pmConn.doFetch(sql);

			if (pmConn.next()) {
				idSupplier = pmConn.getInt("supl_supplierid");
			} else {
				bmUpdateResult.addError(bmoPaccount.getXmlFileUpload().getName(), "No se encontro proveedor en el sistema.");
				return bmUpdateResult;
			}
			sql = "SELECT * FROM paccounts WHERE pacc_invoiceno = '" + invoiceno + "' AND pacc_supplierid =" + idSupplier;
			pmConn.doFetch(sql);
			if (pmConn.next()) {				
				bmUpdateResult.addError(bmoPaccount.getXmlFileUpload().getName(), "La factura ya fue procesada. CXP: " + pmConn.getString("pacc_code"));
				return bmUpdateResult;
			}

			if (Integer.parseInt(requisitionReceip.trim()) == 0) {

				// Partida presupuestal, recibo orden de compra
				sql = "SELECT COUNT(*) total " + "FROM requisitionreceipts " 
						+ "LEFT JOIN requisitions ON requisitions.reqi_requisitionid = requisitionreceipts.rerc_requisitionid "
						+ " LEFT JOIN requisitiontypes ON rqtp_requisitiontypeid = reqi_reqpaytypeid "
						+ " WHERE rerc_supplierId =" + idSupplier 
						+ " AND rerc_payment = '" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED + "' " 
						+ " AND rerc_status = '" + BmoRequisitionReceipt.STATUS_AUTHORIZED + "' "
						+ " AND rerc_total = " + total
						+ " AND rerc_tax > " + 0;;

						pmConn.doFetch(sql);
						if (pmConn.next()) {
							if (pmConn.getInt("total") > 1) {
								bmUpdateResult.addMsg(BmoPaccount.MORE_REQUISITIONRECEIPTS);
								// bmUpdateResult.setId(idSupplier);
								bmoPaccount.getTotal().setValue(total);
								bmoPaccount.getTaxApplies().setValue(taxApplies);
								bmoPaccount.getSupplierId().setValue(idSupplier);
								bmUpdateResult.setBmObject(bmoPaccount);
								return bmUpdateResult;
							} else if (pmConn.getInt("total") == 1) {
								if (taxApplies == 1) {
									sql = "SELECT rerc_name,rerc_budgetitemid,rerc_currencyParity,rerc_requisitionid,"
											+ "rerc_requisitionreceiptid,reqi_code,rqtp_type " + "FROM requisitionreceipts "
											+ "LEFT JOIN requisitions ON "
											+ "requisitions.reqi_requisitionid = requisitionreceipts.rerc_requisitionid "
											+ "LEFT JOIN requisitiontypes ON rqtp_requisitiontypeid = reqi_reqpaytypeid "
											+ "WHERE rerc_supplierId =" + idSupplier 
											+ " AND rerc_payment = '" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED + "' " 
											+ " AND rerc_status = '" + BmoRequisitionReceipt.STATUS_AUTHORIZED + "' " 
											+ " AND rerc_total = " + total
											+ " AND rerc_tax > " + 0;
									pmConn.doFetch(sql);
									if (pmConn.next()) {
										budgetItemId = pmConn.getInt("rerc_budgetitemid");
										currencyParity = pmConn.getDouble("rerc_currencyParity");
										requisitionid = pmConn.getInt("rerc_requisitionid");
										requisitionReceiptId = pmConn.getInt("rerc_requisitionreceiptid");
										reqi_code = pmConn.getString("reqi_code");
										rqtp_type = pmConn.getString("rqtp_type");
										description = pmConn.getString("rerc_name");

										sql = "SELECT pact_paccounttypeid FROM paccounttypes "
												+ " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "' "
												+ " AND pact_category = '" + BmoPaccountType.CATEGORY_REQUISITION + "' ";
										pmConn.doFetch(sql);
										if (pmConn.next()) {
											paccountTypeId = pmConn.getInt("pact_paccounttypeid");
										}
									}
								} else {

									sql = "SELECT rerc_name,rerc_budgetitemid,rerc_currencyParity,rerc_requisitionid, "
											+ " rerc_requisitionreceiptid,reqi_code,rqtp_type "
											+ " FROM requisitionreceipts "
											+ " LEFT JOIN requisitions ON (requisitions.reqi_requisitionid = requisitionreceipts.rerc_requisitionid) "
											+ " LEFT JOIN requisitiontypes ON (rqtp_requisitiontypeid = reqi_reqpaytypeid) "
											+ " WHERE rerc_supplierId =" + idSupplier 
											+ " AND rerc_payment = '" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED + "' " 
											+ " AND rerc_status = '" + BmoRequisitionReceipt.STATUS_AUTHORIZED + "' " 
											+ " AND rerc_total = " + total
											+ " AND rerc_tax = " + 0;
									pmConn.doFetch(sql);
									if (pmConn.next()) {
										budgetItemId = pmConn.getInt("rerc_budgetitemid");
										currencyParity = pmConn.getDouble("rerc_currencyParity");
										requisitionid = pmConn.getInt("rerc_requisitionid");
										requisitionReceiptId = pmConn.getInt("rerc_requisitionreceiptid");
										reqi_code = pmConn.getString("reqi_code");
										rqtp_type = pmConn.getString("rqtp_type");
										description = pmConn.getString("rerc_name");

										sql = "SELECT pact_paccounttypeid FROM paccounttypes "
												+ " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "' "
												+ " AND pact_category = '" + BmoPaccountType.CATEGORY_REQUISITION + "' ";										
										pmConn.doFetch(sql);
										if (pmConn.next()) {
											paccountTypeId = pmConn.getInt("pact_paccounttypeid");
										}
									}
								}
							}
						}
			} else {
				if (!requisitionReceip.isEmpty()) {
					sql = "SELECT rerc_name,rerc_budgetitemid,rerc_currencyParity,rerc_requisitionid,"
							+ " rerc_requisitionreceiptid,reqi_code,rqtp_type " 
							+ " FROM requisitionreceipts "
							+ " LEFT JOIN requisitions ON (requisitions.reqi_requisitionid = requisitionreceipts.rerc_requisitionid) "
							+ " LEFT JOIN requisitiontypes ON (rqtp_requisitiontypeid = reqi_reqpaytypeid) "
							+ " WHERE rerc_supplierId =" + idSupplier 
							+ " AND rerc_payment = '" + BmoRequisitionReceipt.PAYMENT_NOTPROVISIONED + "' " 
							+ " AND rerc_status = '" + BmoRequisitionReceipt.STATUS_AUTHORIZED + "' "
							+ " AND rerc_requisitionreceiptid = " + Integer.parseInt(requisitionReceip.trim());
					pmConn.doFetch(sql);
					if (pmConn.next()) {
						budgetItemId = pmConn.getInt("rerc_budgetitemid");
						currencyParity = pmConn.getDouble("rerc_currencyParity");
						requisitionid = pmConn.getInt("rerc_requisitionid");
						requisitionReceiptId = pmConn.getInt("rerc_requisitionreceiptid");
						reqi_code = pmConn.getString("reqi_code");
						rqtp_type = pmConn.getString("rqtp_type");
						description = pmConn.getString("rerc_name");

						sql = "SELECT pact_paccounttypeid FROM paccounttypes "
								+ " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "' "
								+ " AND pact_category = '" + BmoPaccountType.CATEGORY_REQUISITION + "'";
						pmConn.doFetch(sql);
						if (pmConn.next()) {
							paccountTypeId = pmConn.getInt("pact_paccounttypeid");
						}
					}
				}
			}



			if (paccountTypeId == 0) {
				sql = "SELECT pact_paccounttypeid FROM paccounttypes "
						+ " WHERE pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "' "
						+ " AND pact_category = '" + BmoPaccountType.CATEGORY_OTHER + "'";
				pmConn.doFetch(sql);
				if (pmConn.next()) {
					paccountTypeId = pmConn.getInt("pact_paccounttypeid");
				}
			}

			sql = "SELECT comp_companyid FROM companies WHERE comp_rfc = '" + companyRfc + "'";
			pmConn.doFetch(sql);
			if (pmConn.next()) {
				idCompany = pmConn.getInt("comp_companyid");
			} else {
				bmUpdateResult.addError(bmoPaccount.getXmlFileUpload().getName(),
						"No se encontro empresa en el sistema");
				return bmUpdateResult;
			}

			pmConn.disableAutoCommit();

			PmPaccount pmPaccount = new PmPaccount(getSFParams());		

			if(invoiceno == "")invoiceno = "N/A";
			if (invoiceno == "" || date == "" || tax == "" || amount == 0.0 || total == 0.0 || paccountTypeId == 0
					|| idSupplier == 0 || idCompany == 0 || currencyId == 0) {
				bmUpdateResult.addError(bmoPaccount.getXmlFileUpload().getName(), "Error al crear cuanta por pagar.");
				return bmUpdateResult;
			}

			BmoPaccount bmoPaccountNew = new BmoPaccount();
			bmoPaccountNew.getStatus().setValue(BmoPaccount.STATUS_REVISION);
			bmoPaccountNew.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);
			bmoPaccountNew.getInvoiceno().setValue(invoiceno);
			bmoPaccountNew.getReceiveDate().setValue(date);
			bmoPaccountNew.getDueDate().setValue(date);
			bmoPaccountNew.getDescription().setValue(description);
			bmoPaccountNew.getTaxApplies().setValue(taxApplies);
			bmoPaccountNew.getTax().setValue(Double.parseDouble(tax));
			bmoPaccountNew.getAmount().setValue(amount);
			bmoPaccountNew.getTotal().setValue(total);
			bmoPaccountNew.getBalance().setValue(total);
			bmoPaccountNew.getPaccountTypeId().setValue(paccountTypeId);
			bmoPaccountNew.getSupplierId().setValue(idSupplier);
			bmoPaccountNew.getCompanyId().setValue(idCompany);
			bmoPaccountNew.getBudgetItemId().setValue(budgetItemId);
			bmoPaccountNew.getCurrencyId().setValue(currencyId);
			bmoPaccountNew.getCurrencyParity().setValue(currencyParity);
			bmoPaccountNew.getXmlFileUpload().setValue(value);
			bmoPaccountNew.getDiscount().setValue(discount);
			bmoPaccountNew.getRequisitionId().setValue(requisitionid);// Orden de Compra
			bmoPaccountNew.getRequisitionReceiptId().setValue(requisitionReceiptId);// Recibo O.C
			bmoPaccountNew.getIsXml().setValue(1);
			bmoPaccountNew.getReqiCode().setValue(reqi_code);// Clave OC
			bmoPaccountNew.getRequisitionType().setValue(rqtp_type);// Tipo OC
			bmoPaccountNew.getSumRetention().setValue(retentions);//suma retenciones

			// pmPaccount.save(pmConn, bmoPaccountNew, bmUpdateResult);
			bmUpdateResult = pmPaccount.saveSimple(pmConn2, bmoPaccountNew, bmUpdateResult);
			// pmPaccount.saveWithXml(pmConn, bmoPaccountNew, bmUpdateResult);

			int paccountId = bmUpdateResult.getId();
			String code = BmoPaccount.CODE_PREFIX + paccountId;
			//Insertar Archivo (Jaqueline pidio que se insertara)
			if (!bmUpdateResult.hasErrors()) {				
				String sql2 = "INSERT INTO sffiles (file_name,file_blobkey,file_foreignid,file_usercreateid,file_datecreate,file_programid)\n" + 
						"VALUES (\"XML\",\"" + bmoPaccountNew.getXmlFileUpload().toString() + ".xml" +
						"\","+ paccountId + "," + getSFParams().getLoginInfo().getUserId() + ",\"" + 
						SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()) +
						"\"," + getSFParams().getProgramId(bmoPaccount.getProgramCode()) + ")";
				
				pmConn2.doUpdate(sql2);
			}

			// Actualiza Recibos de Ordenes de Compra si existia liga
			if (bmoPaccountNew.getRequisitionReceiptId().toInteger() > 0) {
				PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
				BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt) pmRequisitionReceipt.get(pmConn,
						bmoPaccountNew.getRequisitionReceiptId().toInteger());
				pmRequisitionReceipt.updatePaymentStatus(pmConn2, bmoRequisitionReceipt, bmUpdateResult);
			}

			bmoPaccountNew.getCode().setValue(code);
			super.save(pmConn2, bmoPaccountNew, bmUpdateResult);
			//Si esta ligada a una orden de cuenta hacer copia de los documentos de la Orden de Compra
			if (bmoPaccountNew.getRequisitionReceiptId().toInteger() > 0) {
				createLinkReqiFiles(pmConn2, bmoPaccountNew, bmUpdateResult);
			}

			NodeList nList = doc.getElementsByTagName("cfdi:Concepto");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElementC = (Element) nNode;

					String name = "";
					double quantity = 0.0;
					String price = "";
					String amountI = "";
					String discountItem = "";
					double retentionSum = 0.0;
					double taxItem = 0.0;

					BmoPaccountItem bmoPaccountItemNew = new BmoPaccountItem();

					if (!eElementC.getAttribute("Descripcion").isEmpty()) {
						name = eElementC.getAttribute("Descripcion");

					} else {
						name = eElementC.getAttribute("descripcion");
					}

					if (!eElementC.getAttribute("Cantidad").isEmpty()) {
						quantity = Double.parseDouble(eElementC.getAttribute("Cantidad"));
					} else {
						quantity = Double.parseDouble(eElementC.getAttribute("cantidad"));
					}

					if (!eElementC.getAttribute("ValorUnitario").isEmpty()) {
						price = eElementC.getAttribute("ValorUnitario");
					} else {
						price = eElementC.getAttribute("valorUnitario");
					}

					if (!eElementC.getAttribute("Importe").isEmpty()) {
						amountI = eElementC.getAttribute("Importe");
					} else {
						amountI = eElementC.getAttribute("importe");
					}
					//Descuento
					if (!eElementC.getAttribute("Descuento").isEmpty()) {
						discountItem = eElementC.getAttribute("Descuento");
					} else if (!eElementC.getAttribute("descuento").isEmpty()) {
						discountItem = eElementC.getAttribute("descuento");
					} else {
						discountItem = "0.0";
					}
					//leer impuestos primer tag Impuestos
					NodeList impuestoList =	eElementC.getElementsByTagName("cfdi:Impuestos");					
					for (int contImpuesto = 0; contImpuesto < impuestoList.getLength(); contImpuesto++) {
						Node nNodeImpuesto = impuestoList.item(contImpuesto);
						if (nNodeImpuesto.getNodeType() == Node.ELEMENT_NODE) {
							Element eElementImpuesto = (Element) nNodeImpuesto;		
							//leer impuestos segundo tag Traslados
							NodeList trasladosList =	eElementImpuesto.getElementsByTagName("cfdi:Traslados");							
							for (int contTraslados= 0; contTraslados < trasladosList.getLength(); contTraslados++) {
								Node nNodeTraslados = trasladosList.item(contTraslados);
								if (nNodeTraslados.getNodeType() == Node.ELEMENT_NODE) {
									Element eElementTraslados = (Element) nNodeTraslados;
									//leer impuestos tercer tag Traslado
									NodeList trasladoList =	eElementTraslados.getElementsByTagName("cfdi:Traslado");
									for (int contTraslado= 0; contTraslado < trasladoList.getLength(); contTraslado++) {
										Node nNodeTraslado = trasladoList.item(contTraslado);
										if (nNodeTraslado.getNodeType() == Node.ELEMENT_NODE) {
											Element eElementTraslado= (Element)nNodeTraslado;
											//leer el valor de el impuesto trasladado
											if (!eElementTraslado.getAttribute("Importe").isEmpty()) {
												taxItem = Double.parseDouble(eElementTraslado.getAttribute("Importe"));
											}else {
												taxItem = Double.parseDouble(eElementTraslado.getAttribute("importe"));
											}
										}
									}
								}
							}
							//leer impuestos segundo tag retencion
							NodeList retentionsList =	eElementImpuesto.getElementsByTagName("cfdi:Retenciones");
							for (int contRetentions= 0; contRetentions < retentionsList.getLength(); contRetentions++) {
								Node nNodeRetentions = retentionsList.item(contRetentions);
								if (nNodeRetentions.getNodeType() == Node.ELEMENT_NODE) {
									Element eElementRetentions = (Element) nNodeRetentions;
									//leer impuestos tercer tag retencion
									NodeList retentionList =	eElementRetentions.getElementsByTagName("cfdi:Retencion");
									for (int contRetention = 0; contRetention < retentionList.getLength(); contRetention++) {
										Node nNodeRetention = retentionList.item(contRetention);
										if (nNodeRetention.getNodeType() == Node.ELEMENT_NODE) {
											Element eElementRetention = (Element)nNodeRetention;
											//leer el valor de el impuesto retenido
											if (!eElementRetention.getAttribute("Importe").isEmpty()) {
												retentionSum = retentionSum + Double.parseDouble(eElementRetention.getAttribute("Importe"));
											}else {
												retentionSum = retentionSum + Double.parseDouble(eElementRetention.getAttribute("importe"));
											}
										}										
									}
								}
							}
						}
					}
					bmoPaccountItemNew.getName().setValue(name);
					bmoPaccountItemNew.getQuantity().setValue(quantity);
					bmoPaccountItemNew.getPrice().setValue(Double.parseDouble(price));
					bmoPaccountItemNew.getAmount().setValue(Double.parseDouble(amountI));
					bmoPaccountItemNew.getPaccountId().setValue(paccountId);
					bmoPaccountItemNew.getDiscount().setValue(Double.parseDouble(discountItem));
					bmoPaccountItemNew.getTax().setValue(taxItem);
					bmoPaccountItemNew.getSumRetention().setValue(retentionSum);

					PmPaccountItem pmPaccountItem = new PmPaccountItem(getSFParams());
					pmPaccountItem.saveXML(pmConn2, bmoPaccountItemNew, bmUpdateResult);
				}

			}
			if (!bmUpdateResult.hasErrors())
				pmConn2.commit();

		} catch (ParserConfigurationException e) {
			System.out.println("Error Parse: " + e.toString());
		} catch (SAXException | IOException e) {
			System.out.println("Error SAX: " + e.toString());
		} catch (SFException e) {
			pmConn2.rollback();
			bmUpdateResult.addMsg("Error al crear la cuenta por pagar" + e.toString());
		} finally {
			pmConn.close();
			pmConn2.close();
		}
		return bmUpdateResult;
	}

	//Obtener el Saldo de la Orden de Compra
	private Double getRequisitionReceitpBalance(PmConn pmConn, int requisitionReceiptId, BmUpdateResult bmUpdateResult) throws SFPmException {

		String sql="";		
		double withDraw = 0, deposit = 0, balance = 0;

		sql = " SELECT rerc_total FROM requisitionreceipts "
				+ " WHERE rerc_requisitionreceiptid = " + requisitionReceiptId;

		pmConn.doFetch(sql);
		if (pmConn.next()){ 
			withDraw = pmConn.getDouble("rerc_total");			
		} 

		//Obtener el total de Abonos ligados a la orden de compra
		sql = " SELECT SUM(pacc_total) AS deposit FROM paccounts "
				+ " LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) "				
				+ " WHERE pacc_requisitionreceiptid = " + requisitionReceiptId				
				+ " AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'";			
		pmConn.doFetch(sql);		
		if (pmConn.next()){ 
			deposit = pmConn.getDouble("deposit");
		}

		balance = FlexUtil.roundDouble(withDraw - deposit, 2);


		return balance;
	}

	// Crea la cuenta por pagar a partir de un recibo de orden de compra
	public void createFromRequisitionReceipt(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPaccount = new BmoPaccount();

		try {
			// Si ya existe, no lo vuelve a crear
			bmoPaccount = (BmoPaccount)this.getBy(pmConn, bmoRequisitionReceipt.getId(), bmoPaccount.getRequisitionReceiptId().getName());
		} catch (SFException e) {
			// Obtener OC
			PmRequisition pmRequisition = new PmRequisition(getSFParams());
			BmoRequisition bmoRequisition = new BmoRequisition();
			bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoRequisitionReceipt.getRequisitionId().toInteger());

			// No existe, lo crea
			bmoPaccount.getInvoiceno().setValue(bmoRequisitionReceipt.getName().toString());
			bmoPaccount.getDescription().setValue(bmoRequisitionReceipt.getName().toString());

			// Obtiene la cuenta default para ordenes de compra
			PmPaccountType pmPaccountType = new PmPaccountType(getSFParams());
			bmoPaccount.getPaccountTypeId().setValue(pmPaccountType.getRequisitionPaccountType(pmConn));

			bmoPaccount.getReceiveDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoPaccount.getDueDate().setValue(bmoRequisition.getPaymentDate().toString());
			bmoPaccount.getStatus().setValue(BmoPaccount.STATUS_REVISION);

			// Obtiene la empresa a asignar
			bmoPaccount.getCompanyId().setValue(bmoRequisitionReceipt.getBmoRequisition().getCompanyId().toInteger());
			bmoPaccount.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());
			//Actualizar el presupuesto
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (!(bmoRequisitionReceipt.getBudgetItemId().toInteger() > 0)) 
					bmUpdateResult.addError(bmoRequisitionReceipt.getBudgetItemId().getName(), "El Recibo de O.C. no tiene asignada una Partida Presupuestal");

				bmoPaccount.getBudgetItemId().setValue(bmoRequisitionReceipt.getBudgetItemId().toInteger());
				bmoPaccount.getAreaId().setValue(bmoRequisitionReceipt.getBmoRequisition().getAreaId().toInteger());
			}

			bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);
			bmoPaccount.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
			bmoPaccount.getSupplierId().setValue(bmoRequisitionReceipt.getSupplierId().toInteger());
			bmoPaccount.getCurrencyId().setValue(bmoRequisitionReceipt.getCurrencyId().toInteger());
			bmoPaccount.getCurrencyParity().setValue(bmoRequisitionReceipt.getCurrencyParity().toDouble());

			//Guardar el nombre del tipo de la orden de compra
			bmoPaccount.getRequisitionType().setValue(bmoRequisitionReceipt.getBmoRequisition().getBmoRequisitionType().getType().toString());
			this.save(pmConn, bmoPaccount, bmUpdateResult);
		}
	}

	// Crea la cuenta por pagar a partir de un recibo de orden de compra
	public void createCreditNoteFromRequisitionReceiptReturn(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPaccount = new BmoPaccount();

		// No existe, lo crea
		bmoPaccount.getInvoiceno().setValue(bmoRequisitionReceipt.getName().toString());
		bmoPaccount.getDescription().setValue(bmoRequisitionReceipt.getName().toString());

		// Obtiene la cuenta default para ordenes de compra
		PmPaccountType pmPaccountType = new PmPaccountType(getSFParams());
		bmoPaccount.getPaccountTypeId().setValue(pmPaccountType.getRequisitionReturnPaccountType(pmConn));

		bmoPaccount.getReceiveDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		bmoPaccount.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		bmoPaccount.getStatus().setValue(BmoPaccount.STATUS_REVISION);

		// Obtiene la empresa a asignar
		bmoPaccount.getCompanyId().setValue(bmoRequisitionReceipt.getBmoRequisition().getCompanyId().toInteger());
		bmoPaccount.getRequisitionReceiptId().setValue(bmoRequisitionReceipt.getId());

		bmoPaccount.getPaymentStatus().setValue(BmoPaccount.PAYMENTSTATUS_PENDING);
		bmoPaccount.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
		bmoPaccount.getSupplierId().setValue(bmoRequisitionReceipt.getSupplierId().toInteger());

		this.save(pmConn, bmoPaccount, bmUpdateResult);			
	}

	// Revisa que la O.C. tenga CxP autorizadas
	public boolean requisitionHasAuthorizedPaccounts(PmConn pmConn, int requisitionId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int c = 0;

		sql = " SELECT COUNT(pacc_paccountid) AS c FROM paccounts " +
				" LEFT JOIN requisitionreceipts ON (pacc_requisitionreceiptid = rerc_requisitionreceiptid) " +
				" LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +
				" WHERE rerc_requisitionid = " + requisitionId + 
				" AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
				" AND pacc_status = '" + BmoPaccount.STATUS_AUTHORIZED + "'";
		pmConn.doFetch(sql);

		if (pmConn.next())
			c = pmConn.getInt("c");

		if (c > 0) 
			return true;
		else
			return false;
	}

	public boolean existPaccountItems(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		boolean result = false;
		double sumItems = 0;
		sql = " SELECT SUM(pait_amount) AS sumItems FROM paccountitems " +
				" WHERE pait_paccountid = " + bmoPaccount.getId();
		pmConn.doFetch(sql);

		if (pmConn.next()) sumItems = pmConn.getDouble("sumItems"); 

		if (sumItems > 0) result = true;

		return result;
	}

	// Pagos Realizados en forma de anticipo
	public double getDownPayment(BmoWorkContract bmoWorkContract) throws SFException{
		String sql ="";
		PmConn pmConn = new PmConn(getSFParams());
		try {			
			pmConn .open();
			double result = 0;
			sql= " SELECT SUM(pacc_amount) AS downPayment FROM paccounts " +
					" LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +		
					" LEFT JOIN requisitions ON (pacc_requisitionid = reqi_requisitionid)" +
					" LEFT JOIN contractestimations ON (reqi_contractestimationid = coes_contractestimationid) " + 
					" WHERE coes_workcontractid = " + bmoWorkContract.getId() +
					" and pact_type = '" + BmoPaccountType.TYPE_DEPOSIT + "'";

			pmConn.doFetch(sql);
			if(pmConn.next()) result = pmConn.getDouble("downPayment");



			//Restar los pagos al total del contracto
			result = bmoWorkContract.getTotal().toDouble() - result;

			return result;
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "-getDownPayment() " + e.toString());
		} finally {
			pmConn.close();			
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoPaccount = (BmoPaccount) bmObject;

		PmPaccountType pmPaccountType = new PmPaccountType(getSFParams());
		BmoPaccountType bmoPaccountType = (BmoPaccountType)pmPaccountType.get(pmConn, bmoPaccount.getPaccountTypeId().toInteger());

		// Validaciones 
		if (bmoPaccount.getStatus().toChar() == BmoPaccount.STATUS_AUTHORIZED) {
			bmUpdateResult.addMsg("La CxP no se puede Eliminar: está Autorizada.");
		} else {
			// Valida que no exista movimientos de banco ligados
			/*if (existBankMovConcept(pmConn, bmoPaccount.getId(), bmUpdateResult))
				bmUpdateResult.addMsg("No se puede Eliminar La CxP: esta ligada a un Movimiento de Bancos.");*/

			if (bmoPaccountType.getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE)) {
				//Eliminar aplicaciones
				deleteAssignment(pmConn, bmoPaccount, bmUpdateResult);

			} else {
				deleteAssignment(pmConn, bmoPaccount.getId(), bmUpdateResult);
			}

			// Eliminar los items
			deleteItems(pmConn, bmoPaccount.getId(), bmUpdateResult);

			// Elimina la CxP
			super.delete(pmConn, bmoPaccount, bmUpdateResult);

			// Cambiar el estatus del recibo OC			
			if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0) {
				PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
				BmoRequisitionReceipt bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmoPaccount.getRequisitionReceiptId().toInteger());
				pmRequisitionReceipt.updatePaymentStatus(pmConn, bmoRequisitionReceipt, bmUpdateResult);
			}
		}

		return bmUpdateResult;
	}

	//Tiene asignaciones
	public boolean hasAssignment(PmConn pmConn, int paccountId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		int items = 0;
		sql = " SELECT COUNT(*) AS items FROM paccountassignments " +
				" WHERE rass_raccountid = " + paccountId;		
		pmConn.doFetch(sql);
		if (pmConn.next()) items =  pmConn.getInt("items");

		if (items > 0) return true;
		else return false;

	}

	// Delete assignments
	public void deleteAssignment(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		BmoPaccountAssignment bmoPaccountAssignment = new BmoPaccountAssignment();
		PmPaccountAssignment pmPaccountAssignment = new PmPaccountAssignment(getSFParams());
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoPaccountAssignment.getKind(), bmoPaccountAssignment.getPaccountId().getName(), bmoPaccount.getId());
		Iterator<BmObject> listPaccAssig = pmPaccountAssignment.list(pmConn, bmFilter).iterator();
		while (listPaccAssig.hasNext()) {			
			bmoPaccountAssignment = (BmoPaccountAssignment)listPaccAssig.next();
			pmPaccountAssignment.delete(pmConn, bmoPaccountAssignment, bmUpdateResult);
		}
	}

	// Delete assignments
	public void deleteAssignment(PmConn pmConn, int paccountId, BmUpdateResult bmUpdateResult) throws SFException {		
		pmConn.doUpdate("DELETE FROM paccountassignments WHERE pass_paccountid = " + paccountId);			
	}

	// Delete items
	public void deleteItems(PmConn pmConn, int paccountId, BmUpdateResult bmUpdateResult) throws SFException {		
		pmConn.doUpdate("DELETE FROM paccountitems WHERE pait_paccountid = " + paccountId);			
	}

	// Elimina las cuentas x pagar de un recibo de orden de compra
	public void deleteByRequisitionReceipt(PmConn pmConn, BmoRequisitionReceipt bmoRequisitionReceipt, BmUpdateResult bmUpdateResult) throws SFException {
		// Filtrar cuentas por pagar por orden de compra
		BmFilter filterByRequisitionReceipt = new BmFilter();
		filterByRequisitionReceipt.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionReceiptId().getName(), bmoRequisitionReceipt.getId());
		Iterator<BmObject> listPaccounts = this.list(pmConn, filterByRequisitionReceipt).iterator();
		while (listPaccounts.hasNext()) {
			BmoPaccount bmoPaccount = (BmoPaccount)listPaccounts.next();
			this.delete(pmConn, bmoPaccount, bmUpdateResult);
		}
	}
	//Valida si tiene documentos cargados
	private boolean getFiles(int programId, int foreignid) throws SFPmException {
		boolean result;
		String sql = "";
		PmConn pmConn = new PmConn(getSFParams());			
		sql = "SELECT * FROM sffiles where file_programid = " + programId + " AND file_foreignid = " + foreignid;

		pmConn.open();
		pmConn.doFetch(sql);

		if (pmConn.next()) {
			result = true;
			pmConn.close();
		}else {
			result = false;
			pmConn.close();
		}
		return result;
	}

}


