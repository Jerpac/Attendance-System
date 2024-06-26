<html>

<head>
    <title>Login</title>
</head>
<link rel="stylesheet" type="text/css" href="Login.css">

<body>
    <div class="container">
        <div id='utd_image_container'>
            <div class='test'>
                <img src="utd_logo.png">
            </div>
        </div>
        <div class="screen">
            <div class="screen__content">
                <form class="login" action="LoginController" method="POST">
                    <div class="login__field">
                        <i class="login__icon fas fa-user"></i>
                        <input type="text" class="login__input" placeholder="10-digit UTD ID" name="student-id">
                    </div>
                    <div class="login__field">
                        <i class="login__icon fas fa-lock"></i>
                        <input type="password" class="login__input" placeholder="Password" name="password-input">
						<%
						String errorMessage = (String) request.getAttribute("errorMessage");
						%>
						<%
						if (errorMessage != null) {
						%>
						<p class="error">
							<%=errorMessage%>
						</p>
						<%
						}
						%>
					</div>
                    <button id="login_button" class="button login__submit">
                        <span class="button__text">Login</span>
                        <i class="button__icon fas fa-chevron-right"></i>
                    </button>
                </form>
            </div>
            <div class="screen__background">
                <span class="screen__background__shape screen__background__shape4"></span>
                <span class="screen__background__shape screen__background__shape3"></span>
                <span class="screen__background__shape screen__background__shape2"></span>
                <span class="screen__background__shape screen__background__shape1"></span>
            </div>
        </div>
    </div>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
    <script type="text/javascript" src="Login.js"></script>
</body>

</html>