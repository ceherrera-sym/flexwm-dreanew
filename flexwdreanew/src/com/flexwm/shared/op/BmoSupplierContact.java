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


public class BmoSupplierContact extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField firstName, fatherLastName, motherLastName, email, position, number, extension, supplierId, alias, comments, cellPhone;


	public BmoSupplierContact() {
		super("com.flexwm.server.op.PmSupplierContact", "suppliercontacts", "suppliercontactid", "SUCO", "Contactos");
		firstName = setField("firstname", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		fatherLastName = setField("fatherlastname", "", "A. Paterno", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		motherLastName = setField("motherlastname", "", "A. Materno", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		position = setField("position", "", "Cargo", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		email = setField("email", "", "Email", 50, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		number = setField("number", "", "Teléfono", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		cellPhone = setField("cellphone", "", "Móvil", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		extension = setField("extension", "", "Ext.", 5, Types.VARCHAR, true, BmFieldType.STRING, false);
		supplierId = setField("supplierid", "", "Proveedor", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		alias = setField("alias", "", "Alias", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		comments = setField("comments", "", "Comentarios", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getFirstName(),
				getFatherLastName(),
				getPosition(),
				getEmail(),
				getCellPhone(),
				getNumber(),
				getExtension()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getFirstName().getName(), getNumber().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
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

	public BmField getFirstName() {
		return firstName;
	}

	public void getFirstName(BmField firstName) {
		this.firstName = firstName;
	}

	public BmField getEmail() {
		return email;
	}

	public void setEmail(BmField email) {
		this.email = email;
	}

	public BmField getAlias() {
		return alias;
	}

	public void setAlias(BmField alias) {
		this.alias = alias;
	}

	public BmField getComments() {
		return comments;
	}

	public void setComments(BmField comments) {
		this.comments = comments;
	}

	public BmField getPosition() {
		return position;
	}

	public void setPosition(BmField position) {
		this.position = position;
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
}
