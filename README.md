txgh
====
Synchronization app for Transifex/GitHub projects.

How it works
============
Both Transifex and GitHub can send POST requests when specific events happen. This Java servlet can consume these requests and when specific ones are detected, synchronize the updated content.

  * The GitHub content is updated only when the translation to the particular language in Transifex is completed and all texts are verified.
  * The Transifex resource is updated only when the corresponding GitHub source file is modified. GitHub updates in translated files are out of sync.


Configuration
=============
For correct functionality it is necessary to set up both Transifex and GitHub hooks in your projects settings. The mapping between both projects is specified in a PostgreSQL database.

~~~~
CREATE TABLE github (project VARCHAR PRIMARY KEY, userid VARCHAR, name VARCHAR NOT NULL, email VARCHAR NOT NULL, credentials VARCHAR, transifexproject VARCHAR NOT NULL);
CREATE TABLE transifex (project VARCHAR PRIMARY KEY, userid VARCHAR, password VARCHAR, githubproject VARCHAR NOT NULL);
~~~~

The [Transifex configuration file](http://docs.transifex.com/client/config/) specifies available resources for the given project, file naming conventions and the source file format. Exactly the same configuration file is used by Transifex client application. This configuration file must be available at `.tx/config` in the GitHub project tree.

    [main]
    host = https://www.transifex.com

    [transifex-project.msg]
    file_filter = resources/l10n/msg_<lang>.properties
    trans.fr = resources/l10/msg_special_fr.properties
    source_file = resources/l10n/msg_en.properties
    source_lang = en
    type = UNICODEPROPERTIES

When this app is deployed to e.g. `https://collaborne-txgh.herokuapp.com/txgh` then:
  * in Transifex project settings
      * set the hook URL to `https://collaborne-txgh.herokuapp.com/txgh/transifex`
  * in GitHub project settings
      * set the webhook to `http://collaborne-txgh.herokuapp.com/txgh/github`
      * change the content type to `application/x-www-form-urlencoded`
      * set to process just push events


Running on Heroku
=================
This application is intended for running on Heroku with an attached PostgreSQL database (schema see above) and these configuration variables:

| Key                               | Value |
| --------------------------------- | ----- |
| `TXGH_DEFAULT_GITHUB_USER`        | User to use when `github.userid` is `NULL` for a project |
| `TXGH_DEFAULT_GITHUB_PASSWORD`    | Password for the default GitHub user |
| `TXGH_DEFAULT_TRANSIFEX_USER`     | User to use when `transifex.userid` is `NULL` for a project |
| `TXGH_DEFAULT_TRANSIFEX_PASSWORD` | Password for the default Transifex user |
| `JDBC_DATABASE_URL`               | (Heroku-provided JDBC URL for the PostgreSQL database) |


Note that the `TXGH_DEFAULT...` keys are deprecated, and will be replaced with a dedicated "login with GitHub"/"Authorize Transifex" functionality.

Dependencies
============
This Java app requires JDK 8.

All dependencies are specified in the Maven project file. This application relies especially on:
  * Eclipse EGit connector
  * Jersey RESTful client
  * INI4J for parsing INI files

It has been successfully tested on both Apache Tomcat 8.

Acknowledgment
==============
The original sources are based on the https://github.com/drifted-in/txgh project.

