<!DOCTYPE html>
<html lang="zh">

<head>
    <title>管理后台 - 预约信息</title>
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

                <!-- Page Heading -->
                <h1 class="h3 mb-2 text-gray-800">活动室预约信息管理</h1>
                <p class="mb-2">您可以点击单元格以选择时段，并使用页面下方的功能按钮进行管理。</p>
                <p class="mb-2">占用时段后，该时段不可被用户预约，在占用时，可以选择不同颜色以区分显示；清除时段后，时段的占用、用户的预约都会被解除；编辑显示可以修改非空闲时段的显示文本</p>

                <!-- DataTales Example -->
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <div class="form-row">
                            <h6
                                    class="m-0 font-weight-bold text-primary col form-control p-2 border-0 bg-transparent">
                                预约管理</h6>
                            <select class="form-control col-md-8" id="roomSelectTag">
                                <option>部活室</option>
                            </select>
                        </div>
                    </div>
                    <div class="card-body my-3">
                        <div class="form-row">
                            <button id="prevBtn" type="button" class="form-control btn btn-primary col-1 min-w-2"><i
                                        class="fas fa-angle-double-left"></i></button>
                            <div class="form-control bg-transparent p-2 text-gray-800 border-0 col text-center">第<span
                                        id="weekNumber">~</span>周
                            </div>
                            <button id="nextBtn" type="button" class="form-control btn btn-primary col-1 min-w-2"><i
                                        class="fas fa-angle-double-right"></i></button>
                        </div>
                        <div class="time-tb my-3">
                            <div class="head row">
                                <div class="col"></div>
                                <div class="col">MON.</div>
                                <div class="col">TUE.</div>
                                <div class="col">WED.</div>
                                <div class="col">THU.</div>
                                <div class="col">FRI.</div>
                                <div class="col">SAT.</div>
                                <div class="col">SUN.</div>
                            </div>
                            <div id="tabBody"></div>
                        </div>

                        <button class="btn btn-primary" id="blockBtn">占用时段</button>
                        <button class="btn btn-danger" id="clearBtn">清除时段</button>
                        <button class="btn btn-success" id="editBtn">编辑显示</button>
                    </div>

                </div>
                <!-- /.container-fluid -->
            </div>
            <#-- 内容区域结束 -->

            <#include "../common/copyright.ftl">
        </div>
        <!-- End of Content Wrapper -->
    </div>
</div>
<!-- End of Page Wrapper -->
<#include "../common/footer.ftl">
<script type="text/javascript" src="/key3/js/room-time.js"></script>
</body>
</html>