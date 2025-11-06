// src/types/order.ts
export enum OrderStatus {
  PENDING_PAYMENT = 0,  // 待支付
  IN_PROGRESS = 1,      // 进行中
  PENDING_REVIEW = 2,   // 待评价
  COMPLETED = 3          // 已完成
}

export interface Order {
  id: number
  title: string
  address: string
  price: number
  status: OrderStatus
  createTime: string
  expectTime?: string
}

export interface CreateOrderRequest {
  title: string
  address: string
  price: number
  expectTime: string
}