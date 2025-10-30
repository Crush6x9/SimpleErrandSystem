/* eslint-disable */
// 告诉 TypeScript：所有 .vue 文件都是 Vue 组件，应该被识别为 DefineComponent 类型
declare module '*.vue' {
  import { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}
