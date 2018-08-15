$(function () {
    initTable();
    initValidate();
    modalHiddenEvent('modalApp');
});

function initTable() {
    var $table = $("#table");
    $table.bootstrapTable({ // 对应table标签的id
        url: "/admin/app/list", // 获取表格数据的url
        method: 'post',
        contentType:'application/x-www-form-urlencoded; charset=UTF-8',//巨坑
        cache: false, // 设置为 false 禁用 AJAX 数据缓存， 默认为true
        striped: true,  //表格显示条纹，默认为false
        pagination: true, // 在表格底部显示分页组件，默认false
        pageList: [5, 10], // 设置页面可以显示的数据条数
        pageSize: 10, // 页面数据条数
        pageNumber: 1, // 首页页码
        sidePagination: 'server', // 设置为服务器端分页
        toolbar: '#toolbar',                //工具按钮用哪个容器
        minimumCountColumns: 2,             //最少允许的列数
        detailView: false,                   //是否显示父子表
        cardView: false,                    //是否显示详细视图
        uniqueId: "appId",                     //每一行的唯一标识，一般为主键列
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
                field: 'appName',
                title: '应用名称'
            }, {
                field: 'appCode',
                title: '应用编码'
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
                title: "操作",
                align: 'center',
                valign: 'middle',
                width: 160, // 定义列的宽度，单位为像素px
                formatter: function (value, row, index) {
                    return '<span class="optIcon glyphicon glyphicon-edit" data-toggle="tooltip" title="编辑" onclick="editApp(2,' +','+index+')"></span>'
                        + '<span class="optIcon glyphicon glyphicon-trash" data-toggle="tooltip" title="删除" onclick="editApp(3,' +','+index+ ')"></span>';
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
function initValidate() {
    setValidateDefault();
    $("#appForm").validate({
        rules: {
            appName: {required: true},
            appCode: {required: true},
            sort: {required: true}
        },
        messages: {
            appName: {required: '应用名不能为空'},
            appCode: {required: '应用编码不能为空'},
            sort: {required: '排序号不能为空'}
        }
    })
}

var option = 1;//1:添加，2：修改，3：删除
// var optionUrl=['/admin/app/add','/admin/app/edit','']
function editApp(opt,rowIndex) {
    option = opt;
    if (opt == 1) {
        $('#editAppTitle').html('新增应用');
        $('#modalApp').modal('show');
        $("#appForm").clearForm();
        return;
    }
    var $table = $('#table');
    if(rowIndex!=null){
        $table.bootstrapTable('uncheckAll');
        $table.bootstrapTable('check', rowIndex);
    }
    //修改
    if (opt == 2) {
        var apps = $table.bootstrapTable('getSelections');
        if(apps.length!=1){
            Alert('请选择一项进行修改');
            return;
        }
        fillAppForm(apps[0]);
        $('#editAppTitle').html('修改应用');
        $('#modalApp').modal('show');
    }else if(opt==3){
        delApp();
    }
}
//填充表单
function fillAppForm(app) {
    if(!app)
        return;
    var $appForm = $('#appForm');
    $appForm.find("input[type!='radio']").each(function () {
        var $this = $(this);
        $this.val(app[$this.attr('name')]);
    });
    $appForm.find("input[type='radio']").each(function () {
        var $this = $(this);
        if(app[$this.attr('name')] == $this.val()){
            $this.prop("checked", true);
        }
    });
}
//增加或修改
function submitApp() {
    var $form = $("#appForm");
    var object = formToJsonObj($form);
    object.option = option;
    if ($form.valid()) {
        $.ajax({
            type: "post",
            url: '/admin/app/edit',
            dataType: 'json',
            // contentType: 'application/json;charset=utf-8',
            data: object,
            // beforeSubmit: showRequest,
            success: function (result) {
                if (!result || result.code != 0) {
                    Alert(result.msg);
                    return;
                }
                $('#modalApp').modal('hide');
                $("#table").bootstrapTable("refresh");
            },
            error: function () {
                Alert("操作失败");
            }
        });
    }
}
//删除应用
function delApp() {
    var apps = $('#table').bootstrapTable('getSelections');
    if(!apps||apps.length==0){
        return;
    }
    Confirm('确定删除选中项？').sure(function () {
        $.ajax({
            url:'/admin/app/delete',
            type:'post',
            contentType:'application/json;charset=UTF-8',
            data:JSON.stringify(apps),
            dataType:'json',
            success:function (result) {
                if (!result || result.code != 0) {
                    Alert(result.msg);
                    return;
                }
                $("#table").bootstrapTable("refresh");
            },
            error:function () {
                Alert('操作失败');
            }
        })
    });
}