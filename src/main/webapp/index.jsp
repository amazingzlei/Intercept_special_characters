<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<link href="css/bootstrap.css" rel="stylesheet">
<script src="js/jquery-2.0.3.min.js"></script>
</body>
    <div>
        <input type="textarea" name="content" id="content"/>
        <button onclick="doSub()" class="btn btn-primary">提交</button>
        <button onclick="doJsonSub()" class="btn btn-primary">json提交</button>
    </div>


    <section>
        <div style="margin-top: 5px">
            <input type="textarea" name="content" id="searchContent" placeholder="请输入关键字"/>
            <button onclick="search()" class="btn btn-primary">查询</button>
            <br>
            <table class="table table-bordered table-hover table-striped">
                <thead>
                <td>内容</td>
                <td>时间</td>
                </thead>
                <tbody id="tbody">
                    <td>aa</td>
                    <td>aa</td>
                </tbody>
            </table>
        </div>
    </section>

</html>
<script>
    function doSub() {
        var content = $("#content").val().trim();
        if(content.length != 0){
            $.ajax({
                url:"./saveContent",
                data:{content:content},
                success:function (data) {
                    if(data.code === 200){
                        if(data.data == 'OK'){
                            alert("添加成功!");
                            $("#content").val("");
                        }
                    }
                }
            })
        }else{
            alert('请输入内容')
        }
    };

    function doJsonSub() {
        var content = $("#content").val().trim();
        var title = {};
        title.content = content;
        if(content.length != 0){
            $.ajax({
                url:"./saveJsonContent",
                data:JSON.stringify(title),
                contentType:"application/json",
                type:'POST',
                dataType:"json",
                success:function (data) {
                    if(data.code === 200){
                        if(data.data == 'OK'){
                            alert("添加成功!");
                            $("#content").val("");
                        }
                    }
                }
            })
        }else{
            alert('请输入内容')
        }
    };

    function search() {
        $("#tbody").text("");
        var content = $("#searchContent").val().trim();
        if(content.length != 0){
            $.ajax({
                url:"./searchContent",
                data:{content:content},
                dataType:"json",
                success:function (data) {
                    if(data.code === 200){
                        var dataList = data.data;
                        var html = null;
                        for(var i = 0;i<dataList.length;i++){
                            html += "<tr><td>"+dataList[i].CONTENT+"</td>"+
                                "<td>"+dataList[i].CREATETIME+"</td></tr>";
                        }
                        if(html!=null){
                            $("#tbody").html(html);
                        }
                    }else {
                        alert(data.msg)
                    }
                }
            })
        }else{
            alert('请输入内容')
        }
    };


    // 判断是否时延，如果时延则表示为登录，重定向至登录页面
    $.ajaxSetup({
        complete:function (xhr,status) {
            var sessionStatus = xhr.getResponseHeader('sessionStatus');
            if(sessionStatus == 'timeout'){
                var top = getTopWindow();
                var loginAddress = xhr.getResponseHeader('clflag');
                top.location.href = loginAddress;
            }
        }
    });
    function getTopWindow() {
        var p = window;
        while(p != p.parent){
            p = p.parent;
        }
        return p;
    }


</script>
<%--<script>--%>
<%--    setInterval(function () {--%>
<%--        $("body").append("<img src=\"http://seopic.699pic.com/photo/50038/1181.jpg_wh1200.jpg\" width='50px'/>")--%>
<%--    },5000)--%>
<%--</script>--%>
