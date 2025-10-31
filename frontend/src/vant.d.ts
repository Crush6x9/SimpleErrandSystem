// src/vant.d.ts
declare module 'vant' {
  import { Plugin } from 'vue';
  const Vant: Plugin;
  export default Vant;
}

declare module 'vant/es' {
  export * from 'vant';
}

declare module 'vant/es/*' {
  import { Component } from 'vue';
  const component: Component;
  export default component;
}