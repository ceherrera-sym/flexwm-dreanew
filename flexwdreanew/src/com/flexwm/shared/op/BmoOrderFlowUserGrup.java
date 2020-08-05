package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmSearchField;
import com.symgae.shared.sf.BmoProfile;

public class BmoOrderFlowUserGrup extends BmObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private BmField  profileId;
	private BmoProfile bmoProfile = new BmoProfile();


	public BmoOrderFlowUserGrup() {
		super("com.flexwm.server.op.PmOrderFlowUserGrup", "orderflowusergroup", "orderflowusergroupid", "OFUG", "Grupos Wflow");		

		profileId = setField("profileid", "", "Perfil", 20, Types.INTEGER, false, BmFieldType.ID, false);
	}
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
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

}
