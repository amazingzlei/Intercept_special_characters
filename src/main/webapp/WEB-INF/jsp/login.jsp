<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2019/9/25
  Time: 16:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>登录</title>
    <link href="css/bootstrap.css" rel="stylesheet">
</head>
<body>
    <form action="./userLogin" method="post">
        <input type="text" placeholder="请输入手机号" name="phone" class="form-group"/>
        <input type="password" placeholder="请输入密码" name="password" class="form-group"/>
        <input type="submit" value="登录" class="btn btn-primary"/>
    </form>
</body>
</html>
