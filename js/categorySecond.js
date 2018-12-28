$(function () {
    template.helper('getJquery',function () {
        return jQuery;
    });


var rend=function () {
    getCateData(function (data) {
      // var html=template('list',data);
       //console.log(html)
      //console.log(data.data)
        var str='';
        $.each(data.data,function (i,item) {
        str+='<tr>'+
            '<th scope="row">'+(i+1+(data.page-1)*data.size)+'</th>'+
            '<td>'+item.categoryId+'</td>'+
            '<td>'+item.brandName+'</td>'+
            '<td><img width="30" height="30" src="'+item.brandLogo+'" alt=""></td>'+
            '</tr>';
        });
        $('tbody').html(str);
    });
};
rend();
$('.ad_content .btn').on('click',function () {
   $('#save').modal('show');
});
});
var getCateData=function(callback){
$.ajax({
    url:'json/categorySecond/package.json',
    type:"get",
    dataType:"json",
    success:function (data) {
        callback&&callback(data);
    }
})
};
