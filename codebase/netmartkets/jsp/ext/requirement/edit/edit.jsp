<%@page import="com.ptc.netmarkets.util.beans.NmContextBean"%> <%@page
import="com.ptc.netmarkets.util.beans.NmCommandBean"%> <%@page
import="ext.requirement.info.RequiremenrtInfoService"%> <%@page
import="ext.ait.util.PartUtil"%> <%@page language="java" session="true"
pageEncoding="UTF-8"%> <% String path = request.getContextPath(); String
basePath = request.getScheme() + "://" + request.getServerName() + ":" +
request.getServerPort() + path + "/"; NmCommandBean nmCommandBean = new
NmCommandBean(); nmCommandBean.setInBeginJsp (true);
nmCommandBean.setOpenerCompContext (request.getParameter("compContext"));
nmCommandBean.setOpenerElemAddress
(NmCommandBean.convert(request.getParameter("openerElemAddress")));
nmCommandBean.setCompContext
(NmCommandBean.convert(request.getParameter("compContext")));
nmCommandBean.setElemAddress
(NmCommandBean.convert(request.getParameter("elemAddress")));
nmCommandBean.setRequest (request); nmCommandBean.setResponse (response);
nmCommandBean.setOut (out); nmCommandBean.setContextBean (new NmContextBean());
String oid = NmCommandBean.convert(request.getParameter("oid")); oid =
oid.startsWith("VR") ? PartUtil.getORbyVR(oid) : oid; String json =
RequiremenrtInfoService.getRequirementJsonByOid(oid); %>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>编辑需求</title>
    <link
      rel="stylesheet"
      href="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/css/milligram.css"
    />
    <link
      rel="stylesheet"
      href="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/layui-v2.7.6/layui/css/layui.css"
    />
    <style>
      * {
        margin: 0;
        padding: 0;
      }
      .requirement {
        /* width: 1000px; */
        width: 100%;
        /* height: 900px; */
        border: 1px solid gray;
        /* margin-left: 10px; */
      }
      .lefts {
        /* margin-left: 10%; */
        display: flex;
        padding-left: 4%;
      }
      .form-group {
        margin-top: 10px;
        /* float: left; */
        display: flex;
      }
      .linsd {
        margin-left: 4%;
        /* margin-top: 20%;  */
        clear: both;
      }
      .selet {
        /* margin-left:170%; */
        /* padding-right: 60%; */
        float: left;
      }
      .let {
        margin-top: 10px;
        display: flex;
      }
      .Suggestion {
        /* padding: 0;
        margin: 0; */
        /* float:left; */
        width: 95%;
        margin-top: 10px;
      }
      .layui-textarea {
        margin-top: 10px;
      }
      .buttons {
        /* float: right; */
        margin-left: 45%;
        margin-top: 10px;
      }
      label {
        width: 90px;
      }
    </style>
  </head>
  <body>
    <div class="layui-container">
      <div class="layui-row">
        <div class="layui-col-md6 boders">
          <div class="requirement">
            <div class="layui-col-md12 lefts">
              <div>
                <div class="form-group" style="width: 80%">
                  <label for="exampleInputEmail2">名称</label>
                  <input
                    class="form-control3"
                    id="name"
                    placeholder="请输入名称"
                    style="margin-left: 32px"
                    autocomplete="off"
                  />
                </div>
                <div class="form-group">
                  <label for="exampleInputEmail">需求类别</label>
                  <select
                    name=""
                    id="HHT_ReqCategory"
                    style="width: 100px"
                  ></select>
                </div>
                <div class="form-group">
                  <label for="exampleInputEmail2">优先级&emsp;</label>
                  <select
                    name=""
                    id="HHT_Priority"
                    class="form-control4"
                    style="width: 100px"
                  ></select>
                </div>
                <div class="form-group">
                  <label for="exampleInputEmail2">客户角色</label>
                  <select
                    name=""
                    id="HHT_CustomerRole"
                    class="form-control4"
                    style="width: 100px"
                  ></select>
                </div>
              </div>

              <div class="selet">
                <div class="let">
                  <label for="exampleInputEmail2">需求IPD类</label>
                  <select
                    class="form-control input-lg"
                    id="HHT_ipdReq"
                    style="width: 100px"
                  ></select>
                </div>
                <div class="let">
                  <label for="exampleInputEmail2">需求归属&nbsp;&nbsp;</label>
                  <select
                    class="form-control input-lg"
                    id="HHT_ReqBelong"
                    style="width: 100px"
                  ></select>
                </div>
                <div class="let">
                  <label for="exampleInputEmail2"
                    >需求组&emsp;&nbsp;&nbsp;</label
                  >
                  <select
                    class="form-control input-lg"
                    id="HHT_ReqGroup"
                    style="width: 100px"
                  ></select>
                </div>
                <div class="let">
                  <label for="exampleInputEmail2">需求来源&nbsp;&nbsp;</label>
                  <select
                    class="form-control input-lg"
                    id="HHT_ReqSource"
                    style="width: 100px"
                  ></select>
                </div>
              </div>
              <!-- </form> -->
            </div>

            <div class="linsd">
              <div class="form-group">
                <label for="exampleInputEmail4">需求描述</label>
                <div class="mytextareas" style="width: 90%">
                  <form method="post">
                    <textarea id="mytextarea"></textarea>
                  </form>
                </div>
              </div>
              <div class="Suggestion">
                <div class="form-group">
                  <label for="exampleInputEmail4">客户意见</label>
                  <!-- <div class="layui-input-block"> -->
                  <textarea
                    name="desc"
                    placeholder="请输入内容"
                    class="layui-textarea"
                    id="HHT_CustomerComment"
                    style="width: 90%"
                  ></textarea>
                </div>
              </div>
            </div>
            <div class="buttons">
              <div class="layui-input-block rightsd">
                <button
                  class="layui-btn"
                  lay-submit
                  lay-filter="formDemo"
                  id="submit"
                >
                  立即提交
                </button>
                <button
                  type="reset"
                  class="layui-btn layui-btn-primary"
                  id="Cancel"
                >
                  取消
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <script
      type="text/javascript"
      src="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/js/axios.js"
    ></script>
    <script
      type="text/javascript"
      src="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/layui-v2.7.6/layui/layui.js"
    ></script>
    <script
      type="text/javascript"
      src="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/js/template-web.js"
    ></script>
    <script
      type="text/javascript"
      src="<%= basePath %>/netmarkets/jsp/ext/requirement/insert/tinymce/tinymce.js"
    ></script>
    <script type="text/javascript">

          let datas=<%= json %>;
          let elements = {
              name: document.querySelector("#name"),
              description: document.getElementById("mytextarea"),
              hhtcustomerComment: HHT_CustomerComment,
              hhtreqCategory: HHT_ReqCategory,
              hhtpriority: HHT_Priority,
              hhtipdReq: HHT_ipdReq,
              hhtreqBelong: HHT_ReqBelong,
              hhtreqSource: HHT_ReqSource,
              hhtreqGroup: HHT_ReqGroup,
              hhtcustomerRole: HHT_CustomerRole,
          };

          let datasToChange = {
              name:"",
              description:"",
              hhtreqCategory: "",
              hhtpriority: "",
              hhtipdReq: "",
              hhtreqBelong: "",
              hhtreqSource: "",
              hhtreqGroup: "",
              hhtcustomerRole: "",
              hhtcustomerComment:"",
          };

          tinymce.init({
              selector: "#mytextarea,#editor,#tinydemo,#textarea3",
              language: "zh-Hans", // 配置语言包
              promotion: true,
              plugins:
              "lists,advlist,emoticons,table ,fullscreen ,preview,image,template,quickbars,insertdatetime, autolink,link",
              branding: false,
              menubar: false,
              // forced_root_block:'  ',
              toolbar: [
              'undo redo | styleselect | bold italic forecolor backcolor link image alignleft aligncenter alignright "emoticons,numlist bullist table insertdatetime emoticons hr preview removeformat fullscreen',
              ],
              statusbar: false,
          });
          fetch("http://uat.honghe-tech.com/Windchill/netmarkets/jsp/ext/requirement/insert/json/data.json")
          .then((response) => response.json())
          .then((data) => {
              // populateDropdown(data.postsd[2]["HHT_ReqCategorySelt"], HHT_ReqCategory);
          populateDropdown(data.postsd[1]["HHT_PrioritySelet"], HHT_Priority);
          for (const key in data.postsd[2]["HHT_ReqCategorySelt"]) {
          var newoption2 = document.createElement("option");
          if (key !== "R") {
          newoption2.value = key;
          newoption2.innerHTML = data.postsd[2]["HHT_ReqCategorySelt"][key];
          HHT_ReqCategory.appendChild(newoption2); // 将选项添加到下拉列表
          }
        }
              populateDropdown(data.postsd[3]["HHT_CustomerRoleSelt"], HHT_CustomerRole);
              populateDropdown(data.postsd[4]["HHT_ipdReqSelet"], HHT_ipdReq);
              populateDropdown(data.postsd[0]["HHT_ReqBelong"], HHT_ReqBelong);
              populateDropdown(data.postsd[5]["HHT_ReqGroupSelet"], HHT_ReqGroup);
              populateDropdown(data.postsd[6]["HHT_ReqSourceSelet"], HHT_ReqSource);
              HHT_Priority.value=datas["hhtreqCategory"]
              Object.keys(elements).forEach((key) => {
              if (key === "description") {
                  mytextarea.value = datas[key];
              } else {
                  elements[key].value= datas[key];
              }
          });

          });
            //  给更新页面初始化从后端获取的值
          // Object.keys(elements).forEach((key) => {
          //     if (key === "description") {
          //         mytextarea.value = datas[key];
          //     } else {
          //         elements[key].value= datas[key];
          //     }
          // });

      //     HHT_ReqCategory.onchange = () => {
      // // console.log(HHT_ReqCategory.value);
      //    HHT_ReqCategorys = HHT_ReqCategory.value;
      //    console.log(HHT_ReqCategory.value)
      //  }
          // 接受一个数据数组和一个选择元素，并负责将数据数组中的项添加为下拉选项
          function populateDropdown(dataArray, selectElement) {
              selectElement.innerHTML = ""; // Clear existing options
              if (Array.isArray(dataArray)) {
                  dataArray.forEach((item) => {
                      if (item !== "请选择") {
                          var newOption = new Option(item, item);
                          selectElement.appendChild(newOption);
                      }
                  });
              }else {
                  Object.values(dataArray).forEach((value) => {
                      if (value !== "请选择") {
                          var newOption = new Option(value, value);
                          selectElement.appendChild(newOption);
                      }
                  });
              }
          }

          var currentfr = document.getElementById("mytextarea");
          currentfr.style.width = "95%";
          let urls = window.location.href;
          let submit = document.querySelector("#submit");
          let Cancel = document.querySelector("#Cancel");
          let names = document.querySelector("#name");

          let handleResize = () => {
              if (document.documentElement.clientWidth > 700) {
                  document.querySelector(".boders").className = "layui-col-md12 boders";
                  document.querySelector(".selet").style = "margin-left:45%";
                  document.querySelector(".buttons").style = "margin-left:69% ";
              } else if (document.documentElement.clientWidth < 700) {
                  document.querySelector(".boders").className = "layui-col-md6 boders";
                  document.querySelector(".selet").style = 'margin-left:"" ';
                  document.querySelector(".buttons").style = "margin-left:43%";
              }
          };
          handleResize()
          window.addEventListener("resize", handleResize);
              let dataMenth={}
          //接口代理
          submit.onclick = () => {

              // Object.keys(datasToChange).forEach((item) => {
              //     const oldData=datas[item];
              //     const newData=elements[item].value;
              //     if(oldData != newData){
              //         if(item == "description"){
              //             datasToChange[item] = tinymce.activeEditor.getContent();
              //         }else{
              //             datasToChange[item] =newData;
              //         }
              //     }
              // });
              // datasToChange["ID"] = "<%= oid %>";
              Object.keys(datasToChange).forEach((item)=>{
                if(datas[item]!=elements[item].value){
                  dataMenth[item]=elements[item].value
                }else if(datas["description"]!==tinymce.activeEditor.getContent()){
                  dataMenth["description"]=tinymce.activeEditor.getContent()
                }
              })
              dataMenth["ID"] = "<%= oid %>";
              console.log(dataMenth);
              axios({
                  method: "POST",
                  url: "http://uat.honghe-tech.com/Windchill/servlet/Navigation/requirement/edit",
                  data: { dataMenth },
              }).then( function (formation) {
                  if (formation.data.code == 200) {
                      window.close();
                  } else {
                      layer.msg("提交失败!");
                  }
                  }).catch(function(error){
                      layer.msg("请求失败:", error);
              })
          }

          Cancel.onclick = () => {
              tinymce.activeEditor.setContent("");
              HHT_CustomerComment.value = "";
              names.value = "";
              window.close();
          }
    </script>
  </body>
</html>
