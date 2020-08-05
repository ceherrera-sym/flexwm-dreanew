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

import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoSupplierPhone extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, number, extension, fax, supplierId;

	public static char TYPE_WORK = 'W';
	public static char TYPE_HOME = 'H';
	public static char TYPE_MOBILE = 'M';
	public static char TYPE_OTHER = 'O';
	
	public BmoSupplierPhone() {
		super("com.flexwm.server.op.PmSupplierPhone", "supplierphones", "supplierphoneid", "SUPH", "Teléfonos");
		
		// Campo de datos
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_MOBILE, "Móvil", "./icons/mobile.png"),
				new BmFieldOption(TYPE_HOME, "Casa", "./icons/home.png"),
				new BmFieldOption(TYPE_WORK, "Trabajo", "./icons/office.png"),
				new BmFieldOption(TYPE_OTHER, "Otro", "./icons/phone.png")
				)));
		
		number = setField("number", "", "Número", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		extension = setField("extension", "", "Ext.", 5, Types.VARCHAR, true, BmFieldType.STRING, false);
		fax = setField("fax", "", "Tel. Fax", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		supplierId = setField("supplierid", "", "Proveedor", 8, Types.INTEGER, true, BmFieldType.ID, false);		
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
						getType(), 
						getNumber(),
						getFax(),
						getExtension()
						));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getNumber().getName(), getNumber().getLabel())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getNumber(), BmOrder.ASC)));
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getNumber() {
		return number;
	}

	public void setNumber(BmField number) {
		this.number = number;
	}

	public BmField getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(BmField supplierId) {
		this.supplierId = supplierId;
	}
	
	public BmField getExtension() {
		return extension;
	}

	public void setExtension(BmField extension) {
		this.extension = extension;
	}

	public BmField getFax() {
		return fax;
	}

	public void setFax(BmField fax) {
		this.fax = fax;
	}
	
}
