kinds = kinds || [];

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
                        + '<img src="' + imgUrl + (movie.cover ? movie.cover : 'image.png') + '"'
                        + 'class="transition visible">'
                        + '<div class="content" style="padding: 5px 5px;">'
                        + '片名：<a class="header">' + movie.name + '</a>'
                        + '<div class="description">类型：'
                        + getKindName(movie.kinds)
                        + '</div>'
                        + '<div class="description">'
                        + '<div>'
                        + '价格：￥' + movie.price
                        + '</div>'
                        + '</div>'
                        + '<div class="description">'
                        + '上映时间：' + formatDate(movie.showYear)
                        + '</div>'
                        + '</div>'
                        + '</div>'
                        + '</div>'
                    )
                }
                if (cardsWrap && cardsWrap.children().length > 0) {
                    $movieList.append(cardsWrap);
                }
            }
        },
        error: function () {
            alert('请求失败');
        }
    })
}

function formatDate(date) {
    if (!date)
        return '--';
    return date.substring(0, date.indexOf(' '));
}

function getKindName(key) {

    var result = '';
    if (key) {
        for (var i in kinds) {
            var kind = kinds[i];
            if (key.indexOf(kind.id) >= 0) {
                result += kind.name + ',';
            }
        }
    }
    return result == '' ? '--' : result.substring(0, result.length - 1);
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
