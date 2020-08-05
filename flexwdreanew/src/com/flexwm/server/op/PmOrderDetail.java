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

import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.op.BmoOrder;
import com.flexwm.shared.op.BmoOrderDetail;
import com.flexwm.shared.wf.BmoWFlowLog;


public class PmOrderDetail extends PmObject {
	BmoOrderDetail bmoOrderDetail;
	PmOrder pmOrder = new PmOrder(getSFParams());

	public PmOrderDetail(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoOrderDetail = new BmoOrderDetail();
		setBmObject(bmoOrderDetail);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoOrderDetail.getOrderId(), bmoOrderDetail.getBmoOrder()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getOrderTypeId(), bmoOrderDetail.getBmoOrder().getBmoOrderType()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getCurrencyId(), bmoOrderDetail.getBmoOrder().getBmoCurrency()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getCustomerId(), bmoOrderDetail.getBmoOrder().getBmoCustomer()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoCustomer().getSalesmanId(), bmoOrderDetail.getBmoOrder().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoCustomer().getBmoUser().getAreaId(), bmoOrderDetail.getBmoOrder().getBmoCustomer().getBmoUser().getBmoArea()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoCustomer().getBmoUser().getLocationId(), bmoOrderDetail.getBmoOrder().getBmoCustomer().getBmoUser().getBmoLocation()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoCustomer().getTerritoryId(), bmoOrderDetail.getBmoOrder().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoCustomer().getReqPayTypeId(), bmoOrderDetail.getBmoOrder().getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getWFlowId(), bmoOrderDetail.getBmoOrder().getBmoWFlow()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoWFlow().getWFlowPhaseId(), bmoOrderDetail.getBmoOrder().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getWFlowTypeId(), bmoOrderDetail.getBmoOrder().getBmoWFlowType()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoWFlowType().getWFlowCategoryId(), bmoOrderDetail.getBmoOrder().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoOrderDetail.getBmoOrder().getBmoWFlow().getWFlowFunnelId(), bmoOrderDetail.getBmoOrder().getBmoWFlow().getBmoWFlowFunnel())
				)));
	}

	@Override
	public String getDisclosureFilters() {
		String filters = "";
		//int loggedUserId = getSFParams().getLoginInfo().getUserId();
		if (filters.length() > 0) filters += " AND ";
		filters += pmOrder.getDisclosureFilters();

		return filters;
	}	

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoOrderDetail bmoOrderDetail = (BmoOrderDetail) autoPopulate(pmConn, new BmoOrderDetail());

		// BmoOrder
		BmoOrder bmoOrder = new BmoOrder();
		if (pmConn.getInt(bmoOrder.getIdFieldName()) > 0) bmoOrderDetail.setBmoOrder((BmoOrder) new PmOrder(getSFParams()).populate(pmConn));
		else bmoOrderDetail.setBmoOrder(bmoOrder);

		return bmoOrderDetail;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoOrderDetail bmoOrderDetail = (BmoOrderDetail)bmObject;
		BmoOrderDetail bmoOrderDetailPrevious = bmoOrderDetail;
		PmOrder pmOrder = new PmOrder(getSFParams());
		//boolean newRecord = false;

		if (!(bmoOrderDetail.getId() > 0)) {
			//newRecord = true;
		}
		else {
			bmoOrderDetailPrevious = (BmoOrderDetail) this.get(bmoOrderDetail.getId());
			String comment = "";
			BmoOrder bmoOrder = (BmoOrder) pmOrder.get(bmoOrderDetail.getOrderId().toInteger());
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			if (!bmoOrderDetail.getStatus().toString().equals(bmoOrderDetailPrevious.getStatus().toString())) {
				comment += " Se Modificó el Estatus del Detalle del Pedido: "+bmoOrderDetail.getStatus().getSelectedOption().getLabel();
			}

			if (!(bmoOrderDetail.getDesireDate().toString().equals(bmoOrderDetailPrevious.getDesireDate().toString()))) {
				comment += ", Se Modificó Fecha Deseada del Detalle del Pedido a: "+bmoOrderDetail.getDesireDate().toString();
			}

			if (!(bmoOrderDetail.getStartDate().toString().equals(bmoOrderDetailPrevious.getStartDate().toString()))) {
				comment += ", Se Modificó Fecha Inicio del Detalle del Pedido a: "+bmoOrderDetail.getStartDate().toString();
			}

			if (!(bmoOrderDetail.getDeliveryDate().toString().equals(bmoOrderDetailPrevious.getDeliveryDate().toString()))) {
				comment += ", Se Modificó Fecha Pactada del Detalle del Pedido a: "+bmoOrderDetail.getDeliveryDate().toString();
			}
			if(!comment.equals(""))
				pmWFlowLog.addDataLog(bmUpdateResult, bmoOrder.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, comment, "");

			// Asignar estatus de detalle al pedido
			bmoOrder.getStatusDetail().setValue(bmoOrderDetail.getStatus().toString());
			pmOrder.saveSimple(pmConn, bmoOrder, bmUpdateResult);
		}		
		
		
		super.save(pmConn, bmoOrderDetail, bmUpdateResult);
		return bmUpdateResult; 	
	}

	public boolean orderDetailExists(PmConn pmConn, int orderId) throws SFPmException {
		pmConn.doFetch("SELECT ordt_orderdetailid FROM orderdetails WHERE ordt_orderid = " + orderId);
		return pmConn.next();
	}
}
