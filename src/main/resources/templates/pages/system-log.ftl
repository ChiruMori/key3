<!DOCTYPE html>
<html lang="en">

<head>
    <title>管理后台 - 日志</title>
    <#include "../common/header.ftl">
    <#-- 只读数据表格使用 -->
    <link rel="stylesheet" href="/key3/vendor/datatables/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="/key3/vendor/datatables/responsive/responsive.bootstrap4.min.css">
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
                <h1 class="h3 mb-2 text-gray-800">系统日志查看</h1>
                <p class="mb-4" id="hintArea">系统日志，通常是系统根据用户行为或特殊任务生成的系统记录，可用于追踪用户行为、分析系统状态等。不支持直接修改。</p>

                <#-- 日志一览表 -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">日志一览表</h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                <thead>
                                <tr>
                                    <th>头像</th>
                                    <th>人员</th>
                                    <th>时间</th>
                                    <th>内容</th>
                                    <th>IP</th>
                                </tr>
                                </thead>
                                <tfoot>
                                <tr>
                                    <th>头像</th>
                                    <th>人员</th>
                                    <th>时间</th>
                                    <th>内容</th>
                                    <th>IP</th>
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

<#include "../common/footer.ftl">

<script src="/key3/vendor/datatables/jquery.dataTables.min.js"></script>
<script src="/key3/vendor/datatables/dataTables.bootstrap4.min.js"></script>
<script src="/key3/vendor/datatables/responsive/dataTables.responsive.min.js"></script>
<script src="/key3/vendor/datatables/responsive/responsive.bootstrap4.min.js"></script>

<script src="/key3/js/system-log.min.js"></script>

</body>

</html>