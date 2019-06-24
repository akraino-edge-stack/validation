# Changelog
All notable changes to this project will be documented in this file.

## [Unreleased]

## [0.0.1-SNAPSHOT] - 4 June 2019
### Added
- A user can commit and be informed about submissions of blueprint validation requests.
- Appropriate dockerfiles have been added for the UI and the required postgreSQL db.
- .gitignore file has been updated
- Integration with Jenkins completed
- The URL of the result is retrieved and displayed
- Multi-threading is now supported
- Notification callback from Jenkins implemented
- Results are retrieved from Nexus
- Results are displayed
- UI and postgreSQL docker projects support the makefile automation build and push process
- README file is included
- CHANGELOG is included
- Coala static code analysis performed for Java and JS files

### Changed

### Removed

## [0.0.1-SNAPSHOT] - 5 June 2019
### Added

### Changed
- PostgreSQL database model has been refactored in order to support 4 tables, namely timeslot, blueprint, blueprint_instance, submission
- Trailing spaces removed from comments in javascript files

### Removed

## [0.0.1-SNAPSHOT] - 6 June 2019
### Added
- Community lab is now supported

### Changed
- README file is updated.
- Coala static analysis issues fixed for Javascript files
- README.md has been renamed to README.rst

### Removed

## [0.0.1-SNAPSHOT] - 7 June 2019
### Added
- Arm lab is now supported

### Changed
- README file is updated.

### Removed

## [0.0.1-SNAPSHOT] - 10 June 2019
### Added

### Changed
- Trailing spaces removed from all files.
- README file is updated.

### Removed

## [0.1.0-SNAPSHOT] - 24 June 2019
### Added
- The following database initialization scripts of ONAP portal SDK project have been added (but modified in order to support the Akraino database) : epsdk-app-common/db-scripts/EcompSdkDDLMySql_2_4_Common.sql, epsdk-app-os/db-scripts/EcompSdkDDLMySql_2_4_OS.sql, epsdk-app-common/db-scripts/EcompSdkDMLMySql_2_4_Common.sql and epsdk-app-os/db-scripts/EcompSdkDMLMySql_2_4_OS.sql. The copyrights of these files have not been changed.

### Changed
- Adaptation to ONAP portal SDK completed. Version 2.4.0 (Casablanca) has been used.
- The new URL of the results stored in Nexus is now used.

### Removed
