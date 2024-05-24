<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<html lang="ko" xmlns:th="http://www.thymeleaf.org">--%>
<html>
<head>
    <title>메인페이지</title>
</head>
<body>
<h1>main</h1>
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
<%--<head>
    <meta charset="UTF-8">
    <title th:text="|${pageName}|"></title>
</head>
<body>
<div>
    <h1><a th:href="|/${loginType}|">[[${pageName}]]</a></h1> <hr/>
    <div th:if="${username == null}">
        <h3>로그인 되어있지 않습니다!</h3>
        <button th:onclick="|location.href='@{/{loginType}/join (loginType=${loginType})}'|">회원 가입</button> <br/><br/>
        <button th:onclick="|location.href='@{/{loginType}/login (loginType=${loginType})}'|">로그인</button>
    </div>
    <div th:unless="${username == null}">
        <h3>[[${username}]]님 환영합니다!</h3>
        <button th:onclick="|location.href='@{/{loginType}/info (loginType=${loginType})}'|">유저 정보</button> <br/><br/>
        <button th:onclick="|location.href='@{/{loginType}/admin (loginType=${loginType})}'|">관리자 페이지</button> <br/><br/>
        <button th:onclick="|location.href='@{/{loginType}/logout (loginType=${loginType})}'|">로그아웃</button>
    </div>
</div>
</body>--%>
</html>


