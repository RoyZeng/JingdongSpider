package net.spider;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HtmlBrowser {

    WebClient webClient;

    public HtmlBrowser() {
        webClient = new WebClient();
        // 1 启动JS
        webClient.getOptions().setJavaScriptEnabled(false);
        // 2 禁用Css，可避免自动二次请求CSS进行渲染
        webClient.getOptions().setCssEnabled(false);
        // 3 启动客户端重定向
        webClient.getOptions().setRedirectEnabled(true);
        // 4 js运行错误时，是否抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        // 5 设置超时
        webClient.getOptions().setTimeout(50000);
    }

    public HtmlPage getHtmlPage(String url) {
        HtmlPage htmlPage = null;
        try {
            htmlPage = webClient.getPage(url);
        } catch (IOException | FailingHttpStatusCodeException ex) {
            Logger.getLogger(HtmlBrowser.class.getName()).log(Level.SEVERE, null, ex);
        }
        // 等待JS驱动dom完成获得还原后的网页
        //webClient.waitForBackgroundJavaScript(10000);
        return htmlPage;
    }
      
    public void enableJS() {
        webClient.getOptions().setJavaScriptEnabled(true);
    }

    public void disableJS() {
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    public void close() {
        webClient.closeAllWindows();
    }
}
