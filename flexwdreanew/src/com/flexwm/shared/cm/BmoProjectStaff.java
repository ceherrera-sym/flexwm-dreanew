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
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoUser;

public class BmoProjectStaff extends BmObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private BmField code,projectId,notes,profileId,userId;
	BmoUser bmoUser = new BmoUser();
	BmoProfile  bmoProfile = new BmoProfile();

	public BmoProjectStaff() {
		super("com.flexwm.server.cm.PmProjectStaff", "projectstaff", "projectstaffid", "PSTF", "Personal del Proyecto");
		
		code = setField("code", "", "Clave Personal",50 , Types.VARCHAR, false, BmFieldType.CODE, false);
		notes = setField("notes", "", "Notas", 1000, Types.VARCHAR, true, BmFieldType.STRING, false);
		
		projectId = setField("projectid", "", "Proyecto", 5, Types.INTEGER, false, BmFieldType.ID, false);
		profileId = setField("profileid", "", "Puesto", 5, Types.INTEGER, false, BmFieldType.ID, false);
		userId = setField("userid", "", "Usuario", 5, Types.INTEGER, false, BmFieldType.ID, false);
	}
	
	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode(), 
				getBmoUser().getCode(), 		
				getBmoProfile().getName(),
				getBmoUser().getBmoArea().getName()				
				));
	}
	@Override
	public ArrayList<BmField> getListBoxFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getCode()
				));
	}
	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getCode()), 
				new BmSearchField(getBmoUser().getCode()), 
				new BmSearchField(getBmoUser().getBmoArea().getName())));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getCode(), BmOrder.ASC)));
	}
	
	public BmField getCode() {
		return code;
	}

	public void setCode(BmField code) {
		this.code = code;
	}

	public BmField getProjectId() {
		return projectId;
	}

	public void setProjectId(BmField projectId) {
		this.projectId = projectId;
	}

	public BmField getNotes() {
		return notes;
	}

	public void setNotes(BmField notes) {
		this.notes = notes;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setProfileId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmField getUserId() {
		return userId;
	}

	public void setUserId(BmField userId) {
		this.userId = userId;
	}

	public BmoUser getBmoUser() {
		return bmoUser;
	}

	public void setBmoUser(BmoUser bmoUser) {
		this.bmoUser = bmoUser;
	}

	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}

	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}
	
	
}
