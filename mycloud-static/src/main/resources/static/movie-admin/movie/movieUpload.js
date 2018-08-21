var jqXHR;

$("#videoFile").fileupload({
    url: '/file/uploadFilePair',
    fileName: "file",//文件的名称，此处是变量名称，不是文件的原名称只是后台接收的参数
    limitConcurrentUploads: 1,
    sequentialUploads: true,
    progressInterval: 100,
    maxChunkSize: 100000, //设置上传片段大小，不设置则为整个文件上传
    dataType: "json",
    autoUpload: true,
    add: function (e, data) {
        jqXHR = data.submit();
        addTaskItem();
        changeTab('one');
    }
}).bind('fileuploadprogress', function (e, data) {
    var result = data.result;
    if (!commonResultHandle(result)) {
        alert(result.msg);
    }

    var progress = parseInt(data.loaded / data.total * 100, 10) - 1;

    $('#uploadItem').attr('data-percent', progress + '%')
        .find('.bar').css('width', progress + '%')
        .end().find('progress').html(progress + '%');

    //更新进度条
}).bind('fileuploaddone', function (e, data) {
    //更新完成后的状态
}).bind('fileuploadpaste', function (e, data) {
    alert("aaa");
});

//取消上传
function cancelUpload() {
    jqXHR.abort();
}
//切换tab
function changeTab(index) {
    $("#tabHead[data-tab=" + index + "]").addClass("active").siblings().removeClass('active');
    $("#tabPage[data-tab=" + index + "]").addClass("active").siblings().removeClass('active');
}

//添加任务
function addTaskItem(task) {
    var item = '<div class="ui middle aligned divided list">'
        + '<div class="item">'
        + '<div class="content">'
        + '<div class="header" style="padding: 10px 15px 5px;">'
        + '<span style="font-size: 14px;">一出好戏</span>'
        + '<div class="right floated content">'
        + '<i style="cursor: pointer" class="upload icon arrow large circle up outline">'
        + '</i>'
        + '</div>'
        + '</div>'
        + '<div class="description" style="padding: 5px 10px;clear: both;">'
        + '<div class="ui active progress blue" style="height: 11px" data-percent="0" id="uploadItem">'
        + '<div class="bar"  style="transition-duration: 300ms; width: 0;height: 11px">'
        + '<div class="progress">0%</div>'
        + '</div>'
        + '</div>'
        + '</div>'
        + '</div>'
        + '</div>';

    $('#task_unFinish').append(item);
}

function changeProgress(id, percent) {

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

        submitMovie();
    }
});


$(function () {


});
