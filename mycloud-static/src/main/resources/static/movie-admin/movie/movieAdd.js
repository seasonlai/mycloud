//因为和jquery.ui.js有同名函数，要立刻执行
'use strict';
$('#movieForm').form({
    inline: true,
    on: 'blur',
    fields: {
        movieName: {
            identifier: 'movieName',
            rules: [{
                type: 'empty',
                prompt: '电影名称不能为空'
            }]
        },
        movieKind: {
            identifier: 'movieKind',
            rules: [{
                type: 'minCount[1]',
                prompt: '至少选择一个类别'
            }]
        },

        showTime: {
            identifier: 'showTime',
            rules: [{
                type: 'empty',
                prompt: '请上映日期'
            }]
        },
        price: {
            identifier: 'price',
            rules: [{
                type: 'empty',
                prompt: '请输入价格'
            }, {
                type: 'decimal',
                prompt: '请输入正确的格式'
            }]
        }
    },
    onSuccess:function (e) {
        //阻止表单的提交
        e.preventDefault();

        submitMovie();
    },
    onValid: function(){
        //
        console.log('onValid');
    },
    onFailure: function(){
        //
        console.log('onFailure');
    }
});


$(function () {
    $("#datepicker").datepicker({
            changeMonth: true,
            changeYear: true
        }
    );

    initUploadImg();

});

function submitMovie() {
    var $form = $('#movieForm');
    $form.ajaxSubmit({
        type: "post",
        url: "/admin/addMovie",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        dataType: 'json',
        // data: {appCode: appCode, backUrl: backUrl},
        success: function (result) {
            if (commonResultHandle(result)) {
                alert('添加成功');
                window.location.reload();
            }
        },
        error: function () {
            alert("请求失败");
        }
    });
}

function initUploadImg() {
    var console = window.console || {
            log: function () {
            }
        };
    var URL = window.URL || window.webkitURL;

    var $image = $('#movieCropImg');
    var options = {
        aspectRatio: 1,
        preview: '.img-preview',
        resizable: false,
        crop: function (e) {
        }
    };
    var originalImageURL = $image.attr('src');
    var uploadedImageName = 'cover.jpg';
    var uploadedImageType = 'image/jpeg';
    var uploadedImageURL;

    // Cropper
    $image.on({
        ready: function (e) {
            console.log(e.type);
        },
        crop: function (e) {
            console.log(e.type);
        },
        zoom: function (e) {
            console.log(e.type, e.detail.ratio);
        }
    }).cropper(options);

    // Import image
    var $inputImage = $('#inputImg');

    if (URL) {
        $inputImage.change(function () {
            var files = this.files;
            var file;

            if (!$image.data('cropper')) {
                return;
            }

            if (files && files.length) {
                file = files[0];
                var fileSize = file.size;
                if (fileSize > 1024 * 1024) {
                    alert("文件不得大于一兆")
                    return;
                }
                if (/^image\/\w+$/.test(file.type)) {
                    uploadedImageName = file.name;
                    uploadedImageType = file.type;
                    if (uploadedImageURL) {
                        URL.revokeObjectURL(uploadedImageURL);
                    }

                    uploadedImageURL = URL.createObjectURL(file);
                    $image.cropper('destroy').attr('src', uploadedImageURL).cropper(options);
                    $inputImage.val('');

                    $(".ui.actionmodal").modal({
                        closable: false,
                        onDeny: function () {
                        },
                        onApprove: function () {
                            //开始上传
                            var canvas = $image.cropper('getCroppedCanvas', {width: 200, height: 200});
                            var data = canvas.toDataURL();
                            $.ajax({
                                url: '/file/uploadImg',
                                dataType: 'json',
                                type: "POST",
                                data: {
                                    "imgData": data.toString(),
                                    "filename": 'cover'+uploadedImageType
                                },
                                success: function (data) {
                                    if (!data) {
                                        alert("上传失败");
                                        return;
                                    }
                                    if (data.code !== 0) {
                                        alert(data.msg);
                                        return;
                                    }
                                    $("#uploadImg").attr("src", data.data);
                                    $('#movieImg').val(data.data);
                                    $(".ui.actionmodal").modal('hide');
                                },
                                error: function () {
                                    alert('上传失败')
                                }
                            });
                        }
                    }).modal("show");
                } else {
                    window.alert('请选择图片文件.');
                }
            }
        });
    } else {
        $inputImage.prop('disabled', true).parent().addClass('disabled');
    }
}


//
// $('#uploadImg').fileupload({
//     url: '/admin/uploadImg',//请求发送的目标地址
//     Type: 'POST',//请求方式 ，可以选择POST，PUT或者PATCH,默认POST
//     dataType: 'json',//服务器返回的数据类型
//     autoUpload: true,
//     acceptFileTypes: /(gif|jpe?g|png)$/i,//验证图片格式
//     maxNumberOfFiles: 1,//最大上传文件数目
//     maxFileSize: 1000000, // 文件上限1MB
//     minFileSize: 100,//文件下限  100b
//     messages: {//文件错误信息
//         acceptFileTypes: '文件类型不匹配',
//         maxFileSize: '文件过大，要求小于1M',
//         minFileSize: '文件过小，要求大于100b'
//     }
// })
// //图片添加完成后触发的事件
//     .on("fileuploadadd", function (e, data) {
//         //validate(data.files[0])这里也可以手动来验证文件格式和大小
//         switchUploadImgBtn(false);
//     })
//     //当一个单独的文件处理队列结束触发(验证文件格式和大小)
//     .on("fileuploadprocessalways", function (e, data) {
//         //获取文件
//         var file = data.files[0];
//         //获取错误信息
//         if (file.error) {
//             console.log(file.error);
//             $("#uploadFile").hide();
//         }
//     })
//     //显示上传进度条
//     .on("fileuploadprogressall", function (e, data) {
//         var $progress = $('#progress');
//         $progress.show();
//         var progress = parseInt(data.loaded / data.total * 100, 10);
//         $progress.css(
//             'width', '15%'
//         );
//         $progress.find('.bar').css(
//             'width', progress + '%'
//         );
//     })
//     //上传请求失败时触发的回调函数
//     .on("fileuploadfail", function (e, data) {
//         console.log(data.errorThrown);
//     })
//     //上传请求成功时触发的回调函数
//     .on("fileuploaddone", function (e, data) {
//         var result = data.result;
//         alert(result.msg);
//         if (result.code === 0) {
//             //获取图片路径并显示
//             $("#movieImg").attr("src", result.data);
//         }
//     })
//     //上传请求结束后，不管成功，错误或者中止都会被触发
//     .on("fileuploadalways", function (e, data) {
//         switchUploadImgBtn(true);
//     });
//
// function switchUploadImgBtn(show) {
//     var $progress = $('#progress');
//     var $uploadImgBtn = $("#uploadImgBtn");
//     if (show) {
//         $uploadImgBtn.show();
//         $progress.hide();
//     } else {
//         $uploadImgBtn.hide();
//         $progress.find('.bar').css(
//             'width', '0%'
//         );
//         $progress.css("display", "inherit");
//     }
//
// }
//
// //手动验证
// function validate(file) {
//     //获取文件名称
//     var fileName = file.name;
//     //验证图片格式
//     if (!/.(gif|jpg|jpeg|png|gif|jpg|png)$/.test(fileName)) {
//         console.log("文件格式不正确");
//         return true;
//     }
//     //验证excell表格式
//     /*  if(!/.(xls|xlsx)$/.test(fileName)){
//      alert("文件格式不正确");
//      return true;
//      } */
//
//     //获取文件大小
//     var fileSize = file.size;
//     if (fileSize > 1024 * 1024) {
//         alert("文件不得大于一兆")
//         return true;
//     }
//     return false;
// }
//
// //获取图片地址
// function getUrl(file) {
//     var url = null;
//     if (window.createObjectURL != undefined) {
//         url = window.createObjectURL(file);
//     } else if (window.URL != undefined) {
//         url = window.URL.createObjectURL(file);
//     } else if (window.webkitURL != undefined) {
//         url = window.webkitURL.createObjectURL(file);
//     }
//     return url;
// }
