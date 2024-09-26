package com.org.jsengine;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebResponseData;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;


public class RhinoJS {
    private WebClient client;
    private HtmlPage page;
    
    public HtmlPage getPage() {
        return page;
    }

    public void setPage(HtmlPage page) {
        this.page = page;
    }
    
    public WebClient getClient(){
        return client;
    }

    public Document getDocument(){
        return Jsoup.parse(page.asXml());
    }
    
    private HtmlPage toHtml(String html, String base_url) {
        HtmlPage page = null;
        try {
            URL url = new URL(base_url);
            WebResponseData responseData = new WebResponseData(
                html.getBytes(),
                200,
                "OK",
                new ArrayList<NameValuePair>()
            );
            WebResponse webResponse = new WebResponse(responseData, url, com.gargoylesoftware.htmlunit.HttpMethod.GET, 0);
            page = (HtmlPage) client.getPageCreator().createPage(webResponse, client.getCurrentWindow());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return page;
    }
    
    private String getDomainFromUrl(String url){
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        String domain = uri.getHost();
        return domain.startsWith("www.") ? domain.substring(4) : domain;
    }
    
    private void setCookies(String domain, Map<String, String> cookies){
        for (Map.Entry<String, String> entry : cookies.entrySet())
            client.getCookieManager().addCookie(new Cookie(domain, entry.getKey(), entry.getValue()));
    }
    
    public RhinoJS(String raw_html, String base_url, Map<String, String> cookies){
        warnings(true);
        
        client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setJavaScriptEnabled(true);
        client.getOptions().setActiveXNative(true);
        client.getOptions().setAppletEnabled(true);
        client.getOptions().setCssEnabled(true);
        client.getOptions().setDoNotTrackEnabled(true);
        client.getOptions().setPopupBlockerEnabled(true);
        client.getOptions().setRedirectEnabled(true);
        client.getOptions().setUseInsecureSSL(true);
        client.getOptions().setThrowExceptionOnFailingStatusCode(true);
        client.getOptions().setThrowExceptionOnScriptError(true);
        client.setAjaxController(new NicelyResynchronizingAjaxController());
        
        client.getCookieManager().setCookiesEnabled(true);
        setCookies(getDomainFromUrl(base_url), cookies);
        
        client.waitForBackgroundJavaScript(60 * 1000);
        
        page = toHtml(raw_html, base_url);
    }
    
    private void warnings(boolean enable){
        if(!enable)
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
        else
            java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.ALL); 
    }
}