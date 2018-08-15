var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "pid",
            rootPId: 0
        },
        key: {
            title: 'title'
        }
    },
    async: {
        enable: true,
        type: "post",
        url: "/admin/permission/tree",
        autoParam: ["id", "pid"],
        // otherParam: {"appId": $('#appSelected').val()},
        dataFilter: dataFilter
    },
    callback: {
        onAsyncError: onAsyncError,
        onAsyncSuccess: onAsyncSuccess
    },
    view: {
        addHoverDom: addHoverDom,
        removeHoverDom: removeHoverDom
    }
};
$(document).ready(function () {
    refreshTree();
    initValidate();
    modalHiddenEvent('modalPermission');
});

function refreshTree() {
    var treeObj = $.fn.zTree.getZTreeObj("permissionTree");
    if (treeObj) treeObj.destroy();
    setting.async.otherParam={"appId": $('#appSelected').val()};
    $.fn.zTree.init($("#permissionTree"), setting);
}

function dataFilter(treeId, parentNode, responseData) {
    if (!responseData || responseData.code != 0) {
        return {};
    }

    return responseData.data;
}

/**
 * 添加按钮
 * @param treeId
 * @param treeNode
 */
function addHoverDom(treeId, treeNode) {
    var aObj = $("#" + treeNode.tId + "_a");
    if ($("#diyBtn_space_" + treeNode.id).length > 0) return;
    aObj.append("<span id='diyBtn_space_" + treeNode.id + "'> </span>"
        + "<span id='diyBtn_" + treeNode.id + "_a' class='fa fa-plus' style='color: #286090;margin-left: 6px;cursor: pointer;' title='添加' onfocus='this.blur();'></span>");

    var addBtn = $("#diyBtn_" + treeNode.id + '_a');
    if (addBtn) addBtn.bind("click", function () {
        editPermission(1, treeNode);
    });
    if (treeNode.parentTId == null) {
        return;
    }
    aObj.append("<span id='diyBtn_" + treeNode.id + "_b' class='fa fa-pencil' style='margin-left: 6px;color:#398439;cursor: pointer;' title='编辑' onfocus='this.blur();'></span>"
        + "<span id='diyBtn_" + treeNode.id + "_c' class='fa fa-times' style='color: #ac2925;margin-left: 6px;cursor: pointer;' title='删除' onfocus='this.blur();'></span>")
    // aObj.append(editStr);
    // $("[data-toggle='tooltip']").tooltip();
    var editBtn = $("#diyBtn_" + treeNode.id + '_b');
    if (editBtn) editBtn.bind("click", function () {
        editPermission(2, treeNode);
    });
    var delBtn = $("#diyBtn_" + treeNode.id + '_c');
    if (delBtn) delBtn.bind("click", function () {
        delPermission(treeNode);
    });
}

function removeHoverDom(treeId, treeNode) {
    $("#diyBtn_space_" + treeNode.id).unbind().remove();
    $("#diyBtn_" + treeNode.id + '_a').unbind().remove();
    if (treeNode.parentTId == null)
        return;
    $("#diyBtn_" + treeNode.id + '_b').unbind().remove();
    $("#diyBtn_" + treeNode.id + '_c').unbind().remove();
}


function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
    console.log('error: ' + treeId);
}

function onAsyncSuccess(event, treeId, treeNode, msg) {
    $.fn.zTree.getZTreeObj(treeId).expandAll(true);
}

//-------------------- 分割 ----------------------//

var parentSort;
jQuery.validator.addMethod("noSmallParent", function (value, element, param) {
    return this.optional(element) || ( value >= parentSort );
}, '不能小于父节点的排序号');
function initValidate() {
    setValidateDefault();
    $("#permissionForm").validate({
        rules: {
            permissionName: {required: true},
            permissionUrl: {required: true},
            textDescription: {maxlength: 80},
            sort: {required: true, number: true, noSmallParent: ''}
        },
        messages: {
            permissionName: {required: '权限名不能为空'},
            permissionUrl: {required: '权限URL不能为空'},
            sort: {required: '排序号不能为空'}
        }
    })
}


var option = 1;//1:添加，2：修改，3：删除
// var optionUrl=['/admin/app/add','/admin/app/edit','']
function editPermission(opt, node) {
    if (!opt || !node)
        return;
    option = opt;
    parentSort = node.p_sort;
    $("#permissionForm").clearForm();
    var appId = $('#appSelected').val();
    if (opt == 1) {
        $('#editPermissionTitle').html('新增权限');
        $('#appBelong').val(appId);
        $('#inputPermissionId').val('');
        $('#inputParentIdId').val(node.id);
        $('#inputPermissionAppId').val(appId);
        $('#modalPermission').modal('show');
        return;
    }
    //修改
    if (opt == 2) {
        fillTreeForm(node);
        $('#editPermissionTitle').html('修改权限');
        $('#appBelong').val(appId);
        $('#inputPermissionAppId').val(appId);
        $('#modalPermission').modal('show');
    }
}
function delPermission(treeNode) {
    Confirm("确定删除该权限及其子权限？").sure(function () {
        var nodes = [];
        var treeObj = $.fn.zTree.getZTreeObj('permissionTree');
        var childNodes = treeObj.transformToArray(treeNode);
        for (i = 0; i < childNodes.length; i++) {
            nodes[i] = childNodes[i].id;
        }
        $.ajax({
            type: 'POST',
            url: '/admin/permission/del',
            data: {ids: nodes.join(","),appId:$('#appSelected').val()},
            success: function (result) {
                refreshTree();
                if (result && result.code != 0)
                    Alert(result.msg);
            },
            error: function () {
                Alert('请求失败');
            }
        });
    });
}

//填充表单
function fillTreeForm(node) {
    if (!node)
        return;
    var $permissionForm = $('#permissionForm');
    $permissionForm.find("input[type!='radio']").each(function () {
        var $this = $(this);
        $this.val(node['p_' + $this.attr('name')]);
    });
    $permissionForm.find("input[type='radio']").each(function () {
        var $this = $(this);
        if (node['p_' + $this.attr('name')] == $this.val()) {
            $this.prop("checked", true);
        }
    });
    $permissionForm.find('#inputPermissionId').val(node.id);
    $permissionForm.find('#inputPermissionName').val(node.name);
    $permissionForm.find('#textDescription').val(node.p_description);
}

function submitPermission() {

    var $form = $("#permissionForm");
    var object = formToJsonObj($form);
    object.option = option;
    object.appId = $('#appBelong').val();
    if ($form.valid()) {
        $.ajax({
            url: '/admin/permission/edit',
            type: 'post',
            data: object,
            success: function (result) {
                refreshTree();
                if (result && result.code != 0) {
                    Alert(result.msg);
                    result;
                }
                $('#modalPermission').modal('hide');
            },
            error: function () {
                Alert('请求失败');
            }
        });
    }
}