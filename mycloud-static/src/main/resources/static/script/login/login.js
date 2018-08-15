$(function () {
    setValidateDefault();
    $("#loginForm").validate({
            rules: {
                username: {
                    required: true
                },
                password: {required: true, minlength: 5},
                captcha: 'required'
            },
            messages: {
                username: {required: '用户名不能为空'},
                password: {required: '密码不能为空'},
                captcha: {required: '请输入验证码'}
            },
            submitHandler: function (form) {
                $form = $(form);
                // var formData = $form.serialize(); //取表单值 并进行序列化；此时formData已经是乱码了
                // formData = decodeURIComponent(formData,true);//一次转码
                // formData = encodeURI(encodeURI(formData)); //两次转码
                $form.ajaxSubmit({
                    type: "post",
                    url: "/sso/login",
                    contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                    dataType: 'json',
                    data: {appCode: appCode, backUrl: backUrl},
                    xhrFields: {
                        withCredentials: true // 携带跨域cookie
                    },
                    //beforeSubmit: showRequest,
                    success: function (result) {
                        if (!result || result.code != 0) {
                            Alert(result.msg);
                            $('#imgCaptcha').trigger("click");
                            $('#inputCaptcha').val("");
                            return;
                        }
                        if(result.data){
                            backUrl = result.data;
                        }
                        if (backUrl) {
                            window.location.href = backUrl;
                        } else {
                            window.location.href = "/";
                        }
                    },
                    error: function () {
                        Alert("登录失败，请重新登录");
                        $('#imgCaptcha').trigger("click");
                        $('#inputCaptcha').val("");
                    }
                });
            }
        }
    );
});