<!DOCTYPE html>
<html lang="zh">

<head>
    <title>管理后台 - 成员</title>
    <#include "../common/table-header.ftl">
</head>

<body id="page-top">

<!-- 整个页面的容器 -->
<div id="wrapper">

    <#include "../common/side_bar.ftl">

    <#-- 内容区域 -->
    <div id="content-wrapper" class="d-flex flex-column">

        <!-- 主要内容区域 -->
        <div id="content">

            <#include "../common/top_bar.ftl">

            <!-- Begin Page Content -->
            <div class="container-fluid">

                <!-- Page Heading -->
                <h1 class="h3 mb-2 text-gray-800">社团成员基本信息管理</h1>
                <p class="mb-2">您可以在此页面的表格中修改社团成员的基本信息，如果需要为当前社团增加用户或者删除用户，请在社团关系页面进行操作。</p>
                <#if base.systemAdmin>
                    <p class="mb-2 text-warning">因为您是系统管理员，所以在这里提供删除、新建功能，但是注意，您新建的用户是与社团没有任何关联的；
                    您删除用户时，会一并删除用户所有的关键信息，包括在社团内的信息，如贡献点数、社团职务、预定信息等</p>
                </#if>


                <!-- 经费一览表 -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">成员信息一览表</h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>头像</th>
                                    <th>姓名</th>
                                    <th>微信名</th>
                                    <th>学号</th>
                                    <th>学院</th>
                                    <th>专业</th>
                                    <th>邮箱</th>
                                    <th>个性签名</th>
                                </tr>
                                </thead>
                                <tfoot>
                                <tr>
                                    <th>ID</th>
                                    <th>头像</th>
                                    <th>姓名</th>
                                    <th>微信名</th>
                                    <th>学号</th>
                                    <th>学院</th>
                                    <th>专业</th>
                                    <th>邮箱</th>
                                    <th>个性签名</th>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.container-fluid -->

        </div>

        <!-- 主要内容区域结束 -->

        <#include "../common/copyright.ftl">

    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<#include "../common/table-footer.ftl">
<script src="/key3/js/club-users.min.js"></script>
</body>
</html>