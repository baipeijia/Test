NProgress.configure({
    showSpinner: false
});
$(window).ajaxStart(function(){
    NProgress.start();
});
/*在ajax结束请求的时候  把进度条完成*/
$(window).ajaxStart(function(){
    NProgress.done();
});
$('[data-menu]').on('click',function(){
    $('.ad_aside').toggle();
    $('.ad_section').toggleClass('menu');
});
$('.menu [href="javascript:;"]').on('click',function () {
   $(this).siblings('.child').slideToggle();
})
var modalHtml = ['<div class="modal fade" id="logoutModal">',
    '    <div class="modal-dialog modal-sm">',
    '        <div class="modal-content">',
    '            <div class="modal-header">',
    '                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>',
    '                <h4 class="modal-title">温馨提示</h4>',
    '            </div>',
    '            <div class="modal-body">',
    '                <p class="text-danger"><span class="glyphicon glyphicon-info-sign"></span>您确定要退出后台管理系统吗？</p>',
    '            </div>',
    '            <div class="modal-footer">',
    '                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>',
    '                <button type="button" class="btn btn-primary">确定</button>',
    '            </div>',
    '        </div>',
    '    </div>',
    '</div>'].join("");
$('body').append(modalHtml);
$('[data-logout]').on('click',function () {
    var $logoutModal=$('#logoutModal')
    $logoutModal.modal('show').find('.btn-primary').on('click',function () {
        $.ajax({
            url:'json/login/package.json',
            type:'get',
            dataType:'json',
            success:function (data) {
                console.log(data)
                if(data.success==true){
                    $logoutModal.modal('hide');
                    location.href='login.html'
                }
            }
        })
    });
});