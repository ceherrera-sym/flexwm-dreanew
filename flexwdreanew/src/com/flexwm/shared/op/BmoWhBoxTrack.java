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


public class BmoWhBoxTrack extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField quantity, whBoxId, whTrackId;
	private BmoWhBox bmoWhBox = new BmoWhBox();
	private BmoWhTrack bmoWhTrack = new BmoWhTrack();
	
	public BmoWhBoxTrack() {
		super("com.flexwm.server.op.PmWhBoxTrack", "whboxtracks", "whboxtrackid", "WHBT", "Caja Rastreo");
		
		quantity = setField("quantity", "", "Cantidad", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);
		whBoxId = setField("whboxid", "", "Caja Productos", 8, Types.INTEGER, false, BmFieldType.ID, false);		
		whTrackId = setField("whtrackid", "", "Producto", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoWhTrack().getBmoProduct().getCode(),
				getBmoWhTrack().getBmoProduct().getName(),
				getBmoWhTrack().getBmoProduct().getModel(),
				getBmoWhTrack().getSerial(),
				getQuantity()
				
		));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoWhTrack().getBmoProduct().getCode()),
				new BmSearchField(getBmoWhTrack().getBmoProduct().getName()),
				new BmSearchField(getBmoWhTrack().getBmoProduct().getBrand()),
				new BmSearchField(getBmoWhTrack().getBmoProduct().getModel()),
				new BmSearchField(getBmoWhTrack().getSerial())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoWhTrack().getBmoProduct().getCode(), BmOrder.ASC)
				));
	}

	public BmField getWhBoxId() {
		return whBoxId;
	}

	public void setWhBoxId(BmField whBoxId) {
		this.whBoxId = whBoxId;
	}

	public BmField getWhTrackId() {
		return whTrackId;
	}

	public void setWhTrackId(BmField whTrackId) {
		this.whTrackId = whTrackId;
	}

	public BmoWhBox getBmoWhBox() {
		return bmoWhBox;
	}

	public void setBmoWhBox(BmoWhBox bmoWhBox) {
		this.bmoWhBox = bmoWhBox;
	}

	public BmoWhTrack getBmoWhTrack() {
		return bmoWhTrack;
	}

	public void setBmoWhTrack(BmoWhTrack bmoWhTrack) {
		this.bmoWhTrack = bmoWhTrack;
	}

	public BmField getQuantity() {
		return quantity;
	}

	public void setQuantity(BmField quantity) {
		this.quantity = quantity;
	}
}
