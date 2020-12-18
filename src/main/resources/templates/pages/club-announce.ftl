<!DOCTYPE html>
<html lang="zh">

<head>
    <title>管理后台 - 公告管理</title>
    <#include "../common/table-header.ftl">
</head>

<body id="page-top">

<!-- 整个页面的容器 -->
<div id="wrapper">

    <#include "../common/side_bar.ftl">

    <#-- 内容区域 -->
    <div id="content-wrapper" class="d-flex flex-column">

        <#-- 主要内容区域 -->
        <div id="content">

            <#include "../common/top_bar.ftl">

            <!-- 页面内容 -->
            <!-- Begin Page Content -->
            <div class="container-fluid">

                <!-- Page Heading -->
                <h1 class="h3 mb-2 text-gray-800">公告管理</h1>
                <p class="mb-2">您可以在这里发布、修改、删除公告（注意，即使删除了公告，因公告发出的通知无法撤回）</p>
                <#if base.systemAdmin>
                    <p class="mb-2 text-warning">您是系统管理员，可以发布社团无关的全局系统公告，所有的用户都会接收到，可用于通知系统相关的调整、变动等。</p>
                </#if>

                <#-- 公告一览表 -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">社团公告一览表</h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>头像</th>
                                    <th>发布人</th>
                                    <th>时间</th>
                                    <th>标题</th>
                                    <th>内容</th>
                                </tr>
                                </thead>
                                <tfoot>
                                <tr>
                                    <th>ID</th>
                                    <th>头像</th>
                                    <th>发布人</th>
                                    <th>时间</th>
                                    <th>标题</th>
                                    <th>内容</th>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>

            </div>
            <!-- /.container-fluid -->
        </div>
        <#-- 内容区域结束 -->

        <#include "../common/copyright.ftl">
    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<#include "../common/table-footer.ftl">
<script type="text/javascript" src="/key3/js/club-announce.min.js"></script>
</body>
</html>