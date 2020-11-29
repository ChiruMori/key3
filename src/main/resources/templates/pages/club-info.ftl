<!DOCTYPE html>
<html lang="zh">

<head>
    <title>管理后台 - 社团</title>
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
                <h1 class="h3 mb-2 text-gray-800">社团基本信息维护</h1>
                <p class="mb-4" id="hintText">您可以在这里修改社团名称、直接修改社团可用经费，也可以禁用社团财务功能。如果您管理了多个社团，可以点击上方社团名称进行切换，或者新增社团。</p>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">社团信息</h6>
                    </div>
                    <div class="card-body">
                        <form id="club-info-form" onsubmit="return false;">
                            <input id="clubId" name="clubId" class="d-none" type="text">
                            <div class="form-group">
                                <label for="clubName">社团名称 <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="clubName" name="name"
                                       aria-describedby="clubNameHelp">
                                <small id="clubNameHelp" class="form-text text-muted">社团名称，不可超过 50 个字符</small>
                            </div>
                            <div class="form-group">
                                <label for="clubAssets">可用社团经费</label>
                                <input type="text" class="form-control" id="clubAssets" name="assets"
                                       aria-describedby="clubAssetsHelp">
                                <small id="clubAssetsHelp"
                                       class="form-text text-muted">不建议直接修改，建议通过财务模块添加收支项来修改经费</small>
                            </div>
                            <div class="form-group form-check">
                                <input type="checkbox" class="form-check-input" id="enableAssets" name="billEnabled">
                                <label class="form-check-label" for="enableAssets">启用财务功能</label>
                            </div>
                            <button type="submit" class="btn btn-primary" id="submitBtn">提交修改</button>
                        </form>
                    </div>
                </div>

            </div>
        </div>
        <#-- 内容区域结束 -->

        <#include "../common/copyright.ftl">
    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<#include "../common/footer.ftl">
<script type="text/javascript" src="/key3/js/club-info.min.js"></script>
</body>
</html>