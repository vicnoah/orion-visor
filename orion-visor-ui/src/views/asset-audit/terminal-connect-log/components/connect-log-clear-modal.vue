<template>
  <a-modal v-model:visible="visible"
           modal-class="modal-form-large"
           title-align="start"
           title="清空终端连接日志"
           :align-center="false"
           :draggable="true"
           :mask-closable="false"
           :unmount-on-close="true"
           ok-text="清空"
           :ok-button-props="{ disabled: loading }"
           :cancel-button-props="{ disabled: loading }"
           :on-before-ok="handlerOk"
           @close="handleClose">
    <a-spin class="full" :loading="loading">
      <a-form :model="formModel"
              label-align="right"
              :auto-label-width="true">
        <!-- 开始时间 -->
        <a-form-item field="startTimeRange" label="开始时间">
          <a-range-picker v-model="formModel.startTimeRange"
                          :time-picker-props="{ defaultValue: ['00:00:00', '23:59:59'] }"
                          show-time
                          format="YYYY-MM-DD HH:mm:ss" />
        </a-form-item>
        <!-- 连接用户 -->
        <a-form-item field="userId" label="连接用户">
          <user-selector v-model="formModel.userId"
                         placeholder="请选择用户"
                         allow-clear />
        </a-form-item>
        <!-- 连接主机 -->
        <a-form-item field="hostId" label="连接主机">
          <host-selector v-model="formModel.hostId"
                         placeholder="请选择主机"
                         allow-clear />
        </a-form-item>
        <!-- 主机地址 -->
        <a-form-item field="hostAddress" label="主机地址">
          <a-input v-model="formModel.hostAddress"
                   placeholder="请输入主机地址"
                   allow-clear />
        </a-form-item>
        <!-- 连接状态 -->
        <a-form-item field="status" label="连接状态">
          <a-select v-model="formModel.status"
                    placeholder="请选择状态"
                    :options="toOptions(connectStatusKey)"
                    allow-clear />
        </a-form-item>
        <!-- 连接类型 -->
        <a-form-item field="type" label="连接类型">
          <a-select v-model="formModel.type"
                    placeholder="请选择类型"
                    :options="toOptions(connectTypeKey)"
                    allow-clear />
        </a-form-item>
        <!-- 数量限制 -->
        <a-form-item field="limit" label="数量限制">
          <a-input-number v-model="formModel.limit"
                          :min="1"
                          :max="maxClearLimit"
                          :placeholder="`请输入数量限制 最大: ${maxClearLimit}`"
                          hide-button
                          allow-clear />
        </a-form-item>
      </a-form>
    </a-spin>
  </a-modal>
</template>

<script lang="ts">
  export default {
    name: 'connectLogClearModal'
  };
</script>

<script lang="ts" setup>
  import type { TerminalConnectLogQueryRequest } from '@/api/terminal/terminal-connect-log';
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import useVisible from '@/hooks/visible';
  import { connectStatusKey, connectTypeKey, maxClearLimit } from '../types/const';
  import { getTerminalConnectLogCount, clearTerminalConnectLog } from '@/api/terminal/terminal-connect-log';
  import { Message, Modal } from '@arco-design/web-vue';
  import { useDictStore } from '@/store';
  import UserSelector from '@/components/user/user/selector/index.vue';
  import HostSelector from '@/components/asset/host/selector/index.vue';

  const emits = defineEmits(['clear']);

  const { toOptions } = useDictStore();
  const { visible, setVisible } = useVisible();
  const { loading, setLoading } = useLoading();

  const defaultForm = (): TerminalConnectLogQueryRequest => {
    return {
      userId: undefined,
      hostId: undefined,
      hostAddress: undefined,
      type: undefined,
      status: undefined,
      startTimeRange: undefined,
      limit: maxClearLimit,
    };
  };

  const formModel = ref<TerminalConnectLogQueryRequest>({});

  // 打开
  const open = (record: any) => {
    renderForm({ ...defaultForm(), ...record });
    setVisible(true);
  };

  // 渲染表单
  const renderForm = (record: any) => {
    formModel.value = Object.assign({}, record);
  };

  defineExpose({ open });

  // 确定
  const handlerOk = async () => {
    if (!formModel.value.limit) {
      Message.error('请输入数量限制');
      return false;
    }
    setLoading(true);
    try {
      // 获取总数量
      const { data } = await getTerminalConnectLogCount(formModel.value);
      if (data) {
        // 清空
        doClear(data);
      } else {
        // 无数据
        Message.warning('当前条件未查询到数据');
      }
    } catch (e) {
    } finally {
      setLoading(false);
    }
    return false;
  };

  // 执行删除
  const doClear = (count: number) => {
    Modal.confirm({
      title: '删除清空',
      content: `确定要删除 ${count} 条数据吗? 确定后将立即删除且无法恢复!`,
      onOk: async () => {
        setLoading(true);
        try {
          // 调用清空
          const { data } = await clearTerminalConnectLog(formModel.value);
          Message.success(`已成功清空 ${data} 条数据`);
          emits('clear');
          // 清空
          setVisible(false);
          handlerClear();
        } catch (e) {
        } finally {
          setLoading(false);
        }
      }
    });
  };

  // 关闭
  const handleClose = () => {
    handlerClear();
  };

  // 清空
  const handlerClear = () => {
    setLoading(false);
  };

</script>

<style lang="less" scoped>

</style>
