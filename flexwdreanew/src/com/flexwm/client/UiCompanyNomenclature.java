package com.flexwm.client;

import com.flexwm.shared.BmoCompanyNomenclature;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.symgae.client.ui.UiFormDialog;
import com.symgae.client.ui.UiList;
import com.symgae.client.ui.UiListBox;
import com.symgae.client.ui.UiParams;
import com.symgae.client.ui.UiSuggestBox;
import com.symgae.shared.BmException;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.sf.BmoCompany;
import com.symgae.shared.sf.BmoProgram;


public class UiCompanyNomenclature extends UiList {
	BmoCompanyNomenclature bmoCompanyNomenclature;

	public UiCompanyNomenclature(UiParams uiParams) {
		super(uiParams, new BmoCompanyNomenclature());
		bmoCompanyNomenclature = (BmoCompanyNomenclature)getBmObject();
	}

	@Override
	public void open(BmObject bmObject) {
		UiCompanyNomenclatureForm uiCompanyNomenclatureForm = new UiCompanyNomenclatureForm(getUiParams(), bmObject.getId());
		uiCompanyNomenclatureForm.show();
	}

	@Override
	public void edit(BmObject bmObject) {
		UiCompanyNomenclatureForm uiCompanyNomenclatureForm = new UiCompanyNomenclatureForm(getUiParams(), bmObject.getId());
		uiCompanyNomenclatureForm.show();
	}

	@Override
	public void create() {
		UiCompanyNomenclatureForm uiCompanyNomenclatureForm = new UiCompanyNomenclatureForm(getUiParams(), 0);
		uiCompanyNomenclatureForm.show();
	}

	@Override
	public void postShow() {
		addFilterSuggestBox(new UiSuggestBox(new BmoProgram()), new BmoProgram(), bmoCompanyNomenclature.getProgramId());
	}

	public class UiCompanyNomenclatureForm extends UiFormDialog {
		BmoCompanyNomenclature bmoCompanyNomenclature;

		TextBox acronymTextBox = new TextBox();
		UiListBox companyListBox = new UiListBox(getUiParams(),new BmoCompany());
		UiSuggestBox programSuggestBox;
		TextBox consecutiveTextBox = new TextBox();
		TextBox digitsFormatTextBox = new TextBox();
		Label codeCustom = new Label();

		TextBox codeProgram = new TextBox();
		String generalSection = "Datos Generales";
		int companyId = 0;
		public UiCompanyNomenclatureForm(UiParams uiParams, int id) {
			super(uiParams, new BmoCompanyNomenclature(), id);
			//			bmoCompanyNomenclature = (BmoCompanyNomenclature)getBmObject();
			initialize();
		}

		private void initialize() {

			programSuggestBox = new UiSuggestBox(new BmoProgram());
			//			bmoCompanyNomenclature = (BmoCompanyNomenclature)getBmObject();
			//						
			//			showSystemMessage("ID:"+bmoCompanyNomenclature.getId()
			//			+"|comp:"+companyId
			//			+"|prog:"+bmoCompanyNomenclature.getProgramId());
			//			public void setNotInFilter(String kind, String field, String foreignField, String foreignFilterField, String value){
			//				// Ejemplo SQL: prof_profileid not in (select pgpf_profileid from programprofiles where pgpf_programid = 1);
			//				this.kind = kind; // programprofiles
			//				this.field = field; // prof_profileid
			//				this.foreignField = foreignField; // pgpf_profileid
			//				this.foreignFilterField = foreignFilterField; // pgpf_programid
			//				this.value = value; // 1
			//				this.filterType = NOT_IN;
			//			}

			//			ValueChangeHandler<String> acronymChangeHandler = new ValueChangeHandler<String>() {
			//				@Override
			//				public void onValueChange(ValueChangeEvent<String> event) {
			//					codeCustom();
			//				}
			//			};
			//			acronymTextBox.addValueChangeHandler(acronymChangeHandler);
			//			
			//			ValueChangeHandler<String> digitsChangeHandler = new ValueChangeHandler<String>() {
			//				@Override
			//				public void onValueChange(ValueChangeEvent<String> event) {
			//					codeCustom();
			//				}
			//			};
			//			digitsFormatTextBox.addValueChangeHandler(digitsChangeHandler);


			acronymTextBox.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					codeCustom();
				}
			});

			digitsFormatTextBox.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					codeCustom();
				}
			});
		}

		@Override
		public void populateFields() {
			bmoCompanyNomenclature = (BmoCompanyNomenclature)getBmObject();

			int row = 1;
			formFlexTable.addSectionLabel(row++, 0, generalSection, 2);
			formFlexTable.addField(row++, 0, companyListBox, bmoCompanyNomenclature.getCompanyId());
			setProgramSuggestBox(bmoCompanyNomenclature.getCompanyId().toInteger());
			formFlexTable.addField(row++, 0, programSuggestBox, bmoCompanyNomenclature.getProgramId());
			formFlexTable.addField(row++, 0, acronymTextBox, bmoCompanyNomenclature.getAcronym());
			formFlexTable.addField(row++, 0, digitsFormatTextBox, bmoCompanyNomenclature.getCodeFormatDigits());

			formFlexTable.addLabelCell(row++, 0, codeCustom);
			formFlexTable.addLabelField(row++, 0, "Ej. Clave Personalizada", codeCustom);
			formFlexTable.addField(row++, 0, consecutiveTextBox, bmoCompanyNomenclature.getConsecutive());

			statusEffect();

		}

		private void statusEffect() {

			programSuggestBox.setEnabled(false);
			codeCustom.setTitle("Ejemplos de clave personalizada: OC-5, CUST-003, OPO-0025");
			if (newRecord) codeCustom.setText("OPO-0025");
			else codeCustom();

			consecutiveTextBox.setEnabled(false);

			if (!consecutiveTextBox.getText().equals("")) {
				if (Integer.parseInt(consecutiveTextBox.getText()) > 0) {
					companyListBox.setEnabled(false);
					programSuggestBox.setEnabled(false);
					acronymTextBox.setEnabled(false);
					digitsFormatTextBox.setEnabled(false);
				}
			}

			if (Integer.parseInt(companyListBox.getSelectedId()) > 0 && newRecord)
				programSuggestBox.setEnabled(true);
		}

		private void codeCustom() {
			codeCustom.setText(codeFormatDigits(1, Integer.parseInt(digitsFormatTextBox.getText()), acronymTextBox.getText() + "-"));
		}

		@Override
		public BmObject populateBObject() throws BmException {
			bmoCompanyNomenclature.setId(id);
			bmoCompanyNomenclature.getAcronym().setValue(acronymTextBox.getText());
			bmoCompanyNomenclature.getCompanyId().setValue(companyListBox.getSelectedId());
			bmoCompanyNomenclature.getProgramId().setValue(programSuggestBox.getSelectedId());
			bmoCompanyNomenclature.getCodeFormatDigits().setValue(digitsFormatTextBox.getText());
			return bmoCompanyNomenclature;
		}

		@Override
		public void postShow() {
			if ( bmoCompanyNomenclature.getConsecutive().toInteger() > 0) 
				deleteButton.setVisible(false);
		}

		@Override
		public void formListChange(ChangeEvent event) {
			if (event.getSource() == companyListBox) {
				BmoCompany bmoCompany = new BmoCompany();
				bmoCompany = (BmoCompany)companyListBox.getSelectedBmObject();

				if (bmoCompany != null) {
					populateProgram(bmoCompany.getId());
				} else {
					programSuggestBox.setSelectedId(-1);
					programSuggestBox.setSelectedBmObject(null);
					programSuggestBox.setValue("");
				}
			}
			statusEffect();
		}

		// Cambios en los SuggestBox
		@Override
		public void formSuggestionSelectionChange(UiSuggestBox uiSuggestBox) {
			if (uiSuggestBox == programSuggestBox) {
				BmoProgram bmoProgram = new BmoProgram();
				bmoProgram = (BmoProgram)programSuggestBox.getSelectedBmObject();
				acronymTextBox.setText(bmoProgram.getCode().toString());
				codeCustom();
			}
		}

		// Llena combo actividades del proyectos
		private void populateProgram(int companyId) {
			programSuggestBox.setSelectedId(-1);
			programSuggestBox.setSelectedBmObject(null);
			programSuggestBox.setValue("");
			programSuggestBox.getFilterList().clear();
			setProgramSuggestBox(companyId);
		}

		// Asigna filtros de las actividades
		private void setProgramSuggestBox(int companyId) {
			BmoProgram bmoProgram = new BmoProgram();

			if (companyId > 0) {
//				BmFilter bmFilterByUse = new BmFilter();
//				bmFilterByUse.setNotInFilter(bmoCompanyNomenclature.getKind(), 
//						bmoProgram.getIdFieldName(),
//						bmoCompanyNomenclature.getProgramId().getName(), 
//						"true",
//						"true");
//
//				programSuggestBox.addFilter(bmFilterByUse);

				BmFilter bmFilterByCompany = new BmFilter();
				bmFilterByCompany.setNotInFilter(bmoCompanyNomenclature.getKind(),
						bmoProgram.getIdFieldName(),
						bmoCompanyNomenclature.getProgramId().getName(),
						bmoCompanyNomenclature.getCompanyId().getName(),
						"" + companyId);
				programSuggestBox.addFilter(bmFilterByCompany);
			} else {
				BmFilter bmFilter = new BmFilter();
				bmFilter.setValueFilter(bmoProgram.getKind(), bmoProgram.getIdField(), -1);
				programSuggestBox.addFilter(bmFilter);
			}
		}


		// Asigna ceros a la izquierda
		private String codeFormatDigits(int id, int zeros, String codeFormat) {
			String s = "" + id;
			int nowId = s.length();
			for (int i = nowId; i < zeros; i++) {
				if (s.length() < zeros)
					s = "0" + s;
			}
			return codeFormat + s;
		}

		public void close() {
			list();
		}
	}
}
