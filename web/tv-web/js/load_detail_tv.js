if(typeof _tvload == "undefined"){
     _tvload=false;
}
(function(){
    if(_tvload){
        return;
    }
    _tvload=true;
  /*  if(window.location.href.startsWith("https://v.qq.com/x/cover/")){
        return ;
    }*/
    function loadDetailByUrl(url){
       /* if(url.startsWith("https://www.yangshipin.cn/tv/home")){
            return "cctv";
        }*/
        if(url.startsWith("https://tv.cctv.com/live")){
            return "tv/cctv";
        }
        if(url.startsWith("https://www.miguvideo.com/p/live")){
            return "ty/mg";
        }
        //各大tv
        if(url.startsWith("https://live.jstv.com")){
            return "tv/jstv"
        }
        if(url.startsWith("https://live.kankanews.com/")){
            return "tv/sh"
        }
        if(url.startsWith("https://live.fjtv.net/")){
            return "tv/fjtv"
        }
        if(url.startsWith("https://www.btime.com")){
            return "tv/bjtv"
        }
        if(url.startsWith("https://www.jxntv.cn/")){
            return "tv/jxntv"
        }
        if(url.startsWith("https://www.jlntv.cn/")){
            return "tv/jltv"
        }
        if(url.startsWith("https://www.gdtv.cn/tvChannelDetail")){
            return "tv/gdtv"
        }
      /*  if(url.startsWith("https://tv.hoolo.tv")){
            return "tv/hztv"
        }*/
        if(url.startsWith("https://www.lcxw.cn/")){
            _tvLoadRes.js("https://cdn.bootcdn.net/ajax/libs/hls.js/1.5.13/hls.js");
            return "tv/lctv"
        }
        if(url.startsWith("https://www.fengshows.com/")){
            return "tv/fengshows"
        }
        if(url.startsWith("https://www.rzw.com.cn")){
            return "tv/rztv"
        }
        if(url.startsWith("https://www.nmtv.cn")){
            return "tv/nmtv"
        }
        if(url.startsWith("https://www.mgtv.com/live")){
            return "tv/hntv"
        }
        if(url.startsWith("https://www.cbg.cn/")){
            return "tv/cqtv";
        }
        return "tv/common"
    }
    let detailPath=loadDetailByUrl(window.location.href);
    console.log("detailPath:: "+detailPath);
    if(window.location.href.startsWith("https://www.huya.com")){
        _tvLoadRes.css(`https://www.huya.com/tv-web/css/my.css`);
        _tvLoadRes.js(`https://www.huya.com/tv-web/js/zepto.min.js?v=x`);
        _tvLoadRes.js(`https://www.huya.com/tv-web/js/common.js?v=x`);
        _tvLoadRes.js(`https://www.huya.com/tv-web/js/${detailPath}/detail.js`);
        return;
    }
    _tvLoadRes.css(_browser.getURL("css/my.css?v=x"));
    _tvLoadRes.js(_browser.getURL("js/zepto.min.js?v=x"));
    _tvLoadRes.js(_browser.getURL("js/common.js?v=x"));
    _tvLoadRes.js(_browser.getURL(`js/${detailPath}/detail.js?v=x`));//"js/youku/detail.js?v=x"

})();