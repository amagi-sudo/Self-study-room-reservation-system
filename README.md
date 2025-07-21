# 自习室预约系统
# 自习室预约系统 - 基于SpringBoot+Vue2的跨端解决方案

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

## 技术栈架构
```mermaid
graph LR
A[前端] --> B[Vue2]
A --> C[ElementPlus]
A --> D[Axios]
A --> E[ECharts]
F[后端] --> G[SpringBoot]
H[工程化] --> I[Vue-Router]
H --> J[Vuex状态管理]
