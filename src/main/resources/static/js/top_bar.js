// 全局变量
const GLOBAL_VAL = {
    nowClubId: null,
    systemAdmin: false,
};

$().ready(function () {
    const clubNameDiv = $('#now-club-name');
    const newClubBtn = $('#newClubBtn');
    const refreshStorage = $('#refreshStorage');

    let managingClub = utils.readCache(CONST_VAL.managingClubKey);

    // 点击退出登录按钮
    $('#logoutBtn').click(function () {
        utils.ajax('admin/api/logout', {}, 'GET', function () {
            utils.removeCache(CONST_VAL.tokenTime);
            utils.removeCache(CONST_VAL.tokenKey);
            utils.removeCache(CONST_VAL.refreshKey);
            window.location.href = '/key3/admin/page/login';
        }, function (res) {
            console.error(res);
            alert('登出失败，请重试');
        });
    });

    refreshStorage.on('click', function () {
        utils.confirm('清除选项', '当前社团被删除、修改后，可以执行本操作清除选项缓存', '清除', '算了').then(function (ok) {
            if (ok) {
                utils.removeCache(CONST_VAL.managingClubKey);
                location.reload();
            }
        });
    });

    if (newClubBtn[0]) { // 跳转到新建社团页面，同时设置新建功能锁
        newClubBtn.click(function () {
            utils.writeCache(CONST_VAL.newClubLockKey, true);
            window.location.href = '/key3/admin/page/club-info?' + CONST_VAL.tokenQueryName + '=' + utils.readCache(CONST_VAL.tokenKey);
        });
    }

    // 切换社团，变化显示文字、全局数据，发布事件
    const changeClub = function (targetClub) {
        clubNameDiv.text(targetClub.name);
        GLOBAL_VAL.nowClubId = targetClub.id;
        managingClub = targetClub;
        utils.writeCache(CONST_VAL.managingClubKey, targetClub);
        utils.publishEvent(CONST_VAL.clubChangedEventKey, targetClub);
    }

    if (!managingClub) { // 初次登录或缓存已清除
        let clubNodes = $('.club-item');
        if (!clubNodes[0]) {
            utils.alert("社团无效，如果您正在初始化系统，请新建一个社团。如果不是，请联系系统管理员报告问题");
            return;
        }
        managingClub = {
            id: clubNodes[0].getAttribute('itemId'),
            name: clubNodes[0].textContent
        };
        // 写入缓存
        utils.writeCache(CONST_VAL.managingClubKey, managingClub);
    }
    // 替换为当前社团
    changeClub(managingClub);
    // 监听社团下拉列表点击事件
    $('a.dropdown-item.d-flex').click(function () {
        let clubDiv = $(this).find('.club-item');
        let clickClub = {
            id: clubDiv.attr('itemId'),
            name: clubDiv.text()
        };
        // 忽略类型，ID 相同说明为同一社团
        if (clickClub.id !== managingClub.id) {
            changeClub(clickClub);
        }
    });
});