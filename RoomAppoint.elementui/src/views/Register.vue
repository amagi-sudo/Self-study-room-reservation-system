<template>
  <div>
    <canvas id="starrySky"></canvas>
    <div style="height: 100vh; display: flex; align-items: center; justify-content: center;">
      <div
        style="background-color: rgba(255, 255, 255, 0.8); padding: 40px 60px; border-radius: 15px; box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1); width: 500px;">
        <h2 class="welcome-title" style="margin-bottom: 30px; font-size: 22px; text-align: center;">用户注册</h2>
        <el-form ref="loginForm" :model="formData" label-width="80px" label-position="left" :rules="rules">
          <el-form-item label="账号" prop="UserName" style="margin-bottom: 20px;">
            <el-input type="text" v-model.trim="formData.UserName" placeholder="请输入账号" />
          </el-form-item>
          <el-form-item label="密码" prop="Password" style="margin-bottom: 20px;">
            <el-input type="password" v-model.trim="formData.Password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="邮箱" prop="Email" style="margin-bottom: 20px;">
            <el-input type="email" v-model.trim="formData.Email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="联系方式" prop="PhoneNumber" style="margin-bottom: 20px;">
            <el-input type="tel" v-model.trim="formData.PhoneNumber" placeholder="请输入联系方式" />
          </el-form-item>
          <el-form-item label="姓名" prop="Name" style="margin-bottom: 20px;">
            <el-input type="text" v-model.trim="formData.Name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="验证码" prop="Code" style="margin-bottom: 25px;">
            <el-input type="text" v-model.trim="formData.Code" placeholder="请输入验证码" />
            <div style="width: 120px; height: 44px; display: inline-block; margin-left: 8px;">
              <ValidCode ref="ValidCode" />
            </div>
          </el-form-item>
          <el-form-item style="margin-bottom: 30px;">
            <el-button type="primary" @click="RegisterBtn"
              style="width: 100%; height: 48px; font-size: 16px; letter-spacing: 2px;">
              注 册
            </el-button>
          </el-form-item>
        </el-form>
        <div style="text-align: center; font-size: 14px;">
          已有账号？<RouterLink :to="{ path: '/Login' }" style="color: #E91E63; font-weight: 500; margin-left: 8px;">立即登录
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ValidCode from '@/components/Code/canvas.vue'
import store from '@/store'

export default {
  components: { ValidCode },
  data() {
    return {
      formData: {
        UserName: '',
        Password: '',
        Email: '',
        PhoneNumber: '',
        Name: '',
        Code: ''
      },
      rules: {
        UserName: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        Password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        Email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        PhoneNumber: [
          { required: true, message: '请输入联系方式', trigger: 'blur' },
          {
            validator: (_, value, callback) => {
              if (!/^1[3-9]\d{9}$/.test(value)) callback(new Error('请输入正确的手机号'));
              else callback();
            }, trigger: 'blur'
          }
        ],
        Name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
        Code: [
          { required: true, message: '请输入验证码', trigger: 'blur' },
          {
            validator: (_, value, callback) => {
              const code = this.$refs.ValidCode.getCode()
              if (value !== code) callback(new Error('验证码错误'));
              else callback();
            }, trigger: 'blur'
          }
        ]
      }
    }
  },
  mounted() {
    this.initStarrySky()
  },
  methods: {
    initStarrySky() {
      const canvas = document.getElementById('starrySky')
      const ctx = canvas.getContext('2d')
      canvas.width = window.innerWidth
      canvas.height = window.innerHeight
      canvas.style.position = 'fixed'
      canvas.style.top = '0'
      canvas.style.left = '0'
      canvas.style.zIndex = '-1'

      const stars = []
      const starCount = 300 // 星星数量减少一半
      for (let i = 0; i < starCount; i++) {
        stars.push({
          x: Math.random() * canvas.width,
          y: Math.random() * canvas.height,
          size: Math.random() * 1.5 + 0.5,
          speedX: (Math.random() - 0.5) * 1,
          speedY: (Math.random() - 0.5) * 1
        })
      }

      const animate = () => {
        ctx.fillStyle = '#000033'
        ctx.fillRect(0, 0, canvas.width, canvas.height)

        stars.forEach(star => {
          star.x += star.speedX
          star.y += star.speedY

          if (star.x < 0 || star.x > canvas.width) star.speedX = -star.speedX
          if (star.y < 0 || star.y > canvas.height) star.speedY = -star.speedY

          ctx.beginPath()
          ctx.arc(star.x, star.y, star.size, 0, Math.PI * 2)
          ctx.fillStyle = '#FFFF00'
          ctx.shadowColor = '#FFFF00'
          ctx.shadowBlur = star.size * 2
          ctx.fill()
        })

        requestAnimationFrame(animate)
      }

      animate()

      window.addEventListener('resize', () => {
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
      })
    },
    async RegisterBtn() {
      this.$refs.loginForm.validate(async (valid) => {
        if (valid) {
          // 这里替换为实际注册接口
          const res = { Success: true }
          if (res.Success) {
            this.$message.success('注册成功！')
            this.$router.push('/Login')
          } else {
            this.$refs.ValidCode.refreshCode()
            this.$message.error('注册失败，请重试')
          }
        }
      })
    }
  }
};
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
  margin-bottom: 20px;
}

.el-input__inner {
  transition: all 0.3s ease;
  border-radius: 8px;
  border: 1px solid #ebeef5;
}

.el-input__inner:focus {
  border-color: #4f6d7a !important;
  box-shadow: 0 0 0 2px rgba(79, 109, 122, 0.1);
}

.el-button--primary {
  background-color: #4f6d7a;
  border-color: #4f6d7a;
  border-radius: 8px;
  font-weight: 500;
}

.el-button--primary:hover {
  background-color: #6a8b9a;
  transform: translateY(-2px);
}

/* 调整验证码标签对齐 */
.el-form-item__label {
  display: flex;
  align-items: center;
  line-height: 1.2;
  /* 调整行高确保竖向对齐 */
}
</style>