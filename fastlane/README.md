fastlane documentation
================
# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```
xcode-select --install
```

Install _fastlane_ using
```
[sudo] gem install fastlane -NV
```
or alternatively using `brew install fastlane`

# Available Actions
## Android
### android build
```
fastlane android build
```
Build and Test
### android deploy_alpha
```
fastlane android deploy_alpha
```
Deploy a new version to the alpha track on Google Play
### android deploy_internal_share
```
fastlane android deploy_internal_share
```
Deploy a new internal sharing build on Google Play
### android email_build
```
fastlane android email_build
```
Email new build

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
