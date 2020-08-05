/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.fi;


import com.flexwm.client.fi.UiRaccount.UiRaccountForm.RaccountUpdater;
import com.flexwm.shared.fi.BmoRaccount;
import com.flexwm.shared.fi.BmoRaccountLink;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiRaccountLinks extends UiList {

	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoRaccountLink bmoRaccountLink;
	BmoRaccount bmoRaccount;
	protected RaccountUpdater raccountUpdater;
	int raccountId;
	
	public UiRaccountLinks(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, int id, RaccountUpdater raccountUpdater) {
		super(uiParams, defaultPanel, new BmoRaccountLink());
		raccountId = id;
		bmoRaccountLink = new BmoRaccountLink();
		this.bmoRaccount = bmoRaccount;
		this.raccountUpdater = raccountUpdater;
	}
	

	@Override
	public void create() {
		UiRaccountLinksForm uiRaccountLinksForm = new UiRaccountLinksForm(getUiParams(), 0);
		uiRaccountLinksForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		bmoRaccountLink = (BmoRaccountLink)bmObject;
		UiRaccountLinksForm uiRaccountLinksForm = new UiRaccountLinksForm(getUiParams(), bmoRaccountLink.getId());
		uiRaccountLinksForm.show();
	}
	
	@Override
	public void edit(BmObject bmObject) {		
		UiRaccountLinksForm uiRaccountLinksForm = new UiRaccountLinksForm(getUiParams(), 0);
		uiRaccountLinksForm.show();
	}

	private class UiRaccountLinksForm extends UiFormDialog {
		UiSuggestBox foreignSuggestBox = new UiSuggestBox(new BmoRaccount());

		public UiRaccountLinksForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRaccountLink(), id);
		}

		@Override
		public void populateFields() {
			bmoRaccountLink = (BmoRaccountLink)getBmObject();

			BmFilter justLinked = new BmFilter();
			justLinked.setValueFilter(bmoRaccountLink.getBmoForeign().getKind(), bmoRaccountLink.getBmoForeign().getLinked(), "1");
			foreignSuggestBox.addFilter(justLinked);

			formFlexTable.addField(1, 0, foreignSuggestBox, bmoRaccountLink.getForeignId());

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoRaccountLink = new BmoRaccountLink();
			bmoRaccountLink.setId(id);
			bmoRaccountLink.getRaccountId().setValue(raccountId);
			bmoRaccountLink.getForeignId().setValue(foreignSuggestBox.getSelectedId());

			return bmoRaccountLink;
		}

		@Override
		public void close() {
			list();

			if (raccountUpdater != null)
				raccountUpdater.changeRaccount();
		}		
	}
}
