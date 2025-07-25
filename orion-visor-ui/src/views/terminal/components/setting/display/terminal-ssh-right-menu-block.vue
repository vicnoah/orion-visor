<template>
  <div class="terminal-setting-block">
    <!-- 顶部 -->
    <div class="terminal-setting-subtitle-wrapper">
      <h3 class="terminal-setting-subtitle">
        SSH 右键菜单设置
      </h3>
    </div>
    <!-- 提示 -->
    <a-alert class="mb16">修改后会立刻保存, 重新打开终端后生效</a-alert>
    <!-- 非安全环境提示 -->
    <a-alert v-if="!isSecureEnvironment"
             type="warning"
             class="mb16">
      当前环境非 HTTPS 环境, 因浏览器安全策略限制, 自定义 '粘贴' 功能无法使用
    </a-alert>
    <!-- 内容区域 -->
    <div class="terminal-setting-body block-body setting-body">
      <!-- 功能项 -->
      <div class="actions-container">
        <div class="vertical-form-label">功能</div>
        <!-- 功能项列表 -->
        <div class="actions-wrapper">
          <a-row :gutter="[8, 8]">
            <a-col :span="12"
                   class="action-item-wrapper"
                   v-for="(action, index) in SshActionBarItems"
                   :key="index">
              <div class="action-item" @click="toggleAction(action.item)">
                <!-- 图标 -->
                <div class="action-icon">
                  <component :is="action.icon" />
                </div>
                <!-- 描述 -->
                <div class="action-desc">
                  {{ action.content }}
                </div>
              </div>
            </a-col>
          </a-row>
        </div>
      </div>
      <!-- 菜单预览容器 -->
      <div class="preview-container">
        <div class="vertical-form-label">菜单预览</div>
        <div ref="popupContainer" />
      </div>
      <!-- 预览下拉菜单 -->
      <a-dropdown v-if="popupContainer"
                  :popup-visible="true"
                  :popup-container="popupContainer"
                  :popup-max-height="false">
        <template #content v-if="rightActions.length">
          <a-doption v-for="(action, index) in rightActions"
                     :key="index">
            <div class="preview-action">
              <!-- 图标 -->
              <div class="preview-icon">
                <component :is="action.icon" />
              </div>
              <!-- 文本 -->
              <div>{{ action.content }}</div>
            </div>
            <!-- 关闭按钮 -->
            <div class="close-icon" @click="toggleAction(action.item)">
              <icon-close />
            </div>
          </a-doption>
        </template>
        <!-- 空数据 -->
        <template #content v-else>
          <a-doption>
            点击左侧功能添加
          </a-doption>
        </template>
      </a-dropdown>
    </div>
  </div>
</template>

<script lang="ts">
  export default {
    name: 'terminalSshRightMenuBlock'
  };
</script>

<script lang="ts" setup>
  import type { ContextMenuItem } from '@/views/terminal/types/define';
  import { computed, ref } from 'vue';
  import { TerminalPreferenceItem } from '@/store/modules/terminal';
  import { SshActionBarItems } from '@/views/terminal/types/const';
  import { isSecureEnvironment } from '@/utils/env';
  import useTerminalPreference from '@/views/terminal/types/use-terminal-preference';

  const popupContainer = ref();

  const { formModel: rightActionItems } = useTerminalPreference<Array<string>>(TerminalPreferenceItem.SSH_RIGHT_MENU_SETTING, true);

  // 实际操作项
  const rightActions = computed<Array<ContextMenuItem>>(() => {
    return rightActionItems.value
      .map(s => SshActionBarItems.find(i => i.item === s) as ContextMenuItem)
      .filter(Boolean);
  });

  // 触发操作项
  const toggleAction = (item: string) => {
    if (rightActionItems.value.includes(item)) {
      // 移除
      rightActionItems.value.splice(rightActionItems.value.indexOf(item), 1);
    } else {
      // 添加
      rightActionItems.value.push(item);
    }
  };

</script>

<style lang="less" scoped>
  @container-width: 418px;
  @wrapper-margin-r: 32px;
  @transform-x: 8px;
  @item-width: (@container-width - @wrapper-margin-r) / 2;

  .setting-body {
    display: flex;
  }

  .actions-container {
    width: @container-width;
    height: auto;

    .actions-wrapper {
      padding-right: 8px;
      margin-right: @wrapper-margin-r;
    }

    .action-item-wrapper {
      transition: all 0.2s;
      border-radius: 4px;
      width: calc((@item-width) - @transform-x);

      &:hover {
        width: calc(@item-width);
        padding: 4px 0 !important;

        .action-item {
          background: var(--color-fill-3);
        }

        .action-icon {
          background: var(--color-fill-4);
        }
      }
    }

    .action-item {
      display: flex;
      padding: 6px;
      align-items: center;
      cursor: pointer;
      border-radius: 4px;
      width: inherit;
      background: var(--color-fill-2);
    }

    .action-icon {
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
      border-radius: 4px;
      margin-right: 8px;
      background-color: var(--color-fill-3);
    }

    .action-desc {
      display: flex;
      align-items: center;
      font-size: 14px;
      user-select: none;
    }
  }

  .preview-container {
    width: 242px;
    height: auto;

    :deep(.arco-dropdown-option-content) {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;

      &:hover {
        .close-icon {
          display: flex;
        }
      }
    }

    .preview-action {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .preview-icon {
      font-size: 18px;
      margin-right: 6px;
    }

    .close-icon {
      display: none;
      padding: 3px;
      border-radius: 12px;
      align-items: center;
      justify-content: center;
      transition: .2s;
      font-size: 15px;

      &:hover {
        background-color: var(--color-fill-3);
      }
    }
  }

  :deep(.arco-trigger-popup) {
    position: relative;
  }

</style>
