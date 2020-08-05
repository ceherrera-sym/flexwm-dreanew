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
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoCustomerDate extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField type, relevantDate, description, customerId, emailReminder, remindDate;

	public static char TYPE_BIRTHDAY = 'B';
	public static char TYPE_ANNIVERSARY = 'A';
	public static char TYPE_OTHER = 'O';

	public BmoCustomerDate() {
		super("com.flexwm.server.cm.PmCustomerDate", "customerdates", "customerdateid", "CUDA", "Fechas Importantes");

		// Campo de datos
		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_BIRTHDAY, "Cumpleaños", "./icons/gift.png"),
				new BmFieldOption(TYPE_ANNIVERSARY, "Aniversario", "./icons/diamond-o.png"),
				new BmFieldOption(TYPE_OTHER, "Otra", "./icons/calendar.png")
				)));

		relevantDate = setField("relevantdate", "", "Fecha", 12, Types.DATE, false, BmFieldType.DATE, false);
		description = setField("description", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);		
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);	
		emailReminder = setField("emailreminder", "", "Notificación", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		remindDate = setField("reminddate", "0", "Recordar x días antes", 3, Types.INTEGER, true, BmFieldType.NUMBER, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getType(),
				getRelevantDate(),
				getDescription(),
				getEmailReminder(),
				getRemindDate()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getRelevantDate().getName(), getRelevantDate().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getRelevantDate(), BmOrder.ASC)));
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getRelevantDate() {
		return relevantDate;
	}

	public void setRelevantDate(BmField relevantDate) {
		this.relevantDate = relevantDate;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getEmailReminder() {
		return emailReminder;
	}

	public BmField getRemindDate() {
		return remindDate;
	}

	public void setEmailReminder(BmField emailReminder) {
		this.emailReminder = emailReminder;
	}

	public void setRemindDate(BmField remindDate) {
		this.remindDate = remindDate;
	}

}
