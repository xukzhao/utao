
(function(){
    _tvFunc.fixedW("body");
    _detailInit(null,999990,true);
    _tvFunc.check(function (){return $$(".fs-live-banner-cluster-schedule-station").length>0},function (){
        let url = window.location.href;
        let index= url.indexOf("tag=");
        let tag = url.substring(index+4,url.length);
        console.log(tag);
        let currentTag=0;
        $$(".fs-live-banner-cluster-schedule-station").each(function (i,item){
            let active=  $$(item).hasClass("is-active");
            if(active){
                currentTag=i;
            }
        });
        console.log(currentTag);
        if(Number(tag)!==currentTag){
            $$(".fs-live-banner-cluster-schedule-station")[Number(tag)].click();
        }
        _tvFunc.videoReady(function (){
            $$(".vjs-error-modal").hide();
            $$(".fs-cookies-dialog-ok-btn").click();
            $$(".fs-video").addClass("utv-video-full");
            $$(".vjs-controlbar").hide();
            if($$(".vjs-icon-muted").length>0){
                $$(".vjs-icon-muted").click();
            }
        });
        /*_tvFunc.check(function (){return document.getElementsByTagName("video").length>0;},function (){
            //document.getElementsByTagName("video")[0].classList.add("utv-video-full");
            //_tvFunc.fullscreen(".fs-video");

        });*/
        // $("#programMain .title")[1].click()
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
        _tvFunc.volume100(function (){
            document.getElementsByTagName("video")[0].classList.add("utv-video-full");
            setTimeout(function (){
                document.getElementsByTagName("video")[0].volume=1;
                document.getElementsByTagName("video")[0].classList.add("utv-video-full");
            },3000);
        });
    }
};