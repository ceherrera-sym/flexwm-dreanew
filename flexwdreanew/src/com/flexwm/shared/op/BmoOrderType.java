/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */
package com.flexwm.shared.op;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;

import com.symgae.shared.BmField;
import com.symgae.shared.BmFieldOption;
import com.symgae.shared.BmFieldType;
import com.symgae.shared.BmObject;
import com.symgae.shared.BmOrder;
import com.symgae.shared.BmSearchField;


public class BmoOrderType extends BmObject implements Serializable {
	private static final long serialVersionUID = 1L;
	private BmField name, description, autoRenew, type, emailRemindersOrderStart, emailRemindersOrderEnd,  
	emailChangeStatusOppo, remindDaysBeforeEndOrder,remindDaysBeforeEndOrderTwo, profileId, dispersionProfileId, enableDeliverySend,
	scheduleStart, scheduleEnd, enableReqiOrderFinish, hasTaxes, defaultBudgetItemId, requiredLoseComments,
	defaultAreaId, enableExtraOrder, wFlowTypeId, filterOnScrum,remindDaysBeforeRentIncrease, remindDaysBeforeRentIncreaseTwo,
	dataFiscal,remindDaysBeforeEndContractDate, remindDaysBeforeEndContractDateTwo,emailReminderContractEnd, atmCCRevision,statusDefaultDetail,
	areaDefaultDetail,remindDaysBeforeEndContractDateThree, requiredPropertyModel, defaultWFlowTypeId,sendExtraMail;

	public static final char TYPE_SALE = 'S';
	public static final char TYPE_RENTAL = 'R';
	public static final char TYPE_PROPERTY = 'P';
	public static final char TYPE_SESSION = 'E';
	public static final char TYPE_SERVICE = 'V';
	public static final char TYPE_CREDIT = 'C';
	public static final char TYPE_LEASE = 'L';
	public static final char TYPE_CONSULTANCY = 'N';
	
	public static char STATUS_INITIAL = 'I';
	public static char STATUS_DOING = 'N';
	public static char STATUS_DONE = 'D';
	public static char STATUS_HOLD = 'H';

	public BmoOrderType() {
		super("com.flexwm.server.op.PmOrderType","ordertypes", "ordertypeid", "ORTP", "Tipos de Pedidos");

		name = setField("name", "", "Nombre T. Ped.", 30, Types.VARCHAR, false, BmFieldType.STRING, true);
		description = setField("description", "", "Descripción", 255, Types.VARCHAR, true, BmFieldType.STRING, false);
		autoRenew = setField("autorenew", "", "Auto Renovable?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		
		type = setField("type", "", "Tipo Pedido", 1, Types.CHAR, false, BmFieldType.OPTIONS, false);
		type.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(TYPE_SALE, "Venta", "./icons/ortp_type_sale.png"),
				new BmFieldOption(TYPE_RENTAL, "Renta", "./icons/ortp_type_rental.png"),
				new BmFieldOption(TYPE_PROPERTY, "Inmueble", "./icons/ortp_type_property.png"),
				new BmFieldOption(TYPE_SESSION, "Sesiones", "./icons/ortp_type_session.png"),
//				new BmFieldOption(TYPE_SERVICE, "Servicio", "./icons/ortp_type_service.png"),
				new BmFieldOption(TYPE_CREDIT, "Creditos", "./icons/ortp_type_credit.png"),
				new BmFieldOption(TYPE_LEASE, "Arrendamiento", "./icons/ortp_type_lease.png"),
				new BmFieldOption(TYPE_CONSULTANCY, "Consultoría", "./icons/ortp_type_consultancy.png")
				)));
		// se usa para notificar el inicio y renovación de un pedido																			
		emailRemindersOrderStart = setField("emailremindersorderstart", "false", "Notificación Inicio Ped", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		emailRemindersOrderEnd = setField("emailremindersorderend", "false", "Notificación Fin Ped", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		remindDaysBeforeEndOrder = setField("reminddaysbeforeendorder", "0", "Recordar Días Antes Fin Ped.", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		emailChangeStatusOppo = setField("emailchangestatusoppo", "false", "Notif.Cambio Est. OP", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		profileId = setField("profileid", "", "Perfil Notf. Fin Pedido", 20, Types.INTEGER, true, BmFieldType.ID, false);
		
		//Dispersión en Creditos
		dispersionProfileId = setField("dispersionprofileid", "", "Dispersión Crédito", 20, Types.INTEGER, true, BmFieldType.ID, false);
				
		//Horarios flotis
		scheduleStart = setField("schedulestart", "", "Hrs.Inicio", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		scheduleEnd = setField("scheduleend", "", "Hrs.Fin", 11, Types.INTEGER, true, BmFieldType.NUMBER, false);
		
		//Activar el envio automático
		enableDeliverySend = setField("enabledeliverysend", "false", "Envio Automatico", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		enableReqiOrderFinish = setField("enablereqiorderfinish", "false", "Permitir OC Ped Fin.", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		hasTaxes = setField("hasTaxes", "0", "Aplicar Iva", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		// Partida presupuestal defauilt
		defaultBudgetItemId = setField("defaultbudgetitemid", "", "Partida Presup.", 20, Types.INTEGER, true, BmFieldType.ID, false);
		defaultAreaId = setField("defaultareaid", "", "Departamento", 20, Types.INTEGER, true, BmFieldType.ID, false);
		
		requiredLoseComments = setField("requiredlosecomments", "false", "Coment Perder OP. Req", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		// Permitir pedido Extra
		enableExtraOrder= setField("enableextraorder", "false", "Permitir pedido Extra", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		wFlowTypeId = setField("wflowtypeid", "", "Tipo Flujo Extra", 20, Types.INTEGER, true, BmFieldType.ID, false);
		sendExtraMail = setField("sendextramail", "", "Notificar Aut. Extra?", 5, Types.INTEGER, false, BmFieldType.BOOLEAN, false);
		
		filterOnScrum = setField("filteronscrum", "false", "Filtrar en SCRUM", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
	
		//Campos inadico recordar termino de contrato
		remindDaysBeforeEndOrderTwo = setField("reminddaysbeforeendordertwo", "0", "Recordar Días Antes Fin Ped 2.", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		remindDaysBeforeRentIncrease = setField("reminddaysbeforerentincrease", "0", "Not. Días Incremento Rent.", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		remindDaysBeforeRentIncreaseTwo = setField("reminddaysbeforerentincreasetwo", "0", "Not. Días Incremento Rent. 2.", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		remindDaysBeforeEndContractDate = setField("reminddaysbeforeenddate", "0", "Recordar Días Fin Contrato", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		remindDaysBeforeEndContractDateTwo = setField("reminddaysbeforeenddatetwo", "0", "Recordar Días Fin Contrato 2", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		remindDaysBeforeEndContractDateThree = setField("reminddaysbeforeenddatethree", "0", "Not. Días Incremento Rent. 3", 8, Types.INTEGER, true, BmFieldType.NUMBER, false);
		dataFiscal = setField("datafiscal", "false", "Ver Datos Fiscales OP.?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		emailReminderContractEnd = setField("emailremindercontractend", "false", "Notificación Fin  Cont.", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		atmCCRevision = setField("atmccrevision", "false", "Crear CC Atm. Revisión?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		statusDefaultDetail = setField("statusdefaultdetail", "", "Estatus Det.", 1, Types.CHAR, true, BmFieldType.OPTIONS, false);
		statusDefaultDetail.setOptionList(new ArrayList<BmFieldOption>(Arrays.asList(
				new BmFieldOption(STATUS_INITIAL, "Por Iniciar", "./icons/status_revision.png"),
				new BmFieldOption(STATUS_DOING, "En Proceso", "./icons/status_authorized.png"),
				new BmFieldOption(STATUS_DONE, "Terminado", "./icons/status_finished.png"),
				new BmFieldOption(STATUS_HOLD, "Retenido", "./icons/status_cancelled.png")
				)));
		areaDefaultDetail = setField("areadefaultdetail", "", "Area Det.", 8, Types.INTEGER, true, BmFieldType.ID, false);
//		createProject = setField("createproject", "false", "Crear Proyecto", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);
		
		requiredPropertyModel = setField("requiredpropertymodel", "", "Modelo Requerido?", 5, Types.INTEGER, true, BmFieldType.BOOLEAN, false);

		defaultWFlowTypeId = setField("defaultwflowtypeid", "", "Efecto Default(Oport.)", 20, Types.INTEGER, true, BmFieldType.ID, false);
	}

	@Override
	public ArrayList<BmField> getDisplayFieldList() {
		return new ArrayList<BmField>(Arrays.asList(
				getName(),
				getDescription(),
				getType()
				));
	}

	@Override
	public ArrayList<BmSearchField> getSearchFields() {
		return new ArrayList<BmSearchField>(Arrays.asList(
				new BmSearchField(getName()),
				new BmSearchField(getDescription())
				));
	}

	@Override
	public ArrayList<BmOrder> getOrderFields() {
		return new ArrayList<BmOrder>(Arrays.asList(new BmOrder(getKind(), getName(), BmOrder.ASC)));
	}

//	public BmField getCreateProject() {
//		return createProject;
//	}
//
//	public void setCreateProject(BmField createProject) {
//		this.createProject = createProject;
//	}
	
	public BmField getName() {
		return name;
	}

	public BmField getSendExtraMail() {
		return sendExtraMail;
	}

	public void setSendExtraMail(BmField sendExtraMail) {
		this.sendExtraMail = sendExtraMail;
	}

	public void setName(BmField name) {
		this.name = name;
	}

	public BmField getDescription() {
		return description;
	}

	public void setDescription(BmField description) {
		this.description = description;
	}

	public BmField getType() {
		return type;
	}

	public void setType(BmField type) {
		this.type = type;
	}

	public BmField getAutoRenew() {
		return autoRenew;
	}

	public void setAutoRenew(BmField autoRenew) {
		this.autoRenew = autoRenew;
	}

	public BmField getRemindDaysBeforeEndOrder() {
		return remindDaysBeforeEndOrder;
	}

	public void setRemindDaysBeforeEndOrder(BmField remindDaysBeforeEndOrder) {
		this.remindDaysBeforeEndOrder = remindDaysBeforeEndOrder;
	}

	public BmField getProfileId() {
		return profileId;
	}

	public void setGroupId(BmField profileId) {
		this.profileId = profileId;
	}

	public BmField getEmailRemindersOrderStart() {
		return emailRemindersOrderStart;
	}

	public void setEmailRemindersOrderStart(BmField emailRemindersOrderStart) {
		this.emailRemindersOrderStart = emailRemindersOrderStart;
	}

	public BmField getEmailRemindersOrderEnd() {
		return emailRemindersOrderEnd;
	}

	public void setEmailRemindersOrderEnd(BmField emailRemindersOrderEnd) {
		this.emailRemindersOrderEnd = emailRemindersOrderEnd;
	}

	public BmField getEmailChangeStatusOppo() {
		return emailChangeStatusOppo;
	}

	public void setEmailChangeStatusOppo(BmField emailChangeStatusOppo) {
		this.emailChangeStatusOppo = emailChangeStatusOppo;
	}

	public BmField getDispersionProfileId() {
		return dispersionProfileId;
	}

	public void setDispersionProfileId(BmField dispersionProfileId) {
		this.dispersionProfileId = dispersionProfileId;
	}

	public BmField getScheduleStart() {
		return scheduleStart;
	}

	public void setScheduleStart(BmField scheduleStart) {
		this.scheduleStart = scheduleStart;
	}

	public BmField getScheduleEnd() {
		return scheduleEnd;
	}

	public void setScheduleEnd(BmField scheduleEnd) {
		this.scheduleEnd = scheduleEnd;
	}

	public BmField getEnableDeliverySend() {
		return enableDeliverySend;
	}

	public void setEnableDeliverySend(BmField enableDeliverySend) {
		this.enableDeliverySend = enableDeliverySend;
	}

	public BmField getEnableReqiOrderFinish() {
		return enableReqiOrderFinish;
	}

	public void setEnableReqiOrderFinish(BmField enableReqiOrderFinish) {
		this.enableReqiOrderFinish = enableReqiOrderFinish;
	}

	public BmField getHasTaxes() {
		return hasTaxes;
	}

	public void setHasTaxes(BmField hasTaxes) {
		this.hasTaxes = hasTaxes;
	}

	public BmField getDefaultBudgetItemId() {
		return defaultBudgetItemId;
	}

	public void setDefaultBudgetItemId(BmField defaultBudgetItemId) {
		this.defaultBudgetItemId = defaultBudgetItemId;
	}

	public BmField getRequiredLoseComments() {
		return requiredLoseComments;
	}

	public void setRequiredLoseComments(BmField requiredLoseComments) {
		this.requiredLoseComments = requiredLoseComments;
	}

	public BmField getDefaultAreaId() {
		return defaultAreaId;
	}

	public void setDefaultAreaId(BmField defaultAreaId) {
		this.defaultAreaId = defaultAreaId;
	}

	public BmField getEnableExtraOrder() {
		return enableExtraOrder;
	}

	public void setEnableExtraOrder(BmField enableExtraOrder) {
		this.enableExtraOrder = enableExtraOrder;
	}

	public BmField getwFlowTypeId() {
		return wFlowTypeId;
	}

	public void setwFlowTypeId(BmField wFlowTypeId) {
		this.wFlowTypeId = wFlowTypeId;
	}

	public BmField getFilterOnScrum() {
		return filterOnScrum;
	}

	public void setFilterOnScrum(BmField filterOnScrum) {
		this.filterOnScrum = filterOnScrum;
	}

	public BmField getRemindDaysBeforeEndOrderTwo() {
		return remindDaysBeforeEndOrderTwo;
	}

	public void setRemindDaysBeforeEndOrderTwo(BmField remindDaysBeforeEndOrderTwo) {
		this.remindDaysBeforeEndOrderTwo = remindDaysBeforeEndOrderTwo;
	}

	public BmField getRemindDaysBeforeRentIncrease() {
		return remindDaysBeforeRentIncrease;
	}

	public void setRemindDaysBeforeRentIncrease(BmField remindDaysBeforeRentIncrease) {
		this.remindDaysBeforeRentIncrease = remindDaysBeforeRentIncrease;
	}

	public BmField getRemindDaysBeforeRentIncreaseTwo() {
		return remindDaysBeforeRentIncreaseTwo;
	}

	public void setRemindDaysBeforeRentIncreaseTwo(BmField remindDaysBeforeRentIncreaseTwo) {
		this.remindDaysBeforeRentIncreaseTwo = remindDaysBeforeRentIncreaseTwo;
	}

	public BmField getDataFiscal() {
		return dataFiscal;
	}

	public void setDataFiscal(BmField dataFiscal) {
		this.dataFiscal = dataFiscal;
	}

	public BmField getRemindDaysBeforeEndContractDate() {
		return remindDaysBeforeEndContractDate;
	}

	public void setRemindDaysBeforeEndContractDate(BmField remindDaysBeforeEndContractDate) {
		this.remindDaysBeforeEndContractDate = remindDaysBeforeEndContractDate;
	}

	public BmField getRemindDaysBeforeEndContractDateTwo() {
		return remindDaysBeforeEndContractDateTwo;
	}

	public void setRemindDaysBeforeEndContractDateTwo(BmField remindDaysBeforeEndContractDateTwo) {
		this.remindDaysBeforeEndContractDateTwo = remindDaysBeforeEndContractDateTwo;
	}

	public BmField getEmailReminderContractEnd() {
		return emailReminderContractEnd;
	}

	public void setEmailReminderContractEnd(BmField emailReminderContractEnd) {
		this.emailReminderContractEnd = emailReminderContractEnd;
	}

	public BmField getAtmCCRevision() {
		return atmCCRevision;
	}

	public void setAtmCCRevision(BmField atmCCRevision) {
		this.atmCCRevision = atmCCRevision;
	}

	public BmField getStatusDefaultDetail() {
		return statusDefaultDetail;
	}

	public void setStatusDefaultDetail(BmField statusDefaultDetail) {
		this.statusDefaultDetail = statusDefaultDetail;
	}

	public BmField getAreaDefaultDetail() {
		return areaDefaultDetail;
	}

	public void setAreaDefaultDetail(BmField areaDefaultDetail) {
		this.areaDefaultDetail = areaDefaultDetail;
	}

	public BmField getRemindDaysBeforeEndContractDateThree() {
		return remindDaysBeforeEndContractDateThree;
	}

	public void setRemindDaysBeforeEndContractDateThree(BmField remindDaysBeforeEndContractDateThree) {
		this.remindDaysBeforeEndContractDateThree = remindDaysBeforeEndContractDateThree;
	}

	public BmField getRequiredPropertyModel() {
		return requiredPropertyModel;
	}

	public void setRequiredPropertyModel(BmField requiredPropertyModel) {
		this.requiredPropertyModel = requiredPropertyModel;
	}

	public BmField getDefaultWFlowTypeId() {
		return defaultWFlowTypeId;
	}

	public void setDefaultWFlowTypeId(BmField defaultWFlowTypeId) {
		this.defaultWFlowTypeId = defaultWFlowTypeId;
	}
}
