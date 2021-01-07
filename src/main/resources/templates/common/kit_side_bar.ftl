<!-- 侧边栏（导航栏） -->
<ul class="navbar-nav bg-gradient-danger sidebar sidebar-dark accordion" id="accordionSidebar">

    <!-- 侧边栏：顶部图标 -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center" href="/key3/admin/page/login">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fas fa-compact-disc fa-spin"></i>
        </div>
        <div class="sidebar-brand-text mx-3">琴房助手 <sup>3</sup></div>
    </a>

    <!-- 分隔线 -->
    <hr class="sidebar-divider my-0">

    <!-- 导航选项 - 仪表盘 -->
    <li class="nav-item active">
        <a class="nav-link add-query" href="/key3/kit/page/kit-index">
            <i class="fas fa-fw fa-tachometer-alt"></i>
            <span>Dashboard</span></a>
    </li>

    <!-- 分隔线 -->
    <hr class="sidebar-divider">
    <div class="sidebar-heading">维护选项</div>

    <!-- 运行日志查看 -->
    <li class="nav-item">
        <a class="nav-link add-query" href="/key3/kit/page/kit-log">
            <i class="fas fa-fw fa-clipboard"></i>
            <span>运行日志</span>
        </a>
    </li>

    <!-- 缓存维护 -->
    <li class="nav-item">
        <a class="nav-link add-query" href="/key3/kit/page/kit-cache">
            <i class="fas fa-fw fa-hdd"></i>
            <span>缓存维护</span></a>
    </li>

    <!-- 侧边栏：开关按钮 -->
    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>

</ul>
<!-- 侧边栏结束 -->

<script>
    const getQuery = function() {
        let queryOrigin = location.search.substring(1);
        let queryArray = queryOrigin.split('&');
        let queryMap = {};
        queryArray.forEach(kvStr => {
            let kv = kvStr.split('=');
            queryMap[kv[0]] = kv[1];
        });
        return queryMap;
    }

    let aTags = $('.add-query');
    let querySuffix = location.search;
    for (let i = 0, len = aTags.length; i < len; i++) {
        aTags[i].href = aTags[i].href + querySuffix;
    }
</script>