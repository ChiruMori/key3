$(document).ready(function () {

    const runtimeBtn = $('#runtimeBtn');
    const smtpBtn = $('#smtpBtn');
    const primaryBtn = $('#primaryBtn');
    const smtpTestBtn = $('#smtpTestBtn');

    const formatDate = function (timestamp) {
        let d = new Date(timestamp);
        let res = '';
        res += d.getFullYear();
        res += '-'
        let month = d.getMonth() + 1;
        if (month < 10) {
            res += ('0' + month);
        }
        let day = d.getDate();
        res += '-'
        if (day < 10) {
            res += ('0' + day);
        }
        return res;
    }

    const url = 'admin/api/options';
    let localOptionMap;

    const uploadFormData = function (formData) {
        utils.showLoading();
        if (formData['week_start_stamp']) {
            formData['week_start_stamp'] = new Date(formData['week_start_stamp']).getTime();
        }
        utils.ajax(url + '/map_view', formData, 'PUT', function () {
            for (let k in formData) {
                if (formData.hasOwnProperty(k)) {
                    localOptionMap[k] = formData[k];
                }
            }
            refreshForms();
        }, function (res) {
            utils.error('ERROR', res.responseJSON.msg);
            console.debug(res);
        }, utils.hideLoading);
    };

    runtimeBtn.on('click', function () {
        let updateData = utils.formToObj('runtimeForm');
        uploadFormData(updateData);
    });

    smtpBtn.on('click', function () {
        let updateData = utils.formToObj('smtpForm');
        uploadFormData(updateData);
    });

    primaryBtn.on('click', function () {
        let updateData = utils.formToObj('primaryForm');
        uploadFormData(updateData);
    });

    smtpTestBtn.on('click', function () {
        utils.prompt('测试邮件配置', '注意，请在提交后进行测试。请输入要接受测试邮件的邮箱地址', 'email', '如：eg@eg.com').then(function (addr) {
            if (!addr) {
                utils.alert('您取消了操作');
                return;
            }
            utils.ajax(url + '/mail_test?addr=' + addr, {}, 'POST', function (res) {
                utils.success('SUCCESS', res.msg);
            }, function (res) {
                console.debug(res);
                utils.error('ERROR', res.responseJSON.msg);
            });
        });
    });

    // 将内存中的项显示到表格中
    const refreshForms = function () {
        for (let k in localOptionMap) {
            if (localOptionMap.hasOwnProperty(k)) {
                if (k === 'week_start_stamp') {
                    $('#' + k).val(formatDate(localOptionMap[k]));
                } else {
                    $('#' + k).val(localOptionMap[k]);
                }
            }
        }
    };

    utils.showLoading();
    utils.ajax(url + '/map_view', {}, 'GET', function (res) {
        localOptionMap = res.data;
        refreshForms();
    }, function (res) {
        utils.error('ERROR', res.responseJSON.msg);
        console.debug(res);
    }, utils.hideLoading);
});