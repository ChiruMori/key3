<!DOCTYPE html>
<html lang="zh">

<head>
    <#include "../common/header.ftl">
    <title>管理后台 - 登录</title>
</head>

<body class="bg-gradient-primary">

<div class="container">

    <!-- Outer Row -->
    <div class="row justify-content-center">

        <div class="col-xl-10 col-lg-12 col-md-9">

            <div class="card o-hidden border-0 shadow-lg my-5">
                <div class="card-body p-0">
                    <!-- Nested Row within Card Body -->
                    <div class="row">
                        <div class="col-lg-6 d-none d-lg-block bg-login-image"></div>
                        <div class="col-lg-6">
                            <div class="p-5">
                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-4">Welcome Back!</h1>
                                </div>
                                <form class="user" id="loginForm">
                                    <div class="form-group">
                                        <input type="number" class="form-control form-control-user"
                                               id="studentNoInput" name="studentNo"
                                               placeholder="输入您的学号">
                                        <small id="studentNoHint" class="form-text text-danger ml-3"></small>
                                    </div>
                                    <div class="form-group">
                                        <input type="text" class="form-control form-control-user"
                                               id="passcodeInput" name="passcode"
                                               placeholder="输入您的登录口令">
                                        <small id="passcodeHint" class="form-text text-danger ml-3"></small>
                                    </div>
                                    <div id="errMsgDiv" class="text-danger small mb-2 ml-2"></div>
                                    <a id="loginBtn" href="#" class="btn btn-primary btn-user btn-block">登录管理后台</a>
                                    <a id="maintainBtn" href="#" class="btn btn-outline-danger btn-user btn-block">登录维护后台</a>
                                </form>
                                <hr>
                                <div class="text-center">
                                    <a class="small" href="#"
                                       onclick="utils.alert('如果您是管理员，可以登录小程序后在页面“我”中点击生成口令选项，即可生成您的口令。注意，口令只能使用一次，非管理员无法生成口令')">如何获取登录口令？</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<#include "../common/footer.ftl">
<script type="application/javascript" src="/key3/js/login.min.js"></script>
</body>

</html>