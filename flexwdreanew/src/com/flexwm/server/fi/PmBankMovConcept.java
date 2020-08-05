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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import com.flexwm.server.op.PmOrder;
import com.flexwm.server.op.PmOrderDelivery;
import com.flexwm.server.op.PmRequisition;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.fi.BmoBankAccount;
import com.flexwm.shared.fi.BmoBankMovConcept;
import com.flexwm.shared.fi.BmoBankMovType;
import com.flexwm.shared.fi.BmoBankMovement;
import com.flexwm.shared.fi.BmoBudgetItem;
import com.flexwm.shared.fi.BmoCurrency;
import com.flexwm.shared.fi.BmoLoanDisbursement;
import com.flexwm.shared.fi.BmoLoanPayment;
import com.flexwm.shared.fi.BmoPaccount;
import com.flexwm.shared.fi.BmoPaccountItem;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountType;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDelivery;
import com.flexwm.shared.op.BmoOrderType;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionType;
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


public class PmBankMovConcept extends PmObject {
	BmoBankMovConcept bmoBankMovConcept;

	public PmBankMovConcept(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoBankMovConcept = new BmoBankMovConcept();
		setBmObject(bmoBankMovConcept);

		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoBankMovConcept.getBankMovementId(), bmoBankMovConcept.getBmoBankMovement()),
				new PmJoin(bmoBankMovConcept.getRaccountId(), bmoBankMovConcept.getBmoRaccount()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBankAccountId(), bmoBankMovConcept.getBmoBankMovement().getBmoBankAccount()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoBankAccount().getCompanyId(), bmoBankMovConcept.getBmoBankMovement().getBmoBankAccount().getBmoCompany()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoBankAccount().getCurrencyId(),	bmoBankMovConcept.getBmoBankMovement().getBmoBankAccount().getBmoCurrency()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoBankAccount().getCompanyId(), bmoBankMovConcept.getBmoBankMovement().getBmoBankAccount().getBmoBankAccountType()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBankMovTypeId(), bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType().getPaccountTypeId(), bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType().getBmoPaccountType()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType().getRaccountTypeId(), bmoBankMovConcept.getBmoBankMovement().getBmoBankMovType().getBmoRaccountType()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getSupplierId(), bmoBankMovConcept.getBmoBankMovement().getBmoSupplier()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoSupplier().getSupplierCategoryId(), bmoBankMovConcept.getBmoBankMovement().getBmoSupplier().getBmoSupplierCategory()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getCustomerId(), bmoBankMovConcept.getBmoBankMovement().getBmoCustomer()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getSalesmanId(), bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getBmoUser().getAreaId(), bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getBmoUser().getLocationId(), bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getTerritoryId(), bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getReqPayTypeId(), bmoBankMovConcept.getBmoBankMovement().getBmoCustomer().getBmoReqPayType()))));

	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept) autoPopulate(pmConn, new BmoBankMovConcept());

		// BmoBankMovement
		BmoBankMovement bmoBankMovement = new BmoBankMovement();
		if (pmConn.getInt(bmoBankMovement.getIdFieldName()) > 0) 
			bmoBankMovConcept.setBmoBankMovement((BmoBankMovement) new PmBankMovement(getSFParams()).populate(pmConn));
		else
			bmoBankMovConcept.setBmoBankMovement(bmoBankMovement);

		BmoRaccount bmoRaccount = new BmoRaccount();
		if (pmConn.getInt(bmoRaccount.getIdFieldName()) > 0) 
			bmoBankMovConcept.setBmoRaccount((BmoRaccount) new PmRaccount(getSFParams()).populate(pmConn));
		else
			bmoBankMovConcept.setBmoRaccount(bmoRaccount);

		return bmoBankMovConcept;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept)bmObject;

		// Obtiene el movimiento de banco
		PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
		BmoBankMovement bmoBankMovement = new BmoBankMovement();
		bmoBankMovement = (BmoBankMovement) pmBankMovement.get(pmConn, bmoBankMovConcept.getBankMovementId().toInteger());

		// Obtiene el tipo de movimiento
		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = new BmoBankMovType();
		bmoBankMovType = (BmoBankMovType) pmBankMovType.get(pmConn, bmoBankMovement.getBankMovTypeId().toInteger());

		// Se guarda para generar ID
		if (!(bmoBankMovConcept.getId() > 0)) {
			// Es un registro nuevo, guardar una vez para recuperar ID, luego
			super.save(pmConn, bmoBankMovConcept, bmUpdateResult);
			bmoBankMovConcept.setId(bmUpdateResult.getId());
		}

		// Valida el valor del concepto de movimiento
		if (!(bmoBankMovConcept.getAmount().toDouble() > 0))
			bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "El monto deber ser mayor a $0.00");
		//		else {
		//			// Si de tipo Anticipo Proveedor, valida que no sea mayor al monto de banco
		//			if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)
		//					&& bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
		//				if (bmoBankMovConcept.getAmount().toDouble() > bmoBankMovement.getWithdraw().toDouble())
		//					bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "El Monto No Debe Ser Mayor a $ " + bmoBankMovement.getWithdraw().toDouble());
		//			} 
		//		}

		// Revisar que no este cancelado y que no haya errores
		if (!bmoBankMovement.getStatus().equals(BmoBankMovement.STATUS_CANCELLED) && !bmUpdateResult.hasErrors()) {

			// Es de tipo Cuenta x Pagar
			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CXP) 
					|| bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				saveRequisitionConcept(pmConn, bmoBankMovement, bmoBankMovType, bmoBankMovConcept, bmUpdateResult);
			}
			// Es de tipo Cuenta x Cobrar
			else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CXC) ||
					bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC) ||
					bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_MULTIPLECXC)) {
				saveRaccountConcept(pmConn, bmoBankMovement, bmoBankMovType, bmoBankMovConcept, bmUpdateResult);
			} 
			// Categoria de disposicion libre
			else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DISPOSALFREE)) {
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					if (bmoBankMovement.getBudgetItemId().toInteger() > 0) {
						if (bmoBankMovConcept.getBudgetItemId().toInteger() > 0) {
							PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
							BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());
							pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
						}
					}
				}
			} 
			// Categoria disposicion de credito
			else if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_CREDITDISPOSAL)) {
				// Actualizar el estatus
				PmOrderDelivery pmOrderDelivery = new PmOrderDelivery(getSFParams());
				BmoOrderDelivery bmoOrderDelivery = (BmoOrderDelivery)pmOrderDelivery.get(pmConn, bmoBankMovConcept.getOrderDeliveryId().toInteger());
				bmoBankMovConcept.getCode().setValue(bmoOrderDelivery.getName().toString());
			}
		}

		// Continua si no hay errores
		if (!bmUpdateResult.hasErrors()) {
			super.save(pmConn, bmoBankMovConcept, bmUpdateResult);

			// Actualizar la cantidad en movimiento bancario		
			//			if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
			//				if (bmoBankMovement.getBmoBankMovType().getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
			//					updateBkacBalanceByConcept(pmConn, bmoBankMovConcept, bmoBankMovement, bmUpdateResult);
			//				}
			//			} else {
			updateBalanceBankAccount(pmConn, bmoBankMovConcept, bmoBankMovement, bmUpdateResult);
			//			}

			// Actualiza monto de movimiento bancario
			pmBankMovement.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);

			// La ultima vez que se guarda debe ser el mismo objeto
			super.save(pmConn, bmoBankMovConcept, bmUpdateResult);
		}

		return bmUpdateResult;
	}

	// Tipo Cuentas por Pagar
	private void saveRequisitionConcept(PmConn pmConn, BmoBankMovement bmoBankMovement, BmoBankMovType bmoBankMovType, BmoBankMovConcept bmoBankMovConcept, BmUpdateResult bmUpdateResult) throws SFException {
		// Movimientos Retiro CxC
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
			String reqiCode = "";

			// De anticipo a OC
			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				if (!(bmoBankMovConcept.getRequisitionId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoBankMovConcept.getRequisitionId().getName(), "Debe Seleccionar una O.C.");
				}

				if (!(bmoBankMovConcept.getAmount().toDouble() > 0))
					bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "Debe capturar el monto.");

				PmRequisition pmRequisition = new PmRequisition(getSFParams());
				BmoRequisition bmoRequisition = (BmoRequisition) pmRequisition.get(pmConn, bmoBankMovConcept.getRequisitionId().toInteger());

				bmoBankMovConcept.getCode().setValue(bmoRequisition.getCode().toString());

				// Ultima validacion, no puedes pagar mas de la OC
				if (bmoBankMovConcept.getAmount().toDouble() > bmoRequisition.getTotal().toDouble())
					bmUpdateResult.addError(bmoRequisition.getTotal().getName(), "<b>Los Pagos exceden el total de la Orden de Compra.</b>");


				// Calcular montos
				pmRequisition.updateBalance(pmConn, bmoRequisition, bmUpdateResult);

				// Actualizar pagos a la OC
				pmRequisition.updatePayments(pmConn, bmoRequisition, bmUpdateResult);

				// Actualiza el estatus de recepcion de la orden de compra
				pmRequisition.updateDeliveryStatus(pmConn, bmoRequisition, bmUpdateResult);

				// Crear bitacora en OC
				pmRequisition.createWFloLogRequisition(pmConn, bmoRequisition, "Modificó el pago de ", bmUpdateResult);

				// Calcular la partida presupuestal
				if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
					bmoBankMovConcept.getBudgetItemId().setValue(bmoRequisition.getBudgetItemId().toInteger());
					if (bmoBankMovConcept.getBudgetItemId().toInteger() > 0) {
						PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
						BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());
						printDevLog("Presp.|Partida: " +bmoBudgetItem.getBmoBudget().getName() + "|"+bmoBudgetItem.getBmoBudgetItemType().getName());
						pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
					}
				}

				// Guarda cambios
				super.save(pmConn, bmoBankMovConcept, bmUpdateResult);

				//Si la OC es de tipo crédito crear un pago al crédito
				if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_CREDIT)) {
					bmUpdateResult.addError(bmoBankMovConcept.getRequisitionId().getName(), "La O.C. de Tipo Crédito no acepta Pagos de Anticipos.");
				}	
			} else {
				// Valida seleccion de CxP
				if (!(bmoBankMovConcept.getPaccountId().toInteger() > 0)) {
					bmUpdateResult.addError(bmoBankMovConcept.getPaccountId().getName(), "Debe Seleccionar una CxP.");
				} 
				// Si esta seleccionada CxP
				else {
					// Valida saldo de banco segun configuracion
					if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getNegativeBankBalance().toBoolean()) {
						if (!bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
							if (!validBkacBalance(pmConn, bmoBankMovConcept, bmUpdateResult))
								bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "No cuenta con saldo suficiente en la Cuenta de Banco.");
						}	
					}

					PmRequisition pmRequisition = new PmRequisition(getSFParams());
					BmoRequisition bmoRequisition = new BmoRequisition();	

					// Obtener los Datos de la CxP original
					PmPaccount pmPaccount = new PmPaccount(getSFParams());
					BmoPaccount bmoPaccount = new BmoPaccount();			
					bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoBankMovConcept.getPaccountId().toInteger());
					bmoBankMovConcept.getCode().setValue(bmoPaccount.getInvoiceno().toString() + reqiCode);

					// saul: REVISAR SI APLICA ESTO
					if (!(bmoBankMovConcept.getCurrencyParity().toDouble() > 0)) {
						bmoBankMovConcept.getCurrencyParity().setValue(bmoPaccount.getCurrencyId().toDouble());
					}

					super.save(pmConn, bmoBankMovConcept, bmUpdateResult);

					// Validar la cantidad. Si en campo de cantidad esta vacio obtener la cantidad de la CxP
					double amountFromBankMovConcept = 0;

					if (!bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
						if (bmoBankMovConcept.getAmountCoverted().toDouble() > 0) {
							amountFromBankMovConcept = bmoBankMovConcept.getAmountCoverted().toDouble();
						} else if (bmoBankMovConcept.getAmount().toDouble() > 0) {
							amountFromBankMovConcept = bmoBankMovConcept.getAmount().toDouble();
						} else {	
							bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "Debe capturar un Monto.");
						}

						// Valida que el monto no sea mayor al saldo de la CxP
						if (bmoPaccount.getBalance().toDouble() < amountFromBankMovConcept)
							bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "<b>El monto a Pagar no puede ser superior al Saldo.</b>");
					} else {
						// Cuando viene de un anticipo la cxp, no validar que el monto no sea mayor al saldo de la CxP, 
						//ya que despues se le hace devolucion de prov.(viaticos)
						amountFromBankMovConcept = bmoBankMovConcept.getAmount().toDouble();
					}

					// Validar que el monto del concepto no sobrepase a los pagos de la OC
					if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0 ) {
						bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoPaccount.getRequisitionId().toInteger());

						//printDevLog("amountFromBankMovConcept: "+amountFromBankMovConcept);

						// Si el saldo de la OC						
						if (bmoRequisition.getPaymentStatus().equals(BmoRequisition.PAYMENTSTATUS_TOTAL))
							bmUpdateResult.addError(bmoPaccount.getPayments().getName(), "<b>La Orden de Compra de la CxP está pagada totalmente.</b>");
						else if (bmoRequisition.getPaymentStatus().equals(BmoRequisition.PAYMENTSTATUS_PENDING)) {
							if (amountFromBankMovConcept > bmoRequisition.getBalance().toDouble())
								bmUpdateResult.addError(bmoBankMovConcept.getPaccountId().getName(), "<b>El monto a Pagar no puede ser superior al saldo de la O.C.</b>");
						}
					}

					// Actualizar el saldo de la CxP
					pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);
					
					// Calcular la partida presupuestal
					if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
						bmoBankMovConcept.getBudgetItemId().setValue(bmoPaccount.getBudgetItemId().toInteger());

						if (bmoBankMovConcept.getBudgetItemId().toInteger() > 0) {
							PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
							BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());
							pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
						}
					}

					// Crear el pago en el crédito
					if (!bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
						// Obtener la OC y tomar el crédito
						if (bmoPaccount.getRequisitionReceiptId().toInteger() > 0 ) {
							bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoPaccount.getRequisitionId().toInteger());

							if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_CREDIT)) {
								//Relizar el pago
								BmoLoanPayment bmoLoanPayment = new BmoLoanPayment();
								PmLoanPayment pmLoanPayment = new PmLoanPayment(getSFParams());
								bmoLoanPayment.getLoanId().setValue(bmoRequisition.getLoanId().toInteger());
								bmoLoanPayment.getDate().setValue(bmoBankMovement.getDueDate().toString());
								bmoLoanPayment.getAmount().setValue(bmoBankMovConcept.getAmount().toDouble());
								bmoLoanPayment.getBankMovConceptId().setValue(bmoBankMovConcept.getId());
								pmLoanPayment.save(pmConn, bmoLoanPayment, bmUpdateResult);
							}
						}
					} 	
				}
			}
		} 
		// Movimientos de Abono CxP	
		else {
			if (!(bmoBankMovConcept.getPaccountId().toInteger() > 0))
				bmUpdateResult.addError(bmoBankMovConcept.getPaccountId().getName(), "Debe Seleccionar una CxP.");

			// Obtener los Datos de la CxP original
			PmPaccount pmPaccount = new PmPaccount(getSFParams());
			BmoPaccount bmoPaccount = new BmoPaccount();			
			bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, bmoBankMovConcept.getPaccountId().toInteger());

			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
				pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);	
			}

			bmoBankMovConcept.getBudgetItemId().setValue(bmoPaccount.getBudgetItemId().toInteger());

			bmoBankMovConcept.getCode().setValue(bmoPaccount.getInvoiceno().toString());

			// Calcular la partida
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (bmoPaccount.getBudgetItemId().toInteger() > 0) {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());
					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
		}
	}

	// Guarda concepto de cuentas x cobrar
	private void saveRaccountConcept(PmConn pmConn, BmoBankMovement bmoBankMovement, BmoBankMovType bmoBankMovType, BmoBankMovConcept bmoBankMovConcept, BmUpdateResult bmUpdateResult) throws SFException {
		if (!(bmoBankMovConcept.getRaccountId().toInteger() > 0))
			bmUpdateResult.addMsg("Debe seleccionar una CxC.");

		super.save(pmConn, bmoBankMovConcept, bmUpdateResult);

		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = new BmoRaccount();
		bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoBankMovConcept.getRaccountId().toInteger());

		// Colocar fecha de pago en bancos
		bmoRaccount.getPaymentDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));

		// Revisar si la CxC tiene bandera autocreate y lo quita
		if (bmoRaccount.getOrderId().toInteger() > 0 && bmoRaccount.getAutoCreate().toInteger() > 0) {
			bmoRaccount.getAutoCreate().setValue(0);					
			super.save(pmConn, bmoRaccount, bmUpdateResult);
		}

		// Validar la cantidad, si en campo de cantidad esta vacio obtener la cantidad de la CxC
		double amountFromBankMovConcept = 0;
		if (bmoBankMovConcept.getAmountCoverted().toDouble() > 0) {
			amountFromBankMovConcept = bmoBankMovConcept.getAmountCoverted().toDouble();
		} else if (bmoBankMovConcept.getAmount().toDouble() > 0) {
			amountFromBankMovConcept = bmoBankMovConcept.getAmount().toDouble();
		} else {	
			bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "Debe capturar un monto.");
		}

		// Revisa partida presupuestal y asigna valor
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (bmoRaccount.getBudgetItemId().toInteger() > 0) {						
				bmoBankMovConcept.getBudgetItemId().setValue(bmoRaccount.getBudgetItemId().toInteger());
			}
		}	

		// Calcular el saldo del CxC	
		if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
			// Obtiene el pedido
			if (bmoRaccount.getOrderId().toInteger() > 0) {
				PmOrder pmOrder = new PmOrder(getSFParams());
				BmoOrder bmoOrder = (BmoOrder)pmOrder.get(pmConn, bmoRaccount.getOrderId().toInteger());

				// Tipos de Pedido que no sean de tipo credito
				if (!bmoOrder.getBmoOrderType().getType().equals(BmoOrderType.TYPE_CREDIT)) {
					// Categoria de CxC nota de credito
					if (bmoRaccount.getBmoRaccountType().getCategory().equals(BmoRaccountType.CATEGORY_CREDITNOTE)) {
						// Si los pagos son menores a la devolucion, no permite pasar
						if ((bmoRaccount.getPayments().toDouble() + amountFromBankMovConcept) > bmoRaccount.getTotal().toDouble())
							bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "Los Pagos no pueden ser superiores al Total.");
					} 
					// Otras Categorias de CxC
					else	 {
						// Si los pagos son menores a la devolucion, no permite pasar
						if (bmoRaccount.getPayments().toDouble() < amountFromBankMovConcept)
							bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "El monto a devolver no puede ser superior a los Pagos.");
						else {
							if (bmoRaccount.getTotal().toDouble() < amountFromBankMovConcept)
								bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "El monto a devolver no puede ser superior al Total de la CxC.");
						}
					}
				}
			}					
		}

		pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
		// Obtener el id de la aplicacion				
		bmoBankMovConcept.getCode().setValue(bmoRaccount.getCode().toString());				

		// Calcular la partida presupuestal
		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (bmoRaccount.getBudgetItemId().toInteger() > 0) {
				PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
				BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());
				pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
			}
		}
	}

	// Actualiza balance de la cuenta de banco
	private void updateBalanceBankAccount(PmConn pmConn, BmObject bmObject, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept) bmObject;

		// Obtener Tipo de MB
		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = (BmoBankMovType)pmBankMovType.get(pmConn, bmoBankMovement.getBankMovTypeId().toInteger());
		
		// Obtener Cuenta de banco
		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
		BmoBankAccount bmoBankAccount = (BmoBankAccount)pmBankAccount.get(pmConn, bmoBankMovement.getBankAccountId().toInteger());

		String sql = "";
		double balanceNow = 0;
		double amountBkmc = 0;

		amountBkmc = bmoBankMovConcept.getAmount().toDouble();

		// Ya no toma en cuenta los anticipo a prov.(CON LIGA A CxP) porque ya fueron sacados en su momento
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)
			&& bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)
			&& bmoBankMovConcept.getRequisitionId().toInteger() > 0
			&& bmoBankMovConcept.getPaccountId().toInteger() > 0) {
			amountBkmc = 0;
		}
		else {
			amountBkmc = bmoBankMovConcept.getAmount().toDouble();
		}
		// Obtener los saldos de la cuenta de banco
		balanceNow = bmoBankAccount.getBalance().toDouble();

		//Validar que exista saldo en la cuenta de banco
		if (!((BmoFlexConfig)getSFParams().getBmoAppConfig()).getNegativeBankBalance().toBoolean()) {
			if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				if (!bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
					if (amountBkmc > balanceNow) {
						bmUpdateResult.addError(bmoBankMovConcept.getAmount().getName(), "No cuenta con saldo suficiente en la Cuenta de Banco.");
					}
				}
			}
		}

		// Actualizar el saldo en la cuenta de banco
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
			balanceNow += amountBkmc;
		} else {
			balanceNow -= amountBkmc;
		}

		// Actualizar el nuevo saldo
		sql = " UPDATE bankaccounts SET bkac_balance = " + SFServerUtil.roundCurrencyDecimals(balanceNow)
		+ " WHERE bkac_bankaccountid = " + bmoBankMovement.getBankAccountId().toInteger();
		pmConn.doUpdate(sql);
	}

	// Actualiza balance de cuenta de banco
	private void updateBalanceBankAccountDelete(PmConn pmConn, BmObject bmObject, BmoBankMovement bmoBankMovement, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept) bmObject;

		// Obtener Tipo de MB
		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = (BmoBankMovType)pmBankMovType.get(pmConn, bmoBankMovement.getBankMovTypeId().toInteger());

		// Obtener Cuenta de banco
		PmBankAccount pmBankAccount = new PmBankAccount(getSFParams());
		BmoBankAccount bmoBankAccount = (BmoBankAccount)pmBankAccount.get(pmConn, bmoBankMovement.getBankAccountId().toInteger());
				
		String sql = "";
		double balanceNow = 0;
		double amountBkmc = 0;

		amountBkmc = bmoBankMovConcept.getAmount().toDouble();

		// Obtener los saldos de la cuenta de banco
		balanceNow = bmoBankAccount.getBalance().toDouble();

		// Actualizar el saldo en la cuenta de banco
		if (bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT)) {
			balanceNow -= amountBkmc;
		} else {
			balanceNow += amountBkmc;
		}

		// Actualizar el nuevo saldo
		sql = " UPDATE bankaccounts SET bkac_balance = " + SFServerUtil.roundCurrencyDecimals(balanceNow)
		+ " WHERE bkac_bankaccountid = " + bmoBankAccount.getId();
		pmConn.doUpdate(sql);
	}

	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String data, String value) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		double result = 0;

		try {
			pmConn.open();
			// Obtener el saldo de la cuenta x pagar
			if (data.equals("" + BmoBankMovType.CATEGORY_CXP )) {
				result = getAmountInformationPaccount(pmConn, Integer.parseInt(value), bmUpdateResult);
			} else if (data.equals("" + BmoBankMovType.CATEGORY_CXC)) {
				result = getAmountInformationCXC(pmConn, Integer.parseInt(value), bmUpdateResult);				
			} else if (data.equals("" + BmoBankMovConcept.GETTOTAL)) {
				result = updateTotalConcepts(pmConn, Integer.parseInt(value), bmUpdateResult);
			} else if (data.equals("" + BmoBankMovConcept.GETPARITY)) {
				PmCurrency pmCurrency = new PmCurrency(getSFParams());
				BmoCurrency bmoCurrency = (BmoCurrency)pmCurrency.get(pmConn, Integer.parseInt(value));
				result = bmoCurrency.getParity().toDouble();
			} else if (data.equals("" + BmoBankMovConcept.ACTION_DELETEPACCOUNTID)) {


				PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
				BmoBankMovConcept bmoBankMovConcept = (BmoBankMovConcept)bmObject;
				BmoBankMovement bmoBankMovement = bmoBankMovConcept.getBmoBankMovement();

				// Guardar la cp y borrarla del CMB
				int paccountId = 0;
				paccountId = bmoBankMovConcept.getPaccountId().toInteger();
				bmoBankMovConcept.getPaccountId().setValue(-1);
				super.save(pmConn, bmoBankMovConcept, bmUpdateResult);

				//updateBkacBalanceDeleteByConcept(pmConn, bmoBankMovConcept, bmoBankMovConcept.getBmoBankMovement(), bmUpdateResult);

				//Actualizar el pago
				PmPaccount pmPaccount = new PmPaccount(getSFParams());
				BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn, paccountId);

				pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);

				PmRequisition pmRequisition = new PmRequisition(getSFParams());
				BmoRequisition bmoRequisition = (BmoRequisition) pmRequisition.get(pmConn, bmoBankMovConcept.getRequisitionId().toInteger());

				// Calcular montos
				pmRequisition.updateBalance(pmConn, bmoRequisition, bmUpdateResult);
				// Actualizar pagos y balance
				pmRequisition.updatePayments(pmConn, bmoRequisition,bmUpdateResult);

				// Crear bitacora en OC
				//pmRequisition.createWFloLogRequisition(pmConn, bmoRequisition, bmUpdateResult);

				pmBankMovement.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);

			}
		} catch (SFPmException e) {

		} finally {
			pmConn.close();
		}

		bmUpdateResult.setMsg("" + result);

		return bmUpdateResult;
	}

	// Obtiene
	private double getAmountInformationPaccount(PmConn pmConn, int paccId, BmUpdateResult bmUpdateResult) throws SFException {
		// Declaracion e instancia de objetos a utilizar
		PmPaccount pmPaccount = new PmPaccount(getSFParams());
		BmoPaccount bmoPaccount = new BmoPaccount();
		bmoPaccount = (BmoPaccount) pmPaccount.get(paccId);

		return bmoPaccount.getAmount().toDouble() - bmoPaccount.getPayments().toDouble();
	}

	private double getAmountInformationCXC(PmConn pmConn, int raccId,BmUpdateResult bmUpdateResult) throws SFException {
		// Declaracion e instancia de objetos a utilizar
		PmRaccount pmRaccount = new PmRaccount(getSFParams());
		BmoRaccount bmoRaccount = new BmoRaccount();
		bmoRaccount = (BmoRaccount) pmRaccount.get(pmConn, raccId);

		return bmoRaccount.getTotal().toDouble() - bmoRaccount.getPayments().toDouble();
	}

	// Revisa si la cuenta tiene saldo suficiente
	private boolean validBkacBalance(PmConn pmConn, BmoBankMovConcept bmoBankMovConcept, BmUpdateResult bmUpdateResult) throws SFException {
		//bmoBankMovConcept = (BmoBankMovConcept) bmObject;
		String sql = "";
		double balanceInitial = 0;
		double balanceNow = 0;
		double amountBkmc = 0;
		boolean result = true;

		BmoBankMovement bmoBankMovement = new BmoBankMovement();
		PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
		bmoBankMovement = (BmoBankMovement) pmBankMovement.get(pmConn,
				bmoBankMovConcept.getBankMovementId().toInteger());

		// Obtener la cantidad del concepto bancario
		amountBkmc = bmoBankMovConcept.getAmount().toDouble();
		// Obtener los saldos de la cuenta de banco
		sql = " select bkac_initialbalance, bkac_balance from bankaccounts "
				+ " where bkac_bankaccountid = "
				+ bmoBankMovement.getBankAccountId().toInteger();
		pmConn.doFetch(sql);
		if (pmConn.next()) {
			balanceInitial = pmConn.getDouble("bkac_initialbalance");
			balanceNow = pmConn.getDouble("bkac_balance");
		}

		// Validar si la cantidad a pagar no sobrepasa el saldo
		if ((balanceInitial + balanceNow) >= amountBkmc) {
			result = true;
		} else {
			result = false;
		}

		return result;
	}

	// Actualizar la partidas del presupuesto
	public void updateBudgetItem(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
		BmFilter bmFilter = new BmFilter();
		bmFilter.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getPaccountId(), bmoPaccount.getId());

		Iterator<BmObject> bankMovConceptIterator = this.list(pmConn, bmFilter).iterator();

		while (bankMovConceptIterator.hasNext()) {
			bmoBankMovConcept = (BmoBankMovConcept)bankMovConceptIterator.next();

			bmoBankMovConcept.getBudgetItemId().setValue(bmoPaccount.getBudgetItemId().toInteger());			

			super.saveSimple(pmConn, bmoBankMovConcept, bmUpdateResult);			
		}
	}

	// Actualiza conceptos 
	private double updateTotalConcepts(PmConn pmConn, int bankMovementId, BmUpdateResult bmUpdateResult) throws SFException {
		String sql = "";
		double total = 0;
		sql = " SELECT SUM(bkmc_amount) AS total FROM bankmovconcepts " +
				" WHERE bkmc_bankmovementid = " + bankMovementId;
		pmConn.doFetch(sql);

		if (pmConn.next()) {
			total = pmConn.getDouble("total");
		}

		return total;
	} 

	// Ligar la CxP a un Anticipo de OC
	public void setPaccountToBankMovConcepts(PmConn pmConn, BmoPaccount bmoPaccount, BmUpdateResult bmUpdateResult) throws SFException {
		PmConn pmConn2 = new PmConn(getSFParams());
		printDevLog("setPaccountToBankMovConcepts");
		try {
			pmConn2.open();
			BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
			ArrayList<BmFilter> filterList = new ArrayList<BmFilter>();

			// Filtro por OC
			BmFilter filterByRequisition = new BmFilter();
			filterByRequisition.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getRequisitionId(), bmoPaccount.getRequisitionId().toInteger());			
			filterList.add(filterByRequisition);

			// Filtra por monto exacto
			//			BmFilter filterByAmount = new BmFilter();
			//			filterByAmount.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getAmount(), "" + bmoPaccount.getAmount().toDouble());
			//			filterList.add(filterByAmount);

			Iterator<BmObject> bkmcIterator = this.list(pmConn, filterList).iterator();
			if (bkmcIterator.hasNext()) {
				bmoBankMovConcept = (BmoBankMovConcept)bkmcIterator.next();
				printDevLog("MB: " + bmoBankMovConcept.getBmoBankMovement().getCode().toString()
						+ " -> Concepto:"+ bmoBankMovConcept.getCode().toString());

				// Validar que no tenga una liga
				if (!(bmoBankMovConcept.getPaccountId().toInteger() > 0)) {
					bmoBankMovConcept.getPaccountId().setValue(bmoPaccount.getId());

					PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
					pmBankMovConcept.save(pmConn, bmoBankMovConcept, bmUpdateResult);
				}
			}

		} catch (SFException e) {
			bmUpdateResult.addMsg("Error setPaccountToBankMovConcepts(): " + e.toString());
		} finally {
			pmConn2.close();
		}	
	}

	// Existen conceptos de banco de tipo Anticipo Proveedor
	public boolean hasMovConceptRequisitionAdvance(PmConn pmConn, int requisitionId, BmUpdateResult bmUpdateResult) throws SFException {		
		String sql = "";
		//Obtener el correo del vendedor
		sql = " SELECT * FROM  " + formatKind("bankmovconcepts")
		+ " LEFT JOIN " + formatKind("bankmovements") + " ON (bkmc_bankmovementid = bkmv_bankmovementid) "
		+ " LEFT JOIN " + formatKind("bankmovtypes") + " ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) "
		+ " WHERE bkmc_requisitionid = " + requisitionId
		+ " AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "' " 
		+ " AND bkmt_type = '" + BmoBankMovType.TYPE_WITHDRAW + "' " 
		+ " AND bkmc_requisitionid > 0";
		pmConn.doFetch(sql);
		return pmConn.next();
	}

	// Regresar sumatoria de monto del anticipo proveedor
	public double getSumAmountRequisitionAdvance(PmConn pmConn, int requisitionId, BmUpdateResult bmUpdateResult) throws SFException {
		double sumAmount = 0;
		BmoBankMovConcept bmoBankMovConcept = new BmoBankMovConcept();
		BmFilter bmFilterByRequisition = new BmFilter();
		bmFilterByRequisition.setValueFilter(bmoBankMovConcept.getKind(), bmoBankMovConcept.getRequisitionId(), requisitionId);
		PmBankMovConcept pmBankMovConcept = new PmBankMovConcept(getSFParams());
		Iterator<BmObject> bkmcRequisitionIterator = pmBankMovConcept.list(pmConn, bmFilterByRequisition).iterator();

		while (bkmcRequisitionIterator.hasNext()) {
			bmoBankMovConcept = (BmoBankMovConcept)bkmcRequisitionIterator.next();
			if (bmoBankMovConcept.getAmountCoverted() .toDouble() > 0)
				sumAmount += bmoBankMovConcept.getAmountCoverted().toDouble();
			else
				sumAmount += bmoBankMovConcept.getAmount().toDouble();
		}
		return sumAmount;
	}
	
	// Regresa verdadero si la CxC tiene devolucion
	public boolean hasDevolutionCxC(PmConn pmConn, int raccountId) throws SFException {
		boolean hasDevolutionCxC = false;
		// Buscar Devoluciones de banco de la cxc
		String sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" WHERE bkmc_raccountid = " + raccountId + 
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC + "'"; 
		pmConn.doFetch(sql);
		if (pmConn.next()) { hasDevolutionCxC = true; }
		
		return hasDevolutionCxC;
	}
	
	// Regresa verdadero si la CxP tiene devolucion
	public boolean hasDevolutionCxP(PmConn pmConn, int paccountId) throws SFException {
		boolean hasDevolutionCxC = false;
		// Buscar Devoluciones de banco de la cxp
		String sql = " SELECT * FROM bankmovconcepts " +
				" LEFT JOIN bankmovements ON (bkmc_bankmovementid = bkmv_bankmovementid) " +
				" LEFT JOIN bankmovtypes ON (bkmv_bankmovtypeid = bkmt_bankmovtypeid) " +
				" WHERE bkmc_paccountid = " + paccountId + 
				" AND bkmt_type = '" + BmoBankMovType.TYPE_DEPOSIT + "'" +
				" AND bkmt_category = '" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE + "'";
		pmConn.doFetch(sql);
		if (pmConn.next()) { hasDevolutionCxC = true; }
		
		return hasDevolutionCxC;
	}

	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoBankMovConcept = (BmoBankMovConcept)bmObject;

		PmBankMovement pmBankMovement = new PmBankMovement(getSFParams());
		BmoBankMovement bmoBankMovement = (BmoBankMovement) pmBankMovement.get(pmConn, bmoBankMovConcept.getBankMovementId().toInteger());

		PmBankMovType pmBankMovType = new PmBankMovType(getSFParams());
		BmoBankMovType bmoBankMovType = (BmoBankMovType) pmBankMovType.get(pmConn, bmoBankMovement.getBankMovTypeId().toInteger());

		if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
			if (bmoBankMovConcept.getPaccountId().toInteger() > 0) {
				//Eliminar el pago hecho al crédito

				PmPaccount pmDepositPaccount = new PmPaccount(getSFParams());
				BmoPaccount bmoDepositPaccount = (BmoPaccount)pmDepositPaccount.get(pmConn, bmoBankMovConcept.getPaccountId().toInteger());
				PmRequisition pmRequisition = new PmRequisition(getSFParams());

				if (bmoDepositPaccount.getRequisitionId().toInteger() > 0) {
					BmoRequisition bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, bmoDepositPaccount.getRequisitionId().toInteger());

					if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_CXP) &&
							bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_CREDIT)) {

						BmoLoanPayment bmoLoanPayment = new BmoLoanPayment();
						PmLoanPayment pmLoanPayment = new PmLoanPayment(getSFParams());

						BmFilter bmFilter = new BmFilter();
						bmFilter.setValueFilter(bmoLoanPayment.getKind(), bmoLoanPayment.getBankMovConceptId(), bmoBankMovConcept.getId());

						//Si existe un registro eliminar el pago
						if (pmLoanPayment.count(bmFilter) > 0) {					
							bmoLoanPayment = (BmoLoanPayment)pmLoanPayment.getBy(pmConn, bmoBankMovConcept.getId(), bmoLoanPayment.getBankMovConceptId().getName());
							pmLoanPayment.delete(pmConn, bmoLoanPayment, bmUpdateResult);
						}
					}
				} 
			} else if (bmoBankMovement.getBmoBankMovType().getCategory().equals(BmoBankMovType.CATEGORY_LOANDISPOSAL) && 
					bmoBankMovement.getLoanId().toInteger() > 0) {
				BmoLoanDisbursement bmoLoanDisbursement = new BmoLoanDisbursement();
				PmLoanDisbursement pmLoanDisbursement = new PmLoanDisbursement(getSFParams());
				bmoLoanDisbursement = (BmoLoanDisbursement)pmLoanDisbursement.getBy(pmConn, bmoBankMovConcept.getId(), bmoLoanDisbursement.getBankMovConceptId().getName());

				pmLoanDisbursement.delete(pmConn, bmoLoanDisbursement, bmUpdateResult);

			}
		}
		
		// Validar si tiene una devolucion la CxC
		if (bmoBankMovConcept.getRaccountId().toInteger() > 0 
				&& !bmoBankMovType.getCategory().equals("" + BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {
			if (hasDevolutionCxC(pmConn, bmoBankMovConcept.getRaccountId().toInteger()))
				bmUpdateResult.addError(bmoBankMovConcept.getRaccountId().getName(), "<b>No se puede eliminar, debe quitar primero la devolución.</b>");
		}
		
		// Validar si tiene una devolucion la CxP
		if (bmoBankMovConcept.getPaccountId().toInteger() > 0 
				&& !(bmoBankMovType.getCategory().equals("" + BmoBankMovType.CATEGORY_REQUISITIONADVANCE) 
						&& bmoBankMovType.getType().equals("" + BmoBankMovType.TYPE_DEPOSIT)) ) {
			if (hasDevolutionCxP(pmConn, bmoBankMovConcept.getPaccountId().toInteger())) {
				bmUpdateResult.addError(bmoBankMovConcept.getPaccountId().getName(), "<b>No se puede eliminar, debe quitar primero la devolución.</b>");
			}
		}
		
		// Elimina el concepto
		super.delete(pmConn, bmoBankMovConcept, bmUpdateResult);

		// Elimina la cuenta vinculada
		if (bmoBankMovConcept.getRaccountId().toInteger() > 0 ) {			

			if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_DEVOLUTIONCXC)) {					
				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoBankMovConcept.getRaccountId().toInteger());

				//Calcular los saldos				
				pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);								

			} else {
				//Actualizar el saldo del padre
				PmRaccount pmRaccount = new PmRaccount(getSFParams());
				BmoRaccount bmoRaccount = (BmoRaccount)pmRaccount.get(pmConn, bmoBankMovConcept.getRaccountId().toInteger());

				pmRaccount.updateBalance(pmConn, bmoRaccount, bmUpdateResult);
			}

			//Calcular la partida
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (bmoBankMovConcept.getBudgetItemId().toInteger() > 0) {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());

					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
		} else if (bmoBankMovConcept.getPaccountId().toInteger() > 0) {
			//Actualizar el pago
			PmPaccount pmPaccount = new PmPaccount(getSFParams());
			BmoPaccount bmoPaccount = (BmoPaccount)pmPaccount.get(pmConn,bmoBankMovConcept.getPaccountId().toInteger());

			pmPaccount.updateBalance(pmConn, bmoPaccount, bmUpdateResult);
		} else if (bmoBankMovConcept.getDepositPaccountItemId().toInteger() > 0 
				&& bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {

			//Eliminar el Item del Deposito
			PmPaccountItem pmDepositPaccountItem = new PmPaccountItem(getSFParams());
			BmoPaccountItem bmoDepositPaccountItem = (BmoPaccountItem)pmDepositPaccountItem.get(pmConn, bmoBankMovConcept.getDepositPaccountItemId().toInteger());			

			// CxP Destino
			PmPaccount pmDepositPaccount = new PmPaccount(getSFParams());
			BmoPaccount bmoDepositPaccount = (BmoPaccount)pmDepositPaccount.get(pmConn, bmoDepositPaccountItem.getPaccountId().toInteger());
			bmoDepositPaccount.getStatus().setValue(BmoPaccount.STATUS_REVISION);

			pmDepositPaccount.delete(pmConn, bmoDepositPaccount, bmUpdateResult);

		}

		// Actualizas la información del movimiento de banco
		if (bmoBankMovType.getCategory().equals(BmoBankMovType.CATEGORY_REQUISITIONADVANCE)) {
			if(bmoBankMovType.getType().equals(BmoBankMovType.TYPE_DEPOSIT))
				updateBalanceBankAccountDelete(pmConn, bmoBankMovConcept, bmoBankMovement, bmUpdateResult);
			else if(bmoBankMovType.getType().equals(BmoBankMovType.TYPE_WITHDRAW)) {
				updateBalanceBankAccountDelete(pmConn, bmoBankMovConcept, bmoBankMovement, bmUpdateResult);

				PmRequisition pmRequisition = new PmRequisition(getSFParams());
				BmoRequisition bmoRequisition = (BmoRequisition) pmRequisition.get(pmConn, bmoBankMovConcept.getRequisitionId().toInteger());

				// Calcular montos
				pmRequisition.updateBalance(pmConn, bmoRequisition, bmUpdateResult);

				// Calcular pagos y saldo
				pmRequisition.updatePayments(pmConn, bmoRequisition,bmUpdateResult);

				// Crear bitacora en OC
				pmRequisition.createWFloLogRequisition(pmConn, bmoRequisition, "Modificó el pago de ", bmUpdateResult);
			}
			
			// Calcular la partida presupuestal
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (bmoBankMovConcept.getBudgetItemId().toInteger() > 0) {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());
					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
		} else {			
			updateBalanceBankAccountDelete(pmConn, bmoBankMovConcept, bmoBankMovement, bmUpdateResult);	
			
			// Calcular la partida presupuestal
			if (((BmoFlexConfig)getSFParams().getBmoAppConfig()).getEnableWorkBudgetItem().toBoolean()) {
				if (bmoBankMovConcept.getBudgetItemId().toInteger() > 0) {
					PmBudgetItem pmBudgetItem = new PmBudgetItem(getSFParams());
					BmoBudgetItem bmoBudgetItem = (BmoBudgetItem)pmBudgetItem.get(pmConn, bmoBankMovConcept.getBudgetItemId().toInteger());
					pmBudgetItem.updateBalance(pmConn, bmoBudgetItem, bmUpdateResult);
				}
			}
		}

		pmBankMovement.updateAmount(pmConn, bmoBankMovement, bmUpdateResult);

		return bmUpdateResult;
	}
}
