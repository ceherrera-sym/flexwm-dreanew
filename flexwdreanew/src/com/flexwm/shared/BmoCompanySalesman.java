package com.flexwm.shared;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoProfile;

public class BmoCompanySalesman extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BmoProfile bmoProfile = new BmoProfile();

	private BmField profileId, coordinatorProfileId, companyId, flexConfigId;
	public BmoCompanySalesman() {
		super("com.flexwm.server.PmCompanySalesman", "companysalesmen", "companysalesmanid", "COSA", "Perfil Vendedor x Emp.");
		companyId = setField("companyid", "", "Empresa", 11, Types.INTEGER, false, BmFieldType.ID, false);
		profileId = setField("profileid", "", "Perfil Vendedor", 11, Types.INTEGER, false, BmFieldType.ID, false);
		coordinatorProfileId = setField("coordinatorprofileid", "", "Perfil Coordinador", 11, Types.INTEGER, false, BmFieldType.ID, false);

		flexConfigId = setField("flexconfigid", "", "Conf. Flex ID", 11, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProfile().getName()
				));
	}
	
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProfile().getName()
				));
	}
	
	@Override
	public ArrayList<BmField> getMobileFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getBmoProfile().getName()
				));
	}
	
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getBmoProfile().getName())
				));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoProfile().getName(), BmOrder.ASC)));
	}
	
	public BmField getProfileId() {
		return profileId;
	}
	
	public void setProfileId(BmField profileId) {
		this.profileId = profileId;
	}
	
	public BmField getCompanyId() {
		return companyId;
	}
	
	public void setCompanyId(BmField companyId) {
		this.companyId = companyId;
	}

	public BmField getFlexConfigId() {
		return flexConfigId;
	}

	public void setFlexConfigId(BmField flexConfigId) {
		this.flexConfigId = flexConfigId;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}

	public BmField getCoordinatorProfileId() {
		return coordinatorProfileId;
	}

	public void setCoordinatorProfileId(BmField coordinatorProfileId) {
		this.coordinatorProfileId = coordinatorProfileId;
	}

}
