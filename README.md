# Reversing Bytecode into Bounties Resources


## Demo Vulnerable Jira Plugin

This repo contains a Jira Data Centre plugin which is intentionally vulnerable to XXE. It also contains a decompile script and malicious XML payload.

### Getting Started

To get started you will need to download the [Atlassian SDK](https://developer.atlassian.com/server/framework/atlassian-sdk/downloads/) and Java 8.

### Compiling and Running the Vulnerable Plugin

Note: A copy of the built plugin has been included in the root of the repo so that you can get started with decompiling straight away without having to first compile the plugin. If you would like you can skip straight to [Using the Decompile Script](#using-the-decompile-script).

Once you have the Atlassian SDK, compiling and running the vulnerable plugin with Jira DC is easy. Navigate to the `spreadsheetsForJira` directory and run `atlas-run`. This will compile the app and start an instance of Jira DC with the plugin already installed.

Once the instance has started it will be available at `http://localhost:2990/jira` and you can login with the default credentials username: admin, password: admin.

From there you can exploit the vulnerable code with the `malicious.xml` file supplied.

### Using the Decompile Script

Once you have compiled the plugin and have a `target` directory with an `obr` and `jar` file, you will be able to use the decompile script to decompile it. I know this sounds counter intuitive, because you have the source code for this plugin already; but usually you are not provided the source code and need to derive it from bytecode, so practicing how to do this is useful.

The `decompile.sh` script requires you to download [CFR](https://www.benf.org/other/cfr/) and [Procyon](https://github.com/mstrobel/procyon/releases/tag/v0.6.0). Download the jar files for each and then export the two environment variables `CFR_PATH` and `PROCYON_PATH` to be the paths to those jar files. E.g., `export CFR_PATH=~/demo-vulnerable-dc-app/cfr-0.152.jar && export PROCYON_PATH=~/demo-vulnerable-dc-app/procyon-decompiler-0.6.0.jar`.

After downloading the decompilers and exporting the environment variables, run `./decompile.sh` in the same directory as the jar or obr you wish to decompile. E.g., `cp spreadsheetsForJira/target/spreadsheetsForJira-1.0.0-SNAPSHOT.jar . && ./decompile.sh`

### Running Standalone Jira or Confluence DC

If you want to run a standalone instance of Jira or Confluence without a plugin already pre-installed (E.g., if you wanted to test a plugin from the Atlassian Marketplace) then use the `atlas-run-standalone` command instead. 

For example if you wanted to run Jira DC version 10.6.1 you could use: `atlas-run-standalone --product jira --version 10.6.1`.

Then you can install the plugin by uploading it's jar or obr to the instance as the admin user.

### References

https://developer.atlassian.com/server/framework/atlassian-sdk/rest-plugin-module/ 

https://developer.atlassian.com/platform/marketplace/dc-apps-platform-7-preparing-for-secure-endpoints/#:~:text=However%2C%20from%20Jira%20Software%2010.0,With%20the%20class%20ExampleServlet%20: 

https://developer.atlassian.com/platform/marketplace/security-requirements-dc/#:~:text=Sensitive%20Data%20Management&text=An%20application%20must%20securely%20store,or%20within%20the%20source%20code.    

https://marketplace.atlassian.com/  

### Tools

https://developer.atlassian.com/server/framework/atlassian-sdk/install-the-atlassian-sdk-on-a-linux-or-mac-system/ & https://developer.atlassian.com/server/framework/atlassian-sdk/atlas-run/  

https://github.com/mstrobel/procyon/releases/tag/v0.6.0  

https://www.benf.org/other/cfr/  

https://semgrep.dev/docs/getting-started/quickstart 

https://www.jetbrains.com/idea/download/?section=mac 

### Join Private Atlassian Marketplace Bounty Programs

https://bugcrowd-support.freshdesk.com 