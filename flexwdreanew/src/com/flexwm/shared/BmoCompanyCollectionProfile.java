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

public class BmoCompanyCollectionProfile extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private BmoProfile bmoProfile = new BmoProfile();
	private BmField collectProfileId, companyId, flexConfigId, sendEmailAuthorizedMB, remaindRaccountInCustomer, dayBeforeRemindRaccount, dayBeforeRemindRaccountTwo ;
	
	public BmoCompanyCollectionProfile() {
		super("com.flexwm.server.PmCompanyCollectionProfile", "companycollectionprofiles", "companycollectionprofileid", "COCP", "Perfil Cobranza x Emp.");
		companyId = setField("companyid", "", "Empresa", 11, Types.INTEGER, false, BmFieldType.ID, false);
		flexConfigId = setField("flexconfigid", "", "Conf. Flex ID", 11, Types.INTEGER, false, BmFieldType.ID, false);
		
		collectProfileId = setField("collectprofileid", "", "Perfil Cobranza", 8, Types.INTEGER, true, BmFieldType.ID, false);
		sendEmailAuthorizedMB = setField("sendemailauthorizedmb", "", "Email al Autorizar MB CxC", 8, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		// envio de correos en pago de CxC	
		remaindRaccountInCustomer = setField("remaindraccountincustomer", "", " Recordar Pago de CxC?", 8, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		dayBeforeRemindRaccount = setField("daybeforeremindraccount", "", "Dias para recordar CxC ", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		dayBeforeRemindRaccountTwo = setField("daybeforeremindraccounttwo", "", "Dias para recordar CxC 2", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
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
	
	public BmField getCollectProfileId() {
		return collectProfileId;
	}
	
	public void setCollectProfileId(BmField collectProfileId) {
		this.collectProfileId = collectProfileId;
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

	public BmField getSendEmailAuthorizedMB() {
		return sendEmailAuthorizedMB;
	}

	public void setSendEmailAuthorizedMB(BmField sendEmailAuthorizedMB) {
		this.sendEmailAuthorizedMB = sendEmailAuthorizedMB;
	}

	public BmField getRemaindRaccountInCustomer() {
		return remaindRaccountInCustomer;
	}

	public void setRemaindRaccountInCustomer(BmField remaindRaccountInCustomer) {
		this.remaindRaccountInCustomer = remaindRaccountInCustomer;
	}

	public BmField getDayBeforeRemindRaccount() {
		return dayBeforeRemindRaccount;
	}

	public void setDayBeforeRemindRaccount(BmField dayBeforeRemindRaccount) {
		this.dayBeforeRemindRaccount = dayBeforeRemindRaccount;
	}

	public BmField getDayBeforeRemindRaccountTwo() {
		return dayBeforeRemindRaccountTwo;
	}

	public void setDayBeforeRemindRaccountTwo(BmField dayBeforeRemindRaccountTwo) {
		this.dayBeforeRemindRaccountTwo = dayBeforeRemindRaccountTwo;
	}

}
