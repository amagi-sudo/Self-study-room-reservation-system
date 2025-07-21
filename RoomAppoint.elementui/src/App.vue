<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>

export default {
  name: 'App',

}
</script>

<style>
@import './css/index.css';
@import "./css/font/iconfont.css";


/* .bg-header {
  background-color: rgb(17, 144, 255);
  display: flex;
  height: 60px;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  color: white;
} */


/* -xs -sm  -md  -lg*/
body {
  background-color: rgb(231, 231, 233);
}

.pointer {
  cursor: pointer;
}

.Card {
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  height: 100%;
  border-radius: 10px;
  padding: 30px;
  background: white;
}

.margin-top-xs {
  margin-top: 5px;
}

.margin-top-lg {
  margin-top: 20px;
}

.margin-bottom-xs {
  margin-bottom: 5px;
}

.margin-bottom-lg {
  margin-bottom: 20px;
}
</style>
