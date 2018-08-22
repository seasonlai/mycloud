function initUpload() {
    $("#uploadForm").fileupload({
        url: '/file/uploadFilePair',
        paramName: "file",//文件的名称，此处是变量名称，不是文件的原名称只是后台接收的参数
        limitConcurrentUploads: 1,
        sequentialUploads: true,
        progressInterval: 100,
        maxChunkSize: 100000, //设置上传片段大小，不设置则为整个文件上传
        dataType: "json",
        autoUpload: false,
        add: function (e, data) {
            jqXHR = data.submit();
            addTaskItem();
            changeTab('one');
        }
    }).bind('fileuploadprogress', function (e, data) {
        var progress = parseInt(data.loaded / data.total * 100, 10) - 1;
        //更新进度条
        updateProgress(data.files[0].name, progress);

    }).bind('fileuploaddone', function (e, data) {
        //更新完成后的状态

    }).bind('fileuploadpaste', function (e, data) {

    }).bind('fileuploadchunkfail', function (e, data) {
        cancelUpload(data.files[0].name)
    }).bind('fileuploadchunksend', function (e, data) {
        // data.formData={fileName}
    }).bind('fileuploadchunkdone', function (e, data) {
        var result = data.result;
        if (!commonResultHandle(result)) {
            cancelUpload(data.files[0].name)
        }
    });

}

function startUpload(taskName) {
    var task = getTask(taskName);
    var jqXHR = $('#fileupload').fileupload('send', {files: filesList});


}

function getFiles(filePath) {
    return $("<input type='file' value='" + filePath + "'>").prop('files');
}

//取消上传
function cancelUpload(taskName) {
    var task = getTask(taskName);
    if (task && task.jqXHR)
        task.jqXHR.abort();
}
//获取任务
function getTask(taskName) {
    for (var i in task_unfinish) {
        var task = task_unfinish[i];
        if (task.name === taskName)
            return task;
    }
}
//更新进度条
function updateProgress(taskName, progress) {
    var task = getTask(taskName);
    if (task) {
        $('#task_' + task.id).attr('data-percent', progress)
            .find('.bar').css('width', progress + '%')
            .end().find('progress').html(progress + '%');
    }
}
//切换tab
function changeTab(tag) {
    var $taskList = $('#taskList');
    $taskList.find('a[data-tab="' + tag + '"]').addClass('active').siblings().removeClass('active');
    $taskList.find('div[data-tab="' + tag + '"]').addClass('active').siblings().removeClass('active');
}
//添加任务
function addTaskItem(task) {
    if(!task)
        return;
    var item = '<div class="ui middle aligned divided list">'
        + '<div class="item">'
        + '<div class="content">'
        + '<div class="header" style="padding: 10px 15px 5px;">'
        + '<span style="font-size: 14px;">' + task.name + '</span>'
        + '<div class="right floated content">'
        + '<i style="cursor: pointer" class="upload icon arrow large circle up outline">'
        + '</i>'
        + '</div>'
        + '</div>'
        + '<div class="description" style="padding: 5px 10px;clear: both;">'
        + '<div class="ui active progress blue" style="height: 11px" data-percent="0" '
        + 'id="task_' + task.id + '">'
        + '<div class="bar"  style="transition-duration: 300ms; width: 0;height: 11px">'
        + '<div class="progress">0%</div>'
        + '</div>'
        + '</div>'
        + '</div>'
        + '</div>'
        + '</div>';
    task_unfinish.push(task);

    $('#task_unFinish').append(item);
}

$('#videoForm').form({
    inline: true,
    on: 'blur',
    fields: {
        videoName: {
            identifier: 'videoName',
            rules: [{
                type: 'empty',
                prompt: '视频名称不能为空'
            }]
        },
        videoFile: {
            identifier: 'videoFile',
            rules: [{
                type: 'empty',
                prompt: '请选择视频文件'
            }]
        },
        videoQuality: {
            identifier: 'videoQuality',
            rules: [{
                type: 'checked',
                prompt: '请选择视频质量'
            }]
        }
    },
    onSuccess: function (e) {
        //阻止表单的提交
        e.preventDefault();
        submitVideo();
    }
});
//提交视频
function videoForm() {
    //添加一个video
    var $form = $('#videoForm');
    $form.ajaxSubmit({
        url: '/video/add',
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        dataType: 'json',
        // data: {appCode: appCode, backUrl: backUrl},
        success: function (result) {
            if (commonResultHandle(result)) {
                var targetId = result.data;
                //添加任务
                $.post('/task/add', {
                    kind: 0,//类型为上传
                    targetId: targetId,
                    name: $('#videoName').val(),
                    filePath: $('#videoFile').val(),
                }, function (result) {
                    if (commonResultHandle(result)) {
                        //添加任务条目
                        addTaskItem(result.data);
                        //切换TAB
                        changeTab('one');
                    }
                }, 'json');
            }
        },
        error: function () {
            alert("请求失败");
        }
    });
}
$(function () {
    initUpload();
});
