package com.flexwm.server.op;

import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.op.BmoProduct;
import com.flexwm.shared.op.BmoWhMovItem;
import com.flexwm.shared.op.BmoWhMovement;
import com.flexwm.shared.op.BmoWhTrack;
import com.symgae.server.PmConn;
import com.symgae.server.PmJoin;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;


public class PmWhTrack extends PmObject {
	BmoWhTrack bmoWhTrack;

	public PmWhTrack(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoWhTrack = new BmoWhTrack();
		setBmObject(bmoWhTrack);

		// Lista de joins
		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
				new PmJoin(bmoWhTrack.getProductId(), bmoWhTrack.getBmoProduct()),
				new PmJoin(bmoWhTrack.getBmoProduct().getProductFamilyId(), bmoWhTrack.getBmoProduct().getBmoProductFamily()),
				new PmJoin(bmoWhTrack.getBmoProduct().getProductGroupId(), bmoWhTrack.getBmoProduct().getBmoProductGroup()),
				new PmJoin(bmoWhTrack.getBmoProduct().getUnitId(), bmoWhTrack.getBmoProduct().getBmoUnit())
				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		bmoWhTrack = (BmoWhTrack) autoPopulate(pmConn, new BmoWhTrack());		

		// BmoProduct
		BmoProduct bmoProduct = new BmoProduct();
		int productIdId = (int)pmConn.getInt(bmoProduct.getIdFieldName());
		if (productIdId > 0) bmoWhTrack.setBmoProduct((BmoProduct) new PmProduct(getSFParams()).populate(pmConn));
		else bmoWhTrack.setBmoProduct(bmoProduct);

		return bmoWhTrack;
	}

	public BmUpdateResult updateWhTrack(PmConn pmConn, BmoWhMovement bmoWhMovement, BmoWhMovItem bmoWhMovItem, BmoWhTrack bmoWhTrack, double inQuantity, double outQuantity, BmUpdateResult bmUpdateResult) throws SFException {
		this.bmoWhTrack = bmoWhTrack;

		// Si es nuevo, asigna el movitemid
		if (!(bmoWhTrack.getId() > 0))
			bmoWhTrack.getWhMovItemId().setValue(bmoWhMovItem.getId());

		bmoWhTrack.getInQuantity().setValue(inQuantity);
		bmoWhTrack.getOutQuantity().setValue(outQuantity);
		bmoWhTrack.getDatemov().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		bmoWhTrack.getSerial().setValue(bmoWhMovItem.getSerial().toString());
		bmoWhTrack.getDatemov().setValue(SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()));
		bmoWhTrack.getProductId().setValue(bmoWhMovItem.getProductId().toInteger());

		save(pmConn, bmoWhTrack, bmUpdateResult);

		return bmUpdateResult;
	}

	// Busca por clave de producto
	public BmoWhTrack searchSerialByBarcode(PmConn pmConn, String barcode) {
		bmoWhTrack = new BmoWhTrack();

		try {
			bmoWhTrack = (BmoWhTrack)this.getBy(pmConn, barcode, bmoWhTrack.getSerial().getName());	
		} catch (SFException e) {
			System.out.println("No se encontro el Código de Barras: " + barcode + " como Número de Serie / Lote.");
		}

		return bmoWhTrack;
	}
}
