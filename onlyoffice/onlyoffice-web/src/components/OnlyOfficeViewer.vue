<template>
  <div class="onlyoffice-container">
    <div class="toolbar">
      <el-button type="primary" @click="switchToEditMode" v-if="currentMode === 'view'">
        编辑模式
      </el-button>
      <el-button type="info" @click="switchToViewMode" v-if="currentMode === 'edit'">
        预览模式
      </el-button>
    </div>
    
    <div class="editor-wrapper">
      <DocumentEditor
        id="docEditor"
        documentServerUrl="http://10.164.130.137:9631/"
        :config="editorConfig"
      />
    </div>
  </div>
</template>

<script setup>
import { DocumentEditor } from "@onlyoffice/document-editor-vue";
import { computed, reactive, ref, watch } from "vue";
import { ElButton, ElMessage } from 'element-plus';

// 定义组件属性
const props = defineProps({
  fileUrl: {
    type: String,
    required: true
  },
  fileName: {
    type: String,
    default: '文档.docx'
  },
  fileType: {
    type: String,
    default: 'docx'
  },
  fileKey: {
    type: String,
    default: () => `doc_${Date.now()}`
  },
  initialMode: {
    type: String,
    default: 'view' // 'view' 或 'edit'
  }
});

// 事件
const emit = defineEmits(['modeChanged', 'documentSaved']);

// 当前模式
const currentMode = ref(props.initialMode);

// 文档类型映射
const documentTypeMap = {
  'docx': 'word',
  'doc': 'word',
  'xlsx': 'cell',
  'xls': 'cell',
  'pptx': 'slide',
  'ppt': 'slide',
  'pdf': 'pdf',
  'txt': 'word'
};

// 计算文档类型
const documentType = computed(() => {
  const extension = props.fileType.toLowerCase();
  return documentTypeMap[extension] || 'word';
});

// 切换到编辑模式
const switchToEditMode = () => {
  currentMode.value = 'edit';
  emit('modeChanged', 'edit');
};

// 切换到预览模式
const switchToViewMode = () => {
  currentMode.value = 'view';
  emit('modeChanged', 'view');
};

// 编辑器配置
const editorConfig = reactive({
  "documentType": documentType,
  "document": {
    "title": props.fileName,
    "url": props.fileUrl,
    "permissions": {
      "print": true,
      "download": true,
      "edit": true,
      "review": true
    },
    "fileType": props.fileType,
    "key": props.fileKey
  },
  "editorConfig": {
    "customization": {
      "autosave": true,
      "forcesave": true,
      "chat": false,
      "comments": true,
      "compactHeader": true,
      "compactToolbar": true,
      "compatibleFeatures": true,
      "help": true,
      "toolbarNoTabs": false,
      "hideRightMenu": false,
    },
    "mode": currentMode,
    // "callbackUrl": "/api/onlyoffice/callback",
    "callbackUrl": "http://10.164.130.137:8089/api/onlyoffice/callback",

    "lang": "zh-CN",
    "user": {
      "name": "用户",
      "id": "user_" + Date.now(),
      "plan": "basic"
    },
  }
});

// 监听属性变化并更新配置
watch(() => [props.fileUrl, props.fileName, props.fileType, props.fileKey], 
  ([newUrl, newName, newType, newKey]) => {
    editorConfig.document.url = newUrl;
    editorConfig.document.title = newName;
    editorConfig.document.fileType = newType;
    editorConfig.document.key = newKey;
    editorConfig.documentType = documentTypeMap[newType.toLowerCase()] || 'word';
  }
);

// 监听模式变化
watch(currentMode, (newMode) => {
  editorConfig.editorConfig.mode = newMode;
});

// 暴露方法给父组件
defineExpose({
  switchToEditMode,
  switchToViewMode,
  getCurrentMode: () => currentMode.value
});
</script>

<style scoped>
.onlyoffice-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 100%;
}

.toolbar {
  padding: 10px;
  display: flex;
  justify-content: flex-end;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.editor-wrapper {
  flex: 1;
  min-height: 400px;
}
</style> 