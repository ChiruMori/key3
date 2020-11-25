(function ($) {
    "use strict"; // Start of use strict

    // Toggle the side navigation
    $("#sidebarToggle, #sidebarToggleTop").on('click', function (e) {
        $("body").toggleClass("sidebar-toggled");
        $(".sidebar").toggleClass("toggled");
        if ($(".sidebar").hasClass("toggled")) {
            $('.sidebar .collapse').collapse('hide');
        }
        ;
    });

    // Close any open menu accordions when window is resized below 768px
    $(window).resize(function () {
        if ($(window).width() < 768) {
            $('.sidebar .collapse').collapse('hide');
        }
        ;

        // Toggle the side navigation when window is resized below 480px
        if ($(window).width() < 480 && !$(".sidebar").hasClass("toggled")) {
            $("body").addClass("sidebar-toggled");
            $(".sidebar").addClass("toggled");
            $('.sidebar .collapse').collapse('hide');
        }
        ;
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

    const server = "http://127.0.0.1:8520/key3/";

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
     */
    const ajax = function (url, data, method, successFn, failFn) {
        if (!method) method = 'GET';
        if (!successFn) successFn = function () {
        };
        if (!failFn) failFn = function () {
        };
        if (method === 'GET' || method === 'DELETE') {
            url += parseParam(data);
            data = '';
        } else {
            data = JSON.stringify(data);
        }
        $.ajax({
            url: server + url,
            type: method,
            data: data,
            dataType: 'json',
            contentType: 'application/json;charset=utf-8',
            success: function (res) {
                successFn(res);
            },
            error: function(res) {
                failFn(res);
            }
        })
    }

    /**
     * 从 localStorage 中读取数据
     */
    const readCache = function (key) {
        return JSON.parse(localStorage.getItem(key));
    }

    /**
     * 向 localStorage 中写入数据
     */
    const writeCache = function(key, val) {
        localStorage.setItem(key, JSON.stringify(val));
    }

    return {
        formToObj: formToObj,
        ajax: ajax,
        readCache: readCache,
        writeCache: writeCache,
    }
})();

const CONST_VAL = {
    tokenKey: 'accessToken',
    refreshKey: 'refreshToken',
    tokenTime: 'accessEndTime',
    tokenQueryName: 'admin_token_Authorization',
    tokenHeaderName: 'ADMIN-Authorization'
};
