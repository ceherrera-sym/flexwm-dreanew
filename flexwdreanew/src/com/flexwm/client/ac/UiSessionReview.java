/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.ac;

import java.util.Date;
import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.ac.BmoProgramSession;
import com.flexwm.shared.ac.BmoProgramSessionLevel;
import com.flexwm.shared.ac.BmoProgramSessionSubLevel;
import com.flexwm.shared.ac.BmoSessionReview;
import com.flexwm.shared.ac.BmoSessionSale;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.symgae.client.ui.UiDateBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;
import com.symgae.shared.sf.BmoUser;
import com.symgae.shared.sf.BmoProfileUser;


public class UiSessionReview extends UiList {
	BmoSessionReview  bmoSessionReview;
	BmoSessionSale bmoSessionSale = new BmoSessionSale();

	public UiSessionReview(UiParams uiParams) {
		super(uiParams, new BmoSessionReview());
	}

	public UiSessionReview(UiParams uiParams, BmoSessionSale bmoSessionSale) {
		super(uiParams, new BmoSessionReview());
		this.bmoSessionSale = bmoSessionSale;
		bmoSessionReview = (BmoSessionReview)getBmObject();
	}

	@Override
	public void create() {
		if (bmoSessionSale.getId() > 0) {
			UiSessionReviewForm uiSessionReviewForm = new UiSessionReviewForm(getUiParams(), 0, bmoSessionSale);
			uiSessionReviewForm.show();
		}
		else 
			list();
	}

	@Override
	protected void open(BmObject bmObject) {
		UiSessionReviewForm uiSessionReviewForm;
		if (bmoSessionSale.getId() > 0)
			uiSessionReviewForm = new UiSessionReviewForm(getUiParams(), bmObject.getId(), bmoSessionSale);
		else 
			uiSessionReviewForm = new UiSessionReviewForm(getUiParams(), bmObject.getId());
		uiSessionReviewForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiSessionReviewForm uiSessionReviewForm;
		if (bmoSessionSale.getId() > 0)
			uiSessionReviewForm = new UiSessionReviewForm(getUiParams(), bmObject.getId(), bmoSessionSale);
		else 
			uiSessionReviewForm = new UiSessionReviewForm(getUiParams(), bmObject.getId());
		uiSessionReviewForm.show();
	}

	public class UiSessionReviewForm extends UiFormDialog {
		UiDateBox dateReviewTextBox = new UiDateBox();
		UiSuggestBox userSuggestBox;	
		TextArea commentsTextArea = new TextArea();
		CheckBox integrationCheckBox = new CheckBox();
		CheckBox interactionCheckBox = new CheckBox();
		CheckBox coordinationCheckBox = new CheckBox();
		CheckBox recognitionCheckBox = new CheckBox();
		CheckBox crawlCheckBox = new CheckBox();
		CheckBox backCheckBox = new CheckBox();
		CheckBox chestCheckBox = new CheckBox();
		CheckBox butterflySwimCheckBox = new CheckBox();	
		CheckBox programMiniCheckBox = new CheckBox();	
		CheckBox programJuniorCheckBox = new CheckBox();
		UiListBox programSessionUiListBox = new UiListBox(getUiParams(), new BmoProgramSession());
		UiListBox programSessionLevelUiListBox = new UiListBox(getUiParams(), new BmoProgramSessionLevel());

		BmoSessionReview bmoSessionReview;
		BmoSessionSale bmoSessionSale;
		SessionReviewUpdater sessionReviewUpdater = new SessionReviewUpdater();

		String programsSection = "Programas";

		public UiSessionReviewForm(UiParams uiParams, int id) {
			super(uiParams, new BmoSessionReview(), id);
			initialize();
		}

		public UiSessionReviewForm(UiParams uiParams, int id, BmoSessionSale bmoSessionSale) {
			super(uiParams, new BmoSessionReview(), id);
			this.bmoSessionSale = bmoSessionSale;
			initialize();

		}

		private void initialize() {
			// Filtrar por vendedores
			userSuggestBox = new UiSuggestBox(new BmoUser());
			BmoUser bmoUser = new BmoUser();
			BmoProfileUser bmoProfileUser = new BmoProfileUser();
			BmFilter filterSalesmen = new BmFilter();
			int salesGroupId = ((BmoFlexConfig)getUiParams().getSFParams().getBmoAppConfig()).getSalesProfileId().toInteger();
			filterSalesmen.setInFilter(bmoProfileUser.getKind(), 
					bmoUser.getIdFieldName(),
					bmoProfileUser.getUserId().getName(),
					bmoProfileUser.getProfileId().getName(),
					"" + salesGroupId);	
			userSuggestBox.addFilter(filterSalesmen);

			// Filtrar por vendedores activos
			BmFilter filterSalesmenActive = new BmFilter();
			filterSalesmenActive.setValueFilter(bmoUser.getKind(), bmoUser.getStatus(), "" + BmoUser.STATUS_ACTIVE);
			userSuggestBox.addFilter(filterSalesmenActive);
		}

		@Override
		public void populateFields() {
			bmoSessionReview = (BmoSessionReview)getBmObject();

			if (newRecord) {
				try {
					//Session de Venta
					bmoSessionReview.getSessionSaleId().setValue(bmoSessionSale.getId());

					//Fecha 
					bmoSessionReview.getDateReview().setValue(GwtUtil.dateToString(new Date(), getSFParams().getDateFormat()));
					//Ventas
					bmoSessionReview.getUserId().setValue(getSFParams().getLoginInfo().getUserId());
				} catch (BmException e) {
					showErrorMessage(this.getClass().getName() + "-populateFields(): ERROR " + e.toString());
				}
			}

			formFlexTable.addField(1, 0, userSuggestBox, bmoSessionReview.getUserId());		
			formFlexTable.addField(2, 0, dateReviewTextBox, bmoSessionReview.getDateReview());
			formFlexTable.addField(3, 0, commentsTextArea, bmoSessionReview.getComments());

			formFlexTable.addField(4, 0, programSessionUiListBox, bmoSessionReview.getProgramSessionId());
			setProgramSessionLevel(bmoSessionReview.getProgramSessionId().toInteger());
			formFlexTable.addField(5, 0, programSessionLevelUiListBox, bmoSessionReview.getProgramSessionLevelId());

			if (!newRecord) {
				// Items
				BmoProgramSessionSubLevel bmoProgramSessionSubLevel = new BmoProgramSessionSubLevel();
				FlowPanel programSessionSubLevelFP = new FlowPanel();
				BmFilter filterBudgetItems = new BmFilter();
				filterBudgetItems.setValueFilter(bmoProgramSessionSubLevel.getKind(), bmoProgramSessionSubLevel.getSessionReviewId(), bmoSessionReview.getId());
				getUiParams().setForceFilter(bmoProgramSessionSubLevel.getProgramCode(), filterBudgetItems);
				UiProgramSessionSubLevel uiProgramSessionSubLevel = new UiProgramSessionSubLevel(getUiParams(), programSessionSubLevelFP, bmoSessionReview, bmoSessionReview.getId(), sessionReviewUpdater);
				setUiType(bmoProgramSessionSubLevel.getProgramCode(), UiParams.MINIMALIST);
				uiProgramSessionSubLevel.show();
				formFlexTable.addPanel(12, 0, programSessionSubLevelFP, 2);
			}
			
			statusEffect();
		}
		
		public void statusEffect() {
			if (!newRecord) {
				programSessionUiListBox.setEnabled(false);
				programSessionLevelUiListBox.setEnabled(false);
			}
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == programSessionUiListBox) {
				populateProgramSessionLevel(Integer.parseInt(programSessionUiListBox.getSelectedId()));
			}
		}

		// Llena combo creditos
		private void populateProgramSessionLevel(int programSessionId) {
			programSessionLevelUiListBox.clear();
			programSessionLevelUiListBox.clearFilters();
			setProgramSessionLevel(programSessionId);
			programSessionLevelUiListBox.populate(bmoSessionReview.getProgramSessionLevelId());
		}

		// Asigna filtros de los creditos
		private void setProgramSessionLevel(int programSessionId) {
			BmoProgramSessionLevel bmoProgramSessionLevel = new BmoProgramSessionLevel();
			if (programSessionId > 0) {
				BmFilter bmFilterCompany = new BmFilter();
				bmFilterCompany.setValueFilter(bmoProgramSessionLevel.getKind(), bmoProgramSessionLevel.getProgramSessionId(), programSessionId);
				programSessionLevelUiListBox.addBmFilter(bmFilterCompany);
			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoProgramSessionLevel.getKind(), bmoProgramSessionLevel.getIdField(), bmoSessionReview.getProgramSessionLevelId().toInteger());
				programSessionLevelUiListBox.addBmFilter(bmFilter);
			}
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoSessionReview.setId(id);

			bmoSessionReview.getUserId().setValue(userSuggestBox.getSelectedId());
			bmoSessionReview.getDateReview().setValue(dateReviewTextBox.getTextBox().getText());
			bmoSessionReview.getComments().setValue(commentsTextArea.getText());
			bmoSessionReview.getProgramSessionId().setValue(programSessionUiListBox.getSelectedId());
			bmoSessionReview.getProgramSessionLevelId().setValue(programSessionLevelUiListBox.getSelectedId());

			return bmoSessionReview;
		}
		
		@Override
		public void saveNext() {
			if (newRecord && bmoSessionSale.getId() > 0) {
				UiSessionReviewForm uiSessionReviewForm = new UiSessionReviewForm(getUiParams(), getBmObject().getId(), bmoSessionSale);
				uiSessionReviewForm.show();
			} else {
				list();
			}
		}

		@Override
		public void close() {
			if (bmoSessionSale.getId() > 0) {
				UiSessionReview uiSessionReviewList = new UiSessionReview(getUiParams(), bmoSessionSale);
				uiSessionReviewList.show();

			} else 
				list();

		}

		public class SessionReviewUpdater {
			public void update() {
				stopLoading();
			}		
		}
	}
}