
<%@ page language="java" import="java.util.*,com.locate.websocket.LocateWebSocketServlet" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>



<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'MyJsp.jsp' starting page</title>
    <script type="text/javascript" src="js/json.js"></script> 
    <script type="text/javascript" src="js/ArrayList.js"></script> 
    <script type="text/javascript">
	var ws = null;
	//var list = ArrayList();
	var list= new ArrayList();
	
	function startServer() {//页面加载函数
		page = new Page(5, 'table1', 'group_one');
		var url = "ws://211.144.85.109:8080/LocationWebsocket/chat.ws?username=server";
		if ('WebSocket' in window) {
			ws = new WebSocket(url);
		} else if ('MozWebSocket' in window) {
			ws = new MozWebSocket(url);
		} else {
			alert("你的浏览器过时了！都不支持WebSocket！");
			return;
		}
		ws.onopen = function() {//；连接开启是触发
			document.getElementById("content").innerHTML += 'websocket open! Welcome!<br />';
		};
		ws.onmessage = function(event) {//接收服务器消息时触发
			var arr = new Array();
			arr = event.data.split(",");
			var str="";
			var table="";
			if(arr.length==3){//收到断开连接服务器的消息（包括服务器发送断开参数和客户端主动断开）
				if(arr[2]=="disconn"){
					for(var i=0;i<list.size();i++){
						var strs =list.get(i);
						if(arr[1]==strs[4]){
							list.removeObj(strs); 
						}
					}
					for(var i=0;i<list.size();i++){
						var strs =list.get(i);
						var locatetrans=trans(strs);
						if(arr[1]==strs[4]){
							list.removeObj(strs); 
						}else{//更新页面显示。（不显示连接已经断开的项）
							table+="<tr id="+strs[4]+" style='background-color:#B4D6FC'>"+
							"<td>"+strs[4]+"</td>"+
							"<td>"+strs[2]+"</td>"+
							"<td>"+strs[3]+"</td>"+
							"<td>"+locatetrans+"</td>"+
							"<td>定位时间间隔："+
							  "<select onchange='setTime(this)'>"+
							  "<option  selected = 'selected'>程序默认值</option>"+
							   "<option >1分钟</option>"+
							   "<option >10分钟</option>"+
							   "<option >1小时</option>"+
							   "<option >20秒</option>"+
							   "</select>"+
							  "<input type='button' onclick='forcetrans()' value='强制传输'><input type='button' onclick='forceLocate("+strs[4]+")' value='强制定位'><input type='button' onclick='forceReset("+strs[4]+")' value='重置'><input type='button' onclick='sendMyMessage("+strs[4]+")' value='断开'></td>";
						}
					}
				var online = arr[0]-1;
				str +="<p>当前在线设备："+
				"<input type='text' value=' " +online+"' style='width:20px;'/>  台</p>";
				document.getElementById("sum").innerHTML=str;
				document.getElementById("group_one").innerHTML=table+'<br/>';
				}
			}else if(arr.length==7){
				list.add(arr);
					for(var i=0;i<list.size();i++){
						var strs =list.get(i);
						var locatetrans=trans(strs);
						table+="<tr id="+strs[4]+" style='background-color:#B4D6FC'>"+
						"<td>"+strs[4]+"</td>"+
						"<td>"+strs[2]+"</td>"+
						"<td>"+strs[3]+"</td>"+
						"<td>"+locatetrans+"</td>"+
						"<td>定位时间间隔："+
								"<select onchange='setTime(this,"+strs[4]+")'>"+
							      "<option  selected = 'selected'>程序默认值</option>"+
								  "<option >1分钟</option>"+
								  "<option >10分钟</option>"+
								  "<option >1小时</option>"+
								  "<option >20秒</option>"+
								  "</select>"+
								 "<input type='button' onclick='forcetrans("+strs[4]+")' value='强制传输'><input type='button' onclick='forceLocate("+strs[4]+")' value='强制定位'><input type='button' onclick='forceReset("+strs[4]+")' value='重置'><input type='button' onclick='sendMyMessage("+strs[4]+")' value='断开'></td>";
								//"<td><input type='text' value='' onkeyup='sendMyMessage("+strs[4]+")' /></td>";
				document.getElementById("group_one").innerHTML=table+'<br/>';
				}
					var online = list.size();
					str +="<p>当前在线设备："+
					"<input type='text' value=' " +online+"' style='width:20px;'/>  台</p>";
					document.getElementById("sum").innerHTML=str;
			}
			
			document.getElementById("content").innerHTML+=event.data+'<br/>';
		};
		ws.onclose = function() {
		
			document.getElementById("content").innerHTML += 'websocket closed! Byebye!<br />';
		};
	}
	//判断定位方式
	function trans(strs){
		if(strs[5]=="001"){
			return "GPS定位"
		}if(strs[5]=="002"){
			return "WIFI定位"
		}if(strs[5]=="003"){
			return "A-GPS"
		}if(strs[5]=="004"){
			return "基站"
		}if(strs[5]=="005"){
			return "离线定位"
		}		
		
	}
		
	//设定定位时间间隔
	function setTime(obj,str) {
		
		if(obj.value=="1分钟"){
			var msg=str+",60";
		}else if(obj.value=="10分钟"){
			var msg=str+",600";
		}else if(obj.value=="1小时"){
			var msg=str+",3600";
		}else if(obj.value=="20秒"){
			var msg=str+",20";
		}else if(obj.value=="程序默认值"){
			alert(str+",default");
		}
		
		if (ws != null) {
			ws.send(msg);
		}
	}
    //断开连接
	function sendMyMessage(str) {
		 if(confirm("确定断开此次连接？"))
		 {
			 var msg = str + ",disconn";
			 if (ws != null) {
				  ws.send(msg);
			  }
		  } else {
				
		   }

	}
	//强制传输
	function forcetrans(str) {
		var msg = str + ",forcetrans";
		if (ws != null) {
			ws.send(msg);
		}
	}				
	//强制定位
	function forceLocate(str) {
		var msg = str + ",forceLocate";
		if (ws != null) {
			ws.send(msg);
		}
	}			
	//重置配置
	function forceReset(str) {
		 if(confirm("重置操作会初始化所有定位策略配置？"))
		 {
				var msg = str + ",reset";
				if (ws != null) {
					ws.send(msg);
				}
		  } else {
				
		   }
	}				
				</script>
    
    
    

<script type="text/javascript" language="javascript" src="js/fenye.js">
	
</script> 
</head>

<body onload="startServer()">
 <center>
<div id="sum">
 <p>当前在线设备</p>
</div>
     <div id="table_group">
	 <table id="table1" border="0">
		
		 <thead>
			<tr style="background-color:#CCCCCC;">
				<th style="cursor:pointer;width:143px;">在线设备</th>
				<th style="cursor:pointer;width:143px;">纬度</th>
				<th style="cursor:pointer;width:143px;">经度</th>
				<th style="cursor:pointer;width:143px;">定位方式</th>
				<th style="cursor:pointer;width:443px;">操作</th>
			</tr>
		</thead>
		 <tbody id="group_one" >
			<!-- <tr id="delCell" style="background-color:#f3f3f3">
			
			
			<tr style="background-color:#B4D6FC">
		   
		
				<td>2342123123</td>
				<td>31.45643</td>
				<td>141.231223</td>
				<td><input type="button" onclick="sendMyMessage()" value="断开"></td>
		  </tr> -->
		</tbody> 
	</table>
	
	<span id="s"></span>
	<table>
		<tr>
			<td><a href="#" onclick="page.firstPage();">首页</a></td>
			<td><a href="#" onclick="page.prePage();">上一页</a></td>
			<td>第<span id="pageindex">1</span>页</td>
			<td><a href="#" onclick="page.nextPage();">下一页</a></td>
			<td><a href="#" onclick="page.lastPage();">尾页</a></td>
			<td>第<select id="pageselect" onchange="page.changePage();"></select>页</td>
		</tr>
	</table> 
	</div>
</center>
	<div id="content"></div> 
</body>
</html>