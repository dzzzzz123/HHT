<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page import="wt.org.WTUser"%>
<%@page import="java.util.*"%>
<%@page import="ext.HHT.project.workHours.user.Service"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    ArrayList<String> groups = Service.getGroups();
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>工时信息</title>
        <style>
            button{
                margin-left: 25px;
                display: inline-block;
            }
            label{
                font-size: 15px;
                margin-left: 25px;
            }
            select{
                font-size: 15px;
            }
            #tableContainer tbody td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
                font-size: 12px;
                text-align: center;
            }
            #tableContainer thead td {
                padding: 3px;
                background-color: #f2f2f2;
                font-size: 12px;
                text-align: center;
            }
            #tableContainer {
                border-collapse: collapse;
                width: 95%;
                margin-top: 15px;
                margin-left: 20px;
            }
        </style>
    </head>
    <body>
        <label for="groupSelect">选择组：</label>
        <select id="groupSelect">
            <option value="">请选择</option>
            <% for (String group : groups) { %>
            <option value="<%= group %>"><%= group %></option>
            <!-- <option value="HHT_系统管理员"><%= group %></option> -->
            <% } %>
        </select>
        &nbsp;&nbsp;<label for="daysSelect">选择天数访问：</label>
        <select id="daysSelect" name="time">
            <option value="1">当天</option>
            <option value="2">当周</option>
            <option value="3">当月</option>
            <option value="4">当年</option>
        </select>
        <button onclick="genTable()">开始统计</button>

        <table id="tableContainer">
            <thead>
                <td>工号</td>
                <td>用户名称</td>
                <td>工时数</td>
                <td>开始时间</td>
                <td>结束时间</td>
            </thead>
            <tbody></tbody>
        </table>

        <!-- <script type="text/javascript" src="<%= basePath %>/core-ui/2.2/lib/jquery.js"></script> -->
        <script type="text/javascript">
            PTC.navigation.loadScript('core-ui/2.2/lib/jquery.js');

            function genTable() {
                var data={};
                data["name"]= document.getElementById('groupSelect').value;
                data["condition"]= document.getElementById('daysSelect').value;
                console.log(data);

                // 执行 AJAX 请求
                $.ajax({
                    type: "POST",
                    url: "https://hhplm.honghe-tech.com/Windchill/servlet/Navigation/UserWorkHoursServlet",
                    headers: {
                        "Authorization": "Basic " + btoa("wcadmin:HHT@PLM123") // 添加基本认证信息
                    },
                    contentType: "application/json",
                    data: JSON.stringify(data),
                    success: function (response) {
                    console.log(response);
                    // 调用生成表格的函数
                        generateTable(response);
                    },
                    error: function (error) {
                        console.error("Error in AJAX request:", error);
                    }
                });
            }

            function generateTable(data) {
                var tbody = $("#tableContainer tbody");
                tbody.empty();

                $.each(data, function (_, row) {
                    var dataRow = $("<tr>").appendTo(tbody);
                    $("<td>").text(row.userName).appendTo(dataRow);
                    $("<td>").text(row.userFullName).appendTo(dataRow);
                    $("<td>").text(row.doneEffort).appendTo(dataRow);
                    $("<td>").text(row.beforeTime).appendTo(dataRow);
                    $("<td>").text(row.afterTime).appendTo(dataRow);
                });
            }
                
        </script>
    <body>
</html>