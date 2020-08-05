/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.fi;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.flexwm.shared.op.BmoProductGroup;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoProfile;


/**
 * @author jhernandez
 *
 */

public class BmoCommission extends BmObject implements Serializable{

	private static final long serialVersionUID = 1L;
	private BmField profileId, productGroupId, percentage;
	public static final char STATUS_REVISION = 'R';
	public static final char STATUS_AUTHORIZED = 'A';
	public static String ACTION_GETCOMMISSION = "GETCOMISS";
	private BmoProfile bmoProfile = new BmoProfile();
	private BmoProductGroup  bmoProductGroup = new BmoProductGroup();

	public BmoCommission() {
		super("com.flexwm.server.fi.PmCommission","commissions", "commissionid", "COMI","Comisiones");

		//Campo de Datos		
		profileId = setField("profileid", "", "Perfil", 11, Types.INTEGER, false, BmFieldType.ID, false);
		productGroupId = setField("productgroupid", "", "Grupo Producto", 30, Types.VARCHAR, true, BmFieldType.STRING, false);
		percentage = setField("percentage", "", "% Comisión", 20, Types.DOUBLE, false, BmFieldType.NUMBER, false);

	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(				
				getBmoProductGroup().getName(),
				getBmoProfile().getName(),
				getPercentage()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProfile().getName().getName(), getBmoProfile().getName().getLabel()), 
				new BmSearchField(getBmoProductGroup().getName().getName(), getBmoProductGroup().getName().getLabel())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getIdField(), BmOrder.ASC)));
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setGroupId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmField getProductGroupId() {
		return productGroupId;
	}

	public void setProductGroupId(BmField productGroupId) {
		this.productGroupId = productGroupId;
	}

	public BmField getPercentage() {
		return percentage;
	}

	public void setPercentage(BmField percentage) {
		this.percentage = percentage;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}

	public BmoProductGroup getBmoProductGroup() {
		return bmoProductGroup;
	}

	public void setBmoProductGroup(BmoProductGroup bmoProductGroup) {
		this.bmoProductGroup = bmoProductGroup;
	}	

}

