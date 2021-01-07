<!DOCTYPE html>

<head>
    <#include "../common/table-header.ftl">
    <title>维护后台 - 缓存维护</title>

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
                    <h1 class="h3 mb-0 mb-2">系统缓存</h1>
                    <p class="mb-2">缓存的 CRUD，注意，这些操作对系统的性能有严重影响，甚至可能造成数据错误，请务必在必要且明确知道意图的情况下使用。</p>
                </div>

                <div class="card shadow mb-4">
                    <div class="card-header py-3" style="display: inline-flex">
                        <h6 class="m-0 font-weight-bold text-danger" style="flex: 1; padding-top: 0.5em;">缓存维护</h6>
                        <button id="clearCacheBtn" class="btn btn-danger"><i class="fas fa-eraser"></i> 清空缓存</button>
                    </div>

                    <div class="card-body">
                        <div class="table-responsive">
                            <table class="table table-bordered" id="dataTable" width="100%" cellspacing="0">
                                <thead>
                                <tr>
                                    <th>key</th>
                                    <th>value</th>
                                </tr>
                                </thead>
                                <tfoot>
                                <tr>
                                    <th>key</th>
                                    <th>value</th>
                                </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- 内容区域结束 -->
    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->
<#include "../common/table-footer.ftl">
<script>
    let dataTableInstant;

    const commonAjaxFailHandler = function (error) {
        return function (res) {
            error(res);
            console.error(res);
        };
    };

    $().ready(() => {
        utils.showLoading('下载缓存数据中');
        const dataTable = $('#dataTable');
        dataTableInstant = dataTable.DataTable({
            language: {
                url: '/key3/js/datatable-zh.json'
            },
            dom: "Bfrtip",
            ajax: {
                url: '/key3/kit/api/mappingCache' + location.search,
                dataSrc: function (resp) {
                    let dataToUse = [];
                    for (let key in resp.data) {
                        if (!resp.data.hasOwnProperty(key)) continue;
                        dataToUse.push({key: key, value: resp.data[key]});
                    }
                    utils.hideLoading();
                    return dataToUse;
                },
            },

            order: [0, 'desc'],
            select: 'single',
            responsive: true,
            altEditor: true, // Enable altEditor
            columns: [{
                data: 'key',
                required: true
            }, {
                data: 'value',
                required: true,
                type: 'textarea',
                render: function (value) {
                    if (value.length > 100) {
                        return value.substring(0, 99) + '...';
                    }
                    return value;
                },
                // altEditorRender: function (role) {
                //     return role === 'SYSTEM_ADMIN';
                // }
            }],
            buttons: [{
                extend: 'selected',
                text: '<i class="fas fa-pen-square pr-1"></i>编辑',
                name: 'edit',
                className: 'btn btn-info'
            }, {
                text: '<i class="fas fa-plus-square pr-1"></i>添加',
                name: 'add',
                className: 'btn btn-success'
            }, {
                extend: 'selected',
                text: '<i class="fas fa-trash-alt pr-1"></i>删除',
                name: 'delete',
                className: 'btn btn-danger'
            }],
            onAddRow: function (datatable, rowdata, success, error) {
                utils.showLoading('UPLOADING...');
                utils.ajax("kit/api/updateCache" + location.search, rowdata, 'POST', function () {
                    success(rowdata);
                }, commonAjaxFailHandler(error), utils.hideLoading);
            },
            onDeleteRow: function (datatable, rowdata, success, error) {
                utils.confirm('删除缓存？', '您的操作将可能引起系统不稳定、数据错误，如果您不明确了解自己在做什么，请不要尝试', '算了', '是的').then(ok => {
                    if (ok) return utils.alert('您取消了危险的尝试');
                    utils.showLoading('DELETING...');
                    utils.ajax("kit/api/deleteCache/" + rowdata.key + location.search, {}, 'DELETE', function (res) {
                        success(res.data);
                        utils.success("操作成功", "已删除指定的缓存");
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                });
            },
            onEditRow: function (datatable, rowdata, success, error) {
                utils.showLoading('UPLOADING...');
                utils.ajax("kit/api/updateCache" + location.search, rowdata, 'POST', function () {
                    success(rowdata);
                }, commonAjaxFailHandler(error), utils.hideLoading);
            }
        });

        $('#clearCacheBtn').on('click', () => {
            utils.confirm('清空缓存？', '您将要同时清空缓存中间件、应用缓存中保存的所有数据，如果您不清楚这意味着什么请不要尝试', '算了', '是的').then(ok => {
                if (ok) return utils.alert('您已取消了危险的尝试');
                utils.ajax('kit/api/clearCache' + location.search, {}, 'DELETE', () => {
                    // 清空缓存后需要刷新页面
                    location.reload();
                });
            });
        });
    });
</script>
</body>

</html>