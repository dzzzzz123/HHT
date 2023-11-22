<%@page import="com.ptc.netmarkets.util.beans.NmContextBean" %>
  <%@page import="com.ptc.netmarkets.util.beans.NmCommandBean" %>
    <%@page import="ext.requirement.info.RequiremenrtInfoService" %>
      <%@page import="ext.ait.util.PartUtil" %>
        <%@page language="java" session="true" pageEncoding="UTF-8" %>
          <% String path=request.getContextPath(); String basePath=request.getScheme() + "://" + request.getServerName()
            + ":" + request.getServerPort() + path + "/" ; NmCommandBean nmCommandBean=new NmCommandBean();
            nmCommandBean.setInBeginJsp (true); nmCommandBean.setOpenerCompContext
            (request.getParameter("compContext")); nmCommandBean.setOpenerElemAddress
            (NmCommandBean.convert(request.getParameter("openerElemAddress"))); nmCommandBean.setCompContext
            (NmCommandBean.convert(request.getParameter("compContext"))); nmCommandBean.setElemAddress
            (NmCommandBean.convert(request.getParameter("elemAddress"))); nmCommandBean.setRequest (request);
            nmCommandBean.setResponse (response); nmCommandBean.setOut (out); nmCommandBean.setContextBean (new
            NmContextBean()); String oid=NmCommandBean.convert(request.getParameter("oid")); oid=oid.startsWith("VR") ?
            PartUtil.getORbyVR(oid) : oid; String json=RequiremenrtInfoService.getRequirementJsonByOid(oid); %>

            <!DOCTYPE html>
            <html>

            <head>
              <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
              <title>需求信息</title>
              <link rel="stylesheet"
                href="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/layui-v2.7.6/layui/css/layui.css" />
              <style>
                * {
                  margin: 0;
                  padding: 0;
                }

                .showTop {
                  margin-top: 40px;
                  font-size: 16px;
                }

                .lefts {
                  height: 460px;
                  /* text-align: center; */
                }

                .lineTuo {
                  font-size: 16px;
                  line-height: 50px;
                }

                .content {
                  height: 400px;
                }
              </style>
            </head>

            <body>
              <div class="showTop">
                <div class="layui-col-md9">
                  <div class="layui-row grid-demo">
                    <div class="tops">
                      <div class="layui-col-md3">
                        <div class="layui-panel lefts" style="margin-left: 30px">
                          <div style="padding: 30px">
                            <!-- <div class="lines"> -->
                            <ul class="lineTuo">
                              <li class="name">名称:</li>
                              <li class="number">编号:</li>
                              <li class="hhtreqCategory">需求类别</li>
                              <li class="hhtpriority">优先级</li>
                              <li class="hhtipdReq">需求IPD类</li>
                              <li class="hhtreqBelong">需求归属</li>
                              <li class="hhtreqgroup">需求组</li>
                              <li class="hhtreqSource">需求来源</li>
                            </ul>
                          </div>
                        </div>
                      </div>
                      <div class="layui-col-md8">
                        <div class="layui-panel content" style="margin-left: 30px;padding: 30px;">
                          <label class="exampleInputEmail4" style="font-weight: 900;">需求描述：</label>
                          <div style="padding: 40px">
                            <div class="mytextareas" style="width: 90%">
                              <div id="description"></div>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                    <div class="layui-col-md11">
                      <div class="layui-panel" style="margin-top: 50px; margin-left: 30px">
                        <div style="padding: 30px">
                          <p class="hhtcustomerRole">客户角色</p>
                          <label class="hhtcustomerComment" style="font-weight: 900">客户意见</label>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <script type="text/javascript"
                src="<%=basePath %>/netmarkets/jsp/ext/requirement/layui-v2.7.6/layui/layui.js"></script>
              <script type="text/javascript">
                let showData =<%= json %>;
                console.log(showData);
                document.querySelector("#description").innerHTML = showData.description;
                document.querySelector(".name").innerHTML = "名称 ：" + "" + showData["name"];
                document.querySelector(".number").innerHTML = "编号 ：" + "" + showData["number"];
                if (showData["hhtreqCategory"] == "R0") {
                  document.querySelector(".hhtreqCategory").innerHTML = "需求类别 ：" + "根";
                } else if (showData["hhtreqCategory"] == "R1") {
                  document.querySelector(".hhtreqCategory").innerHTML = "需求类别 ：" + "组";
                } else if (showData["hhtreqCategory"] == "R2") {
                  document.querySelector(".hhtreqCategory").innerHTML = "需求类别 ：" + "条目";
                } else if (showData["hhtreqCategory"] == "R3") {
                  document.querySelector(".hhtreqCategory").innerHTML = "需求类别 ：" + "客户";
                }
                document.querySelector(".hhtpriority").innerHTML = "优先级 ：" + "" + showData["hhtpriority"];
                document.querySelector(".hhtipdReq").innerHTML = "需求IPD类 ：" + "" + showData["hhtipdReq"];
                document.querySelector(".hhtreqBelong").innerHTML = "需求归属 ：" + "" + showData["hhtreqBelong"];
                document.querySelector(".hhtreqgroup").innerHTML = "需求组 ：" + "" + showData["hhtreqGroup"];
                document.querySelector(".hhtreqSource").innerHTML = "需求来源 ：" + "" + showData["hhtreqSource"];
                document.querySelector(".hhtcustomerRole").innerHTML = "客户角色 ：" + "" + showData["hhtcustomerRole"];
                document.querySelector(".hhtcustomerComment").innerHTML = "客户意见 ：" + "" + showData["hhtcustomerComment"];

                let getAllDescendants = (childElements) => {
                  var descendants = [];
                  for (var i = 0; i < childElements.length; i++) {
                    var childElement = childElements[i];
                    // 将直接子元素添加到结果数组
                    descendants.push(childElement);
                    // 递归获取子元素的子元素
                    var childDescendants = getAllDescendants(childElement);
                    // 将子元素的子元素合并到结果数组
                    descendants = descendants.concat(childDescendants);
                  }
                  return descendants;
                };
                let add = getAllDescendants(description);

                for (let i = 0; i < add.length; i++) {
                  add[i].style.width = "500px";
                }
              </script>
            </body>

            </html>