const COLOR_CLASS_MAP = {
    'IDLE': 'col',
    'OCCUPIED': 'col ele-pale-blue',
    'FOLLOWED': 'col ele-pale-purple',
    'MINE': 'col ele-pale-green',
    'PASSED': 'col ele-pale-yellow',
    'NOT_OPEN': 'col ele-pale-yellow',
    'DISABLED_RED': 'col ele-pale-red',
    'DISABLED_WARM': 'col ele-pale-warm',
    'DISABLED_COOL': 'col ele-pale-cool',
    'OCCUPIED_HL': 'col ele-light-blue',
    'FOLLOWED_HL': 'col ele-light-purple',
    'MINE_HL': 'col ele-light-green',
    'PASSED_HL': 'col ele-light-yellow',
    'NOT_OPEN_HL': 'col ele-light-yellow',
    'DISABLED_RED_HL': 'col ele-light-red',
    'DISABLED_WARM_HL': 'col ele-light-warm',
    'DISABLED_COOL_HL': 'col ele-light-cool',
};

$().ready(function () {

    const url = 'admin/api/';

    const blockBtn = $('#blockBtn');
    const clearBtn = $('#clearBtn');
    const editBtn = $('#editBtn');
    const roomSelectTag = $('#roomSelectTag');
    const prevBtn = $('#prevBtn');
    const nextBtn = $('#nextBtn');
    const tabBody = $('#tabBody');
    const weekNumberTag = $('#weekNumber');

    let clubRoomDTOs;
    let selectingRoom = null;
    let nowWeek = 0;
    let idSet = new Set();
    let classMap = {};

    // 翻页监听
    prevBtn.on('click', function () {
        nowWeek--;
        refreshTable();
    });

    nextBtn.on('click', function () {
        nowWeek++;
        refreshTable();
    });

    // 活动室下拉选项监听
    roomSelectTag.on('change', function () {
        let index = roomSelectTag.val();
        selectingRoom = index === '-1' ?
            {id: -1} :
            clubRoomDTOs[index];
        refreshTable();
    });

    const submitTable = function (api, color, text) {
        if (idSet.size === 0) {
            utils.warning('没有数据', '请至少选择一项');
            return;
        }
        let ids = [];
        idSet.forEach(function (val) {
            ids.push(val);
        });
        let dataToSend = {
            ids: ids,
            week: nowWeek,
            colorState: color,
            showText: text
        };
        utils.ajax(url + api, dataToSend, 'PUT', function (res) {
            displayBy(res.data);
        }, function (res) {
            utils.error('ERROR', res.responseJSON.msg);
        });
    }

    // 禁用按钮
    blockBtn.on('click', function () {
        utils.select('请选择颜色', '选择指定的颜色，在占用时段后，显示该颜色，点击按钮确认您的选择',
            [{text: '红色', value: 'DISABLED_RED'},
                {text: '暖渐变色', value: 'DISABLED_WARM'},
                {text: '冷渐变色', value: 'DISABLED_COOL'}], 'form-control', '取消操作')
            .then(function (selectedValue) {
                if (!COLOR_CLASS_MAP[selectedValue]) {
                    utils.alert("操作已取消");
                    return;
                }
                submitTable('block', selectedValue, null);
            });
    });

    // 清除按钮
    clearBtn.on('click', function () {
        utils.confirm('确认清除？', '您将要清除' + idSet.size + '个时段', '提交', '取消')
            .then(function (ok) {
                if (!ok) return;
                submitTable('clear', null, null);
            });
    });

    // 编辑按钮
    editBtn.on('click', function () {
        utils.prompt('编辑时段', '请输入要显示的文本', 'text', '提示文本')
            .then(function (inputVal) {
                if (!inputVal) return;
                submitTable('text', null, inputVal);
            });
    });

    const displayBy = function (timeTableVO) {
        idSet.clear();
        classMap = {};
        weekNumberTag.text(timeTableVO.week);  // 显示周次
        // 显示表格
        let title = timeTableVO.timeTitle;
        let tabToShow = timeTableVO.timeTable;
        tabBody.html('');
        // 生成各行
        for (let i = 0, titleLen = title.length; i < titleLen; i++) {
            let tabRow = $('<div class="row"></div>'); // 行容器
            let rowTitle = $('<div class="col">' + title[i] + '</div>'); // 行标题
            tabRow.append(rowTitle);
            let rowData = tabToShow[i];
            // 行内其他时段
            for (let j = 0; j < rowData.length; j++) {
                let timeDTO = rowData[j];
                classMap[timeDTO.id] = COLOR_CLASS_MAP[timeDTO.state];  // 保存初始类名
                let rowEle = $('<div id="' + timeDTO.id + '" ' +
                    'class="' + COLOR_CLASS_MAP[timeDTO.state] + '" ' +
                    'data-state="' + timeDTO.state + '">' + timeDTO.showText + '</div>');
                // 时段被点击时
                rowEle.on('click', function (e) {
                    let timeId = e.target.id;
                    if (idSet.has(timeId)) {  // 列表中存在此时段，删除
                        idSet.delete(timeId);
                        e.target.className = classMap[timeId];
                    } else {  // 新的时段，添加到集合
                        idSet.add(timeId);
                        e.target.className = COLOR_CLASS_MAP.MINE_HL;
                    }
                });
                tabRow.append(rowEle);
            }
            tabBody.append(tabRow);
        }
    }

    /**
     * 刷新表格
     */
    const refreshTable = function () {
        if (selectingRoom === null || selectingRoom.id === -1) {
            blockBtn.attr('disabled', true);
            clearBtn.attr('disabled', true);
            editBtn.attr('disabled', true);
            tabBody.html('<p class="m-3 text-muted">没有数据</p>');
            return;
        }
        utils.showLoading();
        blockBtn.removeAttr('disabled');
        clearBtn.removeAttr('disabled');
        editBtn.removeAttr('disabled');
        utils.ajax(url + 'table/' + selectingRoom.id + '/' + nowWeek, {}, 'GET', function (res) {
            displayBy(res.data);
        }, function (res) {
            utils.error('ERROR', res.responseJSON.msg);
        }, utils.hideLoading);
    };

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        selectingRoom = null;
        nowWeek = 0;

        // 获取社团全部活动室数据
        utils.ajax(url + 'room/' + club.id, {}, 'GET', function (res) {
            clubRoomDTOs = res.data;

            if (selectingRoom === null) {
                selectingRoom = clubRoomDTOs.length ? clubRoomDTOs[0] : {id: -1};
            }
            // 更换活动室选项
            let optionsHTML = '';
            for (let i = 0, len = clubRoomDTOs.length; i < len; i++) {
                let dto = clubRoomDTOs[i];
                optionsHTML += ('<option value="' + i + '">' + dto.name + '</option>\n');
            }
            if (clubRoomDTOs.length === 0) {
                optionsHTML += ('<option id="lastOpt" value="-1">没有活动室</option>\n');
            }
            roomSelectTag.html(optionsHTML);
            utils.hideLoading();
            refreshTable();
        }, function (res) {
            console.error(res);
            utils.error("ERROR", res.msg);
            utils.hideLoading();
        });
    });
});