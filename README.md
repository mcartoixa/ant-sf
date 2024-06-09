# ant-sf
[![Build status](https://github.com/mcartoixa/ant-sf/actions/workflows/build.yml/badge.svg?branch=main)](https://github.com/mcartoixa/ant-sf/actions/workflows/build.yml)
[![Code coverage](https://codecov.io/github/mcartoixa/ant-sf/graph/badge.svg?token=EtwTskQ89A)](https://codecov.io/github/mcartoixa/ant-sf)

Ant tasks that encapsulate [the Salesforce CLI](https://developer.salesforce.com/tools/salesforcecli).

## Rationale

### The Salesforce CLI
One of the promises of the Salesforce CLI is to allow easy Continuous Integration on Salesforce projects. There are many samples
of how to achieve that on platforms like [CircleCI](https://developer.salesforce.com/docs/atlas.en-us.sfdx_dev.meta/sfdx_dev/sfdx_dev_ci_circle.htm),
[Jenkins](https://developer.salesforce.com/docs/atlas.en-us.sfdx_dev.meta/sfdx_dev/sfdx_dev_ci_jenkins.htm)
or [Travis CI](https://developer.salesforce.com/docs/atlas.en-us.sfdx_dev.meta/sfdx_dev/sfdx_dev_ci_travis.htm).

The direct use of the CLI for CI integration seems to be alright for simple scenarii but there are several limitations to it:
* the build is difficult to reproduce locally.
* the build is tied to the shell technology provided by the platform, be it *bash*, *PowerShell* or (gasp!) *Windows Command*.
* the CLI commands outputs can be very verbose making the build log difficult to interpret when there is a problem.
* the real fun begins when you have an advanced scenario where the execution of a command depends on the result of another command...
  * the CLI commands have a JSON output option to allow interpretation, but then you lose the regular output.
  * there is no easy way to interpret JSON in, say, *bash* (try [jq](https://stedolan.github.io/jq/) if you want to do just that).
  * the JSON to be interpreted can be (and is often) inconsistent: the *result* property can be an object or an array for the same command depending on the status...
* YAML and shell seem to be the new makefiles. But at least makefiles could handle dependencies...

### Apache Ant
[Apache Ant](http://ant.apache.org/) is not for everyone. You have to understand it in order to respect it (like any technology) and you must not be repelled by XML.
But if you can handle it, this project allows you to:
* repeat your build locally, cross-platform.
* significantly clean the output of your build.
* use advanced concepts like [filesets](https://ant.apache.org/manual/Types/fileset.html) or [resources](https://ant.apache.org/manual/Types/resources.html).
* handle complex scenarii where the execution of a command depends on the result of another command:
  * with regular Ant [properties](http://ant.apache.org/manual/properties.html) and [conditions](http://ant.apache.org/manual/Tasks/condition.html).
  * with native JSON support in Ant [scripts](http://ant.apache.org/manual/Tasks/script.html) if necessary.
* easily integrate third-party Java based tools like [PMD](https://pmd.github.io/) or [ApexDoc](https://github.com/SalesforceFoundation/ApexDoc).
* read your build more easily (if you can accept [the angle bracket tax](https://blog.codinghorror.com/xml-the-angle-bracket-tax/)). What's more readable:
  * `sf org create scratch -v HubOrg -d -f config/project-scratch-def.json -a ciorg -w 2`?
  * or `<sf:org-create-scratch targetdevhubusername="HubOrg" defaultusername="true" definitionfile="config/project-scratch-def.json" alias="ciorg" wait="2" />`?

## Usage

The current documentation (`main` branch) for these tasks can be found at https://mcartoixa.github.io/ant-sf/. The documentation
for any specific version can be downloaded [from the releases section](https://github.com/mcartoixa/ant-sf/releases) as an asset of
the release in question.

These tasks can be downloaded directly [from the releases section](https://github.com/mcartoixa/ant-sf/releases) or with
a dependency manager like [Apache Ivy](http://ant.apache.org/ivy/) (preferred). You will need the following settings in your
`ivysettings.xml` file:
```xml
<ivysettings>
  <!-- GET is required by github: HEAD returns 403 -->
  <settings defaultResolver="default" httpRequestMethod="GET" />

  <resolvers>
    <url name="github">
      <ivy pattern="https://github.com/[organisation]/[module]/releases/download/v[revision]/ivy.xml" />
      <artifact pattern="https://github.com/[organisation]/[module]/releases/download/v[revision]/[artifact].[ext]" />
    </url>
  </resolvers>
  <modules>
    <module organisation="mcartoixa" name="*" resolver="github" />
  </modules>
</ivysettings>
```

## Development

### Prerequisites
* [Java SE Development Kit 11.0.20](https://www.oracle.com/java/technologies/downloads/#java11)
  * Ubuntu:
    * [Follow the procedure](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-22-04#option-2-installing-oracle-jdk-11).
    * Set the `JAVA_HOME` environment variable in your `~/.profile` file: `export JAVA_HOME="/usr/lib/jvm/java-11-oracle/jre"`.
  * Windows:
    * Execute the installer.
* Perl (unused on Windows) :
  * Ubuntu:
    * `sudo apt install perl`

Alternatively you can set your environment variables in a local `.env` file, e.g.:
```
JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
```

### Build
The build script at the root of the project can be used to perform different tasks:
* `./build.sh clean`: cleans the workspace.
* `./build.sh compile`: compiles the project.
* `./build.sh test`: executes unit tests.
* `./build.sh analysis`: source code analysis (with [PMD](https://pmd.github.io/)).
* `./build.sh package`: generates deployment packages.
* `./build.sh build`: `compile` + `test` + `analysis`.
* `./build.sh rebuild`: `clean` + `build`.
* `./build.sh release`: `rebuild` + `package`.

### Environment
* [NetBeans](https://netbeans.apache.org/download/index.html) 20:
  * Execute the build script at least once before opening NetBeans.
