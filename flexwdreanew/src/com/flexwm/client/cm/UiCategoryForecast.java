/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.client.cm;

import java.util.ArrayList;

import com.flexwm.shared.BmoFlexConfig;
import com.flexwm.shared.cm.BmoCategoryForecast;
import com.flexwm.shared.cm.BmoOpportunity;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmObject;
import com.symgae.shared.GwtUtil;


public class UiCategoryForecast extends UiList {
	BmoCategoryForecast bmoCategoryForecast;

	
	public UiCategoryForecast(UiParams uiParams) {
		super(uiParams, new BmoCategoryForecast());
		bmoCategoryForecast = (BmoCategoryForecast)getBmObject();
	}

	@Override
	public void create() {
		UiCategoryForecastForm UiCategoryForecastForm = new UiCategoryForecastForm(getUiParams(), 0);
		UiCategoryForecastForm.show();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCategoryForecastForm UiCategoryForecastForm = new UiCategoryForecastForm(getUiParams(), bmObject.getId());
		UiCategoryForecastForm.show();
	}

	public class UiCategoryForecastForm extends UiFormDialog {
		TextBox codeTextBox = new TextBox();
		TextBox nameTextBox = new TextBox();
		TextArea descriptionTextArea = new TextArea();
		UiListBox statusOpportunityListBox = new UiListBox(getUiParams());
		BmoCategoryForecast bmoCategoryForecast;

		public UiCategoryForecastForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCategoryForecast(), id);
		}

		@Override
		public void populateFields() {
			populateStatusListBox();
			bmoCategoryForecast = (BmoCategoryForecast)getBmObject();
			formFlexTable.addField(1, 0, nameTextBox, bmoCategoryForecast.getName());
			formFlexTable.addField(2, 0, descriptionTextArea, bmoCategoryForecast.getDescription());
			formFlexTable.addField(3, 0, statusOpportunityListBox, bmoCategoryForecast.getStatusOpportunity());
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCategoryForecast.setId(id);
			bmoCategoryForecast.getName().setValue(nameTextBox.getText());
			bmoCategoryForecast.getDescription().setValue(descriptionTextArea.getText());
			bmoCategoryForecast.getStatusOpportunity().setValue(statusOpportunityListBox.getSelectedCode());
			return bmoCategoryForecast;
		}

		@Override
		public void close() {
			list();
		}
	}
	
	public void populateStatusListBox() {

		ArrayList<BmFieldOption> status = new ArrayList<BmFieldOption>();

		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusRevision().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_REVISION, "En Revisión", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_revision.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusGanada().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_WON, "Ganada", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_authorized.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusPerdida().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_LOST, "Perdida", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_cancelled.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusExpirada().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_EXPIRED, "Expirado", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_expired.png")));
		}
		if ((((BmoFlexConfig)getSFParams().getBmoAppConfig()).getOppoStatusHold().toBoolean())) {
			status.add(new BmFieldOption(BmoOpportunity.STATUS_HOLD, "Detenido", GwtUtil.getProperUrl(getUiParams().getSFParams(), "/icons/status_on_hold.png")));
		}

		bmoCategoryForecast.getStatusOpportunity().setOptionList(status);
	}
}
