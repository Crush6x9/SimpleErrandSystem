<script setup lang="ts">
const props = defineProps<{
  modelValue: boolean
  orderId?: number
}>()
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', data: { rate: number; comment: string }): void
}>()

const rate = ref(5)
const comment = ref('')

const submit = () => {
  emit('submit', { rate: rate.value, comment: comment.value })
  emit('update:modelValue', false)
}
</script>

<template>
  <van-popup v-model:show="props.modelValue" position="bottom" round :style="{ height: '50%' }">
    <div class="p-6">
      <h3 class="text-lg font-medium text-center mb-4">订单评价</h3>
      <van-rate v-model="rate" size="28" class="justify-center mb-4" />
      <van-field v-model="comment" rows="3" type="textarea" placeholder="写下你的评价（可选）" />
      <van-button round block type="primary" class="mt-4" @click="submit">提交评价</van-button>
    </div>
  </van-popup>
</template>