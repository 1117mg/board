<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<head>
    <title>로그인페이지</title>
    <style>
        .container {
            max-width: 560px;
        }
        .field-error {
            border-color: #dc3545;
            color: #dc3545;
        }
    </style>
</head>
<body>

<div class="container">

    <div class="py-5 text-center">
        <h2>로그인</h2>
    </div>

    <form action="/login" method="post">

        <div>
            <label for="loginId">로그인 ID</label>
            <input type="text" id="loginId" name="loginId" value="${loginForm.loginId}" class="form-control"
                   th:errorclass="field-error">
        </div>
        <div>
            <label for="password">비밀번호</label>
            <input type="password" id="password" name="password" value="${loginForm.password}" class="form-control"
                   th:errorclass="field-error">
        </div>

        <hr class="my-4">

        <div class="row">
            <div class="col">
                <button class="w-100 btn btn-primary btn-lg" type="submit">로그인</button>
            </div>
            <div class="col">
                <button class="w-100 btn btn-secondary btn-lg" onclick="location.href='/login'">취소</button>
            </div>
        </div>

    </form>

</div> <!-- /container -->
</body>
</html>