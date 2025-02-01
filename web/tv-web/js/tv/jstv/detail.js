const _ctrlx={
    play(){
        _menuCtrl.menu();
    }
};
(function(){
    _tvFunc.check(function (){return $$("#programMain .title").length>0},function (){
        let url = window.location.href;
         let index= url.indexOf("tag=");
         let tag =  decodeURI(url.substring(index+4,url.length));
         console.log(tag);
         let currentTag="";
         let tagIndex=0;
        $$("#programMain .swiper-slide").each(function (i,item){
             let active=  $$(item).hasClass("active");
             let text = $$(item).find(".title").text().trim();
             //console.log(text);
             if(active){
                 currentTag=text;
             }
             if(tag===text){
                 tagIndex=i;
             }
        });
        console.log(currentTag);
        if(tag!==currentTag){
            $$("#programMain .title")[tagIndex].click();
        }
        // $("#programMain .title")[1].click()
        _tvFunc.check(function (){return document.getElementsByTagName("video").length>0;},function (){
            document.getElementsByTagName("video")[0].classList.add("utv-video-full");
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