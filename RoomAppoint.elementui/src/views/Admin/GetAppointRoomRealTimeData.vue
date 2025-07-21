<template>
  <div>

    <div class="echart Card" id="GetAppointRoomRealTimeData" :style="{ float: 'left', width: '100%', height: '500px' }">
    </div>

  </div>
</template>

<script>
import * as echarts from "echarts";
export default {
  name: 'Home',
  data() {
    return {


    };
  },
  mounted() {
    this.GetAppointRoomRealTimeData();

  },
  methods: {
    // 统计各个自习室实时在场人数
    async GetAppointRoomRealTimeData() {
      let { Data } = await this.$Post('/AppointRecord/GetAppointRoomRealTimeData', this.searchForm);


      this.$nextTick(() => {

        let myChart = echarts.init(document.getElementById(`GetAppointRoomRealTimeData`));// 图标初始化

        var option = {
          title: {
            text: '统计各个自习室实时在场人数'
          },
          tooltip: {
            trigger: 'axis'
          },
          toolbox: {
            show: true,
            feature: {
              dataZoom: {
                yAxisIndex: 'none'
              },
              dataView: { readOnly: false },

              restore: {},
              saveAsImage: {}
            }
          },
          xAxis: {
            type: 'category',
            data: Data.map(x => x.Room.Name)
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              data: Data.map(x => x.Value),
              type: 'bar'
            }
          ]
        };

        myChart.setOption(option);// 渲染页面
        //随着屏幕大小调节图表
        window.addEventListener("resize", () => {
          myChart.resize();
        });

      })
    },

  }
};
</script>

<style scoped>
.box-card {
  margin-bottom: 20px;
}

.tb-body {
  display: flex;
  flex-direction: column;
}

.el-form-item {
  margin-bottom: 0;
  /* 去掉默认的 margin-bottom */
}

.echart {
  width: 100%;
  height: 500px;
}
</style>