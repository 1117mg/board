<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE HTML>
<head>
    <title>회원가입페이지</title>
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
<h1>
    회원가입
</h1>

<form action="/join" method="post">
    <div>
        <label for="loginId">아이디:</label>
        <input type="text" id="loginId" name="loginId" required>
        <button type="button" onclick="checkUsername()">중복 확인</button>
        <div id="usernameCheckResult"></div>
    </div>
    <div>
        <label for="password">비밀번호:</label>
        <input type="password" id="password" name="password" required>
    </div>
    <div>
        <label for="passwordCheck">비밀번호 확인:</label>
        <input type="password" id="passwordCheck" name="passwordCheck" required>
    </div>
    <div>
        <label for="username">이름:</label>
        <input type="text" id="username" name="username" required>
    </div>
    <div>
        <button type="submit">가입하기</button>
    </div>
</form>
</body>
<script>
    function checkUsername() {
        const loginIdInput = document.getElementById('loginId');
        const loginId = loginIdInput.value;
        const resultDiv = document.getElementById('usernameCheckResult');

        fetch(`/check-username?loginId=`+loginId)
            .then(response => response.json())
            .then(data => {
                if (data) {
                    resultDiv.textContent = '이미 사용 중인 아이디입니다.';
                    resultDiv.style.color = 'red';
                    loginIdInput.classList.add('field-error');
                } else {
                    resultDiv.textContent = '사용 가능한 아이디입니다.';
                    resultDiv.style.color = 'green';
                    loginIdInput.classList.remove('field-error');
                }
            });
    }
</script>
</html>
