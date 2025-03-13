(function(){
    _tvFunc.fixedW("body");
    _tvFunc.videoReady(function (video){
        _tvFunc.fullscreen("video");
        $$("video").css("position","fixed !important");
        $$(".head-nav").hide();
        if(video.paused){
            video.play();
        }
        _tvFunc.volume100();
    });
})();