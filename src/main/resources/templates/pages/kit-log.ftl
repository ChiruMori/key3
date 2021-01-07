<!DOCTYPE html>
<html lang="zh">

<head>
    <#include "../common/header.ftl">
    <link rel="stylesheet" href="/key3/vendor/select2/select2.min.css">
    <title>维护后台 - 运行日志</title>
    <style>
        .input-group > .select2-container--bootstrap {
            width: auto;
            flex: 1 1 auto;
        }

        .input-group > .select2-container--bootstrap .select2-selection--single {
            height: 100%;
            line-height: inherit;
            padding: 0.5rem 1rem;
            border-top-right-radius: 0;
            border-bottom-right-radius: 0;
        }
    </style>
</head>

<body id="page-top">

<!-- 整个页面的容器 -->
<div id="wrapper">
    <#include "../common/kit_side_bar.ftl">
    <!-- 内容区域 -->
    <div id="content-wrapper" class="d-flex flex-column">
        <!-- 主要内容区域 -->
        <div id="content">
            <!-- 页面内容 -->
            <div class="container-fluid">
                <!-- 页面标题 -->
                <div class="py-3">
                    <h1 class="h3 mb-0 mb-2">运行日志查看</h1>
                    <p class="mb-2">您可以在这里查看系统运行期间产生的运行日志，这些内容为 log4j 接管的日志内容。请首先选择要查看的日期后点击查询进行查看。</p>
                    <p class="mb-2">注意，日志文件有可能较大，下载耗时可能较长；测试环境使用本功能可能造成日志指数级增长。非必要情况尽量不要随意查看，这会极大占用系统带宽与性能。</p>
                </div>
                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <h6 class="m-0 font-weight-bold text-danger">运行日志查看</h6>
                    </div>
                    <div class="card-body">
                        <div class="input-group">
                            <select class="custom-select" id="logDateSelect" aria-label="选择要查看的日志日期">
                            </select>
                            <div class="input-group-append">
                                <button class="btn btn-outline-secondary" type="button" id="selectAppendBtn"><i
                                            class="fas fa-search"></i> 查看
                                </button>
                            </div>
                        </div>
                        <h4 class="my-3" id="logTitle"></h4>
                        <samp id="logOutputArea"></samp>
                    </div>
                </div>
            </div>
        </div>
        <!-- 内容区域结束 -->
    </div>
    <!-- End of Content Wrapper -->
</div>
<!-- End of Page Wrapper -->
<#include "../common/footer.ftl">
<script src="/key3/vendor/select2/select2.min.js"></script>
<script src="/key3/vendor/select2/zh-CN.min.js"></script>
<script>

    const COLOR_MAP = {
        'INFO': 'text-info',
        'ERROR': 'text-danger',
        'error': 'text-danger',
        'WARN': 'text-warning',
        'failed': 'text-danger'
    }

    const wrapLine = function (line) {
        for (let colormapKey in COLOR_MAP) {
            if (!COLOR_MAP.hasOwnProperty(colormapKey)) continue;
            let split = line.split(colormapKey);
            line = split.join('<span class="' + COLOR_MAP[colormapKey] + '">' + colormapKey + '</span>');
        }
        return '<p>' + line + '</p>';
    };

    $().ready(function () {
        const logDateSelect = $('#logDateSelect');
        const selectAppendBtn = $('#selectAppendBtn');
        const logOutputArea = $('#logOutputArea');
        const logTitle = $('#logTitle');

        selectAppendBtn.on('click', () => {
            utils.showLoading("下载日志文件中");
            let selectedDate = logDateSelect.val();
            utils.ajax('kit/api/log/' + selectedDate + location.search, {}, 'GET', content => {
                utils.hideLoading();
                utils.showLoading("解析日志文件内容");
                logTitle.html(new Date(Number(selectedDate)).toLocaleDateString() + '的运行日志内容');
                let lines = content.data.value.split('\r\n');
                // 过滤掉空行
                lines.filter(s => s.length !== 0);
                // 清空原有显示
                logOutputArea.html('');
                lines.forEach(line => {
                    logOutputArea.append(wrapLine(line));
                });
            }, err => utils.error('请求出错', err.data.msg), () => utils.hideLoading());
        });

        utils.ajax('kit/api/dates' + location.search, {}, 'GET', dateArray => {
            logDateSelect.html('');
            dateArray.data.forEach(datetime => {
                let showTime = new Date(datetime).toLocaleDateString();
                logDateSelect.append($('<option value="' + datetime + '">' + showTime + '</option>'));
            }, err => utils.error('请求出错', err.data.msg));
            logDateSelect.select2({
                placeholder: '选择要查看的日期',
                language: 'zh-CN',
                theme: 'bootstrap',
                selectionCssClass: 'custom-select'
            });
        });
    });
</script>

</body>
</html>