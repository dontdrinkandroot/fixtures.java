JPA fixture loader
==================

[![Build Status](https://travis-ci.org/dontdrinkandroot/fixtures.java.svg?branch=master)](https://travis-ci.org/dontdrinkandroot/fixtures.java)
[![Coverage Status](https://coveralls.io/repos/github/dontdrinkandroot/fixtures.java/badge.svg?branch=master)](https://coveralls.io/github/dontdrinkandroot/fixtures.java?branch=master)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=W9NAXW8YAZ4D6&item_name=fixtures.java%20Donation&currency_code=EUR)

About
-----

This library allows you to load a controlled set of data into a database through the Java Persistence API. This can
be useful for (integration) testing or to provide initial data to an application.

Usage
-----

### Installation

The library is available via Maven Central. Include the following in your `<dependencies>` section:

```xml
<dependency>
    <groupId>net.dontdrinkandroot</groupId>
    <artifactId>fixtures</artifactId>
    <version>RELEASE</version>
</dependency>
```