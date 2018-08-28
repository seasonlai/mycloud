$(function () {
    $(".datepicker").datepicker({
            changeMonth: true,
            changeYear: true
        }
    );
    refreshTable(1, opts.items_per_page, true);//刷新视频列表
});

var videoCache;
var pageNumCache, pageSizeCache;
function refreshTable(pageNum, pageSize, init) {
    $.post('/video/queryPage', {
        pageNum: pageNum || pageNumCache,
        pageSize: pageSize || pageSizeCache
    }, function (result) {
        var $videoTable = $('#videoTbBody');
        $videoTable.empty();
        if (commonResultHandle(result)) {
            var data = result.data;
            var page = data.page;
            var list = data.list;
            videoCache = list;
            pageNumCache = pageNum;
            pageSizeCache = pageSize;
            if (init) {
                var total = page.total;
                initPagination(total);
            }
            for (var i in list) {
                var video = list[i];
                $videoTable.append(
                    '<tr>'
                    + '<td><input type="radio" name="videoCheck" value="' + video.id + '"></td>'
                    + '<td><a style="cursor: pointer" onclick="previewVideo(\''+video.code+'\',\''+video.name+'\')">'
                    + video.name + '</a></td>'
                    + '<td>' + video.qualityName + '</td>'
                    + '<td>' + video.createTime + '</td>'
                    + '<td>' + video.userName + '</td>'
                    + '</tr>'
                );
            }
        }
    }, 'json')
}

var opts = {
    prev_text: '<i class="left chevron icon"></i>',
    next_text: '<i class="right chevron icon"></i>',
    item_class: 'item',//item的class
    active_class: "active",
    pre_class: 'icon',
    next_class: 'icon',
    items_per_page: 10,
    // prev_show: false,
    // next_show: false,
    callback: function (pageIndex) {
        refreshTable(pageIndex + 1, opts.items_per_page);
    }
};
//初始化分页
function initPagination(totalPage) {
    $("#pagination").pagination(totalPage, opts);
}
var video = new Video();
function previewVideo(code,title) {
    if (!code || code === '') {
        return;
    }
    video.reload("/videos/" + code).title(title);
}
