<template>
  <div>
    <div class="margin-top-lg Card">
      <el-carousel :interval="carouselInterval" arrow="always" @change="resetInterval">
        <el-carousel-item v-for="(item, index) in BannerList" :key="item">
          <div>
            <img :src="item.Cover" class="carousel-image">
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>
    <div class="Card margin-top-lg">
      <div class="item-header">自习室列表</div>
      <Pagination url="/Room/List">
        <template v-slot:content="{ data }">
          <div class="room-list">
            <div class="room-item" v-for="(item, index) in data" :key="index" @click="ToRoom(item)">
              <img class="room-cover" :src="item.Cover">
              <div class="text-container">
                <span class="tit">{{ item.Name }}</span>
              </div>
            </div>
          </div>
        </template>
      </Pagination>
    </div>
  </div>
</template>

<script>
import Pagination from '@/components/Pagination/PaginationBox.vue';
import BannerList from '../Admin/BannerList.vue';

export default {
  components: {
    Pagination
  },
  data() {
    return {
      BannerList: [],
      RoomList: [],
      carouselInterval: 3000 // 轮播图切换时间间隔，3秒
    };
  },
  created() {
    this.BannerListApi();
  },
  methods: {

    async BannerListApi() {
      let { Data: { Items } } = await this.$Post("/Banner/List", {});
      this.BannerList = Items;
    },

    // 重置轮播图间隔时间，当轮播图切换时
    resetInterval() {
      this.carouselInterval = 3000; // 重置为3秒
    },

    // 跳转到自习室详情
    async ToRoom(item) {
      this.$router.push({
        path: '/Front/Room',
        query: {
          RoomId: item.Id
        }
      })
    }
  },
};
</script>

<style scoped>
.room-list {
  display: flex;
  flex-wrap: wrap;
  margin-left: 10px;
}

.room-list .room-item {
  width: 260px;
  margin-bottom: 22px;
  margin-left: 42px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  /* 居中对齐 */
  justify-content: center;
}

.room-list .room-item:hover {
  transform: scale(1.03);
  transition: all 0.35s ease;
}

.room-list .room-item .room-cover {
  width: 260px;
  height: 260px;
  border-radius: 20px;
}

.room-list .room-item .text-container {
  display: flex;
  justify-content: center;
  /* 水平居中 */
  align-items: center;
  /* 垂直居中 */
  width: 100%;
  padding-top: 10px;
  /* 图片和文字之间的间距 */
}

.room-list .room-item .tit {
  font-weight: bold;
  width: 100%;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  /* -webkit-line-clamp: 2; */
  overflow: hidden;
  text-align: center;
  text-overflow: ellipsis;
}

.Card {
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  height: 100%;
  border-radius: 10px;
  padding: 30px;
  background: white;
}

.item-header {
  border-left: 5px solid rgb(17, 144, 255);
  padding-left: 5px;
  font-weight: bolder;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: fill;
  /* 允许拉伸或变形，填满容器 */
}
</style>
