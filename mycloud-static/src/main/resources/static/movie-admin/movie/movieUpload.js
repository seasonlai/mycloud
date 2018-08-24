var task_unfinish = [];
function initUpload() {
    var context;
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
            context = this;
            addTaskItem(data, e, context);
        }
    }).bind('fileuploadprogress', function (e, data) {
        var progress = parseInt(data.loaded / data.total * 100, 10);
        //更新进度条
        updateProgress(data.formData.name, progress);
    }).bind('fileuploaddone', function (e, data) {
        //更新完成后的状态
        var result = data.result;
        taskFinish(data.formData.name, result.data);
    }).bind('fileuploadchunkfail', function (e, data) {
        alert('上传失败');
        cancelUpload(data.formData.name)
    }).bind('fileuploadchunkdone', function (e, data) {
        var result = data.result;
        if (!commonResultHandle(result)) {
            cancelUpload(data.formData.name)
        }
    });

}
//开始上传
function startUpload(taskName) {
    var task = getTask(taskName);
    if (task) {

        $('#uploadBtn_' + task.id).after('<div>正在计算MD5...<span id="md5progress_' + task.id + '"></span></div>').hide();
        var tmpMd5;
        getFileMd5({
            file: task.data.files[0],
            chunkSize: 1000000
        }).progress(function (progress) {
            $('#md5progress_' + task.id).html(progress + '%');
        }).success(function (md5) {
            tmpMd5 = md5;
        }).always(function () {
            $.getJSON('/file/getFileOffset', {fileName: task.filePath, md5: tmpMd5}, function (result) {
                if (commonResultHandle(result)) {
                    if (result.data == -2) {
                        //已经上传过了
                        task.filePath = result.msg;
                        taskFinish(task.name);
                        return;
                    } else if (result.data > 0) {
                        task.data.uploadedBytes = result.data;
                        $.blueimp.fileupload.prototype
                            .options.add.call(task.context, task.e, task.data);
                    }
                    task.jqXHR = task.data.submit(); //更改按钮为停止
                    updateProgressStatus(taskName, true);
                    $('#uploadBtn_' + task.id)
                        .removeClass("arrow")
                        .removeClass('up')
                        .addClass('pause')
                        .unbind()
                        .click(function () {
                            cancelUpload(taskName);
                        })
                        .show()
                        .siblings()
                        .remove();
                }
            }).error(function () {
                alert('请求资源失败');
            });

        });

    }
}

function getFiles(filePath) {
    return $("<input type='file' value='" + filePath + "'>").prop('files');
}

//取消上传
function cancelUpload(taskName) {
    var task = getTask(taskName);
    if (task) {
        task.jqXHR.abort();
        updateProgressStatus(taskName, false);
        //更改为上传按钮
        $('#uploadBtn_' + task.id)
            .addClass("arrow")
            .addClass('up')
            .removeClass('pause')
            .unbind()
            .click(function () {
                startUpload(taskName);
            });
    }
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
        $('#progress_' + task.id).attr('data-percent', progress)
            .find('.bar').css('width', progress + '%')
            .end().find('.progress').html(progress + '%');
    }
}
//更新进度条状态
function updateProgressStatus(taskName, status) {
    var task = getTask(taskName);
    if (!task) {
        return;
    }
    if (status) {
        $('#progress_' + task.id).addClass('active');
    } else {
        $('#progress_' + task.id).removeClass('active');
    }
}

//切换tab
function changeTab(tag) {
    var $taskList = $('#taskList');
    $taskList.find('a[data-tab="' + tag + '"]').addClass('active').siblings().removeClass('active');
    $taskList.find('div[data-tab="' + tag + '"]').addClass('active').siblings().removeClass('active');
}
//添加任务
function addTaskItem(data, e, context) {
    $.ajax({
        url: '/video/add',
        dataType: 'json',
        type: 'post',
        data: {
            videoName: $('#videoName').val(),
            videoDesc: $('#videoDesc').val(),
            'videoQuality.id': 1,
            'videoQuality.name': '高清'//$('#videoQuality').val()
        },
        success: function (result) {
            if (commonResultHandle(result)) {
                var targetId = result.data;
                //添加任务
                $.post('/task/add', {
                    kind: 0,//类型为上传
                    targetId: targetId,
                    name: $('#videoName').val(),
                    filePath: $('#videoFile').val(),
                    size: data.files[0].size
                }, function (result) {
                    if (commonResultHandle(result)) {
                        var task = result.data;
                        //添加表单信息
                        data.formData = {
                            fileName: task.filePath,
                            name: task.name,
                            taskId: task.id
                        };
                        task.data = data;
                        task.e = e;
                        task.context = context;
                        //添加任务条目
                        task_unfinish.push(task);
                        addTaskItemUI(task);
                        //切换TAB
                        changeTab('one');
                        startUpload(task.name);
                    }
                }, 'json');
            }
        },
        error: function () {
            alert("请求失败");
        }

    });

}

function addTaskItemUI(task) {
    if (!task)
        return;
    var item = '<div class="item" id="task_' + task.id + '">'
        + '<div class="content">'
        + '<div class="header" style="padding: 10px 15px 5px;">'
        + '<span style="font-size: 14px;">' + task.name + '</span>'
        + '<div class="right floated content">'
        + '<i style="cursor: pointer" id="uploadBtn_' + task.id + '" class="upload icon arrow large circle up outline">'
        + '</i>'
        + '</div>'
        + '</div>'
        + '<div class="description" style="padding: 5px 10px;clear: both;">'
        + '<div class="ui progress blue" style="height: 11px" data-percent="0" '
        + 'id="progress_' + task.id + '">'
        + '<div class="bar"  style="transition-duration: 300ms; width: 0;height: 11px">'
        + '<div class="progress">0%</div>'
        + '</div>'
        + '</div>'
        + '</div>'
        + '</div>';
    $('#task_unFinish').append(item);
}

function taskFinish(taskName, filePath) {
    var task = getTask(taskName);
    if (!task) {
        return;
    }
    updateProgress(task.name, 100);
    //
    $.post('/video/update', {
        id: task.targetId,
        code: filePath || task.filePath,
        status: 0//可用
    }, function (result) {
        //把其从未完成列表移到已完成列表
        if (commonResultHandle(result)) {
            $('#task_' + task.id).remove();
            addFinishItem(task);
        }
    }, 'json').error(function () {
        //提交信息失败
        alert("请求失败");
    });

}

function addFinishItem(task) {
    if (!task)
        return;
    var item = '<div class="item" id="finish_' + task.id + '">'
        + '<div class="content">'
        + '<div class="header" style="padding: 10px 15px 5px;">'
        + '<span style="font-size: 14px;">' + task.name + '</span>'
        + '</div>'
        + '<div class="description" style="padding: 5px 10px;clear: both;">'
        + '<span>文件大小：' + getBetterFileSize(task.size) + '</span>'
        + '</div>'
        + '</div>'
        + '</div>';
    $('#task_finish').append(item);
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
function submitVideo() {
    var task = getTask($('#videoName').val());
    if (task) {
        alert('任务已存在');
        return;
    }
    //添加一个video
    $('#uploadForm').fileupload('add', {files: $('#videoFile').prop('files')});
}
$(function () {
    initUpload();
});
