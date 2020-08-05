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
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoCustomerRelative extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField fullName, fatherLastName, motherLastName, type, email, number, cellPhone, extension, customerId, responsible;

	public static char TYPE_SPOUSE = 'S';
	public static char TYPE_FATHER = 'F';
	public static char TYPE_MOTHER = 'M';
	public static char TYPE_BROTHER = 'B';
	public static char TYPE_SON = 'C';
	public static char TYPE_OTHER = 'O';

	public BmoCustomerRelative() {
		super("com.flexwm.server.cm.PmCustomerRelative", "customerrelatives", "customerrelativeid", "CURL", "Familiares");

		type = setField("type", "", "Tipo", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_SPOUSE, "Esposo(a)", "./icons/spouse.png"),
				new BmFieldOption(TYPE_FATHER, "Padre", "./icons/male.png"),
				new BmFieldOption(TYPE_MOTHER, "Madre", "./icons/female.png"),
				new BmFieldOption(TYPE_BROTHER, "Hermano(a)", "./icons/brother.png"),
				new BmFieldOption(TYPE_SON, "Hijo(a)", "./icons/users.png"),
				new BmFieldOption(TYPE_OTHER, "Otro", "./icons/user.png")
				)));

		fullName = setField("fullname", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		fatherLastName = setField("fatherlastname", "", "Apellido Paterno", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		motherLastName = setField("motherlastname", "", "Apellido Materno", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		email = setField("email", "", "Email", 50, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		number = setField("number", "", "Teléfono", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		cellPhone = setField("cellphone", "", "T. Celular", 15, Types.VARCHAR, false, BmFieldType.PHONE, false);
		extension = setField("extension", "", "Ext.", 5, Types.VARCHAR, true, BmFieldType.STRING, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);
		responsible = setField("responsible", "", "Responsable", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getType(),
				getFullName(), 
				getFatherLastName(),
				getMotherLastName(),
				getEmail(),
				getCellPhone(),
				getNumber(),
				getExtension()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getNumber().getName(), getNumber().getLabel()),
				new BmSearchField(getFatherLastName().getName(), getFatherLastName().getLabel()),
				new BmSearchField(getMotherLastName().getName(), getFatherLastName().getLabel()),
				new BmSearchField(getFullName().getName(), getFatherLastName().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getNumber() {
		return number;
	}

	public void setNumber(BmField number) {
		this.number = number;
	}

	public BmField getExtension() {
		return extension;
	}

	public void setExtension(BmField extension) {
		this.extension = extension;
	}

	public BmField getEmail() {
		return email;
	}

	public void setEmail(BmField email) {
		this.email = email;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getFullName() {
		return fullName;
	}

	public void setFullName(BmField fullName) {
		this.fullName = fullName;
	}

	public BmField getFatherLastName() {
		return fatherLastName;
	}

	public void setFatherLastName(BmField fatherLastName) {
		this.fatherLastName = fatherLastName;
	}

	public BmField getMotherLastName() {
		return motherLastName;
	}

	public void setMotherLastName(BmField motherLastName) {
		this.motherLastName = motherLastName;
	}

	public BmField getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(BmField cellPhone) {
		this.cellPhone = cellPhone;
	}

	public BmField getResponsible() {
		return responsible;
	}

	public void setResponsible(BmField responsible) {
		this.responsible = responsible;
	}	

}
