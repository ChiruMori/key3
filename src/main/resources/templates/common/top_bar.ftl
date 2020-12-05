<#-- 顶部导航栏 -->
<nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">

    <#-- 侧边栏在顶部导航栏的开关按钮 -->
    <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
        <i class="fa fa-bars"></i>
    </button>

    <#-- 搜索框 -->
    <!--form
            class="d-none d-sm-inline-block form-inline mr-auto ml-md-3 my-2 my-md-0 mw-100 navbar-search">
        <div class="input-group">
            <input type="text" class="form-control bg-light border-0 small" placeholder="搜索人员、活动室信息"
                   aria-label="Search" aria-describedby="basic-addon2">
            <div class="input-group-append">
                <button class="btn btn-primary" type="button">
                    <i class="fas fa-search fa-sm"></i>
                </button>
            </div>
        </div>
    </form-->

    <!-- 顶部导航选项 -->
    <ul class="navbar-nav ml-auto">

        <!-- 搜索框 -->
        <li class="nav-item dropdown no-arrow d-sm-none">
            <a class="nav-link dropdown-toggle" href="#" id="searchDropdown" role="button"
               data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-search fa-fw"></i>
            </a>
            <!-- 下拉菜单 -->
            <div class="dropdown-menu dropdown-menu-right p-3 shadow animated--grow-in"
                 aria-labelledby="searchDropdown">
                <form class="form-inline mr-auto w-100 navbar-search">
                    <div class="input-group">
                        <input type="text" class="form-control bg-light border-0 small"
                               placeholder="搜索人员、活动室信息" aria-label="Search"
                               aria-describedby="basic-addon2">
                        <div class="input-group-append">
                            <button class="btn btn-primary" type="button">
                                <i class="fas fa-search fa-sm"></i>
                            </button>
                        </div>
                    </div>
                </form>
            </div>
        </li>

        <!-- 社团选择 -->
        <li class="nav-item dropdown no-arrow mx-1">
            <a class="nav-link dropdown-toggle" href="#" id="clubDropdown" role="button"
               data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <i class="fas fa-users-cog fa-fw"></i>
                <div class="ml-2 font-weight-bold" id="now-club-name">加载中...</div>
            </a>
            <!-- 下拉选框 - 社团列表 -->
            <div class="dropdown-list dropdown-menu dropdown-menu-right shadow animated--grow-in"
                 aria-labelledby="clubDropdown">
                <h6 class="dropdown-header">
                    <span>管理的社团</span>
                    <#if base.systemAdmin>
                        <a href="javascript:" id="newClubBtn" class="float-right h5 text-light" title="新增社团">+</a>
                    </#if>
                </h6>
                <#list base.clubs as club>
                    <a class="dropdown-item d-flex align-items-center" href="#${club.id}">
                        <div class="dropdown-list-image mr-3 mb-0 pt-1 h3">
                            <i class="fas fa-user-friends"></i>
                        </div>
                        <div class="font-weight-bold">
                            <div itemid="${club.id}" class="text-truncate club-item">${club.name}</div>
                        </div>
                    </a>
                <#else>
                    <div class="text-mute p-3">什么也没有</div>
                </#list>
            </div>
        </li>

        <div class="topbar-divider d-none d-sm-block"></div>

        <!-- 选项，用户信息 -->
        <li class="nav-item dropdown no-arrow">
            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
               data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <span class="mr-2 d-none d-lg-inline text-gray-600 small">欢迎，${base.userName}</span>
                <img class="img-profile rounded-circle" src="${base.head}" alt="头像而已">
            </a>
            <!-- 下拉列表：管理员信息维护 -->
            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                 aria-labelledby="userDropdown">
                <a class="dropdown-item" href="#">
                    <i class="fas fa-user fa-sm fa-fw mr-2 text-gray-400"></i>
                    个人信息
                </a>
                <a class="dropdown-item" href="#">
                    <i class="fas fa-cogs fa-sm fa-fw mr-2 text-gray-400"></i>
                    系统设置
                </a>
                <div class="dropdown-divider"></div>
                <a class="dropdown-item" href="#" data-toggle="modal" data-target="#logoutModal">
                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                    登出
                </a>
            </div>
        </li>
    </ul>
</nav>

<#-- 模态弹窗：登出 -->
<div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
     aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="exampleModalLabel">退出系统？</h5>
                <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body">点击登出将清除您的登录状态</div>
            <div class="modal-footer">
                <button class="btn btn-secondary" type="button" data-dismiss="modal">取消</button>
                <a class="btn btn-primary" href="#" id="logoutBtn">登出</a>
            </div>
        </div>
    </div>
</div>

<script src="/key3/js/top_bar.min.js" type="text/javascript"></script>
<script>
    GLOBAL_VAL.systemAdmin = ${base.systemAdmin?c};
</script>