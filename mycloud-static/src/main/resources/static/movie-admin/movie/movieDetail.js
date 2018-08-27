function initFormValidate() {
    $('#movieForm').form({
        inline: true,
        on: 'blur',
        fields: {
            movieName: {
                identifier: 'movieName',
                rules: [{
                    type: 'empty',
                    prompt: '电影名称不能为空'
                }]
            },
            movieKind: {
                identifier: 'movieKind',
                rules: [{
                    type: 'minCount[1]',
                    prompt: '至少选择一个类别'
                }]
            },

            showTime: {
                identifier: 'showTime',
                rules: [{
                    type: 'empty',
                    prompt: '请上映日期'
                }]
            },
            price: {
                identifier: 'price',
                rules: [{
                    type: 'empty',
                    prompt: '请输入价格'
                }, {
                    type: 'decimal',
                    prompt: '请输入正确的格式'
                }]
            }
        },
        onSuccess: function (e) {
            //阻止表单的提交
            e.preventDefault();

            updateMovie();
        },
        onValid: function () {
            //
            console.log('onValid');
        },
        onFailure: function () {
            //
            console.log('onFailure');
        }
    });
}


function initChangeCover() {
    initUploadImg('inputImg', function (data1, data2, suffix) {
        $.ajax({
            url: '/file/uploadImg',
            dataType: 'json',
            type: "POST",
            contentType: 'application/json; charset=UTF-8',
            data: JSON.stringify({
                "imgData": data1.toString(),
                'imgData2': data2.toString(),
                "fileName": 'cover.' + suffix
            }),
            success: function (data) {
                if (!data) {
                    alert("上传失败");
                    return;
                }
                if (data.code !== 0) {
                    alert(data.msg);
                    return;
                }

                $.post('/admin/updateCover',
                    {
                        id: movieId,
                        cover: data.data.fileName
                    }, function (result) {
                        $("#uploadImg").attr("src", imgUrl + result.data);
                    }, 'json'
                ).error(function () {
                    alert('请求失败');
                });

            },
            error: function () {
                alert('上传失败')
            }
        });
    });
}


function updateMovie() {
    var $form = $('#movieForm');
    $form.ajaxSubmit({
        type: "post",
        url: "/admin/updateMovie",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        dataType: 'json',
        success: function (result) {
            if (commonResultHandle(result)) {
                alert('修改成功');
            }
        },
        error: function () {
            alert("请求失败");
        }
    });
}


$(function () {

    $(".datepicker").datepicker({
            changeMonth: true,
            changeYear: true
        }
    );
    initChangeCover();
    initFormValidate();
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
                    + '<td><a onclick="previewVideo(video.code)">' + video.name + '</a></td>'
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
//确认提交关联视频
function submitMovieVideo() {
    var $check = $('input[name="videoCheck"]');
    if ($check.length == 0) {
        alert('请选择一条记录');
        return;
    }
    var checkId = $check.val();
    for (var i in videoCache) {
        var video = videoCache[i];
        if (video.id == checkId || video.id + '' == checkId) {
            $.post('/admin/updateMV', {
                movieId: movieId,
                videoId: video.id
            }, function (result) {
                if (commonResultHandle(result)) {
                    alert('操作成功');
                    $('#joinVideo').unbind()
                        .click(function () {
                            previewVideo(video.code);
                        })
                        .text(video.name);
                }
            }).error(function () {
                alert('请求失败')
            });
            return;
        }
    }
}

function previewVideo(code) {
    $('.ui.videomodal').modal('show');
}
