///**
// * SYMGF
// * Derechos Reservados Mauricio Lopez Barba
// * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
// * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
// * 
// * @author Mauricio Lopez Barba
// * @version 2013-10
// */
//
//package com.flexwm.client.cm;
//
//import java.util.ArrayList;
//
//import com.flexwm.shared.cm.BmoProjectActivities;
//import com.flexwm.shared.cm.BmoProjectActivitiesDep;
//import com.google.gwt.user.client.ui.Panel;
//import com.symgae.client.ui.UiFormDialog;
//import com.symgae.client.ui.UiList;
//import com.symgae.client.ui.UiListBox;
//import com.symgae.client.ui.UiParams;
//import com.symgae.shared.BmException;
//import com.symgae.shared.BmFilter;
//import com.symgae.shared.BmObject;
//
//public class UiProjectActivitiesDep extends UiList {
//	BmoProjectActivitiesDep bmoProjectActivitiesDep;
//	BmoProjectActivities bmoProjectActivities;
//	int proyectId;
//	public UiProjectActivitiesDep(UiParams uiParams, Panel defaultPanel, BmoProjectActivities bmoProjectActivities,int proyectId) {
//		super(uiParams, defaultPanel,new BmoProjectActivitiesDep());
//		bmoProjectActivitiesDep = (BmoProjectActivitiesDep)getBmObject();
//		this.bmoProjectActivities = bmoProjectActivities;
//		this.proyectId = proyectId;
//	}
//	public UiProjectActivitiesDep(UiParams uiParams) {
//		super(uiParams,new BmoProjectActivitiesDep());
//		bmoProjectActivitiesDep = (BmoProjectActivitiesDep)getBmObject();
//	}
//
//	@Override
//	public void create() {
//		UiProjectActivitiesDepForm uiProjectActivitiesDepForm = new UiProjectActivitiesDepForm(getUiParams(), 0);
//		uiProjectActivitiesDepForm.show();
//	}
//	@Override
//	public void open(BmObject bmObject) {
//		UiProjectActivitiesDepForm uiProjectActivitiesDepForm = new UiProjectActivitiesDepForm(getUiParams(), bmObject.getId());
//		uiProjectActivitiesDepForm.show();
//	}
//	public class UiProjectActivitiesDepForm extends UiFormDialog {
//		BmoProjectActivitiesDep bmoProjectActivitiesDep;
//		UiListBox projectActiListBox ;
//
//		public UiProjectActivitiesDepForm(UiParams uiParams, int id) {			
//			super(uiParams, new BmoProjectActivitiesDep(), id);
//			bmoProjectActivitiesDep = (BmoProjectActivitiesDep)getBmObject();
//		}
//		@Override
//		public void populateFields(){
//			bmoProjectActivitiesDep = (BmoProjectActivitiesDep)getBmObject();
//			
//			ArrayList<BmFilter> filters = new ArrayList<BmFilter>();
//			
//			BmFilter bmFilter = new BmFilter();			
//			BmFilter bmFilterNotEquals = new BmFilter();			
//			BmFilter bmFilterMinor = new BmFilter();
//			
//			bmFilterMinor.setValueOperatorFilter(bmoProjectActivities.getKind(),bmoProjectActivities.getNumber(),  
//					BmFilter.MINOR, bmoProjectActivities.getNumber().toInteger());
//			
//			bmFilterNotEquals.setValueOperatorFilter(bmoProjectActivities.getKind(), bmoProjectActivities.getIdField(), 
//					BmFilter.NOTEQUALS, bmoProjectActivities.getId());
//			bmFilter.setValueFilter(bmoProjectActivities.getKind(), 
//					bmoProjectActivities.getStepProjectId(), proyectId); 
//			filters.add(bmFilterMinor);
//			filters.add(bmFilter);
//			filters.add(bmFilterNotEquals);
//		
//			projectActiListBox = new UiListBox(getUiParams(), new BmoProjectActivities(),filters);
//			
//			
//			formFlexTable.addField(1, 0, projectActiListBox,bmoProjectActivitiesDep.getProjectActivitieId());
//		
//		}
//		@Override
//		public BmObject populateBObject() throws BmException {
//			bmoProjectActivitiesDep = new BmoProjectActivitiesDep();
//			bmoProjectActivitiesDep.setId(id);
//			bmoProjectActivitiesDep.getChildProjectActivityId().setValue(bmoProjectActivities.getId());
//			bmoProjectActivitiesDep.getProjectActivitieId().setValue(projectActiListBox.getSelectedId());;
//			return bmoProjectActivitiesDep;
//			
//		}
//		
//		@Override
//		public void close() {
//			list();
//		}
//		@Override
//		public void saveNext() {
//			UiProjectStepDetail uiProjectStepDetail = new UiProjectStepDetail(getUiParams(), proyectId);
//			uiProjectStepDetail.show();
//		}
//		
//	}
//}