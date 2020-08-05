/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.in;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.flexwm.shared.cm.BmoCustomer;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;


public class BmoPolicyRecipient extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField relation, percentage, policyId, customerId;
	private BmoCustomer bmoCustomer = new BmoCustomer();

	public static char RELATION_SPOUSE = 'S';
	public static char RELATION_FATHER = 'F';
	public static char RELATION_MOTHER = 'M';
	public static char RELATION_BROTHER = 'B';
	public static char RELATION_SON = 'C';
	public static char RELATION_OTHER = 'O';

	public BmoPolicyRecipient() {
		super("com.flexwm.server.in.PmPolicyRecipient", "policyrecipients", "policyrecipientid", "PORC", "Beneficiarios");

		relation = setField("relation", "", "Relación", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		relation.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(RELATION_SPOUSE, "Esposo(a)", "./icons/spouse.png"),
				new BmFieldOption(RELATION_FATHER, "Padre", "./icons/male.png"),
				new BmFieldOption(RELATION_MOTHER, "Madre", "./icons/female.png"),
				new BmFieldOption(RELATION_BROTHER, "Hermano(a)", "./icons/brother.png"),
				new BmFieldOption(RELATION_SON, "Hijo(a)", "./icons/users.png"),
				new BmFieldOption(RELATION_OTHER, "Otro", "./icons/user.png")
				)));

		percentage = setField("percentage", "", "Porcentaje (%)", 20, Types.DOUBLE, true, BmFieldType.PERCENTAGE, false);

		policyId = setField("policyid", "", "Póliza", 8, Types.INTEGER, false, BmFieldType.ID, false);
		customerId = setField("customerid", "", "Beneficiario", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoCustomer().getDisplayName(),
				getRelation(), 
				getPercentage()
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getPercentage(), BmOrder.DESC)));
	}

	public BmField getRelation() {
		return relation;
	}

	public void setRelation(BmField relation) {
		this.relation = relation;
	}

	public BmField getPercentage() {
		return percentage;
	}

	public void setPercentage(BmField percentage) {
		this.percentage = percentage;
	}

	public BmField getPolicyId() {
		return policyId;
	}

	public void setPolicyId(BmField policyId) {
		this.policyId = policyId;
	}

	public BmField getCustomerId() {
		return customerId;
	}

	public void setCustomerId(BmField customerId) {
		this.customerId = customerId;
	}

	public BmoCustomer getBmoCustomer() {
		return bmoCustomer;
	}

	public void setBmoCustomer(BmoCustomer bmoCustomer) {
		this.bmoCustomer = bmoCustomer;
	}
}
