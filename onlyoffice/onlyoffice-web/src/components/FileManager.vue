<template>
  <div class="file-manager">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>文档管理</h2>
      <el-button type="primary" @click="showUploadDialog">上传新文档</el-button>
    </div>
    
    <!-- 文件列表 -->
    <el-card class="file-list-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>文档列表</span>
          <el-input
            v-model="searchQuery"
            placeholder="搜索文件名"
            clearable
            prefix-icon="Search"
            style="width: 300px;"
          />
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="filteredFiles"
        stripe
        style="width: 100%"
        empty-text="暂无文件，请上传"
      >
        <el-table-column prop="fileName" label="文件名" min-width="200" />
        <el-table-column prop="fileType" label="类型" width="100" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="scope">
            <el-button type="primary" @click="previewFile(scope.row)" size="small">预览</el-button>
            <el-button type="success" @click="editFile(scope.row)" size="small">编辑</el-button>
            <el-button type="danger" @click="deleteFile(scope.row)" size="small">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 上传对话框 -->
    <el-dialog
      v-model="uploadDialogVisible"
      title="上传新文档"
      width="500px"
    >
      <el-upload
        class="upload-demo"
        drag
        action="/api/onlyoffice/upload"
        :on-success="handleUploadSuccess"
        :on-error="handleUploadError"
        :before-upload="beforeUpload"
        accept=".docx,.xlsx,.pptx,.doc,.xls,.ppt,.txt,.pdf"
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            支持的文件格式: .docx, .xlsx, .pptx, .doc, .xls, .ppt, .txt, .pdf
          </div>
        </template>
      </el-upload>
    </el-dialog>
    
    <!-- 文件预览/编辑对话框 -->
    <el-dialog
      v-model="editorDialogVisible"
      :title="currentFile.fileName || '文档查看'"
      fullscreen
      :destroy-on-close="true"
      :show-close="true"
    >
      <div style="height: calc(100vh - 110px);">
        <OnlyOfficeViewer
          v-if="currentFile.fileUrl"
          :fileUrl="currentFile.fileUrl"
          :fileName="currentFile.fileName"
          :fileType="currentFile.fileType"
          :fileKey="currentFile.fileKey"
          :initialMode="editorMode"
          @modeChanged="handleModeChanged"
          ref="officeViewer"
        />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UploadFilled, Search } from '@element-plus/icons-vue';
import OnlyOfficeViewer from './OnlyOfficeViewer.vue';

// 文件列表
const files = ref([]);
// 加载状态
const loading = ref(false);
// 对话框状态
const uploadDialogVisible = ref(false);
const editorDialogVisible = ref(false);
// 当前处理的文件
const currentFile = ref({});
// 编辑器模式
const editorMode = ref('view');
// 搜索查询
const searchQuery = ref('');

// 过滤后的文件列表
const filteredFiles = computed(() => {
  if (!searchQuery.value) return files.value;
  const query = searchQuery.value.toLowerCase();
  return files.value.filter(file => 
    file.fileName.toLowerCase().includes(query)
  );
});

// 加载文件列表
const loadFiles = async () => {
  loading.value = true;
  try {
    const response = await fetch('/api/onlyoffice/files');
    if (response.ok) {
      const data = await response.json();
      files.value = data || [];
    } else {
      ElMessage.error('加载文件列表失败');
    }
  } catch (error) {
    console.error('加载文件失败:', error);
    ElMessage.error('连接服务器失败');
  } finally {
    loading.value = false;
  }
};

// 显示上传对话框
const showUploadDialog = () => {
  uploadDialogVisible.value = true;
};

// 上传前检查
const beforeUpload = (file) => {
  const allowedTypes = [
    'application/vnd.openxmlformats-officedocument.wordprocessingml.document', // docx
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // xlsx
    'application/vnd.openxmlformats-officedocument.presentationml.presentation', // pptx
    'application/msword', // doc
    'application/vnd.ms-excel', // xls
    'application/vnd.ms-powerpoint', // ppt
    'text/plain', // txt
    'application/pdf', // pdf
  ];

  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('不支持的文件类型');
    return false;
  }
  return true;
};

// 上传成功处理
const handleUploadSuccess = (response, uploadFile) => {
  ElMessage.success('文件上传成功');
  const fileInfo = {
    fileName: response.fileName,
    fileUrl: response.fileUrl,
    fileType: response.fileType,
    fileKey: response.fileKey,
    uploadTime: new Date().toLocaleString()
  };
  
  files.value.unshift(fileInfo);
  uploadDialogVisible.value = false;
};

// 上传失败处理
const handleUploadError = (error, file, fileList) => {
  console.error('文件上传错误:', error);
  if (error.response) {
    try {
      const errorData = JSON.parse(error.response);
      ElMessage.error(`上传失败: ${errorData.message || '未知错误'}`);
    } catch (e) {
      ElMessage.error(`上传失败: ${error.status || error.message || '未知错误'}`);
    }
  } else {
    ElMessage.error(`上传失败: ${error.message || '可能是跨域问题或服务器未响应'}`);
  }
};

// 预览文件
const previewFile = (file) => {
  currentFile.value = file;
  editorMode.value = 'view';
  editorDialogVisible.value = true;
};

// 编辑文件
const editFile = (file) => {
  currentFile.value = file;
  editorMode.value = 'edit';
  editorDialogVisible.value = true;
};

// 删除文件
const deleteFile = (file) => {
  ElMessageBox.confirm(
    `确定要删除文件 "${file.fileName}" 吗？此操作不可撤销。`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(() => {
      // 调用后端删除API
      fetch(`/api/onlyoffice/delete/${file.fileKey}`, {
        method: 'DELETE',
      })
        .then(response => response.json())
        .then(data => {
          if (data.success) {
            files.value = files.value.filter(item => item.fileKey !== file.fileKey);
            ElMessage.success('文件删除成功');
          } else {
            ElMessage.error(data.message || '文件删除失败');
          }
        })
        .catch(() => {
          ElMessage.error('删除请求失败');
        });
    })
    .catch(() => {
      // 用户取消删除
    });
};

// 处理模式变化
const handleModeChanged = (mode) => {
  editorMode.value = mode;
};

// 组件挂载后加载文件列表
loadFiles();
</script>

<style scoped>
.file-manager {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.file-list-card {
  margin-bottom: 20px;
}

h2 {
  margin: 0;
  font-weight: 500;
  color: #303133;
}
</style> 