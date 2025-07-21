<template>
  <div>
    <el-container style="height: 100vh">
      <!-- 头部 -->
      <el-header>
        <div class="bg-header">
          <div>
            <span style="font-size: 24px; color: white; font-weight: bold;">诚凛自习室管理端</span>
          </div>
          <div style="display: flex; align-items: center;">
            <el-avatar :size="60" :src="UserInfo.ImageUrls" size="medium">
              <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png" />
            </el-avatar>
            <el-dropdown style="margin-left: 10px;">
              <span class="el-dropdown-link" style="color: white;">
                {{ UserInfo.Name }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item>
                  <RouterLink :to="{ path: '/Admin/UserPerson' }">个人信息</RouterLink>
                </el-dropdown-item>
                <el-dropdown-item>
                  <RouterLink :to="{ path: '/Admin/PasswordEdit' }">修改密码</RouterLink>
                </el-dropdown-item>
                <el-dropdown-item>
                  <div @click="LoginOut()">退出</div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </el-header>

      <!-- 主体 -->
      <el-container>
        <!-- 侧边栏 -->
        <el-aside width="200px">
          <el-menu class="menu-list" :router="true" active-text-color="blue">
            <el-menu-item index="/Admin/Home">
              <i class="el-icon-shujufenxi"></i>
              <span>控制台</span>
            </el-menu-item>
            <el-submenu index="/Admin/UserList">
              <template slot="title">
                <i class="el-icon-s-home"></i>
                <span>用户管理</span>
              </template>
              <el-menu-item index="/Admin/UserList">
                <span>用户信息</span>
              </el-menu-item>
            </el-submenu>
            <el-menu-item index="/Admin/RoomList">
              <i class="el-icon-AIzixishi"></i>
              <span>自习室</span>
            </el-menu-item>
            <el-menu-item index="/Admin/SeatList">
              <i class="el-icon-zuowei"></i>
              <span>座位</span>
            </el-menu-item>
            <el-menu-item index="/Admin/AppointRecordList">
              <i class="el-icon-yuyuejilu"></i>
              <span>预约记录</span>
            </el-menu-item>
            <el-menu-item index="/Admin/BannerList">
              <i class="el-icon-wenanfengmian"></i>
              <span>封面</span>
            </el-menu-item>
            <el-menu-item index="/Admin/IntegralList">
              <i class="el-icon-jifen"></i>
              <span>积分</span>
            </el-menu-item>
            <el-submenu index="/Admin/AppointRoomAppointStatusData">
              <template slot="title">
                <i class="el-icon-shujutongji"></i>
                <span>数据统计</span>
              </template>
              <el-menu-item index="/Admin/AppointRoomAppointStatusData">
                <span>预约状态分析</span>
              </el-menu-item>
              <el-menu-item index="/Admin/GetIntegralConsumeAndGainChart">
                <span>最近30天积分分析</span>
              </el-menu-item>
              <el-menu-item index="/Admin/GetAppointRoomRealTimeData">
                <span>统计实时在场人数</span>
              </el-menu-item>
            </el-submenu>
          </el-menu>
        </el-aside>

        <!-- 主内容区域 -->
        <el-main class="admin-main">
          <el-breadcrumb separator-class="el-icon-arrow-right" class="margin-bottom-lg">
            <el-breadcrumb-item v-for="(item, index) in breadList" :key="index" :to="item.path">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>

          <!-- 可滚动过渡容器 -->
          <div class="content-container">
            <transition name="card-flip" mode="out-in">
              <div :key="$route.fullPath" class="transition-wrapper">
                <router-view></router-view>
              </div>
            </transition>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { adminRouters } from "@/router/index";
import { mapGetters } from "vuex";

export default {
  name: 'Layout',
  data() {
    return {
      routerMenu: [],
      breadList: [],
    };
  },
  computed: {
    ...mapGetters(["UserInfo", "Token"])
  },
  watch: {
    $route() {
      this.getBreadcrumb();
    },
  },
  created() {
    this.routerMenu = adminRouters;
    this.getBreadcrumb();
  },
  methods: {
    async LoginOut() {
      console.log("点击退出");
      await this.$store.dispatch('Logout');
      this.$router.push(`/Login`);
    },
    isHome(route) {
      return route.path === "/Admin";
    },
    getBreadcrumb() {
      let matched = this.$route.matched;
      if (!this.isHome(matched[0])) {
        matched = [{ path: "/Admin", meta: { title: "控制台" } }].concat(matched);
      }
      this.breadList = matched;
    }
  }
};
</script>

<style>
.el-header {
  height: 60px;
  line-height: 60px;
  padding: 0;
  border-bottom: 1px solid #ccc;
  box-sizing: border-box;
}

.bg-header {
  background-color: #1A2D6F;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.menu-list {
  height: calc(100vh - 60px);
}

.el-submenu__title {
  text-align: left;
}

.el-menu-item {
  text-align: left;
}

.margin-bottom-lg {
  margin-bottom: 15px !important;
}

/* 主内容区域 */
.admin-main {
  height: calc(100vh - 60px) !important;
  display: flex;
  flex-direction: column;
  padding: 15px 20px 20px;
  background: #f0f2f5;
  overflow: hidden;
}

/* 可滚动容器 */
.content-container {
  flex: 1;
  position: relative;
  overflow-y: auto;
  perspective: 1000px;
  background: inherit;
}

/* 过渡容器 */
.transition-wrapper {
  min-height: 100%;
  padding: 20px;
  background: transparent;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 4px;
  transform-origin: center center;
}

/* 卡片翻转动画 (加快版本) */
.card-flip-enter-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.6, 1);
  /* 进入时间缩短到0.3秒 */
}

.card-flip-leave-active {
  transition: all 0.2s cubic-bezier(0.4, 0, 0.6, 1);
  /* 离开时间缩短到0.2秒 */
}

.card-flip-enter {
  transform: rotateY(60deg) scale(0.98);
  /* 减小旋转角度 */
  opacity: 0.95;
}

.card-flip-enter-to {
  transform: rotateY(0deg) scale(1);
  opacity: 1;
}

.card-flip-leave {
  transform: rotateY(0deg) scale(1);
  opacity: 1;
}

.card-flip-leave-to {
  transform: rotateY(-30deg) scale(0.98);
  /* 减小旋转角度 */
  opacity: 0.95;
}

/* 滚动条样式 */
.content-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.content-container::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: 3px;
}

.content-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.05);
}

/* 通用样式 */
[class^="el-icon-"] {
  vertical-align: middle;
  margin-right: 5px;
}

a {
  text-decoration: none;
  color: inherit;
}

.router-link-active {
  color: inherit;
}

.el-main {
  padding-top: 15px;
}
</style>