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
import com.flexwm.shared.fi.BmoRaccountLink2;
import com.flexwm.shared.op.BmoOrder;

public class PmRaccountLink2 extends PmObject {
	BmoRaccountLink2 bmoRaccountLink2;

	public PmRaccountLink2(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoRaccountLink2 = new BmoRaccountLink2();
		setBmObject(bmoRaccountLink2);
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoRaccountLink2.getRaccountId(), bmoRaccountLink2.getBmoForeign()),
				new PmJoin(bmoRaccountLink2.getBmoForeign().getOrderId(), bmoRaccountLink2.getBmoOrder()),
				new PmJoin(bmoRaccountLink2.getBmoForeign().getCustomerId(),bmoRaccountLink2.getBmoForeign().getBmoCustomer()),				
				new PmJoin(bmoRaccountLink2.getBmoForeign().getBmoCustomer().getTerritoryId(),bmoRaccountLink2.getBmoForeign().getBmoCustomer().getBmoTerritory()),				
				new PmJoin(bmoRaccountLink2.getBmoForeign().getBmoCustomer().getReqPayTypeId(),bmoRaccountLink2.getBmoForeign().getBmoCustomer().getBmoReqPayType()),				
				new PmJoin(bmoRaccountLink2.getBmoForeign().getCompanyId(),bmoRaccountLink2.getBmoForeign().getBmoCompany()),
				new PmJoin(bmoRaccountLink2.getBmoForeign().getCurrencyId(),bmoRaccountLink2.getBmoForeign().getBmoCurrency()),				
				new PmJoin(bmoRaccountLink2.getBmoForeign().getRaccountTypeId(),bmoRaccountLink2.getBmoForeign().getBmoRaccountType()),				
				new PmJoin(bmoRaccountLink2.getBmoOrder().getOrderTypeId(),bmoRaccountLink2.getBmoOrder().getBmoOrderType()),				
				new PmJoin(bmoRaccountLink2.getBmoOrder().getBmoCustomer().getSalesmanId(),bmoRaccountLink2.getBmoOrder().getBmoCustomer().getBmoUser()),
				new PmJoin(bmoRaccountLink2.getBmoForeign().getBmoUser().getAreaId(),bmoRaccountLink2.getBmoForeign().getBmoUser().getBmoArea()),
				new PmJoin(bmoRaccountLink2.getBmoForeign().getBmoUser().getLocationId(),bmoRaccountLink2.getBmoForeign().getBmoUser().getBmoLocation()),
				new PmJoin(bmoRaccountLink2.getBmoOrder().getWFlowId(),bmoRaccountLink2.getBmoOrder().getBmoWFlow()),
				new PmJoin(bmoRaccountLink2.getBmoOrder().getBmoWFlow().getWFlowPhaseId(),bmoRaccountLink2.getBmoOrder().getBmoWFlow().getBmoWFlowPhase()),
				new PmJoin(bmoRaccountLink2.getBmoOrder().getWFlowTypeId(),bmoRaccountLink2.getBmoOrder().getBmoWFlowType()),
				new PmJoin(bmoRaccountLink2.getBmoOrder().getBmoWFlowType().getWFlowCategoryId(),bmoRaccountLink2.getBmoOrder().getBmoWFlowType().getBmoWFlowCategory()),
				new PmJoin(bmoRaccountLink2.getBmoOrder().getBmoWFlow().getWFlowFunnelId(),bmoRaccountLink2.getBmoOrder().getBmoWFlow().getBmoWFlowFunnel()))));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoRaccountLink2 = (BmoRaccountLink2) autoPopulate(pmConn, new BmoRaccountLink2());

		BmoRaccount bmoForeign = new BmoRaccount();
		if (pmConn.getInt(bmoForeign.getIdFieldName()) > 0)
			bmoRaccountLink2.setBmoForeign((BmoRaccount) new PmRaccount(getSFParams()).populate(pmConn));
		else
			bmoRaccountLink2.setBmoForeign(bmoForeign);
		
		BmoOrder bmoOrder = new BmoOrder();
		if (pmConn.getInt(bmoOrder.getIdFieldName()) > 0)
			bmoRaccountLink2.setBmoOrder((BmoOrder) new PmOrder(getSFParams()).populate(pmConn));
		else
			bmoRaccountLink2.setBmoOrder(bmoOrder);
		
		return bmoRaccountLink2;
	}
	
	

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		

		super.save(pmConn, bmoRaccountLink2, bmUpdateResult);

		// Obtener el pedido
		PmOrder pmOrder = new PmOrder(getSFParams());
		if (bmoRaccountLink2.getForeignId().toInteger() > 0) {
			PmRaccount pmRaccount = new PmRaccount(getSFParams());
			BmoRaccount bmoForeignRacc = (BmoRaccount) pmRaccount.get(pmConn,
					bmoRaccountLink2.getForeignId().toInteger());

			if (bmoForeignRacc.getOrderId().toInteger() > 0) {
				BmoOrder bmoOrder = (BmoOrder) pmOrder.get(pmConn, bmoForeignRacc.getOrderId().toInteger());
				bmoRaccountLink2.getOrderId().setValue(bmoOrder.getId());
			}
		}

		super.save(pmConn, bmoRaccountLink2, bmUpdateResult);
		return bmUpdateResult;
	}

}
