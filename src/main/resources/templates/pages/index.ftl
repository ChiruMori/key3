<!DOCTYPE html>
<html lang="zh">

<head>
    <title>管理后台 - 仪表盘</title>
    <#include "../common/header.ftl">
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
            <div class="container-fluid">

                <!-- 页面标题 -->
                <div class="d-sm-flex align-items-center justify-content-between mb-4">
                    <h1 class="h3 mb-0 text-gray-800" id="pageTitle">没加载粗来</h1>
                </div>

                <!-- 统计信息 -->
                <div class="row">

                    <!-- 成员统计 -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-primary shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                            注册成员数</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800" id="enrollNumber">0</div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-users fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 已激活用户 -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-success shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                            已激活用户数
                                        </div>
                                        <div class="row no-gutters align-items-center">
                                            <div class="col-auto">
                                                <div class="h5 mb-0 mr-3 font-weight-bold text-gray-800" id="activeMembers">0</div>
                                            </div>
                                            <div class="col">
                                                <div class="progress progress-sm mr-2">
                                                    <div class="progress-bar bg-success" role="progressbar"
                                                         style="width: 0" aria-valuenow="50" aria-valuemin="0"
                                                         aria-valuemax="100" id="activeBar"></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-user-check fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 社团可用经费 -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-warning shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                            社团可用经费</div>
                                        <div class="h5 mb-0 font-weight-bold text-gray-800" id="enableAssets">0</div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fas fa-yen-sign fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- 活动室使用率 -->
                    <div class="col-xl-3 col-md-6 mb-4">
                        <div class="card border-left-info shadow h-100 py-2">
                            <div class="card-body">
                                <div class="row no-gutters align-items-center">
                                    <div class="col mr-2">
                                        <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                            活动室使用率 (本周)</div>
                                        <div class="row no-gutters align-items-center">
                                            <div class="col-auto">
                                                <div class="h5 mb-0 mr-3 font-weight-bold text-gray-800" id="roomUsage">0%</div>
                                            </div>
                                            <div class="col">
                                                <div class="progress progress-sm mr-2">
                                                    <div class="progress-bar bg-info" role="progressbar"
                                                         style="width: 0" aria-valuenow="50" id="usageBar" aria-valuemin="0"
                                                         aria-valuemax="100"></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-auto">
                                        <i class="fab fa-buromobelexperte fa-2x text-gray-300"></i>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- 其他版块 -->
                <div class="row">

                    <!-- 系统日志 -->
                    <div class="col-lg-6 mb-4">

                        <!-- 日志版块 -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">系统日志</h6>
                            </div>
                            <div class="card-body">
                                <ul class="list-group list-group-flush" id="logArea"></ul>
                                <a class="dropdown-item text-center small text-gray-500" href="#" id="allLogATag">什么都没有...</a>
                            </div>
                        </div>
                    </div>

                    <div class="col-lg-6 mb-4">
                        <!-- 最近收支版块 -->
                        <div class="card shadow mb-4">
                            <div class="card-header py-3">
                                <h6 class="m-0 font-weight-bold text-primary">收支</h6>
                            </div>
                            <div class="card-body">
                                <ul class="list-group list-group-flush" id="billArea"></ul>
                                <a class="dropdown-item text-center small text-gray-500" href="#" id="allBillATag">什么都没有...</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 内容区域结束 -->

        <#include "../common/copyright.ftl">
    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<#include "../common/footer.ftl">
<script type="text/javascript" src="/key3/js/index.min.js"></script>
</body>
</html>