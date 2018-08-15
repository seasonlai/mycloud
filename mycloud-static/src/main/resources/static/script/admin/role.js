$(function () {
    initTable();
    initValidate();
    modalHiddenEvent('modalRole');
});

function initTable() {
    var $table = $("#table");
    // var appId = $("#appSelected").val();
    $table.bootstrapTable({ // 对应table标签的id
        url: "/admin/role/list", // 获取表格数据的url
        method: 'post',
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',//巨坑
        cache: false, // 设置为 false 禁用 AJAX 数据缓存， 默认为true
        striped: true,  //表格显示条纹，默认为false
        pagination: true, // 在表格底部显示分页组件，默认false
        pageList: [5, 10], // 设置页面可以显示的数据条数
        pageSize: 5, // 页面数据条数
        pageNumber: 1, // 首页页码
        sidePagination: 'server', // 设置为服务器端分页
        minimumCountColumns: 2,             //最少允许的列数
        detailView: false,                   //是否显示父子表
        cardView: false,                    //是否显示详细视图
        uniqueId: "roleId",                     //每一行的唯一标识，一般为主键列
        toolbar: '#toolbar',                //工具按钮用哪个容器
        // clickToSelect: true,                //是否启用点击选中行
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
                pageNum: params.offset, // 每页显示数据的开始行号
                appId: $("#appSelected").val() // 额外添加的参数
                // sort: params.sort, // 要排序的字段
                // sortOrder: params.order, // 排序规则
            }
        },
        columns: [
            {
                checkbox: true, // 显示一个勾选框
                align: 'center', // 居中显示
                valign: 'middle' // 上下居中
            }, {
                field: 'roleName',
                title: '角色名称'
            }, {
                field: 'sort',
                title: '排序号',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'enable',
                title: '是否启用',
                align: 'center',
                valign: 'middle',
                width: 80,
                formatter: function (value, row, index) { // 单元格格式化函数
                    return value == 1 ?
                        '<label class="label label-success">是</label>' : '<label class="label label-danger">否</label>';
                }
            }, {
                field: 'createTime',
                title: '创建时间',
                align: 'center',
                valign: 'middle'
            }, {
                field: 'description',
                title: '描述',
                align: 'center',
                valign: 'middle',
                width: 200
            }, {
                title: "操作",
                align: 'center',
                valign: 'middle',
                width: 160, // 定义列的宽度，单位为像素px
                formatter: function (value, row, index) {
                    return '<span class="optIcon fa fa-edit" data-toggle="tooltip" title="编辑" onclick="editRole(2,' + index + ')"></span>'
                        + '<span class="optIcon fa fa-play-circle-o" data-toggle="tooltip" title="启用" onclick="updateEnable(1,' + index + ')"></span>'
                        + '<span class="optIcon fa fa-ban" data-toggle="tooltip" title="禁用" onclick="updateEnable(0,' + index + ')"></span>'
                        + '<span class="optIcon fa fa-trash" data-toggle="tooltip" title="删除" onclick="editRole(3,' + index + ')"></span>'
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
                return {total: 0};
            }
            var data = result.data;
            if (!data) {
                return {total: 0};
            }
            return {
                total: data.totalElements,//总数据条数
                rows: data.content   //数据
            };
        }
    })
}


//------------------------------------------------------------

function refreshTable() {
    $("#table").bootstrapTable("refresh");
}

function initValidate() {
    setValidateDefault();
    $("#roleForm").validate({
        rules: {
            roleName: {required: true},
            sort: {required: true}
        },
        messages: {
            roleName: {required: '角色名不能为空'},
            sort: {required: '排序号不能为空'}
        }
    });
}

var option = 1;//1:添加，2：修改，3：删除
// var optionUrl=['/admin/app/add','/admin/app/edit','']
function editRole(opt, rowIndex) {
    option = opt;
    $("#roleForm").clearForm();
    $('#appBelong').val($('#appSelected').val());
    $('#inputRoleAppId').val($('#appBelong').val());
    if (opt == 1) {
        $('#editRoleTitle').html('新增角色');
        $('#modalRole').modal('show');
        return;
    }
    var $table = $('#table');
    if (rowIndex != null) {
        $table.bootstrapTable('uncheckAll');
        $table.bootstrapTable('check', rowIndex);
    }
    //修改
    if (opt == 2) {
        var roles = $table.bootstrapTable('getSelections');
        if (roles.length != 1) {
            Alert('请选择一项进行修改');
            return;
        }
        fillRoleForm(roles[0]);
        $('#editRoleTitle').html('修改应用');
        $('#modalRole').modal('show');
    } else if (opt == 3) {
        delRole();
    }
}
//填充表单
function fillRoleForm(role) {
    if (!role)
        return;
    var $roleForm = $('#roleForm');
    $roleForm.find("input[type!='radio']").each(function () {
        var $this = $(this);
        $this.val(role[$this.attr('name')]);
    });
    $roleForm.find("input[type='radio']").each(function () {
        var $this = $(this);
        if (role[$this.attr('name')] == $this.val()) {
            $this.prop("checked", true);
        }
    });
}
//增加或修改
function submitRole() {
    var $form = $("#roleForm");
    var object = formToJsonObj($form);
    object.option = option;
    if ($form.valid()) {
        $.ajax({
            type: "post",
            url: '/admin/role/edit',
            dataType: 'json',
            // contentType: 'application/json;charset=utf-8',
            data: object,
            // beforeSubmit: showRequest,
            success: function (result) {
                if (!result)return;
                if (result.code != 0) {
                    Alert(result.msg);
                    return;
                }
                $('#modalRole').modal('hide');
                $("#table").bootstrapTable("refresh");
            },
            error: function () {
                Alert("请求失败");
            }
        });
    }
}
//删除应用
function delRole() {
    var roles = $('#table').bootstrapTable('getSelections');
    if (!roles || roles.length == 0) {
        return;
    }
    Confirm('确定删除选中项？').sure(function () {
        $.ajax({
            url: '/admin/role/delete',
            type: 'post',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify(roles),
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


//----------------------------------------
//停用或启用角色
function updateEnable(status, rowIndex) {
    var $table = $('#table');
    if (rowIndex != null) {
        $table.bootstrapTable('uncheckAll');
        $table.bootstrapTable('check', rowIndex);
    }
    var roles = $table.bootstrapTable('getSelections');
    if (roles.length == 0) {
        return;
    }
    Confirm('确定' + (status == 1 ? '启用' : '停用') + '选中的角色？').sure(function () {
        $.ajax({
            url: '/admin/role/updateEnable?status=' + status,
            type: 'post',
            contentType: 'application/json;charset=UTF-8',
            data: JSON.stringify(roles),
            dataType: 'json',
            success: function (result) {
                if (!result)return;
                if (result.code != 0) {
                    Alert(result.msg);
                    return;
                }
                $table.bootstrapTable("refresh");
            },
            error: function () {
                Alert('请求失败');
            }
        })
    });
}


//授权角色
function authorize() {
    window.location.href = '/admin/role_perms'
}
