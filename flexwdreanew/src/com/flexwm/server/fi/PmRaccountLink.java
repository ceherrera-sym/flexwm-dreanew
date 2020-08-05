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
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountLink;
import com.flexwm.shared.op.BmoOrder;


public class PmRaccountLink extends PmObject {
	BmoRaccountLink bmoRaccountLink;

	public PmRaccountLink(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRaccountLink = new BmoRaccountLink();
		setBmObject(bmoRaccountLink);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRaccountLink.getForeignId(), bmoRaccountLink.getBmoForeign()),
				new PmJoin(bmoRaccountLink.getOrderId(), bmoRaccountLink.getBmoOrder()),
				new PmJoin(bmoRaccountLink.getBmoForeign().getCustomerId(),
						bmoRaccountLink.getBmoForeign().getBmoCustomer()),
				//new PmJoin(bmoRaccountLink.getBmoForeign().getUserId(), bmoRaccountLink.getBmoForeign().getBmoUser()),


				new PmJoin(bmoRaccountLink.getBmoForeign().getBmoCustomer().getTerritoryId(),
						bmoRaccountLink.getBmoForeign().getBmoCustomer().getBmoTerritory()),
				new PmJoin(bmoRaccountLink.getBmoForeign().getBmoCustomer().getReqPayTypeId(),
						bmoRaccountLink.getBmoForeign().getBmoCustomer().getBmoReqPayType()),
				new PmJoin(bmoRaccountLink.getBmoForeign().getCompanyId(),
						bmoRaccountLink.getBmoForeign().getBmoCompany()),
				new PmJoin(bmoRaccountLink.getBmoForeign().getCurrencyId(),
						bmoRaccountLink.getBmoForeign().getBmoCurrency()),
				new PmJoin(bmoRaccountLink.getBmoForeign().getRaccountTypeId(),
						bmoRaccountLink.getBmoForeign().getBmoRaccountType()),
				new PmJoin(bmoRaccountLink.getBmoOrder().getOrderTypeId(),
						bmoRaccountLink.getBmoOrder().getBmoOrderType()),	
				new PmJoin(bmoRaccountLink.getBmoOrder().getBmoCustomer().getSalesmanId(),
						bmoRaccountLink.getBmoOrder().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoRaccountLink.getBmoForeign().getBmoUser().getAreaId(),
						bmoRaccountLink.getBmoForeign().getBmoUser().getBmoArea()),
				new PmJoin(bmoRaccountLink.getBmoForeign().getBmoUser().getLocationId(),
						bmoRaccountLink.getBmoForeign().getBmoUser().getBmoLocation()),
				new PmJoin(bmoRaccountLink.getBmoOrder().getWFlowId(), bmoRaccountLink.getBmoOrder().getBmoWFlow()),
				new PmJoin(bmoRaccountLink.getBmoOrder().getBmoWFlow().getWFlowPhaseId(),
						bmoRaccountLink.getBmoOrder().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoRaccountLink.getBmoOrder().getWFlowTypeId(),
						bmoRaccountLink.getBmoOrder().getBmoWFlowType()),
				new PmJoin(bmoRaccountLink.getBmoOrder().getBmoWFlowType().getWFlowCategoryId(),
						bmoRaccountLink.getBmoOrder().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoRaccountLink.getBmoOrder().getBmoWFlow().getWFlowFunnelId(),
						bmoRaccountLink.getBmoOrder().getBmoWFlow().getBmoWFlowFunnel()))));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRaccountLink = (BmoRaccountLink) autoPopulate(pmConn, new BmoRaccountLink());

		// BmoRaccountForeig
		BmoRaccount bmoForeign = new BmoRaccount();
		if (pmConn.getInt(bmoForeign.getIdFieldName()) > 0)
			bmoRaccountLink.setBmoForeign((BmoRaccount) new PmRaccount(getSFParams()).populate(pmConn));
		else
			bmoRaccountLink.setBmoForeign(bmoForeign);
		
		// BmoOrder
		BmoOrder bmoOrder = new BmoOrder();
		if (pmConn.getInt(bmoOrder.getIdFieldName()) > 0)
			bmoRaccountLink.setBmoOrder((BmoOrder) new PmOrder(getSFParams()).populate(pmConn));
		else
			bmoRaccountLink.setBmoOrder(bmoOrder);

		return bmoRaccountLink;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoRaccountLink = (BmoRaccountLink) bmObject;

//		boolean newRecord = false;

		// Se almacena de forma preliminar para asignar ID
//		if (!(bmoRaccountLink.getId() > 0)) {
//			newRecord = true;
//		}

		super.save(pmConn, bmoRaccountLink, bmUpdateResult);

		// Obtener el pedido de CXC Foreign foreignId
		PmOrder pmOrder = new PmOrder(getSFParams());
		if (bmoRaccountLink.getForeignId().toInteger() > 0) {
			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			BmoRaccount bmoForeignRacc = (BmoRaccount) pmRaccount.get(pmConn,
					bmoRaccountLink.getForeignId().toInteger());

			if (bmoForeignRacc.getOrderId().toInteger() > 0) {
				BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn, bmoForeignRacc.getOrderId().toInteger());
				bmoRaccountLink.getOrderId().setValue(bmoOrder.getId());
			}
		}

		// Obtener la clave del pedido de la CxC raccountId
		if (bmoRaccountLink.getRaccountId().toInteger() > 0) {
			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			BmoRaccount bmoForeignRacc = (BmoRaccount) pmRaccount.get(pmConn,
					bmoRaccountLink.getRaccountId().toInteger());

			if (bmoForeignRacc.getOrderId().toInteger() > 0) {
				BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn, bmoForeignRacc.getOrderId().toInteger());
				bmoRaccountLink.getOrderCode().setValue(bmoOrder.getCode().getValue());
			}
		}

		super.save(pmConn, bmoRaccountLink, bmUpdateResult);

		return bmUpdateResult;
	}

}
