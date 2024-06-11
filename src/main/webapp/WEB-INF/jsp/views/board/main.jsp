<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE HTML>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <title>게시판 메인</title>
    <style>
        .button-container {
            margin-top: 20px;
        }
        .button-container input {
            margin-right: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
        }
        th {
            background-color: #f2f2f2;
        }
    </style>
</head>
<body>
<h1>게시글 목록</h1>
게시판 총 ${cntBoard} 개
<table>
    <thead>
    <tr>
        <th class="one wide">번호</th>
        <th class="ten wide">글제목</th>
        <th class="two wide">작성자</th>
        <th class="three wide">작성일</th>
        <th class="four wide">조회수</th>
    </tr>
    </thead>

    <tbody>
    <c:forEach var="board" items="${board}">
        <tr>
            <td><span>${board.bno}</span></td>
            <td><a href="/board/${board.bno}"><span>${board.title}</span></a></td>
            <td><span>${board.writer}</span></td>
            <td><span>${board.regdate}</span></td>
            <td><span>${board.hit}</span></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<!-- 로그인 여부에 따라 버튼 표시 -->
<div class="button-container">
    <c:choose>
        <c:when test="${pageContext.request.userPrincipal != null}">
            <!-- 사용자가 로그인한 경우 -->
            <input type="button" value="user 목록" onclick="location.href='/user/main'"><br/><br/>
            <input type="button" value="글 작성" onclick="location.href='/write'"><br/><br/>
            <input type="button" value="로그아웃" onclick="location.href='/logout'"><br/><br/>
        </c:when>
        <c:otherwise>
            <!-- 사용자가 로그인하지 않은 경우 -->
            <input type="button" value="로그인" onclick="location.href='/login'"><br/><br/>
            <input type="button" value="회원가입" onclick="location.href='/join'"><br/><br/>
        </c:otherwise>
    </c:choose>
</div>

<%--페이징 처리 시작--%>
<div class="col-sm-12 col-md-7" style="margin: auto">
    <div class="dataTables_paginate paging_simple_numbers" id="dataTable_paginate">
        <div class="paging">
            <c:set var="_paginate" value="${paginate}" />
            <c:if test="${not empty _paginate}">
                <c:set var="_page_no" value="${_paginate.pageNo}" />
                <c:set var="_page_name" value="${_paginate.pageName}" />
                <c:set var="_page_total" value="${_paginate.totalPage}" />
                <c:set var="_page_params" value="${fn:escapeXml(_paginate.params)}${empty _paginate.params ? '' : '&'}" />
                <c:set var="_nation_size" value="${_paginate.nationSize}" />
                <c:set var="_nation_begin" value="${_paginate.nationBegin}" />
                <c:set var="_nation_close" value="${_paginate.nationClose}" />
                <c:set var="_paging" value="" />

                <c:choose>
                    <c:when test="${_nation_begin gt 1}">
                        <c:set var="_page_prev" value="${_nation_begin - 1}" />
                        <c:set var="_paging">${_paging}<a href="?${_page_params}${_page_name}=${_page_prev}" page="${_page_prev}" class="prev">이전</a></c:set>
                    </c:when>
                    <c:otherwise>
                        <c:set var="_paging">${_paging}<a href="#" onclick="return false" class="prev">이전</a></c:set>
                    </c:otherwise>
                </c:choose>

                <c:forEach var="__p" begin="${_nation_begin}" end="${_nation_close}">
                    <c:choose>
                        <c:when test="${__p eq _page_no}">
                            <c:set var="_paging">${_paging}<strong class="current">${__p}</strong></c:set>
                        </c:when>
                        <c:otherwise>
                            <c:set var="_paging">${_paging}<a href="?${_page_params}${_page_name}=${__p}" page="${__p}">${__p}</a></c:set>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:choose>
                    <c:when test="${_nation_close ne _page_total}">
                        <c:set var="_page_next" value="${_nation_begin + _nation_size}" />
                        <c:set var="_paging">${_paging}<a href="?${_page_params}${_page_name}=${_page_next}" page="${_page_next}" class="next">다음</a></c:set>
                    </c:when>
                    <c:otherwise>
                        <c:set var="_paging">${_paging}<a href="#" onclick="return false" class="next">다음</a></c:set>
                    </c:otherwise>
                </c:choose>

                <c:out value="${_paging}" escapeXml="false" />
            </c:if>
        </div>
    </div>
</div>
<%--페이징 처리 끝--%>
   <%-- <%
    // 쿠키 배열 가져오기
    Cookie[] cookies = request.getCookies();
    boolean isLoggedIn = false;

    // 쿠키 배열을 순회하며 idx 쿠키의 존재 여부 확인
    if (cookies != null) {
        for (Cookie c : cookies) {
            if (c.getName().equals("idx")) {
                // idx 쿠키가 존재하면 로그인 상태로 설정
                isLoggedIn = true;
                break; // 로그인 상태이므로 더 이상 반복할 필요가 없음
            }
        }
    }
%>

<!-- 쿠키 정보 출력 -->
<div>
    <h3>쿠키 정보:</h3>
    <ul>
        <%
            if (cookies != null) {
                for (Cookie c : cookies) {
                    out.println("<li>" + c.getName() + " = " + c.getValue() + "</li>");
                }
            } else {
                out.println("<li>쿠키가 없습니다.</li>");
            }
        %>
    </ul>
</div>

<div class="button-container">
    <%
        // 로그인 상태에 따라 버튼 표시
        if (isLoggedIn) {
    %>
    <input type="button" value="user 목록" onclick="location.href='/user/main'"><br/><br/>
    <input type="button" value="글 작성" onclick="location.href='/write'"><br/><br/>
    <input type="button" value="로그아웃" onclick="location.href='/logout'"><br/><br/>
    <%
    } else {
    %>
    <input type="button" value="로그인" onclick="location.href='/login'"><br/><br/>
    <input type="button" value="회원가입" onclick="location.href='/join'"><br/><br/>
    <%
        }
    %>
</div>--%>

</body>
</html>


