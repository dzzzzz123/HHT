<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="ext.chervon.resource.Constants"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ include file="/netmarkets/jsp/util/begin_comp.jspf"%>
<%@ page import="wt.session.SessionHelper"%>
<%@ page import="java.util.ResourceBundle"%>
<%@ page import="java.util.Locale"%>
<%
	String contextPath = request.getContextPath();
%>
<div style="width: 100%" id="loadDiv">
	<label for="id">编号:</label>
	<input type="text" id="id" name="id" >

	<label for="name">名称:</label>
	<input type="text" id="name" name="name" >

	<label for="year">年度:</label>
	<select id="year" name="year">
		<option value="2023">2023</option>
		<option value="2024">2024</option>
		<option value="2025">2025</option>
	</select>

	<label for="version">版本:</label>
	<input type="text" id="version" name="version" >
</div>
<%@ include file="/netmarkets/jsp/util/end_comp.jspf"%>