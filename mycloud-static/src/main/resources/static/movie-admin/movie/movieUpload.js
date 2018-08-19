var jqXHR;

$("#myFile").fileupload({
    url: 'uploadDemo/doUpload',
    limitConcurrentUploads: 1,
    sequentialUploads: true,
    progressInterval: 100,
    maxChunkSize: 100000, //设置上传片段大小，不设置则为整个文件上传
    dataType: "json",
    add: function (e, data) {
        jqXHR = data.submit();
    }
}).bind('fileuploadprogress', function (e, data) {
    var progress = parseInt(data.loaded / data.total * 100, 10) - 1;
    $("#weixin_progress").css('width', progress + '%');
    $("#weixin_progress").html(progress + '%');
}).bind('fileuploaddone', function (e, data) {
    $("#weixin_progress").css('width', 100 + '%');
    $("#weixin_progress").html(100 + '%');
    /* $("#weixin_show").attr("src","resource/"+data.result); */
    $("#weixin_upload").css({display: "none"});
    $("#weixin_cancle").css({display: ""});
}).bind('fileuploadpaste', function (e, data) {
    alert("aaa");
}); //取消上传
function cancelUpload() {
    jqXHR.abort();
}

$(function () {

    $(".ui.actionmodal").modal({
        closable: false,
        onDeny: function () {
        },
        onApprove: function () {

        }
    });

});

function showMovieAdd() {
    $('#videoForm').modal("show");
}