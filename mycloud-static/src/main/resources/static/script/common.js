function getScrollTop() {
    try {
        var scrollTop = document.body.scrollTop || document.documentElement.scrollTop;
    } catch (e) {

    }
    return scrollTop;
}
function isEmpty(obj) {
    if (obj == undefined || obj == null || obj == '') {
        return true;
    }
    return false;
}
//刷新图片
function refreshImg(imgObj) {
    if (!imgObj) {
        return;
    }
    var $img = $(imgObj);
    var urlPath = $img.attr('src');
    urlPath && $img.attr('src', urlPath + "?t=" + Math.random()); //显示图片
}
//表单验证设置
function setValidateDefault(options) {
    $.validator.setDefaults($.extend({
        unhighlight: function (element, errorClass, validClass) { //验证通过
            if (!$(element).parent().hasClass(validClass))
                $(element).addClass(validClass).popover('destroy');
        },
        highlight: function (element, errorClass, validClass) {
            if ($(element).hasClass(validClass))
                $(element).removeClass(validClass);
        },
        errorPlacement: function (error, element) {
            if (!$(element).hasClass("valid")) {
                $(element).attr("data-content",
                    $(error).text()).popover("show");
            }
        },
        onkeyup: false
        // onfocusout:false
        // errorClass: "has-error"
    }, options || {}));
}
//分页设置
function getPaginationDefaultOpt(options) {
    return $.extend({
        first: '首页',
        prev: '上一页',
        next: '下一页',
        last: '尾页',
    }, options || {});
}

function formToJsonObj(form) {
    var formObject = {};
    if (!form) {
        return formObject;
    }
    if (!(form instanceof jQuery)) {
        form = $(form);
    }
    var formArray = form.serializeArray();
    $.each(formArray, function (i, item) {
        if (item.value)
            formObject[item.name] = item.value;
    });
    return formObject;
}

function modalHiddenEvent(id) {
    var $modal = $('#' + id);
    if (!$modal) {
        return
    }
    $modal.on('hidden.bs.modal',function () {
        $modal.find('[data-container="body"]').popover('destroy');
    })
}