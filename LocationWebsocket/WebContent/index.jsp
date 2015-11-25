<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript" src="js/json.js"></script> 
<script type="text/javascript" src="js/ArrayList.js"></script> 
<script type="text/javascript"> 


//新建一个List 
var list=new ArrayList(); 
//增加一个元素 
list.add("0").add("1").add("2").add("3"); 
//增加指定位置 
list.add(2,"22222222222"); 
//删除指定元素 
list.removeObj("3"); 
//删除指定位置元素 
list.removeIndex(0); 

for(var i=0;i<list.size();i++){ 
document.writeln(list.get(i)); 
} 
document.writeln(list.contains("2")) 
document.writeln(list.contains("33")) 
</script> 
</head> 
<body> 
</body>
</head>
<body>
  
 
</html>