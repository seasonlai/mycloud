$(function () {
    setValidateDefault();
    $("#registerForm").validate({
            rules: {
                username: {required: true,
                    minlength: 2,
                    remote:{
                        type: "post",
                        url: "/register/judgeUser",
                        data: {
                            username: function() {
                                return $("#inputUser").val();
                            }
                        },
                        dataType: "html",
                        dataFilter: function(data, type) {
                            if (data == "true")
                                return true;
                            else
                                return false;
                        }
                    }
                },
                password: {required: true, minlength: 5},
                password2: {equalTo: "#inputPassword"},
                captcha: 'required'
            },
            messages: {
                username:{required:'用户名不能为空',remote:'用户名已存在'},
                password:{required:'密码不能为空'},
                password2: {equalTo: "两次密码输入不一致"},
                captcha:{required:'请输入验证码'}
            },
            submitHandler:function(form){
                $form = $(form);
                // var formData = $form.serialize(); //取表单值 并进行序列化；此时formData已经是乱码了
                // formData = decodeURIComponent(formData,true);//一次转码
                // formData = encodeURI(encodeURI(formData)); //两次转码
                $form.ajaxSubmit({
                    type:"post",
                    url: $form.attr('action'),
                    contentType : "application/x-www-form-urlencoded; charset=UTF-8",
                    dataType:'json',
                    //beforeSubmit: showRequest,
                    success: function (result) {
                        if(!result||result.code!=0){
                            // $('.modal-body').html(result.msg);
                            // $('#myModal').modal('show');
                            Alert(result.msg);
                            return;
                        }

                        if(backUrl){
                            window.location.href=backUrl;
                        }else {
                            /*<![CDATA[*/
                            window.location.href=homeUrl;
                            /*]]>*/
                        }
                    },
                    error:function (data) {
                        alert(data);
                    }
                });
            }
        }
    );
});