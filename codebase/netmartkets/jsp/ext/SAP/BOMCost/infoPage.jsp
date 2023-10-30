<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="ext.ait.util.PartUtil"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    NmCommandBean nmCommandBean = new NmCommandBean();
    nmCommandBean.setInBeginJsp (true);
    nmCommandBean.setOpenerCompContext (request.getParameter("compContext"));
    nmCommandBean.setOpenerElemAddress(NmCommandBean.convert(request.getParameter("openerElemAddress")));
    nmCommandBean.setCompContext(NmCommandBean.convert(request.getParameter("compContext")));
    nmCommandBean.setElemAddress(NmCommandBean.convert(request.getParameter("elemAddress")));
    nmCommandBean.setRequest (request);
    nmCommandBean.setResponse (response);
    nmCommandBean.setOut (out);
    nmCommandBean.setContextBean (new NmContextBean());

    String oid = NmCommandBean.convert(request.getParameter("oid"));
    oid = oid.startsWith("VR") ? PartUtil.getORbyVR(oid) : oid;
%>

<!DOCTYPE html>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>需求信息</title>
    <style>
    </style>
    </head>
    <body>
    <div><%= oid %></div>
    <script type="text/javascript">
    
    </script>
    </body>
</html>
