$(document).ready(function () {

    // Datatable 实例
    let datatable = null;
    const commonAjaxFailHandler = function (error) {
        return function (res) {
            error(res);
            console.error(res);
        };
    };

    const refreshTable = function (newId) {
        if (datatable === null) {
            datatable = $('#dataTable').DataTable({
                language: {
                    url: '/key3/js/datatable-zh.json'
                },
                dom: "Bfrtip",
                ajax: {
                    url: utils.wrapUrlWithToken('/key3/admin/api/announcement/' + newId),
                    dataSrc: function (resp) {
                        utils.hideLoading();
                        return resp.data;
                    },
                },
                order: [0, 'desc'],
                select: 'single',
                responsive: true,
                altEditor: true, // Enable altEditor
                columns: [{
                    visible: false,
                    data: 'id',
                    type: 'readonly'
                }, {
                    data: 'showHead',
                    render: function (headUrl) {
                        return '<img alt="头像而已" style="width: 3rem; height: 3rem" src="' +
                            headUrl + '"/>'
                    },
                    orderable: false,
                    searchable: false,
                    type: 'readonly'
                }, {
                    data: 'who',
                    type: 'readonly'
                }, {
                    data: 'createTime',
                    render: function (ts) {
                        return new Date(ts).toLocaleDateString();
                    },
                    type: 'readonly',
                    altEditorRender: function (timestamp) {
                        return utils.whenIs(timestamp);
                    }
                }, {
                    data: 'title',
                    required: true
                }, {
                    data: 'content',
                    type: 'textarea',
                    required: true
                }],
                buttons: [{
                    text: '<i class="fas fa-plus-square pr-1"></i>添加',
                    name: 'add',
                    className: 'btn btn-success'
                }, {
                    extend: 'selected',
                    text: '<i class="fas fa-pen-square pr-1"></i>编辑',
                    name: 'edit',
                    className: 'btn btn-info'
                }, {
                    extend: 'selected',
                    text: '<i class="fas fa-trash-alt pr-1"></i>删除',
                    name: 'delete',
                    className: 'btn btn-danger'
                }],
                onAddRow: function (datatable, rowdata, success, error) {
                    if (GLOBAL_VAL.systemAdmin) {
                        utils.confirm("系统公告？", "这条公告是一个系统级公告吗？如果是，则所用用户都会看到；如果不是，则只有社团成员才能看到", "是", "不是").then(function (ok) {
                            utils.showLoading('ADDING...');
                            rowdata.clubId = ok ? -1 : newId;
                            delete(rowdata.createTime);
                            utils.ajax("admin/api/announcement", rowdata, 'POST', function (res) {
                                success(res.data);
                            }, commonAjaxFailHandler(error), utils.hideLoading);
                        });
                    }
                },
                onDeleteRow: function (datatable, rowdata, success, error) {
                    utils.confirm("删除公告？", "公告将从公告列表中移除，但因公告产生的通知无法撤回", "删除", "取消")
                        .then(function (ok) {
                            if (!ok) return;
                            utils.ajax("admin/api/announcement/" + rowdata.id, {}, 'DELETE', function (res) {
                                success(res.data);
                                utils.success("操作成功", "已删除公告");
                            }, commonAjaxFailHandler(error));
                        });
                },
                onEditRow: function (datatable, rowdata, success, error) {
                    utils.showLoading('MODIFYING...');
                    delete(rowdata.createTime);
                    delete(rowdata.publisherId);
                    delete(rowdata.clubId);
                    utils.ajax("admin/api/announcement", rowdata, 'PUT', function (res) {
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                }
            });
        } else if (newId) { // 加载了其他社团
            datatable.ajax.url(utils.wrapUrlWithToken('/key3/admin/api/announcement/' + newId)).load();
        } else { // 普通的刷新
            datatable.ajax.reload();
        }
    };

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        refreshTable(club.id);
    });

});