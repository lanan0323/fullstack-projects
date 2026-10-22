<template>
  <div class="ai-assistant">
    <!-- 悬浮入口 -->
    <div class="ai-fab" @click="show = true">
      <van-icon name="chat-o" size="26" />
      <span>AI助手</span>
    </div>

    <!-- 聊天弹窗 -->
    <van-popup v-model:show="show" position="bottom" round :style="{ height: '72%' }">
      <div class="chat-panel">
        <div class="chat-head">
          <div class="head-info">
            <span class="head-name">茶仔 · AI点单助手</span>
            <span class="head-sub">基于门店菜单与活动知识库</span>
          </div>
          <van-icon name="cross" size="18" @click="show = false" />
        </div>

        <div class="chat-body" ref="bodyRef">
          <div
            v-for="(msg, i) in messages"
            :key="i"
            class="msg-row"
            :class="msg.role === 'user' ? 'right' : 'left'"
          >
            <div class="bubble" :class="msg.role">
              <span>{{ msg.content }}</span>
              <div v-if="msg.refs && msg.refs.length" class="refs">
                <span v-for="r in msg.refs" :key="r" class="ref-tag">{{ r }}</span>
              </div>
            </div>
          </div>
          <div v-if="loading" class="msg-row left">
            <div class="bubble assistant typing">
              <van-loading size="14" /> <span>茶仔思考中…</span>
            </div>
          </div>
        </div>

        <!-- 快捷提问 -->
        <div class="quick-asks" v-if="messages.length <= 1 && !loading">
          <span
            v-for="q in quickAsks"
            :key="q"
            class="quick-chip"
            @click="send(q)"
          >{{ q }}</span>
        </div>

        <div class="chat-input">
          <van-field
            v-model="draft"
            placeholder="想喝什么？问我呀～"
            maxlength="100"
            @keyup.enter="send()"
          />
          <van-button type="primary" :disabled="loading || !draft.trim()" @click="send()">
            发送
          </van-button>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { nextTick, ref } from 'vue'
import { showToast } from 'vant'
import { askAssistant } from '../api/ai'

const show = ref(false)
const draft = ref('')
const loading = ref(false)
const bodyRef = ref(null)
const quickAsks = ['推荐一杯不太甜的奶茶', '9.9 特价有什么', '优惠券怎么用']

const messages = ref([
  { role: 'assistant', content: '你好呀，我是茶仔～想喝点什么？告诉我口味或预算，我来帮你挑！' }
])

const scrollBottom = () => {
  nextTick(() => {
    if (bodyRef.value) bodyRef.value.scrollTop = bodyRef.value.scrollHeight
  })
}

const send = async preset => {
  const question = (preset || draft.value).trim()
  if (!question || loading.value) return
  draft.value = ''
  messages.value.push({ role: 'user', content: question })
  scrollBottom()
  loading.value = true
  try {
    // 只携带最近 6 条，节省 token
    const history = messages.value.slice(-7, -1).map(m => ({ role: m.role, content: m.content }))
    const res = await askAssistant({ question, history })
    messages.value.push({ role: 'assistant', content: res.data.answer, refs: res.data.references })
  } catch (e) {
    messages.value.push({ role: 'assistant', content: '网络好像不太顺畅，稍后再试试吧～' })
    showToast('AI 助手暂时不可用')
  } finally {
    loading.value = false
    scrollBottom()
  }
}
</script>

<style scoped>
.ai-fab {
  position: fixed;
  right: 14px;
  bottom: 96px;
  z-index: 99;
  width: 54px;
  height: 54px;
  border-radius: 50%;
  background: var(--brand-grad);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  gap: 1px;
  box-shadow: 0 4px 12px rgba(58, 158, 99, 0.4);
  cursor: pointer;
}
.chat-panel {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 14px;
}
.chat-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eef4f0;
}
.head-name {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: var(--brand-deep);
}
.head-sub {
  font-size: 11px;
  color: #9aa8a0;
}
.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 12px 2px;
}
.msg-row {
  display: flex;
  margin-bottom: 10px;
}
.msg-row.right {
  justify-content: flex-end;
}
.bubble {
  max-width: 78%;
  padding: 9px 12px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.55;
  white-space: pre-wrap;
  word-break: break-word;
}
.bubble.assistant {
  background: #fff;
  color: #2b2320;
  border: 1px solid #e8f0ea;
  border-top-left-radius: 4px;
}
.bubble.user {
  background: var(--brand-grad);
  color: #fff;
  border-top-right-radius: 4px;
}
.bubble.typing {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #8aa596;
}
.refs {
  margin-top: 6px;
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
.ref-tag {
  font-size: 10px;
  color: var(--brand-deep);
  background: var(--brand-light);
  border-radius: 4px;
  padding: 1px 6px;
}
.quick-asks {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  padding: 6px 0;
}
.quick-chip {
  font-size: 12px;
  color: var(--brand-deep);
  background: var(--brand-light);
  border-radius: 999px;
  padding: 5px 12px;
  cursor: pointer;
}
.chat-input {
  display: flex;
  gap: 8px;
  align-items: center;
  padding-top: 8px;
  border-top: 1px solid #eef4f0;
}
.chat-input .van-field {
  flex: 1;
  background: #f5faf7;
  border-radius: 8px;
  padding: 6px 10px;
}
</style>
