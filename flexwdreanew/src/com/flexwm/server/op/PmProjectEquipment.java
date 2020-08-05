/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.server.op;

import com.symgae.server.PmObject;
import com.symgae.server.SFServerUtil;
import com.symgae.server.PmConn;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmUpdateResult;
import com.symgae.shared.SFException;
import com.symgae.shared.SFParams;
import com.symgae.shared.SFPmException;
import com.flexwm.shared.op.BmoProjectEquipment;


public class PmProjectEquipment extends PmObject {
	BmoProjectEquipment bmoProjectEquipment;

	public PmProjectEquipment(SFParams sfParams) throws SFPmException {
		super(sfParams);
		bmoProjectEquipment = new BmoProjectEquipment();
		setBmObject(bmoProjectEquipment);

		// Lista de joins
		//		setJoinList(new ArrayList<PmJoin>(Arrays.asList(
		//				new PmJoin(bmoProjectEquipment.getEquipmentTypeId(), bmoEquipment.getBmoEquipmentType()),
		//				new PmJoin(bmoEquipment.getUserId(), bmoEquipment.getBmoUser()),
		//				new PmJoin(bmoEquipment.getBmoUser().getAreaId(), bmoEquipment.getBmoUser().getBmoArea()),
		//				new PmJoin(bmoEquipment.getBmoUser().getLocationId(), bmoEquipment.getBmoUser().getBmoLocation())
		//				)));
	}

	@Override
	public BmObject populate(PmConn pmConn) throws SFException {
		BmoProjectEquipment bmoProjectEquipment = (BmoProjectEquipment)autoPopulate(pmConn, new BmoProjectEquipment());

		//		// Tipo de Equipo
		//		BmoEquipmentType bmoEquipmentType = new BmoEquipmentType();
		//		int productGroupId = (int)pmConn.getInt(bmoEquipmentType.getIdFieldName());
		//		if (productGroupId > 0) bmoEquipment.setBmoEquipmentType((BmoEquipmentType) new PmEquipmentType(getSFParams()).populate(pmConn));
		//		else bmoEquipment.setBmoEquipmentType(bmoEquipmentType);
		//
		//		// Responsable
		//		BmoUser bmoUser = new BmoUser();
		//		int userId = (int)pmConn.getInt(bmoUser.getIdFieldName());
		//		if (userId > 0) bmoEquipment.setBmoUser((BmoUser) new PmUser(getSFParams()).populate(pmConn));
		//		else bmoEquipment.setBmoUser(bmoUser);

		return bmoProjectEquipment;
	}

	@Override
	public BmUpdateResult save(PmConn pmConn, BmObject bmObject, BmUpdateResult bmUpdateResult) throws SFException {
		BmoProjectEquipment bmoProjectEquipment = (BmoProjectEquipment)bmObject;
		boolean newRecord = false;
		if(!(bmoProjectEquipment.getId()>0)) {
			newRecord= true;
		}

	if(newRecord) {	
			String sql =("SELECT * FROM projectequipment WHERE peqi_equipmentid = " + bmoProjectEquipment.getEquipmentId() + ""
					+ " AND peqi_projectid = " + bmoProjectEquipment.getProjectId());
			pmConn.doFetch(sql);
			if(pmConn.next()) {

				bmUpdateResult.addError(bmoProjectEquipment.getEquipmentId().getName(), "El Recurso se encuentra en uso en este proyecto");

			}else {
			sql =" SELECT count(*) as contador  FROM projectequipment  WHERE peqi_equipmentid = "+bmoProjectEquipment.getEquipmentId()+" AND ((peqi_startdate between '"+ 
					" "+bmoProjectEquipment.getStartDate().toString()+"'  AND '"+bmoProjectEquipment.getEndDate().toString()+"') or( peqi_enddate between '"+bmoProjectEquipment.getStartDate().toString()+"' AND '"+bmoProjectEquipment.getEndDate().toString()+"' )"+
					" or ( '"+bmoProjectEquipment.getStartDate().toString()+"' between peqi_startdate and peqi_enddate ) or ('"+bmoProjectEquipment.getEndDate().toString()+"' between peqi_startdate and peqi_enddate)); ";
			int contequipments = 0;
			pmConn.doFetch(sql);
			if(pmConn.next())
				contequipments=pmConn.getInt("contador");


			if(contequipments>=1) {
				bmUpdateResult.addError(bmoProjectEquipment.getEquipmentId().getName(), "El Recurso se encuentra en uso en otro proyecto ");

			}else {
				if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
						bmoProjectEquipment.getEndDate().toString(), bmoProjectEquipment.getStartDate().toString()))
					bmUpdateResult.addError(bmoProjectEquipment.getEndDate().getName(), 
							"No puede ser Anterior a " + bmoProjectEquipment.getStartDate().getLabel());
				super.save(pmConn, bmoProjectEquipment, bmUpdateResult);
			}
			
		}

		}else{
			String sql =" SELECT count(*) as contador  FROM projectequipment  WHERE peqi_equipmentid = "+bmoProjectEquipment.getEquipmentId()+" AND ((peqi_startdate between '"+ 
					" "+bmoProjectEquipment.getStartDate().toString()+"'  AND '"+bmoProjectEquipment.getEndDate().toString()+"') or( peqi_enddate between '"+bmoProjectEquipment.getStartDate().toString()+"' AND '"+bmoProjectEquipment.getEndDate().toString()+"' )"+
					" or ( '"+bmoProjectEquipment.getStartDate().toString()+"' between peqi_startdate and peqi_enddate ) or ('"+bmoProjectEquipment.getEndDate().toString()+"' between peqi_startdate and peqi_enddate)); ";
			int contequipments = 0;
			pmConn.doFetch(sql);
			if(pmConn.next())
				contequipments=pmConn.getInt("contador");
			

			if(contequipments>1) {
				bmUpdateResult.addError(bmoProjectEquipment.getEquipmentId().getName(), "El Recurso se encuentra en uso en otro proyecto ");

			}else {
				if (SFServerUtil.isBefore(getSFParams().getDateTimeFormat(), getSFParams().getTimeZone(), 
						bmoProjectEquipment.getEndDate().toString(), bmoProjectEquipment.getStartDate().toString()))
					bmUpdateResult.addError(bmoProjectEquipment.getEndDate().getName(), 
							"No puede ser Anterior a " + bmoProjectEquipment.getStartDate().getLabel());
				super.save(pmConn, bmoProjectEquipment, bmUpdateResult);
			}
			
		
			
		}




		return bmUpdateResult;
	}
}
