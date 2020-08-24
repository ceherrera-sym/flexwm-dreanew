package com.flexwm.server.cm;

import java.util.Iterator;

import com.flexwm.shared.cm.BmoQuote;
import com.flexwm.shared.cm.BmoQuoteGroup;
import com.flexwm.shared.cm.BmoQuoteItem;
import com.flexwm.shared.cm.BmoQuoteMainGroup;
import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;

public class PmQuoteMainGroup extends PmObject {
	BmoQuoteMainGroup bmoQuoteMainGroup;
	
	public PmQuoteMainGroup(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoQuoteMainGroup = new BmoQuoteMainGroup();
		setBmObject(bmoQuoteMainGroup);
	}
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {

		return autoPopulate(pmConn, new BmoQuoteMainGroup());

	}
	@Override
	public BmUpdateResult delete(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		bmoQuoteMainGroup = (BmoQuoteMainGroup)bmObject;
		PmQuote pmQuote = new PmQuote(getSFParams());
		BmoQuote bmoQuote = (BmoQuote)pmQuote.get(pmConn, bmoQuoteMainGroup.getQuoteId().toInteger());
		//eliminar SubGrupos eitems
		deleteSubGroups(pmConn,bmoQuoteMainGroup,bmUpdateResult);
		
		if (!bmUpdateResult.hasErrors()) {
			super.delete(pmConn, bmoQuoteMainGroup, bmUpdateResult);
			pmQuote.updateBalance(pmConn, bmoQuote, bmUpdateResult);
		}
		
		return bmUpdateResult;
	}
	
	public void deleteSubGroups(PmConn pmConn,BmoQuoteMainGroup bmoQuoteMainGroup,BmUpdateResult bmUpdateResult) throws SFException {
		PmQuoteGroup pmQuoteGroup = new PmQuoteGroup(getSFParams());
		BmoQuoteGroup bmoQuoteGroup = new BmoQuoteGroup();
		BmFilter bmFilterGroup = new BmFilter();
		bmFilterGroup.setValueFilter(bmoQuoteGroup.getKind(), bmoQuoteGroup.getMainGroupId(), bmoQuoteMainGroup.getId());
		Iterator<BmObject> mainGroupsIteretor = pmQuoteGroup.list(pmConn, bmFilterGroup).iterator();
		
		while (mainGroupsIteretor.hasNext()) {
			BmoQuoteGroup nextBmoQuoteGroup = (BmoQuoteGroup)mainGroupsIteretor.next();
			BmoQuoteItem bmoQuoteItem = new BmoQuoteItem();
			PmQuoteItem pmQuoteItem = new PmQuoteItem(getSFParams());
			BmFilter bmFilterItem = new BmFilter();
			bmFilterItem.setValueFilter(bmoQuoteItem.getKind(), bmoQuoteItem.getQuoteGroupId(), nextBmoQuoteGroup.getId());
			
			Iterator<BmObject> itemIterator = pmQuoteItem.list(pmConn, bmFilterItem).iterator();
			
			while (itemIterator.hasNext()) {
				BmoQuoteItem nextBmoQuoteItem = (BmoQuoteItem)itemIterator.next();
				
				pmQuoteItem.delete(pmConn, nextBmoQuoteItem, bmUpdateResult);
				//Eliminar item
			}
			
			if (!bmUpdateResult.hasErrors())
				pmQuoteGroup.delete(pmConn, nextBmoQuoteGroup, bmUpdateResult);
		}
		
	}
	
}
