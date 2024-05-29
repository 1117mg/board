<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<head>
    <title>글 상세</title>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
</head>
<body>
<h1>글 상세보기</h1>

<form id="boardForm" method="post" action="/insertBoard">
    <table width="90%">
        <tr width="90%">
            <td width="10%" align="center">작성자</td>
            <c:if test="${not empty board.writer}">
                <td width="50%">${board.writer}</td>
            </c:if>
            <td width="50%">${user.username}</td>
        </tr>
        <tr>
            <td align="center">제목</td>
            <td><input type="text" name="title" style="width: 95%;" value="${board.title}"></td>
        </tr>
        <tr>
            <td align="center">내용</td>
            <td><textarea name="content" style="width: 95%;height: 200px;">${board.content}</textarea></td>
        </tr>
        <tr>
            <td align="center">첨부파일 등록</td>
            <td>
            <input type="file" name="files" id="files" multiple="multiple" onchange="uploadFile(this)">
            <div id="uploadDiv">
                <c:forEach items="${getFile}" var="file" varStatus="status">
                    <div class="uploadResult">
                        <span>${status.count}. </span><a href="/downloadFile?uuid=${file.uuid}&fileName=${file.filename}" download>${file.filename}</a>
                        <input type="hidden" name="uuids" value="${file.uuid}">
                        <input type="hidden" name="filenames" value="${file.filename}">
                        <input type="hidden" name="contentTypes" value="${file.contentType}">
                        <label class="delBtn">◀ 삭제</label>
                    </div>
                </c:forEach>
            </div>
            </td>
            <br><br><br>

        </tr>
        <tr>
            <td colspan="2" align="center">
                <input type="hidden" name="bno" value="${board.bno}">
                <button type="submit">등록</button>
                <button type="button" onclick="gotoMain();">목록</button>
                <c:if test="${not empty board.bno}">
                    <button type="button" onclick="deleteBoard()">삭제</button>
                </c:if>
            </td>
        </tr>
    </table>
</form>
</body>
<script>

    // delBtn 개별 삭제 버튼
    $(function () {
        delBtnFile();
    });

    function delBtnFile() {
        $(".delBtn").on("click",function () {
            $(this).parent().remove();
            stCnt();
        });
    }

    // status.count ajax사용시 다시 그려주기
    function stCnt() {
        $('.uploadResult').each(function(index, item){
            $(this).children('span').html(index+1+'. ');
        });
    }


    // ajax 첨부파일 업로드
    function uploadFile(obj) {
        let files = obj.files;
        let formData = new FormData();

        for (var i = 0; i < files.length; i++){
            formData.append("files", files[i]);
        }

        $.ajax({
            type: 'post',
            enctype: 'multipart/form-data',
            url: '/ajaxFile',
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                console.log(data);
                let result = "";
                let cnt = $('.uploadResult').length;
                for (var i = 0; i < data.length; i++){
                    result += '<div class="uploadResult">';
                    result += '<span>' +(cnt + i + 1)  +  '. </span><a href="/downloadFile?uuid=' + data[i].uuid + '&filename=' + data[i].filename + '" download>' + data[i].filename + '</a>';
                    result += '<input type="hidden" name="uuids" value="' + data[i].uuid + '">';
                    result += '<input type="hidden" name="filenames" value="' + data[i].filename + '">';
                    result += '<input type="hidden" name="contentTypes" value="' + data[i].contentType + '">';
                    result += '<label type="button" class="delBtn"> ◀ 삭제</label>';
                    result += '</div>';
                }
                $('#uploadDiv').append(result);
                delBtnFile();
            }
        });

    }

    // 목록이동
    function gotoMain() {
        location.href = "/main";
    }

    // 글 삭제
    function deleteBoard() {
        const bno = document.getElementsByName("bno")[0].value;
        if (confirm("정말 삭제하시겠습니까?")) {
            fetch(`/delete/${bno}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        alert("삭제되었습니다.");
                        location.href = "/main";
                    } else {
                        alert("삭제에 실패하였습니다.");
                    }
                })
                .catch(error => {
                    console.error("Error:", error);
                    alert("삭제 중 오류가 발생하였습니다.");
                });
        }
    }

</script>
</html>

