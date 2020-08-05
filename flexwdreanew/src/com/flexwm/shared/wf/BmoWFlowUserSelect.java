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
import com.symgae.shared.sf.BmoArea;
import com.symgae.shared.sf.BmoProfile;


public class BmoWFlowUserSelect extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField areaId, profileId;
	BmoArea bmoArea = new BmoArea();
	BmoProfile bmoProfile = new BmoProfile();

	public BmoWFlowUserSelect() {
		super("com.flexwm.server.wf.PmWFlowUserSelect", "wflowuserselect", "wflowuserselectid", "WFUS", "Resp. Asign. Usuarios");

		areaId = setField("areaid", "", "Departamento", 20, Types.INTEGER, false, BmFieldType.ID, false);
		profileId = setField("profileid", "", "Perfil", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoArea().getName(),
				getBmoProfile().getName()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoArea().getName())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoArea().getName(), BmOrder.ASC)));
	}

	public BmField getAreaId() {
		return areaId;
	}

	public void setAreaId(BmField areaId) {
		this.areaId = areaId;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setGroupId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmoArea getBmoArea() {
		return bmoArea;
	}

	public void setBmoArea(BmoArea bmoArea) {
		this.bmoArea = bmoArea;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}
}
