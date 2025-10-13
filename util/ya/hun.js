var hun = require('javascript-obfuscator');
const fs = require("fs");
const utf8="utf8";
let option={
    compact: true,
    controlFlowFlattening: false,
    deadCodeInjection: true,
    debugProtection: false,
    debugProtectionInterval: 0,
    disableConsoleOutput: true,
  //  identifierNamesGenerator: 'hexadecimal',
    log: false,
    numbersToExpressions: false,
    renameGlobals: true,
    selfDefending: true,
    simplify: false,
    stringArray: true,
    stringArrayCallsTransform: false,
    stringArrayEncoding: ['base64'],
    stringArrayIndexShift: true,
    stringArrayRotate: true,
    stringArrayShuffle: true,
    stringArrayWrappersCount: 1,
    stringArrayWrappersChainedCalls: true,
    stringArrayWrappersParametersMaxCount: 2,
    stringArrayWrappersType: 'variable',
    stringArrayThreshold: 0.75,
    transformObjectKeys: true, // 转换对象键名
    splitStrings: true, // 分割字符串
    splitStringsChunkLength: 5, // 字符串块长度
    // 增强函数混淆
    functionOutlining: true, // 函数轮廓化
    flattenIdentifier: true, // 扁平化标识符
    identifierNamesGenerator: 'mangled', // 更复杂的标识符生成器
    unicodeEscapeSequence: false
};
fs.readFile("1.js", 'utf8',function (err,data){
    console.log(hun.obfuscate(data, option).getObfuscatedCode());
    fs.writeFileSync("2.js",hun.obfuscate(data, option).getObfuscatedCode(),"utf8")
});