$().ready(function () {

    const getLiItemFromDTO = function (head, info, who, createTime) {
        return '<li class="list-group-item d-flex align-items-center">\n' +
            '    <div class="img-profile mr-3">\n' +
            '        <img class="w-100 rounded-circle"\n' +
            '             src="' + head + '"\n' +
            '             alt="头像而已">\n' +
            '        <div class="status-indicator"></div>\n' +
            '    </div>\n' +
            '    <div class="w-100">\n' +
            '        <div class="text-truncate">' + info + '</div>\n' +
            '        <div class="small text-gray-500">' + who + ' · ' + utils.whenIs(createTime) + '</div>\n' +
            '    </div>\n' +
            '</li>';
    }

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        utils.ajax('/admin/api/dashboard/' + club.id, {}, 'GET', function (res) {
            let resData = res.data;
            console.log(resData);
            $('#pageTitle').text(club.name);
            $('#enrollNumber').text(resData.enrollMembers);
            $('#activeMembers').text(resData.activeMembers);
            $('#activeBar').attr('style', 'width: ' + (resData.activeMembers / resData.enrollMembers * 100) + '%');
            $('#enableAssets').text(resData.assets);
            $('#roomUsage').text(resData.usage + '%');
            $('#usageBar').attr('style', 'width: ' + resData.usage + '%');
            let logUlContainer = $('#logArea');
            let logATag = $('#allLogATag');
            if (resData.logs.content.length !== 0) {
                resData.logs.content.forEach(function (logDTO) {
                    logUlContainer.append($(getLiItemFromDTO(logDTO.showHead, logDTO.content, logDTO.who, logDTO.createTime)));
                });
                logATag.text('全部日志');
                logATag.attr('href', ''); // TODO 跳转到日志页面
            } else {
                logATag.text('什么都没有...');
                logATag.attr('href', '#');
            }
            let billUlContainer = $('#billArea');
            let billATag = $('#allBillATag');
            if (resData.bills.content.length !== 0) {
                resData.bills.content.forEach(function (billDTO) {
                    billUlContainer.append($(getLiItemFromDTO(billDTO.showHead, billDTO.info, billDTO.who, billDTO.createTime)));
                });
                logATag.text('全部收支');
                logATag.attr('href', ''); // TODO 跳转到收支页面
            } else {
                billATag.text('什么都没有...');
                billATag.attr('href', '#');
            }
        }, function (res) {
            alert('数据加载失败，请尝试刷新页面或重新登录');
            console.error(res);
        }, function () {
            utils.hideLoading();
        });
    });
});