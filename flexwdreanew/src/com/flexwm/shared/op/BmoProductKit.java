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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoProductKit extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, amount, image;

	public BmoProductKit() {
		super("com.flexwm.server.op.PmProductKit", "productkits", "productkitid", "PRKT", "Kits Productos");

		name = setField("name", "", "Nombre Kit", 50, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 512, Types.VARCHAR, true, BmFieldType.STRING, false);
		amount = setField("amount", "", "Precio", 20, Types.FLOAT, false, BmFieldType.CURRENCY, false);
		image = setField("image", "", "Imagen", 500, Types.VARCHAR, true, BmFieldType.IMAGE, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getAmount()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getAmount()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getAmount()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()), 
				new BmSearchField(getDescription())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

	public BmField getName() {
		return name;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getAmount() {
		return amount;
	}

	public void setAmount(BmField amount) {
		this.amount = amount;
	}

	public BmField getImage() {
		return image;
	}

	public void setImage(BmField image) {
		this.image = image;
	}
}
