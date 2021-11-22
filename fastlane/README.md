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
### android build_feature
```
fastlane android build_feature
```
Feature build
### android release
```
fastlane android release
```
Release
### android build
```
fastlane android build
```
Build and Test
### android increment_version
```
fastlane android increment_version
```
Increment release version
### android deploy_play_store
```
fastlane android deploy_play_store
```
Deploy a new version to the alpha track on Google Play
### android deploy_internal_share
```
fastlane android deploy_internal_share
```
Deploy a new internal sharing build on Google Play
### android email_internal_share_build
```
fastlane android email_internal_share_build
```
Email new internal share build
### android email_play_store_internal_testing_release
```
fastlane android email_play_store_internal_testing_release
```
Email Play Store Internal Testing Release Ready
### android get_versions
```
fastlane android get_versions
```
Get app version information from the build

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.
More information about fastlane can be found on [fastlane.tools](https://fastlane.tools).
The documentation of fastlane can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
