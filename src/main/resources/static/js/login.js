$().ready(function () {
    // 存储登录凭证到 localStorage 并跳转到仪表盘页面
    const refreshTokenByAuth = function (authToken) {
        utils.writeCache(CONST_VAL.tokenTime, new Date().getTime() + (Number)(authToken.expired_in) * 1000)
        utils.writeCache(CONST_VAL.tokenKey, authToken.access_token);
        utils.writeCache(CONST_VAL.refreshKey, authToken.refresh_token);
        window.location.href = "/key3/admin/page/dashboard?admin_token_Authorization=" + authToken.access_token;
    }

    // 读取本地缓存的登录状态
    let now = new Date().getTime();
    let expire = utils.readCache(CONST_VAL.tokenTime);
    if (expire && now < expire) { // 存在 token 且未过期
        // 刷新一下
        let url = '/admin/api/refresh/' + utils.readCache(CONST_VAL.refreshKey) +
            '?' + CONST_VAL.tokenQueryName + '=' + utils.readCache(CONST_VAL.tokenKey);
        utils.ajax(url, null, 'POST', function(res){
            refreshTokenByAuth(res.data);
        }, function() {
            console.error("Token 已过期，自动登录失败，需要手动登录");
        });
    }
    // 登录
    let loginBtn = $('#loginBtn');
    let errDiv = $('#errMsgDiv');
    let formData;
    loginBtn.click(function () {
        formData = utils.formToObj("loginForm");
        utils.ajax('admin/api/login', formData, "POST", function (res) {
            refreshTokenByAuth(res.data);
        }, function (res) {
            let resObj = res.responseJSON;
            errDiv.text(resObj.msg);
            for (let inpName in resObj.data) {
                // 显示错误提示
                if (resObj.data.hasOwnProperty(inpName)) {
                    $('#' + inpName + 'Hint').text(resObj.data[inpName]);
                    delete formData[inpName];
                }
            }
            // 清除错误提示
            for (let inpName in formData) {
                if (formData.hasOwnProperty(inpName)) {
                    $('#' + inpName + 'Hint').text('');
                }
            }
        });
    })
})