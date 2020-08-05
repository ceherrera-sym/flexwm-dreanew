package com.flexwm.server.cron;

import java.util.Iterator;
import com.flexwm.server.op.PmOrder;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.op.BmoOrder;
import com.symgae.server.SFServerUtil;
import com.symgae.server.cron.ICronJob;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;


public class CronOrderLockStatus implements ICronJob {

	@Override
	public String init(SFParams sFParams) throws SFException {
		return execute(sFParams);
	}

	private String execute(SFParams sFParams) throws SFException {
		// Si esta habilitado el bloqueo de pedidos
		if (((BmoFlexConfig)sFParams.getBmoAppConfig()).getEnableOrderLock().toBoolean()) {

			// Obten los pedidos
			PmOrder pmOrder = new PmOrder(sFParams);
			BmoOrder bmoOrder = new BmoOrder();
			BmFilter filterByDate = new BmFilter();
			filterByDate.setValueOperatorFilter(bmoOrder.getKind(), bmoOrder.getLockStart(), BmFilter.MAJOREQUAL, SFServerUtil.nowToString(sFParams, sFParams.getDateFormat()));
			Iterator<BmObject> orderList = pmOrder.list(filterByDate).iterator();
			BmUpdateResult bmUpdateResult = new BmUpdateResult();

			// Guarda cada pedido para que se actualice estatus de bloqueo
			while (orderList.hasNext()) {
				BmoOrder nextBmoOrder = (BmoOrder)orderList.next();
				pmOrder.save(nextBmoOrder, bmUpdateResult);
			}

		}
		return this.getClass().getName() + "-execute(): OK";
	}
}
