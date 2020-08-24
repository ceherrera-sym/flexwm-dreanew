/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */

package com.flexwm.shared.cm;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmField;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;

public class BmoProjectDetail extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField prepDate, deliveryDate, loadStartDate, unloadStartDate, exitDate, returnDate, equipmentLoadDate, testDate ;
	private BmField projectId;

	public BmoProjectDetail() {
		super("com.flexwm.server.cm.PmProjectDetail", "projectdetails", "projectdetailid", "PJDE", "Detalle");

		prepDate = setField("prepdate", "", "Preparación", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		exitDate = setField("exitdate", "", "Salida", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		deliveryDate = setField("deliverydate", "", "Entrega", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		loadStartDate = setField("loadstartdate", "", "Inicio montaje", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		unloadStartDate = setField("unloadstartdate", "", "Inicio desmontaje", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);
		returnDate = setField("returndate", "", "Regreso", 20, Types.TIMESTAMP, true, BmFieldType.DATETIME, false);		
		equipmentLoadDate = setField("equipmentloaddate", "", "Carga de equipo", 20, Types.TIMESTAMP, true,  BmFieldType.DATETIME, false);
		testDate = setField("testdate", "", "Pruebas", 20, Types.TIMESTAMP, true,  BmFieldType.DATETIME, false);

		projectId = setField("projectid", "", "Proyecto", 8, Types.INTEGER, false, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getDeliveryDate()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getDeliveryDate())));
	}
	
	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getDeliveryDate(), BmOrder.ASC)));
	}

	public BmField getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(BmField deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public BmField getLoadStartDate() {
		return loadStartDate;
	}

	public void setLoadStartDate(BmField loadStartDate) {
		this.loadStartDate = loadStartDate;
	}

	public BmField getUnloadStartDate() {
		return unloadStartDate;
	}

	public void setUnloadStartDate(BmField unloadStartDate) {
		this.unloadStartDate = unloadStartDate;
	}

	public BmField getProjectId() {
		return projectId;
	}

	public void setProjectId(BmField projectId) {
		this.projectId = projectId;
	}

	public BmField getPrepDate() {
		return prepDate;
	}

	public void setPrepDate(BmField prepDate) {
		this.prepDate = prepDate;
	}

	public BmField getExitDate() {
		return exitDate;
	}

	public void setExitDate(BmField exitDate) {
		this.exitDate = exitDate;
	}

	public BmField getReturnDate() {
		return returnDate;
	}

	public void setReturnDate(BmField returnDate) {
		this.returnDate = returnDate;
	}

	public BmField getEquipmentLoadDate() {
		return equipmentLoadDate;
	}

	public void setEquipmentLoadDate(BmField equipmentLoadDate) {
		this.equipmentLoadDate = equipmentLoadDate;
	}

	public BmField getTestDate() {
		return testDate;
	}

	public void setTestDate(BmField testDate) {
		this.testDate = testDate;
	}
	
	
}