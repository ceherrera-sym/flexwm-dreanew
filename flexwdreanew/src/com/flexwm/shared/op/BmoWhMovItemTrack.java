/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoWhMovItemTrack extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField quantity, whMovItemId, whTrackId;
	private BmoWhTrack bmoWhTrack;
	
	public BmoWhMovItemTrack() {
		super("com.flexwm.server.op.PmWhMovItemTrack", "whmovitems", "whmovitemid", "WHMI", "Item Mov. Almacén");
		
		//Campo de Datos
		quantity = setField("quantity", "", "Cantidad", 8, Types.INTEGER, false, BmFieldType.NUMBER, false);
		whMovItemId = setField("whmovitemid", "", "Movimiento", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		whTrackId = setField("whtrackid", "", "Rastreo", 8, Types.INTEGER, true, BmFieldType.ID, false);
		
		bmoWhTrack = new BmoWhTrack();
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getQuantity(),
				getBmoWhTrack().getBmoProduct().getCode()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoWhTrack().getBmoProduct().getCode())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoWhTrack().getBmoProduct().getCode(), BmOrder.ASC)
				));
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}

	public BmField getWhMovItemId() {
		return whMovItemId;
	}

	public void setWhMovItemId(BmField whMovItemId) {
		this.whMovItemId = whMovItemId;
	}

	public BmField getWhTrackId() {
		return whTrackId;
	}

	public void setWhTrackId(BmField whTrackId) {
		this.whTrackId = whTrackId;
	}

	public BmoWhTrack getBmoWhTrack() {
		return bmoWhTrack;
	}

	public void setBmoWhTrack(BmoWhTrack bmoWhTrack) {
		this.bmoWhTrack = bmoWhTrack;
	}
}
