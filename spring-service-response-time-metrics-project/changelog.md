# Changelog
All notable changes to this project will be documented in this file.



## [1.1.2] - 2021-01-22
### Changed
- Add properties hint

## [1.1.1] - 2019-02-18
### Changed
- Append httpStatusCode for the metric hash map key

## [1.1.0] - 2019-01-24
### Changed
- Change handle exception due to NullPointerException in case of request timeout
- Fix missing HTTP request method in metric

## [1.0.1] - 
### Changed
- Thread-safe by change from Hashmap to ConcurrentHashMap

## [1.0.0] - 2019-01-10
First release
- Publish service response time in metric
- Configurable for Group url with regex