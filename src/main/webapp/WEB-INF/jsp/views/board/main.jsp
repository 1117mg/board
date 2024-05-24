<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<html lang="ko" xmlns:th="http://www.thymeleaf.org">--%>
<html>
<head>
    <title>board main</title>
</head>
<body>
<h1>게시글 목록</h1>
<%
    // 쿠키 배열 가져오기
    Cookie[] cookies = request.getCookies();

    boolean isLoggedIn = false;

    // 쿠키 배열을 순회하며 idx 쿠키의 존재 여부 확인
    if (cookies != null) {
        for(Cookie c : cookies){
            if (c.getName().equals("idx")) {
                // idx 쿠키가 존재하면 로그인 상태로 설정
                isLoggedIn = true;
                break; // 로그인 상태이므로 더 이상 반복할 필요가 없음
            }
        }
    }

    // 로그인 상태에 따라 버튼 표시
    if (isLoggedIn) {
    // 로그아웃 버튼 표시
%>
<input type="button" value="Logout" onclick="location.href='/logout'"><br/><br/>
<%
    } else {
    // 로그인 버튼 표시
%>
<input type="button" value="Login" onclick="location.href='/login'"><br/><br/>
<%
    }
%>

</body>
<script>
    const getCookie = (key) => {

        const cookies = document.cookie;

        const cookieList = cookies.split("; ").map(el => el.split("="));

        const obj = {};

        for(let i = 0 ; i < cookieList.length ; i++) {
            const k = cookieList[i][0];
            const v = cookieList[i][1];
            obj[k] = v;
        }
        return obj[key];
    }

    // 쿠키에 저장된 idx 값 출력
    console.log(getCookie("idx"));

</script>
</html>


