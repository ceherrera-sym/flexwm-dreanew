package com.flexwm.shared.op;

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

public class BmoExtraOrderProfile extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField orderTypeId,profileId;
	private BmoProfile bmoProfile = new BmoProfile();
	
	public BmoExtraOrderProfile() {
		super("com.flexwm.server.op.PmExtraOrderProfile","extraorderprofiles", "extraorderprofileid", "EOPR", "Perfiles Pedido Extra");
		
		orderTypeId =  setField("ordertypeid", "", "Tipo de Pedido", 20, Types.INTEGER, true, BmFieldType.ID, false);
		profileId =  setField("profileid", "", "Perfil", 20, Types.INTEGER, true, BmFieldType.ID, false);
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
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getBmoProfile().getName(), BmOrder.ASC)));
	}
	
	
	public BmField getOrderTypeId() {
		return orderTypeId;
	}
	public void setOrderTypeId(BmField orderTypeId) {
		this.orderTypeId = orderTypeId;
	}
	public BmField getProfileId() {
		return profileId;
	}
	public void setProfileId(BmField profileId) {
		this.profileId = profileId;
	}
	public BmoProfile getBmoProfile() {
		return bmoProfile;
	}
	public void setBmoProfile(BmoProfile bmoProfile) {
		this.bmoProfile = bmoProfile;
	}
	
	
}
