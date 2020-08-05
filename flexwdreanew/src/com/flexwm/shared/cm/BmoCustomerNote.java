/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoCustomerNote extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField customerId, type, notes, file;

	public static char TYPE_PHONE = 'P';
	public static char TYPE_EMAIL = 'E';
	public static char TYPE_DATA = 'D';
	public static char TYPE_SYS = 'S';
	public static char TYPE_OTHER = 'O';

	public BmoCustomerNote() {
		super("com.flexwm.server.cm.PmCustomerNote", "customernotes", "customernoteid", "CUNO", "Notas");

		// Campo de datos
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);	

		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_PHONE, "Llamada Telefónica", "./icons/phone.png"),
				new BmFieldOption(TYPE_EMAIL, "Email", "./icons/email.png"),
				new BmFieldOption(TYPE_DATA, "Archivo", "./icons/file.png"),
				new BmFieldOption(TYPE_SYS, "Sistema", "./icons/window.png"),
				new BmFieldOption(TYPE_OTHER, "Otros", "./icons/cuno_other.png")		
				)));
		notes = setField("notes", "", "Notas", 500, Types.VARCHAR, false, BmFieldType.STRING, false);
		file = setField("file", "", "Documento", 255, Types.VARCHAR, true, BmFieldType.FILEUPLOAD, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getType(),
				getNotes(),
				getFile()
				));
	}

	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getType(),
				getNotes()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getNotes().getName(), getNotes().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public BmField getType() {
		return type;
	}

	public BmField getNotes() {
		return notes;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public void setNotes(BmField notes) {
		this.notes = notes;
	}

	public BmField getFile() {
		return file;
	}

	public void setFile(BmField file) {
		this.file = file;
	}

}
