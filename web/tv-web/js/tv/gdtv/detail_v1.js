
(function(){
    //_tvFunc.fixedW("body");
    _tvFunc.check(function (){return document.getElementsByTagName("video").length>0;},function (){
        console.log("video found")
         document.getElementsByTagName("video")[0].classList.add("utv-video-full");
        _layer.open(`<div></div>`, 9980, null, "tv-index", "tv-bg-black");
       // _tvFunc.addKeyFullScreen(document.getElementById("video_html5_api"));
       // _apiX.msgStr("key","F");
        //_tvFunc.fullscreen("#video");
        //$$("video").css({width:"auto !important"});
        //$$("video").css("margin-left","none !important");
        //$$("video").css("position","fixed !important");
       // $$("video").css({position:"fixed !important",margin:"auto",width:"auto !important"});
        $$(".head-nav").hide();
    });
    _tvFunc.check(function (){
        let  gdUrl =  sessionStorage.getItem("gdUrl");
        console.log("gdUrl"+gdUrl);
        return  null!=gdUrl;},function (){
       let  gdUrl =  sessionStorage.getItem("gdUrl");
       console.log("gdUrl"+gdUrl);
    });
    _tvFunc.volume100();
    _tvFunc.videoReady(function (video){
        if(video.paused){
            video.play();
        }
    })

})();
