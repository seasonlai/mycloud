function Video() {
    this.video = videojs('my-player', {
        controls: true,
        autoplay: false,
        preload: 'auto'
    });
    // this.source = document.getElementById('videoMP4');
    this.$modal = $('.ui.videomodal');
    this.modal_status = 0;
    this.$modal.on('show.bs.modal', function (e) {
        this.modal_status = 1;
        console.log('窗口打开');
    });
    this.$modal.on('hidden.bs.modal', function (e) {
        this.modal_status = 0;
        this.video.dispose();
        console.log('窗口关闭');
    });
}

Video.prototype = {
    constructor: Video,

    reload: function (src) {
        if (this.$modal.length == 0 || !this.video) {
            alert('没加载视频fragment')
            return;
        }
        this.video.pause();
        this.video.src(src);
        this.video.load();
        this.video.play();
        if (this.modal_status == 0) {
            this.$modal.modal({closable: false}).modal('show');
        }
        return this;
    },

    pause: function () {

        this.video.pause();
    },

    title: function (title) {
        this.$modal.find('.header').html(title);
        return this;
    }

};