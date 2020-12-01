$(document).ready(function () {

    // Datatable 实例
    let datatable = null;

    const refreshTable = function (newId) {
        if (datatable === null) {
            datatable = $('#dataTable').DataTable({
                language: {
                    url: '/key3/js/datatable-zh.json'
                },
                dom: "Bfrtip",
                ajax: {
                    url: utils.wrapUrlWithToken('/key3/admin/api/logs/' + newId),
                    dataSrc: function (resp) {
                        utils.hideLoading();
                        return resp.data;
                    },
                },
                order: [2, 'desc'],
                responsive: true,
                columns: [{
                    data: 'showHead',
                    render: function (headUrl) {
                        return '<img alt="头像而已" style="width: 3rem; height: 3rem" src="' +
                            headUrl + '"/>'
                    },
                    orderable: false,
                    searchable: false
                }, {
                    data: "who"
                }, {
                    data: "createTime",
                    render: function (ts) {
                        return new Date(ts).toLocaleDateString();
                    }
                }, {
                    data: "content"
                }, {
                    data: "ip"
                }]
            });
        } else if (newId) { // 加载了其他社团
            datatable.ajax.url(utils.wrapUrlWithToken('/key3/admin/api/logs/' + newId)).load();
        } else { // 普通的刷新
            datatable.ajax.reload();
        }
    };

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        refreshTable(club.id);
    });

});