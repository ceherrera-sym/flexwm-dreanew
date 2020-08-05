/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.co;

import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.wf.BmoWFlowLog;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.server.op.PmSupplier;
import com.flexwm.server.wf.PmWFlowLog;
import com.flexwm.shared.co.BmoPropertySale;
import com.flexwm.shared.co.BmoPropertySaleDetail;
import com.flexwm.shared.op.BmoSupplier;

public class PmPropertySaleDetail extends PmObject {
	BmoPropertySaleDetail bmoPropertySaleDetail;
	
	public PmPropertySaleDetail(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoPropertySaleDetail = new BmoPropertySaleDetail();
		setBmObject(bmoPropertySaleDetail);
		
		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoPropertySaleDetail.getNotary(), bmoPropertySaleDetail.getBmoSupplier()),
				new PmJoin(bmoPropertySaleDetail.getBmoSupplier().getSupplierCategoryId(), bmoPropertySaleDetail.getBmoSupplier().getBmoSupplierCategory())
				)));
	}
		
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoPropertySaleDetail bmoPropertySaleDetail = (BmoPropertySaleDetail) autoPopulate(pmConn, new BmoPropertySaleDetail());		

		// BmoSupplier
		BmoSupplier bmoSupplier = new BmoSupplier();
		int supplierId = (int)pmConn.getInt(bmoSupplier.getIdFieldName());
		if (supplierId > 0) bmoPropertySaleDetail.setBmoSupplier((BmoSupplier) new PmSupplier(getSFParams()).populate(pmConn));
		else bmoPropertySaleDetail.setBmoSupplier(bmoSupplier);
		
		return bmoPropertySaleDetail;
	}
		
	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {		
		BmoPropertySaleDetail bmoPropertySaleDetail = (BmoPropertySaleDetail)bmObject;
		
		// Obtener la venta
		PmPropertySale pmPropertySale = new PmPropertySale(getSFParams());
		BmoPropertySale bmoPropertySale  = (BmoPropertySale)pmPropertySale.get(pmConn, bmoPropertySaleDetail.getPropertySaleId().toInteger());
		
		// Si hay notario en el detalle entra a generar bitacora
		if (bmoPropertySaleDetail.getNotary().toInteger() > 0) {
			
			// Obtener notario
			PmSupplier pmSupplier = new PmSupplier(getSFParams());
			BmoSupplier bmoSupplier = new BmoSupplier();
			bmoSupplier  = (BmoSupplier)pmSupplier.get(pmConn, bmoPropertySaleDetail.getNotary().toInteger());
		
			// Registra en la bitacora de la venta el Notario y Num. Escritura
			PmWFlowLog pmWFlowLog = new PmWFlowLog(getSFParams());
			pmWFlowLog.addLog(pmConn, bmUpdateResult, bmoPropertySale.getWFlowId().toInteger(), BmoWFlowLog.TYPE_OTHER, 
					"Notario: " + bmoSupplier.getCode().toString() + 
								" " + bmoSupplier.getName().toString() + "," +
					" Número de Escritura: " + bmoPropertySaleDetail.getWrintingNumber().toString());
		}
		if(!bmoPropertySaleDetail.getDescription().toString().equals("")) {
			String descriptionLog = SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat())
					+ " (" + getSFParams().getLoginInfo().getBmoUser().getCode() + ") "	
					+ "\n" + getSFParams().getFieldFormTitle(bmoPropertySaleDetail.getDescription()) + ": "+ bmoPropertySaleDetail.getDescription().toString()
					+ "\n\n" + bmoPropertySaleDetail.getDescriptionLog().toString();
			bmoPropertySaleDetail.getDescriptionLog().setValue(descriptionLog);
			bmoPropertySaleDetail.getDescription().setValue("");
		}
		super.save(pmConn, bmoPropertySaleDetail, bmUpdateResult);

		return bmUpdateResult;
	}
	
	// Revisar si existe un detalle en la venta de inmueble
	public boolean propertySaleDetailExists(PmConn pmConn, int propertysaleId) throws SFPmException {
		pmConn.doFetch("SELECT prsd_propertysaledetailid FROM propertysaledetails WHERE prsd_propertysaleid = " + propertysaleId);
		return pmConn.next();
	}
}
