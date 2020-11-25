<!DOCTYPE html>
<html lang="en">

<head>
    <#include "../common/header.ftl">
    <title>管理后台 - 哦吼完蛋</title>
</head>

<body id="page-top">

<!-- Page Wrapper -->
<div id="wrapper">

    <#include "../common/side_bar.ftl">

    <!-- Content Wrapper -->
    <div id="content-wrapper" class="d-flex flex-column">

        <!-- Main Content -->
        <div id="content">

            <#include "../common/top_bar.ftl">

            <!-- Begin Page Content -->
            <div class="container-fluid">

                <!-- Error Text -->
                <div class="text-center">
                    <div class="error mx-auto" data-text="${(error.status)!500}">${(error.status)!500}</div>
                    <p class="lead text-gray-800 mb-5">${(error.error)!'未知错误'}</p>
                    <p class="text-gray-500 mb-0">${(error.message)!'未知错误！请联系系统管理员报告问题'}</p>
                    <a href="/key3/admin/page/dashboard">&larr; 返回到仪表盘</a>
                </div>

            </div>
            <!-- /.container-fluid -->

        </div>
        <!-- End of Main Content -->

        <#include "../common/copyright.ftl">

    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<#include "../common/footer.ftl">
</body>

</html>