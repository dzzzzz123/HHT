<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.ptc.projectmanagement.plan.PlanActivity"%>
<%@page import="ext.HHT.project.TrackHoursController"%>
<%@page import="ext.HHT.Config"%>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

    String contextPath=request.getContextPath(); 
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setInBeginJsp        (true);
    nmCommandBean.setOpenerCompContext (request.getParameter("compContext"));
    nmCommandBean.setOpenerElemAddress (NmCommandBean.convert(request.getParameter("openerElemAddress")));
    nmCommandBean.setCompContext       (NmCommandBean.convert(request.getParameter("compContext")));
    nmCommandBean.setElemAddress       (NmCommandBean.convert(request.getParameter("elemAddress")));
    nmCommandBean.setRequest           (request);
    nmCommandBean.setResponse          (response);
    nmCommandBean.setOut               (out);
    nmCommandBean.setContextBean       (new NmContextBean());

    Map<String, String[]> paramMap = request.getParameterMap();
    String[] oids = new String[] {};

    for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
        String key = entry.getKey();
        String[] values = entry.getValue();
        if(key.equals("oid")){
            oids = values;
        }

        // out.println("<p>Key: " + key + "</p>");
        // out.println("<ul>");
        // for (String value : values) {
        //     out.println("<li>" + value + "</li>");
        // }
        // out.println("</ul>");
    }

    PlanActivity planActivity = TrackHoursController.processSoid(oids[0]);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>跟踪工时</title>
    </head>
    <body>
    <% if ( planActivity != null ) { %>
        <table id="TrackHoursTable">
            <tr>
                <th>名称</th>
                <th>实际工时</th>
                <th>完工率</th>
            </tr>
            <tr>
                <td><input name="Name" type="text" value="<%= planActivity.getName() %>" readonly></td>
                <td><input name="DoneEffort" type="text" value="<%= Config.getHHT_DoneEffort(planActivity) %>" ></td>
                <td><input name="PercentWorkComplete" type="text" value="<%= planActivity.getPercentWorkComplete() %>" ></td>
            </tr>
        </table>
    <% } else { %>
        <p>没有获取到正确信息，请联系管理员！</p>
    <% } %>
    <body>
</html>
