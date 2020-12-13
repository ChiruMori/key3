<!DOCTYPE html>
<html lang="en">

<head>
    <title>管理后台 - 日志</title>
    <#include "../common/header.ftl">
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
                <h1 class="h3 mb-2 text-gray-800">系统参数设置</h1>
                <p class="mb-4">您可以在这里调整系统选项，注意有些参数的调整可能会影响系统运行，请注意各部分说明</p>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-primary">系统参数选项</h6>
                    </div>
                    <div class="card-body">
                        <ul class="nav nav-tabs" id="myTab" role="tablist">
                            <li class="nav-item" role="presentation">
                                <a class="nav-link active" id="runtime-tab" data-toggle="tab" href="#runtime"
                                   role="tab" aria-controls="runtime" aria-selected="true"><i
                                            class="fas fa-sitemap"></i> 运行选项</a>
                            </li>
                            <li class="nav-item" role="presentation">
                                <a class="nav-link" id="smtp-tab" data-toggle="tab" href="#smtp" role="tab"
                                   aria-controls="smtp" aria-selected="false"><i class="fas fa-envelope"></i>
                                    邮件配置</a>
                            </li>
                            <li class="nav-item" role="presentation">
                                <a class="nav-link" id="primary-tab" data-toggle="tab" href="#primary" role="tab"
                                   aria-controls="primary" aria-selected="false"><i class="fas fa-edit"></i>
                                    定制化</a>
                            </li>
                        </ul>
                        <div class="tab-content p-2" id="myTabContent">
                            <div class="tab-pane fade show active" id="runtime" role="tabpanel"
                                 aria-labelledby="runtime-tab">
                                <h5 class="mt-2">包含可能需要经常修改的配置项，提交修改后即可生效。</h5>
                                <hr/>
                                <form onsubmit="return false;" id="runtimeForm">
                                    <div class="form-group">
                                        <label for="week_start_stamp">周次计算起始日期</label>
                                        <input type="date" class="form-control" id="week_start_stamp"
                                               aria-describedby="dateHelp" name="week_start_stamp">
                                        <small id="dateHelp"
                                               class="form-text text-muted">调整这个值为第一周的任意一天，即可在时间表格中显示当前周次</small>
                                    </div>
                                    <button type="submit" class="btn btn-primary" id="runtimeBtn">提交</button>
                                </form>
                            </div>
                            <div class="tab-pane fade" id="smtp" role="tabpanel" aria-labelledby="smtp-tab">
                                <h5 class="mt-2">配置发送系统邮件的配置，通常不需要修改，如果不清楚这是什么请不要修改。</h5>
                                <hr/>
                                <form onsubmit="return false;" id="smtpForm">
                                    <div class="form-group">
                                        <label for="email_protocol">协议</label>
                                        <input type="text" name="email_protocol" class="form-control"
                                               id="email_protocol" aria-describedby="protocol_help"
                                               placeholder="如：smtp">
                                        <small id="protocol_help" class="form-text text-muted">邮件协议</small>
                                    </div>
                                    <div class="form-group">
                                        <label for="email_host">服务器</label>
                                        <input type="text" name="email_host" class="form-control" id="email_host"
                                               aria-describedby="email_host_help">
                                        <small id="email_host_help" class="form-text text-muted">如：smtp.163.com</small>
                                    </div>
                                    <div class="form-group">
                                        <label for="email_ssl_port">SSL 端口</label>
                                        <input type="number" name="email_ssl_port" class="form-control"
                                               id="email_ssl_port">
                                    </div>
                                    <div class="form-group">
                                        <label for="email_username">邮箱账户</label>
                                        <input type="text" name="email_username" class="form-control"
                                               id="email_username" aria-describedby="email_name_help">
                                        <small id="email_name_help" class="form-text text-muted">这通常为您的邮箱地址</small>
                                    </div>
                                    <div class="form-group">
                                        <label for="email_password">账户密码</label>
                                        <input type="text" name="email_password" class="form-control"
                                               id="email_password" aria-describedby="email_pwd_help">
                                        <small id="email_pwd_help" class="form-text text-muted">这通常为您的邮箱密码</small>
                                    </div>
                                    <div class="form-group">
                                        <label for="email_from_name">发件人昵称</label>
                                        <input type="text" name="email_from_name" class="form-control"
                                               id="email_from_name" aria-describedby="from_name_help">
                                        <small id="from_name_help" class="form-text text-muted">系统邮件的发件人名称</small>
                                    </div>
                                    <button type="submit" class="btn btn-primary" id="smtpBtn">提交</button>
                                    <button type="submit" class="btn btn-info" id="smtpTestBtn">测试</button>
                                </form>
                            </div>
                            <div class="tab-pane fade" id="primary" role="tabpanel" aria-labelledby="primary-tab">
                                <form onsubmit="return false;" id="primaryForm">
                                    <div class="form-group">
                                        <label for="favicon_url">站点图标 URL</label>
                                        <input type="text" class="form-control" id="favicon_url" name="favicon_url">
                                    </div>
                                    <div class="form-group">
                                        <label for="mini_code_url">小程序码 URL</label>
                                        <input type="text" class="form-control" id="mini_code_url" name="mini_code_url">
                                    </div>
                                    <button type="submit" class="btn btn-primary" id="primaryBtn">提交</button>
                                </form>
                            </div>
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

<script src="/key3/js/system-opt.min.js"></script>

</body>

</html>