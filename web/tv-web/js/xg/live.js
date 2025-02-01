let url = window.location.href;
let index= url.indexOf("url=");
let link = url.substring(index+4,url.length);
const config = {
    "id": "mse",
    "url": link,
    "playsinline": true,
    "plugins": [],
    "isLive": true,
    "autoplay": true,
    "width": "100%",
    "height": "100%"
}
config.plugins.push(HlsPlayer)
let player = new Player(config);
const _ctrlx={
    play(){
        _menuCtrl.menu();
    }
};
(function(){
    let _app={
        init(){
            let menuId = _detailInit(null,999990,true);
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