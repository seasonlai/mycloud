$.fn.calendar.settings = $.extend(
    $.fn.calendar.settings,
    {
        text: {
            days: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
            months: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
            monthsShort: ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月'],
            today: '今天',
            now: '现在',
            am: '上午',
            pm: '下午'
        }
    }
);


$.fn.calendar.settings.formatter = $.extend(
    $.fn.calendar.settings.formatter,
    {
        date: function (date, settings) {
            if (!date) return '';

            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();

            month = month < 10 ? '0' + month : month;
            day = day < 10 ? '0' + day : day;
            return year + '-' + month + '-' + day;
        }
    }
);


