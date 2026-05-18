/*
 * Vue 应用启动入口，挂载 SeedMall 前端工作台。
 */
import { createApp } from 'vue';
import App from './App.vue';
import './styles.css';

/**
 * 创建并挂载 Vue 应用。
 */
createApp(App).mount('#app');
