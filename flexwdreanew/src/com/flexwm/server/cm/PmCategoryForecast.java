package com.flexwm.server.cm;

import com.flexwm.shared.cm.BmoCategoryForecast;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmCategoryForecast extends PmObject {
	BmoCategoryForecast bmoCategoryForecast;

	public PmCategoryForecast(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoCategoryForecast = new BmoCategoryForecast();
		setBmObject(bmoCategoryForecast);
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoCategoryForecast());
	}
	
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoCategoryForecast bmoCategoryForecast = (BmoCategoryForecast)bmObject;
//		boolean newRecord = false;
		if (existStatusOpportunity(pmConn, bmoCategoryForecast.getStatusOpportunity().toString(), bmoCategoryForecast.getId()))
			bmUpdateResult.addError(bmoCategoryForecast.getStatusOpportunity().getName(),
					"El " + bmoCategoryForecast.getStatusOpportunity().getLabel() + " ya se encuentra en otra " + getSFParams().getProgramFormTitle(bmoCategoryForecast));
			
//		// Se almacena de forma preliminar para asignar ID
//		if (!(bmoCategoryForecast.getId() > 0)) {
//			newRecord = true;
//			super.save(pmConn, bmoCategoryForecast, bmUpdateResult);
//		}
		
		if (!bmUpdateResult.hasErrors())
			super.save(pmConn, bmoCategoryForecast, bmUpdateResult);

		return bmUpdateResult;
	}
	
	public boolean existStatusOpportunity(PmConn pmConn, String statusOpportunity, int categoryForecastId) throws SFPmException {
		String sql = "SELECT cafo_statusopportunity FROM " + bmoCategoryForecast.getKind()
		 			+ " WHERE cafo_statusopportunity LIKE '" + statusOpportunity + "' ";
		if (categoryForecastId > 0)
			sql += " AND cafo_categoryforecastid <> " + categoryForecastId;
		
		pmConn.doFetch(sql);
		return pmConn.next();
	}
}
