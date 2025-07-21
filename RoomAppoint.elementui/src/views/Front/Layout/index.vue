<template>
  <div>
    <el-container>
      <!-- 顶部导航栏（保持原有优化） -->
      <el-header style="
        background-color: rgba(51, 51, 51, 0.9);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        padding: 0;
      ">
        <el-menu :default-active="active" class="main-container" mode="horizontal" text-color="purpleblue"
          active-text-color="white" style="background: transparent; border: none;">
          <!-- 左侧LOGO和主菜单 -->
          <el-menu-item @click="ToPath('/Front/Home')">
            <img style="height: 60px; object-fit: cover;" :src="require('@/assets/white.jpg')">
          </el-menu-item>
          <el-menu-item index="/Front/Home" @click="ToPath('/Front/Home')">IKUN 自习室预约</el-menu-item>

          <!-- 右侧用户相关菜单 -->
          <el-menu-item style="float: right;" v-if="!Token" @click="ToRegister()">注册</el-menu-item>
          <el-menu-item style="float: right;" v-if="!Token" @click="ToLogin()">登录</el-menu-item>
          <el-submenu style="float: right; font-weight: bold;" v-if="Token" index="userMenu">
            <template #title>{{ UserInfo.UserName }}</template>
            <el-menu-item index="user个人信息" @click="ToUserInfo()">个人信息</el-menu-item>
            <el-menu-item index="user修改密码" @click="ToEditPassword()">修改密码</el-menu-item>
            <el-menu-item index="user退出" @click="LoginOut()">退出</el-menu-item>
          </el-submenu>
          <el-menu-item style="float: right;" index="/Front/AppointRecordList"
            @click="ToPath('/Front/AppointRecordList')" v-if="Token">我的预约记录</el-menu-item>
          <el-menu-item style="float: right;" index="/Front/IntegralList" @click="ToPath('/Front/IntegralList')"
            v-if="Token">我的积分</el-menu-item>
        </el-menu>
      </el-header>

      <!-- 主体内容区域 -->
      <el-main class="main-container main-box">
        <canvas id="backgroundCanvas"
          style="position: fixed; top: 64px; left: 0; width: 100%; height: calc(100vh - 144px); z-index: -1;"></canvas>
        <transition name="page-fade" mode="out-in">
          <router-view :key="$route.fullPath" class="transparent-bg"></router-view>
        </transition>
      </el-main>

      <!-- 底部栏（字体缩小并水平居中） -->
      <el-footer style="
        background-color: rgba(51, 51, 51, 0.8);
        height: 40px;
        margin-top: 30px;
        display: flex;
        align-items: center;
        justify-content: center;  /* 水平居中 */
        font-size: 12px;  /* 字体缩小 */
      ">
        <div style="color: aliceblue; font-weight: 6px;">
          系统归属于IKUN团队，版权所有 ©2024 - 2025 IKUN团队
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script>
import store from '@/store';
import { mapGetters } from 'vuex';

export default {
  data() {
    return { active: 'home' };
  },
  mounted() {
    this.initCanvas();
    console.log(this.UserInfo);
  },
  computed: {
    ...mapGetters(["UserInfo", "Token"])
  },
  methods: {
    ToLogin() { this.$router.push({ path: '/Login' }); },
    ToRegister() { this.$router.push({ path: '/Register' }); },
    async LoginOut() {
      await this.$store.dispatch('Logout');
      this.$router.push(`/Login`);
    },
    async ToPath(url) { this.$router.push({ path: url }); },
    async ToUserInfo() { this.$router.push({ path: "/Front/UserPerson" }); },
    async ToEditPassword() { this.$router.push({ path: "/Front/PasswordEdit" }); },
    goBack() { this.$router.go(-1); },

    initCanvas() {
      const canvas = document.getElementById('backgroundCanvas');
      const ctx = canvas.getContext('2d');
      const width = canvas.width = window.innerWidth;
      const height = canvas.height = window.innerHeight - 144;

      class Shape {
        constructor(x, y, speedX, speedY, type) {
          this.x = x;
          this.y = y;
          this.speedX = speedX;
          this.speedY = speedY;
          this.type = type;
        }
        update() {
          this.x += this.speedX;
          this.y += this.speedY;
          if (this.x < 0 || this.x > width) this.speedX = -this.speedX;
          if (this.y < 0 || this.y > height) this.speedY = -this.speedY;
        }
        draw() {
          ctx.fillStyle = 'rgba(0, 0, 30, 0.4)';
          ctx.beginPath();
          switch (this.type) {
            case 0: ctx.arc(this.x, this.y, 20, 0, Math.PI * 2); break;
            case 1: ctx.rect(this.x - 20, this.y - 20, 40, 40); break;
            case 2:
              const size = 40;
              ctx.moveTo(this.x, this.y - size / 2);
              ctx.lineTo(this.x + size / 2, this.y + size / 2);
              ctx.lineTo(this.x - size / 2, this.y + size / 2);
              ctx.closePath();
              break;
          }
          ctx.fill();
        }
      }

      const shapes = [];
      const numShapes = 15;
      for (let i = 0; i < numShapes; i++) {
        shapes.push(new Shape(
          Math.random() * width,
          Math.random() * height,
          (Math.random() * 4 - 2),
          (Math.random() * 4 - 2),
          Math.floor(Math.random() * 3)
        ));
      }

      const animate = () => {
        ctx.clearRect(0, 0, width, height);
        shapes.forEach(shape => { shape.update(); shape.draw(); });
        requestAnimationFrame(animate);
      };
      animate();
    }
  }
};
</script>

<style scoped>
.main-container {
  width: 1300px;
  margin: 0 auto;
  font-weight: bold;
}

.el-menu-item.is-active {
  background-color: #409EFF !important;
  color: #fff !important;
  text-shadow: 0 0 5px #ff00ff, 0 0 10px #ff00ff;
}

.main-box {
  min-height: calc(100vh - 160px);
}

.el-main {
  padding: 0 !important;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: all 0.25s ease;
}

.page-fade-enter,
.page-fade-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

.el-menu-item {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.el-menu-item:not(.is-active):hover {
  opacity: 0.8;
  transform: translateY(-2px);
}

.el-submenu__title {
  transition: opacity 0.3s ease;
}

.el-submenu:hover .el-submenu__title {
  opacity: 0.9;
}

.transparent-bg {
  background-color: transparent !important;
  padding: 20px;
}
</style>