function queryList(pageNum, pageSize, init) {
    $.ajax({
        url: '/admin/movieList',
        type: 'post',
        data: {pageNum: pageNum, pageSize: pageSize},
        success: function (result) {
            if (commonResultHandle(result)) {
                var movies = result.data.list;
                var total = result.data.total;
                if (init) {
                    initPagination(total);
                }
                var $movieList = $("#movieList").empty();
                var cardsWrap;
                //开始添加列表
                for (var i in movies) {
                    var movie = movies[i];
                    if (i % 5 === 0) {
                        $movieList.append(cardsWrap);
                        cardsWrap = $('<div class="ui five stackable cards">');
                    }
                    cardsWrap.append('<div class="ui card">'
                        + '<div class="image">'
                        + '<img src="' + imgUrl + (movie.cover?movie.cover:'image.png') + '"'
                        // + 'style="width: 150px;height: 150px"'
                        + 'class="transition visible">'
                        // + 'onerror="this.src="'+imgUrl+'image.png'+'";this.onerror=null"'
                        + '<div class="content">'
                        + '<a class="header">' + movie.name + '</a>'
                        + '<div class="meta">'
                        + '<span class="date">' + movie.showYear + '</span>'
                        + '</div>'
                        + '<div class="description">'
                        + 'xxx'
                        + '</div>' + '</div>'
                        + '<div class="extra content">'
                        + '<a>'
                        + '<i class="user icon"></i> 185 Friends'
                        + '</a>'
                        + '</div>'
                        + '</div>'
                        + '</div>'
                    )
                }
                if (cardsWrap.children().length > 0) {
                    $movieList.append(cardsWrap);
                }
            }
        },
        error: function () {
            alert('请求失败');
        }
    })

}

$(function () {
    queryList(1, opts.items_per_page, true);
});

var opts = {
    prev_text: '<i class="left chevron icon"></i>',
    next_text: '<i class="right chevron icon"></i>',
    item_class: 'item',//item的class
    active_class: "active",
    pre_class: 'icon',
    next_class: 'icon',
    items_per_page: 10,
    prev_show: false,
    next_show: false,
    callback: function (pageIndex) {
        queryList(pageIndex + 1, opts.items_per_page);
    }
};

function initPagination(totalPage) {
    $("#pagination").pagination(totalPage, opts);
}
