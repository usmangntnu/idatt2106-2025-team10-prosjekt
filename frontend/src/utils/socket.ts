import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import type { IMessage, Frame } from '@stomp/stompjs'

const BACKEND_BASE = import.meta.env.VITE_API_BASE_URL_SOCKET

export function createWebSocket(onEvent: (evt: any) => void) {
  const sockUrl = `${BACKEND_BASE}/ws`
  console.log('[WS] Forsøker å koble til', sockUrl)

  const client = new Client({
    webSocketFactory: () =>
      new SockJS(sockUrl, undefined, { withCredentials: true }),
    reconnectDelay: 5000,

    onWebSocketError: (evt) => console.error('[WS] WebSocket error:', evt),
    onStompError: (frame: Frame) => {
      console.error('[WS] STOMP error:', frame.headers['message'], frame.body)
    },
    onWebSocketClose: (evt) => console.warn('[WS] WebSocket closed:', evt),

    onConnect: (frame: Frame) => {
      console.log('[WS] STOMP CONNECTED:', frame.headers)
      client.subscribe('/topic/events', (msg: IMessage) => {
        console.log('[WS] Raw MESSAGE:', msg)
        if (msg.body) {
          try {
            const payload = JSON.parse(msg.body)
            console.log('[WS] Parsed payload:', payload)
            onEvent({ topic: '/topic/events', data: payload })
          } catch (err) {
            console.error('[WS] Kunne ikke parse msg.body', err)
          }
        }
        console.log('[WS] Subscribed to /topic/events')
      })
      client.subscribe('/topic/events/delete', (msg: IMessage) => {
        console.log('[WS] Raw MESSAGE:', msg)
        if (msg.body) {
          try {
            const payload = JSON.parse(msg.body)
            console.log('[WS] Parsed payload:', payload)
            onEvent({ topic: '/topic/events/delete', data: payload })
          } catch (err) {
            console.error('[WS] Kunne ikke parse msg.body', err)
          }
        }
        console.log('[WS] Subscribed to /topic/events/delete')
      })
      client.subscribe('/topic/positions', (msg: IMessage) => {
        console.log('[WS] Raw MESSAGE:', msg)
        if (msg.body) {
          try {
            const payload = JSON.parse(msg.body)
            console.log('[WS] Parsed payload:', payload)
            onEvent({ topic: '/topic/positions', data: payload })
          } catch (err) {
            console.error('[WS] Kunne ikke parse msg.body', err)
          }
        }
        console.log('[WS] Subscribed to /topic/positions')
      })
      client.subscribe('/topic/positions/delete', (msg: IMessage) => {
        console.log('[WS] Raw MESSAGE:', msg)
        if (msg.body) {
          try {
            const payload = JSON.parse(msg.body)
            console.log('[WS] Parsed payload:', payload)
            onEvent({ topic: '/topic/positions/delete', data: payload })
          } catch (err) {
            console.error('[WS] Kunne ikke parse msg.body', err)
          }
        }
        console.log('[WS] Subscribed to /topic/positions/delete')
      })

      client.subscribe('/topic/notifications', (msg: IMessage) => {
        if (msg.body) {
          const payload = JSON.parse(msg.body)
          onEvent({ topic: '/topic/notifications', data: payload })
        }
      })

      client.subscribe('/topic/notifications/delete', (msg: IMessage) => {
        if (msg.body) {
          // Usually, delete just sends an ID
          const payload = JSON.parse(msg.body)
          onEvent({ topic: '/topic/notifications/delete', data: payload })
        }
      })
    },
  })

  client.activate()
  return client
}
