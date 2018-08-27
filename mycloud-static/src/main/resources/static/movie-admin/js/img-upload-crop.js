

//图片裁剪
function initUploadImg(inputId, callback) {

    var URL = window.URL || window.webkitURL;

    var $image = $('#movieCropImg');
    if ($image.length == 0) {
        alert('未引入裁剪窗口');
        return;
    }
    // Import image
    var $inputImage = $('#' + inputId);
    if ($inputImage.length == 0) {
        alert('id为' + inputId + '的元素不存在');
        return;
    }
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

                    var $modal = $(".ui.cropmodal");
                    $modal.modal({
                        closable: false,
                        onDeny: function () {
                        },
                        onApprove: function () {
                            //开始上传图片
                            if (typeof callback == 'function') {
                                var canvas = $image.cropper('getCroppedCanvas', {width: 200, height: 200});
                                var canvas2 = $image.cropper('getCroppedCanvas', {width: 386, height: 200});
                                var data = canvas.toDataURL();
                                var data2 = canvas2.toDataURL();
                                callback(data,data2,uploadedImageType);
                            }
                            $modal.modal('hide');
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
