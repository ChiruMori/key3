$().ready(function () {

    const url = '/admin/api/room/';

    const submitBtn = $('#submitBtn');
    const roomSelectTag = $('#roomSelectTag');
    const locationDropDown = $('#locationDropDown');
    const roomIdInp = $('#id');
    const errorHint = $('#errorHint');
    const deleteBtn = $('#deleteBtn');

    let clubRoomDTOs, roomLocationDTOs;
    let selectingRoom = null;

    const autoFillDataToPage = function () {
        submitBtn.off('click');
        if (selectingRoom.id === -1) {
            // 新建活动室模式
            roomIdInp.removeAttr('readonly');
            let showingObj = utils.formToObj('roomForm');
            for (let name in showingObj) {
                $('#' + name).val('');  // 清空
            }
            submitBtn.on('click', function () {
                uploadRoomData('POST', function () {
                    let newOption = $('<option value="' + clubRoomDTOs.length + '">' + selectingRoom.name + '</option>');
                    clubRoomDTOs.push(selectingRoom);
                    newOption.insertBefore($('#lastOpt'));
                    newOption.attr('selected', true);
                    clearError();
                });
            });
            return;
        }
        // 编辑模式，ID 不允许修改
        roomIdInp.attr('readonly', 'true');
        for (let name in selectingRoom) {
            if (selectingRoom.hasOwnProperty(name)) {
                $('#' + name).val(selectingRoom[name]);  // 赋默认值
            }
        }
        submitBtn.on('click', function () {
            uploadRoomData('PUT', clearError);
        });
    };

    const clearError = function () {
        $('#roomForm input').removeClass('is-invalid');
        errorHint.text('');
    }

    const uploadRoomData = function (method, fn) {
        utils.showLoading('Uploading...');
        let dataToSend = utils.formToObj('roomForm');
        dataToSend.clubId = GLOBAL_VAL.nowClubId;
        utils.ajax(url, dataToSend, method, function (res) {
            selectingRoom = res.data;
            autoFillDataToPage();
            if (typeof fn === 'function') {
                fn();
            }
        }, function (res) {
            console.log(res);
            errorHint.text(res.responseJSON.msg);
            showErrorHint(res.responseJSON.data);
        }, utils.hideLoading);
    };

    deleteBtn.on('click', function(){
        let confirmText = prompt("注意，这会同时删除该活动室有关的预约信息（含历史数据），如果您仍要这么做，请输入该活动室名称以确认", "");
        if (confirmText !== selectingRoom.name) {
            alert('您取消了操作，数据仍然是安全的');
            return;
        }
        utils.showLoading('DELETING...');
        utils.ajax(url + GLOBAL_VAL.nowClubId + '/' + selectingRoom.id, {}, 'DELETE', function() {
            alert('完成删除，即将自动刷新页面');
            location.reload();
        }, function(res) {
            console.debug(res);
            errorHint.text(res.data.msg);
        });
    });

    // 活动室下拉选项监听
    roomSelectTag.on('change', function () {
        let index = roomSelectTag.val();
        selectingRoom = index === '-1' ?
            {id: -1} :
            clubRoomDTOs[index];
        autoFillDataToPage();
    });

    // 坐标选择下拉列表监听
    const fillLocation = function (index) {
        let nowLocation = roomLocationDTOs[index];
        $('#longitude').val(nowLocation.longitude);
        $('#latitude').val(nowLocation.latitude);
    };

    // 显示字段验证提示，并添加提示信息
    const showErrorHint = function (errData) {
        if (!errData) return;
        for (let k in errData) {
            if (!errData.hasOwnProperty(k)) continue;
            let errorInp = $('#' + k);
            errorInp.addClass('is-invalid');
            errorInp.tooltip('dispose');
            errorInp.tooltip({title: errData[k]});
        }
    }

    utils.subscribeEvent(CONST_VAL.clubChangedEventKey, function (club) {
        utils.showLoading();
        selectingRoom = null;

        let roomDataPromise = new Promise(function (resolve, reject) {
            // 获取社团全部活动室数据
            utils.ajax(url + club.id, {}, 'GET', function (res) {
                clubRoomDTOs = res.data;
                resolve(res.data);
            }, function (res) {
                console.error(res);
                reject(res.responseJSON);
                errorHint.text(res.responseJSON.msg);
            });
        });

        let roomLocationPromise = new Promise(function (resolve, reject) {
            utils.ajax(url + 'locations/' + club.id, {}, 'GET', function (res) {
                roomLocationDTOs = res.data;
                resolve(res.data);
            }, function (res) {
                console.error(res);
                reject(res.responseJSON);
                errorHint.text(res.responseJSON.msg);
            });
        });

        Promise.all([roomDataPromise, roomLocationPromise]).then(function () {
            if (selectingRoom === null) {
                selectingRoom = clubRoomDTOs.length ? clubRoomDTOs[0] : {id: -1};
            }
            // 更换活动室选项
            let optionsHTML = '';
            for (let i = 0, len = clubRoomDTOs.length; i < len; i++) {
                let dto = clubRoomDTOs[i];
                optionsHTML += ('<option value="' + i + '">' + dto.name + '</option>\n');
            }
            optionsHTML += ('<option id="lastOpt" value="-1">添加活动室</option>\n');
            roomSelectTag.html(optionsHTML);
            // 填充坐标下拉框
            locationDropDown.html('');
            for (let i = 0, len = roomLocationDTOs.length; i < len; i++) {
                let newLocationItem = $('<a class="dropdown-item" data-id="' + i + '" href="javascript:">No.' + dto.dataId + '</a>');
                newLocationItem.on('click', function () {
                    fillLocation(this.dataset.id);
                });
                locationDropDown.append(newLocationItem);
            }
            if (roomLocationDTOs.length === 0) {
                locationDropDown.html('<p class="text-mute m-0 px-2">无可用坐标</p>');
            }
            // 更新页面内容
            autoFillDataToPage();
            utils.hideLoading();
        });
    });
});