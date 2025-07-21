<template>
  <div v-if="RoomDetail">
    <el-page-header class="Card margin-top-lg" @back="goBack" :content="RoomDetail.Name">
    </el-page-header>
    <div class=" Card margin-top-lg margin-bottom-lg">
      <div>
        <img :src="RoomDetail.Cover" style="width: 100%;border-radius: 10px;">
      </div>
      <div class="container">
        <el-tabs v-model="activeName">
          <el-tab-pane label="详情介绍" name="详情介绍">
            <div class="content" v-html="RoomDetail.Content"></div>
          </el-tab-pane>
          <el-tab-pane label="选座列表" name="选座列表">
            <h4>先选择日期</h4>
            <el-tabs v-model="SelectDate" @tab-click="handleClick">
              <el-tab-pane v-for="(item, index) in DateList" :key="item.id || index" :label="item" :name="item">
              </el-tab-pane>
            </el-tabs>
            <template v-if="SeatArrange">
              <h4>上午</h4>
              <em class="tip">如果有人提前离开了，座位是会被释放的</em>
              <el-empty v-if="SeatArrange.AmSeatDtoList.length == 0" description="暂时没有数据"></el-empty>
              <div class="seat-list">
                <div class="seat-row" v-for="(row, rowIndex) in SeatArrange.AmSeatDtoList">
                  <div class="seat-col" v-for="(col, colIndex) in row">
                    <div v-if="col.Id" @click="ToAppoint(col, 1)">
                      <img v-if="col.IsOccupy" :src="require('@/assets/seatCheck.png')" alt="已占用">
                      <img v-else :src="require('@/assets/seat.png')" alt="未占用">
                      <div class="tit"><span>{{ col.No }}</span></div>
                    </div>
                  </div>
                </div>
              </div>

              <h4>下午</h4>
              <em class="tip">如果有人提前离开了，座位是会被释放的</em>
              <el-empty v-if="SeatArrange.PmSeatDtoList.length == 0" description="暂时没有数据"></el-empty>
              <div class="seat-list">
                <div class="seat-row" v-for="(row, rowIndex) in SeatArrange.PmSeatDtoList">
                  <div class="seat-col" v-for="(col, colIndex) in row" :key="col.Id">
                    <div v-if="col.Id" @click="ToAppoint(col, 2)">
                      <img v-if="col.IsOccupy" :src="require('@/assets/seatCheck.png')" alt="已占用">
                      <img v-else :src="require('@/assets/seat.png')" alt="未占用">
                      <div class="tit"><span>{{ col.No }}</span></div>
                    </div>
                  </div>
                </div>
              </div>
              <h4>晚上</h4>
              <em class="tip">如果有人提前离开了，座位是会被释放的</em>
              <el-empty v-if="SeatArrange.NmSeatDtoList.length == 0" description="暂时没有数据"></el-empty>
              <div class="seat-list">
                <div class="seat-row" v-for="(row, rowIndex) in SeatArrange.NmSeatDtoList">
                  <div class="seat-col" v-for="(col, colIndex) in row" :key="col.Id">
                    <div v-if="col.Id" @click="ToAppoint(col, 3)">
                      <img v-if="col.IsOccupy" :src="require('@/assets/seatCheck.png')" alt="已占用">
                      <img v-else :src="require('@/assets/seat.png')" alt="未占用">
                      <div class="tit"><span>{{ col.No }}</span></div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </el-tab-pane>
          <el-tab-pane label="使用评价" name="使用评价">
            <Pagination url="/AppointRecord/List" :where="{ RoomId: RoomId, AppointStatus: 3 }">
              <template v-slot:content="{ data }">
                <div class="comment-list">
                  <div class="comment-item" v-for="(item, index) in data" :key="index" v-id="item.UserDto">
                    <div class="comment-header">
                      <img class="head"
                        :src="item.UserDto.ImageUrls ? item.UserDto.ImageUrls : require('@/assets/空头像.png')">
                      <div class="user-info">
                        <span class="name">{{ item.UserDto.Name }}</span>
                        <el-rate disabled v-model="item.CommentScore"></el-rate>
                        <em class="seat">当时得到选座{{ item.SeatDto && item.SeatDto.No }}号位</em>
                      </div>
                    </div>
                    <div class="content">
                      {{ item.SComment }}
                    </div>
                  </div>
                </div>
              </template>
            </Pagination>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import store from '@/store';

import { mapGetters } from 'vuex';
import Pagination from '@/components/Pagination/PaginationBox.vue';

export default {
  components: {
    Pagination
  },
  name: 'Room',
  computed: {
    ...mapGetters(['UserInfo', "Token", "UserId"])
  },
  data() {
    return {
      activeName: "选座列表",
      RoomId: undefined,
      RoomDetail: null,
      DateList: [],
      SeatArrange: null,
      SelectDate: null,


    }
  },
  created() {
    this.RoomId = this.$route.query.RoomId;
    this.GetRoomApi();
    this.GetSevenDaysApi();


  },
  methods: {
    //得到自习室详情接口
    async GetRoomApi() {
      let { Data } = await this.$Post("/Room/Get", { Id: this.RoomId });
      this.RoomDetail = Data;
    },
    // 得到最近7天
    async GetSevenDaysApi() {
      let { Data } = await this.$Post("/Seat/GetSevenDays", {});
      this.DateList = Data;
      this.SelectDate = this.DateList[0];
      this.GetArrange();

    },
    // 得到选中日期的座位信息
    async GetArrange() {
      let { Data } = await this.$Post('/Seat/GetArrange', { RoomId: this.RoomId, SelectDate: this.SelectDate + " 00:00:00" }, { params: { _random: Date.now() } });
      this.SeatArrange = Data;

      // 将所有座位的 IsOccpuy 转换为布尔值
      this.SeatArrange.AmSeatDtoList.forEach(row => {
        row.forEach(col => {
          col.IsOccpuy = Boolean(col.IsOccpuy); // 确保 IsOccpuy 为布尔值
        });
      });

      this.SeatArrange.PmSeatDtoList.forEach(row => {
        row.forEach(col => {
          col.IsOccpuy = Boolean(col.IsOccpuy);
        });
      });

      this.SeatArrange.NmSeatDtoList.forEach(row => {
        row.forEach(col => {
          col.IsOccpuy = Boolean(col.IsOccpuy);
        });
      });
    },

    // 选中日期
    async handleClick(e) {
      console.log(e);
      this.GetArrange();
    },
    // 返回上一页
    goBack() {
      this.$router.go(-1);
    },
    // 去预约
    async ToAppoint(col, type) {
      if (col.IsOccpuy) {
        return;
      }
      if (!this.Token) {
        this.$message.warning("请先登录后再操作")
        return;
      }

      let body = {
        RoomId: this.RoomId,
        UserId: this.UserId,
        SeatId: col.Id,
        AppointDateType: type,
        AppointDate: this.SelectDate + " 00:00:00"
      };



      let { Success } = await this.$Post("/AppointRecord/CheckIsAbleAppoint", body);
      let tick = new Date().getTime();
      localStorage.setItem(tick, JSON.stringify(body));


      if (Success) {
        this.$router.push({
          path: '/Front/ToOrder',
          query: {
            tick: tick
          }
        })
      }
    },

  }
}
</script>

<style>
.content {
  font-family: Arial, sans-serif;
  font-size: 14px;
  line-height: 50px;
  color: #333;
}

.seat-list {
  margin: 0 auto;
}

.seat-list .seat-row {
  display: flex;
  margin: 0 auto;
  width: fit-content;
  cursor: pointer;
}

.seat-list .seat-row .seat-col {
  margin-top: 20px;
  margin-right: 20px;
  width: 70px;
  height: 70px;
}

.seat-list .seat-row .seat-col img {
  width: 40px;
  height: 40px;
  pointer-events: none;
}

.seat-list .seat-row .seat-col .tit {
  font-weight: bold;
}

.comment-list {
  width: 100%;
}

.comment-list .comment-item {
  display: flex;
  flex-direction: column;
  /* 使用列布局 */
  margin-bottom: 20px;
  /* 增加间距 */
  border-bottom: 2px dashed #b0b0b0;
}

.comment-list .comment-item .head {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 10px;
  /* 头像与用户信息之间的间距 */
}

.comment-list .comment-item .user-info {
  display: flex;
  align-items: center;
  /* 垂直居中 */
  margin-bottom: 10px;
  /* 用户信息与评论内容之间的间距 */
}

.comment-list .comment-item .name {
  font-weight: bold;
  color: darkblue;
  margin-right: 10px;
  /* 名字与评分之间的间距 */
}

.comment-list .comment-item .el-rate {
  /* 评分组件的样式，根据需要调整 */
}

.comment-list .comment-item .content {
  font-family: Arial, sans-serif;
  font-size: 16px;
  font-weight: 200;
  /* 将字体权重调整为200，使文字更细 */
  line-height: 1.5;
  color: #333;
  padding-left: 20px;
}

.comment-list .comment-item .seat {
  font-size: 13px;
  margin-left: 20px;
  color: #535252;
}

.tip {
  color: blue;
  font-size: 15px;
}
</style>