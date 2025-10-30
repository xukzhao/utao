/**
 * 将 js/cctv/tv.yml 转换为 js/cctv/tv3.yml
 * 规则：
 * 1) 把任何包含 ".../tv-web/.../live.html?url=" 的 URL，前缀都去掉为 "live.html?url="
 * 2) 排除（删除）所有以 "https://www.gdtv.cn" 开头的 URL 项
 */
const fs = require('fs');
const path = require('path');
const yaml = require('js-yaml');

const srcPath = path.join(__dirname, 'js', 'cctv', 'tv.yml');
const outPath = path.join(__dirname, 'js', 'cctv', 'tv2.yml');

function transformUrl(url) {
  // 规则2：排除 https://www.gdtv.cn 前缀
 /* if (/^https:\/\/www\.gdtv\.cn/i.test(url)) {
    return null; // 表示该项需要被移除
  }
*/
  // 新增规则：排除 https://www.fengshows.com 前缀
  /*if (/^https:\/\/www\.fengshows\.com/i.test(url)) {
    return null; // 表示该项需要被移除
  }*/

  // 规则1：移除任何 URL 中的 "tv-web/" 前缀，并保留其后的路径（如 js/tv/iapp/... 或 live.html?url=...）
  const lower = url.toLowerCase();
  const idx = lower.indexOf('tv-web/');
  if (idx !== -1) {
    url = url.substring(idx + 'tv-web/'.length);
  }

  return url;
}

function main() {
  const raw = fs.readFileSync(srcPath, 'utf8');
  const doc = yaml.load(raw);

  let removedCount = 0;
  let transformedCount = 0;

  if (doc && Array.isArray(doc.data)) {
    doc.data = doc.data.map(group => {
      if (Array.isArray(group.vods)) {
        const newVods = [];
        for (const vod of group.vods) {
          if (!vod || typeof vod.url !== 'string') {
            newVods.push(vod);
            continue;
          }
          const newUrl = transformUrl(vod.url);
          if (newUrl === null) {
            removedCount++;
            continue; // 删除该 vod
          }
          if (newUrl !== vod.url) {
            transformedCount++;
          }
          newVods.push({ ...vod, url: newUrl });
        }
        group.vods = newVods;
      }
      return group;
    });
  }

  const outYaml = yaml.dump(doc, { lineWidth: -1 });
  fs.writeFileSync(outPath, outYaml, 'utf8');

  console.log(`转换完成：${outPath}`);
  console.log(`已过滤掉 gdtv.cn URL 数量：${removedCount}`);
  console.log(`已规范化 tv-web 前缀 URL 数量：${transformedCount}`);
}

main();
