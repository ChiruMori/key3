<#-- 返回顶部的按钮 -->
<a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
</a>

<#-- 加载层，需要使用 JQuery 的 fadeIn fadeOut 控制，默认为隐藏状态 -->
<div class="position-absolute w-100 h-100 loading-layer" id="loadingLayer" style="display: none;">
    <div class="spinner-grow text-danger m-3" role="status">
        <span class="sr-only">Loading...</span>
    </div>
    <div class="spinner-grow text-primary m-3" role="status">
        <span class="sr-only">Loading...</span>
    </div>
    <div class="spinner-grow text-success m-3" role="status">
        <span class="sr-only">Loading...</span>
    </div>
    <div class="text-light font-weight-bold h1 mt-3" id="loadingText">???</div>
</div>

<script src="/key3/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="/key3/vendor/jquery-easing/jquery.easing.min.js"></script>
<script src="/key3/vendor/sweetalert/sweetalert.min.js"></script>

<script src="/key3/js/sb-admin-2.min.js"></script>