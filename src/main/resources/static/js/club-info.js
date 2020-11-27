$().ready(function () {

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        let url = '/admin/api/club';
        let method;
        let hintArea = $('#hintText');
        let submitBtn = $('#submitBtn');
        if (utils.readCache(CONST_VAL.newClubLockKey)) { // 新建社团模式
            utils.removeCache(CONST_VAL.newClubLockKey);  // 清除锁
            hintArea.text("您当前处于新建社团模式，点击创建按钮后可新建社团，如果您想退出新建社团模式，只需刷新页面即可。");
            submitBtn.text("新建社团");
            $('#clubId').val('');
            method = 'POST';
            utils.hideLoading();
        } else {
            $('#clubId').val(club.id);
            // 获取社团完整数据
            utils.ajax('/admin/api/club/' + club.id, {}, 'GET', function (res) {
                let clubDTO = res.data;
                document.getElementById('enableAssets').checked = clubDTO.billEnabled;
                $('#clubName').val(clubDTO.name);
                $('#clubAssets').val(clubDTO.assets);
                $('#clubId').val(clubDTO.id);
                $('#removeClubBtn').remove();
                let removeClubBtn = $('<a class="btn btn-danger" id="removeClubBtn">删除社团</a>');
                removeClubBtn.click(function () {
                    let confirmText = prompt('您将要删除该社团，以及相关的财务信息、活动室归属关系、成员归属关系、公告信息。注意，该操作不可逆，如果仍要删除，请输入该社团的名称以确认操作', '');
                    if (confirmText === club.name) {
                        utils.showLoading('DELETING...');
                        utils.ajax('/admin/api/club/' + club.id, {}, 'DELETE', function () {
                            alert('已删除');
                            location.reload();
                        }, function (res) {
                            console.error(res);
                            hintArea.text("删除社团失败：" + res.responseJSON.msg);
                            hintArea.addClass('text-danger');
                        }, utils.hideLoading);
                    }
                });
                $('#submitBtn').parent().append(removeClubBtn);
            }, function (res) {
                console.error(res);
                hintArea.text("信息请求失败，错误原因：" + res.responseJSON.msg);
                hintArea.addClass('text-danger');
            }, utils.hideLoading);
            method = 'PUT';
        }
        submitBtn.click(function () {
            utils.showLoading();
            let fromObj = utils.formToObj('club-info-form');
            fromObj.billEnabled = (Boolean)(fromObj.billEnabled);
            utils.ajax(url, fromObj, method, function () {
                alert('操作成功');
                location.reload();
            }, function (res) {
                hintArea.text('操作失败，' + res.responseJSON.msg);
                hintArea.addClass('text-danger');
            }, utils.hideLoading);
        });
    });
})