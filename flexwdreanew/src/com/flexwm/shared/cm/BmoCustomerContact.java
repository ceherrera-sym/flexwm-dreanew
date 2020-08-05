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
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoCustomerContact extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField fullName,fatherLastName, motherLastName, email, position, number, extension, customerId, 
	alias, commentAlias, cellPhone, contactMain, titleId;
	 
	private BmoCustomer bmoCustomer = new BmoCustomer();

	public BmoCustomerContact() {
		super("com.flexwm.server.cm.PmCustomerContact", "customercontacts", "customercontactid", "CUCO", "Contactos");
		fullName = setField("fullname", "", "Nombre", 50, Types.VARCHAR, false, BmFieldType.STRING, false);
		fatherLastName = setField("fatherlastname", "", "A. Paterno", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		motherLastName = setField("motherlastname", "", "A. Materno", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		titleId = setField("titleid", "", "Título", 8, Types.INTEGER, true, BmFieldType.ID, false);
		position = setField("position", "", "Cargo", 50, Types.VARCHAR, true, BmFieldType.STRING, false);
		email = setField("email", "", "Email", 50, Types.VARCHAR, true, BmFieldType.EMAIL, false);
		number = setField("number", "", "Teléfono", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		cellPhone = setField("cellphone", "", "T. Celular", 15, Types.VARCHAR, true, BmFieldType.PHONE, false);
		extension = setField("extension", "", "Ext.", 5, Types.VARCHAR, true, BmFieldType.STRING, false);
		customerId = setField("customerid", "", "Cliente", 8, Types.INTEGER, true, BmFieldType.ID, false);		
		alias = setField("alias", "", "Alias", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		commentAlias = setField("commentalias", "", "Comentario", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		contactMain = setField("contactmain", "", "Contacto Principal", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);		
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getFullName(),
				getFatherLastName(),
				getBmoCustomer().getCode(),
				getBmoCustomer().getDisplayName(),
				getPosition(),
				getEmail(),
				getCellPhone(),
				getNumber(),
				getExtension()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getFullName(),
				getFatherLastName(),
				getEmail()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getFullName().getName(), getNumber().getLabel())));
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

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmField getExtension() {
		return extension;
	}

	public void setExtension(BmField extension) {
		this.extension = extension;
	}

	public BmField getFullName() {
		return fullName;
	}

	public void setFullName(BmField fullName) {
		this.fullName = fullName;
	}

	public BmField getEmail() {
		return email;
	}

	public void setEmail(BmField email) {
		this.email = email;
	}

	/**
	 * @return the alias
	 */
	public BmField getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(BmField alias) {
		this.alias = alias;
	}

	/**
	 * @return the commentAlias
	 */
	public BmField getCommentAlias() {
		return commentAlias;
	}

	/**
	 * @param commentAlias the commentAlias to set
	 */
	public void setCommentAlias(BmField commentAlias) {
		this.commentAlias = commentAlias;
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

	public BmField getContactMain() {
		return contactMain;
	}

	public void setContactMain(BmField contactMain) {
		this.contactMain = contactMain;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}

	public BmField getTitleId() {
		return titleId;
	}

	public void setTitleId(BmField titleId) {
		this.titleId = titleId;
	}	
		
}
