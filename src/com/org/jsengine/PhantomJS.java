package com.org.jsengine;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class PhantomJS {
    private WebDriver client;
    
    private final int CLIENT_WIDTH = 1920;
    private final int CLIENT_HEIGHT = 1080;
    
    public WebDriver getClient() {
        return client;
    }
    
    private void setProperties(){
        client.manage().window().setSize(new Dimension(CLIENT_WIDTH, CLIENT_HEIGHT));
    }
    
    public Document getDocument(){
        return Jsoup.parse(client.getPageSource());
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
            client.manage().addCookie(new Cookie(entry.getKey(), entry.getValue(), domain, "/", null));
    }
    
	public PhantomJS() {
		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
		client = new ChromeDriver(options);
		setProperties();
	}
    
    public void run(String base_url, String domain_url, Map<String, String> cookies, EngineCallback callback){
        if(cookies!=null)
            setCookies(getDomainFromUrl(domain_url), cookies);
        
        if(callback!=null) callback.before_get(this);
        
        client.get(base_url);
        
        if(callback!=null) callback.after_get(this);
    }
    
    public byte[] take_screenshot(String screensht_filepath, boolean as_file){
        if(!as_file)
            return ((TakesScreenshot)client).getScreenshotAs(OutputType.BYTES);
        else{
            File scrFile = ((TakesScreenshot)client).getScreenshotAs(OutputType.FILE);
            try { 
                FileUtils.copyFile(scrFile, new File(screensht_filepath));
            } catch (IOException e) { e.printStackTrace(); }
        }
        
        return null;
    }
    
    public void quit(){
        client.quit();
    }
    
    public Map<String, String> auth(EngineAuthCallback callback, String url_base, boolean manual){
        Set<Cookie> cookies = null;
        
        if(manual){
            ChromeOptions options = new ChromeOptions();
            WebDriver manual_auth_driver = new ChromeDriver(options);
            manual_auth_driver.get(url_base);
    
            // You might want to implement a different way to wait for user authentication
            // For example, you could use a custom dialog or wait for a specific element to appear
            try {
                Thread.sleep(30000); // Wait for 30 seconds for manual authentication
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            cookies = manual_auth_driver.manage().getCookies();
            manual_auth_driver.quit();
        } else {
            client.get(url_base);
            callback.on_auth(client);
            cookies = client.manage().getCookies();
        }
        
        return EngineAuthCallback.toCookies(cookies);
    }
}