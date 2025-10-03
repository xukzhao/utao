let wasmInstance = null;
let memory = null;
let memory_p = null;
let memory_h = null;

const f = 'PBTxuWiTEbUPPFcpyxs0ww==';
const p = '10010';

function stringToUTF8(string, offset, length) {
    const encoder = new TextEncoder();
    const encoded = encoder.encode(string);
    for (let i = 0; i < encoded.length && i < length - 1; i++) {
        memory_p[offset + i] = encoded[i];
    }
    memory_p[offset + encoded.length] = 0; // Null-terminate
    return encoded.length;
}

function UTF8ToString(offset) {
    let s = '';
    let i = 0;
    while (memory_p[offset + i]) {
        s += String.fromCharCode(memory_p[offset + i]);
        i++;
    }
    return s;
}

async function getPlayerVersion() {
    const response = await fetch('https://app-sc.miguvideo.com/common/v1/settings/H5_DetailPage');
    const data = await response.json();
    const paramValue = data.body.paramValue;
    const playerVersion = JSON.parse(paramValue).playerVersion;
    return playerVersion;
}

// WASM导入函数
function a(e, t, r, n) {
    let s = 0;
    for (let i = 0; i < r; i++) {
        const d = memory_h[t + 4 >> 2];
        t += 8;
        s += d;
    }
    memory_h[n >> 2] = s;
    return 0;
}
function b(e, t, r, n) {}
function c(e) {}

async function initWasm() {
    const playerVersion = await getPlayerVersion();
    const wasmUrl = `https://www.miguvideo.com/mgs/player/prd/${playerVersion}/dist/mgprtcl.wasm`;

    const response = await fetch(wasmUrl);
    const wasmBinary = await response.arrayBuffer();

    const importObject = {
        a: {
            b: b,
            c: c,
            a: a,
        },
    };

    const { instance } = await WebAssembly.instantiate(wasmBinary, importObject);
    wasmInstance = instance;

    // 设置内存视图
    memory = wasmInstance.exports.d;
    memory_p = new Uint8Array(memory.buffer);
    memory_h = new Uint32Array(memory.buffer);

    // 分配导出函数到全局
    window.CallInterface1 = wasmInstance.exports.h;
    window.CallInterface2 = wasmInstance.exports.i;
    window.CallInterface3 = wasmInstance.exports.j;
    window.CallInterface4 = wasmInstance.exports.k;
    window.CallInterface6 = wasmInstance.exports.m;
    window.CallInterface7 = wasmInstance.exports.n;
    window.CallInterface8 = wasmInstance.exports.o;
    window.CallInterface9 = wasmInstance.exports.p;
    window.CallInterface10 = wasmInstance.exports.q;
    window.CallInterface11 = wasmInstance.exports.r;
    window.CallInterface14 = wasmInstance.exports.t;
    window.malloc = wasmInstance.exports.u;
}

function encrypt(url) {
    const parsedUrl = new URL(url);
    const query = Object.fromEntries(new URLSearchParams(parsedUrl.search));

    const o = query.userid || '';
    const a = query.timestamp || '';
    const s = query.ProgramID || '';
    const u = query.Channel_ID || '';
    const v = query.puData || '';

    const d = malloc(o.length + 1);
    const h = malloc(a.length + 1);
    const y = malloc(s.length + 1);
    const m = malloc(u.length + 1);
    const g = malloc(v.length + 1);
    const b = malloc(f.length + 1);

    const _ = malloc(64);
    const E = malloc(128);
    const T = malloc(128);

    stringToUTF8(o, d, o.length + 1);
    stringToUTF8(a, h, a.length + 1);
    stringToUTF8(s, y, s.length + 1);
    stringToUTF8(u, m, u.length + 1);
    stringToUTF8(v, g, v.length + 1);
    stringToUTF8(f, b, f.length + 1);

    const S = CallInterface6();
    CallInterface1(S, y, s.length);
    CallInterface10(S, h, a.length);
    CallInterface9(S, d, o.length);
    CallInterface3(S, 0, 0);
    CallInterface11(S, 0, 0);
    CallInterface8(S, g, v.length);
    CallInterface2(S, m, u.length);
    CallInterface14(S, b, f.length, T, 128);

    const w = UTF8ToString(T);
    const I = malloc(w.length + 1);

    stringToUTF8(w, I, w.length + 1);
    CallInterface7(S, I, w.length);
    CallInterface4(S, E, 128);
    const O = UTF8ToString(E);
//encrypt ddCalcu + '&sv=' + p
    return url + '&ddCalcu=' + encodeURIComponent(O) ;
}

function encrypt2(url) {
    const parsedUrl = new URL(url);
    const query = Object.fromEntries(new URLSearchParams(parsedUrl.search));

    const o = query.userid || '';
    const a = query.timestamp || '';
    const s = query.ProgramID || '';
    const u = query.Channel_ID || '';

    const d = malloc(o.length + 1);
    const h = malloc(a.length + 1);
    const y = malloc(s.length + 1);
    const m = malloc(u.length + 1);
    const b = malloc(f.length + 1);

    const _ = malloc(64);
    const E = malloc(128);
    const T = malloc(128);

    stringToUTF8(o, d, o.length + 1);
    stringToUTF8(a, h, a.length + 1);
    stringToUTF8(s, y, s.length + 1);
    stringToUTF8(u, m, u.length + 1);
    stringToUTF8(f, b, f.length + 1);

    const S = CallInterface6();
    CallInterface1(S, y, s.length);
    CallInterface10(S, h, a.length);
    CallInterface9(S, d, o.length);
    CallInterface3(S, 0, 0);
    CallInterface11(S, 0, 0);
    CallInterface2(S, m, u.length);
    CallInterface14(S, b, f.length, T, 128);

    const w = UTF8ToString(T);
    const I = malloc(w.length + 1);

    stringToUTF8(w, I, w.length + 1);
    CallInterface7(S, I, w.length);
    CallInterface4(S, E, 128);
    const O = UTF8ToString(E);

    return url + '&ddCalcu=' + encodeURIComponent(O) + '&sv=' + p;
}

async function getUrl(vid, rate_type = 3) {
    const channel_id = '2604039500-99000-200300020100001';//2604039500-99000-200300020100001
    const headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4343.0 Safari/537.36 Edg/89.0.727.0',
        //'Appcode': 'miguvideo_default_www',
       // 'Appid': 'miguvideo',
       // 'Channel': 'H5',
        'userInfo':'{"areaId":"210","cityId":"0210","expiredOn":"1758357867000","mobile":"OjT==wKrczM5ADO4EjNwMTM","passId":"453096490747161154","userId":"1419621633","carrierCode":"CU","encrypted":"1","userNum":"MzDrsyNzkDM4gTM2AzMxYDO","userToken":"nlpsD825DED698CF344361FC","blurMobile":"130****0937"}',
               'appVersion':'2604039500',
       // 'x-up-client-channel-id': channel_id,
    };
    //&salt=04055844&sign=2cf1a39e3b5d49f0c187abf0fa817caf&timestamp=1753178933577
   // const url = `https://play.miguvideo.com/playurl/v1/play/playurl?contId=63178053220250722029`;
//contId=${vid}
    const url = `https://webapi.miguvideo.com/gateway/playurl/v3/play/playurl?contId=${vid}&rateType=4&2Kvivid=true&4kDifinition=false&4kvivid=true&chip=mgwww&channelId=${channel_id}&super4k=true&superPlay=1&trackSubtitle=true&ua=iPhone13%2C3&vr=true&vr4k=1&xavs2=true&xh265=true&vr4k=1&xavs2=true&xh265=true`;

    const response = await fetch(url, { headers });
    const data = await response.json();
    return data.body.urlInfo.url;
}

async function test(){
    await initWasm();
    const videoId = 949142313;
    getUrl(videoId).then(url => {
        const encryptedUrl = encrypt(url);
        console.log("加密后的URL:", encryptedUrl);
    });
}
function processM3U8Content(m3u8Content, urlHandler) {
    const lines = m3u8Content.split('\n');
    const processedLines = [];
    let isMediaSegment = false;

    lines.forEach(line => {
        const trimmedLine = line.trim();

        // 检查是否为媒体片段信息行
        if (trimmedLine.startsWith('#EXTINF:')) {
            isMediaSegment = true;
            processedLines.push(trimmedLine);
            return;
        }

        // 如果上一行是媒体片段信息行，当前行就是TS文件URL
        if (isMediaSegment) {
            // 应用URL处理函数
            const processedUrl = urlHandler(trimmedLine);
            processedLines.push(processedUrl);
            isMediaSegment = false;
            return;
        }

        // 其他行直接添加
        processedLines.push(trimmedLine);
    });

    return processedLines.join('\n');
}
function createBlobUrl(content) {
    const blob = new Blob([content], { type: 'application/vnd.apple.mpegurl' });
    return URL.createObjectURL(blob);
}
function getPathBeforeQuery(url) {
    // 分割URL，只保留?之前的部分
    const pathPart = url.split('?')[0];
    // 找到最后一个斜杠的位置
    const lastSlashIndex = pathPart.lastIndexOf('/');
    // 如果找到斜杠，返回斜杠之前的部分；否则返回原路径
    return lastSlashIndex !== -1 ? pathPart.substring(0, lastSlashIndex) : pathPart;
}

// 示例使用
//test();

let url = window.location.href;
let index= url.indexOf("tag=");
let tag = url.substring(index+4,url.length);
//api.vonchange.com
let playUrl=null;
async function initPlayer  (){
    await initWasm();
    const videoId = 63178053220250722029;
    getUrl(videoId).then(url => {
        let preUrl=getPathBeforeQuery(url);
       // url=url.replace("https://h5live.gslb.cmvideo.cn", "https://hlszymgsplive.miguvideo.com:443");
        //mgsp.vod.miguvideo.com:8088
        let urlR=new URL(url);
        const query = Object.fromEntries(new URLSearchParams(urlR.search));
        let puData = query.puData;
        const encryptedUrl = encrypt(url);
        console.log("加密后的URL:", encryptedUrl);
        fetch(encryptedUrl)
            .then((response) => response.text())
            .then((data) => {console.log(data);
               // let result =data;
               //let url= createBlobUrl(result);
                const result=   processM3U8Content(data,function (url){
                    return encrypt2(preUrl+"/"+url,puData);
                });
                console.log(result);
                let url= createBlobUrl(result);
                const config = {
                    "id": "mse",
                    "url": '',
                    "playsinline": true,
                    "plugins": [],
                    "isLive": true,
                    "autoplay": true,
                    volume: 1,
                    "width": "100%",
                    "height": "100%"
                }
               // config.plugins.push(HlsPlayer);
//config.plugins.push(FlvPlayer)
                player = new HlsJsPlayer(config);
            });

    });
}


initPlayer();





