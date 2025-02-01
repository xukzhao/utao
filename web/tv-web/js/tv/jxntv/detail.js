const _ctrlx={
    play(){
        _menuCtrl.menu();
    }
};
(function(){
    _tvFunc.check(function (){return $$(".channel-list .item").length>0},function (){
        let url = window.location.href;
         let index= url.indexOf("tag=");
         let tag = url.substring(index+4,url.length);
         if(tag.includes("#")){
             tag=tag.substring(0,tag.indexOf("#"));
         }
         console.log("tag",tag);
         let currentTag=0;
        $$(".channel-list .item").each(function (i,item){
             let active=  $$(item).find(".watching").length>0;
             if(active){
                 currentTag=i;
             }
        });
        console.log("currentTag",tag);
        if(Number(tag)!==currentTag){
            $$(".channel-list .item")[Number(tag)].click();
        }
        // $("#programMain .title")[1].click()
        _tvFunc.check(function (){return document.getElementsByTagName("video").length>0;},function (){
            // _tvFunc.addKeyFullScreen(document.getElementsByTagName("video")[0]);
            // _apiX.msgStr("key","F");
            _tvFunc.fullscreenW("#liveplayer");
            //$$(".xb-hiden-video").css("position","fixed !important");
            //$$("video").css("position","fixed !important");
            // document.getElementsByTagName("video")[0].classList.add("utv-video-full");
        });
    });

    let _app={
        init(){
            _tvFunc.check(function(){return  document.getElementsByTagName("video").length>0;},function(index){
                //全屏
                let menuId = _detailInit(null,999990,true);
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
    }
};