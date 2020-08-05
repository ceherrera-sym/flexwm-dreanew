<!--  
/**
 * SYMGF
 * Derechos Reservados Mauricio Lopez Barba
 * Este software es propiedad de Mauricio Lopez Barba, y no puede ser
 * utilizado, distribuido, copiado sin autorizacion expresa por escrito.
 * 
 * @author Mauricio Lopez Barba
 * @version 2013-10
 */ -->
 
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.symgae.server.*" %>
<%@ page import="com.symgae.server.sf.*" %>
<%@ page import="com.symgae.shared.SFParams" %>
<%@ page import="com.symgae.shared.BmFilter" %>
<%@ page import="com.symgae.shared.BmSearchField" %>
<%@ page import="com.symgae.shared.BmOrder" %>
<%@ page import="com.symgae.shared.BmObject" %>
<%@ page import="com.symgae.shared.BmField" %>
<%@ page import="com.symgae.shared.sf.*" %>

<%@ page import="com.symgae.shared.LoginInfo" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.lang.reflect.Constructor" %>

<%@ page import="com.symgae.server.google.*" %>
<%@ page import="com.flexwm.server.*" %>
<%@ page import="java.util.Date" %>
<%@ page import="com.google.api.services.calendar.model.*" %>




<html>
<body>

<%
	String appTitle = "SYMGF";
	String defaultCss = "symgf.css";
	SFParams sFParams = null;
	
	try {		
		sFParams = (SFParams)session.getAttribute(SFParams.SFPARAMSNAME);
		appTitle = sFParams.getMainAppTitle();
		defaultCss = sFParams.getDefaultCss();
		
		String resultString = "";
		SFGCalendar SFGCalendar; 
		Event event;
		String googleId = "";
		String action = "list";
		String summary = "Evento XZC";
		String eventId = "";
		String searchText = "";
		
		if (request.getParameter("action") != null) action = request.getParameter("action");
		
		if (request.getParameter("googleid") != null) googleId = request.getParameter("googleid");

		if (!googleId.equals("")) SFGCalendar = new SFGCalendar(sFParams, googleId);
		else SFGCalendar = new SFGCalendar(sFParams);
			
		if (action.equals("create")) {
			// Crear evento	
			Date startDate = new Date();
			Date endDate = new Date(startDate.getTime() + 3600000);
			if (request.getParameter("summary") != null) summary = request.getParameter("summary");
			//eventId = SFGCalendar.insertEvent(summary, "GDL", startDate, endDate);	
			resultString = "El evento id " + eventId + ", <b>" + summary + "</b> fue creado existosamente.";
		} else if (action.equals("update")) {
			// Actualizar evento
			if (request.getParameter("eventid") != null) eventId = request.getParameter("eventid");
			if (request.getParameter("summary") != null) summary = request.getParameter("summary");
			Date startDate = new Date();
			Date endDate = new Date(startDate.getTime() + 3600000);
			//SFGCalendar.updateEvent(eventId, summary, "GDL", startDate, endDate);
			resultString = "El evento id " + eventId + ", <b>" + summary + "</b> fue actualizado existosamente.";
		} else if (action.equals("get")) {
			if (request.getParameter("eventid") != null) eventId = request.getParameter("eventid");
			//event = SFGCalendar.getEvent(eventId);
			//summary = event.getSummary();
			resultString = "El evento id " + eventId + ", <b>" + summary + "</b> fue recuperado existosamente.";
		} else if (action.equals("delete")) {
			if (request.getParameter("eventid") != null) eventId = request.getParameter("eventid");
			SFGCalendar.deleteEvent(eventId);
			resultString = "El evento id " + eventId + ", fue eliminado existosamente.";
		} else if (action.equals("search")) {
			if (request.getParameter("searchtext") != null) searchText = request.getParameter("searchtext");
			resultString = SFGCalendar.searchEventsToString(searchText);
		} else {
			resultString = SFGCalendar.listEventsToString();
		}
%>
	<h1>Accion: <%= action %></h1>
	
	<%= resultString %>
<%
	} catch (Exception e) {
%>		
		Error de LOGIN: <%= e.toString() %>	
<%		
	}
%>
</body>
</html>