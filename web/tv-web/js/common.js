window.$$=Zepto;
if(typeof _tvIsApp === "undefined"){
    _tvIsApp=false;
}
if(typeof _tvIsGecko === "undefined"){
    _tvIsGecko=false;
}
var _tvFunc={
    isApp(){
        return _tvIsApp;
    },
    isGecko(){
        return _tvIsGecko;
    },
    url(url){
      if(url.startsWith("http")){
          return url;
      }
     let index= window.location.href.lastIndexOf("/")
        return window.location.href.substring(0,index+1)+url;
    },
   loadCssCode(code) {
      var style = document.createElement('style')
    // style.type = 'text/css'
      style.rel = 'stylesheet'
      style.appendChild(document.createTextNode(code))
      var head = document.getElementsByTagName('head')[0]
      head.appendChild(style);
    },
    fullscreen(id){
        var css=`
   ${id}{
        position: fixed !important;
       z-index: 99990 !important;
       width: 100% !important;
       height: 100% !important;
      top: 0 !important;
      left: 0 !important;
      right:0 !important;
      bottom: 0 !important;
          background-color: rgb(0, 0, 0); 
    }
 `;
        this.loadCssCode(css);
    },
    fullscreenW(id){
        var css=`
   ${id}{
        position: fixed !important;
       z-index: 99990 !important;
       width: 100vw !important;
       height: 100vh !important;
      top: 0 !important;
      left: 0 !important;
      right:0 !important;
      margin-left:0 !important;
          background-color: rgb(0, 0, 0); 
      bottom: 0 !important;
    }
 `;
        this.loadCssCode(css);
    },
    toggleFullScreen(elem) {
        if (!document.fullscreenElement) {
            if (elem.requestFullscreen) {
                elem.requestFullscreen();
            }
        } else {
            if (document.exitFullscreen) {
                document.exitFullscreen();
            }
        }
    },
    addKeyFullScreen(elem){
        document.addEventListener('keydown', function(event) {
            console.log("addKeyFullScreen",event.keyCode);
            //f
            if (event.keyCode === 70) {
                _tvFunc.toggleFullScreen(elem);
            }
        });
    },
    title(title){
          if(isNaN(title)){
              return title;
          }
          return `第${title}集`
    },
    titleQ(titleInt,title){
        if(isNaN(titleInt)){
            return title;
        }
        if(titleInt>2000){
            return title;
        }
        return `第${titleInt}集`
    },
    userAgent(isH5){
        let userAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 Edg/128.0.0.0";
        if(isH5){
            userAgent="Mozilla/5.0 (iPhone; CPU iPhone OS 17_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Cronet Mobile/15E148 Safari/604.1";
        }
        return userAgent;
    },
    video:null,
    volume100(ready){
       this.videoReady(function (video){
           video.volume = 1;
           console.log("video volume set ",video.volume);
       })
    },
    getVideo(){
        if(null!=this.video){
            return this.video;
        }
        let videos = document.getElementsByTagName("video");
        if(videos.length > 0){
            this.video=videos[0];
        }
        return this.video;
    },
    videoPlay(){
        let video = this.getVideo();
        if(video.paused){
            video.play();
        }else{
            video.pause();
        }
    },
    videoReady(ready){
        let video = this.getVideo();
        if(null==video){
            this.check(function (){return document.getElementsByTagName("video").length>0;},function (){
                video=_tvFunc.getVideo();
                _tvFunc.check(function (){return video.readyState>2;},function (){
                    console.log("video found ",video.readyState);
                    if(ready){
                        ready(video);
                    }
                })
            })
        }
        if(null!=video){
            this.check(function (){return video.readyState>2;},function (){
                console.log("video found ",video.readyState);
                if(ready){
                    ready(video);
                }
            })
        }
    },
    currentXj(item){
        //title,site,vodId,url
        _layer.notify("当前"+item.title);
        console.log("currentXj "+item.vodId+"  "+item.site);
        //记录当前
        if(null!=item.vodId&&""!==item.site){
            _apiX.msg("history.update",
                {site:item.site,vodId:item.vodId,url:item.url,remark:"看至"+item.title});
        }
    },
    hzLevel(name,type){
       if(type===1){
         if(name.includes("4K")){
            return 4096;
         }
         if(name.includes("1080")){
            return 1080;
         }
         if(name.includes("720")){
            return 720;
         }
         return 480;
       }
       if(type===2){
          if(name.includes("超清")){
            return 1080;
         }
         if(name.includes("高清")){
            return 720;
         }
         return 480;
       }

    },
    paramStr(data,start){
        let param="";
        Object.keys(data).forEach(key => {
            param+=`&${key}=${data[key]}`
        });
        if(start){
            return "?"+param.substring(1);
        }
        return  param;
    },
   image(url){
     if(url.includes("?")){
        return url+"&tvImg=1";
     }
    return url+"?tvImg=1";
   },
   show(){
    document.body.style.visibility="visible";
   },
   maxCheck(check,callback,maxNum){
     let num=0;
     let index=setInterval(function(){
        if(check()){
            clearInterval(index);
            callback(index);
        }
        num++;
        if(num>maxNum){
          clearInterval(index);
          callback(index);
        }
     },1000);
   },
   check(check,callback,time,maxNumOrg){
    if(!time){
        time=500;
    }
    let num=0;
    let maxNum=50;
    if(maxNumOrg&&maxNumOrg>0){
       maxNum=maxNumOrg;
    }
    let index=setInterval(function(){
          if(check()){
            clearInterval(index);
            callback(index);
          }
          num++;
          if(num>maxNum){
            clearInterval(index);
          }
    },time);
    return index;
   },
   sessionStorageCheckTime(key){
     let time= sessionStorage.getItem(key+"Time");
     if(time){
         let now=  new Date().getTime();
         if(now<(Number(time)+5000)){
            return true;
         }
     }
     return false;
   }
};
var _apiX={
    userAgent(isH5){
        let userAgent="Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36 Edg/128.0.0.0";
        if(isH5){
            userAgent="Mozilla/5.0 (iPhone; CPU iPhone OS 17_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Cronet Mobile/15E148 Safari/604.1";
        }
        return userAgent;
    },
    msg(service,data){
        let dataStr=  JSON.stringify(data);
        _api.message(service,dataStr);
    },
    msgStr(service,dataStr){
        _api.message(service,dataStr);
    },
    queryByService:async function (service,param,callback){
        if(null!=param){
            param=  JSON.stringify(param);
        }
        let  result= await  _api.queryByService(service,param);
        callback(result);
    },
    message(service,data,msgId,callback){
        data.msgId=msgId;
        let dataStr=  JSON.stringify(data);
        _api.message(service,dataStr);
        let index = setInterval(function (){
                            let data=  sessionStorage.getItem(msgId);
                            if(data){
                                clearInterval(index);
                               callback(data);
                            }},200)
    },
    postJson:async function (reqUrl, header, data, callback,errBack) {
        let headerStr=  JSON.stringify(header);
        let dataStr=  JSON.stringify(data);
        let result ="505";
        try{
             result= _api.postJson(reqUrl,headerStr,dataStr);
        }catch (e){
            console.error(e);
        }
        let isError=this.errorWork(result,errBack);
        if(isError){
            return;
        }
       callback(result);
    },
    getHtml: async function(reqUrl, header, callback,errBack) {
        let headerStr=  JSON.stringify(header);
        let result="505";
        try{
            result= _api.getHtml(reqUrl,headerStr);
        }catch (e){
            console.error(e);
        }
        let isError=this.errorWork(result,errBack);
        if(isError){
            return;
        }
        callback(result);
    },
    getJson: async function (reqUrl, header, callback,errBack) {
        let headerStr=  JSON.stringify(header);
        let result="505";
        try{
             result= _api.getJson(reqUrl,headerStr);
        }catch (e){
            console.error(e);
        }
        let isError=this.errorWork(result,errBack);
        if(isError){
            return;
        }
        callback(result);
    },
    errorWork(result,errBack){
        if(result==="500"||result==="400"||result==="505"){
            if(errBack){
                errBack(result);
                let  extMsg="";
                if(result==="505"){
                    extMsg="请检查网络";
                }
                _layer.notify("请求接口失败 "+extMsg)
            }
            return true;
        }
        return false;
    }
};

var _layer={
    init(id,html,index,idName,...classNames){
        var myDiv = document.createElement("div");
        if(!idName){
            idName="tv-index-"+index;
        }
        myDiv.id = idName;
        myDiv.style.zIndex=index;
        myDiv.style.visibility="hidden";//hidden  visible
        myDiv.style.overflowY="auto";
        if(classNames.length==0){
            myDiv.className="tv-index";
        }else{
            myDiv.className=classNames.join(" ");
        }
        myDiv.innerHTML = html;
        if(null==id){
            document.body.appendChild(myDiv);
        }else{
            let chooseId=id.substring(1);
            if(id.startsWith("#")){
                document.getElementById(chooseId).appendChild(myDiv);
            }
            if(id.startsWith(".")){
                document.getElementsByClassName(chooseId)[0].appendChild(myDiv);
            }
        }
        return idName;
    },
    initMenu(id,html,index,idName,...classNames){
        let menuId=this.init(id,html,index,idName,...classNames);
        window._tv_menuId=menuId;
        return menuId;
    },
    toggle(id){
        let elem=document.getElementById(id);
        if(elem){
            if(elem.style.visibility=="hidden"){
                elem.style.visibility="visible";
                return true;
            }
            elem.style.visibility="hidden";
        }
        return false;
    },
    isShow(id){
        if(typeof(id)=='undefined'||null==id){
            return false;
        }
        if(null!=document.getElementById(id)){
            return document.getElementById(id).style.visibility==="visible";
        }
        return false;
    },
    show(id){
        if(document.getElementById(id)){
            document.getElementById(id).style.visibility="visible";
        }
    },
    hide(id){
        if(document.getElementById(id)){
            document.getElementById(id).style.visibility="hidden";
        }
    },
    open(html,index,idName,...classNames){
        var myDiv = document.createElement("div");
        if(!idName){
            idName="tv-index-"+index;
        }
        myDiv.id = idName;
        myDiv.style.zIndex=index;
        if(classNames.length==0){
            myDiv.className="tv-index";
        }else{
            myDiv.className=classNames.join(" ");
        }
        myDiv.innerHTML = html;
        document.body.appendChild(myDiv);
        return idName;
    },
    openById(id,html,index,idName,className){
        var myDiv = document.createElement("div");
        if(!idName){
            idName="tv-index-"+index;
        }
        myDiv.id = idName;
        myDiv.style.zIndex=index;
        if(!className){
            className="tv-index";
        }
        myDiv.className=className;
        myDiv.innerHTML = html;
        document.getElementById(id).appendChild(myDiv);
        return idName;
    },
    notify(text,index,time){
        if(!time){
            time=2.5;
        }
        if(!index){
            index=9999999;
        }
        let html='<div>'+text+'</div>';
        let id= this.open(html,index,null,"tv-notify");
        setTimeout(function(){
            _layer.close(id);
        },time*1000);
    },
    notifyLess(text,index,time){
        if(!time){
            time=1;
        }
        if(!index){
            index=9999998;
        }
        let html='<div style="color: white;">'+text+'</div>';
        let lastId="tv-index-"+index;
        this.close(lastId);
        let id= this.open(html,index,null,"notify-less");
        setTimeout(function(){
            _layer.close(id);
        },time*1000);
    },
    wait(text,index){
        if(!index){
            index=9999999;
        }
        let html='<div style="color: white;">'+text+'</div>';
        let id= this.open(html,index,null,"tv-wait");
        return id;
    },
    close(id){
        if(document.getElementById(id)){
            document.getElementById(id).remove();
        }
    }
}
var _tvMsg={
    notVip:" 建议在拼多多/抖音/淘宝等购物软件里搜索购买"
}