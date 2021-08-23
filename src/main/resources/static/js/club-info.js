$().ready(function () {
    const submitBtn = $('#submitBtn');
    const hintArea = $('#hintText');
    let skipOnce = false;

    const newClubMode = function () {
        let res = utils.readCache(CONST_VAL.newClubLockKey);
        if (res) {
            utils.removeCache(CONST_VAL.newClubLockKey); // 清除锁
            hintArea.text("您当前处于新建社团模式，点击创建按钮后可新建社团，如果您想退出新建社团模式，只需刷新页面即可。");
            submitBtn.text("新建社团");
            $('#clubId').val('');
            bindBtn('POST');
        }
        return res;
    }

    const bindBtn = function (method) {
        let url = 'admin/api/club';
        submitBtn.off('click');
        submitBtn.on('click', function () {
            utils.showLoading();
            let fromObj = utils.formToObj('club-info-form');
            fromObj.billEnabled = (Boolean)(fromObj.billEnabled);
            utils.ajax(url, fromObj, method, function () {
                utils.success("操作成功", "数据上传成功，即将刷新页面").then(function () {
                    location.reload();
                });
            }, function (res) {
                utils.error("ERROR", res.responseJSON.msg);
                hintArea.text('操作失败，' + res.responseJSON.msg);
                hintArea.addClass('text-danger');
            }, utils.hideLoading);
        });
    }

    if (newClubMode()) {
        skipOnce = true;
    }

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        if (skipOnce || newClubMode()) { // 新建社团模式
            skipOnce = false;  // BUG：点击新建社团后，加载动画不会自动关闭
            utils.hideLoading();
        } else {
            $('#clubId').val(club.id);
            // 获取社团完整数据
            utils.ajax('/admin/api/club/' + club.id, {}, 'GET', function (res) {
                let clubDTO = res.data;
                document.getElementById('enableAssets').checked = clubDTO.billEnabled;
                $('#clubName').val(clubDTO.name);
                $('#absentLimit').val(clubDTO.absentLimit);
                $('#clubAssets').val(clubDTO.assets);
                $('#clubId').val(clubDTO.id);
                $('#removeClubBtn').remove();
                let removeClubBtn = $('<a class="btn btn-danger" id="removeClubBtn">删除社团</a>');
                removeClubBtn.on('click', function () {
                    utils.prompt("删除社团？",
                        "您将要删除该社团，以及相关的财务信息、活动室归属关系、成员归属关系、公告信息。注意，该操作不可逆，如果仍要删除，请输入该社团的名称以确认操作",
                        "text", club.name).then(function (inputValue) {
                        if (inputValue === club.name) {
                            utils.ajax('/admin/api/club/' + club.id, {}, 'DELETE', function () {
                                swal("操作成功", "已删除社团" + inputValue + "，即将刷新页面", "success", {
                                    button: "OK!"
                                }).then(location.reload);
                            }, function (res) {
                                console.error(res);
                                utils.error('ERROR', res.responseJSON.msg);
                                hintArea.text("删除社团失败：" + res.responseJSON.msg);
                                hintArea.addClass('text-danger');
                            });
                        } else {
                            utils.alert("您取消了操作");
                        }
                    });
                });
                $('#submitBtn').parent().append(removeClubBtn);
            }, function (res) {
                console.error(res);
                utils.error("ERROR", res.responseJSON.msg);
                hintArea.text("信息请求失败，错误原因：" + res.responseJSON.msg);
                hintArea.addClass('text-danger');
            }, utils.hideLoading);
            bindBtn('PUT');
        }
    });
})