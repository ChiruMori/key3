$(document).ready(function () {

    // Datatable 实例
    let datatable = null;
    let clubAssetsDiv = $('#clubAssets');
    const hintArea = $('#hintArea');
    const commonAjaxFailHandler = function (error) {
        return function (res) {
            error(res);
            console.error(res);
            hintArea.text("操作失败：" + res.responseJSON.msg);
            hintArea.addClass('text-danger');
        };
    };

    const refreshClubAssets = function (nowAssets) {
        clubAssetsDiv.text(nowAssets);
    };

    const refreshTable = function (newId) {
        if (datatable === null) {
            datatable = $('#dataTable').DataTable({
                language: {
                    url: '/key3/js/datatable-zh.json'
                },
                dom: "Bfrtip",
                ajax: {
                    url: utils.wrapUrlWithToken('/key3/admin/api/bills/' + newId),
                    dataSrc: function (resp) {
                        refreshClubAssets(resp.data.clubAssets);
                        utils.hideLoading();
                        return resp.data.bills;
                    },
                },
                order: [1, 'asc'],
                select: 'single',
                responsive: true,
                altEditor: true, // Enable altEditor
                columns: [{
                    visible: false,
                    data: "id",
                    type: "readonly"
                }, {
                    data: "createTime",
                    render: function (ts) {
                        return new Date(ts).toLocaleDateString();
                    },
                    type: "readonly"
                }, {
                    data: "who",
                    type: "readonly"
                }, {
                    data: "info"
                }, {
                    data: "cost",
                    render: function (num) {
                        let formated = $.fn.dataTable.render.number(',', '.', 2, '￥')
                            .display(num);
                        let className = 'text-success';
                        if (num >= 0) {
                            className = 'text-danger';
                            formated = '+' + formated;
                        }
                        return '<span class="' + className + '">' + formated + '</span>';
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
                    rowdata.clubId = GLOBAL_VAL.nowClubId;
                    utils.ajax("admin/api/bills", rowdata, 'POST', function (res) {
                        refreshClubAssets(res.data.clubAssets);
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                },
                onDeleteRow: function (datatable, rowdata, success, error) {
                    utils.ajax("admin/api/bills/" + rowdata.id, {}, 'DELETE', function (res) {
                        utils.showLoading('DELETING...');
                        refreshClubAssets(res.data.clubAssets);
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                },
                onEditRow: function (datatable, rowdata, success, error) {
                    utils.showLoading('MODIFYING...');
                    rowdata.clubId = GLOBAL_VAL.nowClubId;
                    utils.ajax("admin/api/bills", rowdata, 'PUT', function (res) {
                        refreshClubAssets(res.data.clubAssets);
                        success(res.data);
                    }, commonAjaxFailHandler(error), utils.hideLoading);
                }
            });
        } else if (newId) { // 加载了其他社团
            datatable.ajax.url(utils.wrapUrlWithToken('/key3/admin/api/bills/' + newId)).load();
        } else { // 普通的刷新
            datatable.ajax.reload();
        }
    };

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        refreshTable(club.id);
    });

});