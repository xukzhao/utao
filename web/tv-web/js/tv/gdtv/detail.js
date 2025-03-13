
(function(){
    _tvFunc.fixedW("body");
    _tvFunc.check(function (){return document.getElementsByTagName("video").length>0;},function (){
        console.log("video found")
        // document.getElementsByTagName("video")[0].classList.add("utv-video-full");
        _layer.open(`<div></div>`, 9980, null, "tv-index", "tv-bg-black");
        _tvFunc.fullscreen("video");
        //$$("video").css("position","fixed !important");
        $$("video").css({position:"fixed !important",margin:"auto",width:"auto !important"});
        $$(".head-nav").hide();
    });
    _tvFunc.volume100();
    _tvFunc.videoReady(function (video){
        if(video.paused){
            video.play();
        }
    })

})();
