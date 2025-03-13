
(function(){
     _tvFunc.fixedW("body");
    _tvFunc.check(function (){return document.getElementsByClassName("playerBox").length>0;},function (){
        document.getElementsByClassName("playerBox")[0].classList.add("utv-video-full");
    });
    _tvFunc.volume100();
})();

