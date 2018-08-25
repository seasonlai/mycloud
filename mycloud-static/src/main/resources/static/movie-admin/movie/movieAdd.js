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
    onSuccess: function (e) {
        //阻止表单的提交
        e.preventDefault();

        submitMovie();
    },
    onValid: function () {
        //
        console.log('onValid');
    },
    onFailure: function () {
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
//提交表单
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
//图片裁剪
function initUploadImg() {
    var console = window.console || {
            log: function () {
            }
        };
    var URL = window.URL || window.webkitURL;

    var $image = $('#movieCropImg');
    var options = {
        aspectRatio: 1.38,
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
        // ready: function (e) {
        //     console.log(e.type);
        // },
        // crop: function (e) {
        //     console.log(e.type);
        // },
        // zoom: function (e) {
        //     console.log(e.type, e.detail.ratio);
        // }
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
                    var suffixIndex = uploadedImageName.lastIndexOf(".");
                    if (suffixIndex >= 0) {
                        uploadedImageType = uploadedImageName.substring(suffixIndex + 1);
                    } else {
                        uploadedImageType = uploadedImageType.substring(6);
                    }
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
                            //开始上传图片
                            var canvas = $image.cropper('getCroppedCanvas', {width: 200, height: 200});
                            var canvas2 = $image.cropper('getCroppedCanvas', {width: 386, height: 200});
                            var data = canvas.toDataURL();
                            var data2 = canvas2.toDataURL();
                            $.ajax({
                                url: '/file/uploadImg',
                                dataType: 'json',
                                type: "POST",
                                contentType: 'application/json; charset=UTF-8',
                                data: JSON.stringify({
                                    "imgData": data.toString(),
                                    'imgData2': data2.toString(),
                                    "fileName": 'cover.' + uploadedImageType
                                }),
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
