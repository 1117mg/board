<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML>
<head>
    <title>글 상세</title>
</head>
<body>
<h1>글 상세보기</h1>

<table border="1" cellspacing="0" cellpadding="0" width="90%">
    <tr width="90%">
        <td width="10%" align="center">작성자</td>
        <c:if test="${board.writer ne null}">
            <td width="50%">${board.writer}</td>
        </c:if>
        <td width="50%">${user.username}</td>
    </tr>
    <tr>
        <td align="center">제목</td>
        <td><input type="text" name="subject" style="width: 95%;" value="${board.title}"></td>
    </tr>
    <tr>
        <td align="center">내용</td>
        <td><textarea name="content" style="width: 95%;height: 200px;">${board.content}</textarea></td>
    </tr>
    <tr>
        <td colspan="2" align="center">
            <input type="hidden" id="boardBno" value="${board.bno}">

            <button type="submit">등록</button>
            <button type="button" onclick="gotoMain();">목록</button>
            <c:if test="${board.bno ne null}">
                <button type="button" onclick="deleteBoard()">삭제</button>
            </c:if>

        </td>
    </tr>
</table>
</body>
<script>

    // 목록이동
    function gotoMain() {
        location.href = "/main";
    }

    // 글 삭제
    function deleteBoard() {
        const bno = document.getElementById("boardBno").value;
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

