/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.wf;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoProfile;


public class BmoWFlowCategoryProfile extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField required, autoDate, profileId, autoProfile, wFlowCategoryId;
	private BmoWFlowCategory bmoWFlowCategory = new BmoWFlowCategory();

	private BmoProfile bmoProfile = new BmoProfile();

	public BmoWFlowCategoryProfile() {
		super("com.flexwm.server.wf.PmWFlowCategoryProfile", "wflowcategoryprofiles", "wflowcategoryprofileid", "WFCP", "Perfiles Cat. WFlow");

		// Campos de datos
		required = setField("required", "", "Requerido?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		autoDate = setField("autodate", "1", "Fecha Autom.?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		autoProfile = setField("autoprofile", "", "Crear Reg. Automático", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);

		profileId = setField("profileid", "", "Perfil", 8, Types.INTEGER, false, BmFieldType.ID, false);
		wFlowCategoryId = setField("wflowcategoryid", "", "Categoría de Flujo", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {		
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProfile().getName(),
				getAutoDate(),
				getRequired(),
				getAutoProfile()
				));
	}	

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProfile().getName())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(
				new BmOrder(getKind(), getBmoProfile().getName(), BmOrder.ASC)
				));
	}

	public BmField getRequired() {
		return required;
	}

	public void setRequired(BmField required) {
		this.required = required;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setGroupId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}

	public BmField getWFlowCategoryId() {
		return wFlowCategoryId;
	}

	public void setWFlowCategoryId(BmField wFlowCategoryId) {
		this.wFlowCategoryId = wFlowCategoryId;
	}

	public BmoWFlowCategory getBmoWFlowCategory() {
		return bmoWFlowCategory;
	}

	public void setBmoWFlowCategory(BmoWFlowCategory bmoWFlowCategory) {
		this.bmoWFlowCategory = bmoWFlowCategory;
	}

	public BmField getAutoDate() {
		return autoDate;
	}

	public void setAutoDate(BmField autoDate) {
		this.autoDate = autoDate;
	}

	public BmField getAutoProfile() {
		return autoProfile;
	}

	public void setAutoProfile(BmField autoProfile) {
		this.autoProfile = autoProfile;
	}

}
