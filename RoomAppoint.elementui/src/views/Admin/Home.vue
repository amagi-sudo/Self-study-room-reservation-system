<template>
  <div>
    <el-row>
      <el-col :span="12">
        <div class="grid-content bg-purple"></div>
      </el-col>
      <el-col :span="12">
        <div class="grid-content bg-purple-light"></div>
      </el-col>
    </el-row>
    <el-card class="box-card margin-top-lg">
      <div slot="header" class="clearfix">
        <span>数据统计</span>
      </div>
      <div class="board-list" v-if="DataCollect != null">
        <div class="board-item">
          <div class="tit">自习室个数</div>
          <div class="num">{{ DataCollect.RoomCount }}</div>
        </div>
        <div class="board-item">
          <div class="tit">总座位数</div>
          <div class="num">{{ DataCollect.SeatCount }}</div>
        </div>
        <div class="board-item">
          <div class="tit">总预约人次</div>
          <div class="num">{{ DataCollect.AppointCount }}</div>
        </div>

        <div class="board-item">
          <div class="tit">待使用次数</div>
          <div class="num">{{ DataCollect.WaitAppointCount }}</div>
        </div>
        <div class="board-item">
          <div class="tit">逾期人次</div>
          <div class="num">{{ DataCollect.OverdueCount }}</div>
        </div>

        <div class="board-item">
          <div class="tit">总剩余积分</div>
          <div class="num">{{ DataCollect.TotalIntegral }}</div>
        </div>



      </div>
    </el-card>

    <el-card class="box-card margin-top-lg">
      <div slot="header" class="clearfix">
        <span>今日每个自习室早中晚的使用率（%）</span>
      </div>
      <div class="echart" id="echartDiv" :style="{ float: 'left', width: '100%', height: '500px' }">
      </div>
    </el-card>

    <el-card class="box-card margin-top-lg">
      <div slot="header" class="clearfix">
        <span>欢迎您查看使用日历</span>
      </div>

      <el-row :gutter="10">
        <el-col :span="12">
          <el-calendar></el-calendar>
        </el-col>
        <el-col :span="12">
          <el-descriptions title="系统简介" direction="horizontal" :column="1" border>
            <el-descriptions-item label="系统名称">基于SpringBoot+Vue的自习室管理系统设计与实现</el-descriptions-item>
            <el-descriptions-item label="后端">SpringBoot3.3</el-descriptions-item>
            <el-descriptions-item label="前端">Vue</el-descriptions-item>
            <el-descriptions-item label="数据库">Mysql</el-descriptions-item>
            <el-descriptions-item label="学校">
              <el-tag size="small">东莞理工</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="Github地址"><a
                href="https://github.com/amagi-sudo">点击跳转</a></el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>

    </el-card>


  </div>
</template>

<script>
import * as echarts from "echarts";
export default {
  name: 'Home',
  props: {

  },
  data() {
    return {
      DataCollect: null
    };
  },
  created() {

  },
  mounted() {
    this.GetAppointRoomUseRate();
    this.GetDataCollect();
  },
  methods: {
    //统计早中晚每个自习室的使用率&&空闲率
    async GetAppointRoomUseRate() {
      let { Data } = await this.$Post('AppointRecord/GetAppointRoomUseRate', {});
      console.log(Data);
      let myChart = echarts.init(document.getElementById("echartDiv"));// 图标初始化
      const posList = [
        'left',
        'right',
        'top',
        'bottom',
        'inside',
        'insideTop',
        'insideLeft',
        'insideRight',
        'insideBottom',
        'insideTopLeft',
        'insideTopRight',
        'insideBottomLeft',
        'insideBottomRight'
      ];
      app.configParameters = {
        rotate: {
          min: -90,
          max: 90
        },
        align: {
          options: {
            left: 'left',
            center: 'center',
            right: 'right'
          }
        },
        verticalAlign: {
          options: {
            top: 'top',
            middle: 'middle',
            bottom: 'bottom'
          }
        },
        position: {
          options: posList.reduce(function (map, pos) {
            map[pos] = pos;
            return map;
          }, {})
        },
        distance: {
          min: 0,
          max: 100
        }
      };
      app.config = {
        rotate: 90,
        align: 'left',
        verticalAlign: 'middle',
        position: 'insideBottom',
        distance: 15,
        onChange: function () {
          const labelOption = {
            rotate: app.config.rotate,
            align: app.config.align,
            verticalAlign: app.config.verticalAlign,
            position: app.config.position,
            distance: app.config.distance
          };
          myChart.setOption({
            series: [
              {
                label: labelOption
              },
              {
                label: labelOption
              },
              {
                label: labelOption
              },
              {
                label: labelOption
              }
            ]
          });
        }
      };
      const labelOption = {
        show: true,
        position: app.config.position,
        distance: app.config.distance,
        align: app.config.align,
        verticalAlign: app.config.verticalAlign,
        rotate: app.config.rotate,
        formatter: '{c}  {name|{a}}',
        fontSize: 16,
        rich: {
          name: {}
        }
      };
      let option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['上午', '下午', '夜晚']
        },
        toolbox: {
          show: true,
          orient: 'vertical',
          left: 'right',
          top: 'center',
          feature: {
            mark: { show: true },
            dataView: { show: true, readOnly: false },
            magicType: { show: true, type: ['line', 'bar', 'stack'] },
            restore: { show: true },
            saveAsImage: { show: true }
          }
        },
        xAxis: [
          {
            type: 'category',
            axisTick: { show: false },
            data: Data.map(x => x.Room.Name)
          }
        ],
        yAxis: [
          {
            type: 'value',
            axisLabel: {
              formatter: '{value} %' //在这里设置y轴标签的格式，添加单位
            }
          }
        ],
        series: [
          {
            name: '上午',
            type: 'bar',
            barGap: 0,
            label: labelOption,
            emphasis: {
              focus: 'series'
            },
            data: Data.map(x => x.AmUseRate)
          },
          {
            name: '下午',
            type: 'bar',
            label: labelOption,
            emphasis: {
              focus: 'series'
            },
            data: Data.map(x => x.PmUseRate)
          },
          {
            name: '夜晚',
            type: 'bar',
            label: labelOption,
            emphasis: {
              focus: 'series'
            },
            data: Data.map(x => x.NmUseRate)
          },

        ]
      };


      myChart.setOption(option);// 渲染页面
      //随着屏幕大小调节图表
      window.addEventListener("resize", () => {
        myChart.resize();
      });


    },
    //统计各类数据到看板
    async GetDataCollect() {
      let { Data } = await this.$Post('AppointRecord/GetDateCollect', {});
      this.DataCollect = Data;
    }
  }
}       
</script>


<style scoped>
.board-list {
  display: flex;

}

.board-list .board-item {
  text-align: center;
  padding: 10px;
  border-radius: 10px;
  color: rgb(249, 245, 245);
  margin-right: 20px;
  width: 150px;
}

.board-list .board-item:nth-child(1) {
  background-color: rgba(19, 162, 115, 0.671);
}

.board-list .board-item:nth-child(2) {
  background-color: rgb(148, 127, 255);
}

.board-list .board-item:nth-child(3) {
  background-color: rgb(255, 127, 138);
}

.board-list .board-item:nth-child(4) {
  background-color: rgb(244, 130, 17);
}

.board-list .board-item:nth-child(5) {
  background-color: rgba(62, 244, 17, 0.707);
}

.board-list .board-item:nth-child(6) {
  background-color: rgba(244, 55, 17, 0.707);
}

.board-list .board-item:nth-child(7) {
  background-color: rgba(17, 191, 244, 0.848);
}

.board-list .board-item:nth-child(8) {
  background-color: rgba(244, 17, 142, 0.707);
}

.board-list .board-item .tit {}

.board-list .board-item .num {
  margin-top: 5px;
  font-weight: bolder;
}
</style>