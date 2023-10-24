<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="ext.ait.util.PartUtil"%>
<%@page import="ext.requirement.info.RequiremenrtInfoService"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>

<% 
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/"; 
    String contextPath=request.getContextPath();
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setInBeginJsp (true);
    nmCommandBean.setOpenerCompContext (request.getParameter("compContext"));
    nmCommandBean.setOpenerElemAddress (NmCommandBean.convert(request.getParameter("openerElemAddress")));
    nmCommandBean.setCompContext (NmCommandBean.convert(request.getParameter("compContext")));
    nmCommandBean.setElemAddress (NmCommandBean.convert(request.getParameter("elemAddress")));
    nmCommandBean.setRequest (request); nmCommandBean.setResponse (response);
    nmCommandBean.setOut (out); nmCommandBean.setContextBean (new NmContextBean());
    
    String oid =  NmCommandBean.convert(request.getParameter("oid"));
    String json = RequiremenrtInfoService.getRequirementJsonByOid(oid);
%>

<!doctype html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>需求信息</title>
    </head>
    <body>
        <div>需求信息</div>
        <div><%= oid %></div>
        <div><%= json %></div>
    </body>
</html>