<template>
  <div>
    <canvas id="starrySky"></canvas>
    <div style="height: 100vh; display: flex; align-items: center; justify-content: center;">
      <div
        style="background-color: rgba(255, 255, 255, 0.8); padding: 30px 40px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);">
        <div style="width: 480px;">
          <h2 class="welcome-title" style="margin-bottom: 25px; font-size: 22px;">欢迎您~来到诚凛自习室预约系统</h2>
          <el-form ref="loginForm" :model="formData" label-width="70px" label-position="left" :rules="rules">
            <el-form-item label="账号" prop="UserName" style="margin-bottom: 20px;">
              <el-input type="text" v-model.trim="formData.UserName" placeholder="请输入账号"
                style="height: 44px; padding: 0 15px; font-size: 15px;"></el-input>
            </el-form-item>
            <el-form-item label="密码" prop="Password" style="margin-bottom: 20px;">
              <el-input type="password" v-model.trim="formData.Password" placeholder="请输入密码"
                style="height: 44px; padding: 0 15px; font-size: 15px;"></el-input>
            </el-form-item>
            <el-form-item label="角色" prop="RoleType" style="margin-bottom: 20px; display: flex; align-items: center;">
              <el-radio-group v-model="formData.RoleType"
                style="display: flex; gap: 20px; align-items: center; flex-wrap: nowrap;">
                <el-radio v-for="(item, index) in roleOptions" :key="item.Code" :label="item.Code"
                  style="font-size: 15px;">{{ item.Label }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="验证码" prop="Code" style="margin-bottom: 25px;">
              <div style="display: flex; align-items: center; gap: 15px;">
                <el-input type="text" v-model.trim="formData.Code" placeholder="请输入验证码"
                  style="width: 180px; height: 44px; padding: 0 15px; font-size: 15px; min-width: 150px;">
                </el-input>
                <div style="width: 120px; height: 44px;">
                  <ValidCode ref="ValidCode"></ValidCode>
                </div>
              </div>
            </el-form-item>
            <!-- 关键调整：合并按钮到同一form-item并设置flex布局 -->
            <el-form-item style="margin-bottom: 25px; display: flex; justify-content: center; gap: 15px;">
              <el-button type="primary" @click="LoginBtn"
                style="width: 180px; height: 48px; font-size: 16px; letter-spacing: 2px;">登 录</el-button>
              <el-button type="danger" @click="ToHome"
                style="width: 180px; height: 48px; font-size: 16px; letter-spacing: 2px;">前 台</el-button>
            </el-form-item>
          </el-form>
          <div style="display: flex; justify-content: space-between; align-items: center; gap: 10px;">
            <div style="display: flex; align-items: center; gap: 8px; font-size: 14px;">
              <span>还没有账号？</span>
              <RouterLink :to="{ path: '/Register' }" style="color: #F98C54; cursor: pointer; font-weight: 500;">立即注册
              </RouterLink>
            </div>
            <div style="color: #E91E63; cursor: pointer; font-size: 14px; font-weight: 500;"
              @click="handleForgetPassword">忘记密码？</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ValidCode from '@/components/Code/canvas.vue'
import store from '@/store';

export default {
  components: { ValidCode },
  data() {
    return {
      formData: {
        UserName: '',
        Password: '',
        RoleType: "",
        Code: ""
      },
      roleOptions: [],
      rules: {
        UserName: [
          { required: true, message: '请输入账号', trigger: 'blur' },
        ],
        Password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
        ],
        RoleType: [
          { required: true, message: '请选择角色', trigger: 'blur' },
        ],
        Code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              let identifyCode = this.$refs.ValidCode.getCode();
              if (value != identifyCode) {
                callback(new Error('请输入正确的验证码'));
              } else {
                callback();
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  created() {
    this.GetRoleTypeApi();
  },
  mounted() {
    this.initStarrySky();
  },
  beforeDestroy() {
    if (this.starrySkyAnimation) {
      cancelAnimationFrame(this.starrySkyAnimation);
    }
  },
  methods: {
    async GetRoleTypeApi() {
      let { Data: { Items } } = await this.$Post("/Select/RoleType");
      this.roleOptions = Items;
    },
    LoginBtn() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          let res = await store.dispatch("Login", this.formData);
          if (res.Success) {
            this.$message.success("登录成功!");
            this.$router.push({ path: "/Admin" });
          } else {
            this.$refs.ValidCode.refreshCode();
          }
        } else {
          this.$message.error("登录验证不通过");
          this.$refs.ValidCode.refreshCode();
          return false;
        }
      });
    },
    ToHome() {
      this.$router.push({ path: "/Front/Home" });
    },
    initStarrySky() {
      const canvas = document.getElementById('starrySky');
      const ctx = canvas.getContext('2d');
      const width = canvas.width = window.innerWidth;
      const height = canvas.height = window.innerHeight;

      class Star {
        constructor() {
          this.x = Math.random() * width;
          this.y = Math.random() * height;
          this.size = Math.random() * 2;
          this.speedX = Math.random() * 3 - 1.5;
          this.speedY = Math.random() * 3 - 1.5;
        }

        update() {
          this.x += this.speedX;
          this.y += this.speedY;
          if (this.x > width || this.x < 0) this.speedX = -this.speedX;
          if (this.y > height || this.y < 0) this.speedY = -this.speedY;
        }

        draw() {
          ctx.beginPath();
          ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
          ctx.fillStyle = '#FFFF00';
          ctx.fill();
        }
      }

      const stars = Array(400).fill(0).map(() => new Star());

      const animate = () => {
        ctx.fillStyle = '#000033';
        ctx.fillRect(0, 0, width, height);
        stars.forEach(star => {
          star.update();
          star.draw();
        });
        this.starrySkyAnimation = requestAnimationFrame(animate);
      };

      animate();
    },
    handleForgetPassword() {
      this.$message.info('请联系管理员重置密码');
    }
  }
}
</script>

<style scoped>
.welcome-title {
  font-size: 22px;
  font-weight: 600;
  background: linear-gradient(90deg, #4f6d7a 20%, #6a8b9a 50%, #8b9bb7 80%);
  -webkit-background-clip: text;
  color: transparent;
  text-align: center;
  letter-spacing: 1.5px;
  line-height: 1.4;
}

#starrySky {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background: linear-gradient(180deg, #000033 0%, #000022 100%);
}

.el-radio__label {
  font-size: 15px;
}

.el-input__inner {
  transition: all 0.3s ease;
}

.el-input__inner:focus {
  border-color: #4f6d7a !important;
  box-shadow: 0 0 0 2px rgba(79, 109, 122, 0.1);
}

.el-button--primary {
  background-color: #4f6d7a;
  border-color: #4f6d7a;
}

.el-button--primary:hover {
  background-color: #6a8b9a;
  border-color: #6a8b9a;
}

.el-button--danger {
  background-color: #E91E63;
  border-color: #E91E63;
}

.el-button--danger:hover {
  background-color: #FF4081;
  border-color: #FF4081;
}

.el-button {
  transition: transform 0.2s ease;
  flex-shrink: 0;
  /* 防止按钮被压缩变形 */
}

.el-button:hover {
  transform: translateY(-2px);
}

.el-form-item {
  margin-bottom: 20px;
}

/* 验证码内容区布局 */
.el-form-item__content {
  display: flex;
  align-items: center;
  gap: 15px;
}

/* 按钮区域样式 */
.el-form-item:last-child {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-bottom: 25px;
  /* 与下方链接保持间距 */
}
</style>