$().ready(function () {

    const getLiItemFromDTO = function (head, info, who, createTime, change) {
        let bill = change !== undefined;
        let billClass = bill && change > 0 ? 'text-danger' : 'text-success';
        change = change > 0 ? ('+' + change) : (change + '');
        return '<li class="list-group-item d-flex align-items-center">\n' +
            '    <div class="img-profile mr-3">\n' +
            '        <img class="w-100 rounded-circle"\n' +
            '             src="' + head + '"\n' +
            '             alt="头像而已">\n' +
            '        <div class="status-indicator"></div>\n' +
            '    </div>\n' +
            '    <div class="w-100' + (bill ? 'pr-rem-5' : '') + '">\n' +
            '        <div class="text-truncate">' + info + '</div>\n' +
            '        <div class="small text-gray-500">' + who + ' · ' + utils.whenIs(createTime) + '</div>\n' +
            '    </div>\n' +
            (bill ? ('<p class="bill font-weight-bold ' + billClass + '">' + change + '</p>\n') : '') +
            '</li>';
    }

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        utils.ajax('/admin/api/dashboard/' + club.id, {}, 'GET', function (res) {
            let resData = res.data;
            $('#pageTitle').text(club.name);
            $('#enrollNumber').text(resData.enrollMembers);
            $('#activeMembers').text(resData.activeMembers);
            $('#activeBar').attr('style', 'width: ' + (resData.activeMembers / resData.enrollMembers * 100) + '%');
            $('#enableAssets').text(resData.assets);
            $('#roomUsage').text(resData.usage.toFixed(2) + '%');  // 保留两位小数
            $('#usageBar').attr('style', 'width: ' + resData.usage + '%');
            let logUlContainer = $('#logArea');
            logUlContainer.html('');  // 清空原内容
            let logATag = $('#allLogATag');
            if (resData.logs.content.length !== 0) {
                resData.logs.content.forEach(function (logDTO) {
                    logUlContainer.append($(getLiItemFromDTO(logDTO.showHead, logDTO.content, logDTO.who, logDTO.createTime)));
                });
                logATag.text('全部日志');
                logATag.attr('href', utils.wrapUrlWithToken('/key3/admin/page/system-log'));
            } else {
                logATag.text('什么都没有...');
                logATag.attr('href', '#');
            }
            let billUlContainer = $('#billArea');
            billUlContainer.html('');  // 清空原内容
            let billATag = $('#allBillATag');
            if (resData.bills.content.length !== 0) {
                resData.bills.content.forEach(function (billDTO) {
                    billUlContainer.append($(getLiItemFromDTO(billDTO.showHead, billDTO.info, billDTO.who,
                        billDTO.createTime, billDTO.cost)));
                });
                billATag.text('全部收支');
                billATag.attr('href', utils.wrapUrlWithToken('/key3/admin/page/club-bill'));
            } else {
                billATag.text('什么都没有...');
                billATag.attr('href', '#');
            }
        }, function (res) {
            utils.error('加载失败', '当前社团已被删除或者网络连接错误');
            console.error(res);
        }, function () {
            utils.hideLoading();
        });
    });
});