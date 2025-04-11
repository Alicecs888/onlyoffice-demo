<template>
  <div class="upload-component">
    <h2>文件上传</h2>
    <div class="upload-area">
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
    </div>
    
    <div v-if="fileList.length > 0" class="file-list">
      <h3>已上传文件</h3>
      <el-table :data="fileList" style="width: 100%">
        <el-table-column prop="fileName" label="文件名" />
        <el-table-column prop="fileType" label="文件类型" width="120" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button type="primary" size="small" @click="openFile(scope.row)">
              打开编辑
            </el-button>
            <el-button type="danger" size="small" @click="deleteFile(scope.row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { UploadFilled } from '@element-plus/icons-vue';

const emit = defineEmits(['fileUploaded']);
const fileList = ref([]);

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
    fileName: response.fileKey,
    fileUrl: response.fileUrl,
    fileType: response.fileType,
    fileKey: response.fileKey,
    uploadTime: new Date().toLocaleString()
  };
  
  fileList.value.push(fileInfo);
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

// 打开文件
const openFile = (file) => {
  emit('fileUploaded', file);
};

// 删除文件
const deleteFile = (file) => {
  ElMessageBox.confirm(
    '确定要删除该文件吗？',
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
            fileList.value = fileList.value.filter(item => item.fileKey !== file.fileKey);
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
</script>

<style scoped>
.upload-component {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.upload-area {
  margin-bottom: 30px;
}

.file-list {
  margin-top: 30px;
}

h2, h3 {
  margin-bottom: 20px;
  text-align: center;
}
</style> 