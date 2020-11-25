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
                    <h1 class="h3 mb-0 text-gray-800">键盘乐团</h1>
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
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">199</div>
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
                                                <div class="h5 mb-0 mr-3 font-weight-bold text-gray-800">77</div>
                                            </div>
                                            <div class="col">
                                                <div class="progress progress-sm mr-2">
                                                    <div class="progress-bar bg-success" role="progressbar"
                                                         style="width: 80%" aria-valuenow="50" aria-valuemin="0"
                                                         aria-valuemax="100"></div>
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
                                        <div class="h5 mb-0 font-weight-bold text-gray-800">302</div>
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
                                                <div class="h5 mb-0 mr-3 font-weight-bold text-gray-800">89%</div>
                                            </div>
                                            <div class="col">
                                                <div class="progress progress-sm mr-2">
                                                    <div class="progress-bar bg-info" role="progressbar"
                                                         style="width: 80%" aria-valuenow="50" aria-valuemin="0"
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
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item d-flex align-items-center">
                                        <div class="img-profile mr-3">
                                            <img class="w-100 rounded-circle"
                                                 src="https://thirdwx.qlogo.cn/mmopen/vi_32/fCHucn9xIdhq7LM88tCcMx3CibY0bAoqEW1ebbVjS6DLxMBhZ4gcFxibaOlHe17ReicwmtLe3c9jvluetw3ibKdcJA/132"
                                                 alt="">
                                            <div class="status-indicator"></div>
                                        </div>
                                        <div class="w-100">
                                            <div class="text-truncate">预定了 209 活动室的 9:00~10:00</div>
                                            <div class="small text-gray-500">羚初雪 · 1d</div>
                                        </div>
                                    </li>
                                    <li class="list-group-item d-flex align-items-center">
                                        <div class="img-profile mr-3">
                                            <img class="w-100 rounded-circle"
                                                 src="https://thirdwx.qlogo.cn/mmopen/vi_32/fCHucn9xIdhq7LM88tCcMx3CibY0bAoqEW1ebbVjS6DLxMBhZ4gcFxibaOlHe17ReicwmtLe3c9jvluetw3ibKdcJA/132"
                                                 alt="">
                                            <div class="status-indicator"></div>
                                        </div>
                                        <div class="w-100">
                                            <div class="text-truncate">登陆了管理后台</div>
                                            <div class="small text-gray-500">羚初雪 · 2d</div>
                                        </div>
                                    </li>
                                    <li class="list-group-item d-flex align-items-center">
                                        <div class="img-profile mr-3">
                                            <img class="w-100 rounded-circle"
                                                 src="https://thirdwx.qlogo.cn/mmopen/vi_32/fCHucn9xIdhq7LM88tCcMx3CibY0bAoqEW1ebbVjS6DLxMBhZ4gcFxibaOlHe17ReicwmtLe3c9jvluetw3ibKdcJA/132"
                                                 alt="">
                                            <div class="status-indicator"></div>
                                        </div>
                                        <div class="w-100">
                                            <div class="text-truncate">添加了用户 [2220173210]
                                                到键盘乐团，然后生成了一个超长超长的系统日志放在了这里</div>
                                            <div class="small text-gray-500">羚初雪 · 2d</div>
                                        </div>
                                    </li>
                                </ul>
                                <a class="dropdown-item text-center small text-gray-500" href="#">全部日志...</a>
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
                                <ul class="list-group list-group-flush">
                                    <li class="list-group-item d-flex align-items-center">
                                        <div class="img-profile mr-3">
                                            <img class="w-100 rounded-circle"
                                                 src="https://thirdwx.qlogo.cn/mmopen/vi_32/fCHucn9xIdhq7LM88tCcMx3CibY0bAoqEW1ebbVjS6DLxMBhZ4gcFxibaOlHe17ReicwmtLe3c9jvluetw3ibKdcJA/132"
                                                 alt="">
                                            <div class="status-indicator"></div>
                                        </div>
                                        <div class="w-100 pr-rem-5">
                                            <div class="text-truncate">209 钢琴调音</div>
                                            <div class="small text-gray-500">羚初雪 · 1d</div>
                                        </div>
                                        <p class="bill font-weight-bold text-success">-150</p>
                                    </li>
                                    <li class="list-group-item d-flex align-items-center">
                                        <div class="img-profile mr-3">
                                            <img class="w-100 rounded-circle"
                                                 src="https://thirdwx.qlogo.cn/mmopen/vi_32/fCHucn9xIdhq7LM88tCcMx3CibY0bAoqEW1ebbVjS6DLxMBhZ4gcFxibaOlHe17ReicwmtLe3c9jvluetw3ibKdcJA/132"
                                                 alt="">
                                            <div class="status-indicator"></div>
                                        </div>
                                        <div class="w-100 pr-rem-5">
                                            <div class="text-truncate">金主XXX赞助演出</div>
                                            <div class="small text-gray-500">羚初雪 · 2d</div>
                                        </div>
                                        <p class="bill font-weight-bold text-danger">+1500</p>
                                    </li>
                                    <li class="list-group-item d-flex align-items-center">
                                        <div class="img-profile mr-3">
                                            <img class="w-100 rounded-circle"
                                                 src="https://thirdwx.qlogo.cn/mmopen/vi_32/fCHucn9xIdhq7LM88tCcMx3CibY0bAoqEW1ebbVjS6DLxMBhZ4gcFxibaOlHe17ReicwmtLe3c9jvluetw3ibKdcJA/132"
                                                 alt="">
                                            <div class="status-indicator"></div>
                                        </div>
                                        <div class="w-100 pr-rem-5">
                                            <div class="text-truncate">演出费用（场地租金、宣传材料、服装租金、摄影）</div>
                                            <div class="small text-gray-500">羚初雪 · 2d</div>
                                        </div>
                                        <p class="bill font-weight-bold text-success">-2000</p>
                                    </li>
                                </ul>
                                <a class="dropdown-item text-center small text-gray-500" href="#">全部收支...</a>
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
</body>
</html>