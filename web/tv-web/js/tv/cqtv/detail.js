(function () {
   console.log("2333");
    _tvFunc.waitForVideoElement().then(video => {
        if (video == null) {
            _apiX.msg("tobackup", "{}");
        }
        else {

            _tvFunc.waitForVideoPlay(video).then(isplay => {
                if (isplay) {
                    _tvFunc.fixedW("body");
                    _tvFunc.fullscreen("video");
                    //_apiX.hideMask(300);
                    _tvFunc.volume100();
                }
                else {
                    _apiX.msg("tobackup", "{}");
                }
            });

        }
    });

})();
