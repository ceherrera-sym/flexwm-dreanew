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
import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.StringTokenizer;
import com.flexwm.server.co.PmContractEstimation;
import com.flexwm.server.co.PmEstimationItem;
import com.flexwm.server.co.PmProperty;
import com.flexwm.server.fi.PmBankMovConcept;
import com.flexwm.server.fi.PmBankMovement;
import com.flexwm.server.fi.PmBudgetItem;
import com.flexwm.server.fi.PmCurrency;
import com.flexwm.server.fi.PmPaccount;
import com.flexwm.server.wf.PmWFlow;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.server.wf.PmWFlowType;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.co.BmoContractEstimation;
import com.flexwm.shared.co.BmoEstimationItem;
import com.flexwm.shared.co.BmoProperty;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoReqPayType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionItem;
import com.flexwm.shared.op.BmoRequisitionReceipt;
import com.flexwm.shared.op.BmoRequisitionType;
import com.flexwm.shared.op.BmoSupplier;
import com.flexwm.shared.op.BmoWarehouse;
import com.flexwm.shared.wf.BmoWFlow;
import com.flexwm.shared.wf.BmoWFlowLog;
import com.symgae.server.HtmlUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFSendMail;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmArea;
import com.symgae.server.sf.PmCompany;
import com.symgae.server.sf.PmFile;
import com.symgae.server.sf.PmProgram;
import com.symgae.server.sf.PmUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.SFException;
import com.symgae.shared.SFMailAddress;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoFile;
import com.symgae.shared.sf.BmoProgram;
import com.symgae.shared.sf.BmoUser;


public class PmRequisition extends PmObject {
	BmoRequisition bmoRequisition;

	public PmRequisition(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRequisition = new BmoRequisition();
		setBmObject(bmoRequisition);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRequisition.getCompanyId(), bmoRequisition.getBmoCompany()),
				new PmJoin(bmoRequisition.getSupplierId(), bmoRequisition.getBmoSupplier()),
				new PmJoin(bmoRequisition.getBmoSupplier().getSupplierCategoryId(), bmoRequisition.getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoRequisition.getReqPayTypeId(), bmoRequisition.getBmoReqPayType()),
				new PmJoin(bmoRequisition.getCurrencyId(), bmoRequisition.getBmoCurrency()),
				new PmJoin(bmoRequisition.getRequisitionTypeId(), bmoRequisition.getBmoRequisitionType())				
				)));
	}

	@Override
	public void validate(BmObject bmObject, BmUpdateResult bmUpdateResult) {

		if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_COMMISION)) {
			if (!(bmoRequisition.getOrderId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoRequisition.getOrderId().getName(), "Seleccionar un Pedido.");
			}		

			//Todas la OC deben de tener un proveedor
			if (!(bmoRequisition.getSupplierId().toInteger() > 0)) {
				bmUpdateResult.addError(bmoRequisition.getSupplierId().getName(), "Seleccionar un proveedor");
			}
		}
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		int loggedUserId = getSFParams().getLoginInfo().getUserId();
		int loggedUserAreaId = getSFParams().getLoginInfo().getBmoUser().getAreaId().toInteger();

		// Filtro muestra las OC que solicito el usuario y sus subordinados
		if (getSFParams().restrictData(bmoRequisition.getProgramCode())) {

			// Solicitado Por
			filters = " ( reqi_requestedby IN " +
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

					// Usuario que lo creo
					" OR ( reqi_usercreateid IN " +
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
					" ) " +
					" ) " +

					// Por departamento
					" OR  ( " +
					" reqi_areaid = " + loggedUserAreaId + 
					" ) " +

					" ) " +
					" ) ";
		}

		// Filtro de OC de empresas del usuario
		if (getSFParams().restrictData(new BmoCompany().getProgramCode())) {
			if (filters.length() > 0) filters += " AND ";
			filters += "( reqi_companyid IN (" +
					" SELECT uscp_companyid FROM usercompanies " +
					" WHERE " + 
					" uscp_userid = " + loggedUserId + " )"
					+ ") ";			
		}

		// Filtro de empresa seleccionada
		if (getSFParams().getSelectedCompanyId() > 0) {
			if (filters.length() > 0) filters += " AND ";
			filters += " reqi_companyid = " + getSFParams().getSelectedCompanyId();
		}

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoRequisition bmoRequisition = (BmoRequisition) autoPopulate(pmConn, new BmoRequisition());		

		// BmoCompany
		BmoCompany bmoCompany = new BmoCompany();
		int companyId = pmConn.getInt(bmoCompany.getIdFieldName());
		if (companyId > 0) bmoRequisition.setBmoCompany((BmoCompany) new PmCompany(getSFParams()).populate(pmConn));
		else bmoRequisition.setBmoCompany(bmoCompany);

		// BmoSupplier
		BmoSupplier bmoSupplier = new BmoSupplier();
		int supplierId = pmConn.getInt(bmoSupplier.getIdFieldName());
		if (supplierId > 0) bmoRequisition.setBmoSupplier((BmoSupplier) new PmSupplier(getSFParams()).populate(pmConn));
		else bmoRequisition.setBmoSupplier(bmoSupplier);

		// BmoRequisitionType
		BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
		int requisitionTypeId = pmConn.getInt(bmoRequisitionType.getIdFieldName());
		if (requisitionTypeId > 0) bmoRequisition.setBmoRequisitionType((BmoRequisitionType) new PmRequisitionType(getSFParams()).populate(pmConn));
		else bmoRequisition.setBmoRequisitionType(bmoRequisitionType);

		// BmoReqPayType
		BmoReqPayType bmoReqPayType = new BmoReqPayType();
		int reqPayTypeId = pmConn.getInt(bmoReqPayType.getIdFieldName());
		if (reqPayTypeId > 0) bmoRequisition.setBmoReqPayType((BmoReqPayType) new PmReqPayType(getSFParams()).populate(pmConn));
		else bmoRequisition.setBmoReqPayType(bmoReqPayType);

		//BmoCurrency
		BmoCurrency bmoCurrency = new BmoCurrency();
		int currencyId = pmConn.getInt(bmoCurrency.getIdFieldName());
		if (currencyId > 0) bmoRequisition.setBmoCurrency((BmoCurrency) new PmCurrency(getSFParams()).populate(pmConn));
		else bmoRequisition.setBmoCurrency(bmoCurrency);

		return bmoRequisition;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoRequisition = (BmoRequisition)bmObject;
		boolean newRecord = false;
		int programId = getSFParams().getProgramId(bmoRequisition.getProgramCode());
		BmoRequisition bmoRequisitionPrev = new BmoRequisition();

		PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
		BmoRequisitionType bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.get(pmConn, bmoRequisition.getRequisitionTypeId().toInteger());

		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		
		if (bmoRequisition.getBmoRequisitionType().getOutFormat().toBoolean()) {
			if (bmoRequisition.getShowOnOutFormat().toBoolean()) {
				if (bmoRequisition.getDeliveryTime().toString().equals("")) {
					bmUpdateResult.addError(bmoRequisition.getDeliveryTime().getName(), "Complete este campo");
				}
				if (bmoRequisition.getResponsibleId().toInteger() <= 0) {
					bmUpdateResult.addError(bmoRequisition.getResponsibleId().getName(), "Complete este campo");
				}
			}
		}

		// Se almacena de forma preliminar para asignar ID y la Clave
		if (!(bmoRequisition.getId() > 0)) {

			// Si la OC es de tipo compra asignar la empresa del almancen
			if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_PURCHASE)) {
				PmWarehouse pmWarehouse = new PmWarehouse(getSFParams());
				BmoWarehouse bmoWarehouse =new BmoWarehouse();
				if (bmoRequisition.getWarehouseId().toInteger() > 0)
					bmoWarehouse = (BmoWarehouse)pmWarehouse.get(pmConn, bmoRequisition.getWarehouseId().toInteger());
				else 
					bmUpdateResult.addError(bmoRequisition.getWarehouseId().getName(), "Debe seleccionar un Almacén.");

				bmoRequisition.getCompanyId().setValue(bmoWarehouse.getCompanyId().toInteger());				
			} else {
				if (!(bmoRequisition.getCompanyId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoRequisition.getCompanyId().getName(), "Debe seleccionar una Empresa.");
				}

				if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_CREDIT)) {
					if (!(bmoRequisition.getLoanId().toInteger() > 0)) {
						bmUpdateResult.addError(bmoRequisition.getLoanId().getName(), "Debe seleccionar un Crédito.");
					}
				}
			}

			if(bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_PURCHASE)) {
				if(!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CREATEPURCHASE)) 
					bmUpdateResult.addError(bmoRequisition.getRequisitionTypeId().getName(), "No tiene permiso para crear O.C. de tipo de Compra.");
			}	
			
			if(bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)) {
				if(!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CREATEVIATICS)) 
					bmUpdateResult.addError(bmoRequisition.getRequisitionTypeId().getName(), "No tiene permiso para crear O.C. de tipo de Viáticos.");					
			}
						
						
			newRecord = true;
			super.save(pmConn, bmoRequisition, bmUpdateResult);
			bmoRequisition.setId(bmUpdateResult.getId());
			bmoRequisition.getCode().setValue(bmoRequisition.getCodeFormat());
		} else {
			// Obtener la OC con valores pasados
			PmRequisition pmRequisitionPrev = new PmRequisition(getSFParams());
			bmoRequisitionPrev = (BmoRequisition)pmRequisitionPrev.get(bmoRequisition.getId());
		}

		//Agregar la paridad de la moneda
		if (bmoRequisition.getCurrencyId().toInteger() > 0) {
			if (bmoRequisition.getCurrencyParity().equals("")) {
				PmCurrency pmCurrency = new PmCurrency(getSFParams());
				BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(pmConn, bmoRequisition.getCurrencyId().toInteger());
				bmoRequisition.getCurrencyParity().setValue(bmoCurrency.getParity().toString());
			}
		}

		// Si no es nuevo registro revisa cambios de estatus
		if (!newRecord) {

			// Se esta cambiando a En Edicion o En Revision
			if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)
					|| bmoRequisition.getStatus().equals(BmoRequisition.STATUS_REVISION)) {

				if (bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)) {
					// Si no es de compra o viaticos, elimina los ROC creados automaticamente si no estan pagados
					if (!bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_PURCHASE) &&
							!bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)) {
						//Validar que las los ROC no tengan pagos				
						if (hasPayment(pmConn, bmoRequisitionPrev, bmUpdateResult)) {
							bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "La Orden de Compra tiene CxP Autorizadas o pagos Realizados.");
						} else {
							//Eliminar las cuentas por pagar
							removeROC(pmConn, bmoRequisition, bmUpdateResult);
						}
					} else {
						if (bmoRequisition.getDeliveryStatus().toChar() != BmoRequisition.DELIVERYSTATUS_REVISION) {
							bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "La Orden de Compra ya esta Recibida.");
						} else if (this.existROC(pmConn, bmoRequisition, bmUpdateResult)) {
							//Revisar que exista un recibo
							bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "La Orden de Compra Tiene un Recibo.");
						}
					}
				}	

				// Limpiar fecha y usuario de autorizacion
				bmoRequisition.getAuthorizedDate().setValue("");
				bmoRequisition.getAuthorizedUser().setValue("");

				// Genear bitacora en la OC
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)
						&& !bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_EDITION))
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoRequisition.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Orden de Compra se guardó En Edición.");
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_REVISION)
						&& !bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_REVISION))
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoRequisition.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Orden de Compra se guardó En Revisión.");
			} 

			// Revisar cancelacion	
			else if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_CANCELLED)) {
				//Si si esta recibida no se puede cancelar
				if (bmoRequisition.getDeliveryStatus().toChar() != BmoRequisition.DELIVERYSTATUS_REVISION &&
						!bmoRequisition.getBmoRequisitionType().getCreateReceipt().toBoolean()) {
					bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "La Orden de Compra ya esta Recibida no se puede modificar.");
				}

				//Validar si esta recibida
				if (bmoRequisition.getDeliveryStatus().toChar() != BmoRequisition.DELIVERYSTATUS_REVISION) {
					bmUpdateResult.addError(bmoRequisition.getDeliveryStatus().getName(), "La Orden de Compra ya esta Recibida no se puede cancelar.");
				} else if (this.existROC(pmConn, bmoRequisition, bmUpdateResult)) {
					//Revisar que exista un recibo
					bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "La Orden de Compra Tiene un Recibo.");
				}

				//Validar que no existe un pago
				if (bmoRequisition.getPaymentStatus().toChar() != BmoRequisition.PAYMENTSTATUS_PENDING) {
					bmUpdateResult.addError(bmoRequisition.getDeliveryStatus().getName(), "La Orden de Compra tiene pagos no se puede cancelar.");
				} else if (this.existPacc(pmConn, bmoRequisition, bmUpdateResult)) {
					bmUpdateResult.addError(bmoRequisition.getDeliveryStatus().getName(), "La Orden de Compra tiene una CxP ligada, no se puede cancelar.");
				}

				// Limpiar fecha y usuario de autorizacion
				bmoRequisition.getAuthorizedDate().setValue("");
				bmoRequisition.getAuthorizedUser().setValue("");

				// Genear bitacora en la OC
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_CANCELLED)
						&& !bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_CANCELLED))
					pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoRequisition.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, "La Orden de Compra se guardó como Cancelada.");
			} 

			// Se esta autorizando
			else if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)) {
				//				BmoRequisition bmoPrevRequisition = new BmoRequisition();
				//				bmoPrevRequisition = (BmoRequisition)this.get(bmoRequisition.getId());

				// Si no estaba autorizada ya, revisa que tenga permisos de autorizar
				if (!bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)) {
					if (!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTHORIZE)) {
						bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No cuenta con Permisos para Autorizar Órdenes de Compra.");	
					}

					// Actualiza fecha y usuario de autorizacion
					bmoRequisition.getAuthorizedDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
					bmoRequisition.getAuthorizedUser().setValue(getSFParams().getLoginInfo().getUserId());
				}				
			}
		if((!(bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)))&& bmoRequisition.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)) {
			//validar que no sea mayor al minimo a pagar en la OC-
			if(!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_CHANGESTATUS)) {
				bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No tiene permiso para cambiar el estatus de la O.C");					

			}else {
				if(!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTHORIZE)) {
					bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No tiene permiso para autorizar la O.C");					
				}else {
					if(((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequestAuthReqi().toBoolean()) {
						if( getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTMAX)){

						}else {
							if( getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTMIN) && ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequestAuthReqiAmount().toDouble() >= (bmoRequisition.getTotal().toDouble()))
								{
									}else {
										if( !getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTMAX) && (!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTMIN))){	
											bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No tiene permisos para autorizar la O.C ");		
												}else {
													if(!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTMIN)) {
														bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No tiene permiso para autorizar la O.C");
													}else {
														if(getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_AUTMIN) && (bmoRequisition.getTotal().toDouble()) >((BmoFlexConfig)getSFParams().getBmoAppConfig()).getRequestAuthReqiAmount().toDouble()){
															bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No tiene permiso para autorizar la O.C con este monto ");

														}
													}
													
												}
									}												
						}
					}					
					}	
				}
		}
			//Validar que todos los doc. que esten en la CXP existan en la oc si no es así que elimine aquellos doc. que no coicidan
			copyFilestoCxP(pmConn, bmoRequisition, bmUpdateResult);
	
		}

		printDevLog("Llego despues de la revision de estatus");

		// Si no hay errores hasta este momento continua
		if (!bmUpdateResult.hasErrors()) {

			// Validar que tenga asignado presupuesto si son requeridos
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (!(bmoRequisition.getBudgetItemId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoRequisition.getBudgetItemId().getName(), "Debe seleccionar la Partida Presupuestal.");
				} else {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = new BmoBudgetItem();

					if (!newRecord) {
						// Obtener el Item de Presupuesto anterior, y calcular
						//						BmoRequisition bmoPrevRequisition = new BmoRequisition();
						//						bmoPrevRequisition = (BmoRequisition)this.get(bmoRequisition.getId());

						bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoRequisitionPrev.getBudgetItemId().toInteger());
						pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);

						// Si es diferente al actual, actualizar el nuevo
						if (bmoRequisitionPrev.getBudgetItemId().toInteger() != bmoRequisition.getBudgetItemId().toInteger()) {

							//Si el OC TIENE estimación						
							if (!bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)) {
								// Calcular los saldos del presupuesto
								bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoRequisition.getBudgetItemId().toInteger());

								//Actualizar las partidad en los ROC, CxP y Conceptos de MB
								updateBudgetItem(pmConn, bmoRequisition, bmUpdateResult);

								pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
							} else {
								bmUpdateResult.addError(bmoRequisition.getBudgetItemId().getName(), "Se debe cambiar la Partida desde el Contrato de Obra.");
							}
						}
					} else {
						bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoRequisition.getBudgetItemId().toInteger());					
						pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
					}
				}
			}

			// Calcular montos
			updateBalance(pmConn, bmoRequisition, bmUpdateResult);
			printDevLog("Antes de guardar");

			// Actualiza la OC
			super.save(pmConn, bmoRequisition, bmUpdateResult);

			// Movimientos con Pedidos
			PmOrder pmOrder = new PmOrder(getSFParams());
			BmoOrder bmoOrder = new BmoOrder();

			// Si proviene de un pedido, Obtener pedido
			if (bmoRequisition.getOrderId().toInteger() > 0) {
				bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoRequisition.getOrderId().toInteger());
			}

			// Si proviene de un pedido, validar estatus
			if (bmoOrder.getId() > 0) {

				if (!bmoOrder.getBmoOrderType().getEnableReqiOrderFinish().toBoolean()) {
					if (bmoOrder.getStatus().equals(BmoOrder.STATUS_FINISHED)) {
						bmUpdateResult.addMsg("No se puede Modificar la Órden de Compra: el Pedido Ligado ya está Finalizado.");
					}
				}

				// Refresca el estatus del pedido, en cuanto a bloqueos, guardando el pedido
				if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_RENTAL)) {
					pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);
				}

				// Si el pedido esta autorizado no se puede modificar
				if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED)) {
					if (bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_RENTAL)) {
						// Revisar si la OC de compra estaba antes como Autorizada y se esta intentando cambiar a En Revision, en caso afirmativo enviar error
						if (!newRecord && bmoRequisition.getStatus().equals(BmoRequisition.STATUS_REVISION)) {
							// Se hace una peticion fuera de la transaccion PmConn, por eso toma el valor previo a esta modificacion, de la base de datos
							//BmoRequisition bmoRequisitionPrev = (BmoRequisition)pmRequisitionPrev.get(bmoRequisition.getId());
							if (bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)
									|| bmoRequisitionPrev.getStatus().equals(BmoRequisition.STATUS_EDITION))	
								bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No se puede cambiar Estatus: El Pedido está Autorizado.");
						}
					}
				} else {
					// Revisar si la OC de compra estaba antes como Autorizada y se esta intentando cambiar a En Edicion o En Revision, en caso afirmativo recalcular items del pedido
					if (!newRecord && (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)
							|| bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION))) {
						if (bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_COMMISION)) {
							if (this.existROC(pmConn, bmoRequisition, bmUpdateResult)) {
								bmUpdateResult.addError(bmoRequisition.getStatus().getName(), "No se puede cambiar Estatus: Existe un Recibo ligado.");
							}
						}

						// Si si esta recibida no se puede cancelar
						if (bmoRequisition.getDeliveryStatus().toChar() != BmoRequisition.DELIVERYSTATUS_REVISION &&
								!bmoRequisition.getBmoRequisitionType().getCreateReceipt().toBoolean()) {
							bmUpdateResult.addError(bmoRequisition.getDeliveryStatus().getName(), "La Orden de Compra ya esta Recibida no se puede modificar");
						}
					}				
				}
			} else if (bmoRequisition.getContractEstimationId().toInteger() > 0) {
				// Crear los conceptos de la estimacion
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_EDITION)) {				
					//Validar que los item ya esten creados
					if (!existItems(pmConn, bmoRequisition, bmUpdateResult)) 
						createItemsFromContractEstimation(pmConn, bmoRequisition, bmUpdateResult);
				}	
			} 

			// Guarda bitacora si esta ligada a un pedido
			if (!newRecord && bmoRequisition.getStatus().toChar() == BmoRequisition.STATUS_AUTHORIZED
					&& bmoRequisitionPrev.getStatus().toChar() != BmoRequisition.STATUS_AUTHORIZED) {
				addDataLog(pmConn, bmoRequisition, bmoOrder, "Orden de Compra Autorizada.", bmUpdateResult);
			}

			// Actualiza la OC
			super.save(pmConn, bmoRequisition, bmUpdateResult);

			// Crear los recibos de Oc en automatico
			if (bmoRequisition.getBmoRequisitionType().getCreateReceipt().toBoolean()) {
				if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)) {
					//Validar que no existe el ROC
					if (!this.existROC(pmConn, bmoRequisition, bmUpdateResult)) {
						//Crear el ROC en automatico
						this.createRequisitionReceipt(pmConn, bmoRequisition, bmUpdateResult);
					}				

					if (bmoRequisition.getContractEstimationId().toInteger() > 0) {
						bmoRequisition.getTaxApplies().setValue(0);					
					}
				}
			}

			// Recibir la OC si se crea el recibo en automatico
			if (bmoRequisition.getBmoRequisitionType().getCreateReceipt().toBoolean() 
					&& bmoRequisition.getStatus().equals(BmoRequisition.STATUS_AUTHORIZED)) {
				bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_TOTAL);
			}

			// Actualiza el estatus de recepcion de la orden de compra
			updateDeliveryStatus(pmConn, bmoRequisition, bmUpdateResult);

			// Calcular montos
			updateBalance(pmConn, bmoRequisition, bmUpdateResult);

			// Calcular pagos
			updatePayments(pmConn, bmoRequisition, bmUpdateResult);

			printDevLog("Justo antes de enviar correos");

			// Si se esta cambian a en revision, enviar correo 
			if (!newRecord && bmoRequisition.getStatus().toChar() == BmoRequisition.STATUS_REVISION) {
				BmoRequisition bmoReqiPrev = (BmoRequisition)this.get(bmoRequisition.getId());
				if (bmoReqiPrev.getStatus().equals(BmoRequisition.STATUS_EDITION))
					if (bmoRequisitionType.getEnableSend().toInteger() > 0) {
						sendMailRequestAuthorize(pmConn, bmoRequisition);
					}
			} // Si se esta cambian a en revision, enviar correo 
			else if (!newRecord && bmoRequisition.getStatus().toChar() == BmoRequisition.STATUS_AUTHORIZED) {
				BmoRequisition bmoReqiPrev = (BmoRequisition)this.get(bmoRequisition.getId());
				if (bmoReqiPrev.getStatus().equals(BmoRequisition.STATUS_REVISION))
					if (bmoRequisitionType.getEnableSend().toInteger() > 0) {
						sendMailAuthorized(pmConn, bmoRequisition);
					}
			}
			if (getFiles(programId, bmoRequisition.getId())) {
				bmoRequisition.getFile().setValue("1");
			}else {
				bmoRequisition.getFile().setValue("0");
			}

			printDevLog("Ya se enviaron correos");

			// Si no esta asignado el tipo de wflow, toma el primero
			if (!(bmoRequisition.getWFlowTypeId().toInteger() > 0)) {
				int wFlowTypeId = new PmWFlowType(getSFParams()).getFirstWFlowTypeId(pmConn, bmoRequisition.getProgramCode());
				if (wFlowTypeId > 0) {
					bmoRequisition.getWFlowTypeId().setValue(wFlowTypeId);
				} else {
					bmUpdateResult.addError(bmoRequisition.getCode().getName(), "Debe crearse Tipo de WFlow para Orden de Compra.");
				}
			}

			// Crea el WFlow y asigna el ID recien creado
			PmWFlow pmWFlow = new PmWFlow(getSFParams());
			bmoRequisition.getWFlowId().setValue(pmWFlow.updateWFlow(pmConn, bmoRequisition.getWFlowTypeId().toInteger(), bmoRequisition.getWFlowId().toInteger(), 
					bmoRequisition.getProgramCode(), bmoRequisition.getId(), bmoRequisition.getUserCreateId().toInteger(), bmoRequisition.getCompanyId().toInteger(), -1,
					bmoRequisition.getCode().toString(), bmoRequisition.getName().toString(), bmoRequisition.getDescription().toString(), 
					bmoRequisition.getRequestDate().toString(), bmoRequisition.getDeliveryDate().toString(), BmoWFlow.STATUS_ACTIVE, bmUpdateResult).getId());

			// Registrar bitacora si es nuevo
			if (newRecord && bmoRequisition.getWFlowId().toInteger() > 0) {
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoRequisition.getWFlowId().toInteger(), BmoWFlowLog.TYPE_WFLOW, "Se creó la Orden de Compra.");
			}

			if (!newRecord)
				// Crear bitacora en OC de cambis al saldo
				createWFloLogRequisition(pmConn, bmoRequisition, "Modificó", bmUpdateResult);

			// Actualiza la OC
			return super.save(pmConn, bmoRequisition, bmUpdateResult);	
		}

		return bmUpdateResult;
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String data, String value) throws SFException {		
		double result = 0;
		bmoRequisition = (BmoRequisition)bmObject;

		PmConn pmConn = new PmConn(getSFParams());
		pmConn.open();

		try {
			if (data.equals(BmoRequisition.ACTION_REQIAMOUNT)) {			
				result = getReqiAmount(pmConn, Integer.parseInt(value), bmUpdateResult);
			} else if(data.equals(BmoRequisition.ACTION_PROFIT)){				
				result = getProfit(pmConn, Integer.parseInt(value), bmUpdateResult);
			}
			else if (data.equals(BmoRequisition.ACTION_COPYREQUISITION)) {
				int fromRequisitionId = Integer.parseInt(value);				
				bmUpdateResult = saveRequisitionCopy(pmConn, fromRequisitionId, bmUpdateResult);
			}
			else if (data.equals(BmoRequisition.ACTION_GETORDERPROPERTY)) {
				int orderId = Integer.parseInt(value);				
				bmUpdateResult = getOrderProperties(pmConn, orderId, bmUpdateResult);
			}
			else if (data.equals(BmoRequisition.ACTION_GETORDERPROPERTYTAX)) {
				int orderId = Integer.parseInt(value);				
				bmUpdateResult = getOrderPropertiesTax(pmConn, orderId, bmUpdateResult);
			} else if (data.equals(BmoRequisition.ACTION_CHEECKINGCOSTS)) {
				int bankAccountid = 0, requisitionId = 0;
				StringTokenizer tabs = new StringTokenizer(value, "|");
				while (tabs.hasMoreTokens()) {
					requisitionId = Integer.parseInt(tabs.nextToken());
					bankAccountid = Integer.parseInt(tabs.nextToken());	
				}
				// Comprobacion de viaticos
				cheekingCostTravelExpenses(pmConn, requisitionId, bankAccountid, bmUpdateResult);
			}

			bmUpdateResult.setMsg("" + result);
		} catch (SFException e) {
			throw new SFException(this.getClass().getName() + "-action() " + e.toString());
		} finally {
			pmConn.close();			
		}

		return bmUpdateResult;
	}

	// Comprobacion de Gastos/Viaticos
	private BmUpdateResult cheekingCostTravelExpenses(PmConn pmConn, int requisitionId, int bankAccountId, BmUpdateResult bmUpdateResult) throws SFException {
		// OC
		BmoRequisition bmoRequisition = new BmoRequisition();
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		// ROC
		BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		// CP
		BmoPaccount bmoPaccount = new BmoPaccount();
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		// MB
		BmoBankMovement bmoBankMovement = new BmoBankMovement();
		PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
		// CMB
		BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
		PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());

		pmConn.disableAutoCommit();

		// Validar que existe una OC
		if (!(requisitionId > 0))
			bmUpdateResult.addError(bmoRequisition.getCode().getName(), "No se encontró la O.C.");
		else {
			bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, requisitionId);

			// Validar que la OC sea de tipo viaticos
			if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_TRAVELEXPENSE)) {
				// Validar que en el Tipo de OC este configurado un tipo de MB.
				if (!(bmoRequisition.getBmoRequisitionType().getDevolutionBankmovTypeId().toInteger() > 0))
					bmUpdateResult.addError(bmoRequisition.getRequisitionTypeId().getName(), 
							"<b>El Tipo de O.C. '"+ bmoRequisition.getBmoRequisitionType().getName() + "' no está configurado para Viáticos. No está seleccionado un tipo de MB de Devolución.</b>");

				if (!(bmoRequisition.getBmoRequisitionType().getPaymentBankmovTypeId().toInteger() > 0))
					bmUpdateResult.addError(bmoRequisition.getRequisitionTypeId().getName(), 
							"<b>El Tipo de O.C. '"+ bmoRequisition.getBmoRequisitionType().getName() + "' no está configurado para Viáticos. No está seleccionado un tipo de MB de Pago Proveedor.</b>");

				// Validar que no exista ya una ROC
				if (!this.existROC(pmConn, bmoRequisition, bmUpdateResult)) {
					// Crear el ROC en automatico (este metodo tambien puede crear la CP y se valida abajo  si existe la CP)
					createRequisitionReceipt(pmConn, bmoRequisition, bmUpdateResult);
					// Obtener el ROC
					bmoRequisitionReceipt = (BmoRequisitionReceipt)pmRequisitionReceipt.get(pmConn, bmUpdateResult.getId());
					bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);
					pmRequisitionReceipt.saveSimple(pmConn, bmoRequisitionReceipt, bmUpdateResult);

					// Validar que no exista la CP
					if (!this.existPacc(pmConn, bmoRequisition, bmUpdateResult)) {
						// Crear la CxP en automatico
						pmPaccount.createFromRequisitionReceipt(pmConn, bmoRequisitionReceipt, bmUpdateResult);
						// Obtener la cxp
						bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmUpdateResult.getId());
					} else {
						bmoPaccount = (BmoPaccount)pmPaccount.getBy(pmConn, bmoRequisitionReceipt.getId(), bmoPaccount.getRequisitionReceiptId().getName());
					}
					
					// Si es negativo el saldo y van a comprobar, Autorizar y ligar MB a la CP
					if (bmoRequisition.getBalance().toDouble() < 0) {
						bmoPaccount.getStatus().setValue(BmoPaccount.STATUS_AUTHORIZED);
						pmPaccount.saveSimple(pmConn, bmoPaccount, bmUpdateResult);

						// Obtener la CMB
						bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.getBy(pmConn, bmoRequisition.getId(), bmoBankMovConcept.getRequisitionId().getName());					
						// Ligar la CP al MB de anticipo proveedor.
						if (bmoBankMovConcept.getId() > 0) {
							if (!(bmoBankMovConcept.getPaccountId().toInteger() > 0)) {
								bmoBankMovConcept.getPaccountId().setValue(bmoPaccount.getId());
								pmBankMovConcept.saveSimple(pmConn, bmoBankMovConcept, bmUpdateResult);
							}
						} else {
							printDevLog("No hay Anticipo de la OC, no debería entrar aqui porque siempre debe tener pago la oc.");
						}
					} else {
						// Autorizar la cxp para que ligue en automatico hasta el CMB
						bmoPaccount.getStatus().setValue(BmoPaccount.STATUS_AUTHORIZED);
						pmPaccount.save(pmConn, bmoPaccount, bmUpdateResult);
					}
					
//					bmoPaccount.getStatus().setValue(BmoPaccount.STATUS_AUTHORIZED);
//					pmPaccount.saveSimple(pmConn, bmoPaccount, bmUpdateResult);
//
//					// Obtener la CMB
//					bmoBankMovConcept = (BmoBankMovConcept)pmBankMovConcept.getBy(pmConn, bmoRequisition.getId(), bmoBankMovConcept.getRequisitionId().getName());					
//					// Ligar la CP al MB de anticipo proveedor.
//					if (bmoBankMovConcept.getId() > 0) {
//						if (!(bmoBankMovConcept.getPaccountId().toInteger() > 0)) {
//							bmoBankMovConcept.getPaccountId().setValue(bmoPaccount.getId());
//							pmBankMovConcept.saveSimple(pmConn, bmoBankMovConcept, bmUpdateResult);
//						}
//					} else {
//						printDevLog("No hay Anticipo de la OC, no debería entrar aqui porque siempre debe tener pago la oc.");
//					}

					// Si el saldo de la oc es mayor o menor a 0, crear MB
					if (bmoRequisition.getBalance().toDouble() != 0) {

						// Crear el MB devolucion o pago proveedor
						bmoBankMovement.getBankAccountId().setValue(bankAccountId);
						bmoBankMovement.getSupplierId().setValue(bmoRequisition.getSupplierId().toInteger());
						bmoBankMovement.getBankReference().setValue("");
						bmoBankMovement.getDescription().setValue("" + bmoRequisition.getCode().toString());

						double amountBkmc = 0;
						// Recuperar saldo de la OC para aplicar al MB
						if (bmoRequisition.getBalance().toDouble() < 0) {
							amountBkmc = (bmoRequisition.getBalance().toDouble() * -1);
							bmoBankMovement.getBankMovTypeId().setValue(bmoRequisition.getBmoRequisitionType().getDevolutionBankmovTypeId().toInteger());
						} else {
							amountBkmc = bmoRequisition.getBalance().toDouble();
							bmoBankMovement.getBankMovTypeId().setValue(bmoRequisition.getBmoRequisitionType().getPaymentBankmovTypeId().toInteger());
						}
						bmoBankMovement.getStatus().setValue("" + BmoBankMovement.STATUS_REVISION);
						bmoBankMovement.getInputDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
						bmoBankMovement.getDueDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
						pmBankMovement.save(pmConn, bmoBankMovement, bmUpdateResult);

						// Crear el concepto de Banco
						bmoBankMovConcept = new BmoBankMovConcept();
						bmoBankMovConcept.getBankMovementId().setValue(bmoBankMovement.getId());
						bmoBankMovConcept.getCode().setValue(bmoPaccount.getCode().toString());
						bmoBankMovConcept.getPaccountId().setValue(bmoPaccount.getId());
						bmoBankMovConcept.getBudgetItemId().setValue(bmoRequisition.getBudgetItemId().toInteger());
						bmoBankMovConcept.getAmount().setValue(amountBkmc);
						pmBankMovConcept.save(pmConn, bmoBankMovConcept, bmUpdateResult);
					}

					if (!bmUpdateResult.hasErrors())
						pmConn.commit();
				}
			} else 
				bmUpdateResult.addError(bmoRequisition.getRequisitionTypeId().getName(), "<b>La O.C. debe ser de Tipo Viáticos.</b>");
		}
		return bmUpdateResult;
	}

	// Genera bitacora de la OC
	public void addWFlowLog(PmConn pmConn, BmoRequisition bmoRequisition, String comments, BmUpdateResult bmUpdateResult) throws SFException {
		String logComments = "Se " + comments + " la Orden de Compra: " + bmoRequisition.getCode().toString() + ", ";
		//logComments += "Nombre: " + bmoRequisition.getName().toString()  + ", ";
		logComments += "Total: " + SFServerUtil.formatCurrency(bmoRequisition.getTotal().toDouble())  + ", ";
		logComments += "Pagos: " + SFServerUtil.formatCurrency(bmoRequisition.getPayments().toDouble())  + ", ";
		logComments += "Saldo: " + SFServerUtil.formatCurrency(bmoRequisition.getBalance().toDouble())  + ". ";
		//logComments += "Estatus: " + bmoRequisition.getStatus().getSelectedOption().getLabel()  + "";

		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
		pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoRequisition.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, logComments, "");
	}

	private BmUpdateResult getOrderProperties(PmConn pmConn,int orderId,BmUpdateResult bmUpdateResult)throws SFException {

		String sql = "";
		int propertyId = 0;
		sql = "SELECT orpy_propertyid from orderproperties " + 
				"WHERE orpy_orderid = "+orderId;

		pmConn.doFetch(sql);
		if (pmConn.next()) 
			propertyId = pmConn.getInt("orpy_propertyid");

		if(propertyId > 0){
			BmoProperty bmoProperty = new BmoProperty();
			PmProperty pmProperty = new PmProperty(getSFParams());
			bmoProperty = (BmoProperty) pmProperty.get(propertyId);
			bmUpdateResult.setBmObject(bmoProperty);
		}
		return bmUpdateResult;
	}

	private BmUpdateResult getOrderPropertiesTax(PmConn pmConn,int orderId,BmUpdateResult bmUpdateResult)throws SFException {

		String sql = "";
		int propertyId = 0;
		sql = "SELECT orpt_propertyid FROM orderpropertiestax " + 
				"WHERE orpt_orderid = "+orderId;
		printDevLog("Consulta getOrderPropertiesTax(): "+sql);		
		pmConn.doFetch(sql);
		if (pmConn.next()) 
			propertyId = pmConn.getInt("orpt_propertyid");

		if(propertyId > 0){
			BmoProperty bmoProperty = new BmoProperty();
			PmProperty pmProperty = new PmProperty(getSFParams());
			bmoProperty = (BmoProperty) pmProperty.get(propertyId);
			bmUpdateResult.setBmObject(bmoProperty);
		}
		return bmUpdateResult;
	}

	private BmUpdateResult saveRequisitionCopy(PmConn pmConn,int fromRequisitionId,BmUpdateResult bmUpdateResult)throws SFException {
		bmoRequisition = (BmoRequisition)this.get(fromRequisitionId);
		//Obtener el Tipo de OC
		BmoRequisitionType bmoRequisitionType = new BmoRequisitionType();
		PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
		bmoRequisitionType = (BmoRequisitionType)pmRequisitionType.get(bmoRequisition.getRequisitionTypeId().toInteger());

		if(bmoRequisitionType.getName().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)){
			bmUpdateResult.addError(bmoRequisition.getCode().toString(),"No se puede compiar una OC de tipo Estimación");
			return bmUpdateResult;
		}

		BmoReqPayType bmoReqPayType = new BmoReqPayType();
		PmReqPayType pmReqPayType = new PmReqPayType(getSFParams());
		bmoReqPayType = (BmoReqPayType)pmReqPayType.get(bmoRequisition.getReqPayTypeId().toInteger());

		BmoRequisition bmoRequisitionNew = new BmoRequisition();
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		String name = "";

		if(bmoRequisition.getName().toString().length() > 23)
			name = bmoRequisition.getName().toString().substring(0,23);
		else
			name = bmoRequisition.getName().toString();

		bmoRequisitionNew.getRequisitionTypeId().setValue(bmoRequisitionType.getId());		
		bmoRequisitionNew.getName().setValue("Copia_"+ name);
		bmoRequisitionNew.getDescription().setValue(bmoRequisition.getDescription().toString()+"(Copia de "+bmoRequisition.getCode().toString()+" )");
		bmoRequisitionNew.getRequestDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));

		if (bmoReqPayType != null) {
			if (!bmoRequisition.getDeliveryDate().toString().equals("")) {
				bmoRequisitionNew.getPaymentDate().setValue(SFServerUtil.addDays(getSFParams().getDateFormat(), 
						bmoRequisition.getDeliveryDate().toString(), bmoReqPayType.getDays().toInteger()));
			}
		}

		bmoRequisitionNew.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
		bmoRequisitionNew.getStatus().setValue(BmoRequisition.STATUS_EDITION);
		bmoRequisitionNew.getRequestedBy().setValue(bmoRequisition.getRequestedBy().toInteger());
		bmoRequisitionNew.getPaymentStatus().setValue(BmoRequisition.PAYMENTSTATUS_PENDING);
		bmoRequisitionNew.getAmount().setValue(bmoRequisition.getAmount().toDouble());
		bmoRequisitionNew.getTaxApplies().setValue(bmoRequisition.getTaxApplies().toInteger());
		bmoRequisitionNew.getTax().setValue(bmoRequisition.getTax().toDouble());
		bmoRequisitionNew.getHoldBack().setValue(bmoRequisition.getHoldBack().toDouble());
		bmoRequisitionNew.getAreaId().setValue(bmoRequisition.getAreaId().toInteger());
		bmoRequisitionNew.getSupplierId().setValue(bmoRequisition.getSupplierId().toInteger());
		bmoRequisitionNew.getReqPayTypeId().setValue(bmoRequisition.getReqPayTypeId().toInteger());
		bmoRequisitionNew.getWarehouseId().setValue(bmoRequisition.getWarehouseId().toInteger());

		if(bmoRequisitionType.getType().equals(BmoRequisitionType.TYPE_COMMISION)){
			bmoRequisitionNew.getOrderId().setValue(bmoRequisition.getOrderId().toInteger());
		}
		bmoRequisitionNew.getContractEstimationId().setValue(bmoRequisition.getContractEstimationId().toInteger());

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			bmoRequisitionNew.getBudgetItemId().setValue(bmoRequisition.getBudgetItemId().toInteger());
		}
		bmoRequisitionNew.getCompanyId().setValue(bmoRequisition.getCompanyId().toInteger());
		bmoRequisitionNew.getCurrencyId().setValue(bmoRequisition.getCurrencyId().toInteger());
		bmoRequisitionNew.getCurrencyParity().setValue(bmoRequisition.getCurrencyParity().toDouble());			
		bmoRequisitionNew.getLoanId().setValue(bmoRequisition.getLoanId().toInteger());

		pmRequisition.save(pmConn,bmoRequisitionNew,bmUpdateResult);
		copyItemsFromRequisition(pmConn, fromRequisitionId,bmoRequisitionNew, bmUpdateResult);

		return bmUpdateResult;
	}
	//Copia de Archivos
	private void copyFilestoCxP(PmConn pmConn,BmoRequisition bmoRequisition , BmUpdateResult bmUpdateResult) throws SFException {
		//Validar que todos los doc. que esten en la CXP existan en la oc si no es así que elimine aquellos doc. que no coicidan
		ArrayList<BmObject> filterListCxP = new ArrayList<BmObject>();
		PmPaccount pmPaccount;
		BmoFile bmoFile = new BmoFile();		
		BmoFile bmoFileOC = new BmoFile();
		try {
			pmPaccount = new PmPaccount(getSFParams());
			ArrayList<BmFilter> filterListRequi = new ArrayList<BmFilter>();
			BmoPaccount bmoPaccount = new BmoPaccount();
			// Buscamos todas CxP de la OC
			BmFilter filterRequiDelete = new BmFilter();
			filterRequiDelete.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionId(),bmoRequisition.getId());
			filterListRequi.add(filterRequiDelete);

			Iterator<BmObject> fileIteratorCxPDelete = pmPaccount.list(filterListRequi).iterator();

			// Por cada CxP
			while (fileIteratorCxPDelete.hasNext()) {
				bmoPaccount = (BmoPaccount) fileIteratorCxPDelete.next();
				filterListCxP.add(bmoPaccount);

				ArrayList<BmFilter> filterListFile = new ArrayList<BmFilter>();
				PmFile pmFile = new PmFile(getSFParams());

				// Buscamos los archivos de la CxP
				BmFilter filterForeign = new BmFilter();
				filterForeign.setValueFilter(bmoFile.getKind(), bmoFile.getForeignId(), bmoPaccount.getId());
				filterListFile.add(filterForeign);

				Iterator<BmObject> fileIteratorFilesCxP = pmFile.list(filterListFile).iterator();

				while (fileIteratorFilesCxP.hasNext()) {
					BmoFile bmoNewFile = new BmoFile();
					bmoNewFile = (BmoFile) fileIteratorFilesCxP.next();

					// Buscamos si existe los archivos de la OC si no existe borra el archivo de la CXP
					ArrayList<BmFilter> filterListDelete = new ArrayList<BmFilter>();

					BmFilter filterProgram = new BmFilter();
					filterProgram.setValueFilter(bmoFile.getKind(), bmoFile.getProgramId(),getSFParams().getProgramId(bmoRequisition.getProgramCode()));
					filterListDelete.add(filterProgram);

					BmFilter filterFiles = new BmFilter();
					filterFiles.setValueFilter(bmoFile.getKind(), bmoFile.getBlobkey(),bmoNewFile.getBlobkey().getValue());
					filterListDelete.add(filterFiles);

					BmFilter filterByName = new BmFilter();
					filterByName.setValueFilter(bmoFile.getKind(), bmoFile.getName(), bmoNewFile.getName().getValue());
					filterListDelete.add(filterByName);

					Iterator<BmObject> fileDelete = pmFile.list(filterListDelete).iterator();
					if (!fileDelete.hasNext()) {
						pmFile.delete(pmConn, bmoNewFile, bmUpdateResult);
					}
				}
			}
			// CxP
			Iterator<BmObject> fileIteratorCxP = filterListCxP.iterator();

			BmoPaccount bmoPaccountCxP = new BmoPaccount();			
			// Por cada CxP copiar documentos a la CxP
			while (fileIteratorCxP.hasNext()) {
				bmoPaccountCxP = (BmoPaccount) fileIteratorCxP.next();

				ArrayList<BmFilter> filterListFile = new ArrayList<BmFilter>();
				PmFile pmFile = new PmFile(getSFParams());

				// Buscamos los archivos de la OC
				BmFilter filterForeign = new BmFilter();
				filterForeign.setValueFilter(bmoFile.getKind(), bmoFile.getForeignId(), bmoRequisition.getId());
				filterListFile.add(filterForeign);

				BmFilter filterProgramOC = new BmFilter();
				filterProgramOC.setValueFilter(bmoFile.getKind(), bmoFile.getProgramId(),getSFParams().getProgramId(bmoRequisition.getProgramCode()));
				filterListFile.add(filterProgramOC);

				Iterator<BmObject> fileIteratorFilesOC = pmFile.list(filterListFile).iterator();

				while (fileIteratorFilesOC.hasNext()) {

					// Buscamos un archivo que se igual al de la OC pero con el modulo,id, de la de  CxP

					bmoFileOC = (BmoFile) fileIteratorFilesOC.next();
					ArrayList<BmFilter> filterListCopy = new ArrayList<BmFilter>();

					BmFilter filterProgram = new BmFilter();
					filterProgram.setValueFilter(bmoFile.getKind(), bmoFile.getProgramId(),getSFParams().getProgramId(bmoPaccountCxP.getProgramCode()));
					filterListCopy.add(filterProgram);

					BmFilter filterFiles = new BmFilter();
					filterFiles.setValueFilter(bmoFile.getKind(), bmoFile.getBlobkey(),
							bmoFileOC.getBlobkey().getValue());
					filterListCopy.add(filterFiles);

					BmFilter filterForeignId = new BmFilter();
					filterForeignId.setValueFilter(bmoFile.getKind(), bmoFile.getForeignId(), bmoPaccountCxP.getId());
					filterListCopy.add(filterForeignId);

					BmFilter filterByName = new BmFilter();
					filterByName.setValueFilter(bmoFile.getKind(), bmoFile.getName(), bmoFileOC.getName().getValue());
					filterListCopy.add(filterByName);

					Iterator<BmObject> fileCopy = pmFile.list(filterListCopy).iterator();
					// Si NO existe file con el mismo file blobkey y con el modulo de CxP
					if (!fileCopy.hasNext()) {	
						BmoFile bmoNewFile = new BmoFile();
						bmoNewFile.getName().setValue(bmoFileOC.getName().toString());
						bmoNewFile.getDescription().setValue(bmoFileOC.getDescription().toString());
						bmoNewFile.getEncoding().setValue(bmoFileOC.getEncoding().toString());
						bmoNewFile.getBlobkey().setValue(bmoFileOC.getBlobkey().toString());
						bmoNewFile.getForeignId().setValue(bmoPaccountCxP.getId());
						bmoNewFile.getProgramId().setValue(getSFParams().getProgramId(bmoPaccountCxP.getProgramCode()));

						pmFile.save(pmConn, bmoNewFile, bmUpdateResult);
					}
				}
			}
		} catch (SFPmException e) {
			e.printStackTrace();
		}
	}

	// Creacion de los items de la O.C.  con los datos de la O.C. a copiar
	private void copyItemsFromRequisition(PmConn pmConn, int fromRequisitionId,BmoRequisition bmoRequisitionNew , BmUpdateResult bmUpdateResult)
			throws SFException {
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
		PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());

		BmFilter filterRequisitionItem = new BmFilter();
		filterRequisitionItem.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(),
				fromRequisitionId);
		Iterator<BmObject> listRequisitionItem = new PmRequisitionItem(getSFParams())
				.list(pmConn, filterRequisitionItem).iterator();

		while (listRequisitionItem.hasNext()) {
			bmoRequisitionItem = (BmoRequisitionItem) listRequisitionItem.next();

			// Crear un nuevo item en la OC
			BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
			bmoRequisitionItemNew.getName().setValue(bmoRequisitionItem.getName().toString());
			bmoRequisitionItemNew.getQuantityReceipt().setValue(0);
			bmoRequisitionItemNew.getQuantityReturned().setValue(0);
			bmoRequisitionItemNew.getQuantity().setValue(bmoRequisitionItem.getQuantity().toDouble());
			if(bmoRequisitionItem.getDays().toDouble() > 0) bmoRequisitionItemNew.getDays().setValue(bmoRequisitionItem.getDays().toDouble());
			bmoRequisitionItemNew.getPrice().setValue(bmoRequisitionItem.getPrice().toDouble());
			bmoRequisitionItemNew.getAmount().setValue(bmoRequisitionItem.getAmount().toDouble());
			bmoRequisitionItemNew.getProductId().setValue(bmoRequisitionItem.getProductId().toInteger());
			bmoRequisitionItemNew.getEstimationItemId().setValue(bmoRequisitionItem.getEstimationItemId().toInteger());
			bmoRequisitionItemNew.getRequisitionId().setValue(bmoRequisitionNew.getId());

			pmRequisitionItem.saveSimple(pmConn, bmoRequisitionItemNew, bmUpdateResult);
		}
		pmRequisition.save(pmConn,bmoRequisitionNew, bmUpdateResult);
	}

	// Calcula el monto de orden de compra
	private double getReqiAmount(PmConn pmConn, int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double result = 0;
		double amount = 0;
		double orderParity = 0;

		//Obtener el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, orderId);

		//Si no existe paridad en el pedido tomar la actual
		if (!(bmoOrder.getCurrencyParity().toDouble() > 0)) {
			orderParity = bmoOrder.getBmoCurrency().getParity().toDouble();
		} else {
			orderParity = bmoOrder.getCurrencyParity().toDouble();
		}

		//Sumar las OC que tengan la misma moneda del pedido
		sql = " SELECT SUM(reqi_amount) AS amount FROM requisitions " +		      
				" WHERE reqi_orderid = " + orderId +
				" AND reqi_currencyid = " + bmoOrder.getCurrencyId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) result += pmConn.getDouble("amount");

		//Sumar las OC que tengan otra moneda que la del pedido
		sql = " SELECT * FROM requisitions " +		      
				" LEFT JOIN currencies ON (reqi_currencyid = cure_currencyid) " +
				" WHERE reqi_orderid = " + orderId +
				" AND reqi_currencyid <> " + bmoOrder.getCurrencyId().toInteger();
		pmConn.doFetch(sql);
		while (pmConn.next()) {

			double reqiParity = pmConn.getDouble("reqi_currencyparity");
			if (!(reqiParity > 0))
				reqiParity = pmConn.getDouble("cure_parity");

			//Convertir el monto a la moneda del pedido
			amount += pmConn.getDouble("reqi_amount") * (reqiParity / orderParity);

			result += amount;
		}

		return result;
	}

	// Calcula la utilidad del pedido
	private double getProfit(PmConn pmConn, int orderId, BmUpdateResult bmUpdateResult) throws SFException {
		//String sql = "";
		double result = 0;
		PmOrder pmOrder = new PmOrder(getSFParams());
		BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, orderId);
		double orderTotal = bmoOrder.getTotal().toDouble();		

		//Eliminar el IVA del Total
		if (bmoOrder.getTax().toDouble() > 0) {		
			orderTotal = orderTotal - bmoOrder.getTax().toDouble();
		}			

		result = orderTotal - getReqiAmount(pmConn, orderId, bmUpdateResult);
		return result;
	}

	// Actualiza saldos y sumas de la orden de compra
	public void updateBalance(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		double amount = 0;

		pmConn.doFetch("SELECT sum(rqit_amount) FROM requisitionitems "
				+ " WHERE rqit_requisitionid = " + bmoRequisition.getId());

		if (pmConn.next()) amount = pmConn.getDouble(1);

		// Calcular montos
		if (amount == 0) {
			bmoRequisition.getAmount().setValue(0);
			bmoRequisition.getDiscount().setValue(0);
			bmoRequisition.getTax().setValue(0);
			bmoRequisition.getHoldBack().setValue(0);			
			bmoRequisition.getTotal().setValue(0);
			bmoRequisition.getPayments().setValue(0);
			bmoRequisition.getBalance().setValue(0);
		} else {
			bmoRequisition.getAmount().setValue(SFServerUtil.roundCurrencyDecimals(amount));

			if (bmoRequisition.getDiscount().toString().equals("")) bmoRequisition.getDiscount().setValue(0);

			double taxRate = ((BmoFlexConfig)getSFParams().getBmoAppConfig()).getTax().toDouble() / 100;
			double tax = 0;
			if (bmoRequisition.getTaxApplies().toBoolean()) tax = (bmoRequisition.getAmount().toDouble() - bmoRequisition.getDiscount().toDouble()) * taxRate;
			double total = bmoRequisition.getAmount().toDouble() - bmoRequisition.getDiscount().toDouble() + tax;

			if (bmoRequisition.getHoldBack().toDouble() > 0) {
				bmoRequisition.getHoldBack().setValue(SFServerUtil.roundCurrencyDecimals(bmoRequisition.getHoldBack().toDouble()));	
				total = total - bmoRequisition.getHoldBack().toDouble();
			}	
			bmoRequisition.getTax().setValue(SFServerUtil.roundCurrencyDecimals(tax));
			bmoRequisition.getTotal().setValue(SFServerUtil.roundCurrencyDecimals(total));			
			bmoRequisition.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(bmoRequisition.getTotal().toDouble() - bmoRequisition.getPayments().toDouble()));
		}

		super.save(pmConn, bmoRequisition, bmUpdateResult); 
	}

	// Creacion de los items de la oc con los datos de la estimacion
	private void createItemsFromContractEstimation(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		BmoEstimationItem bmoEstimationItem = new BmoEstimationItem();

		PmContractEstimation pmContractEstimation = new PmContractEstimation(getSFParams());
		BmoContractEstimation bmoContractEstimaton = (BmoContractEstimation)pmContractEstimation.get(pmConn, bmoRequisition.getContractEstimationId().toInteger());

		BmFilter filterEstimationItem = new BmFilter();
		filterEstimationItem.setValueFilter(bmoEstimationItem.getKind(), bmoEstimationItem.getContractEstimationId(), bmoContractEstimaton.getId());
		Iterator<BmObject> listEstimationItem = new PmEstimationItem(getSFParams()).list(pmConn, filterEstimationItem).iterator();			

		while (listEstimationItem.hasNext()) {
			bmoEstimationItem = (BmoEstimationItem)listEstimationItem.next();

			// Crear un nuevo item en el recibo
			BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
			bmoRequisitionItemNew.getRequisitionId().setValue(bmoRequisition.getId());
			bmoRequisitionItemNew.getEstimationItemId().setValue(bmoEstimationItem.getId());
			bmoRequisitionItemNew.getQuantity().setValue(bmoEstimationItem.getQuantity().toDouble());				
			bmoRequisitionItemNew.getName().setValue(bmoEstimationItem.getBmoContractConceptItem().getName().toString());

			if (bmoEstimationItem.getQuantity().toDouble() > 0) {					
				bmoRequisitionItemNew.getPrice().setValue(bmoEstimationItem.getPrice().toDouble());
				// Establecer monto
				double amount = (bmoEstimationItem.getPrice().toDouble() * bmoEstimationItem.getQuantity().toDouble());
				if (bmoRequisitionItemNew.getDays().toDouble() > 0)
					amount = amount * bmoRequisitionItemNew.getDays().toDouble();
				bmoRequisitionItemNew.getAmount().setValue(amount);
			} else {
				bmoRequisitionItemNew.getPrice().setValue(bmoEstimationItem.getPrice().toDouble());
				bmoRequisitionItemNew.getAmount().setValue(0);
			}
			super.save(pmConn,bmoRequisitionItemNew, bmUpdateResult);
		}		

		//Calcular el precio con las amortizaciones

		//Fondo de Garantia
		if (bmoContractEstimaton.getBmoWorkContract().getPercentGuaranteeFund().toDouble() > 0) {
			// Crear un nuevo item en el recibo
			BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
			bmoRequisitionItemNew.getRequisitionId().setValue(bmoRequisition.getId());
			bmoRequisitionItemNew.getEstimationItemId().setValue(bmoEstimationItem.getId());
			bmoRequisitionItemNew.getQuantity().setValue(1);				
			bmoRequisitionItemNew.getName().setValue("Fondo de Garantia");
			bmoRequisitionItemNew.getPrice().setValue(-(bmoContractEstimaton.getWarrantyFund().toDouble()));
			bmoRequisitionItemNew.getAmount().setValue(bmoRequisitionItemNew.getPrice().toDouble() * bmoRequisitionItemNew.getQuantity().toDouble());
			super.save(pmConn,bmoRequisitionItemNew, bmUpdateResult);
		}

		//Calcular el amortizado del anticipo		
		if (bmoContractEstimaton.getBmoWorkContract().getPercentDownPayment().toDouble() > 0) {
			// Crear un nuevo item en el recibo
			BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
			bmoRequisitionItemNew.getRequisitionId().setValue(bmoRequisition.getId());
			bmoRequisitionItemNew.getEstimationItemId().setValue(bmoEstimationItem.getId());
			bmoRequisitionItemNew.getQuantity().setValue(1);				
			bmoRequisitionItemNew.getName().setValue("Amort.Anticipo");
			bmoRequisitionItemNew.getPrice().setValue(-(bmoContractEstimaton.getDownPayment().toDouble()));
			bmoRequisitionItemNew.getAmount().setValue(bmoRequisitionItemNew.getPrice().toDouble() * bmoRequisitionItemNew.getQuantity().toDouble());
			super.save(pmConn,bmoRequisitionItemNew, bmUpdateResult);
		}

		//Iva		
		if (bmoContractEstimaton.getBmoWorkContract().getTax().toDouble() > 0) {
			// Crear un nuevo item en el recibo
			BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
			bmoRequisitionItemNew.getRequisitionId().setValue(bmoRequisition.getId());
			bmoRequisitionItemNew.getEstimationItemId().setValue(bmoEstimationItem.getId());
			bmoRequisitionItemNew.getQuantity().setValue(1);				
			bmoRequisitionItemNew.getName().setValue("Iva de la Estimación");
			bmoRequisitionItemNew.getPrice().setValue(bmoContractEstimaton.getTax().toDouble());
			bmoRequisitionItemNew.getAmount().setValue(bmoRequisitionItemNew.getPrice().toDouble() * bmoRequisitionItemNew.getQuantity().toDouble());
			super.save(pmConn,bmoRequisitionItemNew, bmUpdateResult);
		}

		//Otros Cargos		
		if (bmoContractEstimaton.getOthersExpenses().toDouble() > 0) {
			// Crear un nuevo item en el recibo
			BmoRequisitionItem bmoRequisitionItemNew = new BmoRequisitionItem();
			bmoRequisitionItemNew.getRequisitionId().setValue(bmoRequisition.getId());
			bmoRequisitionItemNew.getEstimationItemId().setValue(bmoEstimationItem.getId());
			bmoRequisitionItemNew.getQuantity().setValue(1);				
			bmoRequisitionItemNew.getName().setValue("Otros Cargos");
			bmoRequisitionItemNew.getPrice().setValue(-(bmoContractEstimaton.getOthersExpenses().toDouble()));
			bmoRequisitionItemNew.getAmount().setValue(bmoRequisitionItemNew.getPrice().toDouble() * bmoRequisitionItemNew.getQuantity().toDouble());
			super.save(pmConn,bmoRequisitionItemNew, bmUpdateResult);
		}

		super.save(pmConn, bmoRequisition, bmUpdateResult);
	}

	// Crear el recibo de OC
	private void createRequisitionReceipt(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();		
		bmoRequisitionReceipt = pmRequisitionReceipt.createFromRequisition(pmConn, bmoRequisition, bmUpdateResult);
		bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_AUTHORIZED);
		super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);
	}

	// Validar si es posible rechazar la OC, dependiendo si existen Cuentas x Pagar
	public boolean existPacc(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		//Validar que no exista un ROC ligado a la OC
		String sql = "";
		boolean result = false;
		int items = 0;
		sql = " SELECT COUNT(pacc_paccountid) AS paccs FROM paccounts  " +
				" WHERE pacc_requisitionid = " + bmoRequisition.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) items = pmConn.getInt("paccs"); 

		if (items > 0) {
			result = true;			
		} 

		return result;		
	}

	//Validar que exista en ROC
	public boolean existROC(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		boolean result = false;
		int items = 0;
		sql = " SELECT COUNT(rerc_requisitionreceiptid) AS items FROM requisitionreceipts " + 
				" WHERE rerc_requisitionid = " + bmoRequisition.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) items = pmConn.getInt("items");

		if (items > 0) result = true;

		return result;
	}

	//Creacion de los items de la oc con los datos de la estimacion
	private boolean existItems(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		boolean result = false;
		int items = 0;
		sql = " SELECT COUNT(rqit_requisitionitemid) AS items FROM requisitionitems " + 
				" WHERE rqit_requisitionid = " + bmoRequisition.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) items = pmConn.getInt("items");

		if (items > 0) result = true;

		return result;
	}

	// Revisar si tiene pagos la OC
	private boolean hasPayment(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		boolean result = false;
		int items = 0;

		//Buscar anticipos a OC		
		sql = " SELECT COUNT(*) FROM bankmovements " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +			  	
				" WHERE bkmv_requisitionid = " + bmoRequisition.getId() +
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'";		
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			items = pmConn.getInt(1);
		}

		if (items > 0) {
			result = true;
		} else {

			BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
			BmFilter bmFilter = new BmFilter();
			bmFilter.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getRequisitionId(), bmoRequisition.getId());
			PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
			Iterator<BmObject> requisitionReceiptIterator = pmRequisitionReceipt.list(pmConn, bmFilter).iterator();		

			while (requisitionReceiptIterator.hasNext()) {

				bmoRequisitionReceipt = (BmoRequisitionReceipt)requisitionReceiptIterator.next();

				//Obtener las CxP que tienen pagos
				sql = " SELECT COUNT(*) FROM paccounts " +
						" LEFT JOIN paccounttypes ON (pacc_paccounttypeid = pact_paccounttypeid) " +
						" WHERE pacc_requisitionreceiptid  = " + bmoRequisitionReceipt.getId() +
						" AND pact_type = '" + BmoPaccountType.TYPE_WITHDRAW + "'" +
						" AND pacc_balance < pacc_total";
				pmConn.doFetch(sql);
				if(pmConn.next()) {
					items = pmConn.getInt(1);
				}

				if (items > 0) {
					result = true;
					break;
				}
			}
		}	

		return result;
	}

	// Elimina los recibos de OC ligados
	private void removeROC(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {

		BmoRequisitionReceipt bmoRequisitionReceipt = new BmoRequisitionReceipt();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoRequisitionReceipt.getKind(), bmoRequisitionReceipt.getRequisitionId(), bmoRequisition.getId());
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		Iterator<BmObject> requisitionReceiptIterator = pmRequisitionReceipt.list(pmConn, bmFilter).iterator();

		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		BmoPaccount bmoPaccount = new BmoPaccount();

		while (requisitionReceiptIterator.hasNext()) {
			bmoRequisitionReceipt = (BmoRequisitionReceipt)requisitionReceiptIterator.next();
			BmFilter bmFilterPacc = new BmFilter();
			bmFilterPacc.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionReceiptId(), bmoRequisitionReceipt.getId());			
			Iterator<BmObject> paccountIterator = pmPaccount.list(pmConn, bmFilterPacc).iterator();

			while(paccountIterator.hasNext()) {
				bmoPaccount = (BmoPaccount)paccountIterator.next();
				pmPaccount.delete(pmConn, bmoPaccount, bmUpdateResult);
			}

			bmoRequisitionReceipt.getStatus().setValue(BmoRequisitionReceipt.STATUS_REVISION);
			super.save(pmConn, bmoRequisitionReceipt, bmUpdateResult);

			pmRequisitionReceipt.delete(pmConn, bmoRequisitionReceipt, bmUpdateResult);
		}		
	}

	// Determina si tiene items que afecten inventario
	public boolean requisitionAffectsInventory(PmConn pmConn, int requisitionId) throws SFException {
		boolean requisitionAffectsInventory = false;

		pmConn.doFetch("SELECT * FROM requisitionitems "
				+ " LEFT JOIN products ON (rqit_productid = prod_productid)"
				+ " WHERE rqit_requisitionid = " + requisitionId 
				+ " AND prod_inventory = 1");

		if (pmConn.next()) requisitionAffectsInventory = true;

		return requisitionAffectsInventory;
	}

	// Determina si hay ordenes de compra pendientes por proyecto
	public boolean hasPendingRequisitionsByOrder(PmConn pmConn, int orderId) throws SFException {
		boolean hasPendingRequisitions = false;

		pmConn.doFetch("SELECT * FROM requisitions "
				+ " LEFT JOIN requisitiontypes ON (reqi_requisitiontypeid = rqtp_requisitiontypeid)"
				+ " WHERE reqi_orderid = " + orderId
				+ " AND (rqtp_type = '" + BmoRequisitionType.TYPE_RENTAL + "'"
				+ " OR rqtp_type = '" + BmoRequisitionType.TYPE_PURCHASE + "') "
				+ " AND reqi_status = '" + BmoRequisition.STATUS_REVISION + "'");

		if (pmConn.next()) hasPendingRequisitions = true;

		return hasPendingRequisitions;
	}

	// Revisar ingresos de material a almacenes, para determinar estatus de entrega
	public void updateDeliveryStatus(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		double rQuantity = 0;
		double dQuantity = 0;

		// Obten cantidad de items de la requisición, de productos tipo producto y no servicio
		String sql = "SELECT SUM(rqit_quantity) FROM requisitionitems "
				+ " LEFT JOIN products on (rqit_productid = prod_productid) "	
				+ " WHERE rqit_requisitionid = " + bmoRequisition.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) rQuantity = pmConn.getDouble(1);

		// Obten cantidad de items en almacén
		sql = "SELECT SUM(reit_quantity) FROM requisitionreceiptitems "
				+ " LEFT JOIN requisitionreceipts ON (reit_requisitionreceiptid = rerc_requisitionreceiptid) "
				+ " WHERE rerc_requisitionid = " + bmoRequisition.getId();
		pmConn.doFetch(sql);
		if (pmConn.next()) dQuantity = pmConn.getDouble(1);

		// Revisar cantidades
		if (dQuantity == 0) {
			bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_REVISION);
		} else if (dQuantity >= rQuantity) {
			bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_TOTAL);
		} else {
			bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_PARTIAL);
		}

		// Actualizar status
		super.save(pmConn, bmoRequisition, bmUpdateResult);
	}

	// Actualiza estatus y montos de pagos
	public void updatePayments(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		double totalPaccounts = 0, totalCreditNotes = 0, totalBalance = 0, totalPayments = 0; // totalMBAntProv = 0;
		String sql = "";

		// Asigna el saldo igual al total de la OC
		totalBalance = bmoRequisition.getTotal().toDouble();

		// Se calculan pagos directos de MB (anticipos a OC)
		//Calcular los MB con diferente moneda
		//		sql = " SELECT * FROM bankmovements " +		      
		//				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
		//				" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid) " +
		//				" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
		//				" WHERE bkmv_requisitionid = " + bmoRequisition.getId() +
		//				" AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'" +
		//				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'" +
		//				" AND bkmv_status <> '" + BmoBankMovement.STATUS_CANCELLED + "'" ; 
		//
		//		pmConn.doFetch(sql);
		//		while (pmConn.next()) {
		//			double payment = 0;
		//			// Valida la moneda y si requiere conversion
		//			if (bmoRequisition.getCurrencyId().toInteger() == pmConn.getInt("cure_currencyid")) {
		//				payment += pmConn.getDouble("bkmv_withdraw");
		//			} else {
		//				payment += (pmConn.getDouble("bkmv_withdraw") * pmConn.getDouble("cure_parity")) / bmoRequisition.getCurrencyParity().toDouble();
		//			}
		//			totalPayments += payment;
		//			totalBalance -= payment;
		//			totalMBAntProv += payment;
		//			printDevLog("MB anticipo: "+totalPayments);
		//		}

		// Se calculan pagos directos de MB (anticipos a OC)
		sql = " SELECT bkmc_amount, bkmc_amountconverted, bkmc_currencyparity, cure_currencyid, cure_parity, bkmc_requisitionid, bkmc_paccountid FROM bankmovconcepts " +		      
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" LEFT JOIN bankaccounts ON (bkmv_bankaccountid = bkac_bankaccountid) " +
				" LEFT JOIN currencies ON (bkac_currencyid = cure_currencyid) " +
				" WHERE bkmc_requisitionid = " + bmoRequisition.getId() +
				" AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "'" +
				" AND (bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "') " +
				" AND bkmc_requisitionid > 0 " +
				" AND bkmc_paccountid IS NULL " +
				" AND bkmv_status <> '" + BmoBankMovement.STATUS_CANCELLED + "'" ; 

		printDevLog("sql_MBC: " +sql);
		pmConn.doFetch(sql);
		while (pmConn.next()) {
			double amount = 0;
			printDevLog("El Total de Conceptos de Movimientos de Banco de Anticipo Ligados es: " + amount);
			printDevLog("pacc-"+ pmConn.getInt("bkmc_paccountid") + " |reqi-" +pmConn.getInt("bkmc_requisitionid") );
			// Valida la moneda y si requiere conversion
			if (bmoRequisition.getCurrencyId().toInteger() == pmConn.getInt("cure_currencyid")) {
				amount += pmConn.getDouble("bkmc_amount");
			} else {
				printDevLog("monto: "+pmConn.getDouble("bkmc_amount"));
				printDevLog("montoConvertido: "+pmConn.getDouble("bkmc_amountconverted"));
				printDevLog("paridadMoneda: "+pmConn.getDouble("cure_parity"));
				printDevLog("paridadConcepto: "+pmConn.getDouble("bkmc_currencyparity"));
				printDevLog("paridadOC: "+bmoRequisition.getCurrencyParity().toDouble());

				if (pmConn.getDouble("bkmc_amountconverted") > 0 && pmConn.getDouble("bkmc_currencyparity") > 1)
					amount += pmConn.getDouble("bkmc_amountconverted");
				else
					amount += (pmConn.getDouble("bkmc_amount") * pmConn.getDouble("cure_parity")) / pmConn.getDouble("bkmc_currencyparity");

				//amount += (pmConn.getDouble("bkmc_amount") * pmConn.getDouble("cure_parity")) / bmoRequisition.getCurrencyParity().toDouble();
			}
			totalPayments += amount;
			totalBalance -= amount;
			//totalMBAntProv += amount;
		}
		printDevLog("Pagos MB Anticipo Prov.: " +totalPayments);

		// Asigna montos a campos de sumas de pagos del OC
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		BmoPaccount bmoPaccount = new BmoPaccount();
		ArrayList<BmFilter> filters = new ArrayList<BmFilter>();

		// Filtro de notas de credito
		BmFilter filterByRequisition = new BmFilter();
		filterByRequisition.setValueFilter(bmoPaccount.getKind(), bmoPaccount.getRequisitionId(), bmoRequisition.getId());
		filters.add(filterByRequisition);
		Iterator<BmObject> paccountList = pmPaccount.list(pmConn, filters).iterator();

		// Se calculan pagos de CxP ligadas
		while (paccountList.hasNext()) {
			BmoPaccount bmoNextPaccount = (BmoPaccount)paccountList.next();

			printDevLog(" --- Inicio revision de CxP: " + bmoNextPaccount.getCode().toString() + " ---");
			printDevLog("Total: " + bmoNextPaccount.getTotal().toDouble());
			printDevLog("Pagos: " + bmoNextPaccount.getPayments().toDouble());
			printDevLog("Saldo: " + bmoNextPaccount.getBalance().toDouble());
			printDevLog("Pagos Calculados: " + (bmoNextPaccount.getTotal().toDouble() - bmoNextPaccount.getBalance().toDouble()));

			// Notas de Credito
			if (bmoNextPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_CREDITNOTE)) {
				double creditNoteInRequisitionCurrency = 0;
				if (bmoRequisition.getCurrencyId().toInteger() == bmoNextPaccount.getCurrencyId().toInteger()) {
					creditNoteInRequisitionCurrency = bmoNextPaccount.getAmount().toDouble();
				} else {
					creditNoteInRequisitionCurrency = ((bmoNextPaccount.getAmount().toDouble() * bmoNextPaccount.getCurrencyParity().toDouble()) / bmoRequisition.getCurrencyParity().toDouble());
				}
				totalCreditNotes += creditNoteInRequisitionCurrency;
				printDevLog("Nota de Credito " + bmoNextPaccount.getCode().toString() + " en moneda OC : " + creditNoteInRequisitionCurrency);
			} 
			// Cuentas x Pagar
			else if (bmoNextPaccount.getBmoPaccountType().getCategory().equals(BmoPaccountType.CATEGORY_REQUISITION)
					&& bmoNextPaccount.getBmoPaccountType().getType().equals(BmoPaccountType.TYPE_WITHDRAW)) {
				// Cifras CxP
				double paccountInRequisitionCurrency = 0, balanceInRequisitionCurrency = 0, paymentInRequisitionCurrency = 0;
				if (bmoRequisition.getCurrencyId().toInteger() == bmoNextPaccount.getCurrencyId().toInteger()) {
					paccountInRequisitionCurrency = bmoNextPaccount.getAmount().toDouble();
					balanceInRequisitionCurrency = bmoNextPaccount.getBalance().toDouble();
					paymentInRequisitionCurrency = bmoNextPaccount.getTotal().toDouble() - bmoNextPaccount.getBalance().toDouble();

				} else {
					paccountInRequisitionCurrency = ((bmoNextPaccount.getAmount().toDouble() * bmoNextPaccount.getCurrencyParity().toDouble()) / bmoRequisition.getCurrencyParity().toDouble());	
					balanceInRequisitionCurrency = ((bmoNextPaccount.getBalance().toDouble() * bmoNextPaccount.getCurrencyParity().toDouble()) / bmoRequisition.getCurrencyParity().toDouble());
					paymentInRequisitionCurrency = (((bmoNextPaccount.getTotal().toDouble() - bmoNextPaccount.getBalance().toDouble()) * bmoNextPaccount.getCurrencyParity().toDouble()) / bmoRequisition.getCurrencyParity().toDouble());
				}

				printDevLog("Pagos CxP: " + SFServerUtil.roundCurrencyDecimals(paymentInRequisitionCurrency));

				totalPaccounts += paccountInRequisitionCurrency;
				totalPayments += paymentInRequisitionCurrency;
				totalBalance -= paymentInRequisitionCurrency;

				printDevLog("Moneda OC: " + bmoRequisition.getBmoCurrency().getCode().toString() + " " + ", en CxP: " + bmoNextPaccount.getBmoCurrency().getCode().toString());
				printDevLog("CxP " + bmoNextPaccount.getCode().toString() + " en moneda pedido : " + paccountInRequisitionCurrency);
				printDevLog("Saldo  en CxP " + bmoNextPaccount.getCode().toString() + " en moneda pedido : " + balanceInRequisitionCurrency);
			}
		}		

		// Redondear pagos y saldo
		totalPayments = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(totalPayments));
		totalBalance = SFServerUtil.stringToDouble(SFServerUtil.roundCurrencyDecimals(totalBalance));

		bmoRequisition.getPayments().setValue(SFServerUtil.roundCurrencyDecimals(totalPayments));
		bmoRequisition.getBalance().setValue(SFServerUtil.roundCurrencyDecimals(totalBalance));

		printDevLog("Total CxP Moneda del pedido: " + SFServerUtil.roundCurrencyDecimals(totalPaccounts));

		printDevLog("\nTotales generales");
		printDevLog("Total Pagos CxP+MB: " + SFServerUtil.roundCurrencyDecimals(totalPayments));
		printDevLog("Total Notas Credito Moneda del OC: " + SFServerUtil.roundCurrencyDecimals(totalCreditNotes));
		printDevLog("Total Saldo Moneda del OC: " + SFServerUtil.roundCurrencyDecimals(totalBalance));

		// Se ha pagado mas de la O.C.
		if (totalPayments > 0 && totalPayments > bmoRequisition.getTotal().toDouble()) {
			printDevLog("pagos superiores");
			// Permiso para pagar de mas para OC de tipo Viaticos
			if (bmoRequisition.getBmoRequisitionType().getType().toChar() != BmoRequisitionType.TYPE_TRAVELEXPENSE) {
				bmUpdateResult.addError(bmoRequisition.getTotal().getName(), "<b>Los Pagos exceden el total de la Orden de Compra.</b>");
			} else if (bmoRequisition.getBmoRequisitionType().getType().toChar() == BmoRequisitionType.TYPE_TRAVELEXPENSE) {
				if (!getSFParams().hasSpecialAccess(BmoRequisition.ACCESS_REQTRAVEXP)) {
					bmUpdateResult.addError(bmoRequisition.getTotal().getName(), "<b>Los Pagos exceden el total de la Orden de Compra.</b>");
				}

				// Esta validacion se lleva a nivel de concepto de MB
				// Ultima validacion, no puedes pagar mas de la OC
				//				if (totalMBAntProv > bmoRequisition.getTotal().toDouble())
				//					bmUpdateResult.addError(bmoRequisition.getTotal().getName(), "<b>Los Pagos exceden el total de la Orden de Compra.</b>");
			}	
		}

		// Si no hay saldo, el estatus es pagado
		else if (totalBalance == 0 && bmoRequisition.getTotal().toDouble() > 0) {
			printDevLog("pago total");
			bmoRequisition.getPaymentStatus().setValue(BmoRequisition.PAYMENTSTATUS_TOTAL);

			//Actualizar el status de pago de la estimacion
			if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)
					&& bmoRequisition.getContractEstimationId().toInteger() > 0) {
				PmContractEstimation pmContractEstimation = new PmContractEstimation(getSFParams());
				BmoContractEstimation bmoContractEstimation = (BmoContractEstimation)pmContractEstimation.get(pmConn, bmoRequisition.getContractEstimationId().toInteger());

				bmoContractEstimation.getPaymentStatus().setValue(BmoContractEstimation.PAYMENTSTATUS_TOTAL);
				super.save(pmConn, bmoContractEstimation, bmUpdateResult);
			}
		} else {
			printDevLog("pago pendiente");
			bmoRequisition.getPaymentStatus().setValue(BmoRequisition.PAYMENTSTATUS_PENDING);

			//Actualizar el status de pago de la estimacion
			if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_CONTRACTESTIMATION)
					&& bmoRequisition.getContractEstimationId().toInteger() > 0) {
				PmContractEstimation pmContractEstimation = new PmContractEstimation(getSFParams());
				BmoContractEstimation bmoContractEstimation = (BmoContractEstimation)pmContractEstimation.get(pmConn, bmoRequisition.getContractEstimationId().toInteger());

				bmoContractEstimation.getPaymentStatus().setValue(BmoContractEstimation.PAYMENTSTATUS_PENDING);
				super.save(pmConn, bmoContractEstimation, bmUpdateResult);
			}
		}		

		// Almacena cambios
		super.save(pmConn, bmoRequisition, bmUpdateResult);
	}

	// Crear bitacora cuando se MODIFICA el saldo de la OC
	public void createWFloLogRequisition(PmConn pmConn, BmoRequisition bmoRequisition, String comments, BmUpdateResult bmUpdateResult) throws SFException{
		// Obtener la OC con valores pasados
		PmRequisition pmRequisitionPrev = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisitionPrev = (BmoRequisition)pmRequisitionPrev.get(bmoRequisition.getId());
		printDevLog("\n \n Saldo anterior: "+bmoRequisitionPrev.getBalance().toDouble());
		printDevLog(" Saldo nuevo: "+bmoRequisition.getBalance().toDouble());

		// Generar bitacora en la OC si cambia el saldo
		if (bmoRequisitionPrev.getBalance().toDouble() != bmoRequisition.getBalance().toDouble()) {			
			printDevLog("Generar Bitacora de modificacion \n \n");
			addWFlowLog(pmConn, bmoRequisition, comments, bmUpdateResult);
		}			
	}

	// Genera automáticamente el Recibo de Orden de Compra y la Cuenta x Pagar
	public void autoReceiveAndCreatePaccounts(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {

		// Crear y obtener el recibo de orden de compra, y autorecibirlo
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		BmoRequisitionReceipt bmoRequisitionReceipt = pmRequisitionReceipt.createFromRequisition(pmConn, bmoRequisition, bmUpdateResult);

		// Crear las Cuentas x Pagar
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		pmPaccount.createFromRequisitionReceipt(pmConn, bmoRequisitionReceipt, bmUpdateResult);
	}

	// Genera automáticamente el Recibo de Orden de Compra y la Cuenta x Pagar
	public void holdAutoReceiveAndCreatePaccounts(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {

		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		PmPaccount pmPaccount = new PmPaccount(getSFParams());

		// Revisar si las CxP ya estan autorizadas
		if (!pmPaccount.requisitionHasAuthorizedPaccounts(pmConn, bmoRequisition.getId(), bmUpdateResult)) {

			// Las CxP no estan autorizadas, por lo tanto procede a eliminar recibos
			pmRequisitionReceipt.deleteByRequisition(pmConn, bmoRequisition, bmUpdateResult);

			// Cambiar estatus a O.C.
			bmoRequisition.getStatus().setValue(BmoRequisition.STATUS_REVISION);
			super.saveSimple(pmConn, bmoRequisition, bmUpdateResult);
		}
	}

	// Genera automáticamente el Recibo de Orden de Compra y la Cuenta x Pagar
	public void cancelAutoReceiveAndCreatePaccounts(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {

		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());
		PmPaccount pmPaccount = new PmPaccount(getSFParams());

		// Revisar si las CxP ya estan autorizadas
		if (pmPaccount.requisitionHasAuthorizedPaccounts(pmConn, bmoRequisition.getId(), bmUpdateResult)) {

			System.out.println("Ya estan autorizadas, genera nota de credito");

			// Ya estan autorizadas, genera la devolución de la orden de compra
			BmoRequisitionReceipt bmoRequisitionReceipt = pmRequisitionReceipt.createReturnFromRequisition(pmConn, bmoRequisition, bmUpdateResult);

			// Crear las Cuentas x Pagar ligadas a la devolucion
			pmPaccount.createCreditNoteFromRequisitionReceiptReturn(pmConn, bmoRequisitionReceipt, bmUpdateResult);

		} else {
			System.out.println("Esta por eliminar una recepción de de oc");

			// Las CxP no estan autorizadas, por lo tanto procede a eliminar recibos
			pmRequisitionReceipt.deleteByRequisition(pmConn, bmoRequisition, bmUpdateResult);

			// Cambiar estatus a O.C.
			bmoRequisition.getStatus().setValue(BmoRequisition.STATUS_REVISION);
			super.saveSimple(pmConn, bmoRequisition, bmUpdateResult);

			this.delete(pmConn, bmoRequisition, bmUpdateResult);
		}
	}

	// Obtiene la orden de compra segun pedido y comision
	public int getRequisitionIdByOrderAndCommissionAmount(PmConn pmConn, int orderId, int orderCommissionAmountId) throws SFException {
		int requisitionId = -1;
		pmConn.doFetch("SELECT reqi_requisitionid FROM requisitions "
				+ " WHERE reqi_orderid = " + orderId + " "
				+ " AND reqi_ordercommissionamountid = " + orderCommissionAmountId);
		if (pmConn.next())
			requisitionId = pmConn.getInt("reqi_requisitionid");

		return requisitionId;
	}

	private void addDataLog(PmConn pmConn, BmoRequisition bmoRequisition, BmoOrder bmoOrder, String comment, BmUpdateResult bmUpdateResult) throws SFException {		
		// Obtener orden de compra
		String url = GwtUtil.getProperUrl(getSFParams(), "frm/flex_requisition.jsp" + "?h=" + new Date().getTime() + "format&w=EXT&z=" + 
				GwtUtil.encryptId(bmoRequisition.getId()) + "&resource=oppo" + (new Date().getTime() * 456+"&log=true"));

		
		String data = SFServerUtil.fetchUrlToString(url);
//		String data = "";
		PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());

		// Generar bitacora con documento en pedido y OC, sino  solo bitacora
		try {
			if (bmoOrder.getId() > 0 ) {
				pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_DATA, 
						comment + ", Valor Total: " 
								+ SFServerUtil.formatCurrency(bmoRequisition.getTotal().toDouble()) + " " + bmoRequisition.getBmoCurrency().getCode().toString(), 
								data);
			}
			pmWFlowLog.addDataLog(pmConn, bmUpdateResult, bmoRequisition.getWFlowId().toInteger(), BmoWFlowLog.TYPE_DATA, 
					comment + ", Valor Total: "
							+ SFServerUtil.formatCurrency(bmoRequisition.getTotal().toDouble()) + " " + bmoRequisition.getBmoCurrency().getCode().toString(), 
							data);

		} catch (SFException e) {
			printDevLog("No se genero el documento de la oc " + bmoRequisition.getCode().toString() + " -  ERROR: " + e);
			if (bmoOrder.getId() > 0 ) {
				pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
						comment + ", (" + bmoRequisition.getCode().toString() + ") Valor Total: "
								+ SFServerUtil.formatCurrency(bmoRequisition.getTotal().toDouble()) + " " + bmoRequisition.getBmoCurrency().getCode().toString());
			}
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoRequisition.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
					comment + ", (" + bmoRequisition.getCode().toString() + ") Valor Total: " 
							+ SFServerUtil.formatCurrency(bmoRequisition.getTotal().toDouble()) + " " + bmoRequisition.getBmoCurrency().getCode().toString());
		}
	}

	// Actualiza la partida en ROC
	public void updateBudgetItem(PmConn pmConn, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		PmRequisitionReceipt pmRequisitionReceipt = new PmRequisitionReceipt(getSFParams());

		//Actualizar en CXP
		pmRequisitionReceipt.updateBudgetItem(pmConn, bmoRequisition, bmUpdateResult);
	}

	// Enviar correo solicitando autorizacion
	public void sendMailRequestAuthorize(PmConn pmConn, BmoRequisition bmoRequisition) throws SFException {
		// Solicitado por
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoRequestedByUser = (BmoUser)pmUser.get(pmConn, bmoRequisition.getRequestedBy().toInteger());

		// Usuario que autoriza
		PmArea pmArea = new PmArea(getSFParams());
		BmoArea bmoArea = (BmoArea)pmArea.get(pmConn, bmoRequisition.getAreaId().toInteger());
		if (bmoArea.getUserId().toInteger() > 0) {

			BmoUser bmoAuthorizeByUser = (BmoUser)pmUser.get(pmConn, bmoArea.getUserId().toInteger());

			// Validar si el usuario esta activo
			if (bmoAuthorizeByUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {

				// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar notificacion
				ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();
				mailList.add(new SFMailAddress(bmoAuthorizeByUser.getEmail().toString(), bmoAuthorizeByUser.getCode().toString()));

				// Cadena de Items de la orden de compra
				String itemString = "";
				PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());
				BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
				BmFilter filterByRequisition = new BmFilter();
				filterByRequisition.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(), bmoRequisition.getId());
				Iterator<BmObject> itemIterator = pmRequisitionItem.list(pmConn, filterByRequisition).iterator();
				itemString += "<table style=\"font-size:12px\" border=\"0\" width=\"100%\" cellspacing=\\\"0\\\" cellpading=\"0\">";
				itemString += "<tr>";
				itemString += 	"<th>Cantidad</th>" 
						+ "<th>Nombre</th>" 
						+ "<th>Precio</th>"
						+ "<th>Monto</th>";
				itemString += "</tr>";
				while (itemIterator.hasNext()) {
					bmoRequisitionItem = (BmoRequisitionItem)itemIterator.next();
					itemString += "<tr>";
					itemString += "<td>" + bmoRequisitionItem.getQuantity().toString() + "</td>" 
							+ "<td>" + bmoRequisitionItem.getName().toString()  + "</td>" 
							+ "<td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisitionItem.getPrice().toDouble()) + "</td>"
							+ "<td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisitionItem.getAmount().toDouble()) + "</td>";
					itemString += "</tr>";
				}
				itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>SubTotal:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getAmount().toDouble()) + "</td></tr>";
				itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>Descuento:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getDiscount().toDouble()) + "</td></tr>";
				itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>IVA:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getTax().toDouble()) + "</td></tr>";
				itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>Total:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getTotal().toDouble()) + "</td></tr>";
				itemString += "</table>";

				// Cadena de Archivos ligados
				String fileString = "";
				PmFile pmFile = new PmFile(getSFParams());
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				BmoFile bmoFile = new BmoFile();
				// Filtrar por módulo que llama a los archivos
				PmProgram pmProgram = new PmProgram(getSFParams());
				BmoProgram bmoProgram = new BmoProgram();
				bmoProgram = (BmoProgram)pmProgram.getBy(bmoRequisition.getProgramCode(), bmoProgram.getCode().getName());
				BmFilter filterPrograms = new BmFilter();
				filterPrograms.setValueFilter(bmoFile.getKind(), bmoFile.getProgramId().getName(), bmoProgram.getId());
				filterList.add(filterPrograms);
				// Filtrar por id 
				BmFilter filterForeign = new BmFilter();
				filterForeign.setValueFilter(bmoFile.getKind(), bmoFile.getForeignId().getName(), bmoRequisition.getId());
				filterList.add(filterForeign);
				Iterator<BmObject> fileIterator = pmFile.list(pmConn, filterList).iterator();
				while (fileIterator.hasNext()) {
					bmoFile = (BmoFile)fileIterator.next();

					String link = HtmlUtil.parseImageLink(getSFParams(), bmoFile.getBlobkey());

					fileString +=  "<li><a target=\"_blank\" href=\"" + link + "\">" + bmoFile.getName().toString() + "</a></li>";
				}

				// Acciones
				String actionString = "<table border=\"0\" width=\"100%\" cellspacing=\\\"0\\\" cellpading=\"0\">";
				actionString += "<tr>";
				actionString += "<td width=\"25%\" style=\"white-space:nowrap;\">"
						+ "<a target=\"_blank\" href=\"" + GwtUtil.getProperUrl(getSFParams(), "start.jsp?startprogram=" + bmoRequisition.getProgramCode() + "&foreignid=" + bmoRequisition.getId()) + "\">Abrir O.C.</a>"
						+ "</td>";
				actionString += "<td width=\"25%\" style=\"white-space:nowrap;\">"
						+ "<a target=\"_blank\" href=\"" + GwtUtil.getProperUrl(getSFParams(), "/jsp/op/reqi_status.jsp?h=" + new Date().getTime() + "reqi&w=EXT&z=" + SFServerUtil.encryptId(bmoRequisition.getId()) + "&v=" + BmoRequisition.STATUS_AUTHORIZED + "&r=reqi" + (new Date().getTime() * 4546)) + "\">Autorizar</a>"
						+ "</td>"; 
				actionString += "<td width=\"25%\" style=\"white-space:nowrap;\">"
						+ "<a target=\"_blank\" href=\"" + GwtUtil.getProperUrl(getSFParams(), "/jsp/op/reqi_status.jsp?h=" + new Date().getTime() + "reqi&w=EXT&z=" + SFServerUtil.encryptId(bmoRequisition.getId()) + "&v=" + BmoRequisition.STATUS_EDITION + "&r=reqi" + (new Date().getTime() * 4456)) + "\">Editar</a>"
						+ "</td>"; 
				actionString += "<td width=\"25%\" style=\"white-space:nowrap;\">"
						+ "<a target=\"_blank\" href=\"" + GwtUtil.getProperUrl(getSFParams(), "/jsp/op/reqi_status.jsp?h=" + new Date().getTime() + "reqi&w=EXT&z=" + SFServerUtil.encryptId(bmoRequisition.getId()) + "&v=" + BmoRequisition.STATUS_CANCELLED + "&r=reqi" + (new Date().getTime() * 4556)) + "\">Cancelar</a>"
						+ "</td>"; 
				actionString += "</tr>";
				actionString += "</table>";

				String subject = getSFParams().getAppCode() 
						+ " Solicitud de Autorización de Orden de Compra " + bmoRequisition.getCode().toString() 
						+ bmoRequisition.getCode().toString() 
						+ " " + bmoRequisition.getName().toString();

				String msgBody = HtmlUtil.mailBodyFormat(
						getSFParams(), 
						"Solicitud de Autorización de Orden de Compra: ", 
						" 	<p style=\"font-size:12px\"> "
								+ " <b>Orden de Compra: " + bmoRequisition.getCode().toString() + "</b> " + bmoRequisition.getName().toString()
								+ "	<b>Descripcion:</b> " + bmoRequisition.getDescription().toString()
								+ "	<b>Tipo:</b> " + bmoRequisition.getBmoRequisitionType().getName().toString()
								+ "	<b>Proveedor:</b> " + bmoRequisition.getBmoSupplier().getName().toString()  
								+ "	<b>Solicitado Por:</b> " + bmoRequestedByUser.getFirstname().toString()
								+ " " + bmoRequestedByUser.getFatherlastname().toString()
								+ " " + bmoRequestedByUser.getMotherlastname().toString()
								+ "<br>"
								+ "<br>"
								+ "<b>Items:</b>"
								+ itemString
								+ "<br><br>"
								+ "<b>Archivos:</b> "
								+ "<br>"
								+ fileString
								+ "<br>"
								+ "<br>"
								+ "<b>Acciones:</b> "
								+ "<br>"
								+ actionString
								+ "<br>"
						);

				SFSendMail.send(getSFParams(),
						mailList,  
						getSFParams().getBmoSFConfig().getEmail().toString(), 
						getSFParams().getBmoSFConfig().getAppTitle().toString(), 
						subject, 
						msgBody);
			}
		} else {
			printDevLog("El Responsable del Área " + bmoArea.getName().toString() + " no está asignado: no se enviará Email.");
		}
	}

	// Enviar correo avisando de autorizacion
	public void sendMailAuthorized(PmConn pmConn, BmoRequisition bmoRequisition) throws SFException {

		// Solicitado por
		PmUser pmUser = new PmUser(getSFParams());
		BmoUser bmoRequestedByUser = (BmoUser)pmUser.get(pmConn, bmoRequisition.getRequestedBy().toInteger());

		// Usuario que autorizó
		BmoUser bmoAuthorizeByUser = (BmoUser)pmUser.get(pmConn, bmoRequisition.getAuthorizedUser().toInteger());

		// Lista de correos del grupo del Tipo de Pedido, que se le va a enviar notificacion
		ArrayList<SFMailAddress> mailList = new ArrayList<SFMailAddress>();

		// Validar si el usuario esta activo
		if (bmoRequestedByUser.getStatus().toChar() == BmoUser.STATUS_ACTIVE) {

			mailList.add(new SFMailAddress(bmoRequestedByUser.getEmail().toString(), bmoRequestedByUser.getCode().toString()));

			// Cadena de Items de la orden de compra
			String itemString = "";
			PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());
			BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
			BmFilter filterByRequisition = new BmFilter();
			filterByRequisition.setValueFilter(bmoRequisitionItem.getKind(), bmoRequisitionItem.getRequisitionId(), bmoRequisition.getId());
			Iterator<BmObject> itemIterator = pmRequisitionItem.list(pmConn, filterByRequisition).iterator();
			itemString += "<table style=\"font-size:12px\" border=\"0\" width=\"100%\" cellspacing=\\\"0\\\" cellpading=\"0\">";
			itemString += "<tr>";
			itemString += 	"<th>Cantidad</th>" 
					+ "<th>Nombre</th>" 
					+ "<th>Precio</th>"
					+ "<th>Monto</th>";
			itemString += "</tr>";
			while (itemIterator.hasNext()) {
				bmoRequisitionItem = (BmoRequisitionItem)itemIterator.next();
				itemString += "<tr>";
				itemString += "<td>" + bmoRequisitionItem.getQuantity().toString() + "</td>" 
						+ "<td>" + bmoRequisitionItem.getName().toString()  + "</td>" 
						+ "<td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisitionItem.getPrice().toDouble()) + "</td>"
						+ "<td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisitionItem.getAmount().toDouble()) + "</td>";
				itemString += "</tr>";
			}
			itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>SubTotal:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getAmount().toDouble()) + "</td></tr>";
			itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>Descuento:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getDiscount().toDouble()) + "</td></tr>";
			itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>IVA:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getTax().toDouble()) + "</td></tr>";
			itemString += "<tr><td>&nbsp;</td><td>&nbsp;</td><td><b>Total:</b></td><td align=\"right\">" + SFServerUtil.formatCurrency(bmoRequisition.getTotal().toDouble()) + "</td></tr>";
			itemString += "</table>";

			// Cadena de Archivos ligados
			String fileString = "";
			PmFile pmFile = new PmFile(getSFParams());
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
			BmoFile bmoFile = new BmoFile();
			// Filtrar por módulo que llama a los archivos
			PmProgram pmProgram = new PmProgram(getSFParams());
			BmoProgram bmoProgram = new BmoProgram();
			bmoProgram = (BmoProgram)pmProgram.getBy(bmoRequisition.getProgramCode(), bmoProgram.getCode().getName());
			BmFilter filterPrograms = new BmFilter();
			filterPrograms.setValueFilter(bmoFile.getKind(), bmoFile.getProgramId().getName(), bmoProgram.getId());
			filterList.add(filterPrograms);
			// Filtrar por id 
			BmFilter filterForeign = new BmFilter();
			filterForeign.setValueFilter(bmoFile.getKind(), bmoFile.getForeignId().getName(), bmoRequisition.getId());
			filterList.add(filterForeign);
			Iterator<BmObject> fileIterator = pmFile.list(pmConn, filterList).iterator();
			while (fileIterator.hasNext()) {
				bmoFile = (BmoFile)fileIterator.next();

				String link = HtmlUtil.parseImageLink(getSFParams(), bmoFile.getBlobkey());

				fileString +=  "<li><a target=\"_blank\" href=\"" + link + "\">" + bmoFile.getName().toString() + "</a></li>";
			}

			// Acciones
			String actionString = "<table border=\"0\" width=\"100%\" cellspacing=\\\"0\\\" cellpading=\"0\">";
			actionString += "<tr>";
			actionString += "<td width=\"100%\" align=\"center\" style=\"white-space:nowrap;\">"
					+ "<a target=\"_blank\" href=\"" + GwtUtil.getProperUrl(getSFParams(), "start.jsp?startprogram=" + bmoRequisition.getProgramCode() + "&foreignid=" + bmoRequisition.getId()) + "\">Abrir O.C.</a>"
					+ "</td>";
			actionString += "</tr>";
			actionString += "</table>";

			String subject = getSFParams().getAppCode() 
					+ " Notificación de Autorización de Orden de Compra: " 
					+ bmoRequisition.getCode().toString() 
					+ " " + bmoRequisition.getName().toString();

			String msgBody = HtmlUtil.mailBodyFormat(
					getSFParams(), 
					" Notificación de Autorización de Orden de Compra " + bmoRequisition.getCode().toString(), 
					" 	<p style=\"font-size:12px\"> "
							+ " Se Autorizó la Orden de Compra: <b>" + bmoRequisition.getCode().toString() + "</b> " + bmoRequisition.getName().toString()
							+ "	<b>Descripcion:</b> " + bmoRequisition.getDescription().toString()
							+ "	<b>Tipo:</b> " + bmoRequisition.getBmoRequisitionType().getName().toString()
							+ "	<b>Proveedor:</b> " +  bmoRequisition.getBmoSupplier().getName().toString()  
							+ "	<b>Autorizado Por:</b> " + bmoAuthorizeByUser.getFirstname().toString()
							+ " " + bmoAuthorizeByUser.getFatherlastname().toString()
							+ " " + bmoAuthorizeByUser.getMotherlastname().toString()
							+ "<br>"
							+ "<br>"
							+ "<b>Items:</b>"
							+ itemString
							+ "<br><br>"
							+ "<b>Archivos:</b> "
							+ "<br>"
							+ fileString
							+ "<br>"
							+ "<br>"
							+ "<b>Acciones:</b> "
							+ "<br>"
							+ actionString
							+ "<br>"
					);

			SFSendMail.send(getSFParams(),
					mailList,  
					getSFParams().getBmoSFConfig().getEmail().toString(), 
					getSFParams().getBmoSFConfig().getAppTitle().toString(), 
					subject, 
					msgBody);
		}
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRequisition = (BmoRequisition)bmObject;

		if (bmoRequisition.getStatus().toChar() == BmoRequisition.STATUS_REVISION ||
				bmoRequisition.getStatus().toChar() == BmoRequisition.STATUS_EDITION) {
			// Si proviene de un proyecto, validar estatus
			if (bmoRequisition.getOrderId().toInteger() > 0) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = new BmoOrder();
				bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoRequisition.getOrderId().toInteger());

				if (bmoOrder.getStatus().toChar() != BmoOrder.STATUS_REVISION)
					bmUpdateResult.addError(bmoRequisition.getCode().getName(), "No se puede Eliminar La Orden de Compra: el Pedido No está en Revisión.");

				// Genera bitácora de la eliminacion de la orden de compra
				addDataLog(pmConn, bmoRequisition, bmoOrder, "Orden de Compra Eliminada.", bmUpdateResult);	

				// Elimina los items de la OC
				PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());
				pmRequisitionItem.deleteItemsByRequisition(pmConn, bmoRequisition);

				// Elimina la O.C.
				super.delete(pmConn, bmoRequisition, bmUpdateResult);

				// Eliminar flujos
				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				BmoWFlow bmoWFlow = new BmoWFlow();
				BmFilter filterByReqi = new BmFilter();
				filterByReqi.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoRequisition.getId());			
				BmFilter filterByProgram = new BmFilter();
				filterByProgram.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoRequisition.getProgramCode());
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				filterList.add(filterByReqi);
				filterList.add(filterByProgram);
				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
				while (wFlowList.hasNext()) {
					bmoWFlow = (BmoWFlow)wFlowList.next();
					pmWFlow.delete(pmConn, bmoWFlow, bmUpdateResult);
				}

				// Refresca el estatus del pedido, en cuanto a bloqueos, guardando el pedido
				pmOrder.updateLockStatus(pmConn, bmoOrder, bmUpdateResult);

			} else {

				// Sin efectos adicionales, elimina la OC
				pmConn.doUpdate("DELETE FROM requisitionitems WHERE rqit_requisitionid = " + bmoRequisition.getId());
				super.delete(pmConn, bmoRequisition, bmUpdateResult);

				// Eliminar flujos
				PmWFlow pmWFlow = new PmWFlow(getSFParams());
				BmoWFlow bmoWFlow = new BmoWFlow();
				BmFilter filterByReqi = new BmFilter();
				filterByReqi.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerId(), bmoRequisition.getId());			
				BmFilter filterByProgram = new BmFilter();
				filterByProgram.setValueFilter(bmoWFlow.getKind(), bmoWFlow.getCallerCode(), bmoRequisition.getProgramCode());
				ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();
				filterList.add(filterByReqi);
				filterList.add(filterByProgram);
				ListIterator<BmObject> wFlowList = pmWFlow.list(filterList).listIterator();
				while (wFlowList.hasNext()) {
					bmoWFlow = (BmoWFlow)wFlowList.next();
					pmWFlow.delete(pmConn, bmoWFlow, bmUpdateResult);
				}
			}
		} else {
			bmUpdateResult.addMsg("No se puede Eliminar la Orden de Compra. Ya está Autorizada.");
		}

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (bmoRequisition.getBudgetItemId().toInteger() > 0) {
				//Calcular los saldos del presupuesto
				PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
				BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoRequisition.getBudgetItemId().toInteger());

				pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
			}
		}	

		return bmUpdateResult;
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
