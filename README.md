## Current Status

The only endpoint I've tested so far is: `/url-method-targets`

# Universal-Web-Scraper API

This project was forked from https://github.com/miguelangelo78/Universal-Web-Scraper

I have removed the app and I've turned it into a webapi. 

I am primarily a .NET developer, which is why I've created this fork. This appears to be a very capable web scraper, but there's
nothing comparable in C# and it relies on things that I couldn't really find in the C# ecosystem, so instead of opting for a
translation, I decided I'd make it a web service and host it in docker, which will suit my needs just fine.

Hope others find it useful as well.

## Building

I have never run this locally. I've only run it in docker, so if you want to know how to run it locally, I'm afraid I can't be of much help.

That said, I've used Claude and ChatGPT extensively for this conversion and maybe they can help you out, as they've helped me out.

So to build in docker:

`docker build -t webscraper-api .`

## Running

`docker run -d -p 8080:8080 webscraper-api`



