
(function(){
    _tvFunc.fixedW("body");
    _detailInit(null,999990,true);
    _tvFunc.check(function (){return $$(".item-wp").length>0},function (){
        let url = window.location.href;
        let index= url.indexOf("tag=");
        let tag = url.substring(index+4,url.length);
        console.log(tag);
        let currentTag=0;
        $$(".item-wp").each(function (i,item){
            let active=  $$(item).hasClass("active");
            if(active){
                currentTag=i;
            }
        });
        console.log(currentTag);
        if(Number(tag)!==currentTag){
            $$(".item-wp")[Number(tag)].click();
        }
        _tvFunc.check(function (){return document.getElementsByTagName("video").length>0;},function (){
            console.log("video found");
            //_tvFunc.addKeyFullScreen(document.getElementsByTagName("video")[0]);
            // _detailHz();
            $$("#live-player").children().css({width:"100%",height:"100%"});
            $$("#live-player").children().children().css({width:"100%",height:"100%"});
            _tvFunc.fullscreenW("#live-player");
            // _tvFunc.fullscreen("#live-player");
            //$$("video").css("position","fixed !important");
            //$$("video").css("position","unset");
        });
    });


    let _app={
        init(){
            _tvFunc.check(function(){return  document.getElementsByTagName("video").length>0;},function(index){
                //全屏
                // let menuId = _detailInit(null,999990,true);
                _detailHz();
            },1000);
        }
    };
    _app.init();


})();

let _data={
    vue:null,
    initData(vue){
        this.vue=vue;
        this.vue.video=false;
        this.fullscreen();
    },
    fullscreen(){
        //$$("#player_pagefullscreen_msg_player").click();
        //音量100
        _tvFunc.volume100();
        _tvFunc.videoReady(function (video){
            if(video.paused){
                video.play();
            }
        })
    }
};