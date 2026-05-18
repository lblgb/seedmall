/*
 * Vite 配置文件，定义 Vue 插件和测试环境。
 */
import vue from '@vitejs/plugin-vue';
import { defineConfig } from 'vite';

/**
 * 导出 Vite 工程配置。
 */
export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'node'
  }
});
