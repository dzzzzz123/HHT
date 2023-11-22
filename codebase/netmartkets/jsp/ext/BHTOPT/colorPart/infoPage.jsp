<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ext.BHTOPT2023.colorPart.ColorPartService"%>
<%@page import="ext.BHTOPT2023.colorPart.Entity"%>
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
    List<Entity> entityList = ColorPartService.getColParts(oid);
    %>

<!DOCTYPE html>
<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>颜色部件管理</title>
    <style>
        #colTable th,#colTable td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }

        #colTable th {
            background-color: #f2f2f2;
        }

        #colTable {
            border-collapse: collapse;
            width: 95%;
            margin-bottom: 30px;
        }
        #errorMsg {
            color:red;
        }
    </style>
    </head>
    <body>
        <% if (entityList.size() > 0) { %>
            <table id="colTable">
                <tr>
                    <th>颜色件编号</th>
                    <th>颜色件名称</th>
                    <th>表面处理</th>
                    <th>颜色条目</th>
                </tr>
        <% for (Entity entity : entityList) { %>
            <tr>
                <td name="Number"  value="<%= entity.getNumber() %>"><%= entity.getNumber() %></td>
                <td name="Name"  value="<%= entity.getName() %>"><%= entity.getName() %></td>
                <td name="Surface"  value="<%=  entity.getSurface() %>">"<%=  entity.getSurface() %></td>
                <td name="Color"  value="<%= entity.getColor() %>"><%= entity.getColor() %></td>
            </tr>
        <% } %>
            </table>
        
        <% }  else { %>
            <p id="errorMsg">当前部件没有符合条件的颜色部件</p>
        <% } %>
    </body>
</html>