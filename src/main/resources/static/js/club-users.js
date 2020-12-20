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
                    url: utils.wrapUrlWithToken('/key3/admin/api/user/all/' + newId),
                    dataSrc: function (resp) {
                        utils.hideLoading();
                        return resp.data;
                    },
                },

                order: [4, 'desc'],
                select: 'single',
                responsive: true,
                altEditor: true, // Enable altEditor
                columns: [{
                    visible: false,
                    data: 'id',
                    type: 'readonly'
                }, {
                    data: 'head',
                    render: function (headUrl) {
                        return '<img alt="头像而已" style="width: 3rem; height: 3rem" src="' +
                            headUrl + '" onerror="this.src=\'https://cxlm.work/upload/2020/12/error-df59117371c948238cd6d68dba76449a.png\'"/>'
                    },
                    orderable: false,
                    searchable: false
                }, {
                    data: 'realName',
                    required: true
                }, {
                    data: 'wxName',
                    defaultContent: '-',
                    type: 'readonly'
                }, {
                    data: 'studentNo',
                    type: "number",
                    required: true
                }, {
                    data: 'institute',
                    defaultContent: '-',
                }, {
                    data: 'major',
                    defaultContent: '-',
                }, {
                    data: 'email',
                    defaultContent: '-',
                }, {
                    data: 'sign',
                    type: 'textarea'
                }, {
                    data: 'role',
                    type: GLOBAL_VAL.systemAdmin ? 'checkbox' : 'readonly',
                    render: function (role) {
                        return role === 'SYSTEM_ADMIN' ? '是' : '否';
                    },
                    altEditorRender: function (role) {
                        return role === 'SYSTEM_ADMIN';
                    }
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
                    utils.showLoading('ADDING...');
                    let systemAdmin = rowdata.role;
                    delete (rowdata.role);
                    rowdata.systemAdmin = systemAdmin;
                    utils.ajax("admin/api/user", rowdata, 'POST', function (res) {
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                },
                onDeleteRow: function (datatable, rowdata, success, error) {
                    utils.prompt("删除用户？",
                        "注意，这会同时删除用户全部信息，包括社团的参加信息、活动的参加信息、所有该用户的预约；如果您不顾一切要这样做，请输入该用户的名字以进行确认",
                        "input", rowdata.realName).then(function (inputValue) {
                        if (inputValue === rowdata.realName) {
                            utils.ajax("admin/api/user/" + rowdata.id, {}, 'DELETE', function (res) {
                                success(res.data);
                                utils.success("操作成功", "已删除指定的用户");
                            }, commonAjaxFailHandler(error));
                        }
                    });
                },
                onEditRow: function (datatable, rowdata, success, error) {
                    utils.showLoading('MODIFYING...');
                    let systemAdmin = rowdata.role;
                    delete (rowdata.role);
                    rowdata.systemAdmin = systemAdmin;
                    utils.ajax("admin/api/user", rowdata, 'PUT', function (res) {
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                }
            });
        } else if (newId) { // 加载了其他社团
            datatable.ajax.url(utils.wrapUrlWithToken('/key3/admin/api/user/all/' + newId)).load();
        } else { // 普通的刷新
            datatable.ajax.reload();
        }
    };

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        refreshTable(club.id);
    });
});