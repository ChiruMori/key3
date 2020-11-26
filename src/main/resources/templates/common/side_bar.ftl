
<#-- 侧边栏（导航栏） -->
<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

    <!-- 侧边栏：顶部图标 -->
    <a class="sidebar-brand d-flex align-items-center justify-content-center" href="#">
        <div class="sidebar-brand-icon rotate-n-15">
            <i class="fas fa-compact-disc fa-spin"></i>
        </div>
        <div class="sidebar-brand-text mx-3">琴房助手 <sup>3</sup></div>
    </a>

    <!-- 分隔线 -->
    <hr class="sidebar-divider my-0">

    <!-- 导航选项 - 仪表盘 -->
    <li class="nav-item active">
        <a class="nav-link" href="/key3/admin/page/dashboard">
            <i class="fas fa-fw fa-tachometer-alt"></i>
            <span>Dashboard</span></a>
    </li>

    <!-- 分隔线 -->
    <hr class="sidebar-divider">
    <div class="sidebar-heading">社团信息管理</div>

    <!-- 扩展列表：社团信息管理 -->
    <li class="nav-item">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseClub"
           aria-expanded="true" aria-controls="collapseClub">
            <i class="fas fa-fw fa-users-cog"></i>
            <span>社团管理</span>
        </a>
        <div id="collapseClub" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionSidebar">
            <div class="py-2 collapse-inner rounded">
                <a class="collapse-item" href="#"><i class="fas fa-info-circle mr-2"></i>基本信息</a>
                <a class="collapse-item" href="#"><i class="fas fa-coins mr-2"></i>财务管理</a>
                <a class="collapse-item" href="#"><i class="fas fa-bell mr-2"></i>通知公告</a>
                <a class="collapse-item" href="#"><i class="fas fa-calendar-day mr-2"></i>活动管理</a>
            </div>
        </div>
    </li>

    <!-- 扩展列表：活动室管理 -->
    <li class="nav-item">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseRoom"
           aria-expanded="true" aria-controls="collapseRoom">
            <i class="fas fa-fw fa-house-user"></i>
            <span>活动室管理</span>
        </a>
        <div id="collapseRoom" class="collapse" aria-labelledby="headingUtilities"
             data-parent="#accordionSidebar">
            <div class="py-2 collapse-inner rounded">
                <a class="collapse-item" href="#"><i class="fas fa-info-circle mr-2"></i>基本信息</a>
                <a class="collapse-item" href="#"><i class="fas fa-table mr-2"></i>预约管理</a>
            </div>
        </div>
    </li>

    <!-- 扩展列表：成员管理 -->
    <li class="nav-item">
        <a class="nav-link collapsed" href="#" data-toggle="collapse" data-target="#collapseUser"
           aria-expanded="true" aria-controls="collapseUser">
            <i class="fas fa-fw fa-user-cog"></i>
            <span>成员管理</span>
        </a>
        <div id="collapseUser" class="collapse" aria-labelledby="headingUtilities"
             data-parent="#accordionSidebar">
            <div class="py-2 collapse-inner rounded">
                <a class="collapse-item" href="#"><i class="fas fa-info-circle mr-2"></i>基本信息</a>
            </div>
        </div>
    </li>

    <!-- 分隔线 -->
    <hr class="sidebar-divider">
    <div class="sidebar-heading">系统管理</div>

    <!-- 系统日志查看 -->
    <li class="nav-item">
        <a class="nav-link collapsed" href="#">
            <i class="fas fa-fw fa-clipboard"></i>
            <span>系统日志</span>
        </a>
    </li>

    <!-- 系统参数设置 -->
    <li class="nav-item">
        <a class="nav-link" href="#">
            <i class="fas fa-fw fa-th-list"></i>
            <span>系统参数</span></a>
    </li>

    <!-- 侧边栏：开关按钮 -->
    <div class="text-center d-none d-md-inline">
        <button class="rounded-circle border-0" id="sidebarToggle"></button>
    </div>

    <#if base.showSideBarCard>
        <#-- 侧边栏：信息卡片，只在 dashboard 页面显示 -->
        <div class="sidebar-card">
            <i class="fab fa-github fa-3x mb-2"></i>
            <p class="text-center mb-2">欢迎使用本程序，使用期间，如果遇到问题、功能建议或者希望贡献代码等，可以在 <a class="text-success"
                                                                                 href="https://github.com/first-snow/qfzs">github</a>
                上进行反馈，也可以<a class="text-success"
                            href="mailto:cxlm@cxlm.work">发邮件</a>给<a class="text-success" href="https://cxlm.work">作者</a>
            </p>
        </div>
    </#if>

</ul>
