(function () {
    $('body').append(
        '<div class="modal fade" id="alertModal" tabindex="-1" role="dialog" '
        + 'aria-labelledby="alertModalLabel" aria-hidden="true">'
        + '<div class="modal-dialog">'
        + '<div class="modal-content" style="width:400px;" >'
        + '<div class="modal-header" style="border: 0;">'
        + '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'
        + '<h4 class="modal-title" id="alertModalLabel">提示</h4>'
        + '</div>'
        + '<div class="modal-body" style="border: 0">在这里添加一些文本</div>'
        + '<div class="modal-footer" style="border: 0">'
        + '<div class="Alert">'
        + '<button type="button" id="AlertSureBtn" class="btn btn-primary" data-dismiss="modal">确定</button>'
        + '</div>'
        + '</div>'
        + '</div><!-- /.modal-content -->'
        + '</div><!-- /.modal -->'
        + '</div>');

    $('body').append(
        '<div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" '
        + 'aria-labelledby="confirmModalLabel" aria-hidden="true">'
        + '<div class="modal-dialog">'
        + '<div class="modal-content" style="width:400px;" >'
        + '<div class="modal-header" style="border: 0;">'
        + '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>'
        + '<h4 class="modal-title" id="confirmModalLabel">提示</h4>'
        + '</div>'
        + '<div class="modal-body" style="border: 0">在这里添加一些文本</div>'
        + '<div class="modal-footer" style="border: 0">'
        + '<div class="Confirm">'
        + '<button type="button" id="ConfirmCancelBtn"  class="btn btn-default" data-dismiss="modal">取消</button>'
        + '<button type="button" id="ConfirmSureBtn" class="btn btn-primary" data-dismiss="modal">确定</button>'
        + '</div>'
        + '</div>'
        + '</div><!-- /.modal-content -->'
        + '</div><!-- /.modal -->'
        + '</div>');

    $('#confirmModal').on('hidden.bs.modal', unbindEvent);
    $(document).on('show.bs.modal', '.modal', function(event) {
        $(this).appendTo($('body'));
    }).on('shown.bs.modal', '.modal.in', function(event) {
        setModalsAndBackdropsOrder();
    }).on('hidden.bs.modal', '.modal', function(event) {
        setModalsAndBackdropsOrder();
    });



})();

function setModalsAndBackdropsOrder() {
    var modalZIndex = 1040;
    $('.modal.in').each(function(index) {
        var $modal = $(this);
        modalZIndex++;
        $modal.css('zIndex', modalZIndex);
        $modal.next('.modal-backdrop.in').addClass('hidden').css('zIndex', modalZIndex - 1);
    });
    $('.modal.in:visible:last').focus().next('.modal-backdrop.in').removeClass('hidden');
}

function Alert(msg) {
    $('#alertModal')
        .find('.modal-title').html('提示框').end()
        .find('.modal-body').html(msg).end()
        .modal('show');
}

function Confirm(msg) {
    var $confirmModal = $('#confirmModal');
    var surFuc;
    $confirmModal
        .find('.modal-title').html('确认框').end()
        .find('.modal-body').html(msg).end()
        .find('#ConfirmCancelBtn').show().end()
        .modal('show');
    $confirmModal.find('#ConfirmSureBtn').bind('click', function () {
        if (surFuc != null) {
            surFuc();
        }
    });
    return {
        sure: function sureCb(func) {
            if (typeof func == 'function') {
                surFuc = func;
            }
        }
    }
}

function unbindEvent() {
    // console.log('unbind');
    $('#confirmModal').find('button').unbind();
}