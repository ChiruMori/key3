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
                            headUrl + '" onerror="this.src=\'https://upload.cc/i1/2020/11/30/561A98.png\'"/>'
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
                    data: 'sign'
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
                    utils.ajax("admin/api/user", rowdata, 'POST', function (res) {
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                },
                onDeleteRow: function (datatable, rowdata, success, error) {
                    let confirmText = prompt('注意，这会同时删除用户全部信息，包括社团的参加信息、活动的参加信息、所有该用户的预约；如果您不顾一切要这样做，请输入该用户的名字以进行确认', '');
                    if (confirmText !== rowdata.realName) {
                        alert('您取消了操作，用户数据仍然是安全的');
                        return;
                    }
                    utils.ajax("admin/api/user/" + rowdata.id, {}, 'DELETE', function (res) {
                        utils.showLoading('DELETING...');
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                },
                onEditRow: function (datatable, rowdata, success, error) {
                    utils.showLoading('MODIFYING...');
                    utils.ajax("admin/api/user", rowdata, 'PUT', function (res) {
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                }
            });
        } else if (newId) { // 加载了其他社团
            datatable.ajax.url(utils.wrapUrlWithToken('/key3/admin/api/users/' + newId)).load();
        } else { // 普通的刷新
            datatable.ajax.reload();
        }
    };

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        refreshTable(club.id);
    });
});