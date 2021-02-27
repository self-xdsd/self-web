<img alt="self-xsdsd-logo" src="https://self-xdsd.com/files/self-xdsd.png" width="80" height="80"/>

## self-web

[![Build Status](https://travis-ci.org/self-xdsd/self-web.svg?branch=master)](https://travis-ci.org/self-xdsd/self-web)
[![Coverage Status](https://coveralls.io/repos/github/self-xdsd/self-web/badge.svg?branch=master)](https://coveralls.io/github/self-xdsd/self-web?branch=master)

[![Managed By Self XDSD](https://self-xdsd.com/b/mbself.svg)](https://self-xdsd.com/p/self-xdsd/self-web?provider=github)
[![DevOps By Rultor.com](http://www.rultor.com/b/self-xdsd/self-web)](http://www.rultor.com/p/self-xdsd/self-web)
[![We recommend IntelliJ IDEA](http://amihaiemil.github.io/images/intellij-idea-recommend.svg)](https://www.jetbrains.com/idea/)

The Self Web Application, version `0.0.2`.

## Contributing 

If you would like to contribute, just open an issue or a PR.

You will need Java 11.
Make sure the maven build:

``$mvn clean install -Pcheckstyle,itcases``

passes before making a PR. [Checkstyle](http://checkstyle.sourceforge.net/) will make sure
you're following our code style and guidelines.

It's better to make changes on a separate branch (derived from ``master``), so you won't have to cherry pick commits in case your PR is rejected.

## Maven Settings

This project depends on jars from Self's Github Packages server. This server
requires authentication so, in order for Maven to be able to fetch dependencies,
you need to specify your credentials in Maven's ``settings.xml``:

```xml
<settings>
    ...
    <servers>
        <server>
            <id>github</id>
            <username>yourGithubUsername</username>
            <password>your_github_token</password>
        </server>
    </servers>
</settings>
```

Make sure you [generate](https://github.com/settings/tokens) a token with the appropriate
permissions. The ``settings.xml`` file usually resides on your computer at ``${user.home}/.m2/settings.xml``.
If the file is not there, you can create it.

## Cash Bounties

Some of the tickets have a cash bounty assigned to them. If you want to solve a ticket and get the bounty, tell me and I'll assign it to you. You will have 10 days to provide a PR and close the ticket. Pay attention: if the 10 days pass, I **might** take it away from you and give it to someone else -- if this happens, you won't get any cash. 

**You don't have to solve the whole ticket!** Many times, it will happen that the ticket requires more effort than what the bounty is worth. If this is the case, solve the ticket only **partially** and leave "todo" markers in the code -- these will automatically be transformed into Github Issues. You will have to leave the code in a consistent state, the build has to pass always.

More details [here](https://amihaiemil.com/2020/02/15/solve-github-issues-and-get-cash.html).

## LICENSE

This product's code is open source. However, the [LICENSE](https://github.com/self-xdsd/self-core/blob/master/LICENSE) only allows you to read the code. Copying, downloading or forking the repo is strictly forbidden unless you are one of the project's contributors.
