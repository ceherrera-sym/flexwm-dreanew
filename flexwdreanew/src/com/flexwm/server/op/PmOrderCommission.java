package com.flexwm.server.op;

import java.util.Iterator;
import com.flexwm.server.wf.PmWFlowUser;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderCommission;
import com.flexwm.shared.op.BmoOrderCommissionAmount;
import com.flexwm.shared.op.BmoRequisition;
import com.flexwm.shared.op.BmoRequisitionItem;
import com.flexwm.shared.op.BmoRequisitionType;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmUser;
import com.symgae.server.sf.PmProfileUser;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoUser;


public class PmOrderCommission extends PmObject {
	BmoOrderCommission bmoOrderCommission;

	public PmOrderCommission(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderCommission = new BmoOrderCommission();
		setBmObject(bmoOrderCommission);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoOrderCommission());
	}

	// Calcula y actualiza los montos de las comisiones
	public void updateCommissionPayments(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException{

		// Obtiene el Tabulador de commision
		if (bmoOrder.getOrderCommissionId().toInteger() > 0) {
			bmoOrderCommission = (BmoOrderCommission)this.get(pmConn, bmoOrder.getOrderCommissionId().toInteger()); 	

			// Obtiene la lista de montos de la comisión
			PmOrderCommissionAmount pmOrderCommissionAmount = new PmOrderCommissionAmount(getSFParams());
			BmoOrderCommissionAmount bmoOrderCommissionAmount = new BmoOrderCommissionAmount();
			BmFilter filterByCommission = new BmFilter();
			filterByCommission.setValueFilter(bmoOrderCommissionAmount.getKind(), bmoOrderCommissionAmount.getOrderCommissionId(), bmoOrderCommission.getId());
			Iterator<BmObject> orderCommissionAmountList = pmOrderCommissionAmount.list(pmConn, filterByCommission).iterator();
			while (orderCommissionAmountList.hasNext()) {
				bmoOrderCommissionAmount = (BmoOrderCommissionAmount)orderCommissionAmountList.next();

				// Crea / Actualiza Orden de Compra
				updateRequisition(pmConn, bmoOrder, bmoOrderCommission, bmoOrderCommissionAmount, bmUpdateResult);
			}
		}
	}

	// Revisa y en su caso dispara pagos de ordenes de compra de comisiones
	public void triggerCommissionPayments(PmConn pmConn, BmoOrder bmoOrder, BmUpdateResult bmUpdateResult) throws SFException {

		// Actualiza status de comision
		PmOrderCommissionAmount pmOrderCommissionAmount = new PmOrderCommissionAmount(getSFParams());
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = new BmoRequisition();
		BmFilter filterByOrder = new BmFilter();
		filterByOrder.setValueFilter(bmoRequisition.getKind(), bmoRequisition.getOrderId(), bmoOrder.getId());
		Iterator<BmObject> requisitionList = pmRequisition.list(pmConn, filterByOrder).iterator();

		while (requisitionList.hasNext()) {
			bmoRequisition = (BmoRequisition)requisitionList.next();
			if (bmoRequisition.getBmoRequisitionType().getType().equals(BmoRequisitionType.TYPE_COMMISION) 
					&& (bmoRequisition.getOrderCommissionAmountId().toInteger() > 0)) {
				BmoOrderCommissionAmount bmoOrderCommissionAmount = (BmoOrderCommissionAmount)pmOrderCommissionAmount.get(pmConn, bmoRequisition.getOrderCommissionAmountId().toInteger());

				// Dispara o no los pagos
				triggerCommissionAmount(pmConn, bmoOrder, bmoOrderCommissionAmount, bmoRequisition, bmUpdateResult);
			}
		}
	}

	// Crea / Actualiza la orden de compra
	private void updateRequisition(PmConn pmConn, BmoOrder bmoOrder, BmoOrderCommission bmoOrderCommission, BmoOrderCommissionAmount bmoOrderCommissionAmount, BmUpdateResult bmUpdateResult) throws SFException {

		// Revisa si existe ya la orden de compra ligada
		PmRequisition pmRequisition = new PmRequisition(getSFParams());
		BmoRequisition bmoRequisition = new BmoRequisition();
		int requisitionId = pmRequisition.getRequisitionIdByOrderAndCommissionAmount(pmConn, bmoOrder.getId(), bmoOrderCommissionAmount.getId());

		//Obtener el id del tipo comision
		PmRequisitionType pmRequisitionType = new PmRequisitionType(getSFParams());
		int requisitionTypeId = pmRequisitionType.getRequisitionTypeIdCommission(pmConn);

		if (!(requisitionTypeId > 0)) 
			bmUpdateResult.addError(bmoOrder.getCode().getName(), "No existe el Tipo Comisiones.");

		// No fue encontrado el registro, crearlo
		if (!(requisitionId > 0)) {						
			bmoRequisition.getRequisitionTypeId().setValue(requisitionTypeId);
			bmoRequisition.getCode().setValue(bmoOrderCommission.getName().toString());
			bmoRequisition.getName().setValue(bmoOrderCommissionAmount.getName().toString());
			bmoRequisition.getDescription().setValue(bmoOrderCommission.getDescription().toString());
			bmoRequisition.getRequestDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoRequisition.getPaymentDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoRequisition.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
			bmoRequisition.getStatus().setValue(BmoRequisition.STATUS_REVISION);
			bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_REVISION);
			bmoRequisition.getTaxApplies().setValue(0);
			bmoRequisition.getPaymentStatus().setValue(BmoRequisition.PAYMENTSTATUS_PENDING);
			bmoRequisition.getRequestedBy().setValue(getSFParams().getLoginInfo().getUserId());
			bmoRequisition.getOrderCommissionAmountId().setValue(bmoOrderCommissionAmount.getId());
			bmoRequisition.getOrderId().setValue(bmoOrder.getId());
			bmoRequisition.getCompanyId().setValue(bmoOrder.getCompanyId().toInteger());

			// Obtener el colaborador / usuario que aplica, para obtener el proveedor y el área que aplica
			BmoUser bmoUser = new BmoUser();
			if (bmoOrderCommissionAmount.getUserAssignRequired().toBoolean()) {
				PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
				bmoUser = pmWFlowUser.getUserByGroup(pmConn, bmoOrder.getWFlowId().toInteger(), bmoOrderCommissionAmount.getBmoProfile(), bmUpdateResult);
			} else {
				// No requiere tomarlo de los colaboradores, sino de los usuarios del grupo
				PmProfileUser pmProfileUser = new PmProfileUser(getSFParams());
				int userId = pmProfileUser.onlyUserInProfile(pmConn, bmoOrderCommissionAmount.getProfileId().toInteger());
				if (userId > 0) {
					PmUser pmUser = new PmUser(getSFParams());
					bmoUser = (BmoUser)pmUser.get(pmConn, userId);
				} else {
					bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No se encuentra Usuario único del Grupo: " + bmoOrderCommissionAmount.getBmoProfile().getName().toString());
				}
			}

			// Asigna datos del usuario obtenido
			PmSupplierUser pmSupplierUser = new PmSupplierUser(getSFParams());
			bmoRequisition.getSupplierId().setValue(pmSupplierUser.getSupplierIdByLinkedUser(pmConn, bmoUser, bmUpdateResult));
			bmoRequisition.getAreaId().setValue(bmoUser.getAreaId().toInteger());

			// Obtener el tipo de pago de la orden de compra
			bmoRequisition.getReqPayTypeId().setValue(bmoOrderCommission.getReqPayTypeId().toInteger());

			// Almacenar la requisición
			pmRequisition.save(pmConn, bmoRequisition, bmUpdateResult);

			// Crear el requisition item del monto a calcular
			PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());
			BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
			bmoRequisitionItem.getName().setValue("Pago de Comisión");
			bmoRequisitionItem.getQuantity().setValue(1);
			bmoRequisitionItem.getDays().setValue(1);
			bmoRequisitionItem.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(getAmount(pmConn, bmoOrder, bmoOrderCommissionAmount)));
			bmoRequisitionItem.getRequisitionId().setValue(bmoRequisition.getId());

			pmRequisitionItem.save(pmConn, bmoRequisitionItem, bmUpdateResult);
		} else {
			// Ya existe, actualizala
			bmoRequisition = (BmoRequisition)pmRequisition.get(pmConn, requisitionId);

			// Si no esta autorizada, actualizala
			if (bmoRequisition.getStatus().equals(BmoRequisition.STATUS_REVISION)) {
				bmoRequisition.getRequestDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				bmoRequisition.getPaymentDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				bmoRequisition.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				bmoRequisition.getDeliveryStatus().setValue(BmoRequisition.DELIVERYSTATUS_REVISION);
				bmoRequisition.getTaxApplies().setValue(0);
				bmoRequisition.getPaymentStatus().setValue(BmoRequisition.PAYMENTSTATUS_PENDING);
				bmoRequisition.getRequestedBy().setValue(getSFParams().getLoginInfo().getUserId());

				// Obtener el colaborador / usuario que aplica, para obtener el proveedor y el área que aplica
				BmoUser bmoUser = new BmoUser();
				if (bmoOrderCommissionAmount.getUserAssignRequired().toBoolean()) {
					PmWFlowUser pmWFlowUser = new PmWFlowUser(getSFParams());
					bmoUser = pmWFlowUser.getUserByGroup(pmConn, bmoOrder.getWFlowId().toInteger(), bmoOrderCommissionAmount.getBmoProfile(), bmUpdateResult);
				} else {
					// No requiere tomarlo de los colaboradores, sino de los usuarios del grupo
					PmProfileUser pmProfileUser = new PmProfileUser(getSFParams());
					int userId = pmProfileUser.onlyUserInProfile(pmConn, bmoOrderCommissionAmount.getProfileId().toInteger());
					if (userId > 0) {
						PmUser pmUser = new PmUser(getSFParams());
						bmoUser = (BmoUser)pmUser.get(pmConn, userId);
					} else {
						bmUpdateResult.addError(bmoOrder.getStatus().getName(), "No se encuentra Usuario único del Grupo: " + bmoOrderCommissionAmount.getBmoProfile().getName().toString());
					}
				}

				// Asigna datos del usuario obtenido
				PmSupplierUser pmSupplierUser = new PmSupplierUser(getSFParams());
				bmoRequisition.getSupplierId().setValue(pmSupplierUser.getSupplierIdByLinkedUser(pmConn, bmoUser, bmUpdateResult));
				bmoRequisition.getAreaId().setValue(bmoUser.getAreaId().toInteger());

				// Obtener el tipo de pago de la orden de compra
				bmoRequisition.getReqPayTypeId().setValue(bmoOrderCommission.getReqPayTypeId().toInteger());

				// Almacenar la requisición
				pmRequisition.save(pmConn, bmoRequisition, bmUpdateResult);

				// Operaciones con los items de la O.C.
				PmRequisitionItem pmRequisitionItem = new PmRequisitionItem(getSFParams());

				// Elimina los items
				pmRequisitionItem.deleteItemsByRequisition(pmConn, bmoRequisition);

				// Crear el requisition item del monto a calcular
				BmoRequisitionItem bmoRequisitionItem = new BmoRequisitionItem();
				bmoRequisitionItem.getName().setValue("Pago de Comisión");
				bmoRequisitionItem.getQuantity().setValue(1);
				bmoRequisitionItem.getDays().setValue(1);
				bmoRequisitionItem.getPrice().setValue(SFServerUtil.roundCurrencyDecimals(getAmount(pmConn, bmoOrder, bmoOrderCommissionAmount)));
				bmoRequisitionItem.getRequisitionId().setValue(bmoRequisition.getId());

				pmRequisitionItem.save(pmConn, bmoRequisitionItem, bmUpdateResult);
			}
		}
	}

	private void triggerCommissionAmount(PmConn pmConn, BmoOrder bmoOrder, BmoOrderCommissionAmount bmoOrderCommissionAmount, BmoRequisition bmoRequisition, BmUpdateResult bmUpdateResult) throws SFException {
		PmRequisition pmRequisition = new PmRequisition(getSFParams());

		// Si el pedido esta cancelado, eliminar todo
		if (bmoOrder.getStatus().equals(BmoOrder.STATUS_CANCELLED)) {

			// Eliminar automaticamente las ROC y las CxP si ya estan creadas, debido a que no se puede disparar el pago
			pmRequisition.cancelAutoReceiveAndCreatePaccounts(pmConn, bmoRequisition, bmUpdateResult);

		} else {
			// No esta cancelado, revisa si debe disparar accion
			if (shallTrigger(bmoOrder, bmoOrderCommissionAmount)) {

				// Actualiza datos de la orden de compra
				bmoRequisition.getStatus().setValue(BmoRequisition.STATUS_AUTHORIZED);
				bmoRequisition.getPaymentDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				bmoRequisition.getDeliveryDate().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateFormat()));
				pmRequisition.saveSimple(pmConn, bmoRequisition, bmUpdateResult);

				// Crear automaticamente las ROC y las CxP, debido a que ya se puede disparar el pago
				pmRequisition.autoReceiveAndCreatePaccounts(pmConn, bmoRequisition, bmUpdateResult);

			} else {
				// Eliminar automaticamente las ROC y las CxP si ya estan creadas, debido a que no se puede disparar el pago
				pmRequisition.holdAutoReceiveAndCreatePaccounts(pmConn, bmoRequisition, bmUpdateResult);
			}
		}
	}

	private double getAmount(PmConn pmConn, BmoOrder bmoOrder, BmoOrderCommissionAmount bmoOrderCommissionAmount) throws SFException {
		double amount = 0;

		// Obtiene valor fijo
		if (bmoOrderCommissionAmount.getType().equals(BmoOrderCommissionAmount.TYPE_AMOUNT)) {
			amount = bmoOrderCommissionAmount.getValue().toDouble();
		} else {
			// Obtiene valor variable tipo porcentaje
			amount = ((bmoOrderCommissionAmount.getValue().toDouble() / 100) * bmoOrder.getTotal().toDouble());
		}

		// Descuenta pagos anticipados
		PmOrderCommissionAmount pmOrderCommissionAmount = new PmOrderCommissionAmount(getSFParams());
		amount = amount - pmOrderCommissionAmount.getCommissionPreviousPayments(pmConn, bmoOrder, bmoOrderCommissionAmount);

		return amount;
	}

	// Revisa si cumple las condiciones
	private boolean shallTrigger(BmoOrder bmoOrder, BmoOrderCommissionAmount bmoOrderCommissionAmount){
		boolean trigger = false;

		// Revisa Pedido Autorizado
		if (bmoOrderCommissionAmount.getTrigger().equals(BmoOrderCommissionAmount.TRIGGER_AUTHORIZED)) {
			if (bmoOrder.getStatus().equals(BmoOrder.STATUS_AUTHORIZED))
				trigger = true;
		} 
		// Revisa pago parcial
		/*else if (bmoOrderCommissionAmount.getTrigger().equals(BmoOrderCommissionAmount.TRIGGER_PARTIALPAYMENT)) {
			if (bmoOrder.getStatus().equals(BmoOrder.PAYMENTSTATUS_PARTIAL))
				trigger = true;
		} */
		// Revisa pago total
		else if (bmoOrderCommissionAmount.getTrigger().equals(BmoOrderCommissionAmount.TRIGGER_FULLPAYMENT)) {
			if (bmoOrder.getStatus().equals(BmoOrder.PAYMENTSTATUS_TOTAL))
				trigger = true;
		} 
		// Revisa entrega
		else if (bmoOrderCommissionAmount.getTrigger().equals(BmoOrderCommissionAmount.TRIGGER_DELIVERED)) {
			if (bmoOrder.getStatus().equals(BmoOrder.DELIVERYSTATUS_TOTAL))
				trigger = true;
		} 
		// Revisa bloqueo
		else if (bmoOrderCommissionAmount.getTrigger().equals(BmoOrderCommissionAmount.TRIGGER_LOCK)) {
			if (bmoOrder.getStatus().equals(BmoOrder.LOCKSTATUS_LOCKED))
				trigger = true;
		} 

		return trigger;
	}
}

