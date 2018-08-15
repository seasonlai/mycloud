$(function () {
    initTable();
    initValidate();
});

function initTable() {
    var $table = $("#table");
    $table.bootstrapTable({ // 对应table标签的id
        url: "/admin/user/list", // 获取表格数据的url
        method: 'post',
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',//巨坑
        cache: false, // 设置为 false 禁用 AJAX 数据缓存， 默认为true
        striped: true,  //表格显示条纹，默认为false
        pagination: true, // 在表格底部显示分页组件，默认false
        pageList: [5, 10], // 设置页面可以显示的数据条数
        pageSize: 10, // 页面数据条数
        pageNumber: 1, // 首页页码
        sidePagination: 'server', // 设置为服务器端分页
        toolbar: '#toolbar',                //工具按钮用哪个容器
        minimumCountColumns: 2,             //最少允许的列数
        // clickToSelect: true,                //是否启用点击选中行
        detailView: false,                   //是否显示父子表
        uniqueId: "userId",                     //每一行的唯一标识，一般为主键列
        cardView: false,                    //是否显示详细视图
        // showRefresh: true,                  //是否显示刷新按钮
        // showColumns: true,                  //是否显示所有的列操作
        // showToggle:true,                    //是否显示详细视图和列表视图的切换按钮
        // search: true,                       //是否显示表格搜索
        // height: 500,                        //行高，如果没有设置height属性，表格自动根据记录条数觉得表格高度
        // sortName: 'id', // 要排序的字段
        // sortOrder: 'desc', // 排序规则
        queryParams: function (params) { // 请求服务器数据时发送的参数，可以在这里添加额外的查询参数，返回false则终止请求
            return {
                pageSize: params.limit, // 每页要显示的数据条数
                pageNum: params.offset // 每页显示数据的开始行号
                // sort: params.sort, // 要排序的字段
                // sortOrder: params.order, // 排序规则
                // dataId: $("#dataId").val() // 额外添加的参数
            }
        },
        columns: [
            {
                checkbox: true, // 显示一个勾选框
                align: 'center', // 居中显示
                valign: 'middle' // 上下居中
            }, {
                field: 'userName',
                title: '管理员名称',
                valign: 'middle' // 上下居中
            }, {
                field: 'lastVisit',
                title: '上次登录时间',
                align: 'center', // 左右居中
                valign: 'middle' // 上下居中
            }, {
                field: 'lastIp',
                title: '上次登录IP',
                align: 'center', // 左右居中
                valign: 'middle' // 上下居中
            }, {
                field: 'locked',
                title: '是否锁定',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) { // 单元格格式化函数
                    return value == 1 ?
                        '<label class="label label-danger">是</label>' : '<label class="label label-default">否</label>';
                }
            }, {
                field: 'createTime',
                title: '创建时间',
                align: 'center',
                valign: 'middle'
            }, {
                title: '操作',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row, index) { // 单元格格式化函数
                    return '<span class="optIcon fa fa-lock" data-toggle="tooltip" title="锁定" onclick="lockOrUnlock(1,' + index + ')"></span>'
                        + '<span class="optIcon fa fa-unlock" data-toggle="tooltip" title="解锁" onclick="lockOrUnlock(0,' + index + ')"></span>'

                }
            }
        ],
        onLoadSuccess: function (result) {  //加载成功时执行
            $("[data-toggle='tooltip']").tooltip();
        },
        onLoadError: function () {  //加载失败时执行
            Alert("加载数据失败");
        }
        , responseHandler: function (result) {
            if (!result || result.code != 0) {
                return null;
            }
            var data = result.data;
            if (!data) {
                return null;
            }
            return {
                total: data.totalElements,//总数据条数
                rows: data.content   //数据
            };
        }
    })
}

var lockStatus;

function lockOrUnlock(status, rowIndex) {
    lockStatus = status;
    var $table = $('#table');
    if (rowIndex != null) {
        $table.bootstrapTable('uncheckAll');
        $table.bootstrapTable('check', rowIndex);
    }
    var users = $table.bootstrapTable('getSelections');
    if (users.length == 0) {
        return;
    }
    Confirm('确定' + (status == 1 ? '锁定' : '解锁') + '选中的用户？').sure(function () {
        $.ajax({
            url: '/admin/user/lockOrUnlock?status=' + lockStatus,
            type: 'post',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify(users),
            dataType: 'json',
            success: function (result) {
                if (!result || result.code != 0) {
                    Alert(result.msg);
                    return;
                }
                $("#table").bootstrapTable("refresh");
            },
            error: function () {
                Alert('操作失败');
            }
        })
    });
}

//-------------------- 分割线 ----------------------//

function initValidate() {
    setValidateDefault();
    $("#userForm").validate({
            rules: {
                userName: {
                    required: true,
                    minlength: 2,
                    remote: {
                        type: "post",
                        url: "/register/judgeUser",
                        data: {
                            username: function () {
                                return $("#inputUserName").val();
                            }
                        },
                        dataType: "html",
                        dataFilter: function (data, type) {
                            if (data == "true")
                                return true;
                            else
                                return false;
                        }
                    }
                },
                password: {required: true},
                password2: {equalTo: '#inputPassword'}
            },
            messages: {
                userName: {
                    required: '用户名不能为空',
                    remote: '用户名已存在'
                }
                ,
                password: {
                    required: '密码不能为空'
                },
                password2: {equalTo: "两次密码输入不一致"},
            }
        }
    )
}

var option;
function addUser() {
    option = 1;
    $("#userForm").clearForm();
    $('#modalUser').modal('show');
}

function delUser() {
    var $table = $("#table");
    var users = $table.bootstrapTable('getSelections');
    if (!users || users.length === 0) {
        return;
    }
    Confirm('确定删除选中项？').sure(function () {
        $.ajax({
            url: '/admin/user/del',
            type: 'post',
            data: JSON.stringify(users),
            contentType: 'application/json;charset=UTF-8',
            success: function (result) {
                if (!result)return;
                if (result.code !== 0) {
                    Alert(result.msg);
                    return;
                }
                $table.bootstrapTable("refresh");
            },
            error: function () {
                Alert('请求失败');
            }
        })
    })

}

function submitUser() {
    var $form = $("#userForm");
    var object = formToJsonObj($form);
    object.option = option;
    object.userType = 1;//管理员
    if ($form.valid()) {
        $.ajax({
            url: '/admin/user/edit',
            type: 'post',
            data: object,
            success: function (result) {
                if (result && result.code !== 0) {
                    Alert(result.msg);
                    return;
                }
                $("#table").bootstrapTable("refresh");
                $('#modalUser').modal('hide');
            },
            error: function () {
                Alert('请求失败');
            }
        });
    }
}


//分配角色
var firstAuthenticate = true;
function authenticate() {

    var users = $('#table').bootstrapTable('getSelections');
    if (!users || users.length !== 1) {
        Alert('请选择一个用户');
        return;
    }

    if (firstAuthenticate) {
        firstAuthenticate = false;
        $.ajax({
            url: '/admin/app/listAll',
            type: 'post',
            async: false,
            success: function (result) {
                if (!result) result;
                if (result.code != 0) Alert(result.msg);
                var data = result.data;
                if (!data || data.length == 0)return;
                var $appBelong = $('#appBelong');
                for (var i in data) {
                    var app = data[0];
                    $appBelong.append("<option value='" + app.appId + "'>" + app.appName + "</option>");
                }
            }
        })
    }
    initRoles(users[0].userId);
    $('#modalUserRole').modal('show');
}

function submitUserRole() {
    var appBelong = $('#appBelong').val();
    if (!appBelong)return;
    var users = $('#table').bootstrapTable('getSelections');
    if (!users || users.length !== 1) {
        return;
    }
    var userId = users[0].userId;
    var roles = $('#rolesContainer').find('input:checked');
    var ids = '';
    if (roles && roles.length != 0) {
        roles.each(function (i, item) {
            ids += $(item).val() + ',';
        });
        ids = ids.substring(0, ids.length - 1);
    }
    $.ajax({
        url: '/admin/user_role/update',
        type: 'post',
        data: {roles: ids, appId: appBelong, userId: userId},
        success: function (result) {
            if (!result)return;
            if (result.code != 0) {
                Alert(result.msg);
                return;
            }
            $('#modalUserRole').modal('hide');
        },
        error: function () {
            Alert('请求失败');
        }
    })
}
var roles;
function initRoles(userId) {
    var appBelong = $('#appBelong').val();
    if (!appBelong)return;

    var $rolesContainer = $('#rolesContainer');
    $rolesContainer.empty();
    $rolesContainer.append("<p>正在加载...</p>")
    $.ajax({
        url: '/admin/user_role/list',
        type: 'post',
        data: {appId: appBelong, userId: userId},
        success: function (result) {
            if (!result)return;
            if (result.code != 0) Alert(result.msg);
            var data = result.data;
            roles = data;
            if (!data || data.length == 0)
                $rolesContainer.children(":first").html('该系统暂无角色');
            else
                $rolesContainer.empty();
            for (var i in data) {
                var role = data[i];
                $rolesContainer.append("<li style='list-style: none;display: inline-block;margin-right: 10px;'>" +
                    "<label class='checkbox-inline'>" +
                    "<input type='checkbox' " + (role.author ? "checked='true'" : '') + " value='" + role.id
                    + "' >" + role.name + "</label>" +
                    "</li>");
            }
        },
        error: function () {
            Alert('请求失败')
        }
    })
}