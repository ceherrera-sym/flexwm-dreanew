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
import com.flexwm.shared.fi.BmoRaccountLink2;
import com.google.gwt.user.client.ui.Panel;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;


public class UiRaccountLinks2 extends UiList {

	UiListBox typeListBox = new UiListBox(getUiParams());
	BmoRaccountLink2 bmoRaccountLink2;
	BmoRaccount bmoRaccount;
	protected RaccountUpdater raccountUpdater;
	int raccountId;

	public UiRaccountLinks2(UiParams uiParams, Panel defaultPanel, BmoRaccount bmoRaccount, int id, RaccountUpdater raccountUpdater) {
		super(uiParams, defaultPanel, new BmoRaccountLink2());
		raccountId = id;
		bmoRaccountLink2 = new BmoRaccountLink2();
		this.bmoRaccount = bmoRaccount;
		this.raccountUpdater = raccountUpdater;
	}

	@Override
	public void postShow() {
		//Se ocultan estos botones porque no se puede eliminar y agregar CxC relacionadas solo  para CxC tipo Otros
		newImage.setVisible(false);
		deleteImage.setVisible(false);
	}
	@Override
	public void create() {
		UiRaccountLinksForm uiRaccountLinksForm = new UiRaccountLinksForm(getUiParams(), 0);
		uiRaccountLinksForm.show();
	}
	
	

	@Override
	public void open(BmObject bmObject) {
		bmoRaccountLink2 = (BmoRaccountLink2)bmObject;
		//bmoRaccount = (BmoRaccount)bmObject;

		UiRaccount raccountForm = new UiRaccount(getUiParams());

		//UiRaccountLinksForm uiRaccountLinksForm = new UiRaccountLinksForm(getUiParams(), bmoRaccountLink2.getId());
		raccountForm.edit(bmoRaccountLink2.getRaccountId().toInteger());
	}

	@Override
	public void edit(BmObject bmObject) {		
		UiRaccountLinksForm uiRaccountLinksForm = new UiRaccountLinksForm(getUiParams(), 0);
		uiRaccountLinksForm.show();
	}

	private class UiRaccountLinksForm extends UiFormDialog {
		UiSuggestBox foreignSuggestBox = new UiSuggestBox(new BmoRaccount());

		public UiRaccountLinksForm(UiParams uiParams, int id) {
			super(uiParams, new BmoRaccountLink2(), id);
		}

		@Override
		public void populateFields() {
			bmoRaccountLink2 = (BmoRaccountLink2)getBmObject();

			BmFilter justLinked = new BmFilter();
			justLinked.setValueFilter(bmoRaccountLink2.getKind(), bmoRaccountLink2.getRaccountId(), bmoRaccount.getId());
			foreignSuggestBox.addFilter(justLinked);

			formFlexTable.addField(1, 0, foreignSuggestBox, bmoRaccountLink2.getRaccountId());

		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoRaccountLink2 = new BmoRaccountLink2();
			bmoRaccountLink2.setId(id);
			bmoRaccountLink2.getRaccountId().setValue(raccountId);
			bmoRaccountLink2.getForeignId().setValue(foreignSuggestBox.getSelectedId());

			return bmoRaccountLink2;
		}

		@Override
		public void close() {
			list();

			if (raccountUpdater != null)
				raccountUpdater.changeRaccount();
		}		
	}
}
