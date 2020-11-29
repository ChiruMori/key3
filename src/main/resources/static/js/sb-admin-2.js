// 全局静态常量
const CONST_VAL = {
    tokenKey: 'accessToken',
    refreshKey: 'refreshToken',
    tokenTime: 'accessEndTime',
    tokenQueryName: 'admin_token_Authorization',
    tokenHeaderName: 'ADMIN-Authorization',
    managingClubKey: 'managing_club',
    clubChangedEventKey: 'club_changed',
    newClubLockKey: 'new_club_lock'
};

// 全局变量
const GLOBAL_VAL = {
    nowClubId: null,
};

(function ($) {
    "use strict"; // Start of use strict

    // Toggle the side navigation
    $("#sidebarToggle, #sidebarToggleTop").on('click', function (e) {
        $("body").toggleClass("sidebar-toggled");
        $(".sidebar").toggleClass("toggled");
        if ($(".sidebar").hasClass("toggled")) {
            $('.sidebar .collapse').collapse('hide');
        }
    });

    // Close any open menu accordions when window is resized below 768px
    $(window).resize(function () {
        if ($(window).width() < 768) {
            $('.sidebar .collapse').collapse('hide');
        }

        // Toggle the side navigation when window is resized below 480px
        if ($(window).width() < 480 && !$(".sidebar").hasClass("toggled")) {
            $("body").addClass("sidebar-toggled");
            $(".sidebar").addClass("toggled");
            $('.sidebar .collapse').collapse('hide');
        }
    });

    // Prevent the content wrapper from scrolling when the fixed side navigation hovered over
    $('body.fixed-nav .sidebar').on('mousewheel DOMMouseScroll wheel', function (e) {
        if ($(window).width() > 768) {
            var e0 = e.originalEvent,
                delta = e0.wheelDelta || -e0.detail;
            this.scrollTop += (delta < 0 ? 1 : -1) * 30;
            e.preventDefault();
        }
    });

    // Scroll to top button appear
    $(document).on('scroll', function () {
        var scrollDistance = $(this).scrollTop();
        if (scrollDistance > 100) {
            $('.scroll-to-top').fadeIn();
        } else {
            $('.scroll-to-top').fadeOut();
        }
    });

    // Smooth scrolling using jQuery easing
    $(document).on('click', 'a.scroll-to-top', function (e) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: ($($anchor.attr('href')).offset().top)
        }, 1000, 'easeInOutExpo');
        e.preventDefault();
    });

})(jQuery); // End of use strict

/**
 * 全局工具包
 */
let utils = (function () {
    /**
     * 使用 jquery 将 form 转化为可传递的 json
     * @param formId form 的 ID
     * @return 转化为 JSON 字符串的 form 表单
     */
    const formToObj = function (formId) {
        let formObj = {};
        let arr = $('#' + formId).serializeArray();
        $.each(arr, function () {
            if (formObj[this.name]) {
                formObj[this.name] = [formObj[this.name], this.value];
                return;
            }
            formObj[this.name] = this.value;
        });
        return formObj;
    }

    const server = "/key3/";

    /**
     * 使用 JQuery 通过递归将对象转化为 URL Param
     * @param obj 参数对象
     * @param key 递归过程中需要，可不传
     * @return {string} url param 字符串
     */
    const parseParam = function (obj, key) {
        let paramStr = "";
        if (obj instanceof String || obj instanceof Number || obj instanceof Boolean) {
            paramStr += "&" + key + "=" + encodeURIComponent(obj);
        } else {
            $.each(obj, function (i) {
                let k = key == null ? i : key + (obj instanceof Array ? "[" + i + "]" : "." + i);
                paramStr += '&' + parseParam(this, k);
            });
        }
        return paramStr.substr(1);
    }

    /**
     * 发送 Ajax 请求
     * @param url 请求的 URL 后缀
     * @param data 要发送的数据
     * @param method 请求方式
     * @param successFn 成功时的回调函数
     * @param failFn 失败时的回调函数
     * @param complete 无论是否成功都会执行的回调函数，可选
     */
    const ajax = function (url, data, method, successFn, failFn, complete) {
        if (!complete) complete = function () {
        };
        if (method === 'GET' || method === 'DELETE') {
            let connector = /\?.+=/.test(url) ? '&' : '?'; // 判断 URL 中是否已经带有参数
            url = url + connector + parseParam(data);
            data = '';
        } else {
            data = JSON.stringify(data);
        }
        let tokenHeaders = {};
        let cachedAccessToken = readCache(CONST_VAL.tokenKey);
        if (cachedAccessToken) { // 在请求头中设置 token
            tokenHeaders[CONST_VAL.tokenHeaderName] = cachedAccessToken;
        }
        $.ajax({
            url: server + url,
            type: method,
            data: data,
            dataType: 'json',
            headers: tokenHeaders,
            contentType: 'application/json;charset=utf-8',
            success: function (res) {
                successFn(res);
            },
            error: function (res) {
                failFn(res);
            },
            complete: function (res) {
                complete(res);
            }
        })
    };

    /**
     * 从 localStorage 中读取数据
     */
    const readCache = function (key) {
        return JSON.parse(localStorage.getItem(key));
    };

    /**
     * 清除 localStorage 中指定的缓存
     */
    const removeCache = function (key) {
        localStorage.removeItem(key);
    };

    /**
     * 向 localStorage 中写入数据
     */
    const writeCache = function (key, val) {
        localStorage.setItem(key, JSON.stringify(val));
    };

    /**
     * 简易的事件分发中心，只积压一条事件
     */
    const simpleEventCenter = {};
    const listeners = {};

    /**
     * 发布事件，如果有监听函数则直接处理，否则进行积压，不进行多事件积压，只有一个监听函数
     * @param type 事件类型
     * @param event 事件
     */
    const publishEvent = function (type, event) {
        if (typeof listeners[type] === 'function') {
            listeners[type](event);
            return;
        }
        simpleEventCenter[type] = event;
    }

    /**
     * 订阅消息
     * @param type 消息类型
     * @param fn 监听函数，传入非函数类型以取消订阅
     */
    const subscribeEvent = function (type, fn) {
        if (typeof fn !== 'function') {
            delete listeners[type];
        }
        listeners[type] = fn;
        if (simpleEventCenter[type]) {
            fn(simpleEventCenter[type]);
            delete simpleEventCenter[type];
        }
    }

    let loadingShowing = false;

    return {
        formToObj: formToObj,
        ajax: ajax,
        readCache: readCache,
        writeCache: writeCache,
        removeCache: removeCache,
        publishEvent: publishEvent,
        subscribeEvent: subscribeEvent,
        showLoading: function (text) {
            if (loadingShowing) {
                throw '加载层已在显示中，不能重复打开';
            }
            loadingShowing = true;
            if (!text) {
                text = 'LOADING...'
            }
            $('#loadingLayer').fadeIn();
            $('#loadingText').text(text);
        },
        hideLoading: function () {
            if (!loadingShowing) {
                throw '加载层尚未显示，无法关闭';
            }
            loadingShowing = false;
            $('#loadingLayer').fadeOut();
        },
        whenIs: function (targetDate) {
            if (targetDate.constructor === Date) {
                targetDate = targetDate.getTime();
            }
            let timeDiff = (new Date().getTime() - targetDate) / 1000;
            if (timeDiff > 31104000) {
                return ~~(timeDiff / 31104000) + '年前';
            }
            if (timeDiff > 2592000) {
                return ~~(timeDiff / 2592000) + '个月前';
            }
            if (timeDiff > 86400 * 2) {
                return ~~(timeDiff / 86400) + '天前';
            }
            if (timeDiff > 86400) {
                return '昨天';
            }
            if (timeDiff > 3600) {
                return ~~(timeDiff / 3600) + '小时前';
            }
            if (timeDiff > 60) {
                return ~~(timeDiff / 60) + '分钟前';
            }
            return '刚刚';
        },
        /**
         * 将 URL 包装为带有 token 的 URL
         */
        wrapUrlWithToken: function (url) {
            let connector = /\?.+=/.test(url) ? '&' : '?'; // 判断 URL 中是否已经带有参数
            return url + connector + CONST_VAL.tokenQueryName + '=' + readCache(CONST_VAL.tokenKey);
        }
    }
})();
