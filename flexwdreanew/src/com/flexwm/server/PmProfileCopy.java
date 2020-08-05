package com.flexwm.server;

import java.util.ArrayList;
import java.util.Iterator;

import com.symgae.server.PmConn;
import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.sf.PmProfile;
import com.symgae.server.sf.PmProfileUser;
import com.symgae.server.sf.PmProgramProfile;
import com.symgae.server.sf.PmProgramProfileSpecial;
import com.symgae.shared.BmField;
import com.symgae.shared.BmFilter;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.symgae.shared.sf.BmoProfile;
import com.symgae.shared.sf.BmoProfileUser;
import com.symgae.shared.sf.BmoProgramProfile;
import com.symgae.shared.sf.BmoProgramProfileSpecial;

public class PmProfileCopy extends PmObject {
	BmoProfile bmoProfile;

	public PmProfileCopy(SFParams sFParams) throws SFPmException {
		super(sFParams);
		bmoProfile = new BmoProfile();
		setBmObject(bmoProfile);
	}
	
	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		return autoPopulate(pmConn, new BmoProfile());
	}
	
	@Override
	public BmUpdateResult actionBatch(ArrayList<BmObject> bmObjectList, BmUpdateResult bmUpdateResult, String action,
			BmField bmField, String value) throws SFException {
		copyProfiles(bmUpdateResult, bmObjectList,value);

		return bmUpdateResult;
	}
	
	@Override
	public BmUpdateResult action(BmObject bmObject, BmUpdateResult bmUpdateResult, String action, String value)
			throws SFException {
		copyProfiles(bmUpdateResult,bmObject,value);
		return bmUpdateResult;
	}
	
	private void copyProfiles(BmUpdateResult bmUpdateResult,BmObject bmObject,String value) throws SFException {
		ArrayList<BmObject> bmObjectList = new ArrayList<BmObject>();
		bmObjectList.add(bmObject);
		copyProfiles(bmUpdateResult, bmObjectList,value);
	}
	
	private void copyProfiles(BmUpdateResult bmUpdateResult,ArrayList<BmObject> bmObjectList,String value) throws SFException {
		PmConn pmConn = new PmConn(getSFParams());
		PmConn pmConn2 = new PmConn(getSFParams());
		PmConn pmConn3 = new PmConn(getSFParams());
		PmProfile pmProfile = new PmProfile(getSFParams());
		PmProfileUser pmProfileUser = new PmProfileUser(getSFParams());
		PmProgramProfile pmProgramProfile = new PmProgramProfile(getSFParams());
		PmProgramProfileSpecial pmProgramProfileSpecial = new PmProgramProfileSpecial(getSFParams());

		int newProfileId = 0;
		pmConn.open();		
		pmConn2.open();
		pmConn3.open();

		Iterator<BmObject> objetctIterator = bmObjectList.iterator();

		while(objetctIterator.hasNext()) {
			BmoProfile nextBmoProfile = (BmoProfile)objetctIterator.next();
			BmoProfile newBmoProfile = new BmoProfile();

			if (value.length() > 0 )
				newBmoProfile.getName().setValue(value);
			else
				newBmoProfile.getName().setValue("[Copia] " +nextBmoProfile.getName().toString());
			newBmoProfile.getDescription().setValue(nextBmoProfile.getDescription().toString());
			newBmoProfile.getPrice().setValue(nextBmoProfile.getPrice().toString());
			newBmoProfile.getCost().setValue(nextBmoProfile.getCost().toString());			
			pmProfile.save(pmConn,newBmoProfile, bmUpdateResult);

			if(!bmUpdateResult.hasErrors()) {
				newProfileId = bmUpdateResult.getId();
				BmoProfileUser bmoProfileUser = new BmoProfileUser();
				BmFilter filterByprofileUser = new BmFilter();
				filterByprofileUser.setValueFilter(bmoProfileUser.getKind(), bmoProfileUser.getProfileId(), nextBmoProfile.getId());
				Iterator<BmObject> profileUserIterator = pmProfileUser.list(filterByprofileUser).iterator();
				// Crear perflies de usuario
				while(profileUserIterator.hasNext()) {					
					BmoProfileUser nextBmoProfileUser = (BmoProfileUser)profileUserIterator.next();
					BmoProfileUser newBmoProfileUser = new BmoProfileUser();

					newBmoProfileUser.getProfileId().setValue(newProfileId);
					newBmoProfileUser.getUserId().setValue(nextBmoProfileUser.getUserId().toInteger());
					newBmoProfileUser.getDefaultUser().setValue(nextBmoProfileUser.getDefaultUser().toBoolean());
					pmProfileUser.save(pmConn, newBmoProfileUser, bmUpdateResult);
				}
				// Crear  accesos --  no usare el save del Pm porque solo inserta maximo 40 registros desconozco el motivo, ya lo intente
				BmFilter programProflieFilter = new BmFilter();
				BmoProgramProfile bmoProgramProfile = new BmoProgramProfile();
				programProflieFilter.setValueFilter(bmoProgramProfile.getKind(), bmoProgramProfile.getProfileId(), nextBmoProfile.getId());
				Iterator<BmObject> programProfileIterator = pmProgramProfile.list(programProflieFilter).iterator();

				
				while(programProfileIterator.hasNext()) {

					BmoProgramProfile nextBmoProgramProfile = (BmoProgramProfile)programProfileIterator.next();
					String sql = "INSERT INTO " + bmoProgramProfile.getKind() + " SET " + 
							bmoProgramProfile.getRead().getName() + " = " + nextBmoProgramProfile.getRead().toInteger() +"," + 
							bmoProgramProfile.getWrite().getName() + " = " + nextBmoProgramProfile.getWrite().toInteger() +"," +
							bmoProgramProfile.getDelete().getName() + " = " + nextBmoProgramProfile.getDelete().toInteger() + "," + 
							bmoProgramProfile.getPrint().getName() + " = " + nextBmoProgramProfile.getPrint().toInteger() + "," +
							bmoProgramProfile.getAllData().getName() + " = " + nextBmoProgramProfile.getAllData().toInteger() + "," +
							bmoProgramProfile.getProfileId().getName() + " = " +newProfileId + "," +
							bmoProgramProfile.getMenu().getName() + " = " + nextBmoProgramProfile.getMenu().toInteger() + "," + 
							bmoProgramProfile.getProgramId().getName() + " = " + nextBmoProgramProfile.getProgramId().toInteger() + "," +
							bmoProgramProfile.getUserCreateId().getName() + " = " + getSFParams().getLoginInfo().getUserId() + "," +
							bmoProgramProfile.getDateCreate().getName() + " = '"+ SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()) +"'";

					pmConn2.prepareStatement(sql);
					pmConn2.doUpdate();
					int newProgramProfileId = pmConn2.getNewId();

					//						//Crear accesos especiales
					BmoProgramProfileSpecial bmoProgramProfileSpecial = new BmoProgramProfileSpecial();
					BmFilter programProfileSpecialFilter = new BmFilter();
					programProfileSpecialFilter.setValueFilter(bmoProgramProfileSpecial.getKind(),
							bmoProgramProfileSpecial.getProgramProfileId(), nextBmoProgramProfile.getId());

					Iterator<BmObject> programProfileSpecialIterator = pmProgramProfileSpecial.list(programProfileSpecialFilter).iterator();

					while(programProfileSpecialIterator.hasNext()) {

						BmoProgramProfileSpecial nextBmoProgramProfileSpecial = (BmoProgramProfileSpecial)programProfileSpecialIterator.next();
						sql = "INSERT INTO " + nextBmoProgramProfileSpecial.getKind() + " SET \n" + 
								nextBmoProgramProfileSpecial.getProgramProfileId().getName() + "  = " + newProgramProfileId + ",\n" + 
								nextBmoProgramProfileSpecial.getProgramSpecialId().getName() + "  = " + nextBmoProgramProfileSpecial.getProgramSpecialId().toInteger() + ",\n" + 
								nextBmoProgramProfileSpecial.getUserCreateId().getName() + "  = " + getSFParams().getLoginInfo().getUserId() + ", \n" + 
								nextBmoProgramProfileSpecial.getDateCreate().getName() + "  =  '" + SFServerUtil.nowToString(getSFParams(), getSFParams().getDateTimeFormat()) + "'";
						System.err.println(">>  " + sql);
						pmConn3.doUpdate(sql);
					}
				}

			}
		}
		pmConn2.close();
		pmConn.close();
		pmConn3.close();
	}
}
