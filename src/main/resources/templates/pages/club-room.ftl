<!DOCTYPE html>
<html lang="zh">

<head>
    <title>管理后台 - 活动室</title>
    <#include "../common/header.ftl">
</head>

<body id="page-top">

<!-- 整个页面的容器 -->
<div id="wrapper">

    <#include "../common/side_bar.ftl">

    <#-- 内容区域 -->
    <div id="content-wrapper" class="d-flex flex-column">

        <#-- 主要内容区域 -->
        <div id="content">

            <#include "../common/top_bar.ftl">

            <!-- 页面内容 -->
            <!-- Begin Page Content -->
            <div class="container-fluid">

                <!-- Page Heading -->
                <h1 class="h3 mb-2 text-gray-800">活动室基本信息维护</h1>
                <p class="mb-4">您可以在这里修改活动室的基本信息、开放时间等，也可以创建、解除活动室</p>

                <div class="card shadow mb-4">
                    <div class="card-header py-3">
                        <div class="form-row">
                            <h6 class="m-0 font-weight-bold text-primary col form-control p-2 border-0 bg-transparent">
                                活动室信息</h6>
                            <select class="form-control col-md-8" id="roomSelectTag">
                                <option>新たなクラブ</option>
                            </select>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="text-danger mb-2" id="errorHint"></div>
                        <form id="roomForm" onsubmit="return false;">
                            <div class="form-group">
                                <label for="id">活动室 ID</label>
                                <input type="number" class="form-control" id="id" name="id"
                                       aria-describedby="roomIdHelper">
                                <small id="roomIdHelper" class="form-text text-muted">活动室 ID，大多数情况无需填写，仅在需要与其他社团共用活动室时填写</small>
                            </div>
                            <div class="form-group">
                                <label for="name">名称<span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="name" name="name"
                                       aria-describedby="roomNameHelper">
                                <small id="roomNameHelper" class="form-text text-muted">活动室名称，如科学会馆1024</small>
                            </div>
                            <div class="form-group">
                                <label for="cost">贡献值消耗</label>
                                <input type="number" class="form-control" id="cost" name="cost"
                                       aria-describedby="pointCostHelp">
                                <small id="pointCostHelp" class="form-text text-muted">用户使用活动室的贡献点数消耗，可以指定为负数或
                                    0</small>
                            </div>
                            <div class="form-row" id="limitRow">
                                <div class="form-group col-md-6">
                                    <label for="weekLimit">周上限</label>
                                    <input type="number" class="form-control" id="weekLimit" name="weekLimit">
                                    <small id="limitRowHelp" class="form-text text-muted">限制用户使用活动室的时长上限</small>
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="dayLimit">日上限</label>
                                    <input type="number" class="form-control" id="dayLimit" name="dayLimit">
                                </div>
                            </div>
                            <div class="form-row" id="timeBorder">
                                <div class="form-group col-md-6">
                                    <label for="startHour">开放时间</label>
                                    <input type="number" class="form-control" id="startHour" name="startHour">
                                    <small id="timeBorderHelp"
                                           class="form-text text-muted">指定活动室的开放时间（24时整点）</small>
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="endHour">关闭时间</label>
                                    <input type="number" class="form-control" id="endHour" name="endHour">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="location">地理位置</label>
                                <div class="input-group" id="location">
                                    <input type="text" readonly class="form-control" id="longitude" placeholder="经度" name="longitude">
                                    <input type="text" readonly class="form-control" id="latitude" placeholder="纬度" name="latitude">
                                    <div class="input-group-append">
                                        <button class="btn btn-outline-secondary dropdown-toggle" type="button"
                                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">选择坐标</button>
                                        <div class="dropdown-menu" id="locationDropDown">
                                            <a class="dropdown-item" href="#">No.2099</a>
                                        </div>
                                    </div>
                                </div>
                                <small id="locationHelp" class="form-text text-muted">指定活动室的地理位置，用于签到检验</small>
                            </div>
                            <div class="form-group form-check">
                                <input type="checkbox" class="form-check-input" id="needSign" name="needSign">
                                <label class="form-check-label"
                                       for="enableAssets">启用签到限制，这会通过限制用户使用来强制成员进行签到，请在活动室中使用小程序获取坐标后开启此功能</label>
                            </div>
                            <div class="form-group form-check">
                                <input type="checkbox" class="form-check-input" id="available" name="available">
                                <label class="form-check-label"
                                       for="available">活动室是否开放，关闭后，活动室将不可预约，但现有预约信息将保留</label>
                            </div>
                            <button class="btn btn-primary" id="submitBtn">提交</button>
                            <button class="btn btn-danger" id="deleteBtn">删除</button>
                        </form>
                    </div>
                </div>

            </div>
            <!-- /.container-fluid -->
        </div>
        <#-- 内容区域结束 -->

        <#include "../common/copyright.ftl">
    </div>
    <!-- End of Content Wrapper -->

</div>
<!-- End of Page Wrapper -->

<#include "../common/footer.ftl">
<script type="text/javascript" src="/key3/js/club-room.min.js"></script>
</body>
</html>