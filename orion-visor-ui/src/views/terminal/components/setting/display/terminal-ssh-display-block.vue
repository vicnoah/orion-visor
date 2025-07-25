<template>
  <div class="terminal-setting-block">
    <!-- 顶部 -->
    <div class="terminal-setting-subtitle-wrapper">
      <h3 class="terminal-setting-subtitle">
        SSH 显示偏好
      </h3>
    </div>
    <!-- 内容区域 -->
    <div class="terminal-setting-body block-body setting-body">
      <a-form class="terminal-setting-form"
              :model="formModel"
              layout="vertical">
        <a-row :gutter="48">
          <!-- 字体样式 -->
          <a-col :span="12">
            <a-form-item field="fontFamily" label="字体样式">
              <a-select v-model="formModel.fontFamily"
                        placeholder="请选择字体样式"
                        :options="toOptions(fontFamilyKey)"
                        :allow-create="true"
                        :filter-option="labelFilter">
                <!-- label -->
                <template #label="{ data }">
                  <span :style="{ fontFamily: data.value === '_' ? defaultFontFamily : data.value }">{{ data.label }}</span>
                </template>
                <!-- 选项 -->
                <template #option="{ data }">
                  <span :style="{ fontFamily: data.value === '_' ? defaultFontFamily : data.value }">{{ data.label }}</span>
                </template>
              </a-select>
            </a-form-item>
          </a-col>
          <!-- 字体大小 -->
          <a-col :span="12">
            <a-form-item field="fontSize" label="字体大小">
              <a-select v-model="formModel.fontSize"
                        placeholder="请选择字体大小"
                        :options="toOptions(fontSizeKey)" />
            </a-form-item>
          </a-col>
          <!-- 行高 -->
          <a-col :span="12">
            <a-form-item field="lineHeight" label="行高">
              <a-input-number v-model="formModel.lineHeight"
                              placeholder="请输入行高"
                              :precision="2"
                              :step="0.05"
                              :min="1"
                              :max="2" />
            </a-form-item>
          </a-col>
          <!-- 字间距 -->
          <a-col :span="12">
            <a-form-item field="lineHeight" label="字间距 (px)">
              <a-input-number v-model="formModel.letterSpacing"
                              placeholder="请输入字间距"
                              :precision="0"
                              :step="1"
                              :min="-5"
                              :max="5" />
            </a-form-item>
          </a-col>
          <!-- 普通文本字重 -->
          <a-col :span="12">
            <a-form-item field="fontWeight" label="普通文本字重">
              <a-select v-model="formModel.fontWeight"
                        placeholder="请选择字重"
                        :options="toOptions(fontWeightKey)" />
            </a-form-item>
          </a-col>
          <!-- 加粗文本字重 -->
          <a-col :span="12">
            <a-form-item field="fontWeightBold" label="加粗文本字重">
              <a-select v-model="formModel.fontWeightBold"
                        placeholder="请选择字重"
                        :options="toOptions(fontWeightKey)" />
            </a-form-item>
          </a-col>
          <!-- 光标样式 -->
          <a-col :span="12">
            <a-form-item field="cursorStyle" label="光标样式">
              <a-radio-group type="button"
                             v-model="formModel.cursorStyle"
                             class="form-item-cursor-style usn"
                             :options="toRadioOptions(cursorStyleKey)" />
            </a-form-item>
          </a-col>
          <!-- 光标闪烁 -->
          <a-col :span="12">
            <a-form-item field="cursorBlink" label="光标是否闪烁">
              <a-switch v-model="formModel.cursorBlink" type="round" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <!-- 预览区域 -->
      <div class="terminal-example">
        <span class="vertical-form-label">预览效果</span>
        <div class="terminal-example-wrapper"
             :style="{ background: background }">
          <terminal-example :schema="preference.sshTheme.schema"
                            ref="previewTerminal" />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'terminalSshDisplayBlock'
  };
</script>

<script lang="ts" setup>
  import type { TerminalSshDisplaySetting } from '@/store/modules/terminal/types';
  import type { ISshSession } from '@/views/terminal/interfaces';
  import { ref } from 'vue';
  import { useDictStore, useTerminalStore } from '@/store';
  import { fontFamilyKey, fontSizeKey, fontWeightKey, cursorStyleKey, TerminalSessionTypes } from '@/views/terminal/types/const';
  import { labelFilter } from '@/types/form';
  import { defaultFontFamily } from '@/types/xterm';
  import { TerminalPreferenceItem } from '@/store/modules/terminal';
  import useTerminalPreference from '@/views/terminal/types/use-terminal-preference';
  import TerminalExample from '../terminal-example.vue';

  const { toOptions, toRadioOptions } = useDictStore();
  const { preference, sessionManager } = useTerminalStore();

  const background = preference.sshTheme.schema.background;
  const previewTerminal = ref();

  const { formModel } = useTerminalPreference<TerminalSshDisplaySetting>(TerminalPreferenceItem.SSH_DISPLAY_SETTING, true, (v) => {
    if (!v || !Object.keys(v).length) {
      return;
    }
    const options = previewTerminal.value?.term?.options;
    if (!options) {
      return;
    }
    // 修改预览终端配置
    Object.keys(v).forEach(key => {
      if (key === 'fontFamily') {
        const fontFamily = (formModel.value as any)[key];
        options[key] = fontFamily === '_' ? defaultFontFamily : `${fontFamily}, ${defaultFontFamily}`;
      } else {
        options[key] = (formModel.value as any)[key];
      }
    });
    // 修改终端样式
    Object.values(sessionManager.sessions)
      .filter(Boolean)
      .filter(s => s.type === TerminalSessionTypes.SSH.type)
      .map(s => s as ISshSession)
      .forEach(s => {
        const options = s.inst.options;
        s.inst;
        // 修改样式
        Object.keys(v).forEach(k => {
          let value = v[k as keyof TerminalSshDisplaySetting];
          if (k === 'fontFamily') {
            value = value === '_' ? defaultFontFamily : `${value}, ${defaultFontFamily}`;
          }
          options[k as keyof typeof options] = value;
        });
        // 自适应
        s.fit();
      });
    // 聚焦
    previewTerminal.value.term.focus();
  });

</script>

<style lang="less" scoped>
  @terminal-width: 458px;

  .setting-body {
    height: 326px;
    justify-content: space-between;
  }

  :deep(.arco-form) {
    width: 412px;

    .arco-form-item-label {
      user-select: none;
    }

    .arco-form-item {
      margin-bottom: 14px;
    }

    .form-item-cursor-style {

      .arco-radio-button-content {
        padding: 0 24px;
      }
    }
  }

  .terminal-example {
    height: 100%;

    &-wrapper {
      border-radius: 4px;
      width: calc(@terminal-width - 16px);
      height: calc(100% - 16px - 12px);
    }
  }

</style>
