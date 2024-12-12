const yaml = require('js-yaml');
//const toml = require('toml');
const fs = require("fs");
const utf8="utf8";
function  yamlToJson(path){
    fs.readdir(path, (err, files) => {
        files.forEach(file => {
            //let name = file.name;
            if(file.endsWith(".yml")){
                let  jsonData=  yaml.load(fs.readFileSync(path+file, utf8));
                fs.writeFileSync(path+file.substring(0,file.indexOf("."))+".json",JSON.stringify(jsonData),utf8);
            }
          /*  if(file.endsWith(".toml")){
                let  jsonObj=  toml.parse(fs.readFileSync(path+file, utf8));
                fs.writeFileSync(path+file.substring(0,file.indexOf("."))+".json",JSON.stringify(jsonObj),utf8);
            }*/
        });
    });
}

yamlToJson("config/");