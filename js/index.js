$(function () {
barCharts();
pieCharts();
});
var barCharts=function(){
    var data = [
        {
            name:'一月',
            value:300
        },
        {
            name:'二月',
            value:400
        },
        {
            name:'三月',
            value:500
        },
        {
            name:'四月',
            value:200
        },
        {
            name:'五月',
            value:600
        }
    ];
    var xdata=[],ydata=[];
    data.forEach(function (item,i) {
       xdata.push(item.name);
       ydata.push(item.value);
    });
    console.log(ydata)
    //var box = document.querySelector('.picTable:first-child');
    var box=document.getElementsByClassName('picTable')[0];
    console.log(box);
    var myChart = echarts.init(box);
    /*4.配置参数*/
    var options = {
        title:{
            text:'2017年注册人数'
        },
        legend:{
            data:['注册人数']
        },
        tooltip : {
        },
        xAxis : [
            {
                data : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            }
        ],
        yAxis : [
            {
                type : 'value'
            }
        ],
        series : [
            {
                name:'注册人数',
                type:'bar',
                barWidth: '60%',
                data:[10, 52, 200, 334, 390, 330, 220]
            }
        ]
    }
    /*5.设置参数*/
    options.xAxis[0].data=xdata;
    options.series[0].data=ydata;
    myChart.setOption(options);
};
var pieCharts = function () {
    /*1.引入echarts.min.js文件*/
    /*2.找到画图的容器*/
    var box = document.querySelector('.picTable:last-child');
    /*3.初始化插件*/
    var myChart = echarts.init(box);
    /*4.配置参数*/
    var options = {
        title : {
            text: '品牌销售占比',
            subtext: '2017年10月',
            x:'center'
        },
        tooltip : {
            trigger: 'item',
            /*series.name  a  */
            /*data.name  b */
            /*data.value  c */
            /*占比  d */
            formatter: "{b} : {c} ({d}%)"
        },
        legend: {
            orient: 'vertical',
            left: 'left',
            data: ['李宁','耐克','阿迪','匡威','回力']
        },
        series : [
            {
                name: '销售情况',
                type: 'pie',
                radius : '55%',
                center: ['50%', '60%'],
                data:[
                    {value:335, name:'李宁'},
                    {value:310, name:'耐克'},
                    {value:234, name:'阿迪'},
                    {value:135, name:'匡威'},
                    {value:1548, name:'回力'}
                ],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                }
            }
        ]
    }
    /*5.设置参数*/
    myChart.setOption(options);
};