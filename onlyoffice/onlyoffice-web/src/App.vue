<template>
  <div class="container">
    <el-config-provider :locale="zhCn">
      <el-container class="app-container">
        <el-header height="60px" class="app-header">
          <div class="logo">OnlyOffice在线文档系统</div>
          <el-menu
            :default-active="activeMenu"
            mode="horizontal"
            @select="handleMenuSelect"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
          >
            <el-menu-item index="home">首页</el-menu-item>
            <el-menu-item index="file-manager">文件管理</el-menu-item>
            <el-menu-item index="demo">演示文档</el-menu-item>
          </el-menu>
        </el-header>
        
        <el-main class="app-main">
          <!-- 首页 -->
          <div v-if="activeMenu === 'home'" class="home-page">
            <h1>欢迎使用OnlyOffice在线文档系统</h1>
            <p class="description">这是一个基于OnlyOffice的在线文档编辑与管理系统，支持多种文档格式的预览和编辑。</p>
            <div class="feature-list">
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-card class="feature-card">
                    <template #header>
                      <div class="card-header">
                        <span>文档编辑</span>
                      </div>
                    </template>
                    <div class="feature-content">
                      支持Word、Excel、PowerPoint等格式的在线编辑，无需安装Office软件。
                    </div>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card class="feature-card">
                    <template #header>
                      <div class="card-header">
                        <span>文件管理</span>
                      </div>
                    </template>
                    <div class="feature-content">
                      便捷的文件管理功能，支持上传、预览、编辑和删除操作。
                    </div>
                  </el-card>
                </el-col>
                <el-col :span="8">
                  <el-card class="feature-card">
                    <template #header>
                      <div class="card-header">
                        <span>实时保存</span>
                      </div>
                    </template>
                    <div class="feature-content">
                      自动保存功能确保您的修改不会丢失，支持版本控制和历史记录。
                    </div>
                  </el-card>
                </el-col>
              </el-row>
            </div>
            <div class="action-buttons">
              <el-button type="primary" size="large" @click="handleMenuSelect('file-manager')">开始使用</el-button>
              <el-button type="info" size="large" @click="handleMenuSelect('demo')">查看演示</el-button>
            </div>
          </div>
          
          <!-- 文件管理 -->
          <FileManager v-if="activeMenu === 'file-manager'" />
          
          <!-- 演示文档 -->
          <div v-if="activeMenu === 'demo'" class="demo-container">
            <OnlyOfficeViewer
              :fileUrl="demoDocUrl"
              fileName="演示文档.docx"
              fileType="docx"
              fileKey="demo_document_key"
            />
          </div>
        </el-main>
        
        <el-footer height="50px" class="app-footer">
          &copy; 2023 OnlyOffice在线文档系统
        </el-footer>
      </el-container>
    </el-config-provider>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ElButton, ElMenu, ElMenuItem, ElContainer, ElHeader, ElMain, ElFooter, ElCard, ElRow, ElCol, ElConfigProvider } from 'element-plus';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import OnlyOfficeViewer from './components/OnlyOfficeViewer.vue';
import FileManager from './components/FileManager.vue';

// 当前选中的菜单
const activeMenu = ref('home');

// 演示文档URL
const demoDocUrl = 'https://d2nlctn12v279m.cloudfront.net/assets/docs/samples/zh/demo.docx';

// 菜单选择处理
const handleMenuSelect = (key) => {
  activeMenu.value = key;
};
</script>

<style>
html, body, #app {
  height: 100%;
  width: 100%;
  margin: 0;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
}

.container {
  height: 100%;
  width: 100%;
}

.app-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: #545c64;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
}

.app-main {
  flex: 1;
  padding: 0;
  overflow: auto;
}

.app-footer {
  text-align: center;
  background-color: #f5f7fa;
  color: #606266;
  line-height: 50px;
}

.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  text-align: center;
}

.description {
  font-size: 1.2rem;
  color: #606266;
  margin-bottom: 40px;
}

.feature-list {
  margin-bottom: 40px;
}

.feature-card {
  height: 100%;
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
}

.feature-content {
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.action-buttons {
  margin-top: 30px;
}

.action-buttons .el-button {
  margin: 0 10px;
  padding: 12px 24px;
}

.demo-container {
  height: calc(100vh - 110px);
}
</style>
