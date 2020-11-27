$().ready(function(){
    let aTags = $('.token-tag');
    let tokenSuffix = '?' + CONST_VAL.tokenQueryName + '=' + utils.readCache(CONST_VAL.tokenKey);
    // 为页面所有 .token-tag 的 href 属性添加 accessToken
    for (let i = 0, len = aTags.length; i < len; i++) {
        aTags[i].href = aTags[i].href + tokenSuffix;
    }
})