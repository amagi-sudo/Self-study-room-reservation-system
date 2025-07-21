<template>
  <div>

    <div class="echart Card" id="GetIntegralConsumeAndGainChart"
      :style="{ float: 'left', width: '100%', height: '500px' }">
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
    this.GetIntegralConsumeAndGainChart();

  },
  methods: {
    // 统计最近30天积分的消耗和获取的折线图
    async GetIntegralConsumeAndGainChart() {
      let { Data } = await this.$Post('/Integral/GetIntegralConsumeAndGainChart', this.searchForm);
      this.Datalist = Data;

      this.$nextTick(() => {

        let myChart = echarts.init(document.getElementById(`GetIntegralConsumeAndGainChart`));// 图标初始化

        var option = {
          title: {
            text: '最近30天积分的获取和使用'
          },
          tooltip: {
            trigger: 'axis'
          },
          legend: {},
          toolbox: {
            show: true,
            feature: {
              dataZoom: {
                yAxisIndex: 'none'
              },
              dataView: { readOnly: false },
              magicType: { type: ['line', 'bar'] },
              restore: {},
              saveAsImage: {}
            }
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: Data.map(x => x.Date)
          },
          yAxis: {
            type: 'value',
            axisLabel: {
              formatter: '{value} 积分'
            }
          },
          series: [
            {
              name: '获取积分',
              type: 'line',
              data: Data.map(x => x.GainIntegral),
              markPoint: {
                data: [
                  { type: 'max', name: 'Max' },
                  { type: 'min', name: 'Min' }
                ]
              },
              markLine: {
                data: [{ type: 'average', name: 'Avg' }]
              }
            },
            {
              name: '使用积分',
              type: 'line',
              data: Data.map(x => x.UseIntegral),
              markPoint: {
                data: [{ name: '月最低', value: -2, xAxis: 2, yAxis: -1.5 }]
              },
              markLine: {
                data: [
                  { type: 'average', name: 'Avg' },
                  [
                    {
                      symbol: 'none',
                      x: '90%',
                      yAxis: 'max'
                    },
                    {
                      symbol: 'circle',
                      label: {
                        position: 'start',
                        formatter: 'Max'
                      },
                      type: 'max',
                      name: '最高点'
                    }
                  ]
                ]
              }
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