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
                <h1 class="h3 mb-2 text-gray-800">社团成员关系管理</h1>
                <p class="mb-2">您可以在此页面的表格中修改成员加入社团后产生的数据，您可以从当前社团中添加或删除成员。</p>
                <p class="mb-2">如果您需要修改成员的基本信息，请移步<a class="token-tag" href="/key3/admin/page/club-users">成员基本信息管理</a>页面
                </p>

                <div class="row">
                    <div class="col mb-4">
                        <div class="card border-left-primary shadow h-100 py-2">

                            <div class="card-body">
                                <h6>你可以通过一个 Excel 文件来批量导入成员信息，点击<a href="/key3/vendor/template.xlsx" class="font-weight-bold">此链接</a>开始下载 Excel
                                    的模板文件。</h6>
                                <hr/>

                                <div class="input-group">
                                    <div class="custom-file">
                                        <input type="file" class="custom-file-input" id="fileUploader"
                                               aria-describedby="fileUplodBtn">
                                        <label class="custom-file-label" for="fileUploader">选择要上传的文件</label>
                                    </div>
                                    <div class="input-group-append">
                                        <button class="btn btn-outline-secondary" type="button" id="fileUploadBtn"><i class="fas fa-upload"></i> 上传
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

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
                                    <th>头像</th>
                                    <th>姓名</th>
                                    <th>学号</th>
                                    <th>职务</th>
                                    <th>时长</th>
                                    <th>贡献值</th>
                                    <th>周缺勤数</th>
                                    <th>管理员</th>
                                </tr>
                                </thead>
                                <tfoot>
                                <tr>
                                    <th>头像</th>
                                    <th>姓名</th>
                                    <th>学号</th>
                                    <th>职务</th>
                                    <th>时长</th>
                                    <th>贡献值</th>
                                    <th>周缺勤数</th>
                                    <th>管理员</th>
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
<script src="/key3/js/club-relation.min.js"></script>
</body>
</html>