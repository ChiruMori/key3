
// 全局变量
const GLOBAL_VAL = {
    nowClubId: null,
    systemAdmin: false,
};

$().ready(function () {
    let clubNameDiv = $('#now-club-name');
    let managingClub = utils.readCache(CONST_VAL.managingClubKey);

    // 切换社团，变化显示文字、全局数据，发布事件
    const changeClub = function (targetClub) {
        clubNameDiv.text(targetClub.name);
        GLOBAL_VAL.nowClubId = targetClub.id;
        managingClub = targetClub;
        utils.publishEvent(CONST_VAL.clubChangedEventKey, targetClub);
    }

    if (!managingClub) { // 初次登录或缓存已清除
        let clubNodes = $('.club-item');
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
        if (clickClub.id != managingClub.id) {
            changeClub(clickClub);
        }
    });
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

    let newClubBtn = $('#newClubBtn');
    if (newClubBtn[0]) { // 跳转到新建社团页面，同时设置新建功能锁
        newClubBtn.click(function () {
            utils.writeCache(CONST_VAL.newClubLockKey, true);
            window.location.href = '/key3/admin/page/club-info?' + CONST_VAL.tokenQueryName + '=' + utils.readCache(CONST_VAL.tokenKey);
        });
    }
});