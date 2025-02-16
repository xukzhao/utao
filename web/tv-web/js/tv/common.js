const MyFocus = new Proxy(TvFocus, {
    ok:function (){
        _ctrlx.ok();
        return true;
    },
    menu:function(){
        if(_ctrlx.menu){
            _ctrlx.menu();
        }
        return true;
    },
    left:function(){
        if(_isVideo){
            _tvFunc.getVideo().currentTime-=20;_layer.notifyLess("进度减20秒");
        }
        return true;
    },
    right:function(){
        if(_isVideo){
            _tvFunc.getVideo().currentTime+=20;_layer.notifyLess("进度加20秒");
        }
        return true;
    },
    up:function(){
        let video= _tvFunc.getVideo();
        let currentVolume=_tvFunc.getVideo().volume;
        if(currentVolume<1){if((currentVolume+0.2)>1){video.volume=1}else{video.volume+=0.2}}
        let name= Math.floor(video.volume*100);
        _layer.notifyLess("音量"+name);
        return true;
    },
    down:function(){
        let video= _tvFunc.getVideo();
        let currentVolume=_tvFunc.getVideo().volume;
        if(currentVolume>0){if((currentVolume-0.2)<0){video.volume=0}else{video.volume-=0.2;}}
        let name= Math.floor(video.volume*100);
        _layer.notifyLess("音量"+name);
        return true;
    }
});
/*class MyFocus extends  TvFocus{

}*/
const _ctrlx={
    play(){
        _menuCtrl.menu();
    },
    menu(){
        console.log("menuxxx");
        //let url=window.location.href;
      /*  let currentChannel=null;
        _data.vue.channels.forEach((channel,index)=>{
            channel.vods.forEach((item,index)=>{
                if(url.startsWith(item.url)){
                    currentChannel=channel;
                }
            })
        });
        if(null!=currentChannel){
            console.log("#tv-"+currentChannel.tag);
          //  $$("#tv-"+currentChannel.tag).click();
           // _data.vue.currentChannel=currentChannel;

        }*/

    },
};
(function(){
    const _html={
        _ctrl:{
            xjChoose(item){
                let waitId= _layer.wait("请耐心等待跳转。。。");
                if(""!==item.url){
                    window.location.href=item.url;
                    return;
                }
                $$("#xxj-"+item.id).click();
                _data.xjExt(item);
                _layer.close(waitId);
            },

            next(){
                let index=_data.vue.now.xj.index+1;
                let item=_data.vue.xjs[index];
                this.xjChoose(item);
            }
        },
        xjList(){
            let requestUrl=_browser.getURL("js/cctv/tv.json");
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
        init(id,index,notReload){
            let menuId= this.initApp(id,index);
            this.initCtrl();
            if(notReload){
                return menuId;
            }
            let orgUrl=window.location.href;
            setInterval(function(){
                let nowUrl=window.location.href;
                if(nowUrl!==orgUrl){
                    console.log("UUUUUUUPP 已经发生变化！");
                    window.location.reload();
                }
            },2000);
            return menuId;
        },
        initCtrl(){
            this._ctrl.play = !_ctrlx.play ? function () { $$("#xtv-play").click();} : _ctrlx.play;
        },
        initHtml(){
            const html = `
            <div class="tv-body" id="tv-body" style="background:none;" @vue:mounted="initData()">
            <div class="tv-header">
                <div  tabindex="0" v-for="item in channels" class="tv-btn" :id="tvId(item.tag)" :class="{'tv-active':item.tag==currentChannel.tag}"
                 @focus="switchChannel(item)" @click="switchChannel(item)"  :move-down="moveDown(item.tag)" >{{item.name}}</div>
            </div>
            <div  v-for="item in channels" class="tv-content" :id="tvId(item.tag,'tvd-')" :style="{display:(item.tag==currentChannel.tag ? 'grid' : 'none')}">
               <div tabindex="0"  v-for="(vod,index) in item.vods"    @click="goto(vod)" :id="tvId(index,item.tag)" :move-updown-id=item.tag class="tv-item"
                 move-updown="5" :move-up="tvId(item.tag,'#tv-')">
                    <img v-if="vod.pic" :src=vod.pic :alt=vod.name />
                    <span :class="{'tv-btn':!vod.pic}">{{vod.name}}{{vod.remark}}</span>
               </div>
            </div>
           </div>`
            return html;
        },
        vue:null,
        getData(){
            let data={
                now:this.vue.now,
                video: this.vue.video,
                hzs:this.vue.hzs,
                xjs:this.vue.xjs,
                jds:this.vue.jds,
                rates:this.vue.rates,
                tab:this.vue.tab,
                focusId:this.vue.focusId,
                isVip:this.vue.isVip
            }
            console.log("all: "+JSON.stringify(data));
            return data;
        },
        initApp(id,index){
            let  html= this.initHtml();
            let menuId = _layer.initMenu(id,html,index);
            let _ctrl=this._ctrl;
            PetiteVue.createApp({
                now:{
                    hz:{},
                },
                channels:[],
                currentChannel:null,
                focusId:"tv",
                hzs:[],
                isVip:true,
                initData(){
                    let _this=this;
                    _html.vue=this;
                    _data.initData(this,function(){
                        //vip自动选择画质1080p
                        //非vip 选择不是vip的最高
                        let isVip=_this.isVip;
                        console.log("hzLevel",_this.now.hz.level);
                        if(_this.now.hz.level<1080){
                            console.log("need qie")
                            $$.each(_this.hzs,function(index,item){
                                if(isVip&&item.level===1080){
                                    setTimeout(function(){
                                        _layer.notify("正在切换到"+item.name);
                                        _this.hzChoose(item);
                                    },2000);
                                    return false;
                                }
                                if(!isVip&&!item.isVip){
                                    setTimeout(function(){
                                        _layer.notify("正在切换到"+item.name);
                                        _this.hzChoose(item);
                                    },2000);
                                    return false;
                                }
                            });
                        }
                    });
                    _html.xjList();

                  if(this.channels.length>0){
                      let url=window.location.href;
                      let currentChannel=null;
                      this.channels.forEach((channel,index)=>{
                          channel.vods.forEach((item,index)=>{
                              if(url.startsWith(item.url)){
                                  currentChannel=channel;
                              }
                          })
                      });
                         this.currentChannel=currentChannel;
                         this.$nextTick(()=>{
                             let idFirst = "#"+this.tvId(this.currentChannel.tag);
                             $$(idFirst).addClass("tv-focus");
                         });
                     }
                    window._isVideo=this.video;
                },
                switchChannel(item){
                    console.log("switchChannel",item.tag);
                    this.currentChannel=item;
                    let channelId = "#"+this.tvId(item.tag);
                    let hasFocus= $$(channelId).hasClass("tv-focus");
                    if(item.vods.length===0){
                        _data.channelPage(item);
                    }
                    this.$nextTick(function (){

                        console.log("hasFocus",hasFocus);
                        if(hasFocus){
                            $$(channelId).addClass("tv-focus");
                        }
                    })

                },
                moveDown(id){
                    return "#tvd-"+id+":.tv-item";
                },
                next(){
                    _ctrl.next();
                    _layer.hide(menuId);
                },
                reload(){
                    window.location.reload();
                },
                goto(item){
                    item.url=_tvFunc.url(item.url);
                    console.log(item.url);
                    window.location.href = item.url;
                },
                xjChoose(item){
                    _ctrl.xjChoose(item);
                    this.now.xj=item;
                    _layer.hide(menuId);
                },
                tvId(value,pre){
                    if(pre){
                        return pre+value;
                    }
                    return 'tv-'+value;
                },
                tabChoose(tabName){
                    console.log("tabName:"+tabName);
                    this.tab=tabName;
                }
            }).mount('#'+menuId);
            return menuId;
        }
    };
    window._detailInitOnly=function(id,index,notReload){
        return  _html.init(id,index,notReload);
    }
    window._detailInit=function(id,index,notReload){
        let menuId = _html.init(id,index,notReload);
        // new MyFocus(menuId).init();
        TvFocus.init(menuId);
        return menuId;
    }
    window._detailData=function(){
        return _html.getData();
    }
})();
