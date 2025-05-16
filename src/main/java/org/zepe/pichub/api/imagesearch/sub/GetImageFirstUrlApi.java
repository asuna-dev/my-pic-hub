package org.zepe.pichub.api.imagesearch.sub;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.zepe.pichub.exception.BusinessException;
import org.zepe.pichub.exception.ErrorCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzpus
 * @datetime 2025/5/15 23:05
 * @description
 */
@Slf4j
public class GetImageFirstUrlApi {

    /**
     * 获取图片列表页面地址
     *
     * @param url
     * @return
     */
    public static String getImageFirstUrl(String url) {
        try {
            // 使用 Jsoup 获取 HTML 内容
            Document document = Jsoup.connect(url).timeout(5000).get();

            // 获取所有 <script> 标签
            Elements scriptElements = document.getElementsByTag("script");

            // 遍历找到包含 `firstUrl` 的脚本内容
            for (Element script : scriptElements) {
                String scriptContent = script.html();
                if (scriptContent.contains("\"firstUrl\"")) {
                    // 正则表达式提取 firstUrl 的值
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
                    Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        // 处理转义字符
                        firstUrl = firstUrl.replace("\\/", "/");
                        return firstUrl;
                    }
                }
            }

            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未找到 url");
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        String imageUrl = "https://www.codefather.cn/logo.png";
        String url = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        // 请求目标 URL
        String imageFirstUrl = getImageFirstUrl(url);
        log.info("搜索成功，结果 URL:{}", imageFirstUrl);
    }
}
