let _data={
    vue:null,
    initData(vue){
        this.vue=vue;
        this.channels();
        this.channelPage();
    },
    channels(){
        let requestUrl="js/cctv/tv.json";
        _data.vue.focusId="cctv";
        _apiX.getJson(requestUrl, {}, function(text) {
            console.log("text::: "+text)
            let data = JSON.parse(text);
            data.data.forEach((item,index)=>{
                item.id=index;
                _data.vue.channels.push(item);
            })
        });
    },
    channelPage(channelItem){

    }
};
$$(function(){
    _tvHtmlInit();
});