$(function(){
    $('#login').bootstrapValidator(
        {
            feedbackIcons: {
                valid: 'glyphicon glyphicon-ok',
                invalid: 'glyphicon glyphicon-remove',
                validating: 'glyphicon glyphicon-refresh'
            },
            fields:{
                username:{
                    validators:{
                        notEmpty:{
                            message:'用户名不能为空'
                        },
                        callback:{
                            message:'用户名错误'
                        }
                    }
                },
                password:{
                    validators: {
                        notEmpty: {
                            message:'密码不能为空'
                        },
                        stringLength:{
                            min:6,
                            max:18,
                            message:'密码必须在6到18个字符间'
                        },
                        callback: {
                            message:'密码错误'
                        }
                    }
                }
            }
    }).on('success.form.bv', function (e) {
        e.preventDefault();
        var $form=$(e.target);
        console.log($form.serialize());
        $.ajax({
            url:'json/login/package.json',
            type:'post',
            dataType:'json',
            data:$form.serialize(),
            success:function (data) {
                //console.log(data)
                if (data.success==true){
                    location.href='index.html';
                }else{
                    if(data.error==1000){
                        $form.data('bootstrapValidator').updateStatus('username','INVALID','callback');
                    }else if(data.error==1001){
                        $form.data('bootstrapValidator').updateStatus('password','INVALID','callback');
                    }
                }
            }
        })
    });

});