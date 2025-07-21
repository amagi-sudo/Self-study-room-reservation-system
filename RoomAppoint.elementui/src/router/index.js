import Vue from 'vue'
import VueRouter from 'vue-router'
import store from '@/store'
import { Message } from 'element-ui'
import { getToken, setToken, removeToken } from '@/utils/cache'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style

Vue.use(VueRouter)
import AdminLayout from "@/views/Admin/Layout/index.vue"
import FrontLayout from "@/views/Front/Layout/index.vue"

const routes = [
  {
    path: "/", redirect: "/Front",
  },
  {
    path: "/Login", component: () => import("@/views/Login.vue")
  },
  {
    path: "/Register", component: () => import("@/views/Register.vue")
  },
]

// 前台的路由 
export const frontRouters = [
  {
    path: "/Front",
    redirect: "/Front/Home",
    component: FrontLayout,
    meta: {
      title: "前台",
      isAdmin: false,
    },
    children: [
      {
        path: "/Front/Home",
        meta: {
          isAdmin: false,
        },
        component: () => import("@/views/Front/Home")
      },
      {
        path: "/Front/Room",
        meta: {
          title: "自习室详情",
          isAdmin: false,
        },
        component: () => import("@/views/Front/Room")
      },
      {
        path: "/Front/ToOrder",
        meta: {
          title: "预约下单",
          isAdmin: false,
        },
        component: () => import("@/views/Front/ToOrder")
      },
      {
        path: "/Front/AppointRecordList",
        meta: {
          title: "我的预约",
          isAdmin: false,
        },
        component: () => import("@/views/Front/AppointRecordList")
      },
      {
        path: "/Front/IntegralList",
        meta: {
          title: "我的积分记录",
          isAdmin: false,
        },
        component: () => import("@/views/Front/IntegralList")
      },
      {
        path: "/Front/UserPerson",
        meta: {
          title: "我的个人信息",
          isAdmin: false,
        },
        component: () => import("@/views/Front/UserPerson")
      },
      {
        path: "/Front/PasswordEdit",
        meta: {
          title: "修改密码",
          isAdmin: false,
        },
        component: () => import("@/views/Front/PasswordEdit")
      },
    ]
  }
]

// 后台的路由
export const adminRouters = [
  {
    path: "/Admin",
    redirect: "/Admin/Home",
    component: AdminLayout,
    meta: {
      title: "控制台",
      isAdmin: true,
    },
    children: [
      {
        path: "Home",
        meta: {
          title: "控制台",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/Home")
      },
      {
        path: "UserList",
        meta: {
          title: "用户信息",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/UserList")
      },
      {
        path: "UserPerson",
        meta: {
          title: "个人信息",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/UserPerson")
      },
      {
        path: "PasswordEdit",
        meta: {
          title: "修改密码",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/PasswordEdit")
      },
      {
        path: "AppointRecordList",
        meta: {
          title: "预约记录",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/AppointRecordList")
      },
      {
        path: "BannerList",
        meta: {
          title: "封面",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/BannerList")
      },
      {
        path: "IntegralList",
        meta: {
          title: "积分",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/IntegralList")
      },
      {
        path: "RoomList",
        meta: {
          title: "自习室",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/RoomList")
      },
      {
        path: "SeatList",
        meta: {
          title: "座位",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/SeatList")
      },
      {
        path: "AppointRoomAppointStatusData",
        meta: {
          title: "自习室预约状态统计",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/AppointRoomAppointStatusData")
      },
      {
        path: "GetIntegralConsumeAndGainChart",
        meta: {
          title: "最近30天积分的获取和使用",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/GetIntegralConsumeAndGainChart")
      },
      {
        path: "GetAppointRoomRealTimeData",
        meta: {
          title: "统计实时在场人数",
          isAdmin: true,
        },
        component: () => import("@/views/Admin/GetAppointRoomRealTimeData")
      },

    ]
  }
];

const router = new VueRouter({
  routes: [...routes, ...frontRouters, ...adminRouters] // (缩写) 相当于 routes: routes
})

// 编写导航路由
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  const hasToken = getToken()
  console.log(hasToken)
  if (hasToken) {
    if (store.getters.HasUserInfo == false) {
      await store.dispatch('GetInfo');
    }
    // 如果去往的页面是后台
    if (to.meta && to.meta.isAdmin) {
      if (store.getters.RoleType == "管理员") {
        next()
      } else {
        next({ path: "/" })
      }
    } else {
      if (store.getters.RoleType == "管理员") {
        next({ path: "/Admin" })
      } else {
        next()
      }
    }
  } else {
    if (to.meta && to.meta.isAdmin) {
      next({ path: "/Login" })
    } else {
      next()
    }
  }
  NProgress.done()
})

router.afterEach(() => {
  // finish progress bar
  NProgress.done()
})

export default router;
