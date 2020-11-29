<!DOCTYPE html>
<html lang="en">

<head>
    <title>管理后台 - 财务</title>
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
                <h1 class="h3 mb-2 text-gray-800">社团收支管理</h1>
                <p class="mb-4" id="hintArea">您可以通过新建收支来调整社团经费。</p>

                <div class="row">
                    <div class="col mb-4">
                        <div class="card border-left-warning shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="h3 font-weight-bold text-warning text-uppercase mb-1">
                                            可用
                                        </div>
                                    </div>
                                    <div class="col-auto d-inline-flex">
                                        <i class="fas fa-yen-sign fa-2x text-warning"></i>
                                        <div class="h3 mb-0 font-weight-bold text-gray-800 ml-2" id="clubAssets">0</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- 经费一览表 -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">经费一览表</h6>
                    </div>
                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                <thead>
                                <tr>
                                    <th>流水号</th>
                                    <th>时间</th>
                                    <th>创建者</th>
                                    <th>事由</th>
                                    <th>收支</th>
                                </tr>
                                </thead>
                                <tfoot>
                                <tr>
                                    <th>流水号</th>
                                    <th>时间</th>
                                    <th>创建者</th>
                                    <th>事由</th>
                                    <th>收支</th>
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
<script src="/key3/js/club-bill.min.js"></script>

</body>

</html>