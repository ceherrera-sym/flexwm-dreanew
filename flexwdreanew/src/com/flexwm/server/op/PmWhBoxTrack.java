/**
 * 
 */
package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;

import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhBox;
import com.flexwm.shared.op.BmoWhBoxTrack;
import com.flexwm.shared.op.BmoWhSection;
import com.flexwm.shared.op.BmoWhStock;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWhBoxTrack extends PmObject {
	BmoWhBoxTrack bmoWhBoxTrack;

	public PmWhBoxTrack(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhBoxTrack = new BmoWhBoxTrack();
		setBmObject(bmoWhBoxTrack);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWhBoxTrack.getWhBoxId(), bmoWhBoxTrack.getBmoWhBox()),
				new PmJoin(bmoWhBoxTrack.getWhTrackId(), bmoWhBoxTrack.getBmoWhTrack()),
				new PmJoin(bmoWhBoxTrack.getBmoWhTrack().getProductId(), bmoWhBoxTrack.getBmoWhTrack().getBmoProduct()),
				new PmJoin(bmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getProductFamilyId(), bmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getProductGroupId(), bmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getUnitId(), bmoWhBoxTrack.getBmoWhTrack().getBmoProduct().getBmoUnit())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWhBoxTrack = (BmoWhBoxTrack) autoPopulate(pmConn, new BmoWhBoxTrack());		

		// BmoWhTrack
		BmoWhTrack bmoWhTrack = new BmoWhTrack();
		int whTrackIdId = (int)pmConn.getInt(bmoWhTrack.getIdFieldName());
		if (whTrackIdId > 0) bmoWhBoxTrack.setBmoWhTrack((BmoWhTrack) new PmWhTrack(getSFParams()).populate(pmConn));
		else bmoWhBoxTrack.setBmoWhTrack(bmoWhTrack);

		// BmoWhBox
		BmoWhBox bmoWhBox = new BmoWhBox();
		int whBoxIdId = (int)pmConn.getInt(bmoWhBox.getIdFieldName());
		if (whBoxIdId > 0) bmoWhBoxTrack.setBmoWhBox((BmoWhBox) new PmWhBox(getSFParams()).populate(pmConn));
		else bmoWhBoxTrack.setBmoWhBox(bmoWhBox);

		return bmoWhBoxTrack;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoWhBoxTrack = (BmoWhBoxTrack)bmObject;

		// Obtener info del rastreo
		PmWhTrack pmWhTrack = new PmWhTrack(getSFParams());
		BmoWhTrack bmoWhTrack = (BmoWhTrack)pmWhTrack.get(pmConn, bmoWhBoxTrack.getWhTrackId().toInteger());

		BmoWhStock bmoWhStock = new BmoWhStock();
		PmWhStock pmWhStock = new PmWhStock(getSFParams());

		// Obtener empresa del almacen
		PmWhSection pmWhSection = new PmWhSection(getSFParams());
		BmoWhSection bmoWhSection = new  BmoWhSection();
		int companyId = -1, whSectionId = -1;
		whSectionId = pmWhStock.getWhSectionByWhTrackId(pmConn, bmoWhTrack.getId());

		if (whSectionId > 0) {
			bmoWhSection = (BmoWhSection)pmWhSection.get(whSectionId);
			companyId = bmoWhSection.getBmoWarehouse().getCompanyId().toInteger();
		}
		// Revisar existencia suficiente en almacen
		bmoWhStock.getProductId().setValue(bmoWhTrack.getProductId().toInteger());
		double stockQuantity = pmWhStock.getStockQuantity(bmoWhStock, companyId);

		// Si el producto ya esta en la caja, ya no se puede duplicar
		if (!(bmoWhBoxTrack.getId() > 0)) {
			if (this.productInWhBox(pmConn, bmoWhBoxTrack) > 0)
				bmUpdateResult.addError(bmoWhBoxTrack.getWhTrackId().getName(), "Producto ya existe en la Caja.");
		}

		// Obtener Producto
		BmoProduct bmoProduct = new BmoProduct();
		PmProduct pmProduct = new PmProduct(getSFParams());
		bmoProduct = (BmoProduct)pmProduct.get(pmConn, bmoWhTrack.getProductId().toInteger());

		// Validar que la cantidad no sea fraccion si el producto es SERIE
		// Si es Sin Rastreo, verificar por la Unidad del producto
		if (!pmProduct.applyFraction(bmoProduct, bmoWhBoxTrack.getQuantity().toDouble()))
			bmUpdateResult.addError(bmoWhBoxTrack.getQuantity().getName(), "<b>La Cantidad del Producto no acepta decimales.</b>");

		if (bmoWhBoxTrack.getQuantity().toDouble() > stockQuantity) 
			bmUpdateResult.addError(bmoWhBoxTrack.getQuantity().getName(), "No hay suficiente inventario del producto; Existencia: " + stockQuantity);

		return super.save(pmConn, bmoWhBoxTrack, bmUpdateResult);
	}

	public int productInWhBox(PmConn pmConn, BmoWhBoxTrack bmoWhBoxTrack) throws SFPmException {
		int countProductInWhBox = 0;
		pmConn.doFetch("SELECT COUNT(*) AS countProductInWhBox FROM whboxtracks " +
				" LEFT JOIN whtracks ON (whtr_whtrackid = whbt_whtrackid) " +
				" LEFT JOIN whboxes ON (whbx_whboxid = whbt_whboxid) " + 
				" WHERE whbx_whboxid = " + bmoWhBoxTrack.getWhBoxId().toInteger() +
				" AND whbt_whtrackid = " + bmoWhBoxTrack.getWhTrackId().toInteger());

		if (pmConn.next()) countProductInWhBox = pmConn.getInt("countProductInWhBox");
		return countProductInWhBox;
	}

}
