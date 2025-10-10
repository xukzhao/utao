//let url = window.location.href;
//let index= url.indexOf("url=");
//let link = url.substring(index+4,url.length);
_tvFunc= {
    // 获取url请求参数
    getQueryParams() {
        return Object.fromEntries(
            new URLSearchParams(window.location.search)
        );
    }
}
let link =  _tvFunc.getQueryParams()["url"];
const config = {
    "id": "mse",
    "url": link,
    "playsinline": true,
    "plugins": [],
    "isLive": true,
    "autoplay": true,
    volume: 1,
    "width": "100%",
    "height": "100%"
}
//config.plugins.push(HlsPlayer);
//config.plugins.push(FlvPlayer)
let player = new HlsJsPlayer(config);


