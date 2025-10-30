
(function(){
    //_tvFunc.fixedW("body");
    _tvFunc.check(function (){return $$(".channel-list li").length>0},function (){
        let url = window.location.href;
        // let index= url.indexOf("tag=");
        let tag =  _tvFunc.getQueryParams()["tag"];
         console.log(tag);
         let currentTag=0;
        let tagIndex=0;
        $$(".channel-list li").each(function (i,item){
             let active=  $$(item).hasClass("active");
            let text = $$(item).find("span").text().trim();
             if(active){
                 currentTag=text;
             }
            if(tag===text){
                tagIndex=i;
            }
        });
        console.log(currentTag);
        if(tag!==currentTag){
            $$(".channel-list li")[tagIndex].click();
        }
        _tvFunc.check(function (){return document.getElementsByTagName("video").length>0;},function (){
           // document.getElementsByTagName("video")[0].classList.add("utv-video-full");
            _tvFunc.fullscreenW("video");
        });
        // $("#programMain .title")[1].click()
    });

    _tvFunc.volume100(function (){
        document.getElementsByTagName("video")[0].classList.add("utv-video-full");
        setTimeout(function (){
            document.getElementsByTagName("video")[0].volume=1;
            document.getElementsByTagName("video")[0].classList.add("utv-video-full");
        },3000);
    });
})();
