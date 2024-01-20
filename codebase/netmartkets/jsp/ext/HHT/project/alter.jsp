<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%>
<%@page import="com.ptc.netmarkets.util.beans.NmCommandBean"%>
<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="wt.part.WTPart"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.ptc.projectmanagement.assignment.ResourceAssignment"%>
<%@page import="ext.HHT.project.TrackHours.TrackHoursService"%>
<%@page import="ext.HHT.project.TrackHours.entity.DoneEffortVO"%>

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

    ResourceAssignment resourceAssignment = TrackHoursService.getResourceAssignment(oids[0]);
    DoneEffortVO doneEffortVO = TrackHoursService.getDoneEffortVO(resourceAssignment);
%>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <title>跟踪工时</title>
        <style>
            #TrackHoursTable tr td{
                font-size: 16px !important;
                display: flex;
                align-items: center;
                white-space: nowrap;
                margin: 12px;
            }
            input{
                font-size: 16px !important;
                width: 60%;
                padding: 8px;
                border: 1px solid #ccc;
                border-radius: 3px;
                box-sizing: border-box;
                position: absolute;
                left: 35%;
            }
            input[readonly]{
                background-color: #f2f2f2;
            }
        </style>
        <script type="text/javascript">
            function DoneEffortOnchange() {
                var previousDoneEffort = parseFloat(document.getElementById("PreviousDoneEffort").value);
                var currentDoneEffort = parseFloat(document.getElementById("CurrentDoneEffort").value);
                if (check(currentDoneEffort)) {
                    alert("请不要输入除了阿拉伯数字和小数点之外的其他字符！");
                    document.getElementById("CurrentDoneEffort").value = 0.0;
                    return;
                }
                var totalDoneEffort = previousDoneEffort + currentDoneEffort;
                document.getElementById("TotalDoneEffort").value = totalDoneEffort;
            }

            function PercentWorkCompleteOnchange(row) {
                var currentPercentWorkComplete = parseFloat(document.getElementById("CurrentPercentWorkComplete").value);
                if (check(currentPercentWorkComplete)) {
                    alert("请不要输入除了阿拉伯数字和小数点之外的其他字符！");
                    document.getElementById("CurrentPercentWorkComplete").value = 0.0;
                    return;
                }
                var totalPercentWorkComplete =  currentPercentWorkComplete;
                if (totalPercentWorkComplete > 100) {
                    alert("完工率不能大于100，请重新输入！");
                    document.getElementById("CurrentPercentWorkComplete").value = 0.0;
                    totalPercentWorkComplete = previousPercentWorkComplete;
                }
                document.getElementById("TotalPercentWorkComplete").value = totalPercentWorkComplete;
            }

            function check(input){
                var regex = /[^0-9.]/
                return regex.test(input);
            }
        </script>
        </head>
    <body>
    <% if ( doneEffortVO != null ) { %>
        <table id="TrackHoursTable">
            <tr>
                <td>活动名称: <input name="PlanActivityName" type="text" value="<%= doneEffortVO.getPlanActivityName() %>" readonly></td>
            </tr>
            <tr>
                <td>活动所有者: <input name="UserName" type="text" value="<%= doneEffortVO.getUserName() %>" readonly></td>
            </tr>
            <tr>
                <td>实际工时: <input id="PreviousDoneEffort" name="PreviousDoneEffort" type="text" value="<%= doneEffortVO.getPreviousDoneEffort() %>" readonly></td>
            </tr>
            <tr>
                <td>当前完工率: 
                    <input id="PreviousPercentWorkComplete" name="PreviousPercentWorkComplete" type="text" value="<%= doneEffortVO.getPreviousPercentWorkComplete() %>" readonly>
                </td>
                <td>%</td>
            </tr>
            <tr>
                <td>本次报工工时: <input id="CurrentDoneEffort" name="CurrentDoneEffort" type="text" value="0.0" onchange="DoneEffortOnchange()"></td>
            </tr>
            <tr>
                <% if(Double.valueOf(doneEffortVO.getPreviousPercentWorkComplete()) == 100d ){ %>
                    <td>本次报工进度: <input id="CurrentPercentWorkComplete" name="CurrentPercentWorkComplete" type="text" value="<%= doneEffortVO.getPreviousPercentWorkComplete() %>" readonly></td>
                <% } else { %>
                    <td>本次报工进度: <input id="CurrentPercentWorkComplete" name="CurrentPercentWorkComplete" type="text" value="<%= doneEffortVO.getPreviousPercentWorkComplete() %>" onchange="PercentWorkCompleteOnchange()"></td>
                <% } %>
            </tr>
            <tr>
                <td>本次报工时间: <input id="CurrentTime" name="CurrentTime" type="text" value="<%= TrackHoursService.parseTimestamp(doneEffortVO.getCurrentTime()) %>" readonly></td>
            </tr>
            <tr>
                <td>本次报工后实际工时: <input id="TotalDoneEffort" name="TotalDoneEffort" type="text" value="<%= doneEffortVO.getPreviousDoneEffort() %>" readonly></td>
            </tr>
            <tr>
                <td>本次报工后完成进度: <input id="TotalPercentWorkComplete" name="TotalPercentWorkComplete" type="text" value="<%= doneEffortVO.getPreviousPercentWorkComplete() %>" readonly></td>
            </tr>
            </table>
    <% } else { %>
        <p>没有获取到正确信息，请联系管理员！</p>
    <% } %>
    <body>
</html>
