# 自习室预约系统

**一站式自习空间管理平台 | 用户端与管理端双系统设计**  
管理员测试账号：admin / 123456

## 项目亮点
🚀 **全栈技术体系**  
采用SpringBoot后端 + Vue2前端分离架构，通过RESTful API实现高效数据交互

💻 **前端技术实现**  
- 基于Vue2组件化开发，集成ElementPlus构建响应式UI
- 使用Vue-Router实现动态路由，Vuex集中管理全局状态（用户登录态/预约状态）
- 结合Grid与Flex布局实现动态座位渲染，支持可视化选座交互
- 通过Axios封装全局拦截器，实现Token鉴权与异常状态码处理
- 针对预约接口实现防抖控制，有效防止重复提交
- 集成ECharts开发数据可视化模块，实时展示使用统计

🔒 **权限双端分离**  
- **用户端**：座位预约/状态查看/个人中心  
- **管理端**：场地管理/预约审核/数据监控  

## 🛠 技术栈

### 前端技术栈
<p align="left">
  <img src="https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D" alt="Vue2">
  <img src="https://img.shields.io/badge/Element%20Plus-409EFF?style=for-the-badge&logo=element&logoColor=white" alt="ElementPlus">
  <img src="https://img.shields.io/badge/Vuex-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white" alt="Vuex">
  <img src="https://img.shields.io/badge/Vue_Router-35495E?style=for-the-badge&logo=vue.js&logoColor=4FC08D" alt="Vue Router">
  <img src="https://img.shields.io/badge/Axios-5A29E4?style=for-the-badge&logo=axios&logoColor=white" alt="Axios">
  <img src="https://img.shields.io/badge/ECharts-AA344D?style=for-the-badge&logo=apacheecharts&logoColor=white" alt="ECharts">
</p>

### 后端技术栈
<p align="left">
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="SpringBoot">
</p>

### 核心功能实现
| 功能模块         | 技术实现方案                     | 关键特性                          |
|------------------|----------------------------------|-----------------------------------|
| 状态管理         | Vuex                             | 集中管理用户登录态和预约状态      |
| 路由控制         | Vue Router                       | 动态路由跳转和权限控制            |
| HTTP通信         | Axios + 拦截器                   | Token自动注入和异常状态码处理     |
| 座位可视化       | Grid + Flex布局                  | 动态渲染和交互式选座              |
| 防重复提交       | 防抖控制(debounce)               | 关键操作防抖(300ms)               |
| 数据可视化       | ECharts集成                      | 使用统计实时图表展示              |
| 安全认证         | JWT鉴权方案                      | 基于Token的访问控制               |


