# 💎 Treasure

**Treasure** is a curated collection of **reusable infrastructure modules** for Java Spring applications. It focuses on solving cross-cutting concerns commonly encountered in enterprise development — such as exception logging, unified error handling, OAuth2 login, and pagination — all designed for seamless reuse across multiple Spring projects.

> Instead of general utilities, Treasure provides **plug-and-play building blocks** to help you standardize architecture and accelerate development.

---

## ✨ Features

| Module                         | Description                                                                 |
|-------------------------------|-----------------------------------------------------------------------------|
| AOP Exception Logging          | Logs runtime exceptions with rich context using Spring AOP                  |
| REST Exception Handling        | Unified error response via `@ControllerAdvice`, customizable error codes    |
| Data Pagination                | Abstraction for paging results, integrates easily with MyBatis/custom DAO   |
| WeCom OAuth2 Login             | 企业微信 OAuth2 登录流程封装，支持 token 获取与用户信息解析                   |

---

