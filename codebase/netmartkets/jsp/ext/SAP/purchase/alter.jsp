<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ext.sap.purchase.AlterAttrProcessorContrller"%>
<%@page import="ext.sap.Config"%>

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
    String oid = NmCommandBean.convert(request.getParameter("oid"));
    String[] soid = new String[] {};

    for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
        String key = entry.getKey();
        String[] values = entry.getValue();
        if(key.equals("soid")){
            soid = values;
        }

        // out.println("<p>Key: " + key + "</p>");
        // out.println("<ul>");
        // for (String value : values) {
        //     out.println("<li>" + value + "</li>");
        // }
        // out.println("</ul>");
    }

    ArrayList<WTPart> partList = AlterAttrProcessorContrller.processSoid(soid);
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>需求信息</title>
        <link rel="stylesheet" href="<%= basePath %>/netmarkets/jsp/ext/SAP/DataTables/datatables.css" />
        <style>
        </style>
    </head>
    <body>
    <%
        if (partList.size() > 0) {
    %>
        <table id="priceTable">
            <tr>
                <th>部件编号</th>
                <th>价格单位当前属性</th>
                <th>价格单位新属性</th>
                <th>价格当前属性</th>
                <th>价格新属性</th>
            </tr>
    <%
        for (WTPart part : partList) {
            String number = part.getNumber();
            String price = Config.getHHT_Price(part);
            String priceUnit = Config.getHHT_PriceUnit(part);
    %>
        <tr>
            <td><input name="number" type="text" value="<%= number %>" readonly></td>
            <td><input name="priceUnit" type="text" value="<%= priceUnit %>" readonly></td>
            <td><input type="text" name="newPriceUnit"></td>
            <td><input name="price" type="text" value="<%= price %>" readonly></td>
            <td><input type="text" name="newPrice"></td>
        </tr>
    <%
        }
    %>
        </table>
    <%
        } else {
    %>
        <p>没有符合条件的部件。</p>
    <%
        }
    %>
    <script type="text/javascript">
        PTC.navigation.loadScript('core-ui/2.2/lib/jquery.js');
        PTC.navigation.loadScript('netmarkets/jsp/ext/SAP/DataTables/datatables.js');
    </script>
    <body>
</html>
