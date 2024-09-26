package com.org.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.jsoup.Connection.Method;
import com.org.scraper.WebScraper;

import ch.qos.logback.classic.Level;

@SpringBootApplication
@RestController
@RequestMapping("/api/scraper")
public class WebScraperApiApplication {


    private static final Logger logger = LoggerFactory.getLogger(WebScraperApiApplication.class);
    private static final boolean IS_DOCKER = System.getenv("DOCKER_ENV") != null;

    public static void main(String[] args) {
        SpringApplication.run(WebScraperApiApplication.class, args);
    }

    @PostMapping("/login-dest")
    public String scrapeWithLoginDest(@RequestParam String urlLogin,
                                      @RequestParam String urlDest,
                                      @RequestParam Method method,
                                      @RequestParam boolean usingHeadless,
                                      @RequestParam String params,
                                      @RequestParam String targets) {
        logger.info("Received request to scrape with login. Login URL: {}, Destination URL: {}", urlLogin, urlDest);
        try {
            WebScraper scraper = new WebScraper(urlLogin, urlDest, method, IS_DOCKER || usingHeadless, params, targets);
            String result = scraper.toJSON();
            logger.info("Successfully scraped with login. Destination URL: {}", urlDest);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred while scraping with login. Destination URL: {}", urlDest, e);
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/login-dest-params")
    public String scrapeWithLoginDestParams(@RequestParam String urlLogin,
                                            @RequestParam String urlDest,
                                            @RequestParam boolean usingHeadless,
                                            @RequestParam String params,
                                            @RequestParam String bytokens) {
        logger.info("Received request to scrape with login. Login URL: {}, Destination URL: {} Params: {}  Bytokens: {}", urlLogin, urlDest, params, bytokens);
        try {
            WebScraper scraper = new WebScraper(urlLogin, urlDest, usingHeadless, params, bytokens);
            String result = scraper.toJSON();
            logger.info("Successfully scraped with login. Destination URL: {}", urlDest);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred while scraping with login. Destination URL: {}", urlDest, e);
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/login-dest-method")
    public String scrapeWithLoginDestMethod(@RequestParam String urlLogin,
                                            @RequestParam String urlDest,
                                            @RequestParam Method method,
                                            @RequestParam boolean usingHeadless,
                                            @RequestParam String params) {
        logger.info("Received request to scrape with login. Login URL: {}, Destination URL: {} Method: {} Params: {}", urlLogin, urlDest, method, params);
        try {
            WebScraper scraper = new WebScraper(urlLogin, urlDest, method, usingHeadless, params);
            String result = scraper.toJSON();
            logger.info("Successfully scraped with login. Destination URL: {}", urlDest);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred while scraping with login. Destination URL: {}", urlDest, e);
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/login-dest-simple")
    public String scrapeWithLoginDestSimple(@RequestParam String urlLogin,
                                            @RequestParam String urlDest,
                                            @RequestParam boolean usingHeadless) {
        logger.info("Received request to scrape with login. Login URL: {}", urlLogin, urlDest);
        try {
            WebScraper scraper = new WebScraper(urlLogin, urlDest, usingHeadless);
            String result = scraper.toJSON();
            logger.info("Successfully scraped with login. Destination URL: {}", urlDest);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred while scraping with login. Destination URL: {}", urlDest, e);
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/url")
    public String scrapeWithUrl(@RequestParam String url) {
        logger.info("Received request to scrape URL: {}", url);
        try {
            WebScraper scraper = new WebScraper(url);
            scraper.scrape(Method.GET, null);
            String result = scraper.toJSON();
            logger.info("Successfully scraped URL: {}", url);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred while scraping URL: {}", url, e);
            return "Error: " + e.getMessage();
        }
    }

    @GetMapping("/url-headless")
    public String scrapeWithUrlHeadless(@RequestParam String url,
                                        @RequestParam boolean usingHeadless) {
        logger.info("Received request to scrape URL: {}  usingHeadless: {}", url, usingHeadless);
        try {
            WebScraper scraper = new WebScraper(url, usingHeadless);
            scraper.scrape(Method.GET, null);
            String result = scraper.toJSON();
            logger.info("Successfully scraped URL: {}", url);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred while scraping URL: {}", url, e);
            return "Error: " + e.getMessage();
        }
    }

    @PostMapping("/url-method-targets")
    public String scrapeWithUrlMethodTargets(@RequestParam String url,
                                             @RequestParam Method method,
                                             @RequestParam boolean usingHeadless,
                                             @RequestParam String targets) {
        logger.info("Received request to scrape URL: {}  usingHeadless: {}  targets: {}", url, usingHeadless, targets);
        try {
            WebScraper scraper = new WebScraper(url, method, usingHeadless, targets);
            scraper.scrape(Method.GET, targets);
            String result = scraper.toJSON();
            logger.info("Successfully scraped URL: {}", url);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred while scraping URL: {}", url, e);
            return "Error: " + e.getMessage();
        }
    }
}