var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "pid",
            rootPId: 0
        },key:{
            title:'title'
        }
    },
    async: {
        enable: true,
        type: "post",
        url: "/admin/role_perms/tree",
        otherParam: {"appId": $('#appSelected').val()},
        dataFilter: dataFilter
    },
    callback: {
        onAsyncError: onAsyncError,
        onAsyncSuccess: onAsyncSuccess
    }, check: {
        enable: true,
        chkStyle: "checkbox",
        chkboxType: {"Y": "p", "N": "s"}
    }
};

function dataFilter(treeId, parentNode, responseData) {
    if (!responseData || responseData.code != 0) {
        return {};
    }

    return responseData.data;
}

function onAsyncSuccess(event, treeId, treeNode, msg) {
    $.fn.zTree.getZTreeObj(treeId).expandAll(true);
}

function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus, errorThrown) {
    console.log('error: ' + treeId);
}


function refreshTree() {
    var treeObj = $.fn.zTree.getZTreeObj("rolePermissionTree");
    if (treeObj) treeObj.destroy();
    $.fn.zTree.init($("#rolePermissionTree"), setting);
}

$(function () {
    refreshTree();
});

function submitRolePerms() {

    var treeObj = $.fn.zTree.getZTreeObj("rolePermissionTree");
    var nodes = treeObj.getCheckedNodes(true);
    var data = {};
    if (nodes && nodes.length > 0) {
        for (var i in nodes) {
            var node = nodes[i];
            var key =  node.roleId; //"'" + + "'";
            var val = data[key] ? data[key] + ',' : '';
            val += node.id;
            data[key] = val;
        }
    }
    data.appId = $('#appSelected').val();
    $.ajax({
        url: '/admin/role_perms/update',
        type: 'post',
        contentType:'application/json;charset=UTF-8',
        data: JSON.stringify(data),
        success: function (result) {
            refreshTree();
            if (result && result.code != 0)
                Alert(result.msg);
        }, error: function () {
            Alert('请求失败');
        }
    })

}
