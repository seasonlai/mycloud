function getFileMd5(data) {

    var file = data.file;
    if (!file) {
        if (typeof error == 'function')
            error('文件参数为空');
        return;
    }
    var success, error, progress, always;
    var chunkSize = data.chunkSize || 8097152;
    var tmp_md5;
    var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
        chunks = Math.ceil(file.size / chunkSize),
        currentChunk = 0,
        spark = new SparkMD5.ArrayBuffer(),
        fileReader = new FileReader();

    fileReader.onload = function (e) {
        // console.log('read chunk nr', currentChunk + 1, 'of', chunks);
        spark.append(e.target.result); // Append array buffer
        currentChunk++;
        var md5_progress = Math.floor((currentChunk / chunks) * 100);
        if (typeof progress == 'function') {
            progress(md5_progress);
        }
        if (currentChunk < chunks) {
            loadNext();
        } else {
            tmp_md5 = spark.end();
            if (typeof success == 'function') {
                success(tmp_md5);
            }
            if (typeof always == 'function')
                always();
        }
    };

    fileReader.onerror = function () {
        if (typeof error == 'function')
            error('文件操作失败');
        if (typeof always == 'function')
            always();
    };

    function loadNext() {
        var start = currentChunk * chunkSize,
            end = ((start + chunkSize) >= file.size) ? file.size : start + chunkSize;
        fileReader.readAsArrayBuffer(blobSlice.call(file, start, end));
    }

    loadNext();

    return {
        success: function (sucFuc) {
            success = sucFuc;
            return this;
        },
        error: function (errFunc) {
            error = errFunc;
            return this;
        },
        progress: function (proFunc) {
            progress = proFunc;
            return this;
        },
        always: function (alwFunc) {
            always = alwFunc;
            return this;
        }
    }
}

function getBetterFileSize(size) {
    if (!size) {
        return;
    }
    try {
        size = new Number(size);
    } catch (e) {
        return;
    }
    var unit = ['B', 'KB', 'MB', 'GB', 'TB'];
    var count = 0;
    while (count < 4 && size >= 1024) {
        size = size / 1024;
        count++;
    }
    return numberFormat(size, 2, ".", "") + unit[count];
}